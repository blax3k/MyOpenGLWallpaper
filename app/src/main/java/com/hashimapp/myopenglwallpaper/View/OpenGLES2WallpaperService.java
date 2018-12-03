package com.hashimapp.myopenglwallpaper.View;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.hashimapp.myopenglwallpaper.Model.GLRenderer;

/**
 * Created by Blake Hashimoto on 8/14/2015.
 */
public class OpenGLES2WallpaperService extends GLWallpaperService {
    private static Context context;
    Display display;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    public static Context getAppContext() {
        return OpenGLES2WallpaperService.context;
    }

    @Override
    public Engine onCreateEngine() {
        return new OpenGLES2Engine();
    }


    class OpenGLES2Engine extends GLWallpaperService.GLEngine implements SensorEventListener {
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

            if (supportsEs2) {
                setEGLContextClientVersion(2);
                setPreserveEGLContextOnPause(true);
                setRenderer(new GLRenderer());


            } else {
                return;
            }

        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
                                     float yStep, int xPixels, int yPixels) {
            renderer.OnOffsetChanged(xOffset, yOffset);
        }


        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                renderer.OnSensorChanged(event.values, display.getRotation());
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }


        @Override
        public void onVisibilityChanged(boolean visible) {
            if (rendererSet) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                sensorManager.unregisterListener(this);
            }
            super.onVisibilityChanged(visible);
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
            if (sensorManager.getSensorList(Sensor.TYPE_GRAVITY).size() > 0) {
                sensorManager.registerListener(this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                        SensorManager.SENSOR_DELAY_GAME);
            }
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

}
