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
    }

    public Location getCentroidLocationFromArea(String areaname){
        Areas.Area area = Areas.getAreaByName(context,areaname);
        if (area==null){
            return null;
        } else {
            Location location = new Location("weather");
            location.setLatitude(area.polygon.getPolygonYCentroid());
            location.setLongitude(area.polygon.getPolygonXCentroid());
            Bundle bundle = new Bundle();
            bundle.putString(Weather.WeatherLocation.EXTRAS_NAME,area.name);
            bundle.putInt(Weather.WeatherLocation.EXTRAS_ITEMS_TO_SHOW,300);
            location.setExtras(bundle);
            return location;
        }
    }
}
