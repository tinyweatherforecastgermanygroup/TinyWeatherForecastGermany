/**
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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class WeatherWarnings {

    public final static int COMMUNEUNION_DWD_DIFF = 0;
    public final static int COMMUNEUNION_DWD_STAT = 1;

    public static void writeWarningsToDatabase(Context context, ArrayList<WeatherWarning> warnings){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        if (warnings!=null){
            for (int i=0; i<warnings.size(); i++){
                contentResolver.insert(WeatherContentManager.WARNING_URI_ALL, WeatherContentManager.getContentValuesFromWeatherWarning(warnings.get(i)));
            }
        } else {
            PrivateLog.log(context,PrivateLog.WARNINGS,PrivateLog.INFO,"Nothing written to database, fetched warning list is empty.");
        }
        WeatherSettings weatherSettings = new WeatherSettings(context);
        weatherSettings.setWarningsLastUpdateTime();
    }

    public static ArrayList<WeatherWarning> getCurrentWarnings(Context context, boolean initPolygons){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        Cursor cursor  = null;
        ArrayList<WeatherWarning> warnings = new ArrayList<WeatherWarning>();
        try {
            cursor = contentResolver.query(WeatherContentManager.WARNING_URI_ALL, null,null,null,null);
            int i=0;
            if (cursor.moveToFirst()){
                do {
                    WeatherWarning weatherWarning = WeatherContentManager.getWeatherWarningFromCursor(cursor);
                    i++;
                    if (weatherWarning!=null){
                        if (initPolygons){
                            weatherWarning.initPolygons(context);
                        }
                        warnings.add(weatherWarning);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            Collections.sort(warnings);
            return warnings;
        } catch (Exception e) {
            PrivateLog.log(context,PrivateLog.WARNINGS,PrivateLog.ERR,"database error when getting weather warnings: "+e.getMessage());
        }
        // return null if no corresponding data set found in local database.
        return null;
    }

    public static void cleanWeatherWarningsDatabase(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        int i = contentResolver.delete(WeatherContentManager.WARNING_URI_ALL,null,null);
        PrivateLog.log(context,PrivateLog.WARNINGS,PrivateLog.INFO,i+" warnings removed from database.");
    }

    public static ArrayList<WeatherWarning> getWarningsForLocation(Context context, ArrayList<WeatherWarning> warnings, Weather.WeatherLocation location){
        ArrayList<WeatherWarning> result = new ArrayList<WeatherWarning>();
        if (warnings!=null) {
            for (int i=0; i<warnings.size(); i++){
                if ((warnings.get(i).polygonlist==null) || (warnings.get(i).excluded_polygonlist==null)){
                    warnings.get(i).initPolygons(context);
                }
                if (warnings.get(i).isInPolygonGeo((float) location.latitude,(float) location.longitude)){
                    result.add(warnings.get(i));
                }
            }
        }
        return result;
    }

    public static class getWarningsForLocationRunnable implements Runnable{

        private Context context;
        private ArrayList<WeatherWarning> warnings;
        private Weather.WeatherLocation location;

        public getWarningsForLocationRunnable(Context context,ArrayList<WeatherWarning> warnings, Weather.WeatherLocation location){
            this.context = context;
            this.warnings = warnings;
            this.location = location;
            if (warnings==null){
                this.warnings = WeatherWarnings.getCurrentWarnings(context,true);
            }
            if (location==null){
                this.location = WeatherSettings.getSetStationLocation(context);
            }
        }

        public void onResult(ArrayList<WeatherWarning> result){
            // override this
        }

        @Override
        public void run() {
            ArrayList<WeatherWarning> result = getWarningsForLocation(context,warnings,location);
            onResult(result);
        }
    }

    public static final class WarningStringType{
        public static final int HEADLINE = 0;
        public static final int EVENT = 1;
    }

    public static SpannableStringBuilder getMiniWarningsString(Context context, ArrayList<WeatherWarning> applicableWarnings, long itemStartTime, long itemStopTime, boolean multiLine, int textType){
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int textPosition = 0;
        int warningsEnd = applicableWarnings.size();
        if (!multiLine){
            warningsEnd = 1;
        }
        ArrayList<String> alreadyAddedWarnings = new ArrayList<String>();
        for (int i=0; i<warningsEnd; i++){
            String text = applicableWarnings.get(i).headline;
            if (textType==WarningStringType.EVENT){
                text = applicableWarnings.get(i).event;
            }
            if (applicableWarnings.get(i).onset>itemStartTime){
                text = context.getResources().getString(R.string.from)+" "+simpleDateFormat.format(new Date(applicableWarnings.get(i).onset))+": " + text;
            }
            if (applicableWarnings.get(i).expires<itemStopTime){
                text = text + " ("+context.getResources().getString(R.string.ends)+" "+simpleDateFormat.format(new Date(applicableWarnings.get(i).expires))+")";
            }
            if ((!multiLine) && (applicableWarnings.size()>1)){
                text = text + " …";
            }
            if (!alreadyAddedWarnings.contains(text)){
                alreadyAddedWarnings.add(text);
                spannableStringBuilder.append(text);
                spannableStringBuilder.setSpan(new ForegroundColorSpan(ThemePicker.adaptColorToTheme(context,applicableWarnings.get(i).getWarningColor())),textPosition,textPosition+text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                textPosition = textPosition + text.length();
                String s = System.getProperty("line.separator");
                if ((s!=null) && (i<warningsEnd-1)){
                    if (s.length()>0){
                        spannableStringBuilder.append(s);
                        textPosition = textPosition + s.length();
                    }
                }
            }
        }
        return spannableStringBuilder;
    }

    public static class NotificationListDbHelper extends SQLiteOpenHelper{

        private final static String DBNAME="notifications.db";
        private final static int DBVERSION=1;
        private final static String TABLENAME = "notificationlist";
        private static final String COLUMN_ID = "ID";
        private static final String COLUMN_WARNID = "WARNID";
        private static final String COLUMN_NFID = "NFID";
        private static final String COLUMN_TIME = "TIME";
        private static final String SQL_CREATE = "CREATE TABLE "+TABLENAME+" ("+
                COLUMN_ID + " INTEGER PRIMARY KEY,"+
                COLUMN_WARNID + " TEXT," +
                COLUMN_NFID + " INTEGER," +
                COLUMN_TIME + " INTEGER)";
        private static final String SQL_DELETE ="DROP TABLE IF EXISTS "+TABLENAME;

        public NotificationListDbHelper(Context context) {
            super(context, DBNAME, null, DBVERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(SQL_DELETE);
            onCreate(sqLiteDatabase);
        }
    }

    public static class WarningNotification{
        String warnID;
        int nfid;
        long time;
    }

    public static long addToNotified(Context context, WeatherWarning warning, int id){
        NotificationListDbHelper notificationListDbHelper = new NotificationListDbHelper(context);
        SQLiteDatabase sqLiteDatabase = notificationListDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NotificationListDbHelper.COLUMN_WARNID,warning.identifier);
        contentValues.put(NotificationListDbHelper.COLUMN_NFID,id);
        contentValues.put(NotificationListDbHelper.COLUMN_TIME, Calendar.getInstance().getTimeInMillis());
        long rowID = sqLiteDatabase.insert(NotificationListDbHelper.TABLENAME,null,contentValues);
        sqLiteDatabase.close();
        return rowID;
    }

    public static ArrayList<WarningNotification> getNotificationElements(Context context){
        ArrayList<WarningNotification> result = new ArrayList<WarningNotification>();
        NotificationListDbHelper notificationListDbHelper = new NotificationListDbHelper(context);
        SQLiteDatabase sqLiteDatabase = notificationListDbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(NotificationListDbHelper.TABLENAME, null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                WarningNotification warningNotification = new WarningNotification();
                warningNotification.nfid = cursor.getInt(cursor.getColumnIndex(NotificationListDbHelper.COLUMN_NFID));
                warningNotification.warnID = cursor.getString(cursor.getColumnIndex(NotificationListDbHelper.COLUMN_WARNID));
                warningNotification.time = cursor.getLong(cursor.getColumnIndex(NotificationListDbHelper.COLUMN_TIME));
                result.add(warningNotification);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return result;
    }

    public static Integer getNotificationIdFromWeatherWarning(final ArrayList<WarningNotification> warningNotifications, final WeatherWarning weatherWarning){
        for (int i=0; i<warningNotifications.size(); i++){
            if (warningNotifications.get(i).warnID.equals(weatherWarning.identifier)){
                return warningNotifications.get(i).nfid;
            }
        }
        return null;
    }

    public static ArrayList<Integer> getExpiredWarningIds(Context context){
        ArrayList<Integer> result = new ArrayList<Integer>();
        ArrayList<WarningNotification> warningNotifications = getNotificationElements(context);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        ArrayList<WeatherWarning> weatherWarnings = WeatherWarnings.getCurrentWarnings(context,false);
        for (int i=0; i<weatherWarnings.size(); i++){
            WeatherWarning warning = weatherWarnings.get(i);
            Integer r = getNotificationIdFromWeatherWarning(warningNotifications,warning);
            if (r!=null){
                if (warning.expires<currentTime){
                    result.add(r);
                }
            }
        }
        return result;
    }

    public static boolean alreadyNotified(Context context, WeatherWarning weatherWarning){
        boolean result = false;
        NotificationListDbHelper notificationListDbHelper = new NotificationListDbHelper(context);
        SQLiteDatabase sqLiteDatabase = notificationListDbHelper.getReadableDatabase();
        String selection = NotificationListDbHelper.COLUMN_WARNID + " = ?";
        String[] selectionArgs = {weatherWarning.identifier};
        Cursor cursor = sqLiteDatabase.query(NotificationListDbHelper.TABLENAME, new String[]{NotificationListDbHelper.COLUMN_WARNID},selection,selectionArgs,null,null,null);
        if (cursor.moveToFirst()){
            result = true;
        }
        cursor.close();
        sqLiteDatabase.close();
        return result;
    }

    public static int clearNotified(Context context){
        NotificationListDbHelper notificationListDbHelper = new NotificationListDbHelper(context);
        SQLiteDatabase sqLiteDatabase = notificationListDbHelper.getReadableDatabase();
        String selection = NotificationListDbHelper.COLUMN_TIME + " < ?";
        String[] selectionArgs = {Long.toString(Calendar.getInstance().getTimeInMillis()-12*60*60*1000)};
        int i = sqLiteDatabase.delete(NotificationListDbHelper.TABLENAME,selection,selectionArgs);
        sqLiteDatabase.close();
        return i;
    }

    public static int clearAllNotified(Context context){
        NotificationListDbHelper notificationListDbHelper = new NotificationListDbHelper(context);
        SQLiteDatabase sqLiteDatabase = notificationListDbHelper.getReadableDatabase();
        // the "1" triggers count deleted
        int i = sqLiteDatabase.delete(NotificationListDbHelper.TABLENAME,"1",null);
        sqLiteDatabase.close();
        return i;
    }

}

