package com.hashimapp.myopenglwallpaper.Model.DataStorage;


import android.util.Log;

import com.hashimapp.myopenglwallpaper.Model.DataStorage.SpriteColorData;
import com.hashimapp.myopenglwallpaper.Model.WeatherManager;

import java.util.ArrayList;
import java.util.Arrays;

public class SpriteData
{
    public SpriteColorData SpriteColorData;

    public String SpriteName;
    public String BitmapName;
    public float[] ShapeVertices;
    public float[] TextureVertices;
    protected short[] indices;
    private float zVertice;
    private float zVerticeInverse;


    protected boolean essentialLayer;


    public SpriteData()
    {
        indices = new short[]{0, 1, 2, 0, 2, 3};
        SpriteColorData = new SpriteColorData();
    }


    public float[] getColor(int timeOfDay, int phasePercentage)
    {
            if (SpriteColorData.GetColorSets().size() <= 0)
            {
                return new float[]{
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f };
            }
            return SpriteColorData.GetColor(WeatherManager.SUNNY_WEATHER, timeOfDay, phasePercentage);
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
