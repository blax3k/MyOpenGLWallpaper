package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.SceneData.BackgroundSprite;
import com.hashimapp.myopenglwallpaper.SceneData.GirlSprite;
import com.hashimapp.myopenglwallpaper.SceneData.TemplateSprite;
import com.hashimapp.myopenglwallpaper.View.OpenGLES2WallpaperService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Blake on 9/19/2015.
 */
public class SceneSetter
{
    Textures textures;
    Random randomGenerator;
    private int girlTextureChoice;

    SharedPreferences preferences;
    Context context;
    TimeTracker timeTracker;
    List<Sprite> spriteList;

    private int mColorHandle;
    private int mPositionHandle;
    private int mTexCoordLoc;
    private int mtrxHandle;
    private int mSamplerLoc;
    private int biasHandle;

    private long FocalPointResetTime;
    private long FocalPointTargetTime;

    private float focalPoint;
    private float focalPointStartingPoint;
    private float focalPointEndingPoint;
    boolean swapTextures = false;


    public SceneSetter()
    {
        context = OpenGLES2WallpaperService.getAppContext();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        randomGenerator = new Random();
        timeTracker = new TimeTracker();
        spriteList = new ArrayList<>();
        textures = new Textures(this.context);
        focalPointStartingPoint = -1.0f;
        focalPointEndingPoint = 0.0f;
    }

    public void swapTextures(){
        swapTextures = true;
//        textures.SwapTextures();
    }


    public void InitSprites(){
        textures.InitTextures();
        spriteList.add(new Sprite(new BackgroundSprite()));
        spriteList.add(new Sprite(new GirlSprite()));
        spriteList.add(new Sprite(new TemplateSprite()));
        //enable transparency
//
//        for(Sprite sprite : spriteList){
//            sprite.SetTexture(context);
//        }

    }

    float[] scratch = new float[16];

    public void DrawSprites(float[] mtrxView, float[] mtrxProjection, float[] mModelMatrix){
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        if(swapTextures){
            textures.SwapTextures();
            swapTextures = false;
        }

        for(Sprite sprite : spriteList){
            sprite.draw(mtrxView, mtrxProjection, mModelMatrix, mColorHandle,
                    mPositionHandle, mTexCoordLoc, mtrxHandle, mSamplerLoc, biasHandle, scratch, textures.textureNames);
        }
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }

    public void SetTimeOfDay(int time, int percentage){
        for(Sprite sprite : spriteList){
            sprite.SetTime(time, percentage);
        }
    }

    public void OffsetChanged(float xOffset, boolean portraitOrientation){
        for(Sprite sprite : spriteList){
            sprite.OffsetChanged(xOffset, portraitOrientation);
        }
    }

    public void SensorChanged(float xOffset, float yOffset){
        for(Sprite sprite : spriteList){
            sprite.SensorChanged(xOffset, yOffset);
        }
    }

    public void SurfaceChanged(boolean portrait, boolean motionOffset, float spriteXPosOffset){
        GetMembers();
//        GLES20.glEnableVertexAttribArray(mColorHandle);
//        GLES20.glEnableVertexAttribArray(mPositionHandle);
//        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        for(Sprite sprite : spriteList){
            sprite.SetOrientation(portrait, motionOffset, spriteXPosOffset);
        }
    }

    public void UpdateFocalPoint(){
        long currentTime = System.currentTimeMillis();
        float focalPointProgression = (float)(currentTime - FocalPointResetTime)/(FocalPointTargetTime - FocalPointResetTime);

        focalPoint = focalPointProgression * Math.abs(focalPointStartingPoint - focalPointEndingPoint) + focalPointStartingPoint;

        for(Sprite sprite : spriteList){
            sprite.SetFocalPoint(focalPoint);
        }
    }

    public boolean FocalPointReached(){
        return focalPoint >= focalPointEndingPoint;
    }

    public void ResetFocalPoint(){
        FocalPointResetTime = System.currentTimeMillis();
        FocalPointTargetTime = FocalPointResetTime + 1500;
        focalPoint = focalPointStartingPoint;
        for(Sprite sprite : spriteList) {
            sprite.SetFocalPoint(focalPointEndingPoint);
        }

    }

    private void GetMembers() {
        // get handle to vertex shader's vPosition member
        mColorHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_Color");
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "vPosition");
        mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_texCoord");
        // Get handle to shape's transformation matrix and apply it
        mtrxHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");
        // Get handle to textures locations
        mSamplerLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "s_texture");
        biasHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "bias");
        Log.d("blur", "bias handle: " + biasHandle);


    }

}
