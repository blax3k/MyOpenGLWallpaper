package com.hashimapp.myopenglwallpaper.Model;

interface ISpriteData {

    int getTextureIndex();
    float[] getColor(int time);
    float[] getShapeVertices();
    float[] getTextureVertices();
    short[] getIndices();
    float getZVertices();
    void setOrientation(boolean portrait);

}
