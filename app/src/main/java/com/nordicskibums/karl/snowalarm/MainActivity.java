package com.nordicskibums.karl.snowalarm;

import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private TextView updatePosition;
    private TextView getMinSnow;
    private TextView getMaxDistance;
    private TextView getAlarmTime;
    private TextView saveSettings;

    private Location userLocation;

    private String alarmTime = "";
    private String alarmDate = "";
    private final Date today = new Date();
    private final SimpleDateFormat alarmDateFormat = new SimpleDateFormat("yyyyMMdd");
    private Date alarmDateTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connecting to user interface
        updatePosition = (TextView) findViewById(R.id.updatePosition); updatePosition.setEnabled(false);
        getMinSnow = (TextView) findViewById(R.id.editSnow);
        getMaxDistance = (TextView) findViewById(R.id.editDist);
        getAlarmTime = (TextView) findViewById(R.id.editTime);
        saveSettings = (TextView) findViewById(R.id.setAlarm); saveSettings.setEnabled(false);

        // Alarm is set to next day by default, can be changed to current day with "Test Alarm"
        long day = 86400000;
        alarmDate = alarmDateFormat.format(new Date((today.getTime()+day)));

        // Get user position
        UserPosition pos = new UserPosition(this);
        userLocation = pos.device;
        Settings.getInstance().setUserLocation(userLocation);
        if(userLocation == null){ // TODO: Clean up logic
            userLocation = getLastKnownLocation();
            Settings.getInstance().setUserLocation(userLocation);
            if(userLocation == null){
                updatePosition.setEnabled(true);
                alert("gps");
            }
        }

        // Load resort data
        Settings.getInstance().setAllResortsCSV(this.getResources().openRawResource(R.raw.resorts));
    }
    public void onUpdateGPS(View view) {
        userLocation = getLastKnownLocation();
        if(userLocation == null){
            Toast.makeText(MainActivity.this, "No position found yet...", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.this, "Found you!", Toast.LENGTH_SHORT).show();
            Settings.getInstance().setUserLocation(userLocation);
            updatePosition.setEnabled(false);
        }
    }
    private Location getLastKnownLocation() { // TODO: Integrate into UserPosition
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
            Toast.makeText(MainActivity.this, "That´s not gonna work...", Toast.LENGTH_SHORT).show();
        }
        else {
            Settings.getInstance().setMinSnow(getInt(getMinSnow));
            checkSettings();
        }
    }
    public void onSetDist(View view) {
        if(isEmpty(getMaxDistance) || getInt(getMaxDistance) < 0 || getInt(getMaxDistance) > 2147483647){
            Toast.makeText(MainActivity.this, "That´s not gonna work...", Toast.LENGTH_SHORT).show();
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
    public Integer getInt(TextView text) { //TODO: Fix validation of input for big numbers to avoid crashes
        try {
            return Integer.parseInt(text.getText().toString());
        } catch (NumberFormatException e) {
            alert("unvalid");
            return null;
        }
    }
//    private Integer getInt(TextView text){
//            return Integer.parseInt(text.getText().toString());
//    }

    public void onSetTime(View view) { // TODO: Clean up validation with a Time Picker
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
                alert("badDateTime");
            }
            checkSettings();
        }
        else{
            alert("badDateTime");
        }
    }

    public void checkSettings(){ // Check if user has given all essential input to set the alarm
        if(Settings.getInstance().getMinSnow()>-1 && Settings.getInstance().getMaxDist()>-1 && alarmDateTime!=null){
            saveSettings.setEnabled(true);
        }
    }

    public void onSetAlarm(View view){
        // Save settings for findResorts() and CheckSnow
        Settings.getInstance().savePreferences(this);
        InitSnowCheck.setupAlarm(getApplicationContext());
        alert("alarmSet");
    }

    public void onTestAlarm(View view){
        alarmDate = alarmDateFormat.format(new Date(today.getTime()));
    }

    public void alert(String type){ // TODO: Extract into separate class file
        String title = "";
        String message = "";
        String button = "";
        switch (type){
            case "gps":
                title = "Your location can´t be found!";
                message = "Please activate GPS and update your position.";
                button = "Ok.";
                break;
            case "badDateTime":
                title = "Wrong date or time format!";
                message = "Example: '20170205' and '0730' works.";
                button = "Ok.";
                break;
            case "unvalid":
                title = "Not a valid number";
                message = "Try again!";
                button = "Ok.";
                break;
            case "alarmSet":
                title = "Time to relax!";
                message = "I´ll tell you at "+alarmTime+" ("+alarmDate+")\nif it´s worth heading out.";
                button = "Cool, see you later.";
                break;
            default:
                break;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
//    public void onCancelAlarm(View view){ // TODO: Change to jobScheduler and implement active cancellation, current code cancels at app shutdown
//        InitSnowCheck.cancelAlarm(getApplicationContext());
//    }
}