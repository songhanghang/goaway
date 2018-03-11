/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.mi.song.goaway.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mi.song.goaway.R;
import com.mi.song.goaway.bean.MyUsageStats;
import com.mi.song.goaway.util.AppsUtil;
import com.mi.song.goaway.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * https://github.com/googlesamples/android-AppUsageStatistics/blob/master/Application/src/main/java/com/example/android/appusagestatistics/CustomUsageStats.java
 * Provide views to RecyclerView with the directory entries.
 */
public class UsageListAdapter extends RecyclerView.Adapter<UsageListAdapter.ViewHolder> {

    private List<MyUsageStats> mCustomUsageStatsList = new ArrayList<>();
    private Random mRandom = new Random();
    private Context mContext;
    private int[] mColors;

    public UsageListAdapter(Context context) {
        this.mContext = context;
        this.mColors = TimeUtil.getColorArray(context);
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView mCardView;
        private final TextView mPackageName;
        private final TextView mUsageInfo;
        private final ImageView mAppIcon;

        private ViewHolder(View v) {
            super(v);
            mCardView = v.findViewById(R.id.cardview);
            mPackageName = v.findViewById(R.id.package_text);
            mUsageInfo = v.findViewById(R.id.usage_text);
            mAppIcon = v.findViewById(R.id.app_icon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.usage_card, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        MyUsageStats myUsageStats = mCustomUsageStatsList.get(position);
        viewHolder.mCardView.setCardBackgroundColor(mColors[mRandom.nextInt(5)]);
        viewHolder.mPackageName.setText(AppsUtil.getAppName(mContext, myUsageStats.usageStats.getPackageName()));
        String time = TimeUtil.timeToString(myUsageStats.usageStats.getTotalTimeInForeground());
        viewHolder.mUsageInfo.setText(time);
        viewHolder.mAppIcon.setImageDrawable(myUsageStats.appIcon);
    }

    @Override
    public int getItemCount() {
        return mCustomUsageStatsList.size();
    }

    public void setCustomUsageStatsList(List<MyUsageStats> customUsageStats) {
        mCustomUsageStatsList = customUsageStats;
    }
}