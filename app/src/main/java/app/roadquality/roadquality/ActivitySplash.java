package app.roadquality.roadquality;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ActivitySplash extends AppCompatActivity {

    private static final String TAG = "ActivitySplash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(
                getString(R.string.PREFS_NAME), 0);
        int timesOpened = settings.getInt(
                getString(R.string.PREFS_TIMES_OPENED), 0);
        boolean permissionGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        Log.d(TAG, "onCreate: This app has been opened " + timesOpened + " time(s) before");
        
        Intent intent;
        
        if (permissionGranted) {
            Log.d(TAG, "onCreate: Permissions have been granted, starting Main Activity");
            intent = new Intent(this, ActivityMain.class);
        } else {
            Log.d(TAG, "onCreate: Permissions have not been granted, starting Intro Activity");
            intent = new Intent(this, ActivityIntro.class);
        }
        
        timesOpened++;
        settings.edit()
                .putInt(getString(R.string.PREFS_TIMES_OPENED), timesOpened)
                .apply();

        startActivity(intent);
        finish();
    }
}
