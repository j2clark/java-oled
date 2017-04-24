package com.j2clark.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StationLineupBuilder {

    private String direction;
    private List<StationCall> stationCalls = new ArrayList<>();

    public StationLineupBuilder(String direction) {
        this.direction = direction;
    }

    public StationLineupBuilder withStationCalls(StationCall... stationCalls) {
        if (stationCalls != null) {
            return withStationCalls(Arrays.asList(stationCalls));
        }
        return this;
    }

    public StationLineupBuilder withStationCalls(Collection<StationCall> stationCalls) {
        this.stationCalls.addAll(stationCalls);
        return this;
    }

    public StationLineup build() {
        return new StationLineup(direction, stationCalls);
    }
}
