package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.Model.WeatherManager;
import com.hashimapp.myopenglwallpaper.R;


public class BunnySprite extends SpriteData
{

    int currentBitmap = R.drawable.guy;

    public BunnySprite()
    {
        SpriteName = "BunnySprite";
        SetZVertice(.2f);
        essentialLayer = false;
        ShapeVertices = new float[]{
                -0.8f, 0.3f, 0.0f,   // top left
                -0.8f, -2.0f, (0.0f),   // bottom left
                0.8f, -2.0f, (0.0f),   // bottom right
                0.8f, 0.3f, (0.0f)};  // top right

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
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        float[][] colorSet = new float[SpriteColorData.DAY_PHASE_COUNT][];
        colorSet[0] = dawnStartColor;
        colorSet[1] = dawnEndColor;
        colorSet[2] = dayStartColor;
        colorSet[3] = dayEndColor;
        colorSet[4] = duskStartColor;
        colorSet[5] = duskEndColor;
        colorSet[6] = nightStartColor;
        colorSet[7] = nightEndColor;

        SpriteColorData.SetColor(WeatherManager.SUNNY_WEATHER, colorSet);
    }


    @Override
    public float[] GetTextureVertices()
    {
                TextureVertices = new float[]{
                        0.0f, 0.0f,
                        0.0f, 0.5f,
                        0.33f, 0.5f,
                        0.33f, 0.0f
                };
//        changeTextureVertices = CHANGE_NOW;
//        switch (scene)
//        {
//            case SceneManager.DEFAULT:
//                textureVertices = new float[]{
//                        0.0f, 0.0f,
//                        0.0f, 0.5f,
//                        0.33f, 0.5f,
//                        0.33f, 0.0f
//                };
//                break;
//            case SceneManager.BROWN:
//                textureVertices = new float[]{
//                        0.33f, 0.0f,
//                        0.33f, 0.5f,
//                        0.66f, 0.5f,
//                        0.66f, 0.0f
//                };
//                break;
//            case SceneManager.BLUE:
//                textureVertices = new float[]{
//                        0.66f, 0.0f,
//                        0.66f, 0.5f,
//                        1.0f, 0.5f,
//                        1.0f, 0.0f
//                };
//                break;
//            case SceneManager.GREEN:
//                textureVertices = new float[]{
//                        0.0f, 0.5f,
//                        0.0f, 1.0f,
//                        0.33f, 1.0f,
//                        0.33f, 0.5f
//                };
//                break;
//            case SceneManager.PINK:
//                textureVertices = new float[]{
//                        0.33f, 0.5f,
//                        0.33f, 1.0f,
//                        0.66f, 1.0f,
//                        0.66f, 0.5f
//                };
//                break;
//            case SceneManager.YELLOW:
//                textureVertices = new float[]{
//                        0.66f, 0.5f,
//                        0.66f, 1.0f,
//                        1.0f, 1.0f,
//                        1.0f, 0.5f
//                };
//                break;
//        }
        return TextureVertices;
    }
}


