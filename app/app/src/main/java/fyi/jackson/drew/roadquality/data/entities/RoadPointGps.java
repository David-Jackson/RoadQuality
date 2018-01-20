package fyi.jackson.drew.roadquality.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.HashMap;
import java.util.Map;

import fyi.jackson.drew.roadquality.utils.maps.LatLng;

// A RoadPoint with only the GPS bits

@Entity
public class RoadPointGps {

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

    @ColumnInfo(name = "provider")
    private String provider;

    @ColumnInfo(name = "accuracy")
    private Float accuracy;

    @ColumnInfo(name = "altitude")
    private Double altitude;

    @ColumnInfo(name = "speed")
    private Double speed;

    public RoadPointGps() {}

    // RoadPoint GENERATORS
    public static RoadPointGps fromRoadPoint(RoadPoint rp) {
        RoadPointGps r = new RoadPointGps();

        r.setInterpolated(rp.isInterpolated());
        r.setTimestamp(rp.getTimestamp());
        r.setLongitude(rp.getLongitude());
        r.setLatitude(rp.getLatitude());

        r.setProvider(rp.getProvider());
        r.setAccuracy(rp.getAccuracy());
        r.setAltitude(rp.getAltitude());
        r.setSpeed(rp.getSpeed());

        return r;
    }

    Map<String, Object> toHashMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("interpolated", interpolated);
        map.put("timestamp", timestamp);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("provider", provider);
        map.put("accuracy", accuracy);
        map.put("altitude", altitude);
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

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
