package com.hashimapp.myopenglwallpaper.Model;

import android.util.Log;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Blake on 9/18/2015.
 */
public class TimeTracker
{
    public static final int DAWN = 0;
    public static final int SUNRISE = 1;
    public static final int DAY = 2;
    public static final int SUNSET = 3;
    public static final int DUSK = 4;
    public static final int NIGHT = 5;

    public static final double DEFAULT_LATITTUDE = 47.8734952;
    public static final double DEFAULT_LONGITUDE = -122.2495432;

    public static final int TIME_PHASE_INDEX = 0;
    public static final int TIME_PHASE_PROGRESSION_INDEX = 1;

    Calendar NauticalSunrise, NauticalSunset, AstronomicalSunrise, AstronomicalSunset,
            CivilSunrise, CivilSunset, OfficialSunrise, OfficialSunset,
            PreOfficialSunset, PostOfficialSunrise;

    SunriseSunsetCalculator calculator;
    Location location;
    TimeZone timeZone;

    public TimeTracker()
    {
        location = new Location(DEFAULT_LATITTUDE, DEFAULT_LONGITUDE);
        timeZone = Calendar.getInstance().getTimeZone();
        calculator = new SunriseSunsetCalculator(location, timeZone);
    }

    public void SetLocation(double latitude, double longitude){
        location.setLocation(latitude, longitude);
    }

    public int[] GetTimePhase(Calendar calendar)
    {
        int[] timeInfo = new int[2];
        UpdateSunriseSunsetTimes(calendar);
        int percentage = 100;
        int CurrentTimePhase = DAY;

        long currentTimeMillis = calendar.getTimeInMillis();
        if(currentTimeMillis <= AstronomicalSunrise.getTimeInMillis() || currentTimeMillis >= AstronomicalSunset.getTimeInMillis()){
            CurrentTimePhase = NIGHT;
        }
        else if(currentTimeMillis >= PostOfficialSunrise.getTimeInMillis() && currentTimeMillis <= PreOfficialSunset.getTimeInMillis()){
            CurrentTimePhase = DAY;
        }
        else if(currentTimeMillis >= AstronomicalSunrise.getTimeInMillis() && currentTimeMillis < CivilSunrise.getTimeInMillis()){
            CurrentTimePhase = DAWN;
            percentage = CalculateCurrentProgress(calendar.getTimeInMillis(), AstronomicalSunrise.getTimeInMillis(), CivilSunrise.getTimeInMillis());
        }
        else if(currentTimeMillis >= CivilSunrise.getTimeInMillis() && currentTimeMillis < PostOfficialSunrise.getTimeInMillis()){
            CurrentTimePhase = SUNRISE;
            percentage = CalculateCurrentProgress(calendar.getTimeInMillis(), CivilSunrise.getTimeInMillis(), PostOfficialSunrise.getTimeInMillis());
        }
        else if(currentTimeMillis >= PreOfficialSunset.getTimeInMillis() && currentTimeMillis < OfficialSunset.getTimeInMillis()){
            CurrentTimePhase = SUNSET;
            percentage = CalculateCurrentProgress(calendar.getTimeInMillis(), PreOfficialSunset.getTimeInMillis(), OfficialSunset.getTimeInMillis());
        }
        else if(currentTimeMillis >= OfficialSunset.getTimeInMillis() && currentTimeMillis < AstronomicalSunset.getTimeInMillis()){
            CurrentTimePhase = DUSK;
            percentage = CalculateCurrentProgress(calendar.getTimeInMillis(), OfficialSunset.getTimeInMillis(), AstronomicalSunset.getTimeInMillis());
        }

        timeInfo[TIME_PHASE_INDEX] = CurrentTimePhase;
        timeInfo[TIME_PHASE_PROGRESSION_INDEX] = percentage;

        return timeInfo;
    }

    private int CalculateCurrentProgress(long currentTime, long currentPhase, long nextPhase){

        double currentProgress = (Math.abs(currentTime - currentPhase));
        double endPhase = Math.abs(currentPhase - nextPhase);
        double result = currentProgress/endPhase;

        int returnValue = (int)(result * 100);
        return returnValue;
    }

//    public int GetTimePhaseProgress(Calendar calendar, int timePhase){
//
//        UpdateSunriseSunsetTimes(calendar);
//
//        switch(timePhase){
//            case DAWN:
//                break;
//            case SUNRISE:
//                break;
//            case DAY:
//                break;
//            case SUNSET:
//                break;
//            case DUSK:
//                break;
//            case NIGHT:
//                break;
//        }
//
//        return 0;
//    }

    private int GetProgress(long phaseStart, long phaseEnd, long currentTime){
        return 0;
    }

    public void UpdateSunriseSunsetTimes(Calendar rightNow){
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
                        "\n oRise: " + OfficialSunrise.getTime()+
                        "\n pRise: " + PostOfficialSunrise.getTime() +
                        "\n pSet: " + PreOfficialSunset.getTime() +
                        "\n oSet: " + OfficialSunset.getTime()+
                        "\n cSet: " + CivilSunset.getTime() +
                        "\n nSet: " + NauticalSunset.getTime() +
                        "\n aSet: " + AstronomicalSunset.getTime()
        );

    }
}
