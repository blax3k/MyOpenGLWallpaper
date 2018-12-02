package com.hashimapp.myopenglwallpaper.Model;

interface ISpriteData {

    int getTextureIndex();
    float[] getColor();
    float[] getVertices();
    int getBitmapID();
    int getGLImageID();
    short[] getIndices();
    float getZVertices();
    void setOrientation(boolean portrait);

}
