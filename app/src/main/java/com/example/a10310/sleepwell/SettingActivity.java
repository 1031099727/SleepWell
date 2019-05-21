package com.example.a10310.sleepwell;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SettingActivity extends FragmentActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.SettingContainer, new SettingFragment()).commit();
    }




}
