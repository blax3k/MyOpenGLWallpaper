package com.hashimapp.myopenglwallpaper.Model;

import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;
import android.util.Log;

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
    int _bitmapSize;

    Date dateCreated;
    Location location;
    Context context;
    Resources resources;
    TimeTracker timeTracker;
    GLCamera camera;

//    private boolean autoTimeEnabled;
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
    }

    public boolean OnOffsetChanged(float xOffset, float yOffset)
    {
        if (camera.TouchOffsetEnabled())
        {
            float newXOffset = camera.GetXOffset(xOffset);
            sceneSetter.OffsetChanged(newXOffset);
            return true;
        }
        return false;
    }


    public void OnSensorChanged(SensorEvent event, int rotation)
    {
        if (camera.MotionOffsetEnabled())
        {
            float[] newSensorValues = camera.SensorChanged(event, rotation);
            sceneSetter.SensorChanged(newSensorValues[0], newSensorValues[1], camera.isMotionOffsetInverted());
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.d("textures", "surface created");
        startTime = System.currentTimeMillis();
        if (sceneSetter == null)
        {
            sceneSetter = new SceneSetter(this.context);
        }
        // Set the clear color to white
        GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

        sceneSetter.InitSprites();

        // Create the shaders, images
        InitSpriteProgram();
        sceneSetter.SetMaxBlurAmount(camera.maxBlur);

//        riGraphicTools.sp_Particle = GLES20.glCreateProgram();             // create empty OpenGL ES Program

//        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Particle);
//        int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Particle);
//
//        GLES20.glAttachShader(riGraphicTools.sp_Particle, vertexShader);   // add the vertex shader to program
//        GLES20.glAttachShader(riGraphicTools.sp_Particle, fragmentShader); // add the fragment shader to program
//        GLES20.glLinkProgram(riGraphicTools.sp_Particle);                  // creates OpenGL ES program executrees
//        GLES20.glBlendFunc(GLES20.GL_ONE_MINUS_CONSTANT_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

//        sceneSetter.SetToTargetFocalPoint();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.d("textures", "surface changed. width: " + width + " height: " + height);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glFlush();

        mScreenHeight = width;
        mScreenHeight = height;
        int newWidthHeight = Math.max(width, height);

        Log.d("textures", "surface changed. widthHeight: " + newWidthHeight + " widthHeight: " + widthHeight);

        if(!_created){
            _created = true;
            widthHeight = newWidthHeight;
//            sceneSetter.InitTextures(widthHeight);
            sceneSetter.InitScene(SceneManager.DEFAULT, timeOfDay, percentage, 0, widthHeight);
        }

        GLES20.glViewport(0, 0, width, height);

        camera.OnSurfaceChanged(width, height);
        sceneSetter.SurfaceChanged(camera.IsPortrait(), camera.GetXOffsetPosition(),
                camera.GetTouchOffsetScale(), camera.GetMotionOffsetScale());
    }


    private void InitSpriteProgram(){
        riGraphicTools.sp_Image = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Image);
        int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Image);

        GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(riGraphicTools.sp_Image);                  // creates OpenGL ES program executrees

        // Set our shader program
        GLES20.glUseProgram(riGraphicTools.sp_Image);
        GLES20.glDepthMask(false);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        // get handle to vertex shader's vPosition member
        int mColorHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_Color");
        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "vPosition");
        int mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_texCoord");
        // Get handle to shape's transformation matrix and apply it
        int mtrxHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");
        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "s_texture");
        int biasHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "bias");
        int alphaHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "alpha");

        sceneSetter.SetSpriteMembers(mColorHandle, mPositionHandle, mTexCoordLoc, mtrxHandle, mSamplerLoc, biasHandle, alphaHandle);
    }

    public void UpdateVisibility(boolean visible)
    {
        Log.d("rotate", "visibility changed. visible: " + visible);
        if (visible)
        {
            UpdateTime();
            if (timeTracker.SceneChangeRequired())
            {
//                sceneSetter.InitTextureSwap();
                timeTracker.SignalSceneChanged();
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

//            if(_zoomCameraEnabled){
//                sceneSetter.SetTarget
//            }

        } else
        {
            camera.ResetSensorOffset();
            sceneSetter.SensorChanged(0, 0, camera.isMotionOffsetInverted());
        }
    }

    public void SetScene(int scene){
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

    public void SetTouchOffset(boolean touchOffsetOn)
    {
        if (!touchOffsetOn)
        {
            Log.d("touch", "setTouchOffset x");
            float xOffset = 0.5f; //halfway between 0 and 1.0
            OnOffsetChanged(xOffset, 0.0f);
        }
        camera.EnableTouchOffset(touchOffsetOn);
    }


    public void SetCameraBlurAmount(int amount){
        Log.d("blur", "set blur amount to: " + amount);
        camera.maxBlur = amount;
        if(amount == 0){
            _cameraBlurEnabled = false;
            sceneSetter.TurnOffBlur();
        }else{
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



    public void SwapTextures()
    {
        //only swap if the scene setter is not already swapping
        if (sceneSetter.GetTextureSwapStatus() == SceneSetter.STATUS_DONE)
        {
            sceneSetter.InitTextureSwap();
        }
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

        if (sceneSetter.GetTextureSwapStatus() > 0)
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

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        sceneSetter.DrawSprites(camera.mtrxView, camera.mtrxProjection, camera.mModelMatrix);


        GLES20.glUseProgram(riGraphicTools.sp_Image);

//        }

    }

}
