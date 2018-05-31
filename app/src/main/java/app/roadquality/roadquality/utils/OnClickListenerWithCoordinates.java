package app.roadquality.roadquality.utils;

import android.view.MotionEvent;
import android.view.View;

/*
 * onClickListener that includes the coordinates of the click by utilizing onTouchListener
 */

public abstract class OnClickListenerWithCoordinates implements View.OnTouchListener {
    private int CLICK_ACTION_THRESHOLD = 200;
    private float startX;
    private float startY;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                if (isAClick(startX, endX, startY, endY)) {
                    onClick(endX, endY);// WE HAVE A CLICK!!
                }
                break;
        }
        return true;
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD);
    }

    public abstract void onClick(float clickX, float clickY);
}
