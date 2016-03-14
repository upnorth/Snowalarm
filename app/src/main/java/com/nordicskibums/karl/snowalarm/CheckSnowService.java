package com.nordicskibums.karl.snowalarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.AlarmClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckSnowService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public CheckSnowService() {
        super(CheckSnowService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, CheckSnowService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, CheckSnowService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processStartNotification() {
        Settings.getInstance().loadPreferences(this);
        new CheckSnowTask().execute(findResorts(Settings.getInstance().getUserLocation()));
    }
    public ArrayList<Resort> findResorts(Location user){

        ArrayList<Resort> resorts = new ArrayList<>();
        BufferedReader fileReader = null;
        final String DELIMITER = ";";
        try
        {
            String line;
            //Create the file reader
            InputStreamReader resortsReader = new InputStreamReader(Settings.getInstance().getAllResortsCSV());
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
                int distance = Math.round(Settings.getInstance().getUserLocation().distanceTo(dest) / 1000); // distance in Km
                if (distance <= Settings.getInstance().getMaxDist()){
                    Resort resort = new Resort(Integer.parseInt(res[0]),res[1],res[2],res[5],dest);
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
    class CheckSnowTask extends AsyncTask<ArrayList<Resort>, Void, ArrayList<Resort>> {
        @Override
        protected ArrayList<Resort> doInBackground(ArrayList<Resort>... args) {
            ArrayList<Resort> resorts = args[0];
            Document document;
            Elements elements;
            final String updatedURL = "#snow_conditions > div.sr_module_header_grad > div.sr_module_header > div > ul:nth-child(1) > li.left > strong";
            final String newSnowURL = "#conditions_content > div.content > ul:nth-child(2) > li._report_content > div > ul > li.today > div.station.top > div > div";
            final String snowPackURL = "#conditions_content > div.content > div.snow_depth > ul:nth-child(1) > li.elevation.upper > div.white_pill.long";
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
                /*for possibly different URL locales:
                switch(result.get(i).getFormat()){
                    case "cm":
                    case "inch":
                }*/
                String snow = result.get(i).getSnow24h();
                snow = snow.substring(0,snow.length()-2);
                if(Integer.parseInt(snow) >= Settings.getInstance().getMinSnow()){
                    String status = Settings.getInstance().getAlarmMessage();
                    status += result.get(i).getName() + " has " + result.get(i).getSnow24h() + " fresh on a " + result.get(i).getSnowPack()
                            + " snowpack!\n(updated: " + result.get(i).getUpdated() + ")\n\n";
                    Settings.getInstance().setAlarmMessage(status);
                    Settings.getInstance().setAlarm(true);
                }
            }
            if(Settings.getInstance().isAlarm()){ alarm(); }
        }
    }
    private void alarm(){
        if(Settings.getInstance().isAlarm()){

            // Wake up user
            SimpleDateFormat hhmm = new SimpleDateFormat("HHmm");
            final long ONE_MINUTE_IN_MILLIS = 60000; //millisecs
            long checkSnowTime = Settings.getInstance().getAlarmDateTime().getTime();
            Date alarmTime = new Date(checkSnowTime + (1 * ONE_MINUTE_IN_MILLIS));
            String time = hhmm.format(alarmTime);
            Intent alarm = new Intent(AlarmClock.ACTION_SET_ALARM);
            alarm.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(time.substring(0, 2)));
            alarm.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(time.substring(2, 4)));
            alarm.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(alarm);

            // Create report notification
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("Snow alarm:")
                    .setAutoCancel(true)
                    .setContentText("Check out the latest snowfall!")
                    .setSmallIcon(R.drawable.notification_icon);

            Intent mainIntent = new Intent(this, AlarmNotification.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    NOTIFICATION_ID,
                    mainIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setDeleteIntent(InitSnowCheck.getDeleteIntent(this));

            final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}