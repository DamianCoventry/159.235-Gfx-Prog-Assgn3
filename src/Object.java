public abstract class Object {
    private final Vector3 _position;
    private final Vector3 _rotation;
    private final Vector3 _scale;
    private final Material[] _materials;
    protected TextureSampling _textureSampling;

    public Object(Vector3 position, Vector3 rotation, Vector3 scale, Material[] materials, TextureSampling textureSampling) {
        _position = position;
        _rotation = rotation;
        _scale = scale;
        _materials = materials;
        _textureSampling = textureSampling;
    }

    public Vector3 getScale() { return _scale; }

    public Material[] getMaterials() { return _materials; }

    public Matrix4 getModelMatrix() {
        Matrix4 rotZ = Matrix4.rotateZ(_rotation._z);
        Matrix4 rotY = Matrix4.rotateY(_rotation._y);
        Matrix4 rotX = Matrix4.rotateX(_rotation._x);
        Matrix4 translate = Matrix4.translate(_position);
        return translate.multiply(rotX).multiply(rotY).multiply(rotZ);
    }

    public Matrix4 getInverseModelMatrix() {
        Matrix4 rotZ = Matrix4.rotateZ(-_rotation._z);
        Matrix4 rotY = Matrix4.rotateY(-_rotation._y);
        Matrix4 rotX = Matrix4.rotateX(-_rotation._x);
        Matrix4 translate = Matrix4.translate(_position.negate());
        return rotZ.multiply(rotY).multiply(rotX).multiply(translate);
    }

    public Ray toLocalCoords(Ray ray) {
        Matrix4 rotZ = Matrix4.rotateZ(-_rotation._z);
        Matrix4 rotY = Matrix4.rotateY(-_rotation._y);
        Matrix4 rotX = Matrix4.rotateX(-_rotation._x);
        Matrix3 rotZ3 = new Matrix3(rotZ);
        Matrix3 rotY3 = new Matrix3(rotY);
        Matrix3 rotX3 = new Matrix3(rotX);
        Matrix3 m3 = rotZ3.multiply(rotY3).multiply(rotX3);

        Matrix4 m4 = getInverseModelMatrix();
        return new Ray(
                m4.multiply(new Vector4(ray.getOrigin(), 1.0)).toVector3(),
                m3.multiply(ray.getDirection()), ray.getPixelX(), ray.getPixelY()
        );
    }

    public IntersectResult toWorldCoords(IntersectResult result) {
        Matrix4 m4 = getModelMatrix();

        Matrix3 rotZ3 = Matrix3.rotateZ(_rotation._z);
        Matrix3 rotY3 = Matrix3.rotateY(_rotation._y);
        Matrix3 rotX3 = Matrix3.rotateX(_rotation._x);
        Matrix3 m3 = rotX3.multiply(rotY3).multiply(rotZ3);

        IntersectResult r = new IntersectResult();
        r._intersected = result._intersected;
        r._distance = result._distance;
        r._point = m4.multiply(new Vector4(result._point, 1.0)).toVector3();
        r._normal = m3.multiply(result._normal);
        r._material = result._material;
        r._diffuseTextureColour = result._diffuseTextureColour;
        return r;
    }

    public abstract IntersectResult intersect(Ray rayLocalCoords);
}
