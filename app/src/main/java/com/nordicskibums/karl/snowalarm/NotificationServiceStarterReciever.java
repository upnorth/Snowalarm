package com.nordicskibums.karl.snowalarm;

/**
 * Created by wmli115015 on 2016-02-08.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by klogi
 *
 * Broadcast receiver for: BOOT_COMPLETED, TIMEZONE_CHANGED, and TIME_SET events. Sets Alarm Manager for notification;
 */
public final class NotificationServiceStarterReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationEventReceiver.setupAlarm(context);
    }
}
