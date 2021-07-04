import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

/*
 * Assignment Requirements:
 *    A plane surface with a texture pasted on it
 *          - the SceneGraph class has a member named '_plane' that has the file OldPainting.jpg mapped onto it
 *    The visible faces of a uniform colour cube correctly placed and shaded
 *          - the SceneGraph class has a member named '_boxes'. the item at index 0 is a solid colour box (material BlueBox).
 *    A uniform colour sphere with a shiny surface (i.e. specular highlight)
 *          - the SceneGraph class has a member named '_spheres'. the item at index 2 is a solid colour shiny sphere (material ShinyGreen).
 *    A sphere with a texture pasted on it (e.g. the beach ball)
 *          - the SceneGraph class has a member named '_spheres'. the item at index 3 is a sphere that has the file Mars.jpg mapped onto it (material Mars).
 *
 * To get full marks you should generate a scene that adds more to the above. Marks will be awarded for initiative. Be creative!
 *
 *    What I've added that is 'more to the above':
 *          Texture mapped boxes
 *          Anti-aliasing (Super-sampling)
 *          Reflection (mirrored spheres)
 *          Refraction (a 'glass' sphere)
 *          Shadows
 *          Multiple coloured lights
 *          Polyhedron objects
 *              Loaded from .obj files
 *              Materials loaded from .mtl files
 *              Texture mapping via Barycentric coordinates
 *              Bounding volume hierarchies
 *          Bilinear texture filtering
 *          Texture coordinate wrapping
 *          Spherical environment mapping
 */

public class MainWindow extends JFrame {
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 768;
    private BufferedImage _outputImage;
    private final RenderingOptions _ro;

    public MainWindow() {
        _ro = new RenderingOptions();
        //_ro.setDevelopment();

        setTitle("159.235 Assignment 3");
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // The act of rendering takes some time. If we rendered right now the user will not see the main window
        // until the render is done. For a slightly better UX let's display some text, allow JSwing to paint
        // that text upon the window, then start the render.
        displayMessageToUser();

        try {
            Camera camera = new Camera(_ro._cameraPosition, _ro._cameraEulersDegrees);
            RayCaster rayCaster = new RayCaster(_ro);
            RayTracer rayTracer = new RayTracer(_ro);

            // Put a JSwing timer in the window message queue now, then yield this thread. When the timer
            // fires in 100ms time we will perform the render. This allows a window paint message to get
            // through and show the user the JLabel text above.

            callOnce(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    IAntiAliasing sampler;
                    if (_ro._antiAliasing == RenderingOptions.AntiAliasing.SUPER_SAMPLING) {
                        sampler = new SuperSamplingAA(camera, rayCaster, rayTracer, _ro._numberRayBounces);
                    }
                    else {
                        sampler = new NoAntiAliasing(camera, rayCaster, rayTracer, _ro._numberRayBounces);
                    }
                    rayTraceImage(sampler);
                }
            });
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayMessageToUser() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        Font font = new Font("MS Sans Serif", Font.PLAIN, 20);
        JLabel titleText = new JLabel("Rendering the image... please wait");
        titleText.setFont(font);
        titleText.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panel.add(titleText);

        addRenderingOptions(panel);
        setContentPane(panel);
    }

    private void addRenderingOptions(JPanel parentPanel) {
        Font font = new Font("MS Sans Serif", Font.PLAIN, 12);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("_outputImageWidth: " + _ro._outputImageWidth);
        label.setFont(font);
        panel.add(label);

        label = new JLabel("_outputImageHeight: " + _ro._outputImageHeight);
        label.setFont(font);
        panel.add(label);

        label = new JLabel("_outputImageFilename: " + _ro._outputImageFilename);
        label.setFont(font);
        panel.add(label);

        label = new JLabel("_outputImageFileFormat: " + _ro._outputImageFileFormat);
        label.setFont(font);
        panel.add(label);

        label = new JLabel("_envMappingImageFilename: " + _ro._envMappingImageFilename);
        label.setFont(font);
        panel.add(label);

        label = new JLabel("_cameraPosition: " + _ro._cameraPosition.toString());
        label.setFont(font);
        panel.add(label);

        label = new JLabel("_cameraEulersDegrees: " + _ro._cameraEulersDegrees.toString());
        label.setFont(font);
        panel.add(label);

        label = new JLabel("_cameraVfovDegrees: " + _ro._cameraVfovDegrees);
        label.setFont(font);
        panel.add(label);

        label = new JLabel("_numberRayBounces: " + _ro._numberRayBounces);
        label.setFont(font);
        panel.add(label);

        label = new JLabel("_antiAliasing: " + _ro._antiAliasing);
        label.setFont(font);
        panel.add(label);

        label = new JLabel("_texCoordWrapping: " + _ro._texCoordWrapping);
        label.setFont(font);
        panel.add(label);

        label = new JLabel("_textureFiltering: " + _ro._textureFiltering);
        label.setFont(font);
        panel.add(label);

        label = new JLabel("_ambientLight: " + _ro._ambientLight.toString());
        label.setFont(font);
        panel.add(label);

        parentPanel.add(panel, BorderLayout.CENTER);
    }

    private void rayTraceImage(IAntiAliasing sampler) {
        _outputImage = new BufferedImage(_ro._outputImageWidth, _ro._outputImageHeight, BufferedImage.TYPE_INT_RGB);

        System.out.println("Starting render...");
        long w = _ro._outputImageWidth;
        long h = _ro._outputImageHeight;
        long totalPixels = w * h;

        long startTimeMs = System.currentTimeMillis();
        long oldMs = startTimeMs;
        for (long y = 0; y < h; ++y) {
            for (long x = 0; x < w; ++x) {
                _outputImage.setRGB((int)x, (int)y, Utility.Vector3ToColour(sampler.takeSample(x, y)));

                long nowMs = System.currentTimeMillis();
                if (nowMs - oldMs >= 3000) {
                    oldMs = nowMs;
                    long numCompletedPixels = y * w + x;
                    System.out.println("Completed " + numCompletedPixels + "/" + totalPixels + " pixels (" +
                            (numCompletedPixels * 100 / totalPixels) + "%)");
                }
            }
        }

        System.out.println("Render complete. " + (System.currentTimeMillis() - startTimeMs) + "ms elapsed.");

        saveToFile(_outputImage);

        callOnce(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { drawImage(); }
        });
    }

    private void saveToFile(BufferedImage bufferedImage) {
        try {
            File outputFile = new File(_ro._outputImageFilename);
            ImageIO.write(bufferedImage, _ro._outputImageFileFormat, outputFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawImage() {
        ImagePanel panel = new ImagePanel(_outputImage);
        panel.setPreferredSize(new Dimension(_ro._outputImageWidth, _ro._outputImageHeight));
        getContentPane().removeAll();
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().validate();
        getContentPane().repaint();
    }

    private void callOnce(ActionListener actionListener) {
        Timer timer = new Timer(100, actionListener);
        timer.setRepeats(false);
        timer.start();
    }

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.pack();
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }
}
