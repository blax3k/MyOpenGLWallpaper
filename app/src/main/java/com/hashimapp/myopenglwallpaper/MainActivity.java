package com.hashimapp.myopenglwallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
        Intent intent = new Intent(this, MyPreferencesActivity.class);
        startActivity(intent);
    }
    public void setWallpaper(View view)
    {
        if (Build.VERSION.SDK_INT >= 16)
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
            } catch (Exception anfe)
            {
                Log.d("Live wallpaper chooser", "not launched");
                intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
                startActivity(intent);
            }
        } else
        {
            Log.d("Live wallpaper chooser", "skipped the thing");
            Intent intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
            startActivity(intent);
        }
    }
}
