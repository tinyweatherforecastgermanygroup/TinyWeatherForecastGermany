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

import android.content.Context;
import android.location.Location;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StationsManager {

    private Context context;
    public ArrayList<Weather.WeatherLocation> stations = new ArrayList<Weather.WeatherLocation>();
    public boolean loaded = false;

    public StationsManager(Context context){
        this.context = context;
        stations = new ArrayList<Weather.WeatherLocation>();
        this.loaded = false;
    }

    public StationsManager(Context context, ArrayList<Weather.WeatherLocation> stations){
        this.context = context;
        this.stations = stations;
        this.loaded = false;
        if (stations!=null){
            if (stations.size()>0){
                this.loaded = true;
            }
        }
    }

    private static String getStationsStringFromResource(Context context){
        InputStream inputStream = context.getResources().openRawResource(R.raw.stations5);
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

    public static ArrayList<Weather.WeatherLocation> readStations(Context context) {
        ArrayList<Weather.WeatherLocation> stations = new ArrayList<Weather.WeatherLocation>();
        String stationString = getStationsStringFromResource(context);
        String[] station_items = stationString.split("\\|");
        int count = 0;
        for (int i = 0; i < station_items.length; i++) {
            Weather.WeatherLocation weatherLocation = new Weather.WeatherLocation();
            String[] station_values = station_items[i].split(";");
            if (station_values.length>3){
                weatherLocation.type=Integer.parseInt(station_values[0]);
                weatherLocation.setDescription(station_values[1]);
                weatherLocation.setName(station_values[2]);
                /*
                if (weatherLocation.type==RawWeatherInfo.Source.DMO){
                    weatherLocation.description = weatherLocation.description+" (Gebiet)";
                }
                 */
                String[] station_coordinates = station_values[3].split(",");
                if (station_coordinates.length>2){
                    weatherLocation.longitude = Location.convert(station_coordinates[0]);
                    weatherLocation.latitude = Location.convert(station_coordinates[1]);
                    weatherLocation.altitude = Location.convert(station_coordinates[2]);
                    stations.add(weatherLocation);
                } else {
                    PrivateLog.log(context,PrivateLog.STATIONS,PrivateLog.ERR,"Error parsing station geo-data (ignoring): "+station_values[2]+" => "+station_items[i]);
                }
            } else {
                PrivateLog.log(context,PrivateLog.STATIONS,PrivateLog.ERR,"Error parsing station (ignoring): "+station_items[i] +" (items found: "+station_values.length+")");
            }
            count++;
        }
        Collections.sort(stations, new Weather.WeatherLocation());
        return stations;
    }

    public void readStations(){
        stations = readStations(context);
        loaded = true;
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

    public static class StationsReader implements Runnable {

        private Context context;

        public StationsReader(Context context){
            this.context = context;
        }

        public void onLoadingListFinished(ArrayList<Weather.WeatherLocation> new_stations){
            // override as needed
        }

        @Override
        public void run() {
            ArrayList<Weather.WeatherLocation> stations = readStations(context);
            onLoadingListFinished(stations);
        }
    }


    public ArrayList<Weather.WeatherLocation> getStations(){
        return stations;
    }

    public static boolean StringArrayContains(String s, String[] strings){
        for (int i=0; i<strings.length; i++){
            if (strings[i].equalsIgnoreCase(s)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Weather.WeatherLocation> getStations(String[] ignoreNames){
        if (ignoreNames!=null){
            ArrayList<Weather.WeatherLocation> resultStations = new ArrayList<>();
            for (int i=0; i<stations.size(); i++){
                Weather.WeatherLocation station = stations.get(i);
                if (!StringArrayContains(station.getName(),ignoreNames)){
                    resultStations.add(station);
                }
            }
            return resultStations;
        }
        return stations;
    }

    public String getName(int position){
        if (stations != null){
            if (position<stations.size()){
                return stations.get(position).getName();
            }
        }
        return null;
    }

    public String getDescription(int position){
        if (stations != null){
            if (position<stations.size()){
                return stations.get(position).getDescription(context);
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

    public int getStationCount(){
        if (stations != null){
            return stations.size();
        }
        return 0;
    }

    public Integer getPositionFromDescription(String description){
        Integer index_dmo = null;
        if (stations!=null){
            int index = 0;
            while (index < stations.size()){
                if (stations.get(index).getOriginalDescription().toUpperCase().equals(description.toUpperCase())){
                    // priorize mos source and return immediately when found
                    if (stations.get(index).type==RawWeatherInfo.Source.MOS){
                        return index;
                    } else {
                        // return other source (dmo) after running through whole list
                        index_dmo = index;
                    }
                }
                index ++;
            }
        }
        return index_dmo;
    }

    public Integer getPositionFromDescription(String description, boolean lenient){
        Integer index_dmo = null;
        if (stations!=null){
            // try exact search always first
            Integer i = getPositionFromDescription(description);
            // second, try lenient umlaut variants
            if ((i==null) & (lenient)) {
                int index = 0;
                while (index < stations.size()){
                    if ((stations.get(index).getOriginalDescription().toUpperCase().equals(StationSearchEngine.toUmlaut(description))) ||
                        (stations.get(index).getOriginalDescription().toUpperCase().equals(StationSearchEngine.toInternationalUmlaut(description)))){
                        // priorize mos source and return immediately when found
                        if (stations.get(index).type==RawWeatherInfo.Source.MOS){
                            return index;
                        } else {
                            // return other source (dmo) after running through whole list
                            index_dmo = index;
                        }
                    }
                    index ++;
                }
            } else {
                return i;
            }
        }
        return index_dmo;
    }

    public Integer getPositionFromName(String name){
        Integer index_dmo = null;
        if (stations!=null){
            int index = 0;
            while (index < stations.size()){
                if (stations.get(index).getName().toUpperCase().equals(name.toUpperCase())){
                    // priorize mos source and return immediately when found
                    if (stations.get(index).type==RawWeatherInfo.Source.MOS){
                        return index;
                    } else {
                        // return other source (dmo) after running through whole list
                        index_dmo = index;
                    }
                }
                index ++;
            }
        }
        return index_dmo;
    }

    public Weather.WeatherLocation getFromName(String name){
        if (stations.size()>0){
            Integer position = getPositionFromName(name);
            if (position!=null){
                return stations.get(position);
            }
        }
        return null;
    }

    public Weather.WeatherLocation getLocationFromDescription(String description){
        Integer position = getPositionFromDescription(description);
        if (position!=null){
            Weather.WeatherLocation weatherLocation = stations.get(position);
            return weatherLocation;
        }
        return null;
    }

    public ArrayList<Weather.WeatherLocation> getStationsInPolygon(final Polygon polygon){
        if (stations==null){
            return null;
        }
        if (stations.size()==0){
            return null;
        }
        ArrayList<Weather.WeatherLocation> resultList = new ArrayList<Weather.WeatherLocation>();
        for (int i=0; i<stations.size();i++){
            Weather.WeatherLocation station = stations.get(i);
            if (polygon.isInPolygon((float) station.latitude,(float) station.longitude)){
                resultList.add(station);
            }
        }
        return resultList;
    }

    public static ArrayList<Weather.WeatherLocation> sortStationsByDistance(ArrayList<Weather.WeatherLocation> stations, final Location targetLocation){
        for (int i=0; i<stations.size(); i++){
            Location location_station = new Location("weather");
            location_station.setLatitude(stations.get(i).latitude);
            location_station.setLongitude(stations.get(i).longitude);
            stations.get(i).distance = location_station.distanceTo(targetLocation);
        }
        Collections.sort(stations, new Comparator<Weather.WeatherLocation>() {
            @Override
            public int compare(Weather.WeatherLocation t0, Weather.WeatherLocation t1) {
                if (t0.distance<t1.distance){
                    return -1;
                }
                if (t0.distance==t1.distance){
                    return 0;
                }
                return 1;
            }
        });
        return stations;
    }




}

