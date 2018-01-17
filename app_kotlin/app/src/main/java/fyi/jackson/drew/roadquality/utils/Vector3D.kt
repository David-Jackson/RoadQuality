package fyi.jackson.drew.roadquality.utils

class Vector3D(var x: Float = 0f,
               var y: Float = 0f,
               var z: Float = 0f) {

    fun add(v: Vector3D): Vector3D {
        return Vector3D(x + v.x,
                y + v.y,
                z + v.z)
    }

    fun subtract(v: Vector3D): Vector3D {
        return Vector3D(x - v.x,
                y - v.y,
                z - v.z)
    }

    fun dot(v: Vector3D): Float {
        return ((x * v.x) + (y * v.y) + (z * v.z))
    }

    fun project(onToVector: Vector3D) : Float {
        return this.dot(onToVector) / onToVector.dot(onToVector);
    }

    override fun toString(): String {
        return "Vector3D(x: $x, y: $y, z: $z)";
    }

}
