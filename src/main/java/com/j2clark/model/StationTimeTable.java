package com.j2clark.model;

public class StationTimeTable {
    private final String line;
    private final String name;
    private final StationTime arrives;
    private final StationTime departs;

    public StationTimeTable(String line, String name, StationTime arrives, StationTime departs) {
        this.line = line;
        this.name = name;
        this.arrives = arrives;
        this.departs = departs;
    }

    public String getLine() {
        return line;
    }

    public String getName() {
        return name;
    }

    public StationTime getArrives() {
        return arrives;
    }

    public StationTime getDeparts() {
        return departs;
    }
}
