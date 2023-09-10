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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeatherUpdateBroadcastReceiver extends BroadcastReceiver {

    public final static String UPDATE_ACTION = "de.kaffeemitkoffein.broadcast.REQUEST_UPDATE";

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
                PrivateLog.log(context, PrivateLog.UPDATER, PrivateLog.INFO, "+-> action is a update request.");
                checkForLocation(context);
                UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(context, UpdateAlarmManager.CHECK_FOR_UPDATE, null);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void checkForLocation(Context context) {
        String message = "* LOCATION CHECK: " + Weather.SIMPLEDATEFORMATS.TIME.format(new Date(Calendar.getInstance().getTimeInMillis()));
        PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,message);
        if (WeatherLocationManager.hasBackgroundLocationPermission(context)){
            Location location = WeatherLocationManager.getLastKnownLocation(context);
            if (location!=null) {
                Weather.WeatherLocation oldWeatherLocation = WeatherSettings.getLastPassiveLocation(context);
                Weather.WeatherLocation newWeatherLocation = new Weather.WeatherLocation(location);
                // DEBUG
                newWeatherLocation.time = Calendar.getInstance().getTimeInMillis();
                if (newWeatherLocation.time > oldWeatherLocation.time) {
                    // check for distance significance
                    float distance = oldWeatherLocation.toLocation().distanceTo(newWeatherLocation.toLocation());
                    // DEBUG
                    newWeatherLocation.accuracy = -12;
                    if (distance > newWeatherLocation.accuracy){
                        // is significant geo
                        // find closest station
                        Weather.WeatherLocation closestStation = findClosestStation(context,newWeatherLocation.toLocation());
                        if (closestStation!=null){
                            Weather.WeatherLocation currentStation = WeatherSettings.getSetStationLocation(context);
                            if (!closestStation.name.equals(currentStation.name)){
                                // new station is different to old one
                                WeatherSettings.setStation(context,closestStation);
                            }
                        }
                    }
                }
            }
        }
    }

    public static Weather.WeatherLocation findClosestStation(Context context, Location location) {
        StationsManager stationsManager = new StationsManager(context);
        stationsManager.readStations();
        ArrayList<Weather.WeatherLocation> stations = stationsManager.getStations();
        if (stations.size()>0) {
            stations = StationsManager.sortStationsByDistance(stations,location);
            return stations.get(0);
        }
        return null;
    }

}
