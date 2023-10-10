/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021, 2022, 2023 Pawel Dube
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

    /* type:
     * 12 = Bundesland
     * 1, 10 = Kreis
     * 7,8 = Gemeinden
     * 2 = Binnensee
     * 5 = Küste
     * 4 = See
     */

    public static class Area{
        public String warncellID;
        public String warncenter;
        public int type;
        public String name;
        public ArrayList<Polygon> polygons;
        public String polygonString;
        public double centroidLatitude;
        public double centroidLongitude;

        final static class Type{
            final static int BUNDESLAND = 12;
            final static int KREIS = 1;
            final static int GEMEINDE = 6;
            final static int BINNENSEE = 2;
            final static int SEE = 4;
            final static int KUESTE = 5;
            final static int UNKNOWN = -1;
        }

        public int getType(){
            if (type==12){
                return Type.BUNDESLAND;
            }
            if ((type==1) || (type==10)){
                return Type.KREIS;
            }
            if ((type==7) || (type==8)){
                return Type.GEMEINDE;
            }
            if (type==2){
                return Type.BINNENSEE;
            }
            if (type==5){
                return Type.KUESTE;
            }
            if (type==4){
                return Type.SEE;
            }
            return Type.UNKNOWN;
        }

        public boolean isInArea(float longitude, float latitude){
            if (polygons==null){
                polygons = Polygon.getPolygonArraylistFromString(polygonString);
            }
            if (polygons.size()>0){
                for (int i=0; i<polygons.size(); i++){
                    Polygon polygon = polygons.get(i);
                    if (polygon.isInPolygon(longitude,latitude)){
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean isInArea(Weather.WeatherLocation weatherLocation){
            return isInArea((float) weatherLocation.longitude,(float) weatherLocation.latitude);
        }

    }

    public static class AreaDatabaseCreator{

        public final static int DATABASE_SIZE = 11676; // old in 2.0: 11638;

        private final Context context;
        private final Executor executor;
        private String versionLine;
        private int progressSteps = 50;

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

        public AreaDatabaseCreator(Context context, final Executor executor){
            this.context = context;
            this.executor = executor;
        }

        public void setProgressSteps(int p){
            this.progressSteps = p;
        }

        public void create() {
            executor.execute(readAreasRunnable);
        }

        public void showProgress(final int progress, final String text){
            /*
             * Override this in the main app to show the progress
             */
        }

        public void onFinished(){
            WeatherSettings.setAreaDatabaseReady(context);
            WeatherSettings.setAreaDatabaseVersion(context,getAreaDataVersion(versionLine));
        }

        private String removeQuotes(String s){
            return s.replace("\"","");
        }

        private final Runnable readAreasRunnable = new Runnable() {
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
                        area.polygons = Polygon.getPolygonArraylistFromString(area.polygonString);
                        // calculate centroid
                        double xSum = 0;
                        double ySum = 0;
                        if (area.polygons.size()>0){
                            for (int j=0; j<area.polygons.size(); j++) {
                                xSum = xSum + area.polygons.get(j).getPolygonXCentroid();
                                ySum = ySum + area.polygons.get(j).getPolygonYCentroid();
                            }
                            area.centroidLongitude = xSum / area.polygons.size();
                            area.centroidLatitude  = ySum / area.polygons.size();
                        }
                        if (!knownWarncellIDs.contains(area.warncellID)){
                            contentResolver.insert(WeatherContentManager.AREA_URI_ALL,WeatherContentManager.getContentValuesFromArea(area));
                        }
                        i++;
                        if ((i % progressSteps) == 0) {
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
        String[] columns = {WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_warncellid};
        Cursor cursor = contentResolver.query(WeatherContentManager.AREA_URI_ALL,columns,null,null,null);
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
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        String selection = WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_warncellid + " =?";
        String[] selectionArg = {warincellID};
        Cursor cursor = contentResolver.query(WeatherContentManager.AREA_URI_ALL,null,selection,selectionArg,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                Area area = WeatherContentManager.getAreaFromCursor(cursor);
                area.polygons = Polygon.getPolygonArraylistFromString(area.polygonString);
                cursor.close();
                return area;
            } else {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Gets Areas with initialized polygons.
     *
     * @param context
     * @param warincellIDs the warncellIDs to get the polygons for. When null, the whole database will be returned.
     * @return an arraylist of areas.
     */

    public static ArrayList<Area> getAreas(Context context, ArrayList<String> warincellIDs){
        ArrayList<Area> areas = new ArrayList<Area>();
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        String selection = null;
        String[] selectionArg = null;
        if (warincellIDs!=null){
            String s = "";
            for (int i=0; i<warincellIDs.size(); i++){
                s=s+"?";
                if (i<warincellIDs.size()-1){
                    s=s+",";
                }
            }
            selection = WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_warncellid + " IN("+s+")";
            selectionArg = warincellIDs.toArray(new String[warincellIDs.size()]);
        }
        Cursor cursor = contentResolver.query(WeatherContentManager.AREA_URI_ALL,null,selection,selectionArg,null);
        int i = 0;
        if (cursor!=null){
            if (cursor.moveToFirst()){
                do {
                    Area area = WeatherContentManager.getAreaFromCursor(cursor);
                    area.polygons = Polygon.getPolygonArraylistFromString(area.polygonString);
                    areas.add(area); i++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return areas;
    }

    public static ArrayList<Area> getAreas(Context context, int type){
        ArrayList<Area> areas = new ArrayList<Area>();
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        String s = "?";
        String[] selectionArg = {String.valueOf(type)};
        if (type== Area.Type.KREIS){
            s="?,?";
            selectionArg= new String[]{"1","10"};
        }
        if (type== Area.Type.GEMEINDE){
            s="?,?";
            selectionArg= new String[]{"7","8"};
        }
        String selection = WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_type + " IN("+s+")";
        Cursor cursor = contentResolver.query(WeatherContentManager.AREA_URI_ALL,null,selection,selectionArg,null);
        int i = 0;
        if (cursor!=null){
            if (cursor.moveToFirst()){
                do {
                    Area area = WeatherContentManager.getAreaFromCursor(cursor);
                    area.polygons = Polygon.getPolygonArraylistFromString(area.polygonString);
                    areas.add(area); i++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return areas;
    }

    public static ArrayList<Area> getAreas(Context context, int[] types){
        ArrayList<Area> areas = new ArrayList<Area>();
        for (int i=0; i<types.length; i++){
            areas.addAll(getAreas(context,types[i]));
        }
        return areas;
    }

    public static Area getAreaByName(Context context, String areaname){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        String selection = WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_name + " =?";
        String[] selectionArg = {areaname};
        Cursor cursor = contentResolver.query(WeatherContentManager.AREA_URI_ALL,null,selection,selectionArg,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                Area area = WeatherContentManager.getAreaFromCursor(cursor);
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
        final String[] columns = {WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_name};
        try {
            Cursor cursor = contentResolver.query(WeatherContentManager.AREA_URI_ALL,columns,null,null,null);
            if (cursor!=null){
                if (cursor.moveToFirst()){
                    do {
                        String s = WeatherContentManager.getAreaNameFromCursor(cursor);
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
        final String[] columns = {WeatherContentProvider.WeatherDatabaseHelper.KEY_AREAS_warncellid};
        try {
            Cursor cursor = contentResolver.query(WeatherContentManager.AREA_URI_ALL,columns,null,null,null);
            if (cursor!=null){
                if (cursor.moveToFirst()){
                    do {
                        String s = WeatherContentManager.getWarncellIDFromCursor(cursor);
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

    public static class AreaReader implements Runnable{

        private ArrayList<Area> areas;
        private ArrayList<String> warincellIDs;
        private Context context;

        public AreaReader(Context context, ArrayList<String> warincellIDs){
            this.context = context;
            this.warincellIDs = warincellIDs;
        }

        public void onFinished(ArrayList<Area> areas){

        }

        @Override
        public void run() {
            areas = getAreas(context,warincellIDs);
            onFinished(areas);
        }

    }

    public static int test(Context context){
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        Cursor cursor = contentResolver.query(WeatherContentManager.AREA_URI_ALL,null,null,null,null);
        int i = 0;
        try {
            if (cursor.moveToFirst()){
                do {
                    Area area = WeatherContentManager.getAreaFromCursor(cursor);
                    i++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e){
        }
        return i;
    }

}
