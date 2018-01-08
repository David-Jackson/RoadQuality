package fyi.jackson.drew.roadquality;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import fyi.jackson.drew.roadquality.animation.MorphingFab;

public class ActivityTest extends AppCompatActivity {

    private MorphingFab morphingFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        morphingFab = new MorphingFab(this,
                (FloatingActionButton) findViewById(R.id.fab), // FAB
                findViewById(R.id.fab_reveal),                 // View after FAB Clicked
                R.id.fab_reveal_image,                         // Shared Element
                R.drawable.avd_play_to_pause_96dp,             // Animated Vector Drawable (AVD) closed to open
                R.drawable.avd_pause_to_play_96dp) {           // AVD open to close
            @Override
            public boolean onFabClick() {
                return true;
            }
        };

    }
}
