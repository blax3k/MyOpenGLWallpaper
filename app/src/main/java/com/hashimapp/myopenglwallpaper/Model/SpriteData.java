package com.hashimapp.myopenglwallpaper.Model;


public class SpriteData implements ISpriteData {

    protected float[] portraitVertices;
    protected float[] landscapeVertices;
    protected int textureIndex;
    protected int glImageID;
    protected int bitmapID;
    protected short[] indices;
    protected float[] defaultColor;
    protected float zVertice;
    protected boolean portrait = true;

    @Override
    public int getTextureIndex() {
        return textureIndex;
    }

    @Override
    public float[] getColor() {
        return defaultColor;
    }

    @Override
    public float[] getVertices() {
        if(portrait){
            return portraitVertices;
        }else
            return landscapeVertices;
    }

    @Override
    public int getBitmapID() {
        return bitmapID;
    }

    @Override
    public int getGLImageID() {
        return glImageID;
    }

    @Override
    public short[] getIndices(){
        return indices;
    }

    @Override
    public float getZVertices() {
        return zVertice;
    }

    @Override
    public void setOrientation(boolean portrait){
        this.portrait = portrait;
    }

}
