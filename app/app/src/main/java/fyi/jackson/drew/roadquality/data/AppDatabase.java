package fyi.jackson.drew.roadquality.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import fyi.jackson.drew.roadquality.data.dao.AccelerometerDao;
import fyi.jackson.drew.roadquality.data.dao.GpsDao;
import fyi.jackson.drew.roadquality.data.dao.RoadPointDao;
import fyi.jackson.drew.roadquality.data.dao.UploadDao;
import fyi.jackson.drew.roadquality.data.entities.Accelerometer;
import fyi.jackson.drew.roadquality.data.entities.Gps;
import fyi.jackson.drew.roadquality.data.entities.RoadPoint;
import fyi.jackson.drew.roadquality.data.entities.Upload;
import fyi.jackson.drew.roadquality.data.migrations.Migrations;

@Database(entities = {Accelerometer.class, Gps.class, RoadPoint.class, Upload.class}, version = 10)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "RoadQualityDatabase.db";
    public abstract AccelerometerDao accelerometerDao();
    public abstract GpsDao gpsDao();
    public abstract RoadPointDao roadPointDao();
    public abstract UploadDao uploadDao();

    private static AppDatabase appDatabaseInstance = null;

    public synchronized static AppDatabase getInstance(Context context) {
        if (appDatabaseInstance == null) {
            appDatabaseInstance = Room.databaseBuilder(context,
                    AppDatabase.class, AppDatabase.DATABASE_NAME)
                    .addMigrations(
                            Migrations.MIGRATION_7_8,
                            Migrations.MIGRATION_8_9,
                            Migrations.MIGRATION_9_10)
                    .build();
        }
        return appDatabaseInstance;
    }
}
