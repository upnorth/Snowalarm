package com.nordicskibums.karl.snowalarm;

import android.location.Location;

import java.util.List;

interface IResortData {
    List<Resort> GetResortData (int minSnow, int maxDist, Location userGPS);
}