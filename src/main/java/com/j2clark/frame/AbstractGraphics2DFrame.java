package com.j2clark.frame;

import com.j2clark.model.I2CDisplay;

import java.awt.*;

public abstract class AbstractGraphics2DFrame implements Frame {

    protected abstract void render(Graphics2D graphics);

    @Override
    public final void display(I2CDisplay display) {
        display.clearImage();

        render(display.getGraphics());

        display.displayImage();
    }
}
