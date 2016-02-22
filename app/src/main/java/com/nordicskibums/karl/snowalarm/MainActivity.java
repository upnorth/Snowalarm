package com.nordicskibums.karl.snowalarm;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
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
        String url = "http://www.skiinfo.se/vast-norge/voss-resort/snorapport.html";
        String progress = "";
        String result = "";
        GetSnow snowfall = (GetSnow) new GetSnow().execute(url, progress, result);
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
    // Fetch snowfall for last 12 hours at given station
    class GetSnow extends AsyncTask<String, String, String> {

        String question;
        @Override
        protected String doInBackground(String... args) {

                /*
        Röldal

GPS: 59.823430, 6.740173
URL: http://www.skiinfo.se/vast-norge/roldal/skidort.html
Snö 24h: //*[@id="conditions_content"]/div[2]/ul[1]/li[2]/div/ul/li[3]/div[2]/div/div
#conditions_content > div.content > ul:nth-child(2) > li._report_content > div > ul > li.today > div.station.top > div > div
Uppdaterat: //*[@id="snow_conditions"]/div[2]/div[1]/div/ul[1]/li[1]/strong

Voss

GPS: 60.612322, 6.473834
URL: http://www.skiinfo.se/vast-norge/voss-resort/snorapport.html
Snö 24h: //*[@id="conditions_content"]/div[2]/ul[1]/li[2]/div/ul/li[3]/div[2]/div/div
Uppdaterat: //*[@id="snow_conditions"]/div[2]/div[1]/div/ul[1]/li[1]/strong
        */

            try {
                Document document = Jsoup.connect("http://www.skiinfo.se/vast-norge/voss-resort/snorapport.html").get();
                Elements elements = document.select("#conditions_content > div.content > div.snow_depth > ul:nth-child(1) > li.elevation.upper > div.white_pill.long");
                if (!elements.isEmpty()) {
                    question = String.valueOf(elements.first().text());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return question;
        }
        @Override
        protected void onPostExecute(String result) {
            showSnow.setText(result);
        }
    }
}