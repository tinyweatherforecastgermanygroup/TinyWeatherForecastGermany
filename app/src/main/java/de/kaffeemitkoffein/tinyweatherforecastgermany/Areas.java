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
import android.database.SQLException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Executor;

public class Areas {

    public static class Area{
        public String warncellID;
        public String warncenter;
        public int type;
        public String name;
        public ArrayList<Polygon> polygons;
        public String polygonString;
    }

    public static class AreaDatabaseCreator{

        public final static int DATABASE_SIZE = 11638;

        private final Context context;
        private final Executor executor;
        private String versionLine;

        public static int getAreaDataVersion(String versionLine){
            try {
                String versionString = versionLine.substring(versionLine.indexOf(":")+2,versionLine.indexOf(",")).trim();
                String dateString = versionLine.substring(versionLine.indexOf(",")+2);
                int version = Integer.parseInt(versionString);
                return version;
            } catch (Exception e){
                return 0;
            }
        }

        public static int getAreaDataVersion(Context context){
            try {
                InputStream inputStream = context.getApplicationContext().getResources().openRawResource(R.raw.areas);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String versionLine = bufferedReader.readLine();
                while ((versionLine.length()==0) || String.valueOf(versionLine.charAt(0)).equals("#")){
                    versionLine = bufferedReader.readLine();
                }
                return getAreaDataVersion(versionLine);
            } catch (Exception e){
                return 0;
            }
        }

        public static boolean areAreasUpToDate(Context context){
            int sqlVersion = WeatherSettings.getAreaDatabaseVersion(context);
            int rawVersion = Areas.AreaDatabaseCreator.getAreaDataVersion(context);
            if (rawVersion==0){
                return false;
            }
            if (sqlVersion<rawVersion){
                return false;
            }
            return true;
        }

        public AreaDatabaseCreator(Context context, Executor executor){
            this.context = context;
            this.executor = executor;
        }

        public void create() {
            executor.execute(readAreasRunnable);
        }

        public void showProgress(final int progress, final String text){
            /*
             * Override this in the main app to show the progress
             */
            //Log.v("twfg","Progress is "+progress);
        }

        public void onFinished(){
            WeatherSettings.setAreaDatabaseReady(context);
            WeatherSettings.setAreaDatabaseVersion(context,getAreaDataVersion(versionLine));
        }

        private String removeQuotes(String s){
            return s.replace("\"","");
        }

        private Runnable readAreasRunnable = new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
                //contentResolver.delete(AreaContentProvider.URI_AREADATA,null,null);
                ArrayList<String> knownWarncellIDs = Areas.getAllWarncellIDs(context.getApplicationContext());
                try {
                    InputStream inputStream = context.getApplicationContext().getResources().openRawResource(R.raw.areas);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    versionLine = bufferedReader.readLine();
                    while ((versionLine.length()==0) || String.valueOf(versionLine.charAt(0)).equals("#")){
                        versionLine = bufferedReader.readLine();
                    }
                    String line;
                    int i = 0;
                    while ((line = bufferedReader.readLine()) != null){
                        String[] items = line.split("@");
                        Area area = new Area();
                        area.warncellID = items[0];
                        area.warncenter = items[1];
                        area.type = Integer.parseInt(items[2]);
                        area.name = removeQuotes(items[3]);
                        area.polygonString = items[4];
                        // check if entry exists
                        /*
                        if (getArea(context, area.warncellID)==null){
                            contentResolver.insert(AreaContentProvider.URI_AREADATA,AreaContentProvider.getContentValuesFromArea(area));
                        }

                         */
                        if (!knownWarncellIDs.contains(area.warncellID)){
                            contentResolver.insert(AreaContentProvider.URI_AREADATA,AreaContentProvider.getContentValuesFromArea(area));
                        }
                        i++;
                        if ((i % 25) == 0) {
                            showProgress((i*100)/DATABASE_SIZE, area.name);
                        }
                    }
                    showProgress(100,context.getResources().getString(R.string.welcome_ready));
                    onFinished();
                } catch (Exception e){
                    // do nothing
                }
            }
        };

    }

    public static int getAreaDatabaseSize(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        String[] columns = {AreaContentProvider.AreaDatabaseHelper.KEY_warncellid};
        Cursor cursor = contentResolver.query(AreaContentProvider.URI_AREADATA,columns,null,null,null);
        cursor.close();
        return cursor.getCount();
    }

    public static boolean doesAreaDatabaseExist(Context context){
        long i = getAreaDatabaseSize(context);
        if (i==AreaDatabaseCreator.DATABASE_SIZE){
            return true;
        }
        return false;
    }

    private Areas(){
    }

    public static Area getArea(Context context, String warincellID){
        AreaContentProvider areaContentProvider = new AreaContentProvider();
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        String selection = AreaContentProvider.KEY_warncellid + " =?";
        String[] selectionArg = {warincellID};
        Cursor cursor = contentResolver.query(AreaContentProvider.URI_AREADATA,null,selection,selectionArg,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                Area area = AreaContentProvider.getAreaFromCursor(cursor);
                area.polygons = Polygon.getPolygonArraylistFromString(area.polygonString);
                cursor.close();
                return area;
            } else {
                cursor.close();
            }
        }
        return null;
    }

    public static ArrayList<Area> getAreas(Context context, ArrayList<String> warincellIDs){
        ArrayList<Area> areas = new ArrayList<Area>();
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        String s = "";
        for (int i=0; i<warincellIDs.size(); i++){
            s=s+"?";
            if (i<warincellIDs.size()-1){
                s=s+",";
            }
        }
        String selection = AreaContentProvider.KEY_warncellid + " IN("+s+")";
        String[] selectionArg = warincellIDs.toArray(new String[warincellIDs.size()]);
        Cursor cursor = contentResolver.query(AreaContentProvider.URI_AREADATA,null,selection,selectionArg,null);
        int i = 0;
        if (cursor!=null){
            if (cursor.moveToFirst()){
                do {
                    Area area = AreaContentProvider.getAreaFromCursor(cursor);
                    area.polygons = Polygon.getPolygonArraylistFromString(area.polygonString);
                    areas.add(area); i++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return areas;
    }

    public static Area getAreaByName(Context context, String areaname){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        String selection = AreaContentProvider.KEY_name + " =?";
        String[] selectionArg = {areaname};
        Cursor cursor = contentResolver.query(AreaContentProvider.URI_AREADATA,null,selection,selectionArg,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                Area area = AreaContentProvider.getAreaFromCursor(cursor);
                area.polygons = Polygon.getPolygonArraylistFromString(area.polygonString);
                return area;
            }
            cursor.close();
        }
        return null;
    }

    public static ArrayList<String> getAllAreaNames(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        ArrayList<String> result = new ArrayList<String>();
        final String[] columns = {AreaContentProvider.KEY_name};
        try {
            Cursor cursor = contentResolver.query(AreaContentProvider.URI_AREADATA,columns,null,null,null);
            if (cursor!=null){
                if (cursor.moveToFirst()){
                    do {
                        String s = AreaContentProvider.getAreaNameFromCursor(cursor);
                        if (s!=null){
                            result.add(s);
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (SQLException e){
            // do nothing
        }
        return result;
    }

    public static ArrayList<String> getAllWarncellIDs(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        ArrayList<String> result = new ArrayList<String>();
        final String[] columns = {AreaContentProvider.KEY_warncellid};
        try {
            Cursor cursor = contentResolver.query(AreaContentProvider.URI_AREADATA,columns,null,null,null);
            if (cursor!=null){
                if (cursor.moveToFirst()){
                    do {
                        String s = AreaContentProvider.getWarncellIDFromCursor(cursor);
                        if (s!=null){
                            result.add(s);
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (SQLException e){
            // do nothing
        }
        return result;
    }

    public static class AreaNameReader implements Runnable{

        private Context context;

        public AreaNameReader(Context context){
            this.context = context;
        }

        public void onFinished(ArrayList<String> areanames){

        }

        @Override
        public void run() {
            ArrayList<String> areanames = Areas.getAllAreaNames(context);
            onFinished(areanames);
        }
    }

    public static int test(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        Cursor cursor = contentResolver.query(AreaContentProvider.URI_AREADATA,null,null,null,null);
        int i = 0;
        try {
            if (cursor.moveToFirst()){
                do {
                    Area area = AreaContentProvider.getAreaFromCursor(cursor);
                    i++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e){
        }
        return i;
    }

}
