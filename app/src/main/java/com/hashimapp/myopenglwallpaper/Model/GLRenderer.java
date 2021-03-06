package com.hashimapp.myopenglwallpaper.Model;

import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.Model.DataStorage.SceneIndex;
import com.hashimapp.myopenglwallpaper.R;
import com.hashimapp.myopenglwallpaper.SceneData.SceneManager;
import com.luckycatlabs.sunrisesunset.dto.Location;


public class GLRenderer implements Renderer
{

    SceneSetter sceneSetter;
    // Our screen resolution
    int mScreenWidth;
    int mScreenHeight;
    int widthHeight;

    Date dateCreated;
    Location location;
    Context context;
    Resources resources;
    TimeTracker timeTracker;
    GLCamera camera;
    SceneManager sceneManager;

    private String timePhaseSelected;
    private boolean _cameraBlurEnabled;
    private boolean _rackFocusEnabled;
    private boolean _cameraZoomEnabled;
    private boolean _created;
    int timeOfDay, percentage;


    public GLRenderer(Context context)
    {
        this.context = context;
        resources = context.getResources();

        dateCreated = new Date();
        camera = new GLCamera();
        sceneSetter = new SceneSetter(context);
        location = new Location(47.760012, -122.307209);
        timeTracker = new TimeTracker(resources);
        timePhaseSelected = resources.getString(R.string.time_key_day);
        sceneManager = new SceneManager(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        startTime = System.currentTimeMillis();
        if (sceneSetter == null)
        {
            sceneSetter = new SceneSetter(this.context);
        }
        // Set the clear color to white
        GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

        sceneSetter.OnSurfaceCreated();
        sceneSetter.SetMaxBlurAmount(camera.maxBlur);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glFlush();

        mScreenHeight = width;
        mScreenHeight = height;
        int newWidthHeight = Math.max(width, height);

        if(!_created){
            _created = true;
            widthHeight = newWidthHeight;
            sceneSetter.InitScene(timeOfDay, percentage, 0, widthHeight);
        }

        GLES20.glViewport(0, 0, width, height);

        camera.OnSurfaceChanged(width, height);
        sceneSetter.SurfaceChanged(camera.IsPortrait(), camera.GetXOffsetPosition(),
                camera.GetTouchOffsetScale(), camera.GetMotionOffsetScale(), mScreenWidth, mScreenHeight);
    }

    public boolean OnOffsetChanged(float xOffset, float yOffset)
    {
        if (camera.TouchOffsetEnabled())
        {
            camera.SetXOffset(xOffset);
            return true;
        }
        return false;
    }

    public void OnSensorChanged(SensorEvent event, int rotation)
    {
        if (camera.MotionOffsetEnabled())
        {
            camera.SensorChanged(event, rotation);
        }
    }

    public void UpdateVisibility(boolean visible)
    {
        Log.d("rotate", "visibility changed. visible: " + visible);
        if (visible)
        {
            UpdateTime();
            if (timeTracker.SceneChangeRequired())
            {
                timeTracker.SignalSceneChange();
                int transition = sceneSetter.QueueScene(sceneManager.GetNextScene(), timeOfDay, percentage, 0);
                sceneSetter.InitSceneChange(transition);
            }

            if (_cameraBlurEnabled)
            {
                if(_rackFocusEnabled)
                {
                    sceneSetter.ResetFocalPoint();
                }
                else {
                    sceneSetter.SetToTargetFocalPoint();
                }
            }else{
                sceneSetter.TurnOffBlur();
            }

            if(_cameraZoomEnabled){
                sceneSetter.ResetZoomPercent();
            }

        } else
        {
//            if(_cameraZoomEnabled){
//                sceneSetter.ResetZoomPercent();
//            }
            camera.ResetSensorOffset();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SetScene(String scene)
    {
        int transition = sceneSetter.QueueScene(scene, timeOfDay, percentage, 0);
        sceneSetter.InitSceneChange(transition);
    }

    public void SetMotionOffsetStrength(int offsetStrength)
    {
        camera.setMotionOffsetStrength(offsetStrength);
        sceneSetter.SetMotionsSale(camera.GetMotionOffsetScale());
    }

    public void SetMotionOffsetInverted(boolean inverted){
        camera.SetMotionOffsetInverted(inverted);
    }

    public void SetTouchOffsetEnabled(boolean touchOffsetOn)
    {
        if (!touchOffsetOn)
        {
            float xOffset = 0.5f; //halfway between 0 and 1.0
            OnOffsetChanged(xOffset, 0.0f);
        }
        camera.EnableTouchOffset(touchOffsetOn);
    }

    public void SetCameraBlurAmount(int amount)
    {
        camera.maxBlur = amount;
        if (amount == 0)
        {
            _cameraBlurEnabled = false;
            sceneSetter.TurnOffBlur();
        } else
        {
            _cameraBlurEnabled = true;
            sceneSetter.SetMaxBlurAmount(amount);
        }
    }

    public void SetLocation(double latitude, double longitude)
    {
        location = new Location(latitude, longitude);
        timeTracker.SetLocation(location);
        UpdateTime();
    }

    public void SetDefaultLocation(){
        timeTracker.SetDefaultLocation();
        UpdateTime();
    }

    public void SetRackFocusEnabled(boolean enabled)
    {
        _rackFocusEnabled = enabled;
        if(!enabled){
            sceneSetter.SetToTargetFocalPoint();
        }
    }

    public void SetZoomCameraEnabled(boolean enabled){
        _cameraZoomEnabled = enabled;
        if(!enabled){
            Log.d("zoom", "set to target zoom point");
            sceneSetter.SetToMaxZoomPercent();
        }
    }

    public void SetParticlesEnabled(boolean enabled){
        sceneSetter.SetParticlesEnabled(enabled);
    }


    public void SetTimePhase(String phaseOfDay)
    {
        timePhaseSelected = phaseOfDay;
        UpdateTime();
    }

    private void UpdateTime()
    {
        int[] timeInfo;

        timeInfo = timeTracker.GetTimePhase(timePhaseSelected, null);

        timeOfDay = timeInfo[TimeTracker.TIME_PHASE_INDEX];
        percentage = timeInfo[TimeTracker.TIME_PHASE_PROGRESSION_INDEX];

        sceneSetter.SetTimeOfDay(timeOfDay, percentage);
    }


    private int mFPS = 0;         // the value to show
    private int mFPSCounter = 0;  // the value to count
    private long mFPSTime = 0;     // last update time

    long lastFrameTime;
    long endTime, deltaTime, startTime;


    @Override
    public void onDrawFrame(GL10 unused)
    {
        if (SystemClock.uptimeMillis() - mFPSTime > 3000)
        {
            mFPSTime = SystemClock.uptimeMillis();
            mFPS = mFPSCounter;
            mFPSCounter = 0;
        } else
        {
            mFPSCounter++;
        }

        if (sceneSetter.TransitioningScene())
        {
            sceneSetter.UpdateFade();
        }

        if (sceneSetter.RackingFocus())
        {
            sceneSetter.UpdateFocalPoint();
        }

        if(sceneSetter.ZoomingCamera()){
            sceneSetter.UpdateZoomPoint();
        }

        GLES20.glUseProgram(riGraphicTools.sp_Image);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        sceneSetter.DrawSprites(camera);
    }

}
