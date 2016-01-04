package com.hashimapp.myopenglwallpaper;

import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.service.wallpaper.WallpaperService;
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
                new MyPreferenceFragment()).commit();


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
            findPreference("set_wallpaper");
            Preference setWallpaperPref = (Preference) findPreference("set_wallpaper");
            setWallpaperPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    if(Build.VERSION.SDK_INT >= 16)
                    {
                        Log.d("Live wallpaper chooser", "tried launching the chooser");
                        Intent intent = new Intent();
                        intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                        String pkg = OpenGLES2WallpaperService.class.getPackage().getName();
                        String cls = OpenGLES2WallpaperService.class.getCanonicalName();
                        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(pkg, cls));
                        try
                        {
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            Log.d("Live wallpaper chooser", "activity was started");
                        }
                        catch(Exception anfe)
                        {
                            Log.d("Live wallpaper chooser", "not launched");
                            intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
                            startActivity(intent);
                        }
                    }
                    else
                    {
                        Log.d("Live wallpaper chooser", "skipped the thing");
                        Intent intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
                        startActivity(intent);
                    }
//                    Log.d("stuff", "onPreferenceclick() was clicked");

                    return true;
                }
            });
        }
    }
}
