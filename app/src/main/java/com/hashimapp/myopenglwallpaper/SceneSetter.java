package com.hashimapp.myopenglwallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Blake on 9/19/2015.
 */
public class SceneSetter
{
    DataHolder dataHolder = new DataHolder();
    SharedPreferences preferences;
    Context context;
    private float[] currentGirlVertices;

    public SceneSetter(SharedPreferences nPreferences, Context nContext)
    {
        this.preferences = nPreferences;
        this.context = nContext;
    }

    public float[] getSpriteVertices(String sprite)
    {
        if(sprite.equals("table"))
        {
            return dataHolder.tableVertices;
        }
        else if(sprite.equals("girl"))
        {
            String choice = preferences.getString("texture_model", "1");
            switch(choice)
            {
                case("1"):
                    return dataHolder.girlFrontReading;
                case("2"):
                    return dataHolder.girlFrontReading;
                case("3"):
                    return dataHolder.girlMidStanding;
                case("4"):
                    return dataHolder.girlMidStanding;
                case("5"):
                    return dataHolder.girlBackSitting;
                case("6"):
                    return dataHolder.girlBackSitting;
            }
        }
//        else if(sprite.equals("girlFront"))
//        {
//            return dataHolder.girlFrontReading;
//        }
//        else if(sprite.equals("girlMid"))
//        {
//            return dataHolder.girlMidStanding;
//        }
//        else if(sprite.equals("girlBack"))
//        {
//            return dataHolder.girlBackSitting;
//        }
        else if(sprite.contentEquals("building"))
        {
            return dataHolder.buildingVertices;
        }
        else if (sprite.equals("city"))
        {
            return dataHolder.cityVertices;
        }
        else if (sprite.equals("sky"))
        {
            return dataHolder.skyVertices;
        }
        else //it's the room (field)
        {
            return dataHolder.roomVertices;
        }
        return null;
    }

    public void setNewScene()
    {

    }

    public float[] getSpriteColor(String sprite)
    {
        if(sprite.equals("table"))
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
        else if(sprite.equals("room"))
        {
            if(preferences.getBoolean("activate_sunset", true))
            {
                return dataHolder.nightColor;
            }
            return dataHolder.normalColor;
        }
        else if(sprite.equals("city"))
        {
            if(preferences.getBoolean("activate_sunset", true))
            {
                return dataHolder.nightColor;
            }
            return dataHolder.normalColor;
        }
        else if(sprite.equals("building"))
        {
            if(preferences.getBoolean("activate_sunset", true))
            {
                return dataHolder.nightColor;
            }
            return dataHolder.normalColor;

        }
        else //it's the sky
        {
            if(preferences.getBoolean("activate_sunset", true))
            {
                return dataHolder.nightColor;
            }
            return dataHolder.normalColor;
        }
    }

    public int getGirlRender()
    {
        if(preferences.getBoolean("remove_layer", true))
        {
            return 0;
        }
        String choice = preferences.getString("texture_model", "1");
        switch(choice)
        {
            case("1"):
                return 1;
            case("2"):
                return 2;
            case("3"):
                return 3;
        }
        return 1;
    }
    public void setOpacity(float newOpacity)
    {
        dataHolder.setOpacity(newOpacity);
    }

    public float getOpacity()
    {
        return dataHolder.getOpacity();
    }

    public Bitmap getTexture(String sprite)
    {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlmidsword);
        if(sprite.equals("girl"))
        {
            String choice = preferences.getString("texture_model", "1");
            switch(choice)
            {
                case("1"):
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlfrontbook);
                    bmp = BlurBuilder.blur(context, bmp);
                    break;
                case("2"):
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlfrontflower);
                    break;
                case("3"):
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlmidsword);
                    break;
                case("4"):
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlmidheadphones);
                    break;
                case("5"):
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlbacksit);
                    break;
                case("6"):
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlbackstand);
                    break;
            }
        }
        return bmp;
    }
}
