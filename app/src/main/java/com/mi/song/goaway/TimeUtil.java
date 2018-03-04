package com.mi.song.goaway;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author by songhang on 2018/2/21
 */

public class TimeUtil {

    private static final String PREFERENCE_NAME = "time";
    private static final ArgbEvaluator sArgbEvaluator = new ArgbEvaluator();
    private static final SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());

    /**
     * Save used time in sharedPreferences
     * @param context the context
     * @return  used time
     */
    public static long getTodayUsedTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        Date date = new Date(System.currentTimeMillis());
        String today = sSimpleDateFormat.format(date);
        return sharedPreferences.getLong(today, 0);
    }

    public static void setTodayUsedTime(Context context, long time) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        Date date = new Date(System.currentTimeMillis());
        String today = sSimpleDateFormat.format(date);
        edit.putLong(today, time);
        edit.apply();
    }

    public static String timeToString(long time) {
        long s = time / 1000;
        if (s < 0) {
            return "err";
        } else if (s < 60) {
            return s + "s";
        } else if (s < 3600) {
            return s / 60 + "m" + timeToString(s % 60 * 1000);
        } else if (s <= 86400) {
            return s / 3600 + "h" + timeToString(s % 3600 * 1000);
        }

        return "err";
    }

    public static long getZeroTime(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static boolean isSameDay(long time1, long time2) {
        return getZeroTime(time1) == getZeroTime(time2);
    }

    public static int getColor(long time, Context context) {
        float h = 3600000f;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        int color0 = sp.getInt(SettingFragment.PRE_KEY_COLOR0, SettingFragment.BLUE);
        int color1 = sp.getInt(SettingFragment.PRE_KEY_COLOR1, SettingFragment.GREEN);
        int color2 = sp.getInt(SettingFragment.PRE_KEY_COLOR2, SettingFragment.ORGANE);
        int color3 = sp.getInt(SettingFragment.PRE_KEY_COLOR3, SettingFragment.LIGHT_RED);
        int color4 = sp.getInt(SettingFragment.PRE_KEY_COLOR4, SettingFragment.RED);

        int color = Color.RED;
        if (time < h) {
            color = (Integer) sArgbEvaluator.evaluate(time / h, color0, color1);
        } else if (time < 2 * h) {
            color = (Integer) sArgbEvaluator.evaluate(time / h - 1, color1, color2);
        } else if (time < 3 * h) {
            color = (Integer) sArgbEvaluator.evaluate(time / h - 2, color2, color3);
        } else if (time < 4 * h) {
            color = (Integer) sArgbEvaluator.evaluate(time / h - 3, color3, color4);
        }
        return color;
    }

    public static String getTips(long time, Context context) {
        //todo set use phone time in setting, default 3h
        float h = 3600000f; //1h
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String tips = sp.getString(SettingFragment.PRE_KEY_TIP3, context.getString(R.string.tip4)); //when time > 3h
        if (time < h) {
            tips = sp.getString(SettingFragment.PRE_KEY_TIP0, context.getString(R.string.tip1));
        } else if (time < 2 * h) {
            tips = sp.getString(SettingFragment.PRE_KEY_TIP1, context.getString(R.string.tip2));
        } else if (time < 3 * h) {
            tips = sp.getString(SettingFragment.PRE_KEY_TIP2, context.getString(R.string.tip3));
        }
        return tips;
    }
}
