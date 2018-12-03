package com.hashimapp.myopenglwallpaper.SceneData;

import android.opengl.GLES20;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.R;

public class TestImage extends SpriteData {

    public TestImage(){
        zVertice =  0.0f;
        portraitVertices = new float[]{
                -2.4f,  2.4f, 0.0f,   // top left
                -2.4f, -2.4f, 0.0f,   // bottom left
                 2.4f, -2.4f, 0.0f,   // bottom right
                 2.4f,  2.4f, 0.0f }; // top right

        landscapeVertices = new float[]{
                -2.6f,  2.6f, 0.0f,   // top left
                -2.6f, -2.6f, 0.0f,   // bottom left
                2.6f, -2.6f, 0.0f,   // bottom right
                2.6f,  2.6f, 0.0f }; // top right

        glImageID = GLES20.GL_TEXTURE1;
        textureIndex = 1;
        bitmapID = R.drawable.wp_template;
        indices = new short[]{0, 1, 2, 0, 2, 3};
        defaultColor = new float[]
                       {1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f};
    }
}
