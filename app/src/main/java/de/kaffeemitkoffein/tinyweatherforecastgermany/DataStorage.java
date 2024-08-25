package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class DataStorage {
    public final static int DATASTORAGE_STATION = 0;
    public final static int DATASTORAGE_TEST = 2;
    public final static int DATASTORAGE_POLLENREGION_ID = 10;
    public final static int DATASTORAGE_POLLENPARTREGION_ID = 11;
    public final static int DATASTORAGE_POLLENREGION_DESCRIPTION = 12;
    public final static int DATASTORAGE_LASTGPSFIX = 13;
    public final static int DATASTORAGE_NOTIFICATION_IDENTIFIER = 14;
    public final static int DATASTORAGE_LAST_NOTIFICATIONS_UPDATE_TIME = 15;
    public final static int DATASTORAGE_NC_CHANNEL_DETAIL = 15;

    public final static int DATASTORAGE_POLLENREGION_ID_DEFAULT = -1;
    public final static int DATASTORAGE_POLLENPARTREGION_ID_DEFAULT = -1;
    public final static String DATASTORAGE_POLLENREGION_DESCRIPTION_DEFAULT = "";
    public final static long DATASTORAGE_LASTGPSFIX_DEFAULT = 0l;
    public final static int DATASTORAGE_NOTIFICATION_IDENTIFIER_DEFAULT = -2147483640;
    public final static long DATASTORAGE_LAST_NOTIFICATIONS_UPDATE_TIME_DEFAULT =0l;
    public final static long DATASTORAGE_NC_CHANNEL_DETAIL_DEFAULT = 0l;

    public static ArrayList<DataPackage> readAllPackages(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(WeatherContentManager.DATA_URI_ALL,null,null,null,null);
        ArrayList<DataPackage> dataPackages = new ArrayList<DataPackage>();
        if ((cursor!=null) && (cursor.moveToFirst())){
            do {
                DataPackage dataPackage = WeatherContentManager.getDataFromCursor(cursor);
                dataPackages.add(dataPackage);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return dataPackages;

    }

    public static void printPackages(Context context, ArrayList<DataPackage> dataPackages){
        PrivateLog.log(context,PrivateLog.DATA,PrivateLog.INFO,"----------------------");
        for (int i=0; i<dataPackages.size(); i++){
            PrivateLog.log(context,PrivateLog.DATA,PrivateLog.INFO,dataPackages.get(i).toString());
        }
        PrivateLog.log(context,PrivateLog.DATA,PrivateLog.INFO,"----------------------");
    }

    public static DataPackage readDataPackage(Context context, int id){
        DataPackage dataPackage = null;
        ContentResolver contentResolver = context.getContentResolver();
        String selection = WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_id+ " LIKE ?";
        String[] selectionArgs = new String[]{Integer.toString(id)};
        Cursor cursor = contentResolver.query(WeatherContentManager.DATA_URI_ALL,null,selection,selectionArgs,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                dataPackage = WeatherContentManager.getDataFromCursor(cursor);
                //PrivateLog.log(context,PrivateLog.DATASTORAGE,PrivateLog.ERR,"Got data package: "+dataPackage.valueLong+" id="+dataPackage.id);
            } else {
                //PrivateLog.log(context,PrivateLog.DATASTORAGE,PrivateLog.ERR,"Entry not found: "+DATASTORAGE_STATION);
            }
            cursor.close();
        } else {
            //PrivateLog.log(context,PrivateLog.DATASTORAGE,PrivateLog.ERR,"Entry not found, cursor is null: "+DATASTORAGE_STATION);
        }
        if (dataPackage==null){
            //PrivateLog.log(context,PrivateLog.DATASTORAGE,PrivateLog.ERR,"+-> PACKAGE NULL "+dataPackage);
        }
        return dataPackage;
    }

    private static void putContentValue(Context context, int id, ContentValues contentValues){
        ContentResolver contentResolver = context.getContentResolver();
        String selection = WeatherContentProvider.WeatherDatabaseHelper.KEY_DATA_id+ " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        contentResolver.delete(WeatherContentManager.DATA_URI_ALL,selection,selectionArgs);
        Uri rows = contentResolver.insert(WeatherContentManager.DATA_URI_ALL,contentValues);
        PrivateLog.log(context,PrivateLog.DATASTORAGE,PrivateLog.INFO,"Station update: rows updated "+rows.toString());
    }

    public static void clear(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        int delCount = contentResolver.delete(WeatherContentManager.DATA_URI_ALL,null,null);
        PrivateLog.log(context,PrivateLog.DATASTORAGE,PrivateLog.INFO,"Datastore cleared. Removed entries: "+delCount);
    }

    public static void setBlob(Context context, int id, byte[] value){
        ContentValues contentValues = WeatherContentManager.getContentValuesFromData_Blob(id,value);
        putContentValue(context,id,contentValues);
    }

    public static byte[] getBlob(Context context, int id, byte[] defaultValue){
        DataPackage dataPackage = readDataPackage(context,id);
        if (dataPackage==null){
            setBlob(context,id,defaultValue);
            return defaultValue;
        }
        return dataPackage.valueBlob;
    }

    public static void setFloat(Context context, int id, float value){
        ContentValues contentValues = WeatherContentManager.getContentValuesFromData_Float(id,value);
        putContentValue(context,id,contentValues);
    }

    public static float getFloat(Context context, int id, float defaultValue){
        DataPackage dataPackage = readDataPackage(context,id);
        if (dataPackage==null){
            setFloat(context,id,defaultValue);
            return defaultValue;
        }
        return dataPackage.valueFloat;
    }

    public static void setInt(Context context, int id, int value){
        ContentValues contentValues = WeatherContentManager.getContentValuesFromData_Int(id,value);
        putContentValue(context,id,contentValues);
    }

    public static int getInt(Context context, int id, int defaultValue){
        DataPackage dataPackage = readDataPackage(context,id);
        if (dataPackage==null){
            setInt(context,id,defaultValue);
            return defaultValue;
        }
        return dataPackage.valueInt;
    }

    public static void setLong(Context context, int id, long value){
        ContentValues contentValues = WeatherContentManager.getContentValuesFromData_Long(id,value);
        putContentValue(context,id,contentValues);
    }

    public static long getLong(Context context, int id, long defaultValue){
        DataPackage dataPackage = readDataPackage(context,id);
        if (dataPackage==null){
            setLong(context,id,defaultValue);
            return defaultValue;
        }
        return dataPackage.valueLong;
    }

    public static void setString(Context context, int id, String value){
        ContentValues contentValues = WeatherContentManager.getContentValuesFromData_String(id,value);
        putContentValue(context,id,contentValues);
    }

    public static String getString(Context context, int id, String defaultValue){
        DataPackage dataPackage = readDataPackage(context,id);
        if (dataPackage==null){
            setString(context,id,defaultValue);
            return defaultValue;
        }
        return dataPackage.valueString;
    }

    public static void setBoolean(Context context, int id, boolean value){
        ContentValues contentValues = WeatherContentManager.getContentValuesFromData_Boolean(id,value);
        putContentValue(context,id,contentValues);
    }

    public boolean getBoolean(Context context, int id, boolean defaultValue){
        DataPackage dataPackage = readDataPackage(context,id);
        if (dataPackage==null){
            setBoolean(context,id,defaultValue);
            return defaultValue;
        }
        return dataPackage.valueLong == 0;
    }

    public static void setStation(Context context, Weather.WeatherLocation weatherLocation){
        String stationString = weatherLocation.serializeToString();
        setString(context,DATASTORAGE_STATION,stationString);
    }

    public static Weather.WeatherLocation getSetStationLocation(Context context){
        String stationString = getString(context,DATASTORAGE_STATION,WeatherSettings.getDefaultWeatherLocation().serializeToString());
        Weather.WeatherLocation weatherLocation;
        if (stationString!=null){
            weatherLocation = new Weather.WeatherLocation(stationString);
        } else {
            weatherLocation = WeatherSettings.getDefaultWeatherLocation();
            DataStorage.setStation(context,weatherLocation);
            PrivateLog.log(context,PrivateLog.DATA,PrivateLog.INFO,"No entry for station found, setting the default station to "+weatherLocation.getDescription(context));
        }
        return weatherLocation;

    }

    public static class Updates{

        public static long getMillis(int interval) {
            switch (interval) {
                case Updates.Intervals.MIN15:
                    return 1000 * 60 * 15;
                case Updates.Intervals.MIN30:
                    return 1000 * 60 * 30;
                case Updates.Intervals.HOUR1:
                    return 1000 * 60 * 60;
                case Updates.Intervals.HOUR2:
                    return 1000 * 60 * 60 * 2;
                case Updates.Intervals.HOUR3:
                    return 1000 * 60 * 60 * 3;
                case Updates.Intervals.HOUR6:
                    return 1000 * 60 * 60 * 6;
                case Updates.Intervals.HOUR12:
                    return 1000 * 60 * 60 * 12;
                case Updates.Intervals.HOUR18:
                    return 1000 * 60 * 60 * 18;
                case Updates.Intervals.HOUR24:
                    return 1000 * 60 * 60 * 24;
                default:
                    return 0;
            }
        }

        public final class Intervals{

            public static final int NEVER = -1;
            public static final int MIN15 = 1;
            public static final int MIN30 = 2;
            public static final int HOUR1 = 10;
            public static final int HOUR2 = 11;
            public static final int HOUR3 = 12;
            public static final int HOUR6 = 13;
            public static final int HOUR12 = 14;
            public static final int HOUR18 = 15;
            public static final int HOUR24 = 16;
        }

        public final class Category{
            public static final int WEATHER = 0;
            public static final int WARNINGS = 1;
            public static final int TEXTS = 2;
            public static final int POLLEN = 3;
            public static final int LAYERS = 4;
        }

        public static final int CategoryItemsCount = 5;

        public static final int DATASTORAGE_UPDATE_WEATHER_LASTUPDATE   = 102;
        public static final int DATASTORAGE_UPDATE_WARNINGS_LASTUPDATE  = 105;
        public static final int DATASTORAGE_UPDATE_TEXTS_LASTUPDATE     = 108;
        public static final int DATASTORAGE_UPDATE_POLLEN_LASTUPDATE    = 111;
        public static final int DATASTORAGE_UPDATE_LAYERS_LASTUPDATE    = 114;


        private static int getLastUpdatePreference(int category){
            int preference  = DATASTORAGE_UPDATE_WEATHER_LASTUPDATE;
            switch (category){
                case Category.WARNINGS: preference  = DATASTORAGE_UPDATE_WARNINGS_LASTUPDATE; break;
                case Category.TEXTS: preference  = DATASTORAGE_UPDATE_TEXTS_LASTUPDATE; break;
                case Category.POLLEN: preference  = DATASTORAGE_UPDATE_POLLEN_LASTUPDATE; break;
                case Category.LAYERS: preference  = DATASTORAGE_UPDATE_LAYERS_LASTUPDATE; break;
                default: preference  = DATASTORAGE_UPDATE_WEATHER_LASTUPDATE; break;
            }
            return preference;
        }


        public static long getLastUpdate(Context context, int category){
            return (long) DataStorage.getLong(context,getLastUpdatePreference(category),0l);
        }

        public static void setLastUpdate(Context context, int category, long updateTime){
            DataStorage.setLong(context,getLastUpdatePreference(category),updateTime);
        }

        public static boolean isSyncDue(Context context, int category){
            long lastUpdateTime = getLastUpdate(context,category);
            if (Calendar.getInstance().getTimeInMillis() > lastUpdateTime + WeatherSettings.Updates.getIntervalMillis(context,category)){
                return true;
            }
            return false;
        }

        private static long getIntervalLong(Context context, int category){
            if (WeatherSettings.Updates.isSyncEnabled(context,category)){
                return WeatherSettings.Updates.getIntervalMillis(context,category);
            }
            return Intervals.NEVER;
        }

        public static boolean isSyncNecessary(Context context){
            return ((isSyncDue(context, Category.WEATHER)) ||
                    (isSyncDue(context,Category.WARNINGS)) ||
                    (isSyncDue(context,Category.TEXTS)) ||
                    (isSyncDue(context,Category.POLLEN)) ||
                    (isSyncDue(context,Category.LAYERS)) ||
                    (!Weather.hasCurrentWeatherInfo(context)));
        }

    }

}
