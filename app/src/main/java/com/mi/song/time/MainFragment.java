package com.mi.song.time;


import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mi.song.time.util.AppsUtil;



/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_CODE = 2323;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View useBtn = view.findViewById(R.id.use_btn);
        useBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.use_btn) {
            use();
        }
    }

    private void use() {
        try {
            if (AppsUtil.tryAccessIfNeed(getContext())) {
                Intent intent = new Intent();
                intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT
                    , new ComponentName(getActivity().getPackageName()
                        , GoAwayWallpaperService.class.getCanonicalName()));

                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
