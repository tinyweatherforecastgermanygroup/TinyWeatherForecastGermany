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
import java.io.FileOutputStream;
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
    int[] atop = null;
    int legendType;

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

    public WeatherLayer(int i) {
        WeatherLayer weatherLayer = getLayer(i);
        if (weatherLayer!=null){
            this.layer = weatherLayer.layer;
            this.mapGeo = weatherLayer.mapGeo;
            this.width = weatherLayer.width;
            this.height = weatherLayer.height;
            this.srs = weatherLayer.srs;
            this.updateMode = weatherLayer.updateMode;
            this.atop = weatherLayer.atop;
            this.legendType = weatherLayer.legendType;
        }
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
        public final static int SENSED_TEMPERATURE_MAX_0 = 14;
        public final static int SENSED_TEMPERATURE_MAX_1 = 15;
        public final static int SENSED_TEMPERATURE_MAX_2 = 16;
        public final static int SENSED_TEMPERATURE_MIN_0 = 17;
        public final static int SENSED_TEMPERATURE_MIN_1 = 18;
        public final static int SENSED_TEMPERATURE_MIN_2 = 19;
        public final static int POLLEN_FORECAST_AMBROSIA_0 = 20;
        public final static int POLLEN_FORECAST_AMBROSIA_1 = 21;
        public final static int POLLEN_FORECAST_AMBROSIA_2 = 22;
        public final static int POLLEN_FORECAST_BEIFUSS_0 = 23;
        public final static int POLLEN_FORECAST_BEIFUSS_1 = 24;
        public final static int POLLEN_FORECAST_BEIFUSS_2 = 25;
        public final static int POLLEN_FORECAST_ROGGEN_0 = 26;
        public final static int POLLEN_FORECAST_ROGGEN_1 = 27;
        public final static int POLLEN_FORECAST_ROGGEN_2 = 28;
        public final static int POLLEN_FORECAST_ESCHE_0 = 29;
        public final static int POLLEN_FORECAST_ESCHE_1 = 30;
        public final static int POLLEN_FORECAST_ESCHE_2 = 31;
        public final static int POLLEN_FORECAST_BIRKE_0 = 32;
        public final static int POLLEN_FORECAST_BIRKE_1 = 33;
        public final static int POLLEN_FORECAST_BIRKE_2 = 34;
        public final static int POLLEN_FORECAST_HASEL_0 = 35;
        public final static int POLLEN_FORECAST_HASEL_1 = 36;
        public final static int POLLEN_FORECAST_HASEL_2 = 37;
        public final static int POLLEN_FORECAST_ERLE_0 = 38;
        public final static int POLLEN_FORECAST_ERLE_1 = 39;
        public final static int POLLEN_FORECAST_ERLE_2 = 40;
        public final static int POLLEN_FORECAST_GRAESER_0 = 41;
        public final static int POLLEN_FORECAST_GRAESER_1 = 42;
        public final static int POLLEN_FORECAST_GRAESER_2 = 43;
    }


    public final static int LAYERCOUNT = 44;
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
            "uvi_cl_eu_0","uvi_cs_eu_0",
            "st_max_0","st_max_1","st_max_2","st_min_0","st_min_1","st_min_2",
            "pollen_ambrosia_0","pollen_ambrosia_1", "pollen_ambrosia_2",
            "pollen_beifuss_0","pollen_beifuss_1", "pollen_beifuss_2",
            "pollen_roggen_0", "pollen_roggen_1", "pollen_roggen_2",
            "pollen_esche_0", "pollen_esche_1", "pollen_esche_2",
            "pollen_birke_0", "pollen_birke_1", "pollen_birke_2",
            "pollen_hasel_0", "pollen_hasel_1", "pollen_hasel_2",
            "pollen_erle_0", "pollen_erle_1", "pollen_erle_2",
            "pollen_graeser_0", "pollen_graeser_1", "pollen_graeser_2"
            };

    public static final String[] LayerIDs = {"Warngebiete_Bundeslaender", "UVI_Global_CL", "UVI_Global_CL", "UVI_Global_CL",
            "UVI_CS", "UVI_CS", "UVI_CS","BRD_Orte","Laender",
            "GefuehlteTemp","GefuehlteTemp","GefuehlteTemp",
            "UVI_Global_CL", "UVI_CS",
            "GefuehlteTempMax","GefuehlteTempMax","GefuehlteTempMax","GefuehlteTempMin","GefuehlteTempMin","GefuehlteTempMin",
            null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
            null,null,null};
    private final static int[] browseItemsOrder={Layers.UVI_CLOUDS_0,Layers.UVI_CLOUDS_1,Layers.UVI_CLOUDS_2,
            Layers.UVI_CLOUDLESS_0,Layers.UVI_CLOUDLESS_1,Layers.UVI_CLOUDLESS_2,
            Layers.UVI_CLOUDS_EUROPE_0, Layers.UVI_CLOUDLESS_EUROPE_0,
            Layers.SENSED_TEMPERATURE_1M_0,Layers.SENSED_TEMPERATURE_1M_1,Layers.SENSED_TEMPERATURE_1M_2,
            Layers.SENSED_TEMPERATURE_MIN_0,Layers.SENSED_TEMPERATURE_MIN_1,Layers.SENSED_TEMPERATURE_MIN_2,
            Layers.SENSED_TEMPERATURE_MAX_0,Layers.SENSED_TEMPERATURE_MAX_1,Layers.SENSED_TEMPERATURE_MAX_2,
            Layers.POLLEN_FORECAST_AMBROSIA_0, Layers.POLLEN_FORECAST_AMBROSIA_1, Layers.POLLEN_FORECAST_AMBROSIA_2,
            Layers.POLLEN_FORECAST_BEIFUSS_0, Layers.POLLEN_FORECAST_BEIFUSS_1, Layers.POLLEN_FORECAST_BEIFUSS_2,
            Layers.POLLEN_FORECAST_ROGGEN_0, Layers.POLLEN_FORECAST_ROGGEN_1, Layers.POLLEN_FORECAST_ROGGEN_2,
            Layers.POLLEN_FORECAST_ESCHE_0, Layers.POLLEN_FORECAST_ESCHE_1, Layers.POLLEN_FORECAST_ESCHE_2,
            Layers.POLLEN_FORECAST_BIRKE_0, Layers.POLLEN_FORECAST_BIRKE_1, Layers.POLLEN_FORECAST_BIRKE_2,
            Layers.POLLEN_FORECAST_HASEL_0, Layers.POLLEN_FORECAST_HASEL_1, Layers.POLLEN_FORECAST_HASEL_2,
            Layers.POLLEN_FORECAST_ERLE_0, Layers.POLLEN_FORECAST_ERLE_1, Layers.POLLEN_FORECAST_ERLE_2,
            Layers.POLLEN_FORECAST_GRAESER_0, Layers.POLLEN_FORECAST_GRAESER_1, Layers.POLLEN_FORECAST_GRAESER_2};

    public static class UpdateMode{
        public final static int NEVER = 0;
        public final static int UVI = 1;
        public final static int DAY = 2;
        public final static int POLLEN = 3;
    }

    public static class TZ{
        public final static int UTC = 0;
        public final static int LOCAL = 1;
    }

    public static class Legend{
        public final static int NONE = 0;
        public final static int UVI = 1;
        public final static int TS = 2;
        public final static int POLLEN = 3;
    }

    public static boolean isInIntArray(int[] array, int value){
        for (int i=0; i<array.length; i++){
            if (array[i]==value){
                return true;
            }
        }
        return false;
    }

    private static int[] getBrowseItemsOrder(int[] filter){
        if (filter==null){
            return browseItemsOrder;
        } else {
            int newLength=browseItemsOrder.length - filter.length;
            int[] result = new int[newLength];
            int i=0; int writePosition=0;
            while (i<browseItemsOrder.length){
                int value = browseItemsOrder[i];
                if (!isInIntArray(filter,value)){
                    result[writePosition]=value;
                    writePosition++;
                }
                i++;
            }
            return result;
        }
    }

    public static int[] getDisabledLayersArray(Context context){
        ArrayList<Integer> filterArrayList= new ArrayList<Integer>();
        if (!WeatherSettings.getPollenActiveAmbrosia(context)){
            filterArrayList.add(Layers.POLLEN_FORECAST_AMBROSIA_0);
            filterArrayList.add(Layers.POLLEN_FORECAST_AMBROSIA_1);
            filterArrayList.add(Layers.POLLEN_FORECAST_AMBROSIA_2);
        }
        if (!WeatherSettings.getPollenActiveBeifuss(context)){
            filterArrayList.add(Layers.POLLEN_FORECAST_BEIFUSS_0);
            filterArrayList.add(Layers.POLLEN_FORECAST_BEIFUSS_1);
            filterArrayList.add(Layers.POLLEN_FORECAST_BEIFUSS_2);
        }
        if (!WeatherSettings.getPollenActiveRoggen(context)){
            filterArrayList.add(Layers.POLLEN_FORECAST_ROGGEN_0);
            filterArrayList.add(Layers.POLLEN_FORECAST_ROGGEN_1);
            filterArrayList.add(Layers.POLLEN_FORECAST_ROGGEN_2);
        }
        if (!WeatherSettings.getPollenActiveEsche(context)){
            filterArrayList.add(Layers.POLLEN_FORECAST_ESCHE_0);
            filterArrayList.add(Layers.POLLEN_FORECAST_ESCHE_1);
            filterArrayList.add(Layers.POLLEN_FORECAST_ESCHE_2);
        }
        if (!WeatherSettings.getPollenActiveBirke(context)){
            filterArrayList.add(Layers.POLLEN_FORECAST_BIRKE_0);
            filterArrayList.add(Layers.POLLEN_FORECAST_BIRKE_1);
            filterArrayList.add(Layers.POLLEN_FORECAST_BIRKE_2);
        }
        if (!WeatherSettings.getPollenActiveHasel(context)){
            filterArrayList.add(Layers.POLLEN_FORECAST_HASEL_0);
            filterArrayList.add(Layers.POLLEN_FORECAST_HASEL_1);
            filterArrayList.add(Layers.POLLEN_FORECAST_HASEL_2);
        }
        if (!WeatherSettings.getPollenActiveErle(context)){
            filterArrayList.add(Layers.POLLEN_FORECAST_ERLE_0);
            filterArrayList.add(Layers.POLLEN_FORECAST_ERLE_1);
            filterArrayList.add(Layers.POLLEN_FORECAST_ERLE_2);
        }
        if (!WeatherSettings.getPollenActiveGraeser(context)){
            filterArrayList.add(Layers.POLLEN_FORECAST_GRAESER_0);
            filterArrayList.add(Layers.POLLEN_FORECAST_GRAESER_1);
            filterArrayList.add(Layers.POLLEN_FORECAST_GRAESER_2);
        }
        int[] filterArray = new int[filterArrayList.size()];
        for (int i=0; i< filterArrayList.size(); i++){
            filterArray[i] = filterArrayList.get(i);
        }
        return filterArray;
    }


    public static int[] getFilteredBrowseItemsOrder(Context context){
        return getBrowseItemsOrder(getDisabledLayersArray(context));
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
        final String seperator1=": ";
        final String seperator2=", ";
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
                return context.getResources().getString(R.string.layerlabel_ts_0).replace("6:00","12:00");
            case Layers.SENSED_TEMPERATURE_1M_2:
                return context.getResources().getString(R.string.layerlabel_ts_0).replace("6:00","18:00");
            case Layers.SENSED_TEMPERATURE_MIN_0:
                return context.getResources().getString(R.string.layerlabel_ts_min_0);
            case Layers.SENSED_TEMPERATURE_MIN_1:
                return context.getResources().getString(R.string.layerlabel_ts_min_1);
            case Layers.SENSED_TEMPERATURE_MIN_2:
                return context.getResources().getString(R.string.layerlabel_ts_min_2);
            case Layers.SENSED_TEMPERATURE_MAX_0:
                return context.getResources().getString(R.string.layerlabel_ts_max_0);
            case Layers.SENSED_TEMPERATURE_MAX_1:
                return context.getResources().getString(R.string.layerlabel_ts_max_1);
            case Layers.SENSED_TEMPERATURE_MAX_2:
                return context.getResources().getString(R.string.layerlabel_ts_max_2);
            case Layers.POLLEN_FORECAST_AMBROSIA_0: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_ambrosia)+seperator2+context.getResources().getString(R.string.today);
            case Layers.POLLEN_FORECAST_AMBROSIA_1: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_ambrosia)+seperator2+context.getResources().getString(R.string.tomorrow);
            case Layers.POLLEN_FORECAST_AMBROSIA_2: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_ambrosia)+seperator2+context.getResources().getString(R.string.dayaftertomorrow);
            case Layers.POLLEN_FORECAST_BEIFUSS_0: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_mugwort)+seperator2+context.getResources().getString(R.string.today);
            case Layers.POLLEN_FORECAST_BEIFUSS_1: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_mugwort)+seperator2+context.getResources().getString(R.string.tomorrow);
            case Layers.POLLEN_FORECAST_BEIFUSS_2: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_mugwort)+seperator2+context.getResources().getString(R.string.dayaftertomorrow);
            case Layers.POLLEN_FORECAST_ROGGEN_0: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_rye)+seperator2+context.getResources().getString(R.string.today);
            case Layers.POLLEN_FORECAST_ROGGEN_1: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_rye)+seperator2+context.getResources().getString(R.string.tomorrow);
            case Layers.POLLEN_FORECAST_ROGGEN_2: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_rye)+seperator2+context.getResources().getString(R.string.dayaftertomorrow);
            case Layers.POLLEN_FORECAST_ESCHE_0: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_ash)+seperator2+context.getResources().getString(R.string.today);
            case Layers.POLLEN_FORECAST_ESCHE_1: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_ash)+seperator2+context.getResources().getString(R.string.tomorrow);
            case Layers.POLLEN_FORECAST_ESCHE_2: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_ash)+seperator2+context.getResources().getString(R.string.dayaftertomorrow);
            case Layers.POLLEN_FORECAST_BIRKE_0: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_birch)+seperator2+context.getResources().getString(R.string.today);
            case Layers.POLLEN_FORECAST_BIRKE_1: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_birch)+seperator2+context.getResources().getString(R.string.tomorrow);
            case Layers.POLLEN_FORECAST_BIRKE_2: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_birch)+seperator2+context.getResources().getString(R.string.dayaftertomorrow);
            case Layers.POLLEN_FORECAST_HASEL_0: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_hazel)+seperator2+context.getResources().getString(R.string.today);
            case Layers.POLLEN_FORECAST_HASEL_1: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_hazel)+seperator2+context.getResources().getString(R.string.tomorrow);
            case Layers.POLLEN_FORECAST_HASEL_2: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_hazel)+seperator2+context.getResources().getString(R.string.dayaftertomorrow);
            case Layers.POLLEN_FORECAST_ERLE_0: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_alder)+seperator2+context.getResources().getString(R.string.today);
            case Layers.POLLEN_FORECAST_ERLE_1: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_alder)+seperator2+context.getResources().getString(R.string.tomorrow);
            case Layers.POLLEN_FORECAST_ERLE_2: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_alder)+seperator2+context.getResources().getString(R.string.dayaftertomorrow);
            case Layers.POLLEN_FORECAST_GRAESER_0: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_grasses)+seperator2+context.getResources().getString(R.string.today);
            case Layers.POLLEN_FORECAST_GRAESER_1: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_grasses)+seperator2+context.getResources().getString(R.string.tomorrow);
            case Layers.POLLEN_FORECAST_GRAESER_2: return context.getResources().getString(R.string.pollen_title)+seperator1+context.getResources().getString(R.string.pollen_grasses)+seperator2+context.getResources().getString(R.string.dayaftertomorrow);
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
                return context.getResources().getString(R.string.preference_screen_uvhi_title);
            case Layers.SENSED_TEMPERATURE_1M_0:
            case Layers.SENSED_TEMPERATURE_1M_1:
            case Layers.SENSED_TEMPERATURE_1M_2:
            case Layers.SENSED_TEMPERATURE_MAX_0:
            case Layers.SENSED_TEMPERATURE_MAX_1:
            case Layers.SENSED_TEMPERATURE_MAX_2:
            case Layers.SENSED_TEMPERATURE_MIN_0:
            case Layers.SENSED_TEMPERATURE_MIN_1:
            case Layers.SENSED_TEMPERATURE_MIN_2:
                return context.getResources().getString(R.string.layerlabel_short_ts);
            case Layers.POLLEN_FORECAST_AMBROSIA_0:
            case Layers.POLLEN_FORECAST_AMBROSIA_1:
            case Layers.POLLEN_FORECAST_AMBROSIA_2:
            case Layers.POLLEN_FORECAST_BEIFUSS_0:
            case Layers.POLLEN_FORECAST_BEIFUSS_1:
            case Layers.POLLEN_FORECAST_BEIFUSS_2:
            case Layers.POLLEN_FORECAST_ROGGEN_0:
            case Layers.POLLEN_FORECAST_ROGGEN_1:
            case Layers.POLLEN_FORECAST_ROGGEN_2:
            case Layers.POLLEN_FORECAST_ESCHE_0:
            case Layers.POLLEN_FORECAST_ESCHE_1:
            case Layers.POLLEN_FORECAST_ESCHE_2:
            case Layers.POLLEN_FORECAST_BIRKE_0:
            case Layers.POLLEN_FORECAST_BIRKE_1:
            case Layers.POLLEN_FORECAST_BIRKE_2:
            case Layers.POLLEN_FORECAST_HASEL_0:
            case Layers.POLLEN_FORECAST_HASEL_1:
            case Layers.POLLEN_FORECAST_HASEL_2:
            case Layers.POLLEN_FORECAST_ERLE_0:
            case Layers.POLLEN_FORECAST_ERLE_1:
            case Layers.POLLEN_FORECAST_ERLE_2:
            case Layers.POLLEN_FORECAST_GRAESER_0:
            case Layers.POLLEN_FORECAST_GRAESER_1:
            case Layers.POLLEN_FORECAST_GRAESER_2:
                return context.getResources().getString(R.string.pollen_title);
        }
        return null;
    }

    public static long getMidnightTime(long time, int daysToAdd) {
        return getFullHourTime(time,0,daysToAdd,TZ.UTC);
    }

    public static long getMidnightTime(long time) {
        return getFullHourTime(time,0,0,TZ.UTC);
    }

    public static int  getRelativeDays(long time){
        // long todayMidnightTime = getMidnightTime(Calendar.getInstance().getTimeInMillis());
        // long targetMidnightTime = getMidnightTime(time);
        long todayMidnightTime = getFullHourTime(Calendar.getInstance().getTimeInMillis(),0,0,TZ.LOCAL);
        long targetMidnightTime = getFullHourTime(time,0,0,TZ.LOCAL);
        return (int) (targetMidnightTime - todayMidnightTime)/(1000*60*60*24);
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
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        long newTime = calendar.getTimeInMillis();
        return newTime;
    }


    public boolean isOutdated(Context context) {
        if (updateMode==UpdateMode.POLLEN){
            if (Pollen.isUpdateDue(context)){
                return true;
            } else {
                return false;
            }
        }
        if (updateMode==UpdateMode.NEVER){
            if (cacheFileExists(context)){
                return false;
            }
            return true;
        }
        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar layerCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        File cacheDir = context.getCacheDir();
        File targetFile = new File(cacheDir, getCacheFilename());
        if (!targetFile.exists()){
            // missing file (e.g. cache emptied) means always "outdated"
            return true;
        }
        // too small file below 1k is very likely not a valid png
        if (targetFile.length()<1024){
            return true;
        }
        // also check if any atop-layers are missing. If this is the case, the layer is also "outdated"
        if (atop!=null){
            for (int i=0; i<atop.length; i++){
                WeatherLayer atopLayer = new WeatherLayer(atop[i]);
                if (atopLayer.layer!=layer){
                if (atopLayer.isOutdated(context)){
                        return true;
                    }
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
    }

    public static WeatherLayer getLayer(int i){
        long time = Calendar.getInstance().getTimeInMillis();
        switch (i){
            case Layers.WARNING_AREAS_GERMANY: return new WeatherLayer(Layers.WARNING_AREAS_GERMANY, WarnMapGeo, null, layerMapWidth, layerMapHeight, "4326",UpdateMode.NEVER,null,Legend.NONE);
            case Layers.UVI_CLOUDS_0: return new WeatherLayer(Layers.UVI_CLOUDS_0, WarnMapGeo, getMidnightTime(time, 0), layerMapWidth, layerMapHeight, "4326",UpdateMode.UVI, new int[] {Layers.WARNING_AREAS_GERMANY,Layers.BRD_ORTE},Legend.UVI);
            case Layers.UVI_CLOUDS_1: return new WeatherLayer(Layers.UVI_CLOUDS_1, WarnMapGeo, getMidnightTime(time, 1), layerMapWidth, layerMapHeight, "4326",UpdateMode.UVI,new int[] {Layers.WARNING_AREAS_GERMANY,Layers.BRD_ORTE},Legend.UVI);
            case Layers.UVI_CLOUDS_2: return new WeatherLayer(Layers.UVI_CLOUDS_2, WarnMapGeo, getMidnightTime(time, 2), layerMapWidth, layerMapHeight, "4326",UpdateMode.UVI,new int[] {Layers.WARNING_AREAS_GERMANY,Layers.BRD_ORTE},Legend.UVI);
            case Layers.UVI_CLOUDLESS_0: return new WeatherLayer(Layers.UVI_CLOUDLESS_0, WarnMapGeo, getMidnightTime(time, 0), layerMapWidth, layerMapHeight, "4326",UpdateMode.UVI,new int[] {Layers.WARNING_AREAS_GERMANY,Layers.BRD_ORTE},Legend.UVI);
            case Layers.UVI_CLOUDLESS_1: return new WeatherLayer(Layers.UVI_CLOUDLESS_1, WarnMapGeo, getMidnightTime(time, 1), layerMapWidth, layerMapHeight, "4326",UpdateMode.UVI,new int[] {Layers.WARNING_AREAS_GERMANY,Layers.BRD_ORTE},Legend.UVI);
            case Layers.UVI_CLOUDLESS_2: return new WeatherLayer(Layers.UVI_CLOUDLESS_2, WarnMapGeo, getMidnightTime(time, 2), layerMapWidth, layerMapHeight, "4326",UpdateMode.UVI,new int[] {Layers.WARNING_AREAS_GERMANY,Layers.BRD_ORTE},Legend.UVI);
            case Layers.BRD_ORTE: return new WeatherLayer(Layers.BRD_ORTE, WarnMapGeo, null, layerMapWidth, layerMapHeight, "4326",UpdateMode.NEVER,null,Legend.NONE);
            case Layers.EUROPE_BORDERS_LARGE: return new WeatherLayer(Layers.EUROPE_BORDERS_LARGE,EuropeLargeGeo,getMidnightTime(time,0),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.NEVER,null,Legend.NONE);
            case Layers.SENSED_TEMPERATURE_1M_0: return new WeatherLayer(Layers.SENSED_TEMPERATURE_1M_0,EuropeLargeGeo,getFullHourTime(time,6,0,TZ.LOCAL),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.DAY, new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.TS);
            case Layers.SENSED_TEMPERATURE_1M_1: return new WeatherLayer(Layers.SENSED_TEMPERATURE_1M_1,EuropeLargeGeo,getFullHourTime(time,12,0,TZ.LOCAL),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.DAY, new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.TS);
            case Layers.SENSED_TEMPERATURE_1M_2: return new WeatherLayer(Layers.SENSED_TEMPERATURE_1M_2,EuropeLargeGeo,getFullHourTime(time,18,0,TZ.LOCAL),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.DAY, new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.TS);
            case Layers.UVI_CLOUDS_EUROPE_0: return new WeatherLayer(Layers.UVI_CLOUDS_EUROPE_0,EuropeLargeGeo,getMidnightTime(time,0),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.UVI,new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.UVI);
            case Layers.UVI_CLOUDLESS_EUROPE_0: return new WeatherLayer(Layers.UVI_CLOUDLESS_EUROPE_0,EuropeLargeGeo,getMidnightTime(time,0),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.UVI,new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.UVI);
            case Layers.SENSED_TEMPERATURE_MAX_0: return new WeatherLayer(Layers.SENSED_TEMPERATURE_MAX_0,EuropeLargeGeo,getMidnightTime(time,0),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.UVI,new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.TS);
            case Layers.SENSED_TEMPERATURE_MAX_1: return new WeatherLayer(Layers.SENSED_TEMPERATURE_MAX_1,EuropeLargeGeo,getMidnightTime(time,1),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.UVI,new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.TS);
            case Layers.SENSED_TEMPERATURE_MAX_2: return new WeatherLayer(Layers.SENSED_TEMPERATURE_MAX_2,EuropeLargeGeo,getMidnightTime(time,2),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.UVI,new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.TS);
            case Layers.SENSED_TEMPERATURE_MIN_0: return new WeatherLayer(Layers.SENSED_TEMPERATURE_MIN_0,EuropeLargeGeo,getMidnightTime(time,0),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.UVI,new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.TS);
            case Layers.SENSED_TEMPERATURE_MIN_1: return new WeatherLayer(Layers.SENSED_TEMPERATURE_MIN_1,EuropeLargeGeo,getMidnightTime(time,1),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.UVI,new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.TS);
            case Layers.SENSED_TEMPERATURE_MIN_2: return new WeatherLayer(Layers.SENSED_TEMPERATURE_MIN_2,EuropeLargeGeo,getMidnightTime(time,2),EuropeLargeSize[0],EuropeLargeSize[1],"4326",UpdateMode.UVI,new int[] {Layers.EUROPE_BORDERS_LARGE},Legend.TS);
            case Layers.POLLEN_FORECAST_AMBROSIA_0: return new WeatherLayer(Layers.POLLEN_FORECAST_AMBROSIA_0,WarnMapGeo,getMidnightTime(time,0),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_AMBROSIA_1: return new WeatherLayer(Layers.POLLEN_FORECAST_AMBROSIA_1,WarnMapGeo,getMidnightTime(time,1),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_AMBROSIA_2: return new WeatherLayer(Layers.POLLEN_FORECAST_AMBROSIA_2,WarnMapGeo,getMidnightTime(time,2),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_BEIFUSS_0: return new WeatherLayer(Layers.POLLEN_FORECAST_BEIFUSS_0,WarnMapGeo,getMidnightTime(time,0),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_BEIFUSS_1: return new WeatherLayer(Layers.POLLEN_FORECAST_BEIFUSS_1,WarnMapGeo,getMidnightTime(time,1),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_BEIFUSS_2: return new WeatherLayer(Layers.POLLEN_FORECAST_BEIFUSS_2,WarnMapGeo,getMidnightTime(time,2),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_ROGGEN_0: return new WeatherLayer(Layers.POLLEN_FORECAST_ROGGEN_0,WarnMapGeo,getMidnightTime(time,0),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_ROGGEN_1: return new WeatherLayer(Layers.POLLEN_FORECAST_ROGGEN_1,WarnMapGeo,getMidnightTime(time,1),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_ROGGEN_2: return new WeatherLayer(Layers.POLLEN_FORECAST_ROGGEN_2,WarnMapGeo,getMidnightTime(time,2),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_ESCHE_0: return new WeatherLayer(Layers.POLLEN_FORECAST_ESCHE_0,WarnMapGeo,getMidnightTime(time,0),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_ESCHE_1: return new WeatherLayer(Layers.POLLEN_FORECAST_ESCHE_1,WarnMapGeo,getMidnightTime(time,1),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_ESCHE_2: return new WeatherLayer(Layers.POLLEN_FORECAST_ESCHE_2,WarnMapGeo,getMidnightTime(time,2),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_BIRKE_0: return new WeatherLayer(Layers.POLLEN_FORECAST_BIRKE_0,WarnMapGeo,getMidnightTime(time,0),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_BIRKE_1: return new WeatherLayer(Layers.POLLEN_FORECAST_BIRKE_1,WarnMapGeo,getMidnightTime(time,1),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_BIRKE_2: return new WeatherLayer(Layers.POLLEN_FORECAST_BIRKE_2,WarnMapGeo,getMidnightTime(time,2),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_HASEL_0: return new WeatherLayer(Layers.POLLEN_FORECAST_HASEL_0,WarnMapGeo,getMidnightTime(time,0),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_HASEL_1: return new WeatherLayer(Layers.POLLEN_FORECAST_HASEL_1,WarnMapGeo,getMidnightTime(time,1),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_HASEL_2: return new WeatherLayer(Layers.POLLEN_FORECAST_HASEL_2,WarnMapGeo,getMidnightTime(time,2),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_ERLE_0: return new WeatherLayer(Layers.POLLEN_FORECAST_ERLE_0,WarnMapGeo,getMidnightTime(time,0),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_ERLE_1: return new WeatherLayer(Layers.POLLEN_FORECAST_ERLE_1,WarnMapGeo,getMidnightTime(time,1),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_ERLE_2: return new WeatherLayer(Layers.POLLEN_FORECAST_ERLE_2,WarnMapGeo,getMidnightTime(time,2),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_GRAESER_0: return new WeatherLayer(Layers.POLLEN_FORECAST_GRAESER_0,WarnMapGeo,getMidnightTime(time,0),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_GRAESER_1: return new WeatherLayer(Layers.POLLEN_FORECAST_GRAESER_1,WarnMapGeo,getMidnightTime(time,1),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
            case Layers.POLLEN_FORECAST_GRAESER_2: return new WeatherLayer(Layers.POLLEN_FORECAST_GRAESER_2,WarnMapGeo,getMidnightTime(time,2),layerMapWidth, layerMapHeight, "4326",UpdateMode.POLLEN,null,Legend.POLLEN);
        }
        return null;
    }

    public static boolean isInArray(int[] array, int item){
        for (int i=0; i<array.length; i++){
            if (array[i]==item){
                return true;
            }
        }
        return false;
    }

    public static ArrayList<WeatherLayer> getLayers(Context context) {
        ArrayList<WeatherLayer> list = new ArrayList<WeatherLayer>();
        int[] disabledLayers = getDisabledLayersArray(context);
        for (int i=0; i<LAYERCOUNT; i++){
            if (!isInArray(disabledLayers,i)){
                WeatherLayer weatherLayer = getLayer(i);
                list.add(weatherLayer);
            }
        }
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

    private int getPollenType(){
        switch (layer){
            case Layers.POLLEN_FORECAST_AMBROSIA_0:
            case Layers.POLLEN_FORECAST_AMBROSIA_1:
            case Layers.POLLEN_FORECAST_AMBROSIA_2: return Pollen.Ambrosia;
            case Layers.POLLEN_FORECAST_BEIFUSS_0:
            case Layers.POLLEN_FORECAST_BEIFUSS_1:
            case Layers.POLLEN_FORECAST_BEIFUSS_2: return Pollen.Beifuss;
            case Layers.POLLEN_FORECAST_ROGGEN_0:
            case Layers.POLLEN_FORECAST_ROGGEN_1:
            case Layers.POLLEN_FORECAST_ROGGEN_2: return Pollen.Roggen;
            case Layers.POLLEN_FORECAST_ESCHE_0:
            case Layers.POLLEN_FORECAST_ESCHE_1:
            case Layers.POLLEN_FORECAST_ESCHE_2: return Pollen.Esche;
            case Layers.POLLEN_FORECAST_BIRKE_0:
            case Layers.POLLEN_FORECAST_BIRKE_1:
            case Layers.POLLEN_FORECAST_BIRKE_2: return Pollen.Birke;
            case Layers.POLLEN_FORECAST_HASEL_0:
            case Layers.POLLEN_FORECAST_HASEL_1:
            case Layers.POLLEN_FORECAST_HASEL_2: return Pollen.Hasel;
            case Layers.POLLEN_FORECAST_ERLE_0:
            case Layers.POLLEN_FORECAST_ERLE_1:
            case Layers.POLLEN_FORECAST_ERLE_2: return Pollen.Erle;
            case Layers.POLLEN_FORECAST_GRAESER_0:
            case Layers.POLLEN_FORECAST_GRAESER_1:
            case Layers.POLLEN_FORECAST_GRAESER_2: return Pollen.Graeser;
            default: return -1;
        }
    }

    private int getPollenTimeParam(){
        switch (layer){
            case Layers.POLLEN_FORECAST_AMBROSIA_0:
            case Layers.POLLEN_FORECAST_BEIFUSS_0:
            case Layers.POLLEN_FORECAST_ROGGEN_0:
            case Layers.POLLEN_FORECAST_ESCHE_0:
            case Layers.POLLEN_FORECAST_BIRKE_0:
            case Layers.POLLEN_FORECAST_HASEL_0:
            case Layers.POLLEN_FORECAST_ERLE_0:
            case Layers.POLLEN_FORECAST_GRAESER_0:
                return Pollen.Today;
            case Layers.POLLEN_FORECAST_AMBROSIA_1:
            case Layers.POLLEN_FORECAST_BEIFUSS_1:
            case Layers.POLLEN_FORECAST_ROGGEN_1:
            case Layers.POLLEN_FORECAST_ESCHE_1:
            case Layers.POLLEN_FORECAST_BIRKE_1:
            case Layers.POLLEN_FORECAST_HASEL_1:
            case Layers.POLLEN_FORECAST_ERLE_1:
            case Layers.POLLEN_FORECAST_GRAESER_1:
                return Pollen.Tomorrow;
            case Layers.POLLEN_FORECAST_AMBROSIA_2:
            case Layers.POLLEN_FORECAST_BEIFUSS_2:
            case Layers.POLLEN_FORECAST_ROGGEN_2:
            case Layers.POLLEN_FORECAST_ESCHE_2:
            case Layers.POLLEN_FORECAST_BIRKE_2:
            case Layers.POLLEN_FORECAST_HASEL_2:
            case Layers.POLLEN_FORECAST_ERLE_2:
            case Layers.POLLEN_FORECAST_GRAESER_2:
                return Pollen.DayAfterTomorrow;
            default: return -1;
        }
    }

    public Bitmap getLayerBitmap(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1; // do not subsample
        options.inMutable = true; // always return mutable bitmap
        Bitmap layerBitmap = null;
        layerBitmap = BitmapFactory.decodeFile(getFullChacheFilepath(context), options);
        // build pollen map if necessary
        if (layerBitmap==null && isPollen()){
            int pollenType = getPollenType();
            int timeParam = getPollenTimeParam();
            layerBitmap = ForecastBitmap.getPollenAreasBitmap(context, pollenType, timeParam);
            // put pollen bitmap to cache for faster reading in the future
            saveLayerBitmapToCache(context,layerBitmap);
            timestamp = Pollen.getLastPollenUpdateTime(context)+(long) getPollenTimeParam()*24*60*60*1000;
        } else {
            long originalTimestamp = WeatherSettings.getLayerTime(context,layer);
            // take timestamp from settings only if bitmap decoding was successful and time is not 0
            if ((layerBitmap!=null) && (originalTimestamp!=0)){
                timestamp = originalTimestamp;
            }
        }
        if (layerBitmap!=null){
            Bitmap targetBitmap = layerBitmap.copy(Bitmap.Config.ARGB_8888,true);
            Canvas canvasVisibleMap = new Canvas(targetBitmap);
            if (layerBitmap!=null){
                int outlineColor = ThemePicker.getWidgetTextColor(context);
                if (atop!=null){
                    final Paint cp = new Paint();
                    cp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                    for (int i=0; i<atop.length; i++){
                        WeatherLayer atopLayer = new WeatherLayer(atop[i]);
                        Bitmap atopBitmap = null;
                        if (atopLayer.layer!=layer){
                            atopBitmap = atopLayer.getTransparentLayerBitmap(context,outlineColor);
                        }
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
        if (bitmap!=null){
            int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
            bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
            final int lineColor = ThemePicker.getWidgetTextColor(context);
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
        return null;
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

    public void saveLayerBitmapToCache(Context context, Bitmap bitmap){
        if (bitmap!=null){
            try {
                File cacheDir = context.getCacheDir();
                File targetFile = new File(cacheDir,getCacheFilename());
                FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
                bitmap.compress(Bitmap.CompressFormat.PNG,0,fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e){
                // fails silently
            }
        }
    }

    public boolean isPollenLayerCacheFileOutdated(Context context){
        File cacheDir = context.getCacheDir();
        File targetFile = new File(cacheDir,getCacheFilename());
        long modified = targetFile.lastModified();
        if (modified<Pollen.getLastPollenUpdateTime(context)){
            return true;
        }
        return false;
    }

    private boolean cacheFileExists(Context context){
        File cacheDir = context.getCacheDir();
        File targetFile = new File(cacheDir,getCacheFilename());
        return targetFile.exists();
    }


    public boolean isPollen(){
        return ((layer>=Layers.POLLEN_FORECAST_AMBROSIA_0) && (layer<=Layers.POLLEN_FORECAST_GRAESER_2));
    }

}
