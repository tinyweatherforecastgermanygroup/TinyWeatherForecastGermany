package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

public class CancelNotificationBroadcastReceiver extends BroadcastReceiver {

    public final static String CANCEL_NOTIFICATIONS_ACTION = "de.kaffeemitkoffein.broadcast.CANCEL_NOTIFICATIONS";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent!=null){
            String action = intent.getAction();
            if (action!=null){
                if (action.equalsIgnoreCase(CANCEL_NOTIFICATIONS_ACTION)){
                    // this starts the service with the only action being canceling notifications.
                    // the UpdateAlarmManager will call setCancelNotificationAlarm if necessary
                    PrivateLog.log(context,PrivateLog.ALERTS,PrivateLog.INFO,"Starting service to remove a notification.");
                    UpdateAlarmManager.startDataUpdateService(context,false,false,false);
                }
            }
        }
    }

    public static void setCancelNotificationsAlarm(Context context){
        long alarmTimeInMillis = WeatherWarnings.getFirstNotificationCancelTimeInMillis(context);
        if (alarmTimeInMillis>0){
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context,CancelNotificationBroadcastReceiver.class);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.setAction(CANCEL_NOTIFICATIONS_ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,UpdateAlarmManager.NOTIFICATION_ALARM_IDENTIFIER,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC,alarmTimeInMillis,pendingIntent);
            PrivateLog.log(context,PrivateLog.ALERTS,PrivateLog.INFO,"Registered alarm to remove notification at "+WeatherWarnings.simpleDateFormat2.format(new Date(alarmTimeInMillis))+".");
        } else {
            PrivateLog.log(context,PrivateLog.ALERTS,PrivateLog.INFO,"Currently no notifications to cancel in the future. No alarm set.");
        }
    }
}
