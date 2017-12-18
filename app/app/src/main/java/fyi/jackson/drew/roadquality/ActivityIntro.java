package fyi.jackson.drew.roadquality;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityIntro extends AppCompatActivity {

    private static final String TAG = "ActivityIntro";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 2978;

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private boolean[] nextButtonActiveDefaults;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        viewPager = (ViewPager) findViewById(R.id.view_pager_intro);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnNext = (Button) findViewById(R.id.btn_next);

        layouts = new int[] {
                R.layout.intro_slide_1,
                R.layout.intro_slide_2,
                R.layout.intro_slide_3
        };

        nextButtonActiveDefaults = new boolean[] {
                true,
                false,
                true
        };

        addBottomDots(0);


        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = getItem(+1);
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    launchMainActivity();
                }
            }
        });

    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.darkOverlaySoft));
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.whiteOverlay));
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchMainActivity() {
        startActivity(new Intent(ActivityIntro.this, ActivityMain.class));
        finish();
    }

    // TODO: 12/12/2017 Do not allow ViewPager to scroll past permission screen until permissions are accepted
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addBottomDots(position);

            if (position == layouts.length - 1) {
                btnNext.setText("Got it");
            } else {
                btnNext.setText("Next");
            }

            btnNext.setEnabled(
                    nextButtonActiveDefaults[position]
            );

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {}

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public void imageViewClicked(final View view) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            final AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) imageView.getDrawable();
            drawable.start();
        }
    }

    public void askPermission(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted();
                } else {
                    permissionDenied();
                }
                return;
            }
        }
    }

    private void permissionGranted() {
        SharedPreferences settings = getSharedPreferences(
                getString(R.string.PREFS_NAME), 0);
        settings.edit()
                .putBoolean(getString(R.string.PREFS_PERMISSION_GRANTED), true)
                .apply();

        btnNext.setEnabled(true);
        btnNext.performClick();
    }

    private void permissionDenied() {
        btnNext.setEnabled(false);
        // TODO: 12/12/2017 Make this toast into a snackbar
        Toast.makeText(this,
                "Location Access is required for use of this app.",
                Toast.LENGTH_LONG).show();
    }
}
