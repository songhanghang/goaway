package com.mi.song.goaway;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author by songhang on 2018/2/21
 */

public class TimeUtil {

    private static final String PREFERENCE_NAME = "time";
    private static final ArgbEvaluator sArgbEvaluator = new ArgbEvaluator();
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

    public static int getColor(long time) {
        float h = 3600000f;

        int blue = 0XFF4A7FEB;
        int green = 0XFF46BF7F;
        int orange = 0XFFFF8746;
        int red = 0XFFFF6243;

        int color = Color.RED;
        if (time < h) {
            color = (Integer) sArgbEvaluator.evaluate(time / h, blue, green);
        } else if (time < 2 * h) {
            color = (Integer) sArgbEvaluator.evaluate(time / h - 1, green, orange);
        } else if (time < 3 * h) {
            color = (Integer) sArgbEvaluator.evaluate(time / h - 2, orange, red);
        } else if (time < 4 * h) {
            color = (Integer) sArgbEvaluator.evaluate(time / h - 3, red, Color.RED);
        }
        return color;
    }

    public static String getTips(long time) {
        float h = 3600000f;
        String tips = "自暴自弃...";
        if (time < h) {
            tips = "怡情";
        } else if (time < 2 * h) {
            tips = "适度";
        } else if (time < 3 * h) {
            tips = "伤身";
        } else if (time < 4 * h) {
            tips = "收手!goaway!";
        }
        return tips;
    }
}
