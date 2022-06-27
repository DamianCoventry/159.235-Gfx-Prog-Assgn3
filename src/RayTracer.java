import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class RayTracer {
    private final RenderingOptions _ro;
    private final SceneGraph _sceneGraph;
    private final BufferedImage _environmentMappingImage;
    private final ArrayList<PositionalLight> _positionalLights;
    private final TextureSampling _textureSampling;
    private final ZBuffer _zBuffer;

    public RayTracer(RenderingOptions ro) throws Exception {
        _ro = ro;

        _environmentMappingImage = ImageIO.read(new File(ro._envMappingImageFilename));
        _textureSampling = new TextureSampling(ro._textureFiltering, ro._texCoordWrapping);

        if (ro._antiAliasing == RenderingOptions.AntiAliasing.SUPER_SAMPLING) {
            _zBuffer = new ZBuffer(
                    ro._outputImageWidth, ro._outputImageHeight,
                    SuperSamplingAA.getNumSamples(), SuperSamplingAA.createToSampleIndexFn());
        }
        else {
            _zBuffer = new ZBuffer(
                    ro._outputImageWidth, ro._outputImageHeight,
                    NoAntiAliasing.getNumSamples(), NoAntiAliasing.createToSampleIndexFn());
        }

        _sceneGraph = new SceneGraph(_textureSampling);

        _positionalLights = new ArrayList<>();
        _positionalLights.add(new PositionalLight(new Vector3(1.00, 0.15, 0.15), new Vector3( 1.68,10.75, -9.04), 0.55, 5.63));
        _positionalLights.add(new PositionalLight(new Vector3(0.15, 1.00, 0.15), new Vector3(-5.05, 8.60, -8.75), 0.65, 6.35));
        _positionalLights.add(new PositionalLight(new Vector3(1.00, 1.00, 1.00), new Vector3(-6.25, 4.74, -0.84), 0.85, 4.55));
        _positionalLights.add(new PositionalLight(new Vector3(1.00, 1.00, 1.00), new Vector3( 1.65, 2.15,  3.50), 1.0, 15.05));
    }

    public Vector3 traceRay(Ray ray, int depth) {
        if (depth <= 0) {
            return performEnvironmentMapping(ray);
        }
        final IntersectResult hit = _sceneGraph.intersect(ray, _zBuffer);
        if (!hit._intersected) {
            return performEnvironmentMapping(ray);
        }
        final Vector3 reflection = traceReflection(ray, depth, hit);
        final Vector3 refraction = traceRefraction(ray, depth, hit);
        final Lighting lighting = traceLighting(ray, hit);
        return hit._diffuseTextureColour
                .multiply(lighting._ambient
                    .add(lighting._diffuse)
                    .add(lighting._specular)
                    .add(reflection)
                    .add(refraction)
                );
    }

    private Vector3 traceReflection(Ray ray, int depth, IntersectResult hit) {
        final Vector3 direction = Vector3.reflect(ray.getDirection(), hit._normal).makeNormalised();
        final double dot = direction.dotProduct(hit._normal);
        final Vector3 step = hit._normal.multiply(1e-3);
        final Ray reflected = new Ray(
                dot < 0 ? hit._point.subtract(step) : hit._point.add(step),
                direction, ray.getPixelX(), ray.getPixelY());
        return traceRay(reflected, depth - 1).multiply(hit._material.getAlbedo(2));
    }

    private Vector3 traceRefraction(Ray ray, int depth, IntersectResult hit) {
        final Vector3 direction = Vector3.refract(ray.getDirection(), hit._normal, hit._material.getIndexOfRefraction(), 1.0).makeNormalised();
        final double dot = direction.dotProduct(hit._normal);
        final Vector3 step = hit._normal.multiply(1e-3);
        final Ray refracted = new Ray(
                dot < 0 ? hit._point.subtract(step) : hit._point.add(step),
                direction, ray.getPixelX(), ray.getPixelY());
        return traceRay(refracted, depth - 1).multiply(hit._material.getAlbedo(3));
    }

    private static class Lighting {
        public Vector3 _ambient;
        public Vector3 _diffuse;
        public Vector3 _specular;
        public Lighting(Vector3 ambient, Vector3 diffuse, Vector3 specular) {
            _ambient = ambient;
            _diffuse = diffuse;
            _specular = specular;
        }
    }

    private Lighting traceLighting(Ray ray, IntersectResult hit) {
        double diffusePower = 0.0;
        double specularPower = 0.0;
        Vector3 diffuseColour = new Vector3(1.0, 1.0, 1.0);

        for (PositionalLight light : _positionalLights) {
            final Vector3 lightDirection = light._position.subtract(hit._point);
            final double distanceToLight = Math.abs(lightDirection.magnitude());
            if (distanceToLight > light._radius) {
                continue;
            }

            lightDirection.normalise();
            final double attenuation = light.calculateAttenuation(distanceToLight);

            final double lightDot = lightDirection.dotProduct(hit._normal);
            if (isShadowed(ray, lightDot, distanceToLight, lightDirection, hit)) {
                continue;
            }

            // This light is not casting a shadow on this location. Calculate the diffuse light.
            diffusePower += light._power * Math.max(0.0, lightDot) * attenuation;
            diffuseColour = diffuseColour.multiply(light._colour);

            // Calculate the specular light too
            final Vector3 reflected = Vector3.reflect(lightDirection, hit._normal);
            final double specular = Math.max(0.0, reflected.dotProduct(ray.getDirection()));
            specularPower += Math.pow(specular, hit._material.getSpecularExponent()) * light._power * attenuation;
        }

        return new Lighting(
                hit._material.getDiffuseColour().multiply(_ro._ambientLight),
                hit._material.getDiffuseColour().multiply(diffuseColour.multiply(diffusePower * hit._material.getAlbedo(0))),
                hit._material.getSpecularColour().multiply(specularPower * hit._material.getAlbedo(1))
        );
    }

    private boolean isShadowed(Ray ray, double lightDot, double lightDistance, Vector3 lightDirection, IntersectResult hit) {
        Vector3 step = hit._normal.multiply(1e-3);
        Ray shadowed = new Ray(
                lightDot < 0 ? hit._point.subtract(step) : hit._point.add(step),
                lightDirection, ray.getPixelX(), ray.getPixelY());
        IntersectResult shadowHit = _sceneGraph.intersect(shadowed, _zBuffer);
        if (shadowHit._intersected) {
            return shadowHit._point.subtract(shadowed.getOrigin()).magnitude() < lightDistance;
        }
        return false;
    }

    private Vector3 performEnvironmentMapping(Ray ray) {
        TexCoord tc = TexCoordGenerator.spherical(ray.getDirection());
        return _textureSampling.takeSample(tc._u, tc._v, _environmentMappingImage);
    }
}
