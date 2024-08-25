/**
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

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Calendar;

public class WeatherContentManager {

    private static final String CONTENT_STRING="content://";
    private static final String FORECAST_URI_STRING_SINGLE = CONTENT_STRING+WeatherContentProvider.AUTHORITY+"/"+WeatherContentProvider.TABLE_NAME_FORECASTS;
    private static final String FORECAST_URI_STRING_ALL    = FORECAST_URI_STRING_SINGLE+"/*";
    private static final String WARNING_URI_STRING_SINGLE = CONTENT_STRING+WeatherContentProvider.AUTHORITY+"/"+WeatherContentProvider.TABLE_NAME_WARNINGS;
    private static final String WARNING_URI_STRING_ALL    = WARNING_URI_STRING_SINGLE+"/*";
    private static final String TEXT_URI_STRING_SINGLE = CONTENT_STRING+WeatherContentProvider.AUTHORITY+"/"+WeatherContentProvider.TABLE_NAME_TEXTS;
    private static final String TEXT_URI_STRING_ALL    = TEXT_URI_STRING_SINGLE+"/*";
    private static final String AREA_URI_STRING_SINGLE = CONTENT_STRING+WeatherContentProvider.AUTHORITY+"/"+WeatherContentProvider.TABLE_NAME_AREAS;
    private static final String AREA_URI_STRING_ALL    = AREA_URI_STRING_SINGLE+"/*";
    private static final String POLLENAREA_URI_STRING_SINGLE = CONTENT_STRING+WeatherContentProvider.AUTHORITY+"/"+WeatherContentProvider.TABLE_NAME_POLLENAREAS;
    private static final String POLLENAREA_URI_STRING_ALL    = POLLENAREA_URI_STRING_SINGLE+"/*";
    private static final String POLLEN_URI_STRING_SINGLE = CONTENT_STRING+WeatherContentProvider.AUTHORITY+"/"+WeatherContentProvider.TABLE_NAME_POLLEN;
    private static final String POLLEN_URI_STRING_ALL    = POLLEN_URI_STRING_SINGLE+"/*";
    private static final String DATA_URI_STRING_SINGLE = CONTENT_STRING+WeatherContentProvider.AUTHORITY+"/"+WeatherContentProvider.TABLE_NAME_DATA;
    private static final String DATA_URI_STRING_ALL    = DATA_URI_STRING_SINGLE+"/*";

    public static final Uri FORECAST_URI_SINLGE = Uri.parse(FORECAST_URI_STRING_SINGLE);
    public static final Uri FORECAST_URI_ALL = Uri.parse(FORECAST_URI_STRING_ALL);
    public static final Uri WARNING_URI_SINLGE = Uri.parse(WARNING_URI_STRING_SINGLE);
    public static final Uri WARNING_URI_ALL = Uri.parse(WARNING_URI_STRING_ALL);
    public static final Uri TEXT_URI_SINLGE = Uri.parse(TEXT_URI_STRING_SINGLE);
    public static final Uri TEXT_URI_ALL = Uri.parse(TEXT_URI_STRING_ALL);
    public static final Uri AREA_URI_SINLGE = Uri.parse(AREA_URI_STRING_SINGLE);
    public static final Uri AREA_URI_ALL = Uri.parse(AREA_URI_STRING_ALL);
    public static final Uri POLLENAREAS_URI_SINGLE = Uri.parse(POLLENAREA_URI_STRING_SINGLE);
    public static final Uri POLLENAREAS_URI_ALL = Uri.parse(POLLENAREA_URI_STRING_ALL);
    public static final Uri POLLEN_URI_SINGLE = Uri.parse(POLLENAREA_URI_STRING_SINGLE);
    public static final Uri POLLEN_URI_ALL = Uri.parse(POLLEN_URI_STRING_ALL);
    public static final Uri DATA_URI_SINGLE = Uri.parse(DATA_URI_STRING_SINGLE);
    public static final Uri DATA_URI_ALL = Uri.parse(DATA_URI_STRING_ALL);

    private final static String serial_serparator="_,_";

    public static String serializeString(String s[]){
        return TextUtils.join(serial_serparator,s);
    }

    public static String serializeStringFromArrayList(ArrayList<String> s) {
        return TextUtils.join(serial_serparator, s);
    }

    public static String[] deSerializeString(String s){
        return TextUtils.split(s,serial_serparator);
    }

    public static ArrayList<String> deSerializeStringToArraylist(String s) {
        String[] results = TextUtils.split(s, serial_serparator);
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < results.length; i++) {
            list.add(results[i]);
        }
        return list;
    }

    public static ContentValues getContentValuesFromWeatherCard(RawWeatherInfo rawWeatherInfo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_timetext,rawWeatherInfo.timetext);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_name,rawWeatherInfo.weatherLocation.getName());
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_description,rawWeatherInfo.weatherLocation.getOriginalDescription());
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_description_alternate,rawWeatherInfo.weatherLocation.getDescriptionAlternate());
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_longitude,rawWeatherInfo.weatherLocation.longitude);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_latitude,rawWeatherInfo.weatherLocation.latitude);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_altitude,rawWeatherInfo.weatherLocation.altitude);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_timesteps,serializeString(rawWeatherInfo.timesteps));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_TTT,serializeString(rawWeatherInfo.TTT));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_E_TTT,serializeString(rawWeatherInfo.E_TTT));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_T5cm,serializeString(rawWeatherInfo.T5cm));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Td,serializeString(rawWeatherInfo.Td));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_E_Td,serializeString(rawWeatherInfo.E_Td));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Tx,serializeString(rawWeatherInfo.Tx));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Tn,serializeString(rawWeatherInfo.Tn));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_TM,serializeString(rawWeatherInfo.TM));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_TG,serializeString(rawWeatherInfo.TG));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_DD,serializeString(rawWeatherInfo.DD));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_E_DD,serializeString(rawWeatherInfo.E_DD));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FF,serializeString(rawWeatherInfo.FF));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_E_FF,serializeString(rawWeatherInfo.E_FF));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FX1,serializeString(rawWeatherInfo.FX1));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FX3,serializeString(rawWeatherInfo.FX3));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FXh,serializeString(rawWeatherInfo.FXh));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FXh25,serializeString(rawWeatherInfo.FXh25));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FXh40,serializeString(rawWeatherInfo.FXh40));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FXh55,serializeString(rawWeatherInfo.FXh55));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FX625,serializeString(rawWeatherInfo.FX625));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FX640,serializeString(rawWeatherInfo.FX640));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FX655,serializeString(rawWeatherInfo.FX655));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR1,serializeString(rawWeatherInfo.RR1));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR1c,serializeString(rawWeatherInfo.RR1c));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RRL1c,serializeString(rawWeatherInfo.RRL1c));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR3,serializeString(rawWeatherInfo.RR3));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR6,serializeString(rawWeatherInfo.RR6));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR3c,serializeString(rawWeatherInfo.RR3c));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR6c,serializeString(rawWeatherInfo.RR6c));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RRhc,serializeString(rawWeatherInfo.RRhc));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RRdc,serializeString(rawWeatherInfo.RRdc));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RRS1c,serializeString(rawWeatherInfo.RRS1c));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RRS3c,serializeString(rawWeatherInfo.RRS3c));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R101,serializeString(rawWeatherInfo.R101));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R102,serializeString(rawWeatherInfo.R102));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R103,serializeString(rawWeatherInfo.R103));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R105,serializeString(rawWeatherInfo.R105));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R107,serializeString(rawWeatherInfo.R107));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R110,serializeString(rawWeatherInfo.R110));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R120,serializeString(rawWeatherInfo.R120));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R130,serializeString(rawWeatherInfo.R130));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R150,serializeString(rawWeatherInfo.R150));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR1o1,serializeString(rawWeatherInfo.RR1o1));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR1w1,serializeString(rawWeatherInfo.RR1w1));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR1u1,serializeString(rawWeatherInfo.RR1u1));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R600,serializeString(rawWeatherInfo.R600));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rh00,serializeString(rawWeatherInfo.Rh00));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R602,serializeString(rawWeatherInfo.R602));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rh02,serializeString(rawWeatherInfo.Rh02));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rd02,serializeString(rawWeatherInfo.Rd02));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R610,serializeString(rawWeatherInfo.R610));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rh10,serializeString(rawWeatherInfo.Rh10));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R650,serializeString(rawWeatherInfo.R650));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rh50,serializeString(rawWeatherInfo.Rh50));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rd00,serializeString(rawWeatherInfo.Rd00));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rd10,serializeString(rawWeatherInfo.Rd10));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rd50,serializeString(rawWeatherInfo.Rd50));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwPd,serializeString(rawWeatherInfo.wwPd));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_DRR1,serializeString(rawWeatherInfo.DRR1));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwZ,serializeString(rawWeatherInfo.wwZ));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwZ6,serializeString(rawWeatherInfo.wwZ6));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwZh,serializeString(rawWeatherInfo.wwZh));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwD,serializeString(rawWeatherInfo.wwD));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwD6,serializeString(rawWeatherInfo.wwD6));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwDh,serializeString(rawWeatherInfo.wwDh));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwC,serializeString(rawWeatherInfo.wwC));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwC6,serializeString(rawWeatherInfo.wwC6));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwCh,serializeString(rawWeatherInfo.wwCh));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwT,serializeString(rawWeatherInfo.wwT));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwT6,serializeString(rawWeatherInfo.wwT6));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwTh,serializeString(rawWeatherInfo.wwTh));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwTd,serializeString(rawWeatherInfo.wwTd));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwL,serializeString(rawWeatherInfo.wwL));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwL6,serializeString(rawWeatherInfo.wwL6));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwLh,serializeString(rawWeatherInfo.wwLh));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwS,serializeString(rawWeatherInfo.wwS));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwS6,serializeString(rawWeatherInfo.wwS6));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwSh,serializeString(rawWeatherInfo.wwSh));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwF,serializeString(rawWeatherInfo.wwF));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwF6,serializeString(rawWeatherInfo.wwF6));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwFh,serializeString(rawWeatherInfo.wwFh));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwP,serializeString(rawWeatherInfo.wwP));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwP6,serializeString(rawWeatherInfo.wwP6));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwPh,serializeString(rawWeatherInfo.wwPh));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_VV10,serializeString(rawWeatherInfo.VV10));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_ww,serializeString(rawWeatherInfo.ww));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_ww3,serializeString(rawWeatherInfo.ww3));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_W1W2,serializeString(rawWeatherInfo.W1W2));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_WPc11,serializeString(rawWeatherInfo.WPc11));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_WPc31,serializeString(rawWeatherInfo.WPc31));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_WPc61,serializeString(rawWeatherInfo.WPc61));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_WPch1,serializeString(rawWeatherInfo.WPch1));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_WPcd1,serializeString(rawWeatherInfo.WPcd1));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_N,serializeString(rawWeatherInfo.N));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Neff,serializeString(rawWeatherInfo.Neff));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_N05,serializeString(rawWeatherInfo.N05));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Nl,serializeString(rawWeatherInfo.Nl));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Nm,serializeString(rawWeatherInfo.Nm));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Nh,serializeString(rawWeatherInfo.Nh));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Nlm,serializeString(rawWeatherInfo.Nlm));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_H_BsC,serializeString(rawWeatherInfo.H_BsC));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_PPPP,serializeString(rawWeatherInfo.PPPP));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_E_PPP,serializeString(rawWeatherInfo.E_PPP));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RadS1,serializeString(rawWeatherInfo.RadS1));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RadS3,serializeString(rawWeatherInfo.RadS3));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RRad1,serializeString(rawWeatherInfo.RRad1));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rad1h,serializeString(rawWeatherInfo.Rad1h));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RadL3,serializeString(rawWeatherInfo.RadL3));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_VV,serializeString(rawWeatherInfo.VV));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_D1,serializeString(rawWeatherInfo.D1));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_SunD,serializeString(rawWeatherInfo.SunD));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_SunD3,serializeString(rawWeatherInfo.SunD3));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RSunD,serializeString(rawWeatherInfo.RSunD));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_PSd00,serializeString(rawWeatherInfo.PSd00));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_PSd30,serializeString(rawWeatherInfo.PSd30));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_PSd60,serializeString(rawWeatherInfo.PSd60));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwM,serializeString(rawWeatherInfo.wwM));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwM6,serializeString(rawWeatherInfo.wwM6));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwMh,serializeString(rawWeatherInfo.wwMh));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwMd,serializeString(rawWeatherInfo.wwMd));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_PEvap,serializeString(rawWeatherInfo.PEvap));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_timestamp,rawWeatherInfo.timestamp);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_polling_time,rawWeatherInfo.polling_time);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_elements,rawWeatherInfo.elements);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_UVHazardIndex,serializeString(rawWeatherInfo.uvHazardIndex));
        return contentValues;
    }

    @SuppressLint("Range")
    public static RawWeatherInfo getRawWeatherInfoFromCursor(Cursor c){
        if (c==null){
            return null;
        } else {
            RawWeatherInfo rawWeatherInfo = new RawWeatherInfo();
            if (c.moveToFirst()){
                rawWeatherInfo.timetext = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_timetext));
                rawWeatherInfo.weatherLocation.setName(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_name)));
                rawWeatherInfo.weatherLocation.setDescription(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_description)));
                rawWeatherInfo.weatherLocation.setDescriptionAlternate(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_description_alternate)));
                rawWeatherInfo.weatherLocation.longitude = c.getDouble(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_longitude));
                rawWeatherInfo.weatherLocation.latitude = c.getDouble(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_latitude));
                rawWeatherInfo.weatherLocation.altitude = c.getDouble(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_altitude));
                rawWeatherInfo.timesteps = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_timesteps)));
                rawWeatherInfo.TTT = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_TTT)));
                rawWeatherInfo.E_TTT = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_E_TTT)));
                rawWeatherInfo.T5cm = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_T5cm)));
                rawWeatherInfo.Td = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Td)));
                rawWeatherInfo.E_Td = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_E_Td)));
                rawWeatherInfo.Tx = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Tx)));
                rawWeatherInfo.Tn = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Tn)));
                rawWeatherInfo.TM = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_TM)));
                rawWeatherInfo.TG = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_TG)));
                rawWeatherInfo.DD = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_DD)));
                rawWeatherInfo.E_DD = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_E_DD)));
                rawWeatherInfo.FF = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FF)));
                rawWeatherInfo.E_FF = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_E_FF)));
                rawWeatherInfo.FX1 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FX1)));
                rawWeatherInfo.FX3 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FX3)));
                rawWeatherInfo.FXh = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FXh)));
                rawWeatherInfo.FXh25 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FXh25)));
                rawWeatherInfo.FXh40 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FXh40)));
                rawWeatherInfo.FXh55 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FXh55)));
                rawWeatherInfo.FX625 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FX625)));
                rawWeatherInfo.FX640 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FX640)));
                rawWeatherInfo.FX655 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_FX655)));
                rawWeatherInfo.RR1 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR1)));
                rawWeatherInfo.RR1c = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR1c)));
                rawWeatherInfo.RRL1c = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RRL1c)));
                rawWeatherInfo.RR3 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR3)));
                rawWeatherInfo.RR6 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR6)));
                rawWeatherInfo.RR3c = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR3c)));
                rawWeatherInfo.RR6c = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR6c)));
                rawWeatherInfo.RRhc = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RRhc)));
                rawWeatherInfo.RRdc = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RRdc)));
                rawWeatherInfo.RRS1c = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RRS1c)));
                rawWeatherInfo.RRS3c = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RRS3c)));
                rawWeatherInfo.R101 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R101)));
                rawWeatherInfo.R102 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R102)));
                rawWeatherInfo.R103 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R103)));
                rawWeatherInfo.R105 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R105)));
                rawWeatherInfo.R107 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R107)));
                rawWeatherInfo.R110 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R110)));
                rawWeatherInfo.R120 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R120)));
                rawWeatherInfo.R130 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R130)));
                rawWeatherInfo.R150 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R150)));
                rawWeatherInfo.RR1o1 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR1o1)));
                rawWeatherInfo.RR1w1 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR1w1)));
                rawWeatherInfo.RR1u1 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RR1u1)));
                rawWeatherInfo.R600 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R600)));
                rawWeatherInfo.Rh00 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rh00)));
                rawWeatherInfo.R602 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R602)));
                rawWeatherInfo.Rh02 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rh02)));
                rawWeatherInfo.Rd02 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rd02)));
                rawWeatherInfo.R610 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R610)));
                rawWeatherInfo.Rh10 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rh10)));
                rawWeatherInfo.R650 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_R650)));
                rawWeatherInfo.Rh50 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rh50)));
                rawWeatherInfo.Rd00 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rd00)));
                rawWeatherInfo.Rd10 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rd10)));
                rawWeatherInfo.Rd50 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rd50)));
                rawWeatherInfo.wwPd = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwPd)));
                rawWeatherInfo.DRR1 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_DRR1)));
                rawWeatherInfo.wwZ = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwZ)));
                rawWeatherInfo.wwZ6 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwZ6)));
                rawWeatherInfo.wwZh = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwZh)));
                rawWeatherInfo.wwD = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwD)));
                rawWeatherInfo.wwD6 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwD6)));
                rawWeatherInfo.wwDh = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwDh)));
                rawWeatherInfo.wwC = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwC)));
                rawWeatherInfo.wwC6 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwC6)));
                rawWeatherInfo.wwCh = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwCh)));
                rawWeatherInfo.wwT = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwT)));
                rawWeatherInfo.wwT6 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwT6)));
                rawWeatherInfo.wwTh = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwTh)));
                rawWeatherInfo.wwTd = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwTd)));
                rawWeatherInfo.wwL = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwL)));
                rawWeatherInfo.wwL6 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwL6)));
                rawWeatherInfo.wwLh = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwLh)));
                rawWeatherInfo.wwS = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwS)));
                rawWeatherInfo.wwS6 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwS6)));
                rawWeatherInfo.wwSh = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwSh)));
                rawWeatherInfo.wwF = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwF)));
                rawWeatherInfo.wwF6 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwF6)));
                rawWeatherInfo.wwFh = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwFh)));
                rawWeatherInfo.wwP = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwP)));
                rawWeatherInfo.wwP6 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwP6)));
                rawWeatherInfo.wwPh = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwPh)));
                rawWeatherInfo.VV10 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_VV10)));
                rawWeatherInfo.ww = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_ww)));
                rawWeatherInfo.ww3 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_ww3)));
                rawWeatherInfo.W1W2 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_W1W2)));
                rawWeatherInfo.WPc11 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_WPc11)));
                rawWeatherInfo.WPc31 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_WPc31)));
                rawWeatherInfo.WPc61 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_WPc61)));
                rawWeatherInfo.WPch1 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_WPch1)));
                rawWeatherInfo.WPcd1 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_WPcd1)));
                rawWeatherInfo.N = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_N)));
                rawWeatherInfo.Neff = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Neff)));
                rawWeatherInfo.N05 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_N05)));
                rawWeatherInfo.Nl = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Nl)));
                rawWeatherInfo.Nm = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Nm)));
                rawWeatherInfo.Nh = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Nh)));
                rawWeatherInfo.Nlm = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Nlm)));
                rawWeatherInfo.H_BsC = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_H_BsC)));
                rawWeatherInfo.PPPP = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_PPPP)));
                rawWeatherInfo.E_PPP = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_E_PPP)));
                rawWeatherInfo.RadS1 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RadS1)));
                rawWeatherInfo.RadS3 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RadS3)));
                rawWeatherInfo.RRad1 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RRad1)));
                rawWeatherInfo.Rad1h = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_Rad1h)));
                rawWeatherInfo.RadL3 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RadL3)));
                rawWeatherInfo.VV = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_VV)));
                rawWeatherInfo.D1 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_D1)));
                rawWeatherInfo.SunD = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_SunD)));
                rawWeatherInfo.SunD3 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_SunD3)));
                rawWeatherInfo.RSunD = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_RSunD)));
                rawWeatherInfo.PSd00 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_PSd00)));
                rawWeatherInfo.PSd30 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_PSd30)));
                rawWeatherInfo.PSd60 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_PSd60)));
                rawWeatherInfo.wwM = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwM)));
                rawWeatherInfo.wwM6 = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwM6)));
                rawWeatherInfo.wwMh = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwMh)));
                rawWeatherInfo.wwMd = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_wwMd)));
                rawWeatherInfo.PEvap = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_PEvap)));
                rawWeatherInfo.uvHazardIndex = deSerializeString(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_UVHazardIndex)));
                rawWeatherInfo.timestamp = c.getLong(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_timestamp));
                rawWeatherInfo.polling_time = c.getLong(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_polling_time));
                rawWeatherInfo.elements = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_elements));
                return rawWeatherInfo;
            } else {
                return null;
            }
        }
    }

    public static CurrentWeatherInfo getWeatherInfo(Context context, Cursor cursor){
        return new CurrentWeatherInfo(context,getRawWeatherInfoFromCursor(cursor));
    }

    public static ContentValues getContentValuesFromWeatherWarning(WeatherWarning weatherWarning) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_polling_time, weatherWarning.polling_time);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_identifier, weatherWarning.identifier);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_sender, weatherWarning.sender);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_sent, weatherWarning.sent);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_status, weatherWarning.status);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_msgType, weatherWarning.msgType);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_source, weatherWarning.source);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_scope, weatherWarning.scope);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_codes, serializeStringFromArrayList(weatherWarning.codes));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_references, serializeStringFromArrayList(weatherWarning.references));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_language, weatherWarning.language);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_category, weatherWarning.category);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_event, weatherWarning.event);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_responseType, weatherWarning.responseType);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_urgency, weatherWarning.urgency);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_severity, weatherWarning.severity);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_certainty, weatherWarning.certainty);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_effective, weatherWarning.effective);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_onset, weatherWarning.onset);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_expires, weatherWarning.expires);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_senderName, weatherWarning.senderName);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_headline, weatherWarning.headline);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_description, weatherWarning.description);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_instruction, weatherWarning.instruction);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_web, weatherWarning.web);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_contact, weatherWarning.contact);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_profile_version, weatherWarning.profile_version);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_license, weatherWarning.license);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_ii, weatherWarning.ii);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_groups, serializeStringFromArrayList(weatherWarning.groups));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_area_color, weatherWarning.area_color);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_parameter_names, serializeStringFromArrayList(weatherWarning.parameter_names));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_parameter_values, serializeStringFromArrayList(weatherWarning.parameter_values));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_polygons, serializeStringFromArrayList(weatherWarning.polygons));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_excluded_polygons, serializeStringFromArrayList(weatherWarning.excluded_polygons));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_area_names, serializeStringFromArrayList(weatherWarning.area_names));
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_area_warncellIDs, serializeStringFromArrayList(weatherWarning.area_warncellIDs));
        return contentValues;
    }

    @SuppressLint("Range")
    public static WeatherWarning getWeatherWarningFromCursor(Cursor c) {
        if (c == null) {
            return null;
        } else {
            WeatherWarning weatherWarning = new WeatherWarning();
            weatherWarning.polling_time = c.getLong(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_polling_time));
            weatherWarning.identifier = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_identifier));
            weatherWarning.sender = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_sender));
            weatherWarning.sent = c.getLong(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_sent));
            weatherWarning.status = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_status));
            weatherWarning.msgType = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_msgType));
            weatherWarning.source = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_source));
            weatherWarning.scope = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_scope));
            weatherWarning.codes = deSerializeStringToArraylist(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_codes)));
            weatherWarning.references = deSerializeStringToArraylist(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_references)));
            weatherWarning.language = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_language));
            weatherWarning.category = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_category));
            weatherWarning.event = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_event));
            weatherWarning.responseType = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_responseType));
            weatherWarning.urgency = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_urgency));
            weatherWarning.severity = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_severity));
            weatherWarning.certainty = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_certainty));
            weatherWarning.effective = c.getLong(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_effective));
            weatherWarning.onset = c.getLong(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_onset));
            weatherWarning.expires = c.getLong(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_expires));
            weatherWarning.senderName = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_senderName));
            weatherWarning.headline = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_headline));
            weatherWarning.description = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_description));
            weatherWarning.instruction = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_instruction));
            weatherWarning.web = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_web));
            weatherWarning.contact = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_contact));
            weatherWarning.profile_version = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_profile_version));
            weatherWarning.license = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_license));
            weatherWarning.ii = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_ii));
            weatherWarning.groups = deSerializeStringToArraylist(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_groups)));
            weatherWarning.area_color = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_area_color));
            weatherWarning.parameter_names = deSerializeStringToArraylist(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_parameter_names)));
            weatherWarning.parameter_values = deSerializeStringToArraylist(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_parameter_values)));
            weatherWarning.polygons = deSerializeStringToArraylist(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_polygons)));
            weatherWarning.excluded_polygons = deSerializeStringToArraylist(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_excluded_polygons)));
            weatherWarning.area_names = deSerializeStringToArraylist(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_area_names)));
            weatherWarning.area_warncellIDs = deSerializeStringToArraylist(c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_WARNINGS_area_warncellIDs)));
            return weatherWarning;
        }
    }

    public static ContentValues getContentValuesFromTextForecast(TextForecast textForecast) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_identifier,textForecast.identifier);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_weburl,textForecast.webUrl);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_content,textForecast.content);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_title,textForecast.title);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_subtitle,textForecast.subtitle);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_issued_text,textForecast.issued_text);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_type,textForecast.type);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_issued,textForecast.issued);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_polled,textForecast.polled);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_outdated,textForecast.outdated);
        return contentValues;
    }

    @SuppressLint("Range")
    public static TextForecast getTextForecastFromCursor(Cursor c) {
        if (c == null) {
            return null;
        } else {
            TextForecast textForecast = new TextForecast();
            textForecast.identifier = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_identifier));
            textForecast.webUrl = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_weburl));
            textForecast.content = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_content));
            textForecast.title = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_title));
            textForecast.subtitle = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_subtitle));
            textForecast.issued_text = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_issued_text));
            textForecast.type = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_type));
            textForecast.issued = c.getLong(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_issued));
            textForecast.polled = c.getLong(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_polled));
            textForecast.outdated = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_TEXTS_outdated))>0;
            return textForecast;
        }
    }

    public static ContentValues getContentValuesFromArea(Areas.Area area) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_warncellid, area.warncellID);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_warncenter, area.warncenter);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_type, area.type);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_name, area.name);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_polygonstring, area.polygonString);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_centroid_latitude, area.centroidLatitude);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_centroid_longitude, area.centroidLongitude);
        return contentValues;
    }

    @SuppressLint("Range")
    public static Areas.Area getAreaFromCursor(Cursor c) {
        if (c == null) {
            return null;
        } else {
            Areas.Area area = new Areas.Area();
            area.warncellID = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_warncellid));
            area.warncenter = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_warncenter));
            area.type = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_type));
            area.name = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_name));
            area.polygonString = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_polygonstring));
            area.centroidLatitude = c.getDouble(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_centroid_latitude));
            area.centroidLongitude = c.getDouble(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_centroid_longitude));
            return area;
        }
    }

    @SuppressLint("Range")
    public static String getAreaNameFromCursor(Cursor c) {
        if (c == null) {
            return null;
        } else {
            return c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_name));
        }
    }

    @SuppressLint("Range")
    public static String getWarncellIDFromCursor(Cursor c) {
        if (c == null) {
            return null;
        } else {
            return c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_warncellid));
        }
    }

    public static ContentValues getContentValuesFromPollenArea(PollenArea pollenArea){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLENAREA_region_id,pollenArea.region_id);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLENAREA_partregion_id,pollenArea.partregion_id);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLENAREA_description,pollenArea.description);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLENAREA_polygonstring,pollenArea.polygonString);
        return contentValues;
    }

    @SuppressLint("Range")
    public static PollenArea getPollenAreaFromCursor(Cursor c){
        PollenArea pollenArea = new PollenArea();
        pollenArea.region_id = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLENAREA_region_id));
        pollenArea.partregion_id = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLENAREA_partregion_id));
        pollenArea.description = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLENAREA_description));
        pollenArea.polygonString = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLENAREA_polygonstring));
        return pollenArea;
    }

    public static ContentValues getContentValuesFromPollen(Pollen pollen){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_timestamp,pollen.timestamp);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_region_id,pollen.region_id);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_region_name,pollen.region_name);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_partregion_id,pollen.partregion_id);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_partregion_name,pollen.partregion_name);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_last_update,pollen.last_update);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_next_update,pollen.next_update);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_last_updateUTC,pollen.last_update_UTC);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_next_updateUTC,pollen.next_update_UTC);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_ambrosia0,pollen.ambrosia[0]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_beifuss0,pollen.beifuss[0]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_roggen0,pollen.roggen[0]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_esche0,pollen.esche[0]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_birke0,pollen.birke[0]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_hasel0,pollen.hasel[0]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_erle0,pollen.erle[0]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_graeser0,pollen.graeser[0]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_ambrosia1,pollen.ambrosia[1]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_beifuss1,pollen.beifuss[1]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_roggen1,pollen.roggen[1]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_esche1,pollen.esche[1]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_birke1,pollen.birke[1]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_hasel1,pollen.hasel[1]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_erle1,pollen.erle[1]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_graeser1,pollen.graeser[1]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_ambrosia2,pollen.ambrosia[2]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_beifuss2,pollen.beifuss[2]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_roggen2,pollen.roggen[2]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_esche2,pollen.esche[2]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_birke2,pollen.birke[2]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_hasel2,pollen.hasel[2]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_erle2,pollen.erle[2]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_graeser2,pollen.graeser[2]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_ambrosia3,pollen.ambrosia[3]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_beifuss3,pollen.beifuss[3]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_roggen3,pollen.roggen[3]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_esche3,pollen.esche[3]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_birke3,pollen.birke[3]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_hasel3,pollen.hasel[3]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_erle3,pollen.erle[3]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_graeser3,pollen.graeser[3]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_ambrosia4,pollen.ambrosia[4]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_beifuss4,pollen.beifuss[4]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_roggen4,pollen.roggen[4]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_esche4,pollen.esche[4]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_birke4,pollen.birke[4]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_hasel4,pollen.hasel[4]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_erle4,pollen.erle[4]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_graeser4,pollen.graeser[4]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_ambrosia5,pollen.ambrosia[5]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_beifuss5,pollen.beifuss[5]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_roggen5,pollen.roggen[5]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_esche5,pollen.esche[5]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_birke5,pollen.birke[5]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_hasel5,pollen.hasel[5]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_erle5,pollen.erle[5]);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_graeser5,pollen.graeser[5]);
        return contentValues;
    }

    @SuppressLint("Range")
    public static Pollen getPollenFromCursor(Cursor c){
        Pollen pollen = new Pollen();
        pollen.timestamp = c.getLong(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_timestamp));
        pollen.region_id = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_region_id));
        pollen.region_name = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_region_name));
        pollen.partregion_id = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_partregion_id));
        pollen.partregion_name = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_partregion_name));
        pollen.last_update = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_last_update));
        pollen.next_update = c.getString(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_next_update));
        pollen.last_update_UTC = c.getLong(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_last_updateUTC));
        pollen.next_update_UTC = c.getLong(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_next_updateUTC));
        pollen.ambrosia[0] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_ambrosia0));
        pollen.beifuss[0] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_beifuss0));
        pollen.roggen[0] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_roggen0));
        pollen.esche[0] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_esche0));
        pollen.birke[0] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_birke0));
        pollen.hasel[0] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_hasel0));
        pollen.erle[0] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_erle0));
        pollen.graeser[0] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_graeser0));
        pollen.ambrosia[1] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_ambrosia1));
        pollen.beifuss[1] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_beifuss1));
        pollen.roggen[1] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_roggen1));
        pollen.esche[1] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_esche1));
        pollen.birke[1] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_birke1));
        pollen.hasel[1] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_hasel1));
        pollen.erle[1] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_erle1));
        pollen.graeser[1] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_graeser1));
        pollen.ambrosia[2] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_ambrosia2));
        pollen.beifuss[2] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_beifuss2));
        pollen.roggen[2] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_roggen2));
        pollen.esche[2] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_esche2));
        pollen.birke[2] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_birke2));
        pollen.hasel[2] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_hasel2));
        pollen.erle[2] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_erle2));
        pollen.graeser[2] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_graeser2));
        pollen.ambrosia[3] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_ambrosia3));
        pollen.beifuss[3] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_beifuss3));
        pollen.roggen[3] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_roggen3));
        pollen.esche[3] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_esche3));
        pollen.birke[3] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_birke3));
        pollen.hasel[3] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_hasel3));
        pollen.erle[3] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_erle3));
        pollen.graeser[3] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_graeser3));
        pollen.ambrosia[4] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_ambrosia4));
        pollen.beifuss[4] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_beifuss4));
        pollen.roggen[4] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_roggen4));
        pollen.esche[4] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_esche4));
        pollen.birke[4] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_birke4));
        pollen.hasel[4] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_hasel4));
        pollen.erle[4] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_erle4));
        pollen.graeser[4] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_graeser4));
        pollen.ambrosia[5] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_ambrosia5));
        pollen.beifuss[5] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_beifuss5));
        pollen.roggen[5] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_roggen5));
        pollen.esche[5] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_esche5));
        pollen.birke[5] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_birke5));
        pollen.hasel[5] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_hasel5));
        pollen.erle[5] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_erle5));
        pollen.graeser[5] = c.getInt(c.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLEN_graeser5));
        return pollen;
    }

    public static ContentValues getContentValuesFromData_Blob(int id, byte[] blob){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_type,Cursor.FIELD_TYPE_BLOB);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_id,id);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_timestamp, Calendar.getInstance().getTimeInMillis());
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_valueBlob,blob);
        return contentValues;
    }

    public static ContentValues getContentValuesFromData_Float(int id,float value){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_type,Cursor.FIELD_TYPE_FLOAT);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_id,id);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_timestamp, Calendar.getInstance().getTimeInMillis());
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_valueFloat,value);
        return contentValues;
    }

    public static ContentValues getContentValuesFromData_Int(int id,int value){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_type,Cursor.FIELD_TYPE_INTEGER);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_id,id);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_timestamp, Calendar.getInstance().getTimeInMillis());
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_valueLong,value);
        return contentValues;
    }


    public static ContentValues getContentValuesFromData_Long(int id,long value){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_type,DataPackage.FIELD_TYPE_LONG);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_id,id);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_timestamp, Calendar.getInstance().getTimeInMillis());
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_valueLong,value);
        return contentValues;
    }

    public static ContentValues getContentValuesFromData_String(int id, String value){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_type,Cursor.FIELD_TYPE_STRING);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_id,id);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_timestamp, Calendar.getInstance().getTimeInMillis());
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_valueString,value);
        return contentValues;
    }

    public static ContentValues getContentValuesFromData_Boolean(int id, boolean value){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_type,DataPackage.FIELD_TYPE_BOOLEAN);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_id,id);
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_timestamp, Calendar.getInstance().getTimeInMillis());
        contentValues.put(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_valueLong,value);
        return contentValues;
    }

    @SuppressLint("Range")
    public static DataPackage getDataFromCursor(Cursor cursor){
        int typeValue = cursor.getInt(cursor.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_type));
        int intValue = 0; long longValue = 0l;
        if (typeValue == Cursor.FIELD_TYPE_INTEGER){
            intValue = cursor.getInt(cursor.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_valueLong));
        } else {
            longValue = cursor.getLong(cursor.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_valueLong));
        }
        DataPackage dataPackage = new DataPackage(cursor.getInt(cursor.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_id)),
                cursor.getLong(cursor.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_timestamp)),
                typeValue,
                cursor.getBlob(cursor.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_valueBlob)),
                intValue,
                longValue,
                cursor.getFloat(cursor.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_valueFloat)),
                cursor.getString(cursor.getColumnIndex(WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_valueString))
                );
        return dataPackage;
    }

    public static int checkForDatabaseUpgrade(Context c) {
        WeatherContentProvider.WeatherDatabaseHelper weatherDatabaseHelper = new WeatherContentProvider.WeatherDatabaseHelper(c);
        SQLiteDatabase sqLiteDatabase = weatherDatabaseHelper.getWritableDatabase();
        int i = sqLiteDatabase.getVersion();
        sqLiteDatabase.close();
        return i;
        // this should have triggered the update.
    }
}

