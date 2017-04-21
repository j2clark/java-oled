package com.j2clark.frame;

import com.j2clark.model.I2CDisplay;

import java.awt.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DefaultFrame extends AbstractGraphics2DFrame implements Frame {

    private final int fontWeight;
    private final Font font;
    private final List<String> data;

    public DefaultFrame() throws Exception {

        this.fontWeight = 10;
        this.font = new Font("Monospaced", Font.PLAIN, fontWeight);

        Set<String> ips = new LinkedHashSet<>();
        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
        for (; n.hasMoreElements();)
        {
            NetworkInterface e = n.nextElement();

            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements();)
            {
                InetAddress addr = a.nextElement();

                // I don't care about IPv6
                if (addr instanceof Inet4Address) {
                    final Inet4Address ipv4 = (java.net.Inet4Address) addr;
                    String ipAdd = ipv4.getHostAddress();
                    if (!ipAdd.startsWith("127.") && !ipAdd.startsWith("0.")) {
                        ips.add(addr.getHostAddress());
                    }
                }
            }
        }
        data = new ArrayList<>(ips);
    }

    /*@Override
    public void display(I2CDisplay display) {
        display.clearImage();

        Graphics2D graphics = display.getGraphics();
        graphics.setFont(font);
        int i = 0;
        for (String s : data) {
            graphics.drawString(s, 0, fontWeight * (i++ + 1));
        }


        display.displayImage();
    }*/

    protected void render(Graphics2D graphics) {
        graphics.setFont(font);
        int i = 0;
        for (String s : data) {
            graphics.drawString(s, 0, fontWeight * (i++ + 1));
        }
    }

    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();
        for (String s : data) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(s);
        }
        return sb.toString();
    }
}
