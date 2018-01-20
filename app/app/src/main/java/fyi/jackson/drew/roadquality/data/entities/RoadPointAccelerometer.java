package fyi.jackson.drew.roadquality.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.HashMap;
import java.util.Map;

import fyi.jackson.drew.roadquality.utils.maps.LatLng;

// A RoadPoint with only the Accelerometer bits

@Entity
public class RoadPointAccelerometer {

    // REQUIRED COLUMNS

    @ColumnInfo(name = "interpolated")
    private boolean interpolated;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;


    // OPTIONAL COLUMNS

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

    public RoadPointAccelerometer() {}


    // RoadPointAccelerometer GENERATORS
    public static RoadPointAccelerometer fromRoadPoint(RoadPoint rp) {
        RoadPointAccelerometer r = new RoadPointAccelerometer();

        r.setInterpolated(rp.isInterpolated());
        r.setTimestamp(rp.getTimestamp());
        r.setLongitude(rp.getLongitude());
        r.setLatitude(rp.getLatitude());

        r.setAx(rp.getAx());
        r.setAy(rp.getAy());
        r.setAz(rp.getAz());
        r.setGx(rp.getGx());
        r.setGy(rp.getGy());
        r.setGz(rp.getGz());
        r.setDuration(rp.getDuration());
        r.setDistance(rp.getDistance());
        r.setSpeed(rp.getSpeed());

        return r;
    }

    Map<String, Object> toHashMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("interpolated", interpolated);
        map.put("timestamp", timestamp);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("ax", ax);
        map.put("ay", ay);
        map.put("az", az);
        map.put("gx", gx);
        map.put("gy", gy);
        map.put("gz", gz);
        map.put("duration", duration);
        map.put("distance", distance);
        map.put("speed", speed);
        return map;
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
