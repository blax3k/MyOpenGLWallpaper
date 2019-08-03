package com.hashimapp.myopenglwallpaper.Model;

interface ISpriteData {

    float[] getColor(int time, int phasePercentage);
    float[] getShapeVertices(boolean portrait, boolean motionOffset);
    float[] getTextureVertices();
    short[] getIndices();
    float getZVertices();
    void setOrientation(boolean portrait);
    int GetBitmapID();


}
