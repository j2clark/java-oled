package com.j2clark.frame;

import java.awt.*;

public abstract class AbstractTextImageFrame extends AbstractGraphics2DFrame {

    protected abstract void renderText(Graphics2D graphics2D);

    private final Font font;

    public AbstractTextImageFrame(final Font font) {
        this.font = font;
    }

    protected final void render(Graphics2D graphics2D) {
        graphics2D.setFont(font);

        renderText(graphics2D);
    }

    public Font getFont() {
        return font;
    }
}
