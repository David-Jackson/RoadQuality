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

}
