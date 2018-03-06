package com.mi.song.goaway;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * @author by songhang on 2018/2/21
 */

public class GoAwayWallpaperService extends WallpaperService {
    private static final String TAG = "goaway";
    private static final long TRIGGER_PRESS_DELAY_MILLIS = 1300;
    private static final int DITHER_OFFSET = 100;

    // The time you used phone
    private long mUsedTime;
    // The time when the screen on
    private long mStartTime;
    private int mHeight;
    private int mWidth;
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

    private long calcNewUsedTime() {
        long endTime = System.currentTimeMillis();
        // 新的一天开始了
        if (!TimeUtil.isSameDay(mStartTime, endTime)) {
            mUsedTime = 0;
            mStartTime = TimeUtil.getZeroTime(endTime);
        }
        return mUsedTime + (endTime - mStartTime);
    }

    private class AwayEngine extends Engine {
        private float touchDownX;
        private float touchDownY;

        private Paint guideTextPaint;
        private Paint tipsTextPaint;
        private Paint itemBackgroundPaint;
        private TextPaint itemTextPaint;

        private String[] appUsageStrings = new String[5];
        private Context context = GoAwayWallpaperService.this;

        private BroadcastReceiver mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    mUsedTime = TimeUtil.getTodayUsedTime(context);
                    mStartTime = System.currentTimeMillis();
                    // Because onVisible will call before, so wo need doDraw again!
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

            guideTextPaint = new Paint();
            guideTextPaint.setColor(Color.WHITE);
            guideTextPaint.setTextSize(100);
            guideTextPaint.setTextAlign(Paint.Align.CENTER);
            guideTextPaint.setAntiAlias(true);
            guideTextPaint.setDither(true);

            tipsTextPaint = new Paint();
            tipsTextPaint.setColor(Color.WHITE);
            tipsTextPaint.setTextSize(24);
            tipsTextPaint.setTextAlign(Paint.Align.CENTER);
            tipsTextPaint.setAntiAlias(true);
            tipsTextPaint.setDither(true);

            itemBackgroundPaint = new Paint();
            itemBackgroundPaint.setStyle(Paint.Style.FILL);
            itemBackgroundPaint.setAntiAlias(true);
            itemBackgroundPaint.setDither(true);

            itemTextPaint = new TextPaint();
            itemTextPaint.setTextSize(30);
            itemTextPaint.setColor(Color.WHITE);
            itemTextPaint.setAntiAlias(true);
            itemTextPaint.setDither(true);

            mUsedTime = TimeUtil.getTodayUsedTime(context);
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
                long usedTime = calcNewUsedTime();
                // Draw background
                canvas.drawColor(TimeUtil.getColor(usedTime, context));

                // Draw press guide
                if (!TimeUtil.isSeenAppUsage(context)) {
                    canvas.drawText(context.getString(R.string.try_press), mWidth / 2, mHeight / 2, guideTextPaint);
                }
                // Draw top usage apps
                if (mIsDrawApps) {
                    int itemWidth = mWidth;
                    int itemHeight = mHeight / 5;
                    int[] colors = TimeUtil.getColorArray(context);
                    int length = colors.length;
                    for (int i = length - 1; i >= 0; i--) {
                        itemBackgroundPaint.setColor(colors[i]);
                        int top = (length - i - 1) * itemHeight;
                        canvas.drawRect(0, top, itemWidth, top + itemHeight, itemBackgroundPaint);
                        canvas.save();
                        StaticLayout sl = new StaticLayout(appUsageStrings[length - i - 1], itemTextPaint, mWidth, Layout.Alignment.ALIGN_CENTER, 1.5f, 0.0f, true);
                        canvas.translate(0, top + itemHeight / 5);
                        sl.draw(canvas);
                        canvas.restore();
                    }
                }
                // Draw bottom tips
                String useStr = getString(R.string.used_hint);
                String timeStr = TimeUtil.timeToString(usedTime);
                String tips = TimeUtil.getTips(usedTime, context);
                String content = useStr + ": " + timeStr + " | " + tips;
                int textBottom = ScreenUtil.getTipsBottom(context);
                canvas.drawText(content, mWidth / 2, mHeight - textBottom, tipsTextPaint);
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

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchDownX = event.getX();
                    touchDownY = event.getY();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!TimeUtil.isSeenAppUsage(context)) {
                                TimeUtil.setSeenAppUsage(context);
                            }

                            mIsDrawApps = true;
                            AppsUtil.updateAppsUsage(context, appUsageStrings);
                            doDraw();
                        }
                    }, TRIGGER_PRESS_DELAY_MILLIS);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Ignore dither
                    if (Math.abs(touchDownX - event.getX()) < DITHER_OFFSET
                            && Math.abs(touchDownY - event.getY()) < DITHER_OFFSET ) {
                          break;
                    }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mIsDrawApps) {
                        mIsDrawApps = false;
                        doDraw();
                    }
                    mHandler.removeCallbacksAndMessages(null);
                    break;
            }
        }
    }

}
