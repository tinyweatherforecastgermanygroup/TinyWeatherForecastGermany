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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;

public class TextForecasts {

    public static void writeTextForecastsToDatabaseUnconditionally(Context context, ArrayList<TextForecast> textForecasts){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        TextForecastContentProvider textForecastContentProvider = new TextForecastContentProvider();
        if (textForecasts!=null){
            for (int i=0; i<textForecasts.size(); i++){
                contentResolver.insert(TextForecastContentProvider.URI_TEXTDATA, textForecastContentProvider.getContentValuesFromTextForecast(textForecasts.get(i)));
            }
        } else {
            // nothing to do at the moment.
        }
    }

    public static ArrayList<TextForecast> getNewTextForecasts(Context context, ArrayList<TextForecast> currentTextForecasts){
        ArrayList<TextForecast> newTextForecasts = new ArrayList<TextForecast>();
        if (currentTextForecasts != null){
            ArrayList<TextForecast> savedTextForecasts = getTextForecasts(context);
            for (int i=0; i<currentTextForecasts.size(); i++){
                TextForecast currentTextForecast = currentTextForecasts.get(i);
                // check if this forecast text is already in database
                boolean isNew = true;
                for (int j=0; j<savedTextForecasts.size(); j++){
                    if (currentTextForecast.equals(savedTextForecasts.get(j))){
                        isNew = false;
                        break;
                    }
                }
                // remember if new
                if (isNew){
                    newTextForecasts.add(currentTextForecast);
                }
            }
        }
        return newTextForecasts;
    }

    public static ArrayList<TextForecast> getTextForecasts(Context context){
        TextForecastContentProvider textForecastContentProvider = new TextForecastContentProvider();
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        Cursor cursor  = null;
        ArrayList<TextForecast> textForecasts = new ArrayList<TextForecast>();
        try {
            cursor = contentResolver.query(TextForecastContentProvider.URI_TEXTDATA, null,null,null,null);
            int i=0;
            if (cursor.moveToFirst()){
                do {
                    TextForecast textForecast = textForecastContentProvider.getTextForecastFromCursor(cursor);
                    if (textForecast!=null){
                        textForecasts.add(textForecast);
                    }
                } while (cursor.moveToNext());
            }
            Collections.sort(textForecasts);
            Collections.reverse(textForecasts);
        } catch (Exception e) {
            PrivateLog.log(context,Tag.TEXTS,"database error when getting texts: "+e.getMessage());
        }
        return textForecasts;
    }

    public static void cleanTextForecastDatabase(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        int i = contentResolver.delete(TextForecastContentProvider.URI_TEXTDATA,null,null);
        PrivateLog.log(context,Tag.TEXTS,i+" text forecasts removed from database.");
    }

}
