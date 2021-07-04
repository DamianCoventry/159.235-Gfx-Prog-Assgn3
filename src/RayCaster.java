public class RayCaster {
    private final RenderingOptions _ro;
    private final double _vfovRadians;
    private final double _aspectRatio;

    public RayCaster(RenderingOptions ro) throws Exception {
        if (ro._outputImageWidth <= 0 || ro._outputImageHeight <= 0) {
            throw new Exception("Invalid image size");
        }
        _ro = ro;
        _aspectRatio = (double)ro._outputImageWidth / (double)ro._outputImageHeight;
        _vfovRadians = Math.tan(Math.toRadians(ro._cameraVfovDegrees) / 2.0);
    }

    public Ray castRay(double x, double y, Camera camera) {
        Vector3 direction = new Vector3(
                (2.0 * x / (double)(_ro._outputImageWidth - 1) - 1.0) * _vfovRadians * _aspectRatio,
                -(2.0 * y / (double)(_ro._outputImageHeight - 1) - 1.0) * _vfovRadians,
                -1.0
        );

        Matrix4 rotY = Matrix4.rotateY(camera.getRotation()._y);
        Matrix4 rotX = Matrix4.rotateX(camera.getRotation()._x);
        direction = rotY.multiply(rotX).multiply(direction);

        return new Ray(camera.getPosition(), direction, x, y);
    }
}
