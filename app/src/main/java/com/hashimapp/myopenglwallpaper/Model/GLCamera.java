package com.hashimapp.myopenglwallpaper.Model;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;
import android.view.animation.LinearInterpolator;

public class GLCamera {
    float[] mModelMatrix;
    float[] mtrxProjection;
    float[] mtrxView;

    private static float TEXTURE_WIDTH = 4.8f;

    private static float X_OFFSET_STEP_PORTRAIT = 2.1F;
    private static float X_OFFSET_STEP_LANDSCAPE = 0.3F;
    private static float MOTION_OFFSET_0 = 0.1f;
    private static float MOTION_OFFSET_1 = 0.25f;
    private static float MOTION_OFFSET_2 = 0.4f;
    private static float MOTION_OFFSET_3 = 0.55f;
    private static float MOTION_OFFSET_4 = 0.7f;
    private static float MOTION_OFFSET_5 = 0.85f;
    private static float MOTION_OFFSET_6 = 1.0f;


    private static float FOV_PORTRAIT = 100.0f;
    private static float FOV_LANDSCAPE = 68.0f;
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
    private float offsetDifferenceX;

    private int screenWidth = 1;
    private int screenHeight = 1;

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
            eyeX = 0.0f;
//            eyeZ = 3.0f;
            fov = FOV_PORTRAIT;
            portraitOrientation = true;
            offsetDifferenceX = X_OFFSET_STEP_PORTRAIT;
        } else //landscape
        {
            eyeX = 0.0f;
//            eyeZ = 2.0f;
            fov = FOV_LANDSCAPE;
            portraitOrientation = false;
            offsetDifferenceX = X_OFFSET_STEP_LANDSCAPE;
        }
        lookX = eyeX;
        Matrix.perspectiveM(mtrxProjection, 0, fov, ratio, 1, 12);
        Matrix.setLookAtM(mtrxView, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }


    public boolean IsPortrait() {
        return portraitOrientation;
    }


    public float getxOffsetStepPortrait(float xOffset) {
        if (xOffset > 1.0f) {
            return 1.0f;

        } else if (xOffset < 0.0f) {
            return 0.0f;

        } else {
            return xOffset * offsetDifferenceX;
//            return interpolator.getInterpolation(xScrollOffset * offsetDifferenceX);
        }
    }

    /*
    Sprites will start centered. In order to support different screen ratios, the textures
    will match the Y axis in portrait mode, and then will be shifted along the X axis, so that
    the left edge of the foremost texture lines up with the left edge of the screen.
     */
    public float GetXOffsetPosition(){
        float relativeScreenWidth = (TEXTURE_WIDTH / screenHeight) * screenWidth;
        float screenWidthDiff = TEXTURE_WIDTH - relativeScreenWidth;
        return screenWidthDiff/2;
    }

    private float timestamp;
    float[] finalValues = new float[3];

    /*
    handle user tilting and rotating device
     */
    float[] prevSensorData = new float[3];
    public float[] SensorChanged(SensorEvent event, int rotation) {
//        Log.d("rotation", "X:" + event[0] + " Y:" + event[1] + " Z:" + event[2]);

        float[] rawSensorData = new float[3];
        switch (rotation)
        {
            case Surface.ROTATION_0:
                rawSensorData[0] = event.values[0];
                rawSensorData[1] = event.values[1];
                rawSensorData[2] = event.values[2];
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
        float[] lowPassSensorData = lowPass(rawSensorData, prevSensorData);
        prevSensorData = rawSensorData;


            finalValues[1] += lowPassSensorData[0] * 0.005 * motionOffsetStrength;
            finalValues[0] += lowPassSensorData[1] * 0.005 * motionOffsetStrength;
            finalValues[2] += lowPassSensorData[2] * 0.005 * motionOffsetStrength;

            if (finalValues[0] > motionOffsetLimit) {
                finalValues[0] = motionOffsetLimit;
            } else if (finalValues[0] < -motionOffsetLimit) {
                finalValues[0] = -motionOffsetLimit;
            }

            if (finalValues[1] > motionOffsetLimit) {
                finalValues[1] = motionOffsetLimit;
            } else if (finalValues[1] < -motionOffsetLimit) {
                finalValues[1] = -motionOffsetLimit;
            }
        timestamp = event.timestamp;

        return finalValues;
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;
    }

    public void ResetSensorOffset(){
        finalValues[0] = 0;
        finalValues[1] = 0;
        finalValues[2] = 0;

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

        motionOffsetLimit = 0.16f * motionOffsetStrength;
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
