package com.hashimapp.myopenglwallpaper;

import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by Blake on 8/18/2015.
 */
public class MyPreferencesActivity extends PreferenceActivity
{
    private int REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();


    }

    SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {

        }
    };

    public static class PrefsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_preferences);
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            //set the preference file
            addPreferencesFromResource(R.xml.main_preferences);
            //set the website for the deviantart page
//            findPreference("view_artist");
//            Preference viewArtistPref = (Preference) findPreference("view_artist");
//            viewArtistPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
//            {
//                @Override
//                public boolean onPreferenceClick(Preference preference)
//                {
//                    Intent deviantArtIntent = new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("http://quirkilicious.deviantart.com/"));
//                    startActivity(deviantArtIntent);
//                    return true;
//                }
//            });
            findPreference("set_wallpaper");
            Preference setWallpaperPref = (Preference) findPreference("set_wallpaper");
            setWallpaperPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    Log.d("stuff", "onPreferenceclick() was clicked");
                    Intent setWallpaperIntent = new Intent("android.service.wallpaper.LIVE_WALLPAPER_CHOOSER");
                    startActivity(setWallpaperIntent);
                    
                    return true;
                }
            });
        }
    }
}
