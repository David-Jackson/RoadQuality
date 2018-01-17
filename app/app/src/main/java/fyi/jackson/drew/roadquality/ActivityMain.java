package fyi.jackson.drew.roadquality;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fyi.jackson.drew.roadquality.animation.AnimationManager;
import fyi.jackson.drew.roadquality.animation.MorphingFab;
import fyi.jackson.drew.roadquality.data.AppDatabase;
import fyi.jackson.drew.roadquality.service.ForegroundConstants;
import fyi.jackson.drew.roadquality.service.ForegroundService;
import fyi.jackson.drew.roadquality.utils.BroadcastManager;
import fyi.jackson.drew.roadquality.utils.MapData;
import fyi.jackson.drew.roadquality.utils.RecentTripsAdapter;
import fyi.jackson.drew.roadquality.utils.helpers;

import static fyi.jackson.drew.roadquality.data.AppDatabase.DATABASE_NAME;
import static fyi.jackson.drew.roadquality.utils.helpers.getStatusBarHeight;


public class ActivityMain extends AppCompatActivity {

    private static final String TAG = "ActivityMain";

    private FloatingActionButton fab;
    private MorphingFab morphingFab;

    private BroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFab();

        if (helpers.isIntroNeeded(this)) {
            transitionToIntro();
        }

    }

    private void setupFab() {
        fab = findViewById(R.id.fab);
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent mainActivityIntent = new Intent(getApplicationContext(), ActivityTest.class);
                startActivity(mainActivityIntent);
                return true;
            }
        });

        morphingFab = new MorphingFab(this,
                fab,
                findViewById(R.id.fab_reveal),
                R.id.fab_reveal_image,
                R.drawable.avd_play_to_pause_96dp,
                R.drawable.avd_pause_to_play_96dp) {
            @Override
            public boolean onFabClick() {
                fabClicked();
                return false;
            }
        };

        final CoordinatorLayout layout = findViewById(R.id.activity_main_layout);
        layout.post(new Runnable() {
            @Override
            public void run() {
                if (ForegroundService.IS_SERVICE_RUNNING) {
                    Log.d(TAG, "onGlobalLayout: FAB SHOULD BE OPEN ON START");
                    morphingFab.open();
                } else {
                    Log.d(TAG, "onGlobalLayout: FAB SHOULD BE CLOSED ON START");
                    morphingFab.close();
                }
                setupAfterFirstDraw();
            }
        });

    }


    private void setupAfterFirstDraw() {
        setupBroadcastManager();

        inflateMap();
        setupMap();

        inflateBottomSheet();
        setupBottomSheet();

        setupAnimations();
    }

    private void setupBroadcastManager() {
        broadcastManager = new BroadcastManager(this) {
            @Override
            public void onServiceStatusChanged(int serviceStatus) {
                switch (serviceStatus) {
                    case ForegroundConstants.STATUS_ACTIVE:
                        morphingFab.open();
                        break;
                    case ForegroundConstants.STATUS_INACTIVE:
                        morphingFab.close();
                        break;
                }
            }

            @Override
            public void onDataTransferredToLongTerm(int totalRows, int deletedAccelRows, int deletedGpsRows) {

            }

            @Override
            public void onTripListReceived(JSONArray tripList) {

            }

            @Override
            public void onTripDataReceived(JSONObject tripData) {

            }
        };
    }

    private void inflateMap() {

    }

    private void setupMap() {

    }

    private void inflateBottomSheet() {

    }

    private void setupBottomSheet() {

    }

    private void setupAnimations() {

    }

    private void fabClicked() {
        Intent service = new Intent(ActivityMain.this, ForegroundService.class);
        if (!ForegroundService.IS_SERVICE_RUNNING) {
            service.setAction(ForegroundConstants.ACTION.STARTFOREGROUND_ACTION);
        } else {
            service.setAction(ForegroundConstants.ACTION.STOPFOREGROUND_ACTION);
        }
        startService(service);
    }

    private void transitionToIntro() {
        setupTransitionToIntro();
        Intent introActivityIntent = new Intent(getApplicationContext(), ActivityIntro.class);
        startActivity(introActivityIntent);
    }

    private void setupTransitionToIntro() {

    }

}
