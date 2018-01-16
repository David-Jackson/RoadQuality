package fyi.jackson.drew.roadquality.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import fyi.jackson.drew.roadquality.data.dao.AccelerometerDao;
import fyi.jackson.drew.roadquality.data.dao.GpsDao;
import fyi.jackson.drew.roadquality.data.dao.RoadPointDao;
import fyi.jackson.drew.roadquality.data.entities.Accelerometer;
import fyi.jackson.drew.roadquality.data.entities.Gps;
import fyi.jackson.drew.roadquality.data.entities.RoadPoint;

@Database(entities = {Accelerometer.class, Gps.class, RoadPoint.class}, version = 9)
public abstract class AppDatabase extends RoomDatabase{
    public static final String DATABASE_NAME = "RoadQualityDatabase.db";
    public abstract AccelerometerDao accelerometerDao();
    public abstract GpsDao gpsDao();
    public abstract RoadPointDao roadPointDao();
}
