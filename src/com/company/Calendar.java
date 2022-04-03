package com.company;

import java.time.LocalTime;
import java.util.ArrayList;

public class Calendar {
    private LocalTime start;
    private LocalTime end;
    private ArrayList<Interval> bookedIntervals;

    public Calendar() {
        this.bookedIntervals = new ArrayList<>();
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public ArrayList<Interval> getBookedIntervals() {
        return bookedIntervals;
    }

    public void setBookedIntervals(ArrayList<Interval> bookedIntervals) {
        this.bookedIntervals = bookedIntervals;
    }

    public void addInterval(Interval interval){
        this.bookedIntervals.add(interval);
    }
}
