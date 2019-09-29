package com.hashimapp.myopenglwallpaper.SceneData;

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



    Random randomGenerator;

    public SceneManager(){
        randomGenerator = new Random();
    }

    public static int getScene(int timeOfDay){

        switch (timeOfDay)
        {
            case TimeTracker.DAY:
                return PINK;
            case TimeTracker.NIGHT:
                return BLUE;
            case TimeTracker.EARLY_DAWN:
            case TimeTracker.MID_DAWN:
            case TimeTracker.LATE_DAWN:
                return GREEN;
            case TimeTracker.EARLY_DUSK:
            case TimeTracker.MID_DUSK:
            case TimeTracker.LATE_DUSK:
                return YELLOW;
        }
        return DEFAULT;
    }



}
