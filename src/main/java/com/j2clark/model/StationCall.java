package com.j2clark.model;

public class StationCall {

    private String station; // Herrsching
    private String line;    // S5
    private String train;   // 734
    private StationTime arrives;
    private StationTime departs;
    private boolean startOfLine; // no arrives
    private boolean endOfLine; // no departs

    public static StationCall endStation(String station,
                                         String line,
                                         String train,
                                         StationTime arrives) {
        return new StationCall(station, line, train, false, arrives, true, null);
    }

    public static StationCall originStation(String station,
                                            String line,
                                            String train,
                                            StationTime departs) {
        return new StationCall(station, line, train, true, null, false, departs);
    }

    public static StationCall station(String station,
                                            String line,
                                            String train,
                                            StationTime arrives,
                                            int duration) {
        return new StationCall(station, line, train, false, arrives, false, arrives.addSeconds(duration));
    }

    StationCall(String station,
                String line,
                String train,
                boolean startOfLine,
                StationTime arrives,
                boolean endOfLine,
                StationTime departs) {
        this.station = station;
        this.line = line;
        this.train = train;
        this.arrives = arrives;
        this.departs = departs;
        this.startOfLine = startOfLine;
        this.endOfLine = endOfLine;
    }

    public StationTime getArrives() {
        return arrives;
    }

    public StationTime getDeparts() {
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
