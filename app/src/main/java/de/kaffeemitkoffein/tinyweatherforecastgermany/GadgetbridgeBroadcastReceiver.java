/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021, 2022, 2023 Pawel Dube
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;

import java.util.Calendar;

public class GadgetbridgeBroadcastReceiver  extends BroadcastReceiver{

    public final static String UPDATE_ACTION = "de.kaffeemitkoffein.broadcast.REQUEST_UPDATE";
    public final static long GADGETBRIDGE_UPDATE_INTERVAL_MILLIS = 1000*60*60;
    public static final int PRIVATE_ALARM_IDENTIFIER = 0;
    public static final int PRIVATE_JOBINFO_IDENTIFIER = 1;

    @Override
    public void onReceive(final Context context, Intent intent) {
        PrivateLog.log(context, PrivateLog.UPDATER, PrivateLog.INFO, "received a broadcast to update weather data.");
        if (intent != null) {
            PrivateLog.log(context, PrivateLog.UPDATER, PrivateLog.INFO, "+-> intent not null.");
            String action = intent.getAction();
            if (action != null) {
                PrivateLog.log(context, PrivateLog.UPDATER, PrivateLog.INFO, "+-> intent has an action.");
            }
            if (intent.getAction().equals(UPDATE_ACTION)) {
                PrivateLog.log(context, PrivateLog.UPDATER, PrivateLog.INFO, "+-> action is an update request.");
                if (WeatherSettings.useBackgroundLocation(context)){
                    WeatherLocationManager.checkForBackgroundLocation(context);
                }
                if (WeatherSettings.serveGadgetBridge(context)){
                    setNextGadgetbridgeUpdateAction(context);
                }
                GadgetbridgeAPI.sendWeatherBroadcastIfEnabled(context,null);
            }
        }
    }

    private static long getNextGadgetbridgeUpdateTime(Context context){
        long lastUpdate = WeatherSettings.getGadgetBridgeLastUpdateTime(context);
        long nextUpdate = lastUpdate + GADGETBRIDGE_UPDATE_INTERVAL_MILLIS;
        return nextUpdate;
    }

    public static long setNextGadgetbridgeUpdateAction(Context context){
        long next_update_time_realtime = getNextGadgetbridgeUpdateTime(context);
        /*
         * For API < 27 we use AlarmManager, for API equal or greater 27 we use JobSheduler with JobWorkItem.
         */
        if (Build.VERSION.SDK_INT < 26) {
            PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"Gadgetbridge update: setting new alarm in "+GADGETBRIDGE_UPDATE_INTERVAL_MILLIS/1000/60+" minutes.");
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context,GadgetbridgeBroadcastReceiver.class);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.setAction(GadgetbridgeBroadcastReceiver.UPDATE_ACTION);
            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT>=23){
                pendingIntent = PendingIntent.getBroadcast(context,PRIVATE_ALARM_IDENTIFIER,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getBroadcast(context,PRIVATE_ALARM_IDENTIFIER,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            }
            if (MainActivity.isIgnoringBatteryOptimizations(context)){
                // we can set the alarm exact because system ignores battery savings for this app
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME,next_update_time_realtime,pendingIntent);
            } else {
                // fall back to inexact alarm in other case
                PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.WARN,"Battery optimizations are active. The automatic Gadgetbridge updates may fail when using AlarmManager with inexact alarms.");
                alarmManager.set(AlarmManager.ELAPSED_REALTIME,next_update_time_realtime,pendingIntent);
            }
        } else {
            final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            Intent jobintent = new Intent(context,GadgetbridgeJobService.class);
            jobintent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            jobintent.setAction(GadgetbridgeBroadcastReceiver.UPDATE_ACTION);
            final JobWorkItem jobWorkItem = new JobWorkItem(jobintent);
            final JobInfo jobInfo;
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putString(GadgetbridgeJobService.ACTION,GadgetbridgeBroadcastReceiver.UPDATE_ACTION);
            jobInfo = new JobInfo.Builder(PRIVATE_JOBINFO_IDENTIFIER,new ComponentName(context,GadgetbridgeJobService.class))
                    .setExtras(persistableBundle)
                    .setMinimumLatency(GADGETBRIDGE_UPDATE_INTERVAL_MILLIS)
                    .setOverrideDeadline(GADGETBRIDGE_UPDATE_INTERVAL_MILLIS+5000)
                    .build();
            jobScheduler.enqueue(jobInfo,jobWorkItem);
            PrivateLog.log(context, PrivateLog.UPDATER,PrivateLog.INFO,"Gadgetbridge update: job scheduled in "+GADGETBRIDGE_UPDATE_INTERVAL_MILLIS/1000/60+" minutes.");
        }
        return next_update_time_realtime;
    }


}
