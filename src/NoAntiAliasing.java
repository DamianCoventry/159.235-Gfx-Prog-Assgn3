import java.util.function.BiFunction;

public class NoAntiAliasing implements IAntiAliasing {
    private final Camera _camera;
    private final RayCaster _rayCaster;
    private final RayTracer _rayTracer;
    private final int _numRayBounces;

    public NoAntiAliasing(Camera camera, RayCaster rayCaster, RayTracer rayTracer, int numRayBounces) {
        _camera = camera;
        _rayCaster = rayCaster;
        _rayTracer = rayTracer;
        _numRayBounces = numRayBounces;
    }

    public static int getNumSamples() { return 1; }

    public static BiFunction<Double, Double, Integer> createToSampleIndexFn() {
        return (x, y) -> { return 0; };
    }

    @Override
    public Vector3 takeSample(double imageX, double imageY) {
        // The 0.5 causes the sample to be taken from the centre of the pixel.
        Ray rayWorldCoords = _rayCaster.castRay(imageX + 0.5, imageY + 0.5, _camera);
        return _rayTracer.traceRay(rayWorldCoords, _numRayBounces);
    }
}
