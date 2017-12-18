package fyi.jackson.drew.roadquality.animation.listeners;

import android.view.View;

import fyi.jackson.drew.roadquality.utils.helpers;

public class MapPositionListener {

    View mapView;

    float startY = 0, endY = -200, curY;

    public MapPositionListener(View mapView) {
        this.mapView = mapView;
    }

    public MapPositionListener(View mapView, float startY, float endY) {
        this.mapView = mapView;
        this.startY = startY;
        this.endY = endY;
    }

    public void update(float offset) {
        this.curY = helpers.map(offset, 0, 1, startY, endY);

        mapView.setY(curY);
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
