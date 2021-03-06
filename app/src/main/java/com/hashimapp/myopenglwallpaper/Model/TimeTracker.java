package com.hashimapp.myopenglwallpaper.Model;

import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TimeUtils;

import com.hashimapp.myopenglwallpaper.R;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Blake on 9/18/2015.
 */
public class TimeTracker
{
    public static final int EARLY_DAWN = 0;
    public static final int MID_DAWN = 1;
    public static final int LATE_DAWN = 2;
    public static final int DAY = 3;
    public static final int EARLY_DUSK = 4;
    public static final int MID_DUSK = 5;
    public static final int LATE_DUSK = 6;
    public static final int NIGHT = 7;

    public static final double DEFAULT_LATITTUDE = 47.639138;
    public static final double DEFAULT_LONGITUDE = -122.332183;
    public static final String DEFAULT_TIMEZONE = "GMT+5:00";

    private final long SCENE_CHANGE_INTERVAL = 5 * 1000;

    public static final int TIME_PHASE_INDEX = 0;
    public static final int TIME_PHASE_PROGRESSION_INDEX = 1;

    Resources resources;

    Calendar AstronomicalSunrise,
            NauticalSunrise,
            CivilSunrise,
            OfficialSunrise,
            PostOfficialSunrise,
            PreOfficialSunset,
            OfficialSunset,
            CivilSunset,
            NauticalSunset,
            AstronomicalSunset;

    SunriseSunsetCalculator calculator;
    Location location;
    TimeZone timeZone;

    private Date lastSceneChange;


    public TimeTracker(Resources resources)
    {
        location = new Location(DEFAULT_LATITTUDE, DEFAULT_LONGITUDE);
        timeZone = Calendar.getInstance().getTimeZone();
        calculator = new SunriseSunsetCalculator(location, timeZone);
        lastSceneChange = Calendar.getInstance().getTime();
        this.resources = resources;
    }

    public void SetLocation(Location location){
        this.location = location;
        this.timeZone = Calendar.getInstance().getTimeZone();
        calculator = new SunriseSunsetCalculator(location, timeZone);
    }

    public void SetDefaultLocation(){
        this.location = new Location(DEFAULT_LATITTUDE, DEFAULT_LONGITUDE);
        timeZone = TimeZone.getTimeZone("GMT-8:00");
        calculator = new SunriseSunsetCalculator(location, timeZone);
    }


    //calculates and returns the current time phase (index 0) and the current progression through that phase (index 1)
    public int[] GetTimePhase(Date date)
    {
        int[] timeInfo = new int[2];
        UpdateSunriseSunsetTimes(date);
        int percentage = 100;
        int CurrentTimePhase = DAY;

        long currentTimeMillis = date.getTime();
        //Before sunrise. Night
        if (currentTimeMillis < AstronomicalSunrise.getTimeInMillis())
        {
            CurrentTimePhase = NIGHT;
        } else if (currentTimeMillis >= AstronomicalSunrise.getTimeInMillis() && currentTimeMillis < CivilSunrise.getTimeInMillis())
        {
            CurrentTimePhase = EARLY_DAWN;
            percentage = CalculateCurrentProgress(date.getTime(), AstronomicalSunrise.getTimeInMillis(), CivilSunrise.getTimeInMillis());
        } else if (currentTimeMillis >= CivilSunrise.getTimeInMillis() && currentTimeMillis < OfficialSunrise.getTimeInMillis())
        {
            CurrentTimePhase = MID_DAWN;
            percentage = CalculateCurrentProgress(date.getTime(), CivilSunrise.getTimeInMillis(), OfficialSunrise.getTimeInMillis());
        } else if (currentTimeMillis >= OfficialSunrise.getTimeInMillis() && currentTimeMillis < PostOfficialSunrise.getTimeInMillis())
        {
            CurrentTimePhase = LATE_DAWN;
            percentage = CalculateCurrentProgress(date.getTime(), CivilSunrise.getTimeInMillis(), PostOfficialSunrise.getTimeInMillis());
        } else if (currentTimeMillis >= PostOfficialSunrise.getTimeInMillis() && currentTimeMillis <= PreOfficialSunset.getTimeInMillis())
        {
            CurrentTimePhase = DAY;
        } else if (currentTimeMillis >= PreOfficialSunset.getTimeInMillis() && currentTimeMillis < OfficialSunset.getTimeInMillis())
        {
            CurrentTimePhase = EARLY_DUSK;
            percentage = CalculateCurrentProgress(date.getTime(), PreOfficialSunset.getTimeInMillis(), OfficialSunset.getTimeInMillis());
        } else if (currentTimeMillis >= OfficialSunset.getTimeInMillis() && currentTimeMillis < CivilSunset.getTimeInMillis())
        {
            CurrentTimePhase = MID_DUSK;
            percentage = CalculateCurrentProgress(date.getTime(), OfficialSunset.getTimeInMillis(), CivilSunset.getTimeInMillis());
        } else if (currentTimeMillis >= CivilSunset.getTimeInMillis() && currentTimeMillis < AstronomicalSunset.getTimeInMillis())
        {
            CurrentTimePhase = LATE_DUSK;
            percentage = CalculateCurrentProgress(date.getTime(), CivilSunset.getTimeInMillis(), AstronomicalSunset.getTimeInMillis());
        } else if (currentTimeMillis >= AstronomicalSunset.getTimeInMillis())
        {
            CurrentTimePhase = NIGHT;
        }

        timeInfo[TIME_PHASE_INDEX] = CurrentTimePhase;
        timeInfo[TIME_PHASE_PROGRESSION_INDEX] = percentage;

        return timeInfo;
    }

    public int[] GetTimePhase(String timePhase, @Nullable Date dt)
    {
        if(timePhase.equals(resources.getString(R.string.time_key_automatic))){
            Date date = Calendar.getInstance().getTime();
//            if(dt != null){
//                if(dt.getDay() >= date.getDay()){
//                    dt.setTime(dt.getTime() - 1000*60*60*24);
//                }
//                date = dt;
//                Log.d("timey", "set date: " + date.toString());
//            }
            return  GetTimePhase(date);
        }

        int[] timeInfo = new int[2];
        int percentage = 100;
        int CurrentTimePhase = DAY;

        if (timePhase.equals(resources.getString(R.string.time_key_early_dawn)))
        {
            CurrentTimePhase = EARLY_DAWN;
        } else if (timePhase.equals(resources.getString(R.string.time_key_mid_dawn)))
        {
            CurrentTimePhase = MID_DAWN;

        } else if (timePhase.equals(resources.getString(R.string.time_key_day)))
        {
            CurrentTimePhase = DAY;

        } else if (timePhase.equals(resources.getString(R.string.time_key_early_dusk)))
        {
            CurrentTimePhase = EARLY_DUSK;

        } else if (timePhase.equals(resources.getString(R.string.time_key_mid_dusk)))
        {
            CurrentTimePhase = MID_DUSK;

        } else if (timePhase.equals(resources.getString(R.string.time_key_night)))
        {
            CurrentTimePhase = NIGHT;

        }
        timeInfo[TIME_PHASE_INDEX] = CurrentTimePhase;
        timeInfo[TIME_PHASE_PROGRESSION_INDEX] = percentage;

        return timeInfo;
    }


    public boolean SceneChangeRequired(){
        Date currentTime = Calendar.getInstance().getTime();
        long diff = currentTime.getTime() - lastSceneChange.getTime();
        if(diff > SCENE_CHANGE_INTERVAL)
        {
            lastSceneChange = Calendar.getInstance().getTime();
            return true;
        }

        return false;
    }

    public void SignalSceneChange()
    {
        lastSceneChange = Calendar.getInstance().getTime();
    }

    private int CalculateCurrentProgress(long currentTime, long currentPhase, long nextPhase)
    {

        double currentProgress = (Math.abs(currentTime - currentPhase));
        double endPhase = Math.abs(currentPhase - nextPhase);
        double result = currentProgress / endPhase;

        int returnValue = (int) (result * 100);
        return returnValue;
    }


    public void UpdateSunriseSunsetTimes(Date date)
    {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        NauticalSunrise = calculator.getNauticalSunriseCalendarForDate(rightNow);
        NauticalSunset = calculator.getNauticalSunsetCalendarForDate(rightNow);
        AstronomicalSunrise = calculator.getAstronomicalSunriseCalendarForDate(rightNow);
        AstronomicalSunset = calculator.getAstronomicalSunsetCalendarForDate(rightNow);
        CivilSunrise = calculator.getCivilSunriseCalendarForDate(rightNow);
        CivilSunset = calculator.getCivilSunsetCalendarForDate(rightNow);
        OfficialSunrise = calculator.getOfficialSunriseCalendarForDate(rightNow);
        OfficialSunset = calculator.getOfficialSunsetCalendarForDate(rightNow);
        PostOfficialSunrise = calculator.getPreOfficialSunriseCalendarForDate(rightNow);
        PreOfficialSunset = calculator.getPreOfficialSunsetCalendarForDate(rightNow);

        Log.d("timeTracker", "right now: " + rightNow.getTime());
        Log.d("timeTracker",
                "\n aRise: " + AstronomicalSunrise.getTime() +
                        "\n nRise: " + NauticalSunrise.getTime() +
                        "\n cRise: " + CivilSunrise.getTime() +
                        "\n oRise: " + OfficialSunrise.getTime() +
                        "\n pRise: " + PostOfficialSunrise.getTime() +
                        "\n pSet: " + PreOfficialSunset.getTime() +
                        "\n oSet: " + OfficialSunset.getTime() +
                        "\n cSet: " + CivilSunset.getTime() +
                        "\n nSet: " + NauticalSunset.getTime() +
                        "\n aSet: " + AstronomicalSunset.getTime()
        );

    }
}
