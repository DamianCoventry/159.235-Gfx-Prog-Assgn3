import java.awt.image.BufferedImage;

public class Material {
    private final String _name;
    private IlluminationModel _illuminationModel;
    private Vector3 _ambientColour;
    private Vector3 _diffuseColour;
    private Vector3 _specularColour;
    private Vector3 _emissiveColour;
    private double _specularExponent;
    private double _indexOfRefraction;
    private double _dissolved;
    private double _transparency;
    private Vector3 _transmissionFilterColour;
    private BufferedImage _ambientTexture;
    private BufferedImage _diffuseTexture;
    private BufferedImage _specularTexture;
    private BufferedImage _emissiveTexture;
    private BufferedImage _specularExponentTexture;
    private BufferedImage _indexOfRefractionTexture;
    private BufferedImage _dissolvedTexture;
    private BufferedImage _transparencyTexture;
    private BufferedImage _transmissionFilterTexture;
    private double[] _albedo;

    public Material(String name) {
        _name = name;
        setDefaults();
    }

    public void setDefaults() {
        _illuminationModel = IlluminationModel.HIGHLIGHT_ON;
        _albedo = new double[4];
        _albedo[0] = _albedo[1] = 1.0;
        _albedo[2] = _albedo[3] = 0.0;
    }

    public enum IlluminationModel {
        COLOUR_ON_AND_AMBIENT_OFF, COLOUR_ON_AND_AMBIENT_ON, HIGHLIGHT_ON, REFLECTION_ON_AND_RAY_TRACE_ON,
        GLASS_ON_RAY_TRACE_ON, FRESNEL_ON_AND_RAY_TRACE_ON, REFRACTION_ON_FRESNEL_OFF_AND_RAY_TRACE_ON,
        REFRACTION_ON_FRESNEL_ON_AND_RAY_TRACE_ON, REFLECTION_ON_AND_RAY_TRACE_OFF, GLASS_ON_RAY_TRACE_OFF,
        CASTS_SHADOWS_ONTO_INVISIBLE_SURFACES
    }

    public String getName() { return _name; }

    public IlluminationModel getIlluminationModel() { return _illuminationModel; }
    public void setIlluminationModel(IlluminationModel illuminationModel) { _illuminationModel = illuminationModel; }

    public Vector3 getAmbientColour() { return _ambientColour; }
    public void setAmbientColour(Vector3 ambientColour) { _ambientColour = ambientColour; }

    public Vector3 getDiffuseColour() { return _diffuseColour; }
    public void setDiffuseColour(Vector3 diffuseColour) { _diffuseColour = diffuseColour; }

    public Vector3 getSpecularColour() { return _specularColour; }
    public void setSpecularColour(Vector3 specularColour) { _specularColour = specularColour; }

    public Vector3 getEmissiveColour() { return _emissiveColour; }
    public void setEmissiveColour(Vector3 emissiveColour) { _emissiveColour = emissiveColour; }

    public double getSpecularExponent() { return _specularExponent; }
    public void setSpecularExponent(double specularExponent) { _specularExponent = specularExponent; }

    public double getIndexOfRefraction() { return _indexOfRefraction; }
    public void setIndexOfRefraction(double indexOfRefraction) { _indexOfRefraction = indexOfRefraction; }

    public double getDissolved() { return _dissolved; }
    public void setDissolved(double dissolved) { _dissolved = dissolved; }

    public double getTransparency() { return _transparency; }
    public void setTransparency(double transparency) { _transparency = transparency; }

    public Vector3 getTransmissionFilterColour() { return _transmissionFilterColour; }
    public void setTransmissionFilterColour(Vector3 transmissionFilterColour) { _transmissionFilterColour = transmissionFilterColour; }

    public BufferedImage getAmbientTexture() { return _ambientTexture; }
    public void setAmbientTexture(BufferedImage ambientTexture) { _ambientTexture = ambientTexture; }

    public BufferedImage getDiffuseTexture() { return _diffuseTexture; }
    public void setDiffuseTexture(BufferedImage diffuseTexture) { _diffuseTexture = diffuseTexture; }

    public BufferedImage getSpecularTexture() { return _specularTexture; }
    public void setSpecularTexture(BufferedImage specularTexture) { _specularTexture = specularTexture; }

    public BufferedImage getEmissiveTexture() { return _emissiveTexture; }
    public void setEmissiveTexture(BufferedImage emissiveTexture) { _emissiveTexture = emissiveTexture; }

    public BufferedImage getSpecularExponentTexture() { return _specularExponentTexture; }
    public void setSpecularExponentTexture(BufferedImage specularExponentTexture) { _specularExponentTexture = specularExponentTexture; }

    public BufferedImage getIndexOfRefractionTexture() { return _indexOfRefractionTexture; }
    public void setIndexOfRefractionTexture(BufferedImage indexOfRefractionTexture) { _indexOfRefractionTexture = indexOfRefractionTexture; }

    public BufferedImage getDissolvedTexture() { return _dissolvedTexture; }
    public void setDissolvedTexture(BufferedImage dissolvedTexture) { _dissolvedTexture = dissolvedTexture; }

    public BufferedImage getTransparencyTexture() { return _transparencyTexture; }
    public void setTransparencyTexture(BufferedImage transparencyTexture) { _transparencyTexture = transparencyTexture; }

    public BufferedImage getTransmissionFilterTexture() { return _transmissionFilterTexture; }
    public void setTransmissionFilterTexture(BufferedImage transmissionFilterTexture) { _transmissionFilterTexture = transmissionFilterTexture; }

    public double getAlbedo(int index) { if (index < 0 || index > 3) return 0.0; return _albedo[index]; }
    public void setAlbedo(int index, double albedo) { if (index >= 0 && index < 4) _albedo[index] = albedo; }
}
