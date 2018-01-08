package fyi.jackson.drew.roadquality;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fyi.jackson.drew.roadquality.animation.AnimationManager;
import fyi.jackson.drew.roadquality.animation.MorphingFab;
import fyi.jackson.drew.roadquality.service.ForegroundConstants;
import fyi.jackson.drew.roadquality.service.ForegroundService;
import fyi.jackson.drew.roadquality.utils.BroadcastManager;
import fyi.jackson.drew.roadquality.utils.MapData;
import fyi.jackson.drew.roadquality.utils.RecentTripsAdapter;
import fyi.jackson.drew.roadquality.utils.maps;

import static fyi.jackson.drew.roadquality.utils.helpers.getStatusBarHeight;


public class ActivityMain extends AppCompatActivity {

    private static final String TAG = "ActivityMain";

    private MapData mapData;

    private LinearLayout bottomSheetLayout;

    private BottomSheetBehavior bottomSheetBehavior;
    private FloatingActionButton fab;

    private AnimationManager animationManager;

    private BroadcastManager broadcastManager = null;

    private MorphingFab morphingFab;

    private RecyclerView recyclerView = null;


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

    private void setupMap() {
        View mapView = findViewById(R.id.map);
        mapData = new MapData(null, mapView);
        mapData.setOnMapReadyRunnable(new Runnable() {
            @Override
            public void run() {
                showLastKnownLocation(mapData.getGoogleMap());
                animationManager.onMapReady();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapData);
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
            }
        });

    }

    private void setupBottomSheet() {
        // get the bottom sheet view
        View view = findViewById(R.id.bottom_sheet_title);
        bottomSheetLayout = (LinearLayout) view.getParent();

        if (bottomSheetLayout == null) {
            Log.d(TAG, "initBottomSheet: BottomSheet null");
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
                if (recyclerView != null && newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    //((RecentTripsAdapter)recyclerView.getAdapter()).clearActiveTrips();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                animationManager.update(slideOffset);
            }
        });

    }

    private void setupBottomSheetRecyclerView(JSONArray tripData) {

        recyclerView = findViewById(R.id.recycler_view_bottom_sheet);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter adapter = new RecentTripsAdapter(tripData) {
            @Override
            public void onRowClicked(long tripId) {
                if (broadcastManager != null) {
                    broadcastManager.askToGetTripData(tripId);
                }
                setProperFabStartingPosition();
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setupAnimations() {
        animationManager = new AnimationManager(this);
        animationManager.setFab(fab);
        animationManager.setMap(mapData.getMapView());
        animationManager.setBottomSheet(bottomSheetLayout);
        animationManager.setBottomSheetBehavior(bottomSheetBehavior);
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
                findViewById(R.id.progress_bar_bottom_sheet).setVisibility(View.INVISIBLE);
                findViewById(R.id.recycler_view_bottom_sheet).setVisibility(View.VISIBLE);
            }

            @Override
            public void onTripDataReceived(JSONObject tripData) {
                String toastString = "Nothing received";
                try {
                    toastString = "Received " + tripData.getJSONObject("trip").getJSONArray("coordinates").length();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(ActivityMain.this, toastString, Toast.LENGTH_SHORT).show();

                DisplayMetrics displayMetrics = new DisplayMetrics();
                ActivityMain.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenHeight = displayMetrics.heightPixels - getStatusBarHeight(ActivityMain.this);
                int screenWidth = displayMetrics.widthPixels;
                int mapHeight = screenHeight - bottomSheetLayout.getHeight();
                mapData.putTripDataOnMap(tripData, screenWidth, mapHeight);
                setProperFabStartingPosition();
            }
        };
    }

    //
    // END OF SETUP FUNCTIONS
    //

    private void setProperFabStartingPosition() {
        if (mapData.isShowingData()) {
            animationManager.setFabStartPositionAtBottomSheet();
        } else {
            animationManager.setFabStartPositionAtScreenCenter();
        }
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

    void bumpBottomSheet() {
        bumpBottomSheet(1);
    }

    private void bumpBottomSheet(final int numberOfTimes) {
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

    public void toggleBottomSheet(View view) {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void showLastKnownLocation(GoogleMap googleMap) {
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

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
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
