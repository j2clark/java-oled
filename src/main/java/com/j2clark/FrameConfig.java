package com.j2clark;

import com.j2clark.frame.DefaultFrame;
import com.j2clark.frame.Frame;
import com.j2clark.frame.PlatformFrame;
import com.j2clark.service.ScheduleService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

@Configuration
public class FrameConfig {

    /**
     * disoplays raspberri pi ip address on oled
     * @throws Exception
     */
    @Bean("whoamiFrame")
    public Frame defaultFrame() throws Exception {
        return new DefaultFrame();
    }

    /**
     * default platform frame
     * @throws Exception
     */
    @Bean("platformFrame")
    public Frame platformFrame(Dimension displayDimensions, ScheduleService scheduleService) throws Exception {
        return new PlatformFrame(displayDimensions, scheduleService);
    }

}
