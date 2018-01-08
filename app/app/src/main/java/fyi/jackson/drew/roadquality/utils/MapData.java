package fyi.jackson.drew.roadquality.utils;

import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapData implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private View mapView;

    private boolean isShowingData = false;

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

    public void clearMap() {
        googleMap.clear();
        isShowingData = false;
    }

    public void putTripDataOnMap(JSONObject tripData, int mapWidth, int mapHeight) {
        clearMap();
        try {
            JSONArray coordinates =
                    tripData.getJSONObject("trip").getJSONArray("coordinates");
            PolylineOptions polylineOptions = new PolylineOptions();
            LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
            for (int i = 0; i < coordinates.length(); i++) {
                JSONObject coord = coordinates.getJSONObject(i);
                com.google.android.gms.maps.model.LatLng latLng =
                        new com.google.android.gms.maps.model.LatLng(coord.getDouble("lat"), coord.getDouble("lng"));
                polylineOptions.add(latLng);
                latLngBuilder.include(latLng);
            }
            googleMap.addPolyline(polylineOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(), mapWidth, mapHeight, 10));
            isShowingData = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isShowingData() {
        return isShowingData;
    }
}
