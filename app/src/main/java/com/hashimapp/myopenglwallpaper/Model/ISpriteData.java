package com.hashimapp.myopenglwallpaper.Model;

interface ISpriteData {

    int getTextureIndex();
    int getTexIndex();
    float[] getColor(int time, int phasePercentage);
    float[] getShapeVertices(boolean portrait, boolean motionOffset);
    float[] getTextureVertices();
    short[] getIndices();
    float getZVertices();
    void setOrientation(boolean portrait);


}
