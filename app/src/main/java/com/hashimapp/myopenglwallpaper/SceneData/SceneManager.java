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

    Random randomGenerator;

    public SceneManager(Context context)
    {
        randomGenerator = new Random();
        _reader = new ResourceReader(context);
    }


    public ArrayList<String> GetAllSceneTitles()
    {
        ArrayList<String> names = _reader.GetSceneNames();
        return names;
    }



}
