package com.j2clark.service;

import com.j2clark.frame.PlatformFrame;
import com.j2clark.model.Line;
import com.j2clark.model.LineBuilder;
import com.j2clark.model.StationCall;
import com.j2clark.model.StationCallBuilder;
import com.j2clark.model.StationLineup;
import com.j2clark.model.StationLineupBuilder;
import com.j2clark.model.StationTime;
import com.j2clark.model.Train;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    // returns a list of trains currently running
    private List<Line> lines;

    private static class Feed {
        private final String line;
        private final String origin;
        private final String terminal;

        private Feed(String line, String origin, String terminal) {
            this.line = line;
            this.origin = origin;
            this.terminal = terminal;
        }
    }

    public ScheduleService () {

        List<Feed> feeds = Arrays.asList(
            new Feed("S2", "Holzkirchen", "Peterhausen"),
            new Feed("S4", "Leuchtenbergring", "Geltendorf"),
            new Feed("S5", "Ebersberg", "Herrsching"),
            new Feed("S6", "Erding", "Tutzing"),
            new Feed("S7", "Kreuzstrasse", "Wolfratzhausen")
        );

        this.lines = new ArrayList<>();



        for (Feed feed : feeds) {

            StationTime time = new StationTime(15, 3, 0);
            StationTime last = new StationTime(23, 33, 0);
            String line = feed.line;
            if ("S4".equals(line)) {
                time = time.addMinutes(2);
                last = last.addMinutes(2);
            } else if ("S5".equals(line)) {
                time = time.addMinutes(4);
                last = last.addMinutes(4);
            } else if ("S6".equals(line)) {
                time = time.addMinutes(6);
                last = last.addMinutes(6);
            } else if ("S7".equals(line)) {
                time = time.addMinutes(8);
                last = last.addMinutes(8);
            }

            LineBuilder lineBuilder = new LineBuilder(line);

            while (!time.after(last)) {

                StationTime departsOrigin = time.subtractMinutes(30);
                StationTime arrivesTerminal = time.addMinutes(30);

                lineBuilder.withStationLineups(
                    new StationLineupBuilder("westbound")
                        .withStationCalls(
                            StationCallBuilder.origin().withLine(line).withStation(feed.origin)
                                .withTrain("1").withDeparts(departsOrigin).build(),
                            StationCallBuilder.instance().withLine(line).withStation("Marienplatz")
                                .withTrain("1").withArrives(time)
                                .withDeparts(time.addSeconds(15)).build(),
                            StationCallBuilder.terminal().withLine(line).withStation(feed.terminal)
                                .withTrain("1").withArrives(arrivesTerminal).build()
                        )
                        .build()
                );

                time = time.addMinutes(15);

            }

            lines.add(lineBuilder.build());
        }

        // show me the complete listing for Marienplatz
        List<PlatformFrame.StationTimeTable> trains = findAllTrains().asTrains().asStationTimeTables("Marienplatz", "westbound");
        for (PlatformFrame.StationTimeTable train : trains) {

            StationTime time = train.getArrives();

            System.out
                .printf("%s - %02d:%02d%n", train.getName(), time.getHourOfDay(), time.getMinute());
        }
    }

    public ScheduleSearchResult findAllTrains() {
        return new ScheduleSearchResult(lines);
    }


    public static class Trains {

        private final List<Train> trains;

        public Trains(Collection<Train> trains) {
            this.trains = Collections.unmodifiableList(new ArrayList<>(trains));
        }

        /*public Trains filterByLine(String line) {
            List<Train> filtered = new ArrayList<>();
            if (!StringUtils.isEmpty(line)) {
                for (Train train : trains) {
                    if (train.getLine().equals(line)) {
                        filtered.add(train);
                    }
                }
            }
            return new Trains(filtered);
        }*/

        /*public boolean isRunning(Train train, StationTime time) {
            return train.getStarts().before(time) && train.getEnds().after(time);
        }*/

        /*public Trains currentForStation(String station) {
            List<Train> filtered = new ArrayList<>();
            if (!StringUtils.isEmpty(station)) {
                StationTime now = new StationTime();
                for (Train train : trains) {
                    if (isRunning(train, now)) {
                        Train.Timetable timetable = train.getTimetables().get(station);
                        if (timetable != null) {
                            if (timetable.getDeparts().after(now)) {
                                filtered.add(train);
                            }
                        }
                    }
                }
            }
            return new Trains(filtered);
        }*/

        /*public Trains filterByDirection(String direction) {
            List<Train> filtered = new ArrayList<>();
            if (!StringUtils.isEmpty(direction)) {
                for (Train train : trains) {
                    if (train.getDirection().equals(direction)) {
                        filtered.add(train);
                    }
                }
            }
            return new Trains(filtered);
        }*/

        /*public Trains stopsInStation(String station) {
            List<Train> filtered = new ArrayList<>();
            if (!StringUtils.isEmpty(station)) {
                for (Train train : trains) {
                    if (train.getTimetables().containsKey(station)) {
                        filtered.add(train);
                    }
                }
            }
            return new Trains(filtered);
        }

        public Trains orderByArrivalAtStation(String station) {

            List<Train> results = trains.stream().sorted((o1, o2) -> o1.getTimetables().get(station).getArrives().compareTo(o2.getTimetables().get(station).getArrives())).collect(Collectors.toList());

            return new Trains(trains);
        }*/

        public Trains limit(int count) {
            return new Trains(trains.stream().limit(count).collect(Collectors.toList()));
        }

        public List<Train> asList() {
            return trains;
        }

        public List<PlatformFrame.StationTimeTable> asStationTimeTables(String stationName, String dir) {
            Map<String, List<PlatformFrame.StationTimeTable>> map = new HashMap<>();
            StationTime now = new StationTime();
            for (Train train : trains) {

                Train.Timetable timetable = train.getTimetables().get(stationName);
                if (timetable.getDeparts().after(now)) {
                    String direction = train.getDirection();
                    java.util.List<PlatformFrame.StationTimeTable> list = map.get(direction);
                    if (list == null) {
                        list = new ArrayList<>();
                        map.put(direction, list);
                    }

                    list.add(new PlatformFrame.StationTimeTable(train.getLine(), train.getName(), timetable.getArrives(), timetable.getDeparts()));
                }
            }

            // I only care about westbound for now
            if (map.containsKey(dir)) {
                return map.get(dir).stream()
                        .sorted((o1, o2) -> o1.getArrives().compareTo(o2.getArrives()))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        }
    }


    public static class ScheduleSearchResult {

        private final Map<String,Line> lineMap = new HashMap<>();

        public ScheduleSearchResult(List<Line> lines) {
            for (Line line : lines) {
                lineMap.put(line.getLineName(), line);
            }
        }


        public Trains asTrains() {

            List<Train> trains = new ArrayList<>();
            for (Line line : lineMap.values()) {

                String lineName = line.getLineName();

                for (StationLineup stationLineup : line.getLineup()) {

                    String direction = stationLineup.getDirection();
                    StationCall originStation = stationLineup.getOriginStation();
                    StationCall terminalStation = stationLineup.getTerminalStation();
                    String trainName = terminalStation.getStation();

                    Map<String,Train.Timetable> timetables = new LinkedHashMap<>();
                    for (StationCall stationCall : stationLineup.getStationCalls()) {
                        timetables.put(stationCall.getStation(), new Train.Timetable(stationCall.getStation(), stationCall.getArrives(), stationCall.getDeparts()));
                    }

                    trains.add(new Train(lineName, direction, trainName, originStation.getDeparts(), terminalStation.getArrives(), timetables));
                }
            }

            return new Trains(trains);
        }
    }


}
