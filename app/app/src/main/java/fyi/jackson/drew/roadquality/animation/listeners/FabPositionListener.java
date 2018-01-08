package fyi.jackson.drew.roadquality.animation.listeners;

import android.support.design.widget.FloatingActionButton;

import static fyi.jackson.drew.roadquality.utils.helpers.map;

public class FabPositionListener {

    public static final int LINEAR_INTERPOLATOR = 510;
    public static final int CUBIC_INTERPOLATOR = 511;

    private FloatingActionButton fab;
    private float startX, startY, endX, endY, curX, curY;
    private int interpolator = CUBIC_INTERPOLATOR;
    private float quadConstant;


    public FabPositionListener(FloatingActionButton fab, float startX, float startY, float endX, float endY) {
        this.fab = fab;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        this.quadConstant = (startY - endY) / ((startX - endX) * (startX - endX));
        this.quadConstant *= (endY > startY) ? 1 : -1;
    }

    public void update(float offset) {
        this.curX = map(offset, 0, 1, startX, endX);

        if (interpolator == LINEAR_INTERPOLATOR) {
            // linear
            this.curY = map(offset, 0, 1, startY, endY);
        } else {
            // quad
            float dx = this.curX - this.startX;
            this.curY = this.startY + (this.quadConstant * dx * dx);
        }

        fab.setX(curX);
        fab.setY(curY);
    }

    // GETTERS AND SETTERS

    public FloatingActionButton getFab() {
        return fab;
    }

    public void setFab(FloatingActionButton fab) {
        this.fab = fab;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public float getCurX() {
        return curX;
    }

    public void setCurX(float curX) {
        this.curX = curX;
    }

    public float getCurY() {
        return curY;
    }

    public void setCurY(float curY) {
        this.curY = curY;
    }

    public int getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(int interpolator) {
        this.interpolator = interpolator;
    }
}
