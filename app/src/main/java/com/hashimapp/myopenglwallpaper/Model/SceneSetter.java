package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.SceneData.BackgroundSprite;
import com.hashimapp.myopenglwallpaper.SceneData.BunnySprite;
import com.hashimapp.myopenglwallpaper.SceneData.DeskSprite;
import com.hashimapp.myopenglwallpaper.SceneData.HouseSprite;
import com.hashimapp.myopenglwallpaper.SceneData.LampSprite;
import com.hashimapp.myopenglwallpaper.SceneData.RoomSprite;

import java.util.ArrayList;
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

    private static final int FADE_OUT_TRANSITION_DURATION = 500;
    private static final int FADE_IN_TRANSITION_DURATION = 2000;
    private static final int FOCAL_POINT_RESET_DURATION = 1500;
    private static float MIN_PROGRESS = 0.0f;
    private static float MAX_PROGRESS = 1.0f;


    Textures textures;
    Random randomGenerator;

    SharedPreferences preferences;
    Context context;
    List<Sprite> spriteList;
    Resources resources;

    private long FocalPointResetTime;
    private long FocalPointTargetTime;

    private float focalPoint;
    private float focalPointStartingPoint;
    private float focalPointEndingPoint;
    private boolean rackingFocus;

    private long ZoomPointResetTime;
    private long ZoomPointTargetTime;
    private float zoomPercent;
    private boolean zoomingCamera;

    private int textureSwapStatus;

    private HashMap<Integer, Integer> bitmapIdTextureNameHashMap;

    private long FadeResetTime;
    private long FadeTargetTime;
    private int _bitmapSize;



    Date startDate;


    public SceneSetter(Context context)
    {
        startDate = new Date();
        this.context = context;
        resources = context.getResources();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        randomGenerator = new Random();
        spriteList = new ArrayList<>();
        focalPointStartingPoint = -1.0f;
        focalPointEndingPoint = -0.2f;

        rackingFocus = false;
        bitmapIdTextureNameHashMap = new HashMap<>();
        textureSwapStatus = STATUS_DONE;
    }


    public void InitSprites()
    {
        //todo: add sprite key generator
        spriteList.add(new Sprite(new BackgroundSprite(), 0));
        spriteList.add(new Sprite(new HouseSprite(), 1));
        spriteList.add(new Sprite(new RoomSprite(), 2));
        spriteList.add(new Sprite(new DeskSprite(), 3));
        spriteList.add(new Sprite(new BunnySprite(), 4));
        spriteList.add(new Sprite(new LampSprite(), 5));
    }

    public void InitTextures(int widthHeight)
    {
        _bitmapSize = Textures.GetBitmapSize(widthHeight);
        textures = new Textures(this.context, widthHeight);
        textures.InitTextures();
        int i = 0;
        for (Sprite sprite : spriteList)
        {
            int bitmapID = sprite.GetNextBitmapID(_bitmapSize);
            TextureData textureData = textures.AddTexture(bitmapID, i);
            bitmapIdTextureNameHashMap.put(bitmapID, textures.textureNames[i]);
            sprite.SetTextureData(textureData);
            i++;
        }
    }

    float[] mvpMatrix = new float[16];


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
//        GetMembers();
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


    public void InitTextureSwap()
    {
        bitmapIdTextureNameHashMap.clear();
        //decide which scene

        for (Sprite sprite : spriteList)
        {
            int bitmapID = sprite.GetNextBitmapID(_bitmapSize);

            if (bitmapID >= 0)
            { //valid bitmap
                //check if bitmap is already on list
                if (!bitmapIdTextureNameHashMap.containsKey(bitmapID))
                {
                    bitmapIdTextureNameHashMap.put(bitmapID, sprite.getTextureName());
                    sprite.SetTextureSwapRequired(true);
                }
            }
        }

        textureSwapStatus = STATUS_FADING_OUT;
        ResetFadePoint();


//        textureSwapStatus = STATUS_FADING_OUT;
//        ResetFadePoint();

//        textures.SwapTextures(bitmapIdTextureNameHashMap);


    }

    public void ResetTextureSwap()
    {
        if (textureSwapStatus == STATUS_LOADING_TEXTURES)
        {
            //don't do anything if we're loading int textures
        }
        bitmapIdTextureNameHashMap.clear();
        textureSwapStatus = STATUS_FADING_IN;
        ResetFadePoint();

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
            float fadeProgress = (float) (currentTime - FadeResetTime) / (FadeTargetTime - FadeResetTime);
//            if (textureSwapStatus == STATUS_FADING_OUT)
//            {
//                fadeProgress = MAX_PROGRESS - fadeProgress;
//            }
            //don't have alpha exceed max or min
            if (fadeProgress >= 1.0f)
            {
                fadeProgress = 1.0f;
            } else if (fadeProgress <= 0.0f)
            {
                fadeProgress = 0.0f;
            }

//            Log.d("alpha1", "fade progress:  " + fadeProgress);
            for (Sprite sprite : spriteList)
            {
                if (sprite.TextureSwapRequired())
                {
                    sprite.SetFade(fadeProgress, textureSwapStatus == STATUS_FADING_IN);
                }
            }

            //fade complete
            if (fadeProgress >= 1.0f || fadeProgress <= 0.0f)
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

        Log.d("fade", "fade status: " + textureSwapStatus);
    }

    private void ResetFadePoint()
    {
        FadeResetTime = System.currentTimeMillis();
        if (textureSwapStatus == STATUS_FADING_OUT)
        {
            FadeTargetTime = FadeResetTime + FADE_OUT_TRANSITION_DURATION;
        } else
        { //fading in
            FadeTargetTime = FadeResetTime + FADE_IN_TRANSITION_DURATION;
        }
    }


    public void SetToTargetFocalPoint()
    {
        Log.d("focal", "setting target focal point");
        rackingFocus = false;
        focalPoint = focalPointEndingPoint;
        for (Sprite sprite : spriteList)
        {
            sprite.SetFocalPoint(focalPointEndingPoint);
        }

    }

    public void SetMaxBlurAmount(int max)
    {
        float newMax = max;
        newMax *= 0.1f;
        for (Sprite sprite : spriteList)
        {
            sprite.SetTargetFocalPoint(newMax);
        }
    }

    public boolean RackingFocus()
    {
        return rackingFocus;
    }

    public void ResetFocalPoint()
    {
        FocalPointResetTime = System.currentTimeMillis();
        FocalPointTargetTime = FocalPointResetTime + FOCAL_POINT_RESET_DURATION;
        focalPoint = focalPointStartingPoint;
        for (Sprite sprite : spriteList)
        {
            sprite.SetFocalPoint(focalPoint);
        }
        rackingFocus = true;
    }

    public void UpdateFocalPoint()
    {
        long currentTime = System.currentTimeMillis();
        float focalPointProgression = (float) (currentTime - FocalPointResetTime) / (FocalPointTargetTime - FocalPointResetTime);
        float sinCurveProgression = (float) Math.sin(focalPointProgression * Math.PI / 2);
        focalPoint = sinCurveProgression * Math.abs(focalPointStartingPoint - focalPointEndingPoint) + focalPointStartingPoint;

        for (Sprite sprite : spriteList)
        {
            sprite.SetFocalPoint(focalPoint);
        }

        if (focalPointProgression >= 1.0f)
        {
            rackingFocus = false;
        }
    }

    public void TurnOffBlur()
    {
        FocalPointResetTime = FocalPointTargetTime = 0;
        rackingFocus = false;
        for (Sprite sprite : spriteList)
        {
            sprite.turnOffBlur();
        }
    }

    public void SetToMaxZoomPercent()
    {
        zoomingCamera = false;
        zoomPercent = MAX_PROGRESS;
        Log.d("zoom", "target zoom : " + zoomPercent);
        for (Sprite sprite : spriteList)
        {
            sprite.SetZoomPoint(zoomPercent);
        }
    }

    public boolean ZoomingCamera()
    {
        return zoomingCamera;
    }

    public void ResetZoomPercent()
    {
        ZoomPointResetTime = System.currentTimeMillis();
        ZoomPointTargetTime = ZoomPointResetTime + FOCAL_POINT_RESET_DURATION;
        zoomingCamera = true;
    }

    public void UpdateZoomPoint()
    {
        long currentTime = System.currentTimeMillis();
        float zoomProgressionPercent = (float) (currentTime - ZoomPointResetTime) / (ZoomPointTargetTime - ZoomPointResetTime);

        for (Sprite sprite : spriteList)
        {
            sprite.SetZoomPoint(zoomProgressionPercent);
        }

        if (zoomProgressionPercent >= 1.0f)
        {
            zoomingCamera = false;
        }
    }





    public void DrawSprites(float[] mtrxView, float[] mtrxProjection, float[] mModelMatrix)
    {
        for (Sprite sprite : spriteList)
        {
            sprite.draw(mtrxView, mtrxProjection, mModelMatrix, mvpMatrix);
        }
    }


    public void SetSpriteMembers(int colorHandle, int positionHandle, int texCoordLoc, int mtrxHandle, int samplerLoc, int biasHandle)
    {
        for (Sprite sprite : spriteList)
        {
            sprite.SetSpriteMembers(colorHandle, positionHandle, texCoordLoc, mtrxHandle, samplerLoc, biasHandle);
        }
    }


}
