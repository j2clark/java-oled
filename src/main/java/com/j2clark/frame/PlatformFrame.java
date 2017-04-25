package com.j2clark.frame;

import com.j2clark.model.StationTimeTable;
import com.j2clark.model.TimeOfDay;
import com.j2clark.service.ScheduleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

public class PlatformFrame extends AbstractGraphics2DFrame {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final boolean devEnvironment;
    private final CenteredText stationName;
    private final CenteredText platform;
    private final CenteredClock clock;
    private final TrainTextFactory trainTextFactory;
    private final ScheduleService scheduleService;

    public PlatformFrame(Environment environment,
                         Dimension displayDimensions,
                         ScheduleService scheduleService) {
        this.devEnvironment = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        Font mono_plain = new Font("Monospaced", Font.PLAIN, 8);
        stationName  = new CenteredText("Marienplatz", mono_plain, displayDimensions);
        platform = new CenteredText("Platform 1", mono_plain, displayDimensions);
        clock = new CenteredClock(mono_plain, displayDimensions);
        trainTextFactory = new TrainTextFactory(mono_plain, displayDimensions);
        this.scheduleService = scheduleService;
    }

    protected List<StationTimeTable> getArrivingTrains() {
        // todo: feed this in at initialization, and reduc as time goes on
        return /*new StationTimeTables(*/
            scheduleService.findTimeTablesByStationAndDirection("Marienplatz",
                                                                ScheduleService.Direction.westbound)/*)*/
            .currentForTime(new TimeOfDay())
            .sortedByArrival()
            .asList(3);
    }

    @Override
    protected void render(Graphics2D graphics) {

        StringBuilder output = new StringBuilder(getClass().getSimpleName()).append("[\n");

        int y = 0;

        // train lineup
        List<StationTimeTable> trains = getArrivingTrains();

        // station name
        y += stationName
            .render(y, graphics)
            .getHeight(graphics);

        if (devEnvironment) output.append("\t").append(stationName.getText(graphics)).append("\n");


        // platform
        y += platform
            .render(y, graphics)
            .getHeight(graphics);
        if (devEnvironment) output.append("\t").append(platform.getText(graphics)).append("\n");

        // clock
        y += clock
            .render(y, graphics)
            .getHeight(graphics);
        if (devEnvironment) output.append("\t").append(clock.getText(graphics)).append("\n");

        for (StationTimeTable train : trains) {
            TrainText trainText = trainTextFactory.newInstance(train);
            y += trainText
                .render(y, graphics)
                .getHeight(graphics);

            if (devEnvironment) output.append("\t").append(trainText.getText(graphics)).append("\n");
        }

        if (devEnvironment) {
            logger.info(output.toString());
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

        public TrainText newInstance(/*Train train, String station*/StationTimeTable timeTable) {
            return new TrainText(timeTable, font, display);
        }
    }

    public static class TrainText {

        private final StationTimeTable timeTable;
        private final Font font;
        private final Dimension display;

        public TrainText(final StationTimeTable timeTable,
                         final Font font,
                         final Dimension display) {
            this.timeTable = timeTable;
            this.font = font;
            this.display = display;
        }

        private static String TIME_WSTR = "MM,ss min";
        protected String getName(Graphics graphics) {
            FontMetrics metrics = graphics.getFontMetrics(font);

            int time_width = metrics.getStringBounds(TIME_WSTR, graphics).getBounds().width;
            int train_width = display.width - time_width;

            String name = timeTable.getLine() + " " + timeTable.getName();
            Rectangle bounds = metrics.getStringBounds(name, graphics).getBounds();
            if (bounds.width > train_width) {
                name = GraphicsUtil.abbreviate(name, metrics, train_width);
            }
            return name;
        }

        protected String getTime() {

            TimeOfDay now = new TimeOfDay();

            TimeOfDay time = new TimeOfDay(Math.abs(now.getSecondsOfDay() - timeTable.getArrives().getSecondsOfDay()));

            int min = time.getMinute();
            int sec = time.getSecond();

            if (min < 2) {
                return String.format("%d,%02d min", min, sec);
            } else {
                min += 1;
                return String.format("%d    min", min);
            }
        }

        protected TrainText render(int y, Graphics graphics) {
            String name = getName(graphics);
            String time = getTime();

            FontMetrics metrics = graphics.getFontMetrics(font);
            int time_width = metrics.getStringBounds(TIME_WSTR, graphics).getBounds().width;

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

        public String getText(Graphics graphics) {
            return getName(graphics) + " " + getTime();
        }

        public int getHeight(Graphics graphics) {
            FontMetrics metrics = graphics.getFontMetrics(font);
            return metrics.getStringBounds(timeTable.getName(), graphics).getBounds().height + metrics.getLeading();
        }
    }

    public static class CenteredClock extends CenteredText {

        private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        public CenteredClock(Font font, Dimension display) {
            super(null, font, display);
        }

        @Override
        public String getText(Graphics graphics) {
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

        public String getText(Graphics graphics) {
            return text;
        }

        protected CenteredText render(int y, Graphics graphics) {

            int x = 0;
            int displayWidth = display.width;

            // calculate center
            FontMetrics metrics = graphics.getFontMetrics(font);
            int center_x = x + (displayWidth - metrics.stringWidth(getText(graphics))) / 2;
            int center_y = y + metrics.getHeight();
            graphics.setFont(metrics.getFont());
            graphics.drawString(getText(graphics), center_x, center_y);

            return this;
        }

        private Rectangle getBounds(Graphics graphics, FontMetrics metrics) {
            return metrics.getStringBounds(getText(graphics), graphics).getBounds();
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
