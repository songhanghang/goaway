package com.mi.song.goaway;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

import java.lang.reflect.Method;

/**
 * @author by songhang on 2018/3/2
 */

public class ScreenUtil {
    private volatile static int sHeight;
    private volatile static int sWidth;

    public static int getHeight() {
        if (sHeight > 0) {
            return sHeight;
        } else {
            throw new IllegalStateException("must call calcScreenParams in activity before");
        }
    }

    public static int getWidth() {
        if (sWidth > 0) {
            return sWidth;
        } else {
            throw new IllegalStateException("must call calcScreenParams in activity before");
        }
    }

    /**
     * must call before
     * @param activity activity
     */
    public static void calcScreenParams(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            sHeight = dm.heightPixels;
            sWidth = dm.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
