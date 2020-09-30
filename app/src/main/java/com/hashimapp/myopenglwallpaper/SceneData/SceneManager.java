package com.hashimapp.myopenglwallpaper.SceneData;

import android.content.Context;

import com.hashimapp.myopenglwallpaper.Model.BlurBuilder;
import com.hashimapp.myopenglwallpaper.Model.DataStorage.ResourceReader;
import com.hashimapp.myopenglwallpaper.Model.TimeTracker;

import java.util.ArrayList;
import java.util.Random;

public class SceneManager
{
    private ResourceReader _reader;
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

    Random randomGenerator;

    public SceneManager(Context context)
    {
        randomGenerator = new Random();
        _reader = new ResourceReader(context);
    }

    public static int getScene(int timeOfDay)
    {

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

    public static Integer[] GetAllScenes(){
        return new Integer[]{
                DEFAULT, BROWN, BLUE, GREEN, PINK, YELLOW
        };
    }

    public ArrayList<String> GetAllSceneTitles()
    {
        ArrayList<String> names = _reader.GetSceneNames();
        return names;
    }



}
