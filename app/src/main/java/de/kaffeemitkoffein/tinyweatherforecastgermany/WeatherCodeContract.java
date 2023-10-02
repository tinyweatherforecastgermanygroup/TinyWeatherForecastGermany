/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021, 2022, 2023 Pawel Dube
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

import android.annotation.SuppressLint;
import android.content.Context;

public final class  WeatherCodeContract {
    public final static int NOT_AVAILABLE = 999;
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
    public final static int SLIGHT_SNOWFALL_CONTINUOUS = 71;
    public final static int MODERATE_OR_HEAVY_RAIN_AND_SNOW = 69;
    public final static int SLIGHT_RAIN_AND_SNOW = 68;
    public final static int HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS = 55;
    public final static int MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS = 53;
    public final static int SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS = 51;
    public final static int HEAVY_RAIN_NOT_FREEZING_CONTINUOUS = 65;
    public final static int MODERATE_RAIN_NOT_FREEZING_CONTINUOUS = 63;
    public final static int SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS = 61;
    public final static int ICE_FOG_SKY_NOT_RECOGNIZABLE = 49;
    public final static int FOG_SKY_NOT_RECOGNIZABLE = 45;
    public final static int EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8 = 03;
    public final static int EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8 = 02;
    public final static int EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8 = 01;
    public final static int EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8 = 00;

    public static final class LineageOsWeatherContract {
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

    private int getLineageOSWeatherCode(int weathercode) {
        int result = LineageOsWeatherContract.NOT_AVAILABLE;
        switch (weathercode) {
            case SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW:
                result = LineageOsWeatherContract.THUNDERSTORMS;
                break;
            case DRIZZLE_FREEZING_MODERATE_OR_HEAVY:
                result = LineageOsWeatherContract.FREEZING_DRIZZLE;
                break;
            case DRIZZLE_FREEZING_SLIGHT:
                result = LineageOsWeatherContract.FREEZING_DRIZZLE;
                break;
            case RAIN_FREEZING_MODERATE_OR_HEAVY:
                result = LineageOsWeatherContract.FREEZING_RAIN;
                break;
            case RAIN_FREEZING_SLIGHT:
                result = LineageOsWeatherContract.FREEZING_RAIN;
                break;
            case SNOW_SHOWERS_MODERATE_OR_HEAVY:
                result = LineageOsWeatherContract.SNOW;
                break;
            case SNOW_SHOWERS_SLIGHT:
                result = LineageOsWeatherContract.LIGHT_SNOW_SHOWERS;
                break;
            case SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY:
                result = LineageOsWeatherContract.MIXED_RAIN_AND_SNOW;
                break;
            case SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT:
                result = LineageOsWeatherContract.MIXED_RAIN_AND_SNOW;
                break;
            case EXTREMELY_HEAVY_RAIN_SHOWER:
                result = LineageOsWeatherContract.SHOWERS;
                break;
            case MODERATE_OR_HEAVY_RAIN_SHOWERS:
                result = LineageOsWeatherContract.SHOWERS;
                break;
            case SLIGHT_RAIN_SHOWER:
                result = LineageOsWeatherContract.SCATTERED_SHOWERS;
                break;
            case HEAVY_SNOWFALL_CONTINUOUS:
                result = LineageOsWeatherContract.HEAVY_SNOW;
                break;
            case MODERATE_SNOWFALL_CONTINUOUS:
                result = LineageOsWeatherContract.SNOW;
                break;
            case SLIGHT_SNOWFALL_CONTINUOUS:
                result = LineageOsWeatherContract.LIGHT_SNOW_SHOWERS;
                break;
            case MODERATE_OR_HEAVY_RAIN_AND_SNOW:
                result = LineageOsWeatherContract.MIXED_RAIN_AND_SNOW;
                break;
            case SLIGHT_RAIN_AND_SNOW:
                result = LineageOsWeatherContract.MIXED_RAIN_AND_SNOW;
                break;
            case HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS:
                result = LineageOsWeatherContract.DRIZZLE;
                break;
            case MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS:
                result = LineageOsWeatherContract.DRIZZLE;
                break;
            case SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS:
                result = LineageOsWeatherContract.DRIZZLE;
                break;
            case HEAVY_RAIN_NOT_FREEZING_CONTINUOUS:
                result = LineageOsWeatherContract.SHOWERS;
                break;
            case MODERATE_RAIN_NOT_FREEZING_CONTINUOUS:
                result = LineageOsWeatherContract.SHOWERS;
                break;
            case SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS:
                result = LineageOsWeatherContract.SCATTERED_SHOWERS;
                break;
            case ICE_FOG_SKY_NOT_RECOGNIZABLE:
                result = LineageOsWeatherContract.FOGGY;
                break;
            case FOG_SKY_NOT_RECOGNIZABLE:
                result = LineageOsWeatherContract.FOGGY;
                break;
            case EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8:
                result = LineageOsWeatherContract.CLOUDY;
                break;
            case EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8:
                result = LineageOsWeatherContract.CLOUDY;
                break;
            case EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8:
                result = LineageOsWeatherContract.PARTLY_CLOUDY;
                break;
            case EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8:
                result = LineageOsWeatherContract.SUNNY;
                break;
        }
        return result;
    }

    public static final class OpenWeatherContract{
        public final static int THUNDERSTORM = 211;
        public final static int FREEZING_RAIN = 511;
        public final static int HEAVY_SHOWER_SNOW = 622;
        public final static int LIGHT_SHOWER_SNOW = 620;
        public final static int RAIN_AND_SNOW = 616;
        public final static int LIGHT_RAIN_AND_SNOW = 615;
        public final static int HEAVY_INTENSITY_SHOWER_RAIN = 522;
        public final static int EXTREME_RAIN = 504;
        public final static int LIGHT_INTENSITY_SHOWER_RAIN = 520;
        public final static int HEAVY_SNOW = 602;
        public final static int SNOW = 601;
        public final static int LIGHT_SNOW = 600;
        public final static int HEAVY_INTENSITY_DRIZZE = 302;
        public final static int DRIZZLE = 301;
        public final static int LIGHT_INTENSITY_DRIZZLE = 300;
        public final static int HEAVY_INTENSITY_RAIN = 502;
        public final static int MODERATE_RAIN = 501;
        public final static int LIGHT_RAIN = 500;
        public final static int FOG = 741;
        public final static int OVERCAST_CLOUDS = 804;
        public final static int BROKEN_CLOUDS = 803;
        public final static int SCATTERED_CLOUDS = 802;
        public final static int FEW_CLOUDS = 801;
        public final static int CLEAR_SKY = 800;
        public final static int UNKNOWN = 3200;
    }

    public static int translateToOpenWeatherCode(int code){
        int result = OpenWeatherContract.UNKNOWN;
        switch (code) {
            case SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW:
                result = OpenWeatherContract.THUNDERSTORM;
                break;
            case DRIZZLE_FREEZING_MODERATE_OR_HEAVY:
            case RAIN_FREEZING_SLIGHT:
            case RAIN_FREEZING_MODERATE_OR_HEAVY:
            case DRIZZLE_FREEZING_SLIGHT:
                result = OpenWeatherContract.FREEZING_RAIN;
                break;
            case SNOW_SHOWERS_MODERATE_OR_HEAVY:
                result = OpenWeatherContract.HEAVY_SHOWER_SNOW;
                break;
            case SNOW_SHOWERS_SLIGHT:
                result = OpenWeatherContract.LIGHT_SHOWER_SNOW;
                break;
            case SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY:
            case MODERATE_OR_HEAVY_RAIN_AND_SNOW:
                result = OpenWeatherContract.RAIN_AND_SNOW;
                break;
            case SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT:
            case SLIGHT_RAIN_AND_SNOW:
                result = OpenWeatherContract.LIGHT_RAIN_AND_SNOW;
                break;
            case EXTREMELY_HEAVY_RAIN_SHOWER:
                result = OpenWeatherContract.EXTREME_RAIN;
                break;
            case MODERATE_OR_HEAVY_RAIN_SHOWERS:
                result = OpenWeatherContract.HEAVY_INTENSITY_SHOWER_RAIN;
                break;
            case SLIGHT_RAIN_SHOWER:
                result = OpenWeatherContract.LIGHT_INTENSITY_SHOWER_RAIN;
                break;
            case HEAVY_SNOWFALL_CONTINUOUS:
                result = OpenWeatherContract.HEAVY_SNOW;
                break;
            case MODERATE_SNOWFALL_CONTINUOUS:
                result = OpenWeatherContract.SNOW;
                break;
            case SLIGHT_SNOWFALL_CONTINUOUS:
                result = OpenWeatherContract.LIGHT_SNOW;
                break;
            case HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS:
                result = OpenWeatherContract.HEAVY_INTENSITY_DRIZZE;
                break;
            case MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS:
                result = OpenWeatherContract.DRIZZLE;
                break;
            case SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS:
                result = OpenWeatherContract.LIGHT_INTENSITY_DRIZZLE;
                break;
            case HEAVY_RAIN_NOT_FREEZING_CONTINUOUS:
                result = OpenWeatherContract.HEAVY_INTENSITY_RAIN;
                break;
            case MODERATE_RAIN_NOT_FREEZING_CONTINUOUS:
                result = OpenWeatherContract.MODERATE_RAIN;
                break;
            case SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS:
                result = OpenWeatherContract.LIGHT_RAIN;
                break;
            case ICE_FOG_SKY_NOT_RECOGNIZABLE:
            case FOG_SKY_NOT_RECOGNIZABLE:
                result = OpenWeatherContract.FOG;
                break;
            case EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8:
                result = OpenWeatherContract.OVERCAST_CLOUDS;
                break;
            case EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8:
                result = OpenWeatherContract.BROKEN_CLOUDS;
                break;
            case EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8:
                result = OpenWeatherContract.SCATTERED_CLOUDS;
                break;
            case EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8:
                result = OpenWeatherContract.CLEAR_SKY;
                break;
        }
        return result;
    }

    public static String getWeatherConditionText(Context context, int weathercondition) {
        int resource = getWeatherConditionTextResource(weathercondition);
        String s = context.getResources().getString(resource);
        return s;
    }

    public static int getWeatherConditionTextResource(int weathercondition) {
        int result = R.string.weathercode_UNKNOWN;
        switch (weathercondition) {
            case SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW:
                result = R.string.weathercode_SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW;
                break;
            case DRIZZLE_FREEZING_MODERATE_OR_HEAVY:
                result = R.string.weathercode_DRIZZLE_FREEZING_MODERATE_OR_HEAVY;
                break;
            case DRIZZLE_FREEZING_SLIGHT:
                result = R.string.weathercode_DRIZZLE_FREEZING_SLIGHT;
                break;
            case RAIN_FREEZING_MODERATE_OR_HEAVY:
                result = R.string.weathercode_RAIN_FREEZING_MODERATE_OR_HEAVY;
                break;
            case RAIN_FREEZING_SLIGHT:
                result = R.string.weathercode_RAIN_FREEZING_SLIGHT;
                break;
            case SNOW_SHOWERS_MODERATE_OR_HEAVY:
                result = R.string.weathercode_SNOW_SHOWERS_MODERATE_OR_HEAVY;
                break;
            case SNOW_SHOWERS_SLIGHT:
                result = R.string.weathercode_SNOW_SHOWERS_SLIGHT;
                break;
            case SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY:
                result = R.string.weathercode_SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY;
                break;
            case SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT:
                result = R.string.weathercode_SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT;
                break;
            case EXTREMELY_HEAVY_RAIN_SHOWER:
                result = R.string.weathercode_EXTREMELY_HEAVY_RAIN_SHOWER;
                break;
            case MODERATE_OR_HEAVY_RAIN_SHOWERS:
                result = R.string.weathercode_MODERATE_OR_HEAVY_RAIN_SHOWERS;
                break;
            case SLIGHT_RAIN_SHOWER:
                result = R.string.weathercode_SLIGHT_RAIN_SHOWER;
                break;
            case HEAVY_SNOWFALL_CONTINUOUS:
                result = R.string.weathercode_HEAVY_SNOWFALL_CONTINUOUS;
                break;
            case MODERATE_SNOWFALL_CONTINUOUS:
                result = R.string.weathercode_MODERATE_SNOWFALL_CONTINUOUS;
                break;
            case SLIGHT_SNOWFALL_CONTINUOUS:
                result = R.string.weathercode_SLIGHT_SNOWFALL_CONTINUOUS;
                break;
            case MODERATE_OR_HEAVY_RAIN_AND_SNOW:
                result = R.string.weathercode_MODERATE_OR_HEAVY_RAIN_AND_SNOW;
                break;
            case SLIGHT_RAIN_AND_SNOW:
                result = R.string.weathercode_SLIGHT_RAIN_AND_SNOW;
                break;
            case HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS:
                result = R.string.weathercode_HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS;
                break;
            case MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS:
                result = R.string.weathercode_MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS;
                break;
            case SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS:
                result = R.string.weathercode_SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS;
                break;
            case HEAVY_RAIN_NOT_FREEZING_CONTINUOUS:
                result = R.string.weathercode_HEAVY_RAIN_NOT_FREEZING_CONTINUOUS;
                break;
            case MODERATE_RAIN_NOT_FREEZING_CONTINUOUS:
                result = R.string.weathercode_MODERATE_RAIN_NOT_FREEZING_CONTINUOUS;
                break;
            case SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS:
                result = R.string.weathercode_SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS;
                break;
            case ICE_FOG_SKY_NOT_RECOGNIZABLE:
                result = R.string.weathercode_ICE_FOG_SKY_NOT_RECOGNIZABLE;
                break;
            case FOG_SKY_NOT_RECOGNIZABLE:
                result = R.string.weathercode_FOG_SKY_NOT_RECOGNIZABLE;
                break;
            case EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8:
                result = R.string.weathercode_EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8;
                break;
            case EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8:
                result = R.string.weathercode_EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8;
                break;
            case EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8:
                result = R.string.weathercode_EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8;
                break;
            case EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8:
                result = R.string.weathercode_EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8;
                break;
        }
        return result;
    }

    public static boolean hasSufficientDataForIconCalculation(Weather.WeatherInfo weatherInfo) {
        if ((weatherInfo.hasTemperature()) &&
                (weatherInfo.hasProbPrecipitation()) &&
                (weatherInfo.hasClouds())) {
            return true;
        }
        return false;
    }

    public static int calculateCustomWeatherconditionFromData(Weather.WeatherInfo weatherInfo) {
        final int THRESHOLD_CLOUDS_FOR_SHOWERS = 80;
        final int THRESHOLD_PROB_FOR_RAIN      = 20;
        if (!hasSufficientDataForIconCalculation(weatherInfo)) {
            return NOT_AVAILABLE;
        }
        // sunny day is the standard condition with the lowest priority
        /*
         * cloud conditions
         */
        int condition = EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8;
        if (weatherInfo.getClouds() > 12) {
            condition = EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8;
        }
        if (weatherInfo.getClouds() > 56) {
            condition = EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8;
        }
        if (weatherInfo.getClouds() > 87) {
            condition = EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8;
        }
        /*
         * fog conditions
         */
        if (weatherInfo.hasProbFog()) {
            if (weatherInfo.getProbFog() > 30) {
                condition = FOG_SKY_NOT_RECOGNIZABLE;
                if (weatherInfo.getTemperatureInCelsius()<0){
                    condition = ICE_FOG_SKY_NOT_RECOGNIZABLE;
                }
            }
        } else {
            if (weatherInfo.hasVisibility()) {
                if (weatherInfo.getVisibilityInMetres() < 2000) {
                    condition = FOG_SKY_NOT_RECOGNIZABLE;
                }
                if (weatherInfo.getTemperatureInCelsius()<0){
                    condition = ICE_FOG_SKY_NOT_RECOGNIZABLE;
                }
            }
        }
        /*
         * continuous rain conditions.
         */
        // first determine if we have a rain condition at all
        boolean rain_condition = false;
        if (weatherInfo.hasPrecipitation()){
            if (weatherInfo.getPrecipitation()>0){
                rain_condition = true;
            }
        } else {
            if (weatherInfo.getProbPrecipitation()>THRESHOLD_PROB_FOR_RAIN){
                rain_condition = true;
            }
        }
        if (rain_condition){
            // is it a continuous rain condition?
            if (weatherInfo.getClouds()>THRESHOLD_CLOUDS_FOR_SHOWERS){
                // >= 50 in 1h = very strong showers
                if (!weatherInfo.hasPrecipitation()){
                    // no details about precipitaition known, we simply set the moderate condition
                    condition = MODERATE_RAIN_NOT_FREEZING_CONTINUOUS;
                } else {
                    // we have details about precipitation
                    // >= 50 in 1h = very strong showers
                    condition = EXTREMELY_HEAVY_RAIN_SHOWER;
                    if (weatherInfo.getPrecipitation()<50){
                        condition = MODERATE_OR_HEAVY_RAIN_SHOWERS;
                    }
                    if (weatherInfo.getPrecipitation()<10){
                        condition = MODERATE_RAIN_NOT_FREEZING_CONTINUOUS;
                    }
                    if (weatherInfo.getPrecipitation()<2.5){
                        condition = SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS;
                    }
                }
            }
        }
        /*
         * continuous drizzle conditions
         */
        if (weatherInfo.hasProbDrizzle()){
            if (weatherInfo.getProbDrizzle()>THRESHOLD_PROB_FOR_RAIN){
                if (!weatherInfo.hasPrecipitation()){
                    // no details about precipitaition known, we simply set the moderate condition
                    condition = MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS;
                } else {
                    condition = HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS;
                    if (weatherInfo.getPrecipitation()<10){
                        condition = MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS;
                    }
                    if (weatherInfo.getPrecipitation()<2.5){
                        condition = SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS;
                    }
                }
            }
        }
        /*
         * continuous mixed snow & rain conditions
         */
        if (weatherInfo.hasProbPrecipitation() && (weatherInfo.hasProbSolidPrecipitation()) && weatherInfo.hasTemperature()){
            if ((weatherInfo.getProbSolidPrecipitation()>THRESHOLD_PROB_FOR_RAIN) && (weatherInfo.getProbPrecipitation()>THRESHOLD_PROB_FOR_RAIN) && (weatherInfo.getTemperatureInCelsiusInt()<0)){
                condition = SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY;
                if (weatherInfo.hasPrecipitation()){
                    if (weatherInfo.getPrecipitation()<10){
                        condition = SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT;
                    }
                }
            }
        }
        /*
         * continous snow conditions
         */
        boolean snow_condition = false;
        if (rain_condition) {
            if (weatherInfo.getTemperatureInCelsius()<0){
                snow_condition = true;
            }
        }
        if ((snow_condition) && (weatherInfo.getClouds()>THRESHOLD_CLOUDS_FOR_SHOWERS)){
            if (!weatherInfo.hasPrecipitation()) {
                // no details about precipitaition known, we simply set the moderate condition
                condition = MODERATE_SNOWFALL_CONTINUOUS;
            } else {
                // > 5 mm = heavy snow
                condition = HEAVY_SNOWFALL_CONTINUOUS;
                // < 5mm = moderate snow
                if (weatherInfo.getPrecipitation() < 5) {
                    condition = MODERATE_SNOWFALL_CONTINUOUS;
                }
                // <1.0 = light snow
                if (weatherInfo.getPrecipitation() < 1) {
                    condition = SLIGHT_SNOWFALL_CONTINUOUS;
                }
            }
        }
        /*
         * rain shower conditions
         */
        if ((rain_condition) && (weatherInfo.getClouds()<=THRESHOLD_CLOUDS_FOR_SHOWERS)) {
            if (!weatherInfo.hasPrecipitation()) {
                // no details about precipitaition known, we simply set the moderate condition
                condition = MODERATE_OR_HEAVY_RAIN_SHOWERS;
            } else {
                condition = EXTREMELY_HEAVY_RAIN_SHOWER;
                if (weatherInfo.getPrecipitation() < 50) {
                    condition = MODERATE_OR_HEAVY_RAIN_SHOWERS;
                }
                if (weatherInfo.getPrecipitation() < 2.5) {
                    condition = SLIGHT_RAIN_SHOWER;
                }
            }
        }
        /*
         * mixed rain & snow showers
         */
        if (weatherInfo.hasProbPrecipitation() && (weatherInfo.hasProbSolidPrecipitation()) && weatherInfo.hasTemperature()){
            if ((weatherInfo.getProbSolidPrecipitation()<=THRESHOLD_PROB_FOR_RAIN) && (weatherInfo.getProbPrecipitation()>THRESHOLD_PROB_FOR_RAIN) && (weatherInfo.getTemperatureInCelsiusInt()<0)){
                condition = SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY;
                if (weatherInfo.hasPrecipitation()){
                    if (weatherInfo.getPrecipitation()<10){
                        condition = SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT;
                    }
                }
            }
        }
        /*
         * snow shower conditions
         */
        if ((snow_condition) && (weatherInfo.getClouds()<THRESHOLD_CLOUDS_FOR_SHOWERS)) {
            if (!weatherInfo.hasPrecipitation()) {
                // no details about precipitaition known, we simply set the moderate condition
                condition = SNOW_SHOWERS_MODERATE_OR_HEAVY;
            } else {
                if (weatherInfo.getPrecipitation() < 1) {
                    condition = SNOW_SHOWERS_SLIGHT;
                }
            }
        }
        /*
         * freezing rain conditions
         */
        if (weatherInfo.hasProbFreezingRain()) {
            if (weatherInfo.getProbFreezingRain() > 0) {
                condition = RAIN_FREEZING_SLIGHT;
                if (weatherInfo.hasPrecipitation()) {
                    if (weatherInfo.getProbPrecipitation() >= 10) {
                        condition = RAIN_FREEZING_MODERATE_OR_HEAVY;
                    }
                }
            }
        }
        /*
         * thunderstorms
         */
        if (weatherInfo.hasProbThunderstorms()){
            if (weatherInfo.getProbThunderStorms()>=5){
                condition = SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW;
            }
        }
        return condition;
    }

}


