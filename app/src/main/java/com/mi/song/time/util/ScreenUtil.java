package com.mi.song.time.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.mi.song.time.R;

/**
 * @author by songhang on 2018/3/2
 */

public class ScreenUtil {
    private volatile static int sHeight;
    private volatile static int sWidth;

    public static int getHeight(Context context) {
        if (sHeight > 0) {
            return sHeight;
        }
        calcRealScreenSize(context);
        return sHeight;
    }

    public static int getWidth(Context context) {
        if (sWidth > 0) {
            return sWidth;
        }
        calcRealScreenSize(context);
        return sWidth;
    }

    private static void calcRealScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getRealMetrics(dm);
        sWidth = dm.widthPixels;
        sHeight = dm.heightPixels;
    }
}
