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

    public SceneManager(Context context)
    {
        randomGenerator = new Random();
        _reader = new ResourceReader(context);
        SceneNames = _reader.GetSceneNames();
    }


    public ArrayList<String> GetAllSceneTitles()
    {
        return SceneNames;
    }

    public String GetNextScene()
    {
        Random random = new Random();
        int index = random.nextInt(SceneNames.size());
        return SceneNames.get(index);
    }



}
