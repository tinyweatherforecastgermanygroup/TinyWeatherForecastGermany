package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class StationSearchEngine {

    private WeatherSettings weatherSettings;
    private StationsManager stationsManager;
    private final Context context;
    private Executor executor;

    public ArrayList<String> entries;

    private StationsManager.StationsReader stationsReader;
    private Areas.AreaNameReader areaNameReader;

    public boolean includeInternational = true;

    public StationSearchEngine(Context context, Executor executor, WeatherSettings weatherSettings, StationsManager stationsManager){
        this.context = context;
        this.executor = executor;
        this.weatherSettings = weatherSettings;
        this.stationsManager = stationsManager;
        initValues();
    }

    public StationSearchEngine(Context context, WeatherSettings weatherSettings){
        this.context = context;
        this.weatherSettings = weatherSettings;
        initValues();
    }

    public StationSearchEngine(Context context, StationsManager stationsManager){
        this.context = context;
        this.stationsManager = stationsManager;
        initValues();
    }

    public StationSearchEngine(Context context){
        this.context = context;
        initValues();
    }

    public void setIncludeInternational(boolean b){
        this.includeInternational = b;
    }

    private void initValues(){
        entries = new ArrayList<String>();
        if (executor==null){
            executor = Executors.newSingleThreadExecutor();
        }
        if (weatherSettings==null){
            weatherSettings = new WeatherSettings(context);
        }
        if (stationsManager==null){
            readStations();
        } else {
            if (stationsManager.stations == null){
                readStations();
            } else {
                if (stationsManager.stations.size()==0){
                    readStations();
                } else {
                    addStationsToEntries(stationsManager.stations);
                }
            }
        }
        areaNameReader = new Areas.AreaNameReader(context){
        @Override
        public void onFinished(ArrayList<String> areanames){
            if (areanames!=null){
                newEntries(areanames);
            } else {
            }
        }
        };
        executor.execute(areaNameReader);
    }

    public static String toUmlaut(final String s){
        // üöäß ÄÜÖ
        String result = s;
        result = result.replace("UE","Ü");
        result = result.replace("OE","Ö");
        result = result.replace("AE","Ä");
        result = result.replace("SS","ß");
        result = result.replace("Ue","Ü");
        result = result.replace("Oe","Ö");
        result = result.replace("Ae","Ä");
        result = result.replace("ue","ü");
        result = result.replace("oe","ö");
        result = result.replace("ae","ä");
        result = result.replace("ss","ß");
        return result;
    }

    public static String toInternationalUmlaut(final String s){
        String result = s;
        result = result.replace("Ü","Ue");
        result = result.replace("Ö","Oe");
        result = result.replace("Ä","Ae");
        result = result.replace("ß","Ss");
        result = result.replace("ü","ue");
        result = result.replace("ö","oe");
        result = result.replace("ä","ae");
        return result;
    }

    public static ArrayList<String> toInternationalUmlaut(final ArrayList<String> s){
        if (s==null){
            return null;
        }
        ArrayList<String> result = new ArrayList<String>();
        for (int i=0; i<s.size(); i++){
            String s1 = s.get(i);
            String s2 = toInternationalUmlaut(s1);
            if (!s1.equals(s2)){
                result.add(s2);
            }
        }
        return result;
    }

    private void readStations(){
        stationsReader = new StationsManager.StationsReader(context){
            @Override
            public void onLoadingListFinished(ArrayList<Weather.WeatherLocation> new_stations) {
                stationsManager = new StationsManager(context, new_stations);
                addStationsToEntries(new_stations);
            }
        };
        executor.execute(stationsReader);
    }

    private void addStationsToEntries(ArrayList<Weather.WeatherLocation> stations){
        ArrayList<String> newEntries = new ArrayList<String>();
        for (int i=0; i<stations.size(); i++){
            //entries.add(stations.get(i).description);
            newEntries.add(stations.get(i).description);
        }
        newEntries(newEntries);
    }

    public void newEntries(ArrayList<String> newEntries){
        entries.addAll(newEntries);
        if (includeInternational){
            entries.addAll(toInternationalUmlaut(newEntries));
        }
    }

    public Location getCentroidLocationFromArea(String areaname){
        Areas.Area area = Areas.getAreaByName(context,areaname);
        if (area==null){
            area = Areas.getAreaByName(context,toUmlaut(areaname));
        }
        if (area==null){
            return null;
        }
        if (area.polygons.size()==0) {
            return null;
        }else {
            float xSum = 0;
            float ySum = 0;
            for (int i=0; i<area.polygons.size(); i++) {
                xSum = xSum + area.polygons.get(i).getPolygonXCentroid();
                ySum = ySum + area.polygons.get(i).getPolygonYCentroid();
            }
            Location location = new Location("weather");
            location.setLatitude(ySum/area.polygons.size());
            location.setLongitude(xSum/area.polygons.size());
            Bundle bundle = new Bundle();
            bundle.putString(Weather.WeatherLocation.EXTRAS_NAME,area.name);
            bundle.putInt(Weather.WeatherLocation.EXTRAS_ITEMS_TO_SHOW,300);
            location.setExtras(bundle);
            return location;
        }
    }
}
