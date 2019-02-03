package com.hashimapp.myopenglwallpaper.SceneData;

import android.opengl.GLES20;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.Model.Textures;
import com.hashimapp.myopenglwallpaper.R;

public class TemplateSprite extends SpriteData {

    public TemplateSprite(){
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

        textureIndex = Textures.TEMPLATE_TEXTURE_INDEX;
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
