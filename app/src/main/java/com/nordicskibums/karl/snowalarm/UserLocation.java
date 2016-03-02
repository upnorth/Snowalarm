package com.nordicskibums.karl.snowalarm;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class UserLocation {

    public Location latest(Context context) {
        LocationManager locationManager = (android.location.LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        return locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
    }
}
