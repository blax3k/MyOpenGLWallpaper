package com.hashimapp.myopenglwallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by Blake on 9/19/2015.
 */
public class SceneSetter
{
    static int BLUR_NONE = 300;
    static int BLUR_LOW = 301;
    static int BLUR_HIGH = 302;
    private int blurSetting;

    private int textureIndexTable, textureIndexCity, textureIndexBuilding, textureIndexSky;


    DataHolder dataHolder = new DataHolder();
    SharedPreferences preferences;
    Context context;
    private float[] currentGirlVertices;
    private float girlOffset;

    public SceneSetter(SharedPreferences nPreferences, Context nContext)
    {
        this.preferences = nPreferences;
        this.context = nContext;
    }

    public float[] getSpriteVertices(int sprite)
    {
        if(sprite == DataCodes.TABLE)
        {
            return dataHolder.tableVertices;
        }
        else if(sprite == DataCodes.GIRL)
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

        else if(sprite == DataCodes.BUILDING)
        {
            return dataHolder.buildingVertices;
        }
        else if (sprite == DataCodes.CITY)
        {
            return dataHolder.cityVertices;
        }
        else if (sprite == DataCodes.SKY)
        {
            return dataHolder.skyVertices;
        }
        else //it's the room
        {
            return dataHolder.roomVertices;
        }
        return null;
    }

    public void setNewScene()
    {

    }


    public float getGirlOffset()
    {
        return girlOffset;
    }

    public float[] getSpriteColor(String sprite)
    {
        if(sprite.equals("table"))
        {
            if(preferences.getBoolean("activate_sunset", true))
            {
                return dataHolder.nightColor;
            }
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
            return dataHolder.skyColor;
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
        float temp = newOpacity;
        if(temp > 1.0f)
            temp = 1.0f;
        if(temp < 0.0f)
            temp = 0.0f;
        dataHolder.setOpacity(temp);
    }

    public float getOpacity()
    {
        return dataHolder.getOpacity();
    }

    public void setBlur(String blurString)
    {
        Log.d("Scene Setter", "blur string: " + blurString);
        if(blurString.equals("none"))
        {
            blurSetting = BLUR_NONE;
            Log.d("Scene Setter", "none chosen");
        }
        else if(blurString.equals("low"))
        {
            blurSetting = BLUR_LOW;
            Log.d("Scene Setter", "low chosen");
        }
        else if(blurString.equals("high"))
        {
            blurSetting = BLUR_HIGH;
            Log.d("Scene Setter", "high chosen");
        }
    }

    public Bitmap getTexture(int sprite)
    {
        Bitmap bmp = null; //BitmapFactory.decodeResource(context.getResources(), R.drawable.girlmidsword);
        if(sprite == DataCodes.GIRL)
        {
            String choice = preferences.getString("texture_model", "1");
            switch(choice)
            {
                case("1"):
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlfrontbook);
                    if(blurSetting != BLUR_NONE)
                    {
                        if(blurSetting == BLUR_LOW)
                            bmp = BlurBuilder.blur(context, bmp, 9.5f, 0.8f);
                        else
                            bmp = BlurBuilder.blur(context, bmp, 13.5f, 0.5f);
                    }
                    girlOffset = 1.0f;
                    break;
                case("2"):
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlfrontflower);
                    if(blurSetting != BLUR_NONE)
                    {
                        if(blurSetting == BLUR_LOW)
                            bmp = BlurBuilder.blur(context, bmp, 9.5f, 0.8f);
                        else
                            bmp = BlurBuilder.blur(context, bmp, 13.5f, 0.5f);
                    }
                    girlOffset = 1.0f;
                    break;
                case("3"):
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlmidsword);
                    if (preferences.getString("camera_blur", "none").equals("high"))
                    {
                        bmp = BlurBuilder.blur(context, bmp, 7.5f, 0.9f);
                        Log.d("Scene Setter", "sword blurry image chosen");
                    }
                    else
                        Log.d("Scene Setter", "sword non blurry image chosen");
                    girlOffset = 0.98f;
                    break;
                case("4"):
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlmidheadphones);
                    if (preferences.getString("camera_blur", "none").equals("high"))
                    {
                        bmp = BlurBuilder.blur(context, bmp, 7.5f, 0.9f);
                        Log.d("Scene Setter", "headphone blurry image chosen");
                    }else
                        Log.d("Scene Setter", "headphone non blurry image chosen");
                    girlOffset = 0.98f;
                    break;
                case("5"):
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlbacksit);
                    girlOffset = 0.95f;
                    break;
                case("6"):
                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.girlbackstand);
                    girlOffset = 0.95f;
                    break;
            }
        }
        else if (sprite == DataCodes.TABLE)
        {
            Log.d("Scene Setter" , "table was called");
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.table);
//            if(blurSetting != BLUR_NONE)
//            {
//                if(blurSetting == BLUR_LOW)
//                    bmp = BlurBuilder.blur(context, bmp, 9.5f, 0.8f);
//                else
//                    bmp = BlurBuilder.blur(context, bmp, 13.5f, 0.5f);
//            }
        }
        else if (sprite == DataCodes.ROOM)
        {
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.room);
        }
        else if (sprite == DataCodes.BUILDING)
        {
            Log.d("Scene Setter" , "table was called");
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.building);
//            if(blurSetting != BLUR_NONE)
//            {
//                if(blurSetting == BLUR_LOW)
//                    bmp = BlurBuilder.blur(context, bmp, 9.5f, 0.8f);
//                else
//                    bmp = BlurBuilder.blur(context, bmp, 13.5f, 0.5f);
//            }
        }
        else if (sprite == DataCodes.CITY)
        {
            Log.d("Scene Setter" , "city was called");
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.city);
//            if(blurSetting != BLUR_NONE)
//            {
//                if(blurSetting == BLUR_LOW)
//                    bmp = BlurBuilder.blur(context, bmp, 11.5f, 0.7f);
//                else
//                    bmp = BlurBuilder.blur(context, bmp, 15.5f, 0.4f);
//            }
        }
        else if (sprite == DataCodes.SKY)
        {
            Log.d("Scene Setter" , "sky was called");
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.sky);
//            if(blurSetting != BLUR_NONE)
//            {
//                if(blurSetting == BLUR_LOW)
//                    bmp = BlurBuilder.blur(context, bmp, 11.5f, 0.7f);
//                else
//                    bmp = BlurBuilder.blur(context, bmp, 15.5f, 0.4f);
//            }
        }

        return bmp;
    }
}
