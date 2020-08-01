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

import java.util.ArrayList;

public class StationsArrayList {

    public ArrayList<Weather.WeatherLocation> stations = new ArrayList<Weather.WeatherLocation>();
    private Context context;

    /**
     * Public constructor. Needs the context.
     * @param context
     */

    public StationsArrayList(Context context){
        this.context = context;
        StationsManager stationsManager = new StationsManager(context);
        stations = stationsManager.getStations();
    }

    /**
     * Gets the position of a specific station by name. Returns position or -1 if the station name does
     * not exist in the stations list.
     * @param name
     * @return
     */

    public int getStationPositionByName(String name){
        for (int i=0; i<stations.size(); i++){
            if (stations.get(i).description.equals(name)){
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the position of the set up station.
     * @param context
     * @return
     */

    public int getSetStationPositionByName(Context context){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        return getStationPositionByName(weatherSettings.station);
    }

    /**
     * Returns an arraylist of the station names in the same order like in this class.
     * @return
     */

    public ArrayList<String> getStringArrayListOfNames(){
        ArrayList<String> result = new ArrayList<String>();
        for (int i=0; i<stations.size(); i++){
            result.add(stations.get(i).description);
        }
        return result;
    }
}

