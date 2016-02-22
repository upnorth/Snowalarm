package com.nordicskibums.karl.snowalarm;

import android.location.Location;

public class Resort {

    private int id;
    private String name;
    private Location position;
    private Double snowPack;
    private Double snow24h;

    public Resort(int id, String name, Location position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }
}
