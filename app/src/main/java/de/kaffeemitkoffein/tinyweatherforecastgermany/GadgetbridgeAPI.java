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
import android.content.Intent;
import android.os.Parcelable;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;

import java.util.ArrayList;
import java.util.Calendar;

public class GadgetbridgeAPI {

    public final static String WEATHER_EXTRA="WeatherSpec";
    public final static String WEATHER_ACTION="de.kaffeemitkoffein.broadcast.WEATHERDATA";

    private GadgetbridgeAPI(){
    }

    private static void sendWeatherData(Context context, CurrentWeatherInfo weatherCard){
        WeatherSpec weatherSpec;
        if (weatherCard==null){
            weatherCard = Weather.getCurrentWeatherInfo(context);
        }
        if (weatherCard!=null){
            // build the WeatherSpec instance with current weather
            weatherSpec = new WeatherSpec();
            weatherSpec.location             = weatherCard.getCity(context);
            // fake timestamp for current weather if desired by user; some wearables do not accept a forecast
            // for current weather.
            if (WeatherSettings.fakeTimestampForGadgetBridge(context)){
                weatherSpec.timestamp            = (int) (Calendar.getInstance().getTimeInMillis()/1000);
            } else {
                weatherSpec.timestamp            = (int) (weatherCard.currentWeather.getTimestamp() / 1000);
            }
            if (weatherCard.currentWeather.hasCondition()){
                weatherSpec.currentConditionCode = WeatherCodeContract.translateToOpenWeatherCode(weatherCard.currentWeather.getCondition());
                weatherSpec.currentCondition     = WeatherCodeContract.getWeatherConditionText(context,weatherCard.currentWeather.getCondition());
            }
            if (weatherCard.currentWeather.hasTemperature()){
                weatherSpec.currentTemp          = weatherCard.currentWeather.getTemperatureInt();
            }
            if (weatherCard.currentWeather.hasRH()){
                weatherSpec.currentHumidity      = weatherCard.currentWeather.getRHInt();
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
            if (weatherCard.currentWeather.hasProbPrecipitation()){
                weatherSpec.precipProbability    = weatherCard.currentWeather.getProbPrecipitation();
            }
            if (weatherCard.currentWeather.hasUvHazardIndex()){
                weatherSpec.uvIndex = weatherCard.currentWeather.getUvHazardIndex();
            }
            if (weatherCard.currentWeather.hasTd()){
                weatherSpec.dewPoint = (int) Math.round(weatherCard.currentWeather.getTd());
            }
            if (weatherCard.currentWeather.hasPressure()){
                weatherSpec.pressure = weatherCard.currentWeather.getPressure()/100f; // convert to hPa/mbar
            }
            if (weatherCard.currentWeather.hasClouds()){
                weatherSpec.cloudCover = weatherCard.currentWeather.getClouds();
            }
            if (weatherCard.currentWeather.hasVisibility()){
                weatherSpec.visibility = weatherCard.currentWeather.getVisibilityInMetres();
            }
            weatherSpec.sunRise  = (int) (Weather.getSunriseInUTC(weatherCard.weatherLocation,weatherCard.currentWeather)/1000);
            weatherSpec.sunSet   = (int) (Weather.getSunsetInUTC(weatherCard.weatherLocation,weatherCard.currentWeather)/1000);
            weatherSpec.moonRise = (int) (Weather.getMoonRiseInUTC(weatherCard.weatherLocation,weatherCard.currentWeather)/1000);
            weatherSpec.moonSet  = (int) (Weather.getMoonSetInUTC(weatherCard.weatherLocation,weatherCard.currentWeather)/1000);
            weatherSpec.moonPhase = Weather.getMoonPhaseInDegrees(weatherCard.currentWeather.getTimestamp());
            weatherSpec.latitude = (float) weatherCard.weatherLocation.latitude;
            weatherSpec.longitude = (float) weatherCard.weatherLocation.longitude;
            weatherSpec.isCurrentLocation = -1;

            // build the forecast instance, ingore 1st entry (current day)
            weatherSpec.hourly = new ArrayList<WeatherSpec.Hourly>();
            for (int i=1; i<weatherCard.forecast1hourly.size(); i++){
                WeatherSpec.Hourly hourly = new WeatherSpec.Hourly();
                Weather.WeatherInfo weatherInfo = weatherCard.forecast1hourly.get(i);
                hourly.timestamp = (int) (weatherInfo.getTimestamp()/1000);
                if (weatherInfo.hasTemperature()){
                    hourly.temp = weatherInfo.getTemperatureInt();
                }
                if (weatherInfo.hasCondition()){
                    hourly.conditionCode = WeatherCodeContract.translateToOpenWeatherCode(weatherInfo.getCondition());
                }
                if (weatherInfo.hasRH()){
                    hourly.humidity = weatherInfo.getRHInt();
                }
                if (weatherInfo.hasWindSpeed()){
                    hourly.windSpeed = weatherInfo.getWindSpeedInKmhInt();
                }
                if (weatherInfo.hasWindDirection()){
                    hourly.windDirection = weatherInfo.getWindDirectionInt();
                }
                if (weatherInfo.hasUvHazardIndex()){
                    hourly.uvIndex = weatherInfo.getUvHazardIndex();
                }
                if (weatherInfo.hasProbPrecipitation()){
                    hourly.precipProbability = weatherInfo.getProbPrecipitation();
                }
                weatherSpec.hourly.add(hourly);
            }
            // build the forecast instance, ingore 1st entry (current day)
            weatherSpec.forecasts = new ArrayList<WeatherSpec.Daily>();
            for (int i=1; i<weatherCard.forecast24hourly.size(); i++){
                WeatherSpec.Daily daily = new WeatherSpec.Daily();
                Weather.WeatherInfo weatherInfo = weatherCard.forecast24hourly.get(i);
                if (weatherInfo.hasMinTemperature()){
                    daily.minTemp = weatherInfo.getMinTemperatureInt();
                }
                if (weatherInfo.hasMaxTemperature()){
                    daily.maxTemp = weatherInfo.getMaxTemperatureInt();
                }
                if (weatherInfo.hasCondition()){
                    daily.conditionCode = WeatherCodeContract.translateToOpenWeatherCode(weatherInfo.getCondition());
                }
                if (weatherInfo.hasRH()){
                    daily.humidity = weatherInfo.getRHInt();
                }
                if (weatherInfo.hasWindSpeed()){
                    daily.windSpeed = weatherInfo.getWindSpeedInKmhInt();
                }
                if (weatherInfo.hasWindDirection()){
                    daily.windDirection = weatherInfo.getWindDirectionInt();
                }
                if (weatherInfo.hasUvHazardIndex()){
                    daily.uvIndex = weatherInfo.getUvHazardIndex();
                }
                if (weatherInfo.hasProbPrecipitation()){
                    daily.precipProbability = weatherInfo.getProbPrecipitation();
                }
                daily.sunRise = (int) (Weather.getSunriseInUTC(weatherCard.weatherLocation,weatherInfo)/1000);
                daily.sunSet = (int) (Weather.getSunsetInUTC(weatherCard.weatherLocation,weatherInfo)/1000);
                daily.moonRise = (int) (Weather.getMoonRiseInUTC(weatherCard.weatherLocation,weatherInfo)/1000);
                daily.moonSet = (int) (Weather.getMoonSetInUTC(weatherCard.weatherLocation,weatherInfo)/1000);
                daily.moonPhase = Weather.getMoonPhaseInDegrees(weatherCard.forecast24hourly.get(i).getTimestamp());
                weatherSpec.forecasts.add(daily);
            }
            String timestampHumanReadable = "";
            try {
                timestampHumanReadable = Weather.getSimpleDateFormat(Weather.SimpleDateFormats.TIME_SEC).format(weatherSpec.timestamp);
            } catch (IllegalArgumentException e){
                // do nothing
            }
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Values served to Gadgetbridge:");
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Current weather:");
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Timestamp          : "+weatherSpec.timestamp+" ("+timestampHumanReadable+")");
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Condition Code     : "+weatherSpec.currentConditionCode);
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Condition          : "+weatherSpec.currentCondition);
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Temperature current: "+weatherSpec.currentTemp);
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Temperature min    : "+weatherSpec.todayMinTemp);
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Temperature max    : "+weatherSpec.todayMaxTemp);
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Windspeed          : "+weatherSpec.windSpeed);
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Windspeed direct.  : "+weatherSpec.windDirection);
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"% of precipitation : "+weatherSpec.precipProbability);
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"UV hazard index    : "+weatherSpec.uvIndex);
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Pressure           : "+weatherSpec.pressure);
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Visibility         : "+weatherSpec.visibility);
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Forecasts:");
            for (int i=0; i<weatherSpec.forecasts.size(); i++){
                PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Forecast #"+i+": Tmin: "+(weatherSpec.forecasts.get(i).minTemp)+" Tmax: "+(weatherSpec.forecasts.get(i).maxTemp)+" Cond.: "+weatherSpec.forecasts.get(i).conditionCode+" RH: "+weatherSpec.forecasts.get(i).humidity);
            }
            sendWeatherBroadcast(context,weatherSpec);
            WeatherSettings.setGadgetBridgeLastUpdateTime(context,Calendar.getInstance().getTimeInMillis());
        }
    }

    private static void sendWeatherBroadcast(Context context, WeatherSpec weatherSpec){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        if (weatherSpec!=null){
            Intent intent = new Intent();
            intent.putExtra(WEATHER_EXTRA, (Parcelable) weatherSpec);
            // going by the docs, this requires at least api level 14
            // read the package name from the settings. Users may change the package name to
            // be able to use forks.
            intent.setPackage(weatherSettings.gadgetbridge_packagename);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.setAction(WEATHER_ACTION);
            context.sendBroadcast(intent);
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Sent weather broadcast to GadgetBridge:");
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"+-> package name: "+weatherSettings.gadgetbridge_packagename);
        } else {
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.ERR,"GadgetBridge could not be served because there is no weather data.");
        }
    }

    public static void sendWeatherBroadcastIfEnabled(Context context,CurrentWeatherInfo currentWeatherInfo){
        if (WeatherSettings.serveGadgetBridge(context)){
            long timeSinceLastUpdateInMinutes = (Calendar.getInstance().getTimeInMillis() - WeatherSettings.getGadgetBridgeLastUpdateTime(context))/1000/60;
            PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"Time since last Gadgetbridge update: "+timeSinceLastUpdateInMinutes+ " min.");
            // do not send data more often than 30 minutes.
            if (timeSinceLastUpdateInMinutes>=30){
                PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"+-> updating.");
                sendWeatherData(context,currentWeatherInfo);
            } else {
                PrivateLog.log(context,PrivateLog.GB,PrivateLog.INFO,"+-> not sending data, since 30 min did not pass since last update.");
            }
        }
    }

}


