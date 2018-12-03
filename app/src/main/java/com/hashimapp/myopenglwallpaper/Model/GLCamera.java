package com.hashimapp.myopenglwallpaper.Model;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.Surface;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class GLCamera {
    public float[] mtrxProjection;
    public float[] mtrxView;
    float oldOffset;
    private static float fovy;

    private static float X_OFFSET_STEP_PORTRAIT = 1.0F;
    private static float X_OFFSET_STEP_LANDSCAPE = 0.3F;

    private static float FOV_PORTRAIT = 100.0f;
    private static float FOV_LANDSCAPE = 80.0f;
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

    float accelValues[];

    private boolean portraitOrientation;

    LinearInterpolator interpolator;

    private float offsetDifferenceX;

    public GLCamera() {
        mtrxProjection = new float[16];
        mtrxView = new float[16];
        accelValues = new float[3];
        interpolator = new LinearInterpolator();
    }

    public void OnSurfaceChanged(int width, int height)
    {
        float ratio = (float)width / (float)height;
        float fov;
        lookX = eyeX;
        if (height > width) //portrait
        {
            fov = FOV_PORTRAIT;
            portraitOrientation = true;
            offsetDifferenceX = X_OFFSET_STEP_PORTRAIT;
        } else //landscape
        {
            fov = FOV_LANDSCAPE;
            portraitOrientation = false;
            offsetDifferenceX = X_OFFSET_STEP_LANDSCAPE;
        }
        Matrix.perspectiveM(mtrxProjection, 0, fov, ratio, 1, 12);
        Matrix.setLookAtM(mtrxView, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }



    /*
    Handle home screen scrolling
     */
    public void OffsetChanged(float xOffset) {
        if (xOffset > 1.0f) {
            eyeX = 1.0f;

        } else if (xOffset < 0.0f) {
            eyeX = 0.0f;

        }else{
            eyeX =  interpolator.getInterpolation(xOffset * offsetDifferenceX);
        }
        lookX = eyeX;
        Matrix.setLookAtM(mtrxView, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    /*
    handle user tilting and rotating device
     */
    public void SensorChanged(float[] rawValues, int rotation) {

        float[] rawSensorData = new float[3];
        switch (rotation)
        {
            case Surface.ROTATION_0:
                rawSensorData[0] = rawValues[0];
                rawSensorData[1] = rawValues[1];
                break;
            case Surface.ROTATION_90:
                rawSensorData[0] = -rawValues[1];
                rawSensorData[1] = rawValues[0];
                break;
            case Surface.ROTATION_180:
                rawSensorData[0] = -rawValues[0];
                rawSensorData[1] = -rawValues[1];
                break;
            case Surface.ROTATION_270:
                rawSensorData[0] = rawValues[1];
                rawSensorData[1] = -rawValues[0];
                break;
        }

        accelValues = lowPass(rawSensorData, accelValues);
//
        float newX = accelValues[0] * 0.05f;
        float newY = accelValues[1] * 0.05f;
        float newZ = accelValues[2] * 0.05f;

        Matrix.setLookAtM(mtrxView, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    /*
    smooths the data in the input array and returns the smoothed data in the output
     */
    protected float[] lowPass(float[] input, float[] output)
    {
        if (output == null) return input;

        for( int i = 0; i < input.length; i++)
        {
            output[i] = output[i] + 0.05f * (input[i] - output[i]);
        }
        return  output;
    }

    public float GetEyeX() {
        return eyeX;
    }

    public float getEyeY() {
        return eyeY;
    }
}
