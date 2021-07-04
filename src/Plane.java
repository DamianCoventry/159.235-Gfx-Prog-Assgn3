public class Plane {
    private final Vector3 _pointOnPlane;
    private final Vector3 _normal;

    public Plane(Vector3 pointOnPlane, Vector3 normal) {
        _pointOnPlane = pointOnPlane;
        _normal = normal;
    }

    public Vector3 getPointOnPlane() { return _pointOnPlane; }
    public Vector3 getNormal() { return _normal; }

    public enum Classification { IN_FRONT, SPANNING, BEHIND, ON_PLANE }

    public Classification classify(Vector3 point) {
        Vector3 direction = point.subtract(_pointOnPlane).makeNormalised();
        double dot = _normal.dotProduct(direction);
        if (Math.abs(dot) < 1e-3) {
            return Classification.ON_PLANE;
        }
        return dot > 0.0 ? Classification.IN_FRONT : Classification.BEHIND;
    }

    public Classification classify(Vector3 begin, Vector3 end) {
        return classify(new LineSegment(begin, end));
    }

    public Classification classify(LineSegment lineSegment) {
        Classification beginClass = classify(lineSegment._begin);
        Classification endClass = classify(lineSegment._end);
        if (beginClass == Classification.ON_PLANE) return endClass;
        if (endClass == Classification.ON_PLANE) return beginClass;
        if (beginClass == Classification.IN_FRONT) {
            return endClass == Classification.IN_FRONT ? Classification.IN_FRONT : Classification.SPANNING;
        }
        return endClass == Classification.BEHIND ? Classification.BEHIND : Classification.SPANNING;
    }

    public static class SplitResult{
        public LineSegment _inFrontOfPlane;
        public LineSegment _behindPlane;
        public SplitResult(LineSegment inFrontOfPlane, LineSegment behindPlane) {
            _inFrontOfPlane = inFrontOfPlane;
            _behindPlane = behindPlane;
        }
    }

    public SplitResult split(LineSegment lineSegment) {
        Classification beginClass = classify(lineSegment._begin);
        Classification endClass = classify(lineSegment._end);
        if (beginClass == Classification.ON_PLANE || endClass == Classification.ON_PLANE || beginClass == endClass) {
            return null;
        }

        Vector3 direction = lineSegment._end.subtract(lineSegment._begin);
        double denominator = _normal.dotProduct(direction);
        if (Math.abs(denominator) < 1e-3) {
            return null; // Parallel to plane
        }

        double d = -_normal.dotProduct(_pointOnPlane);
        double percent = -(d + _normal.dotProduct(lineSegment._begin)) / denominator;

        Vector3 intersection = lineSegment._begin.add(direction.multiply(percent));

        // Make 2 new line segments
        if (beginClass == Classification.IN_FRONT) {
            return new SplitResult(new LineSegment(lineSegment._begin, intersection), new LineSegment(intersection, lineSegment._end));
        }
        return new SplitResult(new LineSegment(lineSegment._end, intersection), new LineSegment(intersection, lineSegment._begin));
    }

    public LineSegment getPortionBehind(LineSegment lineSegment) {
        Classification c = classify(lineSegment);
        if (c == Classification.BEHIND || c == Classification.ON_PLANE) {
            return lineSegment;
        }
        if (c == Classification.SPANNING) {
            SplitResult r = split(lineSegment);
            if (r != null && r._behindPlane != null) {
                return r._behindPlane;
            }
        }
        return null;
    }
}
