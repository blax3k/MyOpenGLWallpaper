package com.hashimapp.myopenglwallpaper.SceneData;

import android.content.Context;

import com.hashimapp.myopenglwallpaper.Model.BlurBuilder;
import com.hashimapp.myopenglwallpaper.Model.DataStorage.ResourceReader;
import com.hashimapp.myopenglwallpaper.Model.DataStorage.SceneData;
import com.hashimapp.myopenglwallpaper.Model.TimeTracker;

import java.util.ArrayList;
import java.util.Random;

public class SceneManager
{
    private ResourceReader _reader;

    Random randomGenerator;
    private ArrayList<String> SceneNames;
    int lastIndex;

    public SceneManager(Context context)
    {
        randomGenerator = new Random();
        _reader = new ResourceReader(context);
        SceneNames = _reader.GetSceneNames();
        lastIndex = 0;
    }


    public ArrayList<String> GetAllSceneTitles()
    {
        return SceneNames;
    }


    public String GetNextScene()
    {
        Random random = new Random();
//        int index = random.nextInt(SceneNames.size());
        int index;
        if(lastIndex == 0)
        {
            index = SceneNames.size() - 1;
        }
        else
        {
            index = 0;
        }
        lastIndex = index;
        return SceneNames.get(index);
    }



}
