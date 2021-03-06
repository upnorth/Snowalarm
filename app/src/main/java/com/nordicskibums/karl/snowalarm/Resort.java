package com.nordicskibums.karl.snowalarm;

import android.location.Location;

public class Resort {

    private int id;
    private String name;
    private String url;
    private Location position;
    private String snow24h;
    private String updated;
    private String snowPack;
    private String format;

    public Resort(int id, String name, String url, String format, Location dest) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.format = format;
        this.position = dest;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Currently only static urls available in resorts.csv
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Location getPosition(){ return position; }
    public void setPosition(Location position) { this.position = position; }

    public String getSnow24h() { return snow24h; }
    public void setSnow24h(String snow24h) { this.snow24h = snow24h; }

    public String getUpdated() { return updated; }
    public void setUpdated(String updated) { this.updated = updated; }

    public String getSnowPack() { return snowPack; }
    public void setSnowPack(String snowPack) { this.snowPack = snowPack; }

    // For possible future use of both cm and inches
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}