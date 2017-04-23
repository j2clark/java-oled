package com.j2clark.frame;

import java.awt.*;

public class GraphicsUtil {

    public static void drawCenteredString(Graphics2D g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public static String abbreviate(String str, FontMetrics fm, int width) {
        int lastblank = 0, nchars = 0, cumx = 0;
        while ( cumx < width &&  nchars < str.length() ) {
            if ( Character.isWhitespace(str.charAt(nchars)) ) {
                lastblank = nchars;
            }
            cumx += fm.charWidth(str.charAt(nchars));
            nchars++;
        }
        if ( nchars < str.length() && lastblank > 0 ) { nchars = lastblank; }
        return ( nchars > 0 ? str.substring(0, nchars) : str );
    }
}
