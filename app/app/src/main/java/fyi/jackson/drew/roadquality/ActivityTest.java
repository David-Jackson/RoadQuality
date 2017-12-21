package fyi.jackson.drew.roadquality;

import android.animation.Animator;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import java.nio.channels.FileLock;

public class ActivityTest extends AppCompatActivity {

    public static final String TAG = "ActivityTest";
    View reveal;
    FloatingActionButton fab;
    ImageView revealImageView;
    AnimatedVectorDrawable playToStopAvd;
    AnimatedVectorDrawable stopToPlayAvd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        reveal = findViewById(R.id.fab_reveal);
        fab = findViewById(R.id.fab);
        revealImageView = findViewById(R.id.fab_reveal_image);
        playToStopAvd = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.avd_play_to_pause_96dp);
        stopToPlayAvd = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.avd_pause_to_play_96dp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.setElevation(0);
        }
    }

    public void revealViewClicked(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            Animator circularReveal;
            if (view.getId() != fab.getId()) { // View Clicked
                Log.d(TAG, "revealViewClicked: view clicked");
                circularReveal = ViewAnimationUtils.createCircularReveal(
                        reveal,
                        reveal.getWidth() / 2,
                        reveal.getHeight() / 2,
                        (float) Math.hypot(reveal.getWidth(), reveal.getHeight()),
                        fab.getWidth() / 2
                );
                circularReveal.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        reveal.setVisibility(View.GONE);
                        fab.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                revealImageView.setImageDrawable(stopToPlayAvd);
                stopToPlayAvd.start();
            } else { // FAB Clicked
                Log.d(TAG, "revealViewClicked: fab clicked");
                Log.d(TAG, "revealViewClicked: fab elevation:" + fab.getElevation());
                circularReveal = ViewAnimationUtils.createCircularReveal(
                        reveal,
                        reveal.getWidth() / 2,
                        reveal.getHeight() / 2,
                        fab.getWidth() / 2,
                        (float) Math.hypot(reveal.getWidth(), reveal.getHeight())
                );
                reveal.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
                revealImageView.setImageDrawable(playToStopAvd);
                playToStopAvd.start();
            }

            circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
            circularReveal.start();

        }
    }

}
