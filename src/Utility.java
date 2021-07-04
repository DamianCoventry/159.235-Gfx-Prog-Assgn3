public class Utility {
    public static Vector3 ColourToVector3(int colour) {
        int red = (colour & 0x00ff0000) >> 16;
        int green = (colour & 0x0000ff00) >> 8;
        int blue = colour & 0x000000ff;
        return new Vector3((double)red / 255.0, (double)green / 255.0, (double)blue / 255.0);
    }

    public static int Vector3ToColour(Vector3 unitVector) {
        unitVector = preventOverflow(unitVector);
        return ((int)(unitVector._x * 255.0) << 16) | ((int)(unitVector._y * 255.0) << 8) | (int)(unitVector._z * 255.0);
    }

    public static Vector3 preventOverflow(Vector3 v) {
        double max = Math.max(v._x, Math.max(v._y, v._z));
        if (max > 1.0) {
            return v.multiply(1.0 / max);
        }
        return v;
    }
}
