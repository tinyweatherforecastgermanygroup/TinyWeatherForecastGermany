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

public class WeatherSettings {

    public static final String PREF_STATION = "PREF_station";
    public static final String PREF_SETALARM = "PREF_setalarm";
    public static final String PREF_UPDATEINTERVAL = "PREF_updateinterval";
    public static final String PREF_ISWEATHERPROVIDER = "PREF_isregisteredweatherprovider";
    public static final String PREF_WIDGET_OPACITY = "PREF_widget_opacity";
    public static final String PREF_WIDGET_SHOWDWDNOTE = "PREF_widget_showdwdnote";
    public static final String PREF_LAST_VERSION_CODE = "PREF_last_version_code";
    public static final String PREF_SERVE_GADGETBRIDGE = "PREF_serve_gadgetbridge";
    public static final String PREF_GADGETBRIDGE_PACKAGENAME = "PREF_gadgetbridge_packagename";

    public static final String PREF_STATION_DEFAULT = "Hamburg";
    public static final boolean PREF_SETALARM_DEFAULT = false;
    public static final String PREF_UPDATEINTERVAL_DEFAULT = "6";
    public static final String PREF_WIDGET_OPACITY_DEFAULT = "10";
    public static final boolean PREF_ISWEATHERPROVIDER_DEFAULT = false;
    public static final boolean PREF_WIDGET_SHOWDWDNOTE_DEFAULT = true;
    public final int PREF_LAST_VERSION_CODE_DEFAULT = BuildConfig.VERSION_CODE;
    public static final boolean PREF_SERVE_GADGETBRIDGE_DEFAULT = false;
    public static final String PREF_GADGETBRIDGE_PACKAGENAME_DEFAULT = "nodomain.freeyourgadget.gadgetbridge";

    public String station = PREF_STATION_DEFAULT;
    public boolean setalarm = PREF_SETALARM_DEFAULT;
    public String updateinterval = PREF_UPDATEINTERVAL_DEFAULT;
    public boolean is_weatherprovider = PREF_ISWEATHERPROVIDER_DEFAULT;
    public String widget_opacity = PREF_WIDGET_OPACITY_DEFAULT;
    public boolean widget_showdwdnote = PREF_WIDGET_SHOWDWDNOTE_DEFAULT;
    public int last_version_code = PREF_LAST_VERSION_CODE_DEFAULT;
    public boolean serve_gadgetbridge = PREF_SERVE_GADGETBRIDGE_DEFAULT;
    public String gadgetbridge_packagename = PREF_GADGETBRIDGE_PACKAGENAME_DEFAULT;

    private Context context;
    public SharedPreferences sharedPreferences;

    public WeatherSettings(Context c){
        this.context = c;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        readPreferences();
    }

    public void readPreferences(){
        this.station = readPreference(PREF_STATION,PREF_STATION_DEFAULT);
        this.setalarm = readPreference(PREF_SETALARM,PREF_SETALARM_DEFAULT);
        this.updateinterval = readPreference(PREF_UPDATEINTERVAL,PREF_UPDATEINTERVAL_DEFAULT);
        this.is_weatherprovider = readPreference(PREF_ISWEATHERPROVIDER,PREF_ISWEATHERPROVIDER_DEFAULT);
        this.widget_opacity = readPreference(PREF_WIDGET_OPACITY,PREF_WIDGET_OPACITY_DEFAULT);
        this.widget_showdwdnote = readPreference(PREF_WIDGET_SHOWDWDNOTE,PREF_WIDGET_SHOWDWDNOTE_DEFAULT);
        this.last_version_code = readPreference(PREF_LAST_VERSION_CODE,PREF_LAST_VERSION_CODE_DEFAULT);
        this.serve_gadgetbridge = readPreference(PREF_SERVE_GADGETBRIDGE,PREF_SERVE_GADGETBRIDGE_DEFAULT);
   }

    public void savePreferences(){
        applyPreference(PREF_STATION,this.station);
        applyPreference(PREF_SETALARM,this.setalarm);
        applyPreference(PREF_UPDATEINTERVAL,this.updateinterval);
        applyPreference(PREF_ISWEATHERPROVIDER,this.is_weatherprovider);
        applyPreference(PREF_WIDGET_OPACITY,this.widget_opacity);
        applyPreference(PREF_WIDGET_SHOWDWDNOTE,this.widget_showdwdnote);
        applyPreference(PREF_LAST_VERSION_CODE,this.last_version_code);
        applyPreference(PREF_SERVE_GADGETBRIDGE,this.serve_gadgetbridge);
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

    public int getUpdateInterval(){
        int i = Integer.parseInt(this.updateinterval);
        return i;
    }

    public long getUpdateIntervalInMillis(){
        //return getUpdateInterval()*60*60*1000;
        return 120000;
    }

}
