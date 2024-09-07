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

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

public class WeatherSettings {

    final static class NotificationIDRange {
        //  2147483601 to 2147483647 are reserved for other unique IDs
        final static int MIN = -2147483648;
        final static int MAX = 2147483600;
    }

    final static class StaticNotifationIDs {
        public static final int SERVICE_NOTIFICATION_IDENTIFIER = 2147483601;
    }

    final public static int[] NotificationLEDcolors =
                   {0xfff0e800,0xffffb300,0xffff5100,0xffff0000,
                    0xffa1ff00,0xff26ff00,0xff06ad00,0xff00ad9c,
                    0xff4dffed,0xff4daaff,0xff0073d1,0xff000899,
                    0xffff1f71,0xffd100a0,0xff8800a3,0xffdddddd};

    final static String FAVORITES_SEPERATOR = ";";

    public final static int DISPLAYTYPE_1HOUR = 1;
    public final static int DISPLAYTYPE_6HOURS = 3;
    public final static int DISPLAYTYPE_24HOURS = 4;
    public final static int DISPLAYTYPE_MIXED = 256;

    public static final String PREF_CATEGORY_GENERAL = "PREF_category_general";

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
    public static final String PREF_DISPLAY_WIND_ARC="PREF_display_wind_arc";
    public static final String PREF_DISPLAY_WIND_ARC_PERIOD="PREF_display_wind_arc_period";
    public static final String PREF_DISPLAY_WIND_TYPE = "PREF_display_wind_type";
    public static final String PREF_DISPLAY_WIND_UNIT = "PREF_display_wind_unit";
    public static final String PREF_DISPLAY_DISTANCE_UNIT = "PREF_display_distance_unit";
    public static final String PREF_DISPLAY_CROP_PRECIPITATIONCHART = "PREF_crop_precipchart";
    public static final String PREF_DISPLAY_OVERVIEWCHART = "PREF_display_overviewchart";
    public static final String PREF_DISPLAY_OVERVIEWCHART_DAYS="PREF_display_overviewchart_days";
    public static final String PREF_DISPLAY_OVERVIEWCHART_MINMAXUSE = "PREF_display_overviewchart_mmu";
    public static final String PREF_DISPLAY_OVERVIEWCHART_MIN = "PREF_display_overviewchart_min";
    public static final String PREF_DISPLAY_OVERVIEWCHART_MAX = "PREF_display_overviewchart_max";
    public static final String PREF_WIDGET_OPACITY = "PREF_widget_opacity";
    public static final String PREF_WIDGET_SHOWDWDNOTE = "PREF_widget_showdwdnote";
    public static final String PREF_WIDGET_DISPLAYWARNINGS = "PREF_widget_displaywarnings";
    public static final String PREF_NOTIFY_WARNINGS = "PREF_notify_warnings";
    public static final String PREF_LAST_VERSION_CODE = "PREF_last_version_code";
    public static final String PREF_SERVE_GADGETBRIDGE = "PREF_serve_gadgetbridge";
    public static final String PREF_GADGETBRIDGE_PACKAGENAME = "PREF_gadgetbridge_packagename";
    public static final String PREF_GADGETBRIDGE_FAKE_TIMESTAMP = "PREF_gadgetbridge_fake_timestamp";
    public static final String PREF_LOGGING = "PREF_logging";
    public static final String PREF_LOG_TO_LOGCAT = "PREF_log_to_logcat";
    public static final String PREF_FAVORITESDATA2 = "PREF_favoritesdata2";
    public static final String PREF_WARNINGS_DISABLE = "PREF_warnings_diable";
    public static final String PREF_WARNINGS_NOTIFY_SEVERITY = "PREF_warnings_notify_severity";
    public static final String PREF_IS_FIRST_APP_LAUNCH = "PREF_is_first_app_launch";
    public static final String PREF_USEGPS = "PREF_usegps";
    public static final String PREF_GPSAUTO = "PREF_gpsauto";
    public static final String PREF_GPSMANUAL = "PREF_gpsmanual";
    public static final String PREF_DISABLE_TLS = "PREF_disable_tls";
    public static final String PREF_TEXTFORECAST_FILTER = "PREF_textforecast_filter";
    public static final String PREF_RADAR_SHOW = "PREF_radar_show";
    public static final String PREF_ADMINMAP_SHOW="PREF_adminmap_show";
    public static final String PREF_MAP_DISPLAY_MUNICIPALITIES ="PREF_map_display_municipalities";
    public static final String PREF_MAP_DISPLAY_COUNTIES = "PREF_map_display_counties";
    public static final String PREF_MAP_DISPLAY_STATES = "PREF_map_display_states";
    public static final String PREF_MAP_DISPLAY_SEA_AREAS = "PREF_map_display_sea_areas";
    public static final String PREF_MAP_DISPLAY_COAST_AREAS = "PREF_map_display_coast_areas";
    public static final String PREF_FORCE_NO_MENU_ICONS = "PREF_force_nomenuicons";
    public static final String PREF_DISPLAY_WIND_IN_RADAR = "PREF_display_wind_in_radar";
    public static final String PREF_AREA_DATABASE_READY = "PREF_area_database_ready";
    public static final String PREF_AREA_DATABASE_VERSION = "PREF_area_database_version";
    public static final String PREF_VIEWMODEL = "PREF_viewmodel";
    public static final String PREF_THEME = "PREF_theme";
    public static final String PREF_ALTERNATIVE_ICONS = "PREF_alternative_icons";
    public static final String PREF_USE_METERED_NETWORKS = "PREF_use_metered_networks";
    public static final String PREF_USE_WIFI_ONLY ="PREF_use_wifi_only";
    public static final String PREF_CLEARNOTIFICATIONS = "PREF_clearnotifications"; // has no value
    public static final String PREF_ASKEDFORLOCATIONFLAG = "PREF_askedlocpermflag";
    public static final String PREF_ROTATIONMODE = "PREF_rotationmode";
    public static final String PREF_LED_COLOR = "PREF_led_color";
    public static final String PREF_WARNINGS_NOTIFY_LED = "PREF_warnings_notify_LED";
    public static final String PREF_HINTCOUNTER1 = "PREF_hintcounter1";
    public static final String PREF_HINTCOUNTER2 = "PREF_hintcounter2";
    public static final String PREF_LASTMAPDISPLAYED = "PREF_lastmapdisplayed";
    public static final String PREF_POLLEN_AMBROSIA = "PREF_pollen_ambrosia";
    public static final String PREF_POLLEN_BEIFUSS = "PREF_pollen_beifuss";
    public static final String PREF_POLLEN_ROGGEN = "PREF_pollen_roggen";
    public static final String PREF_POLLEN_ESCHE = "PREF_pollen_esche";
    public static final String PREF_POLLEN_BIRKE = "PREF_pollen_birke";
    public static final String PREF_POLLEN_HASEL = "PREF_pollen_hasel";
    public static final String PREF_POLLEN_ERLE = "PREF_pollen_erle";
    public static final String PREF_POLLEN_GRAESER = "PREF_pollen_graeser";
    public static final String PREF_PREFETCH_MAPS = "PREF_prefetch_maps";
    public static final String PREF_LAST_PREFETCH_TIME = "PREF_prefetch_time";
    public static final String PREF_UVHI_FETCH_DATA ="PREF_uvhi_fetch_data";
    public static final String PREF_UVHI_MAINDISPLAY="PREF_uvhi_maindisplay";
    public static final String PREF_MAX_LOCATIONS_IN_SHARED_WARNINGS = "PREF_max_loc_in_shared_warnings";
    public static final String PREF_USE_BACKGROUND_LOCATION = "PREF_use_backgr_location";
    public static final String PREF_DISPLAY_WIND_IN_CHARTS = "PREF_wind_in_charts";
    public static final String PREF_DISPLAY_WIND_IN_CHARTS_MAX = "PREF_wind_in_charts_max";
    public static final String PREF_REPLACE_BY_MUNICIPALITY = "PREF_replace_by_municipality";
    public static final String PREF_BATTERY_OPTIMIZATION_FLAG = "PREF_battery_opt_flag";
    public static final String PREF_BOLDWIDGET_VERTICAL_BAR = "PREF_boldwidget_vertical_bar";
    public static final String PREF_AREADATABASE_LOCK = "PREF_areadatabase_lock";
    public static final String PREF_BATTERY = "PREF_battery"; // has no value
    public static final String PREF_DATA_SAVER = "PREF_data_saver"; // has no value
    public static final String PREF_DISPLAY_OVERVIEWCHART_DISPLAY_PRECIPITATION_AMOUNT = "PREF_display_overviewchart_pca";
    public static final String PREF_DISPLAY_OVERVIEWCHART_DISPLAY_RH = "PREF_display_overviewchart_RH";
    public static final String PREF_DISPLAY_OVERVIEWCHART_FILTER_WARNINGS = "PREF_display_overviewchart_filter_warnings";
    public static final String PREF_MAP_HIGH_RESOLUTION ="PREF_map_high_resolution";
    public static final String PREF_MAP_PIN_SIZE = "PREF_map_pinsize";
    public static final String PREF_WEATHER_URL = "PREF_weather_url";
    public static final String PREF_VISUALIZE_DAYTIME = "PREF_visualize_daytime";

    public static final String PREF_STATION_NAME_DEFAULT = "P0489";
    public static final String PREF_LOCATION_DESCRIPTION_DEFAULT = "HAMBURG INNENSTADT";
    public static final double PREF_LATITUDE_DEFAULT = 53.55;
    public static final double PREF_LONGITUDE_DEFAULT = 9.98;
    public static final double PREF_ALTITUDE_DEFAULT = 8.0;
    public static final int PREF_STATIONTYPE_DEFAULT = RawWeatherInfo.Source.MOS;
    public static final long PREF_STATION_TIME_DEFAULT = 0;
    public static final boolean PREF_DISPLAY_STATION_GEO_DEFAULT = true;
    public static final String PREF_DISPLAY_TYPE_DEFAULT = "3";
    public static final boolean PREF_DISPLAY_BAR_DEFAULT = true;
    public static final boolean PREF_DISPLAY_SIMPLE_BAR_DEFAULT = false;
    public static final boolean PREF_DISPLAY_PRESSURE_DEFAULT = true;
    public static final boolean PREF_DISPLAY_VISIBILITY_DEFAULT = true;
    public static final boolean PREF_DISPLAY_SUNRISE_DEFAULT = true;
    public static final boolean PREF_DISPLAY_ENDOFDAY_BAR_DEFAULT = false;
    public static final boolean PREF_DISPLAY_GRADIENT_DEFAULT = false;
    public static final boolean PREF_DISPLAY_WIND_ARC_DEFAULT=false;
    public static final String PREF_DISPLAY_WIND_ARC_PERIOD_DEFAULT="6";
    public static final String PREF_DISPLAY_WIND_TYPE_DEFAULT = "0";
    public static final String PREF_DISPLAY_WIND_UNIT_DEFAULT = "0";
    public static final String PREF_DISPLAY_DISTANCE_UNIT_DEFAULT = "0";
    public static final boolean PREF_DISPLAY_CROP_PRECIPITATIONCHART_DEFAULT = false;
    public static final boolean PREF_DISPLAY_OVERVIEWCHART_DEFAULT = false;
    public static final int PREF_DISPLAY_OVERVIEWCHART_DAYS_DEFAULT = 10;
    public static final boolean PREF_DISPLAY_OVERVIEWCHART_MINMAXUSE_DEFAULT = false;
    public static final int PREF_DISPLAY_OVERVIEWCHART_MIN_DEFAULT = 7;
    public static final int PREF_DISPLAY_OVERVIEWCHART_MAX_DEFAULT = 4;
    public static final String PREF_WIDGET_OPACITY_DEFAULT = "90";
    public static final boolean PREF_WIDGET_SHOWDWDNOTE_DEFAULT = true;
    public static final boolean PREF_WIDGET_DISPLAYWARNINGS_DEFAULT = true;
    public static final boolean PREF_NOTIFY_WARNINGS_DEFAULT = true;
    public static final int PREF_LAST_VERSION_CODE_DEFAULT = 0;
    public static final boolean PREF_SERVE_GADGETBRIDGE_DEFAULT = false;
    public static final String PREF_GADGETBRIDGE_PACKAGENAME_DEFAULT = "nodomain.freeyourgadget.gadgetbridge";
    public static final boolean PREF_GADGETBRIDGE_FAKE_TIMESTAMP_DEFAULT = false;
    public static final boolean PREF_LOGGING_DEFAULT = false;
    public static final boolean PREF_LOG_TO_LOGCAT_DEFAULT = false;
    public static final String PREF_FAVORITESDATA_DEFAULT2 = "";
    public static final boolean PREF_WARNINGS_DISABLE_DEFAULT = false;
    public static final String PREF_WARNINGS_NOTIFY_SEVERITY_DEFAULT = WeatherWarning.Severity.MINOR;
    public static final boolean PREF_IS_FIRST_APP_LAUNCH_DEFAULT = true;
    public static final boolean PREF_USEGPS_DEFAULT = true;
    public static final boolean PREF_GPSAUTO_DEFAULT = false;
    public static final boolean PREF_GPSMANUAL_DEFAULT = false;
    public static final boolean PREF_DISABLE_TLS_DEFAULT = false;
    public static final boolean PREF_TEXTFORECAST_FILTER_DEFAULT = false;
    public static final boolean PREF_RADAR_SHOW_DEFAULT = true;
    public static final boolean PREF_ADMINMAP_SHOW_DEFAULT=false;
    public static final boolean PREF_MAP_DISPLAY_MUNICIPALITIES_DEFAULT = false;
    public static final boolean PREF_MAP_DISPLAY_COUNTIES_DEFAULT = true;
    public static final boolean PREF_MAP_DISPLAY_STATES_DEFAULT = false;
    public static final boolean PREF_MAP_DISPLAY_SEA_AREAS_DEFAULT = false;
    public static final boolean PREF_MAP_DISPLAY_COAST_AREAS_DEFAULT = false;
    public static final boolean PREF_FORCE_NO_MENU_ICONS_DEFAULT = false;
    public static final boolean PREF_DISPLAY_WIND_IN_RADAR_DEFAULT = true;
    public static final boolean PREF_AREA_DATABASE_READY_DEFAULT = false;
    public static final int PREF_AREA_DATABASE_VERSION_DEFAULT = 0;
    public static final String PREF_VIEWMODEL_DEFAULT = ViewModel.SIMPLE;
    public static final String PREF_THEME_DEFAULT = Theme.FOLLOW_DEVICE;
    public static final boolean PREF_ALTERNATIVE_ICONS_DEFAULT = true;
    public static final boolean PREF_USE_METERED_NETWORKS_DEFAULT = true;
    public static final boolean PREF_USE_WIFI_ONLY_DEFAULT = false;
    public static final int PREF_ASKEDFORLOCATIONFLAG_DEFAULT = AskedLocationFlag.NONE;
    public static final String PREF_ROTATIONMODE_DEFAULT = DeviceRotation.DEVICE;
    public static final int PREF_LED_COLOR_DEFAULT = 0;
    public static final boolean PREF_WARNINGS_NOTIFY_LED_DEFAULT = true;
    public static final int PREF_HINTCOUNTER1_DEFAULT = 0;
    public static final int PREF_HINTCOUNTER2_DEFAULT = 0;
    public static final int PREF_LASTMAPDISPLAYED_DEFAULT = WeatherLayer.Layers.UVI_CLOUDS_0;
    public static final boolean PREF_POLLEN_AMBROSIA_DEFAULT = true;
    public static final boolean PREF_POLLEN_BEIFUSS_DEFAULT = true;
    public static final boolean PREF_POLLEN_ROGGEN_DEFAULT = true;
    public static final boolean PREF_POLLEN_ESCHE_DEFAULT = true;
    public static final boolean PREF_POLLEN_BIRKE_DEFAULT = true;
    public static final boolean PREF_POLLEN_HASEL_DEFAULT = true;
    public static final boolean PREF_POLLEN_ERLE_DEFAULT = true;
    public static final boolean PREF_POLLEN_GRAESER_DEFAULT = true;
    public static final boolean PREF_PREFETCH_MAPS_DEFAULT = true;
    public static final long PREF_LAST_PREFETCH_TIME_DEFAULT = 0;
    public static final boolean PREF_UVHI_FETCH_DATA_DEFAULT = false;
    public static final boolean PREF_UVHI_MAINDISPLAY_DEFAULT = false;
    public static final int PREF_MAX_LOCATIONS_IN_SHARED_WARNINGS_DEFAULT = 12;
    public static final boolean PREF_USE_BACKGROUND_LOCATION_DEFAULT = false;
    public static final boolean PREF_DISPLAY_WIND_IN_CHARTS_DEFAULT = false;
    public static final String PREF_DISPLAY_WIND_IN_CHARTS_MAX_DEFAULT = "100";
    public static final boolean PREF_REPLACE_BY_MUNICIPALITY_DEFAULT = false;
    public static final int PREF_BATTERY_OPTIMIZATION_FLAG_DEFAULT = BatteryFlag.NOT_ASKED;
    public static final boolean PREF_BOLDWIDGET_VERTICAL_BAR_DEFAULT = false;
    public static final boolean PREF_DISPLAY_OVERVIEWCHART_DISPLAY_PRECIPITATION_AMOUNT_DEFAULT = false;
    public static final boolean PREF_DISPLAY_OVERVIEWCHART_DISPLAY_RH_DEFAULT = false;
    public static final boolean PREF_DISPLAY_OVERVIEWCHART_FILTER_WARNINGS_DEFAULT = false;
    public static final boolean PREF_MAP_HIGH_RESOLUTION_DEFAULT = false;
    public static final int PREF_MAP_PIN_SIZE_DEFAULT = 4;
    public static final String PREF_WEATHER_URL_DEFAULT = "opendata.dwd.de";
    public static final boolean PREF_VISUALIZE_DAYTIME_DEFAULT = false;

    private Context context;
    private SharedPreferences sharedPreferences;

    private WeatherSettings(Context context) {
        this.context = context;
        sharedPreferences = getSharedPreferences(context);
    }

    public static SharedPreferences getSharedPreferences(Context context){
        // Context applicationContext = context.getApplicationContext();
        //SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Log.v("twfg",sharedPreferences.toString());
        return sharedPreferences;
    }

    public static void resetPreferencesToDefault(Context context){
        PrivateLog.log(context,PrivateLog.MAIN,PrivateLog.INFO,"Settings were reset to default!");
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.clear();
        pref_editor.commit();
        DataStorage.clear(context);
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

    public void applyPreference(String value, String pref, String prefDefault) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putString(pref, value);
        pref_editor.apply();
    }

    public void applyPreference(boolean value, String pref, boolean prefDefault) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(pref, value);
        pref_editor.apply();
    }

    public void applyPreference(int value, String pref, int prefDefault) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(pref, value);
        pref_editor.apply();
    }

    public void applyPreference(float value, String pref, float prefDefault) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putFloat(pref, value);
        pref_editor.apply();
    }

    public void applyPreference(long value, String pref, long prefDefault) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putLong(pref, value);
        pref_editor.apply();
    }

    public void applyPreference(double value, String pref, double prefDefault) {
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        applyPreference((float) value, pref, (float) prefDefault);
        pref_editor.apply();
    }


    public static boolean displayStationGeo(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_STATION_GEO,PREF_DISPLAY_STATION_GEO_DEFAULT);
    }

    public static Weather.WeatherLocation getSetStationLocation(Context context) {
        /*Weather.WeatherLocation weatherLocation = new Weather.WeatherLocation();
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        weatherLocation.setDescription(sharedPreferences.getString(PREF_LOCATION_DESCRIPTION,PREF_LOCATION_DESCRIPTION_DEFAULT));
        weatherLocation.setName(sharedPreferences.getString(PREF_STATION_NAME,PREF_STATION_NAME_DEFAULT));
        weatherLocation.latitude = sharedPreferences.getFloat(PREF_LATITUDE,(float) PREF_LATITUDE_DEFAULT);
        weatherLocation.longitude = sharedPreferences.getFloat(PREF_LONGITUDE,(float) PREF_LONGITUDE_DEFAULT);
        weatherLocation.altitude = sharedPreferences.getFloat(PREF_ALTITUDE,(float) PREF_ALTITUDE_DEFAULT);
        weatherLocation.type = sharedPreferences.getInt(PREF_STATIONTYPE, PREF_STATIONTYPE_DEFAULT);
        weatherLocation.time = sharedPreferences.getLong(PREF_STATION_TIME,PREF_STATION_TIME_DEFAULT);
        if (WeatherSettings.loggingEnabled(context)){
            String log = "Read station from Settings: " + weatherLocation.getOriginalDescription() + " [" + weatherLocation.getName() + "]";
            PrivateLog.log(context, PrivateLog.MAIN, PrivateLog.INFO, log);
        }
         */
        return DataStorage.getSetStationLocation(context);
    }

    public static void setStation(final Context context, Weather.WeatherLocation weatherLocation) {
        DataStorage.setStation(context,weatherLocation);
        /*
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putString(PREF_LOCATION_DESCRIPTION, weatherLocation.getOriginalDescription());
        // remove alternate station description when a new station is set.
        // Reason: a station change may be triggered by the JobWorker / Broadcastreceiver
        // in the background doing passive location checks. Determining the alternate description may be
        // resource intensive and runs at risk to be killed when not done in the foreground. So this search
        // is performed either when fetching new weather data (service) or when setting the station spinner in
        // the main app. Both ensures that this action finishes successfully.
        pref_editor.putString(PREF_LOCATION_DESC_ALTERNATE,PREF_LOCATION_DESC_ALTERNATE_DEFAULT);
        pref_editor.putString(PREF_STATION_NAME,weatherLocation.getName());
        pref_editor.putFloat(PREF_LATITUDE,(float) weatherLocation.latitude);
        pref_editor.putFloat(PREF_LONGITUDE,(float) weatherLocation.longitude);
        pref_editor.putFloat(PREF_ALTITUDE,(float) weatherLocation.altitude);
        pref_editor.putInt(PREF_STATIONTYPE, weatherLocation.type);
        pref_editor.putLong(PREF_STATION_TIME,weatherLocation.time);
        pref_editor.apply();
        */
        PollenArea pollenArea = PollenArea.FindPollenArea(context,weatherLocation);
        setPollenRegion(context,pollenArea);
        // reset update time so a sync will occur next time
        DataStorage.Updates.setLastUpdate(context, Updates.Category.WEATHER,0);
        // also reset Gadgetbridge update time if necessary
        if (WeatherSettings.serveGadgetBridge(context)){
            setGadgetBridgeLastUpdateTime(context,0);
        }
        if (WeatherSettings.loggingEnabled(context)){
            String log = "Station in settings changed to " + WeatherSettings.getSetStationLocation(context).getOriginalDescription() + " [" + WeatherSettings.getSetStationLocation(context).getName() + "]";
            PrivateLog.log(context, PrivateLog.MAIN, PrivateLog.INFO, log);
        }

    }

    public static void setDescriptionAlternate(Context context, String newName){
        /*SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putString(PREF_LOCATION_DESC_ALTERNATE,newName);
        pref_editor.apply();
         */
        Weather.WeatherLocation weatherLocation = DataStorage.getSetStationLocation(context);
        weatherLocation.setDescriptionAlternate(newName);
        DataStorage.setStation(context,weatherLocation);
    }

    public static Weather.WeatherLocation getDefaultWeatherLocation(){
        Weather.WeatherLocation weatherLocation = new Weather.WeatherLocation();
        weatherLocation.setName(PREF_STATION_NAME_DEFAULT);
        weatherLocation.latitude = PREF_LATITUDE_DEFAULT;
        weatherLocation.longitude = PREF_LONGITUDE_DEFAULT;
        weatherLocation.altitude = PREF_ALTITUDE_DEFAULT;
        weatherLocation.type = PREF_STATIONTYPE_DEFAULT;
        weatherLocation.setDescription(PREF_LOCATION_DESCRIPTION_DEFAULT);
        weatherLocation.time = PREF_STATION_TIME_DEFAULT;
        return weatherLocation;
    }

    public static void resetStationToDefault(Context context) {
        /*
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putString(PREF_STATION_NAME, PREF_STATION_NAME_DEFAULT);
        pref_editor.putFloat(PREF_LATITUDE, (float) PREF_LATITUDE_DEFAULT);
        pref_editor.putFloat(PREF_LONGITUDE, (float) PREF_LONGITUDE_DEFAULT);
        pref_editor.putFloat(PREF_ALTITUDE, (float) PREF_ALTITUDE_DEFAULT);
        pref_editor.putInt(PREF_STATIONTYPE,PREF_STATIONTYPE_DEFAULT);
        pref_editor.putString(PREF_LOCATION_DESCRIPTION,PREF_LOCATION_DESCRIPTION_DEFAULT);
        pref_editor.putLong(PREF_STATION_TIME,PREF_STATION_TIME_DEFAULT);
        pref_editor.putString(PREF_FAVORITESDATA,PREF_FAVORITESDATA_DEFAULT);
        pref_editor.apply();
        Weather.WeatherLocation weatherLocation = new Weather.WeatherLocation();
        weatherLocation.setName(PREF_STATION_NAME_DEFAULT);
        weatherLocation.latitude = PREF_LATITUDE_DEFAULT;
        weatherLocation.longitude = PREF_LONGITUDE_DEFAULT;
        weatherLocation.altitude = PREF_ALTITUDE_DEFAULT;
        weatherLocation.type = PREF_STATIONTYPE_DEFAULT;
        weatherLocation.setDescription(PREF_LOCATION_DESCRIPTION_DEFAULT);
        weatherLocation.time = PREF_STATION_TIME_DEFAULT;
         */
        DataStorage.setStation(context,getDefaultWeatherLocation());
        //resetLastWeatherUpdateTime(context);
    }

    public static String getFavorites2(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(PREF_FAVORITESDATA2,PREF_FAVORITESDATA_DEFAULT2);
    }

    public static void putFavorites2(Context context, String rawFavorites) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_FAVORITESDATA2,rawFavorites);
        editor.apply();
    }

    public static void setDisplayType(Context context, String s){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putString(PREF_DISPLAY_TYPE, s);
        pref_editor.apply();
    }

    public static int getDisplayType(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String display_type = sharedPreferences.getString(PREF_DISPLAY_TYPE,PREF_DISPLAY_TYPE_DEFAULT);
        try {
            int i = Integer.parseInt(display_type);
            return i;
        } catch (NumberFormatException e) {
            // return to default if entry is corrupted (not a number)
            setDisplayType(context,PREF_DISPLAY_TYPE_DEFAULT);
            return DISPLAYTYPE_6HOURS;
        }
    }

    public static boolean serveGadgetBridge(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_SERVE_GADGETBRIDGE,PREF_SERVE_GADGETBRIDGE_DEFAULT);
    }

    public static long getGadgetBridgeLastUpdateTime(Context context){
        /*SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getLong(PREF_GADGETBRIDGE_LASTUPDATE,PREF_GADGETBRIDGE_LASTUPDATE_DEFAULT);
         */
        long l = DataStorage.getLong(context,DataStorage.DATASTORAGE_GADGETBRIDGE_LASTUPDATE,DataStorage.DATASTORAGE_GADGETBRIDGE_LASTUPDATE_DEFAULT);
        return l;
    }

    public static void setGadgetBridgeLastUpdateTime(Context context, long l){
        /*SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putLong(PREF_GADGETBRIDGE_LASTUPDATE, l);
        pref_editor.apply();
         */
        DataStorage.setLong(context,DataStorage.DATASTORAGE_GADGETBRIDGE_LASTUPDATE,l);
    }

    public static void setViewsLastUpdateTime(Context context, long l){
        /*SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putLong(PREF_VIEWS_LAST_UPDATE_TIME, l);
        pref_editor.apply();
         */
        DataStorage.setLong(context,DataStorage.DATASTORAGE_VIEWS_LAST_UPDATE_TIME,l);
    }


    public static boolean fakeTimestampForGadgetBridge(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_GADGETBRIDGE_FAKE_TIMESTAMP,PREF_GADGETBRIDGE_FAKE_TIMESTAMP_DEFAULT);
    }


    public static boolean areWarningsDisabled(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_WARNINGS_DISABLE,PREF_WARNINGS_DISABLE_DEFAULT);
    }

    public static boolean notifyWarnings(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_NOTIFY_WARNINGS,PREF_NOTIFY_WARNINGS_DEFAULT);
    }

    public static void setNotifyWarnings(Context context, boolean b){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_NOTIFY_WARNINGS, b);
        pref_editor.apply();
    }

    public static boolean displayWarningsInWidget(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        if (sharedPreferences.getBoolean(PREF_WARNINGS_DISABLE,PREF_WARNINGS_DISABLE_DEFAULT)){
            return false;
        }
        return sharedPreferences.getBoolean(PREF_WIDGET_DISPLAYWARNINGS,PREF_WIDGET_DISPLAYWARNINGS_DEFAULT);
    }

    public static void setDisplayWarningsInWidget(Context context, boolean b){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_WIDGET_DISPLAYWARNINGS, b);
        pref_editor.apply();
    }

    public static int getWarningsNotifySeverity(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String result = sharedPreferences.getString(PREF_WARNINGS_NOTIFY_SEVERITY,PREF_WARNINGS_NOTIFY_SEVERITY_DEFAULT);
        return WeatherWarning.Severity.toInt(result);
    }

    public static boolean isFirstAppLaunch(Context c) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        return sharedPreferences.getBoolean(PREF_IS_FIRST_APP_LAUNCH, PREF_IS_FIRST_APP_LAUNCH_DEFAULT);
    }

    public static void setAppLaunchedFlag(Context c) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_IS_FIRST_APP_LAUNCH, false);
        if (pref_editor.commit()){
            PrivateLog.log(c,PrivateLog.MAIN,PrivateLog.INFO,"First launch flag set successfully.");
        } else {
            PrivateLog.log(c,PrivateLog.MAIN,PrivateLog.ERR,"Setting first launch flag failed.");
        }
    }

    public static void resetAppLaunchedFlag(Context c) {
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_IS_FIRST_APP_LAUNCH, true);
        pref_editor.apply();
    }

    public static int getLastAppVersionCode(Context c){
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        return sharedPreferences.getInt(PREF_LAST_VERSION_CODE, PREF_LAST_VERSION_CODE_DEFAULT);
    }

    public static void setCurrentAppVersionCode(Context c){
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(PREF_LAST_VERSION_CODE, BuildConfig.VERSION_CODE);
        pref_editor.apply();
    }

    public static boolean getUseGPSFlag(Context c){
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        return sharedPreferences.getBoolean(PREF_USEGPS, PREF_USEGPS_DEFAULT);
    }

    public static void setUSEGPSFlag(Context c, boolean flag){
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_USEGPS, flag);
        pref_editor.apply();
    }

    public static boolean GPSManual(Context c){
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        return sharedPreferences.getBoolean(PREF_GPSMANUAL, PREF_GPSMANUAL_DEFAULT);
    }

    public static boolean GPSAuto(Context c){
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        return sharedPreferences.getBoolean(PREF_GPSAUTO, PREF_GPSAUTO_DEFAULT);
    }

    public static void setGPSAuto(Context context, boolean b){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_GPSAUTO,b);
        pref_editor.apply();
    }

    public static void saveGPSfixtime(Context context, long time){
        /* SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putLong(PREF_LASTGPSFIX,time);
        pref_editor.apply();
         */
        DataStorage.setLong(context,DataStorage.DATASTORAGE_LASTGPSFIX,time);
    }

    public static long getlastGPSfixtime(Context context){
        // SharedPreferences sharedPreferences = getSharedPreferences(context);
        // return sharedPreferences.getLong(PREF_LASTGPSFIX, PREF_LASTGPSFIX_DEFAULT);
        return (long) DataStorage.getLong(context,DataStorage.DATASTORAGE_LASTGPSFIX,DataStorage.DATASTORAGE_LASTGPSFIX_DEFAULT);
    }

    public static boolean isGPSFixOutdated(Context context, long time){
        long lastgpsfix = getlastGPSfixtime(context);
        return (time > (lastgpsfix+WeatherLocationManager.GPSFIXINTERVAL));
    }

    public static boolean isLastGPSFixOutdated(Context context){
        long time = Calendar.getInstance().getTimeInMillis();
        return isGPSFixOutdated(context,time);
    }


    public static boolean isTLSdisabled(Context c){
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        return sharedPreferences.getBoolean(PREF_DISABLE_TLS, PREF_DISABLE_TLS_DEFAULT);
    }

    public static void setDisableTLS(Context c, boolean flag){
        SharedPreferences sharedPreferences = getSharedPreferences(c);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_DISABLE_TLS, flag);
        pref_editor.apply();
    }

    public static boolean appReleaseIsUserdebug() {
        return BuildConfig.VERSION_NAME.contains("debug");
    }

    public static int getDisplayLayout(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String displayLayout = sharedPreferences.getString(PREF_DISPLAY_LAYOUT,PREF_DISPLAY_TYPE_DEFAULT);
        if (displayLayout.equals("0")){
            return Weather.DisplayLayout.DEFAULT;
        }
        return Weather.DisplayLayout.DEFAULT;
    }

    public static int getWindDisplayType(String s){
        try {
            int i = Integer.parseInt(s);
            return i;
        } catch (NumberFormatException e) {
            return Weather.WindDisplayType.ARROW;
        }
    }

    public static int getWindDisplayUnit(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        try {
            int i = Integer.parseInt(sharedPreferences.getString(PREF_DISPLAY_WIND_UNIT,PREF_DISPLAY_WIND_UNIT_DEFAULT));
            return i;
        } catch (NumberFormatException e) {
            // return to default if entry is corrupted (not a number)
            SharedPreferences.Editor pref_editor = sharedPreferences.edit();
            pref_editor.putString(PREF_DISPLAY_WIND_UNIT,PREF_DISPLAY_WIND_UNIT_DEFAULT);
            pref_editor.apply();
            // return default
            return Weather.WindDisplayUnit.METERS_PER_SECOND;
        }
    }

    public static int getDistanceDisplayUnit(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        try {
            int i = Integer.parseInt(sharedPreferences.getString(PREF_DISPLAY_DISTANCE_UNIT,PREF_DISPLAY_DISTANCE_UNIT_DEFAULT));
            return i;
        } catch (NumberFormatException e) {
            // return to default if entry is corrupted (not a number)
            SharedPreferences.Editor pref_editor = sharedPreferences.edit();
            pref_editor.putString(PREF_DISPLAY_DISTANCE_UNIT,PREF_DISPLAY_DISTANCE_UNIT_DEFAULT);
            pref_editor.apply();
            // return default
            return Weather.DistanceDisplayUnit.METRIC;
        }
    }

    public static boolean displayWindArc(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_WIND_ARC,PREF_DISPLAY_WIND_ARC_DEFAULT);
    }

    public static int getWindArcPeriod(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        try {
            int i = Integer.parseInt(sharedPreferences.getString(PREF_DISPLAY_WIND_ARC_PERIOD,PREF_DISPLAY_WIND_ARC_PERIOD_DEFAULT));
            return i;
        } catch (NumberFormatException e) {
            // return to default if entry is corrupted (not a number)
            SharedPreferences.Editor pref_editor = sharedPreferences.edit();
            pref_editor.putString(PREF_DISPLAY_WIND_ARC_PERIOD,PREF_DISPLAY_WIND_ARC_PERIOD_DEFAULT);
            pref_editor.apply();
            // return default, is 6
            return 6;
        }
    }

    public static boolean getDisplayWindArc(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_WIND_ARC, PREF_DISPLAY_WIND_ARC_DEFAULT);
    }

    public static void setDisplayWindArc(Context context, boolean b){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_DISPLAY_WIND_ARC, b);
        pref_editor.apply();
    }

    public static boolean isTextForecastFilterEnabled(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_TEXTFORECAST_FILTER, PREF_TEXTFORECAST_FILTER_DEFAULT);
    }

    public static void setTextForecastFilterEnabled(Context context, boolean b){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_TEXTFORECAST_FILTER, b);
        pref_editor.apply();
    }

    public static void setPrefRadarLastdatapoll(Context context, long l){
        /*SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putLong(PREF_RADAR_LASTDATAPOLL,l);
        pref_editor.apply();
         */
        DataStorage.setLong(context,DataStorage.DATASTORAGE_RADAR_LASTDATAPOLL,l);
    }

    public static long getPrefRadarLastdatapoll(Context context){
        /*
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        long l = sharedPreferences.getLong(PREF_RADAR_LASTDATAPOLL,PREF_RADAR_LASTDATAPOLL_DEFAULT);
         */
        long l = DataStorage.getLong(context,DataStorage.DATASTORAGE_RADAR_LASTDATAPOLL,DataStorage.DATASTORAGE_RADAR_LASTDATAPOLL_DEFAULT);
        return l;
    }

    public static boolean isRadarDataOutdated(Context context){
        // SharedPreferences sharedPreferences = getSharedPreferences(context);
        long l = getPrefRadarLastdatapoll(context);
        return Calendar.getInstance().getTimeInMillis() > l + RadarMN.RADAR_DATAINTERVAL;
    }

    public static boolean showRadarByDefault(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_RADAR_SHOW,PREF_RADAR_SHOW_DEFAULT);
    }

    public static boolean showAdminMapByDefault(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_ADMINMAP_SHOW,PREF_ADMINMAP_SHOW_DEFAULT);
    }

    public static boolean getDisplayMunicipalities(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_MAP_DISPLAY_MUNICIPALITIES,PREF_MAP_DISPLAY_MUNICIPALITIES_DEFAULT);
    }

    public static boolean getDisplayCounties(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_MAP_DISPLAY_COUNTIES,PREF_MAP_DISPLAY_COUNTIES_DEFAULT);
    }

    public static boolean getDisplayStates(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_MAP_DISPLAY_STATES,PREF_MAP_DISPLAY_STATES_DEFAULT);
    }

    public static boolean getDisplaySeaAreas(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_MAP_DISPLAY_SEA_AREAS,PREF_MAP_DISPLAY_SEA_AREAS_DEFAULT);
    }

    public static boolean getDisplayCoastAreas(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_MAP_DISPLAY_COAST_AREAS,PREF_MAP_DISPLAY_COAST_AREAS_DEFAULT);
    }

    public static int[] getAreaTypeArray(Context context){
        ArrayList<Integer> resultArrayList = new ArrayList<Integer>();
        if (getDisplayMunicipalities(context)){
            resultArrayList.add(Areas.Area.Type.GEMEINDE);
        }
        if (getDisplayCounties(context)){
            resultArrayList.add(Areas.Area.Type.KREIS);
        }
        if (getDisplayStates(context)){
            resultArrayList.add(Areas.Area.Type.BUNDESLAND);
        }
        if (getDisplaySeaAreas(context)){
            resultArrayList.add(Areas.Area.Type.SEE);
        }
        if (getDisplayCoastAreas(context)){
            resultArrayList.add(Areas.Area.Type.KUESTE);
        }
        Object[] o = resultArrayList.toArray();
        int[] result = new int[o.length];
        for (int i=0; i<o.length; i++){
            result[i] = (int) o [i];
        }
        return result;
    }

    public static boolean forceNoMenuIcons(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        boolean b = sharedPreferences.getBoolean(PREF_FORCE_NO_MENU_ICONS,PREF_FORCE_NO_MENU_ICONS_DEFAULT);
        return b;
    }

    public static void setForceNoMenuIconsFlag(Context context, boolean b){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_FORCE_NO_MENU_ICONS,b);
        pref_editor.apply();
    }

    public static int getWindDisplayType(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String s = sharedPreferences.getString(PREF_DISPLAY_WIND_TYPE,PREF_DISPLAY_WIND_TYPE_DEFAULT);
        return getWindDisplayType(s);
    }

    public static boolean displayWindInRadar(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_WIND_IN_RADAR,PREF_DISPLAY_WIND_IN_RADAR_DEFAULT);
    }

    public static boolean isAreaDatabaseReady(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_AREA_DATABASE_READY,PREF_AREA_DATABASE_READY_DEFAULT);
    }

    public static void setAreaDatabaseReady(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_AREA_DATABASE_READY,true);
        pref_editor.apply();
    }

    public static int getAreaDatabaseVersion(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(PREF_AREA_DATABASE_VERSION,PREF_AREA_DATABASE_VERSION_DEFAULT);
    }

    public static void setAreaDatabaseVersion(Context context, int version){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(PREF_AREA_DATABASE_VERSION,version);
        pref_editor.apply();
    }

    public class ViewModel{
        final static String SIMPLE = "SIMPLE";
        final static String EXTENDED = "EXTENDED";
    }

    public static String getViewModel(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(PREF_VIEWMODEL,PREF_VIEWMODEL_DEFAULT);
    }

    public static void setViewModel(Context context, String model){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putString(PREF_VIEWMODEL,model);
        pref_editor.apply();
    }

    public static boolean preferAlternativeIcons(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_ALTERNATIVE_ICONS,PREF_ALTERNATIVE_ICONS_DEFAULT);
    }

    public static void setPreferAlternativeIcons(Context context, boolean b){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_ALTERNATIVE_ICONS,b);
        pref_editor.apply();
    }

    public static boolean getDisplaySunrise(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_SUNRISE,PREF_DISPLAY_SUNRISE_DEFAULT);
    }

    public static void setDisplaySunrise(Context context, boolean b){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_DISPLAY_SUNRISE,b);
        pref_editor.apply();
    }

    public class Theme{
        final static String FOLLOW_DEVICE = "FOLLOW_DEVICE";
        final static String DARK = "DARK";
        final static String LIGHT = "LIGHT";
        final static String SOLARIZED_DARK = "SOLARIZED_DARK";
        final static String SOLARIZED = "SOLARIZED";
    }

    public static String getThemePreference(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(PREF_THEME,PREF_THEME_DEFAULT);
    }

    public static void setThemePreference(Context context, String theme){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putString(PREF_THEME,theme);
        pref_editor.apply();
    }

    public static boolean useMeteredNetworks(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_USE_METERED_NETWORKS,PREF_USE_METERED_NETWORKS_DEFAULT);
    }

    public static boolean useWifiOnly(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_USE_WIFI_ONLY,PREF_USE_WIFI_ONLY_DEFAULT);
    }

    public static int getUniqueNotificationIdentifier(Context context){
        // SharedPreferences sharedPreferences = getSharedPreferences(context);
        // int i = sharedPreferences.getInt(PREF_NOTIFICATION_IDENTIFIER, PREF_NOTIFICATION_IDENTIFIER_DEFAULT);
        int i = (int) DataStorage.getInt(context,DataStorage.DATASTORAGE_NOTIFICATION_IDENTIFIER,DataStorage.DATASTORAGE_NOTIFICATION_IDENTIFIER_DEFAULT);
        int b = i + 1;
        // prevent uncontrolled int rollover & respect reserved notification ID area
        if (b>NotificationIDRange.MAX){
            b = NotificationIDRange.MIN;
        }
        /*SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(PREF_NOTIFICATION_IDENTIFIER,b);
        pref_editor.apply();
         */
        DataStorage.setLong(context,DataStorage.DATASTORAGE_NOTIFICATION_IDENTIFIER,b);
        return i;
    }

    public static void fixUniqueNotificationIdentifier(Context context){
        int identifier = getUniqueNotificationIdentifier(context);
        if (identifier>NotificationIDRange.MAX){
            /*
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            SharedPreferences.Editor pref_editor = sharedPreferences.edit();
            pref_editor.putInt(PREF_DISPLAY_OVERVIEWCHART,NotificationIDRange.MIN);
            pref_editor.apply();
             */
            DataStorage.setLong(context,DataStorage.DATASTORAGE_NOTIFICATION_IDENTIFIER,NotificationIDRange.MIN);
        }
    }

    public static boolean cropPrecipitationChart(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_CROP_PRECIPITATIONCHART,PREF_DISPLAY_CROP_PRECIPITATIONCHART_DEFAULT);
    }

    public static boolean displayOverviewChart(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_OVERVIEWCHART,PREF_DISPLAY_OVERVIEWCHART_DEFAULT);
    }

    public static void setDisplayOverviewChart(Context context, boolean value){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_DISPLAY_OVERVIEWCHART,value);
        pref_editor.apply();
    }

    public static int getDisplayOverviewChartDays(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(PREF_DISPLAY_OVERVIEWCHART_DAYS,PREF_DISPLAY_OVERVIEWCHART_DAYS_DEFAULT);
    }

    public static void setDisplayOverviewChartDays(Context context, int value){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(PREF_DISPLAY_OVERVIEWCHART_DAYS,value);
        pref_editor.apply();
    }

    public class AskedLocationFlag{
        public final static int NONE = 0;
        public final static int LOCATION = 1;
        public final static int BACKGROUND_LOCATION = 2;
    }

    public static void setAskedLocationFlag(Context context, int flag){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(PREF_ASKEDFORLOCATIONFLAG,flag);
        pref_editor.apply();
    }

    public static int getAskedForLocationFlag(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(PREF_ASKEDFORLOCATIONFLAG,PREF_ASKEDFORLOCATIONFLAG_DEFAULT);
    }

    public static long getLastNotificationUpdateTime(Context context){
        // SharedPreferences sharedPreferences = getSharedPreferences(context);
        // return sharedPreferences.getLong(PREF_LAST_NOTIFICATIONS_UPDATE_TIME,PREF_LAST_NOTIFICATIONS_UPDATE_TIME_DEFAULT);
        return (long) DataStorage.getLong(context,DataStorage.DATASTORAGE_LAST_NOTIFICATIONS_UPDATE_TIME,DataStorage.DATASTORAGE_LAST_NOTIFICATIONS_UPDATE_TIME_DEFAULT);
    }

    public static void setLastNotificationUpdateTime(Context context, long l){
        /* SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putLong(PREF_LAST_NOTIFICATIONS_UPDATE_TIME,l);
        pref_editor.apply();
         */
        DataStorage.setLong(context,DataStorage.DATASTORAGE_LAST_NOTIFICATIONS_UPDATE_TIME,l);
    }


    public final class DeviceRotation {
        final static String DEVICE = "0";
        final static String PORTRAIT = "1";
        final static String LANDSCAPE = "2";
    }

    public static String getRotationmode(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String rotationMode = sharedPreferences.getString(PREF_ROTATIONMODE,PREF_ROTATIONMODE_DEFAULT);
        return rotationMode;
    }

    public static String getDeviceRotationString(Context context){
        String rotationMode = getRotationmode(context);
        String[] s = context.getResources().getStringArray(R.array.display_rotation_text);
        if (rotationMode.equals(DeviceRotation.LANDSCAPE)){
            return s[2];
        }
        if (rotationMode.equals(DeviceRotation.PORTRAIT)){
            return s[1];
        }
        return s[0];
    }

    public static void setRotationMode(Activity activity){
        String rotationMode = getRotationmode(activity.getApplicationContext());
        if (rotationMode.equals(DeviceRotation.LANDSCAPE)){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (rotationMode.equals(DeviceRotation.PORTRAIT)){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public static boolean loggingEnabled(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_LOGGING,PREF_LOGGING_DEFAULT);
    }

    public static boolean loggingToLogcatEnabled(Context context){
         SharedPreferences sharedPreferences = getSharedPreferences(context);
         return sharedPreferences.getBoolean(PREF_LOG_TO_LOGCAT,PREF_LOG_TO_LOGCAT_DEFAULT);
    }

    public static void setLoggingToLogcat(Context context, boolean value){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_LOG_TO_LOGCAT,value);
        pref_editor.apply();
    }

    public static boolean useOverviewChartMinMax(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_OVERVIEWCHART_MINMAXUSE,PREF_DISPLAY_OVERVIEWCHART_MINMAXUSE_DEFAULT);
    }

    public static int getOverviewChartMin(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return Integer.parseInt(NumberPickerPreference.minValues[sharedPreferences.getInt(PREF_DISPLAY_OVERVIEWCHART_MIN,PREF_DISPLAY_OVERVIEWCHART_MIN_DEFAULT)]);
    }

    public static int getOverviewChartMax(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return Integer.parseInt(NumberPickerPreference.maxValues[sharedPreferences.getInt(PREF_DISPLAY_OVERVIEWCHART_MAX,PREF_DISPLAY_OVERVIEWCHART_MAX_DEFAULT)]);
    }

    public static String getNotificationChannelID(long identifier){
        return WeatherSyncAdapter.WARNING_NC_ID_SKELETON + String.valueOf(identifier);
    }

    public static String getNotificationChannelID(Context context) {
        // SharedPreferences sharedPreferences = getSharedPreferences(context);
        // long current = sharedPreferences.getLong(PREF_NC_CHANNEL_DETAIL, 0);
        long current = (long) DataStorage.getLong(context,DataStorage.DATASTORAGE_NC_CHANNEL_DETAIL,DataStorage.DATASTORAGE_NC_CHANNEL_DETAIL_DEFAULT);
        String nc = getNotificationChannelID(current);
        return nc;
    }

    public static String setNewNotificationChannelID(Context context){
        long time = Calendar.getInstance().getTimeInMillis();
        /*SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putLong(PREF_NC_CHANNEL_DETAIL,time);
        pref_editor.apply();
         */
        DataStorage.setLong(context,DataStorage.DATASTORAGE_NC_CHANNEL_DETAIL,time);
        return getNotificationChannelID(time);
    }

    /**
     * Removes the old notification channel and creates a new one on API >= 26. On older versions, it simply
     * returns the channel.
     * @param context
     * @return
     */

    public static String newNotificationChannel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String oldChannelID = getNotificationChannelID(context);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.deleteNotificationChannel(oldChannelID);
            String newChannelID = setNewNotificationChannelID(context);
            return newChannelID;
        }
        return getNotificationChannelID(context);
    }

    public static boolean LEDEnabled(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_WARNINGS_NOTIFY_LED,PREF_WARNINGS_NOTIFY_LED_DEFAULT);
    }

    public static int getLEDColorItem(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(PREF_LED_COLOR,PREF_LED_COLOR_DEFAULT);
    }

    public static void setLEDColorItem(Context context, int newColor){
        int oldColor = getLEDColorItem(context);
        if (newColor!=oldColor){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            SharedPreferences.Editor pref_editor = sharedPreferences.edit();
            pref_editor.putInt(PREF_LED_COLOR,newColor);
            pref_editor.apply();
            newNotificationChannel(context);
        }
    }

    public static int getLEDColor(Context context){
        return NotificationLEDcolors[getLEDColorItem(context)];
    }

    public static int getHintCounter1(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(PREF_HINTCOUNTER1,PREF_HINTCOUNTER1_DEFAULT);
    }

    public static void setHintCounter1(Context context, int value){
    SharedPreferences sharedPreferences = getSharedPreferences(context);
    SharedPreferences.Editor pref_editor = sharedPreferences.edit();
            pref_editor.putInt(PREF_HINTCOUNTER1,value);
            pref_editor.apply();
    }

    public static int getHintCounter2(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(PREF_HINTCOUNTER2,PREF_HINTCOUNTER2_DEFAULT);
    }

    public static void setHintCounter2(Context context, int value){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(PREF_HINTCOUNTER2,value);
        pref_editor.apply();
    }

    public static int getLastDisplayedLayer(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(PREF_LASTMAPDISPLAYED,PREF_LASTMAPDISPLAYED_DEFAULT);
    }

    public static void setLastDisplayedLayer(Context context, int layer){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(PREF_LASTMAPDISPLAYED,layer);
        pref_editor.apply();
    }

    public static PollenArea getPollenRegion(Context context){
        // SharedPreferences sharedPreferences = getSharedPreferences(context);
        // int pollenRegion = sharedPreferences.getInt(PREF_POLLENREGION_ID,PREF_POLLENREGION_ID_DEFAULT);
        // int pollenPartRegion = sharedPreferences.getInt(PREF_POLLENPARTREGION_ID,PREF_POLLENPARTREGION_ID_DEFAULT);
        // String description = sharedPreferences.getString(PREF_POLLENREGION_DESCRIPTION,PREF_POLLENREGION_DESCRIPTION_DEFAULT);
        int pollenRegion = (int) DataStorage.getInt(context,DataStorage.DATASTORAGE_POLLENREGION_ID,DataStorage.DATASTORAGE_POLLENREGION_ID_DEFAULT);
        int pollenPartRegion = (int) DataStorage.getInt(context,DataStorage.DATASTORAGE_POLLENPARTREGION_ID,DataStorage.DATASTORAGE_POLLENPARTREGION_ID_DEFAULT);
        String description = (String) DataStorage.getString(context,DataStorage.DATASTORAGE_POLLENREGION_DESCRIPTION,DataStorage.DATASTORAGE_POLLENREGION_DESCRIPTION_DEFAULT);
        if (pollenRegion==-1) {
            return PollenArea.FindPollenArea(context,getSetStationLocation(context));
        }
        return new PollenArea(pollenRegion,pollenPartRegion,description);
    }

    public static void setPollenRegion(Context context, PollenArea pollenArea){
        //SharedPreferences sharedPreferences = getSharedPreferences(context);
        //SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        if (pollenArea==null){
            //pref_editor.putInt(PREF_POLLENREGION_ID,-1);
            //pref_editor.putInt(PREF_POLLENPARTREGION_ID,-1);
            //pref_editor.putString(PREF_POLLENREGION_DESCRIPTION,"");
            DataStorage.setLong(context,DataStorage.DATASTORAGE_POLLENREGION_ID,-1);
            DataStorage.setLong(context,DataStorage.DATASTORAGE_POLLENPARTREGION_ID,-1);
            DataStorage.setString(context,DataStorage.DATASTORAGE_POLLENREGION_DESCRIPTION,"");
        } else {
            // pref_editor.putInt(PREF_POLLENREGION_ID,pollenArea.region_id);
            // pref_editor.putInt(PREF_POLLENPARTREGION_ID,pollenArea.partregion_id);
            // pref_editor.putString(PREF_POLLENREGION_DESCRIPTION,pollenArea.description);
            DataStorage.setLong(context,DataStorage.DATASTORAGE_POLLENREGION_ID,pollenArea.region_id);
            DataStorage.setLong(context,DataStorage.DATASTORAGE_POLLENPARTREGION_ID,pollenArea.partregion_id);
            DataStorage.setString(context,DataStorage.DATASTORAGE_POLLENREGION_DESCRIPTION,pollenArea.description);
        }
        // pref_editor.apply();
    }

    public static boolean getPollenActiveAmbrosia(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_POLLEN_AMBROSIA,PREF_POLLEN_AMBROSIA_DEFAULT);
    }

    public static boolean getPollenActiveBeifuss(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_POLLEN_BEIFUSS,PREF_POLLEN_BEIFUSS_DEFAULT);
    }

    public static boolean getPollenActiveRoggen(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_POLLEN_ROGGEN,PREF_POLLEN_ROGGEN_DEFAULT);
    }

    public static boolean getPollenActiveEsche(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_POLLEN_ESCHE,PREF_POLLEN_ESCHE_DEFAULT);
    }

    public static boolean getPollenActiveBirke(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_POLLEN_BIRKE,PREF_POLLEN_BIRKE_DEFAULT);
    }

    public static boolean getPollenActiveHasel(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_POLLEN_HASEL,PREF_POLLEN_HASEL_DEFAULT);
    }

    public static boolean getPollenActiveErle(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_POLLEN_ERLE,PREF_POLLEN_ERLE_DEFAULT);
    }

    public static boolean getPollenActiveGraeser(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_POLLEN_GRAESER,PREF_POLLEN_GRAESER_DEFAULT);
    }

    public static boolean anyPollenActive(Context context){
        return ((getPollenActiveAmbrosia(context)) || (getPollenActiveBeifuss(context)) || (getPollenActiveRoggen(context)) ||
                (getPollenActiveEsche(context)) || (getPollenActiveBirke(context)) || (getPollenActiveHasel(context)) ||
                (getPollenActiveErle(context)) || (getPollenActiveGraeser(context)));
    }

    public static boolean preFetchMaps(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        long lastPrefetchTime = sharedPreferences.getLong(PREF_LAST_PREFETCH_TIME,PREF_LAST_PREFETCH_TIME_DEFAULT);
        if (Calendar.getInstance().getTimeInMillis()>lastPrefetchTime+1000*60*60) {
            return sharedPreferences.getBoolean(PREF_PREFETCH_MAPS,PREF_PREFETCH_MAPS_DEFAULT);
        }
        return false;
    }

    public static void setPrefetchMapsTime(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putLong(PREF_LAST_PREFETCH_TIME, Calendar.getInstance().getTimeInMillis());
        pref_editor.apply();
    }

    public static boolean UVHIfetchData(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_UVHI_FETCH_DATA,PREF_UVHI_FETCH_DATA_DEFAULT);
    }

    public static void setUVHIfetchData(Context context, boolean b) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_UVHI_FETCH_DATA, b);
        pref_editor.apply();
    }

    public static boolean UVHImainDisplay(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_UVHI_MAINDISPLAY,PREF_UVHI_MAINDISPLAY_DEFAULT);
    }

    public static void setUVHImainDisplay(Context context, boolean b) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_UVHI_MAINDISPLAY, b);
        pref_editor.apply();
    }


    public final static class UpdateType {
        public final static int NONE = 0;
        public final static int DATA = 1;
        public final static int VIEWS = 2;
        public final static int STATION = 4;
    }

    public static boolean hasWeatherUpdatedFlag(Context context, int flag) {
        /*SharedPreferences sharedPreferences = getSharedPreferences(context);
        int savedFlag = sharedPreferences.getInt(PREF_WEATHERUPDATEDFLAG,PREF_WEATHERUPDATEDFLAG_DEFAULT);
         */
        int savedFlag = DataStorage.getInt(context,DataStorage.DATASTORAGE_WEATHERUPDATEDFLAG,DataStorage.DATASTORAGE_WEATHERUPDATEDFLAG_DEFAULT);
        int result = savedFlag & flag;
        return (result>0);
    }

    public static void setWeatherUpdatedFlag(Context context, int flag) {
        /*SharedPreferences sharedPreferences = getSharedPreferences(context);
        int oldFlag = sharedPreferences.getInt(PREF_WEATHERUPDATEDFLAG,PREF_WEATHERUPDATEDFLAG_DEFAULT);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        if (flag==UpdateType.NONE){
            pref_editor.putInt(PREF_WEATHERUPDATEDFLAG, UpdateType.NONE);
        } else {
            pref_editor.putInt(PREF_WEATHERUPDATEDFLAG, oldFlag|flag);
        }
        pref_editor.apply();
         */
        int oldFlag = DataStorage.getInt(context,DataStorage.DATASTORAGE_WEATHERUPDATEDFLAG,DataStorage.DATASTORAGE_WEATHERUPDATEDFLAG_DEFAULT);
        if (flag==UpdateType.NONE){
            DataStorage.setInt(context,DataStorage.DATASTORAGE_WEATHERUPDATEDFLAG, UpdateType.NONE);
        } else {
            DataStorage.setInt(context,DataStorage.DATASTORAGE_WEATHERUPDATEDFLAG, oldFlag|flag);
        }
    }

    public static int getMaxLocationsInSharedWarnings(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        int result = sharedPreferences.getInt(PREF_MAX_LOCATIONS_IN_SHARED_WARNINGS,PREF_MAX_LOCATIONS_IN_SHARED_WARNINGS_DEFAULT);
        return result;
    }

    public static boolean useBackgroundLocation(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_USE_BACKGROUND_LOCATION,PREF_USE_BACKGROUND_LOCATION_DEFAULT);
    }

    public static void setUseBackgroundLocation(Context context, boolean b){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_USE_BACKGROUND_LOCATION,b);
        pref_editor.apply();
    }

    public static boolean displayWindInCharts(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_WIND_IN_CHARTS,PREF_DISPLAY_WIND_IN_CHARTS_DEFAULT);
    }

    public static int getWindInChartsMaxKmh(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String s = sharedPreferences.getString(PREF_DISPLAY_WIND_IN_CHARTS_MAX,PREF_DISPLAY_WIND_IN_CHARTS_MAX_DEFAULT);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e){
            return 100;
        }
    }

    public static boolean replaceByMunicipality(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_REPLACE_BY_MUNICIPALITY,PREF_REPLACE_BY_MUNICIPALITY_DEFAULT);
    }

    public final static class BatteryFlag{
        public final static int NOT_ASKED = 0;
        public final static int AGREED = 1;
        public final static int REJECTED = 2;
    }

    public static int getBatteryOptimiziatonFlag(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(PREF_BATTERY_OPTIMIZATION_FLAG,PREF_BATTERY_OPTIMIZATION_FLAG_DEFAULT);
    }

    public static void setBatteryOptimiziatonFlag(Context context, int i){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(PREF_BATTERY_OPTIMIZATION_FLAG,i);
        pref_editor.apply();
    }

    public static boolean displayBoldwidgetVerticalBar(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_BOLDWIDGET_VERTICAL_BAR,PREF_BOLDWIDGET_VERTICAL_BAR_DEFAULT);
    }

    public static void lockAreaDatabase(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_AREADATABASE_LOCK,true);
        pref_editor.apply();
    }

    public static void unlockAreaDatabase(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putBoolean(PREF_AREADATABASE_LOCK,false);
        pref_editor.apply();
    }

    public static boolean isAreaDatabaseLocked(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_AREADATABASE_LOCK,false);
    }

    public static boolean displayPrecipitationAmountInOverviewChart(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_OVERVIEWCHART_DISPLAY_PRECIPITATION_AMOUNT,PREF_DISPLAY_OVERVIEWCHART_DISPLAY_PRECIPITATION_AMOUNT_DEFAULT);
    }

    public static boolean displayRHInOverviewChart(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_OVERVIEWCHART_DISPLAY_RH,PREF_DISPLAY_OVERVIEWCHART_DISPLAY_RH_DEFAULT);
    }

    public static boolean filterWarningsInOverviewChartBySeverity(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_OVERVIEWCHART_FILTER_WARNINGS,PREF_DISPLAY_OVERVIEWCHART_FILTER_WARNINGS_DEFAULT);
    }

    public static boolean forceMapHighResolution(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_MAP_HIGH_RESOLUTION,PREF_MAP_HIGH_RESOLUTION_DEFAULT);
    }

    public static void setMapPinSize(Context context, int i){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putInt(PREF_MAP_PIN_SIZE,i);
        pref_editor.apply();
    }

    public static int getMapPinSize(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(PREF_MAP_PIN_SIZE,PREF_MAP_PIN_SIZE_DEFAULT);
    }


    public static class Updates{

        public static long getMillis(int interval) {
            switch (interval) {
                case Updates.Intervals.MIN15:
                    return 1000 * 60 * 15;
                case Updates.Intervals.MIN30:
                    return 1000 * 60 * 30;
                case Updates.Intervals.HOUR1:
                    return 1000 * 60 * 60;
                case Updates.Intervals.HOUR2:
                    return 1000 * 60 * 60 * 2;
                case Updates.Intervals.HOUR3:
                    return 1000 * 60 * 60 * 3;
                case Updates.Intervals.HOUR6:
                    return 1000 * 60 * 60 * 6;
                case Updates.Intervals.HOUR12:
                    return 1000 * 60 * 60 * 12;
                case Updates.Intervals.HOUR18:
                    return 1000 * 60 * 60 * 18;
                case Updates.Intervals.HOUR24:
                    return 1000 * 60 * 60 * 24;
                default:
                    return 0;
            }
        }

        public final class Intervals{

            public static final int NEVER = -1;
            public static final int MIN15 = 1;
            public static final int MIN30 = 2;
            public static final int HOUR1 = 10;
            public static final int HOUR2 = 11;
            public static final int HOUR3 = 12;
            public static final int HOUR6 = 13;
            public static final int HOUR12 = 14;
            public static final int HOUR18 = 15;
            public static final int HOUR24 = 16;
        }

        public final class Category{
            public static final int WEATHER = 0;
            public static final int WARNINGS = 1;
            public static final int TEXTS = 2;
            public static final int POLLEN = 3;
            public static final int LAYERS = 4;
        }

        public static final int CategoryItemsCount = 5;

        public static final String PREF_UPDATE_WEATHER_SYNC = "PREF_update_weather_sync";
        public static final String PREF_UPDATE_WEATHER_INTERVAL = "PREF_update_weather_interval";
        public static final String PREF_UPDATE_WARNINGS_SYNC = "PREF_update_warnings_sync";
        public static final String PREF_UPDATE_WARNINGS_INTERVAL = "PREF_update_warnings_interval";
        public static final String PREF_UPDATE_TEXTS_SYNC = "PREF_update_texts_sync";
        public static final String PREF_UPDATE_TEXTS_INTERVAL = "PREF_update_texts_interval";
        public static final String PREF_UPDATE_POLLEN_SYNC = "PREF_update_pollen_sync";
        public static final String PREF_UPDATE_POLLEN_INTERVAL = "PREF_update_pollen_interval";
        public static final String PREF_UPDATE_LAYERS_SYNC = "PREF_update_layers_sync";
        public static final String PREF_UPDATE_LAYERS_INTERVAL = "PREF_update_layers_interval";

        private static String getSyncPreference(int category){
            String preference  = PREF_UPDATE_WEATHER_SYNC;
            switch (category){
                case Category.WARNINGS: preference  = PREF_UPDATE_WARNINGS_SYNC; break;
                case Category.TEXTS: preference  = PREF_UPDATE_TEXTS_SYNC; break;
                case Category.POLLEN: preference  = PREF_UPDATE_POLLEN_SYNC; break;
                case Category.LAYERS: preference  = PREF_UPDATE_LAYERS_SYNC; break;
                default: preference  = PREF_UPDATE_WEATHER_SYNC; break;
            }
            return preference;
        }

        private static boolean getSyncPreferenceDefault(int category){
            String preference  = PREF_UPDATE_WEATHER_SYNC;
            switch (category){
                case Category.TEXTS:
                case Category.LAYERS:
                case Category.POLLEN:
                    return false;
                default: return true;
            }
        }

        private static String getIntervalPreference(int category){
            String preference  = PREF_UPDATE_WEATHER_INTERVAL;
            switch (category){
                case Category.WARNINGS: preference  = PREF_UPDATE_WARNINGS_INTERVAL; break;
                case Category.TEXTS: preference  = PREF_UPDATE_TEXTS_INTERVAL; break;
                case Category.POLLEN: preference  = PREF_UPDATE_POLLEN_INTERVAL; break;
                case Category.LAYERS: preference  = PREF_UPDATE_LAYERS_INTERVAL; break;
                default: preference  = PREF_UPDATE_WEATHER_INTERVAL; break;
            }
            return preference;
        }

        private static int getIntervalPreferenceDefault(int category){
            String preference  = PREF_UPDATE_WEATHER_INTERVAL;
            switch (category){
                case Category.WARNINGS: return Intervals.MIN30;
                default: return Intervals.HOUR24;
            }
        }

        public static boolean isSyncEnabled(Context context, int category){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            return sharedPreferences.getBoolean(getSyncPreference(category),getSyncPreferenceDefault(category));
        }

        public static void setSyncEnabled(Context context, int category, boolean enabled){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            SharedPreferences.Editor pref_editor = sharedPreferences.edit();
            pref_editor.putBoolean(getSyncPreference(category),enabled);
            pref_editor.apply();
        }

        public static int getSyncInterval(Context context, int category){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            String s = sharedPreferences.getString(getIntervalPreference(category),Integer.toString(getIntervalPreferenceDefault(category)));
            try {
                int i = Integer.parseInt(s);
                return i;
            } catch (NumberFormatException e){
                return getIntervalPreferenceDefault(category);
            }
        }

        public static void setSyncInterval(Context context, int category, int interval){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            SharedPreferences.Editor pref_editor = sharedPreferences.edit();
            pref_editor.putString(getIntervalPreference(category),Integer.toString(interval));
            pref_editor.apply();
        }

        public static long getIntervalMillis(Context context, int category){
            return getMillis(getSyncInterval(context,category));
        }

        private static long getIntervalLong(Context context, int category){
            if (Updates.isSyncEnabled(context,category)){
                return Updates.getIntervalMillis(context,category);
            }
            return Intervals.NEVER;
        }

        public static long getSyncAdapterIntervalInMillis(Context context, Weather.WeatherLocation weatherLocation){
            ArrayList<Long> intervalSyncTimes = new ArrayList<Long>();
            for (int category=0; category<CategoryItemsCount; category++){
                long syncTime = getIntervalLong(context,category);
                // tweak weather sync time from 6 to 12 h if station is DMO
                if ((category==Category.WEATHER) && (weatherLocation.isDMO()) && (syncTime<CurrentWeatherInfo.DMO_UPDATE_INTERVAL)){
                        syncTime = CurrentWeatherInfo.DMO_UPDATE_INTERVAL;
                    }
                if (syncTime!=Intervals.NEVER){
                    intervalSyncTimes.add(syncTime);
                }
            }
            if (intervalSyncTimes.size()>0){
                long result = intervalSyncTimes.get(0);
                for (int i=1; i<intervalSyncTimes.size(); i++){
                    if (intervalSyncTimes.get(i)<result){
                        result = intervalSyncTimes.get(i);
                    }
                }
                return result;
            }
            return Intervals.NEVER;
        }

        public static int getSyncAdapterIntervalInSeconds(Context context, Weather.WeatherLocation weatherLocation){
            return (int) (getSyncAdapterIntervalInMillis(context,weatherLocation)/1000);
        }

        public static class DeprecatedPreferences{
            public static final String PREF_SETALARM = "PREF_setalarm";
            public static final String PREF_UPDATEINTERVAL = "PREF_updateinterval";
            public static final String PREF_LASTWEATHERUPDATETIME = "PREF_lastweatherupdatetime";
            public static final String PREF_UPDATE_WARNINGS = "PREF_update_warnings";
            public static final String PREF_WARNINGS_CACHETIME = "PREF_warnings_cachetime";
            public static final String PREF_UPDATE_TEXTFORECASTS = "PREF_update_textforecasts";
            public static final String PREF_MAPLASTUPDATETIME = "PREF_maplastupdatetime";
            public static final String PREF_TEXTFORECAST_LAST_UPDATE_TIME = "PREF_textforecast_last_update_time";

            public static void migrateDeprecatedSyncSettings(Context context){
                SharedPreferences sharedPreferences = getSharedPreferences(context);
                // migrate weather updates
                Updates.setSyncEnabled(context, Category.WEATHER,sharedPreferences.getBoolean(DeprecatedPreferences.PREF_SETALARM,true));
                String updateIntervalWeather = sharedPreferences.getString(DeprecatedPreferences.PREF_UPDATEINTERVAL,"24");
                if (updateIntervalWeather.equals("6")){
                    Updates.setSyncInterval(context,Category.WEATHER, Intervals.HOUR6);
                } else
                if (updateIntervalWeather.equals("12")){
                    Updates.setSyncInterval(context,Category.WEATHER, Intervals.HOUR12);
                } else
                if (updateIntervalWeather.equals("18")){
                    Updates.setSyncInterval(context,Category.WEATHER, Intervals.HOUR18);
                } else {
                    Updates.setSyncInterval(context,Category.WEATHER, Intervals.HOUR24);
                }
                // migrate warnings
                Updates.setSyncEnabled(context,Category.WARNINGS,sharedPreferences.getBoolean(DeprecatedPreferences.PREF_UPDATE_WARNINGS,true));
                String warningsUpdateTime = sharedPreferences.getString(DeprecatedPreferences.PREF_WARNINGS_CACHETIME,"30");
                if (warningsUpdateTime.equals("15")){
                    Updates.setSyncInterval(context,Category.WARNINGS,Intervals.MIN15);
                } else
                if (warningsUpdateTime.equals("30")){
                    Updates.setSyncInterval(context,Category.WARNINGS,Intervals.MIN30);
                } else
                if (warningsUpdateTime.equals("60")){
                    Updates.setSyncInterval(context,Category.WARNINGS,Intervals.HOUR1);
                } else
                if (warningsUpdateTime.equals("120")){
                    Updates.setSyncInterval(context,Category.WARNINGS,Intervals.HOUR2);
                } else
                if (warningsUpdateTime.equals("180")){
                    Updates.setSyncInterval(context,Category.WARNINGS,Intervals.HOUR3);
                } else
                if (warningsUpdateTime.equals("360")){
                    Updates.setSyncInterval(context,Category.WARNINGS,Intervals.HOUR6);
                } else {
                    Updates.setSyncInterval(context,Category.WARNINGS,Intervals.MIN30);
                }
                // migrate texts
                Updates.setSyncEnabled(context,Category.TEXTS,sharedPreferences.getBoolean(DeprecatedPreferences.PREF_UPDATE_TEXTFORECASTS,false));
            }
        }
    }

    public static int getWidgetOpacity(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String opacityString = sharedPreferences.getString(PREF_WIDGET_OPACITY,PREF_WIDGET_OPACITY_DEFAULT);
        int opacity = 90;
        try {
            opacity = Integer.parseInt(opacityString);
        } catch (NumberFormatException e){
            PrivateLog.log(context,PrivateLog.WIDGET,PrivateLog.ERR,"Corrupted widget opacity string: "+opacityString+". Using 90%.");
        }
        return opacity;
    }

    public static boolean showDWDNote(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_WIDGET_SHOWDWDNOTE,PREF_WIDGET_SHOWDWDNOTE_DEFAULT);
    }

    public static boolean getDisplayBar(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_BAR,PREF_DISPLAY_BAR_DEFAULT);
    }

    public static boolean getDisplayPressure(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_PRESSURE,PREF_DISPLAY_PRESSURE_DEFAULT);
    }

    public static boolean getDisplayVisibility(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_VISIBILITY,PREF_DISPLAY_VISIBILITY_DEFAULT);
    }

    public static boolean getDisplayEndOfDayBar(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_ENDOFDAY_BAR,PREF_DISPLAY_ENDOFDAY_BAR_DEFAULT);
    }

    public static boolean getDisplayGradient(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_GRADIENT,PREF_DISPLAY_GRADIENT_DEFAULT);
    }

    public static boolean getDisplaySimpleBar(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_DISPLAY_SIMPLE_BAR,PREF_DISPLAY_SIMPLE_BAR_DEFAULT);
    }

    public static boolean getWarningsDisabled(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_WARNINGS_DISABLE,PREF_WARNINGS_DISABLE_DEFAULT);
    }

    public static String getGadgetBridgePackageName(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(PREF_GADGETBRIDGE_PACKAGENAME,PREF_GADGETBRIDGE_PACKAGENAME_DEFAULT);
    }

    public static String getWeatherUrl(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(PREF_WEATHER_URL,PREF_WEATHER_URL_DEFAULT);
    }

    public static void setWeatherUrl(Context context, String value){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor pref_editor = sharedPreferences.edit();
        pref_editor.putString(PREF_WEATHER_URL,value);
        pref_editor.apply();
    }

    public static boolean visualizeDaytime(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(PREF_VISUALIZE_DAYTIME,PREF_VISUALIZE_DAYTIME_DEFAULT);
    }

}
