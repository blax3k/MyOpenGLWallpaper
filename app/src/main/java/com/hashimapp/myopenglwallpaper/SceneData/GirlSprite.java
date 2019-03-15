package com.hashimapp.myopenglwallpaper.SceneData;

import android.opengl.GLES20;

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
        TexIndex = GLES20.GL_TEXTURE1;
        indices = new short[]{0, 1, 2, 0, 2, 3};
        defaultColor = new float[]
                {1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f};

        dawnColor = new float[]
                        {0f, 0.17f, 0.27f, 1f,
                        0f, 0.17f, 0.27f, 1f,
                        0f, 0.17f, 0.27f, 1f,
                        0f, 0.17f, 0.27f, 1f,};

        dayColor = new float[]
                {1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f};

        sunsetColor = new float[]{0.92f, 0.69f, 0.44f, 1f,
                0.92f, 0.69f, 0.44f, 1f,
                0.92f, 0.69f, 0.44f, 1f,
                0.92f, 0.69f, 0.44f, 1f,};

        twilightColor = new float[]{
                0f, 0.12f, 0.20f, 1f,
                0f, 0.12f, 0.20f, 1f,
                0f, 0.12f, 0.20f, 1f,
                0f, 0.12f, 0.20f, 1f,};

        nightColor = new float[]
                {0f, 0.17f, 0.27f, 1f,
                        0f, 0.17f, 0.27f, 1f,
                        0f, 0.17f, 0.27f, 1f,
                        0f, 0.17f, 0.27f, 1f,};

        textureVertices = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };
    }

}

