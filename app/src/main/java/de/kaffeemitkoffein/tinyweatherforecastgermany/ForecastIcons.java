/*
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
import android.graphics.*;
import android.widget.ImageView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ForecastIcons {

    public static class Holidays{

        public static boolean isChristmas(long time){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            if ((calendar.get(Calendar.MONTH)==Calendar.DECEMBER) && (calendar.get(Calendar.DAY_OF_MONTH)==24)){
                return true;
            }
            if ((calendar.get(Calendar.MONTH)==Calendar.DECEMBER) && (calendar.get(Calendar.DAY_OF_MONTH)==25)){
                return true;
            }
            if ((calendar.get(Calendar.MONTH)==Calendar.DECEMBER) && (calendar.get(Calendar.DAY_OF_MONTH)==26)){
                return true;
            }
            return false;
        }

        public static boolean isHalloween(long time){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            if ((calendar.get(Calendar.MONTH)==Calendar.OCTOBER) && (calendar.get(Calendar.DAY_OF_MONTH)==31) && (calendar.get(Calendar.HOUR_OF_DAY)>12)){
                return true;
            }
            // intervals ending with midnight (=next day) are also Halloween
            if ((calendar.get(Calendar.MONTH)==Calendar.NOVEMBER) && (calendar.get(Calendar.DAY_OF_MONTH)==1) && (calendar.get(Calendar.HOUR_OF_DAY)<1)){
                return true;
            }
            return false;
        }

        public static int getEasterInMarch(int year){
            float a = year%4;
            float b = year%7;
            float c = year%19;
            float d = (19*c + 24)%30;
            float e = (2*a + 4*b + 6*d +5)%7;
            float f = (c + 11*d + 22*e)/451;
            float dayInMarch = 22 + d + e - 7*f;
            return Math.round(dayInMarch);
        }

        public static boolean isEaster(){
            Calendar calendar = Calendar.getInstance();
            return false;
        }

    }

    private final static int DEFAULTICONWIDTH = 256;
    private final static int DEFAULTICONHEIGHT = 256;
    private final ConcurrentHashMap<Integer,Bitmap> bitmapCache = new ConcurrentHashMap<>();
    private final Context context;
    private ImageView imageView;
    private int iconWidth=DEFAULTICONWIDTH;
    private int iconHeight=DEFAULTICONHEIGHT;
    private static final Paint paint = new Paint();
    static {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }
    private static final Paint MoonFill = new Paint();

    public static class Layer{
        public final static int NOT_AVAILABLE = 0;
        public final static int DRIZZLE_1 = 1;
        public final static int DRIZZLE_2 = 2;
        public final static int DRIZZLE_3 = 3;
        public final static int RAIN_1 = 4;
        public final static int RAIN_2 = 5;
        public final static int RAIN_3 = 6;
        public final static int CLOUDS = 7;
        public final static int CLOUD_1 = 8;
        public final static int CLOUD_2 = 9;
        public final static int CLOUD_3 = 10;
        public final static int SNOW_1 = 11;
        public final static int SNOW_2 = 12;
        public final static int SNOW_3 = 13;
        public final static int CLOUDY = 14;
        public final static int FREEZING_1 = 15;
        public final static int FREEZING_2 = 16;
        public final static int FOG = 17;
        public final static int SUN = 18;
        public final static int MOON = 19;
        public final static int LIGHTNING = 20;
        public final static int HALLOWEEN = 201;
    }

    private int getLayerResourceID(int layer){
        switch (layer){
            case Layer.DRIZZLE_1 : return R.mipmap.mod_drizzle_1;
            case Layer.DRIZZLE_2 : return R.mipmap.mod_drizzle_2;
            case Layer.DRIZZLE_3 : return R.mipmap.mod_drizzle_3;
            case Layer.RAIN_1    : return R.mipmap.mod_rain_1;
            case Layer.RAIN_2    : return R.mipmap.mod_rain_2;
            case Layer.RAIN_3    : return R.mipmap.mod_rain_3;
            case Layer.CLOUDS    : return R.mipmap.mod_clouds;
            case Layer.CLOUD_1   : return R.mipmap.mod_cloud_1;
            case Layer.CLOUD_2   : return R.mipmap.mod_cloud_2;
            case Layer.CLOUD_3   : return R.mipmap.mod_cloud_3;
            case Layer.SNOW_1    : return R.mipmap.mod_snow_1;
            case Layer.SNOW_2    : return R.mipmap.mod_snow_2;
            case Layer.SNOW_3    : return R.mipmap.mod_snow_3;
            case Layer.CLOUDY    : return R.mipmap.mod_cloudy;
            case Layer.FREEZING_1: return R.mipmap.mod_freezing_1;
            case Layer.FREEZING_2: return R.mipmap.mod_freezing_2;
            case Layer.FOG       : return R.mipmap.mod_fog;
            case Layer.SUN       : return R.mipmap.mod_sun;
            case Layer.MOON      : return R.mipmap.mod_moon;
            case Layer.LIGHTNING : return R.mipmap.mod_lightning;
            case Layer.HALLOWEEN : return R.mipmap.mod_halloween;
            default: return R.mipmap.not_available;
        }
    }

    private Map<Integer, int[]> ConditionLayers;

    private void initLayers(){
        ConditionLayers = new HashMap<>();
        ConditionLayers.put(WeatherCodeContract.NOT_AVAILABLE,new int[] {Layer.NOT_AVAILABLE});
        ConditionLayers.put(WeatherCodeContract.SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW,new int[] {Layer.CLOUDS,Layer.LIGHTNING});
        ConditionLayers.put(WeatherCodeContract.DRIZZLE_FREEZING_MODERATE_OR_HEAVY,new int[] {Layer.CLOUDS,Layer.DRIZZLE_3,Layer.FREEZING_2});
        ConditionLayers.put(WeatherCodeContract.DRIZZLE_FREEZING_SLIGHT,new int[] {Layer.CLOUDS,Layer.DRIZZLE_1,Layer.FREEZING_1});
        ConditionLayers.put(WeatherCodeContract.RAIN_FREEZING_MODERATE_OR_HEAVY,new int[] {Layer.CLOUDS,Layer.FREEZING_2,Layer.RAIN_3});
        ConditionLayers.put(WeatherCodeContract.RAIN_FREEZING_SLIGHT,new int[] {Layer.CLOUDS,Layer.FREEZING_1,Layer.RAIN_1});
        ConditionLayers.put(WeatherCodeContract.SNOW_SHOWERS_MODERATE_OR_HEAVY,new int[] {Layer.SUN,Layer.CLOUDS,Layer.SNOW_3});
        ConditionLayers.put(WeatherCodeContract.SNOW_SHOWERS_SLIGHT,new int[] {Layer.SUN,Layer.CLOUDS, Layer.SNOW_1});
        ConditionLayers.put(WeatherCodeContract.SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY,new int[] {Layer.SUN,Layer.CLOUDS,Layer.SNOW_2,Layer.RAIN_2});
        ConditionLayers.put(WeatherCodeContract.SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT,new int[] {Layer.SUN,Layer.CLOUDS,Layer.SNOW_1,Layer.RAIN_1});
        ConditionLayers.put(WeatherCodeContract.EXTREMELY_HEAVY_RAIN_SHOWER,new int[] {Layer.SUN,Layer.CLOUDS,Layer.RAIN_3});
        ConditionLayers.put(WeatherCodeContract.MODERATE_OR_HEAVY_RAIN_SHOWERS,new int[] {Layer.SUN,Layer.CLOUDS,Layer.RAIN_2});
        ConditionLayers.put(WeatherCodeContract.SLIGHT_RAIN_SHOWER,new int[] {Layer.SUN,Layer.CLOUDS,Layer.RAIN_1});
        ConditionLayers.put(WeatherCodeContract.HEAVY_SNOWFALL_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.SNOW_3});
        ConditionLayers.put(WeatherCodeContract.MODERATE_SNOWFALL_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.SNOW_2});
        ConditionLayers.put(WeatherCodeContract.SLIGHT_SNOWFALL_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.SNOW_1});
        ConditionLayers.put(WeatherCodeContract.MODERATE_OR_HEAVY_RAIN_AND_SNOW,new int[] {Layer.CLOUDS,Layer.RAIN_2,Layer.SNOW_2});
        ConditionLayers.put(WeatherCodeContract.SLIGHT_RAIN_AND_SNOW,new int[] {Layer.CLOUDS,Layer.RAIN_1,Layer.SNOW_1});
        ConditionLayers.put(WeatherCodeContract.HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.DRIZZLE_3});
        ConditionLayers.put(WeatherCodeContract.MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.DRIZZLE_2});
        ConditionLayers.put(WeatherCodeContract.SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.DRIZZLE_1});
        ConditionLayers.put(WeatherCodeContract.HEAVY_RAIN_NOT_FREEZING_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.RAIN_3});
        ConditionLayers.put(WeatherCodeContract.MODERATE_RAIN_NOT_FREEZING_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.RAIN_2});
        ConditionLayers.put(WeatherCodeContract.SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.RAIN_1});
        ConditionLayers.put(WeatherCodeContract.ICE_FOG_SKY_NOT_RECOGNIZABLE,new int[] {Layer.FOG,Layer.FREEZING_1});
        ConditionLayers.put(WeatherCodeContract.FOG_SKY_NOT_RECOGNIZABLE,new int[] {Layer.FOG});
        ConditionLayers.put(WeatherCodeContract.EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8,new int[] {Layer.CLOUDY});
        ConditionLayers.put(WeatherCodeContract.EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8,new int[] {Layer.SUN,Layer.CLOUD_2});
        ConditionLayers.put(WeatherCodeContract.EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8,new int[] {Layer.SUN,Layer.CLOUD_1});
        ConditionLayers.put(WeatherCodeContract.EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8,new int[] {Layer.SUN});
        MoonFill.setStyle(Paint.Style.FILL_AND_STROKE);
        MoonFill.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.PRIMARYLIGHT));
        MoonFill.setAlpha(230);
        MoonFill.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
    }

    public ForecastIcons(Context context, ImageView imageView){
        this.context = context;
        this.imageView = imageView;
        if (imageView!=null){
            this.iconWidth = imageView.getWidth();
            this.iconHeight = imageView.getHeight();
        }
        if ((this.iconHeight==0) || (this.iconWidth==0)){
            this.iconWidth = DEFAULTICONWIDTH;
            this.iconHeight = DEFAULTICONHEIGHT;
        }
        initLayers();
    }

    public ForecastIcons(Context context, int iconWidth, int iconHeight){
        this.context = context;
        this.iconHeight = iconHeight;
        this.iconWidth = iconWidth;
        initLayers();
    }

    public Bitmap getLayer(int layer){
        Bitmap bitmap = bitmapCache.get(layer);
        if (bitmap==null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inMutable = true;
            BitmapFactory.decodeResource(context.getResources(), getLayerResourceID(layer), options);
            if (imageView!=null){
                options.inSampleSize = ForecastAdapter.calculateInSampleSize(options,imageView);
            } else {
                options.inSampleSize = ForecastAdapter.calculateInSampleSize(options,iconWidth,iconHeight);
            }
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeResource(context.getResources(), getLayerResourceID(layer), options);
            bitmap = Bitmap.createScaledBitmap(bitmap,iconWidth,iconHeight,true);
            bitmapCache.put(layer, bitmap);
        }
        return bitmap;
    }

    public Bitmap getDisposableMoonLayer(Weather.WeatherInfo weatherInfo, Weather.WeatherLocation weatherLocation){
        return getDisposableMoonLayer(weatherInfo.getTimestamp(),weatherLocation);
    }

    public Bitmap getDisposableMoonLayer(long time, Weather.WeatherLocation weatherLocation){
        float moonPhase = (float) Weather.getMoonPhase(time);
        float moonDiameter = iconWidth * 204f/512f;
        float moonPositionX = iconWidth/2f - (moonPhase*moonDiameter*2);
        if (moonPhase>=0.5){
            moonPositionX = iconWidth/2f + moonDiameter*2 -  (moonPhase*moonDiameter*2);
        }
        Bitmap targetBitmap = Bitmap.createBitmap(iconWidth,iconHeight, Bitmap.Config.ARGB_8888);
        Bitmap moonLayer = getLayer(Layer.MOON);
        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawBitmap(moonLayer,0,0,paint);
        canvas.drawCircle(moonPositionX,iconHeight/2f,moonDiameter/2f,MoonFill);
        Bitmap solidBitmap = Bitmap.createBitmap(iconWidth,iconHeight, Bitmap.Config.ARGB_8888);
        canvas.drawBitmap(solidBitmap,0,0,MoonFill);
        // make the earth transition also look right on the southern hemisphere
        if (weatherLocation!=null){
            if (weatherLocation.latitude<0){
                Matrix matrix = new Matrix();
                matrix.preScale(-1,1);
                targetBitmap = Bitmap.createBitmap(targetBitmap,0,0,targetBitmap.getWidth(),targetBitmap.getHeight(),matrix,true);
            }
        }
        if (Holidays.isHalloween(time)){
            Bitmap halloween = getLayer(Layer.HALLOWEEN);
            canvas.drawBitmap(halloween,0,0,paint);
        }
        return targetBitmap;
    }

    private void paintShower(Canvas canvas, int weatherCondition){
        int xOffset=0; int yOffset=0;
        if ((weatherCondition==WeatherCodeContract.SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY) ||
                (weatherCondition==WeatherCodeContract.SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT) ||
                (weatherCondition==WeatherCodeContract.SNOW_SHOWERS_SLIGHT) ||
                (weatherCondition==WeatherCodeContract.SNOW_SHOWERS_MODERATE_OR_HEAVY) ||
                (weatherCondition==WeatherCodeContract.EXTREMELY_HEAVY_RAIN_SHOWER) ||
                (weatherCondition==WeatherCodeContract.MODERATE_OR_HEAVY_RAIN_SHOWERS) ||
                (weatherCondition==WeatherCodeContract.SLIGHT_RAIN_SHOWER)){
                xOffset = iconHeight/4;
                yOffset = -iconHeight/4;
        }
        canvas.drawBitmap(getLayer(Layer.SUN),xOffset,yOffset,paint);
    }

    public Bitmap getIconBitmap(Weather.WeatherInfo weatherInfo, Weather.WeatherLocation weatherLocation){
        int condition = WeatherCodeContract.NOT_AVAILABLE;
        if (weatherInfo.hasCondition()) {
            condition = weatherInfo.getCondition();
        }
        int xOffset=0; int yOffset=0;
        if ((condition==WeatherCodeContract.SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY) ||
                (condition==WeatherCodeContract.SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT) ||
                (condition==WeatherCodeContract.SNOW_SHOWERS_SLIGHT) ||
                (condition==WeatherCodeContract.SNOW_SHOWERS_MODERATE_OR_HEAVY) ||
                (condition==WeatherCodeContract.EXTREMELY_HEAVY_RAIN_SHOWER) ||
                (condition==WeatherCodeContract.MODERATE_OR_HEAVY_RAIN_SHOWERS) ||
                (condition==WeatherCodeContract.SLIGHT_RAIN_SHOWER)){
            xOffset = iconHeight/4;
            yOffset = -iconHeight/4;
        }
        int[] layers = ConditionLayers.get(condition);
        if (layers != null) {
            Bitmap targetBitmap = Bitmap.createBitmap(iconWidth,iconHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(targetBitmap);
            //Paint pnt = new Paint(); pnt.setTextSize(48); pnt.setColor(Color.YELLOW);
            //canvas.drawText("C: "+condition,10,iconHeight,pnt);
            // modify for day/night
            if (layers[0] == Layer.SUN) {
                if (weatherLocation != null) {
                    if (!weatherInfo.isDaytime(weatherLocation)) {
                        canvas.drawBitmap(getDisposableMoonLayer(weatherInfo,weatherLocation),xOffset,yOffset,paint);
                    } else {
                        canvas.drawBitmap(getLayer(Layer.SUN),xOffset,yOffset,paint);
                    }
                } else {
                    canvas.drawBitmap(getLayer(Layer.SUN),xOffset,yOffset,paint);
                }
            } else {
                canvas.drawBitmap(getLayer(layers[0]),0,0,paint);
            }
            for (int i = 1; i < layers.length; i++) {
                Bitmap layerBitmap = getLayer(layers[i]);
                canvas.drawBitmap(layerBitmap, 0, 0, paint);
            }
            // modify thunderstorms for rain or snow
            if (condition == WeatherCodeContract.SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW) {
                if (weatherInfo.hasTemperature()) {
                    Bitmap precipitationBitmap;
                    if (weatherInfo.getTemperatureInCelsiusInt() > 0) {
                        precipitationBitmap = getLayer(Layer.RAIN_2);
                    } else {
                        precipitationBitmap = getLayer(Layer.SNOW_2);
                    }
                    Canvas pcanvas = new Canvas(precipitationBitmap);
                    pcanvas.drawBitmap(targetBitmap,0,0,paint);
                    targetBitmap = precipitationBitmap;
                }
            }
            return targetBitmap;
        }
        return null;
    }

    public void clearCache() {
        bitmapCache.clear();
    }
}
