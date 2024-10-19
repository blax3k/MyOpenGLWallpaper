package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.SceneData;
import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.R;

public class GirlSleepingScene extends SceneData
{
    float[] dawnStartColor = new float[]{
    0.4f, 0.57f, 0.77f, 1f,
            0.4f, 0.57f, 0.77f, 1f,
            0.4f, 0.57f, 0.77f, 1f,
            0.4f, 0.57f, 0.77f, 1f,};

    float[] dawnEndColor = new float[]{
    1.0f, 0.8f, 0.8f, 1f,
            1.0f, 0.8f, 0.8f, 1f,
            1.0f, 0.8f, 0.8f, 1f,
            1.0f, 1.0f, 1.0f, 1f,};

    float[] dayStartColor = new float[]
    {1f, 1f, 1f, 1f,
            1f, 1f, 1f, 1f,
            1f, 1f, 1f, 1f,
            0.9f, 0.9f, 0.9f, 1f};

    float[] dayEndColor = new float[]
    {0.9f, 0.9f, 0.9f, 1f,
            1f, 1f, 1f, 1f,
            1f, 1f, 1f, 1f,
            1f, 1f, 1f, 1f};

    float[] duskStartColor = new float[]{
    1f, 1f, 1f, 1f,
            0.98f, 0.69f, 0.44f, 1f,
            0.98f, 0.45f, 0.098f, 1f,
            1.0f, 0.69f, 0.24f, 1f,};

    float[] duskEndColor = new float[]{
    0.62f, 0.39f, 0.24f, 1f,
            0.12f, 0.19f, 0.14f, 1f,
            0.12f, 0.19f, 0.14f, 1f,
            0.12f, 0.19f, 0.14f, 1f,};

    float[] nightStartColor = new float[]{
    0.1f, 0.17f, 0.37f, 1f,
            0f, 0.07f, 0.17f, 1f,
            0f, 0.07f, 0.17f, 1f,
            0f, 0.07f, 0.17f, 1f,};

    float[] nightEndColor = new float[]{
    0f, 0.07f, 0.17f, 1f,
            0f, 0.07f, 0.17f, 1f,
            0f, 0.07f, 0.17f, 1f,
            0.1f, 0.17f, 0.37f, 1f,};


    public GirlSleepingScene(){
        super();
        float[][] colors = new float[][]{dawnStartColor, dawnEndColor, dayStartColor, dayEndColor, duskStartColor, duskEndColor, nightStartColor, nightEndColor};
        SpriteData background = new SpriteData(R.drawable.background, new float[]{
                -2.4f, 2.4f, 0.0f,   // top left
                -2.4f, -2.4f, 0.0f,   // bottom left
                2.4f, -2.4f, 0.0f,   // bottom right
                2.4f, 2.4f, 0.0f},
                new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        }, 1.0f, colors, true);
        SpriteData girl = new SpriteData(R.drawable.girlspritesheet1, new float[]{
                -0.6f, 0.6f, 0.0f,   // top left
                -0.6f, -1.0f, 0.0f,   // bottom left
                -0.2f, -1.0f, 0.0f,   // bottom right
                -0.2f, 0.6f, 0.0f},
                new float[]{
                        0.65f, 0.0f,
                        0.65f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f
                }, 1.0f, colors, true);
        SpriteData tower = new SpriteData(R.drawable.tower, new float[]{
                -2.4f, 2.4f, 0.0f,   // top left
                -2.4f, -2.4f, 0.0f,   // bottom left
                2.4f, -2.4f, 0.0f,   // bottom right
                2.4f, 2.4f, 0.0f},
                new float[]{
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f
                }, 0.6f, colors, true);
        SpriteData hill = new SpriteData(R.drawable.hill, new float[]{
                -3.0f, 0.0f, 0.0f,   // top left
                -3.0f, -2.4f, 0.0f,   // bottom left
                3.0f, -2.4f, 0.0f,   // bottom right
                3.0f, 0.0f, 0.0f},
                new float[]{
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f
                }, 0.0f, colors, true);

        this.SpriteDataList.add(background);
        this.SpriteDataList.add(girl);
//        this.SpriteDataList.add(tower);
//        this.SpriteDataList.add(hill);
    }
}
