# Methodology

Here is an explanation of various concepts from the app. This will hopefully add more context to the code and make it easier to understand.

## Detecting Bumps

We use the device's accelerometer to detect bumps in the road. The [`AccelerometerSensor`](https://github.com/David-Jackson/RoadQuality/blob/master/app/app/src/main/java/fyi/jackson/drew/roadquality/sensors/AccelerometerSensor.java) class detects these bumps by implementing a [`SensorEventListener`](https://developer.android.com/reference/android/hardware/SensorEventListener.html) and listening to the Accelerometer and Gravity sensors on the device. The Gravity sensor is basically a filtered Accelerometer value (I'm sure it's more complicated than that), and gives us a general idea of which way is 'up'. These two values are not always the same, in fact, they will only be the same with the device is not experiencing any additional acceleration (either moving at a constant speed, or not moving at all). The acceleration and gravity being felt by the device are in the form of a 3-dimensional vector, with an `x`, `y`, and `z` direction. We can represent these vectors as lines in 3D space:

![3D Vector](https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/3D_Vector.svg/300px-3D_Vector.svg.png)

The idea for this app is that we want to record bumps in the road, which would result in the acceleration being felt by the device to increase or decrease with respect to the direction of gravity. So how do we determine how the acceleration changes with respect to gravity? The answer is vector projection, or simpler terms, the component of a vector in terms of another vector. In 2-dimensions, vector projection looks like this: 

![2D Vector Projection](https://i.stack.imgur.com/Y7Gx8.png)

[Khan Academy](https://www.khanacademy.org/) has a great [video](https://www.khanacademy.org/math/linear-algebra/matrix-transformations/lin-trans-examples/v/introduction-to-projections) on how to do this with two 3-dimensional vectors. This app implements this in the [`Vector3D`](https://github.com/David-Jackson/RoadQuality/blob/master/app/app/src/main/java/fyi/jackson/drew/roadquality/utils/Vector3D.java) class: 

``` java
public float project(Vector3D onToVector) {
    return this.dot(onToVector) / onToVector.dot(onToVector);
}
public float dot(Vector3D v) {
    return ((this.x * v.x) + (this.y * v.y) + (this.z * v.z));
}
````

## Locating Bumps

**_This code is located in [`maps.java`](https://github.com/David-Jackson/RoadQuality/blob/master/app/app/src/main/java/fyi/jackson/drew/roadquality/utils/maps.java)._**

After we have the bumps recorded with a specific timestamp, we are then able to use timestamped GPS data to interpolate between two points to get the location of the bump. However, since the Earth is not flat, we are not able to use linear interpolation to get our result; instead we need to use spherical interpolation.

![Spherical Linear Interpolation](http://www.fastgraph.com/slerp1.gif)

So we can imagine the GPS and the accelerometer recording independently at the same time. The GPS would record a point with a timestamp, the accelerometer would record multiple points with different timestamps, and eventually the GPS would record a different point at a different time. For simplicity's sake, we will imagine there are two GPS points with one accelerometer point sometime in between. 

Using the two GPS points and timestamps, we can calculate an average speed and heading between those two points:

``` java
double gpsSpeed =
        geometry.spherical.computeDistanceBetween(startLatLng, endLatLng) /
                (endGps.getTimestamp() - startGps.getTimestamp()); //meters per ms

double heading = geometry.spherical.computeHeading(startLatLng, endLatLng);
```

Distance between two GPS points is calculated by:

``` java

public static double computeDistanceBetween(LatLng startLatLng, LatLng endLatLng, double r) {
    return utils.angleBetween(startLatLng, endLatLng) * r; // where r is earth's radius
}

...

public static double angleBetween(LatLng startLatLng, LatLng endLatLng) {
    double a = longitudeRad(startLatLng); // lng in radians
    double c = latitudeRad(startLatLng); // lat in radians
    double b = longitudeRad(endLatLng);
    double d = latitudeRad(endLatLng);
    return 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((c - d) / 2), 2)
            + Math.cos(c) * Math.cos(d) * Math.pow(Math.sin((a - b) / 2), 2)));
}
```

And heading is calculated by: 

``` java
public static double computeHeading(LatLng startLatLng, LatLng endLatLng) {
    double c = utils.latitudeRad(startLatLng);
    double d = utils.longitudeRad(startLatLng);
    double a = utils.latitudeRad(endLatLng);
    double b = utils.longitudeRad(endLatLng) - d;
    return utils.limitBetween(
            utils.rad2Deg(
                    Math.atan2(
                            Math.sin(b) * Math.cos(a),
                            Math.cos(c) * Math.sin(a) - Math.sin(c) * Math.cos(a) * Math.cos(b)
                    )
            ),
            -180, 180 // limiting the result between -180 and 180
    );
}
```

Now, by looking at the starting GPS point and the accelerometer point, we can calculate an interpolated duration and distance:

``` java
double interpolatedDuration = accelerometer.getTimestamp() - startGps.getTimestamp();
double interpolatedDistance = gpsSpeed * interpolatedDuration;
```

Now we now how far the accelerometer point was recorded from the starting GPS point. Using the heading we calculated earlier, we can now calcuate the latitude and longitude for this accelerometer point:

``` java
LatLng interpolatedLatLng = geometry.spherical.computeOffset(startLatLng, 
                                                    interpolatedDistance, heading);

public static LatLng computeOffset(
        LatLng startLatLng, double distance, double heading, double r) {
    distance /= r; // r is earth's radius
    heading = utils.deg2Rad(heading);
    double e = utils.latitudeRad(startLatLng);
    double a = utils.longitudeRad(startLatLng);
    double d = Math.cos(distance);
    double b = Math.sin(distance);
    double f = Math.sin(e);
    e = Math.cos(e);

    double g = d * f + b * e * Math.cos(heading);
    return new LatLng(
            utils.rad2Deg(Math.asin(g)),
            utils.rad2Deg(a + Math.atan2(b * e * Math.sin(heading), d - f * g))
    );
}
```


**All calculations are based on the [Google Maps Javascript API Geometry Library](https://developers.google.com/maps/documentation/javascript/reference#spherical) and translated to Java by [Drew Jackson](https://github.com/David-Jackson/)**