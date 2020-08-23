package com.hashimapp.myopenglwallpaper.Model;


import android.util.Log;

import java.util.Arrays;

public class SpriteData {

    public static final int NO_CHANGE = 0;
    public static final int CHANGE_NOW = 1;
    public static final int CHANGE_ON_FADE = 2;

    public int changeTextureVertices = NO_CHANGE;

    protected float[] portraitVertices;
    protected float[] textureVertices;

    protected short[] indices;

    protected float[] defaultColor;
    protected float[] earlyDawnColor;
    protected float[] midDawnColor;
    protected float[] dayStartColor;
    protected float[] dayEndColor;
    protected float[] earlyDuskColor;
    protected float[] midDuskColor;
    protected float[] nightStartColor;
    protected float[] nightEndColor;
    protected float zVertice;
    protected boolean essentialLayer;

    protected int bitmapID;


    public float[] getColor(int timeOfDay, int phasePercentage) {
        switch (timeOfDay) {
            case TimeTracker.DAY:
                return MultiplyColors(phasePercentage, dayStartColor, dayEndColor);
            case TimeTracker.NIGHT:
                return MultiplyColors(phasePercentage, nightStartColor, nightEndColor);
            case TimeTracker.EARLY_DAWN:
                return MultiplyColors(phasePercentage, nightEndColor, earlyDawnColor);
            case TimeTracker.MID_DAWN:
                return MultiplyColors(phasePercentage, earlyDawnColor, midDawnColor);
            case TimeTracker.LATE_DAWN:
                return MultiplyColors(phasePercentage, midDawnColor, dayStartColor);
            case TimeTracker.EARLY_DUSK:
                return MultiplyColors(phasePercentage, dayEndColor, earlyDuskColor);
            case TimeTracker.MID_DUSK:
                return MultiplyColors(phasePercentage, earlyDuskColor, midDuskColor);
            case TimeTracker.LATE_DUSK:
                return MultiplyColors(phasePercentage, midDuskColor, nightStartColor);
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

    public float[] getShapeVertices(boolean portrait, boolean motionOffset) {
        if (portrait)
        {
                return portraitVertices;
        } else
        {
            return portraitVertices;
        }
    }


    public float[] GetTextureVertices(int scene) {
        return textureVertices;
    }

    public short[] GetIndices() {
        return indices;
    }

    public int GetBitmapID(int scene){
        return bitmapID;
    }

    public boolean IsEssentialLayer(){
        return essentialLayer;
    }

    public SpriteSceneData GetScene(int scene, int timePhase, int percentage, int weather){
        return new SpriteSceneData(GetBitmapID(scene), GetTextureVertices(scene),
            portraitVertices, zVertice, getColor(timePhase, percentage));
    }




}
