import java.io.*;
import java.util.ArrayList;

public class ObjFile {
    private final ArrayList<PolyhedronMesh> _meshes;
    private String _materialFileName;

    public ObjFile(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        
        int totalVertices = 0, totalTexCoords = 0, totalNormals = 0;
        int numVertices = 0, numTexCoords = 0, numNormals = 0;
        _meshes = new ArrayList<>();
        _materialFileName = null;

        ArrayList<Vector3> vertices = new ArrayList<>();
        ArrayList<Vector3> normals = new ArrayList<>();
        ArrayList<TexCoord> texCoords = new ArrayList<>();
        ArrayList<PolyhedronMesh.Piece> pieces = new ArrayList<>();
        ArrayList<PolyhedronMesh.Face> faces = new ArrayList<>();
        int smoothingGroupId = -1;

        PolyhedronMesh m = null;
        PolyhedronMesh.Piece p = null;

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
            if (words[0].equals("mtllib")) {
                _materialFileName = words[1];
                line = bufferedReader.readLine();
                continue;
            }
            if (words[0].equals("o")) {
                if (p != null) {
                    addPolyhedronFaces(p, smoothingGroupId, faces, pieces);
                    p = null;
                }
                if (m != null) {
                    addPolyhedronMesh(m, vertices, normals, texCoords, pieces);
                }

                totalVertices += numVertices;
                totalTexCoords += numTexCoords;
                totalNormals += numNormals;
                numVertices = 0;
                numTexCoords = 0;
                numNormals = 0;

                m = new PolyhedronMesh(words[1]);
                line = bufferedReader.readLine();
                continue;
            }
            if (m == null) { // don't accept any data until we've found an "o" statement.
                line = bufferedReader.readLine();
                continue;
            }
            if (words[0].equals("v")) { // vertex
                ++numVertices;
                var result = parseVector3(words);
                if (result.getFirst()) {
                    vertices.add(result.getSecond());
                }
            }
            else if (words[0].equals("vt")) { // vertex tex coord
                ++numTexCoords;
                var result = parseTexCoord(words);
                if (result.getFirst()) {
                    texCoords.add(result.getSecond());
                }
            }
            else if (words[0].equals("vn")) { // vertex normal
                ++numNormals;
                var result = parseVector3(words);
                if (result.getFirst()) {
                    normals.add(result.getSecond());
                }
            }
            else if (words[0].equals("usemtl")) { // material assignment
                if (p != null) {
                    addPolyhedronFaces(p, smoothingGroupId, faces, pieces);
                }
                smoothingGroupId = -1;
                p = new PolyhedronMesh.Piece(words[1]);
            }
            else if (words[0].equals("s")) { // smooth shading
                if (words[1].equals("off")) {
                    smoothingGroupId = -1;
                }
                else {
                    var result = parseInteger(words);
                    if (result.getFirst()) {
                        smoothingGroupId = result.getSecond();
                    }
                }
            }
            else if (words[0].equals("f")) { // polyhedron face
                if (words.length != 4) {
                    throw new IOException("Only triangles supported");
                }
                var faceVertices = new PolyhedronMesh.FaceVertex[words.length - 1];
                for (int i = 1; i < words.length; ++i) {
                    String[] indices = words[i].split("/");
                    if (indices.length == 3) {
                        // Note that indices within the file are 1 based, NOT 0 based.
                        faceVertices[i - 1] = new PolyhedronMesh.FaceVertex(
                                Integer.parseInt(indices[0]) - totalVertices - 1,
                                Integer.parseInt(indices[1]) - totalTexCoords - 1,
                                Integer.parseInt(indices[2]) - totalNormals - 1);
                    }
                }
                faces.add(new PolyhedronMesh.Face(faceVertices));
            }
            line = bufferedReader.readLine();
        }
        if (p != null) {
            addPolyhedronFaces(p, smoothingGroupId, faces, pieces);
        }
        if (m != null) {
            addPolyhedronMesh(m, vertices, normals, texCoords, pieces);
        }
    }

    public String getMaterialFileName() { return _materialFileName; }

    public int getMeshCount() { return _meshes.size(); }
    public PolyhedronMesh getMesh(int index) {
        if (index < 0 || index >= _meshes.size()) {
            return null;
        }
        return _meshes.get(index);
    }

    private void addPolyhedronFaces(PolyhedronMesh.Piece p, int smoothingGroupId, ArrayList<PolyhedronMesh.Face> faces,
                                    ArrayList<PolyhedronMesh.Piece> pieces) {
        var faceArray = new PolyhedronMesh.Face[faces.size()];
        faceArray = faces.toArray(faceArray);
        if (smoothingGroupId == -1) {
            p.setFlatShadedFaces(faceArray);
        }
        else {
            p.addSmoothShadedFaceGroup(smoothingGroupId, faceArray);
        }
        faces.clear();
        pieces.add(p);
    }

    private void addPolyhedronMesh(PolyhedronMesh m, ArrayList<Vector3> vertices, ArrayList<Vector3> normals,
                                   ArrayList<TexCoord> texCoords, ArrayList<PolyhedronMesh.Piece> pieces) {
        var vertexArray = new Vector3[vertices.size()];
        vertexArray = vertices.toArray(vertexArray);
        m.setVertices(vertexArray);
        vertices.clear();

        var normalArray = new Vector3[normals.size()];
        normalArray = normals.toArray(normalArray);
        m.setNormals(normalArray);
        normals.clear();

        var texCoordArray = new TexCoord[texCoords.size()];
        texCoordArray = texCoords.toArray(texCoordArray);
        m.setTexCoords(texCoordArray);
        texCoords.clear();

        var pieceArray = new PolyhedronMesh.Piece[pieces.size()];
        pieceArray = pieces.toArray(pieceArray);
        m.setPieces(pieceArray);
        pieces.clear();

        _meshes.add(m);
    }

    private Pair<Boolean, Integer> parseInteger(String[] words) {
        if (words.length == 2) {
            return new Pair<>(true, Integer.parseInt(words[1]));
        }
        return new Pair<>(false, null);
    }

    private Pair<Boolean, TexCoord> parseTexCoord(String[] words) {
        if (words.length == 3) {
            return new Pair<>(true, new TexCoord(
                    Double.parseDouble(words[1]),
                    Double.parseDouble(words[2])
            ));
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
