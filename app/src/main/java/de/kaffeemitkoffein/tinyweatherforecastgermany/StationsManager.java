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
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class StationsManager {

    private Context context;
    private ArrayList<Weather.WeatherLocation> stations = new ArrayList<Weather.WeatherLocation>();

    public StationsManager(Context context){
        this.context = context;
        //readStations();
    }

    private String getStationsStringFromResource(){
        InputStream inputStream = context.getResources().openRawResource(R.raw.stations2);
        try {
            int size = inputStream.available();
            byte[] textdata = new byte[size];
            inputStream.read(textdata);
            inputStream.close();
            String text = new String(textdata);
            return text;
        } catch (IOException e) {
            return null;
        }
    }

    public ArrayList<Weather.WeatherLocation> readStations() {
        String stationString = getStationsStringFromResource();
        String[] station_items = stationString.split("\\|");
        int count = 0;
        for (int i = 0; i < station_items.length; i++) {
            Weather.WeatherLocation weatherLocation = new Weather.WeatherLocation();
            String[] station_values = station_items[i].split(";");
            weatherLocation.description = station_values[0];
            weatherLocation.name = station_values[1];
            String[] station_coordinates = station_values[2].split(",");
            weatherLocation.longitude = Location.convert(station_coordinates[0]);
            weatherLocation.latitude = Location.convert(station_coordinates[1]);
            weatherLocation.altitude = Location.convert(station_coordinates[2]);
            stations.add(weatherLocation);
            count++;
        }
        Collections.sort(stations, new Weather.WeatherLocation());
        return stations;
    }

    /*
     *               .
     *              /|\
     * Longitude     |   Längengrad
     *              \|/
     *               .
     * Latitude <------> Breitengrad
     *
     * Angaben in den DWD-Daten:
     * z.B.: HAMBURG INNENSTADT;P0489;9.98,53.55,8.0
     *  Angabe in +/- Graden
     * 1. Längengrad (longitude)
     * 2. Breitengrad (latitude)
     * 3. Höhe (altititude)
     */


    public class AsyncStationsReader extends AsyncTask<Void, Void, ArrayList<Weather.WeatherLocation>> {

        private Context context;
        private WeatherSettings weatherSettings;

        @Override
        protected ArrayList<Weather.WeatherLocation> doInBackground(Void... voids) {
            ArrayList<Weather.WeatherLocation> stations = readStations();
            return stations;
        }

        public void onLoadingListFinished(ArrayList<Weather.WeatherLocation> new_stations){
            // override as needed
            stations = new_stations;
        }

        @Override
        protected void onPostExecute(ArrayList<Weather.WeatherLocation> stations) {
            super.onPostExecute(stations);
            onLoadingListFinished(stations);
        }
    }

    public ArrayList<Weather.WeatherLocation> getStations(){
        return stations;
    }

    public ArrayList<String> getStationNames(){
        if (stations != null){
            ArrayList<String> names = new ArrayList<String>();
            for (int i=0; i<stations.size(); i++){
                Weather.WeatherLocation weatherLocation = stations.get(i);
                names.add(weatherLocation.description);
            }
            return names;
        }
        return null;
    }

    public int getSetPosition(){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        String description = weatherSettings.station_description;
        if (stations != null){
            for (int i=0; i<stations.size();i++){
                if (stations.get(i).description.equals(description)){
                    return i;
                }
            }
        }
        return 0;
    }

    public Weather.WeatherLocation getStation(int position){
        if (stations != null){
            if (position<stations.size()){
                Weather.WeatherLocation weatherLocation = new Weather.WeatherLocation();
                weatherLocation.name = stations.get(position).name;
                weatherLocation.description = stations.get(position).description;
                weatherLocation.longitude = stations.get(position).longitude;
                weatherLocation.latitude = stations.get(position).latitude;
                weatherLocation.altitude = stations.get(position).altitude;
                weatherLocation.name = stations.get(position).name;
                return weatherLocation;
            }
        }
        return null;
    }

    public String getName(int position){
        if (stations != null){
            if (position<stations.size()){
                return stations.get(position).name;
            }
        }
        return null;
    }

    public String getDescription(int position){
        if (stations != null){
            if (position<stations.size()){
                return stations.get(position).description;
            }
        }
        return null;
    }

    public double getLongitude(int position){
        if (stations != null){
            if (position<stations.size()){
                return stations.get(position).longitude;
            }
        }
        return 0;
    }

    public double getLatitude(int position){
        if (stations != null){
            if (position<stations.size()){
                return stations.get(position).latitude;
            }
        }
        return 0;
    }

    public double getAltitude(int position){
        if (stations != null){
            if (position<stations.size()){
                return stations.get(position).altitude;
            }
        }
        return 0;
    }

    public boolean setStation(int position){
        if (stations != null) {
            if (position < stations.size()) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(WeatherSettings.PREF_STATION_NAME,stations.get(position).name);
                editor.putString(WeatherSettings.PREF_STATION_DESCRIPTION,stations.get(position).description);
                editor.putFloat(WeatherSettings.PREF_STATION_LONGITUDE,(float) stations.get(position).longitude);
                editor.putFloat(WeatherSettings.PREF_STATION_LATIDTUDE,(float) stations.get(position).latitude);
                editor.putFloat(WeatherSettings.PREF_STATION_ALTITUDE,(float) stations.get(position).altitude);
                editor.commit();
                return true;
            }
        }
        return false;
    }

    public int getStationCount(){
        if (stations != null){
            return stations.size();
        }
        return 0;
    }

    public Integer getPositionFromDescription(String description){
        if (stations!=null){
            int index = 0;
            while (index < stations.size()){
                if (stations.get(index).description.toUpperCase().equals(description.toUpperCase())){
                    return index;
                }
                index ++;
            }
        }
        return null;
    }

}

