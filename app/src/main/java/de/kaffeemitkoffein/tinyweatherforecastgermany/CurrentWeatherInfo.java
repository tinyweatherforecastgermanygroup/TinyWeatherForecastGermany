package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.util.Log;

import java.util.ArrayList;

public class CurrentWeatherInfo{

    public static final String EMPTY_TAG = "-";
    String city;
    long issue_timestamp;
    long polling_time;
    Weather.WeatherInfo currentWeather;
    ArrayList<Weather.WeatherInfo> forecast6hourly;
    ArrayList<Weather.WeatherInfo> forecast24hourly;

    public CurrentWeatherInfo(){

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
                    Log.v("CWI","Parsing failed!");
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
        city = rawWeatherInfo.description;
        Log.v("CWI","City: "+rawWeatherInfo.description);
        polling_time = rawWeatherInfo.polling_time;
        currentWeather = new Weather.WeatherInfo();
        // get timesteps_long in long
        long[] timesteps = rawWeatherInfo.getTimeSteps();
        // get current weather data
        int current_weather_position = rawWeatherInfo.getCurrentForecastPosition();
        int next_midnight_position   = rawWeatherInfo.getNextMidnightAfterCurrentForecastPosition();
        currentWeather.setTimestamp(timesteps[current_weather_position]);
        Log.v("CWI","-----------------------------------------------------");
        Log.v("CWI","current weather position: "+current_weather_position);
        Log.v("CWI","next midnight   position: "+next_midnight_position);
        Log.v("CWI","Elements in raw data    : "+rawWeatherInfo.elements);
        //Log.v("CWI","ww data    : "+rawWeatherInfo.ww[current_weather_position]);
        currentWeather.setConditionCode(getIntItem(rawWeatherInfo.ww[current_weather_position]));
        //Log.v("CWI","Condition code current  : "+currentWeather.getCondition());
        currentWeather.setClouds(getIntItem(rawWeatherInfo.N[current_weather_position]));
        //Log.v("CWI","Condition clouds        : "+currentWeather.getClouds());
        currentWeather.setTemperature(getDoubleItem(rawWeatherInfo.TTT[current_weather_position]));
        //Log.v("CWI","Condition temp          : "+currentWeather.getTemperatureInt());
        currentWeather.setLowTemperature(rawWeatherInfo.getMinTemperature(current_weather_position,next_midnight_position));
        currentWeather.setHighTemperature(rawWeatherInfo.getMaxTemperature(current_weather_position,next_midnight_position));
        currentWeather.setWindSpeed(getDoubleItem(rawWeatherInfo.FF[current_weather_position]));
        currentWeather.setWindDirection(getDoubleItem(rawWeatherInfo.DD[current_weather_position]));
        currentWeather.setFlurries(getDoubleItem(rawWeatherInfo.FX1[current_weather_position]));
        currentWeather.setPrecipitation(getDoubleItem(rawWeatherInfo.RR1c[current_weather_position]));
        currentWeather.setProbPrecipitation(getIntItem(rawWeatherInfo.wwP[current_weather_position]));
        currentWeather.setProbThunderstorms(getIntItem(rawWeatherInfo.wwT[current_weather_position]));
        currentWeather.setProbFog(getIntItem(rawWeatherInfo.wwM[current_weather_position]));
        currentWeather.setProbPrecipitation(getIntItem(rawWeatherInfo.wwS[current_weather_position]));
        currentWeather.setProbFreezingRain(getIntItem(rawWeatherInfo.wwF[current_weather_position]));
        currentWeather.setVisibility(getIntItem(rawWeatherInfo.VV[current_weather_position]));
        currentWeather.setUV(getDoubleItem(rawWeatherInfo.RRad1[current_weather_position]));

        // fill 6h forecast arraylist
        forecast6hourly = new ArrayList<Weather.WeatherInfo>();
        int index = rawWeatherInfo.getNext6hPosition();
        Log.v("CWI","First 6-h- Index : "+index);
        for (int j=0;j<24;j++){
            Log.v("CWI",j+":"+rawWeatherInfo.timesteps[j]+":"+rawWeatherInfo.WPc61[j]+":"+rawWeatherInfo.RR6c[j]);
        }
        while (index<rawWeatherInfo.elements){
            Weather.WeatherInfo wi = new Weather.WeatherInfo();
            wi.setTimestamp(timesteps[index]);
            try {
                Log.v("CWI","-----------------------------------------------------");
                Log.v("CWI","6h-Forecast index : "+index);
                //Log.v("CWI","Raw cond code : "+rawWeatherInfo.WPc61[index]);
                wi.setConditionCode(getIntItem(rawWeatherInfo.WPc61[index]));
                //Log.v("CWI","Condition code 6h-forecast ("+index+") : "+wi.getCondition());
                wi.setClouds(rawWeatherInfo.getAverageClouds(index - 5, index));
                //Log.v("CWI","Clouds 6h-forecast ("+index+") : "+wi.getClouds());
                wi.setTemperature(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.TTT,index - 5, index));
                //Log.v("CWI","Temp 6h-forecast ("+index+") : "+wi.getMaxTemperatureInCelsius());
                wi.setLowTemperature(rawWeatherInfo.getMinTemperature(index - 5, index));
                //Log.v("CWI","Temp low 6h-forecast ("+index+") : "+wi.getMinTemperatureInCelsius());
                wi.setHighTemperature(rawWeatherInfo.getMaxTemperature(index - 5, index));
                //Log.v("CWI","Temp high 6h-forecast ("+index+") : "+wi.getMaxTemperatureInCelsius());
                wi.setWindSpeed(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.FF, index - 5, index));
                //Log.v("CWI","Wind speed high 6h-forecast ("+index+") : "+wi.getWindSpeed());
                wi.setWindDirection(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.DD, index - 5, index));
                //Log.v("CWI","Wind dir high 6h-forecast ("+index+") : "+wi.getWindDirectionInt());
                wi.setFlurries(rawWeatherInfo.getMaxDoubleValue(rawWeatherInfo.FX1, index - 5, index));
                //Log.v("CWI","Flurries high 6h-forecast ("+index+") : "+wi.getFlurriesInKmhInt());
                wi.setPrecipitation(getDoubleItem(rawWeatherInfo.RR6c[index]));
                // Log.v("CWI","Precipitation 6h-forecast ("+index+") : "+wi.getPrecipitation());
                wi.setProbPrecipitation(getIntItem(rawWeatherInfo.wwP6[index]));
                //Log.v("CWI","Prob. prec. 6h-forecast ("+index+") : "+wi.getProbPrecipitation());
                wi.setProbThunderstorms(getIntItem(rawWeatherInfo.wwT6[index]));
                //Log.v("CWI","Prob. T 6h-forecast ("+index+") : "+wi.getThunderStormsProb());
                wi.setProbFog(getIntItem(rawWeatherInfo.wwM6[index]));
                //Log.v("CWI","Prob. fog 6h-forecast ("+index+") : "+wi.getFogProb());
                wi.setProbSolidPrecipitation(getIntItem(rawWeatherInfo.wwS6[index]));
                //Log.v("CWI","Prob. solid per 6h-forecast ("+index+") : "+wi.getSolidPrecipitationProb());
                wi.setProbFreezingRain(getIntItem(rawWeatherInfo.wwF6[index]));
                //Log.v("CWI","Prob. FR per 6h-forecast ("+index+") : "+wi.getFreezingRainProb());
                wi.setVisibility(rawWeatherInfo.getAverageValueInt(rawWeatherInfo.VV, index - 5, index));
                //Log.v("CWI","Visibility per 6h-forecast ("+index+") : "+wi.getVisibility());
                wi.setUV(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.RRad1, index - 5, index));
                //Log.v("CWI","Radiation per 6h-forecast ("+index+") : "+wi.getUV());
            } catch (NumberFormatException e){
                // nothing to do, not available already null
            }
            forecast6hourly.add(wi);
            index = index + 6;
        }
        // fill 24h forecast arraylist
        forecast24hourly = new ArrayList<Weather.WeatherInfo>();
        index = rawWeatherInfo.getNext24hPosition();
        Log.v("CWI","First 23h Index : "+index);
        while (index<rawWeatherInfo.elements){
            Weather.WeatherInfo wi = new Weather.WeatherInfo();
            wi.setTimestamp(timesteps[index]);
            try {
                wi.setConditionCode(getIntItem(rawWeatherInfo.WPcd1[index]));
                wi.setClouds(rawWeatherInfo.getAverageClouds(index-23,index));
                wi.setTemperature(rawWeatherInfo.getAverageTemperature(index-23,index));
                wi.setLowTemperature(rawWeatherInfo.getMinTemperature(index-23,index));
                wi.setHighTemperature(rawWeatherInfo.getMaxTemperature(index-23,index));
                wi.setWindSpeed(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.FF,index-23,index));
                wi.setWindDirection(getDoubleItem(rawWeatherInfo.DD[index]));
                wi.setFlurries(rawWeatherInfo.getMaxDoubleValue(rawWeatherInfo.FX1,index-23,index));
                wi.setPrecipitation(getDoubleItem(rawWeatherInfo.RRdc[index]));
                wi.setProbPrecipitation(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwP,index-23,index));
                wi.setProbThunderstorms(Integer.parseInt(rawWeatherInfo.wwTd[index]));
                wi.setProbFog(Integer.parseInt(rawWeatherInfo.wwMd[index]));
                wi.setProbSolidPrecipitation(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwS,index-23,index));
                wi.setProbFreezingRain(rawWeatherInfo.getMaxIntValue(rawWeatherInfo.wwF,index-23,index));
                wi.setVisibility(rawWeatherInfo.getAverageValueInt(rawWeatherInfo.VV,index-23,index));
                wi.setUV(rawWeatherInfo.getAverageValueDouble(rawWeatherInfo.RRad1,index-23,index));
            } catch (NumberFormatException e){
                // nothing to do, not available already null
            }
            forecast24hourly.add(wi);
            index = index + 24;
        }
    }

    public String getCity(){
        return city;
    }

}
