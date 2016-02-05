package com.nordicskibums.karl.snowalarm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Button getGPS;
    private TextView showGPS;

    private Button getSnow;
    private TextView showSnow;

    private boolean hasAccess;

    GoogleApiClient mGoogleApiClient;
    Location userLastLocation;
    String userLatitudeText;
    String userLongitudeText;

    String deviceGPS;
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
        showGPS = (TextView) findViewById(R.id.showPosition);

        getSnow = (Button) findViewById(R.id.getSnowfall);
        showSnow = (TextView) findViewById(R.id.showSnow);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        Snowalarm();
    }

    // Main method
    protected void Snowalarm() {

        showGPS.setText("LA:" + userLatitudeText + "\nLO:" + userLongitudeText);
        //getData weather = new getData();
        //String badgastein = "2782058";
        //destinationName = badgastein;
        //weather.doInBackground(destinationName);

        /*
        Att göra:

        Skapa service
        Hämta deviceGPS
        Räkna ut aktuella destinationer med deviceGPS och maxDistance
        Hämta destinationers GPS och namn
        Hämta nederbörd för aktuella destinationer med väder-API och jämför med minSnow
        Räkna ut om någon destination har fått minSnow eller mer snö
        Sätt igång larm

        Koppla allt till GUI

        */
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    // Get user GPS location
    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
            return;
        }
        userLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (userLastLocation != null) {
            userLatitudeText = String.valueOf(userLastLocation.getLatitude());
            userLongitudeText = String.valueOf(userLastLocation.getLongitude());
        }
    }

    public void getPermission(){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Fetch snowfall for last 12 hours at given station
    class getData extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                String cityID = args[0];
                URL url = new URL("api.openweathermap.org/data/2.5/forecast/city?id="+cityID+"&APPID=4bd8de17de7b3cc02f917116059e5360&type=hour&snow.12h");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            showSnow.setText(result);
        }
    }
}