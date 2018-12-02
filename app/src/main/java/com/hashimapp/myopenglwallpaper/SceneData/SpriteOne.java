package com.hashimapp.myopenglwallpaper.SceneData;

import android.opengl.GLES20;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.R;

public class SpriteOne extends SpriteData {

    public SpriteOne(){
        zVertice = -2.0f;
        portraitVertices = new float[]{
                -2.5f,  2.4f, -0.01f,   // top left
                -2.5f, -2.4f, -0.01f,   // bottom left
                 2.5f, -2.4f, -0.01f,   // bottom right
                 2.5f,  2.4f, -0.01f }; // top right

        landscapeVertices = new float[]{
                -2.5f,  2.4f, -0.01f,   // top left
                -2.5f, -2.4f, -0.01f,   // bottom left
                2.5f, -2.4f, -0.01f,   // bottom right
                2.5f,  2.4f, -0.01f }; // top right

        textureIndex = 0;
        glImageID = GLES20.GL_TEXTURE0;
        bitmapID = R.drawable.bg;
        indices = new short[]{0, 1, 2, 0, 2, 3};
        defaultColor = new float[]
                       {1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f};
    }

}

