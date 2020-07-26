package com.hashimapp.myopenglwallpaper.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.hashimapp.myopenglwallpaper.R;
import com.hashimapp.myopenglwallpaper.SceneData.SceneManager;

import java.util.Arrays;
import java.util.List;

public class SettingsActivityTest extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener
{

    private static Context context;
    boolean rendererSet;
    protected SensorManager sensorManager;
    protected Sensor sensor;
    Display display;
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;
    Resources resources;

    private Button setTimeButton;
    private SeekBar motionOffsetSeekbar;
    int motionOffsetMax;
    int motionOffsetDefault;
    private Switch invertMotionParallaxSwitch;
    private Switch touchOffsetParallaxSwitch;
    private Switch cameraZoomSwitch;
    private SeekBar blurIntensitySeekbar;
    private Switch rackFocusSwitch;
    private Switch useCurrentLocationSwitch;
    private Switch enableParticleSwitch;
    private TextView setTimeText;
    private Button setSceneButton;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        context = this.getApplicationContext();
        resources = context.getResources();
        display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        prefsEditor = prefs.edit();
        InitControls();
        SetupSettings();
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.set_time_button:
                PresentTimesPopup();
                break;
            case R.id.invert_motion_parallax:
                break;
            case R.id.set_scene_button:
                PresentScenesPopup();
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Log.d("prefsChange", "preferences changed");
        if (key.equals(resources.getString(R.string.motion_parallax_strength_key)))
        {
        } else if (key.equals(resources.getString(R.string.time_phase_key)))
        {
            String timePhase = prefs.getString(resources.getString(R.string.time_phase_key), resources.getString(R.string.time_key_automatic));
        } else if (key.equals(resources.getString(R.string.touch_offset_setting_key)))
        {
            boolean touchOffsetEnabled = sharedPreferences.getBoolean(resources.getString(R.string.touch_offset_setting_key), true);
        } else if (key.equals(resources.getString(R.string.blur_enabled_key)))
        {
            boolean motionBlurEnabled = sharedPreferences.getBoolean(resources.getString(R.string.blur_enabled_key), true);
        } else if (key.equals(resources.getString(R.string.rack_focus_enabled_key)))
        {
            boolean rackFocusEnabled = sharedPreferences.getBoolean(resources.getString(R.string.rack_focus_enabled_key), true);
        } else if (key.equals(resources.getString(R.string.setting_zoom_camera_key)))
        {
            boolean zoomCameraEnabled = sharedPreferences.getBoolean(resources.getString(R.string.setting_zoom_camera_key), true);
        }
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

    private void InitControls()
    {
        setTimeButton = findViewById(R.id.set_time_button);
        setTimeButton.setOnClickListener(this);
        setTimeText = findViewById(R.id.set_time_text);

        setSceneButton = findViewById(R.id.set_scene_button);
        setSceneButton.setOnClickListener(this);

        motionOffsetSeekbar = findViewById(R.id.motion_parallax_strength);
        motionOffsetSeekbar.setOnSeekBarChangeListener(this);

        invertMotionParallaxSwitch = findViewById(R.id.invert_motion_parallax);
        invertMotionParallaxSwitch.setOnCheckedChangeListener(this);

        touchOffsetParallaxSwitch = findViewById(R.id.touch_offset);
        touchOffsetParallaxSwitch.setOnClickListener(this);

        cameraZoomSwitch = findViewById(R.id.camera_zoom);
        cameraZoomSwitch.setOnCheckedChangeListener(this);

        blurIntensitySeekbar = findViewById(R.id.blur_amount);
        blurIntensitySeekbar.setOnSeekBarChangeListener(this);

        rackFocusSwitch = findViewById(R.id.rack_focus_enabled);
        rackFocusSwitch.setOnCheckedChangeListener(this);

        useCurrentLocationSwitch = findViewById(R.id.use_current_location);
        useCurrentLocationSwitch.setOnCheckedChangeListener(this);

        enableParticleSwitch = findViewById(R.id.particle_enabled);
        enableParticleSwitch.setOnCheckedChangeListener(this);
    }


    private void PresentTimesPopup()
    {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(resources.getString(R.string.set_time_title));
        builder.setNegativeButton("Cancel", null);

        // add a radio button list
        String[] timeTitles = getResources().getStringArray(R.array.setTimePrefTitles);
        String timePref = prefs.getString(resources.getString(R.string.time_phase_key),
                                          resources.getString(R.string.auto_time_setting_key));
        int selected = Arrays.asList(resources.getStringArray(R.array.setTimePrefValues)).indexOf(timePref);
        builder.setSingleChoiceItems(timeTitles, selected, (dialog, which) ->
                {
                    if(which >= 0){
                        prefsEditor.putString(
                                resources.getString(R.string.time_phase_key),
                                resources.getStringArray(R.array.setTimePrefValues)[which]);

                        prefsEditor.commit();
                        setTimeText.setText(resources.getTextArray(R.array.setTimePrefTitles)[which]);
                    }
                    dialog.dismiss();
                }
        );


        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void PresentScenesPopup()
    {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(resources.getString(R.string.set_scene_title));
        builder.setNegativeButton("Cancel", null);

        // add a radio button list
        String[] sceneTitles = SceneManager.GetAllSceneTitles();
        int scenePref = prefs.getInt(resources.getString(R.string.set_scene_key),
                SceneManager.GetAllScenes()[1]);
        List<Integer> sceneArray = Arrays.asList(SceneManager.GetAllScenes());
        int selected = sceneArray.indexOf(scenePref);
        builder.setSingleChoiceItems(sceneTitles, selected, (dialog, which) ->
                {
                    if(which >= 0){
                        prefsEditor.putInt(
                                resources.getString(R.string.set_scene_key),
                                SceneManager.GetAllScenes()[which]);

                        prefsEditor.commit();
                        setTimeText.setText(resources.getTextArray(R.array.setTimePrefTitles)[which]);
                    }
                    dialog.dismiss();
                }
        );


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void SetupSettings()
    {
        motionOffsetSeekbar.setProgress(prefs.getInt(resources.getString(R.string.motion_parallax_strength_key), 5));
        invertMotionParallaxSwitch.setChecked(prefs.getBoolean(resources.getString(R.string.invert_motion_parallax_key), false));
        touchOffsetParallaxSwitch.setChecked(prefs.getBoolean(resources.getString(R.string.touch_offset_setting_key), true));
        cameraZoomSwitch.setChecked(prefs.getBoolean(resources.getString(R.string.setting_zoom_camera_key), true));
        blurIntensitySeekbar.setProgress(prefs.getInt(resources.getString(R.string.blur_amount_key), 3));
        rackFocusSwitch.setChecked(prefs.getBoolean(resources.getString(R.string.rack_focus_enabled_key), true));
        useCurrentLocationSwitch.setChecked(prefs.getBoolean(resources.getString(R.string.location_setting_key), false));
        enableParticleSwitch.setChecked(prefs.getBoolean(resources.getString(R.string.particle_enabled_key), true));
        setTimeText.setText(GetTimeTitle());

    }

    private String getResourceString(int id){
        return resources.getString(id);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        switch (buttonView.getId())
        {
            case R.id.invert_motion_parallax:
                prefsEditor.putBoolean(resources.getString(R.string.invert_motion_parallax_key), isChecked);
                break;
            case R.id.touch_offset:
                prefsEditor.putBoolean(resources.getString(R.string.touch_offset_setting_key), isChecked);
                break;
            case R.id.camera_zoom:
                prefsEditor.putBoolean(resources.getString(R.string.setting_zoom_camera_key), isChecked);
                break;
            case R.id.rack_focus_enabled:
                prefsEditor.putBoolean(resources.getString(R.string.rack_focus_enabled_key), isChecked);
                break;
            case R.id.use_current_location:
                prefsEditor.putBoolean(resources.getString(R.string.location_setting_key), isChecked);
                break;
            case R.id.particle_enabled:
                prefsEditor.putBoolean(resources.getString(R.string.particle_enabled_key), isChecked);
        }
        prefsEditor.commit();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        switch (seekBar.getId())
        {
            case R.id.motion_parallax_strength:
                prefsEditor.putInt(resources.getString(R.string.motion_parallax_strength_key), progress);
                break;
            case R.id.blur_amount:
                prefsEditor.putInt(resources.getString(R.string.blur_amount_key), progress);
                break;
        }
        prefsEditor.commit();
    }

    private String GetTimeTitle(){
        String currentTime = prefs.getString(resources.getString(R.string.time_phase_key), null);
        String[] timePrefValues = resources.getStringArray(R.array.setTimePrefValues);
        int selected = Arrays.asList(timePrefValues).indexOf(currentTime);
        if(selected < 0)
        {
            selected = 0;
        }
        return resources.getStringArray(R.array.setTimePrefTitles)[selected];
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }
}
