package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;


import com.hashimapp.myopenglwallpaper.R;

public class Textures {
    public static final int TEMPLATE_TEXTURE_INDEX = 0;
    public static final int GIRL_TEXTURE_INDEX = 1;
    public static final int BACKGROUND_TEXTURE_INDEX = 2;

    Context context;

    public Textures(Context inContext){
        context = inContext;
    }

    public void InitTextures(){
        textureNames = new int[20];
        GLES20.glGenTextures(20, textureNames, 0);
        setTexture(R.drawable.layer0, GLES20.GL_TEXTURE0, TEMPLATE_TEXTURE_INDEX);
        setTexture(R.drawable.layer1, GLES20.GL_TEXTURE1, GIRL_TEXTURE_INDEX);
        setTexture(R.drawable.layer2, GLES20.GL_TEXTURE2, BACKGROUND_TEXTURE_INDEX);
    }

    private boolean tempTexture;
    public int[] textureNames;

    public void SwapTextures(){
        Bitmap bmp;
        if(tempTexture){
            tempTexture = false;
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.layer0);
        }else{
            tempTexture = true;
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.layer00);
        }
        updateTexture(bmp, GLES20.GL_TEXTURE0, 0);
        bmp.recycle();
    }

    private void setTexture(int bitmapId, int GLImageID, int textureIndex){
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), bitmapId);
//
        GLES20.glActiveTexture(GLImageID);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureNames[textureIndex]);
        //clamp texture to edge of shape
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        updateTexture(bmp, GLImageID, textureIndex);
    }

    private void updateTexture(Bitmap bitmap, int GLImageID, int textureName){

        GLES20.glActiveTexture(GLImageID);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureNames[textureName]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

    }

    private void GenerateMipMaps(Bitmap bitmap, int GLImageID, int textureName){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int level = 1;

        while(width > 0 && height > 0){
            Bitmap blurred = BlurBuilder.blur(context, bitmap, )
        }
    }

}
