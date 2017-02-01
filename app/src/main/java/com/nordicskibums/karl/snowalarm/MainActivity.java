package com.nordicskibums.karl.snowalarm;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private TextView refreshPos;
    private TextView getMinSnow;
    private TextView getMaxDistance;
    private TextView getAlarmTime;
    private TextView setAlarm;
    private TextView testAlarm;

    Location userLocation;

    int minSnow = 0;
    int maxDist = 0;
    String alarmDate = "";
    String alarmTime = "";
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Date alarmDateTime = null;
    final Date today = new Date();
    final int day = 86400000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Snow alarm settings:");

        refreshPos = (TextView) findViewById(R.id.updatePosition); refreshPos.setEnabled(false);
        getMinSnow = (TextView) findViewById(R.id.editSnow);
        getMaxDistance = (TextView) findViewById(R.id.editDist);
        getAlarmTime = (TextView) findViewById(R.id.editTime);
        setAlarm = (TextView) findViewById(R.id.setAlarm); setAlarm.setEnabled(false);
        testAlarm = (TextView) findViewById(R.id.testAlarm); // testAlarm.setEnabled(false);

        // Alarm is set to next day by default, can be changed to current day with "Test Alarm"
        long day = 86400000;
        alarmDate = sdf.format(new Date((today.getTime()+day)));
        getAlarmTime.setText("0700");

        // Get user position
        UserPosition pos = new UserPosition(this);
        userLocation = pos.device;
        Settings.getInstance().setUserLocation(userLocation);
        if(userLocation == null){
            userLocation = getLastKnownLocation();
            Settings.getInstance().setUserLocation(userLocation);
            if(userLocation == null){
                Toast.makeText(MainActivity.this, "Your location can´t be found. Please activate GPS and update your position.", Toast.LENGTH_SHORT).show();
                refreshPos.setEnabled(true);
            }
        }
        Settings.getInstance().setAllResortsCSV(this.getResources().openRawResource(R.raw.resorts));
    }
    public void onRefreshGPS(View view) {
        userLocation = getLastKnownLocation();
        if(userLocation == null){
            Toast.makeText(MainActivity.this, "No position found yet...", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.this, "Got it!", Toast.LENGTH_SHORT).show();
            Settings.getInstance().setUserLocation(userLocation);
            refreshPos.setEnabled(false);
        }
    }
    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
    public void onSetSnow(View view) {
        if(isEmpty(getMinSnow) || getInt(getMinSnow) < 0){
            Toast.makeText(MainActivity.this, "Are you kidding me!?", Toast.LENGTH_SHORT).show();
        }
        else {
            Settings.getInstance().setMinSnow(getInt(getMinSnow));
            checkSettings();
        }
    }
    public void onSetDist(View view) {
        if(isEmpty(getMaxDistance) || getInt(getMaxDistance) < 0 || getInt(getMaxDistance) > 2147483647){
            Toast.makeText(MainActivity.this, "Are you kidding me!?", Toast.LENGTH_SHORT).show();
        }
        else {
            Settings.getInstance().setMaxDist(getInt(getMaxDistance));
            checkSettings();
        }
    }
    private boolean isEmpty(TextView etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
    public Integer getInt(TextView text) { //TODO: Fiks parsInt ved store tall fra input
        try {
            return Integer.parseInt(text.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Not a valid number, try again!", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
//    private Integer getInt(TextView text){
//            return Integer.parseInt(text.getText().toString());
//    }

    public void onSetTime(View view) {
        if(getAlarmTime.getText().length() == 4 && getInt(getAlarmTime) >=0 && getInt(getAlarmTime) <= 2359){
            alarmTime = getAlarmTime.getText().toString();
            //setDate();
            // Create datetime from input
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMddHHmm");
            String timestamp = alarmDate+alarmTime;
            try{
                alarmDateTime = originalFormat.parse(timestamp);
                Settings.getInstance().setAlarmDateTime(alarmDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
//                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create(); // TODO: Implement dialog instead of toast for some messages
//                alertDialog.setTitle("Heads up!");
//                alertDialog.setMessage("Wrong date or time format!\nExample: 20160205 and 0730 required.");
//                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                alertDialog.show();
                Toast.makeText(MainActivity.this, "Wrong date or time format!\nExample: 20160205 and 0730 required.", Toast.LENGTH_SHORT).show();
            }
            checkSettings();
        }
        else{
            Toast.makeText(MainActivity.this, "Not a valid time, try again!", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkSettings(){
        if(minSnow>-1 && maxDist>-1 && alarmDateTime!=null){
            setAlarm.setEnabled(true);
        }
    }

    public void onSetAlarm(View view){
        // Save settings for findResorts() and CheckSnow
        Settings.getInstance().savePreferences(this);
        InitSnowCheck.setupAlarm(getApplicationContext());
        Toast.makeText(MainActivity.this, "Snow check and hopefully alarm clock set to: "+alarmDate+", "+alarmTime, Toast.LENGTH_SHORT).show();
    }
    public void onTestAlarm(View view){
        alarmDate = sdf.format(new Date(today.getTime()));
    }

//    public void onCancelAlarm(View view){ // TODO: Implement active cancellation, current code cancels at app shutdown
//        InitSnowCheck.cancelAlarm(getApplicationContext());
//    }
}