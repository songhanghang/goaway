/*
* Copyright 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.mi.song.goaway;

import android.app.usage.UsageStatsManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.mi.song.goaway.adapter.UsageListAdapter;
import com.mi.song.goaway.bean.MyUsageStats;
import com.mi.song.goaway.util.AppsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that demonstrates how to use App Usage Statistics API.
 */
public class UsageStatisticsFragment extends Fragment {

  private static final String TAG = "UsageStatisticsFragment";

  UsageListAdapter mUsageListAdapter;
  RecyclerView mRecyclerView;
  RecyclerView.LayoutManager mLayoutManager;
  Spinner mSpinner;

  public UsageStatisticsFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_usage_statistics, container, false);
  }

  @Override
  public void onViewCreated(View rootView, Bundle savedInstanceState) {
    super.onViewCreated(rootView, savedInstanceState);

    mUsageListAdapter = new UsageListAdapter(getActivity());
    mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_app_usage);
    mLayoutManager = mRecyclerView.getLayoutManager();
    LayoutAnimationController controller =
            AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.anim_layout_fall_down);
    mRecyclerView.setLayoutAnimation(controller);
    mRecyclerView.scrollToPosition(0);
    mRecyclerView.setAdapter(mUsageListAdapter);

    mSpinner = (Spinner) rootView.findViewById(R.id.spinner_time_span);
    SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
            R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
    mSpinner.setAdapter(spinnerAdapter);
    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      String[] strings = getResources().getStringArray(R.array.action_list);

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        StatsUsageInterval statsUsageInterval = StatsUsageInterval
                .getValue(strings[position]);
        if (statsUsageInterval != null) {
          List<MyUsageStats> list = AppsUtil.updateAppsUsageData(getActivity().getApplicationContext(), statsUsageInterval.mInterval);
          mUsageListAdapter.setCustomUsageStatsList(list);
          mUsageListAdapter.notifyDataSetChanged();
          runLayoutAnimation(mRecyclerView);
          mRecyclerView.scrollToPosition(0);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  private void runLayoutAnimation(final RecyclerView recyclerView) {

    final LayoutAnimationController controller =
            AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.anim_layout_fall_down);

    recyclerView.setLayoutAnimation(controller);
    recyclerView.getAdapter().notifyDataSetChanged();
    recyclerView.scheduleLayoutAnimation();
  }

  /**
   * Enum represents the intervals for {@link android.app.usage.UsageStatsManager} so that
   * values for intervals can be found by a String representation.
   */
  //VisibleForTesting
  static enum StatsUsageInterval {
    DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
    WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
    MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY);

    private int mInterval;
    private String mStringRepresentation;

    StatsUsageInterval(String stringRepresentation, int interval) {
      mStringRepresentation = stringRepresentation;
      mInterval = interval;
    }

    static StatsUsageInterval getValue(String stringRepresentation) {
      for (StatsUsageInterval statsUsageInterval : values()) {
        if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
          return statsUsageInterval;
        }
      }
      return null;
    }
  }
}
