package com.mi.song.goaway;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author by songhang on 2018/3/5
 */

public class AppsUtil {

    public static void updateAppsUsage(Context context, String[] appUsageStrings) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null)
            return;

        // Query usage apps state
        long time = System.currentTimeMillis();
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, TimeUtil.getZeroTime(time),
                TimeUtil.getZeroTime(time + TimeUtil.DAY));
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

        // Convert to appUsageStrings
        StringBuilder itemStrBuilder = new StringBuilder();
        int index = 0;
        // Item contain app count
        int itemCount = 3;
        int size = queryUsageStats.size();
        for (int i = 0; i < size; i++) {
            UsageStats usageStats = queryUsageStats.get(i);
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
