package fyi.jackson.drew.roadquality.animation;


import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.mapbox.mapboxsdk.maps.MapView;

import fyi.jackson.drew.roadquality.R;
import fyi.jackson.drew.roadquality.animation.listeners.FabPositionListener;
import fyi.jackson.drew.roadquality.animation.listeners.MapPositionListener;

import static fyi.jackson.drew.roadquality.utils.helpers.getStatusBarHeight;

// this animation manager controls the bottom sheet scroll animation for the transition activity
// it also controls the map and bottom sheet enter animation after the map is ready
// it is initialized with the activity, and then must have the fab View and map View set after

public class AnimationManager {

    public static final String TAG = "AnimationManager";

    public static final int ENTRANCE_ANIMATION_DURATION = 600;

    int screenHeight, screenWidth;

    FabPositionListener fabPositionListener;
    MapPositionListener mapPositionListener;

    LinearLayout bottomSheet;
    BottomSheetBehavior bottomSheetBehavior;

    boolean mapReady = false;
    boolean timerExpired = false;
    boolean animatedIn = false;

    int bottomSheetPeekHeight;

    public AnimationManager(Activity activity) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        this.screenHeight = displayMetrics.heightPixels - getStatusBarHeight(activity);
        this.screenWidth = displayMetrics.widthPixels;

        this.bottomSheetPeekHeight = (int) activity.getResources().getDimension(R.dimen.bottom_sheet_peek_height);

        float fabSize = activity.getResources().getDimension(R.dimen.fab_size);

        float endY = screenHeight - activity.getResources().getDimension(R.dimen.bottom_sheet_height) - (fabSize / 2f);
        if (endY < 0) {
            endY = activity.getResources().getDimension(R.dimen.bottom_sheet_title_height) - (fabSize / 2f);
        }

        fabPositionListener = new FabPositionListener(null,
                (screenWidth - fabSize) / 2f,
                (screenHeight - fabSize) / 2f,
                screenWidth - activity.getResources().getDimension(R.dimen.fab_margin) - fabSize,
                endY
        );
        mapPositionListener = new MapPositionListener(null, 0, -screenHeight/4f);
    }

    public void setFab(FloatingActionButton fab) {
        fabPositionListener.setFab(fab);
    }


    public void setMap(MapView view) {
        mapPositionListener.setMapView(view);

        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timerExpired = true;
                        Log.d(TAG, "run: Timer Expired, " + timerExpired);
                        animateIn();
                    }
                }, 300);
            }
        });
    }

    public void setBottomSheet(LinearLayout bottomSheet) {
        this.bottomSheet = bottomSheet;
    }

    public void setBottomSheetBehavior(BottomSheetBehavior bottomSheetBehavior) {
        this.bottomSheetBehavior = bottomSheetBehavior;
    }

    public void update(float offset) {
        fabPositionListener.update(offset);
        mapPositionListener.update(offset);
    }

    public void onMapReady() {
        this.mapReady = true;
        Log.d(TAG, "onMapReady: Map Ready, " + mapReady);
        animateIn();
    }


    void animateIn() {
        if (mapReady && timerExpired && !animatedIn) {
            mapPositionListener.getMapView().animate()
                    .setDuration(ENTRANCE_ANIMATION_DURATION)
                    .y(0)
                    .setInterpolator(new LinearOutSlowInInterpolator())
                    .start();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                bottomSheet.animate()
                        .setDuration(ENTRANCE_ANIMATION_DURATION)
                        .translationY(-bottomSheetPeekHeight)
                        .setInterpolator(new LinearOutSlowInInterpolator())
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                bottomSheet.setY(bottomSheet.getY() + bottomSheetPeekHeight);
                                bottomSheetBehavior.setPeekHeight(bottomSheetPeekHeight);
                            }
                        })
                        .start();
            } else {
                bottomSheet.setY(bottomSheet.getY() + bottomSheetPeekHeight);
                bottomSheetBehavior.setPeekHeight(bottomSheetPeekHeight);
            }

            animatedIn = true;
        }
    }

}
