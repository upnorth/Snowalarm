package com.nordicskibums.karl.snowalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class WakeMeUp extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        InitSnowCheck.setupAlarm(context);
    }
}