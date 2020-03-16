package com.hashimapp.myopenglwallpaper.Model;

public class ParticleData
{

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


    public float[] getColor(int timeOfDay, int phasePercentage)
    {
        switch (timeOfDay)
        {
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

    private float[] MultiplyColors(int phasePercent, float[] current, float[] next)
    {
        float[] result = new float[current.length];
        for (int i = 0; i < current.length; i++)
        {
            //get the current difference between the current phase and the next phase
            //and add it to the current phase
            float percent = ((float) phasePercent) / 100f;
            result[i] = current[i] + ((next[i] - current[i]) * percent);
        }

        return result;
    }

    public int GetBitmapID(int scene){
        return -1;
    }

}
