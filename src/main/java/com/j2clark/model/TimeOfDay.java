package com.j2clark.model;

import java.util.Calendar;

public class TimeOfDay implements Comparable<TimeOfDay> {

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

    public TimeOfDay() {
        Calendar c = Calendar.getInstance();
        hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);
    }

    private static int HOURS_24 = 24*60*60;
    public TimeOfDay(long secondsOfDay) {
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

    public TimeOfDay(int hourOfDay, int minute, int second) {
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

    public boolean before(TimeOfDay time) {
        return getSecondsOfDay() < time.getSecondsOfDay();
    }

    public boolean after(TimeOfDay time) {
        return getSecondsOfDay() > time.getSecondsOfDay();

    }

    public TimeOfDay addSeconds(int seconds) {
        return new TimeOfDay(getSecondsOfDay() + seconds);
    }

    public TimeOfDay addMinutes(int minutes) {
        return new TimeOfDay(getSecondsOfDay() + (minutes*60));
    }

    public TimeOfDay subtractMinutes(int minutes) {
        return new TimeOfDay(getSecondsOfDay() - (60*minutes));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeOfDay)) {
            return false;
        }

        TimeOfDay that = (TimeOfDay) o;

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
    public int compareTo(TimeOfDay o) {
        return Integer.compare(getSecondsOfDay(), o.getSecondsOfDay());
    }
}
