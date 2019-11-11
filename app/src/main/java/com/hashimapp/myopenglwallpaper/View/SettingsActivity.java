package com.hashimapp.myopenglwallpaper.View;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.hashimapp.myopenglwallpaper.Model.MyLocation;
import com.hashimapp.myopenglwallpaper.R;

/**
 * Created by Blake on 8/18/2015.
 */
public class SettingsActivity extends PreferenceActivity
{
    private final static int MY_PERMISSIONS_REQUEST_LOCATION = 99;

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
        SharedPreferences sharedPreferences;
        MyLocation locationManager;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            Log.d("Live Wallpaper Chooser", "onCreate reached");
            super.onCreate(savedInstanceState);
            //set the preference file
            resources = this.getResources();
            addPreferencesFromResource(R.xml.settings);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
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
            } else if (key.equals(resources.getString(R.string.location_setting_key)))
            {
                SetLocationEnabled(sharedPreferences);
            }
        }

        private void SetLocationEnabled(SharedPreferences prefs)
        {
            String locationSettingKey = resources.getString(R.string.location_setting_key);
            SwitchPreference locationPreferenceSwitch = (SwitchPreference) findPreference(resources.getString(R.string.location_setting_key));
            if (prefs.getBoolean(locationSettingKey, true))
            {
                if (ContextCompat.checkSelfPermission(super.getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            } else
            {
                if (locationPreferenceSwitch != null)
                {
                    locationPreferenceSwitch.setChecked(false);
                }
            }

        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
        {
            switch (requestCode)
            {
                case MY_PERMISSIONS_REQUEST_LOCATION:
                {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                    {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(resources.getString(R.string.location_setting_key), false);
                        editor.apply();

                        SwitchPreference locationPreferenceSwitch = (SwitchPreference) findPreference(resources.getString(R.string.location_setting_key));
                        if (locationPreferenceSwitch != null)
                        {
                            locationPreferenceSwitch.setChecked(false);
                        }
                    }
                }
                break;
            }
        }

        private void initPreferences()
        {
            SetMotionParallaxStrengthEnabled(sharedPreferences);
//            SetAutomaticTimeEnabled(sharedPreferences);
            SetCameraBlurEnabled(sharedPreferences);
            SetLocationEnabled(sharedPreferences);

//            Preference seekbar = getPreferenceScreen().findPreference("pref_max_volume");
//            seekbar.setTitle("hello there");
        }

        public void SetWallpaper(View view)
        {
            Log.d("Live wallpaper chooser", "tried launching the chooser");
            Intent intent = new Intent();
            intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this.getContext(), OpenGLES2WallpaperService.class));
            try
            {
                startActivity(intent);
            } catch (ActivityNotFoundException ex)
            {
                Log.d("Live wallpaper chooser", ex.getMessage());
            }
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


//        private void SetAutomaticTimeEnabled(SharedPreferences sharedPreferences)
//        {
//            String setTimePref = resources.getString(R.string.time_phase_key);
//            if (sharedPreferences.getBoolean(resources.getString(R.string.auto_time_setting_key), true))
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


