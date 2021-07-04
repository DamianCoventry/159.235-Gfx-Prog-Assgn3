public class TexCoordGenerator {
    public static TexCoord spherical(Vector3 unitVector) {
        return new TexCoord(
                Math.atan2(unitVector._z, unitVector._x) / (2.0 * Math.PI) + 0.5,
                Math.acos(unitVector._y) / Math.PI
        );
    }
}
