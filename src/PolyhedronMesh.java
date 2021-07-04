import java.util.HashMap;
import java.util.Set;

public class PolyhedronMesh {
    private final String _name;
    private Vector3[] _vertices;
    private Vector3[] _normals;
    private TexCoord[] _texCoords;
    private Piece[] _pieces;

    public PolyhedronMesh(String name) {
        _name = name;
        _vertices = null;
        _normals = null;
        _texCoords = null;
        _pieces = null;
    }

    public String getName() { return _name; }

    public Vector3[] getVertices() { return _vertices; }
    public void setVertices(Vector3[] vertices) { _vertices = vertices.clone(); }

    public Vector3[] getNormals() { return _normals; }
    public void setNormals(Vector3[] normals) { _normals = normals.clone(); }

    public TexCoord[] getTexCoords() { return _texCoords; }
    public void setTexCoords(TexCoord[] texCoords) { _texCoords = texCoords.clone(); }

    public Piece[] getPieces() { return _pieces; }
    public void setPieces(Piece[] pieces) { _pieces = pieces.clone(); }

    public void resolveMaterials(Material[] materials) {
        for (int i = 0; i < _pieces.length; ++i) {
            _pieces[i].resolveMaterial(materials);
        }
    }

    public static class FaceVertex {
        public int _vertexIndex;
        public int _texCoordIndex;
        public int _normalIndex;
        public FaceVertex(int v, int vt, int vn) {
            _vertexIndex = v;
            _texCoordIndex = vt;
            _normalIndex = vn;
        }
    }

    public static class Face {
        public FaceVertex[] _faceVertices;
        public Face(FaceVertex[] faceVertices) {
            _faceVertices = faceVertices;
        }
        public Aabb buildAabb(Vector3[] vertices) {
            Aabb box = new Aabb();
            for (int i = 0; i < _faceVertices.length; ++i) {
                box.adjust(vertices[_faceVertices[i]._vertexIndex]);
            }
            box.rebuildPlanes();
            return box;
        }
    }

    public static class Piece {
        private final String _materialName;
        private final HashMap<Integer, Face[]> _smoothShadedFaceGroups;
        private Face[] _flatShadedFaces;
        private Material _material;

        public Piece(String materialName) {
            _materialName = materialName;
            _smoothShadedFaceGroups = new HashMap<>();
            _flatShadedFaces = null;
            _material = null;
        }

        public Material getMaterial() { return _material; }

        public void resolveMaterial(Material[] materials) {
            for (int i = 0; i < materials.length; ++i) {
                if (materials[i].getName().equals(_materialName)) {
                    _material = materials[i];
                    break;
                }
            }
        }

        public Face[] getFlatShadedFaces() { return _flatShadedFaces; }
        public void setFlatShadedFaces(Face[] flatShadedFaces) { _flatShadedFaces = flatShadedFaces; }

        public Set<Integer> getGroupIds() {
            return _smoothShadedFaceGroups.keySet();
        }

        public Face[] getSmoothShadedFaceGroup(int groupId) {
            if (_smoothShadedFaceGroups.containsKey(groupId)) {
                return _smoothShadedFaceGroups.get(groupId);
            }
            return null;
        }
        public void addSmoothShadedFaceGroup(int groupId, Face[] faces) {
            _smoothShadedFaceGroups.put(groupId, faces);
        }
    }
}
