package com.hashimapp.myopenglwallpaper.View;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.R;

/**
 * Created by Blake on 8/18/2015.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MyPreferenceFragment()).commit();
    }


    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        Resources resources;
        SharedPreferences prefs;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            Log.d("Live Wallpaper Chooser", "onCreate reached");
            super.onCreate(savedInstanceState);
            //set the preference file
            resources = this.getResources();
            addPreferencesFromResource(R.xml.settings);
            prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            initPreferences();
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(resources.getString(R.string.motion_parallax_key))) {
                SetMotionParallaxStrengthEnabled(sharedPreferences);
            }
        }

        private void initPreferences() {
            SetMotionParallaxStrengthEnabled(prefs);
        }

        private void SetMotionParallaxStrengthEnabled(SharedPreferences prefs) {

            String motionParallaxStrengthKey = resources.getString(R.string.motion_parallax_strength_key);
            if (prefs.getBoolean(resources.getString(R.string.motion_parallax_key), true)) {
                getPreferenceScreen().findPreference(motionParallaxStrengthKey).setEnabled(true);
            } else {
                getPreferenceScreen().findPreference(motionParallaxStrengthKey).setEnabled(false);
            }
        }
    }
}


