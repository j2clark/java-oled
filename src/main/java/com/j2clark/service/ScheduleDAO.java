package com.j2clark.service;

import com.j2clark.model.Line;
import com.j2clark.model.LineBuilder;
import com.j2clark.model.Station;
import com.j2clark.model.StationCallBuilder;
import com.j2clark.model.StationLineupBuilder;
import com.j2clark.model.TimeOfDay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

@Component
public class ScheduleDAO {

    // returns a list of trains currently running
    private List<Line> lines;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ScheduleDAO(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        List<Feed> feeds = Arrays.asList(
            new Feed("S2", "Holzkirchen", "Peterhausen"),
            new Feed("S4", "Leuchtenbergring", "Geltendorf"),
            new Feed("S5", "Ebersberg", "Herrsching"),
            new Feed("S6", "Erding", "Tutzing"),
            new Feed("S7", "Kreuzstrasse", "Wolfratzhausen")
        );

        this.lines = new ArrayList<>();

        for (Feed feed : feeds) {

            TimeOfDay time = new TimeOfDay(5, 3, 0);
            TimeOfDay last = new TimeOfDay(23, 33, 0);
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

                TimeOfDay departsOrigin = time.subtractMinutes(30);
                TimeOfDay arrivesTerminal = time.addMinutes(30);

                lineBuilder.withStationLineups(
                    new StationLineupBuilder(ScheduleService.Direction.westbound)
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
    }

    public List<Line> findAllLines() {
        return lines;
    }

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

    public List<Station> findStations() {

        String sql =
            "select\n"
            + "s.id,\n"
            + "s.name,\n"
            + "r.line_name,\n"
            + "r.direction\n"
            + "from \n"
            + "route_plan rp \n"
            + "join station s on rp.station_id = s.id\n"
            + "join route r on rp.route_id = r.id \n"
            + "group by s.name, r.line_name, r.direction\n"
            + "order by\n"
            + "s.id, r.line_name, r.direction"
            ;

        return jdbcTemplate.query(sql, rs -> {

            List<Station> stations = new ArrayList<Station>();

            Integer lastStationId = null;
            String name = null;

            Map<ScheduleService.Direction,Set<String>> lines = new LinkedHashMap<>();
            while(rs.next()) {
                int stationId = rs.getInt("id");
                if (lastStationId != null && !lastStationId.equals(stationId)) {
                    stations.add(new Station(lastStationId, name, lines));
                    lines = new LinkedHashMap<>();
                    lastStationId = stationId;
                } else if (lastStationId == null) {
                    lastStationId = stationId;
                }

                String dirStr = rs.getString("direction");
                ScheduleService.Direction direction;
                if ("W".equalsIgnoreCase(dirStr)) {
                    direction = ScheduleService.Direction.westbound;
                } else if ("E".equalsIgnoreCase(dirStr)) {
                    direction = ScheduleService.Direction.eastbound;
                } else {
                    direction = ScheduleService.Direction.undefined;
                }

                Set<String> directionalTrains = lines.get(direction);
                if (directionalTrains == null) {
                    directionalTrains = new LinkedHashSet<>();
                    lines.put(direction, directionalTrains);
                }
                directionalTrains.add(rs.getString("line_name"));

                name = rs.getString("name");
            }

            if (lastStationId != null) {
                stations.add(new Station(lastStationId, name, lines));
            }

            return stations;
        });
    }

    public List<Route> findRoute(String lineName, ScheduleService.Direction direction, String routeName) {



        return jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection cn)
                throws SQLException {

                String d = "";
                if (ScheduleService.Direction.westbound.equals(direction)) {
                    d = "W";
                } else if (ScheduleService.Direction.westbound.equals(direction)) {
                    d = "E";
                }

                String sql = ROUTE_SQL
                    + " where "
                    + " upper(r.line_name) = upper(?) "
                    + " and r.direction = ? "
                    + " and upper(r.name) = upper(?)  "
                ;

                PreparedStatement ps = cn.prepareStatement(sql);

                ps.setString(1, lineName.trim());
                ps.setString(2, d);
                ps.setString(3, routeName.trim());

                return ps;
            }
        }, new RouteExtractor());
    }

    public List<Route> findAllRoutes() {

        String sql = ROUTE_SQL
            + " ORDER BY\n"
            + "p.route_id, p.order_num";

        return jdbcTemplate.query(sql, new RouteExtractor());
    }

    public List<RouteSchedule> findAllRouteSchedules() {

        String sql =
            "select\n"
            + "ss.*,\n"
            + "s.train_id,\n"
            + "s.order_num as schedule_order,\n"
            + "rp.order_num as station_order,\n"
            + "sta.name as station_name,\n"
            + "r.direction,\n"
            + "r.line_name,\n"
            + "r.name as route_name\n"
            + "from scheduled_stop ss\n"
            + "join schedule s on ss.schedule_id = s.id\n"
            + "join route_plan rp on ss.route_id = rp.route_id and ss.station_id = rp.station_id\n"
            + "join station sta on ss.station_id = sta.id\n"
            + "join route r on ss.route_id = r.id\n"
            + "order by\n"
            + "ss.route_id,\n"
            + "s.id,\n"
            + "--s.order_num,\n"
            + "rp.order_num"
            ;

        return jdbcTemplate.query(sql, new RouteScheduleExtractor());
    }



    public static class RouteSchedule extends Route {

        private final int scheduleId;
        private final int trainId;
        private final int scheduleOrder;

        public RouteSchedule(int scheduleId, int trainId, int routeId, int scheduleOrder, ScheduleService.Direction direction, String lineName,
                             String routeName, List<RoutePlan> plans) {
            super(routeId, direction, lineName, routeName, plans);

            this.scheduleId = scheduleId;
            this.trainId = trainId;
            this.scheduleOrder = scheduleOrder;
        }

        public int getScheduleId() {
            return scheduleId;
        }

        public int getTrainId() {
            return trainId;
        }

        public int getScheduleOrder() {
            return scheduleOrder;
        }
    }

    public static class ScheduledStop extends RoutePlan {

        private final int scheduleId;
        private final TimeOfDay arrives;
        private final TimeOfDay departs;

        public ScheduledStop(int scheduleId, int routeId, int stationId, TimeOfDay arrives, TimeOfDay departs, String stationName, int stationOrder) {
            super(routeId, stationId, stationName, stationOrder);

            this.scheduleId = scheduleId;
            this.arrives = arrives;
            this.departs = departs;
        }

        public int getScheduleId() {
            return scheduleId;
        }

        public TimeOfDay getArrives() {
            return arrives;
        }

        public TimeOfDay getDeparts() {
            return departs;
        }
    }

    public static class RoutePlan {
        private final int routeId;
        private final int stationId;
        private final String stationName;
        private final int stationOrder;

        public RoutePlan(int routeId, int stationId, String stationName, int stationOrder) {
            this.routeId = routeId;
            this.stationId = stationId;
            this.stationName = stationName;
            this.stationOrder = stationOrder;
        }

        public int getRouteId() {
            return routeId;
        }

        public int getStationId() {
            return stationId;
        }

        public String getStationName() {
            return stationName;
        }

        public int getStationOrder() {
            return stationOrder;
        }
    }

    public static class Route {
        private final int routeId;
        private final ScheduleService.Direction direction;
        private final String lineName;
        private final String routeName;
        private final List<RoutePlan> plans;

        public Route(int routeId, ScheduleService.Direction direction, String lineName,
                     String routeName, List<RoutePlan> plans) {
            this.routeId = routeId;
            this.direction = direction;
            this.lineName = lineName;
            this.routeName = routeName;
            this.plans = plans;
        }

        public int getRouteId() {
            return routeId;
        }

        public ScheduleService.Direction getDirection() {
            return direction;
        }

        public String getLineName() {
            return lineName;
        }

        public String getRouteName() {
            return routeName;
        }

        public List<RoutePlan> getPlans() {
            return plans;
        }
    }

    public static class RouteScheduleExtractor implements ResultSetExtractor<List<RouteSchedule>> {

        protected TimeOfDay parseTime(String s) {
            if (!StringUtils.isEmpty(s)) {

                String[] parts = s.split(":");
                if (parts.length != 3) {
                    throw new IllegalArgumentException(
                        "Unexpected format {" + s + "}, expecting [00:00:00]");
                }

                int hour = Integer.parseInt(parts[0]);
                int min = Integer.parseInt(parts[1]);
                int sec = Integer.parseInt(parts[2]);

                return new TimeOfDay(hour, min, sec);
            } else {
                return null;
            }
        }

        protected ScheduledStop newScheduledStop(Integer scheduleId, ResultSet rs)
            throws SQLException {

            int routeId = rs.getInt("route_id");
            int stationId = rs.getInt("station_id");
            String stationName = rs.getString("station_name");
            int stationOrder = rs.getInt("station_order");
            TimeOfDay arrives = parseTime(rs.getString("arrives"));
            TimeOfDay departs = parseTime(rs.getString("departs"));

            return new ScheduledStop(scheduleId, routeId, stationId, arrives, departs, stationName, stationOrder);
        }

        protected RouteSchedule newRouteSchedule(Integer scheduleId, Integer trainId, Integer routeId, Integer scheduleOrder, String directionStr, String lineName, String routeName, List<RoutePlan> plans) {
            ScheduleService.Direction direction;
            if ("W".equalsIgnoreCase(directionStr)) {
                direction = ScheduleService.Direction.westbound;
            } else if ("E".equalsIgnoreCase(directionStr)) {
                direction = ScheduleService.Direction.eastbound;
            } else {
                direction = ScheduleService.Direction.undefined;
            }
            return new RouteSchedule(scheduleId, trainId, routeId, scheduleOrder, direction, lineName, routeName, plans);
        }

        @Override
        public List<RouteSchedule> extractData(ResultSet rs)
            throws SQLException, DataAccessException {

            List<RouteSchedule> routes = new ArrayList<>();

            Integer lastScheduleId = null;
            List<RoutePlan> scheduledStops = new ArrayList<>();

            // keep around for post row usage
            Integer scheduleId = null;
            Integer trainId = null;
            Integer routeId = null;
            Integer scheduleOrder = null;
            String directionStr = null;
            String lineName = null;
            String routeName = null;

            while(rs.next()) {

                scheduleId = rs.getInt("schedule_id");

                if (lastScheduleId != null && !scheduleId.equals(lastScheduleId)) {
                    // new row - clean up old row if required
                    if (!scheduledStops.isEmpty()) {
                        routes.add(newRouteSchedule(lastScheduleId, trainId, routeId, scheduleOrder,
                                                    directionStr, lineName, routeName,
                                                    scheduledStops));
                    }
                    scheduledStops = new ArrayList<>();
                    lastScheduleId = scheduleId;
                } else if (lastScheduleId == null) {
                    lastScheduleId = scheduleId;
                }
                scheduledStops.add(newScheduledStop(scheduleId, rs));

                // save for next iteration
                trainId = rs.getInt("train_id");
                scheduleOrder = rs.getInt("schedule_order");
                routeId = rs.getInt("route_id");
                directionStr = rs.getString("direction");
                lineName = rs.getString("line_name");
                routeName = rs.getString("route_name");
            }

            if (scheduleId != null) {
                // add final entry
                routes.add(newRouteSchedule(scheduleId, trainId, routeId, scheduleOrder, directionStr, lineName, routeName,
                                            scheduledStops));
            }

            return routes;

        }
    }

    public static class RouteExtractor implements ResultSetExtractor<List<Route>> {

        protected RoutePlan newRoutePlan(Integer routeId, ResultSet rs)
            throws SQLException {

            int stationId = rs.getInt("station_id");
            String stationName = rs.getString("station_name");
            int stationOrder = rs.getInt("order_num");


            return new RoutePlan(routeId, stationId, stationName, stationOrder);
        }

        protected Route newRoute(Integer routeId, String directionStr, String lineName, String routeName, List<RoutePlan> plans) {
            ScheduleService.Direction direction;
            if ("W".equalsIgnoreCase(directionStr)) {
                direction = ScheduleService.Direction.westbound;
            } else if ("E".equalsIgnoreCase(directionStr)) {
                direction = ScheduleService.Direction.eastbound;
            } else {
                direction = ScheduleService.Direction.undefined;
            }
            return new Route(routeId, direction, lineName, routeName, plans);
        }

        @Override
        public List<Route> extractData(ResultSet rs)
            throws SQLException, DataAccessException {

            List<Route> routes = new ArrayList<>();

            Integer lastRouteId = null;
            List<RoutePlan> plans = new ArrayList<>();

            Integer routeId = null;
            String directionStr = null;
            String lineName = null;
            String routeName = null;

            while(rs.next()) {

                routeId = rs.getInt("route_id");

                if (lastRouteId != null && !routeId.equals(lastRouteId)) {
                    // new row - clean up old row if required
                    if (!plans.isEmpty()) {

                        routes.add(newRoute(routeId, directionStr, lineName, routeName, plans));
                    }
                    plans = new ArrayList<>();
                } else if (lastRouteId == null) {
                    lastRouteId = routeId;
                }

                plans.add(newRoutePlan(routeId, rs));

                // save for next iteration
                directionStr = rs.getString("direction");
                lineName = rs.getString("line_name");
                routeName = rs.getString("name");
            }

            if (routeId != null) {
                // add final entry
                routes.add(newRoute(routeId, directionStr, lineName, routeName, plans));
            }

            return routes;
        }
    }

    private static final String ROUTE_SQL =
        "SELECT \n"
        + "s.name as station_name,\n"
        + "r.direction,\n"
        + "r.line_name,\n"
        + "r.name,\n"
        + "p.order_num,\n"
        + "p.route_id,\n"
        + "p.station_id\n"
        + "FROM ROUTE_PLAN p \n"
        + "join ROUTE r ON p.route_id = r.id\n"
        + "join STATION s ON p.station_id = s.id\n";
        //+ "ORDER BY\n"
        //+ "p.route_id, p.order_num";
}
