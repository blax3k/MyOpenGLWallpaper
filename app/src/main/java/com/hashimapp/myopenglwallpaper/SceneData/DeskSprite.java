package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.Model.WeatherManager;
import com.hashimapp.myopenglwallpaper.R;

public class DeskSprite extends SpriteData {

    int currentBitmap = R.drawable.layer1;
    public DeskSprite() {
        SpriteName = "deskSprite";
//        ZVertice = 0.2f;
        essentialLayer = true;
        ShapeVertices = new float[]{
                -2.4f, 1.4f, (0.0f),   // top left
                -2.4f, -2.4f, (0.0f),   // bottom left
                2.4f, -2.4f, (0.0f),   // bottom right
                2.4f, 1.4f, (0.0f)}; // top right


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

        duskStartColor = new float[]{
                1f, 0.933f, 0.78f, 1f,
                1f, 0.933f, 0.78f, 1f,
                1f, 0.933f, 0.78f, 1f,
                1f, 0.933f, 0.78f, 1f};

        duskEndColor = new float[]{
                0.5f, 0.5f, 0.5f, 1f,
                0.5f, 0.5f, 0.5f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f};

        nightStartColor = new float[]{
                0.5f, 0.5f, 0.5f, 1f,
                0.5f, 0.5f, 0.5f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f};

        nightEndColor = new float[]{
                0.5f, 0.5f, 0.5f, 1f,
                0.5f, 0.5f, 0.5f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f};

        TextureVertices = new float[]{
                0.0f, 0.2f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.2f
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

    public int GetBitmapID(){
        return R.drawable.desk_1024;
    }

}

