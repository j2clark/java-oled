package com.j2clark.model;

import java.util.Map;

public class Train {
    private final StationTime starts;
    private final StationTime ends;
    private final String line;
    private final String direction;
    private final String name;

    // describes a train's entire run
    private final Map<String,Timetable> timetables;

    public Train(String lineName,
                 String direction,
                 String trainName,
                 StationTime starts,
                 StationTime ends,
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

    public StationTime getStarts() {
        return starts;
    }

    public StationTime getEnds() {
        return ends;
    }

    public static class Timetable {
        private final String stationName;
        private final StationTime arrives;
        private final StationTime departs;

        public Timetable(String stationName, StationTime arrives, StationTime departs) {
            this.stationName = stationName;
            this.arrives = arrives;
            this.departs = departs;
        }

        public String getStationName() {
            return stationName;
        }

        public StationTime getArrives() {
            return arrives;
        }

        public StationTime getDeparts() {
            return departs;
        }
    }
}
