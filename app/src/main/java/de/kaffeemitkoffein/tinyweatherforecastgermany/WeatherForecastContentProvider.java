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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

public class WeatherForecastContentProvider extends ContentProvider {

    static final String AUTHORITY="de.kaffeemitkoffein.tinyweatherforecastgermany.weather";
    static final String DATASERVICE = "weatherforecast";
    static final String URL_SENSORDATA = "content://"+AUTHORITY+"/" + DATASERVICE;
    static final Uri URI_SENSORDATA = Uri.parse(URL_SENSORDATA);

    private WeatherForecastDatabaseHelper weatherForecastDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    public boolean onCreate() {
        weatherForecastDatabaseHelper = new WeatherForecastDatabaseHelper(getContext().getApplicationContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        sqLiteDatabase = weatherForecastDatabaseHelper.getReadableDatabase();
        Cursor c = sqLiteDatabase.query(WeatherForecastDatabaseHelper.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder,null);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return "text/plain";
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        sqLiteDatabase = weatherForecastDatabaseHelper.getWritableDatabase();
        sqLiteDatabase.enableWriteAheadLogging();
        sqLiteDatabase.insert(WeatherForecastDatabaseHelper.TABLE_NAME,null,contentValues);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int i = 0;
        sqLiteDatabase = weatherForecastDatabaseHelper.getWritableDatabase();
        sqLiteDatabase.enableWriteAheadLogging();
            i = sqLiteDatabase.delete(WeatherForecastDatabaseHelper.TABLE_NAME,selection,selectionArgs);
        return i;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        sqLiteDatabase = weatherForecastDatabaseHelper.getWritableDatabase();
        sqLiteDatabase.enableWriteAheadLogging();
        return sqLiteDatabase.update(WeatherForecastDatabaseHelper.TABLE_NAME,contentValues,selection,selectionArgs);
    }

    public static class WeatherForecastDatabaseHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 8;
        public static final String DATABASE_NAME = "weatherforecast";
        public static final String TABLE_NAME = "tables";
        public static final String KEY_id="id";
        public static final String KEY_timetext="timetext";
        public static final String KEY_name="name";
        public static final String KEY_description="description";
        public static final String KEY_longitude="longitude";
        public static final String KEY_latitude="latitude";
        public static final String KEY_altitude="altitude";
        public static final String KEY_polling_time="polling_time";
        public static final String KEY_elements="elements";
        public static final String KEY_timesteps="timesteps";
        public static final String KEY_TTT="TTT";
        public static final String KEY_E_TTT="E_TTT";
        public static final String KEY_T5cm="T5cm";
        public static final String KEY_Td="Td";
        public static final String KEY_E_Td="E_Td";
        public static final String KEY_Tx="Tx";
        public static final String KEY_Tn="Tn";
        public static final String KEY_TM="TM";
        public static final String KEY_TG="TG";
        public static final String KEY_DD="DD";
        public static final String KEY_E_DD="E_DD";
        public static final String KEY_FF="FF";
        public static final String KEY_E_FF="E_FF";
        public static final String KEY_FX1="FX1";
        public static final String KEY_FX3="FX3";
        public static final String KEY_FXh="FXh";
        public static final String KEY_FXh25="FXh25";
        public static final String KEY_FXh40="FXh40";
        public static final String KEY_FXh55="FXh55";
        public static final String KEY_FX625="FX625";
        public static final String KEY_FX640="FX640";
        public static final String KEY_FX655="FX655";
        public static final String KEY_RR1c="RR1c";
        public static final String KEY_RRL1c="RRL1c";
        public static final String KEY_RR3="RR3";
        public static final String KEY_RR6="RR6";
        public static final String KEY_RR3c="RR3c";
        public static final String KEY_RR6c="RR6c";
        public static final String KEY_RRhc="RRhc";
        public static final String KEY_RRdc="RRdc";
        public static final String KEY_RRS1c="RRS1c";
        public static final String KEY_RRS3c="RRS3c";
        public static final String KEY_R101="R101";
        public static final String KEY_R102="R102";
        public static final String KEY_R103="R103";
        public static final String KEY_R105="R105";
        public static final String KEY_R107="R107";
        public static final String KEY_R110="R110";
        public static final String KEY_R120="R120";
        public static final String KEY_R130="R130";
        public static final String KEY_R150="R150";
        public static final String KEY_RR1o1="RR1o1";
        public static final String KEY_RR1w1="RR1w1";
        public static final String KEY_RR1u1="RR1u1";
        public static final String KEY_R600="R600";
        public static final String KEY_Rh00="Rh00";
        public static final String KEY_R602="R602";
        public static final String KEY_Rh02="Rh02";
        public static final String KEY_Rd02="Rd02";
        public static final String KEY_R610="R610";
        public static final String KEY_Rh10="Rh10";
        public static final String KEY_R650="R650";
        public static final String KEY_Rh50="Rh50";
        public static final String KEY_Rd00="Rd00";
        public static final String KEY_Rd10="Rd10";
        public static final String KEY_Rd50="Rd50";
        public static final String KEY_wwPd="wwPd";
        public static final String KEY_DRR1="DRR1";
        public static final String KEY_wwZ="wwZ";
        public static final String KEY_wwZ6="wwZ6";
        public static final String KEY_wwZh="wwZh";
        public static final String KEY_wwD="wwD";
        public static final String KEY_wwD6="wwD6";
        public static final String KEY_wwDh="wwDh";
        public static final String KEY_wwC="wwC";
        public static final String KEY_wwC6="wwC6";
        public static final String KEY_wwCh="wwCh";
        public static final String KEY_wwT="wwT";
        public static final String KEY_wwT6="wwT6";
        public static final String KEY_wwTh="wwTh";
        public static final String KEY_wwTd="wwTd";
        public static final String KEY_wwL="wwL";
        public static final String KEY_wwL6="wwL6";
        public static final String KEY_wwLh="wwLh";
        public static final String KEY_wwS="wwS";
        public static final String KEY_wwS6="wwS6";
        public static final String KEY_wwSh="wwSh";
        public static final String KEY_wwF="wwF";
        public static final String KEY_wwF6="wwF6";
        public static final String KEY_wwFh="wwFh";
        public static final String KEY_wwP="wwP";
        public static final String KEY_wwP6="wwP6";
        public static final String KEY_wwPh="wwPh";
        public static final String KEY_VV10="VV10";
        public static final String KEY_ww="ww";
        public static final String KEY_ww3="ww3";
        public static final String KEY_W1W2="W1W2";
        public static final String KEY_WPc11="WPc11";
        public static final String KEY_WPc31="WPc31";
        public static final String KEY_WPc61="WPc61";
        public static final String KEY_WPch1="WPch1";
        public static final String KEY_WPcd1="WPcd1";
        public static final String KEY_N="N";
        public static final String KEY_N05="N05";
        public static final String KEY_Nl="Nl";
        public static final String KEY_Nm="Nm";
        public static final String KEY_Nh="Nh";
        public static final String KEY_Nlm="Nlm";
        public static final String KEY_H_BsC="H_BsC";
        public static final String KEY_PPPP="PPPP";
        public static final String KEY_E_PPP="E_PPP";
        public static final String KEY_RadS3="RadS3";
        public static final String KEY_RRad1="RRad1";
        public static final String KEY_Rad1h="Rad1h";
        public static final String KEY_RadL3="RadL3";
        public static final String KEY_VV="VV";
        public static final String KEY_D1="D1";
        public static final String KEY_SunD="SunD";
        public static final String KEY_SunD3="SunD3";
        public static final String KEY_RSunD="RSunD";
        public static final String KEY_PSd00="PSd00";
        public static final String KEY_PSd30="PSd30";
        public static final String KEY_PSd60="PSd60";
        public static final String KEY_wwM="wwM";
        public static final String KEY_wwM6="wwM6";
        public static final String KEY_wwMh="wwMh";
        public static final String KEY_wwMd="wwMd";
        public static final String KEY_PEvap="PEvap";
        public static final String KEY_timestamp="timestamp";

        public static final String SQL_COMMAND_CREATE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_id + " INTEGER PRIMARY KEY ASC,"
                + KEY_timetext + " TEXT,"
                + KEY_name + " TEXT,"
                + KEY_description + " TEXT,"
                + KEY_longitude + " REAL,"
                + KEY_latitude + " REAL,"
                + KEY_altitude + " REAL,"
                + KEY_timesteps + " TEXT,"
                + KEY_TTT + " TEXT,"
                + KEY_E_TTT + " TEXT,"
                + KEY_T5cm + " TEXT,"
                + KEY_Td + " TEXT,"
                + KEY_E_Td + " TEXT,"
                + KEY_Tx + " TEXT,"
                + KEY_Tn + " TEXT,"
                + KEY_TM + " TEXT,"
                + KEY_TG + " TEXT,"
                + KEY_DD + " TEXT,"
                + KEY_E_DD + " TEXT,"
                + KEY_FF + " TEXT,"
                + KEY_E_FF + " TEXT,"
                + KEY_FX1 + " TEXT,"
                + KEY_FX3 + " TEXT,"
                + KEY_FXh + " TEXT,"
                + KEY_FXh25 + " TEXT,"
                + KEY_FXh40 + " TEXT,"
                + KEY_FXh55 + " TEXT,"
                + KEY_FX625 + " TEXT,"
                + KEY_FX640 + " TEXT,"
                + KEY_FX655 + " TEXT,"
                + KEY_RR1c + " TEXT,"
                + KEY_RRL1c + " TEXT,"
                + KEY_RR3 + " TEXT,"
                + KEY_RR6 + " TEXT,"
                + KEY_RR3c + " TEXT,"
                + KEY_RR6c + " TEXT,"
                + KEY_RRhc + " TEXT,"
                + KEY_RRdc + " TEXT,"
                + KEY_RRS1c + " TEXT,"
                + KEY_RRS3c + " TEXT,"
                + KEY_R101 + " TEXT,"
                + KEY_R102 + " TEXT,"
                + KEY_R103 + " TEXT,"
                + KEY_R105 + " TEXT,"
                + KEY_R107 + " TEXT,"
                + KEY_R110 + " TEXT,"
                + KEY_R120 + " TEXT,"
                + KEY_R130 + " TEXT,"
                + KEY_R150 + " TEXT,"
                + KEY_RR1o1 + " TEXT,"
                + KEY_RR1w1 + " TEXT,"
                + KEY_RR1u1 + " TEXT,"
                + KEY_R600 + " TEXT,"
                + KEY_Rh00 + " TEXT,"
                + KEY_R602 + " TEXT,"
                + KEY_Rh02 + " TEXT,"
                + KEY_Rd02 + " TEXT,"
                + KEY_R610 + " TEXT,"
                + KEY_Rh10 + " TEXT,"
                + KEY_R650 + " TEXT,"
                + KEY_Rh50 + " TEXT,"
                + KEY_Rd00 + " TEXT,"
                + KEY_Rd10 + " TEXT,"
                + KEY_Rd50 + " TEXT,"
                + KEY_wwPd + " TEXT,"
                + KEY_DRR1 + " TEXT,"
                + KEY_wwZ + " TEXT,"
                + KEY_wwZ6 + " TEXT,"
                + KEY_wwZh + " TEXT,"
                + KEY_wwD + " TEXT,"
                + KEY_wwD6 + " TEXT,"
                + KEY_wwDh + " TEXT,"
                + KEY_wwC + " TEXT,"
                + KEY_wwC6 + " TEXT,"
                + KEY_wwCh + " TEXT,"
                + KEY_wwT + " TEXT,"
                + KEY_wwT6 + " TEXT,"
                + KEY_wwTh + " TEXT,"
                + KEY_wwTd + " TEXT,"
                + KEY_wwL + " TEXT,"
                + KEY_wwL6 + " TEXT,"
                + KEY_wwLh + " TEXT,"
                + KEY_wwS + " TEXT,"
                + KEY_wwS6 + " TEXT,"
                + KEY_wwSh + " TEXT,"
                + KEY_wwF + " TEXT,"
                + KEY_wwF6 + " TEXT,"
                + KEY_wwFh + " TEXT,"
                + KEY_wwP + " TEXT,"
                + KEY_wwP6 + " TEXT,"
                + KEY_wwPh + " TEXT,"
                + KEY_VV10 + " TEXT,"
                + KEY_ww + " TEXT,"
                + KEY_ww3 + " TEXT,"
                + KEY_W1W2 + " TEXT,"
                + KEY_WPc11 + " TEXT,"
                + KEY_WPc31 + " TEXT,"
                + KEY_WPc61 + " TEXT,"
                + KEY_WPch1 + " TEXT,"
                + KEY_WPcd1 + " TEXT,"
                + KEY_N + " TEXT,"
                + KEY_N05 + " TEXT,"
                + KEY_Nl + " TEXT,"
                + KEY_Nm + " TEXT,"
                + KEY_Nh + " TEXT,"
                + KEY_Nlm + " TEXT,"
                + KEY_H_BsC + " TEXT,"
                + KEY_PPPP + " TEXT,"
                + KEY_E_PPP + " TEXT,"
                + KEY_RadS3 + " TEXT,"
                + KEY_RRad1 + " TEXT,"
                + KEY_Rad1h + " TEXT,"
                + KEY_RadL3 + " TEXT,"
                + KEY_VV + " TEXT,"
                + KEY_D1 + " TEXT,"
                + KEY_SunD + " TEXT,"
                + KEY_SunD3 + " TEXT,"
                + KEY_RSunD + " TEXT,"
                + KEY_PSd00 + " TEXT,"
                + KEY_PSd30 + " TEXT,"
                + KEY_PSd60 + " TEXT,"
                + KEY_wwM + " TEXT,"
                + KEY_wwM6 + " TEXT,"
                + KEY_wwMh + " TEXT,"
                + KEY_wwMd + " TEXT,"
                + KEY_PEvap + " TEXT,"
                + KEY_timestamp + " INTEGER,"
                + KEY_polling_time + " INTEGER,"
                + KEY_elements + " INTEGER" + "" + ");";

        public static final String SQL_COMMAND_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public WeatherForecastDatabaseHelper(Context c) {
            super(c, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_COMMAND_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_version, int new_version) {
            sqLiteDatabase.execSQL(SQL_COMMAND_DROP_TABLE);
            onCreate(sqLiteDatabase);
        }
    }

        private final static String serial_serparator="_,_";

        private String serializeString(String s[]){
            return TextUtils.join(serial_serparator,s);
        }

        private String[] deSerializeString(String s){
            return TextUtils.split(s,serial_serparator);
        }

        public ContentValues getContentValuesFromWeatherCard(RawWeatherInfo rawWeatherInfo){
            ContentValues contentValues = new ContentValues();
            contentValues.put(WeatherForecastDatabaseHelper.KEY_timetext,rawWeatherInfo.timetext);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_name,rawWeatherInfo.weatherLocation.name);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_description,rawWeatherInfo.weatherLocation.description);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_longitude,rawWeatherInfo.weatherLocation.longitude);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_latitude,rawWeatherInfo.weatherLocation.latitude);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_altitude,rawWeatherInfo.weatherLocation.altitude);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_timesteps,serializeString(rawWeatherInfo.timesteps));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_TTT,serializeString(rawWeatherInfo.TTT));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_E_TTT,serializeString(rawWeatherInfo.E_TTT));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_T5cm,serializeString(rawWeatherInfo.T5cm));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Td,serializeString(rawWeatherInfo.Td));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_E_Td,serializeString(rawWeatherInfo.E_Td));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Tx,serializeString(rawWeatherInfo.Tx));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Tn,serializeString(rawWeatherInfo.Tn));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_TM,serializeString(rawWeatherInfo.TM));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_TG,serializeString(rawWeatherInfo.TG));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_DD,serializeString(rawWeatherInfo.DD));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_E_DD,serializeString(rawWeatherInfo.E_DD));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_FF,serializeString(rawWeatherInfo.FF));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_E_FF,serializeString(rawWeatherInfo.E_FF));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_FX1,serializeString(rawWeatherInfo.FX1));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_FX3,serializeString(rawWeatherInfo.FX3));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_FXh,serializeString(rawWeatherInfo.FXh));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_FXh25,serializeString(rawWeatherInfo.FXh25));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_FXh40,serializeString(rawWeatherInfo.FXh40));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_FXh55,serializeString(rawWeatherInfo.FXh55));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_FX625,serializeString(rawWeatherInfo.FX625));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_FX640,serializeString(rawWeatherInfo.FX640));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_FX655,serializeString(rawWeatherInfo.FX655));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RR1c,serializeString(rawWeatherInfo.RR1c));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RRL1c,serializeString(rawWeatherInfo.RRL1c));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RR3,serializeString(rawWeatherInfo.RR3));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RR6,serializeString(rawWeatherInfo.RR6));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RR3c,serializeString(rawWeatherInfo.RR3c));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RR6c,serializeString(rawWeatherInfo.RR6c));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RRhc,serializeString(rawWeatherInfo.RRhc));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RRdc,serializeString(rawWeatherInfo.RRdc));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RRS1c,serializeString(rawWeatherInfo.RRS1c));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RRS3c,serializeString(rawWeatherInfo.RRS3c));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R101,serializeString(rawWeatherInfo.R101));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R102,serializeString(rawWeatherInfo.R102));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R103,serializeString(rawWeatherInfo.R103));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R105,serializeString(rawWeatherInfo.R105));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R107,serializeString(rawWeatherInfo.R107));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R110,serializeString(rawWeatherInfo.R110));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R120,serializeString(rawWeatherInfo.R120));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R130,serializeString(rawWeatherInfo.R130));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R150,serializeString(rawWeatherInfo.R150));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RR1o1,serializeString(rawWeatherInfo.RR1o1));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RR1w1,serializeString(rawWeatherInfo.RR1w1));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RR1u1,serializeString(rawWeatherInfo.RR1u1));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R600,serializeString(rawWeatherInfo.R600));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Rh00,serializeString(rawWeatherInfo.Rh00));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R602,serializeString(rawWeatherInfo.R602));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Rh02,serializeString(rawWeatherInfo.Rh02));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Rd02,serializeString(rawWeatherInfo.Rd02));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R610,serializeString(rawWeatherInfo.R610));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Rh10,serializeString(rawWeatherInfo.Rh10));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_R650,serializeString(rawWeatherInfo.R650));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Rh50,serializeString(rawWeatherInfo.Rh50));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Rd00,serializeString(rawWeatherInfo.Rd00));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Rd10,serializeString(rawWeatherInfo.Rd10));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Rd50,serializeString(rawWeatherInfo.Rd50));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwPd,serializeString(rawWeatherInfo.wwPd));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_DRR1,serializeString(rawWeatherInfo.DRR1));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwZ,serializeString(rawWeatherInfo.wwZ));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwZ6,serializeString(rawWeatherInfo.wwZ6));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwZh,serializeString(rawWeatherInfo.wwZh));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwD,serializeString(rawWeatherInfo.wwD));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwD6,serializeString(rawWeatherInfo.wwD6));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwDh,serializeString(rawWeatherInfo.wwDh));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwC,serializeString(rawWeatherInfo.wwC));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwC6,serializeString(rawWeatherInfo.wwC6));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwCh,serializeString(rawWeatherInfo.wwCh));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwT,serializeString(rawWeatherInfo.wwT));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwT6,serializeString(rawWeatherInfo.wwT6));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwTh,serializeString(rawWeatherInfo.wwTh));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwTd,serializeString(rawWeatherInfo.wwTd));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwL,serializeString(rawWeatherInfo.wwL));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwL6,serializeString(rawWeatherInfo.wwL6));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwLh,serializeString(rawWeatherInfo.wwLh));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwS,serializeString(rawWeatherInfo.wwS));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwS6,serializeString(rawWeatherInfo.wwS6));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwSh,serializeString(rawWeatherInfo.wwSh));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwF,serializeString(rawWeatherInfo.wwF));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwF6,serializeString(rawWeatherInfo.wwF6));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwFh,serializeString(rawWeatherInfo.wwFh));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwP,serializeString(rawWeatherInfo.wwP));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwP6,serializeString(rawWeatherInfo.wwP6));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwPh,serializeString(rawWeatherInfo.wwPh));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_VV10,serializeString(rawWeatherInfo.VV10));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_ww,serializeString(rawWeatherInfo.ww));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_ww3,serializeString(rawWeatherInfo.ww3));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_W1W2,serializeString(rawWeatherInfo.W1W2));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_WPc11,serializeString(rawWeatherInfo.WPc11));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_WPc31,serializeString(rawWeatherInfo.WPc31));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_WPc61,serializeString(rawWeatherInfo.WPc61));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_WPch1,serializeString(rawWeatherInfo.WPch1));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_WPcd1,serializeString(rawWeatherInfo.WPcd1));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_N,serializeString(rawWeatherInfo.N));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_N05,serializeString(rawWeatherInfo.N05));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Nl,serializeString(rawWeatherInfo.Nl));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Nm,serializeString(rawWeatherInfo.Nm));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Nh,serializeString(rawWeatherInfo.Nh));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Nlm,serializeString(rawWeatherInfo.Nlm));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_H_BsC,serializeString(rawWeatherInfo.H_BsC));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_PPPP,serializeString(rawWeatherInfo.PPPP));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_E_PPP,serializeString(rawWeatherInfo.E_PPP));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RadS3,serializeString(rawWeatherInfo.RadS3));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RRad1,serializeString(rawWeatherInfo.RRad1));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_Rad1h,serializeString(rawWeatherInfo.Rad1h));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RadL3,serializeString(rawWeatherInfo.RadL3));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_VV,serializeString(rawWeatherInfo.VV));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_D1,serializeString(rawWeatherInfo.D1));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_SunD,serializeString(rawWeatherInfo.SunD));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_SunD3,serializeString(rawWeatherInfo.SunD3));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_RSunD,serializeString(rawWeatherInfo.RSunD));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_PSd00,serializeString(rawWeatherInfo.PSd00));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_PSd30,serializeString(rawWeatherInfo.PSd30));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_PSd60,serializeString(rawWeatherInfo.PSd60));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwM,serializeString(rawWeatherInfo.wwM));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwM6,serializeString(rawWeatherInfo.wwM6));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwMh,serializeString(rawWeatherInfo.wwMh));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wwMd,serializeString(rawWeatherInfo.wwMd));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_PEvap,serializeString(rawWeatherInfo.PEvap));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_timestamp,rawWeatherInfo.timestamp);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_polling_time,rawWeatherInfo.polling_time);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_elements,rawWeatherInfo.elements);
            return contentValues;
        }

        public RawWeatherInfo getWeatherCardFromCursor(Cursor c){
            if (c==null){
                return null;
            } else {
                RawWeatherInfo rawWeatherInfo = new RawWeatherInfo();
                if (c.moveToFirst()){
                    rawWeatherInfo.timetext = c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_timetext));
                    rawWeatherInfo.weatherLocation.name = c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_name));
                    rawWeatherInfo.weatherLocation.description = c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_description));
                    rawWeatherInfo.weatherLocation.longitude = c.getDouble(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_longitude));
                    rawWeatherInfo.weatherLocation.latitude = c.getDouble(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_latitude));
                    rawWeatherInfo.weatherLocation.altitude = c.getDouble(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_altitude));
                    rawWeatherInfo.timesteps = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_timesteps)));
                    rawWeatherInfo.TTT = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_TTT)));
                    rawWeatherInfo.E_TTT = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_E_TTT)));
                    rawWeatherInfo.T5cm = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_T5cm)));
                    rawWeatherInfo.Td = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Td)));
                    rawWeatherInfo.E_Td = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_E_Td)));
                    rawWeatherInfo.Tx = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Tx)));
                    rawWeatherInfo.Tn = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Tn)));
                    rawWeatherInfo.TM = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_TM)));
                    rawWeatherInfo.TG = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_TG)));
                    rawWeatherInfo.DD = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_DD)));
                    rawWeatherInfo.E_DD = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_E_DD)));
                    rawWeatherInfo.FF = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_FF)));
                    rawWeatherInfo.E_FF = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_E_FF)));
                    rawWeatherInfo.FX1 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_FX1)));
                    rawWeatherInfo.FX3 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_FX3)));
                    rawWeatherInfo.FXh = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_FXh)));
                    rawWeatherInfo.FXh25 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_FXh25)));
                    rawWeatherInfo.FXh40 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_FXh40)));
                    rawWeatherInfo.FXh55 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_FXh55)));
                    rawWeatherInfo.FX625 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_FX625)));
                    rawWeatherInfo.FX640 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_FX640)));
                    rawWeatherInfo.FX655 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_FX655)));
                    rawWeatherInfo.RR1c = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RR1c)));
                    rawWeatherInfo.RRL1c = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RRL1c)));
                    rawWeatherInfo.RR3 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RR3)));
                    rawWeatherInfo.RR6 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RR6)));
                    rawWeatherInfo.RR3c = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RR3c)));
                    rawWeatherInfo.RR6c = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RR6c)));
                    rawWeatherInfo.RRhc = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RRhc)));
                    rawWeatherInfo.RRdc = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RRdc)));
                    rawWeatherInfo.RRS1c = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RRS1c)));
                    rawWeatherInfo.RRS3c = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RRS3c)));
                    rawWeatherInfo.R101 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R101)));
                    rawWeatherInfo.R102 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R102)));
                    rawWeatherInfo.R103 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R103)));
                    rawWeatherInfo.R105 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R105)));
                    rawWeatherInfo.R107 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R107)));
                    rawWeatherInfo.R110 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R110)));
                    rawWeatherInfo.R120 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R120)));
                    rawWeatherInfo.R130 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R130)));
                    rawWeatherInfo.R150 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R150)));
                    rawWeatherInfo.RR1o1 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RR1o1)));
                    rawWeatherInfo.RR1w1 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RR1w1)));
                    rawWeatherInfo.RR1u1 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RR1u1)));
                    rawWeatherInfo.R600 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R600)));
                    rawWeatherInfo.Rh00 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Rh00)));
                    rawWeatherInfo.R602 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R602)));
                    rawWeatherInfo.Rh02 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Rh02)));
                    rawWeatherInfo.Rd02 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Rd02)));
                    rawWeatherInfo.R610 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R610)));
                    rawWeatherInfo.Rh10 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Rh10)));
                    rawWeatherInfo.R650 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_R650)));
                    rawWeatherInfo.Rh50 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Rh50)));
                    rawWeatherInfo.Rd00 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Rd00)));
                    rawWeatherInfo.Rd10 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Rd10)));
                    rawWeatherInfo.Rd50 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Rd50)));
                    rawWeatherInfo.wwPd = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwPd)));
                    rawWeatherInfo.DRR1 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_DRR1)));
                    rawWeatherInfo.wwZ = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwZ)));
                    rawWeatherInfo.wwZ6 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwZ6)));
                    rawWeatherInfo.wwZh = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwZh)));
                    rawWeatherInfo.wwD = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwD)));
                    rawWeatherInfo.wwD6 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwD6)));
                    rawWeatherInfo.wwDh = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwDh)));
                    rawWeatherInfo.wwC = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwC)));
                    rawWeatherInfo.wwC6 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwC6)));
                    rawWeatherInfo.wwCh = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwCh)));
                    rawWeatherInfo.wwT = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwT)));
                    rawWeatherInfo.wwT6 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwT6)));
                    rawWeatherInfo.wwTh = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwTh)));
                    rawWeatherInfo.wwTd = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwTd)));
                    rawWeatherInfo.wwL = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwL)));
                    rawWeatherInfo.wwL6 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwL6)));
                    rawWeatherInfo.wwLh = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwLh)));
                    rawWeatherInfo.wwS = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwS)));
                    rawWeatherInfo.wwS6 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwS6)));
                    rawWeatherInfo.wwSh = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwSh)));
                    rawWeatherInfo.wwF = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwF)));
                    rawWeatherInfo.wwF6 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwF6)));
                    rawWeatherInfo.wwFh = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwFh)));
                    rawWeatherInfo.wwP = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwP)));
                    rawWeatherInfo.wwP6 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwP6)));
                    rawWeatherInfo.wwPh = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwPh)));
                    rawWeatherInfo.VV10 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_VV10)));
                    rawWeatherInfo.ww = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_ww)));
                    rawWeatherInfo.ww3 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_ww3)));
                    rawWeatherInfo.W1W2 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_W1W2)));
                    rawWeatherInfo.WPc11 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_WPc11)));
                    rawWeatherInfo.WPc31 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_WPc31)));
                    rawWeatherInfo.WPc61 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_WPc61)));
                    rawWeatherInfo.WPch1 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_WPch1)));
                    rawWeatherInfo.WPcd1 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_WPcd1)));
                    rawWeatherInfo.N = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_N)));
                    rawWeatherInfo.N05 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_N05)));
                    rawWeatherInfo.Nl = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Nl)));
                    rawWeatherInfo.Nm = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Nm)));
                    rawWeatherInfo.Nh = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Nh)));
                    rawWeatherInfo.Nlm = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Nlm)));
                    rawWeatherInfo.H_BsC = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_H_BsC)));
                    rawWeatherInfo.PPPP = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_PPPP)));
                    rawWeatherInfo.E_PPP = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_E_PPP)));
                    rawWeatherInfo.RadS3 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RadS3)));
                    rawWeatherInfo.RRad1 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RRad1)));
                    rawWeatherInfo.Rad1h = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_Rad1h)));
                    rawWeatherInfo.RadL3 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RadL3)));
                    rawWeatherInfo.VV = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_VV)));
                    rawWeatherInfo.D1 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_D1)));
                    rawWeatherInfo.SunD = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_SunD)));
                    rawWeatherInfo.SunD3 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_SunD3)));
                    rawWeatherInfo.RSunD = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_RSunD)));
                    rawWeatherInfo.PSd00 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_PSd00)));
                    rawWeatherInfo.PSd30 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_PSd30)));
                    rawWeatherInfo.PSd60 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_PSd60)));
                    rawWeatherInfo.wwM = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwM)));
                    rawWeatherInfo.wwM6 = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwM6)));
                    rawWeatherInfo.wwMh = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwMh)));
                    rawWeatherInfo.wwMd = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wwMd)));
                    rawWeatherInfo.PEvap = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_PEvap)));
                    rawWeatherInfo.timestamp = c.getLong(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_timestamp));
                    rawWeatherInfo.polling_time = c.getLong(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_polling_time));
                    rawWeatherInfo.elements = c.getInt(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_elements));
                    return rawWeatherInfo;
                } else {
                    return null;
                }
            }
        }

    public int writeWeatherForecast(Context c,RawWeatherInfo weatherCard){
        ContentResolver contentResolver = c.getApplicationContext().getContentResolver();
        WeatherSettings weatherSettings = new WeatherSettings(c);
        int i = contentResolver.update(WeatherForecastContentProvider.URI_SENSORDATA,getContentValuesFromWeatherCard(weatherCard),WeatherForecastContentProvider.WeatherForecastDatabaseHelper.KEY_name+"=?",new String[] {weatherSettings.station_name});
        if (i==0){
            contentResolver.insert(WeatherForecastContentProvider.URI_SENSORDATA,getContentValuesFromWeatherCard(weatherCard));
            i = 1;
        }
        return i;
    }

    // deprecated
    private RawWeatherInfo readWeatherForecast(Context c){
        ContentResolver contentResolver = c.getApplicationContext().getContentResolver();
        Cursor cursor = contentResolver.query(WeatherForecastContentProvider.URI_SENSORDATA,null,null,null,null,null);
        return getWeatherCardFromCursor(cursor);
    }

    public static int checkForDatabaseUpgrade(Context c) {
        WeatherForecastDatabaseHelper weatherForecastDatabaseHelper = new WeatherForecastDatabaseHelper(c);
        SQLiteDatabase sqLiteDatabase = weatherForecastDatabaseHelper.getWritableDatabase();
        int i = sqLiteDatabase.getVersion();
        sqLiteDatabase.close();
        return i;
        // this should have triggered the update.
    }


}


