/**
 * This file is part of Tiny24hWeatherForecastGermany.
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
import android.util.Log;

public class WeatherCodeContract {

    private WeatherCard weatherCard;
    private int start;
    private int stop;

    public final static int WEATHER_TODAY = 0;
    public final static int WEATHER_TOMORROW = 1;
    public final static int WEATHER_24H = 2;

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

    public final static int PARTLY_CLOUDY_DAY_SCATTERED_SHOWERS_LIGHT = 50;
    public final static int PARTLY_CLOUDY_NIGHT_SCATTERED_SHOWERS_LIGHT = 51;
    public final static int PARTLY_CLOUDY_DAY_SCATTERED_SNOW_SHOWERS_LIGHT = 52;
    public final static int PARTLY_CLOUDY_NIGHT_SCATTERED_SNOW_SHOWERS_LIGHT = 53;

    public final int PARTLY_CLOUDY_DAY_SCATTERED_SHOWERS = 54;
    public final int PARTLY_CLOUDY_NIGHT_SCATTERED_SHOWERS = 55;
    public final int PARTLY_CLOUDY_DAY_SCATTERED_SNOW_SHOWERS = 56;
    public final int PARTLY_CLOUDY_NIGHT_SCATTERED_SNOW_SHOWERS = 57;


    /**
     * Constructor defines first and last element that is considered.
     * @param weatherCard
     * @param start
     * @param stop
     */

    public WeatherCodeContract(WeatherCard weatherCard, int start, int stop){
        this.weatherCard = weatherCard;
        this.start = start;
        this.stop = stop;
    }

    public WeatherCodeContract(WeatherCard weatherCard, int period){
        this.weatherCard = weatherCard;
        int delimeter = weatherCard.getEndOfTodayPos();
        if (period==WEATHER_TODAY){
            this.start = 0;
            this.stop = delimeter;
        } else
            if (period == WEATHER_TOMORROW){
            this.start = delimeter;
            this.stop = 8;
        } else {
                this.start = 0;
                this.stop = 8;
            }
    }

    private int getAvaerage(int[] intarray){
        int elements = stop - start + 1;
        int sum = 0;
        for (int i=start; i<=stop; i++){
            sum = sum + intarray[i];
        }
        return sum / elements;
    }

    private boolean isDaytime(){
        // daytime = 6:00 - 18:00
        // result is taken from first entry

        int[] hours = weatherCard.getIntArray(weatherCard.uhrzeit,start,stop);
        if ((hours[start]>6) && (hours[start]<19)){
                return true;
            }
        return false;
    }

    // clear sky = clouds<2 at any time

    private boolean isSunny(){
        int[] bewoelkung = weatherCard.getIntArray(weatherCard.bewoelkung);
        boolean result = false;
        for (int i=start; i<=stop; i++) {
            if (bewoelkung[i]>1){
                return false;
            }
        }
        return true;
    }

    // fair = no rain, clouds < 4

    private boolean isFair(){
        int clouds = getAvaerage(weatherCard.getIntArray(weatherCard.bewoelkung));
        if ((noRain()) && (clouds<4)){
            return true;
        }
        return false;
    }

    // CLOUDY = clouds in average > 7

    private boolean isCloudy(){
        int clouds = getAvaerage(weatherCard.getIntArray(weatherCard.bewoelkung));
        if (clouds>7){
            return true;
        } else {
            return false;
        }
    }

    // PARTLY CLOUDY = clouds > 3 and < 7

    private boolean isPartlyCloudy(){
        int clouds = getAvaerage(weatherCard.getIntArray(weatherCard.bewoelkung));
        if ((clouds>3) && (clouds<7)){
            return true;
        } else {
            return false;
        }
    }

    // MOSTLY CLOUDY = clouds = 7

    private boolean isMostlyCloudy(){
        int clouds = getAvaerage(weatherCard.getIntArray(weatherCard.bewoelkung));
        if (clouds == 7) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isRain(){
        int[] rain = weatherCard.getIntArray(weatherCard.niederschlag);
        boolean result = false;
        for (int i=start; i<=stop; i++) {
            if (rain[i] > 0) {
                result = true;
            }
        }
        return result;
    }

    // RAIN = rain > 1 at any time

    private boolean isShowers(){
        int[] rain = weatherCard.getIntArray(weatherCard.niederschlag);
        for (int i=start; i<=stop; i++) {
            if (rain[i]>1){
                return true;
            }
        }
        return false;
    }

    // drizzle = rain 1-2 occurs at all times

    private boolean isDrizzle(){
        int[] rain = weatherCard.getIntArray(weatherCard.niederschlag);
        boolean result = true;
        for (int i=start; i<=stop; i++) {
            if (!((rain[i] > 0) && (rain[i] < 3))) {
                result = false;
            }
        }
        return result;
    }

    // SCATTERED SHOWERS = less than 50% of time points with rain
    // in case of only one item, the logic is different:
    // min showers = 0 , max showers > 0

    private boolean isScatteredShowers(){
        int[] rain = weatherCard.getIntArray(weatherCard.niederschlag);
        int[] min_rain = weatherCard.getIntArray(weatherCard.niederschlag_min);
        int[] max_rain = weatherCard.getIntArray(weatherCard.niederschlag_max);
        // this is the logic for analyzing more than one item
        if (start != stop){
            int counter = 0;
            for (int i=start; i<=stop; i++) {
                if (rain[i]>0){
                    counter++;
                }
            }
            if (counter==0) {
                return false;
            }
            double result = (stop-start)/counter;
            if (result<0.5){
                return true;
            }
            return false;
        } else {
            if ((min_rain[start] == 0) && (max_rain[start]>0)) {
                return true;
            } else {
                return false;
            }
        }
    }

    // PARTLY_CLOUDY_AND_SCATTERED_SHOWERS = partly cloudy (clouds average < 7) and scattered showers

    private boolean isPartlyCloudyAndScatteredShowers(){
        int clouds = getAvaerage(weatherCard.getIntArray(weatherCard.bewoelkung));
        if ((isScatteredShowers()) && (isPartlyCloudy())){
            return true;
        }
        return false;
    }

    // light rain = rain > 0 at least at on time point & max_rain<3 at all times

    private boolean lightRain(){
        int[] rain = weatherCard.getIntArray(weatherCard.niederschlag);
        int[] min_rain = weatherCard.getIntArray(weatherCard.niederschlag_min);
        int[] max_rain = weatherCard.getIntArray(weatherCard.niederschlag_max);
        boolean no_rain = true;
        boolean light_rain = true;
        for (int i=start; i<=stop; i++) {
            if (rain[i]>0){
                no_rain = false;
            }
            if (max_rain[i]>2){
                light_rain = false;
            }
        }
        if ((!no_rain) && (light_rain)){
                return true;
            }
        return false;
    }

    // IS PARTLY CLOUDY LIGHT SCATTERED SHOWERS = isPartlyCloudyAndScatteredShowers + drizzle

    private boolean isPartlyCloudyAndScatteredShowersLight(){
        if (isPartlyCloudyAndScatteredShowers() && lightRain()){
            return true;
        }
        return false;
    }


    // no rain

    private boolean noRain(){
        int[] rain = weatherCard.getIntArray(weatherCard.niederschlag);
        boolean result = true;
        for (int i=start; i<=stop; i++) {
            if (rain[i] > 0) {
                result = false;
            }
        }
        return result;
    }

    // has snow?

    private boolean isSnow() {
        boolean s = false;
        for (int i = start; i <= stop; i++) {
            if ((weatherCard.niederschlag[i].contains("S"))) {
                s = true;
            }
        }
        if (s && onlySnow()){
            return true;
        }
        return false;
    }

    // onlySnow = rain > 0 at any time, snow at any time, no rain at all times

    private boolean onlySnow(){
        int[] rain = weatherCard.getIntArray(weatherCard.niederschlag);
        boolean has_rain = false;
        boolean has_snow = false;
        boolean has_perception = false;
        for (int i=start; i<=stop; i++) {
            if ((weatherCard.niederschlag[i].contains("R"))) {
                has_rain = true;
            }
            if ((weatherCard.niederschlag[i].contains("S"))) {
                has_snow = true;
            }
            if (rain[i]>0){
                has_perception = true;
            }
        }
        if ((has_perception) && (has_snow) && (!has_rain)){
            return true;
        }
        return false;
    }

    // SNOW SHOWERS = isSnow & R > 4

    private boolean isSnowShowers() {
        int rain = getAvaerage(weatherCard.getIntArray(weatherCard.niederschlag));
        if ((rain>4) && (onlySnow())){
            return true;
        }
        return false;
    }

    // LIGHT SNOW SHOWERS   is light rain + only snow
    // has to be set before heavy snow!

    private boolean isLightSnow(){
        if (lightRain() && onlySnow()){
            return true;
        }
        return false;
    }

    // HEAVY SNOW   = only snow + average snow > 5

    private boolean isHeavySnow(){
        int snow = getAvaerage(weatherCard.getIntArray(weatherCard.niederschlag));
        if (onlySnow() && snow >5){
            return true;
        }

      return false;
    }


    // MIXED RAIN AND SNOW = R or S is present

    private boolean isMixedSnowAndRain(){
        boolean s = false;
        boolean r = false;
        for (int i=start; i<=stop; i++) {
            if ((weatherCard.niederschlag[i].contains("S"))) {
                s = true;
            }
            if ((weatherCard.niederschlag[i].contains("R"))) {
                r = true;
            }
        }
        if (r && s) {
            return true;
        }
        return false;
    }

    // SCATTERERED SNOW SHOWERS = scattered rain + S

    private boolean isScatteredSnowShowers(){
        if ((isScatteredShowers()) && onlySnow()){
            return true;
        }
        return false;
    }

    // PARTLY_CLOUDY_AND_SCATTERED_SHOWERS = partly cloudy (clouds average < 7) and scattered snow showers

    private boolean isPartlyCloudyAndScatteredSnowShowers(){
        int clouds = getAvaerage(weatherCard.getIntArray(weatherCard.bewoelkung));
        if ((isScatteredSnowShowers()) && (clouds<7)){
            return true;
        }
        return false;
    }

    // LIGHT SCATTERED SHOWERS = isPartlyCloudyAndScatteredShowers + drizzle

    private boolean isPartlyCloudyAndScatteredSnowShowersLight(){
        if (isPartlyCloudyAndScatteredSnowShowers() && lightRain()){
            return true;
        }
        return false;
    }


    // FREEZING DRIZZLE = at all times: temperature <= 0, rain, and rain < 3
    // this is a restrictive definition, should rarely appear as whole day result in widget

    private boolean isFreezingDrizzle(){
        int[] temp= weatherCard.getIntArray(weatherCard.lufttemperatur);
        int[] rain = weatherCard.getIntArray(weatherCard.niederschlag);
        boolean result = true;
        for (int i=start; i<=stop; i++) {
            if (!((weatherCard.niederschlag[i].contains("R")) && (rain[i]<3) && temp[i]<1)){
                result = false;
            }
        }
        return result;
    }

    // FREEZING RAIN  =  at all times: temperature <= 0, rain, and rain > 2

    private boolean isFreezingRain(){
        int[] temp = weatherCard.getIntArray(weatherCard.lufttemperatur);
        int[] rain = weatherCard.getIntArray(weatherCard.niederschlag);
        boolean result = true;
        Log.v("BOUNDS: ",start+"|"+stop);
        for (int i=start; i<=stop; i++) {
            if (!((weatherCard.niederschlag[i].contains("R")) && (rain[i]>2) && temp[i]<1)){
                result = false;
            }
        }
        return result;
    }

    // COLD = temp in average below 0° or lowest average below -5°

    private boolean isCold(){
        int temp = getAvaerage(weatherCard.getIntArray(weatherCard.lufttemperatur));
        if (temp<0){
            return true;
        }
        int tempmin = getAvaerage(weatherCard.getIntArray(weatherCard.lufttemperatur_min));
        if (tempmin < -5) {
            return true;
        }
        return false;
    }

    // HOT = at least 2 temperatures above 30°

    private boolean isHot(){
        int[] temp = weatherCard.getIntArray(weatherCard.lufttemperatur);
        int counter = 0;
        for (int i=start; i<=stop; i++) {
            if (temp[i]>30) {
                counter++;
            }
        }
        if (counter>1){
            return true;
        }
        return false;
    }

    // WINDY = wind > 40 km/h

    private boolean isWindy() {
        int wind = getAvaerage(weatherCard.getIntArray(weatherCard.wind));
        if (wind>40) {
            return true;
        }
        return false;
    }

    // blustery = wind > 62 km/h at any time

    private boolean isBlustery(){
        int[] boeen = weatherCard.getIntArray(weatherCard.boeen);
        for (int i=start; i<=stop; i++) {
            if (boeen[i]>62){
                return true;
            }
        }
        return false;
    }

    // snow flurries = snow + isBlustery at the same time

    private boolean isSnowFlurries(){
        int[] boeen = weatherCard.getIntArray(weatherCard.boeen);
        for (int i=start; i<=stop; i++) {
            if ((boeen[i]>62) && (weatherCard.niederschlag[i].contains("S"))){
                return true;
            }
        }
        return false;
    }

    // HURRICANE = flurries/blusts above 102 km/h at any time

    private boolean isHurricane(){
        int[] boeen = weatherCard.getIntArray(weatherCard.boeen);
        for (int i=start; i<=stop; i++) {
            if (boeen[i] > 102) {
                return true;
            }
        }
        return false;
    }

    public int getWeatherConditionDrawableResource(int weathercondition){
        int resource = 0;
        switch (weathercondition){
            case BLUSTERY: resource = R.drawable.blustery; break;
            case CLEAR_NIGHT: resource = R.drawable.clear_night; break;
            case CLOUDY: resource = R.drawable.cloudy; break;
            case COLD: resource = R.drawable.cold; break;
            case DRIZZLE: resource = R.drawable.drizzle; break;
            case FAIR_DAY: resource = R.drawable.fair_day; break;
            case FAIR_NIGHT: resource = R.drawable.fair_night; break;
            case FREEZING_DRIZZLE: resource = R.drawable.freezing_drizzle; break;
            case FREEZING_RAIN: resource = R.drawable.freezing_rain; break;
            case HEAVY_SNOW: resource = R.drawable.heavy_snow; break;
            case HOT: resource = R.drawable.hot; break;
            case HURRICANE: resource = R.drawable.hurricane; break;
            case LIGHT_SNOW_SHOWERS: resource = R.drawable.light_snow_showers; break;
            case MIXED_RAIN_AND_SNOW: resource = R.drawable.mixed_rain_and_snow; break;
            case MOSTLY_CLOUDY_DAY: resource = R.drawable.mostly_cloudy_day; break;
            case MOSTLY_CLOUDY_NIGHT: resource = R.drawable.mostly_cloudy_night; break;
            case NOT_AVAILABLE: resource = 0; break;
            case PARTLY_CLOUDY_DAY: resource = R.drawable.partly_cloudy_day; break;
            case PARTLY_CLOUDY_NIGHT: resource = R.drawable.partly_cloudy_night; break;
            case SCATTERED_SHOWERS: resource = R.drawable.scattered_showers; break;
            case SCATTERED_SNOW_SHOWERS: resource = R.drawable.scattered_snow_showers; break;
            case SHOWERS: resource = R.drawable.showers; break;
            case SNOW: resource = R.drawable.snow; break;
            case SNOW_FLURRIES: resource = R.drawable.snow_flurries; break;
            case SNOW_SHOWERS: resource = R.drawable.snow_showers; break;
            case SUNNY: resource = R.drawable.sunny; break;
            case WINDY: resource = R.drawable.windy; break;
            case PARTLY_CLOUDY_DAY_SCATTERED_SHOWERS: resource = R.drawable.partly_cloudy_day_scattered_showers; break;
            case PARTLY_CLOUDY_NIGHT_SCATTERED_SHOWERS: resource = R.drawable.partly_cloudy_night_scattered_showers; break;
            case PARTLY_CLOUDY_DAY_SCATTERED_SNOW_SHOWERS: resource = R.drawable.partly_cloudy_day_scattered_snow; break;
            case PARTLY_CLOUDY_NIGHT_SCATTERED_SNOW_SHOWERS: resource = R.drawable.partly_cloudy_night_scattered_snow; break;
            case PARTLY_CLOUDY_DAY_SCATTERED_SHOWERS_LIGHT: resource = R.drawable.partly_cloudy_day_scattered_showers_light; break;
            case PARTLY_CLOUDY_NIGHT_SCATTERED_SHOWERS_LIGHT: resource = R.drawable.partly_cloudy_night_scattered_showers_light; break;
            case PARTLY_CLOUDY_DAY_SCATTERED_SNOW_SHOWERS_LIGHT: resource = R.drawable.partly_cloudy_day_scattered_snow_light; break;
            case PARTLY_CLOUDY_NIGHT_SCATTERED_SNOW_SHOWERS_LIGHT: resource = R.drawable.partly_cloudy_night_scattered_snow_light; break;
        }
        return resource;
    }

    public int getWeatherConditionTextResource(int weathercondition){
        int resource = 0;
        switch (weathercondition){
            case BLUSTERY: resource = R.string.weathercode_blustery; break;
            case CLEAR_NIGHT: resource = R.string.weathercode_clear_night; break;
            case CLOUDY: resource = R.string.weathercode_cloudy; break;
            case COLD: resource = R.string.weathercode_cold; break;
            case DRIZZLE: resource = R.string.weathercode_drizzle; break;
            case FAIR_DAY: resource = R.string.weathercode_fair_day; break;
            case FAIR_NIGHT: resource = R.string.weathercode_fair_night; break;
            case FREEZING_DRIZZLE: resource = R.string.weathercode_freezing_drizzle; break;
            case FREEZING_RAIN: resource = R.string.weathercode_freezing_rain; break;
            case HEAVY_SNOW: resource = R.string.weathercode_heavy_snow; break;
            case HOT: resource = R.string.weathercode_hot; break;
            case HURRICANE: resource = R.string.weathercode_hurricane; break;
            case LIGHT_SNOW_SHOWERS: resource = R.string.weathercode_light_snow_showers; break;
            case MIXED_RAIN_AND_SNOW: resource = R.string.weathercode_mixed_rain_and_snow; break;
            case MOSTLY_CLOUDY_DAY: resource = R.string.weathercode_mostly_cloudy_day; break;
            case MOSTLY_CLOUDY_NIGHT: resource = R.string.weathercode_mostly_cloudy_night; break;
            case NOT_AVAILABLE: resource = R.string.weathercode_not_available; break;
            case PARTLY_CLOUDY_DAY: resource = R.string.weathercode_partly_cloudy_day; break;
            case PARTLY_CLOUDY_NIGHT: resource = R.string.weathercode_partly_cloudy_night; break;
            case SCATTERED_SHOWERS: resource = R.string.weathercode_scattered_showers; break;
            case SCATTERED_SNOW_SHOWERS: resource = R.string.weathercode_scattered_snow_showers; break;
            case SHOWERS: resource = R.string.weathercode_showers; break;
            case SNOW: resource = R.string.weathercode_snow; break;
            case SNOW_FLURRIES: resource = R.string.weathercode_snow_flurries; break;
            case SNOW_SHOWERS: resource = R.string.weathercode_snow_showers; break;
            case SUNNY: resource = R.string.weathercode_sunny; break;
            case WINDY: resource = R.string.weathercode_windy; break;
            case PARTLY_CLOUDY_DAY_SCATTERED_SHOWERS_LIGHT: resource = R.string.weathercode_partly_cloudy_day_scattered_showers_light; break;
            case PARTLY_CLOUDY_NIGHT_SCATTERED_SHOWERS_LIGHT: resource = R.string.weathercode_partly_cloudy_night_scattered_showers_light; break;
            case PARTLY_CLOUDY_DAY_SCATTERED_SNOW_SHOWERS_LIGHT: resource = R.string.weathercode_partly_cloudy_day_scattered_snow_showers_light; break;
            case PARTLY_CLOUDY_NIGHT_SCATTERED_SNOW_SHOWERS_LIGHT: resource = R.string.weathercode_partly_cloudy_night_scattered_snow_showers_light; break;
        }
        return resource;
    }

    public String getWeatherConditionText(Context context, int weathercondition){
        int resource = getWeatherConditionTextResource(weathercondition);
        String s = context.getResources().getString(resource);
        return s;
    }

    public Drawable getWeatherConditionDrawable(Context context, int weathercondition){
        Drawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(getWeatherConditionDrawableResource(weathercondition),null);
        } else {
            drawable = context.getResources().getDrawable(weathercondition);
        }
        return drawable;
    }

    public int getWeatherCondition(){
        // int weatherCondition = NOT_AVAILABLE;
        int weatherCondition = FAIR_DAY;
        // *** BASIC WEATHER CONDITIONS ***
        // FAIR
        if (isFair()){
            if (isDaytime()){
                weatherCondition = FAIR_DAY;
            } else {
                weatherCondition = FAIR_NIGHT;
            }
        }
        // SUNNY
        if (isSunny()){
            if (isDaytime()){
                weatherCondition = SUNNY;
            } else {
                weatherCondition = CLEAR_NIGHT;
            }
        }
        // CLOUDY
        if (isCloudy()){
            weatherCondition = CLOUDY;
        }
        // PARTLY CLOUDY
        if (isPartlyCloudy()){
            if (isDaytime()){
                weatherCondition = PARTLY_CLOUDY_DAY;
            } else {
                weatherCondition = PARTLY_CLOUDY_NIGHT;
            }
        }
        // MOSTLY CLOUDY
        if (isMostlyCloudy()){
            if (isDaytime()){
                weatherCondition = MOSTLY_CLOUDY_DAY;
            } else {
                weatherCondition = MOSTLY_CLOUDY_NIGHT;
            }
        }
        // *** RAIN CONDITIONS ***
        // they have to follow the basic weather conditions, must precede snow conditions
        // SHOWERS
        if (isShowers()){
            weatherCondition = SHOWERS;
        }
        // DRIZZLE
        if (isDrizzle()){
            weatherCondition = DRIZZLE;
        }
        // SCATTERED SHOWERS
        if (isScatteredShowers()){
            weatherCondition = SCATTERED_SHOWERS;
        }
        // PARTLY CLOUDY, SCATTERED SHOWERS
        if (isPartlyCloudyAndScatteredShowers()){
            if (isDaytime()){
                weatherCondition = PARTLY_CLOUDY_DAY_SCATTERED_SHOWERS;
            } else {
                weatherCondition = PARTLY_CLOUDY_NIGHT_SCATTERED_SHOWERS;
            }
        }
        // PARTLY CLOUDY, LIGHT SCATTERED SHOWERS
        // has to come after partly cloudy, scattered showers as this is more specific
        if (isPartlyCloudyAndScatteredShowersLight()){
            if (isDaytime()){
                weatherCondition = PARTLY_CLOUDY_DAY_SCATTERED_SHOWERS_LIGHT;
            } else {
                weatherCondition = PARTLY_CLOUDY_NIGHT_SCATTERED_SHOWERS_LIGHT;
            }
        }
        // *** SNOW CONDITIONS ***
        // they have to follow the rain conditions
        // SNOW
        if (isSnow()){
            weatherCondition = SNOW;
        }
        // SNOW SHOWERS
        if (isSnowShowers()){
            weatherCondition = SNOW_SHOWERS;
        }
        // MIXED RAIN & SNOW
        if (isMixedSnowAndRain()){
            weatherCondition = MIXED_RAIN_AND_SNOW;
        }
        // LIGHT SNOW
        if (isLightSnow()) {
            weatherCondition = LIGHT_SNOW_SHOWERS;
        }
        // HEAVY SNOW
        if (isHeavySnow()){
            weatherCondition = HEAVY_SNOW;
        }
        // SCATTERED SNOW SHOWERS, must be later than scattered showers
        if (isScatteredSnowShowers()){
            weatherCondition = SCATTERED_SNOW_SHOWERS;
        }
        // PARTLY CLOUDY, SCATTERED SNOW SHOWERS
        if (isPartlyCloudyAndScatteredSnowShowers()){
            if (isDaytime()){
                weatherCondition = PARTLY_CLOUDY_DAY_SCATTERED_SNOW_SHOWERS;
            } else {
                weatherCondition = PARTLY_CLOUDY_NIGHT_SCATTERED_SNOW_SHOWERS;
            }
        }
        // PARTLY CLOUDY, LIGHT SCATTERED SNOW SHOWERS
        // has to come after partly cloudy, scattered snow showers as this is more specific
        if (isPartlyCloudyAndScatteredSnowShowersLight()){
            if (isDaytime()){
                weatherCondition = PARTLY_CLOUDY_DAY_SCATTERED_SNOW_SHOWERS_LIGHT;
            } else {
                weatherCondition = PARTLY_CLOUDY_NIGHT_SCATTERED_SNOW_SHOWERS_LIGHT;
            }
        }
        // FREEZING DRIZZLE
        if (isFreezingDrizzle()){
            weatherCondition = FREEZING_DRIZZLE;
        }
        // FREEZING RAIN
        if (isFreezingRain()){
            weatherCondition = FREEZING_RAIN;
        }
        // COLD
        if (isCold()){
            weatherCondition = COLD;
        }
        //IS HOT
        if (isHot()) {
            weatherCondition = HOT;
        }
        // *** SPECIAL WIND CONDITIONS ***
        // WINDY
        if (isWindy()){
            weatherCondition = WINDY;
        }
        // BLUSTERY
        if (isBlustery()){
            weatherCondition = BLUSTERY;
        }
        // SNOW FLURRIES has to follow blustery, as more specific
        if (isSnowFlurries()){
            weatherCondition = SNOW_FLURRIES;
        }
        // HURRICANE
        if (isHurricane()){
            weatherCondition = HURRICANE;
        }
        return weatherCondition;
    }
}
