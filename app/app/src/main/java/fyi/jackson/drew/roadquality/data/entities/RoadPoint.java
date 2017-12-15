package fyi.jackson.drew.roadquality.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import fyi.jackson.drew.roadquality.utils.maps.LatLng;

@Entity
public class RoadPoint {

    // REQUIRED COLUMNS

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "trip_id")
    private long tripId;

    @ColumnInfo(name = "interpolated")
    private boolean interpolated;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;


    // OPTIONAL COLUMNS

    @ColumnInfo(name = "provider")
    private String provider;

    @ColumnInfo(name = "accuracy")
    private float accuracy;

    @ColumnInfo(name = "altitude")
    private double altitude;

    @ColumnInfo(name = "ax")
    private float ax;

    @ColumnInfo(name = "ay")
    private float ay;

    @ColumnInfo(name = "az")
    private float az;

    @ColumnInfo(name = "gx")
    private float gx;

    @ColumnInfo(name = "gy")
    private float gy;

    @ColumnInfo(name = "gz")
    private float gz;

    @ColumnInfo(name = "jx")
    private float jx;

    @ColumnInfo(name = "jy")
    private float jy;

    @ColumnInfo(name = "jz")
    private float jz;

    @ColumnInfo(name = "duration")
    private float duration;

    @ColumnInfo(name = "distance")
    private float distance;


    // RoadPoint GENERATORS
    public static RoadPoint fromGps(Gps gps, long tripId) {
        RoadPoint roadPoint = new RoadPoint();

        roadPoint.setTripId(tripId);
        roadPoint.setInterpolated(false);
        roadPoint.setTimestamp(gps.getTimestamp());
        roadPoint.setLatitude(gps.getLatitude());
        roadPoint.setLongitude(gps.getLongitude());

        roadPoint.setProvider(gps.getProvider());
        roadPoint.setAccuracy(gps.getAccuracy());
        roadPoint.setAltitude(gps.getAltitude());

        return roadPoint;
    }

    public static RoadPoint fromAccelerometer(Accelerometer accelerometer,
                                              LatLng latLng, long tripId, float duration, float distance) {
        RoadPoint roadPoint = new RoadPoint();

        roadPoint.setTripId(tripId);
        roadPoint.setInterpolated(true);
        roadPoint.setTimestamp(accelerometer.getTimestamp());
        roadPoint.setLatitude(latLng.lat());
        roadPoint.setLongitude(latLng.lng());

        roadPoint.setAx(accelerometer.getAx());
        roadPoint.setAy(accelerometer.getAy());
        roadPoint.setAz(accelerometer.getAz());
        roadPoint.setGx(accelerometer.getGx());
        roadPoint.setGy(accelerometer.getGy());
        roadPoint.setGz(accelerometer.getGz());
        roadPoint.setJx(accelerometer.getJx());
        roadPoint.setJy(accelerometer.getJy());
        roadPoint.setJz(accelerometer.getJz());
        roadPoint.setDuration(duration);
        roadPoint.setDistance(distance);

        return roadPoint;
    }


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public boolean isInterpolated() {
        return interpolated;
    }

    public void setInterpolated(boolean interpolated) {
        this.interpolated = interpolated;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getAx() {
        return ax;
    }

    public void setAx(float ax) {
        this.ax = ax;
    }

    public float getAy() {
        return ay;
    }

    public void setAy(float ay) {
        this.ay = ay;
    }

    public float getAz() {
        return az;
    }

    public void setAz(float az) {
        this.az = az;
    }

    public float getGx() {
        return gx;
    }

    public void setGx(float gx) {
        this.gx = gx;
    }

    public float getGy() {
        return gy;
    }

    public void setGy(float gy) {
        this.gy = gy;
    }

    public float getGz() {
        return gz;
    }

    public void setGz(float gz) {
        this.gz = gz;
    }

    public float getJx() {
        return jx;
    }

    public void setJx(float jx) {
        this.jx = jx;
    }

    public float getJy() {
        return jy;
    }

    public void setJy(float jy) {
        this.jy = jy;
    }

    public float getJz() {
        return jz;
    }

    public void setJz(float jz) {
        this.jz = jz;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
