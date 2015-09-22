package com.hashimapp.myopenglwallpaper;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Blake on 9/18/2015.
 */
public class TimeTracker
{
    GregorianCalendar calendar;
    Date currentTime;
    public static int MORNING = 0;
    public static int NOON = 1;
    public static int SUNSET = 2;
    public static int NIGHT = 3;

    public TimeTracker()
    {
        calendar = new GregorianCalendar();
        currentTime = new Date();
    }

    public int getDayPart()
    {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Log.d("Hour of day", "Hour of day: " + hour);
        if((hour >= 20 && hour <= 23) || (hour >= 0 && hour <=4))
        {
            return NIGHT;
        }
        else if(hour >= 5 && hour <= 9)
        {
            return MORNING;
        }
        else if(hour <= 10 && hour <= 19)
        {
            return NOON;
        }
        else return SUNSET;
    }
}
