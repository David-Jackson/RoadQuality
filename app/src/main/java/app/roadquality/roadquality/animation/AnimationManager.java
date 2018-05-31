package app.roadquality.roadquality.animation;


import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


import app.roadquality.roadquality.R;
import app.roadquality.roadquality.animation.listeners.FabPositionListener;
import app.roadquality.roadquality.animation.listeners.MapPositionListener;

import static app.roadquality.roadquality.utils.helpers.getStatusBarHeight;

// this animation manager controls the bottom sheet scroll animation for the transition activity
// it also controls the map and bottom sheet enter animation after the map is ready
// it is initialized with the activity, and then must have the fab View and map View set after

public class AnimationManager {

    private static final String TAG = "AnimationManager";

    private static final int ENTRANCE_ANIMATION_DURATION = 600;

    private final int screenHeight, screenWidth;

    private final FabPositionListener fabPositionListener;
    private final MapPositionListener mapPositionListener;

    private LinearLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;

    private boolean mapReady = false;
    private boolean timerExpired = false;
    private boolean animatedIn = false;

    private final int bottomSheetPeekHeight;

    private Runnable onAnimationComplete = null;
    private int onAnimationCompleteDelay = 0;

    private float fabXScreenCenter, fabYScreenCenter, fabXBottomSheet, fabYBottomSheet;

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

        fabXScreenCenter = (screenWidth - fabSize) / 2f;
        fabYScreenCenter = (screenHeight - fabSize) / 2f;
        fabXBottomSheet = screenWidth - activity.getResources().getDimension(R.dimen.fab_margin) - fabSize;
        fabYBottomSheet = screenHeight - bottomSheetPeekHeight - (fabSize / 2f);

        fabPositionListener = new FabPositionListener(null,
                fabXScreenCenter,
                fabYScreenCenter,
                fabXBottomSheet,
                endY
        );

        int bottomSheetHeight = (int) activity.getResources().getDimension(R.dimen.bottom_sheet_height);
        mapPositionListener = new MapPositionListener(null,
                0,
                -bottomSheetHeight/2);
    }

    public void setFab(FloatingActionButton fab) {
        fabPositionListener.setFab(fab);
    }


    public void setMap(View view) {
        mapPositionListener.setMapView(view);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timerExpired = true;
                Log.d(TAG, "run: Timer Expired");
                animateIn();
            }
        }, 300);
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
        Log.d(TAG, "onMapReady: Map Ready");
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
                                if (onAnimationComplete != null) {
                                    animationComplete();
                                }
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

    private void animationComplete() {
        Handler handler = new Handler();
        handler.postDelayed(this.onAnimationComplete, this.onAnimationCompleteDelay);
    }

    public void setOnAnimationComplete(Runnable runnable, int delay) {
        this.onAnimationComplete = runnable;
        this.onAnimationCompleteDelay = delay;
        if (this.animatedIn) {
            animationComplete();
        }
    }

    public void setOnAnimationComplete(Runnable runnable) {
        this.setOnAnimationComplete(runnable, 0);
    }

    public void setFabStartPositionAtScreenCenter() {
        fabPositionListener.setStartX(fabXScreenCenter);
        fabPositionListener.setStartY(fabYScreenCenter);
        fabPositionListener.setInterpolator(FabPositionListener.CUBIC_INTERPOLATOR);
    }

    public void setFabStartPositionAtBottomSheet() {
        fabPositionListener.setStartX(fabXBottomSheet);
        fabPositionListener.setStartY(fabYBottomSheet);
        fabPositionListener.setInterpolator(FabPositionListener.LINEAR_INTERPOLATOR);
    }
}
