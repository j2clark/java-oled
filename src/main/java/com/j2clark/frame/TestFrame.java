package com.j2clark.frame;

import com.j2clark.model.I2CDisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestFrame implements Frame {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<String> data;

    public TestFrame(Collection<String> data) {
        this.data = new ArrayList<>(data);
    }

    public void display(I2CDisplay display) {

        display.clearImage();

        Graphics2D graphics = display.getGraphics();
        int FONT_WEIGHT = 10;
        graphics.setFont(new Font("Monospaced", Font.PLAIN, FONT_WEIGHT));
        int i = 0;
        for (String s : data) {
            logger.info("display["+i+"] " + s);
            graphics.drawString(s, 0, FONT_WEIGHT * (i++ + 1));
        }

        graphics.drawRect(0,
                          0,
                          display.getWidth() - 1,
                          display.getHeight() - 1);

        // tellme: what is diff between display and displayImage
        display.displayImage(); // calls display() after raster
    }

    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();
        for (String s : data) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(s);
        }
        return sb.toString();
    }

}
