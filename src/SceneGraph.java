import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class SceneGraph {
    public static final double FAR_CLIP_PLANE = 1000.0;
    private static final int MAX_BVH_DEPTH = 2;
    private final ArrayList<SphereObject> _spheres;
    private final ArrayList<BoxObject> _boxes;
    private final PlaneObject _plane;
    private PolyhedronObject[] _objects;
    private BoundingVolumeHierarchy[] _bvhs;
    private PolyhedronMesh[] _meshes;
    private Material[] _materials;

    public SceneGraph(TextureSampling textureSampling) throws IOException {
        WellKnownMaterials materials = new WellKnownMaterials();

        _plane = new PlaneObject(
                new Vector3(-6.7, 9.6, -5.5),
                new Vector3(90.0, 0.0, -90.0),
                5.2, 7.1,
                new Material[]{ materials.OldPainting }, textureSampling);

        _spheres = new ArrayList<>();
        _spheres.add(new SphereObject(
                new Vector3(2.05, 3.5, -2.5),
                new Vector3(),
                1.10,
                new Material[] { materials.Mirror }, textureSampling));
        _spheres.add(new SphereObject(
                new Vector3( -2.2, 1.1, -0.5),
                new Vector3(),
                1.15,
                new Material[] { materials.Mirror }, textureSampling));
        _spheres.add(new SphereObject(
                new Vector3(-5.8,  5.2, -9.7),
                new Vector3(),
                1.25,
                new Material[] { materials.ShinyGreen }, textureSampling));
        _spheres.add(new SphereObject(
                new Vector3( -5.6,  1.21, -0.2),
                new Vector3(),
                1.28,
                new Material[] { materials.Mars }, textureSampling));
        _spheres.add(new SphereObject(
                new Vector3( -3.9,  0.85, 0.8),
                new Vector3(),
                0.65,
                new Material[] { materials.Glass }, textureSampling));
        _spheres.add(new SphereObject(
                new Vector3( -5.9,  0.60, 1.25),
                new Vector3(),
                0.55,
                new Material[] { materials.ShinyRed }, textureSampling));

        _boxes = new ArrayList<>();
        _boxes.add(new BoxObject(
                new Vector3(2.08, 0.50, 1.5),
                new Vector3(0.0, 203.0, 0.0),
                new Vector3(1.75, 1.0, 1.9),
                new Material[] { materials.BlueBox }, textureSampling));
        _boxes.add(new BoxObject(
                new Vector3(1.15, 1.25, -3.5),
                new Vector3(0.0, 25.0, 0.0),
                new Vector3(2.5, 2.5, 2.5),
                new Material[] { materials.Crate }, textureSampling));
        _boxes.add(new BoxObject(
                new Vector3(2.0, 1.20, -1.3),
                new Vector3(0.0, -26.0, 0.0),
                new Vector3(2.25, 2.25, 2.25),
                new Material[] { materials.Crate }, textureSampling));

        ObjFile objFile = loadPolyhedronMeshes();
        buildBoundingVolumeHierarchies(objFile);
        createPolyhedronObjects(objFile, textureSampling);
    }

    private ObjFile loadPolyhedronMeshes() throws IOException {
        ObjFile objFile = new ObjFile(new File("OldWarehouse.obj"));
        MtlFile mtlFile = new MtlFile(new File(objFile.getMaterialFileName()));

        _materials = new Material[mtlFile.getMaterialCount()];
        for (int i = 0; i < mtlFile.getMaterialCount(); ++i) {
            _materials[i] = mtlFile.getMaterial(i);
        }

        _meshes = new PolyhedronMesh[objFile.getMeshCount()];
        for (int i = 0; i < objFile.getMeshCount(); ++i) {
            _meshes[i] = objFile.getMesh(i);
            _meshes[i].resolveMaterials(_materials);
        }

        return objFile;
    }

    private void buildBoundingVolumeHierarchies(ObjFile objFile) {
        _bvhs = new BoundingVolumeHierarchy[objFile.getMeshCount()];
        for (int i = 0; i < objFile.getMeshCount(); ++i) {
            _bvhs[i] = buildBoundingVolumeHierarchy(_meshes[i]);
        }
    }

    private BoundingVolumeHierarchy buildBoundingVolumeHierarchy(PolyhedronMesh polyhedronMesh) {
        BoundingVolumeHierarchy bvh = new BoundingVolumeHierarchy(polyhedronMesh);
        bvh.build(MAX_BVH_DEPTH);

        for (int piece = 0; piece < polyhedronMesh.getPieces().length; ++piece) {
            PolyhedronMesh.Piece p = polyhedronMesh.getPieces()[piece];
            LogFile.Instance.write("Inserting flat faces for piece " + piece);
            bvh.insertFaces(p.getFlatShadedFaces(), p.getMaterial());

            Set<Integer> smoothShadedFaceGroups = polyhedronMesh.getPieces()[piece].getGroupIds();
            for (Integer groupId : smoothShadedFaceGroups) {
                LogFile.Instance.write("Inserting smooth faces for piece " + piece + ", group id " + groupId);
                bvh.insertFaces(p.getSmoothShadedFaceGroup(groupId), p.getMaterial());
            }
        }

        bvh.printDebugInformation();
        LogFile.Instance.close();
        //System.exit(0); // temp
        return bvh;
    }

    private void createPolyhedronObjects(ObjFile objFile, TextureSampling textureSampling) {
        _objects = new PolyhedronObject[objFile.getMeshCount()];
        long numFaces = 0;
        for (int i = 0; i < objFile.getMeshCount(); ++i) {
            PolyhedronMesh mesh = objFile.getMesh(i);
            _objects[i] = new PolyhedronObject(
                    new Vector3(), // The origin
                    new Vector3(), // Not rotated
                    new Vector3(1.0, 1.0, 1.0), // Not scaled
                    _materials, textureSampling, mesh, _bvhs[i]);
            numFaces += _objects[i].getNumFaces();
        }
        System.out.println("Loaded " + _meshes.length + " meshes. " + numFaces + " faces total.");
    }

    public IntersectResult intersect(Ray ray, ZBuffer zBuffer) {
        IntersectResult result = null;
        final double x = ray.getPixelX();
        final double y = ray.getPixelY();

        for (SphereObject sphere : _spheres) {
            Ray lc = sphere.toLocalCoords(ray);
            IntersectResult r = sphere.intersect(lc);
            if (r._intersected && zBuffer.testAndUpdate(x, y, r._distance)) {
                result = sphere.toWorldCoords(r);
            }
        }

        for (BoxObject box : _boxes) {
            Ray lc = box.toLocalCoords(ray);
            IntersectResult r = box.intersect(lc);
            if (r._intersected && zBuffer.testAndUpdate(x, y, r._distance)) {
                result = box.toWorldCoords(r);
            }
        }

        for (PolyhedronObject object : _objects) {
            Ray lc = object.toLocalCoords(ray);
            IntersectResult r = object.intersect(lc);
            if (r._intersected && zBuffer.testAndUpdate(x, y, r._distance)) {
                result = object.toWorldCoords(r);
            }
        }

        Ray lc = _plane.toLocalCoords(ray);
        IntersectResult r = _plane.intersect(lc);
        if (r._intersected && zBuffer.testAndUpdate(x, y, r._distance)) {
            result = _plane.toWorldCoords(r);
        }

        if (result == null || zBuffer.getDepthValue(x, y) >= FAR_CLIP_PLANE) {
            return new IntersectResult();
        }
        return result;
    }
}
