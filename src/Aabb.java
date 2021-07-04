public class Aabb {
    public Vector3 _min;
    public Vector3 _max;
    public Plane[] _planes;

    public Aabb() {
        _min = new Vector3();
        _max = new Vector3();
        _planes = null;
    }

    public Aabb(Vector3 min, Vector3 max) {
        _min = min;
        _max = max;
        rebuildPlanes();
    }

    public void adjust(Vector3 v) {
        _min._x = Math.min(_min._x, v._x);
        _min._y = Math.min(_min._y, v._y);
        _min._z = Math.min(_min._z, v._z);
        _max._x = Math.max(_max._x, v._x);
        _max._y = Math.max(_max._y, v._y);
        _max._z = Math.max(_max._z, v._z);
    }
    
    public boolean contains(Aabb other) {
        return (other._min._x >= _min._x) &&
               (other._min._y >= _min._y) &&
               (other._min._z >= _min._z) &&
               (other._max._x <= _max._x) &&
               (other._max._y <= _max._y) &&
               (other._max._z <= _max._z);
    }

    public LineSegment clipRay(Ray ray) {
        Vector3 lineSegmentEnd = ray.getOrigin().add(ray.getDirection().multiply(ray.getMagnitude()));
        return clipLineSegment(ray.getOrigin(), lineSegmentEnd);
    }

    public LineSegment clipLineSegment(Vector3 begin, Vector3 end) {
        LineSegment lineSegment = new LineSegment(begin, end);
        if (_planes == null) {
            rebuildPlanes();
        }
        for (int i = 0; i < 6; ++i) {
            LineSegment ls = _planes[i].getPortionBehind(lineSegment);
            if (ls == null) {
                return null;
            }
            lineSegment = ls;
        }
        return lineSegment;
    }

    public void rebuildPlanes() {
        _planes = new Plane[6];
        _planes[0] = new Plane(new Vector3(_min._x, 0.0, 0.0), new Vector3(-1.0, 0.0, 0.0));
        _planes[1] = new Plane(new Vector3(0.0, _min._y, 0.0), new Vector3(0.0, -1.0, 0.0));
        _planes[2] = new Plane(new Vector3(0.0, 0.0, _min._z), new Vector3(0.0, 0.0, -1.0));
        _planes[3] = new Plane(new Vector3(_max._x, 0.0, 0.0), new Vector3(1.0, 0.0, 0.0));
        _planes[4] = new Plane(new Vector3(0.0, _max._y, 0.0), new Vector3(0.0, 1.0, 0.0));
        _planes[5] = new Plane(new Vector3(0.0, 0.0, _max._z), new Vector3(0.0, 0.0, 1.0));
    }

    public String buildDebugString() {
        return "AABB min = " + _min.toString() + ", max = " + _max.toString();
    }
}
