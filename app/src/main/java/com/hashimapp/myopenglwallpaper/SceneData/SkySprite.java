package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.SceneData;
import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.Model.Textures;
import com.hashimapp.myopenglwallpaper.R;

public class SkySprite extends SpriteData {

    public SkySprite() {
        zVertice = 1.0f;
        essentialLayer = true;
        portraitVertices = new float[]{
                -2.4f, 2.4f, 0.0f,   // top left
                -2.4f, -2.4f, 0.0f,   // bottom left
                2.4f, -2.4f, 0.0f,   // bottom right
                2.4f, 2.4f, 0.0f}; // top right

        indices = new short[]{0, 1, 2, 0, 2, 3};
        defaultColor = new float[]{
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f};

        earlyDawnColor = new float[]{
                0.4f, 0.57f, 0.77f, 1f,
                0.4f, 0.57f, 0.77f, 1f,
                0.4f, 0.57f, 0.77f, 1f,
                0.4f, 0.57f, 0.77f, 1f,};

        midDawnColor = new float[]{
                1.0f, 0.8f, 0.8f, 1f,
                1.0f, 0.8f, 0.8f, 1f,
                1.0f, 0.8f, 0.8f, 1f,
                1.0f, 1.0f, 1.0f, 1f,};

        dayStartColor = new float[]
                {1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        0.9f, 0.9f, 0.9f, 1f};

        dayEndColor = new float[]
                {0.9f, 0.9f, 0.9f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f};

        earlyDuskColor = new float[]{
                1f, 1f, 1f, 1f,
                0.98f, 0.69f, 0.44f, 1f,
                0.98f, 0.45f, 0.098f, 1f,
                1.0f, 0.69f, 0.24f, 1f,};

        midDuskColor = new float[]{
                0.62f, 0.39f, 0.24f, 1f,
                0.12f, 0.19f, 0.14f, 1f,
                0.12f, 0.19f, 0.14f, 1f,
                0.12f, 0.19f, 0.14f, 1f,};

        nightStartColor = new float[]{
                0.1f, 0.17f, 0.37f, 1f,
                0f, 0.07f, 0.17f, 1f,
                0f, 0.07f, 0.17f, 1f,
                0f, 0.07f, 0.17f, 1f,};

        nightEndColor = new float[]{
                0f, 0.07f, 0.17f, 1f,
                0f, 0.07f, 0.17f, 1f,
                0f, 0.07f, 0.17f, 1f,
                0.1f, 0.17f, 0.37f, 1f,};

        textureVertices = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };
    }

    @Override
    public int GetBitmapID( int scene)
    {
        switch(scene){
            case Textures.IMAGE_SIZE_512:
                return R.drawable.sky_1024_border;
            case Textures.IMAGE_SIZE_1024:
                break;
            case Textures.IMAGE_SIZE_2048:
                break;
        }
        return R.drawable.sky_1024_border;
    }

}

