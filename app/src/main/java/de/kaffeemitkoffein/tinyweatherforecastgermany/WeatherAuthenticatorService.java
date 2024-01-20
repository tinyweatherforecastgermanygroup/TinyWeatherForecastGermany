package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WeatherAuthenticatorService extends Service {

    private WeatherAuthenticator weatherAuthenticator;

    @Override
    public void onCreate() {
        weatherAuthenticator = new WeatherAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return weatherAuthenticator.getIBinder();
    }
}
