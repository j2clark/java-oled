package com.j2clark.controller;

import com.j2clark.frame.ClockFrame;
import com.j2clark.frame.Frame;
import com.j2clark.frame.TestFrame;
import com.j2clark.model.I2CDisplay;
import com.j2clark.service.DisplayState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class OledController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DisplayState displayState;
    private final Frame defaultFrame;
    private final ClockFrame clockFrame = new ClockFrame();

    @Autowired
    public OledController(
        final DisplayState displayState,
        @Qualifier("defaultFrame") final Frame defaultFrame) {
        this.displayState = displayState;
        this.defaultFrame = defaultFrame;
    }

    private static final String template = "Hello, %s!";

    @RequestMapping("/hello")
    public String greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return String.format(template, name);
    }

    @RequestMapping("/clock")
    public String clock() {

        this.displayState.setFrame(clockFrame);

        return "OK";
    }

    @RequestMapping("/whoami")
    public String whois() throws Exception {

        this.displayState.setFrame(defaultFrame);

        return defaultFrame.asString();

        /*Set<String> ips = new LinkedHashSet<>();
        StringBuilder sb = new StringBuilder();
        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
        for (; n.hasMoreElements();)
        {
            NetworkInterface e = n.nextElement();

            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements();)
            {
                InetAddress addr = a.nextElement();
                ips.add(addr.getHostAddress());
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(addr.getHostAddress());
            }
        }

        displayState.setFrame(new TestFrame(ips));

        return sb.toString();*/
    }

    @RequestMapping("/displayString")
    public void displayString(@RequestParam(value="text") String[] text) {

        String[] data = new String[0];
        if (!StringUtils.isEmpty(text)) {
            data = text;
        }

        displayState.setFrame(new TestFrame(Arrays.asList(data)));
    }
}
