package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.*;

public final class Weather {

    public final static double KelvinConstant = 273.15;

    public static class WeatherLocation implements Comparator<WeatherLocation> {
        public String description;
        public String name;
        double latitude;
        double longitude;
        double altitude;

        public WeatherLocation(){
        }

        public WeatherLocation(String description, String name, long latitude, long longitude, long altitude){
            this.description = description;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
        }

        @Override
        public int compare(WeatherLocation s1, WeatherLocation s2) {
            return s1.description.compareTo(s2.description);
        }

    }

    public class WeatherItem{
        long polling_time;
        WeatherLocation location;
        String timetext;    // text; original timestamp
        long timestamp;     // millis; UTC stamp for forecast
        double TTT;         // K; temp. 2 m above surface
        double E_TTT;       // K; absolute error of TTT
        double T5cm;        // K; temp. 5 cm above surface
        double Td;          // K; dew point 2m above surface (Taupunkt)
        double E_Td;        // K; absolute error Td;
        double Tx;          // K; max. temp. during last 12h
        double Tn;          // K; min. temp. during last 12h
        double TM;          // K; mean temp. during last 12h
        double TG;          // K; min. surface temp. at 5 cm within last 12h
        double DD;          // km/h; wind direction 0-360°
        double E_DD;        // 0-360°; absolute error DD
        double FF;          // m/s; wind speed in km/h
        double E_FF;        // m/s; absolute error wind speed 10m above surface
        double FX1;         // m/s; max. wind gust within last hour (Windböen)
        double FX3;         // m/s; max. wind gust within last 3h (Windböen)
        double FXh;         // m/s; max. wind gust within last 12h (Windböen)
        int FXh25;          // %, probability of wind gusts >= 25 km/h within last 12h
        int FXh40;          // %, probability of wind gusts >= 40 km/h within last 12h
        int FXh55;          // %, probability of wind gusts >= 55 km/h within last 12h
        int FX625;          // %, probability of wind gusts >= 25 km/h within last 6h
        int FX640;          // %, probability of wind gusts >= 40 km/h within last 6h
        int FX655;          // %, probability of wind gusts >= 55 km/h within last 6h
        double RR1c;        // kg/m², total precipitation during last hour consistent with significant weather
        double RRL1c;       // kg/m², total liquid during the last hour consistent with significant weather
        double RR3;         // kg/m², total precipitation during last 3h
        double RR6;         // kg/m², total precipitation during last 6h
        double RR3c;        // kg/m², total precipitation during last 3h consistent with significant weather
        double RR6c;        // kg/m², total precipitation during last 6h consistent with significant weather
        double RRhc;        // kg/m², total precipitation during last 12h consistent with significant weather
        double RRdc;        // kg/m², total precipitation during last 24h consistent with significant weather
        double RRS1c;       // kg/m², snow-rain equivalent during last hour
        double RRS3c;       // kg/m², snow-rain equivalent during last 3h
        int R101;           // %, probability of precipitation > 0.1 mm during the last hour
        int R102;           // %, probability of precipitation > 0.2 mm during the last hour
        int R103;           // %, probability of precipitation > 0.3 mm during the last hour
        int R105;           // %, probability of precipitation > 0.5 mm during the last hour
        int R107;           // %, probability of precipitation > 0.7 mm during the last hour
        int R110;           // %, probability of precipitation > 1.0 mm during the last hour
        int R120;           // %, probability of precipitation > 2.0 mm during the last hour
        int R130;           // %, probability of precipitation > 3.0 mm during the last hour
        int R150;           // %, probability of precipitation > 5.0 mm during the last hour
        int RR1o1;          // %, probability of precipitation > 10.0 mm during the last hour
        int RR1w1;          // %, probability of precipitation > 15.0 mm during the last hour
        int RR1u1;          // %, probability of precipitation > 25.0 mm during the last hour
        int R600;           // %, probability of precipitation > 0.0 mm during last 6h
        int Rh00;           // %, probability of precipitation > 0.0 mm during last 12h
        int R602;           // %, probability of precipitation > 0.2 mm during last 6h
        int Rh02;           // %, probability of precipitation > 0.2 mm during last 12h
        int Rd02;           // %, probability of precipitation > 0.2 mm during last 24h
        int R610;           // %, probability of precipitation > 1.0 mm during last gh
        int Rh10;           // %, probability of precipitation > 1.0 mm during last 12h
        int R650;           // %, probability of precipitation > 5.0 mm during last 6h
        int Rh50;           // %, probability of precipitation > 5.0 mm during last 12h
        int Rd00;           // %, probability of precipitation > 0.0 mm during last 24h
        int Rd10;           // %, probability of precipitation > 1.0 mm during last 24h
        int Rd50;           // %, probability of precipitation > 5.0 mm during last 24h
        int wwPd;           // %; occurrance of any precipitation within the last 24h
        double DRR1;        // s, duration of precipitation within the last hour
        int wwZ;            // %, occurrence of drizzle within the last hour
        int wwZ6;           // %, occurrence of drizzle within the last 6h
        int wwZh;           // %, occurrence of drizzle within the last 12h
        int wwD;            // %, occurrence of stratiform precipitation within the last hour
        int wwD6;           // %, occurrence of stratiform precipitation within the last 6h
        int wwDh;           // %, occurrence of stratiform precipitation within the last 12h
        int wwC;            // %, occurrence of convective precipitation within the last hour
        int wwC6;           // %, occurrence of convective precipitation within the last 6h
        int wwCh;           // %, occurrence of convective precipitation within the last 12h
        int wwT;            // %, occurrence of thunderstorms within the last hour
        int wwT6;           // %, occurrence of thunderstorms within the last 6h
        int wwTh;           // %, occurrence of thunderstorms within the last 12h
        int wwTd;           // %, occurrence of thunderstorms within the last 24h
        int wwL;            // %, occurrence of liquid precipitation within the last hour
        int wwL6;           // %, occurrence of liquid precipitation within the last 6h
        int wwLh;           // %, occurrence of liquid precipitation within the last 12h
        int wwS;            // %, occurrence of solid precipitation within the last hour
        int wwS6;           // %, occurrence of solid precipitation within the last 6h
        int wwSh;           // %, occurrence of solid precipitation within the last 12h
        int wwF;            // %, occurrence of freezing rain within the last hour
        int wwF6;           // %, occurrence of freezing rain within the last 6h
        int wwFh;           // %, occurrence of freezing rain within the last 12h
        int wwP;            // %, occurrence of precipitation within the last hour
        int wwP6;           // %, occurrence of precipitation within the last 6h
        int wwPh;           // %, occurrence of precipitation within the last 12h
        int VV10;           // %, probability visibility below 1 km
        int ww;             // significant weather
        int ww3;            // significant weather at last 3h
        int W1W2;           // weather during last 6h
        int WPc31;          // optional significant weather (highest priority) during last 3h
        int WPc61;          // optional significant weather (highest priority) during last 6h
        int WPch1;          // optional significant weather (highest priority) during last 12h
        int WPcd1;          // optional significant weather (highest priority) during last 24h (?)
        int N;              // 0-100% total cloud cover
        int N05;            // % cloud cover below 500ft.
        int Nl;             // % low cloud cover (lower than 2 km)
        int Nm;             // % midlevel cloud cover (2-7 km)
        int Nh;             // % high cloud cover (>7 km)
        int Nlm;            // % cloud cover low and mid level clouds below  7 km
        double H_BsC;       // m; cloud base of convective clouds
        double PPPP;        // Pa, surface pressure reduced
        double E_PPP;       // Pa, absolute error of PPPP
        double RadS3;       // kJ/m²; short wave radiation balance during last 3h
        double RRad1;       // kJ/m²; global irradiance within the last hour
        double Rad1h;       // kJ/m²; global irradiance
        double RadL3;       // kJ/m²; long wave radiation balance during last 3h (UVA)
        double VV;          // m; visibility
        double D1;          // s; sunshine duration during last hour
        double SunD;        // s; sunshine duration during last day
        double SunD3;       // s; sunshine duration during last 3h
        int RSunD;          // %; relative sunshine duration last 24h
        int PSd00;          // %; probability relative sunshine duration >0% within 24h
        int PSd30;          // %; probability relative sunshine duration >30% within 24h
        int PSd60;          // %; probability relative sunshine duration >60% within 24h
        int wwM;            // %; probability of fog within last hour
        int wwM6;           // %; probability of fog within last 6h
        int wwMh;           // %; probability of fog within last 12h
        int wwMd;           // %; occurrence of fog within the last 24h
        double PEvap;       // kg/m²; potential evapotranspiration within the last 24h
    }

    public final static int DATA_SIZE = 250;

    public static class WeatherInfo{
        long timestamp;
        int condition_code;
        double temperature;
        double temperature_high;
        double temperature_low;
        double wind_speed;
        double wind_direction;
        double flurries;
        double precipitation;
        int clouds;
        int prob_thunderstorms;
        int prob_precipitation;
        int prob_solid_precipitation;
        int prob_freezing_rain;
        int prob_fog;
        int visibility;
        double uv;

        public Bitmap getArrowBitmap(Context context, float degrees){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.arrow);
            if (bitmap != null){
                Matrix m = new Matrix();
                m.postRotate(degrees);
                return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,false);
            } else {
                return null;
            }
        }

        public Bitmap getArrowBitmap(Context context){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.arrow);
            if (bitmap != null){
                Matrix m = new Matrix();
                m.postRotate((float) wind_direction);
                return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,false);
            } else {
                return null;
            }
        }

        public int getCondition(){
            return condition_code;
        }

        public String getPrecipitation(){
            return String.valueOf(precipitation)+" kg/m²";
        }

        public String getProbPrecipitation(){
            return String.valueOf(prob_precipitation)+" %";
        }

        public String getTemperature(){
            return String.valueOf(temperature+KelvinConstant+" °");
        }

        public int getTemperatureInt(){
            return (int) (temperature+KelvinConstant);
        }

        public String getMaxTemperature(){
            return String.valueOf(temperature_high+KelvinConstant+" °");
        }

        public int getMaxTemperatureInt(){
            return (int) (temperature_high+KelvinConstant);
        }

        public String getMinTemperature(){
            return String.valueOf(temperature_low+KelvinConstant+" °");
        }

        public int getMinTemperatureInt(){
            return (int) (temperature_low+KelvinConstant);
        }

        public String getWindSpeed(){
            return String.valueOf(wind_speed+ "m/s");
        }

        public String getWindSpeedInKmh(){
            int speed = (int) (wind_speed*1000/(60*60));
            return String.valueOf(speed)+ "km/h";
        }

        public int getWindSpeedInKmhInt(){
            int speed = (int) (wind_speed*1000/(60*60));
            return speed;
        }

        public String getFlurries(){
            return String.valueOf(flurries+ "m/s");
        }

        public double getWindDirection(){
            return wind_direction;
        }

        public boolean isDaytime(){
            // daytime = 6:00 - 19:00
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if ((hour>6) && (hour<=19)){
                return true;
            }
            return false;
        }

    }

    public class CurrentWeatherInfo{
        String city;
        long issue_timestamp;
        long polling_time;
        WeatherInfo currentWeather;
        ArrayList<WeatherInfo> forecast6hourly;
        ArrayList<WeatherInfo> forecast24hourly;

        public CurrentWeatherInfo(RawWeatherInfo rawWeatherInfo){
            city = rawWeatherInfo.description;
            polling_time = rawWeatherInfo.polling_time;
            currentWeather = new WeatherInfo();
            // get current weather data
            int current_weather_position = rawWeatherInfo.getCurrentForecastPosition();
            int next_midnight_position   = rawWeatherInfo.getNextMidnightAfterCurrentForecastPosition();
            currentWeather.timestamp                = Integer.parseInt(rawWeatherInfo.timesteps[current_weather_position]);
            currentWeather.condition_code           = Integer.parseInt(rawWeatherInfo.ww[current_weather_position]);
            currentWeather.clouds                   = Integer.parseInt(rawWeatherInfo.N[current_weather_position]);
            currentWeather.temperature              = Double.parseDouble(rawWeatherInfo.TTT[current_weather_position]);
            currentWeather.temperature_low          = rawWeatherInfo.getMinTemperature(current_weather_position,next_midnight_position);
            currentWeather.temperature_high         = rawWeatherInfo.getMaxTemperature(current_weather_position,next_midnight_position);
            currentWeather.wind_speed               = Double.parseDouble(rawWeatherInfo.FF[current_weather_position]);
            currentWeather.wind_direction           = Double.parseDouble(rawWeatherInfo.DD[current_weather_position]);
            currentWeather.flurries                 = Double.parseDouble(rawWeatherInfo.FX1[current_weather_position]);
            currentWeather.precipitation            = Double.parseDouble(rawWeatherInfo.RR1c[current_weather_position]);
            currentWeather.prob_precipitation       = Integer.parseInt(rawWeatherInfo.wwP[current_weather_position]);
            currentWeather.prob_thunderstorms       = Integer.parseInt(rawWeatherInfo.wwT[current_weather_position]);
            currentWeather.prob_fog                 = Integer.parseInt(rawWeatherInfo.wwM[current_weather_position]);
            currentWeather.prob_solid_precipitation = Integer.parseInt(rawWeatherInfo.wwS[current_weather_position]);
            currentWeather.prob_freezing_rain       = Integer.parseInt(rawWeatherInfo.wwF[current_weather_position]);
            currentWeather.visibility               = Integer.parseInt(rawWeatherInfo.VV[current_weather_position]);
            currentWeather.uv                       = Double.parseDouble(rawWeatherInfo.RRad1[current_weather_position]);
            // fill 6h forecast arraylist
            forecast6hourly = new ArrayList<WeatherInfo>();
            int index = rawWeatherInfo.getNext6hPosition();
            while (index<rawWeatherInfo.elements){
                WeatherInfo wi = new WeatherInfo();
                wi.timestamp  = Integer.parseInt(rawWeatherInfo.timesteps[index]);
                wi.condition_code = Integer.parseInt(rawWeatherInfo.WPc61[index]);
                wi.clouds                               = rawWeatherInfo.getAverageClouds(index-5,index);
                currentWeather.temperature              = rawWeatherInfo.getAverageTemperature(index-5,index);
                currentWeather.temperature_low          = rawWeatherInfo.getMinTemperature(index-5,index);
                currentWeather.temperature_high         = rawWeatherInfo.getMaxTemperature(index-5,index);
                currentWeather.wind_speed               = rawWeatherInfo.getAverageValue(rawWeatherInfo.FF,index+5,index);
                currentWeather.wind_direction           = rawWeatherInfo.getAverageValue(rawWeatherInfo.DD,index+5,index);
                currentWeather.flurries                 = rawWeatherInfo.getMaxValue(rawWeatherInfo.FX1,index+5,index);
                currentWeather.precipitation            = Integer.parseInt(rawWeatherInfo.RR6c[index]);
                currentWeather.prob_precipitation       = Integer.parseInt(rawWeatherInfo.wwP6[index]);
                currentWeather.prob_thunderstorms       = Integer.parseInt(rawWeatherInfo.wwT6[index]);
                currentWeather.prob_fog                 = Integer.parseInt(rawWeatherInfo.wwM6[index]);
                currentWeather.prob_solid_precipitation = Integer.parseInt(rawWeatherInfo.wwS6[index]);
                currentWeather.prob_freezing_rain       = Integer.parseInt(rawWeatherInfo.wwF6[index]);
                currentWeather.visibility               = rawWeatherInfo.getAverageValue(rawWeatherInfo.VV,index+5,index);
                currentWeather.uv                       = rawWeatherInfo.getAverageValue(rawWeatherInfo.RRad1,index+5,index);
                forecast6hourly.add(wi);
                index = index + 6;
            }
            // fill 24h forecast arraylist
            forecast24hourly = new ArrayList<WeatherInfo>();
            index = rawWeatherInfo.getNext24hPosition();
            while (index<rawWeatherInfo.elements){
                WeatherInfo wi = new WeatherInfo();
                wi.timestamp  = Integer.parseInt(rawWeatherInfo.timesteps[index]);
                wi.condition_code = Integer.parseInt(rawWeatherInfo.WPcd1[index]);
                wi.clouds                               = rawWeatherInfo.getAverageClouds(index-23,index);
                currentWeather.temperature              = rawWeatherInfo.getAverageTemperature(index-23,index);
                currentWeather.temperature_low          = rawWeatherInfo.getMinTemperature(index-23,index);
                currentWeather.temperature_high         = rawWeatherInfo.getMaxTemperature(index-23,index);
                currentWeather.wind_speed               = rawWeatherInfo.getAverageValue(rawWeatherInfo.FF,index+23,index);
                currentWeather.wind_direction           = rawWeatherInfo.getAverageValue(rawWeatherInfo.DD,index+23,index);
                currentWeather.flurries                 = rawWeatherInfo.getMaxValue(rawWeatherInfo.FX1,index+23,index);
                currentWeather.precipitation            = Integer.parseInt(rawWeatherInfo.RRdc[index]);
                currentWeather.prob_precipitation       = rawWeatherInfo.getMaxValue(rawWeatherInfo.wwP,index+23,index);
                currentWeather.prob_thunderstorms       = Integer.parseInt(rawWeatherInfo.wwTd[index]);
                currentWeather.prob_fog                 = Integer.parseInt(rawWeatherInfo.wwMd[index]);
                currentWeather.prob_solid_precipitation = rawWeatherInfo.getMaxValue(rawWeatherInfo.wwS,index+23,index);
                currentWeather.prob_freezing_rain       = rawWeatherInfo.getMaxValue(rawWeatherInfo.wwF,index+23,index);
                currentWeather.visibility               = rawWeatherInfo.getAverageValue(rawWeatherInfo.VV,index+23,index);
                currentWeather.uv                       = rawWeatherInfo.getAverageValue(rawWeatherInfo.RRad1,index+23,index);
                forecast6hourly.add(wi);
                index = index + 24;
            }
        }

        public String getCity(){
            return city;
        }

    }

    public CurrentWeatherInfo getCurrentWeatherInfo(Context context){
        WeatherForecastContentProvider weatherForecastContentProvider = new WeatherForecastContentProvider();
        RawWeatherInfo rawWeatherInfo = weatherForecastContentProvider.readWeatherForecast(context);
        CurrentWeatherInfo currentWeatherInfo = new CurrentWeatherInfo(rawWeatherInfo);
        return currentWeatherInfo;
    }

}
