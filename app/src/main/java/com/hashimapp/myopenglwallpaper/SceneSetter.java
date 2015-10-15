package com.hashimapp.myopenglwallpaper;

import android.content.SharedPreferences;

import java.util.prefs.Preferences;

/**
 * Created by Blake on 9/19/2015.
 */
public class SceneSetter
{
    DataHolder dataHolder = new DataHolder();
    SharedPreferences preferences;
    public SceneSetter(SharedPreferences preferences)
    {
        this.preferences = preferences;
    }

    public float[] getSpriteVertices(String sprite)
    {
        if(sprite.equals("grass"))
        {
            return dataHolder.grassVertices;
        }
        else if(sprite.equals("girl"))
        {
            return dataHolder.girlVertices;
        }
        else //it's the background (field)
        {
            return dataHolder.fieldVertices;
        }

    }

    public float[] getSpriteColor(String sprite)
    {
        if(sprite.equals("grass"))
        {
            if(preferences.getBoolean("activate_sunset", true))
            {
                return dataHolder.nightColor;
            }
            else
                return dataHolder.normalColor;
        }
        else if(sprite.equals("girl"))
        {
            if(preferences.getBoolean("activate_sunset", true))
            {
                return dataHolder.nightColorGirl;
            }
            return dataHolder.normalColorGirl;
        }
        else //it's the background
        {
            if(preferences.getBoolean("activate_sunset", true))
            {
                return dataHolder.nightColor;
            }
            return dataHolder.normalColor;
        }
    }

    public void setOpacity(float newOpacity)
    {
        dataHolder.setOpacity(newOpacity);
    }

    public float getOpacity()
    {
        return dataHolder.getOpacity();
    }
}
