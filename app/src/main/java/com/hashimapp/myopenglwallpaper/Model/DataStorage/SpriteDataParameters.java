package com.hashimapp.myopenglwallpaper.Model.DataStorage;

import java.util.ArrayList;

public class SpriteDataParameters
{
    public String SpriteFileName;
    public String VerticePosition;
    public String TexturePosition;
    public ArrayList<String[]> ColorData;
    public float ZVertice;
    public boolean EssentialLayer;


    public SpriteDataParameters(String spriteFileName, String verticePosition, float zVertice, String texturePosition, ArrayList<String[]> colorData)
    {
        SpriteFileName = spriteFileName;
        VerticePosition = verticePosition;
        ZVertice = zVertice;
        TexturePosition = texturePosition;
        ColorData = colorData;
    }
}
