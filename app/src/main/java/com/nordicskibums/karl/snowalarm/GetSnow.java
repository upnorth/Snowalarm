package com.nordicskibums.karl.snowalarm;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Fetch snowfall for last 12 hours at given station
class GetSnow extends AsyncTask<String, String, String> {

    HttpURLConnection urlConnection;
    private String APIkey = "eb68145abe8d235f044202efa16cbcb0";
    String newSnow;

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
        newSnow = result;
    }
}
