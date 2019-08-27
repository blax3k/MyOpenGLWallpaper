package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;
import com.hashimapp.myopenglwallpaper.Model.Textures;
import com.hashimapp.myopenglwallpaper.R;

public class BunnySprite extends SpriteData
{

    int currentBitmap = R.drawable.guy;

    public BunnySprite()
    {
        zVertice = -0.2f;
        portraitVertices = new float[]{
                -1.3f, 0.6f, 0.0f,   // top left
                -1.3f, -2.0f, 0.0f,   // bottom left
                1.3f, -2.0f, 0.0f,   // bottom right
                1.3f, 0.6f, 0.0f};  // top right

        landscapeVertices = new float[]{
                -1.3f, 0.6f, 0.0f,   // top left
                -1.3f, -2.0f, 0.0f,   // bottom left
                1.3f, -2.0f, 0.0f,   // bottom right
                1.3f, 0.6f, 0.0f};  // top right

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
                0.4f, 0.57f, 0.77f, 1f,
                0.4f, 0.57f, 0.77f, 1f,
                0.4f, 0.57f, 0.77f, 1f,
                0.4f, 0.57f, 0.77f, 1f,};

        dayColor = new float[]
                {1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f,
                        1f, 1f, 1f, 1f};

        earlyDuskColor = new float[]{0.92f, 0.69f, 0.44f, 1f,
                0.92f, 0.69f, 0.44f, 1f,
                0.92f, 0.69f, 0.44f, 1f,
                0.92f, 0.69f, 0.44f, 1f,};

        midDuskColor = new float[]{
                0f, 0.07f, 0.17f, 1f,
                0f, 0.07f, 0.17f, 1f,
                0f, 0.07f, 0.17f, 1f,
                0f, 0.07f, 0.17f, 1f,};

        nightColor = new float[]
                {0f, 0.17f, 0.27f, 1f,
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

    @Override
    public int GetBitmapID(int bitmapSize){
        switch(bitmapSize){
            case Textures.IMAGE_SIZE_1024:
                return  R.drawable.rabbit;
        }
//        if(currentBitmap == R.drawable.guy){
//            currentBitmap = R.drawable.guy2;
//        }else{
//            currentBitmap = R.drawable.guy;
//        }
//        return currentBitmap;

        return R.drawable.rabbit;
    }
}


