/**
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
import android.graphics.drawable.Drawable;
import android.os.Build;

public final class WeatherCodeContract {
    public final static int SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW = 95;
    public final static int DRIZZLE_FREEZING_MODERATE_OR_HEAVY = 57;
    public final static int DRIZZLE_FREEZING_SLIGHT = 56;
    public final static int RAIN_FREEZING_MODERATE_OR_HEAVY = 67;
    public final static int RAIN_FREEZING_SLIGHT = 66;
    public final static int SNOW_SHOWERS_MODERATE_OR_HEAVY = 86;
    public final static int SNOW_SHOWERS_SLIGHT = 85;
    public final static int SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY = 84;
    public final static int SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT = 83;
    public final static int EXTREMELY_HEAVY_RAIN_SHOWER = 82;
    public final static int MODERATE_OR_HEAVY_RAIN_SHOWERS = 81;
    public final static int SLIGHT_RAIN_SHOWER = 80;
    public final static int HEAVY_SNOWFALL_CONTINUOUS = 75;
    public final static int MODERATE_SNOWFALL_CONTINUOUS = 73;
    public final static int SLIGHT_SNOWFALL_CONTINUOUS= 71;
    public final static int MODERATE_OR_HEAVY_RAIN_AND_SNOW = 69;
    public final static int SLIGHT_RAIN_AND_SNOW = 68;
    public final static int HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS = 55;
    public final static int MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS = 53;
    public final static int SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS = 51;
    public final static int HEAVY_RAIN_NOT_FREEZING_CONTINUOUS = 65;
    public final static int MODERATE_RAIN_NOT_FREEZING_CONTINUOUS = 63;
    public final static int SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS = 61;
    public final static int ICE_FOG_SKY_NOT_RECOGNIZABLE = 24;
    public final static int FOG_SKY_NOT_RECOGNIZABLE = 25;
    public final static int EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8 = 03;
    public final static int EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8 = 02;
    public final static int EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8 = 01;
    public final static int EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8 = 00;

    private static final class LineageOsWeatherContract {
        public final static int BLUSTERY = 22;
        public final static int CLEAR_NIGHT = 30;
        public final static int CLOUDY = 25;
        public final static int COLD = 24;
        public final static int DRIZZLE = 9;
        public final static int FAIR_DAY = 33;
        public final static int FAIR_NIGHT = 32;
        public final static int FREEZING_DRIZZLE = 8;
        public final static int FREEZING_RAIN = 10;
        public final static int HEAVY_SNOW = 39;
        public final static int HOT = 35;
        public final static int HURRICANE = 2;
        public final static int LIGHT_SNOW_SHOWERS = 13;
        public final static int MIXED_RAIN_AND_SNOW = 5;
        public final static int MOSTLY_CLOUDY_DAY = 27;
        public final static int MOSTLY_CLOUDY_NIGHT = 26;
        public final static int NOT_AVAILABLE = 3200;
        public final static int PARTLY_CLOUDY_DAY = 29;
        public final static int PARTLY_CLOUDY_NIGHT = 28;
        public final static int SCATTERED_SHOWERS = 38;
        public final static int SCATTERED_SNOW_SHOWERS = 40;
        public final static int SHOWERS = 11;
        public final static int SNOW = 15;
        public final static int SNOW_FLURRIES = 12;
        public final static int SNOW_SHOWERS = 43;
        public final static int SUNNY = 31;
        public final static int WINDY = 23;
        public final static int THUNDERSTORMS = 4;
        public final static int FOGGY = 19;
        public final static int PARTLY_CLOUDY = 41;
    }

    public int getLineageOSWeatherCode(int weathercode){
     int result = LineageOsWeatherContract.NOT_AVAILABLE;
        switch (weathercode){
            case SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW : result=LineageOsWeatherContract.THUNDERSTORMS; break;
            case DRIZZLE_FREEZING_MODERATE_OR_HEAVY : result=LineageOsWeatherContract.FREEZING_DRIZZLE; break;
            case DRIZZLE_FREEZING_SLIGHT : result=LineageOsWeatherContract.FREEZING_DRIZZLE; break;
            case RAIN_FREEZING_MODERATE_OR_HEAVY : result=LineageOsWeatherContract.FREEZING_RAIN; break;
            case RAIN_FREEZING_SLIGHT : result=LineageOsWeatherContract.FREEZING_RAIN; break;
            case SNOW_SHOWERS_MODERATE_OR_HEAVY : result=LineageOsWeatherContract.SNOW; break;
            case SNOW_SHOWERS_SLIGHT : result=LineageOsWeatherContract.LIGHT_SNOW_SHOWERS; break;
            case SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY : result=LineageOsWeatherContract.MIXED_RAIN_AND_SNOW; break;
            case SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT : result=LineageOsWeatherContract.MIXED_RAIN_AND_SNOW; break;
            case EXTREMELY_HEAVY_RAIN_SHOWER : result=LineageOsWeatherContract.SHOWERS; break;
            case MODERATE_OR_HEAVY_RAIN_SHOWERS : result=LineageOsWeatherContract.SHOWERS; break;
            case SLIGHT_RAIN_SHOWER : result=LineageOsWeatherContract.SCATTERED_SHOWERS; break;
            case HEAVY_SNOWFALL_CONTINUOUS : result=LineageOsWeatherContract.HEAVY_SNOW; break;
            case MODERATE_SNOWFALL_CONTINUOUS : result=LineageOsWeatherContract.SNOW; break;
            case SLIGHT_SNOWFALL_CONTINUOUS: result=LineageOsWeatherContract.LIGHT_SNOW_SHOWERS; break;
            case MODERATE_OR_HEAVY_RAIN_AND_SNOW: result=LineageOsWeatherContract.MIXED_RAIN_AND_SNOW; break;
            case SLIGHT_RAIN_AND_SNOW : result=LineageOsWeatherContract.MIXED_RAIN_AND_SNOW; break;
            case HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS : result=LineageOsWeatherContract.DRIZZLE; break;
            case MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS : result=LineageOsWeatherContract.DRIZZLE; break;
            case SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS : result=LineageOsWeatherContract.DRIZZLE; break;
            case HEAVY_RAIN_NOT_FREEZING_CONTINUOUS : result=LineageOsWeatherContract.SHOWERS; break;
            case MODERATE_RAIN_NOT_FREEZING_CONTINUOUS : result=LineageOsWeatherContract.SHOWERS; break;
            case SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS : result=LineageOsWeatherContract.SCATTERED_SHOWERS; break;
            case ICE_FOG_SKY_NOT_RECOGNIZABLE : result=LineageOsWeatherContract.FOGGY; break;
            case FOG_SKY_NOT_RECOGNIZABLE : result=LineageOsWeatherContract.FOGGY; break;
            case EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8 : result=LineageOsWeatherContract.CLOUDY; break;
            case EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8 : result=LineageOsWeatherContract.CLOUDY; break;
            case EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8 : result=LineageOsWeatherContract.PARTLY_CLOUDY; break;
            case EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8 : result=LineageOsWeatherContract.SUNNY; break;
        }
        return result;
    }

    public static int getWeatherConditionDrawableResource(int weathercondition, boolean daytime){
        int result = 0;
        switch (weathercondition){
            case SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW : result=R.drawable.thunderstorm; break;
            case DRIZZLE_FREEZING_MODERATE_OR_HEAVY : result=R.drawable.freezing_drizzle; break;
            case DRIZZLE_FREEZING_SLIGHT : result=R.drawable.freezing_drizzle_slight; break;
            case RAIN_FREEZING_MODERATE_OR_HEAVY : result=R.drawable.freezing_rain; break;
            case RAIN_FREEZING_SLIGHT : result=R.drawable.freezing_rain_slight; break;
            case SNOW_SHOWERS_MODERATE_OR_HEAVY : if (daytime) {
                result=R.drawable.snow_showers_partly;
            } else {
                result=R.drawable.snow_showers_partly_night;
            }
            break;
            case SNOW_SHOWERS_SLIGHT : if (daytime) {
                result=R.drawable.light_snow_showers_partly;
            } else {
                result=R.drawable.light_snow_showers_partly_night;
            }
            break;
            case SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY : if (daytime) {
                result=R.drawable.mixed_rain_and_snow_partly;
            } else {
                result=R.drawable.mixed_rain_and_snow_partly_night;
            }
            break;
            case SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT : if (daytime) {
                result=R.drawable.light_mixed_rain_and_snow_partly;
            } else {
                result=R.drawable.light_mixed_rain_and_snow_partly_night;
            }
            break;
            case EXTREMELY_HEAVY_RAIN_SHOWER : if (daytime) {
                result=R.drawable.extremely_heavy_showers_partly;
            } else {
                result=R.drawable.extremely_heavy_showers_partly_night;
            }
            case MODERATE_OR_HEAVY_RAIN_SHOWERS : if (daytime) {
                result=R.drawable.showers_partly;
            } else {
                result=R.drawable.showers_partly_night;
            }
            break;
            case SLIGHT_RAIN_SHOWER : if (daytime) {
                result=R.drawable.light_showers_partly;
            } else {
                result=R.drawable.light_showers_partly_night;
            }
            break;
            case HEAVY_SNOWFALL_CONTINUOUS : result=R.drawable.heavy_snow_showers; break;
            case MODERATE_SNOWFALL_CONTINUOUS : result=R.drawable.moderate_snow_showers; break;
            case SLIGHT_SNOWFALL_CONTINUOUS: result=R.drawable.light_snow_showers; break;
            case MODERATE_OR_HEAVY_RAIN_AND_SNOW: result=R.drawable.mixed_rain_and_snow; break;
            case SLIGHT_RAIN_AND_SNOW : result=R.drawable.light_mixed_rain_and_snow; break;
            case HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS : result=R.drawable.heavy_drizzle; break;
            case MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS : result=R.drawable.moderate_drizzle; break;
            case SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS : result=R.drawable.light_drizzle; break;
            case HEAVY_RAIN_NOT_FREEZING_CONTINUOUS : result=R.drawable.heavy_showers; break;
            case MODERATE_RAIN_NOT_FREEZING_CONTINUOUS : result=R.drawable.moderate_showers; break;
            case SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS : result=R.drawable.light_showers; break;
            case ICE_FOG_SKY_NOT_RECOGNIZABLE : result=R.drawable.ice_fog; break;
            case FOG_SKY_NOT_RECOGNIZABLE : result=R.drawable.fog; break;
            case EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8 : result=R.drawable.cloudy; break;
            case EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8 : if (daytime) {
                result = R.drawable.mostly_cloudy_day;
            } else {
                result = R.drawable.mostly_cloudy_night;
            }
            break;
            case EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8 : if (daytime) {
                result=R.drawable.partly_cloudy_day;
            } else {
                result=R.drawable.partly_cloudy_night;
                break;
            }
            break;
            case EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8 : if (daytime) {
                result=R.drawable.sunny;
            } else {
                result=R.drawable.clear_night;
            }
            break;
        }
        return result;
    }

    public String getWeatherConditionText(Context context, int weathercondition){
        int resource = getWeatherConditionTextResource(weathercondition);
        String s = context.getResources().getString(resource);
        return s;
    }

    @SuppressWarnings("deprecation")
    public Drawable getWeatherConditionDrawable(Context context, int weathercondition){
        Drawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(getWeatherConditionDrawableResource(weathercondition,true),null);
        } else {
            drawable = context.getResources().getDrawable(weathercondition);
        }
        return drawable;
    }

    @SuppressWarnings("deprecation")
    public Drawable getWeatherConditionDrawable(Context context, int weathercondition, boolean daytime){
        Drawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(getWeatherConditionDrawableResource(weathercondition,daytime),null);
        } else {
            drawable = context.getResources().getDrawable(weathercondition);
        }
        return drawable;
    }

    public static int getWeatherConditionTextResource(int weathercondition){
        int result = 0;
        switch (weathercondition){
            case SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW : result=R.string.weathercode_SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW; break;
            case DRIZZLE_FREEZING_MODERATE_OR_HEAVY : result=R.string.weathercode_DRIZZLE_FREEZING_MODERATE_OR_HEAVY; break;
            case DRIZZLE_FREEZING_SLIGHT : result=R.string.weathercode_DRIZZLE_FREEZING_SLIGHT; break;
            case RAIN_FREEZING_MODERATE_OR_HEAVY : result=R.string.weathercode_RAIN_FREEZING_MODERATE_OR_HEAVY; break;
            case RAIN_FREEZING_SLIGHT : result=R.string.weathercode_RAIN_FREEZING_SLIGHT; break;
            case SNOW_SHOWERS_MODERATE_OR_HEAVY : result=R.string.weathercode_SNOW_SHOWERS_MODERATE_OR_HEAVY; break;
            case SNOW_SHOWERS_SLIGHT :  result=R.string.weathercode_SNOW_SHOWERS_SLIGHT; break;
            case SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY :  result=R.string.weathercode_SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY; break;
            case SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT :  result=R.string.weathercode_SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT; break;
            case EXTREMELY_HEAVY_RAIN_SHOWER :  result=R.string.weathercode_EXTREMELY_HEAVY_RAIN_SHOWER; break;
            case MODERATE_OR_HEAVY_RAIN_SHOWERS :  result=R.string.weathercode_MODERATE_OR_HEAVY_RAIN_SHOWERS; break;
            case SLIGHT_RAIN_SHOWER :  result=R.string.weathercode_SLIGHT_RAIN_SHOWER; break;
            case HEAVY_SNOWFALL_CONTINUOUS : result=R.string.weathercode_HEAVY_SNOWFALL_CONTINUOUS; break;
            case MODERATE_SNOWFALL_CONTINUOUS : result=R.string.weathercode_MODERATE_SNOWFALL_CONTINUOUS; break;
            case SLIGHT_SNOWFALL_CONTINUOUS: result=R.string.weathercode_SLIGHT_SNOWFALL_CONTINUOUS; break;
            case MODERATE_OR_HEAVY_RAIN_AND_SNOW: result=R.string.weathercode_MODERATE_OR_HEAVY_RAIN_AND_SNOW; break;
            case SLIGHT_RAIN_AND_SNOW : result=R.string.weathercode_SLIGHT_RAIN_AND_SNOW; break;
            case HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS : result=R.string.weathercode_HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS; break;
            case MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS : result=R.string.weathercode_MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS; break;
            case SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS : result=R.string.weathercode_SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS; break;
            case HEAVY_RAIN_NOT_FREEZING_CONTINUOUS : result=R.string.weathercode_HEAVY_RAIN_NOT_FREEZING_CONTINUOUS; break;
            case MODERATE_RAIN_NOT_FREEZING_CONTINUOUS : result=R.string.weathercode_MODERATE_RAIN_NOT_FREEZING_CONTINUOUS; break;
            case SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS : result=R.string.weathercode_SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS; break;
            case ICE_FOG_SKY_NOT_RECOGNIZABLE : result=R.string.weathercode_ICE_FOG_SKY_NOT_RECOGNIZABLE; break;
            case FOG_SKY_NOT_RECOGNIZABLE : result=R.string.weathercode_FOG_SKY_NOT_RECOGNIZABLE; break;
            case EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8 : result=R.string.weathercode_EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8; break;
            case EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8 : result=R.string.weathercode_EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8; break;
            case EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8 : result=R.string.weathercode_EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8; break;
            case EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8 :  result=R.string.weathercode_EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8; break;
        }
        return result;
    }
}

