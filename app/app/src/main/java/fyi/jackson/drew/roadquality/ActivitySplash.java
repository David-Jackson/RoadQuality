package fyi.jackson.drew.roadquality;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ActivitySplash extends AppCompatActivity {

    public static final String TAG = "ActivitySplash";

    public static final String PREFS_NAME = "RoadQualityPrefsFile";
    public static final String PREFS_TIMES_OPENED = "NumberOfTimesOpened";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int timesOpened = settings.getInt(PREFS_TIMES_OPENED, 0);
        Log.d(TAG, "onCreate: This app has been opened " + timesOpened + " time(s) before");
        
        Intent intent;
        
        if (timesOpened == 0) {
            Log.d(TAG, "onCreate: First time opened, starting Intro Activity");
            // intent = new Intent(this, ActivityIntro.class);
        } else {
            Log.d(TAG, "onCreate: Not the first time opened, starting Main Activity");
            // intent = new Intent(this, ActivityMain.class);
        }
        
        timesOpened++;
        settings.edit()
                .putInt(PREFS_TIMES_OPENED, timesOpened)
                .apply();
        
//        startActivity(intent);
//        finish();

    }
}
