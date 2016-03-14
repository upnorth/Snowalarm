package com.nordicskibums.karl.snowalarm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AlarmNotification extends AppCompatActivity {

    private  TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        result = (TextView)findViewById(R.id.reportResult);
        PopulateInterfaceWithData();
    }

    private void PopulateInterfaceWithData(){
        // Get data from settings.
        String report = "Snow report:\n\n" + Settings.getInstance().getAlarmMessage();
        result.setText(report);
    }
}