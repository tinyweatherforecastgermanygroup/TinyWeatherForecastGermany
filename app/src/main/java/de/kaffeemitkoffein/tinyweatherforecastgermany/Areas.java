package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.database.Cursor;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Executor;

public class Areas {

    private Context context;
    private Executor executor;

    public static class Area{
        public String warncellID;
        public String warncenter;
        public int type;
        public String name;
        public Polygon polygon;
        public String polygonString;
    }

    private String removeQuotes(String s){
        return s.replace("\"","");
    }

    private Runnable readAreasRunnable = new Runnable() {
        @Override
        public void run() {
            AreaContentProvider areaContentProvider = new AreaContentProvider();
            areaContentProvider.setContext(context);
            try {
            InputStream inputStream = context.getApplicationContext().getResources().openRawResource(R.raw.areas);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null){
                String[] items = line.split(",");
                Area area = new Area();
                area.warncellID = items[0];
                area.warncenter = items[1];
                area.type = Integer.parseInt(items[2]);
                area.name = removeQuotes(items[3]);
                area.polygonString = new String();
                for (int q=4; q<items.length; q=q+2){
                    // switch lat long to correspond to warnings data
                    String s = items[q+1]+","+items[q];
                    area.polygonString = area.polygonString + s + " ";
                }
                area.polygonString = area.polygonString.trim();
                areaContentProvider.writeArea(context,area);
                //Log.v("TWFG","Database entry: "+i+ " "+ area.warncellID + " " + area.name); i++;
            }
           WeatherSettings.setPrefMunicipalitesDatabaseReady(context);
           test(context);
            } catch (Exception e){
                // do nothing
            }
        }
    };

    public static boolean doesAreaDatabaseExist(Context context){
        AreaContentProvider areaContentProvider = new AreaContentProvider();
        areaContentProvider.setContext(context);
        String[] columns = {AreaContentProvider.AreaDatabaseHelper.KEY_warncellid};
        Cursor cursor = areaContentProvider.query(AreaContentProvider.URI_AREADATA,columns, null, null, null);
        int i=0;
        if (cursor.moveToFirst()){
            do {
                i++;
            } while (cursor.moveToNext());
        }
        if (i>10000){
            return true;
        } else {
            context.deleteDatabase(AreaContentProvider.AreaDatabaseHelper.DATABASE_NAME);
            return false;
        }
    }

    public Areas(final Context context, Executor executor){
        this.context = context;
        if (doesAreaDatabaseExist(context)){
            /*
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    test(context);
                }
            });
             */
            // do nothing here
        } else {
            executor.execute(readAreasRunnable);
            /*
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    test(context);
                }
            });
             */
        }
    }

    public static Area getArea(Context context, String warincellID){
        AreaContentProvider areaContentProvider = new AreaContentProvider();
        areaContentProvider.setContext(context);
        String selection = AreaContentProvider.KEY_warncellid + " =?";
        String[] selectionArg = {warincellID};
        Cursor cursor = areaContentProvider.query(AreaContentProvider.URI_AREADATA,null, selection, selectionArg, null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                Area area = areaContentProvider.getAreaFromCursor(cursor);
                area.polygon = new Polygon(area.polygonString);
                cursor.close();
                return area;
            }
        }
        return null;
    }

    public static ArrayList<Area> getAreas(Context context, ArrayList<String> warincellIDs){
        AreaContentProvider areaContentProvider = new AreaContentProvider();
        areaContentProvider.setContext(context);
        String s = "";
        for (int i=0; i<warincellIDs.size(); i++){
            s=s+"?";
            if (i<warincellIDs.size()-1){
                s=s+",";
            }
        }
        String selection = AreaContentProvider.KEY_warncellid + " IN("+s+")";
        String[] selectionArg = warincellIDs.toArray(new String[warincellIDs.size()]);
        //Log.v("TWFG","selection   : "+selection);
        //Log.v("TWFG","selectionArg: "+s);
        Cursor cursor = areaContentProvider.query(AreaContentProvider.URI_AREADATA,null, selection, selectionArg, null);
        ArrayList<Area> areas = new ArrayList<Area>();
        int i = 0;
        if (cursor!=null){
            if (cursor.moveToFirst()){
                do {
                    Area area = areaContentProvider.getAreaFromCursor(cursor);
                    area.polygon = new Polygon(area.polygonString);
                    areas.add(area); i++;
                } while (cursor.moveToNext());
            }
        }
        return areas;
    }


    public int test(Context context){
        AreaContentProvider areaContentProvider = new AreaContentProvider();
        areaContentProvider.setContext(context);
        Cursor cursor = areaContentProvider.query(AreaContentProvider.URI_AREADATA,null, null, null, null);
        int i = 0;
        try {
            if (cursor.moveToFirst()){
                do {
                    Area area = areaContentProvider.getAreaFromCursor(cursor);
                    i++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e){
        }
        return i;
    }

}
