package com.hashimapp.myopenglwallpaper.Model;

public class TextureData
{
    public int GLTextureIndex;
    public int textureName;
    public int TextureIndex;
    public String BitmapName;

    public TextureData(int GLTextureIndex, int textureName, int textureNameIndex, String bitmapName){
         this.GLTextureIndex = GLTextureIndex;
         this.textureName = textureName;
         this.TextureIndex = textureNameIndex;
         this.BitmapName = bitmapName;
    }

}
