package com.hashimapp.myopenglwallpaper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by Blake on 8/18/2015.
 */
public class MyPreferencesActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();

    }


    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_preferences);
            findPreference("view_artist");
            Preference viewArtistPref = (Preference) findPreference("view_artist");
            viewArtistPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    Intent deviantArtIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://quirkilicious.deviantart.com/"));
                    startActivity(deviantArtIntent);
                    return true;
                }
            });
        }
    }


}
