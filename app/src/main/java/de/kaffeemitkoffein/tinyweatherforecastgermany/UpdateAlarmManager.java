/**
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UpdateAlarmManager {

    private static final int PRIVATE_ALARM_IDENTIFIER = 0;
    private static final int PRIVATE_JOBINFO_IDENTIFIER = 1;
    public static final int NOTIFICATION_ALARM_IDENTIFIER = 2;

     // time to elapse before a next update try when network not available.
    private static final int EARLY_ALARM_TIME = 1000*60*15; // 15 minutes in millis

    // time interval to loop the JobSheduler/Alarm manager
    public static int VIEWS_UPDATE_INTERVAL_DEFAULT = 30*60*1000; // 30 minutes;
    public static int VIEWS_UPDATE_INTERVAL         = VIEWS_UPDATE_INTERVAL_DEFAULT;
    // suppress any view update actions if this time did not pass since last view update
    public static int VIEWS_MAXUPDATETIME_DEFAULT   = 10*60*1000; // 10 minutes;
    public static int VIEWS_MAXUPDATETIME   = VIEWS_MAXUPDATETIME_DEFAULT;

    // used when no valid data available (anymore) from MainActivity, when update triggered by user
    // or during API-testing
    // meaning: always update
    public static final int FORCE_UPDATE = 1;
    // indicates update from widget
    public static final int WIDGET_UPDATE = 2;
    // called from MainActivtity, onBootComplete and/or if update has been requested by 3rd party app
    public static final int CHECK_FOR_UPDATE = 4;
    // update everything and all locations from history
    public static final int TRAVEL_UPDATE = 128;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean updateAndSetAlarmsIfAppropriate(Context context, int update_mode){
        adaptUpdateIntervalsToSettings(context);
        WeatherSettings weatherSettings = new WeatherSettings(context);
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(context);
        boolean travelUpdate = (update_mode&TRAVEL_UPDATE)==TRAVEL_UPDATE;
        /*
         * Create alarms to cancel notifications if applicable
         */
        if (WeatherSettings.notifyWarnings(context)){
            CancelNotificationBroadcastReceiver.setCancelNotificationsAlarm(context);
        }
        /*
         * update_period: this is the update interval from the settings. It means how often
         * data should be polled from the DWD API.
         */
        long update_period = weatherSettings.getForecastUpdateIntervalInMillis();
        // set time for timer to equal next interval as set up by user
        // weatherCard can be null on first app launch or after clearing memory or if station
        // was not used before.
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
        PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"Update interval: "+update_period/1000/60/60);
        if (weatherCard!=null){
            PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"Data issued: "+new Date(weatherCard.issue_time).toString());
            PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"Last data poll: "+new Date(weatherCard.polling_time).toString());
            PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"New data expected: "+new Date(weatherCard.getWhenNewServerDataExpected()).toString());
            //PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"Is new expected: "+weatherCard.isNewServerDataExpected());
            //PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"is 6h: "+weatherSettings.forecastUpdateIntervalIs6h());
        }
        PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"Next update due: "+new Date(update_time_utc).toString());
        boolean result = false;
        if (    ((weatherCard==null)) ||
                ((weatherSettings.serve_gadgetbridge) && (update_time_utc <= Calendar.getInstance().getTimeInMillis())) ||
                ((weatherSettings.setalarm) && (update_time_utc <= Calendar.getInstance().getTimeInMillis())) ||
                ((update_mode==CHECK_FOR_UPDATE) && (update_time_utc <= Calendar.getInstance().getTimeInMillis())) ||
                ((update_mode==WIDGET_UPDATE) && (update_time_utc <= Calendar.getInstance().getTimeInMillis())) ||
                ((update_mode&FORCE_UPDATE)==FORCE_UPDATE) ||
                (!(update_time_utc <= Calendar.getInstance().getTimeInMillis()) && (weatherCard.isNewServerDataExpected() && weatherSettings.forecastUpdateIntervalIs6h()) && (weatherSettings.setalarm))){
            // update now.
            // In case of success and failure of update the views (gadgetbridge and widgets) will get updated directly
            // from the service. Therefore, views are only updated from here if the service has not been called.
            PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"triggering weather update from API...");
            try {
                result = startDataUpdateService(context,true,WeatherSettings.updateWarnings(context),WeatherSettings.updateTextForecasts(context),travelUpdate);
            } catch (SecurityException e){
                PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.WARN,"WeatherUpdateService (weather forecasts) not started because of a SecurityException: "+e.getMessage());
                // views need to be updated from here, because starting service failed!
                updateAppViews(context);
            }
            catch (IllegalStateException e){
                PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.WARN,"WeatherUpdateService (weather forecasts) not started because of an IllegalStateException, the device is probably in doze mode: "+e.getMessage());
                // views need to be updated from here, because starting service failed!
                updateAppViews(context);
            }
        } else {
            // check if an update of warnings only for widgets applies
            // or if notification is set
            if (((update_mode==WIDGET_UPDATE) && (weatherSettings.widget_displaywarnings) && (WeatherSettings.areWarningsOutdated(context))) ||
               ((weatherSettings.notify_warnings) && (WeatherSettings.areWarningsOutdated(context)))){
                PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"triggering warnings update from API...");
                try {
                    result = startDataUpdateService(context,false,true,false,false);
                } catch (SecurityException e){
                    PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.WARN,"WeatherUpdateService (weather warnings only for widgets) not started because of a SecurityException: "+e.getMessage());
                    // views need to be updated from here, because starting service failed!
                    updateAppViews(context);
                }
                catch (IllegalStateException e){
                    PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.WARN,"WeatherUpdateService (weather warnings only for widgets) not started because of an IllegalStateException, the device is probably in doze mode: "+e.getMessage());
                    // views need to be updated from here, because starting service failed!
                    updateAppViews(context);
                }
            } else {
                // update not due, neither for forecasts nor for warnings (widgets)
                PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"update from API not due.");
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
            jobInfo = new JobInfo.Builder(PRIVATE_JOBINFO_IDENTIFIER,new ComponentName(context,UpdateJobService.class))
                    .setMinimumLatency(next_update_due_in_millis)
                    .build();
            jobScheduler.enqueue(jobInfo,jobWorkItem);
            PrivateLog.log(context, PrivateLog.UPDATER,PrivateLog.INFO,"job scheduled in "+next_update_due_in_millis/1000/60+" minutes.");
        }
        return result;
    }

    public static boolean updateWarnings(Context context, boolean forceUpdate){
        if (WeatherSettings.areWarningsOutdated(context) || forceUpdate) {
            try {
                startDataUpdateService(context,false,true,false,false);
                return true;
            } catch (SecurityException e){
                PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.WARN,"WeatherUpdateService (warnings) not started because of a SecurityException: "+e.getMessage());
                // views need to be updated from here, because starting service failed!
                updateAppViews(context);
                return false;
            }
            catch (IllegalStateException e){
                PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.WARN,"WeatherUpdateService (warnings) not started because of an IllegalStateException, the device is probably in doze mode: "+e.getMessage());
                // views need to be updated from here, because starting service failed!
                updateAppViews(context);
                return false;
            }
        }
        return false;
    }

    public static boolean updateTexts(Context context){
        try {
            startDataUpdateService(context,false,false,true,false);
            return true;
        } catch (SecurityException e){
            PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.WARN,"WeatherUpdateService (warnings) not started because of a SecurityException: "+e.getMessage());
            // views need to be updated from here, because starting service failed!
            updateAppViews(context);
            return false;
        }
        catch (IllegalStateException e){
            PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.WARN,"WeatherUpdateService (warnings) not started because of an IllegalStateException, the device is probably in doze mode: "+e.getMessage());
            // views need to be updated from here, because starting service failed!
            updateAppViews(context);
            return false;
        }
    }

    public static void updateAppViews(Context context){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        // update GadgetBridge
        if (weatherSettings.serve_gadgetbridge) {
            GadgetbridgeAPI gadgetbridgeAPI = new GadgetbridgeAPI(context);
            gadgetbridgeAPI.sendWeatherBroadcastIfEnabled();
        }
        updateWarningsViews(context);
        // save the last update time
        weatherSettings.views_last_update_time = Calendar.getInstance().getTimeInMillis();
        weatherSettings.applyPreference(WeatherSettings.PREF_VIEWS_LAST_UPDATE_TIME,weatherSettings.views_last_update_time);
    }

    public static void updateWarningsViews(Context context){
        // update widgets unconditionally
        PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"updating widgets.");
        WidgetRefresher.refresh(context);
    }

    public static boolean startDataUpdateService(final Context context, final ArrayList<String> tasks){
        Intent intent = new Intent(context,DataUpdateService.class);
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
        if (tasks.contains(DataUpdateService.SERVICEEXTRAS_UPDATE_LOCATIONSLIST)){
            ArrayList<Weather.WeatherLocation> weatherLocations = WeatherSettings.getFavoritesWeatherLocations(context);
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
        if (DataUpdateService.isConnectedToInternet(context) || noInternetConnRequired){
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            if (Build.VERSION.SDK_INT<26){
                context.startService(intent);
            } else {
                context.startForegroundService(intent);
            }
            return true;
        }
        return false;
    }

    private static boolean startDataUpdateService(final Context context, final boolean updateWeather, final boolean updateWarnings, final boolean updateTextForecasts, final boolean travelUpdate){
        ArrayList<String> tasks = new ArrayList<String>();
        if (updateWeather){
            tasks.add(DataUpdateService.SERVICEEXTRAS_UPDATE_WEATHER);
        }
        if (updateWarnings){
            tasks.add(DataUpdateService.SERVICEEXTRAS_UPDATE_WARNINGS);
        }
        if (updateTextForecasts){
            tasks.add(DataUpdateService.SERVICEEXTRAS_UPDATE_TEXTFORECASTS);
        }
        if (travelUpdate){
            tasks.add(DataUpdateService.SERVICEEXTRAS_UPDATE_LOCATIONSLIST);
        }
        return startDataUpdateService(context,tasks);
    }
}
