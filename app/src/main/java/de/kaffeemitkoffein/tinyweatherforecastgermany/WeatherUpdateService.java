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
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import java.net.URL;
import java.util.Calendar;

public class WeatherUpdateService extends Service {

    private NotificationManager notificationManager;
    int notification_id;

    public static String IC_ID = "WEATHER_NOTIFICATION";
    public static String IC_NAME = "Updating weather data";
    public static int    IC_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notification_id = (int) Calendar.getInstance().getTimeInMillis();
        startForeground(notification_id,getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        if (UpdateChecker.eligibleForForecastUpdate(this)){
            StationsArrayList stationsArrayList = new StationsArrayList(this);
            int position = stationsArrayList.getSetStationPositionByName(getApplicationContext());
            Station station = stationsArrayList.stations.get(position);
            URL stationURLs[] = station.getAbsoluteWebURLArray();
            final WeatherCard weatherCardArray[] = {new WeatherCard()};
            final Context context = this;
            WeatherForecastReader weatherForecastReader = new WeatherForecastReader(getApplicationContext()){
                @Override
                public void onPositiveResult(WeatherCard wc){
                    GadgetbridgeAPI gadgetbridgeAPI = new GadgetbridgeAPI(context);
                    gadgetbridgeAPI.sendWeatherBroadcastIfEnabled();
                    // notify widget
                    Intent intent = new Intent();
                    intent.setAction(ClassicWidget.WIDGET_CUSTOM_REFRESH_ACTION);
                    sendBroadcast(intent);
                    // notify main class
                    intent = new Intent();
                    intent.setAction(MainActivity.MAINAPP_CUSTOM_REFRESH_ACTION);
                    sendBroadcast(intent);
                    // set new alarms, so that the alert will not be fired too early next time.
                    UpdateAlarmManager.setUpdateAlarmsIfAppropriate(context);
                    stopSelf();
                }
                @Override
                public void onNegativeResult(){
                    stopSelf();
                }
            };
            weatherForecastReader.execute(stationURLs);
            return START_STICKY;
        } else {
           stopSelf();
           return START_NOT_STICKY;
        }
    }

    @Override
    public void onDestroy(){
        notificationManager.cancel(notification_id);
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
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .setChannelId(IC_ID)
                            .build();
        } else {
                    n = new Notification.Builder(getApplicationContext())
                            .setContentTitle(getResources().getString(R.string.service_notification_title))
                            .setContentText(getResources().getString(R.string.service_notification_text))
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setAutoCancel(true)
                            .build();
                }
                //
        // notificationManager.notify(NOTIFICATION_TAG,notification_id,n);
        Toast.makeText(getApplicationContext(),"Hello, World!",Toast.LENGTH_LONG).show();
        return n;
    }

}
