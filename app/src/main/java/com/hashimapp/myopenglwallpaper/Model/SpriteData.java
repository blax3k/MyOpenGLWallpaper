package com.hashimapp.myopenglwallpaper.Model;


import android.util.Log;

import java.util.Arrays;
import java.util.List;

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
//    protected float[] dayColor;
    protected float[] dayStartColor;
    protected float[] dayEndColor;
    protected float[] earlyDuskColor;
    protected float[] midDuskColor;
//    protected float[] nightColor;
    protected float[] nightStartColor;
    protected float[] nightEndColor;
    protected float zVertice;
    protected boolean essentialLayer;

    protected float internalZVertice(float vertice){
        float newVertice = vertice * -1.0f;
        Log.d("vertice"," newVertice: " + newVertice);
        return newVertice;

    }

    private static final float MAX_QUAD_SCALE = 0.125f;


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
//            if(motionOffset)
//            {
//                //enlarge the quad
//                return ScaleQuad(portraitVertices);
//            }
                return portraitVertices;
        } else
        {
//            if(motionOffset)
//            {
//                return ScaleQuad(landscapeVertices);
//            }
            return portraitVertices;
        }
    }


    public float[] GetTextureVertices(int scene) {
        return textureVertices;
    }

    public short[] GetIndices() {
        return indices;
    }

//    /*
//    returns value between 0 and 1.0f with closest values at 0
//     */
//    public float GetZVertice() {
//        return zVertice;
//    }
//
//    /*
//    return values from 0 to 1 with closest values at 1.0
//     */
//    public float GetZVerticeInverse(){
//        return (float) Math.abs(1.0 - zVertice);
//    }

    public int GetBitmapID(int scene){
        return -1;
    }

    public boolean IsEssentialLayer(){
        return essentialLayer;
    }

    public SceneData GetScene(int scene, int timePhase, int percentage, int weather){
        return new SceneData(GetBitmapID(scene), GetTextureVertices(scene),
            portraitVertices, zVertice, getColor(timePhase, percentage));
    }


}
