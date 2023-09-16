package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;

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

    public ArrayList<String> getFavoriteDescriptions(){
        ArrayList<String> descriptions = new ArrayList<String>();
        for (int i=0; i<favorites.size(); i++){
            descriptions.add(favorites.get(i).description);
        }
        return descriptions;
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
        defaultStation.name = WeatherSettings.PREF_STATION_NAME_DEFAULT;
        defaultStation.description = WeatherSettings.PREF_LOCATION_DESCRIPTION_DEFAULT;
        return defaultStation;
    }

    public void deleteList(){
        if (favorites.size()>0){
            ArrayList<Weather.WeatherLocation> newFavorites = new ArrayList<Weather.WeatherLocation>();
            newFavorites.add(favorites.get(0));
            favorites = newFavorites;
            saveFavorites(context,favorites);
        }
    }

    public static ArrayList<Weather.WeatherLocation> resetFavorites(Context context){
        Weather.WeatherLocation favorite = getDefaultWeatherLocation();
        ArrayList<Weather.WeatherLocation> favorites = new ArrayList<Weather.WeatherLocation>();
        favorites.add(favorite);
        saveFavorites(context, favorites);
        return favorites;
    }

}
