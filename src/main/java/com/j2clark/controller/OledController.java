package com.j2clark.controller;

import com.j2clark.frame.Frame;
import com.j2clark.model.Station;
import com.j2clark.service.DisplayState;
import com.j2clark.service.ScheduleDAO;
import com.j2clark.service.ScheduleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OledController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DisplayState displayState;
    private final Frame platformFrame;
    private final Frame whoamiFrame;
    private final ScheduleService scheduleService;

    @Autowired
    public OledController(
        final ScheduleService scheduleService,
        final DisplayState displayState,

        // todo: create a frame registry
        @Qualifier("platformFrame") final Frame platformFrame,
        @Qualifier("whoamiFrame") final Frame whoamiFrame) {

        this.scheduleService = scheduleService;
        this.displayState = displayState;
        this.platformFrame = platformFrame;
        this.whoamiFrame = whoamiFrame;
    }

    @RequestMapping("/routes")
    public List<ScheduleDAO.Route> getRoutes() {
        return scheduleService.findAllRoutes();
    }

    @RequestMapping("/schedules")
    public List<ScheduleDAO.RouteSchedule> getRouteSchedules() {
        return scheduleService.findAllRouteSchedules();
    }

    @RequestMapping("/stations")
    public List<Station> getStations() {
        return scheduleService.findAllStations();
    }

    @RequestMapping("/platform")
    public String station() {

        this.displayState.setFrame(platformFrame);

        return "OK";
    }

    @RequestMapping("/whoami")
    public String whois() throws Exception {

        this.displayState.setFrame(whoamiFrame);

        return "OK";
    }
}
