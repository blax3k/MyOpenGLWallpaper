package com.hashimapp.myopenglwallpaper.Model;

import android.content.Context;
import android.util.Log;

import com.hashimapp.myopenglwallpaper.Model.SceneSetter;
import com.hashimapp.myopenglwallpaper.R;
import com.hashimapp.myopenglwallpaper.View.OpenGLES2WallpaperService;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Blake on 9/18/2015.
 */
public class TimeTracker
{
    public static final int DAWN = 1;
    public static final int DAY = 2;
    public static final int SUNSET = 3;
    public static final int NIGHT = 4;

    public static final double DEFAULT_LATITTUDE = 47.8734952;
    public static final double DEFAULT_LONGITUDE = -122.2495432;

    Calendar NauticalSunrise, NauticalSunset, AstronomicalSunrise, AstronomicalSunset,
            CivilSunrise, CivilSunset, OfficialSunrise, OfficialSunset;
    SunriseSunsetCalculator calculator;
    Calendar calendar;
    Location location;
    TimeZone timeZone;
    int nightBegin = 20;
    int nightEnd = 4;
    int dawnBegin = 5;
    int dawnEnd = 9;
    int dayBegin = 10;
    int dayEnd = 18;

    public TimeTracker()
    {
        location = new Location(DEFAULT_LATITTUDE, DEFAULT_LONGITUDE);
        calendar = Calendar.getInstance();
        calendar.getTime();
        timeZone = calendar.getTimeZone();
        calculator = new SunriseSunsetCalculator(location, timeZone);
        UpdateSunriseSunsetTimes(calendar);
    }

    public void setLocation(double latitude, double longitude){
        location.setLocation(latitude, longitude);
    }

    public int getDayHour()
    {
        calendar.getTime();
        UpdateSunriseSunsetTimes(calendar);

        long currentTimeMillis = calendar.getTimeInMillis();
        if(currentTimeMillis < AstronomicalSunrise.getTimeInMillis() || currentTimeMillis > AstronomicalSunset.getTimeInMillis()){
            return NIGHT;
        }else if(currentTimeMillis > CivilSunrise.getTimeInMillis() && currentTimeMillis < CivilSunset.getTimeInMillis()){
            return DAY;
        }else if(currentTimeMillis > AstronomicalSunrise.getTimeInMillis() && currentTimeMillis < CivilSunrise.getTimeInMillis()){
            return DAWN;
        }else if(currentTimeMillis > AstronomicalSunset.getTimeInMillis() && currentTimeMillis < CivilSunset.getTimeInMillis()){
            return SUNSET;
        }

        return DAWN;


    }

    public void UpdateLocation(){
        //ToDo: set location here


    }

    private void UpdateSunriseSunsetTimes(Calendar rightNow){
        NauticalSunrise = calculator.getNauticalSunriseCalendarForDate(rightNow);
        NauticalSunset = calculator.getNauticalSunsetCalendarForDate(rightNow);
        AstronomicalSunrise = calculator.getNauticalSunriseCalendarForDate(rightNow);
        AstronomicalSunset = calculator.getNauticalSunsetCalendarForDate(rightNow);
        CivilSunrise = calculator.getNauticalSunriseCalendarForDate(rightNow);
        CivilSunset = calculator.getNauticalSunsetCalendarForDate(rightNow);
        OfficialSunrise = calculator.getNauticalSunriseCalendarForDate(rightNow);
        OfficialSunset = calculator.getNauticalSunsetCalendarForDate(rightNow);
    }
}
