/*
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021 Pawel Dube
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
import java.util.Calendar;

public class WeatherSettings {

    final static String FAVORITES_SEPERATOR = ";";

    public final static int DISPLAYTYPE_1HOUR = 1;
    public final static int DISPLAYTYPE_6HOURS = 3;
    public final static int DISPLAYTYPE_MIXED = 256;

    public static final String PREF_STATION_NAME = "PREF_station_name";
    public static final String PREF_STATION_DESCRIPTION = "PREF_station_description";
    public static final String PREF_STATION_LONGITUDE = "PREF_station_longitude";
    public static final String PREF_STATION_LATIDTUDE = "PREF_station_latitude";
    public static final String PREF_STATION_ALTITUDE = "PREF_station_altitude";
    public static final String PREF_DISPLAY_STATION_GEO = "PREF_display_station_geo";
    public static final String PREF_DISPLAY_TYPE = "PREF_display_type";
    public static final String PREF_DISPLAY_LAYOUT = "PREF_display_layout";
    public static final String PREF_DISPLAY_BAR = "PREF_display_bar";
    public static final String PREF_DISPLAY_SIMPLE_BAR = "PREF_display_simple_bar";
    public static final String PREF_DISPLAY_PRESSURE = "PREF_display_pressure";
    public static final String PREF_DISPLAY_VISIBILITY = "PREF_display_visibility";
    public static final String PREF_DISPLAY_SUNRISE = "PREF_display_sunrise";
    public static final String PREF_DISPLAY_ENDOFDAY_BAR = "PREF_display_end_of_day_bar";
    public static final String PREF_DISPLAY_GRADIENT = "PREF_display_gradient";
    public static final String PREF_DISPLAY_WIND_TYPE = "PREF_display_wind_type";
    public static final String PREF_DISPLAY_WIND_UNIT = "PREF_display_wind_unit";
    public static final String PREF_DISPLAY_DISTANCE_UNIT = "PREF_display_distance_unit";
    public static final String PREF_SETALARM = "PREF_setalarm";
    public static final String PREF_UPDATEINTERVAL = "PREF_updateinterval";
    public static final String PREF_AGGRESSIVE_UPDATE = "PREF_aggressive_update";
    public static final String PREF_WIDGET_OPACITY = "PREF_widget_opacity";
    public static final String PREF_WIDGET_SHOWDWDNOTE = "PREF_widget_showdwdnote";
    public static final String PREF_LAST_VERSION_CODE = "PREF_last_version_code";
    public static final String PREF_SERVE_GADGETBRIDGE = "PREF_serve_gadgetbridge";
    public static final String PREF_VIEWS_LAST_UPDATE_TIME = "PREF_views_last_update_time";
    public static final String PREF_GADGETBRIDGE_PACKAGENAME = "PREF_gadgetbridge_packagename";
    public static final String PREF_GADGETBRIDGE_FAKE_TIMESTAMP = "PREF_gadgetbridge_fake_timestamp";
    public static final String PREF_LOGGING = "PREF_logging";
    public static final String PREF_LOG_TO_LOGCAT = "PREF_log_to_logcat";
    public static final String PREF_FAVORITESDATA = "PREF_favoritesdata";
    public static final String PREF_WARNINGS_CACHETIME = "PREF_warnings_cachetime";
    public static final String PREF_WARNINGS_DISABLE = "PREF_warnings_diable";
    public static final String PREF_WARNINGS_LAST_UPDATE_TIME = "PREF_warnings_last_update_time";
    public static final String PREF_IS_FIRST_APP_LAUNCH = "PREF_is_first_app_launch";
    public static final String PREF_USEGPS = "PREF_usegps";
    public static final String PREF_DISABLE_TLS = "PREF_disable_tls";

    public static final String PREF_STATION_NAME_DEFAULT = "P0489";
    public static final String PREF_STATION_DESCRIPTION_DEFAULT = "HAMBURG INNENSTADT";
    public static final double PREF_STATION_LONGITUDE_DEFAULT = 9.98;
    public static final double PREF_STATION_LATIDTUDE_DEFAULT = 53.55;
    public static final double PREF_STATION_ALTITUDE_DEFAULT = 8.0;
    public static final boolean PREF_DISPLAY_STATION_GEO_DEFAULT = true;
    public static final String PREF_DISPLAY_TYPE_DEFAULT = "3";
    public static final String PREF_DISPLAY_LAYOUT_DEFAULT = "0";
    public static final boolean PREF_DISPLAY_BAR_DEFAULT = true;
    public static final boolean PREF_DISPLAY_SIMPLE_BAR_DEFAULT = false;
    public static final boolean PREF_DISPLAY_PRESSURE_DEFAULT = true;
    public static final boolean PREF_DISPLAY_VISIBILITY_DEFAULT = true;
    public static final boolean PREF_DISPLAY_SUNRISE_DEFAULT = true;
    public static final boolean PREF_DISPLAY_ENDOFDAY_BAR_DEFAULT = true;
    public static final boolean PREF_DISPLAY_GRADIENT_DEFAULT = false;
    public static final String PREF_DISPLAY_WIND_TYPE_DEFAULT = "0";
    public static final String PREF_DISPLAY_WIND_UNIT_DEFAULT = "0";
    public static final String PREF_DISPLAY_DISTANCE_UNIT_DEFAULT = "0";
    public static final boolean PREF_SETALARM_DEFAULT = true;
    public static final boolean PREF_AGGRESSIVE_UPDATE_DEFAULT = false;
    public static final String PREF_UPDATEINTERVAL_DEFAULT = "24";
    public static final String PREF_WIDGET_OPACITY_DEFAULT = "10";
    public static final boolean PREF_WIDGET_SHOWDWDNOTE_DEFAULT = true;
    public final int PREF_LAST_VERSION_CODE_DEFAULT = 0;
    public static final boolean PREF_SERVE_GADGETBRIDGE_DEFAULT = false;
    public static final long PREF_VIEWS_LAST_UPDATE_TIME_DEFAULT = 0;
    public static final String PREF_GADGETBRIDGE_PACKAGENAME_DEFAULT = "nodomain.freeyourgadget.gadgetbridge";
    public static final boolean PREF_GADGETBRIDGE_FAKE_TIMESTAMP_DEFAULT = false;
    public static final boolean PREF_LOGGING_DEFAULT = false;
    public static final boolean PREF_LOG_TO_LOGCAT_DEFAULT = false;
    public static final String PREF_FAVORITESDATA_DEFAULT = PREF_STATION_DESCRIPTION_DEFAULT;
    public static final String PREF_WARNINGS_CACHETIME_DEFAULT = "30";
    public static final boolean PREF_WARNINGS_DISABLE_DEFAULT = false;
    public static final long PREF_WARNINGS_LAST_UPDATE_TIME_DEFAULT = 0;
    public static final boolean PREF_IS_FIRST_APP_LAUNCH_DEFAULT = true;
    public static final boolean PREF_USEGPS_DEFAULT = false;
    public static final boolean PREF_DISABLE_TLS_DEFAULT = false;

    public String station_description = PREF_STATION_DESCRIPTION_DEFAULT;
    public String station_name = PREF_STATION_NAME_DEFAULT;
    public double station_longitude = PREF_STATION_LONGITUDE_DEFAULT;
    public double station_latitude = PREF_STATION_LATIDTUDE_DEFAULT;
    public double station_altitude = PREF_STATION_LATIDTUDE_DEFAULT;
    public boolean display_station_geo = PREF_DISPLAY_STATION_GEO_DEFAULT;
    public String display_type = PREF_DISPLAY_TYPE_DEFAULT;
    public String display_layout = PREF_DISPLAY_LAYOUT_DEFAULT;
    public boolean display_bar = PREF_DISPLAY_BAR_DEFAULT;
    public boolean display_simple_bar = PREF_DISPLAY_SIMPLE_BAR_DEFAULT;
    public boolean display_pressure = PREF_DISPLAY_PRESSURE_DEFAULT;
    public boolean display_visibility = PREF_DISPLAY_VISIBILITY_DEFAULT;
    public boolean display_sunrise = PREF_DISPLAY_SUNRISE_DEFAULT;
    public boolean display_endofday_bar = PREF_DISPLAY_ENDOFDAY_BAR_DEFAULT;
    public boolean display_gradient = PREF_DISPLAY_GRADIENT_DEFAULT;
    public String display_wind_type = PREF_DISPLAY_WIND_TYPE_DEFAULT;
    public String display_wind_unit = PREF_DISPLAY_WIND_UNIT_DEFAULT;
    public String display_distance_unit = PREF_DISPLAY_DISTANCE_UNIT_DEFAULT;
    public boolean setalarm = PREF_SETALARM_DEFAULT;
    public boolean aggressive_update = PREF_AGGRESSIVE_UPDATE_DEFAULT;
    public String updateinterval = PREF_UPDATEINTERVAL_DEFAULT;
    public String widget_opacity = PREF_WIDGET_OPACITY_DEFAULT;
    public boolean widget_showdwdnote = PREF_WIDGET_SHOWDWDNOTE_DEFAULT;
    public int last_version_code = PREF_LAST_VERSION_CODE_DEFAULT;
    public boolean serve_gadgetbridge = PREF_SERVE_GADGETBRIDGE_DEFAULT;
    public long views_last_update_time = PREF_VIEWS_LAST_UPDATE_TIME_DEFAULT;
    public long warnings_last_update_time = PREF_WARNINGS_LAST_UPDATE_TIME_DEFAULT;
    public String gadgetbridge_packagename = PREF_GADGETBRIDGE_PACKAGENAME_DEFAULT;
    public boolean gadgetbridge_fake_timestamp = PREF_GADGETBRIDGE_FAKE_TIMESTAMP_DEFAULT;
    public boolean logging = PREF_LOGGING_DEFAULT;
    public boolean log_to_logcat = PREF_LOG_TO_LOGCAT_DEFAULT;
    public String favoritesdata = PREF_FAVORITESDATA_DEFAULT;
    public String warnings_cache_time = PREF_WARNINGS_CACHETIME_DEFAULT;
    public boolean warnings_disabled = PREF_WARNINGS_DISABLE_DEFAULT;
    public boolean is_first_app_launch = true;
    public boolean usegps = PREF_USEGPS_DEFAULT;
    public boolean disable_tls = PREF_DISABLE_TLS_DEFAULT;

    private Context context;
    public SharedPreferences sharedPreferences;

    public WeatherSettings(Context c) {
        this.context = c;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        readPreferences();
    }

    public void readPreferences() {
        this.station_description = readPreference(PREF_STATION_DESCRIPTION, PREF_STATION_DESCRIPTION_DEFAULT);
        this.station_name = readPreference(PREF_STATION_NAME, PREF_STATION_NAME_DEFAULT);
        this.station_longitude = readPreference(PREF_STATION_LONGITUDE, PREF_STATION_LONGITUDE_DEFAULT);
        this.station_latitude = readPreference(PREF_STATION_LATIDTUDE, PREF_STATION_LATIDTUDE_DEFAULT);
        this.station_altitude = readPreference(PREF_STATION_ALTITUDE, PREF_STATION_ALTITUDE_DEFAULT);
        this.setalarm = readPreference(PREF_SETALARM, PREF_SETALARM_DEFAULT);
        this.display_station_geo = readPreference(PREF_DISPLAY_STATION_GEO, PREF_DISPLAY_STATION_GEO_DEFAULT);
        this.display_type = readPreference(PREF_DISPLAY_TYPE, PREF_DISPLAY_TYPE_DEFAULT);
        this.display_layout = readPreference(PREF_DISPLAY_LAYOUT,PREF_DISPLAY_LAYOUT_DEFAULT);
        this.display_bar = readPreference(PREF_DISPLAY_BAR, PREF_DISPLAY_BAR_DEFAULT);
        this.display_simple_bar = readPreference(PREF_DISPLAY_SIMPLE_BAR,PREF_DISPLAY_SIMPLE_BAR_DEFAULT);
        this.display_pressure = readPreference(PREF_DISPLAY_PRESSURE, PREF_DISPLAY_PRESSURE_DEFAULT);
        this.display_visibility = readPreference(PREF_DISPLAY_VISIBILITY, PREF_DISPLAY_VISIBILITY_DEFAULT);
        this.display_sunrise = readPreference(PREF_DISPLAY_SUNRISE, PREF_DISPLAY_SUNRISE_DEFAULT);
        this.display_endofday_bar = readPreference(PREF_DISPLAY_ENDOFDAY_BAR, PREF_DISPLAY_ENDOFDAY_BAR_DEFAULT);
        this.display_gradient = readPreference(PREF_DISPLAY_GRADIENT, PREF_DISPLAY_GRADIENT_DEFAULT);
        this.display_wind_type = readPreference(PREF_DISPLAY_WIND_TYPE,PREF_DISPLAY_WIND_TYPE_DEFAULT);
        this.display_wind_unit = readPreference(PREF_DISPLAY_WIND_UNIT,PREF_DISPLAY_WIND_TYPE_DEFAULT);
        this.display_distance_unit = readPreference(PREF_DISPLAY_DISTANCE_UNIT,PREF_DISPLAY_DISTANCE_UNIT_DEFAULT);
        this.aggressive_update = readPreference(PREF_AGGRESSIVE_UPDATE, PREF_AGGRESSIVE_UPDATE_DEFAULT);
        this.updateinterval = readPreference(PREF_UPDATEINTERVAL, PREF_UPDATEINTERVAL_DEFAULT);
        this.widget_opacity = readPreference(PREF_WIDGET_OPACITY, PREF_WIDGET_OPACITY_DEFAULT);
        this.widget_showdwdnote = readPreference(PREF_WIDGET_SHOWDWDNOTE, PREF_WIDGET_SHOWDWDNOTE_DEFAULT);
        this.last_version_code = readPreference(PREF_LAST_VERSION_CODE, PREF_LAST_VERSION_CODE_DEFAULT);
        this.serve_gadgetbridge = readPreference(PREF_SERVE_GADGETBRIDGE, PREF_SERVE_GADGETBRIDGE_DEFAULT);
        this.views_last_update_time = readPreference(PREF_VIEWS_LAST_UPDATE_TIME, PREF_VIEWS_LAST_UPDATE_TIME_DEFAULT);
        this.gadgetbridge_fake_timestamp = readPreference(PREF_GADGETBRIDGE_FAKE_TIMESTAMP, PREF_GADGETBRIDGE_FAKE_TIMESTAMP_DEFAULT);
        this.logging = readPreference(PREF_LOGGING, PREF_LOGGING_DEFAULT);
        this.log_to_logcat = readPreference(PREF_LOG_TO_LOGCAT, PREF_LOG_TO_LOGCAT_DEFAULT);
        this.favoritesdata = readPreference(PREF_FAVORITESDATA, PREF_FAVORITESDATA_DEFAULT);
        this.warnings_cache_time = readPreference(PREF_WARNINGS_CACHETIME, PREF_WARNINGS_CACHETIME_DEFAULT);
        this.warnings_disabled = readPreference(PREF_WARNINGS_DISABLE, PREF_WARNINGS_DISABLE_DEFAULT);
        this.warnings_last_update_time = readPreference(PREF_WARNINGS_LAST_UPDATE_TIME, PREF_WARNINGS_LAST_UPDATE_TIME_DEFAULT);
        this.is_first_app_launch = readPreference(PREF_IS_FIRST_APP_LAUNCH, PREF_IS_FIRST_APP_LAUNCH_DEFAULT);
        this.usegps = readPreference(PREF_USEGPS,PREF_USEGPS_DEFAULT);
        this.disable_tls = readPreference(PREF_DISABLE_TLS,PREF_DISABLE_TLS_DEFAULT);
    }

    public void savePreferences() {
        applyPreference(PREF_STATION_DESCRIPTION, this.station_description);
        applyPreference(PREF_STATION_NAME, this.station_name);
        applyPreference(PREF_STATION_LONGITUDE, this.station_longitude);
        applyPreference(PREF_STATION_LATIDTUDE, this.station_latitude);
        applyPreference(PREF_STATION_ALTITUDE, this.station_altitude);
        applyPreference(PREF_DISPLAY_STATION_GEO, this.display_station_geo);
        applyPreference(PREF_DISPLAY_TYPE, this.display_type);
        applyPreference(PREF_DISPLAY_LAYOUT,this.display_layout);
        applyPreference(PREF_DISPLAY_BAR, this.display_bar);
        applyPreference(PREF_DISPLAY_SIMPLE_BAR,this.display_simple_bar);
        applyPreference(PREF_DISPLAY_PRESSURE, this.display_pressure);
        applyPreference(PREF_DISPLAY_VISIBILITY, this.display_visibility);
        applyPreference(PREF_DISPLAY_SUNRISE, this.display_sunrise);
        applyPreference(PREF_DISPLAY_ENDOFDAY_BAR, this.display_endofday_bar);
        applyPreference(PREF_DISPLAY_GRADIENT, this.display_gradient);
        applyPreference(PREF_DISPLAY_WIND_TYPE,this.display_wind_type);
        applyPreference(PREF_DISPLAY_WIND_UNIT,this.display_wind_unit);
        applyPreference(PREF_DISPLAY_DISTANCE_UNIT,this.display_distance_unit);
        applyPreference(PREF_SETALARM, this.setalarm);
        applyPreference(PREF_AGGRESSIVE_UPDATE, this.aggressive_update);
        applyPreference(PREF_UPDATEINTERVAL, this.updateinterval);
        applyPreference(PREF_WIDGET_OPACITY, this.widget_opacity);
        applyPreference(PREF_WIDGET_SHOWDWDNOTE, this.widget_showdwdnote);
        applyPreference(PREF_LAST_VERSION_CODE, this.last_version_code);
        applyPreference(PREF_SERVE_GADGETBRIDGE, this.serve_gadgetbridge);
        applyPreference(PREF_GADGETBRIDGE_FAKE_TIMESTAMP, this.gadgetbridge_fake_timestamp);
        applyPreference(PREF_VIEWS_LAST_UPDATE_TIME, this.views_last_update_time);
        applyPreference(PREF_LOGGING, this.logging);
        applyPreference(PREF_LOG_TO_LOGCAT, this.log_to_logcat);
        applyPreference(PREF_WARNINGS_CACHETIME, this.warnings_cache_time);
        applyPreference(PREF_WARNINGS_DISABLE, this.warnings_disabled);
        applyPreference(PREF_WARNINGS_LAST_UPDATE_TIME, this.warnings_last_update_time);
        applyPreference(PREF_IS_FIRST_APP_LAUNCH, this.is_first_app_launch);
        applyPreference(PREF_USEGPS,this.usegps);
        applyPreference(PREF_DISABLE_TLS,this.disable_tls);
    }

    public void commitPreferences() {
        commitPreference(PREF_STATION_DESCRIPTION, this.station_description);
        commitPreference(PREF_STATION_NAME, this.station_name);
        commitPreference(PREF_STATION_LONGITUDE, this.station_longitude);
        commitPreference(PREF_STATION_LATIDTUDE, this.station_latitude);
        commitPreference(PREF_STATION_ALTITUDE, this.station_altitude);
        commitPreference(PREF_DISPLAY_STATION_GEO, this.display_station_geo);
        commitPreference(PREF_DISPLAY_TYPE, this.display_type);
        commitPreference(PREF_DISPLAY_LAYOUT,this.display_layout);
        commitPreference(PREF_DISPLAY_BAR, this.display_bar);
        commitPreference(PREF_DISPLAY_SIMPLE_BAR,this.display_simple_bar);
        commitPreference(PREF_DISPLAY_PRESSURE, this.display_pressure);
        commitPreference(PREF_DISPLAY_VISIBILITY, this.display_visibility);
        commitPreference(PREF_DISPLAY_SUNRISE, this.display_sunrise);
        commitPreference(PREF_DISPLAY_ENDOFDAY_BAR, this.display_endofday_bar);
        commitPreference(PREF_DISPLAY_GRADIENT, this.display_gradient);
        commitPreference(PREF_DISPLAY_WIND_TYPE,this.display_wind_type);
        commitPreference(PREF_DISPLAY_WIND_UNIT,this.display_wind_unit);
        commitPreference(PREF_DISPLAY_DISTANCE_UNIT,this.display_distance_unit);
        commitPreference(PREF_SETALARM, this.setalarm);
        commitPreference(PREF_AGGRESSIVE_UPDATE, this.aggressive_update);
        commitPreference(PREF_UPDATEINTERVAL, this.updateinterval);
        commitPreference(PREF_WIDGET_OPACITY, this.widget_opacity);
        commitPreference(PREF_WIDGET_SHOWDWDNOTE, this.widget_showdwdnote);
        commitPreference(PREF_LAST_VERSION_CODE, this.last_version_code);
        commitPreference(PREF_SERVE_GADGETBRIDGE, this.serve_gadgetbridge);
        commitPreference(PREF_GADGETBRIDGE_FAKE_TIMESTAMP, this.gadgetbridge_fake_timestamp);
        commitPreference(PREF_VIEWS_LAST_UPDATE_TIME, this.views_last_update_time);
        commitPreference(PREF_LOGGING, this.logging);
        commitPreference(PREF_LOG_TO_LOGCAT, this.log_to_logcat);
        commitPreference(PREF_WARNINGS_CACHETIME, this.warnings_cache_time);
        commitPreference(PREF_WARNINGS_DISABLE, this.warnings_disabled);
        commitPreference(PREF_WARNINGS_LAST_UPDATE_TIME, this.warnings_last_update_time);
        commitPreference(PREF_IS_FIRST_APP_LAUNCH, this.is_first_app_launch);
        commitPreference(PREF_USEGPS,this.usegps);
        commitPreference(PREF_DISABLE_TLS,this.disable_tls);
    }

    public String readPreference(String p, String d) {
        return sharedPreferences.getString(p, d);
    }

    public Boolean readPreference(String p, Boolean d) {
        return sharedPreferences.getBoolean(p, d);
    }

    public int readPreference(String p, int d) {
        return sharedPreferences.getInt(p, d);
    }

    public float readPreference(String p, float d) {
        return sharedPreferences.getFloat(p, d);
    }

    public long readPreference(String p, long d) {
        return sharedPreferences.getLong(p, d);
    }

    public double readPreference(String p, double d) {
        return (double) sharedPreferences.getFloat(p, (float) d);
    }

    public void applyPreference(String pref, String value) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putString(pref, value);
        pref_editor.apply();
    }

    public void applyPreference(String pref, Boolean value) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(pref, value);
        pref_editor.apply();
    }

    public void applyPreference(String pref, int value) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(pref, value);
        pref_editor.apply();
    }

    public void applyPreference(String pref, float value) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putFloat(pref, value);
        pref_editor.apply();
    }

    public void applyPreference(String pref, long value) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putLong(pref, value);
        pref_editor.apply();
    }

    public void applyPreference(String pref, double value) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        applyPreference(pref, (float) value);
        pref_editor.apply();
    }

    public void commitPreference(String pref, String value) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putString(pref, value);
        pref_editor.commit();
    }

    public void commitPreference(String pref, Boolean value) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(pref, value);
        pref_editor.commit();
    }

    public void commitPreference(String pref, int value) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(pref, value);
        pref_editor.commit();
    }

    public void commitPreference(String pref, float value) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putFloat(pref, value);
        pref_editor.commit();
    }

    public void commitPreference(String pref, long value) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putLong(pref, value);
        pref_editor.commit();
    }

    public void commitPreference(String pref, double value) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        applyPreference(pref, (float) value);
        pref_editor.commit();
    }

    public int getUpdateInterval() {
        int i = Integer.parseInt(this.updateinterval);
        return i;
    }

    public long getUpdateIntervalInMillis() {
        return getUpdateInterval() * 60 * 60 * 1000;
    }

    public Weather.WeatherLocation getSetStationLocation() {
        Weather.WeatherLocation weatherLocation = new Weather.WeatherLocation();
        weatherLocation.description = this.station_description;
        weatherLocation.name = this.station_name;
        weatherLocation.longitude = this.station_longitude;
        weatherLocation.latitude = this.station_latitude;
        weatherLocation.altitude = this.station_altitude;
        return weatherLocation;
    }

    public void updateFavorites(ArrayList<String> favorites) {
        String result = "";
        for (int i = 0; i < favorites.size(); i++) {
            result = result + favorites.get(i) + FAVORITES_SEPERATOR;
        }
        applyPreference(PREF_FAVORITESDATA, result);
    }

    public ArrayList<String> getFavorites() {
        ArrayList<String> result = new ArrayList<String>();
        String[] split_descriptions = this.favoritesdata.split(FAVORITES_SEPERATOR);
        for (int i = 0; i < split_descriptions.length; i++) {
            result.add(split_descriptions[i]);
        }
        // if favorites are corrupted, simply reset to display current station only and return default
        if (split_descriptions.length==0){
            result.add(this.station_description);
            this.favoritesdata = this.station_description;
            applyPreference(PREF_FAVORITESDATA,this.favoritesdata);
        }
        return result;
    }

    public int getDisplayType() {
        try {
            int i = Integer.parseInt(this.display_type);
            return i;
        } catch (NumberFormatException e) {
            // return to default if entry is corrupted (not a number)
            this.display_type = PREF_DISPLAY_TYPE_DEFAULT;
            applyPreference(PREF_DISPLAY_TYPE, display_type);
            // return default
            return DISPLAYTYPE_6HOURS;
        }
    }

    public long getWarningsCacheTimeInMillis() {
        long l = Long.parseLong(this.warnings_cache_time);
        l = l * 60 * 1000;
        return l;
    }

    public long getWarningsLastUpdateTime() {
        return warnings_last_update_time;
    }

    public void setWarningsLastUpdateTime(long time) {
        applyPreference(PREF_WARNINGS_LAST_UPDATE_TIME, time);
    }

    public void setWarningsLastUpdateTime() {
        setWarningsLastUpdateTime(Calendar.getInstance().getTimeInMillis());
    }

    public static boolean isFirstAppLaunch(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getBoolean(PREF_IS_FIRST_APP_LAUNCH, PREF_IS_FIRST_APP_LAUNCH_DEFAULT);
    }

    public static void setAppLaunchedFlag(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_IS_FIRST_APP_LAUNCH, false);
        pref_editor.apply();
    }

    public static boolean getUseGPSFlag(Context c){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getBoolean(PREF_USEGPS, PREF_USEGPS_DEFAULT);
    }

    public static void setUSEGPSFlag(Context c, boolean flag){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_USEGPS, flag);
        pref_editor.apply();
    }

    public static boolean isTLSdisabled(Context c){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getBoolean(PREF_DISABLE_TLS, PREF_DISABLE_TLS_DEFAULT);
    }

    public static void setDisableTLS(Context c, boolean flag){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_DISABLE_TLS, flag);
        pref_editor.apply();
    }

    public static boolean appReleaseIsUserdebug() {
        return BuildConfig.VERSION_NAME.contains("debug");
    }

    public int getDisplayLayout(){
        if (this.display_layout.equals("0")){
            return Weather.DisplayLayout.DEFAULT;
        }
        return Weather.DisplayLayout.DEFAULT;
    }

    public int getWindDisplayType(){
        try {
            int i = Integer.parseInt(this.display_wind_type);
            return i;
        } catch (NumberFormatException e) {
            // return to default if entry is corrupted (not a number)
            this.display_wind_type = PREF_DISPLAY_WIND_TYPE_DEFAULT;
            applyPreference(PREF_DISPLAY_WIND_TYPE, display_wind_type);
            // return default
            return Weather.WindDisplayType.ARROW;
        }
    }

    public int getWindDisplayUnit(){
        try {
            int i = Integer.parseInt(this.display_wind_unit);
            return i;
        } catch (NumberFormatException e) {
            // return to default if entry is corrupted (not a number)
            this.display_wind_unit = PREF_DISPLAY_WIND_UNIT_DEFAULT;
            applyPreference(PREF_DISPLAY_WIND_UNIT, display_wind_unit);
            // return default
            return Weather.WindDisplayUnit.METERS_PER_SECOND;
        }
    }

    public int getDistanceDisplayUnit(){
        try {
            int i = Integer.parseInt(this.display_distance_unit);
            return i;
        } catch (NumberFormatException e) {
            // return to default if entry is corrupted (not a number)
            this.display_distance_unit = PREF_DISPLAY_DISTANCE_UNIT_DEFAULT;
            applyPreference(PREF_DISPLAY_DISTANCE_UNIT, display_distance_unit);
            // return default
            return Weather.DistanceDisplayUnit.METRIC;
        }
    }

}
