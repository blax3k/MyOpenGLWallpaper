package com.hashimapp.myopenglwallpaper.View;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.hashimapp.myopenglwallpaper.Model.GLRenderer;
import com.hashimapp.myopenglwallpaper.Model.MyLocation;
import com.hashimapp.myopenglwallpaper.R;

import java.io.IOException;
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
    MyLocation locationManager;
    private boolean _useCurrentLocation;

    @Override
    public void onCreate()
    {
        startDate = new Date();
        Log.d("create", "OpenGLES2WallpaperService " + startDate + "onCreate");
        super.onCreate();
        context = this.getApplicationContext();
        String path = context.getFilesDir().getPath();
        String path1 = context.getFilesDir().getAbsolutePath();
        try
        {
            String path2 = context.getFilesDir().getCanonicalPath();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        resources = context.getResources();
        display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        locationManager = new MyLocation();
        _useCurrentLocation = prefs.getBoolean(resources.getString(R.string.location_setting_key), false);

    }



    @Override
    public Engine onCreateEngine()
    {
        return new OpenGLES2Engine(this);
    }


    class OpenGLES2Engine extends GLWallpaperService.GLEngine implements SensorEventListener, SharedPreferences.OnSharedPreferenceChangeListener
    {
        private GestureDetector gestureListener;
        Context context;
        Date startTime;
        long lastLocationUpdate;
        private final long LOCATION_UPDATE_INTERVAL = 1000 * 60 * 60 * 2;

        OpenGLES2Engine(Context context)
        {
            super();
            this.context = context;
        }


        @Override
        public void onCreate(SurfaceHolder surfaceHolder)
        {
            startTime = new Date();
            super.onCreate(surfaceHolder);
            Log.d("create", "OpenGLES2Engine " + startDate + " onCreate");
            //check for GLES2 support
            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

            initSensor();
            prefs.registerOnSharedPreferenceChangeListener(this);

            if (supportsEs2)
            {
                glSurfaceView.setEGLContextClientVersion(2);
                glSurfaceView.setPreserveEGLContextOnPause(true);
                if (renderer == null)
                {
                    setRenderer(new GLRenderer(context));
                }
                gestureListener = new GestureDetector(getApplicationContext(), new GestureListener(renderer));
//                Log.d("create", "OpenGLES2Engine " + startDate + " created renderer " +
//                        renderer.startDate + " in surface view " + glSurfaceView.startDate);

                InitRendererPrefs();
                setTouchEventsEnabled(true);
                UpdateLocation(true);
            } else
            {
                return;
            }

        }


        private void InitRendererPrefs()
        {
            renderer.SetMotionOffsetStrength(prefs.getInt(resources.getString(R.string.motion_parallax_strength_key), 6));
            renderer.SetTouchOffsetEnabled(prefs.getBoolean(resources.getString(R.string.touch_offset_setting_key), true));
            renderer.SetTimePhase(prefs.getString(resources.getString(R.string.time_phase_key), resources.getString(R.string.time_key_automatic)));
            renderer.SetRackFocusEnabled(prefs.getBoolean(resources.getString(R.string.rack_focus_enabled_key), true));
            renderer.SetCameraBlurAmount(prefs.getInt(resources.getString(R.string.blur_amount_key), 5));
            renderer.SetZoomCameraEnabled(prefs.getBoolean(resources.getString(R.string.setting_zoom_camera_key), true));
            renderer.SetParticlesEnabled(prefs.getBoolean(resources.getString(R.string.particle_enabled_key), true));
            //always set the scene last
            renderer.SetScene(prefs.getString(resources.getString(R.string.set_scene_key),""));
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
//                    if (motionParallaxEnabled)
//                    {
                        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
//                    }

                    UpdateLocation(false);

                    renderer.UpdateVisibility(visible);
//                    renderer.SwapTextures();
                    glSurfaceView.requestRender();
                } else
                {
                    //not visible
                    sensorManager.unregisterListener(this);
                    renderer.UpdateVisibility(visible);
                    locationManager.cancelTimer();
                    glSurfaceView.requestRender();
                }
            }
            super.onVisibilityChanged(visible);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            if (key.equals(resources.getString(R.string.motion_parallax_strength_key)))
            {
                int strength = sharedPreferences.getInt(resources.getString(R.string.motion_parallax_strength_key), 6);
                if (strength == 0)
                {
                    sensorManager.unregisterListener(this);
                } else
                {
                    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
                }
                renderer.SetMotionOffsetStrength(sharedPreferences.getInt(resources.getString(R.string.motion_parallax_strength_key), 6));
            } else if (key.equals(resources.getString(R.string.time_phase_key)))
            {
                String timePhase = prefs.getString(resources.getString(R.string.time_phase_key), resources.getString(R.string.time_key_automatic));
                renderer.SetTimePhase(timePhase);
            } else if (key.equals(resources.getString(R.string.touch_offset_setting_key)))
            {
                boolean touchOffsetEnabled = sharedPreferences.getBoolean(resources.getString(R.string.touch_offset_setting_key), true);
                Log.d("touch", "touch offset enabled: " + touchOffsetEnabled);
                renderer.SetTouchOffsetEnabled(touchOffsetEnabled);
            } else if (key.equals(resources.getString(R.string.blur_amount_key)))
            {
                int blurAmount = sharedPreferences.getInt(resources.getString(R.string.blur_amount_key), 5);
                renderer.SetCameraBlurAmount(blurAmount);

            } else if (key.equals(resources.getString(R.string.rack_focus_enabled_key)))
            {
                boolean rackFocusEnabled = sharedPreferences.getBoolean(resources.getString(R.string.rack_focus_enabled_key), true);
                renderer.SetRackFocusEnabled(rackFocusEnabled);
                renderer.UpdateVisibility(true);
            } else if(key.equals(resources.getString(R.string.location_setting_key)))
            {
                _useCurrentLocation = prefs.getBoolean(resources.getString(R.string.location_setting_key), false);
                UpdateLocation(true);
            } else if (key.equals(resources.getString(R.string.setting_zoom_camera_key)))
            {
                boolean cameraZoomEnabled = sharedPreferences.getBoolean(resources.getString(R.string.setting_zoom_camera_key), true);
                renderer.SetZoomCameraEnabled(cameraZoomEnabled);
                renderer.UpdateVisibility(true);
            }else if (key.equals(resources.getString(R.string.invert_motion_parallax_key))){
                boolean motionOffsetInverted = sharedPreferences.getBoolean(resources.getString(R.string.invert_motion_parallax_key), false);
                renderer.SetMotionOffsetInverted(motionOffsetInverted);
            }else if(key.equals(resources.getString(R.string.set_scene_key))){
                String scene = sharedPreferences.getString(resources.getString(R.string.set_scene_key), "");
                renderer.SetScene(scene);
            }else if(key.equals(resources.getString(R.string.particle_enabled_key)))
            {
                boolean particlesEnabled = sharedPreferences.getBoolean(resources.getString(R.string.particle_enabled_key), true);
                renderer.SetParticlesEnabled(particlesEnabled);
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event)
        {
            gestureListener.onTouchEvent(event);
        }

        private void initSensor()
        {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            if (sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0)
            {
                sensorManager.registerListener(this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                        SensorManager.SENSOR_DELAY_GAME);
            }
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        private void UpdateLocation(boolean updateNow)
        {
            if(_useCurrentLocation)
            {
                MyLocation.LocationResult locationResult = new MyLocation.LocationResult()
                {
                    @Override
                    public void gotLocation(android.location.Location location)
                    {
                        if (renderer != null && location != null)
                        {
                            renderer.SetLocation(location.getLatitude(), location.getLongitude());
                            Log.d("locationstuff", "set location. lat: " + location.getLatitude() + " long: " + location.getLongitude());
                        }
                    }
                };

                long currentTime = System.currentTimeMillis();
                if (updateNow || currentTime - lastLocationUpdate > LOCATION_UPDATE_INTERVAL)
                {
                    Log.d("locationstuff", "location updated");
                    lastLocationUpdate = currentTime;
                    locationManager.getLocation(context, locationResult);
                }
            }else{
                renderer.SetDefaultLocation();
            }
        }


        private class GestureListener extends GestureDetector.SimpleOnGestureListener
        {

            public GestureListener(GLRenderer newRenderer)
            {
                renderer = newRenderer;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e)
            {
//                renderer.SwapTextures();
//                renderer.ZoomCamera();
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
