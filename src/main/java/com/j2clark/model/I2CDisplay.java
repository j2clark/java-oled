package com.j2clark.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface I2CDisplay {


    /**
     * Begin with SWITCHCAPVCC VCC mode
     * @see I2CDisplayFactory.I2CDisplayFactoryImpl.SSD1306_Constants#SSD1306_SWITCHCAPVCC
     */
    void begin() throws IOException;

    /**
     * Begin with specified VCC mode (can be SWITCHCAPVCC or EXTERNALVCC)
     * @param vccState VCC mode
     * @see I2CDisplayFactory.I2CDisplayFactoryImpl.SSD1306_Constants#SSD1306_SWITCHCAPVCC
     * @see I2CDisplayFactory.I2CDisplayFactoryImpl.SSD1306_Constants#SSD1306_EXTERNALVCC
     */
    void begin(int vccState) throws IOException;

    /**
     * Pulls reset pin high and low and resets the display
     */
    void reset();

    /**
     * Sends the buffer to the display
     */
    void display();

    /**
     * Clears the buffer by creating a new byte array
     */
    void clear();

    /**
     * Sets the display contract. Apparently not really working.
     * @param contrast Contrast
     */
    void setContrast(byte contrast);

    /**
     * Sets if the backlight should be dimmed
     * @param dim Dim state
     */
    void dim(boolean dim);

    /**
     * Sets if the display should be inverted
     * @param invert Invert state
     */
    void invertDisplay(boolean invert);

    /**
     * Probably broken
     */
    void scrollHorizontally(boolean left, int start, int end);

    /**
     * Probably broken
     */
    void scrollDiagonally(boolean left, int start, int end);

    /**
     * Stops scrolling
     */
    void stopScroll();

    /**
     * @return Display width
     */
    int getWidth();

    /**
     * @return Display height
     */
    int getHeight();

    /**
     * Sets one pixel in the current buffer
     * @param x X position
     * @param y Y position
     * @param white White or black pixel
     * @return True if the pixel was successfully set
     */
    boolean setPixel(int x, int y, boolean white);

    /**
     * Copies AWT image contents to buffer. Calls display()
     * @see I2CDisplayFactory.I2CDisplayFactoryImpl.SSD1306_I2C_Display#display()
     */
    void displayImage();

    /**
     * Sets internal AWT image to specified one.
     * @param img BufferedImage to set
     * @param createGraphics If true, createGraphics() will be called on the image and the result will be saved
     *                       to the internal Graphics field accessible by getGraphics() method
     */
    void setImage(BufferedImage img, boolean createGraphics);

    void clearImage();

    /**
     * Clears the screen and displays the string sent in, adding new lines as needed
     */
    void displayString(String... data);

    void horizontalLine(int position);

    Graphics2D getGraphics();
}
