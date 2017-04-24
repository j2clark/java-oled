package com.j2clark.service;

import com.j2clark.model.Line;
import com.j2clark.model.LineBuilder;
import com.j2clark.model.StationCall;
import com.j2clark.model.StationCallBuilder;
import com.j2clark.model.StationLineup;
import com.j2clark.model.StationLineupBuilder;
import com.j2clark.model.StationTime;
import com.j2clark.model.StationTimeTable;
import com.j2clark.model.StationTimeTables;
import com.j2clark.model.Train;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public ScheduleService() {

        List<Feed> feeds = Arrays.asList(
            new Feed("S2", "Holzkirchen", "Peterhausen"),
            new Feed("S4", "Leuchtenbergring", "Geltendorf"),
            new Feed("S5", "Ebersberg", "Herrsching"),
            new Feed("S6", "Erding", "Tutzing"),
            new Feed("S7", "Kreuzstrasse", "Wolfratzhausen")
        );

        this.lines = new ArrayList<>();

        for (Feed feed : feeds) {

            StationTime time = new StationTime(5, 3, 0);
            StationTime last = new StationTime(23, 33, 0);
            String line = feed.line;
            int step = 0;
            if ("S4".equals(line)) {
                step = 1;
            } else if ("S5".equals(line)) {
                step = 2;
            } else if ("S6".equals(line)) {
                step = 3;
            } else if ("S7".equals(line)) {
                step = 4;
            }

            int minutesToAdd = step * 3;
            time = time.addMinutes(minutesToAdd);
            last = last.addMinutes(minutesToAdd);

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

        for (StationTimeTable timeTable : new StationTimeTables(
            findAllTrains().getStationTimeTables("Marienplatz", "westbound"))
            .sortedByArrival()
            .asList()) {
            StationTime time = timeTable.getArrives();

            System.out.printf("%s - %02d:%02d%n", timeTable.getName(), time.getHourOfDay(),
                              time.getMinute());
        }
        //}
    }

    public ScheduleSearchResult findAllTrains() {
        return new ScheduleSearchResult (lines);
    }

    public static class ScheduleSearchResult {

        private final Map<String,Line> lineMap = new HashMap<>();

        public ScheduleSearchResult (List<Line> lines) {
            for (Line line : lines) {
                lineMap.put(line.getLineName(), line);
            }
        }

        public List<StationTimeTable> getStationTimeTables(String stationName, String direction) {
            Map<String, List<StationTimeTable>> m = getStationTimeTables(stationName);
            if (m.containsKey(direction)) {
                return m.get(direction);
            } else {
                return Collections.emptyList();
            }
        }

        public Map<String, List<StationTimeTable>> getStationTimeTables(String stationName) {
            Map<String, List<StationTimeTable>> map = new HashMap<>();

            for (Line line : lineMap.values()) {

                String lineName = line.getLineName();

                for (StationLineup stationLineup : line.getLineup()) {

                    String direction = stationLineup.getDirection();
                    StationCall terminalStation = stationLineup.getTerminalStation();
                    String trainName = terminalStation.getStation();

                    Map<String,Train.Timetable> timetables = new LinkedHashMap<>();
                    for (StationCall stationCall : stationLineup.getStationCalls()) {
                        timetables.put(stationCall.getStation(),
                                       new Train.Timetable(stationCall.getStation(),
                                                           stationCall.getArrives(),
                                                           stationCall.getDeparts()));
                    }

                    Train.Timetable timetable = timetables.get(stationName);
                    //if (timetable.getDeparts().after(now)) {
                    List<StationTimeTable> list = map.get(direction);
                    if (list == null) {
                        list = new ArrayList<>();
                        map.put(direction, list);
                    }

                    list.add(new StationTimeTable(lineName,
                                                  trainName,
                                                  timetable.getArrives(),
                                                  timetable.getDeparts()));
                }
            }

            return map;
        }
    }


}
