package com.j2clark.model;

import java.util.Calendar;

public class StationTime implements Comparable<StationTime> {

    private final int hourOfDay;

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    private final int minute;
    private final int second;

    public StationTime() {
        Calendar c = Calendar.getInstance();
        hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);
    }

    private static int HOURS_24 = 24*60*60;
    public StationTime(long secondsOfDay) {
        if (secondsOfDay > HOURS_24) {
            if (secondsOfDay > 2* HOURS_24) {
                throw new IllegalArgumentException(secondsOfDay
                                                   + " is Way too large a number. We are expecting the seconds in the day");
            } else {
                // rollover
                secondsOfDay = secondsOfDay - HOURS_24;
            }
        }
        int remainingSeconds = (int) secondsOfDay;

        hourOfDay = (int) (secondsOfDay/(60*60));
        remainingSeconds -= (hourOfDay*60*60);

        minute = remainingSeconds/ 60;
        remainingSeconds -= minute*60;

        second = remainingSeconds;
    }

    public StationTime(int hourOfDay, int minute, int second) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.second = second;
    }

    public String toString() {
        return getClass().getSimpleName() + String.format(" [%d:%02d:%02d]", hourOfDay, minute, second);
    }

    public int getSecondsOfDay() {
        return (hourOfDay * 60*60) + (minute *60) + second;
    }

    public boolean before(StationTime time) {
        return getSecondsOfDay() < time.getSecondsOfDay();
    }

    public boolean after(StationTime time) {
        return getSecondsOfDay() > time.getSecondsOfDay();

    }

    public StationTime addSeconds(int seconds) {
        return new StationTime(getSecondsOfDay() + seconds);
    }

    public StationTime addMinutes(int minutes) {
        return new StationTime(getSecondsOfDay() + (minutes*60));
    }

    public StationTime subtractMinutes(int minutes) {
        return new StationTime(getSecondsOfDay() - (60*minutes));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StationTime)) {
            return false;
        }

        StationTime that = (StationTime) o;

        if (hourOfDay != that.hourOfDay) {
            return false;
        }
        if (minute != that.minute) {
            return false;
        }
        return second == that.second;

    }

    @Override
    public int hashCode() {
        int result = hourOfDay;
        result = 31 * result + minute;
        result = 31 * result + second;
        return result;
    }

    @Override
    public int compareTo(StationTime o) {
        return Integer.compare(getSecondsOfDay(), o.getSecondsOfDay());
    }
}
