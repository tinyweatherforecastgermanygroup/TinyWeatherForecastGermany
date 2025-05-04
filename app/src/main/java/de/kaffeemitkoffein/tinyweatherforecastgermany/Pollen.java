/*
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021, 2022, 2023, 2024 Pawel Dube
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Pollen {

    public final static int Ambrosia = 0;
    public final static int Beifuss = 1;
    public final static int Roggen = 2;
    public final static int Esche = 3;
    public final static int Birke = 4;
    public final static int Hasel = 5;
    public final static int Erle = 6;
    public final static int Graeser = 7;

    public final static int Today = 0;
    public final static int Tomorrow = 1;
    public final static int DayAfterTomorrow = 2;

    public final static String ACTION_UPDATE_POLLEN = "ACTION_UPDATE_POLLEN";
    public final static String UPDATE_POLLEN_RESULT = "ACTION_UPDATE_RESULT";

    int[] ambrosia; int[] beifuss; int[] roggen; int[] esche; int[] birke; int[] hasel; int[] erle; int[] graeser;
    long timestamp;
    int partregion_id;
    String partregion_name;
    int region_id;
    String region_name;
    String last_update;
    long last_update_UTC = 0L;
    String next_update;
    long next_update_UTC;

    /*
    Values:
    id  value   desc                                color
    0   0       keine Belastung                     37ba29
    1   0-1     keine bis geringe Belastung         d6ff9a
    2   1       geringe Belastung                   ffff00
    3   1-2     geringe bis mittlere Belastung      ffd57f
    4   2       mittlere Belastung                  ffab00
    5   2-3     mittlere bis hohe Belastung         ff8080
    6   3       hohe Belastung                      e60000
     */

    final static int[] PollenLoadColors = {0xff37ba29,0xffd6ff9a,0xffffff00,0xffffd57f,0xffffab00,0xffff8080,0xffe60000};

    public Pollen(){
        ambrosia = new int[6];
        beifuss = new int[6];
        roggen = new int[6];
        esche = new int[6];
        birke = new int[6];
        hasel = new int[6];
        erle = new int[6];
        graeser = new int[6];
    }

    public Pollen(Pollen source){
        this.ambrosia = source.ambrosia;
        this.beifuss = source.beifuss;
        this.roggen = source.roggen;
        this.esche = source.esche;
        this.birke = source.birke;
        this.hasel = source.hasel;
        this.erle = source.erle;
        this.graeser = source.graeser;
        this.timestamp = source.timestamp;
        this.partregion_id = source.partregion_id;
        this.partregion_name = source.partregion_name;
        this.region_id = source.region_id;
        this.region_name = source.region_name;
        this.last_update = source.last_update;
        this.last_update_UTC = source.last_update_UTC;
        this.next_update = source.next_update;
        this.next_update_UTC = source.next_update_UTC;
    }

    public static int[] getMinMax(String source){
        int[] result = new int[2];
        if (source.contains("-")){
            String firstValue = source.substring(0,source.indexOf("-"));
            String secondValue = source.substring(source.indexOf("-")+1,source.length());
            result[0] = Integer.parseInt(firstValue);
            result[1] = Integer.parseInt(secondValue);
        } else {
            result[0] = Integer.parseInt(source);
            result[1] = Integer.parseInt(source);
        }
        return result;
    }

    public int[] getValueArray(int type){
        switch (type){
            case Ambrosia: return this.ambrosia;
            case Beifuss: return this.beifuss;
            case Roggen: return this.roggen;
            case Esche: return this.esche;
            case Birke: return this.birke;
            case Hasel: return this.hasel;
            case Erle: return this.erle;
            case Graeser: return this.graeser;
        }
        return new int[]{-1,-1,-1,-1,-1,-1};
    }

    /**
     * Returns the pollen load (0-6) for the pollen type on a given day.
     * @param context the calling context
     * @param type pollen type, e.g. Ambrosia. Valid values are 0-7.
     * @param timeParam 0 = today, 1 = tomorrow, 2 = day after tomorrow.
     * @return the pollen load (0-6). Will return -1 when the load cannot be determined, either
     * because there is no data and/or the parameters are invalid.
     */

    public int getPollenLoad(Context context, int type, int timeParam) {
        int[] values = getValueArray(type);
        /* Since updates occur about 11:00 am, it might happen that "tomorrow" needs to be shifted for
         * today, shortening the forecast period from 3 days to lower values.
         */
        int time = timeParam + getDayShift(context);
        if ((time<Today) || (time>DayAfterTomorrow)){
            return -1;
        }
        int min = values[0];
        int max = values[1];
        if (time == Tomorrow) {
            min = values[2];
            max = values[3];
        }
        if (time == DayAfterTomorrow) {
            min = values[4];
            max = values[5];
        }
        if ((min == 0) && (max == 0)) {
            return 0;
        }
        if ((min == 0) && (max == 1)) {
            return 1;
        }
        if ((min == 1) && (max == 1)) {
            return 2;
        }
        if ((min == 1) && (max == 2)) {
            return 3;
        }
        if ((min == 2) && (max == 2)) {
            return 4;
        }
        if ((min == 2) && (max == 3)) {
            return 5;
        }
        if ((min == 3) && (max == 3)) {
            return 6;
        }
        return -1;
    }

    public static void WritePollenToDatabase(Context context, ArrayList<Pollen> pollenArrayList){
        ContentResolver contentResolver = context.getContentResolver();
        try {
            int i = contentResolver.delete(WeatherContentManager.POLLEN_URI_ALL,null,null);
        } catch (Exception e){
            PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"Deleting pollen data failed: "+e.getMessage());
        }
        for (int i=0; i<pollenArrayList.size(); i++){
            Pollen pollen = pollenArrayList.get(i);
            ContentValues contentValues = WeatherContentManager.getContentValuesFromPollen(pollen);
            contentResolver.insert(WeatherContentManager.POLLEN_URI_ALL,contentValues);
        }
    }

    public static Pollen GetPollenData(Context context, PollenArea pollenArea){
        if (pollenArea!=null){
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(WeatherContentManager.POLLEN_URI_ALL, null,null,null,null);
            int counter = 0;
            if (cursor.moveToFirst()){
               do {
                   Pollen pollen = WeatherContentManager.getPollenFromCursor(cursor);
                   if (pollen.partregion_id==-1){
                        if (pollen.region_id == pollenArea.region_id){
                            cursor.close();
                            return pollen;
                        }
                    } else {
                        if ((pollen.partregion_id == pollenArea.partregion_id) && (pollen.region_id==pollenArea.region_id)){
                            cursor.close();
                            return pollen;
                        }
                    }
                    counter++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return null;
    }

    public static ArrayList<Pollen> GetPollenData(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(WeatherContentManager.POLLEN_URI_ALL, null,null,null,null);
        ArrayList<Pollen> pollenArrayList = new ArrayList<Pollen>();
        if (cursor.moveToFirst()){
            do {
                Pollen pollen = WeatherContentManager.getPollenFromCursor(cursor);
                pollenArrayList.add(pollen);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return pollenArrayList;
    }

    public static Pollen GetPollenData(ArrayList<Pollen> pollens, PollenArea pollenArea){
        for (int i=0; i<pollens.size(); i++) {
            Pollen pollen = pollens.get(i);
            if (pollen.partregion_id == -1) {
                if (pollen.region_id == pollenArea.region_id) {
                    return pollen;
                }
            } else {
                if (pollen.partregion_id == pollenArea.partregion_id) {
                    return pollen;
                }
            }
        }
        return null;
    }

    public static long getNextPollenUpdateTime(Context context){
        Pollen pollen = Pollen.GetPollenData(context,WeatherSettings.getPollenRegion(context));
        if (pollen!=null){
            return pollen.next_update_UTC;
        }
        else return 0;
    }

    public static long getLastPollenUpdateTime(Context context){
        Pollen pollen = Pollen.GetPollenData(context,WeatherSettings.getPollenRegion(context));
        if (pollen!=null){
            return pollen.last_update_UTC;
        }
        else return 0;
    }

    /**
     * Determines day shift for some marginal conditions where the number of pollen forecast days is
     * below three.
     *
     * @param context
     * @return day shift in number of days, always zero (no shift necessary) or positive.
     */

    public int getDayShift(Context context) {
        long pollday = WeatherLayer.getMidnightTime(this.last_update_UTC);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long nowday  = WeatherLayer.getMidnightTime(Calendar.getInstance().getTimeInMillis());
        float shiftInDays = (nowday-pollday)/(1000*60*60*24f);
        // PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"Pollen data polled on day (midnight time): "+sdf.format(new Date(pollday)));
        // PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"Calculated current day    (midnight time): "+sdf.format(new Date(nowday)));
        // PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"Calculated pollen forecast shift in days : "+shiftInDays);
        return Math.round(shiftInDays);
    }

}
