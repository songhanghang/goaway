package com.mi.song.goaway;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.takisoft.fix.support.v7.preference.ColorPickerPreference;
import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import java.util.Locale;

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String PRE_KEY_TIP0 = "pre_key_tip0";
    public static final String PRE_KEY_TIP1 = "pre_key_tip1";
    public static final String PRE_KEY_TIP2 = "pre_key_tip2";
    public static final String PRE_KEY_TIP3 = "pre_key_tip3";
    public static final String PRE_KEY_BOTTOM = "pre_key_bottom";

    public static final String PRE_KEY_COLOR0 = "pref_key_color0";
    public static final String PRE_KEY_COLOR1 = "pref_key_color1";
    public static final String PRE_KEY_COLOR2 = "pref_key_color2";
    public static final String PRE_KEY_COLOR3 = "pref_key_color3";
    public static final String PRE_KEY_COLOR4 = "pref_key_color4";

    public static final int BLUE = 0XFF4A7FEB;
    public static final int GREEN = 0XFF46BF7F;
    public static final int ORGANE = 0XFFFF8746;
    public static final int LIGHT_RED = 0XFFFF6243;
    public static final int RED = 0xFFFF0000;

    EditTextPreference editText0;
    EditTextPreference editText1;
    EditTextPreference editText2;
    EditTextPreference editText3;

    EditTextPreference editTextBottom;

    ColorPickerPreference colorPicker0;
    ColorPickerPreference colorPicker1;
    ColorPickerPreference colorPicker2;
    ColorPickerPreference colorPicker3;
    ColorPickerPreference colorPicker4;

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
        editTextBottom = (EditTextPreference) findPreference(PRE_KEY_BOTTOM);

        colorPicker0 = (ColorPickerPreference) findPreference(PRE_KEY_COLOR0);
        colorPicker1 = (ColorPickerPreference) findPreference(PRE_KEY_COLOR1);
        colorPicker2 = (ColorPickerPreference) findPreference(PRE_KEY_COLOR2);
        colorPicker3 = (ColorPickerPreference) findPreference(PRE_KEY_COLOR3);
        colorPicker4 = (ColorPickerPreference) findPreference(PRE_KEY_COLOR4);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        setSummary(sp);
        setColor(sp);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setSummary(sharedPreferences);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setSummary(SharedPreferences sharedPreferences) {
        editText0.setSummary(sharedPreferences.getString(PRE_KEY_TIP0, getString(R.string.tip1)));
        editText1.setSummary(sharedPreferences.getString(PRE_KEY_TIP1, getString(R.string.tip2)));
        editText2.setSummary(sharedPreferences.getString(PRE_KEY_TIP2, getString(R.string.tip3)));
        editText3.setSummary(sharedPreferences.getString(PRE_KEY_TIP3, getString(R.string.tip4)));

        int height = ScreenUtil.getHeight(getContext());
        int bottomDistance = ScreenUtil.getTipsBottom(getContext());
        String bottomInPreference;

        //check range
        if (bottomDistance < 0 || bottomDistance > height) {
            bottomInPreference = getString(R.string.bottom_distance);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PRE_KEY_BOTTOM, bottomInPreference);
            editor.apply();
        }

        editTextBottom.setSummary(String.format(Locale.getDefault(), getString(R.string.bottom_summary_format), height, bottomDistance));
    }

    private void setColor(SharedPreferences sharedPreferences) {
        colorPicker0.setColor(sharedPreferences.getInt(PRE_KEY_COLOR0, BLUE));
        colorPicker1.setColor(sharedPreferences.getInt(PRE_KEY_COLOR1, GREEN));
        colorPicker2.setColor(sharedPreferences.getInt(PRE_KEY_COLOR2, ORGANE));
        colorPicker3.setColor(sharedPreferences.getInt(PRE_KEY_COLOR3, LIGHT_RED));
        colorPicker4.setColor(sharedPreferences.getInt(PRE_KEY_COLOR4, RED));
    }
}
