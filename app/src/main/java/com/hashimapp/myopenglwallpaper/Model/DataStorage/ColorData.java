package com.hashimapp.myopenglwallpaper.Model.DataStorage;

import java.util.HashMap;
import java.util.Map;

public class ColorData
{
    private Map<String, float[]> AllColors;

    public ColorData()
    {
        AllColors = new HashMap<>();
    }

    public void AddColor(String colorName, float[] colors)
    {
        AllColors.put(colorName, colors);
    }

    public float[] GetColor(String colorName)
    {
        return AllColors.get(colorName);
    }
}
