package com.nordicskibums.karl.snowalarm;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import java.util.List;

public class Position {

    public Location device;
    private Context context;

    public Position(Context context) {
        this.context = context;
        LocationManager locationManager = (android.location.LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        device = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
    }
}