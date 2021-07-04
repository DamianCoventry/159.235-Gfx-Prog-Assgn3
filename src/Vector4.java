public class Vector4 extends Vector3 {
    public double _w;
    public Vector4(double x, double y, double z, double w) {
        super(x, y, z);
        _w = w;
    }
    public Vector4(Vector3 v, double w) {
        super(v);
        _w = w;
    }
    public Vector3 toVector3() {
        return new Vector3(_x, _y, _z);
    }
}
