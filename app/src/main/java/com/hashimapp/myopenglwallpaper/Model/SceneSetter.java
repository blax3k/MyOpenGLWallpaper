package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Matrix;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.SceneData.DataHolder;
import com.hashimapp.myopenglwallpaper.SceneData.SpriteOne;
import com.hashimapp.myopenglwallpaper.SceneData.TestImage;
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

    private int blurSetting;
    Random randomGenerator;
    private int girlTextureChoice;


    SharedPreferences preferences;
    Context context;
    TimeTracker timeTracker;
    List<Sprite> spriteList;


    public SceneSetter()
    {
        context = OpenGLES2WallpaperService.getAppContext();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        randomGenerator = new Random();
        shuffleScene();
        timeTracker = new TimeTracker();
        spriteList = new ArrayList<>();
        initSprites();
    }

    public void initSprites(){
        spriteList.add(new Sprite(new SpriteOne()));
        spriteList.add(new Sprite(new TestImage()));

        for(Sprite sprite : spriteList){
            sprite.SetTexture(context);
        }

    }

    public void DrawSprites(FloatBuffer uvBuffer, float[] mtrxView, float[] mtrxProjection, float[] mModelMatrix){
        for(Sprite sprite : spriteList){
            sprite.draw(uvBuffer, mtrxView, mtrxProjection, mModelMatrix);
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

    public void SetOrientation(boolean portrait){
        for(Sprite sprite : spriteList){
            sprite.SetOrientation(portrait);
        }
    }


    public boolean shuffleScene()
    {
        String choice = preferences.getString("texture_model", "3");
        if (choice.equals("0"))
        {
            shuffleGirl();
            return true;
        }
        return false;
    }


    private void shuffleGirl()
    {
        Log.d("Scene Setter", "girl was shuffled. chose: " + girlTextureChoice);
        int choice = randomGenerator.nextInt(5);
        if (choice == girlTextureChoice)
        {
            if (choice == 5)
                choice = 5 - randomGenerator.nextInt(3) + 1;
            else
                choice++;
        }
        girlTextureChoice = choice;
        Log.d("Scene Setter", "girl was shuffled. chose: " + girlTextureChoice);
    }
}
