package fyi.jackson.drew.roadquality.data.migrations;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;
import android.util.Log;

public class Migrations {

    private static final String TAG = "Migrations";

    public static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Gps " +
                    "ADD COLUMN speed REAL NOT NULL DEFAULT -1;");
            database.execSQL("ALTER TABLE RoadPoint " +
                    "ADD COLUMN speed REAL NOT NULL DEFAULT -1;");
            Log.d(TAG, "migrate: Migrate from 7 to 8 successful");
        }
    };

    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Accelerometer RENAME TO TempAccelerometer");
            database.execSQL("ALTER TABLE RoadPoint RENAME TO TempRoadPoint");

            database.execSQL("CREATE TABLE Accelerometer (" +
                    "uid INTEGER NOT NULL PRIMARY KEY, " +
                    "ts INTEGER NOT NULL, " +
                    "ax REAL NOT NULL, " +
                    "ay REAL NOT NULL, " +
                    "az REAL NOT NULL, " +
                    "gx REAL NOT NULL, " +
                    "gy REAL NOT NULL, " +
                    "gz REAL NOT NULL " +
                    ");");
            database.execSQL("INSERT INTO Accelerometer " +
                    "SELECT uid, ts, ax, ay, az, gx, gy, gz " +
                    "FROM TempAccelerometer");

            database.execSQL("CREATE TABLE RoadPoint (" +
                    "uid INTEGER NOT NULL PRIMARY KEY, " +
                    "trip_id INTEGER NOT NULL, " +
                    "interpolated INTEGER NOT NULL, " +
                    "timestamp INTEGER NOT NULL, " +
                    "latitude REAL NOT NULL, " +
                    "longitude REAL NOT NULL," +
                    "provider TEXT, " +
                    "accuracy REAL, " +
                    "altitude REAL, " +
                    "ax REAL, " +
                    "ay REAL, " +
                    "az REAL, " +
                    "gx REAL, " +
                    "gy REAL, " +
                    "gz REAL, " +
                    "duration REAL, " +
                    "distance REAL, " +
                    "speed REAL" +
                    ");");
            database.execSQL("INSERT INTO RoadPoint " +
                    "SELECT uid, trip_id, interpolated, timestamp, latitude, longitude, " +
                    "provider, accuracy, altitude, ax, ay, az, gx, gy, gz, " +
                    "duration, distance, speed " +
                    "FROM TempRoadPoint");

            database.execSQL("DROP TABLE TempAccelerometer");
            database.execSQL("DROP TABLE TempRoadPoint");
        }
    };

}
