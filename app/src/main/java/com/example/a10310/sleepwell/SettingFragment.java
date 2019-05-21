package com.example.a10310.sleepwell;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private EditTextPreference mAgeText;
    private EditTextPreference mHighText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pre_main);

        mAgeText = (EditTextPreference) getPreferenceScreen().findPreference("edit_text_preference_1");
        mHighText = (EditTextPreference) getPreferenceScreen().findPreference("edit_text_preference_2");

        mAgeText.setOnPreferenceChangeListener(this);
        mHighText.setOnPreferenceChangeListener(this);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mAgeText.setSummary(spf.getString("edit_text_preference_1", "Please enter a city"));
        mHighText.setSummary(spf.getString("edit_text_preference_2", "175"));
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "edit_text_preference_1":
                mAgeText.setSummary(sharedPreferences.getString("edit_text_preference_1", "Please enter a city"));
                break;
            case "edit_text_preference_2":
                mHighText.setSummary(sharedPreferences.getString("edit_text_preference_2", "Please enter a city"));
                break;
        }
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }
}
