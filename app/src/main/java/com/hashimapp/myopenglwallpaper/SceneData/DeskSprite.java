package com.hashimapp.myopenglwallpaper.SceneData;

import android.opengl.GLES20;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.Model.Textures;
import com.hashimapp.myopenglwallpaper.R;

public class DeskSprite extends SpriteData {

    int currentBitmap = R.drawable.layer1;
    public DeskSprite() {
        zVertice = -0.0010f;
        essentialLayer = true;
        portraitVertices = new float[]{
                -2.4f, 1.4f, (zVertice),   // top left
                -2.4f, -2.4f, (zVertice),   // bottom left
                2.4f, -2.4f, (zVertice),   // bottom right
                2.4f, 1.4f, (zVertice)}; // top right


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

        earlyDuskColor = new float[]{
                1f, 0.933f, 0.78f, 1f,
                1f, 0.933f, 0.78f, 1f,
                1f, 0.933f, 0.78f, 1f,
                1f, 0.933f, 0.78f, 1f};

        midDuskColor = new float[]{
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

        textureVertices = new float[]{
                0.0f, 0.2f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.2f
        };

    }

    @Override
    public int GetBitmapID(int scene){
        switch(scene){
            case Textures.IMAGE_SIZE_1024:
                return  R.drawable.desk_1024;
        }
//        if(currentBitmap == R.drawable.layer1){
//            currentBitmap = R.drawable.layer1_flipped;
//            return currentBitmap;
//        }
//        currentBitmap = R.drawable.layer1;
//        return currentBitmap;

        return R.drawable.desk_1024;
    }

}

