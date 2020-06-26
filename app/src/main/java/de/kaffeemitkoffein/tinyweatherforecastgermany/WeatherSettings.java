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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class WeatherSettings {

    public static final String PREF_STATION = "PREF_station";
    public static final String PREF_UPDATEINTERVAL = "PREF_updateinterval";
    public static final String PREF_ISWEATHERPROVIDER = "PREF_isregisteredweatherprovider";
    public static final String PREF_WIDGET_OPACITY = "PREF_widget_opacity";
    public static final String PREF_WIDGET_SHOWDWDNOTE = "PREF_widget_showdwdnote";


    public static final String PREF_STATION_DEFAULT = "Hamburg";
    public static final String PREF_UPDATEINTERVAL_DEFAULT = "6";
    public static final String PREF_WIDGET_OPACITY_DEFAULT = "10";
    public static final boolean PREF_ISWEATHERPROVIDER_DEFAULT = false;
    public static final boolean PREF_WIDGET_SHOWDWDNOTE_DEFAULT = true;

    public String station = PREF_STATION_DEFAULT;
    public String updateinterval = PREF_UPDATEINTERVAL_DEFAULT;
    public boolean is_weatherprovider = PREF_ISWEATHERPROVIDER_DEFAULT;
    public String widget_opacity = PREF_WIDGET_OPACITY_DEFAULT;
    public boolean widget_showdwdnote = PREF_WIDGET_SHOWDWDNOTE_DEFAULT;

    private Context context;
    public SharedPreferences sharedPreferences;

    public WeatherSettings(Context c){
        this.context = c;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        readPreferences();
    }

    public void readPreferences(){
        this.station = readPreference(PREF_STATION,PREF_STATION_DEFAULT);
        this.updateinterval = readPreference(PREF_UPDATEINTERVAL,PREF_UPDATEINTERVAL_DEFAULT);
        this.is_weatherprovider = readPreference(PREF_ISWEATHERPROVIDER,PREF_ISWEATHERPROVIDER_DEFAULT);
        this.widget_opacity = readPreference(PREF_WIDGET_OPACITY,PREF_WIDGET_OPACITY_DEFAULT);
        this.widget_showdwdnote = readPreference(PREF_WIDGET_SHOWDWDNOTE,PREF_WIDGET_SHOWDWDNOTE_DEFAULT);
   }

    public void savePreferences(){
        applyPreference(PREF_STATION,this.station);
        applyPreference(PREF_UPDATEINTERVAL,this.updateinterval);
        applyPreference(PREF_ISWEATHERPROVIDER,this.is_weatherprovider);
        applyPreference(PREF_WIDGET_OPACITY,this.widget_opacity);
        applyPreference(PREF_WIDGET_SHOWDWDNOTE,this.widget_showdwdnote);
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

}
