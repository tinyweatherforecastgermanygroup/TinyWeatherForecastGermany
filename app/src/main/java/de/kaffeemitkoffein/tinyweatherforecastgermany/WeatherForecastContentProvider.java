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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

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

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "weatherforecast";
        public static final String TABLE_NAME = "tables";
        public static final String KEY_id = "id";
        public static final String KEY_fdat = "fdat";
        public static final String KEY_ortscode = "ortscode";
        public static final String KEY_zeitstempel = "zeitstempel";
        public static final String KEY_klimagebiet = "klimagebiet";
        public static final String KEY_ausgegeben_am = "ausgegeben_am";
        public static final String KEY_ausgegeben_von = "ausgegeben_von";
        public static final String KEY_uhrzeit = "uhrzeit";
        public static final String KEY_bewoelkung = "bewoelkung";
        public static final String KEY_bewoelkung_max = "bewoelkung_max";
        public static final String KEY_bewoelkung_min = "bewoelkung_min";
        public static final String KEY_niederschlag = "niederschlag";
        public static final String KEY_niederschlag_max = "niederschlag_max";
        public static final String KEY_niederschlag_min = "niederschlag_min";
        public static final String KEY_lufttemperatur = "lufttemperatur";
        public static final String KEY_lufttemperatur_max = "lufttemperatur_max";
        public static final String KEY_lufttemperatur_min = "lufttemperatur_min";
        public static final String KEY_wind = "wind";
        public static final String KEY_boeen = "boeen";
        public static final String KEY_timestamp = "timestamp";

        public static final String SQL_COMMAND_CREATE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_id + " INTEGER PRIMARY KEY ASC,"
                + KEY_fdat + " TEXT,"
                + KEY_ortscode + " TEXT,"
                + KEY_zeitstempel + " TEXT,"
                + KEY_klimagebiet + " TEXT,"
                + KEY_ausgegeben_am + " TEXT,"
                + KEY_ausgegeben_von + " TEXT,"
                + KEY_uhrzeit + " TEXT,"
                + KEY_bewoelkung + " TEXT,"
                + KEY_bewoelkung_max + " TEXT,"
                + KEY_bewoelkung_min + " TEXT,"
                + KEY_niederschlag + " TEXT,"
                + KEY_niederschlag_max + " TEXT,"
                + KEY_niederschlag_min + " TEXT,"
                + KEY_lufttemperatur + " TEXT,"
                + KEY_lufttemperatur_max + " TEXT,"
                + KEY_lufttemperatur_min + " TEXT,"
                + KEY_wind + " TEXT,"
                + KEY_boeen + " TEXT,"
                + KEY_timestamp + " INTEGER" + ");";

        public static final String SQL_COMMAND_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public WeatherForecastDatabaseHelper(Context c) {
            super(c, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_COMMAND_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
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

        public ContentValues getContentValuesFromWeatherCard(WeatherCard weatherCard){
            ContentValues contentValues = new ContentValues();
            contentValues.put(WeatherForecastDatabaseHelper.KEY_fdat,weatherCard.fdat);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_ortscode,weatherCard.ortscode);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_zeitstempel,weatherCard.zeitstempel);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_klimagebiet,weatherCard.klimagebiet);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_ausgegeben_am,weatherCard.ausgegeben_am);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_ausgegeben_von,weatherCard.ausgegeben_von);
            contentValues.put(WeatherForecastDatabaseHelper.KEY_uhrzeit,serializeString(weatherCard.uhrzeit));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_bewoelkung,serializeString(weatherCard.bewoelkung));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_bewoelkung_max,serializeString(weatherCard.bewoelkung_max));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_bewoelkung_min,serializeString(weatherCard.bewoelkung_min));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_niederschlag,serializeString(weatherCard.niederschlag));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_niederschlag_max,serializeString(weatherCard.niederschlag_max));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_niederschlag_min,serializeString(weatherCard.niederschlag_min));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_lufttemperatur,serializeString(weatherCard.lufttemperatur));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_lufttemperatur_max,serializeString(weatherCard.lufttemperatur_max));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_lufttemperatur_min,serializeString(weatherCard.lufttemperatur_min));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_wind,serializeString(weatherCard.wind));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_boeen,serializeString(weatherCard.boeen));
            contentValues.put(WeatherForecastDatabaseHelper.KEY_timestamp,weatherCard.polling_time);
            return contentValues;
        }

        public Uri writeWeatherForecast(Context c,WeatherCard weatherCard){
            ContentResolver contentResolver = c.getApplicationContext().getContentResolver();
            contentResolver.delete(WeatherForecastContentProvider.URI_SENSORDATA,null,null);
            return contentResolver.insert(WeatherForecastContentProvider.URI_SENSORDATA,getContentValuesFromWeatherCard(weatherCard));
        }

        public WeatherCard readWeatherForecast(Context c){
            ContentResolver contentResolver = c.getApplicationContext().getContentResolver();
            Cursor cursor = contentResolver.query(WeatherForecastContentProvider.URI_SENSORDATA,null,null,null,null,null);
            return getWeatherCardFromCursor(cursor);
        }

        public WeatherCard getWeatherCardFromCursor(Cursor c){
            if (c==null){
                return null;
            } else {
                WeatherCard weatherCard = new WeatherCard();
                if (c.moveToFirst()){
                    weatherCard.fdat = c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_fdat));
                    weatherCard.ortscode = c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_ortscode));
                    weatherCard.zeitstempel = c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_zeitstempel));
                    weatherCard.klimagebiet = c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_klimagebiet));
                    weatherCard.ausgegeben_am = c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_ausgegeben_am));
                    weatherCard.ausgegeben_von = c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_ausgegeben_von));
                    weatherCard.uhrzeit = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_uhrzeit)));
                    weatherCard.bewoelkung = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_bewoelkung)));
                    weatherCard.bewoelkung_max = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_bewoelkung_max)));
                    weatherCard.bewoelkung_min = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_bewoelkung_min)));
                    weatherCard.niederschlag = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_niederschlag)));
                    weatherCard.niederschlag_max = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_niederschlag_max)));
                    weatherCard.niederschlag_min= deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_niederschlag_min)));
                    weatherCard.lufttemperatur = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_lufttemperatur)));
                    weatherCard.lufttemperatur_max = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_lufttemperatur_max)));
                    weatherCard.lufttemperatur_min = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_lufttemperatur_min)));
                    weatherCard.wind = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_wind)));
                    weatherCard.boeen = deSerializeString(c.getString(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_boeen)));
                    weatherCard.polling_time = c.getLong(c.getColumnIndex(WeatherForecastDatabaseHelper.KEY_timestamp));
                    return weatherCard;
                } else {
                    return null;
                }
            }
        }
    }


