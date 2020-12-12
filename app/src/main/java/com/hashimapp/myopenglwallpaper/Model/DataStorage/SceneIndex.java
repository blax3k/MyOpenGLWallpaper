package com.hashimapp.myopenglwallpaper.Model.DataStorage;

import java.util.ArrayList;

public class SceneIndex
{
    /*
    Contains the list of all possible scenes and the conditions required for
    picking them as well as the percentage chance of picking them.
     */

    public class SceneSequenceGroup
    {
        public String SequenceKey;
        public ArrayList<SceneMetaData> sceneMetaDataArrayList;
    }

    public class SceneMetaData
    {
        public String SceneKey;
        public int[] AvailableTimes;

    }
}
