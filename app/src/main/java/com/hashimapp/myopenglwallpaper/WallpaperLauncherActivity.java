package com.hashimapp.myopenglwallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class WallpaperLauncherActivity extends Activity
{
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        if(Build.VERSION.SDK_INT >= 16)
//        {
//            Log.d("Live wallpaper chooser", "tried launching the chooser");
//            Intent intent = new Intent();
//            intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
//            String pkg = OpenGLES2WallpaperService.class.getPackage().getName();
//            String cls = OpenGLES2WallpaperService.class.getCanonicalName();
//            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(pkg, cls));
//            try
//            {
//                startActivityForResult(intent, 0);
//                Log.d("Live wallpaper chooser", "activity was started");
//                this.finish();
//            }
//            catch(Exception anfe)
//            {
//                Log.d("Live wallpaper chooser", "not launched");
//                intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
//                startActivityForResult(intent, 0);
//                this.finish();
//            }
//        }
//        else
//        {
//            Log.d("Live wallpaper chooser", "skipped the thing");
//            Intent intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
//            startActivityForResult(intent, 0);
//            this.finish();
//        }
//    }

}
