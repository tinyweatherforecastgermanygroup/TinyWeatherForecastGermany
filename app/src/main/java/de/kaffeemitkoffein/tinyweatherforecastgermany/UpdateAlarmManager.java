package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class UpdateAlarmManager {

    private Context context;

    private static final int PRIVATE_ALARM_IDENTIFIER=99;

    public UpdateAlarmManager(Context context){
        this.context = context;
    }

    public static void setUpdateAlarmsIfAppropriate(final Context context) {
        WeatherSettings weatherSettings = new WeatherSettings(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long update_period = weatherSettings.getUpdateIntervalInMillis();
        Intent i = new Intent(context, WeatherUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, PRIVATE_ALARM_IDENTIFIER, i, PendingIntent.FLAG_UPDATE_CURRENT);
        if (weatherSettings.setalarm){
            // test hack
            update_period = 60000;
            Log.v("GADGET","SETTING ALARMS period:"+update_period);
            Log.v("GADGET","SETTING ALARMS re.tim:"+(SystemClock.elapsedRealtime() + update_period));
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + update_period, update_period, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }

}
