package com.j2clark.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LineBuilder {

    private String lineName;
    private List<StationLineup> stationLineups = new ArrayList<>();

    public LineBuilder(String lineName) {
        this.lineName = lineName;
    }

    public LineBuilder withStationLineups(StationLineup... stationLineup) {
        if (stationLineup != null) {
            return withStationLineups(Arrays.asList(stationLineup));
        }
        return this;
    }

    public LineBuilder withStationLineups(Collection<StationLineup> stationLineup) {
        this.stationLineups.addAll(stationLineup);
        return this;
    }

    public Line build() {
        return new Line(lineName, stationLineups);
    }
}
