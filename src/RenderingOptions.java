public class RenderingOptions {
    public RenderingOptions() {
        setProduction();
    }

    public void setProduction() {
        _outputImageWidth = 640;
        _outputImageHeight = 480;
        _outputImageFilename = "RayTracedImage.png";
        _outputImageFileFormat = "png";

        _envMappingImageFilename = "EnvironmentMap.jpg";

        _cameraPosition = new Vector3(0.0, 7.0, 12.5);
        _cameraEulersDegrees = new Vector3(-5.0, 0.0, 0.0);
        _cameraEulersDegrees = new Vector3(0.0, 0.0, 0.0);
        _cameraVfovDegrees = 60.0;

        _numberRayBounces = 3;
        _antiAliasing = RenderingOptions.AntiAliasing.SUPER_SAMPLING;
        _texCoordWrapping = TextureSampling.TexCoordWrapping.REPEAT;
        _textureFiltering = TextureSampling.Filtering.BILINEAR;
        _ambientLight = new Vector3(0.095, 0.095, 0.095);
    }

    public void setDevelopment() {
        setProduction();
        _outputImageWidth = 320;
        _outputImageHeight = 240;
        _numberRayBounces = 2;
        _antiAliasing = RenderingOptions.AntiAliasing.NONE;
        _textureFiltering = TextureSampling.Filtering.NEAREST;
    }

    public int _outputImageWidth;
    public int _outputImageHeight;
    public String _outputImageFilename;
    public String _outputImageFileFormat;

    public String _envMappingImageFilename;

    public Vector3 _cameraPosition;
    public Vector3 _cameraEulersDegrees;
    public double _cameraVfovDegrees;

    public int _numberRayBounces;
    public enum AntiAliasing { NONE, SUPER_SAMPLING }
    public AntiAliasing _antiAliasing;
    public TextureSampling.TexCoordWrapping _texCoordWrapping;
    public TextureSampling.Filtering _textureFiltering;
    public Vector3 _ambientLight;
}
