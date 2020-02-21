package com.hashimapp.myopenglwallpaper.Model;

import android.hardware.SensorEvent;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

public class GLCamera {
    float[] mModelMatrix;
    float[] mtrxProjection;
    float[] mtrxView;

    private static float TEXTURE_WIDTH = 4.8f;

    private static float X_OFFSET_STEP_PORTRAIT = 2.1F;
    private static float X_OFFSET_STEP_LANDSCAPE = 0.3F;
    private static float MOTION_OFFSET_0 = 0.0f;
    private static float MOTION_OFFSET_1 = 0.1f;
    private static float MOTION_OFFSET_2 = 0.3f;
    private static float MOTION_OFFSET_3 = 0.5f;
    private static float MOTION_OFFSET_4 = 0.7f;
    private static float MOTION_OFFSET_5 = 0.9f;
    private static float MOTION_OFFSET_6 = 1.0f;


    private static float FOV_PORTRAIT = 100.0f;
    private static float FOV_LANDSCAPE = 100.0f;
    // Position the eye in front of the origin.
    private float eyeX = 0.0f;
    private float eyeY = 0.0f;
    private float eyeZ = 3.0f;
    // We are looking toward the distance
    private float lookX = 0.0f;
    private float lookY = 0.0f;
    private float lookZ = 0.0f;
    // Set our up vector. This is where our head would be pointing were we holding the camera.
    private float upX = 0.0f;
    private float upY = 1.0f;
    private float upZ = 0.0f;

    private float motionOffsetLimit;
    private float motionOffsetStrength;
    private boolean portraitOrientation;
    private boolean motionOffsetInverted;
    private boolean touchOffsetEnabled;

    public int maxBlur;
    private float targetFocalPoint = 0.0f;

    private int screenWidth = 1;
    private int screenHeight = 1;
    float relativeScreenWidth = 0;
    float screenWidthDiff = 0;
    private float xPositionOffset;

    public boolean isMotionOffsetInverted(){
        return motionOffsetInverted;
    }

    public GLCamera() {
        mModelMatrix = new float[16];
        mtrxProjection = new float[16];
        mtrxView = new float[16];
    }

    public void OnSurfaceChanged(int width, int height) {
        Log.d("tag", "Surface Changed");
        screenHeight = height;
        screenWidth = width;
        float ratio = (float) width / (float) height;
        float fov;
        if (height > width) //portrait
        {
            fov = FOV_PORTRAIT;
            portraitOrientation = true;
        } else //landscape
        {
            float fovWidth = ((TEXTURE_WIDTH * screenWidth) / screenHeight)/2;
            float depth = fovWidth/(float)Math.tan(Math.toRadians(FOV_PORTRAIT/2));
            fov = (float) Math.toDegrees(Math.atan((TEXTURE_WIDTH/2)/depth)) * 2;
            portraitOrientation = false;
        }

        relativeScreenWidth = (TEXTURE_WIDTH / screenHeight) * screenWidth;
        screenWidthDiff = TEXTURE_WIDTH - relativeScreenWidth;
        lookX = eyeX;
        Matrix.perspectiveM(mtrxProjection, 0, fov, ratio, 1f, 12);
        Matrix.setLookAtM(mtrxView, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }


    public boolean IsPortrait() {
        return portraitOrientation;
    }


    public float GetXOffset(float xOffsetM) {
        float xOffset = xOffsetM;
        if (xOffsetM > 1.0f) {
            xOffset = 1.0f;

        } else if (xOffsetM < 0.0f) {
            xOffset = 0.0f;
        }
        float result;

        if(portraitOrientation) {
            result = xOffset * screenWidthDiff;
        }else{
            result = xOffset * X_OFFSET_STEP_LANDSCAPE;
        }
        return result;
    }

    /*
    Sprites will start centered. In order to support different screen ratios, the textures
    will match the Y axis in portrait mode, and then will be shifted along the X axis, so that
    the left edge of the foremost texture lines up with the left edge of the screen.
    if it's in landscape then the pixels are going to line up edge to edge anyway, so no x positioning
    is needed
     */
    public float GetXOffsetPosition(){
        if(portraitOrientation){
            xPositionOffset =  screenWidthDiff/2;
        }else{
            xPositionOffset =  X_OFFSET_STEP_LANDSCAPE/2;

        }
        return  xPositionOffset;
    }


    public float GetTouchOffsetScale(){
        if(portraitOrientation){
            return 0;
        }else{
           return (X_OFFSET_STEP_LANDSCAPE) / TEXTURE_WIDTH;
        }
    }

    public float GetMotionOffsetScale(){
        return  motionOffsetStrength / TEXTURE_WIDTH;
    }


    public void SetMotionOffsetInverted(boolean inverted){
        motionOffsetInverted = inverted;
    }

    float[] finalValues = new float[3];
    float[] prevSensorData = new float[3];

    /*
    handle user tilting and rotating device
     */
    public float[] SensorChanged(SensorEvent event, int rotation) {
        float[] rawSensorData = new float[3];
        switch (rotation)
        {
            case Surface.ROTATION_0:
                rawSensorData[0] = event.values[0];
                rawSensorData[1] = -event.values[1];
                rawSensorData[2] = event.values[2];
                break;
            case Surface.ROTATION_90:
                rawSensorData[0] = -event.values[1];
                rawSensorData[1] = -event.values[0];
                break;
            case Surface.ROTATION_180:
                rawSensorData[0] = event.values[0];
                rawSensorData[1] = -event.values[1];
                break;
            case Surface.ROTATION_270:
                rawSensorData[0] = event.values[1];
                rawSensorData[1] = -event.values[0];
                break;
        }
        float[] lowPassSensorData = lowPass(rawSensorData, prevSensorData);
        prevSensorData = rawSensorData;

        finalValues[1] += lowPassSensorData[0] * 0.005 * motionOffsetStrength;
        finalValues[0] += lowPassSensorData[1] * 0.005 * motionOffsetStrength;
        finalValues[2] += lowPassSensorData[2] * 0.005 * motionOffsetStrength;

        if (finalValues[0] > motionOffsetLimit)
        {
            finalValues[0] = motionOffsetLimit;
        } else if (finalValues[0] < -motionOffsetLimit)
        {
            finalValues[0] = -motionOffsetLimit;
        }

        if (finalValues[1] > motionOffsetLimit)
        {
            finalValues[1] = motionOffsetLimit;
        } else if (finalValues[1] < -motionOffsetLimit)
        {
            finalValues[1] = -motionOffsetLimit;
        }

        return finalValues;
    }

    public boolean MotionOffsetEnabled(){
        return motionOffsetStrength != MOTION_OFFSET_0;
    }

    public void EnableTouchOffset(boolean enabled){
        this.touchOffsetEnabled = enabled;
    }

    public boolean TouchOffsetEnabled(){
        return touchOffsetEnabled;
    }


    public void ResetSensorOffset(){
        finalValues[0] = 0;
        finalValues[1] = 0;
        finalValues[2] = 0;

    }

    public void SetTargetFocalPoint(float targetFocalPoint){

    }

    public void setMotionOffsetStrength(int offsetSlider){
        Log.d("motionOffset", "motion offset strength: " + offsetSlider);
        switch (offsetSlider){
            case 0:
                this.motionOffsetStrength = MOTION_OFFSET_0;
                break;
            case 1:
                this.motionOffsetStrength = MOTION_OFFSET_1;
                break;
            case 2:
                this.motionOffsetStrength = MOTION_OFFSET_2;
                break;
            case 3:
                this.motionOffsetStrength = MOTION_OFFSET_3;
                break;
            case 4:
                this.motionOffsetStrength = MOTION_OFFSET_4;
                break;
            case 5:
                this.motionOffsetStrength = MOTION_OFFSET_5;
                break;
            case 6:
                this.motionOffsetStrength = MOTION_OFFSET_6;
                break;
        }

        motionOffsetLimit = 0.22f * motionOffsetStrength;
    }


    public boolean IsPortraitOrientation() {
        return portraitOrientation;
    }

    /*
    smooths the data in the input array and returns the smoothed data in the output
     */
    protected float[] lowPass(float[] input, float[] prev) {
        if (prev == null) return input;

        for (int i = 0; i < input.length; i++) {
            prev[i] = prev[i] + 0.00001f * (input[i] - prev[i]);
        }
        return prev;
    }


    public float GetEyeX() {
        return eyeX;
    }

    public float getEyeY() {
        return eyeY;
    }
}
