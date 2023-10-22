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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Calendar;


public class UpdateAlarmManager {

    public static final int PRIVATE_ALARM_IDENTIFIER = 0;
    public static final int PRIVATE_JOBINFO_IDENTIFIER = 1;
    public static final int NOTIFICATION_ALARM_IDENTIFIER = 2;

     // time to elapse before a next update try when network not available.
    private static final int EARLY_ALARM_TIME = 1000*60*15; // 15 minutes in millis

    // time interval to loop the JobSheduler/Alarm manager
    public static int VIEWS_UPDATE_INTERVAL_DEFAULT = 30*60*1000; // 30 minutes;
    public static int VIEWS_UPDATE_INTERVAL         = VIEWS_UPDATE_INTERVAL_DEFAULT;
    // suppress any view update actions if this time did not pass since last view update
    public static int VIEWS_MAXUPDATETIME_DEFAULT   = 10*60*1000; // 10 minutes;
    public static int VIEWS_MAXUPDATETIME           = VIEWS_MAXUPDATETIME_DEFAULT;

    public static final String EXTRA_UPDATE_SOURCE = "UPDATE_SOURCE";
    public static final int UPDATE_FROM_UNSPECIFIED = -1;
    public static final int UPDATE_FROM_ACTIVITY = 0;
    public static final int UPDATE_FROM_WIDGET = 1;
    public static final int UPDATE_FROM_JOB = 2;


    private UpdateAlarmManager(){
    }

    private static void adaptUpdateIntervalsToSettings(Context context){
        VIEWS_UPDATE_INTERVAL = VIEWS_UPDATE_INTERVAL_DEFAULT;
        VIEWS_MAXUPDATETIME   = VIEWS_MAXUPDATETIME_DEFAULT;
        int warningsUpdateInterval = WeatherSettings.getWarningsUpdateIntervalInMillis(context);
        if ((warningsUpdateInterval<VIEWS_UPDATE_INTERVAL) || (warningsUpdateInterval<VIEWS_MAXUPDATETIME)){
            VIEWS_UPDATE_INTERVAL = warningsUpdateInterval;
            VIEWS_MAXUPDATETIME = warningsUpdateInterval/3;
        }
    }

    public static boolean updateAndSetAlarmsIfAppropriate(final Context context, int updateSource, ArrayList<String> updateTasks, CurrentWeatherInfo weatherCard){
        // updateTasks may be null, meaning there are no mandatory tasks to do. Create an empty list, then.
        if (updateTasks==null){
            updateTasks = new ArrayList<String>();
        }
        adaptUpdateIntervalsToSettings(context);
        if (weatherCard==null){
            weatherCard = Weather.getCurrentWeatherInfo(context,updateSource);
        }
        // add weather update task if not already in list AND
        //   - (regular updates set in settings AND update is due) OR
        //   - (no data) OR
        //   - (serve gadgetbridge AND update is due) OR
        //   - (is new server data expected AND update interval is 6h AND regular updates enabled in settings)
        if (!updateTasks.contains(DataUpdateService.SERVICEEXTRAS_UPDATE_WEATHER) &&
                ((WeatherSettings.getUpdateForecastRegularly(context) && WeatherSettings.isForecastCheckDue(context)) ||
                 (weatherCard==null) ||
                 (WeatherSettings.serveGadgetBridge(context) && WeatherSettings.isForecastCheckDue(context)) ||
                 (weatherCard.isNewServerDataExpected(context) && WeatherSettings.forecastUpdateIntervalIs6h(context) && WeatherSettings.getUpdateForecastRegularly(context))
                )
           ){
            updateTasks.add(DataUpdateService.SERVICEEXTRAS_UPDATE_WEATHER);
        }
        // add warnings if enabled and if update is due
        if (!updateTasks.contains(DataUpdateService.SERVICEEXTRAS_UPDATE_WARNINGS)) {
            if (!WeatherSettings.areWarningsDisabled(context) && WeatherSettings.areWarningsOutdated(context)) {
                updateTasks.add(DataUpdateService.SERVICEEXTRAS_UPDATE_WARNINGS);
            }
        }
        if (!updateTasks.contains(DataUpdateService.SERVICEEXTRAS_UPDATE_TEXTFORECASTS)){
            if (WeatherSettings.updateTextForecasts(context)){
                if (WeatherSettings.areTextForecastsOutdated(context)) {
                    updateTasks.add(DataUpdateService.SERVICEEXTRAS_UPDATE_TEXTFORECASTS);
                }
            }
        }

        // let service build/recover area database if database damaged or not present
        if (MainActivity.prepareAreaDatabase(context)){
            if (!updateTasks.contains(DataUpdateService.SERVICEEXTRAS_CRATE_AREADATABASE)){
                updateTasks.add(DataUpdateService.SERVICEEXTRAS_CRATE_AREADATABASE);
            }
        }
        /*
         * Create alarms to cancel notifications if applicable
         */
        if (WeatherSettings.notifyWarnings(context)) {
            CancelNotificationBroadcastReceiver.setCancelNotificationsAlarm(context);
        }
        // Define alarm or job time for update.
        // note that realtime refers to device up time and not utc.
        long next_update_due_in_millis = VIEWS_UPDATE_INTERVAL;
        long next_update_time_realtime = SystemClock.elapsedRealtime() + next_update_due_in_millis;
        /*
         * Check if views need to be updated.
         * Views means widgets and gadgetbridge.
         */
        if (WeatherSettings.getViewsLastUpdateTime(context) + VIEWS_MAXUPDATETIME < Calendar.getInstance().getTimeInMillis()) {
            if (updateSource!=UPDATE_FROM_WIDGET){
                updateAppViews(context,weatherCard);
            } else {
                // do not trigger back update to widget if call is from widget;
                // update Gadgetbridge only
                GadgetbridgeAPI.sendWeatherBroadcastIfEnabled(context,weatherCard);
                WeatherSettings.setViewsLastUpdateTime(context,Calendar.getInstance().getTimeInMillis());
            }
        } else {
            // set a shorter update period considering the time passed since last update
            long millis_since_last_update = Calendar.getInstance().getTimeInMillis() - WeatherSettings.getViewsLastUpdateTime(context);
            next_update_due_in_millis = VIEWS_UPDATE_INTERVAL - millis_since_last_update;
            next_update_time_realtime = SystemClock.elapsedRealtime() + next_update_due_in_millis;
        }
        boolean result = false;
        // only call service if list of tasks is not empty.
        if (updateTasks.size()>0){
            // update now.
            // In case of success and failure of update the views (gadgetbridge and widgets) will get updated directly
            // from the service. Therefore, views are only updated from here if the service has not been called.
            try {
                result = startDataUpdateService(context,updateSource,updateTasks);
                if (!result){
                    PrivateLog.log(context, PrivateLog.UPDATER, PrivateLog.WARN, "DataUpdateService not started because of missing internet connection or errors.");
                    updateAppViews(context,weatherCard);
                }
                PrivateLog.log(context, PrivateLog.UPDATER, PrivateLog.INFO, "DataUpdateService started with the given tasks: "+updateTasks.toString());
            } catch (SecurityException e) {
                PrivateLog.log(context, PrivateLog.UPDATER, PrivateLog.WARN, "DataUpdateService not started because of a SecurityException: " + e.getMessage());
                // views need to be updated from here, because starting service failed!
                updateAppViews(context,weatherCard);
            } catch (IllegalStateException e) {
                PrivateLog.log(context, PrivateLog.UPDATER, PrivateLog.WARN, "DataUpdateService not started because of an IllegalStateException, the device is probably in doze mode: " + e.getMessage());
                // views need to be updated from here, because starting service failed!
                updateAppViews(context,weatherCard);
            }
        } else {
            // do nothing special if no tasks
            PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO," Service not called, since currently there are no tasks to perform.");
        }
        // Log data about update times if applicable; for efficacy reasons in if clause
        if (WeatherSettings.loggingEnabled(context)){
            String lineBreak = System.getProperty("line.separator");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("----------------------------------"); stringBuilder.append(lineBreak);
            stringBuilder.append("Updater has the following status: "); stringBuilder.append(lineBreak);
            if (updateSource==UPDATE_FROM_ACTIVITY){
                stringBuilder.append("* called from ACTIVITY"); stringBuilder.append(lineBreak);
            }
            if (updateSource==UPDATE_FROM_JOB){
                stringBuilder.append("* called from JOB"); stringBuilder.append(lineBreak);
            }
            if (updateSource==UPDATE_FROM_WIDGET){
                stringBuilder.append("* called from WIDGET"); stringBuilder.append(lineBreak);
            }
            stringBuilder.append(" * next updater job due in "); stringBuilder.append(String.valueOf(next_update_due_in_millis/1000/60)); stringBuilder.append(" minutes."); stringBuilder.append(lineBreak);
            stringBuilder.append(" * last weather data update was "+Weather.SIMPLEDATEFORMATS.DETAILED.format(WeatherSettings.getLastWeatherUpdateTime(context))); stringBuilder.append(lineBreak);
            if (weatherCard!=null){
                stringBuilder.append(" * weather data for ["); stringBuilder.append(weatherCard.weatherLocation.getName()); stringBuilder.append("|"); stringBuilder.append(weatherCard.weatherLocation.getOriginalDescription()); stringBuilder.append("] is from "); stringBuilder.append(Weather.SIMPLEDATEFORMATS.DETAILED.format(weatherCard.polling_time));                 stringBuilder.append(", issued by the DWD: "+Weather.SIMPLEDATEFORMATS.DETAILED.format(weatherCard.issue_time));
                stringBuilder.append(lineBreak);
                if (WeatherSettings.forecastUpdateIntervalIs6h(context)){
                    stringBuilder.append(" * new weather data is expected at "); stringBuilder.append(Weather.SIMPLEDATEFORMATS.DETAILED.format(weatherCard.getWhenNewServerDataExpected(context))); stringBuilder.append(". Due: "); stringBuilder.append(weatherCard.isNewServerDataExpected(context)); stringBuilder.append(lineBreak);
                }
            }
            stringBuilder.append(" * weather update period is every "); stringBuilder.append(WeatherSettings.getForecastUpdateIntervalInMillis(context)/1000/60/60); stringBuilder.append(" hours. Due: "); stringBuilder.append(WeatherSettings.isForecastCheckDue(context)); stringBuilder.append(lineBreak);
            stringBuilder.append(" * weather warnings update period is every "); stringBuilder.append(WeatherSettings.getWarningsUpdateIntervalInMillis(context)/1000/60); stringBuilder.append(" minutes. Due: "); stringBuilder.append(WeatherSettings.areWarningsOutdated(context)); stringBuilder.append(lineBreak);
            if (WeatherSettings.updateTextForecasts(context)){
                stringBuilder.append(" * last text update was "); stringBuilder.append(Weather.SIMPLEDATEFORMATS.DETAILED.format(WeatherSettings.getLastTextForecastsUpdateTime(context))); stringBuilder.append(". Due: "); stringBuilder.append(WeatherSettings.areTextForecastsOutdated(context)); stringBuilder.append(lineBreak);
            }
            if (WeatherSettings.serveGadgetBridge(context)){
                stringBuilder.append(" * serving GadgetBridge"); stringBuilder.append(lineBreak);
            }
            stringBuilder.append(" * current update tasks are: "); stringBuilder.append(updateTasks); stringBuilder.append(lineBreak);
            if (updateTasks.size()>0){
                stringBuilder.append(" * service started successfully: "); stringBuilder.append(result); stringBuilder.append(lineBreak);
            }
            stringBuilder.append("----------------------------------"); stringBuilder.append(lineBreak);
            PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,stringBuilder.toString());
        }
        /*
         * For API < 27 we use AlarmManager, for API equal or greater 27 we use JobSheduler with JobWorkItem.
        */
        if (Build.VERSION.SDK_INT < 26) {
            PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"setting new alarm in "+next_update_due_in_millis/1000/60+" minutes.");
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
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putString(UpdateJobService.ACTION,WeatherUpdateBroadcastReceiver.UPDATE_ACTION);
            jobInfo = new JobInfo.Builder(PRIVATE_JOBINFO_IDENTIFIER,new ComponentName(context,UpdateJobService.class))
                    .setExtras(persistableBundle)
                    .setMinimumLatency(next_update_due_in_millis)
                    .setOverrideDeadline(next_update_due_in_millis+5000)
                    .build();
            jobScheduler.enqueue(jobInfo,jobWorkItem);
            PrivateLog.log(context, PrivateLog.UPDATER,PrivateLog.INFO,"job scheduled in "+next_update_due_in_millis/1000/60+" minutes.");
        }
        return result;
    }

    public static void updateAppViews(Context context, CurrentWeatherInfo weatherCard){
        // update GadgetBridge
        GadgetbridgeAPI.sendWeatherBroadcastIfEnabled(context,weatherCard);
        WidgetRefresher.refresh(context);
        // save the last update time
        WeatherSettings.setViewsLastUpdateTime(context,Calendar.getInstance().getTimeInMillis());
    }

    public static boolean startDataUpdateService(final Context context, int updateSource, final ArrayList<String> tasks){
            Intent intent = new Intent(context,DataUpdateService.class);
            intent.putExtra(EXTRA_UPDATE_SOURCE,updateSource);
            boolean noInternetConnRequired = false;
            if (tasks.contains(DataUpdateService.SERVICEEXTRAS_UPDATE_WEATHER)){
                intent.putExtra(DataUpdateService.SERVICEEXTRAS_UPDATE_WEATHER,true);
            }
            if (tasks.contains(DataUpdateService.SERVICEEXTRAS_UPDATE_WARNINGS)){
                intent.putExtra(DataUpdateService.SERVICEEXTRAS_UPDATE_WARNINGS,true);
            }
            if (tasks.contains(DataUpdateService.SERVICEEXTRAS_UPDATE_TEXTFORECASTS)){
                intent.putExtra(DataUpdateService.SERVICEEXTRAS_UPDATE_TEXTFORECASTS,true);
            }
            if (tasks.contains(DataUpdateService.SERVICEEXTRAS_UPDATE_LAYERS)){
                intent.putExtra(DataUpdateService.SERVICEEXTRAS_UPDATE_LAYERS,true);
            }
            if (tasks.contains(DataUpdateService.SERVICEEXTRAS_UPDATE_POLLEN)){
                intent.putExtra(DataUpdateService.SERVICEEXTRAS_UPDATE_POLLEN,true);
            }
            if (tasks.contains(DataUpdateService.SERVICEEXTRAS_UPDATE_RAINRADAR)){
                intent.putExtra(DataUpdateService.SERVICEEXTRAS_UPDATE_RAINRADAR,true);
            }
            if (tasks.contains(DataUpdateService.SERVICEEXTRAS_UPDATE_LOCATIONSLIST)){
                ArrayList<Weather.WeatherLocation> weatherLocations = StationFavorites.getFavorites(context);
                if (weatherLocations.size()>0){
                    intent = intent.putParcelableArrayListExtra(Weather.WeatherLocation.PARCELABLE_NAME,weatherLocations);
                }
            }
            if (tasks.contains(DataUpdateService.SERVICEEXTRAS_CANCEL_NOTIFICATIONS) || WeatherSettings.notifyWarnings(context)){
                intent.putExtra(DataUpdateService.SERVICEEXTRAS_CANCEL_NOTIFICATIONS,true);
                noInternetConnRequired = true;
            }
            if (tasks.contains(DataUpdateService.SERVICEEXTRAS_UPDATE_NOTIFICATIONS)){
                intent.putExtra(DataUpdateService.SERVICEEXTRAS_UPDATE_NOTIFICATIONS,true);
                noInternetConnRequired = true;
            }
        if (tasks.contains(DataUpdateService.SERVICEEXTRAS_CRATE_AREADATABASE)){
            intent.putExtra(DataUpdateService.SERVICEEXTRAS_CRATE_AREADATABASE,true);
                noInternetConnRequired = true;
        }
        if (DataUpdateService.suitableNetworkAvailable(context) || noInternetConnRequired){
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            try {
                if (Build.VERSION.SDK_INT < 26) {
                    context.startService(intent);
                } else {
                    context.startForegroundService(intent);
                }
            } catch (Exception e) {
                return false;
            }
            // animation progress disabled because too slow on legacy devices
            /*
            Intent mainAppProgressIntent = new Intent();
            mainAppProgressIntent.setAction(MainActivity.MAINAPP_SHOW_PROGRESS);
            context.sendBroadcast(mainAppProgressIntent);
             */
            return true;
        }
        return false;
    }


}
