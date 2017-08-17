package com.example.stephen.soundrecorder;

/**
 * Created by stephen on 17-8-15.
 */

public class CalculateDurationToTime {

    public static String calTime(int druation){
        int min,sec,allSec;
        allSec=druation/1000;
        min=allSec/60;
        sec=allSec%60;
        String minStr,secStr;
        if (min==0){
            minStr="00";
        }else minStr=""+min;
        if (sec<10){
            secStr="0"+sec;
        }else secStr=""+sec;
        return minStr+":"+secStr;
    }

}
