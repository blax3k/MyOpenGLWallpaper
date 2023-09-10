package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.SceneData.GirlSittingScene;
import com.hashimapp.myopenglwallpaper.SceneData.RainParticle;

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
    private float motionOffsetPivotPoint = 0.1f;


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

    private boolean particlesEnabled;

    private int textureSwapStatus;

    private HashMap<Integer, Integer> bitmapIdTextureNameHashMap;
    private HashMap<Integer, float[]> bitmapTextureVerticesHashMap;

    private long FadeResetTime;
    private long FadeTargetTime;
    private int currentScene;

    private boolean _textureSwapRequired;

    private GLParticleRenderer particleRenderer;

    private WallpaperResourceReader wallpaperResourceReader;

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
        bitmapIdTextureNameHashMap = new HashMap<>();
        bitmapTextureVerticesHashMap = new HashMap<>();
        textureSwapStatus = STATUS_DONE;
        particleRenderer = new GLParticleRenderer(new RainParticle());
        wallpaperResourceReader = new WallpaperResourceReader(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void OnSurfaceCreated()
    {

        //todo: add sprite key generator
        SceneData sceneData = new GirlSittingScene();
        for(int i = 0; i < sceneData.SpriteDataList.size(); i ++){
            spriteList.add(new Sprite(sceneData.SpriteDataList.get(i), i, currentScene));
        }

//        SceneData sd = new SceneData();
//        sd.SceneKey = "tempKey";
////        sd.SpriteDataList = spriteList.stream().map((s) -> s.spriteData).collect(Collectors.toCollection(ArrayList::new));
////        wallpaperResourceReader.SaveSceneData(sd);
//        SceneData testSceneData = wallpaperResourceReader.LoadSceneData(sd.SceneKey);
//
//        for(int i = 0; i < testSceneData.SpriteDataList.size(); i++)
//        {
//            spriteList.add(new Sprite(testSceneData.SpriteDataList.get(i), i, currentScene));
//        }

        for (Sprite sprite : spriteList)
        {
            sprite.SetMotionOffsetPivotPoint(motionOffsetPivotPoint);
        }

        particleRenderer.onSurfaceCreated(currentScene);
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
        for (Sprite sprite : spriteList)
        {
            sprite.SetTime(timePhase, percentage);
        }
        particleRenderer.SetTime(timePhase, percentage);
    }

    public void InitScene(int scene, int timePhase, int percentage, int weather, int widthHeight)
    {
//        QueueScene(scene, timePhase, percentage, weather);

        for (Sprite sprite : spriteList)
        {
            sprite.QueueSceneData(scene, timePhase, percentage, weather);
        }

        textures = new Textures(this.context, widthHeight);
        int i = 0;
        for (Sprite sprite : spriteList)
        {
            int bitmapID = sprite.GetQueuedBitmapID();
            TextureData textureData = textures.AddTexture(bitmapID, i);
            bitmapIdTextureNameHashMap.put(bitmapID, textures.textureNames[i]);
            sprite.SetTextureData(textureData);
            sprite.TextureVerticeChange();
            sprite.SetNextTextureVertices();

            i++;
        }

        int bitmapID = particleRenderer.GetQueuedBitmapID();
        TextureData textureData = textures.AddTexture(bitmapID, i);
        bitmapIdTextureNameHashMap.put(bitmapID, textures.textureNames[i]);
        particleRenderer.SetTextureData(textureData);

        for (Sprite sprite : spriteList)
        {
            sprite.DequeueSceneData();
        }

    }

    public int QueueScene(int scene, int timePhase, int percentage, int weather)
    {
        int highestTransition = SceneSetter.NO_TRANSITION;

        currentScene = scene;
        for (Sprite sprite : spriteList)
        {
            int result = sprite.QueueSceneData(scene, timePhase, percentage, weather);
            if (result > highestTransition)
            {
                highestTransition = result;
            }
        }

        return highestTransition;
    }


    public void OffsetChanged(float xOffset)
    {
        for (Sprite sprite : spriteList)
        {
            sprite.SetXOffset(xOffset);
        }
        particleRenderer.SetXOffset(xOffset);
    }

    public void SensorChanged(float xOffset, float yOffset, boolean invert)
    {
        for (Sprite sprite : spriteList)
        {
            sprite.SensorChanged(xOffset, yOffset, invert);
        }
    }

    public void SurfaceChanged(boolean portrait, float spriteXPosOffset, float touchScale, float motionScale,
                               int width, int height)
    {
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
        bitmapIdTextureNameHashMap.clear();
        bitmapTextureVerticesHashMap.clear();

        if (transition == SceneSetter.INSTANT_TRANSITION)
        {
            for (Sprite sprite : spriteList)
            {
                sprite.DequeueSceneData();
            }
        } else if (transition == SceneSetter.PARTIAL_FADE_TRANSITION ||
                transition == SceneSetter.FULL_FADE_TRANSITION)
        {
            for (Sprite sprite : spriteList)
            {
                sprite.DequeueColor();
            }
            InitTextureSwap();
        }
    }

    public void InitTextureSwap()
    {
        //get textures
        bitmapIdTextureNameHashMap.clear();
        bitmapTextureVerticesHashMap.clear();
        boolean FadeAll = false;
        //decide which scene

        for (Sprite sprite : spriteList)
        {
            int bitmapID = sprite.GetQueuedBitmapID();

            if (bitmapID >= 0)
            { //valid bitmap
                //check if bitmap is already on list
                if (!bitmapIdTextureNameHashMap.containsKey(bitmapID))
                {
                    _textureSwapRequired = true;
                    bitmapIdTextureNameHashMap.put(bitmapID, sprite.getTextureName());
                    sprite.SetFadeOutRequired(true);
                    if (sprite.IsEssentialLayer())
                    {
                        FadeAll = true;
                    }
                }
            }

            if (sprite.TextureVerticeChange())
            {
                sprite.SetFadeOutRequired(true);
                if (sprite.IsEssentialLayer())
                {
                    FadeAll = true;
                }
            }
        }
        if (FadeAll)
        {
            for (Sprite sprite : spriteList)
            {
                sprite.SetFadeOutRequired(true);
            }
        }

        textureSwapStatus = STATUS_FADING_OUT;
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
                if (sprite.isFadeOutRequired())
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
                    for (Sprite sprite : spriteList)
                    {
                        sprite.SetFadeOutRequired(false);
                    }
                    textureSwapStatus = STATUS_DONE;
                }
            }
        } else if (textureSwapStatus == STATUS_READY_TO_SWAP)
        {
            for (Sprite sprite : spriteList)
            {
                sprite.DequeueSceneData();
            }
            if (_textureSwapRequired)
            {
                new Thread(() -> textures.LoadTextures(bitmapIdTextureNameHashMap)).start();
                textureSwapStatus = STATUS_LOADING_TEXTURES;
            } else
            {
                textureSwapStatus = STATUS_FADING_IN;
                ResetFadePoint();
            }
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
        Log.d("focal", "focal point: " + focalPoint);

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


    public void DrawSprites(float[] mtrxView, float[] mtrxProjection, float[] mModelMatrix)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        GLES20.glUseProgram(riGraphicTools.sp_Image);
        for (Sprite sprite : spriteList)
        {
            sprite.draw(mtrxView, mtrxProjection, mModelMatrix, mvpMatrix);
        }
        if (particlesEnabled)
        {
            particleRenderer.onDrawFrame(null, mtrxView, mtrxProjection, mModelMatrix, mvpMatrix);

        }

    }


    public void SetSpriteMembers(int colorHandle, int positionHandle, int texCoordLoc, int mtrxHandle, int samplerLoc, int biasHandle, int alphaHandle)
    {
        for (Sprite sprite : spriteList)
        {
            sprite.SetSpriteMembers(colorHandle, positionHandle, texCoordLoc, mtrxHandle, samplerLoc, biasHandle, alphaHandle);
        }
    }


}
