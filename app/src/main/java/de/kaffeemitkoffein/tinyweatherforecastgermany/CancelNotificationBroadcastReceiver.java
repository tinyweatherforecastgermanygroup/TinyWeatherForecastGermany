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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class CancelNotificationBroadcastReceiver extends BroadcastReceiver {

    public final static String CANCEL_NOTIFICATIONS_ACTION = "de.kaffeemitkoffein.broadcast.CANCEL_NOTIFICATIONS";
    public final static String CLEAR_NOTIFICATIONS_ACTION = "de.kaffeemitkoffein.broadcast.CLEAR_NOTIFICATIONS";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent!=null){
            String action = intent.getAction();
            if (action!=null){
                if (action.equalsIgnoreCase(CANCEL_NOTIFICATIONS_ACTION)){
                    // this starts the service with the only action being canceling notifications.
                    // the UpdateAlarmManager will call setCancelNotificationAlarm if necessary
                    PrivateLog.log(context,PrivateLog.ALERTS,PrivateLog.INFO,"Starting service to remove a notification.");
                    // start updater with an empty task list
                    ArrayList<String> updateTasks = new ArrayList<String>();
                    UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(context,UpdateAlarmManager.UPDATE_FROM_JOB,updateTasks,null);
                }
                if (action.equalsIgnoreCase(CLEAR_NOTIFICATIONS_ACTION)){
                    WeatherWarnings.clearAllNotified(context);
                    PrivateLog.log(context,PrivateLog.ALERTS,PrivateLog.INFO,context.getResources().getString(R.string.preference_clearnotifications_message));
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
            PrivateLog.log(context,PrivateLog.ALERTS,PrivateLog.INFO,"Registered alarm to remove notification at "+WeatherWarnings.getTimeMiniString(alarmTimeInMillis)+".");
        } else {
            PrivateLog.log(context,PrivateLog.ALERTS,PrivateLog.INFO,"Currently no notifications to cancel in the future. No alarm set.");
        }
    }
}
