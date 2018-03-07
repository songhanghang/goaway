package com.mi.song.goaway.util;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.mi.song.goaway.R;
import com.mi.song.goaway.UsageListAdapter;

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
     * @param context The context
     * @param intervalType intervalType
     * @param customUsageStatsList receiver to get UsageStats list
     */
    public static void updateAppsUsage(Context context, int intervalType, List<UsageListAdapter.CustomUsageStats> customUsageStatsList) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null)
            return;

        // Query usage apps state
        List<UsageStats> queryUsageStats = new ArrayList<>();
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

        customUsageStatsList.clear();
        for (int i = 0; i < queryUsageStats.size(); i++) {
            UsageListAdapter.CustomUsageStats customUsageStats = new UsageListAdapter.CustomUsageStats();
            customUsageStats.usageStats = queryUsageStats.get(i);
            try {
                Drawable appIcon = context.getPackageManager()
                        .getApplicationIcon(customUsageStats.usageStats.getPackageName());
                customUsageStats.appIcon = appIcon;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("", String.format("App Icon is not found for %s",
                        customUsageStats.usageStats.getPackageName()));
                customUsageStats.appIcon = context
                        .getDrawable(R.mipmap.ic_launcher);
            }
            customUsageStatsList.add(customUsageStats);
        }
    }

  /**
   * get string array for wallpaper
   * @param appUsageStrings receiver to get info array
   */
    public static void getAppInfoForWallPaper(Context context, List<UsageListAdapter.CustomUsageStats> queryUsageStats, String[] appUsageStrings) {
        // Convert to appUsageStrings
        StringBuilder itemStrBuilder = new StringBuilder();
        int index = 0;
        // Item contain app count
        int itemCount = 3;
        int size = queryUsageStats.size();
        for (int i = 0; i < size; i++) {
            UsageStats usageStats = queryUsageStats.get(i).usageStats;
            itemStrBuilder.append(getAppName(usageStats.getPackageName()))
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

    public static synchronized CharSequence getAppName(String packageName) {
        /*try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo packageInfo = packageManager.getApplicationInfo(packageName, 0);
            return packageManager.getApplicationLabel(packageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        int index = packageName.lastIndexOf(".");
        return packageName.substring(index + 1, packageName.length());
    }

}
