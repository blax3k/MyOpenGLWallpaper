package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.ParticleData;
import com.hashimapp.myopenglwallpaper.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

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

        SetupData();
    }

    public int GetBitmapID(int scene){
        return R.drawable.layer00;
    }

    private void SetupData(){
        fVertices = new float[particleCount * particleSize];
        float size;
        float vel;
        int angle;
        float xPos = -1.0f;
        float xStep = 4.0f / particleCount;
        for (int i = 0; i < particleCount; i++)
        {
            size = rnd(50.0f, 100.0f);
            vel = 5.0f + ((size) / 50.0f) * 1.0f;
            angle = (int) (270.0f);
            xPos += xStep;
            //x,y,z
            fVertices[i * particleSize + 0] = xPos;
            fVertices[i * particleSize + 1] = 2.0f;
            fVertices[i * particleSize + 2] = 0;
            //r,g,b
            fVertices[i * particleSize + 3] = 1.0f;
            fVertices[i * particleSize + 4] = 1.0f;
            fVertices[i * particleSize + 5] = 1.0f;
            //dx,dy,dz
            fVertices[i * particleSize + 6] = (float) (Math.cos(Math.toRadians(angle)) * vel);
            fVertices[i * particleSize + 7] = (float) (Math.sin(Math.toRadians(angle)) * vel);
            fVertices[i * particleSize + 8] = 0.0f;

            //life
            fVertices[i * particleSize + 9] = rnd(0.2f, 0.22f);
            //age
            fVertices[i * particleSize + 10] = rnd(0.01f, 0.1f);

            //texCoord-
            fVertices[i * particleSize + 11] = 0.5f;
            fVertices[i * particleSize + 12] = 0.0f;
            //size
            fVertices[i * particleSize + 13] = size;
        }
    }



    public static float rnd(float min, float max)
    {
        float fRandNum = (float) Math.random();
        return min + (max - min) * fRandNum;
    }

}
