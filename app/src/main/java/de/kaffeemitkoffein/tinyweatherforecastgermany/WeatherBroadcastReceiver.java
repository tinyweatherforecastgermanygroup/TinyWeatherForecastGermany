/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021, 2022, 2023, 2024 Pawel Dube
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;

/*

 This is a simple BroadcastReceiver to demonstrate how weather data from this app can be included
 in the Gadgetbridge app. It is not used in this app.

 You need to put this receiver in AndroidManifest.xml:

 <receiver android:name=".WeatherBroadcastReceiver"
           android:exported="true">
     <intent-filter>
          <action android:name="de.kaffeemitkoffein.broadcast.WEATHERDATA"/>
     </intent-filter>
 </receiver>

 To manually trigger a weather update FROM gadgetbridge, do a broadcast with the following action:

 public final static String UPDATE_ACTION = "de.kaffeemitkoffein.broadcast.REQUEST_UPDATE";
 ...
 Intent intent = new Intent();
 intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
 intent.setPackage("de.kaffeemitkoffein.tinyweatherforecastgermany");
 intent.setAction(UPDATE_ACTION);
 sendBroadcast(intent);
 ...

 Anyway, this app - once enabled by the user - will send every weather update to Gadgetbridge.

 */

public class WeatherBroadcastReceiver extends BroadcastReceiver {

    public final static String WEATHER_EXTRA="WeatherSpec";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                WeatherSpec weatherSpec = bundle.getParcelable(WEATHER_EXTRA);
                if (weatherSpec != null){
                    /*
                     * display the weather data on a wearable
                     */
                }
            }
        }
    }
}
