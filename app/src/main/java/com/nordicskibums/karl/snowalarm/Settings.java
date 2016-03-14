package com.nordicskibums.karl.snowalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import java.io.InputStream;
import java.util.Date;

public class Settings {

    private static Settings mInstance = null;
    private static final String APP_SETTINGS = "com.nordicskibums.karl.snowalarm_preferences";
    Location userLocation;
    InputStream allResortsCSV;

    int minSnow = 0;
    int maxDist = 0;
    Date alarmDateTime = null;

    private boolean alarm = false;
    private String alarmMessage = "";

    private Settings(){}

    public static Settings getInstance(){
        if(mInstance == null)
        {
            mInstance = new Settings();
        }
        return mInstance;
    }

    public Location getUserLocation() { return userLocation; }
    public void setUserLocation(Location userLocation) { this.userLocation = userLocation; }

    public int getMinSnow() { return minSnow; }
    public void setMinSnow(int minSnow) { this.minSnow = minSnow; }

    public int getMaxDist() { return maxDist; }
    public void setMaxDist(int maxDist) { this.maxDist = maxDist; }

    public InputStream getAllResortsCSV() { return allResortsCSV; }
    public void setAllResortsCSV(InputStream allResortsCSV) { this.allResortsCSV = allResortsCSV; }

    public Date getAlarmDateTime() { return alarmDateTime; }
    public void setAlarmDateTime(Date alarmDateTime) { this.alarmDateTime = alarmDateTime; }

    public String getAlarmMessage() { return alarmMessage; }
    public void setAlarmMessage(String alarmMessage) { this.alarmMessage = alarmMessage; }

    public boolean isAlarm() { return alarm; }
    public void setAlarm(boolean alarm) { this.alarm = alarm; }

    // Persistent settings:

    public void savePreferences(Context context){
        double la = userLocation.getLatitude();
        double lo = userLocation.getLongitude();
        setL(context, "la",Double.toString(la));
        setL(context, "lo",Double.toString(lo));
        setMinSnow(context, minSnow);
        setMaxDist(context,maxDist);
    }
    public void loadPreferences(Context context){
        double la = Double.parseDouble(getL(context, "la"));
        double lo = Double.parseDouble(getL(context, "lo"));
        userLocation.setLatitude(la);
        userLocation.setLongitude(lo);
        minSnow = getMinSnow(context);
        maxDist = getMaxDist(context);
    }
    public void setL(Context context, String type, String value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(type, value);
        editor.apply();
    }

    public String getL(Context context, String type) {
        return getSharedPreferences(context).getString(type, null);
    }

    public void setMinSnow(Context context, int minSnow) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt("minSnow" , minSnow);
        editor.apply();
    }
    public int getMinSnow(Context context) {
        return getSharedPreferences(context).getInt("minSnow", 0);
    }

    public void setMaxDist(Context context, int maxDist) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt("maxDist", maxDist);
        editor.apply();
    }
    public int getMaxDist(Context context) {
        return getSharedPreferences(context).getInt("maxDist", 0);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }
}