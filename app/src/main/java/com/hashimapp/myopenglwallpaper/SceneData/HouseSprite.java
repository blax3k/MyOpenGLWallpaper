package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.Model.Textures;
import com.hashimapp.myopenglwallpaper.R;

public class HouseSprite extends SpriteData {

    public HouseSprite() {
        zVertice = -0.0097f;
        essentialLayer = true;

        portraitVertices = new float[]{
                -2.4f, 1.6f, zVertice,   // top left
                -2.4f, -0.8f, zVertice,   // bottom left
                2.4f, -0.8f, zVertice,   // bottom right
                2.4f, 1.6f, zVertice }; // top right

        indices = new short[]{0, 1, 2, 0, 2, 3};
        defaultColor = new float[]
                {1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f};

        earlyDawnColor = new float[]
                {0f, 0.17f, 0.27f, 1f,
                        0f, 0.17f, 0.27f, 1f,
                        0f, 0.17f, 0.27f, 1f,
                        0f, 0.17f, 0.27f, 1f,};

        midDawnColor = new float[]{
                1.0f, 0.8f, 0.8f, 1f,
                1.0f, 0.8f, 0.8f, 1f,
                0.5f, 0.5f, 1.0f, 1f,
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

        earlyDuskColor = new float[]{
                0.92f, 0.69f, 0.44f, 1f,
                0.92f, 0.69f, 0.44f, 1f,
                0.92f, 0.69f, 0.44f, 1f,
                0.92f, 0.69f, 0.44f, 1f,};

        midDuskColor = new float[]{
                0.0f, 0.14f, 0.30f, 1f,
                0.0f, 0.10f, 0.20f, 1f,
                0.0f, 0.03f, 0.07f, 1f,
                0.0f, 0.07f, 0.14f, 1f,};


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
                0.0f, 0.5f,
                1.0f, 0.5f,
                1.0f, 0.0f
        };
    }

    @Override
    public int GetBitmapID( int scene){
        switch(scene){
            case Textures.IMAGE_SIZE_1024:
                return  R.drawable.foregroundcitymap_1024;
        }
//        if(currentBitmap == R.drawable.layer0){
//            currentBitmap = R.drawable.layer00;
//            return currentBitmap;
//        }
//        currentBitmap = R.drawable.layer0;
//        return currentBitmap;

        return R.drawable.foregroundcitymap_1024;
    }
}
