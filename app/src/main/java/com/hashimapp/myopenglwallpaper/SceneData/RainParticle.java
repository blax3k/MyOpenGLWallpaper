package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.ParticleData;

public class RainParticle extends ParticleData
{
    public RainParticle()
    {
        defaultColor = new float[]{
                1f, 1f, 1f};

        earlyDawnColor = new float[]{
                0.4f, 0.57f, 0.77f};

        midDawnColor = new float[]{
                1.0f, 0.8f, 0.8f};

        dayStartColor = new float[]
                {1f, 1f, 1f};

        dayEndColor = new float[]
                {0.9f, 0.9f, 0.9f};

        earlyDuskColor = new float[]{
                1f, 1f, 1f};

        midDuskColor = new float[]{
                0.62f, 0.39f, 0.24f};

        nightStartColor = new float[]{
                0.1f, 0.17f, 0.37f};

        nightEndColor = new float[]{
                0f, 0.07f, 0.17f};
    }

}
