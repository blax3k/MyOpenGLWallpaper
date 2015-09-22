package com.hashimapp.myopenglwallpaper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

/**
 * Created by Blake Hashimoto on 8/14/2015.
 */
public class OpenGLES2WallpaperService extends GLWallpaperService
{

    //set up our main_preferences
    SharedPreferences preferences;
    GLRenderer renderer;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private float xOffset = 0;
    Display display;


    @Override
    public Engine onCreateEngine()
    {
        Log.d("GLES2 onCreateEngine", "engine was created");
        getNewRenderer();
        return new OpenGLES2Engine();

    }

    class OpenGLES2Engine extends GLWallpaperService.GLEngine
    {
        @Override
        public void onCreate(SurfaceHolder surfaceHolder)
        {
            super.onCreate(surfaceHolder);
            Log.d("GLES2 onCreate", "surface was created");

            display = ((WindowManager)
                    getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            //set the current preferences
            preferences = PreferenceManager.getDefaultSharedPreferences(OpenGLES2WallpaperService.this);

            //check for GLES2 support
            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

            if (supportsEs2)
            {
                setEGLContextClientVersion(2);

                setPreserveEGLContextOnPause(true);

                setEGLConfigChooser(new MyConfigChooser());

                setRenderer(renderer);

                //set up preference listener
                final SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(OpenGLES2WallpaperService.this);
                //set up the colors
                prefListener = new SharedPreferences.OnSharedPreferenceChangeListener()
                {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences mprefs, String key) {
                        if(mPrefs.getBoolean("activate_sunset", true))
                        {
                            renderer.changeColor(1);
                        }
                        else
                        {
                            renderer.changeColor(0);
                        }
                    }
                };
                mPrefs.registerOnSharedPreferenceChangeListener(prefListener);
            }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
                                     float yStep, int xPixels, int yPixels)
        {
            if(!preferences.getBoolean("pref_key_sim_scroll", true))
            {
                renderer.setEyeX(xOffset);
            }
//            Log.d("onOffsetsChanged", "was called on renderer " + renderer.toString());
        }

        private float xVelocity, xAcceleration, xPosition;
        private float xMax = 1.0f;
        private float xMin = -2.0f;
        public float frameTime = 0.666f;
        private float[] rawSensorData = new float[3];

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            if(preferences.getBoolean("pref_key_sim_scroll", true))
            {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                {
                    switch (display.getRotation()) {
                        case Surface.ROTATION_0:
                            rawSensorData[0] = event.values[0];
                            rawSensorData[1] = event.values[1];
                            break;
                        case Surface.ROTATION_90:
                            rawSensorData[0] = -event.values[1];
                            rawSensorData[1] = event.values[0];
                            break;
                        case Surface.ROTATION_180:
                            rawSensorData[0] = -event.values[0];
                            rawSensorData[1] = -event.values[1];
                            break;
                        case Surface.ROTATION_270:
                            rawSensorData[0] = event.values[1];
                            rawSensorData[1] = -event.values[0];
                            break;
                    }

                    accelVals = lowPass(rawSensorData, accelVals);
                    if(accelVals[0] > 10)
                        accelVals[0] = 10;
                    if(accelVals[0] < -10)
                        accelVals[0] = -10;

                    if(accelVals[1] > 5)
                        accelVals[1] = 5;
                    if(accelVals[1] < 0)
                        accelVals[1] = 0;


                    float newX = accelVals[0] * 0.03f +.5f;
                    float newY = accelVals[1] * .02f;

                    renderer.setEyeX(newX);
                    renderer.setEyeY(newY);
                }
            }
        }

        float ALPHA = 0.2f;
        float accelVals[] = new float[3];

        protected float[] lowPass(float[] input, float[] output)
        {
            if (output == null) return input;

            for( int i = 0; i < input.length; i++)
            {
                output[i] = output[i] + ALPHA * (input[i] - output[i]);
            }
            return  output;
        }

        //set up gesture detection
        private android.view.GestureDetector.OnGestureListener gestureListener = new android.view.GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                if(preferences.getBoolean("pref_key_sim_scroll", true))
//                    renderer.setEyeX( e1.getX() - e2.getX());
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        };
        GestureDetector mGestureDetector = new GestureDetector(OpenGLES2WallpaperService.this, gestureListener);

        @Override
        public void onTouchEvent(MotionEvent event)
        {
            mGestureDetector.onTouchEvent(event);
        }



        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d("GLES2 onSurfaceChanged", "the surface was changed");
        }

        @Override
        public void onSurfaceRedrawNeeded(SurfaceHolder holder)
        {
            super.onSurfaceRedrawNeeded(holder);

            Log.d("GLES2 RedrawNeeded", "the surface was redrawn");

        }

    }

    GLSurfaceView.Renderer getNewRenderer()
    {
        renderer = new GLRenderer(OpenGLES2WallpaperService.this);
        Log.d("getNewRenderer called", "new renderer" + renderer.toString());
        return renderer;
    }
}
