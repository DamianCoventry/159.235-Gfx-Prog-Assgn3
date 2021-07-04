import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;

public class BoundingVolumeHierarchy {
    private final PolyhedronMesh _polyhedronMesh;
    private final BoundingVolumeNode _root;

    public BoundingVolumeHierarchy(PolyhedronMesh polyhedronMesh) {
        _polyhedronMesh = polyhedronMesh;
        _root = new BoundingVolumeNode(_polyhedronMesh);
    }

    public void build(int maxDepth) {
        _root.buildHierarchy(determineLargestBox(), maxDepth);
    }

    public void insertFaces(PolyhedronMesh.Face[] faces, Material material) {
        if (faces == null) {
            LogFile.Instance.write("There are no flat faces");
            return;
        }
        LogFile.Instance.write("There are " + faces.length + " flat faces");
        for (int i = 0; i < faces.length; ++i) {
            Aabb faceAabb = faces[i].buildAabb(_polyhedronMesh.getVertices());
            if (_root._aabb.contains(faceAabb)) {
                LogFile.Instance.write("The BVH root node contains face " + i + ". " + faceAabb.buildDebugString());
                _root.insertFace(faces[i], faceAabb, material, 1);
            }
            else {
                LogFile.Instance.write("The BVH root node does not contain face " + i + ". " + faceAabb.buildDebugString());
            }
        }
    }

    public void traceRay(Ray rayLocalCoords, BiConsumer<PolyhedronMesh.Face, Material> faceFoundFn) {
        LineSegment clip = _root._aabb.clipRay(rayLocalCoords);
        if (clip != null) {
            _root.traceRay(clip._begin, clip._end, faceFoundFn);
        }
    }

    private Aabb determineLargestBox() {
        Aabb box = new Aabb();
        for (int piece = 0; piece < _polyhedronMesh.getPieces().length; ++piece) {
            adjustBox(box, _polyhedronMesh.getPieces()[piece].getFlatShadedFaces());

            Set<Integer> smoothShadedFaceGroups = _polyhedronMesh.getPieces()[piece].getGroupIds();
            Iterator<Integer> faceGroup = smoothShadedFaceGroups.iterator();
            while (faceGroup.hasNext()) {
                adjustBox(box, _polyhedronMesh.getPieces()[piece].getSmoothShadedFaceGroup(faceGroup.next()));
            }
        }
        return box;
    }

    private void adjustBox(Aabb box, PolyhedronMesh.Face[] faces) {
        if (faces == null) {
            return;
        }
        for (int i = 0; i < faces.length; ++i) {
            box.adjust(_polyhedronMesh.getVertices()[faces[i]._faceVertices[0]._vertexIndex]);
            box.adjust(_polyhedronMesh.getVertices()[faces[i]._faceVertices[1]._vertexIndex]);
            box.adjust(_polyhedronMesh.getVertices()[faces[i]._faceVertices[2]._vertexIndex]);
        }
        box.rebuildPlanes();
    }

    public void printDebugInformation() {
        _root.printDebugInformation(0);
    }
}
