import java.util.ArrayList;
import java.util.function.BiConsumer;

public class BoundingVolumeNode {
    public static final int NUM_CHILDREN = 8;
    private final PolyhedronMesh _polyhedronMesh;
    public BoundingVolumeNode[] _children;
    public Aabb _aabb; // Axis-aligned bounding box
    public ArrayList<Pair<PolyhedronMesh.Face, Material>> _facesAndMaterials;

    public BoundingVolumeNode(PolyhedronMesh polyhedronMesh) {
        _polyhedronMesh = polyhedronMesh;
        _children = null;
        _aabb = null;
        _facesAndMaterials = new ArrayList<>();
    }

    public void buildHierarchy(Aabb aabb, int depth) {
        _aabb = aabb;
        _aabb.rebuildPlanes();

        if (depth <= 0) {
            return;
        }

        _children = new BoundingVolumeNode[NUM_CHILDREN];
        for (int i = 0; i < NUM_CHILDREN; ++i) {
            _children[i] = new BoundingVolumeNode(_polyhedronMesh);
        }

        final double halfWidth = (aabb._max._x - aabb._min._x) / 2.0;
        final double halfHeight = (aabb._max._y - aabb._min._y) / 2.0;
        final double halfDepth = (aabb._max._z - aabb._min._z) / 2.0;

        final double minX = aabb._min._x;
        final double centreX = minX + halfWidth;
        final double maxX = aabb._max._x;

        final double minY = aabb._min._y;
        final double centreY = minY + halfHeight;
        final double maxY = aabb._max._y;

        final double minZ = aabb._min._z;
        final double centreZ = minZ + halfDepth;
        final double maxZ = aabb._max._z;
        
        _children[0].buildHierarchy(new Aabb(new Vector3(minX, centreY, minZ), new Vector3(centreX, maxY, centreZ)), depth - 1);
        _children[1].buildHierarchy(new Aabb(new Vector3(centreX, centreY, minZ), new Vector3(maxX, maxY, centreZ)), depth - 1);
        _children[2].buildHierarchy(new Aabb(new Vector3(minX, centreY, centreZ), new Vector3(centreX, maxY, maxZ)), depth - 1);
        _children[3].buildHierarchy(new Aabb(new Vector3(centreX, centreY, centreZ), new Vector3(maxX, maxY, maxZ)), depth - 1);

        _children[4].buildHierarchy(new Aabb(new Vector3(minX, minY, minZ), new Vector3(centreX, centreY, centreZ)), depth - 1);
        _children[5].buildHierarchy(new Aabb(new Vector3(centreX, minY, minZ), new Vector3(maxX, centreY, centreZ)), depth - 1);
        _children[6].buildHierarchy(new Aabb(new Vector3(minX, minY, centreZ), new Vector3(centreX, centreY, maxZ)), depth - 1);
        _children[7].buildHierarchy(new Aabb(new Vector3(centreX, minY, centreZ), new Vector3(maxX, centreY, maxZ)), depth - 1);
    }

    public void insertFace(PolyhedronMesh.Face face, Aabb faceAabb, Material material, int depth) {
        String spaces = " ";
        if (_children != null) {
            // Is this face better suited to be inside one of the children?
            for (int i = 0; i < _children.length; ++i) {
                if (_children[i]._aabb.contains(faceAabb)) {
                    LogFile.Instance.write(spaces.repeat(depth * 4) + "Child[" + i + "] can contain the face. This node: " + _children[i]._aabb.buildDebugString());
                    _children[i].insertFace(face, faceAabb, material, depth + 1);
                    return;
                }
            }
            LogFile.Instance.write(spaces.repeat(depth * 4) + "None of the children can contain the face. Inserting the face at this depth. This node: " + _aabb.buildDebugString());
        }
        else {
            LogFile.Instance.write(spaces.repeat(depth * 4) + "There are no children. Inserting the face at this depth. This node: " + _aabb.buildDebugString());
        }
        _facesAndMaterials.add(new Pair<>(face, material));
        LogFile.Instance.write(spaces.repeat(depth * 4) + "This node contains " + _facesAndMaterials.size() + " faces");
    }

    public void traceRay(Vector3 begin, Vector3 end, BiConsumer<PolyhedronMesh.Face, Material> faceFoundFn) {
        for (int i = 0; i < _facesAndMaterials.size(); ++i) {
            final Pair<PolyhedronMesh.Face, Material> pair = _facesAndMaterials.get(i);
            faceFoundFn.accept(pair.getFirst(), pair.getSecond());
        }
        if (_children == null) {
            return;
        }
        for (int i = 0; i < _children.length; ++i) {
            LineSegment clip = _children[i]._aabb.clipLineSegment(begin, end);
            if (clip != null) {
                _children[i].traceRay(clip._begin, clip._end, faceFoundFn);
            }
        }
    }

    public void printDebugInformation(int depth) {
        String spaces = " ";
        LogFile.Instance.write(spaces.repeat(depth * 4) + "Depth is " + depth + ". " + _aabb.buildDebugString() + ". Num faces is " + _facesAndMaterials.size());

        if (_children != null) {
            for (int i = 0; i < _children.length; ++i) {
                _children[i].printDebugInformation(depth + 1);
            }
        }
    }
}
