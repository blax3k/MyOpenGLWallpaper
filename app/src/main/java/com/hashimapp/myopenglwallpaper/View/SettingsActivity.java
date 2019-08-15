package com.hashimapp.myopenglwallpaper.View;

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
public class SettingsActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MyPreferenceFragment()).commit();
    }


    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        Resources resources;
        SharedPreferences prefs;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            Log.d("Live Wallpaper Chooser", "onCreate reached");
            super.onCreate(savedInstanceState);
            //set the preference file
            resources = this.getResources();
            addPreferencesFromResource(R.xml.settings);
            prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            initPreferences();
        }

        @Override
        public void onResume()
        {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause()
        {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            Log.d("pref change", "key: " + key);
            if (key.equals(resources.getString(R.string.motion_parallax_key)))
            {
                SetMotionParallaxStrengthEnabled(sharedPreferences);
            } else if (key.equals(resources.getString(R.string.auto_time_setting_key)))
            {
//                SetAutomaticTimeEnabled(sharedPreferences);
            } else if (key.equals(resources.getString(R.string.blur_enabled_key)))
            {
                SetCameraBlurEnabled(sharedPreferences);
            }
        }

        private void initPreferences()
        {
            SetMotionParallaxStrengthEnabled(prefs);
//            SetAutomaticTimeEnabled(prefs);
            SetCameraBlurEnabled(prefs);

//            Preference seekbar = getPreferenceScreen().findPreference("pref_max_volume");
//            seekbar.setTitle("hello there");
        }

        private void SetMotionParallaxStrengthEnabled(SharedPreferences prefs)
        {
            String motionParallaxStrengthKey = resources.getString(R.string.motion_parallax_strength_key);
            if (prefs.getBoolean(resources.getString(R.string.motion_parallax_key), true))
            {
                getPreferenceScreen().findPreference(motionParallaxStrengthKey).setEnabled(true);
            } else
            {
                getPreferenceScreen().findPreference(motionParallaxStrengthKey).setEnabled(false);
            }
        }


//        private void SetAutomaticTimeEnabled(SharedPreferences prefs)
//        {
//            String setTimePref = resources.getString(R.string.time_phase_key);
//            if (prefs.getBoolean(resources.getString(R.string.auto_time_setting_key), true))
//            {
//                getPreferenceScreen().findPreference(setTimePref).setEnabled(false);
//            } else
//            {
//                getPreferenceScreen().findPreference(setTimePref).setEnabled(true);
//            }
//        }

        private void SetCameraBlurEnabled(SharedPreferences prefs)
        {
            String rackFocusKey = resources.getString(R.string.rack_focus_enabled_key);
            if (prefs.getBoolean(resources.getString(R.string.blur_enabled_key), true))
            {
                getPreferenceScreen().findPreference(rackFocusKey).setEnabled(true);
            } else
            {
                getPreferenceScreen().findPreference(rackFocusKey).setEnabled(false);
            }
        }


    }
}


