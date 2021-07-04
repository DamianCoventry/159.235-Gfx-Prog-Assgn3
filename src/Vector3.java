import java.text.DecimalFormat;

public class Vector3 {
    public double _x, _y, _z;
    public Vector3() {
        _x = _y = _z = 0;
    }
    public Vector3(double x, double y, double z) {
        _x = x;
        _y = y;
        _z = z;
    }
    public Vector3(Vector3 v) {
        _x = v._x;
        _y = v._y;
        _z = v._z;
    }

    public double dotProduct() {
        return _x * _x + _y * _y + _z * _z;
    }

    public double dotProduct(Vector3 v) {
        return _x * v._x + _y * v._y + _z * v._z;
    }

    public Vector3 crossProduct(Vector3 v) {
        return new Vector3(
                _y * v._z - _z * v._y,
                _z * v._x - _x * v._z,
                _x * v._y - _y * v._x);
    }

    public double magnitude() {
        return Math.sqrt(dotProduct());
    }

    public Vector3 negate() {
        return new Vector3(-_x, -_y, -_z);
    }

    public Vector3 add(Vector3 v) {
        return new Vector3(_x + v._x, _y + v._y, _z + v._z);
    }

    public Vector3 subtract(Vector3 v) {
        return new Vector3(_x - v._x, _y - v._y, _z - v._z);
    }

    public Vector3 multiply(Vector3 v) {
        return new Vector3(_x * v._x, _y * v._y, _z * v._z);
    }

    public Vector3 multiply(double value) {
        return new Vector3(_x * value, _y * value, _z * value);
    }

    public Vector3 divide(double value) {
        if (value == 0.0) {
            return this;
        }
        return new Vector3(_x / value, _y / value, _z / value);
    }

    public void normalise() {
        double length = magnitude();
        if (length != 0.0) {
            _x /= length;
            _y /= length;
            _z /= length;
        }
    }

    public Vector3 makeNormalised() {
        double length = magnitude();
        if (length != 0.0) {
            return new Vector3(_x / length, _y / length, _z / length);
        }
        return new Vector3();
    }

    public static Vector3 reflect(Vector3 incidentUnit, Vector3 normal) {
        // https://math.stackexchange.com/questions/13261/how-to-get-a-reflection-vector
        return incidentUnit.subtract(normal.multiply(2.0 * incidentUnit.dotProduct(normal)));
    }

    public static Vector3 refract(Vector3 incidentUnit, Vector3 normal, double etaT, double etaI) {
        // https://en.wikipedia.org/wiki/Snell%27s_law
        double incidentCos = - Math.max(-1.0, Math.min(1.0, incidentUnit.dotProduct(normal)));
        if (incidentCos < 0.0) {
            return refract(incidentUnit, normal.negate(), etaI, etaT); // if the ray comes from the inside the object, swap the air and the media
        }
        double eta = etaI / etaT;
        double k = 1 - eta * eta * (1 - incidentCos*incidentCos);
        return k < 0.0 ?
                new Vector3(1.0,0.0,0.0) :
                incidentUnit.multiply(eta).add(normal.multiply(eta * incidentCos - Math.sqrt(k)));
    }

    public String toString() {
        var decimalFormat = new DecimalFormat("0.0");
        return "(" + decimalFormat.format(_x) + ", " + decimalFormat.format(_y) + ", " + decimalFormat.format(_z) + ")";
    }
}
