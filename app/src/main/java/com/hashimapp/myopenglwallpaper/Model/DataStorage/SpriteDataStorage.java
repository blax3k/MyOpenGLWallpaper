package com.hashimapp.myopenglwallpaper.Model.DataStorage;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpriteDataStorage
{
    public String Name;
    public int BitmapID;
    public String BitmapName;
    public Map<String, float[]> ShapeVerticeStorage;
    public Map<String, float[]> TextureVerticeStorage;
    public Map<String, float[]> ColorValueStorage;

    public SpriteDataStorage()
    {
        BitmapID = 0;
        BitmapName = "";
        Name = "";
        ShapeVerticeStorage = new HashMap<>();
        TextureVerticeStorage = new HashMap<>();
        ColorValueStorage = new HashMap<>();
    }


    public SpriteData GetSpriteData(SpriteDataParameters parms)
    {
        SpriteData sd = new SpriteData();

        sd.SpriteName = Name;
        sd.TextureVertices = TextureVerticeStorage.get(parms.TexturePosition);
        sd.ShapeVertices = ShapeVerticeStorage.get(parms.VerticePosition);
        sd.BitmapID = BitmapID;
        sd.BitmapName = BitmapName;
        sd.SetZVertice(parms.ZVertice);
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

        sd.SpriteColorData.SetColors(colors);

        return sd;
    }

}
