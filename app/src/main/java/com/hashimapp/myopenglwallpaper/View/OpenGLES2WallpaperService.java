package com.hashimapp.myopenglwallpaper.View;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.hashimapp.myopenglwallpaper.Model.GLRenderer;
import com.hashimapp.myopenglwallpaper.R;

import java.time.DateTimeException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Blake Hashimoto on 8/14/2015.
 */
public class OpenGLES2WallpaperService extends GLWallpaperService
{
    private static Context context;
    Resources resources;
    Display display;
    SharedPreferences prefs;
    private Date startDate;


    @Override
    public void onCreate()
    {
        startDate = new Date();
        Log.d("create", "OpenGLES2WallpaperService " + startDate + "onCreate");
        super.onCreate();
        context = this.getApplicationContext();
        resources = context.getResources();
        display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

    }

    public static Context getAppContext()
    {
        return OpenGLES2WallpaperService.context;
    }

    @Override
    public Engine onCreateEngine()
    {
        return new OpenGLES2Engine();
    }


    class OpenGLES2Engine extends GLWallpaperService.GLEngine implements SensorEventListener, SharedPreferences.OnSharedPreferenceChangeListener
    {
        private GestureDetector gestureListener;
        Date startTime;

        OpenGLES2Engine()
        {
            super();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder)
        {
            startTime  = new Date();
            super.onCreate(surfaceHolder);
            Log.d("create", "OpenGLES2Engine " + startDate + " onCreate");
            //check for GLES2 support
            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

            initSensor();
            initScreenEvent();
            prefs.registerOnSharedPreferenceChangeListener(this);

            if (supportsEs2)
            {
                setEGLContextClientVersion(2);
                setPreserveEGLContextOnPause(true);


                setRenderer(new GLRenderer());
                gestureListener = new GestureDetector(getApplicationContext(), new GestureListener(renderer));
                Log.d("create", "OpenGLES2Engine " + startDate + " created renderer " +
                        renderer.startDate + " in surface view " + glSurfaceView.startDate);


                SetRendererPrefs();
                setTouchEventsEnabled(true);
            } else
            {
                return;
            }

        }


        private void SetRendererPrefs()
        {
            renderer.EnableMotionOffset(prefs.getBoolean(resources.getString(R.string.motion_parallax_key), true));
            renderer.SetMotionOffsetStrength(prefs.getInt(resources.getString(R.string.motion_parallax_strength_key), 6));
            renderer.SetTouchOffset(prefs.getBoolean(resources.getString(R.string.touch_offset_setting_key), true));
            renderer.setAutoTimeEnabled(prefs.getBoolean(resources.getString(R.string.auto_time_setting_key), true));
            renderer.SetTimePhase(prefs.getString(resources.getString(R.string.time_phase_key), resources.getString(R.string.time_key_day)));
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
                                     float yStep, int xPixels, int yPixels)
        {
            if (renderer.OnOffsetChanged(xOffset, yOffset))
            {
                glSurfaceView.requestRender();
            }
        }


        @Override
        public void onSensorChanged(SensorEvent event)
        {
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
            {
                renderer.OnSensorChanged(event, display.getRotation());
                glSurfaceView.requestRender();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }


        @Override
        public void onVisibilityChanged(boolean visible)
        {
            if (rendererSet)
            {
                boolean motionParallaxEnabled = prefs.getBoolean("motion_parallax", false);

                if (visible)
                {
                    if (motionParallaxEnabled)
                    {
                        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
                    }
                    renderer.UpdateVisibility(visible);

                    glSurfaceView.requestRender();
                } else
                {
                    //not visible
                    sensorManager.unregisterListener(this);
                    renderer.UpdateVisibility(visible);
                }
            }
            super.onVisibilityChanged(visible);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            Log.d("prefsChange", "preferences changed");
            if (key.equals(resources.getString(R.string.motion_parallax_key)))
            {
                if (sharedPreferences.getBoolean(resources.getString(R.string.motion_parallax_key), true))
                {
                    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
                    renderer.EnableMotionOffset(true);
                } else
                {
                    sensorManager.unregisterListener(this);
                    renderer.EnableMotionOffset(false);
                    renderer.ResetMotionOffset();
                }
            } else if (key.equals(resources.getString(R.string.motion_parallax_strength_key)))
            {
                renderer.SetMotionOffsetStrength(sharedPreferences.getInt(resources.getString(R.string.motion_parallax_strength_key), 6));
            } else if (key.equals(resources.getString(R.string.time_phase_key)))
            {
                String timePhase = prefs.getString(resources.getString(R.string.time_phase_key), resources.getString(R.string.time_key_day));
                renderer.SetTimePhase(timePhase);
            } else if (key.equals(resources.getString(R.string.auto_time_setting_key)))
            {
                boolean enabled = prefs.getBoolean(resources.getString(R.string.auto_time_setting_key), true);
                renderer.setAutoTimeEnabled(enabled);
            } else if (key.equals(resources.getString(R.string.touch_offset_setting_key)))
            {
                boolean touchOffsetEnabled = sharedPreferences.getBoolean(resources.getString(R.string.touch_offset_setting_key), true);
                renderer.SetTouchOffset(touchOffsetEnabled);
                if (!touchOffsetEnabled)
                {
                    renderer.ResetTouchOffset();
                }
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event)
        {
            gestureListener.onTouchEvent(event);
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


        private void initScreenEvent()
        {
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
                    {
                        renderer.ResetMotionOffset();
                    } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
                    {
                    }
                }
            }, intentFilter);

        }

        private void initSensor()
        {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            if (sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0)
            {
                Log.d("sensorStuff", "gyroscope registered");
                sensorManager.registerListener(this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                        SensorManager.SENSOR_DELAY_GAME);
            }
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }


        private class GestureListener extends GestureDetector.SimpleOnGestureListener
        {
            public Date startDate;

            public GestureListener(GLRenderer newRenderer){
                renderer = newRenderer;
                startDate = new Date();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e)
            {
                Log.d("create", "gesture listener " + startDate + " tapped on renderer " +
                        renderer.startDate);
                renderer.SwapTextures();
////                if(temp){
////                    renderer.SetTimeSetting(resources.getString(R.string.time_day));
////                    temp = false;
////                }else{
////                    renderer.SetTimeSetting(resources.getString(R.string.time_dawn));
////                    temp = true;
////                }
//                glSurfaceView.requestRender();
                return super.onDoubleTap(e);
            }

        }

    }


}
