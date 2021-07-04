import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private final BufferedImage _bufferedImage;
    public ImagePanel(BufferedImage bufferedImage) {
        _bufferedImage = bufferedImage;
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = (getWidth() - _bufferedImage.getWidth(null)) / 2;
        int y = (getHeight() - _bufferedImage.getHeight(null)) / 2;
        g.drawImage(_bufferedImage, x, y, null);
    }
}
