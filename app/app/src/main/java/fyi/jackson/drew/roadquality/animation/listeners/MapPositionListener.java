package fyi.jackson.drew.roadquality.animation.listeners;

import android.view.View;

import fyi.jackson.drew.roadquality.utils.MapData;
import fyi.jackson.drew.roadquality.utils.helpers;

public class MapPositionListener {

    private MapData mapData;
    private View mapView;

    private float startY = 0, endY = -200, curY;

    public MapPositionListener(MapData mapData) {
        setMapData(mapData);
    }

    public MapPositionListener(MapData mapData, float startY, float endY) {
        setMapData(mapData);
        setStartY(startY);
        setEndY(endY);
    }

    public void update(float offset) {
        setCurY(helpers.map(offset, 0, 1, startY, endY));

        mapView.setY(curY);
    }

    public MapData getMapData() {
        return mapData;
    }

    public void setMapData(MapData mapData) {
        this.mapData = mapData;
        this.mapView = mapData.getMapView();
    }

    public View getMapView() {
        return mapView;
    }

    public void setMapView(View mapView) {
        this.mapView = mapView;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public float getCurY() {
        return curY;
    }

    public void setCurY(float curY) {
        this.curY = curY;
    }
}
