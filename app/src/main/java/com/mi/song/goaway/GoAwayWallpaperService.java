package com.mi.song.goaway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import java.util.Locale;

/**
 * @author by songhang on 2018/2/21
 */

public class GoAwayWallpaperService extends WallpaperService {
    private static final String TAG = "goaway";
    private long mUsedTime;
    private long mStartTime;

    private long calcNewUsedTime() {
        long endTime = System.currentTimeMillis();
        // 新的一天开始了
        if (!TimeUtil.isSameDay(mStartTime, endTime)) {
            mUsedTime = 0;
            mStartTime = TimeUtil.getZeroTime(endTime);
        }
        return mUsedTime + (endTime - mStartTime);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        return new AwayEngine();
    }

    private class AwayEngine extends Engine {
        private Paint textPaint;
        private float bottom = 24; // 距离底部


        private BroadcastReceiver mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    mUsedTime = TimeUtil.getTodayUsedTime(context);
                    mStartTime = System.currentTimeMillis();
                    doDraw();
                    Log.i(TAG, "屏幕点亮");
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    TimeUtil.setTodayUsedTime(context, calcNewUsedTime());
                    Log.i(TAG, "屏幕关闭");

                }
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(24);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setAntiAlias(true);
            textPaint.setDither(true);

            mUsedTime = TimeUtil.getTodayUsedTime(GoAwayWallpaperService.this);
            mStartTime = System.currentTimeMillis();

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
            } else {

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

            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas == null) {
                    return;
                }
                long usedTime = calcNewUsedTime();
                canvas.drawColor(TimeUtil.getColor(usedTime));
                String useStr = isChineseLanguage() ? "已使用: " : "used: ";
                String timeStr = TimeUtil.timeToString(usedTime);
                String tips = TimeUtil.getTips(usedTime);
                String content = useStr + timeStr + " | " + tips;
                canvas.drawText(content, MainActivity.WIGHT / 2, MainActivity.HEIGHT - bottom, textPaint);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
        }

    }

    public static boolean isChineseLanguage() {
        return TextUtils.equals(Locale.getDefault().getLanguage(), Locale.CHINA.getLanguage());
    }
}
