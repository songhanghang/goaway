package com.mi.song.goaway;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Intent intent = new Intent();
            intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);//android.service.wallpaper.CHANGE_LIVE_WALLPAPER
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT
                    , new ComponentName(getApplicationContext().getPackageName()
                            , GoAwayWallpaperService.class.getCanonicalName()));

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
