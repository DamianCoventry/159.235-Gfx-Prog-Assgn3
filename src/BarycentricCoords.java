// https://en.wikipedia.org/wiki/Barycentric_coordinate_system
// https://www.scratchapixel.com/lessons/3d-basic-rendering/ray-tracing-rendering-a-triangle/barycentric-coordinates
public class BarycentricCoords {
    public double _u, _v, _w;
    public BarycentricCoords(double u, double v, double w) {
        _u = u; _v = v; _w = w;
    }

    public static BarycentricCoords Calculate(Vector3 p, Vector3 v0, Vector3 v1, Vector3 v2) {
        Vector3 edge0 = v1.subtract(v0);
        Vector3 edge1 = v2.subtract(v0);
        Vector3 lineSegment = p.subtract(v0);

        double d00 = edge0.dotProduct(edge0);
        double d01 = edge0.dotProduct(edge1);
        double d11 = edge1.dotProduct(edge1);
        double d20 = lineSegment.dotProduct(edge0);
        double d21 = lineSegment.dotProduct(edge1);
        double denominator = d00 * d11 - d01 * d01;
        if (denominator == 0.0) { // if 0 then v0, v1, v2 are the same location in 3D space
            return null;
        }

        double v = (d11 * d20 - d01 * d21) / denominator;
        double w = (d00 * d21 - d01 * d20) / denominator;
        double u = 1.0 - v - w;
        return new BarycentricCoords(u, v, w);
    }
}
