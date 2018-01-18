package fyi.jackson.drew.roadquality;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityIntro extends AppCompatActivity {

    private static final String TAG = "ActivityIntro";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 2978;

    private MyViewPagerAdapter myViewPagerAdapter;
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int[] overallLayouts, currentLayouts;
    private boolean[] nextButtonActiveDefaults;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        setupWindowAnimations();

        viewPager = findViewById(R.id.view_pager_intro);
        dotsLayout = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btn_next);

        overallLayouts = new int[] {
                R.layout.intro_slide_1,
                R.layout.intro_slide_2,
                R.layout.intro_slide_3
        };

        currentLayouts = new int[] {
                R.layout.intro_slide_1,
                R.layout.intro_slide_2
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
                if (current < currentLayouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    launchMainActivity();
                }
            }
        });

    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade(Fade.IN);
            fade.setDuration(1000);
            getWindow().setEnterTransition(fade);
        }
    }
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[currentLayouts.length];

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

    private final ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addBottomDots(position);

            if (position == overallLayouts.length - 1) {
                btnNext.setText(getString(R.string.got_it));
            } else {
                btnNext.setText(getString(R.string.next));
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

            View view = layoutInflater.inflate(currentLayouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return currentLayouts.length;
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

    public void askPermission(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            permissionGranted();
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
                break;
            }
        }
    }

    private void permissionGranted() {
        SharedPreferences settings = getSharedPreferences(
                getString(R.string.PREFS_NAME), 0);
        settings.edit()
                .putBoolean(getString(R.string.PREFS_PERMISSION_GRANTED), true)
                .apply();

        currentLayouts = overallLayouts; // add third comfirmation slide
        myViewPagerAdapter.notifyDataSetChanged();

        btnNext.setEnabled(true);
        btnNext.performClick();
    }

    private void permissionDenied() {
        btnNext.setEnabled(false);
        Snackbar.make(dotsLayout,
                getString(R.string.gps_permission_denied_snackbar_text),
                Snackbar.LENGTH_LONG).show();
    }
}
