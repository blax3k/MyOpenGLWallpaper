package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.Model.Textures;

public class GirlSprite extends SpriteData {

    public GirlSprite() {
        zVertice = -0.5f;
        portraitVertices = new float[]{
                -2.4f, 2.4f, 0.0f,   // top left
                -2.4f, -2.4f, 0.0f,   // bottom left
                2.4f, -2.4f, 0.0f,   // bottom right
                2.4f, 2.4f, 0.0f}; // top right

        portraitVerticesMotion = new float[]{
                -2.5f, 2.5f, 0.0f,   // top left
                -2.5f, -2.5f, 0.0f,   // bottom left
                2.5f, -2.5f, 0.0f,   // bottom right
                2.5f, 2.5f, 0.0f}; // top right

        landscapeVertices = new float[]{
                -2.4f, 2.4f, 0.0f,   // top left
                -2.4f, -2.4f, 0.0f,   // bottom left
                2.4f, -2.4f, 0.0f,   // bottom right
                2.4f, 2.4f, 0.0f}; // top right

        landscapeVerticesMotion = new float[]{
                -2.5f, 2.5f, 0.0f,   // top left
                -2.5f, -2.5f, 0.0f,   // bottom left
                2.5f, -2.5f, 0.0f,   // bottom right
                2.5f, 2.5f, 0.0f}; // top right

        textureIndex = Textures.GIRL_TEXTURE_INDEX;
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

