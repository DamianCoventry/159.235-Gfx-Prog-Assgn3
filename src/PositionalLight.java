public class PositionalLight {
    public Vector3 _colour;
    public Vector3 _position;
    public double _power;
    public double _radius;
    public PositionalLight(Vector3 colour, Vector3 position, double power, double radius) throws Exception {
        _colour = colour;
        _position = position;
        _power = power;
        if (radius <= 0.0) {
            throw new Exception("Radius must be > 0");
        }
        _radius = radius;
    }

    // https://gamedev.stackexchange.com/questions/56897/glsl-light-attenuation-color-and-intensity-formula
    public double calculateAttenuation(double distance) {
        double attenuation = Math.min(1.0, Math.max(0.0, 1.0 - distance / _radius));
        return attenuation * attenuation;
    }
}
