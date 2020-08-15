/*
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020 Pawel Dube
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

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import java.util.Calendar;

public class UpdateAlarmManager {

    private static final int PRIVATE_ALARM_IDENTIFIER = 0;
    private static final int PRIVATE_JOBINFO_IDENTIFIER = 1;
    private static final int EARLY_ALARM_TIME = 1000*60*15; // 15 minutes in millis
    public static final boolean FORCE_UPDATE = true;
    public static final boolean CHECK_FOR_UPDATE = false;

    private UpdateAlarmManager(){
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean updateAndSetAlarmsIfAppropriate(Context context, boolean force_update){
        // remove old entries from the data base
        new Weather().cleanDataBase(context);
        WeatherSettings weatherSettings = new WeatherSettings(context);
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(context);
        /*
         * update_period: this is the update interval from the settings. It means how often
         * data should be pulled from the DWD API.
         */
        long update_period = weatherSettings.getUpdateIntervalInMillis();
        // set time for timer to equal next interval as set up by user
        // weatherCard can be null on first app launch or after clearing memory
        // update_time_utc is used to calculate if an update from the DWD API is due.
        long update_time_utc = Calendar.getInstance().getTimeInMillis() + update_period;
        if (weatherCard != null){
            update_time_utc = weatherCard.polling_time + update_period;
        }
        // Define alarm or job time for update.
        long next_update_time_realtime = SystemClock.elapsedRealtime()+update_period;
        long next_update_due_in_millis = update_period;
        boolean result;
        if (((weatherSettings.setalarm) && (update_time_utc <= Calendar.getInstance().getTimeInMillis())) || (force_update)){
            // update now
            PrivateLog.log(context,Tag.ALARMMANAGER,"triggering weather update");
            Intent intent = new Intent(context,WeatherUpdateService.class);
            intent.putExtra(WeatherUpdateService.SERVICE_FORCEUPDATE,true);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            try {
                context.startService(intent);
            } catch (SecurityException e){
                PrivateLog.log(context,Tag.ALARMMANAGER,"WeatherUpdateService not started because of a SecurityException: "+e.getMessage());
            }
            catch (IllegalStateException e){
                PrivateLog.log(context,Tag.ALARMMANAGER,"WeatherUpdateService not started because of an IllegalStateException, the device is probably in doze mode: "+e.getMessage());
            }
            // set result to true, as update was initiated
            result = true;
        } else {
            // update not due, but it is safer to renew the alarm anyway
            // the time left until next update is considered:
            PrivateLog.log(context,Tag.ALARMMANAGER,"update not due");
            next_update_due_in_millis = update_time_utc-Calendar.getInstance().getTimeInMillis();
            next_update_time_realtime = SystemClock.elapsedRealtime() + next_update_due_in_millis;
            // set result to false, as only timer was refreshed and update is not due
            result = false;
        }
        /*
         * override the times with 30 minutes, because otherwise the smart device is not updated.
         * it should be handled like the widget: update with known data every 30 minutes.
         */
        if (weatherSettings.serve_gadgetbridge){
            // check if gadgetbridge update is due
            if (weatherSettings.gadgetbridge_last_update_time + GadgetbridgeAPI.GADGETBRIDGE_MAXUPDATETIME < Calendar.getInstance().getTimeInMillis()){
                GadgetbridgeAPI gadgetbridgeAPI = new GadgetbridgeAPI(context);
                gadgetbridgeAPI.sendWeatherBroadcastIfEnabled();
                // save the last update time
                weatherSettings.gadgetbridge_last_update_time = Calendar.getInstance().getTimeInMillis();
                weatherSettings.applyPreference(WeatherSettings.PREF_GADGETBRIDGE_LAST_UPDATE_TIME,weatherSettings.gadgetbridge_last_update_time);
            }
            next_update_due_in_millis = GadgetbridgeAPI.GADGETBRIDGE_UPDATE_INTERVAL;
            next_update_time_realtime = SystemClock.elapsedRealtime() + next_update_due_in_millis;
        }
        // update later, set timer if wanted by user
        /*
         * For API < 27 we use AlarmManager, for API equal or greater 27 we use JobSheduler with JobWorkItem.
        */
        if ((weatherSettings.setalarm) || (weatherSettings.serve_gadgetbridge)){
            if (Build.VERSION.SDK_INT < 26) {
                PrivateLog.log(context,Tag.ALARMMANAGER,"setting new alarm in "+next_update_due_in_millis/1000/60+" minutes.");
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context,WeatherUpdateBroadcastReceiver.class);
                intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.setAction(WeatherUpdateBroadcastReceiver.UPDATE_ACTION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,PRIVATE_ALARM_IDENTIFIER,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME,next_update_time_realtime,pendingIntent);
            } else {
                final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                Intent jobintent = new Intent(context,UpdateJobService.class);
                jobintent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                jobintent.setAction(WeatherUpdateBroadcastReceiver.UPDATE_ACTION);
                final JobWorkItem jobWorkItem = new JobWorkItem(jobintent);
                final JobInfo jobInfo;
                if (weatherSettings.serve_gadgetbridge){
                    jobInfo = new JobInfo.Builder(PRIVATE_JOBINFO_IDENTIFIER,new ComponentName(context,UpdateJobService.class))
                            .setMinimumLatency(next_update_due_in_millis)
                            .build();
                    PrivateLog.log(context,Tag.ALARMMANAGER,"job defined in "+next_update_due_in_millis/1000/60+" minutes.");
                } else {
                    jobInfo = new JobInfo.Builder(PRIVATE_JOBINFO_IDENTIFIER,new ComponentName(context,UpdateJobService.class))
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                            .setMinimumLatency(next_update_due_in_millis)
                            .build();
                    PrivateLog.log(context,Tag.ALARMMANAGER,"job defined in "+next_update_due_in_millis/1000/60+" minutes, only when network available.");
                }
                jobScheduler.enqueue(jobInfo,jobWorkItem);
                PrivateLog.log(context, Tag.ALARMMANAGER,"job scheduled.");
            }
        }
        return result;
    }

    public static boolean updateAndSetAlarmsIfAppropriate(Context context){
        return updateAndSetAlarmsIfAppropriate(context,CHECK_FOR_UPDATE);
    }

    public static void setEarlyAlarm(Context context){
        if (Build.VERSION.SDK_INT < 26) {
            PrivateLog.log(context,Tag.ALARMMANAGER,"setting early alarm in "+EARLY_ALARM_TIME/1000+" seconds.");
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context,WeatherUpdateBroadcastReceiver.class);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.setAction(WeatherUpdateBroadcastReceiver.UPDATE_ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,PRIVATE_ALARM_IDENTIFIER,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME,SystemClock.elapsedRealtime() + EARLY_ALARM_TIME,pendingIntent);
        } else {
            final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            Intent jobintent = new Intent(context,UpdateJobService.class);
            jobintent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            jobintent.setAction(WeatherUpdateBroadcastReceiver.UPDATE_ACTION);
            final JobWorkItem jobWorkItem = new JobWorkItem(jobintent);
            final JobInfo jobInfo = new JobInfo.Builder(PRIVATE_JOBINFO_IDENTIFIER,new ComponentName(context,UpdateJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setMinimumLatency(EARLY_ALARM_TIME)
                    .build();
            jobScheduler.enqueue(jobInfo,jobWorkItem);
            PrivateLog.log(context,Tag.ALARMMANAGER,"early job enqueued in "+EARLY_ALARM_TIME/1000+"when network available.");
        }
    }
}
