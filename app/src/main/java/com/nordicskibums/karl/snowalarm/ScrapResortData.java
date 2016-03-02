package com.nordicskibums.karl.snowalarm;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class ScrapResortData implements IResortData {

    public ScrapResortData(String url) {

    }

    @Override
    public List<Resort> GetResortData(int minSnow, int maxDist, Location userGPS) {
        return new ArrayList<>();
    }
}
