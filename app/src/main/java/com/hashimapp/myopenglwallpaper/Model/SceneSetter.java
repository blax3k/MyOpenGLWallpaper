package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.Model.DataStorage.ColorData;
import com.hashimapp.myopenglwallpaper.Model.DataStorage.ResourceReader;
import com.hashimapp.myopenglwallpaper.Model.DataStorage.SpriteData;
import com.hashimapp.myopenglwallpaper.SceneData.RainParticle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    public static final int NO_TRANSITION = 0;
    public static final int INSTANT_TRANSITION = 1;
    public static final int PARTIAL_FADE_TRANSITION = 2;
    public static final int FULL_FADE_TRANSITION = 3;

    private static final int FADE_OUT_TRANSITION_DURATION = 500;
    private static final int FADE_IN_TRANSITION_DURATION = 2000;
    private static final int FOCAL_POINT_RESET_DURATION = 1500;
    private static float MIN_PROGRESS = 0.0f;
    private static float MAX_PROGRESS = 1.0f;
    private static float LANDSCAPE_Y_OFFSET_ADJUST = 0.8f;
    private float motionOffsetPivotPoint = 0.0f;


    Textures textures;
    Random randomGenerator;

    SharedPreferences preferences;
    Context context;
    List<Sprite> spriteList;
    private boolean spriteListLocked;
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

    private boolean particlesEnabled;

    private int TextureSwapStatus;

    private long FadeResetTime;
    private long FadeTargetTime;

    private boolean _textureSwapRequired;

    private GLParticleRenderer particleRenderer;
    private ResourceReader resourceReader;

    Date startDate;


    public SceneSetter(Context context)
    {
        startDate = new Date();
        this.context = context;
        resources = context.getResources();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        randomGenerator = new Random();
        spriteList = new ArrayList<>();
        focalPointStartingPoint = 1.0f;
        focalPointEndingPoint = 0.3f;

        rackingFocus = false;
        TextureSwapStatus = STATUS_DONE;
        particleRenderer = new GLParticleRenderer(new RainParticle());
        resourceReader = new ResourceReader(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void OnSurfaceCreated()
    {
        //todo: add sprite key generator

        particleRenderer.onSurfaceCreated();
        InitSpriteProgram();
    }


    private void InitSpriteProgram()
    {
        riGraphicTools.sp_Image = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Image);
        int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Image);

        GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(riGraphicTools.sp_Image);                  // creates OpenGL ES program executrees

        // Set our shader program
        GLES20.glUseProgram(riGraphicTools.sp_Image);
//        GLES20.glDepthMask(true);
//        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//        GLES20.glDepthFunc(GLES20.GL_ALWAYS);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        // get handle to vertex shader's vPosition member
        int mColorHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_Color");
        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "vPosition");
        int mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_texCoord");
        // Get handle to shape's transformation matrix and apply it
        int mtrxHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");
        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "s_texture");
        int biasHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "bias");
        int alphaHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "alpha");

        SetSpriteMembers(mColorHandle, mPositionHandle, mTexCoordLoc, mtrxHandle, mSamplerLoc, biasHandle, alphaHandle);
    }

    float[] mvpMatrix = new float[16];


    public void SetTimeOfDay(int timePhase, int percentage)
    {
        spriteListLocked = true;
        for (Sprite sprite : spriteList)
        {
            sprite.SetTime(timePhase, percentage);
        }
        particleRenderer.SetTime(timePhase, percentage);
        spriteListLocked = false;
    }

    /*
    Load in textures and Queue
     */
    public void InitScene( int timePhase, int percentage, int weather, int widthHeight)
    {
        if(spriteList.isEmpty())
        {
            return;
        }

        for (Sprite sprite : spriteList)
        {
            sprite.QueueSceneData(null, timePhase, percentage, weather);
        }

        textures = new Textures(this.context, widthHeight);
        ArrayList<String> bitmapIDList = new ArrayList<>();
        for (Sprite sprite : spriteList)
        {
            String bitmapID = sprite.spriteData.BitmapName;
            bitmapIDList.add(bitmapID);
        }

        String bitmapName = particleRenderer.GetQueuedBitmapID();
        bitmapIDList.add(bitmapName);

        textures.QueueTextures(bitmapIDList);
        textures.DequeTextures();

        AssignTexturesToSprites();

        for (Sprite sprite : spriteList)
        {
            sprite.DequeueSceneData();
        }
    }

    public int QueueScene(String scene, int timePhase, int percentage, int weather)
    {
        Log.d("ConcurrentModification", "queueScene Start");
        spriteListLocked = true;
        int highestTransition = SceneSetter.NO_TRANSITION;
        ColorData colorData = resourceReader.GetColorData("");
        ArrayList<SpriteData> newSpriteDataList = resourceReader.GetSpriteDataList(scene, colorData);

        //keep track of the untouched sprites from both lists
        ArrayList<Integer> untouchedNewSpriteIndexes = new ArrayList<>();
        ArrayList<Integer> untouchedOldSpriteIndexes = new ArrayList<>();
        for(int i = 0; i < spriteList.size(); i++){
            untouchedOldSpriteIndexes.add(i);
        }

        for(int i = 0; i < newSpriteDataList.size(); i++)
        {
            boolean spriteUnused = true;
            for(int j = 0; j < untouchedOldSpriteIndexes.size(); j++)
            {
                int indexJ = untouchedOldSpriteIndexes.get(j);
                Log.d("ConcurrentModification", "get " + indexJ + "from sprite list");
                if(spriteList.get(indexJ).spriteData.SpriteName.equals(newSpriteDataList.get(i).SpriteName))
                {
                    //we've found a matching sprite name. insert the new sprite data there
                    Log.d("ConcurrentModification", "queue at index " + i + "from sprite list");
                    int result = spriteList.get(indexJ).QueueSceneData(newSpriteDataList.get(i), timePhase, percentage, weather);
                    untouchedOldSpriteIndexes.remove(j);
                    spriteUnused = false;
                    if (result > highestTransition)
                    {
                        highestTransition = result;
                    }
                    break;
                }
            }
            if(spriteUnused)
            {
                untouchedNewSpriteIndexes.add(i);
            }
        }

        //insert the unmatched sprites into the 'old' sprite slots if there are any
        //add a new slot if necessary
        for(int i = 0; i < untouchedNewSpriteIndexes.size(); i++)
        {
            int nIndex = untouchedNewSpriteIndexes.get(i);
            SpriteData newSpriteData = newSpriteDataList.get(nIndex);
            if (untouchedOldSpriteIndexes.size() > 0)
            {
                int oIndex = untouchedOldSpriteIndexes.remove(0);
                Log.d("ConcurrentModification", "queue at index " + oIndex + "from sprite list");
                int result = spriteList.get(oIndex).QueueSceneData(newSpriteData, timePhase, percentage, weather);
                if (result > highestTransition)
                {
                    highestTransition = result;
                }
            } else
            {

                Log.d("ConcurrentModification", "Add to Sprite List");
                spriteList.add(new Sprite(newSpriteData));
            }
        }

        //get rid of extraneous sprites
        for (int i = 0; i < untouchedOldSpriteIndexes.size(); i++)
        {
//            spriteList.remove(untouchedOldSpriteIndexes.get(i));
        }
        Collections.sort(spriteList, (s1, s2) -> Float.compare(s2.spriteData.ZVertice(), s1.spriteData.ZVertice()));

        for (Sprite sprite : spriteList)
        {
            sprite.SetMotionOffsetPivotPoint(motionOffsetPivotPoint);
        }
        Log.d("ConcurrentModification", "QueueScene End");

        spriteListLocked = false;

        return highestTransition;
    }

    public void SurfaceChanged(boolean portrait, float spriteXPosOffset, float touchScale, float motionScale,
                               int width, int height)
    {
        Log.d("ConcurrentModification", "SurfaceChanged Start");
//        GetMembers();
//        GLES20.glEnableVertexAttribArray(mColorHandle);
//        GLES20.glEnableVertexAttribArray(mPositionHandle);
//        GLES20.glEnableVertexAttribArray(mTexCoordLoc);
        // check if textures need to be swapped
        // Get textures and corresponding sprites that need to be swapped
        // tell textures class which textures to load
        Log.d("offset", "sprite x pos offset: " + spriteXPosOffset);

        for (Sprite sprite : spriteList)
        {
            sprite.SetOrientation(spriteXPosOffset, touchScale, motionScale);
            sprite.SetMotionOffsetPivotPoint(motionOffsetPivotPoint);
            if (portrait)
            {
                sprite.SetYOffset(0);
            } else
            {
                sprite.SetYOffset(LANDSCAPE_Y_OFFSET_ADJUST);
            }
        }
        particleRenderer.onSurfaceChanged(null, width, height);
        Log.d("ConcurrentModification", "SurfaceChanged End");

    }

    public void SetMotionsSale(float motionScale)
    {
        for (Sprite sprite : spriteList)
        {
            sprite.SetMotionScale(motionScale);
        }
    }

    public void InitSceneChange(int transition)
    {
        //get textures
//        bitmapIdTextureNameHashMap.clear();
//        bitmapTextureVerticesHashMap.clear();

        Log.d("ConcurrentModification", "InitSceneChange Start");
        if (transition == SceneSetter.INSTANT_TRANSITION)
        {
            for (Sprite sprite : spriteList)
            {
                sprite.DequeueSceneData();
            }
        } else if (transition == SceneSetter.PARTIAL_FADE_TRANSITION ||
                transition == SceneSetter.FULL_FADE_TRANSITION)
        {
            InitTextureSwap();
        }
        Log.d("ConcurrentModification", "InitSceneChange End");
    }

    public void InitTextureSwap()
    {
        Log.d("ConcurrentModification", "InitTextureSwapStart");
        //get textures
        boolean fadeAll = false;
        //decide which scene

        ArrayList<String> bitmapNameList = new ArrayList<>();

        bitmapNameList.add(particleRenderer.GetQueuedBitmapID());

        for (Sprite sprite : spriteList)
        {
            String bitmapName = sprite.GetQueuedBitmapID();
            bitmapNameList.add(bitmapName);

            if (bitmapName != "")
            { //valid bitmap
                //check if bitmap is already on list
                if (textures.GetTextureData(bitmapName) == null)
                {
                    _textureSwapRequired = true;
                    sprite.SetFadeOutRequired(true);
                    if (sprite.IsEssentialLayer())
                    {
                        fadeAll = true;
                    }
                }
            }

            if (sprite.TextureVerticeChange())
            {
                sprite.SetFadeOutRequired(true);
                if (sprite.IsEssentialLayer())
                {
                    fadeAll = true;
                }
            }
        }
        if (fadeAll)
        {
            for (Sprite sprite : spriteList)
            {
                sprite.SetFadeOutRequired(true);
            }
        }
        new Thread(() -> textures.QueueTextures(bitmapNameList)).start();

        ResetFadePoint(STATUS_FADING_OUT);

        Log.d("ConcurrentModification", "InitTextureSwapEnd");
    }


    public Boolean TransitioningScene()
    {
        return TextureSwapStatus != STATUS_DONE;
    }

    ///always called from the main thread
    public void UpdateFade()
    {
        if (TextureSwapStatus == STATUS_FADING_IN || TextureSwapStatus == STATUS_FADING_OUT)
        {
            long currentTime = System.currentTimeMillis();

            //get current progress for fade
            float fadeProgress = (float) (currentTime - FadeResetTime) / (FadeTargetTime - FadeResetTime);

            //don't have alpha exceed max or min
            if (fadeProgress >= 1.0f)
            {
                fadeProgress = 1.0f;
            } else if (fadeProgress <= 0.0f)
            {
                fadeProgress = 0.0f;
            }

            for (Sprite sprite : spriteList)
            {
                if (sprite.IsFadeOutRequired())
                {
                    sprite.SetFade(fadeProgress, TextureSwapStatus == STATUS_FADING_IN);
                }
            }

            //fade complete
            if (fadeProgress >= 1.0f || fadeProgress <= 0.0f)
            {
                if (TextureSwapStatus == STATUS_FADING_OUT)
                { //finished fading out. load textures
                    TextureSwapStatus = STATUS_READY_TO_SWAP;
                } else
                {  //finshed fading in. stop
                    for (Sprite sprite : spriteList)
                    {
                        sprite.SetFadeOutRequired(false);
                    }
                    TextureSwapStatus = STATUS_DONE;
                }
            }
        } else if (TextureSwapStatus == STATUS_READY_TO_SWAP)
        {
            for (Sprite sprite : spriteList)
            {
                sprite.DequeueSceneData();
            }
            if (_textureSwapRequired)
            {
                textures.DequeTextures();
                TextureSwapStatus = STATUS_LOADING_TEXTURES;
            } else
            {
                ResetFadePoint(STATUS_FADING_IN);
            }
        } else if (TextureSwapStatus == STATUS_LOADING_TEXTURES)
        {
            if (textures.UploadComplete())
            {//loaded. fade textures back in
                ResetFadePoint(STATUS_FADING_IN);
                AssignTexturesToSprites();
            }
        }
    }

    private void AssignTexturesToSprites()
    {
        for(Sprite sprite : spriteList)
        {
            TextureData data = textures.GetTextureData(sprite.spriteData.BitmapName);
            if(data != null)
            {
                sprite.SetTextureData(data);
            }
        }
        Collections.sort(spriteList, (s1, s2) -> Float.compare(s2.spriteData.ZVertice(), s1.spriteData.ZVertice()));
    }
    private void ResetFadePoint(int swapStatus)
    {
        TextureSwapStatus = swapStatus;
        FadeResetTime = System.currentTimeMillis();
        if (TextureSwapStatus == STATUS_FADING_OUT)
        {
            FadeTargetTime = FadeResetTime + FADE_OUT_TRANSITION_DURATION;
        } else
        { //fading in
            FadeTargetTime = FadeResetTime + FADE_IN_TRANSITION_DURATION;
        }
    }

    public void SetToTargetFocalPoint()
    {
        rackingFocus = false;
        focalPoint = focalPointEndingPoint;
        for (Sprite sprite : spriteList)
        {
            sprite.SetFocalPoint(focalPointEndingPoint);
        }
        particleRenderer.SetFocalPoint(focalPointEndingPoint);

    }

    public void SetMaxBlurAmount(int max)
    {
        float newMax = max;
        newMax *= 0.12f;
        focalPoint = focalPointEndingPoint;
        for (Sprite sprite : spriteList)
        {
            sprite.SetTargetFocalPoint(newMax);
            sprite.SetFocalPoint(focalPointEndingPoint);
        }
        particleRenderer.SetTargetFocalPoint(newMax);
        particleRenderer.SetFocalPoint(focalPointEndingPoint);
    }

    public void TurnOffBlur()
    {
        FocalPointResetTime = FocalPointTargetTime = 0;
        rackingFocus = false;
        for (Sprite sprite : spriteList)
        {
            sprite.SetDefaultFocalPoint();
        }
        particleRenderer.SetDefaultFocalPoint();
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
        particleRenderer.SetFocalPoint(focalPoint);
        rackingFocus = true;
    }

    public void UpdateFocalPoint()
    {
        long currentTime = System.currentTimeMillis();
        float focalPointProgression = (float) (currentTime - FocalPointResetTime) / (FocalPointTargetTime - FocalPointResetTime);
        float sinCurveProgression = (float) Math.sin(focalPointProgression * Math.PI / 2);
        focalPoint = sinCurveProgression * (focalPointEndingPoint - focalPointStartingPoint) + focalPointStartingPoint;

        for (Sprite sprite : spriteList)
        {
            sprite.SetFocalPoint(focalPoint);
        }
        particleRenderer.SetFocalPoint(focalPoint);

        if (focalPointProgression >= 1.0f)
        {
            rackingFocus = false;
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

    public void SetParticlesEnabled(boolean enabled)
    {
        particlesEnabled = enabled;
    }


    public void DrawSprites(GLCamera camera)
    {
        Log.d("ConcurrentModification", "spriteList Size = " + spriteList.size());
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        GLES20.glUseProgram(riGraphicTools.sp_Image);
        if(!spriteListLocked)
        {
            for (Sprite sprite : spriteList)
            {
                sprite.SetXOffset(camera.GetXOffset());
                sprite.SetSensorData(camera.GetSensorData());
                sprite.draw(camera.mtrxView, camera.mtrxProjection, camera.mModelMatrix, mvpMatrix);
            }
        }
        if (particlesEnabled)
        {
            particleRenderer.SetXOffset(camera.GetXOffset());
            particleRenderer.onDrawFrame(null, camera.mtrxView, camera.mtrxProjection, camera.mModelMatrix, mvpMatrix);
        }
        Log.d("ConcurrentModification", "DrawSprites End");

    }


    public void SetSpriteMembers(int colorHandle, int positionHandle, int texCoordLoc, int mtrxHandle, int samplerLoc, int biasHandle, int alphaHandle)
    {
        for (Sprite sprite : spriteList)
        {
            sprite.SetSpriteMembers(colorHandle, positionHandle, texCoordLoc, mtrxHandle, samplerLoc, biasHandle, alphaHandle);
        }
    }

}
