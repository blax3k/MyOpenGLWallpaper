package com.hashimapp.myopenglwallpaper.Model;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;
import android.view.animation.LinearInterpolator;

public class GLCamera {
    float[] mtrxProjection;
    float[] mtrxView;
    float oldOffset;
    private static float fovy;

    private static float X_OFFSET_STEP_PORTRAIT = 2.1F;
    private static float X_OFFSET_STEP_LANDSCAPE = 0.3F;

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

    private float prevAccelValues[];
    private float[] sensorData;

    private boolean portraitOrientation;

    private LinearInterpolator interpolator;

    private float offsetDifferenceX;

    public GLCamera() {
        mtrxProjection = new float[16];
        mtrxView = new float[16];
        prevAccelValues = new float[3];
        sensorData = new float[2];
        interpolator = new LinearInterpolator();
    }

    public void OnSurfaceChanged(int width, int height) {
        Log.d("tag", "Surface Changed");
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

    public float getOrientationOffsetMultiplier() {
        if (portraitOrientation) {
            return X_OFFSET_STEP_PORTRAIT;
        } else {
            return X_OFFSET_STEP_LANDSCAPE;
        }
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

    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    private final float EPSILON = 1.01f;
    float[] finalValues = new float[3];

    /*
    handle user tilting and rotating device
     */
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
            finalValues[1] += rawSensorData[0] * 0.005;
            finalValues[0] += rawSensorData[1] * 0.005;
            finalValues[2] += rawSensorData[2] * 0.005;

            if (finalValues[0] > 0.20f) {
                finalValues[0] = 0.20f;
            } else if (finalValues[0] < -0.20f) {
                finalValues[0] = -0.20f;
            }

            if (finalValues[1] > 0.10f) {
                finalValues[1] = 0.10f;
            } else if (finalValues[1] < -0.10f) {
                finalValues[1] = -0.10f;
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

    public boolean IsPortraitOrientation() {
        return portraitOrientation;
    }

    /*
    smooths the data in the input array and returns the smoothed data in the output
     */
    protected float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + 0.9f * (input[i] - output[i]);
        }
        return output;
    }


    public float GetEyeX() {
        return eyeX;
    }

    public float getEyeY() {
        return eyeY;
    }
}
