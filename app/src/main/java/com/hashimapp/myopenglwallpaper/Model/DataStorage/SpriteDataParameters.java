package com.hashimapp.myopenglwallpaper.Model.DataStorage;

import java.util.ArrayList;

public class SpriteDataParameters
{
    public String SpriteName; //the unique name of the sprite
    public String SpriteFileName;
    public String VerticePosition;
    public String TexturePosition;
    public ArrayList<String[]> ColorData;
    public float ZVertice;
    public boolean EssentialLayer;


    public SpriteDataParameters(String spriteName, String spriteFileName, String verticePosition, float zVertice, String texturePosition, ArrayList<String[]> colorData)
    {
        SpriteName = spriteName;
        SpriteFileName = spriteFileName;
        VerticePosition = verticePosition;
        ZVertice = zVertice;
        TexturePosition = texturePosition;
        ColorData = colorData;
    }
}
