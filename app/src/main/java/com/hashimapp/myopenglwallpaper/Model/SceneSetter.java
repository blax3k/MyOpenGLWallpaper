package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.R;
import com.hashimapp.myopenglwallpaper.SceneData.DataHolder;
import com.hashimapp.myopenglwallpaper.View.OpenGLES2WallpaperService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Blake on 9/19/2015.
 */
public class SceneSetter
{
    public final static int SKY = 000;
    public final static int BLD3 = 001;
    public final static int BLD1 = 002;
    public final static int BLD0 = 003;
    public final static int BLD2 = 004;

    public final static int BLUR_NONE = 20;
    public final static int BLUR_LOW = 21;
    public final static int BLUR_HIGH = 22;

    public final static int AUTOMATIC = 30;
    public final static int DAWN = 31;
    public final static int DAY = 32;
    public final static int SUNSET = 33;
    public final static int NIGHT = 34;
    public final static int FOUR_EIGHTY_P = 34;

    private int blurSetting;
    Random randomGenerator;
    private int girlTextureChoice;
    private String girlTextureArray[] = {"1", "2", "3", "4", "5", "6"};


    private int textureIndexTable, textureIndexCity, textureIndexBuilding, textureIndexSky;


    DataHolder dataHolder = new DataHolder();
    SharedPreferences preferences;
    Context context;
    private float[] currentGirlVertices;
    private float girlOffset;
    TimeTracker timeTracker;
    private int lastDayHour;

    List<Sprite> spriteList;

    public SceneSetter()
    {
        context = OpenGLES2WallpaperService.getAppContext();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        randomGenerator = new Random();
        shuffleScene();
        timeTracker = new TimeTracker();
    }

    public void initSprites(){


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

    public float[] getSpriteVertices(int sprite)
    {
        switch(sprite){
            case BLD0:
                return dataHolder.bld0Vertices;
            case BLD1:
                return dataHolder.bld1Vertices;
            case BLD2:
                return dataHolder.bld2Vertices;
            case BLD3:
                return  dataHolder.bld3Vertices;
            case SKY:
                return dataHolder.skyVertices;
        }
        return null;
    }


    public float[] getSpriteColor(int sprite)
    {
        int choice = Integer.parseInt(preferences.getString("set_time", "30"));
        if (choice == AUTOMATIC)
        {
            choice = timeTracker.getDayHour();
            lastDayHour = choice;
            Log.d("Scene Setter: ", "color chosen from automatic: " + choice);
        }

        switch(sprite){
            case SKY:
                switch(choice){
                    case DAY:
                        return dataHolder.normalColor;
                    case SUNSET:
                        return dataHolder.sunsetColor;
                    case NIGHT:
                        return dataHolder.nightColor;
                    case DAWN:
                        return dataHolder.dawnColor;
                }
                break;
            case BLD0:
                switch(choice){
                    case DAY:
                        return dataHolder.normalColor;
                    case SUNSET:
                        return dataHolder.sunsetColor;
                    case NIGHT:
                        return dataHolder.nightColor;
                    case DAWN:
                        return dataHolder.dawnColor;
                }
                break;
            case BLD3:
                switch(choice){
                    case DAY:
                        return dataHolder.normalColor;
                    case SUNSET:
                        return dataHolder.sunsetColor;
                    case NIGHT:
                        return dataHolder.nightColor;
                    case DAWN:
                        return dataHolder.dawnColor;
                }
                break;
            case BLD1:
                switch(choice){
                    case DAY:
                        return dataHolder.normalColor;
                    case SUNSET:
                        return dataHolder.sunsetColor;
                    case NIGHT:
                        return dataHolder.nightColor;
                    case DAWN:
                        return dataHolder.dawnColor;
                }
                break;
            case BLD2:
                switch(choice){
                    case DAY:
                        return dataHolder.normalColor;
                    case SUNSET:
                        return dataHolder.sunsetColor;
                    case NIGHT:
                        return dataHolder.nightColor;
                    case DAWN:
                        return dataHolder.dawnColor;
                }
                break;


        }
        return dataHolder.normalColor;
//        }
    }

    public boolean needsColorRefresh()
    {
        int currentTime = timeTracker.getDayHour();
        if (currentTime != lastDayHour)
            return true;
        return false;
    }


    public void setOpacity(float newOpacity)
    {
        float temp = newOpacity;
        if (temp > 1.0f)
            temp = 1.0f;
        if (temp < 0.0f)
            temp = 0.0f;
        dataHolder.setOpacity(temp);
    }

    public float getOpacity()
    {
        return dataHolder.getOpacity();
    }

    public void setBlur(String blurString)
    {
        Log.d("Scene Setter", "blur string: " + blurString);
        if (blurString.equals("none"))
        {
            blurSetting = SceneSetter.BLUR_NONE;
            Log.d("Scene Setter", "no blur chosen");
        } else if (blurString.equals("low"))
        {
            blurSetting = SceneSetter.BLUR_LOW;
            Log.d("Scene Setter", "low blur chosen");
        } else if (blurString.equals("high"))
        {
            blurSetting = SceneSetter.BLUR_HIGH;
            Log.d("Scene Setter", "high blur chosen");
        }
    }

    public Bitmap getTexture(int sprite, int size, int blur)
    {
        Bitmap bmp = null; //BitmapFactory.decodeResource(context.getResources(), R.drawable.girlmidsword);

        switch (sprite)
        {
            case SKY:
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.wp2_sky);
                break;
            case BLD3:
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.wp2_bld3);
                break;
            case BLD2:
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.wp2_bld2);
                break;
            case BLD1:
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.wp2_bld1);
                break;
            case BLD0:
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.wp2_bld0);
                break;
        }
        return bmp;
    }

    public List<Sprite> GetSpriteList(){

        return new ArrayList<>();
    }

    public int getTextureIndex(int texture)
    {
        if (texture == BLD0)
        {
            return 0;
        } else if (texture == BLD1)
        {
            if (preferences.getString("camera_blur", "none").equals("none"))
            {
                return 1;
            }
            return 12;
        } else if (texture == BLD3)
        {
            if (preferences.getString("camera_blur", "none").equals("none"))
            {
                return 3;
            }
            return 13;
        } else if (texture == BLD2)
        {
            if (preferences.getString("camera_blur", "none").equals("none"))
            {
                return 2;
            }
            return 18;
        } else if (texture == SKY)
        {
            if (preferences.getString("camera_blur", "none").equals("none"))
            {
                return 4;
            }
            return 14;

        }
        return 0;
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
