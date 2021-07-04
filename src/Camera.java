public class Camera {
    private final Vector3 _position;
    private final Vector3 _rotation;
    public Camera(Vector3 position, Vector3 rotation) {
        _position = position;
        _rotation = rotation;
    }

    public Vector3 getPosition() { return _position; }
    public Vector3 getRotation() { return _rotation; }
}
