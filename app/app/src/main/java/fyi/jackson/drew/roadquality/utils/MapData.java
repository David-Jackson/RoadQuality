package fyi.jackson.drew.roadquality.utils;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;


public class MapData implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private View mapView;

    private Runnable onMapReadyRunnable = null;

    public MapData(GoogleMap googleMap, View mapView) {
        setGoogleMap(googleMap);
        setMapView(mapView);
    }

    public MapData(View mapView) {
        setMapView(mapView);
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public View getMapView() {
        return mapView;
    }

    public void setMapView(View mapView) {
        this.mapView = mapView;
    }

    public void setOnMapReadyRunnable(Runnable onMapReadyRunnable) {
        this.onMapReadyRunnable = onMapReadyRunnable;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setGoogleMap(googleMap);
        if (onMapReadyRunnable != null) {
            onMapReadyRunnable.run();
        }
    }
}
