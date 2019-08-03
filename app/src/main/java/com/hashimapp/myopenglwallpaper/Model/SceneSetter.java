package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.SceneData.BackgroundSprite;
import com.hashimapp.myopenglwallpaper.SceneData.MechSprite;
import com.hashimapp.myopenglwallpaper.SceneData.GirlSprite;
import com.hashimapp.myopenglwallpaper.View.OpenGLES2WallpaperService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Blake on 9/19/2015.
 */
public class SceneSetter
{
    public static final int STATUS_DONE = 0;
    public static final int STATUS_FADING_OUT = 1;
    public static final int STATUS_READY_TO_SWAP = 2;
    public static final int STATUS_LOADING_TEXTURES = 3;
    public static final int STATUS_FADING_IN = 4;

    private static float MIN_ALPHA = 0.0f;
    private static float MAX_ALPHA = 1.0f;


    Textures textures;
    Random randomGenerator;

    SharedPreferences preferences;
    Context context;
    TimeTracker timeTracker;
    List<Sprite> spriteList;
    Resources resources;

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

    private int textureSwapStatus;

    private HashMap<Integer, Integer> bitmapIdTextureNameHashMap;

    private long FadeResetTime;
    private long FadeTargetTime;
    private float fadeStartingPoint;
    private float fadeEndingPoint;

    Date startDate;


    public SceneSetter()
    {
        startDate = new Date();
        context = OpenGLES2WallpaperService.getAppContext();
        resources = context.getResources();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        randomGenerator = new Random();
        timeTracker = new TimeTracker(resources);
        spriteList = new ArrayList<>();
        textures = new Textures(this.context);
        focalPointStartingPoint = -1.0f;
        focalPointEndingPoint = 0.0f;
        bitmapIdTextureNameHashMap = new HashMap<>();
        textureSwapStatus = STATUS_DONE;
    }


    public void InitSprites()
    {
        //todo: add sprite key generator
        spriteList.add(new Sprite(new BackgroundSprite(), 0));
        spriteList.add(new Sprite(new MechSprite(), 1));
        spriteList.add(new Sprite(new GirlSprite(), 2));

        textures.InitTextures();
        int i = 0;
        for (Sprite sprite : spriteList)
        {
            TextureData textureData = textures.AddTexture(sprite.getCurrentBitmapID(), i);
            sprite.SetTextureData(textureData);
            i++;
        }

    }

    float[] scratch = new float[16];


    public void SetTimeOfDay(int timePhase, int percentage)
    {
        for (Sprite sprite : spriteList)
        {
            sprite.SetTime(timePhase, percentage);
        }
    }


    public void OffsetChanged(float xOffset, boolean portraitOrientation)
    {
        for (Sprite sprite : spriteList)
        {
            sprite.OffsetChanged(xOffset, portraitOrientation);
        }
    }

    public void SensorChanged(float xOffset, float yOffset)
    {
        for (Sprite sprite : spriteList)
        {
            sprite.SensorChanged(xOffset, yOffset);
        }
    }

    public void SurfaceChanged(boolean portrait, boolean motionOffset, float spriteXPosOffset)
    {
        GetMembers();
//        GLES20.glEnableVertexAttribArray(mColorHandle);
//        GLES20.glEnableVertexAttribArray(mPositionHandle);
//        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        // check if textures need to be swapped
        // Get textures and corresponding sprites that need to be swapped
        // tell textures class which textures to load


        for (Sprite sprite : spriteList)
        {
            sprite.SetOrientation(portrait, motionOffset, spriteXPosOffset);
        }
    }


    public boolean RackingFocus()
    {
        return focalPoint < focalPointEndingPoint;
    }

    public void UpdateFocalPoint()
    {
        long currentTime = System.currentTimeMillis();
        float focalPointProgression = (float) (currentTime - FocalPointResetTime) / (FocalPointTargetTime - FocalPointResetTime);

        focalPoint = focalPointProgression * Math.abs(focalPointStartingPoint - focalPointEndingPoint) + focalPointStartingPoint;

        for (Sprite sprite : spriteList)
        {
            sprite.SetFocalPoint(focalPoint);
        }
    }

    public void InitTextureSwap()
    {
        bitmapIdTextureNameHashMap.clear();
        //decide which scene

        for (Sprite sprite : spriteList)
        {
            int bitmapID = sprite.GetNextBitmapID();

            if (bitmapID >= 0)
            { //valid bitmap
                //check if bitmap is already on list
                if (!bitmapIdTextureNameHashMap.containsKey(bitmapID))
                {
                    bitmapIdTextureNameHashMap.put(bitmapID, sprite.getTextureName());
                }
                sprite.SetTextureSwapRequired(true);
            }
        }

        textureSwapStatus = STATUS_FADING_OUT;
        ResetFadePoint();


//        textureSwapStatus = STATUS_FADING_OUT;
//        ResetFadePoint();

//        textures.SwapTextures(bitmapIdTextureNameHashMap);


    }

    public void ResetTextureSwap(){
        bitmapIdTextureNameHashMap.clear();
        textureSwapStatus = STATUS_DONE;
    }

    public int GetTextureSwapStatus()
    {

        return textureSwapStatus;
    }

    ///always called from the main thread
    public void UpdateFade()
    {
        if (textureSwapStatus == STATUS_FADING_IN || textureSwapStatus == STATUS_FADING_OUT)
        {
            long currentTime = System.currentTimeMillis();

            //get current progress for fade
            float fadePercentage = (float) (currentTime - FadeResetTime) / (FadeTargetTime - FadeResetTime);
            float alpha = fadePercentage * MAX_ALPHA;
            if (textureSwapStatus == STATUS_FADING_OUT)
            {
                alpha = MAX_ALPHA - alpha;
            }
            //don't have alpha exceed max or min
            if (alpha >= 1.0f)
            {
                alpha = 1.0f;
            } else if (alpha <= 0.0f)
            {
                alpha = 0.0f;
            }

            for (Sprite sprite : spriteList)
            {
                if (sprite.TextureSwapRequired())
                {
                    sprite.SetAlpha(alpha);
                }
            }

            //fade complete
            if (fadePercentage >= 1.0f || fadePercentage <= 0.0f)
            {
                if (textureSwapStatus == STATUS_FADING_OUT)
                { //finished fading out. load textures
                    textureSwapStatus = STATUS_READY_TO_SWAP;
                } else
                {  //finshed fading in. stop
                    textureSwapStatus = STATUS_DONE;
                }
            }
        } else if (textureSwapStatus == STATUS_READY_TO_SWAP)
        {
            new Thread(() -> textures.LoadTextures(bitmapIdTextureNameHashMap)).start();
            textureSwapStatus = STATUS_LOADING_TEXTURES;
        } else if (textureSwapStatus == STATUS_LOADING_TEXTURES)
        {
            if (!textures.UploadComplete())
            {
                textures.UploadTextures();
            } else
            { //loaded. fade textures back in
                textureSwapStatus = STATUS_FADING_IN;
                ResetFadePoint();
            }
        }
    }

    private void ResetFadePoint()
    {
        if (textureSwapStatus == STATUS_FADING_OUT)
        {
            fadeStartingPoint = MAX_ALPHA;
            fadeEndingPoint = MIN_ALPHA;
        } else
        { //fading in
            fadeStartingPoint = MIN_ALPHA;
            fadeEndingPoint = MAX_ALPHA;
        }
        FadeResetTime = System.currentTimeMillis();
        FadeTargetTime = FadeResetTime + 1000;
    }


    public void ResetFocalPoint()
    {
        FocalPointResetTime = System.currentTimeMillis();
        FocalPointTargetTime = FocalPointResetTime + 1500;
        focalPoint = focalPointStartingPoint;
        for (Sprite sprite : spriteList)
        {
            sprite.SetFocalPoint(focalPointEndingPoint);
        }

    }


    public void DrawSprites(float[] mtrxView, float[] mtrxProjection, float[] mModelMatrix)
    {
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);


        for (Sprite sprite : spriteList)
        {
            sprite.draw(mtrxView, mtrxProjection, mModelMatrix, mColorHandle,
                    mPositionHandle, mTexCoordLoc, mtrxHandle, mSamplerLoc, biasHandle, scratch);
        }
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }


    private void GetMembers()
    {
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
