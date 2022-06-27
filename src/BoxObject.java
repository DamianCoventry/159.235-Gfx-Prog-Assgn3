import java.util.ArrayList;

public class BoxObject extends Object {
    private final ArrayList<PlaneObject> _planes;

    public BoxObject(Vector3 position, Vector3 rotation, Vector3 size, Material[] materials, TextureSampling textureSampling) {
        super(position, rotation, size, materials, textureSampling);
        double halfX = size._x / 2.0;
        double halfY = size._y / 2.0;
        double halfZ = size._z / 2.0;
        _planes = new ArrayList<>();

        // Left
        _planes.add(new PlaneObject(
                        new Vector3(-halfX,0.0,  0.0),
                        new Vector3(0.0,0.0,-90.0),
                        size._y, size._z, materials, textureSampling));
        // Right
        _planes.add(new PlaneObject(
                        new Vector3( halfX,0.0, 0.0),
                        new Vector3(0.0,0.0,90.0),
                        size._y, size._z, materials, textureSampling));
        // Bottom
        _planes.add(new PlaneObject(
                        new Vector3(  0.0, -halfY,0.0),
                        new Vector3(180.0, 0.0,0.0),
                        size._x, size._z, materials, textureSampling));
        // Top
        _planes.add(new PlaneObject(
                        new Vector3(0.0,  halfY,0.0),
                        new Vector3(0.0, 0.0,0.0),
                        size._x, size._z, materials, textureSampling));
        // Back
        _planes.add(new PlaneObject(
                        new Vector3(  0.0, 0.0, -halfZ),
                        new Vector3(-90.0, 0.0, 0.0),
                        size._x, size._y, materials, textureSampling));
        // Front
        _planes.add(new PlaneObject(
                        new Vector3( 0.0, 0.0,  halfZ),
                        new Vector3(90.0, 0.0, 0.0),
                        size._x, size._y, materials, textureSampling));
    }

    public IntersectResult intersect(Ray ray) {
        IntersectResult result = new IntersectResult();
        double closetDistance = Double.MAX_VALUE;
        for (PlaneObject plane : _planes) {
            Ray lc = plane.toLocalCoords(ray);
            IntersectResult r = plane.intersect(lc);
            if (r._intersected && r._distance < closetDistance) {
                closetDistance = r._distance;
                result = plane.toWorldCoords(r);
            }
        }
        return result;
    }
}
