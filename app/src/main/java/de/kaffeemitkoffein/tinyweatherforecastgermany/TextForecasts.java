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
import android.graphics.drawable.Drawable;
import android.os.Build;
import java.util.ArrayList;
import java.util.Calendar;
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
            // debug
            // ArrayList<TextForecast> savedTextForecasts = new ArrayList<TextForecast>();
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
                // exclude "LATEST" entries, since they are double anyway
                if (currentTextForecast.identifier.contains("LATEST")){
                    isNew = false;
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
            cursor.close();
            Collections.sort(textForecasts);
            Collections.reverse(textForecasts);
        } catch (Exception e) {
            PrivateLog.log(context,PrivateLog.WARNINGS,PrivateLog.ERR,"database error when getting texts: "+e.getMessage());
        }
        return textForecasts;
    }

    public static ArrayList<TextForecast> getLatestTextForecastsOnly(Context context){
        ArrayList<TextForecast> textForecasts = getTextForecasts(context);
        ArrayList<TextForecast> latestForecasts = new ArrayList<TextForecast>();
        ArrayList<Integer> alreadyAdded = new ArrayList<Integer>();
        for (int position=0; position<textForecasts.size(); position++){
            TextForecast textForecast = textForecasts.get(position);
            // add all features available
            if (textForecast.type== Type.FEATURE){
                latestForecasts.add(textForecast);
            } else {
                // add newest item from each category. Remember, the arraylist
                // with the textforecasts is already sorted and the first
                // entries are the newest ones.
                if (!alreadyAdded.contains(textForecast.type)){
                    alreadyAdded.add(textForecast.type);
                    latestForecasts.add(textForecast);
                }
            }
        }
        return latestForecasts;
    }

    public static void eraseTextForecastDatabase(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        int i = contentResolver.delete(TextForecastContentProvider.URI_TEXTDATA,null,null);
        PrivateLog.log(context,PrivateLog.WARNINGS,PrivateLog.INFO,i+" text forecasts removed from database.");
    }

    public static void cleanTextForecastDatabase(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        ArrayList<TextForecast> textForecasts = getTextForecasts(context);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        for (int i=0; i<textForecasts.size(); i++){
            if (textForecasts.get(i).issued + 1000*60*60*24*10 < currentTime){
                String whereClause = TextForecastContentProvider.TextForecastDatabaseHelper.KEY_identifier+ " = ?";
                String[] selectionArgs = {textForecasts.get(i).identifier};
                int delCount = contentResolver.delete(TextForecastContentProvider.URI_TEXTDATA,whereClause,selectionArgs);
            }
        }
    }


    public static final String TEXT_WEBPATH = "https://opendata.dwd.de/weather/text_forecasts/txt/";
    public static final String TEXT_WEBPATH_LEGACY = "http://opendata.dwd.de/weather/text_forecasts/txt/";
    public static final String MARITIME_WEBPATH_DE = "https://opendata.dwd.de/weather/maritime/forecast/german/";
    public static final String MARITIME_WEBPATH_LEGACY_DE = "https://opendata.dwd.de/weather/maritime/forecast/german/";
    public static final String MARITIME_WEBPATH_EN = "https://opendata.dwd.de/weather/maritime/forecast/english/";
    public static final String MARITIME_WEBPATH_LEGACY_EN = "https://opendata.dwd.de/weather/maritime/forecast/english/";

    public static class Type{
        public final static int FEATURE = 0;
        public final static int KURZFRIST = 1;
        public final static int MITTELFRIST = 2;
        public final static int MARITIME_NORD_UND_OSTSEE = 100;
        public final static int MARITIME_DEUTSCHE_NORD_UND_OSTSEE= 101;
        public final static int MARITIME_MITTELMEER = 102;
        public final static int MARITIME_NORD_UND_OSTSEE_MITTELFRIST = 103;
        public final static int MARITIME_WARNING= 104;
    }

    public static class TextForecastFile{
        String filename;
        int type;

        public TextForecastFile(String filename, int type){
            this.filename = filename;
            this.type = type;
        }
    }

    public static class TextForecastSource{
        String webPath;
        String webPathLegacy;
        ArrayList<TextForecastFile> files;

        public TextForecastSource(String webPath, String webPathLegacy, ArrayList<TextForecastFile> files){
            this.webPath = webPath;
            this.webPathLegacy = webPathLegacy;
            this.files = files;
        }

        public String getWebPath(Context context){
            if (WeatherSettings.isTLSdisabled(context)){
                return this.webPathLegacy;
            }
            return this.webPath;
        }

        public TextForecastFile getValidFile(String s){
            boolean result = false;
            for (int i=0; i<files.size(); i++){
                if (s.contains(files.get(i).filename)){
                    return files.get(i);
                }
            }
            return null;
        }

    }

    @SuppressWarnings("deprecation")
    public static String getLanguage(Context context){
        String country = "EN";
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            country = context.getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            country = context.getResources().getConfiguration().locale.getCountry();
        }
        return country;
    }

    @SuppressWarnings("deprecation")
    public static Drawable getTextForecastDrawable(Context context, int id){
        int resource = 0;
        switch (id){
            case Type.MARITIME_DEUTSCHE_NORD_UND_OSTSEE: resource = R.drawable.northsea_d; break;
            case Type.MARITIME_NORD_UND_OSTSEE: resource = R.drawable.northsea; break;
            case Type.MARITIME_MITTELMEER: resource = R.drawable.mediterranian; break;
            case Type.MARITIME_NORD_UND_OSTSEE_MITTELFRIST: resource = R.drawable.northsea_mf; break;
            case Type.MARITIME_WARNING: resource = R.drawable.warning; break;
            case Type.KURZFRIST: resource = R.drawable.green; break;
            case Type.MITTELFRIST: resource = R.drawable.yellow; break;
            case Type.FEATURE: resource = R.drawable.purple; break;
            default: resource = R.drawable.blue;
        }
        return context.getResources().getDrawable(resource);
    }

    public static String getTypeString(Context context, TextForecast textForecast) {
        String result;
        switch (textForecast.type) {
            case TextForecasts.Type.MARITIME_DEUTSCHE_NORD_UND_OSTSEE:
                result = context.getResources().getString(R.string.textforecasttype_maritime_german_north_and_baltic);
                break;
            case TextForecasts.Type.MARITIME_NORD_UND_OSTSEE:
                result = context.getResources().getString(R.string.textforecasttype_maritime_north_and_baltic);
                break;
            case TextForecasts.Type.MARITIME_MITTELMEER:
                result = context.getResources().getString(R.string.textforecasttype_maritime_mediterranian);
                break;
            case TextForecasts.Type.MARITIME_NORD_UND_OSTSEE_MITTELFRIST:
                result = context.getResources().getString(R.string.textforecasttype_maritime_north_and_baltic_medium_term);
                break;
            case TextForecasts.Type.MARITIME_WARNING:
                result = context.getResources().getString(R.string.textforecasttype_maritime_warning);
                break;
            case TextForecasts.Type.KURZFRIST:
                result = context.getResources().getString(R.string.textforecasttype_short_term);
                break;
            case TextForecasts.Type.MITTELFRIST:
                result = context.getResources().getString(R.string.textforecasttype_medium_term);
                break;
            case TextForecasts.Type.FEATURE:
                result = context.getResources().getString(R.string.textforecasttype_medium_feature);
                break;
            default:
                result = context.getResources().getString(R.string.textforecasttype_medium_other);
        }
        return result;
    }

    public static ArrayList<TextForecastSource> getTextForecastSources(Context context){
        ArrayList<TextForecastSource> resultList = new ArrayList<TextForecastSource>();
        // construct the features and common forecasts
        TextForecastFile common_feature = new TextForecastFile("FPDL",Type.FEATURE);
        TextForecastFile common_kurzfrist = new TextForecastFile("SXDL31",Type.KURZFRIST);
        TextForecastFile common_mittelfrist = new TextForecastFile("SXDL33",Type.MITTELFRIST);
        ArrayList<TextForecastFile> common_files = new ArrayList<TextForecastFile>();
        common_files.add(common_feature);
        common_files.add(common_kurzfrist);
        common_files.add(common_mittelfrist);
        TextForecastSource common = new TextForecastSource(TEXT_WEBPATH,TEXT_WEBPATH_LEGACY,common_files);
        resultList.add(common);
        // construct the maritime forecasts
        TextForecastFile maritime_NordseeOstseeDE = new TextForecastFile("FQEN50_EDZW",Type.MARITIME_NORD_UND_OSTSEE); // german version
        TextForecastFile maritime_NordseeOstseeEN = new TextForecastFile("FQEN70_EDZW",Type.MARITIME_NORD_UND_OSTSEE); // english version
        TextForecastFile maritime_dNordseeOstseeDE = new TextForecastFile("FQEN51_EDZW",Type.MARITIME_DEUTSCHE_NORD_UND_OSTSEE); // german version
        TextForecastFile maritime_dNordseeOstseeEN = new TextForecastFile("FQEN71_EDZW",Type.MARITIME_DEUTSCHE_NORD_UND_OSTSEE); // english version
        TextForecastFile maritime_MittelmeerDE = new TextForecastFile("FQMM60_EDZW",Type.MARITIME_MITTELMEER);
        TextForecastFile maritime_MittelmeerEN = new TextForecastFile("FQMM80_EDZW",Type.MARITIME_MITTELMEER);
        TextForecastFile maritime_NordseeOstseeMittelfrist = new TextForecastFile("FXDL40_EDZW",Type.MARITIME_NORD_UND_OSTSEE_MITTELFRIST);
        TextForecastFile maritime_Warnings = new TextForecastFile("WODL45_EDZW",Type.MARITIME_WARNING);
        ArrayList<TextForecastFile> maritime_files = new ArrayList<TextForecastFile>();
        maritime_files.add(maritime_NordseeOstseeMittelfrist);
        maritime_files.add(maritime_Warnings);
        if (getLanguage(context).contains("DE")){
            maritime_files.add(maritime_NordseeOstseeDE);
            maritime_files.add(maritime_dNordseeOstseeDE);
            maritime_files.add(maritime_MittelmeerDE);
            TextForecastSource maritime = new TextForecastSource(MARITIME_WEBPATH_DE,MARITIME_WEBPATH_LEGACY_DE,maritime_files);
            resultList.add(maritime);
        } else {
            maritime_files.add(maritime_NordseeOstseeEN);
            maritime_files.add(maritime_dNordseeOstseeEN);
            maritime_files.add(maritime_MittelmeerEN);
            TextForecastSource maritime = new TextForecastSource(MARITIME_WEBPATH_EN,MARITIME_WEBPATH_LEGACY_EN,maritime_files);
            resultList.add(maritime);
        }
        return resultList;
    }

}
