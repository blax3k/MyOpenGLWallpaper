package com.hashimapp.myopenglwallpaper.SceneData;

import java.util.ArrayList;
import java.util.List;

public class ColorData {

    public class ColorDatum{
        public String Name;
        public float[] Color;

        public ColorDatum(String name, float[] Color){

        }

    }

    private List<ColorDatum> colorDataList;

    public ColorData(){
        colorDataList = new ArrayList<ColorDatum>();
    }
}
