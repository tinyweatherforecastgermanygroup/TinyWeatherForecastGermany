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
import android.content.Intent;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;

public class GadgetbridgeAPI {

    public final static String WEATHER_EXTRA="WeatherSpec";
    public final static String WEATHER_ACTION="de.kaffeemitkoffein.broadcast.WEATHERDATA";

    private WeatherSpec weatherSpec;
    private Context context;

    public GadgetbridgeAPI(Context context){
        this.context = context;
    }

    private void setWeatherData(){
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(context);
        // build the WeatherSpec instance with current weather
        weatherSpec = new WeatherSpec();
        weatherSpec.location             = weatherCard.getCity();
        weatherSpec.timestamp            = (int) (weatherCard.polling_time / 1000);
        if (weatherCard.currentWeather.hasCondition()){
            int currentWeatherCondition = new WeatherCodeContract().getLineageOSWeatherCode(weatherCard.currentWeather.getCondition());
            weatherSpec.currentConditionCode = new WeatherCodeContract().getLineageOSWeatherCode(weatherCard.currentWeather.getCondition());
            weatherSpec.currentCondition     = new WeatherCodeContract().getWeatherConditionText(context,currentWeatherCondition);
        }
        if (weatherCard.currentWeather.hasTemperature()){
            weatherSpec.currentTemp          = weatherCard.currentWeather.getTemperatureInt();
        }
        if (weatherCard.currentWeather.hasMinTemperature()){
            weatherSpec.todayMinTemp         = weatherCard.currentWeather.getMinTemperatureInt();
        }
        if (weatherCard.currentWeather.hasMaxTemperature()){
            weatherSpec.todayMaxTemp         = weatherCard.currentWeather.getMaxTemperatureInt();
        }
        if (weatherCard.currentWeather.hasWindSpeed()){
            weatherSpec.windSpeed            = (float) weatherCard.currentWeather.getWindSpeedInKmhInt();
        }
        if (weatherCard.currentWeather.hasWindDirection()){
            weatherSpec.windDirection        = (int) weatherCard.currentWeather.getWindDirection();
        }
        // build the forecast instance
        for (int i=0; i<weatherCard.forecast24hourly.size(); i++){
            // do not add and/or stop adding forecast if values are unknown
            if ((!weatherCard.forecast24hourly.get(i).hasMinTemperature()||
                    (!weatherCard.forecast24hourly.get(i).hasMaxTemperature())||
                    (!weatherCard.forecast24hourly.get(i).hasCondition()))){
                break;
            }
            // construct forecast and add it; @DWD, humidity is always unknown because not served.
            WeatherSpec.Forecast forecast = new WeatherSpec.Forecast(
                    weatherCard.forecast24hourly.get(i).getMinTemperatureInt(),
                    weatherCard.forecast24hourly.get(i).getMaxTemperatureInt(),
                    new WeatherCodeContract().getLineageOSWeatherCode(weatherCard.forecast24hourly.get(i).getCondition()),
                    0);
            weatherSpec.forecasts.add(forecast);
        }
        /*
        Log.v("GADGETBRIDGE-API","Timestamp          : "+weatherSpec.timestamp);
        Log.v("GADGETBRIDGE-API","Condition          : "+weatherSpec.currentCondition);
        Log.v("GADGETBRIDGE-API","Temperature current: "+weatherSpec.currentTemp);
        Log.v("GADGETBRIDGE-API","Temperature min    : "+weatherSpec.todayMinTemp);
        Log.v("GADGETBRIDGE-API","Temperature max    : "+weatherSpec.todayMaxTemp);
        Log.v("GADGETBRIDGE-API","FC-Temperature max : "+weatherSpec.forecasts.get(0).minTemp);
        Log.v("GADGETBRIDGE-API","FC-Temperature max : "+weatherSpec.forecasts.get(0).maxTemp);
        Log.v("GADGETBRIDGE-API","FC-Condition       : "+weatherSpec.forecasts.get(0).conditionCode);
        Log.v("GADGETBRIDGE-API","Windspeed          : "+weatherSpec.windSpeed);
        Log.v("GADGETBRIDGE-API","Windspeed direct.  : "+weatherSpec.windDirection);
        */
    }

    private final void sendWeatherBroadcast(){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        setWeatherData();
        Intent intent = new Intent();
        intent.putExtra(WEATHER_EXTRA,weatherSpec);
        // going by the docs, this requires at least api level 14
        // read the package name from the settings. Users may change the package name to
        // be able to use forks.
        intent.setPackage(weatherSettings.gadgetbridge_packagename);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(WEATHER_ACTION);
        context.sendBroadcast(intent);
        PrivateLog.log(context,Tag.GB,"Sent weather broadcast to GadgetBridge:");
        PrivateLog.log(context,Tag.GB,"+-> package name: "+weatherSettings.gadgetbridge_packagename);
    }

    public final void sendWeatherBroadcastIfEnabled(){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        if (weatherSettings.serve_gadgetbridge){
            sendWeatherBroadcast();
        }
    }

}


