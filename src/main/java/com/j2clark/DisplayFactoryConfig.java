package com.j2clark;

import com.j2clark.frame.Frame;
import com.j2clark.model.I2CDisplay;
import com.j2clark.model.I2CDisplayFactory;
import com.j2clark.service.DisplayState;
import com.pi4j.io.i2c.I2CFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DisplayFactoryConfig {

    @Bean
    public I2CDisplayFactory i2CDisplayFactory(Environment env) {
        Set<String> pset = new HashSet<>();
        if (env.getActiveProfiles() != null) {
            pset = new HashSet<>(Arrays.asList(env.getActiveProfiles()));
        }

        if (pset.contains("dev")) {
            return new I2CDisplayFactory.MockI2CDisplayFactory();
        } else {
            return new I2CDisplayFactory.I2CDisplayFactoryImpl();
        }
    }

    @Bean
    public I2CDisplay display(I2CDisplayFactory i2CDisplayFactory) throws IOException, I2CFactory.UnsupportedBusNumberException {
        I2CDisplay display = i2CDisplayFactory.newInstance();
        display.begin();
        return display;
    }

    @Bean
    public Dimension displayDimensions(I2CDisplay display) {
        return new Dimension(display.getWidth(), display.getHeight());
    }

    @Bean
    public DisplayState displayState(@Qualifier("platformFrame") Frame frame) {
        return new DisplayState(frame);
    }
}
