package com.j2clark.model;

import com.j2clark.service.ScheduleService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StationLineupBuilder {

    private ScheduleService.Direction direction;
    private List<StationCall> stationCalls = new ArrayList<>();

    public StationLineupBuilder(ScheduleService.Direction direction) {
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
