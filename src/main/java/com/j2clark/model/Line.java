package com.j2clark.model;

import java.util.List;

public class Line {

    private final String lineName;
    private final List<StationLineup> lineups;

    public Line(String lineName, List<StationLineup> lineups) {
        this.lineName = lineName;
        this.lineups = lineups;
    }

    public List<StationLineup> getLineup() {
        return lineups;
    }

    public String getLineName() {
        return lineName;
    }
}
