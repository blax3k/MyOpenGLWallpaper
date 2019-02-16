package com.hashimapp.myopenglwallpaper.Model;


public class SpriteData implements ISpriteData {

    protected float[] portraitVertices;
    protected float[] portraitVerticesMotion;
    protected float[] landscapeVertices;
    protected float[] landscapeVerticesMotion;
    protected float[] textureVertices;
    protected int textureIndex;
    //    protected int glImageID;
//    protected int bitmapID;
    protected short[] indices;
    protected float[] defaultColor;
    protected float[] dawnColor;
    protected float[] dayColor;
    protected float[] sunsetColor;
    protected float[] nightColor;
    protected float zVertice;
    protected boolean portrait = true;
    protected int TexIndex;

    @Override
    public int getTextureIndex() {
        return textureIndex;
    }

    @Override
    public int getTexIndex() {
        return TexIndex;
    }

    @Override
    public float[] getColor(int timeOfDay) {
        switch (timeOfDay) {
            case TimeTracker.DAWN:
                return dawnColor;
            case TimeTracker.DAY:
                return dayColor;
            case TimeTracker.SUNSET:
                return sunsetColor;
            case TimeTracker.NIGHT:
                return nightColor;
        }
        return defaultColor;
    }

    @Override
    public float[] getShapeVertices(boolean portrait, boolean motionOffset) {
        if (portrait)
        {
            if(motionOffset)
            {
                return portraitVerticesMotion;
            }
            return portraitVertices;
        } else
        {
            if(motionOffset)
            {
                return landscapeVerticesMotion;
            }
            return landscapeVertices;
        }
    }

    @Override
    public float[] getTextureVertices() {
        return textureVertices;
    }

    @Override
    public short[] getIndices() {
        return indices;
    }

    @Override
    public float getZVertices() {
        return zVertice;
    }

    @Override
    public void setOrientation(boolean portrait) {
        this.portrait = portrait;
    }

}
