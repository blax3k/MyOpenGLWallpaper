package com.hashimapp.myopenglwallpaper.Model;

public class TextureData
{
    public int GLTextureIndex;
    public int textureName;
    public int TextureIndex;
    public int BitmapID;

    public TextureData(int GLTextureIndex, int textureName, int textureNameIndex, int bitmapID){
         this.GLTextureIndex = GLTextureIndex;
         this.textureName = textureName;
         this.TextureIndex = textureNameIndex;
         this.BitmapID = bitmapID;
    }

}
