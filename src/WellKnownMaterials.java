import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class WellKnownMaterials {
    public Material Glass;
    public Material Mirror;
    public Material ShinyGreen;
    public Material ShinyRed;
    public Material Mars;
    public Material Crate;
    public Material BlueBox;
    public Material OldPainting;

    public WellKnownMaterials() throws IOException {
        Glass = new Material("Glass");
        Glass.setDiffuseColour(new Vector3(0.5, 0.6, 0.7));
        Glass.setSpecularColour(new Vector3(0.8, 0.8, 0.8));
        Glass.setAlbedo(0, 0.0);
        Glass.setAlbedo(1, 0.5);
        Glass.setAlbedo(2, 0.1);
        Glass.setAlbedo(3, 0.8);
        Glass.setSpecularExponent(125.0);
        Glass.setIndexOfRefraction(1.5);

        Mirror = new Material("Mirror");
        Mirror.setDiffuseColour(new Vector3(1.0, 1.0, 1.0));
        Mirror.setSpecularColour(new Vector3(1.0, 1.0, 1.0));
        Mirror.setAlbedo(0, 0.0);
        Mirror.setAlbedo(1, 10.0);
        Mirror.setAlbedo(2, 0.8);
        Mirror.setAlbedo(3, 0.0);
        Mirror.setSpecularExponent(1425.0);
        Mirror.setIndexOfRefraction(1.0);

        ShinyGreen = new Material("ShinyGreen");
        ShinyGreen.setDiffuseColour(new Vector3(0.4, 1.0, 0.3));
        ShinyGreen.setSpecularColour(new Vector3(0.85, 0.85, 0.85));
        ShinyGreen.setAlbedo(0, 0.65);
        ShinyGreen.setAlbedo(1, 0.85);
        ShinyGreen.setAlbedo(2, 0.0);
        ShinyGreen.setAlbedo(3, 0.0);
        ShinyGreen.setSpecularExponent(40.0);
        ShinyGreen.setIndexOfRefraction(1.0);

        ShinyRed = new Material("ShinyRed");
        ShinyRed.setDiffuseColour(new Vector3(0.98, 0.25, 0.28));
        ShinyRed.setSpecularColour(new Vector3(0.82, 0.62, 0.62));
        ShinyRed.setAlbedo(0, 0.55);
        ShinyRed.setAlbedo(1, 0.75);
        ShinyRed.setAlbedo(2, 0.0);
        ShinyRed.setAlbedo(3, 0.0);
        ShinyRed.setSpecularExponent(32.0);
        ShinyRed.setIndexOfRefraction(1.0);

        Mars = new Material("Mars");
        Mars.setDiffuseTexture(ImageIO.read(new File("Mars.jpg")));
        Mars.setDiffuseColour(new Vector3(0.98, 0.97, 0.97));
        Mars.setSpecularColour(new Vector3(0.62, 0.62, 0.62));
        Mars.setAlbedo(0, 0.95);
        Mars.setAlbedo(1, 0.35);
        Mars.setAlbedo(2, 0.0);
        Mars.setAlbedo(3, 0.0);
        Mars.setSpecularExponent(24.0);
        Mars.setIndexOfRefraction(1.0);

        Crate = new Material("Crate");
        Crate.setDiffuseTexture(ImageIO.read(new File("Crate.jpg")));
        Crate.setDiffuseColour(new Vector3(0.977, 0.977, 0.977));
        Crate.setSpecularColour(new Vector3(0.65, 0.65, 0.65));
        Crate.setAlbedo(0, 0.73);
        Crate.setAlbedo(1, 0.11);
        Crate.setAlbedo(2, 0.0);
        Crate.setAlbedo(3, 0.0);
        Crate.setSpecularExponent(4.0);
        Crate.setIndexOfRefraction(1.0);

        BlueBox = new Material("OrangeBox");
        BlueBox.setDiffuseColour(new Vector3(0.185, 0.125, 0.827));
        BlueBox.setSpecularColour(new Vector3(0.85, 0.85, 0.85));
        BlueBox.setAlbedo(0, 0.65);
        BlueBox.setAlbedo(1, 0.81);
        BlueBox.setAlbedo(2, 0.0);
        BlueBox.setAlbedo(3, 0.0);
        BlueBox.setSpecularExponent(16.0);
        BlueBox.setIndexOfRefraction(1.0);

        OldPainting = new Material("Crate");
        OldPainting.setDiffuseTexture(ImageIO.read(new File("OldPainting.jpg")));
        OldPainting.setDiffuseColour(new Vector3(0.977, 0.977, 0.977));
        OldPainting.setSpecularColour(new Vector3(0.65, 0.65, 0.65));
        OldPainting.setAlbedo(0, 0.73);
        OldPainting.setAlbedo(1, 0.11);
        OldPainting.setAlbedo(2, 0.0);
        OldPainting.setAlbedo(3, 0.0);
        OldPainting.setSpecularExponent(2.0);
        OldPainting.setIndexOfRefraction(1.0);
    }
}
