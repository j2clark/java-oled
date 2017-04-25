package com.j2clark.model;

public class StationCall {

    private String station; // Herrsching
    private String line;    // S5
    private String train;   // 734
    private TimeOfDay arrives;
    private TimeOfDay departs;
    private boolean startOfLine; // no arrives
    private boolean endOfLine; // no departs

    public static StationCall endStation(String station,
                                         String line,
                                         String train,
                                         TimeOfDay arrives) {
        return new StationCall(station, line, train, false, arrives, true, null);
    }

    public static StationCall originStation(String station,
                                            String line,
                                            String train,
                                            TimeOfDay departs) {
        return new StationCall(station, line, train, true, null, false, departs);
    }

    public static StationCall station(String station,
                                            String line,
                                            String train,
                                            TimeOfDay arrives,
                                            int duration) {
        return new StationCall(station, line, train, false, arrives, false, arrives.addSeconds(duration));
    }

    StationCall(String station,
                String line,
                String train,
                boolean startOfLine,
                TimeOfDay arrives,
                boolean endOfLine,
                TimeOfDay departs) {
        this.station = station;
        this.line = line;
        this.train = train;
        this.arrives = arrives;
        this.departs = departs;
        this.startOfLine = startOfLine;
        this.endOfLine = endOfLine;
    }

    public TimeOfDay getArrives() {
        return arrives;
    }

    public TimeOfDay getDeparts() {
        return departs;
    }

    public String getStation() {
        return station;
    }


    public String getLine() {
        return line;
    }

    public String getTrain() {
        return train;
    }

    public boolean isStartOfLine() {
        return startOfLine;
    }

    public boolean isEndOfLine() {
        return endOfLine;
    }
}
