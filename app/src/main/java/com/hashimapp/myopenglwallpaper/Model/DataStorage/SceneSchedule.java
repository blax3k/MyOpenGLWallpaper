package com.hashimapp.myopenglwallpaper.Model.DataStorage;

import java.util.ArrayList;
import java.util.Date;

public class SceneSchedule
{
    public class SceneParameters
    {
        public String SceneDataKey;
        public ArrayList<Integer> RequiredWeatherList;
        public int[] TimeSlots;
    }

    public class SceneEventGroup
    {
        public String GroupName;
        public ArrayList<SceneParameters> SceneParameterList;
    }

    public ArrayList<SceneEventGroup> AllScenes;
}
