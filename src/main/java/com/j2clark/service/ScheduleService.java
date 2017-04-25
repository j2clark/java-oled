package com.j2clark.service;

import com.j2clark.model.Line;
import com.j2clark.model.Station;
import com.j2clark.model.StationCall;
import com.j2clark.model.StationCallBuilder;
import com.j2clark.model.StationLineup;
import com.j2clark.model.StationTimeTable;
import com.j2clark.model.StationTimeTables;
import com.j2clark.model.TimeOfDay;
import com.j2clark.model.Train;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleService {

    public enum Direction {
        eastbound,
        westbound,
        undefined
    }

    private final ScheduleDAO scheduleDAO;

    @Autowired
    public ScheduleService(final ScheduleDAO scheduleDAO) {
        this.scheduleDAO = scheduleDAO;

        /*// build schedule for S5
        ScheduleDAO.Route s5West = scheduleDAO.findRoute("S5", Direction.westbound, "S5 W WEEKDAY").get(0);
        ScheduleDAO.Route s5east = scheduleDAO.findRoute("S5", Direction.eastbound, "S5 E WEEKDAY").get(0);

        // now lets build up a schedule for 1 train, going back and forth all day
        int start = new TimeOfDay(5, 3, 0).getSecondsOfDay();
        int end = new TimeOfDay(23, 33, 0).getSecondsOfDay();

        TimeOfDay time = new TimeOfDay(start);
        boolean westbound = true;
        while (time.getSecondsOfDay() > end) {

            if (westbound) {
                int index = 0;
                for (ScheduleDAO.RoutePlan plan : s5West.getPlans()) {
                    if (index == 0) {
                        // first station, no arrive time
                    } else if (index == s5West.getPlans().size()) {
                        // end station, no departure
                    } else {

                    }
                    index++;
                }

                // create schedule and add scheduled stops

                scheduleDAO.insertRouteSchedule(schedule);

            } else {

            }

            // add 30 minute wait time in station
            time = time.addMinutes(30);

            // flip direction
            westbound = !westbound;
        }*/



        // show me the complete listing for Marienplatz
        /*for (StationTimeTable timeTable : findTimeTablesByStationAndDirection("Marienplatz", Direction.westbound)
            .sortedByArrival()
            .asList()) {
            TimeOfDay time = timeTable.getArrives();

            System.out.printf("%s - %02d:%02d%n", timeTable.getName(), time.getHourOfDay(),
                              time.getMinute());
        }*/
        scheduleDAO.findAllRoutes();
        scheduleDAO.findAllRouteSchedules();

        System.out.println("***** STATIONS *****");
        for (Station station: scheduleDAO.findStations()) {
            StringBuilder sb = new StringBuilder("[").append(station.getName()).append("]\n");
            for (Direction direction : station.getLines().keySet()) {
                sb.append("\t").append(direction.name().toUpperCase()).append(": ");
                StringBuilder lineb = new StringBuilder();
                for (String line : station.getLines().get(direction)) {
                    lineb.append(line).append(" ");
                }
                sb.append(lineb).append("\n");
            }
            System.out.println(sb);
        }

        System.out.println("\n\n\n***** SCHEDULES *****");
        for (ScheduleDAO.RouteSchedule schedule : scheduleDAO.findAllRouteSchedules()) {
            System.out.println(
                schedule.getLineName() + " (" + schedule.getTrainId() + ") " + schedule
                    .getDirection().name().toUpperCase() + " [" + schedule.getRouteName() + "]");

            for (ScheduleDAO.RoutePlan routePlan : schedule.getPlans()) {
                ScheduleDAO.ScheduledStop stop = (ScheduleDAO.ScheduledStop) routePlan;
                StringBuilder sb = new StringBuilder("\t");
                if (stop.getArrives() == null) {
                    sb.append(" ----- ");
                } else {
                    sb.append(String.format(" %02d:%02d ", stop.getArrives().getHourOfDay(), stop.getArrives().getMinute()));
                }
                sb.append(" ").append(stop.getStationName()).append(" ");
                if (stop.getDeparts() == null) {
                    sb.append(" ---- ");
                } else {
                    sb.append(String.format(" %02d:%02d ", stop.getDeparts().getHourOfDay(), stop.getDeparts().getMinute()));
                }

                System.out.println(sb);
            }

        }
        System.out.println(/*spacer*/);
    }

    public List<Line> findAllLines() {
        return scheduleDAO.findAllLines();
    }

    public Map<Direction,StationTimeTables> findTimeTablesByStation (String station) {
        Map<Direction, List<StationTimeTable>> map = new HashMap<>();

        for (Line line : findAllLines()) {

            String lineName = line.getLineName();

            for (StationLineup stationLineup : line.getLineup()) {

                Direction direction = stationLineup.getDirection();
                StationCall terminalStation = stationLineup.getTerminalStation();
                String trainName = terminalStation.getStation();

                Map<String,Train.Timetable> timetables = new LinkedHashMap<>();
                for (StationCall stationCall : stationLineup.getStationCalls()) {
                    timetables.put(stationCall.getStation(),
                                   new Train.Timetable(stationCall.getStation(),
                                                       stationCall.getArrives(),
                                                       stationCall.getDeparts()));
                }

                Train.Timetable timetable = timetables.get(station);
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

        Map<Direction,StationTimeTables> m = new HashMap<>();
        for (Direction d : map.keySet()) {
            m.put(d, new StationTimeTables(map.get(d)));
        }
        return m;
    }

    public StationTimeTables findTimeTablesByStationAndDirection (String station, Direction direction) {
        Map<Direction, StationTimeTables> stationTimeTables = findTimeTablesByStation(station);
        if (stationTimeTables.containsKey(direction)) {
            return stationTimeTables.get(direction);
        } else {
            return StationTimeTables.empty();
        }
    }
}
