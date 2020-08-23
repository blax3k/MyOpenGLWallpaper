package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.Model.Textures;
import com.hashimapp.myopenglwallpaper.R;

public class GradientBarSprite extends SpriteData
{ int currentBitmap = R.drawable.guy;

    public GradientBarSprite()
    {
        bitmapID = R.drawable.desk_1024;
        zVertice = 0.0f;
        essentialLayer = false;
        portraitVertices = new float[]{
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

        earlyDawnColor = new float[]{
                0f, 0.27f, 0.37f, 1f,
                0f, 0.17f, 0.27f, 1f,
                0f, 0.17f, 0.27f, 1f,
                0f, 0.17f, 0.27f, 1f,};

        midDawnColor = new float[]{
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

        earlyDuskColor = new float[]
                {1f, 0.933f, 0.78f, 1f,
                        1f, 0.933f, 0.78f, 1f,
                        1f, 0.933f, 0.78f, 1f,
                        1f, 0.933f, 0.78f, 1f};

        midDuskColor = new float[]{
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

        textureVertices = new float[]{
                0.0f, 0.08f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 0.08f,
        };
    }

    @Override
    public int GetBitmapID( int scene){
        switch(scene){
            case Textures.IMAGE_SIZE_1024:
                return  R.drawable.desk_1024;
        }
//        if(currentBitmap == R.drawable.guy){
//            currentBitmap = R.drawable.guy2;
//        }else{
//            currentBitmap = R.drawable.guy;
//        }
//        return currentBitmap;

        return R.drawable.desk_1024;
    }
}
