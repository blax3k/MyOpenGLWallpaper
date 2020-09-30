package com.hashimapp.myopenglwallpaper.Model.DataStorage;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpriteDataStorage
{
    public String Name;
    public int BitmapID;
    public Map<String, float[]> ShapeVerticeStorage;
    public Map<String, float[]> TextureVerticeStorage;
    public Map<String, float[]> ColorValueStorage;

    public SpriteDataStorage()
    {
        BitmapID = 0;
        Name = "";
        ShapeVerticeStorage = new HashMap<>();
        TextureVerticeStorage = new HashMap<>();
        ColorValueStorage = new HashMap<>();
    }


    public SpriteData GetSpriteData(SpriteDataParameters parms)
    {
        SpriteData sd = new SpriteData();

        sd.SpriteName = Name;
        sd.textureVertices = TextureVerticeStorage.get(parms.TexturePosition);
        sd.shapeVertices = ShapeVerticeStorage.get(parms.VerticePosition);
        sd.bitmapID = BitmapID;
        ArrayList<float[][]> colors = new ArrayList<>();

        for(String[] colorKeyArray : parms.ColorData)
        {
            float[][] colorSet = new float[SpriteColorData.DAY_PHASE_COUNT][];
            for(int i = 0 ; i < colorKeyArray.length; i++)
            {
                float[] color = ColorValueStorage.get(colorKeyArray[i]);
                colorSet[i] = color;
            }
            colors.add(colorSet);
        }

        sd.spriteColorData.SetColors(colors);

        return sd;
    }

}
