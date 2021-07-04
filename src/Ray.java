public class Ray {
    public static final double DEFAULT_LINE_SEGMENT_LENGTH = 1000.0;
    private final Vector3 _origin;
    private final Vector3 _direction;
    private final double _lineSegmentLength;
    private final double _pixelX;
    private final double _pixelY;

    public Ray(Vector3 origin, Vector3 direction, double pixelX, double pixelY) {
        this(origin, direction, DEFAULT_LINE_SEGMENT_LENGTH, pixelX, pixelY);
    }

    public Ray(Vector3 origin, Vector3 direction, double lineSegmentLength, double pixelX, double pixelY) {
        _origin = origin;
        _direction = direction.makeNormalised();
        _lineSegmentLength = lineSegmentLength;
        _pixelX = pixelX;
        _pixelY = pixelY;
    }

    public Vector3 getOrigin() { return _origin; }
    public Vector3 getDirection() { return _direction; }
    public double getMagnitude() { return _lineSegmentLength; }
    public double getPixelX() { return _pixelX; }
    public double getPixelY() { return _pixelY; }
}
