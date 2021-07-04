public class Matrix3 {
    private static final int Size = 3;
    public double[][] _m = new double[Size][Size];

    public Matrix3() {
        setIdentity();
    }

    public Matrix3(
            double m00, double m01, double m02,
            double m10, double m11, double m12,
            double m20, double m21, double m22) {
        _m[0][0] = m00; _m[0][1] = m01; _m[0][2] = m02;
        _m[1][0] = m10; _m[1][1] = m11; _m[1][2] = m12;
        _m[2][0] = m20; _m[2][1] = m21; _m[2][2] = m22;
    }

    public Matrix3(Matrix4 other) {
        _m[0][0] = other._m[0][0]; _m[0][1] = other._m[0][1]; _m[0][2] = other._m[0][2];
        _m[1][0] = other._m[1][0]; _m[1][1] = other._m[1][1]; _m[1][2] = other._m[1][2];
        _m[2][0] = other._m[2][0]; _m[2][1] = other._m[2][1]; _m[2][2] = other._m[2][2];
    }

    public void setIdentity() {
        _m[0][0] = 1.0; _m[0][1] = 0.0; _m[0][2] = 0.0;
        _m[1][0] = 0.0; _m[1][1] = 1.0; _m[1][2] = 0.0;
        _m[2][0] = 0.0; _m[2][1] = 0.0; _m[2][2] = 1.0;
    }

    public Matrix3 multiply(Matrix3 other) {
        Matrix3 result = new Matrix3();
        for (int i = 0; i < Size; ++i) {
            result._m[i][0] = (_m[i][0] * other._m[0][0]) +
                              (_m[i][1] * other._m[1][0]) +
                              (_m[i][2] * other._m[2][0]);

            result._m[i][1] = (_m[i][0] * other._m[0][1]) +
                              (_m[i][1] * other._m[1][1]) +
                              (_m[i][2] * other._m[2][1]);

            result._m[i][2] = (_m[i][0] * other._m[0][2]) +
                              (_m[i][1] * other._m[1][2]) +
                              (_m[i][2] * other._m[2][2]);
        }
        return result;
    }

    public Vector3 multiply(Vector3 v) {
        return new Vector3(
                (_m[0][0] * v._x) + (_m[0][1] * v._y) + (_m[0][2] * v._z),
                (_m[1][0] * v._x) + (_m[1][1] * v._y) + (_m[1][2] * v._z),
                (_m[2][0] * v._x) + (_m[2][1] * v._y) + (_m[2][2] * v._z)
        );
    }

    public static Matrix3 rotateX(double angleXDegrees)
    {
        double angleXRadians = Math.toRadians(angleXDegrees);
        double cosX = Math.cos(angleXRadians);
        double sinX = Math.sin(angleXRadians);
        return new Matrix3(
                1.0, 0.0, 0.0,
                0.0, cosX, -sinX,
                0.0, sinX, cosX
        );
    }

    public static Matrix3 rotateY(double angleYDegrees)
    {
        double angleYRadians = Math.toRadians(angleYDegrees);
        double cosY = Math.cos(angleYRadians);
        double sinY = Math.sin(angleYRadians);
        return new Matrix3(
                cosY, 0.0, sinY,
                0.0, 1.0, 0.0,
                -sinY, 0.0, cosY
        );
    }

    public static Matrix3 rotateZ(double angleZDegrees)
    {
        double angleZRadians = Math.toRadians(angleZDegrees);
        double cosZ = Math.cos(angleZRadians);
        double sinZ = Math.sin(angleZRadians);
        return new Matrix3(
                cosZ, -sinZ, 0.0,
                sinZ, cosZ, 0.0,
                0.0, 0.0, 1.0
        );
    }
}
