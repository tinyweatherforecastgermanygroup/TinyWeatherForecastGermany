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

import android.content.Context;

import java.util.Calendar;

public class UpdateChecker {

    public UpdateChecker(){
        // nothing to do here.
    }

    public static boolean eligibleForForecastUpdate(Context c) {
        // get settings and time values
        WeatherSettings weatherSettings = new WeatherSettings(c);
        long update_hours = weatherSettings.getUpdateInterval();
        long time = Calendar.getInstance().getTimeInMillis();
        // 3 600 000 millisecs = 1 hour
        WeatherForecastContentProvider weatherForecastContentProvider = new WeatherForecastContentProvider();
        WeatherCard weatherCard = weatherForecastContentProvider.readWeatherForecast(c);
        if (weatherCard != null) {
            if (time >= ((update_hours * 3600000) + weatherCard.polling_time)) {
                // update data because data too old
                return true;
            } else {
                // no update necessary
                return false;
            }
        } else {
            // update because no data present
            return true;
        }
    }
}
