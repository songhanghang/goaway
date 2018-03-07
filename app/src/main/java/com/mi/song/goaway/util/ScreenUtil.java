package com.mi.song.goaway.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.mi.song.goaway.R;
import com.mi.song.goaway.SettingFragment;

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

    /**
     * Tips bottom distance
     */
    public static int getTipsBottom(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String height = sp.getString(SettingFragment.PRE_KEY_BOTTOM, context.getString(R.string.bottom_distance));
        return Integer.valueOf(height);
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
