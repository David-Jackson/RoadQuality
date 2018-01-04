package fyi.jackson.drew.roadquality;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import fyi.jackson.drew.roadquality.animation.AnimationManager;
import fyi.jackson.drew.roadquality.animation.MorphingFab;
import fyi.jackson.drew.roadquality.service.DatabaseService;
import fyi.jackson.drew.roadquality.service.ForegroundConstants;
import fyi.jackson.drew.roadquality.service.ForegroundService;
import fyi.jackson.drew.roadquality.service.ServiceConstants;
import fyi.jackson.drew.roadquality.utils.BroadcastManager;
import fyi.jackson.drew.roadquality.utils.RecentTripsAdapter;


public class ActivityMain extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = "ActivityMain";

    private GoogleMap googleMap;
    private View mapView;
    LinearLayout bottomSheetLayout;

    private BottomSheetBehavior bottomSheetBehavior;
    FloatingActionButton fab;

    AnimationManager animationManager;

    private BroadcastManager broadcastManager;

    MorphingFab morphingFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupMap();
        setupFab();
        setupBottomSheet();
        setupAnimations();
        setupBroadcastManager();

        broadcastManager.askToUpdateTripList();

    }

    //
    // SETUP FUNCTIONS
    //

    void setupMap() {
        mapView = findViewById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    void setupFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
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
            public void onFabClick() {
                fabClicked(null);
            }
        };

    }

    void setupBottomSheet() {
        // get the bottom sheet view
        View view = findViewById(R.id.bottom_sheet_title);
        bottomSheetLayout = (LinearLayout) view.getParent();

        if (bottomSheetLayout == null) {
            Log.d(TAG, "initBottomSheet: Bottomsheet null");
            return;
        }
        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        // change the state of the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                animationManager.update(slideOffset);
            }
        });

    }

    void setupBottomSheetRecyclerView(JSONArray tripData) {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_bottom_sheet);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter adapter = new RecentTripsAdapter(tripData);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    void setupAnimations() {
        animationManager = new AnimationManager(this);
        animationManager.setFab(fab);
        animationManager.setMap(mapView);
        animationManager.setBottomSheet(bottomSheetLayout);
        animationManager.setBottomSheetBehavior(bottomSheetBehavior);
    }

    void setupBroadcastManager() {
        broadcastManager = new BroadcastManager(this) {
            @Override
            public void onDataTransferredToLongTerm(int totalRows, int deletedAccelRows, int deletedGpsRows) {
                Toast.makeText(ActivityMain.this,
                        "Data Received: total = " +
                                totalRows + " | delA = " +
                                deletedAccelRows + " | delG =" +
                                deletedGpsRows, Toast.LENGTH_LONG
                        ).show();
                this.askToUpdateTripList();
            }

            @Override
            public void onTripListReceived(JSONArray tripList) {
                Toast.makeText(ActivityMain.this, "Got Trip List: " + tripList.length() + " trip(s)", Toast.LENGTH_SHORT).show();
                setupBottomSheetRecyclerView(tripList);
                animationManager.setOnAnimationComplete(new Runnable() {
                    @Override
                    public void run() {
                        bumpBottomSheet();
                    }
                }, 1000);
            }

            @Override
            public void onTripDataReceived(JSONObject tripData) {

            }
        };
    }

    //
    // END OF SETUP FUNCTIONS
    //

    public void fabClicked(View view) {
        Intent service = new Intent(ActivityMain.this, ForegroundService.class);
        if (!ForegroundService.IS_SERVICE_RUNNING) {
            service.setAction(ForegroundConstants.ACTION.STARTFOREGROUND_ACTION);
            ForegroundService.IS_SERVICE_RUNNING = true;
        } else {
            service.setAction(ForegroundConstants.ACTION.STOPFOREGROUND_ACTION);
            ForegroundService.IS_SERVICE_RUNNING = false;
        }
        startService(service);
    }

    void bumpBottomSheet() {
        bumpBottomSheet(1);
    }

    void bumpBottomSheet(final int numberOfTimes) {
        if (numberOfTimes == 0) return;
        final int bumpHeight = 50;
        bottomSheetLayout.animate()
                .yBy(-bumpHeight)
                .setDuration(200)
                .setInterpolator(new OvershootInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetLayout.animate()
                                .yBy(bumpHeight)
                                .setDuration(200)
                                .setInterpolator(new OvershootInterpolator())
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        bumpBottomSheet(numberOfTimes - 1);
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    void toggleBottomSheet(View view) {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // show the last known location
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        LatLng latLng = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
        if (latLng == null) {
            latLng = new LatLng(42.3314, -83.0458);
        }

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

        animationManager.onMapReady();
    }

    @Override
    protected void onResume() {
        broadcastManager.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        broadcastManager.onPause();
        super.onPause();
    }

}
