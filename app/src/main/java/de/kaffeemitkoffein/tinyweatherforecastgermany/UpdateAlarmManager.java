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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Calendar;

public class UpdateAlarmManager {

    private static final int PRIVATE_ALARM_IDENTIFIER = 0;
    public static final boolean FORCE_UPDATE = true;
    public static final boolean CHECK_FOR_UPDATE = false;

    private UpdateAlarmManager(){
    }

    public static boolean updateAndSetAlarmsIfAppropriate(Context context, boolean force_update){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        long update_period = weatherSettings.getUpdateIntervalInMillis();
        WeatherForecastContentProvider weatherForecastContentProvider = new WeatherForecastContentProvider();
        WeatherCard weatherCard = weatherForecastContentProvider.readWeatherForecast(context.getApplicationContext());
        // set time for timer to equal next interval as set up by user
        // weatherCard can be null on first app launch or after clearing memory
        long update_time_utc = Calendar.getInstance().getTimeInMillis() + update_period;
        if (weatherCard != null){
            update_time_utc = weatherCard.polling_time + update_period;
        }
        long next_update_time_realtime = SystemClock.elapsedRealtime()+update_period;
        boolean result;
        if ((update_time_utc <= Calendar.getInstance().getTimeInMillis()) || (force_update)){
            // update now
            Intent intent = new Intent(context,WeatherUpdateService.class);
            intent.putExtra(WeatherUpdateService.SERVICE_FORCEUPDATE,true);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.startService(intent);
            // set result to true, as update was initiated
            result = true;
        } else {
            // update not due, but it is safer to renew the alarm anyway
            // the time left until next update is considered:
            next_update_time_realtime = SystemClock.elapsedRealtime() + (update_time_utc-Calendar.getInstance().getTimeInMillis());
            // set result to false, as only timer was refreshed and update is not due
            result = false;
        }
        // update later, set timer if wanted by user
        if (weatherSettings.setalarm){
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context,WeatherUpdateBroadcastReceiver.class);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.setAction(WeatherUpdateBroadcastReceiver.UPDATE_ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,PRIVATE_ALARM_IDENTIFIER,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME,next_update_time_realtime,pendingIntent);
        }
        return result;
    }

    public static boolean updateAndSetAlarmsIfAppropriate(Context context){
        return updateAndSetAlarmsIfAppropriate(context,CHECK_FOR_UPDATE);
    }

    public static boolean updateAndSetAlarmsIfAppropriate(Context context, String debugtext){
        return updateAndSetAlarmsIfAppropriate(context,CHECK_FOR_UPDATE);
    }

}
