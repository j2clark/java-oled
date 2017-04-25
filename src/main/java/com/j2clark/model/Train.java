package com.j2clark.model;

import java.util.Map;

public class Train {
    private final TimeOfDay starts;
    private final TimeOfDay ends;
    private final String line;
    private final String direction;
    private final String name;

    // describes a train's entire run
    private final Map<String,Timetable> timetables;

    public Train(String lineName,
                 String direction,
                 String trainName,
                 TimeOfDay starts,
                 TimeOfDay ends,
                 Map<String,Timetable> timetables) {
        this.line = lineName;
        this.direction = direction;
        this.name = lineName + " " + trainName;
        this.starts = starts;
        this.ends = ends;
        this.timetables = timetables;
    }

    public String getName() {
        return name;
    }

    public String getLine() {
        return line;
    }

    public String getDirection() {
        return direction;
    }

    public Map<String, Timetable> getTimetables() {
        return timetables;
    }

    public TimeOfDay getStarts() {
        return starts;
    }

    public TimeOfDay getEnds() {
        return ends;
    }

    public static class Timetable {
        private final String stationName;
        private final TimeOfDay arrives;
        private final TimeOfDay departs;

        public Timetable(String stationName, TimeOfDay arrives, TimeOfDay departs) {
            this.stationName = stationName;
            this.arrives = arrives;
            this.departs = departs;
        }

        public String getStationName() {
            return stationName;
        }

        public TimeOfDay getArrives() {
            return arrives;
        }

        public TimeOfDay getDeparts() {
            return departs;
        }
    }
}
