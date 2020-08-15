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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.ArrayList;

public class WeatherSettings {

    public static final String PREF_STATION_NAME = "PREF_station_name";
    public static final String PREF_STATION_DESCRIPTION = "PREF_station_description";
    public static final String PREF_STATION_LONGITUDE = "PREF_station_longitude";
    public static final String PREF_STATION_LATIDTUDE = "PREF_station_latitude";
    public static final String PREF_STATION_ALTITUDE  = "PREF_station_altitude";
    public static final String PREF_DISPLAY_STATION_GEO = "PREF_display_station_geo";
    public static final String PREF_SETALARM = "PREF_setalarm";
    public static final String PREF_UPDATEINTERVAL = "PREF_updateinterval";
    public static final String PREF_AGGRESSIVE_UPDATE = "PREF_aggressive_update";
    public static final String PREF_ISWEATHERPROVIDER = "PREF_isregisteredweatherprovider";
    public static final String PREF_WIDGET_OPACITY = "PREF_widget_opacity";
    public static final String PREF_WIDGET_SHOWDWDNOTE = "PREF_widget_showdwdnote";
    public static final String PREF_LAST_VERSION_CODE = "PREF_last_version_code";
    public static final String PREF_SERVE_GADGETBRIDGE = "PREF_serve_gadgetbridge";
    public static final String PREF_GADGETBRIDGE_LAST_UPDATE_TIME = "PREF_gadgetbridge_last_update_time";
    public static final String PREF_GADGETBRIDGE_PACKAGENAME = "PREF_gadgetbridge_packagename";
    public static final String PREF_LOGGING = "PREF_logging";
    public static final String PREF_FAVORITESDATA = "PREF_favoritesdata";

    public static final String PREF_STATION_NAME_DEFAULT = "P0489";
    public static final String PREF_STATION_DESCRIPTION_DEFAULT = "HAMBURG INNENSTADT";
    public static final double PREF_STATION_LONGITUDE_DEFAULT = 9.98;
    public static final double PREF_STATION_LATIDTUDE_DEFAULT = 53.55;
    public static final double PREF_STATION_ALTITUDE_DEFAULT  = 8.0;
    public static final boolean PREF_DISPLAY_STATION_GEO_DEFAULT = true;
    public static final boolean PREF_SETALARM_DEFAULT = false;
    public static final boolean PREF_AGGRESSIVE_UPDATE_DEFAULT = false;
    public static final String PREF_UPDATEINTERVAL_DEFAULT = "24";
    public static final String PREF_WIDGET_OPACITY_DEFAULT = "10";
    public static final boolean PREF_ISWEATHERPROVIDER_DEFAULT = false;
    public static final boolean PREF_WIDGET_SHOWDWDNOTE_DEFAULT = true;
    public final int PREF_LAST_VERSION_CODE_DEFAULT = 0;
    public static final boolean PREF_SERVE_GADGETBRIDGE_DEFAULT = false;
    public static final long PREF_GADGETBRIDGE_LAST_UPDATE_TIME_DEFAULT = 0;
    public static final String PREF_GADGETBRIDGE_PACKAGENAME_DEFAULT = "nodomain.freeyourgadget.gadgetbridge";
    public static final boolean PREF_LOGGING_DEFAULT = false;
    public static final String PREF_FAVORITESDATA_DEFAULT = PREF_STATION_DESCRIPTION_DEFAULT;

    public String station_description = PREF_STATION_DESCRIPTION_DEFAULT;
    public String station_name = PREF_STATION_NAME_DEFAULT;
    public double station_longitude = PREF_STATION_LONGITUDE_DEFAULT;
    public double station_latitude = PREF_STATION_LATIDTUDE_DEFAULT;
    public double station_altitude = PREF_STATION_LATIDTUDE_DEFAULT;
    public boolean display_station_geo = PREF_DISPLAY_STATION_GEO_DEFAULT;
    public boolean setalarm = PREF_SETALARM_DEFAULT;
    public boolean aggressive_update = PREF_AGGRESSIVE_UPDATE_DEFAULT;
    public String updateinterval = PREF_UPDATEINTERVAL_DEFAULT;
    public boolean is_weatherprovider = PREF_ISWEATHERPROVIDER_DEFAULT;
    public String widget_opacity = PREF_WIDGET_OPACITY_DEFAULT;
    public boolean widget_showdwdnote = PREF_WIDGET_SHOWDWDNOTE_DEFAULT;
    public int last_version_code = PREF_LAST_VERSION_CODE_DEFAULT;
    public boolean serve_gadgetbridge = PREF_SERVE_GADGETBRIDGE_DEFAULT;
    public long gadgetbridge_last_update_time;
    public String gadgetbridge_packagename = PREF_GADGETBRIDGE_PACKAGENAME_DEFAULT;
    public boolean logging = PREF_LOGGING_DEFAULT;
    public String favoritesdata = PREF_FAVORITESDATA_DEFAULT;

    private Context context;
    public SharedPreferences sharedPreferences;

    final static String FAVORITES_SEPERATOR = ";";

    public WeatherSettings(Context c){
        this.context = c;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        readPreferences();
    }

    public void readPreferences(){
        this.station_description = readPreference(PREF_STATION_DESCRIPTION,PREF_STATION_DESCRIPTION_DEFAULT);
        this.station_name = readPreference(PREF_STATION_NAME,PREF_STATION_NAME_DEFAULT);
        this.station_longitude = readPreference(PREF_STATION_LONGITUDE,PREF_STATION_LONGITUDE_DEFAULT);
        this.station_latitude = readPreference(PREF_STATION_LATIDTUDE,PREF_STATION_LATIDTUDE_DEFAULT);
        this.station_altitude = readPreference(PREF_STATION_ALTITUDE,PREF_STATION_ALTITUDE_DEFAULT);
        this.setalarm = readPreference(PREF_SETALARM,PREF_SETALARM_DEFAULT);
        this.display_station_geo = readPreference(PREF_DISPLAY_STATION_GEO,PREF_DISPLAY_STATION_GEO_DEFAULT);
        this.aggressive_update = readPreference(PREF_AGGRESSIVE_UPDATE,PREF_AGGRESSIVE_UPDATE_DEFAULT);
        this.updateinterval = readPreference(PREF_UPDATEINTERVAL,PREF_UPDATEINTERVAL_DEFAULT);
        this.is_weatherprovider = readPreference(PREF_ISWEATHERPROVIDER,PREF_ISWEATHERPROVIDER_DEFAULT);
        this.widget_opacity = readPreference(PREF_WIDGET_OPACITY,PREF_WIDGET_OPACITY_DEFAULT);
        this.widget_showdwdnote = readPreference(PREF_WIDGET_SHOWDWDNOTE,PREF_WIDGET_SHOWDWDNOTE_DEFAULT);
        this.last_version_code = readPreference(PREF_LAST_VERSION_CODE,PREF_LAST_VERSION_CODE_DEFAULT);
        this.serve_gadgetbridge = readPreference(PREF_SERVE_GADGETBRIDGE,PREF_SERVE_GADGETBRIDGE_DEFAULT);
        this.gadgetbridge_last_update_time = readPreference(PREF_GADGETBRIDGE_LAST_UPDATE_TIME,PREF_GADGETBRIDGE_LAST_UPDATE_TIME_DEFAULT);
        this.logging = readPreference(PREF_LOGGING,PREF_LOGGING_DEFAULT);
        this.favoritesdata = readPreference(PREF_FAVORITESDATA,PREF_FAVORITESDATA_DEFAULT);
   }

    public void savePreferences(){
        applyPreference(PREF_STATION_DESCRIPTION,this.station_description);
        applyPreference(PREF_STATION_NAME,this.station_name);
        applyPreference(PREF_STATION_LONGITUDE,this.station_longitude);
        applyPreference(PREF_STATION_LATIDTUDE,this.station_latitude);
        applyPreference(PREF_STATION_ALTITUDE,this.station_altitude);
        applyPreference(PREF_DISPLAY_STATION_GEO,this.display_station_geo);
        applyPreference(PREF_SETALARM,this.setalarm);
        applyPreference(PREF_AGGRESSIVE_UPDATE,this.aggressive_update);
        applyPreference(PREF_UPDATEINTERVAL,this.updateinterval);
        applyPreference(PREF_ISWEATHERPROVIDER,this.is_weatherprovider);
        applyPreference(PREF_WIDGET_OPACITY,this.widget_opacity);
        applyPreference(PREF_WIDGET_SHOWDWDNOTE,this.widget_showdwdnote);
        applyPreference(PREF_LAST_VERSION_CODE,this.last_version_code);
        applyPreference(PREF_SERVE_GADGETBRIDGE,this.serve_gadgetbridge);
        applyPreference(PREF_GADGETBRIDGE_LAST_UPDATE_TIME,this.gadgetbridge_last_update_time);
        applyPreference(PREF_LOGGING,this.logging);
    }

    public void commitPreferences(){
        commitPreference(PREF_STATION_DESCRIPTION,this.station_description);
        commitPreference(PREF_STATION_NAME,this.station_name);
        commitPreference(PREF_STATION_LONGITUDE,this.station_longitude);
        commitPreference(PREF_STATION_LATIDTUDE,this.station_latitude);
        commitPreference(PREF_STATION_ALTITUDE,this.station_altitude);
        commitPreference(PREF_DISPLAY_STATION_GEO,this.display_station_geo);
        commitPreference(PREF_SETALARM,this.setalarm);
        commitPreference(PREF_AGGRESSIVE_UPDATE,this.aggressive_update);
        commitPreference(PREF_UPDATEINTERVAL,this.updateinterval);
        commitPreference(PREF_ISWEATHERPROVIDER,this.is_weatherprovider);
        commitPreference(PREF_WIDGET_OPACITY,this.widget_opacity);
        commitPreference(PREF_WIDGET_SHOWDWDNOTE,this.widget_showdwdnote);
        commitPreference(PREF_LAST_VERSION_CODE,this.last_version_code);
        commitPreference(PREF_SERVE_GADGETBRIDGE,this.serve_gadgetbridge);
        commitPreference(PREF_GADGETBRIDGE_LAST_UPDATE_TIME,this.gadgetbridge_last_update_time);
        commitPreference(PREF_LOGGING,this.logging);
    }

    public String readPreference(String p, String d){
        return sharedPreferences.getString(p,d);
    }

    public Boolean readPreference(String p, Boolean d){
        return sharedPreferences.getBoolean(p,d);
    }

    public int readPreference(String p, int d){
        return sharedPreferences.getInt(p,d);
    }

    public float readPreference(String p, float d){
        return sharedPreferences.getFloat(p,d);
    }

    public long readPreference(String p, long d){
        return sharedPreferences.getLong(p,d);
    }

    public double readPreference(String p, double d){
        return (double) sharedPreferences.getFloat(p, (float) d);
    }

    public void applyPreference(String pref, String value){
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putString(pref,value);
        pref_editor.apply();
    }

    public void applyPreference(String pref, Boolean value){
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(pref,value);
        pref_editor.apply();
    }

    public void applyPreference(String pref, int value){
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(pref,value);
        pref_editor.apply();
    }

    public void applyPreference(String pref, float value){
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putFloat(pref,value);
        pref_editor.apply();
    }

    public void applyPreference(String pref, long value){
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putLong(pref,value);
        pref_editor.apply();
    }

    public void applyPreference(String pref, double value){
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        applyPreference(pref, (float) value);
        pref_editor.apply();
    }

    public void commitPreference(String pref, String value){
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putString(pref,value);
        pref_editor.commit();
    }

    public void commitPreference(String pref, Boolean value){
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(pref,value);
        pref_editor.commit();
    }

    public void commitPreference(String pref, int value){
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(pref,value);
        pref_editor.commit();
    }

    public void commitPreference(String pref, float value){
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putFloat(pref,value);
        pref_editor.commit();
    }

    public void commitPreference(String pref, long value){
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putLong(pref,value);
        pref_editor.commit();
    }

    public void commitPreference(String pref, double value){
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        applyPreference(pref, (float) value);
        pref_editor.commit();
    }

    public int getUpdateInterval(){
        int i = Integer.parseInt(this.updateinterval);
        return i;
    }

    public long getUpdateIntervalInMillis(){
        return getUpdateInterval()*60*60*1000;
    }

    public Weather.WeatherLocation getSetStationLocation(){
        Weather.WeatherLocation weatherLocation = new Weather.WeatherLocation();
        weatherLocation.description = this.station_description;
        weatherLocation.name = this.station_name;
        weatherLocation.longitude = this.station_longitude;
        weatherLocation.latitude = this.station_latitude;
        weatherLocation.altitude = this.station_altitude;
        return weatherLocation;
    }

    public void updateFavorites(ArrayList<String> favorites){
        String result = "";
        for (int i=0; i<favorites.size(); i++){
            result = result + favorites.get(i)+ FAVORITES_SEPERATOR;
        }
        applyPreference(PREF_FAVORITESDATA,result);
    }

    public ArrayList<String> getFavorites(){
        ArrayList<String> result = new ArrayList<String>();
        String[] split_descriptions = this.favoritesdata.split(FAVORITES_SEPERATOR);
        for (int i=0; i< split_descriptions.length; i++){
            result.add(split_descriptions[i]);
        }
        return result;
    }

}
