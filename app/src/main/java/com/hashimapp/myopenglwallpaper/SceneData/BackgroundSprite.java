package com.hashimapp.myopenglwallpaper.SceneData;

import android.opengl.GLES20;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.Model.Textures;
import com.hashimapp.myopenglwallpaper.R;

public class BackgroundSprite extends SpriteData {

    public BackgroundSprite() {
        zVertice = -1.0f;
        portraitVertices = new float[]{
                -2.4f, 2.4f, 0.0f,   // top left
                -2.4f, -2.4f, 0.0f,   // bottom left
                2.4f, -2.4f, 0.0f,   // bottom right
                2.4f, 2.4f, 0.0f}; // top right

        portraitVerticesMotion = new float[]{
                -2.7f, 2.7f, 0.0f,   // top left
                -2.7f, -2.7f, 0.0f,   // bottom left
                2.7f, -2.7f, 0.0f,   // bottom right
                2.7f, 2.7f, 0.0f}; // top right

        landscapeVertices = new float[]{
                -2.4f, 2.4f, 0.0f,   // top left
                -2.4f, -2.4f, 0.0f,   // bottom left
                2.4f, -2.4f, 0.0f,   // bottom right
                2.4f, 2.4f, 0.0f}; // top right

        landscapeVerticesMotion = new float[]{
                -2.7f, 2.7f, 0.0f,   // top left
                -2.7f, -2.7f, 0.0f,   // bottom left
                2.7f, -2.7f, 0.0f,   // bottom right
                2.7f, 2.7f, 0.0f}; // top right

        textureIndex = Textures.BACKGROUND_TEXTURE_INDEX;
        TexIndex = GLES20.GL_TEXTURE2;
        indices = new short[]{0, 1, 2, 0, 2, 3};
        defaultColor = new float[]{
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f};

        dawnColor = new float[]{
                0.4f, 0.57f, 0.77f, 1f,
                0.4f, 0.57f, 0.77f, 1f,
                0.4f, 0.57f, 0.77f, 1f,
                0.4f, 0.57f, 0.77f, 1f,};

        dayColor = new float[]{
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f};

        sunsetColor = new float[]{
                1f, 1f, 1f, 1f,
                0.92f, 0.69f, 0.44f, 1f,
                0.92f, 0.45f, 0.098f, 1f,
                0.92f, 0.69f, 0.44f, 1f,};

        twilightColor = new float[]{
                0.62f, 0.39f, 0.24f, 1f,
                0.12f, 0.19f, 0.14f, 1f,
                0.12f, 0.19f, 0.14f, 1f,
                0.12f, 0.19f, 0.14f, 1f,};

        nightColor = new float[]{
                0f, 0.17f, 0.27f, 1f,
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

