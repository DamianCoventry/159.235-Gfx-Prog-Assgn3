import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Set;

public class PolyhedronObject extends Object {
    private static final int MAX_BVH_TRACE_TIMES = 25000;
    private int _bvhTraceTimeIndex;
    private final long[] _bvhTraceTimes;
    private final long[] _bvhNumRayTriIntersectionTests;
    private final PolyhedronMesh _polyhedronMesh;
    private final BoundingVolumeHierarchy _bvh;
    private double _closestDistance;
    private final DecimalFormat _decimalFormat;
    private long _cachedNumFaces;

    public PolyhedronObject(Vector3 position, Vector3 rotation, Vector3 scale, Material[] materials,
                            TextureSampling textureSampling, PolyhedronMesh polyhedronMesh, BoundingVolumeHierarchy bvh) {
        super(position, rotation, scale, materials, textureSampling);
        _polyhedronMesh = polyhedronMesh;
        _bvh = bvh;
        _bvhTraceTimeIndex = 0;
        _bvhTraceTimes = new long[MAX_BVH_TRACE_TIMES];
        _bvhNumRayTriIntersectionTests = new long[MAX_BVH_TRACE_TIMES];
        _decimalFormat = new DecimalFormat("0.000");
        _cachedNumFaces = 0;
    }

    public long getNumFaces() {
        long numFaces = 0;
        for (int j = 0; j < _polyhedronMesh.getPieces().length; ++j) {
            if (_polyhedronMesh.getPieces()[j].getFlatShadedFaces() != null) {
                numFaces += _polyhedronMesh.getPieces()[j].getFlatShadedFaces().length;
            }
            Set<Integer> smoothShadedFaceGroups = _polyhedronMesh.getPieces()[j].getGroupIds();
            Iterator<Integer> faceGroup = smoothShadedFaceGroups.iterator();
            while (faceGroup.hasNext()) {
                numFaces += _polyhedronMesh.getPieces()[j].getSmoothShadedFaceGroup(faceGroup.next()).length;
            }
        }
        _cachedNumFaces = numFaces;
        return numFaces;
    }
    
    @Override
    public IntersectResult intersect(Ray ray) {
        IntersectResult result = new IntersectResult();
        _closestDistance = Double.MAX_VALUE;

        _bvhNumRayTriIntersectionTests[_bvhTraceTimeIndex] = 0;
        long startTimeMs = System.currentTimeMillis();

        _bvh.traceRay(ray, (face, material) -> {
            ++_bvhNumRayTriIntersectionTests[_bvhTraceTimeIndex];
            RayTriResult r = rayTriangleIntersect(ray, _polyhedronMesh, face);
            if (r != null) {
                double distance = ray.getOrigin().subtract(r._point).magnitude();
                if (distance < _closestDistance) {
                    _closestDistance = distance;
                    result._material = material;
                    result._point = r._point;
                    result._normal = r._normal;
                    result._distance = distance;
                    result._diffuseTextureColour = determineDiffuseTextureColour(r, result._material, _polyhedronMesh, face);
                }
            }
        });

        _bvhTraceTimes[_bvhTraceTimeIndex++] = System.currentTimeMillis() - startTimeMs;
        if (_bvhTraceTimeIndex >= MAX_BVH_TRACE_TIMES) {
            _bvhTraceTimeIndex = 0;

            double traceTimeMs = calculateAverageTraceTime();
            long numRayTriIntersections = calculateAverageNumRayTriIntersectionTests();
            long rayTriPercent = numRayTriIntersections * 100 / _cachedNumFaces;

            System.out.println("After " + MAX_BVH_TRACE_TIMES + " traces, the average:");
            System.out.println("    BVH trace time is " + _decimalFormat.format(traceTimeMs) + "ms");
            System.out.println("    Num of ray/tri tests is " + numRayTriIntersections + " out of " + _cachedNumFaces + " faces (" + rayTriPercent + "%).");
        }

        result._intersected = _closestDistance < Double.MAX_VALUE;
        return result;
    }

    private double calculateAverageTraceTime() {
        long totalMs = 0;
        for (int i = 0; i < MAX_BVH_TRACE_TIMES; ++i) {
            totalMs += _bvhTraceTimes[i];
        }
        return (double)totalMs / (double)MAX_BVH_TRACE_TIMES;
    }

    private long calculateAverageNumRayTriIntersectionTests() {
        long total = 0;
        for (int i = 0; i < MAX_BVH_TRACE_TIMES; ++i) {
            total += _bvhNumRayTriIntersectionTests[i];
        }
        return total / MAX_BVH_TRACE_TIMES;
    }

    private static class RayTriResult {
        public Vector3 _point;
        public Vector3 _normal;
        public RayTriResult(Vector3 point, Vector3 normal) {
            _point = point;
            _normal = normal;
        }
    }

    // https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
    private RayTriResult rayTriangleIntersect(Ray ray, PolyhedronMesh mesh, PolyhedronMesh.Face face) {
        Vector3 v0 = mesh.getVertices()[face._faceVertices[0]._vertexIndex];
        Vector3 v1 = mesh.getVertices()[face._faceVertices[1]._vertexIndex];
        Vector3 v2 = mesh.getVertices()[face._faceVertices[2]._vertexIndex];
        Vector3 edge0 = v1.subtract(v0);
        Vector3 edge1 = v2.subtract(v0);

        Vector3 h = ray.getDirection().crossProduct(edge1);
        double dot = edge0.dotProduct(h);
        if (Math.abs(dot) < 1e-3) {
            return null; // line is parallel
        }

        double f = 1.0 / dot;

        Vector3 s = ray.getOrigin().subtract(v0);
        double u = f * s.dotProduct(h);
        if (u < 0.0 || u > 1.0) {
            return null;
        }

        Vector3 q = s.crossProduct(edge0);
        double v = f * ray.getDirection().dotProduct(q);
        if (v < 0.0 || u + v > 1.0) {
            return null;
        }

        double t = f * edge1.dotProduct(q);
        if (t > 1e-3) {
            return new RayTriResult(
                    ray.getOrigin().add(ray.getDirection().multiply(t)),
                    edge0.crossProduct(edge1).makeNormalised());
        }

        return null; // The line intersects, but not the line segment.
    }

    private Vector3 determineDiffuseTextureColour(RayTriResult r, Material material, PolyhedronMesh mesh, PolyhedronMesh.Face face) {
        Vector3 v0 = mesh.getVertices()[face._faceVertices[0]._vertexIndex];
        Vector3 v1 = mesh.getVertices()[face._faceVertices[1]._vertexIndex];
        Vector3 v2 = mesh.getVertices()[face._faceVertices[2]._vertexIndex];
        BarycentricCoords bc = BarycentricCoords.Calculate(r._point, v0, v1, v2);
        if (bc == null) {
            return new Vector3();
        }

        TexCoord tc0 = mesh.getTexCoords()[face._faceVertices[0]._texCoordIndex];
        TexCoord tc1 = mesh.getTexCoords()[face._faceVertices[1]._texCoordIndex];
        TexCoord tc2 = mesh.getTexCoords()[face._faceVertices[2]._texCoordIndex];
        double u = (tc0._u * bc._u) + (tc1._u * bc._v) + (tc2._u * bc._w);
        double v = (tc0._v * bc._u) + (tc1._v * bc._v) + (tc2._v * bc._w);
        return _textureSampling.takeSample(u, 1.0 - v, material.getDiffuseTexture());
    }
}
