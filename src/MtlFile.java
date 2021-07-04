import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;

public class MtlFile {
    private final ArrayList<Material> _materials;
    private static final Material.IlluminationModel[] _illuminationModelLookup = new Material.IlluminationModel[]
            {
    /* index 0 */ Material.IlluminationModel.COLOUR_ON_AND_AMBIENT_OFF,
    /* index 1 */ Material.IlluminationModel.COLOUR_ON_AND_AMBIENT_ON,
    /* index 2 */ Material.IlluminationModel.HIGHLIGHT_ON,
    /* index 3 */ Material.IlluminationModel.REFLECTION_ON_AND_RAY_TRACE_ON,
    /* index 4 */ Material.IlluminationModel.GLASS_ON_RAY_TRACE_ON,
    /* index 5 */ Material.IlluminationModel.FRESNEL_ON_AND_RAY_TRACE_ON,
    /* index 6 */ Material.IlluminationModel.REFRACTION_ON_FRESNEL_OFF_AND_RAY_TRACE_ON,
    /* index 7 */ Material.IlluminationModel.REFRACTION_ON_FRESNEL_ON_AND_RAY_TRACE_ON,
    /* index 8 */ Material.IlluminationModel.REFLECTION_ON_AND_RAY_TRACE_OFF,
    /* index 9 */ Material.IlluminationModel.GLASS_ON_RAY_TRACE_OFF,
    /* index 10 */ Material.IlluminationModel.CASTS_SHADOWS_ONTO_INVISIBLE_SURFACES
            };

    public MtlFile(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        _materials = new ArrayList<>();
        Material m = null;
        String line = bufferedReader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.isEmpty() || line.charAt(0) == '#') {
                line = bufferedReader.readLine();
                continue;
            }
            String[] words = line.split("\\s");
            if (words.length < 2) {
                line = bufferedReader.readLine();
                continue;
            }
            if (words[0].equals("newmtl")) {
                if (m != null) {
                    _materials.add(m);
                }
                m = new Material(words[1]);
                line = bufferedReader.readLine();
                continue;
            }
            if (m == null) { // don't accept any data until we've found a "newmtl" statement.
                line = bufferedReader.readLine();
                continue;
            }

            if (words[0].equals("Ka")) {   // ambient colour
                var result = parseVector3(words);
                if (result.getFirst()) {
                    m.setAmbientColour(result.getSecond());
                }
            } else if (words[0].equals("Kd")) {   // diffuse colour
                var result = parseVector3(words);
                if (result.getFirst()) {
                    m.setDiffuseColour(result.getSecond());
                }
            } else if (words[0].equals("Ks")) {   // specular colour
                var result = parseVector3(words);
                if (result.getFirst()) {
                    m.setSpecularColour(result.getSecond());
                }
            } else if (words[0].equals("Ke")) {   // emissive colour
                var result = parseVector3(words);
                if (result.getFirst()) {
                    m.setEmissiveColour(result.getSecond());
                }
            } else if (words[0].equals("Ns")) {   // specular exponent
                // Blender stores its 'Roughness' value from its Principled BSDF within the Ns value.
                // It maps (0, 1) from the Blender GUI to (900, 0) for some weird reason.
                var result = parseDouble(words);
                if (result.getFirst()) {
                    m.setSpecularExponent(result.getSecond());
                }
            } else if (words[0].equals("Ni")) {   // index of refraction
                var result = parseDouble(words);
                if (result.getFirst()) {
                    m.setIndexOfRefraction(result.getSecond());
                }
            } else if (words[0].equals("d")) {   // dissolved. 0 == fully transparent, 1 = fully opaque
                var result = parseDouble(words);
                if (result.getFirst()) {
                    m.setDissolved(result.getSecond());
                }
            } else if (words[0].equals("Tr")) {   // transparency. 0 == fully opaque, 1 = fully transparent
                var result = parseDouble(words);
                if (result.getFirst()) {
                    m.setTransparency(result.getSecond());
                }
            } else if (words[0].equals("Tf")) {   // transmission filter colour
                var result = parseVector3(words);
                if (result.getFirst()) {
                    m.setTransmissionFilterColour(result.getSecond());
                }
            } else if (words[0].equals("illum")) {   // illumination model
                var result = parseInteger(words);
                if (result.getFirst() && result.getSecond() >= 0 && result.getSecond() < _illuminationModelLookup.length) {
                    m.setIlluminationModel(_illuminationModelLookup[result.getSecond()]);
                }
            } else if (words[0].equals("map_Ka")) {   // ambient texture
                var result = parseString(words);
                if (result.getFirst()) {
                    m.setAmbientTexture(ImageIO.read(new File(result.getSecond())));
                }
            } else if (words[0].equals("map_Kd")) {   // diffuse texture
                var result = parseString(words);
                if (result.getFirst()) {
                    m.setDiffuseTexture(ImageIO.read(new File(result.getSecond())));
                }
            } else if (words[0].equals("map_Ks")) {   // specular texture
                var result = parseString(words);
                if (result.getFirst()) {
                    m.setSpecularTexture(ImageIO.read(new File(result.getSecond())));
                }
            } else if (words[0].equals("map_Ke")) {   // emissive texture
                var result = parseString(words);
                if (result.getFirst()) {
                    m.setEmissiveTexture(ImageIO.read(new File(result.getSecond())));
                }
            } else if (words[0].equals("map_Ns")) {   // specular exponent texture
                var result = parseString(words);
                if (result.getFirst()) {
                    m.setSpecularExponentTexture(ImageIO.read(new File(result.getSecond())));
                }
            } else if (words[0].equals("map_Ni")) {   // index of refraction texture
                var result = parseString(words);
                if (result.getFirst()) {
                    m.setIndexOfRefractionTexture(ImageIO.read(new File(result.getSecond())));
                }
            } else if (words[0].equals("map_d")) {   // dissolved texture
                var result = parseString(words);
                if (result.getFirst()) {
                    m.setDissolvedTexture(ImageIO.read(new File(result.getSecond())));
                }
            } else if (words[0].equals("map_Tr")) {   // transparency texture
                var result = parseString(words);
                if (result.getFirst()) {
                    m.setTransparencyTexture(ImageIO.read(new File(result.getSecond())));
                }
            } else if (words[0].equals("map_Tf")) {   // transmission filter texture
                var result = parseString(words);
                if (result.getFirst()) {
                    m.setTransmissionFilterTexture(ImageIO.read(new File(result.getSecond())));
                }
            }
            line = bufferedReader.readLine();
        }
        if (m != null) {
            _materials.add(m);
        }
    }

    public int getMaterialCount() { return _materials.size(); }
    public Material getMaterial(int index) { if (index < 0 || index >= _materials.size()) return null; return _materials.get(index); }

    private Pair<Boolean, String> parseString(String[] words) {
        if (words.length == 2) {
            return new Pair<>(true, words[1]);
        }
        return new Pair<>(false, null);
    }

    private Pair<Boolean, Integer> parseInteger(String[] words) {
        if (words.length == 2) {
            return new Pair<>(true, Integer.parseInt(words[1]));
        }
        return new Pair<>(false, null);
    }

    private Pair<Boolean, Double> parseDouble(String[] words) {
        if (words.length == 2) {
            return new Pair<>(true, Double.parseDouble(words[1]));
        }
        return new Pair<>(false, null);
    }

    private Pair<Boolean, Vector3> parseVector3(String[] words) {
        if (words.length == 4) {
            return new Pair<>(true, new Vector3(
                    Double.parseDouble(words[1]),
                    Double.parseDouble(words[2]),
                    Double.parseDouble(words[3])
            ));
        }
        return new Pair<>(false, null);
    }
}
