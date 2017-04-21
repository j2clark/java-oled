package com.j2clark;

import eu.ondryaso.ssd1306.Display;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.j2clark.model.I2CDisplay;
import com.j2clark.model.I2CDisplayFactory;
import com.j2clark.service.DisplayState;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.wiringpi.I2C;

import java.awt.*;
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
    public Display display(I2CDisplayFactory i2CDisplayFactory) throws Exception {

        I2CDisplay display;
        try {
            display = i2CDisplayFactory.newInstance();
            display.begin();

            String[] data = new String[] {"Hello World", "Line 2", "Line 3"};

            Graphics2D graphics = display.getGraphics();
            int FONT_WEIGHT = 10;
            graphics.setFont(new Font("Monospaced", Font.PLAIN, FONT_WEIGHT));



            for (int i = 0; i < data.length; i++) {
                graphics.drawString(data[i], 0,
                                    FONT_WEIGHT * (i + 1));
            }

            graphics.drawRect(0,
                                           0,
                                           display.getWidth() - 1,
                                           display.getHeight() - 1);

            // tellme: what is diff between display and displayImage
            display.displayImage();

            display.display();

        } catch (I2CFactory.UnsupportedBusNumberException | IOException e) {
            e.printStackTrace();
        }

        Display disp = null;

        /*
        Display disp = new Display(128, 64, GpioFactory.getInstance(),
                                   SpiFactory.getInstance(SpiChannel.CS0, 8000000), RaspiPin.GPIO_04);
        // Create 128x64 display on CE1 (change to SpiChannel.CS0 for using CE0) with D/C pin on WiringPi pin 04

        disp.begin();
        // Init the display

        Image i = ImageIO.read(BasicGraphics.class.getResourceAsStream("lord.png"));
        disp.getGraphics().setColor(Color.WHITE);
        disp.getGraphics().drawImage(i, 0, 0, null);
        disp.getGraphics().setFont(new Font("Monospaced", Font.PLAIN, 10));
        disp.getGraphics().drawString("Praise him", 64, 60);
        disp.getGraphics().drawRect(0, 0, disp.getWidth() - 1, disp.getHeight() - 1);
        // Deal with the image using AWT

        disp.displayImage();
        // Copy AWT image to an inner buffer and send to the display

        for(int x = 70; x < 90; x += 2) {
            for(int y = 10; y < 30; y += 2) {
                disp.setPixel(x, y, true);
            }
        }
        // Set some pixels in the buffer manually

        disp.display();
        // Send the buffer to the display again, now with the modified pixels

        return disp;*/
        return null;
    }
}