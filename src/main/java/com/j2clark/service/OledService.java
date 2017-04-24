package com.j2clark.service;

import com.j2clark.model.I2CDisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OledService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final I2CDisplay display;
    private final DisplayState displayState;

    @Autowired
    public OledService (final I2CDisplay display,
                        final DisplayState displayState) throws IOException {

        this.displayState = displayState;
        this.display = display;
    }

    //@Scheduled(fixedDelay = 200, initialDelay = 1000)
    @Scheduled(cron = "*/1 * * * * *") // run every second - much smoother display
    public void updateDisplay() {
        long start = System.currentTimeMillis();
        displayState.getFrame().display(display);
        long end = System.currentTimeMillis();

        logger.info("render["+(end-start)+"] (ms)");
    }

}
