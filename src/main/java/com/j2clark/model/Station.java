package com.j2clark.model;

import com.j2clark.service.ScheduleService;

import java.util.Map;
import java.util.Set;

public class Station {

    private final int id;
    private final String name;
    private final Map<ScheduleService.Direction,Set<String>> lines;


    public Station(int id, String name, Map<ScheduleService.Direction,Set<String>> lines) {
        this.id = id;
        this.name = name;
        this.lines = lines;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<ScheduleService.Direction,Set<String>> getLines() {
        return lines;
    }
}
