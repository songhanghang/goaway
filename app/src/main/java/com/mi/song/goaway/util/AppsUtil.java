package com.mi.song.goaway.util;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.widget.Toast;
import com.mi.song.goaway.R;
import com.mi.song.goaway.bean.MyUsageStats;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author by songhang on 2018/3/5
 */

public class AppsUtil {

    /**
     * get appUsage based on intervalType
     *
     * @param context      The context
     * @param intervalType intervalType
     */
    public static List<MyUsageStats> updateAppsUsageData(Context context, int intervalType) {
        List<MyUsageStats> list = new ArrayList<>();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null)
            return list;

        // Query usage apps state
        List<UsageStats> queryUsageStats;
        long time = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        switch (intervalType) {
            case UsageStatsManager.INTERVAL_DAILY:
                queryUsageStats = usageStatsManager.queryUsageStats(intervalType, TimeUtil.getZeroTime(time), time);
                break;
            case UsageStatsManager.INTERVAL_WEEKLY:
                cal.add(Calendar.DAY_OF_MONTH, -7);
                queryUsageStats = usageStatsManager.queryUsageStats(intervalType, TimeUtil.getZeroTime(cal.getTimeInMillis()), time);
                break;
            case UsageStatsManager.INTERVAL_MONTHLY:
                cal.add(Calendar.MONTH, -1);
                queryUsageStats = usageStatsManager.queryUsageStats(intervalType, TimeUtil.getZeroTime(cal.getTimeInMillis()), time);
                break;
            default:
                queryUsageStats = usageStatsManager.queryUsageStats(intervalType, TimeUtil.getZeroTime(time), time);
                break;
        }

        if (queryUsageStats.size() == 0) {
            Toast.makeText(context, "请允许访问使用记录", Toast.LENGTH_LONG).show();
            try {
                context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            } catch (Exception e) {
                Toast.makeText(context, "该设备不支持访问使用记录", Toast.LENGTH_LONG).show();
            }
        }

        // Sort apps by use time
        Collections.sort(queryUsageStats, new Comparator<UsageStats>() {
            @Override
            public int compare(UsageStats o1, UsageStats o2) {
                return (int) (o2.getTotalTimeInForeground() - o1.getTotalTimeInForeground());
            }
        });

        for (int i = 0; i < queryUsageStats.size(); i++) {
            MyUsageStats customUsageStats = new MyUsageStats();
            customUsageStats.usageStats = queryUsageStats.get(i);
            try {
                customUsageStats.appIcon = context.getPackageManager().getApplicationIcon(customUsageStats.usageStats.getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                customUsageStats.appIcon = context.getDrawable(R.mipmap.ic_launcher);
            }
            list.add(customUsageStats);
        }

        return list;
    }

    /**
     * get string array for wallpaper
     *
     */
    public static String[] updateWallpaperStringArray(Context context) {
        String[] appUsageStrings = new String[5];
        // Convert to appUsageStrings
        StringBuilder itemStrBuilder = new StringBuilder();
        int index = 0;
        // Item contain app count
        int itemCount = 3;
        List<MyUsageStats> list = updateAppsUsageData(context, UsageStatsManager.INTERVAL_DAILY);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            UsageStats usageStats = list.get(i).usageStats;
            itemStrBuilder.append(getAppName(context, usageStats.getPackageName()))
                    .append(" : ")
                    .append(TimeUtil.timeToString(usageStats.getTotalTimeInForeground()))
                    .append("\n");

            if ((i + 1) % itemCount == 0 || i == size - 1) {
                appUsageStrings[index++] = "< Num " + index + " >" + "\n" + itemStrBuilder.toString();
                itemStrBuilder = new StringBuilder();
                if (index == appUsageStrings.length) {
                    break;
                }
            }
        }
        return appUsageStrings;
    }

    public static synchronized CharSequence getAppName(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo packageInfo = packageManager.getApplicationInfo(packageName, 0);
            return packageManager.getApplicationLabel(packageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageName;
    }

}
