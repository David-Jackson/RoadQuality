package fyi.jackson.drew.roadquality.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import fyi.jackson.drew.roadquality.utils.Vector3D;


public abstract class AccelerometerSensor implements SensorEventListener {

    public static final String TAG = "AccelerometerSensor";

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor gravitySensor;

    private boolean sensorNeedsToSettle = true;
    private long timeWhenSensorIsSettled = 0;
    private long TEN_SENCONDS_IN_MILLIS = 10 * 1000;

    private Vector3D acceleration, gravity, jolt, MOTION_TRIGGER;
    private static final float DEFAULT_MOTION_TRIGGER_LIMIT = 2;


    public AccelerometerSensor(Context context) {
        this.acceleration = new Vector3D();
        this.gravity = new Vector3D();
        this.jolt = new Vector3D();
        this.MOTION_TRIGGER = new Vector3D(
                DEFAULT_MOTION_TRIGGER_LIMIT,
                DEFAULT_MOTION_TRIGGER_LIMIT,
                DEFAULT_MOTION_TRIGGER_LIMIT);

        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometerSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.gravitySensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    public void start() {
        sensorManager.registerListener(this, this.accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, this.gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        this.sensorNeedsToSettle = true;
        this.timeWhenSensorIsSettled = System.currentTimeMillis() + TEN_SENCONDS_IN_MILLIS;
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public boolean significantMotionDetected() {
        return this.jolt.greaterThanAny(this.MOTION_TRIGGER) && this.sensorSettled();
    }

    public boolean sensorSettled() {
        if (sensorNeedsToSettle) {
            boolean waitTimePassed = System.currentTimeMillis() > timeWhenSensorIsSettled;
            if (waitTimePassed) {
                sensorNeedsToSettle = false;
                Log.d(TAG, "sensorSettled: Wait time has passed");
            }
            return waitTimePassed;
        }
        return true;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            setAcceleration(x, y, z);
        } else if (mySensor.getType() == Sensor.TYPE_GRAVITY) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            setGravity(x, y, z);
        }
        updateJolt();
        onUpdate(this.acceleration, this.gravity, this.jolt);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public abstract void onUpdate(Vector3D a, Vector3D g, Vector3D j);

    // Getters and setters

    public Vector3D getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector3D acceleration) {
        this.acceleration = acceleration;
    }

    public void setAcceleration(float x, float y, float z) {
        this.acceleration = new Vector3D(x, y, z);
    }

    public Vector3D getGravity() {
        return gravity;
    }

    public void setGravity(Vector3D gravity) {
        this.gravity = gravity;
    }

    public void setGravity(float x, float y, float z) {
        this.gravity = new Vector3D(x, y, z);
    }

    public Vector3D getJolt() {
        return jolt;
    }

    public void setJolt(Vector3D jolt) {
        this.jolt = jolt;
    }

    public void setJolt(float x, float y, float z) {
        this.jolt = new Vector3D(x, y, z);
    }

    public void updateJolt() {
        this.jolt = this.acceleration.subtract(this.gravity);
    }

    public void setTriggerLimit(float limit) {
        MOTION_TRIGGER = new Vector3D(limit, limit, limit);
    }

    public Vector3D getTriggerLimit() {
        return this.MOTION_TRIGGER;
    }
}
