package com.j2clark.frame;

import com.j2clark.model.Train;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

public class PlatformFrame extends AbstractGraphics2DFrame {

    private final CenteredText stationName;
    private final CenteredText platform;
    private final CenteredClock clock;
    private final TrainTextFactory trainTextFactory;

    public PlatformFrame(Dimension displayDimensions) {
        Font mono_plain = new Font("Monospaced", Font.PLAIN, 8);
        stationName  = new CenteredText("Marienplatz", mono_plain, displayDimensions);
        platform = new CenteredText("Platform 1", mono_plain, displayDimensions);
        clock = new CenteredClock(mono_plain, displayDimensions);
        trainTextFactory = new TrainTextFactory(mono_plain, displayDimensions);
    }

    protected List<Train> getArrivingTrains() {

        // todo: make this dynamic by hooking into a (dynamic) train schedule

        // train lineup
        return Arrays.asList(
            new Train("train_1", 1),
            new Train("train_2", 3),
            new Train("train_3", 5)
        );
    }

    @Override
    protected void render(Graphics2D graphics) {

        int y = 0;

        // train lineup
        List<Train> trains = getArrivingTrains();

        // station name
        y += stationName
            .render(y, graphics)
            .getHeight(graphics);

        // platform
        y += platform
            .render(y, graphics)
            .getHeight(graphics);

        // clock
        y += clock
            .render(y, graphics)
            .getHeight(graphics);

        for (Train train : trains) {
            y += trainTextFactory.newInstance(train)
                .render(y, graphics)
                .getHeight(graphics);
        }
    }

    public static class TrainTextFactory {

        private final Font font;
        private final Dimension display;
        public TrainTextFactory(Font font,
                                Dimension display) {
            this.font = font;
            this.display = display;
        }

        public TrainText newInstance(Train train) {
            return new TrainText(train, font, display);
        }
    }

    public static class TrainText {

        private final Train train;
        private final Font font;
        private final Dimension display;


        public TrainText(final Train train,
                         final Font font,
                         final Dimension display) {
            this.train = train;
            this.font = font;
            this.display = display;
        }

        protected TrainText render(int y, Graphics graphics) {

            String name = train.getName();
            String time = String.format("%d min", train.getArrive());

            FontMetrics metrics = graphics.getFontMetrics(font);
            int time_width = metrics.getStringBounds("10 min", graphics).getBounds().width;
            int train_width = display.width - time_width;

            Rectangle bounds = metrics.getStringBounds(name, graphics).getBounds();
            if (bounds.width > train_width) {
                name = GraphicsUtil.abbreviate(name, metrics, train_width);
            }

            int time_x = display.width - time_width;
            if (metrics.getStringBounds(time, graphics).getBounds().width < time_width) {
                time_x = display.width - metrics.getStringBounds(time, graphics).getBounds().width;
            }

            y += metrics.getHeight() + metrics.getLeading();
            graphics.setFont(metrics.getFont());
            graphics.drawString(name, 0, y);
            graphics.drawString(time, time_x, y);

            return this;
        }

        public int getHeight(Graphics graphics) {
            FontMetrics metrics = graphics.getFontMetrics(font);
            return metrics.getStringBounds(train.getName(), graphics).getBounds().height + metrics.getLeading();
        }
    }

    public static class CenteredClock extends CenteredText {

        private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        public CenteredClock(Font font, Dimension display) {
            super(null, font, display);
        }

        @Override
        public String getText() {
            return timeFormat.format(new Date());
        }
    }

    public static class CenteredText {

        private final String text;
        private final Font font;
        private final Dimension display;

        public CenteredText(final String text, Font font, Dimension display) {
            this.text = text;
            this.font = font;
            this.display = display;
        }

        public String getText() {
            return text;
        }

        protected CenteredText render(int y, Graphics graphics) {

            int x = 0;
            int displayWidth = display.width;

            // calculate center
            FontMetrics metrics = graphics.getFontMetrics(font);
            int center_x = x + (displayWidth - metrics.stringWidth(getText())) / 2;
            int center_y = y + metrics.getHeight();
            graphics.setFont(metrics.getFont());
            graphics.drawString(getText(), center_x, center_y);

            return this;
        }

        private Rectangle getBounds(Graphics graphics, FontMetrics metrics) {
            return metrics.getStringBounds(getText(), graphics).getBounds();
        }

        private int getLeading(FontMetrics metrics) {
            return metrics.getLeading();
        }

        public int getHeight(Graphics graphics) {
            FontMetrics metrics = graphics.getFontMetrics(font);
            return getBounds(graphics, metrics).height + getLeading(metrics);
        }
    }

}
