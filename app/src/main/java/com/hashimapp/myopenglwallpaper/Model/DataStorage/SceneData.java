package com.hashimapp.myopenglwallpaper.Model.DataStorage;

import com.hashimapp.myopenglwallpaper.Model.SpriteData;

import java.util.ArrayList;

public class SceneData
{
    public String SceneKey;

    public ArrayList<SpriteDataParameters> SpriteDataList;

    public SceneData()
    {
        SpriteDataList = new ArrayList<>();
    }
}
