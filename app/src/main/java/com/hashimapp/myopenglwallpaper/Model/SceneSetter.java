package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.SceneData.BackgroundSprite;
import com.hashimapp.myopenglwallpaper.SceneData.GirlSprite;
import com.hashimapp.myopenglwallpaper.SceneData.TemplateSprite;
import com.hashimapp.myopenglwallpaper.View.OpenGLES2WallpaperService;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Blake on 9/19/2015.
 */
public class SceneSetter
{

    public final static int AUTOMATIC = 30;
    public final static int DAWN = 31;
    public final static int DAY = 32;
    public final static int SUNSET = 33;
    public final static int NIGHT = 34;
    public final static int FOUR_EIGHTY_P = 34;

    Textures textures;
    Random randomGenerator;
    private int girlTextureChoice;

    SharedPreferences preferences;
    Context context;
    TimeTracker timeTracker;
    List<Sprite> spriteList;
    ;
    private int mColorHandle;
    private int mPositionHandle;
    private int mTexCoordLoc;
    private int mtrxHandle;
    private int mSamplerLoc;


    public SceneSetter()
    {
        context = OpenGLES2WallpaperService.getAppContext();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        randomGenerator = new Random();
        timeTracker = new TimeTracker();
        spriteList = new ArrayList<>();
        textures = new Textures(this.context);
        textures.InitTextures();
        initSprites();
    }


    public void initSprites(){
        int timeOfDay = timeTracker.getDayHour();
        spriteList.add(new Sprite(new BackgroundSprite(), timeOfDay));
        spriteList.add(new Sprite(new GirlSprite(), timeOfDay));
        spriteList.add(new Sprite(new TemplateSprite(), timeOfDay));
        //enable transparency
//
//        for(Sprite sprite : spriteList){
//            sprite.SetTexture(context);
//        }

    }

    float[] scratch = new float[16];

    public void DrawSprites(float[] mtrxView, float[] mtrxProjection, float[] mModelMatrix){
//        GLES20.glEnable(GLES20.GL_BLEND);
//        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        for(Sprite sprite : spriteList){
            sprite.draw(mtrxView, mtrxProjection, mModelMatrix, mColorHandle,
                        mPositionHandle, mTexCoordLoc, mtrxHandle, mSamplerLoc, scratch);
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

    }

}
