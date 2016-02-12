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

public class MainActivity extends AppCompatActivity  {
    // implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
    private Button getGPS;
    private TextView distance;

    private Button getSnow;
    private TextView showSnow;

    Location userLastLocation;

    String destinationGPS;
    String destinationName;
    String destinationData;
    int maxDistance;
    int minSnow;
    int alarmTime;

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

        NotificationEventReceiver.setupAlarm(getApplicationContext());
        Snowalarm();
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
        distance.setText(Float.toString(Math.round(location.distanceTo(dest)/1000))+ " km");
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
    protected void Snowalarm() {


        /*
        Att göra:

        Räkna ut aktuella destinationer med deviceGPS och maxDistance
        Hämta destinationers GPS och namn
        Hämta nederbörd för aktuella destinationer med väder-API och jämför med minSnow
        Räkna ut om någon destination har fått minSnow eller mer snö
        Sätt igång larm

        Koppla allt till GUI

        */
    }
}