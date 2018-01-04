package fyi.jackson.drew.roadquality.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

public class helpers {
    static public float map(float value,
                              float istart,
                              float istop,
                              float ostart,
                              float ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    public static float dpToPx(float dp, float dpi) {
        return dp * (dpi / 160);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static String epochToLocalString(long epoch) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:m a");
        return sdf.format(new Date(epoch));
    }
}
