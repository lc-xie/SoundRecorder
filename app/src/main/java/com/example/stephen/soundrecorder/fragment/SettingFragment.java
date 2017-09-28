package com.example.stephen.soundrecorder.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.example.stephen.soundrecorder.R;
import com.example.stephen.soundrecorder.SettingActivity;

/**
 * Created by stephen on 17-9-27.
 */

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.set_preference);

        CheckBoxPreference checkBoxPreference=(CheckBoxPreference)findPreference("check_set_pref");
        checkBoxPreference.setChecked(false);
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return false;
            }
        });

        Preference preference=(Preference)findPreference("version_set_pref");
        preference.setSummary("v{1.0}");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LicensesFragment licensesFragment = new LicensesFragment();
                licensesFragment.show(((SettingActivity)getActivity()).getSupportFragmentManager().beginTransaction(), "dialog_licenses");
                return true;
            }
        });
    }
}
