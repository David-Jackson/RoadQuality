# Methodology

Here is an explanation of various concepts from the app. This will hopefully add more context to the code and make it easier to understand.

## Detecting Bumps

We use the device's accelerometer to detect bumps in the road. The [`AccelerometerSensor`](https://github.com/David-Jackson/RoadQuality/blob/master/app/app/src/main/java/fyi/jackson/drew/roadquality/sensors/AccelerometerSensor.java) class detects these bumps by implementing a [`SensorEventListener`](https://developer.android.com/reference/android/hardware/SensorEventListener.html) and listening to the Accelerometer and Gravity sensors on the device. The Gravity sensor is basically a filtered Accelerometer value (I'm sure it's more complicated than that), and gives us a general idea of which way is 'up'. These two values are not always the same, in fact, they will only be the same with the device is not experiencing any additional acceleration (either moving at a constant speed, or not moving at all). The acceleration and gravity being felt by the device are in the form of a 3-dimensional vector, with an `x`, `y`, and `z` direction. We can represent these vectors as lines in 3D space:

![3D Vector](https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/3D_Vector.svg/300px-3D_Vector.svg.png)

The idea for this app is that we want to record bumps in the road, which would result in the acceleration being felt by the device to increase or decrease with respect to the direction of gravity. So how do we determine how the acceleration changes with respect to gravity? The answer is vector projection, or simpler terms, the component of a vector in terms of another vector. In 2-dimensions, vector projection looks like this: 

![2D Vector Projection](https://i.stack.imgur.com/Y7Gx8.png)

[Khan Academy](https://www.khanacademy.org/) has a great [video](https://www.khanacademy.org/math/linear-algebra/matrix-transformations/lin-trans-examples/v/introduction-to-projections) on how to do this with two 3-dimensional vectors. This app implements this in the [`Vector3D`](https://github.com/David-Jackson/RoadQuality/blob/master/app/app/src/main/java/fyi/jackson/drew/roadquality/utils/Vector3D.java) class: 

```
public float project(Vector3D onToVector) {
    return this.dot(onToVector) / onToVector.dot(onToVector);
}
public float dot(Vector3D v) {
    return ((this.x * v.x) + (this.y * v.y) + (this.z * v.z));
}
````
