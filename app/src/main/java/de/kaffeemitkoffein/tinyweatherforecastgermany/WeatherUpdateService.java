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

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;

import java.util.Calendar;

public class WeatherUpdateService extends Service {

    private NotificationManager notificationManager;
    int notification_id;

    public static String IC_ID = "WEATHER_NOTIFICATION";
    public static String IC_NAME = "Updating weather data";
    public static int    IC_IMPORTANCE = NotificationManager.IMPORTANCE_LOW;
    public static String SERVICE_FORCEUPDATE="SERVICE_FORCEUPDATE";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        PrivateLog.log(this,Tag.SERVICE,"service started: onCreate");
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notification_id = (int) Calendar.getInstance().getTimeInMillis();
        startForeground(notification_id,getNotification());
        PrivateLog.log(this,Tag.SERVICE,"service is foreground now");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        // hack to prevent too frequent api calls
        PrivateLog.log(this,Tag.SERVICE,"service started: onStartCommand");
        WeatherSettings weatherSettings = new WeatherSettings(this);
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(this);
        if (weatherCard != null){
            if (weatherCard.polling_time>Calendar.getInstance().getTimeInMillis()-10000){
                PrivateLog.log(this,Tag.SERVICE,"update cancelled, too frequent call!");
                stopSelf();
            }
        }
        // abort update if there is no internet connection.
        if (!isConnectedToInternet()){
            PrivateLog.log(this,Tag.SERVICE,"update cancelled, no internet connection");
            /*  Check if the user desires to repeat updates when no network is found. If yes, a retry is scheduled
             *  in 5 minutes. Other errors that occur later in this process (404 error, parsing errors) will
             *  NOT trigger a retry update to protect the DWD api from too frequent calls.
             *
             *  Certain conditions like captive portals, local networks with no internet access etc. may
             *  appear as a valid internet connection at first glance and will also NOT trigger
             *  repeated updates.
             */
            if (weatherSettings.aggressive_update){
                UpdateAlarmManager.setEarlyAlarm(this);
            }
            stopSelf();
        }
        final Context context = this;
        WeatherForecastReader weatherForecastReader = new WeatherForecastReader(this){
            @Override
            public void onPositiveResult(){
                // update GadgetBridge and widgets
                UpdateAlarmManager.updateAppViews(context);
                // notify main class
                Intent intent = new Intent();
                intent.setAction(MainActivity.MAINAPP_CUSTOM_REFRESH_ACTION);
                sendBroadcast(intent);
                PrivateLog.log(context,Tag.SERVICE,"update from API: success");
                stopSelf();
            }
            @Override
            public void onNegativeResult(){
                PrivateLog.log(context,Tag.SERVICE,"update from API: failed, error.");
                if (ssl_exception){
                    PrivateLog.log(context,Tag.SERVICE,"SSL exception detected by service.");
                    Intent ssl_intent = new Intent();
                    ssl_intent.setAction(MainActivity.MAINAPP_SSL_ERROR);
                    sendBroadcast(ssl_intent);
                }
                // need to update main app with old data
                Intent intent = new Intent();
                intent.setAction(MainActivity.MAINAPP_CUSTOM_REFRESH_ACTION);
                sendBroadcast(intent);
                // need to update views with old data: GadgetBridge and widgets
                UpdateAlarmManager.updateAppViews(context);
                stopSelf();
            }
        };
        PrivateLog.log(this,Tag.SERVICE,"starting update from API");
        // display progressbar in main app
        Intent progressbar_intent = new Intent();
        progressbar_intent.setAction(MainActivity.MAINAPP_SHOW_PROGRESS);
        sendBroadcast(progressbar_intent);
        // start update
        weatherForecastReader.execute();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        notificationManager.cancel(notification_id);
        // hide progressbar in main app
        Intent progressbar_intent = new Intent();
        progressbar_intent.setAction(MainActivity.MAINAPP_HIDE_PROGRESS);
        sendBroadcast(progressbar_intent);
        PrivateLog.log(this,Tag.SERVICE,"destroyed.");
    }

    @Deprecated
    private Notification getNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel nc = new NotificationChannel(IC_ID,IC_NAME,IC_IMPORTANCE);
            nc.setDescription(getResources().getString(R.string.service_notification_channelname));
            nc.setShowBadge(true);
            notificationManager.createNotificationChannel(nc);
        }
        // Generate a unique ID for the notification, derived from the current time. The tag ist static.
        Notification n;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            n = new Notification.Builder(getApplicationContext())
                            .setContentTitle(getResources().getString(R.string.service_notification_title))
                            .setContentText(getResources().getString(R.string.service_notification_text))
                            .setSmallIcon(R.mipmap.schirm_weiss)
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .setChannelId(IC_ID)
                            .build();
        } else {
                    n = new Notification.Builder(getApplicationContext())
                            .setContentTitle(getResources().getString(R.string.service_notification_title))
                            .setContentText(getResources().getString(R.string.service_notification_text))
                            .setSmallIcon(R.mipmap.schirm_weiss)
                            .setAutoCancel(true)
                            .build();
                }
        return n;
    }

    private boolean isConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                // returns if the network can establish connections and pass data.
                return networkInfo.isConnected();
            }
        }
        return false;
    }

}
