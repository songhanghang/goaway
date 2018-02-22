package com.mi.song.goaway;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author by songhang on 2018/2/21 Email: songhang@xiaomi.com
 */

public class TimeUtil {

    private static final int SECONDS_IN_DAY = 60 * 60 * 24;
    private static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;
    private static final String PREFERENCE_NAME = "time";

    private static final SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");

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
        long zero = time/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset();
        return zero;
    }

    public static  boolean isSameDay(long time1, long time2) {
        return getZeroTime(time1) == getZeroTime(time2);
    }
}
