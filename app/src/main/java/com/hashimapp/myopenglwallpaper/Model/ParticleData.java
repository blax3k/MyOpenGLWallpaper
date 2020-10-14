package com.hashimapp.myopenglwallpaper.Model;

import com.hashimapp.myopenglwallpaper.R;

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

    protected  float[] fVertices;

    protected int particleCount;
    protected int particleSize;

    private final int DEFAULT_PARTICLE_COUNT = 100;
    private final int PARTICLE_SIZE = 14;
    public ParticleData(){
        particleCount = DEFAULT_PARTICLE_COUNT;
        particleSize = PARTICLE_SIZE;
        fVertices = new float[DEFAULT_PARTICLE_COUNT * PARTICLE_SIZE];
    }

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

    public String GetBitmapID(){
        return "guy";
    }

    public float[] GetParticleData(){
        return fVertices;
    }

    public void SetColor(float[] color)
    {
        if(fVertices.length <= 0){
            return;
        }

        for (int i = 0; i < particleCount; i++)
        {
            fVertices[i * particleSize + 3] = color[0];
            fVertices[i * particleSize + 4] = color[1];
            fVertices[i * particleSize + 5] = color[2];
        }
    }

}
