package fyi.jackson.drew.roadquality.utils;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.SimpleDateFormat;
import java.util.Date;

import fyi.jackson.drew.roadquality.data.AppDatabase;
import fyi.jackson.drew.roadquality.data.migrations.Migrations;

public class helpers {

    public static boolean isIntroNeeded(Context context) {
        boolean permissionGranted = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        return !permissionGranted;
    }

    public static AppDatabase getAppDatabase(Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class, AppDatabase.DATABASE_NAME)
                .addMigrations(
                        Migrations.MIGRATION_7_8,
                        Migrations.MIGRATION_8_9,
                        Migrations.MIGRATION_9_10)
                .build();
    }

    public static float map(float value,
                              float iStart,
                              float iStop,
                              float oStart,
                              float oStop) {
        return oStart + (oStop - oStart) * ((value - iStart) / (iStop - iStart));
    }

    public static float dpToPx(float dp, float dpi) {
        return dp * (dpi / 160);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static String epochToLocalString(long epoch) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:m a");
        return sdf.format(new Date(epoch));
    }

    public static String epochToDateString(long epoch) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(new Date(epoch));
    }

    public static String epochToTimeString(long epoch) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        return sdf.format(new Date(epoch));
    }

    public static boolean isGooglePlayServicesAvailable(Context context){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }
}
