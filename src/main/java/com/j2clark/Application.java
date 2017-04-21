package com.j2clark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.j2clark.model.I2CDisplay;
import com.j2clark.model.I2CDisplayFactory;
import com.j2clark.service.DisplayState;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public DisplayState displayState() {
        return new DisplayState();
    }

    @Bean
    public I2CDisplay display(I2CDisplayFactory i2CDisplayFactory) throws IOException, I2CFactory.UnsupportedBusNumberException {
        I2CDisplay display = i2CDisplayFactory.newInstance();
        //display.begin();
        return display;
    }
}