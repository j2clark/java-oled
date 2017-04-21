package com.j2clark.service;

import com.j2clark.model.I2CDisplay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OledService {

    private static final long RUN_EVERY_50_MILLIS = 50L;

    private final I2CDisplay display;
    private final DisplayState displayState;

    @Autowired
    public OledService (final I2CDisplay display,
                        final DisplayState displayState) throws IOException {

        this.displayState = displayState;
        this.display = display;
    }

    @Scheduled(fixedDelay = RUN_EVERY_50_MILLIS, initialDelay = 1000)
    public void updateDisplay() {
        displayState.getFrame().display(display);
    }

}
