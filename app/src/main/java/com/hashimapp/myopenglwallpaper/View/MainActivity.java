package com.hashimapp.myopenglwallpaper.View;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hashimapp.myopenglwallpaper.R;
import com.hashimapp.myopenglwallpaper.View.SettingsActivity;
import com.hashimapp.myopenglwallpaper.View.OpenGLES2WallpaperService;

/**
 * Created by User on 10/5/2015.
 */
public class MainActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void launchSettings(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void SetWallpaper(View view)
    {
        Log.d("Live wallpaper chooser", "tried launching the chooser");
        Intent intent = new Intent();
        intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, OpenGLES2WallpaperService.class));
        try
        {
            startActivity(intent);
        } catch (ActivityNotFoundException ex)
        {
            Log.d("Live wallpaper chooser", ex.getMessage());
        }
    }

    public void VisitArtist(View view)
    {
        String url = "http://www.soundlesswind.com/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
