package com.hashimapp.myopenglwallpaper.Model;


import android.util.Log;

import java.util.Arrays;

public class SpriteData implements ISpriteData {

    protected float[] portraitVertices;
    protected float[] portraitVerticesMotion;
    protected float[] landscapeVertices;
    protected float[] landscapeVerticesMotion;
    protected float[] textureVertices;
    protected int textureIndex;

    protected short[] indices;
    protected float[] defaultColor;
    protected float[] dawnColor;
    protected float[] dayColor;
    protected float[] sunsetColor;
    protected float[] nightColor;
    protected float zVertice;
    protected boolean portrait = true;
    protected int TexIndex;

    @Override
    public int getTextureIndex() {
        return textureIndex;
    }

    @Override
    public int getTexIndex() {
        return TexIndex;
    }

    @Override
    public float[] getColor(int timeOfDay, int phasePercentage) {
        switch (timeOfDay) {
            case TimeTracker.DAY:
                return dayColor;
            case TimeTracker.NIGHT:
                return nightColor;
            case TimeTracker.DAWN:
                return MultiplyColors(phasePercentage, nightColor, dawnColor);
            case TimeTracker.SUNRISE:
                return MultiplyColors(phasePercentage, dawnColor, dayColor);
            case TimeTracker.SUNSET:
                return MultiplyColors(phasePercentage, dayColor, sunsetColor);
            case TimeTracker.DUSK:
                return MultiplyColors(phasePercentage, sunsetColor, nightColor);
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
                return portraitVerticesMotion;
            }
            return portraitVertices;
        } else
        {
            if(motionOffset)
            {
                return landscapeVerticesMotion;
            }
            return landscapeVertices;
        }
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

}
