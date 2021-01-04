/*
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021 Pawel Dube
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

     // time to elapse before a next update try when network not available.
    private static final int EARLY_ALARM_TIME = 1000*60*15; // 15 minutes in millis

    // time interval to loop the JobSheduler/Alarm manager
    public final static int VIEWS_UPDATE_INTERVAL = 30*60*1000; // 30 minutes;
    // suppress any view update actions if this time did not pass since last view update
    public final static int VIEWS_MAXUPDATETIME   = 10*60*1000; // 10 minutes;

    public static final int FORCE_UPDATE = 0;
    public static final int WIDGET_UPDATE = 1;
    public static final int CHECK_FOR_UPDATE = 2;

    private UpdateAlarmManager(){
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean updateAndSetAlarmsIfAppropriate(Context context, int update_mode){
        // remove old entries from the data base
        new Weather().cleanDataBase(context);
        WeatherSettings weatherSettings = new WeatherSettings(context);
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(context);
        /*
         * update_period: this is the update interval from the settings. It means how often
         * data should be polled from the DWD API.
         */
        long update_period = weatherSettings.getForecastUpdateIntervalInMillis();
        // set time for timer to equal next interval as set up by user
        // weatherCard can be null on first app launch or after clearing memory
        // update_time_utc is used to calculate if an update from the DWD API is due.
        // long update_time_utc = Calendar.getInstance().getTimeInMillis() + update_period;
        long update_time_utc = 0; // set default to 1970 to force update if last update time is unknown
        if (weatherCard != null){
            update_time_utc = weatherCard.polling_time + update_period;
        }
        // Define alarm or job time for update.
        // note that realtime refers to device up time and not utc.
        long next_update_due_in_millis = VIEWS_UPDATE_INTERVAL;
        long next_update_time_realtime = SystemClock.elapsedRealtime() + next_update_due_in_millis;
        boolean result;
        if (    ((weatherSettings.serve_gadgetbridge) && (update_time_utc <= Calendar.getInstance().getTimeInMillis())) ||
                ((weatherSettings.setalarm) && (update_time_utc <= Calendar.getInstance().getTimeInMillis())) ||
                ((update_mode==WIDGET_UPDATE) && (update_time_utc <= Calendar.getInstance().getTimeInMillis())) ||
                (update_mode==FORCE_UPDATE)){
            // update now.
            // In case of success and failure of update the views (gadgetbridge and widgets) will get updated directly
            // from the service. Therefore, views are only updated from here if the service has not been called.
            PrivateLog.log(context,Tag.ALARMMANAGER,"triggering weather update from API...");
            /*
            Intent intent = new Intent(context,WeatherUpdateService.class);
            intent.putExtra(WeatherUpdateService.SERVICE_FORCEUPDATE,true);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            try {
                if (Build.VERSION.SDK_INT<26){
                    context.startService(intent);
                } else {
                    context.startForegroundService(intent);
                }
                 */
            try {
                startDataUpdateService(context,true,false,false);
            } catch (SecurityException e){
                PrivateLog.log(context,Tag.ALARMMANAGER,"WeatherUpdateService not started because of a SecurityException: "+e.getMessage());
                // views need to be updated from here, because starting service failed!
                updateAppViews(context);
            }
            catch (IllegalStateException e){
                PrivateLog.log(context,Tag.ALARMMANAGER,"WeatherUpdateService not started because of an IllegalStateException, the device is probably in doze mode: "+e.getMessage());
                // views need to be updated from here, because starting service failed!
                updateAppViews(context);
            }
            // set result to true, as update was initiated
            result = true;
        } else {
            // update not due
            PrivateLog.log(context,Tag.ALARMMANAGER,"update from API not due.");
            result = false;
            /*
             * Check if views need to be updated.
             * Views means widgets and gadgetbridge.
             */
            if (weatherSettings.views_last_update_time + VIEWS_MAXUPDATETIME < Calendar.getInstance().getTimeInMillis()){
                updateAppViews(context);
            } else {
                // set a shorter update period considering the time passed since last update
                long millis_since_last_update = Calendar.getInstance().getTimeInMillis() - weatherSettings.views_last_update_time;
                next_update_due_in_millis = VIEWS_UPDATE_INTERVAL - millis_since_last_update;
                next_update_time_realtime = SystemClock.elapsedRealtime() + next_update_due_in_millis;
            }
        }
        /*
         * For API < 27 we use AlarmManager, for API equal or greater 27 we use JobSheduler with JobWorkItem.
        */
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
            jobInfo = new JobInfo.Builder(PRIVATE_JOBINFO_IDENTIFIER,new ComponentName(context,UpdateJobService.class))
                    .setMinimumLatency(next_update_due_in_millis)
                    .build();
            jobScheduler.enqueue(jobInfo,jobWorkItem);
            PrivateLog.log(context, Tag.ALARMMANAGER,"job scheduled in "+next_update_due_in_millis/1000/60+" minutes.");
        }
        return result;
    }

    public static void updateAppViews(Context context){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        // update GadgetBridge
        if (weatherSettings.serve_gadgetbridge) {
            GadgetbridgeAPI gadgetbridgeAPI = new GadgetbridgeAPI(context);
            gadgetbridgeAPI.sendWeatherBroadcastIfEnabled();
        }
        // update widgets unconditionally
        PrivateLog.log(context,Tag.ALARMMANAGER,"updating widgets.");
        WidgetRefresher.refresh(context);
        // save the last update time
        weatherSettings.views_last_update_time = Calendar.getInstance().getTimeInMillis();
        weatherSettings.applyPreference(WeatherSettings.PREF_VIEWS_LAST_UPDATE_TIME,weatherSettings.views_last_update_time);
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

    public static void startDataUpdateService(final Context context, final boolean updateWeather, final boolean updateWarnings, final boolean updateTextForecasts){
        Intent intent = new Intent(context,DataUpdateService.class);
        intent.putExtra(DataUpdateService.SERVICEEXTRAS_UPDATE_WEATHER,updateWeather);
        intent.putExtra(DataUpdateService.SERVICEEXTRAS_UPDATE_WARNINGS,updateWarnings);
        intent.putExtra(DataUpdateService.SERVICEEXTRAS_UPDATE_TEXTFORECASTS,updateTextForecasts);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        if (Build.VERSION.SDK_INT<26){
            context.startService(intent);
        } else {
            context.startForegroundService(intent);
        }
    }
}
