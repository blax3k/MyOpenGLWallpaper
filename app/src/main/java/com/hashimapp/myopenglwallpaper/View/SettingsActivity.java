package com.hashimapp.myopenglwallpaper.View;

import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.hashimapp.myopenglwallpaper.MainActivityPresenter;
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

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            Log.d("Live Wallpaper Chooser", "onCreate reached");
            super.onCreate(savedInstanceState);
            //set the preference file
            addPreferencesFromResource(R.xml.settings_activity);

        }
    }
}
