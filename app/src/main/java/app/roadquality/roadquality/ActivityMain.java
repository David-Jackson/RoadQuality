package app.roadquality.roadquality;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import app.roadquality.roadquality.animation.AnimationManager;
import app.roadquality.roadquality.animation.DefaultItemAnimator;
import app.roadquality.roadquality.animation.MorphingFab;
import app.roadquality.roadquality.data.AppDatabase;
import app.roadquality.roadquality.data.entities.RoadPoint;
import app.roadquality.roadquality.data.entities.Trip;
import app.roadquality.roadquality.service.ForegroundConstants;
import app.roadquality.roadquality.service.ForegroundService;
import app.roadquality.roadquality.service.ServiceConstants;
import app.roadquality.roadquality.service.TripListLoaderService;
import app.roadquality.roadquality.service.TripLoaderService;
import app.roadquality.roadquality.utils.BroadcastManager;
import app.roadquality.roadquality.utils.MapData;
import app.roadquality.roadquality.recycler.RecentTripsAdapter;
import app.roadquality.roadquality.utils.helpers;

import static app.roadquality.roadquality.utils.helpers.getStatusBarHeight;


public class ActivityMain extends AppCompatActivity {

    private static final String TAG = "ActivityMain";

    private FloatingActionButton fab;
    private MorphingFab morphingFab;

    private BroadcastManager broadcastManager = null;

    private FrameLayout layout;

    private static final int MY_PERMISSIONS_REQUEST_STORAGE_ACCESS = 4378;

    private MapData mapData;

    private LinearLayout bottomSheetLayout;

    private BottomSheetBehavior bottomSheetBehavior;

    private AnimationManager animationManager;

    private RecyclerView recyclerView = null;

    private Button refreshButton;
    private Runnable refreshButtonRunnable;
    private int refreshButtonRunnableDelay = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade(Fade.OUT);
            fade.setDuration(1000);
            getWindow().setExitTransition(fade);
        }
        setupFab();
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

        if (helpers.isIntroNeeded(this)) {
            transitionToIntro();
            return;
        }

        setupBroadcastManager();

        inflateLayout();
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
                startTripListLoader();
            }

            @Override
            public void onTripUploadReceived(int status, String referenceId) {
                startTripListLoader();
            }
        };
    }



    private void inflateLayout() {
        layout = findViewById(R.id.layout_content_frame);
        View child = getLayoutInflater().inflate(R.layout.content_main, layout);
        child.post(new Runnable() {
            @Override
            public void run() {
                setupMap();
                setupBottomSheet();
                setupAnimations();
            }
        });
    }

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

        refreshButton = findViewById(R.id.button_refresh_bottom_sheet);
        refreshButtonRunnable = new Runnable() {
            @Override
            public void run() {
                refreshButton.setVisibility(View.VISIBLE);
                refreshButton.animate().alpha(1).setDuration(300).start();
            }
        };
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefreshButtonClick();
            }
        });
        refreshButton.postDelayed(refreshButtonRunnable, refreshButtonRunnableDelay);

        // Bottom sheet ready, load trip list
        startTripListLoader();
    }

    private void setupAnimations() {
        animationManager = new AnimationManager(this);
        animationManager.setFab(fab);
        animationManager.setMap(mapData.getMapView());
        animationManager.setBottomSheet(bottomSheetLayout);
        animationManager.setBottomSheetBehavior(bottomSheetBehavior);
    }

    private void setupBottomSheetRecyclerView(List<Trip> tripData) {

        recyclerView = findViewById(R.id.recycler_view_bottom_sheet);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.Adapter adapter = new RecentTripsAdapter(tripData) {
            @Override
            public void onRowClicked(long tripId) {
                startTripLoader(tripId);
                setProperFabStartingPosition();
            }

            @Override
            public boolean onRowClickedAgain(long tripId) {
                mapData.clearMap();
                setProperFabStartingPosition();
                return true;
            }

            @Override
            public void onUploadButtonClicked(long tripId) {
                broadcastManager.askToUploadTrip(tripId);
            }

            @Override
            public void onShareButtonClick() {
                shareDatabase();
            }

            @Override
            public void onSettingsButtonClick() {
                openSettings();
            }
        };

//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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

    private void setProperFabStartingPosition() {
        if (mapData.isShowingData()) {
            animationManager.setFabStartPositionAtBottomSheet();
        } else {
            animationManager.setFabStartPositionAtScreenCenter();
        }
    }

    public void toggleBottomSheet(View view) {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void onRefreshButtonClick() {
        startTripListLoader();
        refreshButton.setVisibility(View.INVISIBLE);
        refreshButton.postDelayed(refreshButtonRunnable, refreshButtonRunnableDelay);
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

    private void shareDatabase() {
        askPermissionToShareDatabase();
    }

    private void askPermissionToShareDatabase() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE_ACCESS);
        } else {
            continueToShareDatabase();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE_ACCESS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    continueToShareDatabase();
                } else {
                    permissionDenied();
                }
                break;
            }
        }
    }

    private void continueToShareDatabase() {
        try {
            File dbFile = getDatabasePath("RoadQualityDatabase.db").getAbsoluteFile();
            File outFile = new File(getExternalFilesDir(null), AppDatabase.DATABASE_NAME);
            copyFile(dbFile, outFile);
            Uri dbUri = Uri.fromFile(outFile);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/*");
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            shareIntent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, dbUri);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_database_intent_title)));
        } catch (IllegalArgumentException e) {
            Log.e("File Selector",
                    "The selected file can't be shared: RoadQualityDatabase.db");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void permissionDenied() {
        Snackbar.make(bottomSheetLayout,
                "Unable to share database",
                Snackbar.LENGTH_LONG).show();
    }

    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    private void openSettings() {
        Intent mainActivityIntent = new Intent(getApplicationContext(), ActivitySettings.class);
        startActivity(mainActivityIntent);
    }

    private void openPlayStore() {
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.google_play_uri)));
        startActivity(playStoreIntent);
    }

    @Override
    protected void onResume() {
        if (broadcastManager != null) broadcastManager.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        if (broadcastManager != null) broadcastManager.onPause();
        super.onPause();
    }

    private void transitionToIntro() {
        layout = findViewById(R.id.layout_content_frame);
        final View child = getLayoutInflater().inflate(R.layout.intro_slide_1, layout);
        child.setVisibility(View.INVISIBLE);

        morphingFab = new MorphingFab(this,
                fab,
                child,
                R.id.iv_road,
                R.drawable.avd_play_to_road_96dp,
                R.drawable.avd_road_animation) {
            @Override
            public boolean onFabClick() {
                return false;
            }
        };
        child.postDelayed(new Runnable() {
            @Override
            public void run() {
                morphingFab.open();
                child.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent introActivityIntent = new Intent(getApplicationContext(), ActivityIntro.class);
                        startActivity(introActivityIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }, 300);
            }
        }, 1000);
    }

    private void startTripLoader(long tripId) {
        int LOADER_TASK_ID = 5421;
        Bundle taskBundle = new Bundle();
        taskBundle.putLong(ServiceConstants.TRIP_ID, tripId);
        getSupportLoaderManager().restartLoader(LOADER_TASK_ID, taskBundle, new LoaderManager.LoaderCallbacks<List<RoadPoint>>() {
            @Override
            public Loader<List<RoadPoint>> onCreateLoader(final int id, final Bundle args) {
                long tripId = args.getLong(ServiceConstants.TRIP_ID, -1);
                if (tripId == -1) {
                    return null;
                }
                return new TripLoaderService(ActivityMain.this, tripId);
            }

            @Override
            public void onLoadFinished(final Loader<List<RoadPoint>> loader, final List<RoadPoint> tripData) {
                if (tripData == null) return;

                DisplayMetrics displayMetrics = new DisplayMetrics();
                ActivityMain.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenHeight = displayMetrics.heightPixels - getStatusBarHeight(ActivityMain.this);
                int screenWidth = displayMetrics.widthPixels;
                int mapHeight = screenHeight - bottomSheetLayout.getHeight();
                mapData.putTripDataOnMap(tripData, screenWidth, mapHeight);
                setProperFabStartingPosition();
            }

            @Override
            public void onLoaderReset(final Loader<List<RoadPoint>> loader) {
            }
        });
    }

    private void startTripListLoader() {
        int LOADER_TASK_ID = 5422;

        getSupportLoaderManager().restartLoader(
                LOADER_TASK_ID,
                null,
                new LoaderManager.LoaderCallbacks<List<Trip>>() {
                    @Override
                    public Loader<List<Trip>> onCreateLoader(int id, Bundle args) {
                        return new TripListLoaderService(ActivityMain.this);
                    }

                    @Override
                    public void onLoadFinished(Loader<List<Trip>> loader, List<Trip> data) {
                        setupBottomSheetRecyclerView(data);
                        findViewById(R.id.progress_bar_bottom_sheet).setVisibility(View.INVISIBLE);
                        findViewById(R.id.recycler_view_bottom_sheet).setVisibility(View.VISIBLE);
                        refreshButton.removeCallbacks(refreshButtonRunnable);
                        refreshButton.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onLoaderReset(Loader<List<Trip>> loader) {

                    }
                });
    }
}
