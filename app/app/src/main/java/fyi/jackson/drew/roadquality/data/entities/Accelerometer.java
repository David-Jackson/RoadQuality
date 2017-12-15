package fyi.jackson.drew.roadquality.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Accelerometer {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "ts")
    private long timestamp;

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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
}
