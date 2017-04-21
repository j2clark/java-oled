package com.j2clark.frame;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockFrame extends AbstractTextImageFrame {

    public final DateFormat df = new SimpleDateFormat("HH:mm:ss");

    public ClockFrame() {
        this(new Font("Monospaced", Font.PLAIN, 10));
    }

    public ClockFrame(Font font) {
        super(font);
    }

    @Override
    protected void renderText(Graphics2D graphics) {

        String time = df.format(new Date());

        int x = 0;
        int y = getFont().getSize(); // 0 + font height

        graphics.drawString(time, x, y);

    }

    @Override
    public String asString() {
        return df.format(new Date());
    }
}
