package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

public class OnBootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent!=null){
            if (intent.getAction()!=null){
                if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                    UpdateAlarmManager.setUpdateAlarmsIfAppropriate(context);
                }
            }
        }
    }
}
