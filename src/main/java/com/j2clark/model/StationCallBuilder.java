package com.j2clark.model;

public class StationCallBuilder {

    private String station;
    private String line;
    private String train;
    private boolean origin;
    private TimeOfDay arrives;
    private boolean terminal;
    private TimeOfDay departs;



    public static StationCallBuilder origin() {
        return new StationCallBuilder(true, false);
    }

    public static StationCallBuilder terminal() {
        return new StationCallBuilder(false, true);
    }

    public static StationCallBuilder instance() {
        return new StationCallBuilder(false, false);
    }

    private StationCallBuilder(boolean origin, boolean terminal) {
        this.origin = origin;
        this.terminal = terminal;
    }

    public StationCall build() {
        return new StationCall(station, line, train, origin, arrives, terminal, departs);
    }

    public StationCallBuilder withStation(String station) {
        this.station = station;
        return this;
    }

    public StationCallBuilder withLine(String line) {
        this.line = line;
        return this;
    }

    public StationCallBuilder withTrain(String train) {
        this.train = train;
        return this;
    }

    public StationCallBuilder withArrives(int hour) {
        return withArrives(hour, 0);
    }

    public StationCallBuilder withArrives(int hour, int min) {
        return withArrives(hour, min, 0);
    }

    public StationCallBuilder withArrives(int hour, int min, int sec) {
        return withArrives(new TimeOfDay(hour, min, sec));
    }

    public StationCallBuilder withArrives(TimeOfDay arrives) {
        this.arrives = arrives;
        return this;
    }

    public StationCallBuilder withDeparts(int hour) {
        return withDeparts(hour, 0);
    }

    public StationCallBuilder withDeparts(int hour, int min) {
        return withDeparts(hour, min, 0);
    }

    public StationCallBuilder withDeparts(int hour, int min, int sec) {
        return withDeparts(new TimeOfDay(hour, min, sec));
    }

    public StationCallBuilder withDeparts(TimeOfDay departs) {
        this.departs = departs;
        return this;
    }
}
