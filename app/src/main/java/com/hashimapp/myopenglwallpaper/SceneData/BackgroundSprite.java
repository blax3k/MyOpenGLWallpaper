package com.hashimapp.myopenglwallpaper.SceneData;

import android.opengl.GLES20;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.Model.Textures;
import com.hashimapp.myopenglwallpaper.R;

public class BackgroundSprite extends SpriteData {

    public BackgroundSprite(){
        zVertice = -2.0f;
        portraitVertices = new float[]{
                -2.4f,  2.4f, -0.01f,   // top left
                -2.4f, -2.4f, -0.01f,   // bottom left
                2.4f, -2.4f, -0.01f,   // bottom right
                2.4f,  2.4f, -0.01f }; // top right

        landscapeVertices = new float[]{
                -2.4f,  2.4f, -0.01f,   // top left
                -2.4f, -2.4f, -0.01f,   // bottom left
                2.4f, -2.4f, -0.01f,   // bottom right
                2.4f,  2.4f, -0.01f }; // top right

        textureIndex = Textures.BACKGROUND_TEXTURE_INDEX;
        indices = new short[]{0, 1, 2, 0, 2, 3};
        defaultColor = new float[]
                       {1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f};

        textureVertices = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };
    }

}

