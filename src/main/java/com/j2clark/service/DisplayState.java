package com.j2clark.service;

import com.j2clark.frame.Frame;

import org.springframework.stereotype.Component;

public class DisplayState {

    private Frame frame;

    public DisplayState(final Frame frame) {
        this.frame = frame;
    }

    public synchronized void setFrame(Frame frame) {
        this.frame = frame;
    }

    public Frame getFrame() {
        return frame;
    }


}
