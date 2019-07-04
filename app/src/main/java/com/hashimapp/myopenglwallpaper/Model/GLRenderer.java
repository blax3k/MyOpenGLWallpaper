package com.hashimapp.myopenglwallpaper.Model;

import java.util.Calendar;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;

import com.hashimapp.myopenglwallpaper.View.OpenGLES2WallpaperService;
import com.luckycatlabs.sunrisesunset.dto.Location;


public class GLRenderer implements Renderer {

    SceneSetter sceneSetter;
    // Our screen resolution
    float mScreenWidth = 1280;
    float mScreenHeight = 768;
    //    Sprite sky, template;
    Date dateCreated;
    Location location;
    Context context = OpenGLES2WallpaperService.getAppContext();
    Resources resources = context.getResources();
    TimeTracker timeTracker;
    int manualTimeOfDay = 0;
    private boolean manualTime;


    public GLCamera camera;

    public GLRenderer() {
        dateCreated = new Date();
        camera = new GLCamera();
        sceneSetter = new SceneSetter();
        location = new Location(47.760012, -122.307209);
        timeTracker = new TimeTracker();
        manualTime = false;
    }

    public boolean OnOffsetChanged(float xOffset, float yOffset) {

        if(camera.TouchOffsetEnabled()){
            float newXOffset = camera.getxOffsetStepPortrait(xOffset);
            sceneSetter.OffsetChanged(newXOffset, camera.IsPortraitOrientation());
            return true;
        }
        return false;


//        Calendar calendar = Calendar.getInstance();
//        int timeOfDay, percentage;
//            calendar.set(Calendar.HOUR_OF_DAY, 0);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
//            int minutes = (int)(xOffset * 100f * 1440)/100;
//            Log.d("Debug", "minutes: " + minutes);
//        Log.d("Debug", "xOffset: " + xOffset);
//            calendar.add(Calendar.MINUTE, minutes);
//        int[] timeInfo = timeTracker.GetTimePhase(calendar);
//        timeOfDay = timeInfo[TimeTracker.TIME_PHASE_INDEX];
//        percentage = timeInfo[TimeTracker.TIME_PHASE_PROGRESSION_INDEX];
//
//        Log.d("Debug", "timeOfDay: " + calendar.getTime().toString());
//        Log.d("Debug", "percentage: " + percentage);
//
//        sceneSetter.SetTimeOfDay(timeOfDay, percentage);
    }

    float[] prevSensorValues = new float[3];

    public void OnSensorChanged(SensorEvent event, int rotation) {
        if(camera.MotionOffsetEnabled()){
            float[] newSensorValues = camera.SensorChanged(event, rotation);
            prevSensorValues[0] = newSensorValues[0];
            prevSensorValues[1] = newSensorValues[1];
            sceneSetter.SensorChanged(newSensorValues[0], newSensorValues[1]);
        }
    }

    public void SwapTextures() {
        sceneSetter.swapTextures();
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        startTime = System.currentTimeMillis();
        if(sceneSetter == null){
            sceneSetter = new SceneSetter();
        }
        sceneSetter.InitSprites();
        // Set the clear color to white
        GLES20.glClearColor(0.9f, 0.9f, 0.9f, 0);


//		// Create the shaders for solid color
//		int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_SolidColor);
//		int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_SolidColor);
//
////		riGraphicTools.sp_SolidColor = GLES20.glCreateProgram();             // create empty OpenGL ES Program
////		GLES20.glAttachShader(riGraphicTools.sp_SolidColor, vertexShader);   // add the vertex shader to program
////		GLES20.glAttachShader(riGraphicTools.sp_SolidColor, fragmentShader); // add the fragment shader to program
////		GLES20.glLinkProgram(riGraphicTools.sp_SolidColor);                  // creates OpenGL ES program executrees


        // Create the shaders, images
        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Image);
        int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Image);

        riGraphicTools.sp_Image = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(riGraphicTools.sp_Image);                  // creates OpenGL ES program executrees

        // Set our shader program
        GLES20.glUseProgram(riGraphicTools.sp_Image);
        GLES20.glDepthMask(false);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

    }

    public void EnableMotionOffset(boolean motionOffsetOn) {
        camera.EnableMotionOffset(motionOffsetOn);
    }

    public void ResetMotionOffset() {
        camera.ResetSensorOffset();
        sceneSetter.SensorChanged(0, 0);
    }

    public void SetMotionOffsetStrength(int offsetStrength) {
        camera.setMotionOffsetStrength(offsetStrength);
    }

    public void SetTouchOffset(boolean touchOffsetOn){
        camera.EnableTouchOffset(touchOffsetOn);
    }

    public void ResetTouchOffset(){
        float xOffset = 0.5f; //halfway between 0 and 1.0
        OnOffsetChanged(xOffset, 0.0f);
    }

    public void SetTimeSetting(int minuteOfDay) {
        if (minuteOfDay == 0) {
            manualTime = false;
        } else {
            manualTime = true;
            manualTimeOfDay = minuteOfDay;
        }
    }

    public void UpdateTime() {
        Date date =  Calendar.getInstance().getTime();
        int timeOfDay, percentage;
//        if (!manualTime) {
//
//        } else {
//            calendar.set(Calendar.HOUR_OF_DAY, 0);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
//            calendar.add(Calendar.MINUTE, manualTimeOfDay);
//        }e();
        int[] timeInfo = timeTracker.GetTimePhase(date);
        timeOfDay = timeInfo[TimeTracker.TIME_PHASE_INDEX];
        percentage = timeInfo[TimeTracker.TIME_PHASE_PROGRESSION_INDEX];

        sceneSetter.SetTimeOfDay(timeOfDay, percentage);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glFlush();

        // We need to know the current width and height.
        mScreenWidth = width;
        mScreenHeight = height;
        GLES20.glViewport(0, 0, width, height);

//            String temp = preferences.getString("camera_blur", "none");
//            sceneSetter.setBlur(temp);

        camera.OnSurfaceChanged(width, height);
        sceneSetter.SurfaceChanged(camera.IsPortrait(), camera.MotionOffsetEnabled(), camera.GetXOffsetPosition());
        sceneSetter.ResetFocalPoint();
    }


    private int mFPS = 0;         // the value to show
    private int mFPSCounter = 0;  // the value to count
    private long mFPSTime = 0;     // last update time

    long lastFrameTime;
    long endTime, deltaTime, startTime;

    //checks if there are animated elements in the scene that need to be drawn
    public boolean ContinueDrawing(){
        return sceneSetter.FocalPointReached();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        if (SystemClock.uptimeMillis() - mFPSTime > 1000) {
            mFPSTime = SystemClock.uptimeMillis();
            mFPS = mFPSCounter;
            mFPSCounter = 0;
        } else {
            mFPSCounter++;
        }

        if(!sceneSetter.FocalPointReached()){
            sceneSetter.UpdateFocalPoint();
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//        // Enable alpha blending.
//        GLES20.glEnable(GLES20.GL_BLEND);
//        // Blend based on the fragment's alpha value.
//        GLES20.glBlendFunc(GLES20.GL_ONE /*GL_SRC_ALPHA*/, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        //increase opawater from zero

//		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");

//		Log.d("onDrawFrame", "eyeX: " + eyeX);
//		Matrix.setLookAtM(mtrxView, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

//		GLES20.glUseProgram(riGraphicTools.sp_SolidColor);


        // Calculate the projection and view

        synchronized (this) {
            sceneSetter.DrawSprites(camera.mtrxView, camera.mtrxProjection, camera.mModelMatrix);
        }

//        sky.draw(textureVerticeBuffer, camera.mtrxView, camera.mtrxProjection, mModelMatrix);
        //change the program being used

        //draw the clouds
//		Matrix.setIdentityM(mModelMatrix, 0);
//		Matrix.translateM(mModelMatrix, 0, xScrollOffset * 0.1f, yOffset * 0.1f, 1.0f);
//		Matrix.multiplyMM(scratch1, 0, mtrxView, 0, mModelMatrix, 0);
//		Matrix.multiplyMM(scratch1, 0, mtrxProjection, 0, scratch1, 0);
//		clouds.draw(cloudMVPMatrix, cloudUVBuffer, sceneSetter.getTextureIndex(SceneModel.CLOUDS), mModelMatrix,
//				mtrxView, mtrxProjection, xScrollOffset, yOffset);

        GLES20.glUseProgram(riGraphicTools.sp_Image);

//        }

    }

}
