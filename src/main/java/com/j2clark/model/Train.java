package com.j2clark.model;

public class Train {
    private final String name;
    private final int arrive;

    public Train(String name, int arrive) {
        this.name = name;
        this.arrive = arrive;
    }


    public String getName() {
        return name;
    }

    public int getArrive() {
        return arrive;
    }
}
