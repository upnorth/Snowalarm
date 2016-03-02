package com.nordicskibums.karl.snowalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity  {

    private TextView getMinSnow;
    private TextView getMaxDistance;
    private TextView getAlarmDate;
    private TextView getAlarmTime;
    private TextView setAlarm;

    Location userLocation;
    InputStream allResortsCSV;
    ArrayList<Resort> resortsToCheck;

    private String updatedURL;
    private String newSnowURL;
    private String snowPackURL;
    
    SharedPreferences.Editor editor;

    int minSnow = 0;
    int maxDist = 0;
    int alarmDate = 0;
    int alarmTime = 0;
    Date alarmDateTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getMinSnow = (TextView) findViewById(R.id.editSnow);
        getMaxDistance = (TextView) findViewById(R.id.editDist);
        getAlarmDate = (TextView) findViewById(R.id.editDate);
        getAlarmTime = (TextView) findViewById(R.id.editTime);
        setAlarm = (TextView) findViewById(R.id.setAlarm); setAlarm.setEnabled(false);

        // XML Selectors for ...snorapport.html URLs
        updatedURL = "#snow_conditions > div.sr_module_header_grad > div.sr_module_header > div > ul:nth-child(1) > li.left > strong";
        newSnowURL = "#conditions_content > div.content > ul:nth-child(2) > li._report_content > div > ul > li.today > div.station.top > div > div";
        snowPackURL = "#conditions_content > div.content > div.snow_depth > ul:nth-child(1) > li.elevation.upper > div.white_pill.long";

        // Get user position
        UserLocation userLocation = new UserLocation();
        this.userLocation = userLocation.latest(this);

    }
    protected void Snowalarm() {

        // Get nearby resorts
        resortsToCheck = findResorts(userLocation);

        // Check snowfall at nearby resorts and trigger alarm if user conditions are met
        new GetSnow().execute(resortsToCheck);

        /*
        Att göra:

        Hantering av CM och TUM för data om snö
        Integrera logik i service
        Koppla allt till GUI

        */
    }
    public void onSetSnow(View view) {
        if(getInt(getMinSnow) < 0){
            Toast.makeText(MainActivity.this, "Are you kidding me!?", Toast.LENGTH_SHORT).show();
        }
        else {
            minSnow = getInt(getMinSnow);
            editor = getPreferences(MODE_PRIVATE).edit();
            editor.putInt("minSnow", getInt(getMinSnow));
            editor.apply();
            checkSettings();
            Toast.makeText(MainActivity.this, Integer.toString(minSnow), Toast.LENGTH_SHORT).show();
        }
    }
    public void onSetDist(View view) {
        if(getInt(getMaxDistance) < 0){
            Toast.makeText(MainActivity.this, "Are you kidding me!?", Toast.LENGTH_SHORT).show();
        }
        else {
            maxDist = getInt(getMaxDistance);
            editor = getPreferences(MODE_PRIVATE).edit();
            editor.putInt("maxDistance", getInt(getMaxDistance));
            editor.apply();
            checkSettings();
            Toast.makeText(MainActivity.this, Integer.toString(maxDist), Toast.LENGTH_SHORT).show();
        }

    }
    public void onSetDate(View view) {
        if(getAlarmDate.getText().length() == 6){
            alarmDate = getInt(getAlarmDate);
            setDate();
        }
        else{
            Toast.makeText(MainActivity.this, "Not a valid time, try again!", Toast.LENGTH_SHORT).show();
        }
    }
    public void onSetTime(View view) {
        if(getAlarmTime.getText().length() == 4){
            alarmTime = getInt(getAlarmTime);
            setDate();
        }
        else{
            Toast.makeText(MainActivity.this, "Not a valid time, try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDate() {
        if(alarmDate!=0 && alarmTime!=0){
            try{
                // Create datetime from input
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyMMddHHmm");
                String timestamp = String.valueOf(alarmDate)+String.valueOf(alarmTime);
                alarmDateTime = originalFormat.parse(timestamp);
                // Store datetime as milliseconds in shared preferences
                editor = getSharedPreferences(getSPName(this), MODE_PRIVATE).edit();
                editor.putString("alarmDateTime", timestamp);
                editor.apply();
                // Test that correct date can be retrieved
                SharedPreferences settings = getSharedPreferences(getSPName(this), MODE_PRIVATE);
                SimpleDateFormat createDate = new SimpleDateFormat ("HH:mm, yyyy dd/MM");
                timestamp = String.valueOf(settings.getString("alarmDateTime", ""));
                alarmDateTime = originalFormat.parse(timestamp);
                Toast.makeText(MainActivity.this, "Alarm time set to: "+alarmDateTime.toString(), Toast.LENGTH_SHORT).show();
                checkSettings();
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Wrong date or time format.\nExample: 160305 and 0730 required.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private static String getSPName(Context context) {
        return context.getPackageName() + "_preferences";
    }
    private Integer getInt(TextView text){
        if(!text.getText().equals("")){
            return Integer.parseInt(text.getText().toString());
        }
        else {
            Toast.makeText(MainActivity.this, "Not a valid value, try again!", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    public void checkSettings(){
        if(minSnow>-1 && maxDist>-1 && alarmDateTime!=null){
            setAlarm.setEnabled(true);
        }
    }

    public void onSetAlarm(View view){
        NotificationEventReceiver.setupAlarm(getApplicationContext());
        Snowalarm();
        minSnow = 0;
        maxDist = 0;
        alarmDateTime = null;
        getAlarmDate.setText("");
        getAlarmTime.setText("");
        getMaxDistance.setText("");
        getMinSnow.setText("");
    }
    public void onCancelAlarm(View view){
        NotificationEventReceiver.cancelAlarm(getApplicationContext());
    }

    public ArrayList<Resort> findResorts(Location user){
        allResortsCSV = this.getResources().openRawResource(R.raw.resorts);
        ArrayList<Resort> resorts = new ArrayList<>();
        BufferedReader fileReader = null;
        final String DELIMITER = ";";
        try
        {
            String line;
            //Create the file reader
            InputStreamReader resortsReader = new InputStreamReader(allResortsCSV);
            BufferedReader br = new BufferedReader(resortsReader);
            fileReader = new BufferedReader(br);

            //Read the file line by line
            fileReader.readLine(); // Skip column names
            while ((line = fileReader.readLine()) != null)
            {
                //Get all tokens available in line
                String[] res = line.split(DELIMITER);
                Location dest = new Location("");
                dest.setLatitude(Double.parseDouble(res[3]));
                dest.setLongitude(Double.parseDouble(res[4]));
                int distance = Math.round(userLocation.distanceTo(dest) / 1000); // distance in Km
                if (distance <= maxDist){
                    Resort resort = new Resort(Integer.parseInt(res[0]),res[1],res[2],dest);
                    resorts.add(resort);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resorts;
    }

    // Fetch snowfall for last 12 hours at given station
    class GetSnow extends AsyncTask<ArrayList<Resort>, Void, ArrayList<Resort>> {
        @Override
        protected ArrayList<Resort> doInBackground(ArrayList<Resort>... args) {
            ArrayList<Resort> resorts = args[0];
            Document document;
            Elements elements;
            try {
                for(int i=0;i<resorts.size();i++){
                    document = Jsoup.connect(resorts.get(i).getUrl()).get();
                    elements = document.select(newSnowURL);
                    if (!elements.isEmpty()) {
                        resorts.get(i).setSnow24h(elements.first().text());
                    }
                    elements = document.select(updatedURL);
                    if (!elements.isEmpty()) {
                        resorts.get(i).setUpdated(elements.first().text());
                    }
                    elements = document.select(snowPackURL);
                    if (!elements.isEmpty()) {
                        resorts.get(i).setSnowPack(elements.first().text());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resorts;
        }
        @Override
        protected void onPostExecute(ArrayList<Resort> result) {
            // Check snowfall and trigger alarm
            for(int i=0;i<result.size();i++){
                String snow = result.get(i).getSnow24h();
                snow = snow.substring(0,snow.length()-2);
                if(Integer.parseInt(snow) > minSnow){
                    Toast.makeText(MainActivity.this, "Get up! "+result.get(i).getName()+" has "+result.get(i).getSnow24h()+" fresh on a "+result.get(i).getSnowPack()+" snowpack!!\nUpdated:"+result.get(i).getUpdated(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Chill out", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}