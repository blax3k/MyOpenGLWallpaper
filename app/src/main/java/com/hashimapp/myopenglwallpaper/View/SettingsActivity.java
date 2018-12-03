package com.hashimapp.myopenglwallpaper.View;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.R;

/**
 * Created by Blake on 8/18/2015.
 */
public class SettingsActivity extends Activity
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
            addPreferencesFromResource(R.xml.settings);
        }
    }

}
