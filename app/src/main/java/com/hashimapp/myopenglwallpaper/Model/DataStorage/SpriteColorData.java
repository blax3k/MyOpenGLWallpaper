package com.hashimapp.myopenglwallpaper.Model.DataStorage;

import android.util.Log;

import com.hashimapp.myopenglwallpaper.Model.TimeTracker;
import com.hashimapp.myopenglwallpaper.Model.WeatherManager;

import java.util.ArrayList;
import java.util.Arrays;

/*
Holds and handles data for a single color day/weather set
 */
public class SpriteColorData
{
    public static final int DAWN_START_INDEX = 0;
    public static final int DAWN_END_INDEX = 1;
    public static final int DAY_START_INDEX = 2;
    public static final int DAY_END_INDEX = 3;
    public static final int DUSK_START_INDEX = 4;
    public static final int DUSK_END_INDEX = 5;
    public static final int NIGHT_START_INDEX = 6;
    public static final int NIGHT_END_INDEX = 7;

    public static final int DAY_PHASE_COUNT = 8;

    private ArrayList<float[][]> ColorSets;

    public SpriteColorData()
    {
        ColorSets = new ArrayList<>();
        for(int i = 0; i < WeatherManager.WEATHER_TYPE_COUNT; i++){
            ColorSets.add(new float[DAY_PHASE_COUNT][]);
        }
    }

    public void SetColors(ArrayList<float[][]> inColorSets)
    {
        ColorSets = inColorSets;
    }

    public void SetColor(int weatherIndex, float[][] colors)
    {
        ColorSets.set(weatherIndex, colors);
    }

    public ArrayList<float[][]> GetColorSets()
    {
        return ColorSets;
    }

    public float[] GetColor(int weather, int timeOfDay, int phasePercentage)
    {
        float[][] colorSet = ColorSets.get(weather);
        switch (timeOfDay)
        {
            case TimeTracker.EARLY_DAWN:
                return MultiplyColors(phasePercentage, colorSet[NIGHT_END_INDEX], colorSet[DAWN_START_INDEX]);
            case TimeTracker.MID_DAWN:
                return MultiplyColors(phasePercentage, colorSet[DAWN_START_INDEX], colorSet[DAWN_END_INDEX]);
            case TimeTracker.LATE_DAWN:
                return MultiplyColors(phasePercentage, colorSet[DAWN_END_INDEX], colorSet[DAY_START_INDEX]);
            case TimeTracker.DAY:
                return MultiplyColors(phasePercentage, colorSet[DAY_START_INDEX], colorSet[DAY_END_INDEX]);
            case TimeTracker.EARLY_DUSK:
                return MultiplyColors(phasePercentage, colorSet[DAY_END_INDEX], colorSet[DUSK_START_INDEX]);
            case TimeTracker.MID_DUSK:
                return MultiplyColors(phasePercentage, colorSet[DUSK_START_INDEX], colorSet[DUSK_END_INDEX]);
            case TimeTracker.LATE_DUSK:
                return MultiplyColors(phasePercentage, colorSet[DUSK_END_INDEX], colorSet[NIGHT_START_INDEX]);
            case TimeTracker.NIGHT:
                return MultiplyColors(phasePercentage, colorSet[NIGHT_START_INDEX], colorSet[NIGHT_END_INDEX]);
        }

        return colorSet[DAY_START_INDEX];
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
}
