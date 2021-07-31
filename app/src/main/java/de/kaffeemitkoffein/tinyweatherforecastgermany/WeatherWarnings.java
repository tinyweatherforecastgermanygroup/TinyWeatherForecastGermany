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
import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.Collections;

public class WeatherWarnings {

    public final static int COMMUNEUNION_DWD_DIFF = 0;
    public final static int COMMUNEUNION_DWD_STAT = 1;

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

    public static ArrayList<WeatherWarning> getCurrentWarnings(Context context, boolean initPolygons){
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
            PrivateLog.log(context,Tag.DATABASE,"database error when getting weather warnings: "+e.getMessage());
        }
        // return null if no corresponding data set found in local database.
        return null;
    }

    public static void cleanWeatherWarningsDatabase(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        int i = contentResolver.delete(WeatherWarningContentProvider.URI_WARNINGDATA,null,null);
        PrivateLog.log(context,Tag.WARNINGS,i+" warnings removed from database.");
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

}

