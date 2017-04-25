package com.j2clark.model;

import com.j2clark.service.ScheduleService;

import java.util.List;

public class StationLineup {

    private final ScheduleService.Direction direction;
    private final List<StationCall> stationCalls;

    public StationLineup(ScheduleService.Direction direction, List<StationCall> stationCalls) {
        this.direction = direction;
        this.stationCalls = stationCalls;
    }

    public boolean isCurrent() {
        // find first station and last, compare depart and arrive with now

        StationCall origin = stationCalls.get(0);
        StationCall terminal = stationCalls.get(stationCalls.size() -1);
        TimeOfDay now = new TimeOfDay();

        boolean departedOrigin =  origin.getDeparts().before(now);
        boolean notArrivedTerminal = terminal.getArrives().after(now);

        return (departedOrigin && notArrivedTerminal);
    }

    public StationCall getTerminalStation() {
        return stationCalls.get(stationCalls.size() - 1);
    }

    public StationCall getOriginStation() {
        return stationCalls.get(0);
    }

    public ScheduleService.Direction getDirection() {
        return direction;
    }

    public List<StationCall> getStationCalls() {
        return stationCalls;
    }
}
