package fyi.jackson.drew.roadquality.utils;

public class Vector3D {
    public float x, y, z;

    public Vector3D() {
        x = 0;
        y = 0;
        z = 0;
    }
    public Vector3D(float _x, float _y, float _z) {
        x = _x;
        y = _y;
        z = _z;
    }

    public Vector3D add(Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }

    public Vector3D subtract(Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    public float dot(Vector3D v) {
        return ((this.x * v.x) + (this.y * v.y) + (this.z * v.z));
    }

    public boolean greaterThanAny(Vector3D v) {
        return Math.abs(x) > v.x ||
                Math.abs(y) > v.y ||
                Math.abs(z) > v.z;
    }

    public float project(Vector3D onToVector) {
        return this.dot(onToVector) / onToVector.dot(onToVector);
    }
}
