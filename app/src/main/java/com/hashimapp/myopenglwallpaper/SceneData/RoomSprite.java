package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.Model.Textures;
import com.hashimapp.myopenglwallpaper.R;

public class RoomSprite extends SpriteData {

    int currentBitmap = R.drawable.layer0;
    public RoomSprite() {
        zVertice = 0.5f;
        essentialLayer = true;
        portraitVertices = new float[]{
                -2.4f, 2.4f, 0.0f,   // top left
                -2.4f, -2.4f, 0.0f,   // bottom left
                2.4f, -2.4f, 0.0f,   // bottom right
                2.4f, 2.4f, 0.0f }; // top right

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
                0.8f, 0.8f, 1.0f, 1f,
                0.8f, 0.8f, 1.0f, 1f,
                0.8f, 0.8f, 1.0f, 1f,};

        dayStartColor = new float[]
                {       1f, 1f, 1f, 1f,
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
                0f, 0.18f, 0.40f, 1f,
                0f, 0.18f, 0.40f, 1f,
                0f, 0.14f, 0.30f, 1f,
                0f, 0.14f, 0.30f, 1f,};

        nightStartColor = new float[]
                {0.1f, 0.27f, 0.37f, 1f,
                        0f, 0.17f, 0.27f, 1f,
                        0f, 0.17f, 0.27f, 1f,
                        0f, 0.17f, 0.27f, 1f,};

        nightEndColor = new float[]{
                0f, 0.17f, 0.27f, 1f,
                0f, 0.17f, 0.27f, 1f,
                0f, 0.17f, 0.27f, 1f,
                0.1f, 0.27f, 0.37f, 1f,};

        textureVertices = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };
    }

    @Override
    public int GetBitmapID(int scene){
        switch(scene){
            case Textures.IMAGE_SIZE_1024:
                return  R.drawable.room_1024;
        }
//        if(currentBitmap == R.drawable.layer0){
//            currentBitmap = R.drawable.layer00;
//            return currentBitmap;
//        }
//        currentBitmap = R.drawable.layer0;
//        return currentBitmap;

        return R.drawable.room_1024;
    }
}
