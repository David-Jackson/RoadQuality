package fyi.jackson.drew.roadquality.utils;

import android.graphics.Color;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
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
            LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

            float[] hsv = {0, 1, 1};
            Color color = new Color();

            for (int i = 0; i < coordinates.length() - 1; i++) {
                PolylineOptions polylineOptions = new PolylineOptions();
                JSONObject coord1 = coordinates.getJSONObject(i);
                LatLng latLng1 =
                        new LatLng(coord1.getDouble("lat"), coord1.getDouble("lng"));
                polylineOptions.add(latLng1);
                JSONObject coord2 = coordinates.getJSONObject(i + 1);
                LatLng latLng2 =
                        new LatLng(coord2.getDouble("lat"), coord2.getDouble("lng"));
                polylineOptions.add(latLng2);

                hsv[0] = i % 360;
                polylineOptions.color(color.HSVToColor(hsv));

                latLngBuilder.include(latLng1);
                googleMap.addPolyline(polylineOptions);
            }
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
