package com.j2clark.model;

public class StationTimeTable {
    private final String line;
    private final String name;
    private final TimeOfDay arrives;
    private final TimeOfDay departs;

    public StationTimeTable(String line, String name, TimeOfDay arrives, TimeOfDay departs) {
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

    public TimeOfDay getArrives() {
        return arrives;
    }

    public TimeOfDay getDeparts() {
        return departs;
    }
}
