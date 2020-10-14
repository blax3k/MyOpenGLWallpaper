package com.hashimapp.myopenglwallpaper.Model;


import android.util.Log;

import com.hashimapp.myopenglwallpaper.Model.DataStorage.SpriteColorData;

import java.util.ArrayList;
import java.util.Arrays;

public class SpriteData
{

    public static final int NO_CHANGE = 0;
    public static final int CHANGE_NOW = 1;
    public static final int CHANGE_ON_FADE = 2;

    private static final int DAY_START_INDEX = 0;
    private static final int DAY_END_INDEX = 1;
    private static final int DUSK_START_INDEX = 2;
    private static final int DUSK_END_INDEX = 3;
    private static final int NIGHT_START_INDEX = 4;
    private static final int NIGHT_END_INDEX = 5;
    private static final int DAWN_START_INDEX = 6;
    private static final int DAWN_END_INDEX = 7;

    public int changeTextureVertices = NO_CHANGE;

    public SpriteColorData SpriteColorData;


    public String SpriteName;
    public String BitmapName;
    public float[] ShapeVertices;
    public float[] TextureVertices;
    protected short[] indices;
    private float zVertice;
    private float zVerticeInverse;

    public float[] defaultColor;
    public float[] dawnStartColor;
    public float[] dawnEndColor;
    public float[] dayStartColor;
    public float[] dayEndColor;
    public float[] duskStartColor;
    public float[] duskEndColor;
    public float[] nightStartColor;
    public float[] nightEndColor;


    protected boolean essentialLayer;


    public SpriteData()
    {
        indices = new short[]{0, 1, 2, 0, 2, 3};
        SpriteColorData = new SpriteColorData();
    }


    public float[] getColor(int timeOfDay, int phasePercentage)
    {
        if (dayStartColor == null || dayStartColor.length == 0)
        {
            if (SpriteColorData.GetColorSets().size() <= 0)
            {
                return new float[]{
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        0.9f, 0.9f, 0.9f, 1.0f };
            }
            return SpriteColorData.GetColor(WeatherManager.SUNNY_WEATHER, timeOfDay, phasePercentage);
        }
        switch (timeOfDay)
        {
            case TimeTracker.NIGHT:
                return MultiplyColors(phasePercentage, nightStartColor, nightEndColor);
            case TimeTracker.EARLY_DAWN:
                return MultiplyColors(phasePercentage, nightEndColor, dawnStartColor);
            case TimeTracker.MID_DAWN:
                return MultiplyColors(phasePercentage, dawnStartColor, dawnEndColor);
            case TimeTracker.LATE_DAWN:
                return MultiplyColors(phasePercentage, dawnEndColor, dayStartColor);
            case TimeTracker.DAY:
                return MultiplyColors(phasePercentage, dayStartColor, dayEndColor);
            case TimeTracker.EARLY_DUSK:
                return MultiplyColors(phasePercentage, dayEndColor, duskStartColor);
            case TimeTracker.MID_DUSK:
                return MultiplyColors(phasePercentage, duskStartColor, duskEndColor);
            case TimeTracker.LATE_DUSK:
                return MultiplyColors(phasePercentage, duskEndColor, nightStartColor);
        }
        return defaultColor;
    }


    private float[] MultiplyColors(int phasePercent, float[] current, float[] next)
    {
        float[] result = new float[current.length];
        for (int i = 0; i < current.length; i++)
        {
            if (i + 1 % 4 == 0)
            {
                //don't mess with the alpha channels
            } else
            {
                //get the current difference between the current phase and the next phase
                //and add it to the current phase
                float percent = ((float) phasePercent) / 100f;
                result[i] = current[i] + ((next[i] - current[i]) * percent);
            }
        }
        Log.d("Debug", Arrays.toString(result));
        return result;
    }

    public float[] getShapeVertices(boolean portrait, boolean motionOffset)
    {
        if (portrait)
        {
            return ShapeVertices;
        } else
        {
            return ShapeVertices;
        }
    }


    public void AddColors(ArrayList<float[]> colors)
    {
        dayStartColor = colors.get(DAY_START_INDEX);
        dayEndColor = colors.get(DAY_END_INDEX);
        duskStartColor = colors.get(DUSK_START_INDEX);
        duskEndColor = colors.get(DUSK_END_INDEX);
        nightStartColor = colors.get(NIGHT_START_INDEX);
        nightEndColor = colors.get(NIGHT_END_INDEX);
        dawnStartColor = colors.get(DAWN_START_INDEX);
        dawnEndColor = colors.get(DAWN_END_INDEX);
    }

    public float[] GetTextureVertices()
    {
        return TextureVertices;
    }

    public short[] GetIndices()
    {
        return indices;
    }

    public String GetBitmapName()
    {
        return BitmapName;
    }

    public boolean IsEssentialLayer()
    {
        return essentialLayer;
    }

    public float ZVertice()
    {
        return zVertice;
    }

    public float ZVerticeInverse()
    {
        return zVerticeInverse;
    }

    public void SetZVertice(float z){
        zVertice = z;
        zVerticeInverse = CalculateInverse(z);
    }

    private float CalculateInverse(float z)
    {
        return (float) Math.abs(1.0 - z);
    }



}
