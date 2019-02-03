package com.hashimapp.myopenglwallpaper.View;

import android.app.Activity;
import android.content.SharedPreferences;
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
        SharedPreferences prefs;
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            Log.d("Live Wallpaper Chooser", "onCreate reached");
            super.onCreate(savedInstanceState);
            //set the preference file
            addPreferencesFromResource(R.xml.settings);
            prefs = PreferenceManager.getDefaultSharedPreferences(OpenGLES2WallpaperService.getAppContext());
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

            switch(key){
                case "motion_parallax":
                    if(sharedPreferences.getBoolean("motion_parallax", true)){
                        getPreferenceScreen().findPreference("motion_parallax_strength").setEnabled(true);
                    }else{
                        getPreferenceScreen().findPreference("motion_parallax_strength").setEnabled(false);
                    }
                    break;
            }
        }

        private void initPreferences(){
            if(prefs.getBoolean("motion_parallax", true)){
                getPreferenceScreen().findPreference("motion_parallax_strength").setEnabled(true);
            }else{
                getPreferenceScreen().findPreference("motion_parallax_strength").setEnabled(false);
            }
        }
    }

}
