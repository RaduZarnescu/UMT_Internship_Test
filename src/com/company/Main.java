package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        File file = new File("input.in");
        Scanner reader = new Scanner(file);

        MyParser parser = new MyParser();

        Calendar[] calendars = new Calendar[2];

        for (int i = 0; i < 2; i++) {
            Calendar calendar = new Calendar();

            String line = reader.nextLine();
            parser.parseBookedCalendarLine(line, calendar);
            line = reader.nextLine();
            parser.parseCalendarLimits(line, calendar);

            calendars[i] = calendar;
        }

        String line = reader.nextLine();
        int meetingTime = parser.parseMeetingTime(line);

        List<Interval> result = new ArrayList<>();
        LocalTime start, end;
        if (calendars[0].getStart().isAfter(calendars[1].getStart())) {
            start = calendars[0].getStart();
        } else {
            start = calendars[1].getStart();
        }

        if (calendars[0].getEnd().isBefore(calendars[1].getEnd())) {
            end = calendars[0].getEnd();
        } else {
            end = calendars[1].getEnd();
        }

        Interval interval = new Interval(start, end);
        result.add(interval);

        List<Interval> person1Intersection = getPersonsAvailableTime(calendars[0], meetingTime, result);
        List<Interval> person2Intersection = getPersonsAvailableTime(calendars[1], meetingTime, result);

        List<Interval> finalRes = getIntersection(person1Intersection, person2Intersection, meetingTime);

        System.out.print("[");
        for(Interval i: finalRes){
            System.out.print("['" + i.getStart() + "','" + i.getEnd() + "']");
        }
        System.out.print("]");
    }

    public static List<Interval> getPersonsAvailableTime(Calendar calendar, int meetingTime, List<Interval> result){
        List<Interval> res = new ArrayList<>(result);

        boolean removeFirst = true;

        for(Interval i: calendar.getBookedIntervals()){
            List<Interval> aux = new ArrayList<>();
            for(Interval r: res){
                aux.addAll(getAvailableIntervals(i, r, meetingTime));
            }
            res.addAll(aux);
            if(removeFirst){
                res.remove(0);
                removeFirst = false;
            }
            List<Interval> itemsToBeRemoved = new ArrayList<>();
            for(int j = 0; j < res.size(); j++){
                Interval i1 = res.get(j);
                for(int k = j + 1; k < res.size(); k++){
                    Interval i2 = res.get(k);
                    if(i1.getStart().equals(i2.getStart())){
                        if(i1.getEnd().isBefore(i2.getEnd())){
                            itemsToBeRemoved.add(i2);
                        }
                        else{
                            itemsToBeRemoved.add(i1);
                        }
                    }
                    if(i1.getEnd().equals(i2.getEnd())){
                        if(i1.getStart().isBefore(i2.getStart())){
                            itemsToBeRemoved.add(i1);
                        }
                        else{
                            itemsToBeRemoved.add(i2);
                        }
                    }
                }
            }
            res.removeAll(itemsToBeRemoved);
        }
        return res;
    }

    public static List<Interval> getAvailableIntervals(Interval i, Interval r, int meetingTime){
        List<Interval> result = new ArrayList<>();
        if (i.getStart().isAfter(r.getStart()) && i.getEnd().isBefore(r.getEnd())) { //[{}]
            int timeDiff = i.getStart().getHour() * 60 + i.getStart().getMinute() - r.getStart().getHour() * 60 + r.getStart().getMinute();
            if (timeDiff >= meetingTime) {
                Interval interval1 = new Interval(r.getStart(), i.getStart());
                result.add(interval1);
            }
            timeDiff = r.getEnd().getHour() * 60 + r.getEnd().getMinute() - i.getEnd().getHour() * 60 + i.getEnd().getMinute();
            if (timeDiff >= meetingTime) {
                Interval interval1 = new Interval(i.getEnd(), r.getEnd());
                result.add(interval1);
            }
        } else if (i.getStart().isAfter(r.getStart()) && (i.getEnd().isAfter(r.getEnd()) || i.getEnd().equals(r.getEnd()))) { // [{]}
            int timeDiff = i.getStart().getHour() * 60 + i.getStart().getMinute() - r.getStart().getHour() * 60 + r.getStart().getMinute();
            if (timeDiff >= meetingTime) {
                Interval int1 = new Interval(r.getStart(), i.getStart());
                result.add(int1);
            }
        } else if ((i.getStart().isBefore(r.getStart()) || i.getStart().equals(r.getStart())) && i.getEnd().isBefore(r.getEnd())) { // {[}]
            int timeDiff = r.getEnd().getHour() * 60 + r.getEnd().getMinute() - i.getEnd().getHour() * 60 + i.getEnd().getMinute();
            if (timeDiff >= meetingTime) {
                Interval interval1 = new Interval(i.getEnd(), r.getEnd());
                result.add(interval1);
            }
        } else if (i.getStart().isBefore(r.getStart()) && i.getEnd().isAfter(r.getEnd())) { // {[}]
            result.add(r);
        }
        return result;
    }

    public static List<Interval> getIntersection(List<Interval> l1, List<Interval> l2, int meetingTime){
        List<Interval> result = new ArrayList<>();

        Collections.sort(l1, new CustomComparator());
        Collections.sort(l2, new CustomComparator());

        for(int i = 0, j = 0; i < l1.size() && j < l2.size();){
            Interval i1 = l1.get(i);
            Interval i2 = l2.get(j);
            LocalTime minEnd, maxStart;

            if(i1.getEnd().isBefore(i2.getEnd())){
                minEnd = i1.getEnd();
            }
            else{
                minEnd = i2.getEnd();
            }

            if(i1.getStart().isAfter(i2.getStart())){
                maxStart = i1.getStart();
                j++;
            }
            else{
                maxStart = i2.getStart();
                i++;
            }

            int timeDiff = minEnd.getHour() * 60 + minEnd.getMinute() - maxStart.getHour() * 60 + maxStart.getMinute();
            if(timeDiff >= meetingTime){
                Interval interval = new Interval(maxStart, minEnd);
                result.add(interval);
            }
        }
        return result;
    }

    public static class CustomComparator implements Comparator<Interval> {
        @Override
        public int compare(Interval i1, Interval i2) {
            return i1.getStart().getHour() - i2.getStart().getHour();
        }
    }
}