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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.*;
import org.astronomie.info.Astronomy;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class Weather {

    public final static double KelvinConstant = 273.15;
    public final static int MILLIS_IN_HOUR = 60*60*1000;
    public final static int DELTA_T = 69;

    public static class WeatherLocation implements Comparator<WeatherLocation> {
        public String description;
        public String name;
        double latitude;
        double longitude;
        double altitude;
        float distance;

        public WeatherLocation(){
            description="";
            name="";
            latitude=0;
            longitude=0;
            altitude=0;
            distance=0;
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
        double RRad1;       // % (0..80); global irradiance within the last hour
        double Rad1h;       // kJ/m²; global irradiance
        double RadL3;       // kJ/m²; long wave radiation balance during last 3h 
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
        private long timestamp;
        private int forecast_type = ForecastType.UNKNOWN;
        private Integer condition_code;
        private boolean condition_is_calculated = false;
        private Double temperature;
        private Double temperature_high;
        private Double temperature_low;
        private Double wind_speed;
        private Double wind_direction;
        private Double flurries;
        private Double precipitation;
        private Integer clouds;
        private Integer prob_thunderstorms;
        private Integer prob_precipitation;
        private Integer prob_solid_precipitation;
        private Integer prob_drizzle;
        private Integer prob_freezing_rain;
        private Integer prob_fog;
        private Integer visibility;
        private Integer prob_visibility_below_1km;
        private Double pressure;
        private Double uv;

        final class ForecastType{
            public static final int CURRENT  = 0;
            public static final int ONE_HOUR = 1;
            public static final int HOURS_3  = 2;
            public static final int HOURS_6  = 3;
            public static final int HOURS_12 = 4;
            public static final int HOURS_24 = 5;
            public static final int UNKNOWN  = 128;
        }

        public WeatherInfo(){

        }

        public void setTimestamp(long timestamp){
            this.timestamp = timestamp;
        }

        public void setForecastType(int i){
            this.forecast_type = i;
        }

        public void setConditionCode(Integer condition_code){
            this.condition_code = condition_code;
        }

        public void setTemperature(Double temperature){
            this.temperature = temperature;
        }

        public void setLowTemperature(Double temperature_low){
            this.temperature_low= temperature_low;
        }

        public void setHighTemperature(Double temperature_high){
            this.temperature_high = temperature_high;
        }

        public void setWindSpeed(Double wind_speed){
            this.wind_speed = wind_speed;
        }

        public void setWindDirection(Double wind_direction){
            this.wind_direction = wind_direction;
        }

        public void setFlurries(Double flurries){
            this.flurries = flurries;
        }

        public void setPrecipitation(Double precipitation){
            this.precipitation = precipitation;
        }

        public void setClouds(Integer clouds){
            this.clouds = clouds;
        }

        public void setProbThunderstorms(Integer thunderstorms){
            this.prob_thunderstorms = thunderstorms;
        }

        public void setProbPrecipitation(Integer prob_precipitation){
            this.prob_precipitation = prob_precipitation;
        }

        public void setProbDrizzle(Integer prob_precipitation){
            this.prob_drizzle = prob_precipitation;
        }

        public void setProbSolidPrecipitation(Integer prob_solid_precipitation){
            this.prob_solid_precipitation = prob_solid_precipitation;
        }

        public void setProbFreezingRain(Integer prob_freezing_rain){
            this.prob_freezing_rain = prob_freezing_rain;
        }

        public void setProbFog(Integer prob_fog){
            this.prob_fog = prob_fog;
        }

        public void setProbVisibilityBelow1km(Integer prob_visibility_below_1km){
            this.prob_visibility_below_1km = prob_visibility_below_1km;
        }

        public void setVisibility(Integer visibility){
            this.visibility = visibility;
        }

        public void setPressure(Double pressure){
            this.pressure = pressure;
        }

        public void setUV(Double uv){
            this.uv = uv;
        }

        public long getTimestamp(){
            return this.timestamp;
        }

        public int getForecastType(){
            return this.forecast_type;
        }

        public boolean hasWindDirection(){
            if (wind_direction!=null){
                return true;
            }
            return false;
        }

        public Bitmap getArrowBitmap(Context context){
            // wind direction is in ° and is the direction where the wind comes from.
            // new arrow icon neutral position is 0°.
            // rotation is clockwise.
            if (wind_direction!=null){
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.arrow);
                if (bitmap != null){
                    Matrix m = new Matrix();
                    m.postRotate(wind_direction.floatValue());
                    return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,false);
                }
            }
            return null;
        }

        public Bitmap getBeaufortBitmap(Context context){
            // fall back to arrow if wind speed is unknown
            if (wind_speed==null){
                return getArrowBitmap(context);
            }
            if (wind_direction!=null) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), getBeaufortIconResourceID(getWindSpeedInBeaufortInt()));
                if (bitmap != null) {
                    Matrix m = new Matrix();
                    m.postRotate(wind_direction.floatValue());
                    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
                } else {
                    return null;
                }
            }
            return null;
        }

        private int getBeaufortIconResourceID(int windspeed_beaufort){
            switch (windspeed_beaufort){
                case 0: return R.mipmap.wind_beaufort_00;
                case 1: return R.mipmap.wind_beaufort_01;
                case 2: return R.mipmap.wind_beaufort_02;
                case 3: return R.mipmap.wind_beaufort_03;
                case 4: return R.mipmap.wind_beaufort_04;
                case 5: return R.mipmap.wind_beaufort_05;
                case 6: return R.mipmap.wind_beaufort_06;
                case 7: return R.mipmap.wind_beaufort_07;
                case 8: return R.mipmap.wind_beaufort_08;
                case 9: return R.mipmap.wind_beaufort_09;
                case 10: return R.mipmap.wind_beaufort_10;
                case 11: return R.mipmap.wind_beaufort_11;
                case 12: return R.mipmap.wind_beaufort_12;
            }
            return 12;
        }

        public String getWindDirectionString(Context context){
            if ((wind_direction>337.5) || (wind_direction<=22.5)){
                return context.getResources().getString(R.string.direction_north);
            }
            if ((wind_direction>22.5) && (wind_direction<=67.5)){
                return context.getResources().getString(R.string.direction_northeast);
            }
            if ((wind_direction>67.5) && (wind_direction<=112.5)){
                return context.getResources().getString(R.string.direction_east);
            }
            if ((wind_direction>112.5) && (wind_direction<=157.5)){
                return context.getResources().getString(R.string.direction_southeast);
            }
            if ((wind_direction>157.5) && (wind_direction<=202.5)){
                return context.getResources().getString(R.string.direction_south);
            }
            if ((wind_direction>202.5) && (wind_direction<=247.5)){
                return context.getResources().getString(R.string.direction_southwest);
            }
            if ((wind_direction>247.5) && (wind_direction<=292.5)){
                return context.getResources().getString(R.string.direction_west);
            }
            if ((wind_direction>292.5) && (wind_direction<=337.5)) {
                return context.getResources().getString(R.string.direction_northwest);
            }
          return "?";
        }


        public Bitmap getWindSymbol(Context context, int windDisplayType){
            float width_bitmap = 256;
            float height_bitmap = 256;
            if (windDisplayType==WindDisplayType.BEAUFORT){
                return getBeaufortBitmap(context);
            }
            if (windDisplayType==WindDisplayType.TEXT){
                Bitmap bitmap = Bitmap.createBitmap(Math.round(width_bitmap),Math.round(height_bitmap), Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.TRANSPARENT);
                String windsting = getWindDirectionString(context);
                float max_fontsize = LargeWidget.getMaxPossibleFontsize(windsting,width_bitmap,height_bitmap);
                Paint paint = new Paint();
                paint.setColor(MainActivity.getColorFromResource(context,R.color.widget_textcolor));
                paint.setTextSize(max_fontsize);
                // center text vertically & horizontally
                float x_offset     = (width_bitmap-paint.measureText(windsting))/2;
                float y_offset     = (height_bitmap - paint.getTextSize())/2 + paint.getTextSize();
                Canvas canvas = new Canvas(bitmap);
                canvas.drawText(windsting,x_offset,y_offset,paint);
                return bitmap;
            }
            return getArrowBitmap(context);
        }

        public String getWindSpeedString(Context context, boolean unit){
            WeatherSettings weatherSettings = new WeatherSettings(context);
            if (weatherSettings.getWindDisplayUnit()==WindDisplayUnit.KNOTS){
                String windspeedstring = String.valueOf(getWindSpeedInKnotsInt());
                if (unit){
                    windspeedstring = windspeedstring + "kn";
                }
                return windspeedstring;
            }
            if (weatherSettings.getWindDisplayUnit()==WindDisplayUnit.BEAUFORT){
                String windspeedstring = String.valueOf(getWindSpeedInBeaufortInt());
                if (unit){
                    windspeedstring = windspeedstring + "bf";
                }
                return windspeedstring;
            }
            if (weatherSettings.getWindDisplayUnit()==WindDisplayUnit.METERS_PER_SECOND){
                String windspeedstring = String.valueOf(getWindSpeedInMsInt());
                if (unit){
                    windspeedstring = windspeedstring + "m/s";
                }
                return windspeedstring;
            }
            String windspeedstring = String.valueOf(getWindSpeedInKmhInt());
            if (unit){
                windspeedstring = windspeedstring + "km/h";
            }
            return windspeedstring;
        }

        public double getWindDirection(){
            return wind_direction;
        }

        public int getWindDirectionInt(){
            int j = wind_direction.intValue();
            return j;
        }

        public boolean hasCondition(){
            if (condition_code!=null){
                return true;
            }
            return false;
        }

        public int getCondition(){
            return condition_code;
        }

        public boolean hasTemperature(){
            if (temperature!=null){
                return true;
            }
            return false;
        }

        public int getTemperatureInt(){
            int j = temperature.intValue();
            return j;
        }

        public double getTemperatureInCelsius(){
            Double d = (temperature - KelvinConstant);
            return d;
        }
        public int getTemperatureInCelsiusInt(){
            Double d = (temperature - KelvinConstant);
            int j = d.intValue();
            return j;
        }

        public boolean hasMaxTemperature(){
            if (temperature_high!=null){
                return true;
            }
            return false;
        }

        public int getMaxTemperatureInt(){
            int j = temperature_high.intValue();
            return j;
        }

        public double getMaxTemperatureInCelsius(){
            return (temperature_high - KelvinConstant);
        }

        public int getMaxTemperatureInCelsiusInt(){
            Double d = getMaxTemperatureInCelsius();
            int j = d.intValue();
            return j;
        }

        public boolean hasMinTemperature(){
            if (temperature_low!=null){
                return true;
            }
            return false;
        }

        public double getMinTemperature(){
            return temperature_low;
        }

        public int getMinTemperatureInt(){
            int j = temperature_low.intValue();
            return j;
        }

        public int getMinTemperatureInCelsiusInt(){
            Double d = temperature_low - KelvinConstant;
            int j = d.intValue();
            return j;
        }

        public boolean hasPrecipitation(){
            if (precipitation!=null){
                return true;
            }
            return false;
        }

        public double getPrecipitation(){
            return precipitation;
        }

        public String getPrecipitationString(){
            if (precipitation!=null){
                return String.valueOf(precipitation)+" kg/m²";
            } else {
                return "-";
            }
        }

        public boolean hasProbPrecipitation(){
            if (prob_precipitation!=null){
                return true;
            }
            return false;
        }

        public int getProbPrecipitation(){
            return prob_precipitation;

        }

        public boolean hasProbDrizzle(){
            if (prob_drizzle!=null){
                return true;
            }
            return false;
        }

        public int getProbDrizzle(){
            return prob_drizzle;
        }

        public boolean hasWindSpeed(){
            if (wind_speed!=null){
                return true;
            }
            return false;
        }

        public int getWindSpeedInMsInt(){
            Double d = wind_speed;
            return d.intValue();
        }

        public int getWindSpeedInKmhInt(){
            Double d = (wind_speed*3.6);
            int speed = d.intValue();
            return speed;
        }

        private int fromKmhToBeaufort(int wind_kmh){
            int beauford = 12;
            if (wind_kmh<=117){
                beauford = 11;
            }
            if (wind_kmh<=102){
                beauford = 10;
            }
            if (wind_kmh<=88){
                beauford = 9;
            }
            if (wind_kmh<=74){
                beauford = 8;
            }
            if (wind_kmh<=61){
                beauford = 7;
            }
            if (wind_kmh<=49){
                beauford = 6;
            }
            if (wind_kmh<=38){
                beauford = 5;
            }
            if (wind_kmh<=28){
                beauford = 4;
            }
            if (wind_kmh<=19){
                beauford = 3;
            }
            if (wind_kmh<=11){
                beauford = 2;
            }
            if (wind_kmh<=5){
                beauford = 1;
            }
            if (wind_kmh<=1){
                beauford = 0;
            }
            return beauford;
        }

        public int getWindSpeedInBeaufortInt(){
            return fromKmhToBeaufort(getWindSpeedInKmhInt());
        }

        public int getWindSpeedInKnotsInt(){
            Double d = wind_speed*1.943844;
            return d.intValue();
        }

        public boolean hasFlurries(){
            if (flurries!=null){
                return true;
            }
            return false;
        }

        public int getFlurriesInMsInt(){
            Double d = flurries;
            return d.intValue();
        }

        public int getFlurriesInKmhInt(){
            Double d = (flurries*3.6);
            int flurries = d.intValue();
            return flurries;
        }

        public int getFlurriesInBeaufortInt(){
            return fromKmhToBeaufort(getFlurriesInKmhInt());
        }

        public int getFlurriesInKnotsInt(){
            Double d = flurries*1.943844;
            return d.intValue();
        }

        public boolean hasClouds(){
            if (clouds!=null){
                return true;
            }
            return false;
        }

        public int getClouds(){
            return clouds;
        }

        public boolean hasProbThunderstorms(){
            if (prob_thunderstorms!=null){
                return true;
            }
            return false;
        }

        public int getProbThunderStorms(){
            return prob_thunderstorms;
        }

        public boolean hasProbSolidPrecipitation(){
            if (prob_solid_precipitation!=null){
                return true;
            }
            return false;
        }

        public int getProbSolidPrecipitation(){
            return prob_solid_precipitation;
        }

        public boolean hasProbFreezingRain(){
            if (prob_freezing_rain!=null){
                return true;
            }
            return false;
        }

        public int getProbFreezingRain(){
            return prob_freezing_rain;
        }

        public boolean hasProbFog(){
            if (prob_fog!=null){
                return true;
            }
            return false;
        }

        public int getProbFog(){
            return prob_fog;
        }

        public boolean hasVisibility(){
            if (visibility!=null){
                return true;
            }
            return false;
        }

        public int getVisibilityInMetres(){
            return visibility;
        }

        public double getVisibilityInNauticMiles(){
            Double d = (double) visibility;
            Double nm = d / 1852;
            return nm;
        }

        public double getVisibilityInMiles(){
            Double d = (double) visibility;
            Double nm = d / 1609.344;
            return nm;
        }

        public double getVisibilityInYards(){
            Double d = (double) visibility;
            Double nm = d * 0.9144;
            return nm;
        }

        public boolean hasProbVisibilityBelow1km(){
            return prob_visibility_below_1km!=null;
        }

        public int getProbVisibilityBelow1km(){
            return prob_visibility_below_1km;
        }

        public boolean hasPressure() {
            return pressure!=null;
        }

        public int getPressure(){
            int p = pressure.intValue();
            return p;
        }

        public boolean hasUV(){
            if (uv!=null){
                return true;
            }
            return false;
        }

        public int getUV(){
            int j = uv.intValue();
            return j;
        }


        public boolean isDaytime(WeatherLocation weatherLocation){
            boolean result = Weather.isDaytime(weatherLocation,timestamp);
            return result;
        }

        public boolean calculateMissingCondition(){
            if (condition_code==null){
                condition_code = WeatherCodeContract.calculateCustomWeatherconditionFromData(this);
                if (condition_code != WeatherCodeContract.NOT_AVAILABLE){
                    this.condition_is_calculated = true;
                    return true;
                }
            }
            return false;
        }

        public boolean isConditionCalculated(){
            return condition_is_calculated;
        }

    }

    public CurrentWeatherInfo getCurrentWeatherInfo(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        WeatherSettings weatherSettings = new WeatherSettings(context);
        String station_name = weatherSettings.station_name;
        Cursor cursor;
        String[] selectionArg={station_name};
        try {
            cursor = contentResolver.query(WeatherForecastContentProvider.URI_SENSORDATA,
                    null,WeatherForecastContentProvider.WeatherForecastDatabaseHelper.KEY_name+" = ?",selectionArg,null);
            // read only fist element. Database should not hold more than one data set for one station.
            if (cursor.moveToFirst()){
                WeatherForecastContentProvider weatherForecastContentProvider = new WeatherForecastContentProvider();
                RawWeatherInfo rawWeatherInfo = weatherForecastContentProvider.getWeatherCardFromCursor(cursor);
                CurrentWeatherInfo currentWeatherInfo = new CurrentWeatherInfo(rawWeatherInfo);
                // check if local weather data is outdated
                if (currentWeatherInfo.polling_time<Calendar.getInstance().getTimeInMillis()+weatherSettings.getUpdateIntervalInMillis()){
                    return currentWeatherInfo;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            PrivateLog.log(context,Tag.DATABASE,"database error when getting weather data: "+e.getMessage());
        }
        // return null if no correspondig data set found in local database.
        return null;
    }

    private int deleteWeatherDataSet(Context context, int i){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        int rows = 0;
        try {
            rows = contentResolver.delete(WeatherForecastContentProvider.URI_SENSORDATA,WeatherForecastContentProvider.WeatherForecastDatabaseHelper.KEY_id+"=?",new String[] {String.valueOf(i)});
            //rows = sql_db.delete(TABLE_NAME, KEY_id+"=?", new String[] {String.valueOf(i)});
        } catch (Exception e) {
            // do nothing here
        }
        return rows;
    }

    public static final String[] SQL_COMMAND_QUERYTIMECOLUMN = {WeatherForecastContentProvider.WeatherForecastDatabaseHelper.KEY_timestamp,
                                                                WeatherForecastContentProvider.WeatherForecastDatabaseHelper.KEY_name};

    private ArrayList<RawWeatherInfo> getTimestampArrayList(Context context) {
        WeatherForecastContentProvider weatherForecastContentProvider = new WeatherForecastContentProvider();
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        ArrayList<RawWeatherInfo> dataArrayList = new ArrayList<RawWeatherInfo>();
        Cursor c = null;
        try {
            c = contentResolver.query(WeatherForecastContentProvider.URI_SENSORDATA,SQL_COMMAND_QUERYTIMECOLUMN,null,null,null);
            if (c.moveToFirst()) {
                do {
                    RawWeatherInfo rawWeatherInfo = weatherForecastContentProvider.getWeatherCardFromCursor(c);
                    dataArrayList.add(rawWeatherInfo);
                } while (c.moveToNext());
            }
        } catch (Exception SQLiteException){
            // onCreate(sql_db);
        }
        if (c!=null)
            c.close();
        return dataArrayList;
    }

    private void cleanDataBase(Context context, ArrayList<RawWeatherInfo> data){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        int size = data.size();
        int deleted_count = 0;
        for (int i=0; i<size; i++) {
            if (data.get(i).timestamp + weatherSettings.getUpdateIntervalInMillis() < Calendar.getInstance().getTimeInMillis()){
                // never remove the current location from the database, even if data is old
                if (!data.get(i).weatherLocation.name.toUpperCase().equals(weatherSettings.station_name.toUpperCase()))
                    deleted_count = deleted_count + deleteWeatherDataSet(context,i);
            }
        }
        PrivateLog.log(context,Tag.DATABASE,"Garbage collected "+deleted_count+" data sets.");
    }

    public void cleanDataBase(Context context){
        ArrayList<RawWeatherInfo> dataArrayList = getTimestampArrayList(context);
        if (dataArrayList != null){
            cleanDataBase(context,dataArrayList);
        }
    }

    private static double getJulianDay(long time){
        Calendar c1 = Calendar.getInstance();
        c1.clear();
        c1.set(Calendar.YEAR,2020);
        c1.set(Calendar.MONTH,0);
        c1.set(Calendar.DAY_OF_MONTH,0);
        c1.set(Calendar.HOUR,0);
        c1.set(Calendar.MINUTE,0);
        c1.set(Calendar.SECOND,0);
        c1.set(Calendar.MILLISECOND,0);
        double days_since_2020 = TimeUnit.DAYS.convert(time - c1.getTimeInMillis(),TimeUnit.MILLISECONDS);
        //Log.v("TWF","Days since 01.01.2020: "+days_since_2020);
        double julian_days = 2458849 + days_since_2020; // 2458849.41667 = 01.01.2020 00:00:00
        return julian_days;
    }

    public static Astronomy.Riseset getRiseset(Weather.WeatherLocation weatherLocation, long time){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int zone = ((calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) /(1000*60*60));
        return Astronomy.sunRise(getJulianDay(time),
                DELTA_T,
                Math.toRadians(weatherLocation.longitude),
                Math.toRadians(weatherLocation.latitude),
                zone,false);
    }

    public static boolean isDaytime(Weather.WeatherLocation weatherLocation, long time){
        if (usePreciseIsDaytime(weatherLocation)){
            Astronomy.Riseset riseset = getRiseset(weatherLocation,time);
            // use precise calculation, as geo-location qualifies for use of formula
            // determine timezone offset
            //Log.v("TWF","Time UP   :"+getSunriseInUTC(riseset,time));
            //Log.v("TWF","Time      :"+time);
            //Log.v("TWF","Time Down :"+getSunsetInUTC(riseset,time));
            //Log.v("TWF","Sunrise   :"+riseset.rise);
            //Log.v("TWF","Sunset    :"+riseset.set);
            // now compare in utc....
            if ((time>=getSunriseInUTC(riseset,time)) && (time<getSunsetInUTC(riseset,time))){
                return true;
            }
            return false;
        } else {
            // simple, static formula
            // daytime = 6:00 - 19:00
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            return (hour > 6) && (hour <= 19);
        }
    }

    /**
     * Returns if a precise calculation of day/night time with the Astronomy class makes sense. It
     * makes sense between a latutide of -65° to +65°, but not further south or north.
     *
     * @param weatherLocation
     * @return
     */

    public static boolean usePreciseIsDaytime(WeatherLocation weatherLocation){
        if ((weatherLocation.latitude<-65) || (weatherLocation.latitude>65)){
            return false;
        }
        return true;
    }

    public static long getSunsetInUTC(Astronomy.Riseset riseset, long time){
        Calendar c = Calendar.getInstance();
        // set calendar to midnight
        c.setTimeInMillis(time);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return (long) (c.getTimeInMillis() + riseset.set * MILLIS_IN_HOUR);
    }

    public static long getSunriseInUTC(Astronomy.Riseset riseset, long time){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        // set calendar to midnight
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return (long) (c.getTimeInMillis() + riseset.rise * MILLIS_IN_HOUR);
    }

    public static long getCivilTwilightMorning(Astronomy.Riseset riseset, long time){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        // set calendar to midnight
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return (long) (c.getTimeInMillis() + riseset.cicilTwilightMorning*MILLIS_IN_HOUR);
    }

    public static long getCivilTwilightEvening(Astronomy.Riseset riseset, long time){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        // set calendar to midnight
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return (long) (c.getTimeInMillis() + riseset.cicilTwilightEvening*MILLIS_IN_HOUR);
    }

    public static boolean isSunriseInIntervalUTC(Astronomy.Riseset riseset, long start, long stop){
        long sunrise = getSunriseInUTC(riseset,(start+stop)/2);
        //Log.v("TWF","Time start  :"+toHourMinuteString(start));
        //Log.v("TWF","Time Sunrise:"+toHourMinuteString(sunrise));
        //Log.v("TWF","Time stop   :"+toHourMinuteString(stop));
        if ((sunrise>=start) && (sunrise<=stop)){
            return true;
        }
        return false;
    }

    public static boolean isSunsetInIntervalUTC(Astronomy.Riseset riseset, long start, long stop){
        long sunset = getSunsetInUTC(riseset,(start+stop)/2);
        //Log.v("TWF","Time start  :"+toHourMinuteString(start)+" "+start);
        //Log.v("TWF","Time Sunset :"+toHourMinuteString(sunset)+" "+sunset);
        //Log.v("TWF","Time stop   :"+toHourMinuteString(stop)+" "+stop);
        if ((sunset>=start) && (sunset<=stop)){
            return true;
        }
        return false;
    }

    public static String toHourMinuteString(long time){
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(new Date(time));
    }

    public static String toFullDateTimeString(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, dd.MM.yyyy, HH:mm");
        return simpleDateFormat.format(new Date(time));
    }

    public static class DisplayLayout{
        public final static int DEFAULT = 0;
    }

    public static class WindDisplayType{
        public final static int ARROW = 0;
        public final static int BEAUFORT = 1;
        public final static int TEXT = 2;
    }

    public static class WindDisplayUnit{
        public final static int METERS_PER_SECOND = 0;
        public final static int KILOMETERS_PER_HOUR = 1;
        public final static int BEAUFORT = 2;
        public final static int KNOTS = 3;
    }

    public static String getWindUnitString(int type){
        switch (type){
            case WindDisplayUnit.METERS_PER_SECOND: return "m/s";
            case WindDisplayUnit.KILOMETERS_PER_HOUR: return "km/h";
            case WindDisplayUnit.BEAUFORT: return "bf";
            case WindDisplayUnit.KNOTS: return "kn";
        }
        return "?";
    }

    public static class DistanceDisplayUnit{
        public final static int METRIC = 0;
        public final static int NAUTIC = 1;
        public final static int IMPERIAL = 2;
    }

}

