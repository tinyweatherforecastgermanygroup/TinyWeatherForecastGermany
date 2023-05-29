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

import android.content.Context;
import android.graphics.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class WeatherLayer {
    int layer;
    float[] mapGeo;
    Long timestamp;
    int width;
    int height;
    String srs;
    int updateMode;
    int[] atop;
    int legendType;

    public int outlineColor = Color.WHITE;

    public WeatherLayer(int layer, float[] mapGeo, Long timestamp, int width, int height, String srs, int updateMode, int[] atop, int legendType) {
        this.layer = layer;
        this.mapGeo = mapGeo;
        this.timestamp = timestamp;
        this.width = width;
        this.height = height;
        this.srs = srs;
        this.updateMode = updateMode;
        this.atop = atop;
        this.legendType = legendType;
    }

    public WeatherLayer(Context context, int layer) {
        this.layer = layer;
        this.timestamp = WeatherSettings.getLayerTime(context,layer);
        ArrayList<WeatherLayer> allLayers = getLayers();
        WeatherLayer weatherLayer = allLayers.get(layer);
        this.mapGeo = weatherLayer.mapGeo;
        this.width = weatherLayer.width;
        this.height = weatherLayer.height;
        this.srs = weatherLayer.srs;
        this.updateMode = weatherLayer.updateMode;
        this.atop = weatherLayer.atop;
        this.legendType = weatherLayer.legendType;
    }

    public static class Layers {
        public final static int WARNING_AREAS_GERMANY = 0;
        public final static int UVI_CLOUDS_0 = 1;
        public final static int UVI_CLOUDS_1 = 2;
        public final static int UVI_CLOUDS_2 = 3;
        public final static int UVI_CLOUDLESS_0 = 4;
        public final static int UVI_CLOUDLESS_1 = 5;
        public final static int UVI_CLOUDLESS_2 = 6;
        public final static int BRD_ORTE = 7;
        public final static int EUROPE_BORDERS_LARGE = 8;
        public final static int SENSED_TEMPERATURE_1M_0 = 9;
        public final static int SENSED_TEMPERATURE_1M_1 = 10;
        public final static int SENSED_TEMPERATURE_1M_2 = 11;
        public final static int UVI_CLOUDS_EUROPE_0 = 12;
        public final static int UVI_CLOUDLESS_EUROPE_0 = 13;
    }

    public final static int LAYERCOUNT = 12;
    public final SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd.MM.yyyy");

    public final static float WARNMAPX0 = 5.86599899999999f;
    public final static float WARNMAPY0 = 47.270362f;
    public final static float WARNMAPX1 = 15.037507f;
    public final static float WARNMAPY1 = 55.057375f;
    public final static float[] WarnMapGeo = {WARNMAPX0, WARNMAPY0, WARNMAPX1, WARNMAPY1};
    public final static int[] WarnMapSize = {622,844};
    public final static int layerMapWidth = 622;
    public final static int layerMapHeight = 844;
    public final static float[] EuropeLargeGeo = {-23.5f,29.5f,62.5f,70.5f};
    public final static int[] EuropeLargeSize = {768,366};

    public static final String CACHE_FILENAME_SUFFIX = ".png";
    public static final String[] CacheFileNames = {"warngebiete_de", "uvi_cl_0", "uvi_cl_1", "uvi_cl_2",
            "uvi_cs_0", "uvi_cs_1", "uvi_cs_2","brd_orte","europe_borders_large",
            "sensed_temperature_0","sensed_temperature_1","sensed_temperature_2",
            "uvi_cl_eu_0","uvi_cs_eu_0"};
    public static final String[] LayerIDs = {"Warngebiete_Bundeslaender", "UVI_Global_CL", "UVI_Global_CL", "UVI_Global_CL",
            "UVI_CS", "UVI_CS", "UVI_CS","BRD_Orte","Laender",
            "GefuehlteTemp","GefuehlteTemp","GefuehlteTemp",
            "UVI_Global_CL", "UVI_CS"};
    public final static int[] browseItemsOrder={Layers.UVI_CLOUDS_0,Layers.UVI_CLOUDS_1,Layers.UVI_CLOUDS_2,
            Layers.UVI_CLOUDLESS_0,Layers.UVI_CLOUDLESS_1,Layers.UVI_CLOUDLESS_2,
            Layers.UVI_CLOUDS_EUROPE_0, Layers.UVI_CLOUDLESS_EUROPE_0,
            Layers.SENSED_TEMPERATURE_1M_0,Layers.SENSED_TEMPERATURE_1M_1,Layers.SENSED_TEMPERATURE_1M_2};

    public static class UpdateMode{
        public final static int NEVER = 0;
        public final static int UVI = 1;
        public final static int DAY = 2;
    }

    public static class TZ{
        public final static int UTC = 0;
        public final static int LOCAL = 1;
    }

    public static class Legend{
        public final static int NONE = 0;
        public final static int UVI = 1;
        public final static int TS = 2;
    }

    public static String getCacheFilename(int layer) {
        return CacheFileNames[layer] + CACHE_FILENAME_SUFFIX;
    }

    public String getCacheFilename() {
        return getCacheFilename(layer);
    }

    public String getFullChacheFilepath(Context context) {
        File cacheDir = context.getCacheDir();
        File target = new File(cacheDir, getCacheFilename());
        return target.toString();
    }

    public static String getLayerID(int layer) {
        return LayerIDs[layer];
    }

    public static String getLabel(Context context, int layer) {
        switch (layer) {
            case Layers.WARNING_AREAS_GERMANY:
                return context.getResources().getString(R.string.layerlabel_warning_areas_de);
            case Layers.UVI_CLOUDS_0:
                return context.getResources().getString(R.string.layerlabel_uvi_cl_0);
            case Layers.UVI_CLOUDS_1:
                return context.getResources().getString(R.string.layerlabel_uvi_cl_1);
            case Layers.UVI_CLOUDS_2:
                return context.getResources().getString(R.string.layerlabel_uvi_cl_2);
            case Layers.UVI_CLOUDLESS_0:
                return context.getResources().getString(R.string.layerlabel_uvi_cs_0);
            case Layers.UVI_CLOUDLESS_1:
                return context.getResources().getString(R.string.layerlabel_uvi_cs_1);
            case Layers.UVI_CLOUDLESS_2:
                return context.getResources().getString(R.string.layerlabel_uvi_cs_2);
            case Layers.UVI_CLOUDS_EUROPE_0:
                return context.getResources().getString(R.string.layerlabel_uvi_eu_cl_0);
            case Layers.UVI_CLOUDLESS_EUROPE_0:
                return context.getResources().getString(R.string.layerlabel_uvi_eu_cs_0);
            case Layers.SENSED_TEMPERATURE_1M_0:
                return context.getResources().getString(R.string.layerlabel_ts_0);
            case Layers.SENSED_TEMPERATURE_1M_1:
                return context.getResources().getString(R.string.layerlabel_ts_1);
            case Layers.SENSED_TEMPERATURE_1M_2:
                return context.getResources().getString(R.string.layerlabel_ts_2);

        }
        return null;
    }

    public static String getShortLabel(Context context, int layer){
        switch (layer) {
            case Layers.WARNING_AREAS_GERMANY:
                return context.getResources().getString(R.string.wm_maps);
            case Layers.UVI_CLOUDS_0:
            case Layers.UVI_CLOUDLESS_2:
            case Layers.UVI_CLOUDS_1:
            case Layers.UVI_CLOUDS_2:
            case Layers.UVI_CLOUDLESS_0:
            case Layers.UVI_CLOUDLESS_1:
            case Layers.UVI_CLOUDS_EUROPE_0:
            case Layers.UVI_CLOUDLESS_EUROPE_0:
                return context.getResources().getString(R.string.layerlabel_short_uv);
            case Layers.SENSED_TEMPERATURE_1M_0:
            case Layers.SENSED_TEMPERATURE_1M_1:
            case Layers.SENSED_TEMPERATURE_1M_2:
                return context.getResources().getString(R.string.layerlabel_short_ts);
        }
        return null;
    }

    public static long getMidnightTime(long time, int daysToAdd) {
        return getFullHourTime(time,0,daysToAdd,TZ.UTC);
    }

    public static long getFullHourTime(long time, int hour, int daysToAdd, int timeZone) {
        Calendar calendar;
        if (timeZone==TZ.UTC){
            calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        } else {
            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.roll(Calendar.DAY_OF_MONTH, daysToAdd);
        long newTime = calendar.getTimeInMillis();
        return newTime;
    }

    public boolean isOutdated(Context context) {
        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar layerCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        File cacheDir = context.getCacheDir();
        File targetFile = new File(cacheDir, getCacheFilename());
        if (!targetFile.exists()){
            // missing file (e.g. cache emptied) means always "outdated"
            return true;
        }
        // also check if any atop-layers are missing. If this is the case, the layer is also "outdated"
        if (atop!=null){
            for (int i=0; i<atop.length; i++){
                WeatherLayer atopLayer = new WeatherLayer(context,atop[i]);
                if (atopLayer.isOutdated(context)){
                    return true;
                }
            }
        }
        layerCalendar.setTimeInMillis(targetFile.lastModified()); // will be 0 if file does not exist
        if (updateMode==UpdateMode.UVI){
            // refresh usually occurs at 10:00 am
            if ((layerCalendar.get(Calendar.HOUR_OF_DAY) < 10) && (currentCalendar.get(Calendar.HOUR_OF_DAY) >= 10)) {
                return true;
            }
            // when from previous day, then regarded outdated
            if (layerCalendar.get(Calendar.DAY_OF_YEAR) < currentCalendar.get(Calendar.DAY_OF_YEAR)) {
                return true;
            }
            // when from previous year, then also outdated
            if (layerCalendar.get(Calendar.YEAR) < currentCalendar.get(Calendar.YEAR)) {
                return true;
            }
        }
        if (updateMode==UpdateMode.DAY){
            // when from previous day, then regarded outdated
            if (layerCalendar.get(Calendar.DAY_OF_YEAR) < currentCalendar.get(Calendar.DAY_OF_YEAR)) {
                return true;
            }
            // when from previous year, then also outdated
            if (layerCalendar.get(Calendar.YEAR) < currentCalendar.get(Calendar.YEAR)) {
                return true;
            }
        }
        return false;
        //return true;
    }

    public static ArrayList<WeatherLayer> getLayers() {
        long time = Calendar.getInstance().getTimeInMillis();
        ArrayList<WeatherLayer> list = new ArrayList<WeatherLayer>();
        list.add(new WeatherLayer(Layers.WARNING_AREAS_GERMANY, WarnMapGeo, null, layerMapWidth, layerMapHeight, "4326",UpdateMode.NEVER,null,Legend.NONE));
        list.add(new WeatherLayer(Layers.UVI_CLOUDS_0, WarnMapGeo, getMidnightTime(time, 0), layerMapWidth, layerMapHeight, "4326",UpdateMode.UVI, new int[] {Layers.WARNING_AREAS_GERMANY,Layers.BRD_ORTE},Legend.UVI));
        list.add(new WeatherLayer(Layers.UVI_CLOUDS_1, WarnMapGeo, getMidnightTime(time, 1), layerMapWidth, layerMapHeight, "4326",UpdateMode.UVI,new int[] {Layers.WARNING_AREAS_GERMANY,Layers.BRD_ORTE},Legend.UVI));
        list.add(new WeatherLayer(Layers.UVI_CLOUDS_2, WarnMapGeo, getMidnightTime(time, 2), layerMapWidth, layerMapHeight, "4326",UpdateMode.UVI,new int[] {Layers.WARNING_AREAS_GERMANY,Layers.BRD_ORTE},Legend.UVI));
        list.add(new WeatherLayer(Layers.UVI_CLOUDLESS_0, WarnMapGeo, getMidnightTime(time, 0), layerMapWidth, layerMapHeight, "4326",UpdateMode.UVI,new int[] {Layers.WARNING_AREAS_GERMANY,Layers.BRD_ORTE},Legend.UVI));
        list.add(new WeatherLayer(Layers.UVI_CLOUDLESS_1, WarnMapGeo, getMidnightTime(time, 1), layerMapWidth, layerMapHeight, "4326",UpdateMode.UVI,new int[] {Layers.WARNING_AREAS_GERMANY,Layers.BRD_ORTE},Legend.UVI));
        list.add(new WeatherLayer(Layers.UVI_CLOUDLESS_2, WarnMapGeo, getMidnightTime(time, 2), layerMapWidth, layerMapHeight, "4326",UpdateMode.UVI,new int[] {Layers.WARNING_AREAS_GERMANY,Layers.BRD_ORTE},Legend.UVI));
        list.add(new WeatherLayer(Layers.BRD_ORTE, WarnMapGeo, null, layerMapWidth, layerMapHeight, "4326",UpdateMode.NEVER,null,Legend.NONE));
        list.add(new WeatherLayer(Layers.EUROPE_BORDERS_LARGE,EuropeLargeGeo,getMidnightTime(time,0),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.NEVER,null,Legend.NONE));
        list.add(new WeatherLayer(Layers.SENSED_TEMPERATURE_1M_0,EuropeLargeGeo,getFullHourTime(time,6,0,TZ.LOCAL),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.DAY, new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.TS));
        list.add(new WeatherLayer(Layers.SENSED_TEMPERATURE_1M_1,EuropeLargeGeo,getFullHourTime(time,12,0,TZ.LOCAL),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.DAY, new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.TS));
        list.add(new WeatherLayer(Layers.SENSED_TEMPERATURE_1M_2,EuropeLargeGeo,getFullHourTime(time,18,0,TZ.LOCAL),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.DAY, new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.TS));
        list.add(new WeatherLayer(Layers.UVI_CLOUDS_EUROPE_0,EuropeLargeGeo,getMidnightTime(time,0),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.UVI,new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.UVI));
        list.add(new WeatherLayer(Layers.UVI_CLOUDLESS_EUROPE_0,EuropeLargeGeo,getMidnightTime(time,0),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.UVI,new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.UVI));
        return list;
    }

    public static final int EXACTLY_GERMANY_XOFFSET_PIXEL = 117;
    public static final int EXACTLY_GERMANY_YOFFSET_PIXEL = 79;
    public static final int EXACTLY_GERMANY_WIDTH_PIXEL = 622;
    public static final int EXACTLY_GERMANY_HEIGHT_PIXEL = 844;

    public static Bitmap getExactlyGermanyBitmap(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1; // do not subsample
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), ThemePicker.getGermanyResource(context), options);
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),ThemePicker.getGermanyResource(context)),WeatherWarningActivity.MAP_PIXEL_FIXEDWIDTH,WeatherWarningActivity.MAP_PIXEL_FIXEDHEIGHT,false);
        bitmap = Bitmap.createBitmap(bitmap, EXACTLY_GERMANY_XOFFSET_PIXEL, EXACTLY_GERMANY_YOFFSET_PIXEL, EXACTLY_GERMANY_WIDTH_PIXEL, EXACTLY_GERMANY_HEIGHT_PIXEL);
        return bitmap;
    }

    public Bitmap getLayerBitmap(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1; // do not subsample
        options.inMutable = true; // always return mutable bitmap
        Bitmap layerBitmap = BitmapFactory.decodeFile(getFullChacheFilepath(context), options);
        if (layerBitmap!=null){
            Bitmap targetBitmap = layerBitmap.copy(Bitmap.Config.ARGB_8888,true);
            Canvas canvasVisibleMap = new Canvas(targetBitmap);
            timestamp = WeatherSettings.getLayerTime(context,layer);
            if (layerBitmap!=null){
                if (atop!=null){
                    final Paint cp = new Paint();
                    cp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                    for (int i=0; i<atop.length; i++){
                        WeatherLayer atopLayer = new WeatherLayer(context,atop[i]);
                        Bitmap atopBitmap = atopLayer.getTransparentLayerBitmap(context,outlineColor);
                        if (atopBitmap!=null){
                            canvasVisibleMap.drawBitmap(atopBitmap,0,0,cp);
                        }
                    }
                }
            }
            return targetBitmap;
        }
        return null;
    }

    public Bitmap getTransparentLayerBitmap(Context context, int targetColor) {
        Bitmap bitmap = getLayerBitmap(context);
        int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
        bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        for (int i=0; i<pixels.length; i++){
            if (pixels[i]==-1){
                pixels[i]= Color.TRANSPARENT;
            } else {
                pixels[i]= targetColor;
            }
        }
        bitmap.setPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        return bitmap;
    }

    public static Bitmap replaceBitmapColor(Bitmap bitmap, int sourceColor, int targetColor){
        int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
        bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        for (int i=0; i<pixels.length; i++){
            if (pixels[i]==sourceColor){
                pixels[i]=targetColor;
            }
        }
        bitmap.setPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        return bitmap;
    }

    public String getTimestampString(){
        return dateFormat.format(new Date(timestamp));
    }

}
