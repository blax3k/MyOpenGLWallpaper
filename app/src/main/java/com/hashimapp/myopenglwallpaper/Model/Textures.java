package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;


import com.hashimapp.myopenglwallpaper.R;

public class Textures {
    public static final int BACKGROUND_TEXTURE_INDEX = 0;
    public static final int TEMPLATE_TEXTURE_INDEX = 1;
    public static final int GIRL_TEXTURE_INDEX = 2;

    Context context;

    public Textures(Context inContext){
        context = inContext;
    }

    public void InitTextures(){
        setTexture(R.drawable.bgs,          GLES20.GL_TEXTURE0, BACKGROUND_TEXTURE_INDEX);
        setTexture(R.drawable.wp_templates, GLES20.GL_TEXTURE1, TEMPLATE_TEXTURE_INDEX);
        setTexture(R.drawable.girl,         GLES20.GL_TEXTURE2, GIRL_TEXTURE_INDEX);
    }

    private void setTexture(int bitmapId, int GLImageID, int textureIndex){
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), bitmapId);
        GLES20.glActiveTexture(GLImageID);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIndex);
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //clamp texture to edge of shape
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
    }

}
