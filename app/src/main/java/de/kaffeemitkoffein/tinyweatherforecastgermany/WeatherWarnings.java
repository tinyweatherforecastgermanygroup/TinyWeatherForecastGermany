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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;


public class WeatherWarnings {

    public final static int COMMUNEUNION_DWD_DIFF = 0;
    public final static int COMMUNEUNION_DWD_STAT = 1;


    private static URL getWarningsUrl(int mode) throws MalformedURLException {
        switch (mode){
            case 0: return new URL("https://opendata.dwd.de/weather/alerts/cap/COMMUNEUNION_DWD_DIFF/Z_CAP_C_EDZW_LATEST_PVW_DIFFERENCE_PREMIUMDWD_COMMUNEUNION_DE.zip");
        }
        return new URL("https://opendata.dwd.de/weather/alerts/cap/COMMUNEUNION_DWD_STAT/Z_CAP_C_EDZW_LATEST_PVW_STATUS_PREMIUMDWD_COMMUNEUNION_DE.zip");
    }

    public void WarningsToLog(Context context){
        Log.v(Tag.WARNINGS,"Warnings started!");
        WeatherWarningReader weatherWarningReader = new WeatherWarningReader(context);
        Log.v(Tag.WARNINGS,"Warnings initalized");
        weatherWarningReader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.v(Tag.WARNINGS,"Warnings executed");
    }

    public static void writeWarningsToDatabase(Context context, ArrayList<WeatherWarning> warnings){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        WeatherWarningContentProvider weatherWarningContentProvider = new WeatherWarningContentProvider();
        if (warnings!=null){
            for (int i=0; i<warnings.size(); i++){
                contentResolver.insert(WeatherWarningContentProvider.URI_SENSORDATA,
                                        weatherWarningContentProvider.getContentValuesFromWeatherWarning(warnings.get(i)));
                Log.v("WEATHER WARNINGS", "written to database: "+i);
            }
        } else {
            PrivateLog.log(context,"Nothing written to database, fetched warning list is empty.");
        }
    }

    public static ArrayList<WeatherWarning> getCurrentWarnings(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        Cursor cursor;
        ArrayList<WeatherWarning> warnings = new ArrayList<WeatherWarning>();
        try {
            cursor = contentResolver.query(WeatherWarningContentProvider.URI_SENSORDATA,
                    null,null,null,null);
            if (cursor.moveToFirst()){
                WeatherWarningContentProvider weatherWarningContentProvider = new WeatherWarningContentProvider();
                while (!cursor.isAfterLast()){
                    WeatherWarning weatherWarning = weatherWarningContentProvider.getWeatherWarningFromCursor(cursor);
                    if (weatherWarning!=null){
                        warnings.add(weatherWarning);
                    }
                }
                cursor.moveToNext();
            }
            return warnings;
        } catch (Exception e) {
            PrivateLog.log(context,Tag.DATABASE,"database error when getting weather warnings: "+e.getMessage());
        }
        // return null if no correspondig data set found in local database.
        return null;
    }

    public static void cleanWeatherWarningsDatabase(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        int i = contentResolver.delete(WeatherWarningContentProvider.URI_SENSORDATA,null,null);
        PrivateLog.log(context,Tag.WARNINGS,i+" warnings removed from database.");
    }

    public static long getOldestPollingTime(Context context){
        WeatherWarningContentProvider weatherWarningContentProvider = new WeatherWarningContentProvider();
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        ArrayList<WeatherWarning> weatherWarnings = new ArrayList<WeatherWarning>();
        Cursor cursor;
        String[] projection={WeatherWarningContentProvider.WeatherWarningDatabaseHelper.KEY_polling_time};
        try {
            cursor = contentResolver.query(WeatherWarningContentProvider.URI_SENSORDATA,
                    projection,null,null,null);
            if (cursor!=null){
                do {
                    WeatherWarning warning = weatherWarningContentProvider.getWeatherWarningFromCursor(cursor);
                    if (warning!=null){
                        weatherWarnings.add(warning);
                    }
                } while (cursor.moveToNext());
            } else {
                Log.v(Tag.WARNINGS,"polling time : cursor is null.");
            }
        } catch (Exception e){
            PrivateLog.log(context,Tag.WARNINGS,"Reading timestamps for cleanup failed: "+e.getMessage());
        }
        if (weatherWarnings.size()==0){
            Log.v(Tag.WARNINGS,"polling time : empty list!");
            return 0;
        }
        long oldest_poll = weatherWarnings.get(0).polling_time;
        Log.v(Tag.WARNINGS,"polling time A: "+oldest_poll);
        for (int j=1; j<weatherWarnings.size(); j++){
            if (weatherWarnings.get(j).polling_time<oldest_poll){
                oldest_poll = weatherWarnings.get(j).polling_time;
                Log.v(Tag.WARNINGS,"polling time B: "+oldest_poll);
            }
        }
        return oldest_poll;
    }

    public static boolean areWarningsOutdated(Context context){
        long oldest_poll = getOldestPollingTime(context);
        WeatherSettings weatherSettings = new WeatherSettings(context);
        return oldest_poll + weatherSettings.getWarningsCacheTimeInMillis() <= Calendar.getInstance().getTimeInMillis();
    }

}
