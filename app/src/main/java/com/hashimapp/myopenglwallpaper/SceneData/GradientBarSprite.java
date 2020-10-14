package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.R;

public class GradientBarSprite extends SpriteData
{ int currentBitmap = R.drawable.guy;

    public GradientBarSprite()
    {
//        ZVertice = 0.0f;
        essentialLayer = false;
        ShapeVertices = new float[]{
                -2.4f, 2.4f, 0.0f,   // top left
                -2.4f, 2.0f, 0.0f,   // bottom left
                2.4f, 2.0f, 0.0f,   // bottom right
                2.4f, 2.4f, 0.0f}; // top right

        indices = new short[]{0, 1, 2, 0, 2, 3};
        defaultColor = new float[]
                {1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f};

        dawnStartColor = new float[]{
                0f, 0.27f, 0.37f, 1f,
                0f, 0.17f, 0.27f, 1f,
                0f, 0.17f, 0.27f, 1f,
                0f, 0.17f, 0.27f, 1f,};

        dawnEndColor = new float[]{
                1.0f, 0.8f, 0.8f, 1f,
                0.8f, 0.8f, 1.0f, 1f,
                0.8f, 0.8f, 1.0f, 1f,
                0.8f, 0.8f, 1.0f, 1f,};

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

        duskStartColor = new float[]
                {1f, 0.933f, 0.78f, 1f,
                        1f, 0.933f, 0.78f, 1f,
                        1f, 0.933f, 0.78f, 1f,
                        1f, 0.933f, 0.78f, 1f};

        duskEndColor = new float[]{
                0f, 0.07f, 0.17f, 1f,
                0f, 0.07f, 0.17f, 1f,
                0.9f, 0.9f, 0.9f, 1f,
                1f, 1f, 1f, 1f,};

        nightStartColor = new float[]{
                0f, 0.07f, 0.17f, 1f,
                0f, 0.07f, 0.17f, 1f,
                0.9f, 0.9f, 0.9f, 1f,
                1f, 1f, 1f, 1f,};

        nightEndColor = new float[]{
                0f, 0.07f, 0.17f, 1f,
                0f, 0.07f, 0.17f, 1f,
                0.9f, 0.9f, 0.9f, 1f,
                1f, 1f, 1f, 1f,};

        TextureVertices = new float[]{
                0.0f, 0.08f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 0.08f,
        };
    }

    public int GetBitmapID(){
        return R.drawable.desk_1024;
    }
}
