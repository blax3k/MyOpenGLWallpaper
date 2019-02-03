package com.hashimapp.myopenglwallpaper.Model;


public class SpriteData implements ISpriteData {

    protected float[] portraitVertices;
    protected float[] landscapeVertices;
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

    @Override
    public int getTextureIndex() {
        return textureIndex;
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
    public float[] getShapeVertices() {
        if (portrait) {
            return portraitVertices;
        } else
            return landscapeVertices;
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
