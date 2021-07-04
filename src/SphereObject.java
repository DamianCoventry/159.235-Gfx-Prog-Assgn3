public class SphereObject extends Object {
    private final double _radiusSquared;

    public SphereObject(Vector3 position, Vector3 rotation, double radius, Material[] materials, TextureSampling textureSampling) {
        super(position, rotation, new Vector3(radius, radius, radius), materials, textureSampling);
        _radiusSquared = radius * radius;
    }

    // https://en.wikipedia.org/wiki/Line%E2%80%93sphere_intersection
    @Override
    public IntersectResult intersect(Ray rayLocalCoords) {
        IntersectResult result = new IntersectResult();
        if (determineIntersectionPoint(rayLocalCoords, result)) {
            determineDiffuseTextureColour(result);
            result._intersected = true;
            result._material = getMaterials()[0];
        }
        return result;
    }

    private boolean determineIntersectionPoint(Ray rayLocalCoords, IntersectResult result) {
        Vector3 lineToSphere = rayLocalCoords.getOrigin().negate();
        double lineToSphereDot = lineToSphere.dotProduct(rayLocalCoords.getDirection());
        double projectedDistance = lineToSphere.dotProduct() - (lineToSphereDot * lineToSphereDot);
        if (projectedDistance > _radiusSquared) {
            return false;
        }

        double intersectToSphereDistance = Math.sqrt(_radiusSquared - projectedDistance);
        double lineToSphereDistance = lineToSphereDot - intersectToSphereDistance;
        if (lineToSphereDistance < 0) {
            return false;
        }

        result._point = rayLocalCoords.getOrigin().add(rayLocalCoords.getDirection().multiply(lineToSphereDistance));
        result._normal = result._point.makeNormalised();
        result._distance = rayLocalCoords.getOrigin().subtract(result._point).magnitude();
        return true;
    }

    private void determineDiffuseTextureColour(IntersectResult result) {
        if (getMaterials()[0].getDiffuseTexture() != null) {
            result._diffuseTextureColour = sampleDiffuseTexture(result._normal, getMaterials()[0]);
        }
        else {
            result._diffuseTextureColour = new Vector3(1.0, 1.0, 1.0);
        }
    }

    private Vector3 sampleDiffuseTexture(Vector3 unitVector, Material material) {
        TexCoord tc = TexCoordGenerator.spherical(unitVector);
        return _textureSampling.takeSample(tc._u, tc._v, material.getDiffuseTexture());
    }
}
