/*
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
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class StationFavorites {

    public final static String FAVORITES_SEPARATOR = ";";
    ArrayList<Weather.WeatherLocation> favorites;
    private Context context;

    public StationFavorites(Context context){
        this.context = context;
        favorites = getFavorites(context);
    }

    public static ArrayList<Weather.WeatherLocation> getFavorites(Context context){
        String rawFavorites = WeatherSettings.getFavorites2(context);
        if (rawFavorites.length()==0){
            return resetFavorites(context);
        }
        String[] favoriteStrings = rawFavorites.split(FAVORITES_SEPARATOR);
        ArrayList<Weather.WeatherLocation> favorites = new ArrayList<Weather.WeatherLocation>();
        for (int i=0; i<favoriteStrings.length; i++){
            favorites.add(new Weather.WeatherLocation(favoriteStrings[i]));
        }
        return favorites;
    }

    public static ArrayList<Weather.WeatherLocation> saveFavorites(Context context, ArrayList<Weather.WeatherLocation> favorites){
        // remove double entries
        ArrayList<Weather.WeatherLocation> sanitizedFavorites = new ArrayList<Weather.WeatherLocation>();
        int count = 0;
        for (int i=0; (i<favorites.size()) && (count<10); i++){
            Weather.WeatherLocation favorite = favorites.get(i);
            if (!favorite.isInList(sanitizedFavorites)){
                sanitizedFavorites.add(favorite);
                count++;
            }
        }
        // build raw data string
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<sanitizedFavorites.size(); i++){
            stringBuilder.append(sanitizedFavorites.get(i).serializeToString());
            if (i<sanitizedFavorites.size()-1){
                stringBuilder.append(FAVORITES_SEPARATOR);
            }
        }
        WeatherSettings.putFavorites2(context,stringBuilder.toString());
        Log.v("tinyweather","------------------");
        for (int i=0; i<sanitizedFavorites.size(); i++){
            Log.v("tinyweather",i+": "+sanitizedFavorites.get(i).getDescription(context));
        }
        return sanitizedFavorites;
    }

    public void addFavorite(Weather.WeatherLocation newFavorite){
        ArrayList<Weather.WeatherLocation> newFavorites = new ArrayList<Weather.WeatherLocation>();
        newFavorites.add(newFavorite);
        newFavorites.addAll(favorites);
        favorites = saveFavorites(context,newFavorites);
    }

    public static Weather.WeatherLocation getDefaultWeatherLocation(){
        Weather.WeatherLocation defaultStation = new Weather.WeatherLocation();
        defaultStation.longitude = WeatherSettings.PREF_LONGITUDE_DEFAULT;
        defaultStation.latitude = WeatherSettings.PREF_LATITUDE_DEFAULT;
        defaultStation.altitude = WeatherSettings.PREF_ALTITUDE_DEFAULT;
        defaultStation.setName(WeatherSettings.PREF_STATION_NAME_DEFAULT);
        defaultStation.setDescriptionAlternate(Weather.WeatherLocation.EMPTYVALUE);
        defaultStation.setDescription(WeatherSettings.PREF_LOCATION_DESCRIPTION_DEFAULT);
        return defaultStation;
    }

    public static void deleteList(Context context){
        StationFavorites stationFavorites = new StationFavorites(context);
        stationFavorites.favorites = new ArrayList<Weather.WeatherLocation>();
        Weather.WeatherLocation setStation = WeatherSettings.getSetStationLocation(context);
        if (!setStation.hasAlternateDescription()){
            setStation.setDescriptionAlternate(WeatherLocationManager.getDescriptionAlternate(context,setStation));
        }
        stationFavorites.favorites.add(setStation);
        StationFavorites.saveFavorites(context,stationFavorites.favorites);
    }

    public static ArrayList<Weather.WeatherLocation> resetFavorites(Context context){
        Weather.WeatherLocation favorite = getDefaultWeatherLocation();
        ArrayList<Weather.WeatherLocation> favorites = new ArrayList<Weather.WeatherLocation>();
        favorites.add(favorite);
        saveFavorites(context, favorites);
        return favorites;
    }

}
