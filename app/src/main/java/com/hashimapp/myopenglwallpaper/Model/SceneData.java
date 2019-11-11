package com.hashimapp.myopenglwallpaper.Model;

public class SceneData
{
    public int BitmapID;
    public float[] TexturePosition;
    public float[] Vertices;
    public float[] Colors;
    public float ZVertice;

    public SceneData(){

    }

    public SceneData(int bitmapID, float[] texturePosition, float[] vertices, float zVertice){
        BitmapID = bitmapID;
        TexturePosition = texturePosition;
        Vertices = vertices;
        ZVertice = zVertice;
    }
}
