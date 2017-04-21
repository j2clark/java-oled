package com.j2clark.service;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiFactory;
import eu.ondryaso.ssd1306.Display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OledService {

    private static final long RUN_EVERY_100_MILLIS = 100l;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DisplayState displayState;

    @Autowired
    public OledService (final DisplayState displayState) {
        this.displayState = displayState;
    }

    @Scheduled(fixedDelay = RUN_EVERY_100_MILLIS, initialDelay = 1000)
    public void updateDisplay() {

        logger.info("refreshing display");

        // display state changed externally, and refreshed here

        //display.refresh(displayState);

    }

}
