package com.hashimapp.myopenglwallpaper.Model;

public class SceneData
{
    public int BitmapID;
    public float[] TextureVertices;
    public float[] Vertices;
    public float[] Colors;
    public float ZVertice;

    public SceneData(){

    }

    public SceneData(int bitmapID, float[] textureVertices, float[] vertices, float zVertice, float[] colors)
    {
        BitmapID = bitmapID;
        TextureVertices = textureVertices;
        Vertices = vertices;
        ZVertice = zVertice;
        Colors = colors;
    }
}
