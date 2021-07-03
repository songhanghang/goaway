package com.mi.song.time.util;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Pair;
import android.widget.Toast;

import com.mi.song.time.R;
import com.mi.song.time.bean.AppData;
import com.mi.song.time.bean.MyUsageStats;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    UsageStatsManager usageStatsManager =
        (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    if (usageStatsManager == null) {
      return list;
    }

    // Query usage apps state
    List<UsageStats> queryUsageStats;
    long time = System.currentTimeMillis();
    Calendar cal = Calendar.getInstance();
    switch (intervalType) {
      case UsageStatsManager.INTERVAL_DAILY:
        queryUsageStats =
            usageStatsManager.queryUsageStats(intervalType, TimeUtil.getZeroTime(time), time);
        break;
      case UsageStatsManager.INTERVAL_WEEKLY:
        cal.add(Calendar.DAY_OF_MONTH, -7);
        queryUsageStats = usageStatsManager
            .queryUsageStats(intervalType, TimeUtil.getZeroTime(cal.getTimeInMillis()), time);
        break;
      case UsageStatsManager.INTERVAL_MONTHLY:
        cal.add(Calendar.MONTH, -1);
        queryUsageStats = usageStatsManager
            .queryUsageStats(intervalType, TimeUtil.getZeroTime(cal.getTimeInMillis()), time);
        break;
      default:
        queryUsageStats =
            usageStatsManager.queryUsageStats(intervalType, TimeUtil.getZeroTime(time), time);
        break;
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
        customUsageStats.appIcon = context.getPackageManager()
            .getApplicationIcon(customUsageStats.usageStats.getPackageName());
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
        customUsageStats.appIcon = context.getDrawable(R.mipmap.ic_android);
      }
      list.add(customUsageStats);
    }

    return list;
  }

  public static boolean tryAccessIfNeed(Context context) {
    if (!checkUsagePermission(context)) {
      Toast.makeText(context, "请允许访问使用记录", Toast.LENGTH_LONG).show();
      try {
        context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
      } catch (Exception e) {
        Toast.makeText(context, "该设备不支持访问使用记录", Toast.LENGTH_LONG).show();
      }
      return false;
    } else {
      return true;
    }
  }

  public static boolean checkUsagePermission(Context context) {
    AppOpsManager
        appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
    if (appOps != null) {
      int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(),
          context.getPackageName());
      return mode == AppOpsManager.MODE_ALLOWED;
    }

    return false;
  }

  /**
   * get string array for wallpaper
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

  public static Pair<Long, String> getKwaiUsageInfo(Context context) {
    StringBuilder stringBuilder = new StringBuilder();
    long totalTime = 0;
    Map<String, AppData> usedAppsMap = DataManager.getInstance().getUsedAppsMap(context, SortOrder.TODAY.ordinal());
    AppData kwai = usedAppsMap.get("com.smile.gifmaker");
    if (kwai != null) {
      totalTime += kwai.mUsageTime;
      stringBuilder.append("\n快手:    ").append(TimeUtil.humanReadableMillis(kwai.mUsageTime));
    }
    AppData nebula = usedAppsMap.get("com.kuaishou.nebula");
    if (nebula != null) {
      totalTime += nebula.mUsageTime;
      stringBuilder.append("\n极速版: ").append(TimeUtil.humanReadableMillis(nebula.mUsageTime));
    }

    stringBuilder.append("\n统计:    ").append(TimeUtil.humanReadableMillis(totalTime));
    boolean success = totalTime > TimeUnit.HOURS.toMillis(1);
    if (success) {
      stringBuilder.append("\n！恭喜老铁已达标 ！ ");
    } else {
      stringBuilder.append("\n尚未达标，继续加油 ");
    }

    return new Pair<>(totalTime, stringBuilder.toString());
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
