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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;


public class WeatherWarningContentProvider extends ContentProvider {

    static final String AUTHORITY = "de.kaffeemitkoffein.tinyweatherforecastgermany.warnings";
    static final String DATASERVICE = "weatherwarnings";
    static final String URL_SENSORDATA = "content://" + AUTHORITY + "/" + DATASERVICE;
    static final Uri URI_SENSORDATA = Uri.parse(URL_SENSORDATA);

    private WeatherWarningDatabaseHelper weatherWarningDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    public static class WeatherWarningDatabaseHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "weatherforecast";
        public static final String TABLE_NAME = "tables";
        public static final String KEY_id = "id";
        public static final String KEY_polling_time = "polling_time";
        public static final String KEY_identifier = "identifier";
        public static final String KEY_sender = "sender";
        public static final String KEY_sent = "sent";
        public static final String KEY_status = "status";
        public static final String KEY_msgType = "msgType";
        public static final String KEY_source = "source";
        public static final String KEY_scope = "scope";
        public static final String KEY_codes = "codes";
        public static final String KEY_references = "reference_key";
        public static final String KEY_language = "language";
        public static final String KEY_category = "category";
        public static final String KEY_event = "event";
        public static final String KEY_responseType = "responseType";
        public static final String KEY_urgency = "urgency";
        public static final String KEY_severity = "severity";
        public static final String KEY_certainty = "certainty";
        public static final String KEY_effective = "effective";
        public static final String KEY_onset = "onset";
        public static final String KEY_expires = "expires";
        public static final String KEY_senderName = "senderName";
        public static final String KEY_headline = "headline";
        public static final String KEY_description = "description";
        public static final String KEY_instruction = "instruction";
        public static final String KEY_web = "web";
        public static final String KEY_contact = "contact";
        public static final String KEY_profile_version = "profile_version";
        public static final String KEY_license = "license";
        public static final String KEY_ii = "ii";
        public static final String KEY_groups = "groups";
        public static final String KEY_area_color = "area_color";
        public static final String KEY_parameter_names = "parameter_names";
        public static final String KEY_parameter_values = "parameter_values";
        public static final String KEY_polygons = "polygons";
        public static final String KEY_area_names = "area_names";
        public static final String KEY_area_warncellIDs = "area_warncellIDs";

        public static final String SQL_COMMAND_CREATE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_id + " INTEGER PRIMARY KEY ASC,"
                + KEY_polling_time + " INTEGER,"
                + KEY_identifier + " TEXT,"
                + KEY_sender + " TEXT,"
                + KEY_sent + " INTEGER,"
                + KEY_status + " TEXT,"
                + KEY_msgType + " TEXT,"
                + KEY_source + " TEXT,"
                + KEY_scope + " TEXT,"
                + KEY_codes + " TEXT,"
                + KEY_references + " TEXT,"
                + KEY_language + " TEXT,"
                + KEY_category + " TEXT,"
                + KEY_event + " TEXT,"
                + KEY_responseType + " TEXT,"
                + KEY_urgency + " TEXT,"
                + KEY_severity + " TEXT,"
                + KEY_certainty + " TEXT,"
                + KEY_effective + " INTEGER,"
                + KEY_onset + " INTEGER,"
                + KEY_expires + " INTEGER,"
                + KEY_senderName + " TEXT,"
                + KEY_headline + " TEXT,"
                + KEY_description + " TEXT,"
                + KEY_instruction + " TEXT,"
                + KEY_web + " TEXT,"
                + KEY_contact + " TEXT,"
                + KEY_profile_version + " TEXT,"
                + KEY_license + " TEXT,"
                + KEY_ii + " TEXT,"
                + KEY_groups + " TEXT,"
                + KEY_area_color + " TEXT,"
                + KEY_parameter_names + " TEXT,"
                + KEY_parameter_values + " TEXT,"
                + KEY_polygons + " TEXT,"
                + KEY_area_names + " TEXT,"
                + KEY_area_warncellIDs + " TEXT" + ");";

        public static final String SQL_COMMAND_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public WeatherWarningDatabaseHelper(Context c) {
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

    private final static String serial_serparator = "_,_";

    private String serializeString(ArrayList<String> s) {
        return TextUtils.join(serial_serparator, s);
    }

    private ArrayList<String> deSerializeString(String s) {
        String[] results = TextUtils.split(s, serial_serparator);
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < results.length; i++) {
            list.add(results[i]);
        }
        return list;
    }

    public ContentValues getContentValuesFromWeatherWarning(WeatherWarning weatherWarning) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherWarningDatabaseHelper.KEY_polling_time, weatherWarning.polling_time);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_identifier, weatherWarning.identifier);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_sender, weatherWarning.sender);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_sent, weatherWarning.sent);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_status, weatherWarning.status);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_msgType, weatherWarning.msgType);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_source, weatherWarning.source);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_scope, weatherWarning.scope);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_codes, serializeString(weatherWarning.codes));
        contentValues.put(WeatherWarningDatabaseHelper.KEY_references, serializeString(weatherWarning.references));
        contentValues.put(WeatherWarningDatabaseHelper.KEY_language, weatherWarning.language);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_category, weatherWarning.category);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_event, weatherWarning.event);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_responseType, weatherWarning.responseType);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_urgency, weatherWarning.urgency);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_severity, weatherWarning.severity);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_certainty, weatherWarning.certainty);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_effective, weatherWarning.effective);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_onset, weatherWarning.onset);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_expires, weatherWarning.expires);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_senderName, weatherWarning.senderName);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_headline, weatherWarning.headline);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_description, weatherWarning.description);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_instruction, weatherWarning.instruction);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_web, weatherWarning.web);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_contact, weatherWarning.contact);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_profile_version, weatherWarning.profile_version);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_license, weatherWarning.license);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_ii, weatherWarning.ii);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_groups, serializeString(weatherWarning.groups));
        contentValues.put(WeatherWarningDatabaseHelper.KEY_area_color, weatherWarning.area_color);
        contentValues.put(WeatherWarningDatabaseHelper.KEY_parameter_names, serializeString(weatherWarning.parameter_names));
        contentValues.put(WeatherWarningDatabaseHelper.KEY_parameter_values, serializeString(weatherWarning.parameter_values));
        contentValues.put(WeatherWarningDatabaseHelper.KEY_polygons, serializeString(weatherWarning.polygons));
        contentValues.put(WeatherWarningDatabaseHelper.KEY_area_names, serializeString(weatherWarning.area_names));
        contentValues.put(WeatherWarningDatabaseHelper.KEY_area_warncellIDs, serializeString(weatherWarning.area_warncellIDs));
        return contentValues;
    }

    public WeatherWarning getWeatherWarningFromCursor(Cursor c) {
        if (c == null) {
            return null;
        } else {
            WeatherWarning weatherWarning = new WeatherWarning();
            if (c.moveToFirst()) {
                weatherWarning.polling_time = c.getLong(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_polling_time));
                weatherWarning.identifier = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_identifier));
                weatherWarning.sender = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_sender));
                weatherWarning.sent = c.getLong(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_sent));
                weatherWarning.status = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_status));
                weatherWarning.msgType = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_msgType));
                weatherWarning.source = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_source));
                weatherWarning.scope = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_scope));
                weatherWarning.codes = deSerializeString(c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_codes)));
                weatherWarning.references = deSerializeString(c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_references)));
                weatherWarning.language = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_language));
                weatherWarning.category = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_category));
                weatherWarning.event = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_event));
                weatherWarning.responseType = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_responseType));
                weatherWarning.urgency = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_urgency));
                weatherWarning.severity = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_severity));
                weatherWarning.certainty = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_certainty));
                weatherWarning.effective = c.getLong(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_effective));
                weatherWarning.onset = c.getLong(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_onset));
                weatherWarning.expires = c.getLong(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_expires));
                weatherWarning.senderName = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_senderName));
                weatherWarning.headline = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_headline));
                weatherWarning.description = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_description));
                weatherWarning.instruction = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_instruction));
                weatherWarning.web = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_web));
                weatherWarning.contact = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_contact));
                weatherWarning.profile_version = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_profile_version));
                weatherWarning.license = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_license));
                weatherWarning.ii = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_ii));
                weatherWarning.groups = deSerializeString(c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_groups)));
                weatherWarning.area_color = c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_area_color));
                weatherWarning.parameter_names = deSerializeString(c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_parameter_names)));
                weatherWarning.parameter_values = deSerializeString(c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_parameter_values)));
                weatherWarning.polygons = deSerializeString(c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_polygons)));
                weatherWarning.area_names = deSerializeString(c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_area_names)));
                weatherWarning.area_warncellIDs = deSerializeString(c.getString(c.getColumnIndex(WeatherWarningDatabaseHelper.KEY_area_warncellIDs)));
                return weatherWarning;
            } else {
                return null;
            }
        }
    }

    public void writeWeatherWarning(Context c,WeatherWarning weatherWarning){
        ContentResolver contentResolver = c.getApplicationContext().getContentResolver();
        contentResolver.insert(WeatherWarningContentProvider.URI_SENSORDATA,getContentValuesFromWeatherWarning(weatherWarning));
    }

}
