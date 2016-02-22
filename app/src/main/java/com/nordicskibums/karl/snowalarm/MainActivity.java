package com.nordicskibums.karl.snowalarm;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    private Button getGPS;
    private TextView distance;

    private Button getSnow;
    private TextView showSnow;

    private TextView time;
    private Button getTime;

    Location userLastLocation;
    Location destinationLocation;
    CharSequence alarmTime;
    int hours;
    int minutes;

    ArrayList<Resort> resorts;

    String destinationGPS;
    String destinationName;
    String destinationData;
    int maxDistance;
    int minSnow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getGPS = (Button) findViewById(R.id.getPosition);
        distance = (TextView) findViewById(R.id.dist);

        getSnow = (Button) findViewById(R.id.getSnowfall);
        showSnow = (TextView) findViewById(R.id.showSnow);

        time = (TextView) findViewById(R.id.editTime);
        getTime = (Button) findViewById(R.id.getTime);

        Snowalarm();
    }

    protected void Snowalarm() {

        /*float distance = userLastLocation.distanceTo(destinationLocation);
        if (distance <= maxDistance){
            addResort(id, name, destinationLocation);
        }
        */
        /*
        Att göra:

        Hämta destinationers GPS och namn
        Hämta nederbörd för aktuella destinationer med väder-API och jämför med minSnow
        Räkna ut om någon destination har fått minSnow eller mer snö

        Koppla allt till GUI

        */
    }

    public void onSetTime(View view) {
        alarmTime = time.getText();
        Toast.makeText(MainActivity.this, alarmTime, Toast.LENGTH_SHORT).show();
    }
    public void onSetAlarm(View view){
        NotificationEventReceiver.setupAlarm(getApplicationContext());
    }
    public void onCancelAlarm(View view){
        NotificationEventReceiver.cancelAlarm(getApplicationContext());
    }
    public void addResort(int id, String name, double lo, double la) {
        //Resort resort = new Resort(id, name, lo, la);
        //resorts.add(resort);
    }

    public void onGetGPSClick(View view) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
        //Kittelfjäll 65.256882 N 15.487579 E
        Location dest = new Location("");
        dest.setLatitude(65.256882);
        dest.setLongitude(15.487579);
        distance.setText(Float.toString(Math.round(location.distanceTo(dest) / 1000)) + " km");
    }

    public void onGetSnowClick(View view) {
        //String snowfall;
        //showSnow.setText(snowfall);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    // To prevent crash on resuming activity  : interaction with fragments allowed only after Fragments Resumed or in OnCreate
    // http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        // handleIntent();
    }
    // Main method

}