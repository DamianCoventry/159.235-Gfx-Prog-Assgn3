import java.util.function.BiFunction;

public class SuperSamplingAA implements IAntiAliasing {
    private final Camera _camera;
    private final RayCaster _rayCaster;
    private final RayTracer _rayTracer;
    private final int _numRayBounces;

    private static final int NUM_SUB_PIXEL_ROWS = 2;
    private static final int NUM_SUB_PIXEL_COLS = 2;
    private final double[] _subPixelBiases;

    // An implementation of Super sampling anti-aliasing (SSAA). Sometimes called Full screen anti-aliasing (FSAA).
    // https://en.wikipedia.org/wiki/Spatial_anti-aliasing#Super_sampling_/_full-scene_anti-aliasing
    public SuperSamplingAA(Camera camera, RayCaster rayCaster, RayTracer rayTracer, int numRayBounces) {
        _camera = camera;
        _rayCaster = rayCaster;
        _rayTracer = rayTracer;
        _numRayBounces = numRayBounces;
        _subPixelBiases = new double[] { 0.25, 0.75 };
    }

    public static int getNumSamples() { return 4; }

    public static BiFunction<Double, Double, Integer> createToSampleIndexFn() {
        return (x, y) -> {
            int intX = (int)((double)x); // cast away the whole part
            int intY = (int)((double)y);
            double decimalPartX = x - intX;
            double decimalPartY = y - intY;
            if (decimalPartX < 0.5) {
                return decimalPartY < 0.5 ? 0 : 1;
            }
            return decimalPartY < 0.5 ? 2 : 3;
        };
    }

    @Override
    public Vector3 takeSample(double imageX, double imageY) {
        Vector3 combinedSample = new Vector3();
        for (int row = 0; row < NUM_SUB_PIXEL_ROWS; ++row) {
            for (int col = 0; col < NUM_SUB_PIXEL_COLS; ++col) {
                Ray rayWorldCoords = _rayCaster.castRay(imageX + _subPixelBiases[col], imageY + _subPixelBiases[row], _camera);
                combinedSample = combinedSample.add(_rayTracer.traceRay(rayWorldCoords, _numRayBounces));
            }
        }
        double sampleScale = 1.0 / (NUM_SUB_PIXEL_ROWS * NUM_SUB_PIXEL_COLS);
        return combinedSample.multiply(sampleScale);
    }
}
