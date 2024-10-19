package com.hashimapp.myopenglwallpaper.SceneData;

import com.hashimapp.myopenglwallpaper.Model.BlurBuilder;
import com.hashimapp.myopenglwallpaper.Model.SceneData;
import com.hashimapp.myopenglwallpaper.Model.TimeTracker;

import java.util.Random;

public class SceneManager
{
    public static final int DEFAULT = 0;
    public static final int BROWN = 1;
    public static final int BLUE = 2;
    public static final int GREEN = 3;
    public static final int PINK = 4;
    public static final int YELLOW = 5;

    public static final String DEFAULT_TITLE = "Default";
    public static final String BROWN_TITLE = "Brown";
    public static final String BLUE_TITLE = "Blue";
    public static final String GREEN_TITLE = "Green";
    public static final String PINK_TITLE = "Pink";
    public static final String YELLOW_TITLE = "Yellow";

    public static final int GIRL_SITTING = 0;
    public static final int GIRL_STANDING = 1;
    public static final int GIRL_BACK = 2;
    public static final int GIRL_SQUATTING = 3;
    public static final int GIRL_PICNIC = 4;
    public static final int GIRL_STANDING_2 = 5 ;

    public static final int RABBITS = -1;


    Random randomGenerator;

    public SceneManager()
    {
        randomGenerator = new Random();
    }

    public int getNextSceneId(int currentScene)
    {
        switch(currentScene)
        {
            case GIRL_SITTING:
               return GIRL_STANDING;
            case GIRL_STANDING:
                return GIRL_BACK;
            case GIRL_BACK:
                return GIRL_SQUATTING;
            case GIRL_SQUATTING:
                return GIRL_PICNIC;
            case GIRL_PICNIC:
                return GIRL_STANDING_2;
            case GIRL_STANDING_2:
                return GIRL_SITTING;

        }
        return RABBITS;
    }

    public SceneData getScene(int scene)
    {
        switch (scene)
        {
            case GIRL_SITTING:
                return new GirlSittingScene();
            case GIRL_STANDING:
                return new GirlStandingScene();
            case GIRL_BACK:
                return GIRL_SQUATTING;
            case GIRL_SQUATTING:
                return GIRL_PICNIC;
            case GIRL_PICNIC:
                return GIRL_STANDING_2;
            case GIRL_STANDING_2:
                return GIRL_SITTING;
            default:
                return new GirlBackScene();
        }
    }

    public static Integer[] GetAllScenes()
    {
        return new Integer[]{
                DEFAULT, BROWN, BLUE, GREEN, PINK, YELLOW
        };
    }

    public static String[] GetAllSceneTitles()
    {
        return new String[]{
                DEFAULT_TITLE, BROWN_TITLE, BLUE_TITLE, GREEN_TITLE, PINK_TITLE, YELLOW_TITLE
        };
    }


}
