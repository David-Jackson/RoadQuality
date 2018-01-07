package fyi.jackson.drew.roadquality.utils;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;


public class MapData {

    private GoogleMap googleMap;
    private View mapView;

    public MapData(GoogleMap googleMap, View mapView) {
        setGoogleMap(googleMap);
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

}
