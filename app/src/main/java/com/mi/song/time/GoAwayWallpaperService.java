package com.mi.song.time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;

import com.mi.song.time.util.AppsUtil;
import com.mi.song.time.util.ScreenUtil;
import com.mi.song.time.util.TimeUtil;

/**
 * @author by songhang on 2018/2/21
 */

public class GoAwayWallpaperService extends WallpaperService {
  private static final String TAG = "goaway";
  private static final long TRIGGER_PRESS_DELAY_MILLIS = 1300;
  private static final int DITHER_OFFSET = 300;

  // The time you used phone
  // The time when the screen on
  private int mHeight;
  private int mWidth;
  private long mTotalTime;
  // Display top usage apps
  private boolean mIsDrawApps;

  private Handler mHandler = new Handler();

  @Override
  public void onCreate() {
    super.onCreate();
    mHeight = ScreenUtil.getHeight(this);
    mWidth = ScreenUtil.getWidth(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mHandler.removeCallbacksAndMessages(null);
  }

  @Override
  public Engine onCreateEngine() {
    return new AwayEngine();
  }

  private class AwayEngine extends Engine {
    private TextPaint itemTextPaint;
    private Context context = GoAwayWallpaperService.this;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_SCREEN_ON.equals(action)) {
          // Because onVisible will call before, so wo need doDraw again!
          doDraw();
          Log.i(TAG, "屏幕点亮");
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
          Log.i(TAG, "屏幕关闭");
        }
      }
    };

    @Override
    public void onCreate(SurfaceHolder surfaceHolder) {
      super.onCreate(surfaceHolder);

      itemTextPaint = new TextPaint();
      itemTextPaint.setTextSize(60);
      itemTextPaint.setColor(Color.WHITE);
      itemTextPaint.setAntiAlias(true);
      itemTextPaint.setDither(true);

      IntentFilter filter = new IntentFilter();
      filter.addAction(Intent.ACTION_SCREEN_ON);
      filter.addAction(Intent.ACTION_SCREEN_OFF);
      registerReceiver(mReceiver, filter);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
      super.onVisibilityChanged(visible);
      if (visible) {
        doDraw();
      }
    }

    @Override
    public void onDestroy() {
      super.onDestroy();
      unregisterReceiver(mReceiver);
    }

    private void doDraw() {
      SurfaceHolder surfaceHolder = getSurfaceHolder();
      if (surfaceHolder == null) {
        return;
      }

      long time = System.currentTimeMillis();
      Canvas canvas = null;
      try {
        canvas = surfaceHolder.lockCanvas();
        if (canvas == null) {
          return;
        }
        // Draw background
        canvas.drawColor(TimeUtil.getColor(mTotalTime, context));

        // Draw bottom tips
        Pair<Long, String> kwaiUsageInfo = AppsUtil.getKwaiUsageInfo(context);
        mTotalTime = kwaiUsageInfo.first;
        String content = kwaiUsageInfo.second;
        if (!TextUtils.isEmpty(content)) {
          StaticLayout sl = new StaticLayout(content, itemTextPaint, mWidth,
              Layout.Alignment.ALIGN_NORMAL, 1.5f, 0.0f, true);
          canvas.save();
          canvas.translate(66, mHeight / 2 - 300);
          sl.draw(canvas);
          canvas.restore();
        }
      } catch (Exception | OutOfMemoryError e) {
        e.printStackTrace();
      } finally {
        if (canvas != null) {
          try {
            surfaceHolder.unlockCanvasAndPost(canvas);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      Log.i(TAG, "draw time : " + (System.currentTimeMillis() - time));
    }
  }

}
