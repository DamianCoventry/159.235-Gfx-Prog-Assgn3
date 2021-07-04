public class PlaneObject extends Object {
    private final Vector3 _normal;

    public PlaneObject(Vector3 position, Vector3 rotation, double width, double height, Material[] materials, TextureSampling textureSampling) {
        super(position, rotation, new Vector3(width, 1.0, height), materials, textureSampling);
        _normal = new Vector3(0.0, 1.0, 0.0);
    }
    
    // https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection
    @Override
    public IntersectResult intersect(Ray rayLocalCoords) {
        IntersectResult result = new IntersectResult();
        if (determineIntersectionPoint(rayLocalCoords, result)) {
            determineDiffuseTextureColour(result);
            result._intersected = true;
            result._normal = _normal;
            result._material = getMaterials()[0];
        }
        return result;
    }

    private boolean determineIntersectionPoint(Ray rayLocalCoords, IntersectResult result) {
        Vector3 lineSegment = rayLocalCoords.getDirection().multiply(rayLocalCoords.getMagnitude());
        double denominator = _normal.dotProduct(lineSegment);
        if (Math.abs(denominator) <= 1e-3) { // It's parallel to the plane
            return false;
        }

        // Work out how far along the line segment the intersection is in terms of a percentage
        double percent = -_normal.dotProduct(rayLocalCoords.getOrigin()) / denominator;
        if (percent < 0.0 || percent > 1.0) { // Either before the line segment starts, or after it ends
            return false;
        }

        Vector3 intersectionLineSeg = lineSegment.multiply(percent);
        Vector3 intersection = rayLocalCoords.getOrigin().add(intersectionLineSeg);
        if (Math.abs(intersection._x) >= (getScale()._x / 2.0) ||
            Math.abs(intersection._z) >= (getScale()._z / 2.0)) {
            return false;
        }

        result._point = intersection;
        result._distance = intersectionLineSeg.magnitude();
        return true;
    }

    private void determineDiffuseTextureColour(IntersectResult result) {
        if (getMaterials()[0].getDiffuseTexture() == null) {
            result._diffuseTextureColour = new Vector3(1.0, 1.0, 1.0);
            return;
        }
        double u = (result._point._x / getScale()._x) + 0.5;
        double v = (result._point._z / getScale()._z) + 0.5;
        result._diffuseTextureColour = _textureSampling.takeSample(u, v, getMaterials()[0].getDiffuseTexture());
    }
}
