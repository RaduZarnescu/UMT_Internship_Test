package com.company;

import java.time.LocalTime;

public class MyParser {

    private final String timeRegex = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$";
    private final String singleDigitHourRegex = "^[0-9]:[0-5][0-9]$";

    private LocalTime getLocalTimeFromString(String s){
        //if the hour is single digit add a 0 at the beginning to be of the required format for LocalTime.parse
        if(s.matches(singleDigitHourRegex)){
            s = "0" + s;
        }
        //convert string to LocalTime
        return LocalTime.parse(s);
    }

    public void parseBookedCalendarLine(String line, Calendar c){
        String[] split = line.split("'");

        for(int i = 0; i < split.length; i++){
            //split string by ' to extract the times
            String s = split[i];

            if(s.matches(timeRegex)){
                LocalTime intervalStart = getLocalTimeFromString(s);

                //we know that the string at i+1 will be a ',' and the one at i+2 will be the end of the interval
                //so skip to the next time and update the iteration
                s = split[i+2];
                i += 2;

                LocalTime intervalEnd = getLocalTimeFromString(s);

                c.addInterval(new Interval(intervalStart, intervalEnd));
            }
        }
    }

    public void parseCalendarLimits(String line, Calendar calendar){
        //split string by ' to extract the times
        String[] split = line.split("'");

        //this time we know that exactly two times will be present in the split array at positions 1 and 3
        calendar.setStart(getLocalTimeFromString(split[1]));
        calendar.setEnd(getLocalTimeFromString(split[3]));
    }

    public int parseMeetingTime(String line){
        String[] split = line.split(":");
        String time = split[1].substring(1);
        return Integer.parseInt(time);
    }
}
