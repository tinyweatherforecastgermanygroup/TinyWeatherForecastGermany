package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WeatherSyncService extends Service {

    private static WeatherSyncAdapter staticWeatherSyncAdapter = null;
    private static final Object synchronizedLock = new Object();

    @Override
    public void onCreate() {
        synchronized (synchronizedLock){
            if (staticWeatherSyncAdapter==null){
                staticWeatherSyncAdapter = new WeatherSyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return staticWeatherSyncAdapter.getSyncAdapterBinder();
    }
}
