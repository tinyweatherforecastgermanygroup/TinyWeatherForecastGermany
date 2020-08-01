package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class StationsManager {

    private Context context;

    public StationsManager(Context context){
        this.context = context;
    }

    private String getStationsStringFromResource(){
        InputStream inputStream = context.getResources().openRawResource(R.raw.stations);
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

    private String getStringValue(String source, String seperator){
        int pos = source.indexOf(seperator);
        // return whole string when no seperator present and delete source
        String result;
        if (pos == -1){
            result = source;
        } else {
            // create result-string (=value)
            result = source.substring(0,pos);
        }
        return result;
    }

    private String cutSourceString(String source, String seperator){
        int pos = source.indexOf(seperator);
        // return whole string when no seperator present and delete source
        String result;
        if (pos == -1){
            result = "";
        } else {
            result = source.substring(pos+1);
        }
        return result;
    }

    public ArrayList<Weather.WeatherLocation> getStations(){
        ArrayList<Weather.WeatherLocation> stations = new ArrayList<Weather.WeatherLocation>();
        String stationString = getStationsStringFromResource();
        while (stationString.length()>0){
            // example: NEUHERBERG;G262;10.28,49.52,380.0|
            Weather.WeatherLocation weatherLocation = new Weather.WeatherLocation();
            weatherLocation.description = getStringValue(stationString,";");
            stationString               = cutSourceString(stationString,";");
            weatherLocation.name        = getStringValue(stationString,";");
            stationString               = cutSourceString(stationString,";");
            weatherLocation.latitude    = Double.parseDouble(getStringValue(stationString,";"));
            stationString               = cutSourceString(stationString,";");
            weatherLocation.longitude   = Double.parseDouble(getStringValue(stationString,";"));
            stationString               = cutSourceString(stationString,";");
            weatherLocation.altitude    = Double.parseDouble(getStringValue(stationString,"|"));
            stationString               = cutSourceString(stationString,"|");
        }
        Collections.sort(stations, new Weather.WeatherLocation());
        return stations;
    }

    public ArrayList<String> getStationNames(Context context){
        ArrayList<Weather.WeatherLocation> weatherLocations = getStations();
        ArrayList<String> names = new ArrayList<String>();
        for (int i=0; i<weatherLocations.size(); i++){
            Weather.WeatherLocation weatherLocation = weatherLocations.get(i);
            names.add(weatherLocation.description);
        }
        return names;
    }
}

