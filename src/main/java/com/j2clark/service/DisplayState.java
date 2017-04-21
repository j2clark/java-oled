package com.j2clark.service;

import com.j2clark.frame.Frame;
import com.j2clark.frame.TestFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DisplayState {

    private Frame frame;/* = new TestFrame(Arrays.asList(
        "Good Morning!",
        "128x64",
        "10pt Font"
    ));*/

    @Autowired
    public DisplayState(@Qualifier("defaultFrame") final Frame defaultFrame) {
        this.frame = defaultFrame;
    }

    public synchronized void setFrame(Frame frame) {
        this.frame = frame;
    }

    public Frame getFrame() {
        return frame;
    }


}
