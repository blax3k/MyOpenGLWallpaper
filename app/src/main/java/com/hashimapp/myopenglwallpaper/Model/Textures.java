package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;


import com.hashimapp.myopenglwallpaper.R;

import java.util.Calendar;

import javax.microedition.khronos.opengles.GL;

public class Textures {
    public static final int TEMPLATE_TEXTURE_INDEX = 0;
    public static final int GIRL_TEXTURE_INDEX = 1;
    public static final int BACKGROUND_TEXTURE_INDEX = 2;

    Context context;

    public Textures(Context inContext) {
        context = inContext;
    }

    public void InitTextures() {
        Log.d("blurTime", "INIT TEXTURES");
        textureNames = new int[20];
        GLES20.glGenTextures(20, textureNames, 0);
        setTexture(R.drawable.layer0, GLES20.GL_TEXTURE0, TEMPLATE_TEXTURE_INDEX);
        setTexture(R.drawable.layer1, GLES20.GL_TEXTURE1, GIRL_TEXTURE_INDEX);
        setTexture(R.drawable.layer2, GLES20.GL_TEXTURE2, BACKGROUND_TEXTURE_INDEX);
    }

    private boolean tempTexture;
    public int[] textureNames;

    public void SwapTextures() {
//        Bitmap bmp;
//        if (tempTexture) {
//            tempTexture = false;
//            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.layer0);
//        } else {
//            tempTexture = true;
//            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.layer00);
//        }
//        updateTexture(bmp, GLES20.GL_TEXTURE0, 0);
//        bmp.recycle();
    }

    boolean first = true;

    private void setTexture(int bitmapId, int GLImageID, int textureIndex) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), bitmapId);

        GLES20.glActiveTexture(GLImageID);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureNames[textureIndex]);
        //clamp texture to edge of shape
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bmp, 0 );
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        GenerateBlurredTextures(bitmapId, GLImageID, textureIndex);
    }

    private void updateTexture(Bitmap bitmap, int GLImageID, int textureName) {

        GLES20.glActiveTexture(GLImageID);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureNames[textureName]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

    }

    private void GenerateBlurredTextures(int bitmapId, int GLImageID, int textureIndex) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), bitmapId);

        int originalWidth, bmpWidth;
        originalWidth = bmpWidth = bmp.getWidth();
        int originalHeight, bmpHeight;
        originalHeight = bmpHeight = bmp.getHeight();

        int index = 1;

        int totalProcessingTime = 0;

        while (bmpWidth > 32 && bmpHeight > 32) {
            long start = System.currentTimeMillis();
            //cut image size in half
            bmpWidth /= 2;
            bmpHeight /= 2;

//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureNames[textureIndex]);
            //blur current image
            Bitmap blurredFull = BlurBuilder.blur(context, bmp, (float)bmpWidth / originalWidth, (float)bmpHeight / originalHeight, 4.0f);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, index, blurredFull, 0);
            index++;

            long end = System.currentTimeMillis();
            long total = end - start;
            totalProcessingTime += total;
            Log.d("blurTime", "time to blur '" + bmp.toString() + "' took " + total + " ms");
        }

        Log.d("blurTime", "time to blur all mip maps of '" + bmp.toString() + "' took " + totalProcessingTime + " ms");
        Log.d("blurTime", "final index: " + index);
    }
}


