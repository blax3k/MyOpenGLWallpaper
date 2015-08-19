package com.hashimapp.myopenglwallpaper;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Blake on 8/18/2015.
 */
public class PrefsFragment extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_preferences);
    }
}
