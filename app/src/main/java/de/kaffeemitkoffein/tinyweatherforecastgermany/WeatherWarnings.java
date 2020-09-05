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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class WeatherWarnings {

    public final static int COMMUNEUNION_DWD_DIFF = 0;
    public final static int COMMUNEUNION_DWD_STAT = 1;

    private static URL getWarningsUrl(int mode) throws MalformedURLException {
        switch (mode){
            case 0: return new URL("https://opendata.dwd.de/weather/alerts/cap/COMMUNEUNION_DWD_DIFF/Z_CAP_C_EDZW_LATEST_PVW_DIFFERENCE_PREMIUMDWD_COMMUNEUNION_DE.zip");
        }
        return new URL("https://opendata.dwd.de/weather/alerts/cap/COMMUNEUNION_DWD_STAT/Z_CAP_C_EDZW_LATEST_PVW_STATUS_PREMIUMDWD_COMMUNEUNION_DE.zip");
    }

    public static void writeWarningsToDatabase(Context context, ArrayList<WeatherWarning> warnings){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        WeatherWarningContentProvider weatherWarningContentProvider = new WeatherWarningContentProvider();
        if (warnings!=null){
            for (int i=0; i<warnings.size(); i++){
                contentResolver.insert(WeatherWarningContentProvider.URI_WARNINGDATA, weatherWarningContentProvider.getContentValuesFromWeatherWarning(warnings.get(i)));
            }
        } else {
            PrivateLog.log(context,"Nothing written to database, fetched warning list is empty.");
        }
        WeatherSettings weatherSettings = new WeatherSettings(context);
        weatherSettings.setWarningsLastUpdateTime();
    }

    public static ArrayList<WeatherWarning> getCurrentWarnings(Context context){
        WeatherWarningContentProvider weatherWarningContentProvider = new WeatherWarningContentProvider();
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        Cursor cursor  = null;
        ArrayList<WeatherWarning> warnings = new ArrayList<WeatherWarning>();
        try {
            cursor = contentResolver.query(WeatherWarningContentProvider.URI_WARNINGDATA, null,null,null,null);
            int i=0;
            if (cursor.moveToFirst()){
                do {
                    WeatherWarning weatherWarning = weatherWarningContentProvider.getWeatherWarningFromCursor(cursor);
                    if (weatherWarning!=null){
                        warnings.add(weatherWarning);
                    }
                } while (cursor.moveToNext());
            }
            Collections.sort(warnings);
            return warnings;
        } catch (Exception e) {
            PrivateLog.log(context,Tag.DATABASE,"database error when getting weather warnings: "+e.getMessage());
        }
        // return null if no correspondig data set found in local database.
        return null;
    }

    public static void cleanWeatherWarningsDatabase(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        int i = contentResolver.delete(WeatherWarningContentProvider.URI_WARNINGDATA,null,null);
        PrivateLog.log(context,Tag.WARNINGS,i+" warnings removed from database.");
    }

}
