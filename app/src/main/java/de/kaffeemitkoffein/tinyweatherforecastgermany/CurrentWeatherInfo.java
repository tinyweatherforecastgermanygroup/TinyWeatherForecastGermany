/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021 Pawel Dube
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
import java.util.ArrayList;

public class CurrentWeatherInfo{

    public static final String EMPTY_TAG = "-";
    Weather.WeatherLocation weatherLocation;
    long polling_time;
    Weather.WeatherInfo currentWeather;
    ArrayList<Weather.WeatherInfo> forecast1hourly;
    ArrayList<Weather.WeatherInfo> forecast6hourly;
    ArrayList<Weather.WeatherInfo> forecast24hourly;

    public CurrentWeatherInfo(){
        polling_time = 0;
        weatherLocation = new Weather.WeatherLocation();
        currentWeather = new Weather.WeatherInfo();
        forecast1hourly = new ArrayList<Weather.WeatherInfo>();
        forecast6hourly = new ArrayList<Weather.WeatherInfo>();
        forecast24hourly = new ArrayList<Weather.WeatherInfo>();
    }

    public void setToEmpty(){
        weatherLocation = new Weather.WeatherLocation("","",0,0,0);
        currentWeather = new Weather.WeatherInfo();
        forecast1hourly = new ArrayList<Weather.WeatherInfo>();
        forecast6hourly = new ArrayList<Weather.WeatherInfo>();
        forecast24hourly = new ArrayList<Weather.WeatherInfo>();
    }

    private Integer getIntItem(String s){
        Integer i;
        if (s != null){
            if (!s.equals(EMPTY_TAG)){
                try {
                    int j = (int) Double.parseDouble(s);
                    i = new Integer(j);
                    return i;
                } catch (NumberFormatException e){
                    // nothing to do
                }
            }
        }
        return null;
    }

    private Long getLongItem(String s){
        Long l;
        if (s != null){
            if (!s.equals(EMPTY_TAG)){
                try {
                    l = Long.parseLong(s);
                    return l;
                } catch (NumberFormatException e){
                    // nothing to do
                }
            }
        }
        return null;
    }

    private Double getDoubleItem(String s){
        Double d;
        if (s != null){
            if (!s.equals(EMPTY_TAG)){
                try {
                    d = Double.parseDouble(s);
                    return d;
                } catch (NumberFormatException e){
                    // nothing to do
                }
            }
        }
        return null;
    }

    public CurrentWeatherInfo(RawWeatherInfo rawWeatherInfo){
        if (rawWeatherInfo==null){
            return;
        }
        if (rawWeatherInfo.elements==0){
            return;
        }
        weatherLocation = rawWeatherInfo.weatherLocation;
        polling_time = rawWeatherInfo.polling_time;
        currentWeather = new Weather.WeatherInfo();
        currentWeather.setForecastType(Weather.WeatherInfo.ForecastType.CURRENT);
        // get timesteps_long in long
        long[] timesteps = rawWeatherInfo.getTimeSteps();
        // get current weather data
        int current_weather_position = rawWeatherInfo.getCurrentForecastPosition();
        int next_midnight_position   = rawWeatherInfo.getNextMidnightAfterCurrentForecastPosition();
        currentWeather.setTimestamp(timesteps[current_weather_position]);
        // try significant weather, highest priority first
        currentWeather.setConditionCode(getIntItem((rawWeatherInfo.WPc11[current_weather_position])));
        if (!currentWeather.hasCondition()){
            currentWeather.setConditionCode(getIntItem(rawWeatherInfo.ww[current_weather_position]));
        }
        currentWeather.setClouds(getIntItem(rawWeatherInfo.N[current_weather_position]));
        currentWeather.setTemperature(getDoubleItem(rawWeatherInfo.TTT[current_weather_position]));
        currentWeather.setTemperature5cm(getDoubleItem(rawWeatherInfo.T5cm[current_weather_position]));
        currentWeather.setLowTemperature(rawWeatherInfo.getMinTemperature(current_weather_position,next_midnight_position));
        currentWeather.setHighTemperature(rawWeatherInfo.getMaxTemperature(current_weather_position,next_midnight_position));
        currentWeather.setWindSpeed(getDoubleItem(rawWeatherInfo.FF[current_weather_position]));
        currentWeather.setWindDirection(getDoubleItem(rawWeatherInfo.DD[current_weather_position]));
        currentWeather.setFlurries(getDoubleItem(rawWeatherInfo.FX1[current_weather_position]));
        currentWeather.setPrecipitation(getDoubleItem(rawWeatherInfo.RR1c[current_weather_position]));
        currentWeather.setProbPrecipitation(getIntItem(rawWeatherInfo.wwP[current_weather_position]));
        currentWeather.setProbDrizzle(getIntItem(rawWeatherInfo.wwZ[current_weather_position]));
        currentWeather.setProbThunderstorms(getIntItem(rawWeatherInfo.wwT[current_weather_position]));
        currentWeather.setProbFog(getIntItem(rawWeatherInfo.wwM[current_weather_position]));
        currentWeather.setProbSolidPrecipitation(getIntItem(rawWeatherInfo.wwS[current_weather_position]));
        currentWeather.setProbFreezingRain(getIntItem(rawWeatherInfo.wwF[current_weather_position]));
        currentWeather.setVisibility(getIntItem(rawWeatherInfo.VV[current_weather_position]));
        currentWeather.setProbVisibilityBelow1km(getIntItem(rawWeatherInfo.VV10[current_weather_position]));
        currentWeather.setPressure(getDoubleItem(rawWeatherInfo.PPPP[current_weather_position]));
        currentWeather.setUV(getDoubleItem(rawWeatherInfo.RRad1[current_weather_position]));
        if (!currentWeather.hasCondition()){
            currentWeather.calculateMissingCondition();
        }
        // fill 1h forecast arraylist
        forecast1hourly = new ArrayList<Weather.WeatherInfo>();
        int index = rawWeatherInfo.getCurrentForecastPosition();
        while (index<rawWeatherInfo.elements){
            Weather.WeatherInfo wi = new Weather.WeatherInfo();
            wi.setForecastType(Weather.WeatherInfo.ForecastType.ONE_HOUR);
            wi.setTimestamp(timesteps[index]);
            // try significant weather, highest priority first
            wi.setConditionCode(getIntItem((rawWeatherInfo.WPc11[index])));
            if (!wi.hasCondition()){
                wi.setConditionCode(getIntItem(rawWeatherInfo.ww[index]));
            }
            wi.setClouds(getIntItem(rawWeatherInfo.N[index]));
            wi.setTemperature(getDoubleItem(rawWeatherInfo.TTT[index]));
            wi.setTemperature5cm(getDoubleItem(rawWeatherInfo.TTT[index]));
            if ((getDoubleItem(rawWeatherInfo.TTT[index])!=null) && (getDoubleItem(rawWeatherInfo.E_TTT[index])!=null)){
                wi.setLowTemperature(getDoubleItem(rawWeatherInfo.TTT[index])-getDoubleItem(rawWeatherInfo.E_TTT[index]));
            }
            if ((getDoubleItem(rawWeatherInfo.TTT[index])!=null) && (getDoubleItem(rawWeatherInfo.E_TTT[index])!=null)){
                wi.setHighTemperature(getDoubleItem(rawWeatherInfo.TTT[index])+getDoubleItem(rawWeatherInfo.E_TTT[index]));
            }
            wi.setWindSpeed(getDoubleItem(rawWeatherInfo.FF[index]));
            wi.setWindDirection(getDoubleItem(rawWeatherInfo.DD[index]));
            wi.setFlurries(getDoubleItem(rawWeatherInfo.FX1[index]));
            wi.setPrecipitation(getDoubleItem(rawWeatherInfo.RR1c[index]));
            wi.setProbPrecipitation(getIntItem(rawWeatherInfo.wwP[index]));
            wi.setProbDrizzle(getIntItem(rawWeatherInfo.wwZ[index]));
            wi.setProbThunderstorms(getIntItem(rawWeatherInfo.wwT[index]));
            wi.setProbFog(getIntItem(rawWeatherInfo.wwM[index]));
            wi.setProbSolidPrecipitation(getIntItem(rawWeatherInfo.wwS[index]));
            wi.setProbFreezingRain(getIntItem(rawWeatherInfo.wwF[index]));
            wi.setVisibility(getIntItem(rawWeatherInfo.VV[index]));
            wi.setProbVisibilityBelow1km(getIntItem(rawWeatherInfo.VV10[index]));
            wi.setPressure(getDoubleItem(rawWeatherInfo.PPPP[index]));
            wi.setUV(getDoubleItem(rawWeatherInfo.RRad1[index]));
            if (!wi.hasCondition()){
                wi.calculateMissingCondition();
            }
            forecast1hourly.add(wi);
            index++;
        }
        // fill 6h forecast arraylist
        forecast6hourly = new ArrayList<Weather.WeatherInfo>();
        int startposition6h = rawWeatherInfo.getNext6hPosition();
        index = startposition6h;
        while (index<rawWeatherInfo.elements){
            int start = index - 5;
            if (index==startposition6h){
                start = current_weather_position;
            }
            Weather.WeatherInfo wi = new Weather.WeatherInfo();
            wi.setForecastType(Weather.WeatherInfo.ForecastType.HOURS_6);
            wi.setTimestamp(timesteps[index]);
            wi.setConditionCode(getIntItem(rawWeatherInfo.WPc61[index]));
            wi.setClouds(rawWeatherInfo.getAverageClouds(start, index));
            wi.setTemperature(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.TTT,start, index));
            wi.setTemperature5cm(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.T5cm,start, index));
            if (start==startposition6h){
                if ((getDoubleItem(rawWeatherInfo.TTT[index])!=null) && (getDoubleItem(rawWeatherInfo.E_TTT[index])!=null)){
                    wi.setLowTemperature(getDoubleItem(rawWeatherInfo.TTT[index])-getDoubleItem(rawWeatherInfo.E_TTT[index]));
                }
                if ((getDoubleItem(rawWeatherInfo.TTT[index])!=null) && (getDoubleItem(rawWeatherInfo.E_TTT[index])!=null)){
                    wi.setHighTemperature(getDoubleItem(rawWeatherInfo.TTT[index])+getDoubleItem(rawWeatherInfo.E_TTT[index]));
                }
            } else {
                wi.setLowTemperature(rawWeatherInfo.getMinTemperature(start, index));
                wi.setHighTemperature(rawWeatherInfo.getMaxTemperature(start, index));
            }
            wi.setWindSpeed(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.FF, start, index));
            wi.setWindDirection(getDoubleItem(rawWeatherInfo.DD[index]));
            wi.setFlurries(rawWeatherInfo.getMaxDoubleValue(rawWeatherInfo.FX1, start, index));
            wi.setPrecipitation(getDoubleItem(rawWeatherInfo.RR6c[index]));
            if (!wi.hasPrecipitation()){
               // try to self-calculate this
               wi.setPrecipitation(rawWeatherInfo.getMaxDoubleValue(rawWeatherInfo.RR1c, start,index));
            }
            wi.setProbPrecipitation(getIntItem(rawWeatherInfo.wwP6[index]));
            if (!wi.hasProbPrecipitation()){
                // try to self-calculate this
                wi.setProbPrecipitation(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwP, start,index));
            }
            wi.setProbDrizzle(getIntItem(rawWeatherInfo.wwZ6[current_weather_position]));
            if (!wi.hasProbDrizzle()){
                // try to self-calculate this
                wi.setProbDrizzle(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwZ, start,index));
            }
            wi.setProbThunderstorms(getIntItem(rawWeatherInfo.wwT6[index]));
            if (!wi.hasProbThunderstorms()){
                // try to self-calculate this
                wi.setProbThunderstorms(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwT, start,index));
            }
            wi.setProbFog(getIntItem(rawWeatherInfo.wwM6[index]));
            if (!wi.hasProbFog()){
                // try to self-calculate this
                wi.setProbFog(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwM, start,index));
            }
            wi.setProbSolidPrecipitation(getIntItem(rawWeatherInfo.wwS6[index]));
            if (!wi.hasProbSolidPrecipitation()){
                // try to self-calculate this
                wi.setProbSolidPrecipitation(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwS, start,index));
            }
            wi.setProbFreezingRain(getIntItem(rawWeatherInfo.wwF6[index]));
            if (!wi.hasProbFreezingRain()){
                // try to self-calculate this
                wi.setProbFreezingRain(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwS, start,index));
            }
            wi.setVisibility(rawWeatherInfo.getAverageValueInt(rawWeatherInfo.VV, start, index));
            wi.setProbVisibilityBelow1km(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.VV10,start, index));
            wi.setPressure(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.PPPP, start, index));
            wi.setUV(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.RRad1, start, index));
            if (!wi.hasCondition()){
                wi.calculateMissingCondition();
            }
            forecast6hourly.add(wi);
            index = index + 6;
        }
        // fill 24h forecast arraylist
        forecast24hourly = new ArrayList<Weather.WeatherInfo>();
        int startposition24h = rawWeatherInfo.getNext24hPosition();
        index = startposition24h;
        while (index<rawWeatherInfo.elements){
            int start = index - 23;
            if (index == startposition24h){
                start = current_weather_position;
            }
            Weather.WeatherInfo wi = new Weather.WeatherInfo();
            wi.setForecastType(Weather.WeatherInfo.ForecastType.HOURS_24);
            wi.setTimestamp(timesteps[index]);
            wi.setConditionCode(getIntItem(rawWeatherInfo.WPcd1[index]));
            wi.setClouds(rawWeatherInfo.getAverageClouds(start,index));
            wi.setTemperature(rawWeatherInfo.getAverageTemperature(start,index));
            wi.setTemperature5cm(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.T5cm,start,index));
            if (index==startposition24h){
                if ((getDoubleItem(rawWeatherInfo.TTT[index])!=null) && (getDoubleItem(rawWeatherInfo.E_TTT[index])!=null)){
                    wi.setLowTemperature(getDoubleItem(rawWeatherInfo.TTT[index])-getDoubleItem(rawWeatherInfo.E_TTT[index]));
                }
                if ((getDoubleItem(rawWeatherInfo.TTT[index])!=null) && (getDoubleItem(rawWeatherInfo.E_TTT[index])!=null)){
                    wi.setHighTemperature(getDoubleItem(rawWeatherInfo.TTT[index])+getDoubleItem(rawWeatherInfo.E_TTT[index]));
                }
            } else {
                wi.setLowTemperature(rawWeatherInfo.getMinTemperature(start,index));
                wi.setHighTemperature(rawWeatherInfo.getMaxTemperature(start,index));
            }
            wi.setWindSpeed(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.FF,start,index));
            wi.setWindDirection(getDoubleItem(rawWeatherInfo.DD[current_weather_position]));
            wi.setFlurries(rawWeatherInfo.getMaxDoubleValue(rawWeatherInfo.FX1,start,index));
            wi.setPrecipitation(getDoubleItem(rawWeatherInfo.RRdc[index]));
            if (!wi.hasPrecipitation()){
                // try to self-calculate this
                wi.setPrecipitation(rawWeatherInfo.getMaxDoubleValue(rawWeatherInfo.RR1c, start,index));
            }
            wi.setProbPrecipitation(getIntItem(rawWeatherInfo.wwPd[index]));
            if (!wi.hasProbPrecipitation()){
                // try to self-calculate this
                wi.setProbPrecipitation(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwP, start,index));
            }
            wi.setProbDrizzle(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwZ,start,index));
            wi.setProbThunderstorms(getIntItem(rawWeatherInfo.wwTd[index]));
            if (!wi.hasProbThunderstorms()){
                // try to self-calculate this
                wi.setProbThunderstorms(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwT, start,index));
            }
            wi.setProbFog(getIntItem(rawWeatherInfo.wwMd[index]));
            if (!wi.hasProbFog()){
                // try to self-calculate this
                wi.setProbFog(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwM, start,index));
            }
            wi.setProbSolidPrecipitation(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwS,start,index));
            wi.setProbFreezingRain(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwF,start,index));
            wi.setVisibility(rawWeatherInfo.getAverageValueInt(rawWeatherInfo.VV,start,index));
            wi.setProbVisibilityBelow1km(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.VV10,start, index));
            wi.setPressure(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.RRad1,start,index));
            wi.setUV(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.RRad1,start,index));
            if (!wi.hasCondition()){
                wi.calculateMissingCondition();
            }
            forecast24hourly.add(wi);
            index = index + 24;
        }
    }

    public String getCity(){
        return weatherLocation.description;
    }

    public ArrayList<Weather.WindData> getWindForecast(int number){
        int currentpos = 0;
        ArrayList<Weather.WindData> windData = new ArrayList<Weather.WindData>();
        while (currentpos<forecast1hourly.size() && number>0){
            windData.add(new Weather.WindData(forecast1hourly.get(currentpos)));
            number--;
            currentpos++;
        }
        return windData;
    }

}
