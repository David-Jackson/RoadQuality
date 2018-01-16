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
    private Float accuracy;

    @ColumnInfo(name = "altitude")
    private Double altitude;

    @ColumnInfo(name = "ax")
    private Float ax;

    @ColumnInfo(name = "ay")
    private Float ay;

    @ColumnInfo(name = "az")
    private Float az;

    @ColumnInfo(name = "gx")
    private Float gx;

    @ColumnInfo(name = "gy")
    private Float gy;

    @ColumnInfo(name = "gz")
    private Float gz;

    @ColumnInfo(name = "duration")
    private Float duration;

    @ColumnInfo(name = "distance")
    private Float distance;

    @ColumnInfo(name = "speed")
    private Double speed;


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
        roadPoint.setSpeed(gps.getSpeed());

        return roadPoint;
    }

    public static RoadPoint fromAccelerometer(Accelerometer accelerometer,
                                              LatLng latLng, long tripId, float duration, float distance, double speed) {
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
        roadPoint.setDuration(duration);
        roadPoint.setDistance(distance);
        roadPoint.setSpeed(speed);

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

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Float getAx() {
        return ax;
    }

    public void setAx(Float ax) {
        this.ax = ax;
    }

    public Float getAy() {
        return ay;
    }

    public void setAy(Float ay) {
        this.ay = ay;
    }

    public Float getAz() {
        return az;
    }

    public void setAz(Float az) {
        this.az = az;
    }

    public Float getGx() {
        return gx;
    }

    public void setGx(Float gx) {
        this.gx = gx;
    }

    public Float getGy() {
        return gy;
    }

    public void setGy(Float gy) {
        this.gy = gy;
    }

    public Float getGz() {
        return gz;
    }

    public void setGz(Float gz) {
        this.gz = gz;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
