package com.j2clark.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StationTimeTables {

    private final List<StationTimeTable> timeTables;

    public static StationTimeTables empty() {
        return new StationTimeTables(Collections.emptyList());
    }

    public StationTimeTables(Collection<StationTimeTable> timeTables) {
        if (timeTables != null) {
            this.timeTables = Collections.unmodifiableList(new ArrayList<>(timeTables));
        } else {
            this.timeTables = Collections.emptyList();
        }
    }

    public StationTimeTables currentForTime(TimeOfDay time) {
        List<StationTimeTable> filtered = new ArrayList<>();
        for(StationTimeTable t : timeTables) {
            if (t.getDeparts().after(time)) {
                filtered.add(t);
            }
        }

        return new StationTimeTables(filtered);
    }

    public StationTimeTables sortedByArrival() {
        return new StationTimeTables(timeTables.stream()
            .sorted((o1, o2) -> o1.getArrives().compareTo(o2.getArrives()))
            .collect(Collectors.toList()));
    }

    public List<StationTimeTable> asList() {
        return timeTables;
    }

    public List<StationTimeTable> asList(int limit) {
        return timeTables.stream().limit(3).collect(Collectors.toList());
    }
}
