package com.j2clark.service;

import com.j2clark.model.I2CDisplay;
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

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OledService {

    private static final long RUN_EVERY_100_MILLIS = 100l;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final I2CDisplay display;
    private final DisplayState displayState;

    @Autowired
    public OledService (final I2CDisplay display,
                        final DisplayState displayState) throws IOException {

        this.displayState = displayState;
        this.display = display;

        // initialize display object - here or when we instantiate?
        display.begin();
    }

    @Scheduled(fixedDelay = RUN_EVERY_100_MILLIS, initialDelay = 1000)
    public void updateDisplay() {

        logger.info("refreshing display");


        String[] data = new String[] {"Hello World", "Line 2", "Line 3"};

        Graphics2D graphics = display.getGraphics();
        int FONT_WEIGHT = 10;
        graphics.setFont(new Font("Monospaced", Font.PLAIN, FONT_WEIGHT));
        for (int i = 0; i < data.length; i++) {
            graphics.drawString(data[i], 0, FONT_WEIGHT * (i + 1));
        }

        graphics.drawRect(0,
                          0,
                          display.getWidth() - 1,
                          display.getHeight() - 1);

        // tellme: what is diff between display and displayImage
        display.displayImage();

        display.display();


        // display state changed externally, and refreshed here

        //display.refresh(displayState);

    }

}
