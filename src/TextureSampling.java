import java.awt.image.BufferedImage;

public class TextureSampling {
    private final Filtering _filtering;
    private final TexCoordWrapping _texCoordWrapping;
    
    public TextureSampling(Filtering filtering, TexCoordWrapping texCoordWrapping) {
        _filtering = filtering;
        _texCoordWrapping = texCoordWrapping;
    }

    public enum Filtering { NEAREST, BILINEAR }
    public enum TexCoordWrapping { CLAMP, REPEAT }

    public Vector3 takeSample(double u, double v, BufferedImage bufferedImage) {
        if (_filtering == Filtering.BILINEAR) {
            return applyBilinearFiltering(u, v, bufferedImage);
        }
        int x = (int)(u * (double)bufferedImage.getWidth());
        int y = (int)(v * (double)bufferedImage.getHeight());
        return getPixel(x, y, bufferedImage);
    }

    private Vector3 getPixel(int x, int y, BufferedImage bufferedImage) {
        Pair<Integer, Integer> xy = applyTexCoordWrapping(x, y, bufferedImage);
        return Utility.ColourToVector3(bufferedImage.getRGB(xy.getFirst(), xy.getSecond()));
    }

    // https://www.scratchapixel.com/lessons/mathematics-physics-for-computer-graphics/interpolation/bilinear-filtering
    // https://handwiki.org/wiki/Bilinear_filtering
    private Vector3 applyBilinearFiltering(double u, double v, BufferedImage bufferedImage) {
        u = u * (double)bufferedImage.getWidth() - 0.5;
        v = v * (double)bufferedImage.getHeight() - 0.5;
        int x = (int)Math.floor(u);
        int y = (int)Math.floor(v);
        double ratioU = u - x;
        double ratioV = v - y;
        double oppositeU = 1.0 - ratioU;
        double oppositeV = 1.0 - ratioV;

        Vector3 tl = getPixel(x, y, bufferedImage);
        Vector3 tr = getPixel(x + 1, y, bufferedImage);
        Vector3 bl = getPixel(x, y + 1, bufferedImage);
        Vector3 br = getPixel(x + 1, y + 1, bufferedImage);

        Vector3 topRow = tl.multiply(oppositeU).add(tr.multiply(ratioU));
        Vector3 bottomRow = bl.multiply(oppositeU).add(br.multiply(ratioU));

        topRow = topRow.multiply(oppositeV);
        bottomRow = bottomRow.multiply(ratioV);

        return topRow.add(bottomRow);
    }

    private Pair<Integer, Integer> applyTexCoordWrapping(int x, int y, BufferedImage bufferedImage) {
        return new Pair<>(
                applyTexCoordWrapping(x, bufferedImage.getWidth()),
                applyTexCoordWrapping(y, bufferedImage.getHeight()));
    }

    private int applyTexCoordWrapping(int a, int size) {
        if (a < 0) {
            switch (_texCoordWrapping) {
                case CLAMP: a = 0; break;
                case REPEAT:
                default:
                    while (a < 0) a += size;
                    break;
            }
        }
        else if (a >= size) {
            switch (_texCoordWrapping) {
                case CLAMP: a = size - 1; break;
                case REPEAT:
                default:
                    while (a >= size) a -= size;
                    break;
            }
        }
        return a;
    }
}
