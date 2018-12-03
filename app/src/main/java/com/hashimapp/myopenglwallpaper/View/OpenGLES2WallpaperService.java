package com.hashimapp.myopenglwallpaper.View;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.hashimapp.myopenglwallpaper.Model.GLRenderer;
import com.hashimapp.myopenglwallpaper.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Blake Hashimoto on 8/14/2015.
 */
public class OpenGLES2WallpaperService extends GLWallpaperService {
    private static Context context;
    Display display;
    SharedPreferences prefs;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static Context getAppContext() {
        return OpenGLES2WallpaperService.context;
    }

    @Override
    public Engine onCreateEngine() {
        return new OpenGLES2Engine();
    }




    class OpenGLES2Engine extends GLWallpaperService.GLEngine implements SensorEventListener, SharedPreferences.OnSharedPreferenceChangeListener{
        OpenGLES2Engine() {
            super();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            //check for GLES2 support
            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

            initSensor();
            initScreenEvent();

            if (supportsEs2) {
                setEGLContextClientVersion(2);
                setPreserveEGLContextOnPause(true);
                setRenderer(new GLRenderer());
            } else {
                return;
            }
        }

        private void initScreenEvent() {
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                        renderer.ResetMotionOffset();
                    } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    }
                }
            }, intentFilter);

        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
                                     float yStep, int xPixels, int yPixels) {
            renderer.OnOffsetChanged(xOffset, yOffset);
        }


        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                renderer.OnSensorChanged(event, display.getRotation());
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (rendererSet) {
                boolean motionParallaxEnabled = prefs.getBoolean("motion_parallax", false);

                if (visible && motionParallaxEnabled) {
                    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
                } else{
                    sensorManager.unregisterListener(this);
                    renderer.ResetMotionOffset();
                }
            }
            super.onVisibilityChanged(visible);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch(key){
                case "motion_parallax":
                    if(sharedPreferences.getBoolean("motion_parallax", true)){
                        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
                    }else{
                        sensorManager.unregisterListener(this);
                        renderer.ResetMotionOffset();
                    }
                    break;
            }
        }
//
//        @Override
//        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
//        {
//            super.onSurfaceChanged(holder, format, width, height);
//        }
//
//        @Override
//        public void onSurfaceRedrawNeeded(SurfaceHolder holder)
//        {
//            super.onSurfaceRedrawNeeded(holder);
//        }


        private void initSensor() {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            if (sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0) {
                Log.d("sensorStuff", "gyroscope registered");
                sensorManager.registerListener(this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                        SensorManager.SENSOR_DELAY_GAME);
            }
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

    }

}
