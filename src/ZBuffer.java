import java.util.Arrays;
import java.util.function.*;

public class ZBuffer {
    private final int _width;
    private final int _height;
    private final double[][] _depthValues;
    private final BiFunction<Double, Double, Integer> _toSampleIndex;

    public ZBuffer(int width, int height, int numSamples, BiFunction<Double, Double, Integer> toSampleIndex) {
        _width = width;
        _height = height;
        _toSampleIndex = toSampleIndex;
        _depthValues = new double[numSamples][];
        for (int i = 0; i < numSamples; ++i) {
            _depthValues[i] = new double[_width * _height];
            Arrays.fill(_depthValues[i], Double.MAX_VALUE);
        }
    }

    public boolean testAndUpdate(double x, double y, double value) {
        if (x < 0 || y < 0 || x >= _width || y >= _height) {
            return false;
        }
        int index = _toSampleIndex.apply(x, y);
        int offset = (int)y * _width + (int)x;
        if (value >= _depthValues[index][offset]) {
            return false;
        }
        _depthValues[index][offset] = value;
        return true;
    }

    public double getDepthValue(double x, double y) {
        if (x < 0 || y < 0 || x >= _width || y >= _height) {
            return Double.MAX_VALUE;
        }
        int index = _toSampleIndex.apply(x, y);
        int offset = (int)y * _width + (int)x;
        return _depthValues[index][offset];
    }
}
