package com.hashimapp.myopenglwallpaper.Model;


import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class SpriteData implements ISpriteData {

    protected float[] portraitVertices;
    protected float[] landscapeVertices;
    protected float[] textureVertices;

    protected short[] indices;
    protected float[] defaultColor;
    protected float[] earlyDawnColor;
    protected float[] midDawnColor;
    protected float[] dayColor;
    protected float[] earlyDuskColor;
    protected float[] midDuskColor;
    protected float[] nightColor;
    protected float zVertice;
    protected boolean portrait = true;

    private static final float MAX_QUAD_SCALE = 0.125f;


    @Override
    public float[] getColor(int timeOfDay, int phasePercentage) {
        switch (timeOfDay) {
            case TimeTracker.DAY:
                return dayColor;
            case TimeTracker.NIGHT:
                return nightColor;
            case TimeTracker.EARLY_DAWN:
                return MultiplyColors(phasePercentage, nightColor, earlyDawnColor);
            case TimeTracker.MID_DAWN:
                return MultiplyColors(phasePercentage, earlyDawnColor, midDawnColor);
            case TimeTracker.LATE_DAWN:
                return MultiplyColors(phasePercentage, midDawnColor, dayColor);
            case TimeTracker.EARLY_DUSK:
                return MultiplyColors(phasePercentage, dayColor, earlyDuskColor);
            case TimeTracker.MID_DUSK:
                return MultiplyColors(phasePercentage, earlyDuskColor, midDuskColor);
            case TimeTracker.LATE_DUSK:
                return MultiplyColors(phasePercentage, midDuskColor, nightColor);
        }
        return defaultColor;
    }



    private float[] MultiplyColors(int phasePercent, float[] current, float[] next){
        float[] result = new float[current.length];
        for(int i = 0; i < current.length; i++){
            if(i +1 % 4 == 0){
                //don't mess with the alpha channels
            }else{
                //get the current difference between the current phase and the next phase
                //and add it to the current phase
                float percent = ((float)phasePercent)/100f;
                result[i] = current[i] + ((next[i] - current[i]) * percent);
            }
        }
        Log.d("Debug", Arrays.toString(result));
        return result;
    }

    @Override
    public float[] getShapeVertices(boolean portrait, boolean motionOffset) {
        if (portrait)
        {
            if(motionOffset)
            {
                //enlarge the quad
                return ScaleQuad(portraitVertices);
            }
            else{
                return portraitVertices;
            }
        } else
        {
            if(motionOffset)
            {
                return ScaleQuad(landscapeVertices);
            }
            return portraitVertices;
        }
    }

    private float[] ScaleQuad(float[] verticeArray){
        float[] motionArray = new float[12];
        motionArray[0] = verticeArray[0] - (Math.abs(zVertice) * Math.abs( verticeArray[0] * MAX_QUAD_SCALE));
        motionArray[3] = verticeArray[3] - (Math.abs(zVertice) *Math.abs( verticeArray[3] * MAX_QUAD_SCALE));
        motionArray[4] = verticeArray[4] - (Math.abs(zVertice) *Math.abs( verticeArray[4] * MAX_QUAD_SCALE));
        motionArray[7] = verticeArray[7] - (Math.abs(zVertice) *Math.abs( verticeArray[7] * MAX_QUAD_SCALE));

        motionArray[1] = verticeArray[1] + (Math.abs(zVertice) *Math.abs( verticeArray[1] * MAX_QUAD_SCALE));
        motionArray[6] = verticeArray[6] + (Math.abs(zVertice) *Math.abs( verticeArray[6] * MAX_QUAD_SCALE));
        motionArray[9] = verticeArray[9] + (Math.abs(zVertice) *Math.abs( verticeArray[9] * MAX_QUAD_SCALE));
        motionArray[10] = verticeArray[10] +(Math.abs(zVertice) * Math.abs( verticeArray[10] * MAX_QUAD_SCALE));

        return motionArray;
    }

    @Override
    public float[] getTextureVertices() {
        return textureVertices;
    }

    @Override
    public short[] getIndices() {
        return indices;
    }

    @Override
    public float getZVertices() {
        return zVertice;
    }

    @Override
    public void setOrientation(boolean portrait) {
        this.portrait = portrait;
    }

    @Override
    public int GetBitmapID(){
        return 0;
    }

}
