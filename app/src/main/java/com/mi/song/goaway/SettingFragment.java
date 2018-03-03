package com.mi.song.goaway;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static String PRE_KEY_TIP0 = "pre_key_tip0";
    public static String PRE_KEY_TIP1 = "pre_key_tip1";
    public static String PRE_KEY_TIP2 = "pre_key_tip2";
    public static String PRE_KEY_TIP3 = "pre_key_tip3";
    public static String PRE_KEY_BOTTOM = "pre_key_bottom";

    EditTextPreference editText0;
    EditTextPreference editText1;
    EditTextPreference editText2;
    EditTextPreference editText3;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        editText0 = (EditTextPreference) findPreference(PRE_KEY_TIP0);
        editText1 = (EditTextPreference) findPreference(PRE_KEY_TIP1);
        editText2 = (EditTextPreference) findPreference(PRE_KEY_TIP2);
        editText3 = (EditTextPreference) findPreference(PRE_KEY_TIP3);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        setSummary(sp);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        editText0.setSummary(sharedPreferences.getString(PRE_KEY_TIP0, getString(R.string.tip0)));
        editText1.setSummary(sharedPreferences.getString(PRE_KEY_TIP1, getString(R.string.tip1)));
        editText2.setSummary(sharedPreferences.getString(PRE_KEY_TIP2, getString(R.string.tip2)));
        editText3.setSummary(sharedPreferences.getString(PRE_KEY_TIP3, getString(R.string.tip3)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setSummary(SharedPreferences sharedPreferences) {
        editText0.setSummary(sharedPreferences.getString(PRE_KEY_TIP0, getString(R.string.tip0)));
        editText1.setSummary(sharedPreferences.getString(PRE_KEY_TIP1, getString(R.string.tip1)));
        editText2.setSummary(sharedPreferences.getString(PRE_KEY_TIP2, getString(R.string.tip2)));
        editText3.setSummary(sharedPreferences.getString(PRE_KEY_TIP3, getString(R.string.tip3)));
    }
}
