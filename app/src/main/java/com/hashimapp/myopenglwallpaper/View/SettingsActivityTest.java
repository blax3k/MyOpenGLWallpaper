package com.hashimapp.myopenglwallpaper.View;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.hashimapp.myopenglwallpaper.Model.GLRenderer;
import com.hashimapp.myopenglwallpaper.R;

public class SettingsActivityTest extends Activity implements SensorEventListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static Context context;
    private MyGLSurfaceView glSurfaceView;
    boolean rendererSet;
    protected SensorManager sensorManager;
    protected Sensor sensor;
    public GLRenderer renderer;
    Display display;
    SharedPreferences prefs;
    Resources resources;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        glSurfaceView = findViewById(R.id.GLView);

        SetRenderer(new GLRenderer(this));
        context = this.getApplicationContext();
        resources = context.getResources();
        display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        initSensor();
    }


    @Override
    public void onResume() {
        super.onResume();
//        if (rendererSet)
//        {
//            glSurfaceView.onResume();
//            glSurfaceView.requestRender();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(rendererSet){
//            glSurfaceView.onPause();
//        }
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
    public void onDestroy()
    {
        super.onDestroy();
        glSurfaceView.onDestroy();
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
            }
        } else if (key.equals(resources.getString(R.string.motion_parallax_strength_key)))
        {
            renderer.SetMotionOffsetStrength(sharedPreferences.getInt(resources.getString(R.string.motion_parallax_strength_key), 6));
        } else if (key.equals(resources.getString(R.string.time_phase_key)))
        {
            String timePhase = prefs.getString(resources.getString(R.string.time_phase_key), resources.getString(R.string.time_key_automatic));
            renderer.SetTimePhase(timePhase);
        }
        else if (key.equals(resources.getString(R.string.touch_offset_setting_key)))
        {
            boolean touchOffsetEnabled = sharedPreferences.getBoolean(resources.getString(R.string.touch_offset_setting_key), true);
            renderer.SetTouchOffset(touchOffsetEnabled);
        } else if(key.equals(resources.getString(R.string.blur_enabled_key))){
            boolean motionBlurEnabled = sharedPreferences.getBoolean(resources.getString(R.string.blur_enabled_key), true);
            renderer.SetCameraBlurEnabled(motionBlurEnabled);
        } else if(key.equals(resources.getString(R.string.rack_focus_enabled_key))){
            boolean rackFocusEnabled = sharedPreferences.getBoolean(resources.getString(R.string.rack_focus_enabled_key), true);
            renderer.SetRackFocusEnabled(rackFocusEnabled);
        } else if(key.equals(resources.getString(R.string.setting_zoom_camera_key))){
            boolean zoomCameraEnabled = sharedPreferences.getBoolean(resources.getString(R.string.setting_zoom_camera_key), true);
            renderer.SetZoomCameraEnabled(zoomCameraEnabled);
        }
    }

    private void SetRenderer(MyGLSurfaceView.Renderer newRenderer){
        renderer = (GLRenderer) newRenderer;
        glSurfaceView.setRenderer(newRenderer);
        rendererSet = true;
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
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
}
