package fyi.jackson.drew.roadquality.utils;

import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

import fyi.jackson.drew.roadquality.data.entities.RoadPoint;


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

    public void putTripDataOnMap(List<RoadPoint> roadPoints, int mapWidth, int mapHeight) {
        clearMap();

        PolylineOptions polylineOptions = new PolylineOptions();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        List<LatLng> latLngs = new ArrayList<>();

        for (RoadPoint roadPoint : roadPoints) {
            LatLng latLng = new LatLng(
                    roadPoint.getLatitude(), roadPoint.getLongitude());
            if (roadPoint.isInterpolated()) {
                // Add interpolated points to LatLng list to be shown on heatmap
                latLngs.add(latLng);
            } else {
                // Add GPS to polyline and bounds
                // All interpolated points will be in between GPS points,
                // so both the polyline and bounds will enclose all points
                polylineOptions.add(latLng);
                boundsBuilder.include(latLng);
            }
        }

        googleMap.addPolyline(polylineOptions);

        if (latLngs.size() > 0) {
            TileProvider mProvider = new HeatmapTileProvider.Builder()
                    .data(latLngs)
                    .build();
            googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }

        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(),
                        mapWidth,
                        mapHeight,
                        30));

        isShowingData = true;
    }

    public boolean isShowingData() {
        return isShowingData;
    }
}
