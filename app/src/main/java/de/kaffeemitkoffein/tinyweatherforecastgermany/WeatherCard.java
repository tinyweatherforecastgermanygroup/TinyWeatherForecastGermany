/**
 * This file is part of Tiny24hWeatherForecastGermany.
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WeatherCard {

    public String fdat;             // e.g. FDAT84
    public String ortscode;         // e.g. JJPD
    public String zeitstempel;      // e.g. 150500
    public String klimagebiet;      // e.g. Niederlausitz
    public String ausgegeben_am;    // e.g. Sonntag, den 15.03.2020 um 07:15 Uhr
    public String ausgegeben_von;   // e.g. Deutscher Wetterdienst
    public String[] uhrzeit;        // e.g. 10
    public String[] bewoelkung;
    public String[] bewoelkung_max;
    public String[] bewoelkung_min;
    public String[] niederschlag;
    public String[] niederschlag_max;
    public String[] niederschlag_min;
    public String[] lufttemperatur;
    public String[] lufttemperatur_max;
    public String[] lufttemperatur_min;
    public String[] wind;
    public String[] boeen;
    public long polling_time;

    public WeatherCard(){
        initEmptyClassValues();
    }

    public WeatherCard(ArrayList<String> weatherText){
        /**
         * Known abbrevations:
         * R = Regen
         * SR= Schneeregen
         * S = Schnee
         */
        if (weatherText==null){
            initEmptyClassValues();
        } else
        if (weatherText.size()<26){
            initEmptyClassValues();
        } else {
            String s;
            // get filename elements
            s = weatherText.get(0);
            this.fdat = s.substring(0,5);
            Log.v("WEATHERCARD","fdat:"+this.fdat+"<");
            this.ortscode = s.substring(7,11);
            Log.v("WEATHERCARD","fdat:"+this.ortscode+"<");
            this.zeitstempel = s.substring(12,18);
            Log.v("WEATHERCARD","fdat:"+this.zeitstempel+"<");
            // get weather area in readable text
            s = weatherText.get(3);
            this.klimagebiet = truncateSpaces( s.substring(15) );
            Log.v("WEATHERCARD","Klimagebiet:"+this.klimagebiet+"<");
            if (this.klimagebiet.contains("Hoehen")){
                int hp = this.klimagebiet.indexOf("Hoehen");
                this.klimagebiet = this.klimagebiet.substring(0,hp-1);
                this.klimagebiet = this.klimagebiet.trim();
            }
            s = weatherText.get(4);
            this.ausgegeben_am = truncateSpaces( s.substring(15) );
            Log.v("WEATHERCARD","am          :"+this.ausgegeben_am+"<");
            s = weatherText.get(5);
            this.ausgegeben_von = truncateSpaces( s.substring(15) );
            Log.v("WEATHERCARD","von         :"+this.ausgegeben_von+"<");
            s = weatherText.get(7);
            Log.v("WEATHERCARD","Timeline String:"+s);
            this.uhrzeit = getValuesFromLine(15,weatherText.get(7),2);

            this.bewoelkung_max = getValuesFromLine(16,weatherText.get(9),1);
            this.bewoelkung     = getValuesFromLine(16,weatherText.get(10),1);
            this.bewoelkung_min = getValuesFromLine(16,weatherText.get(11),1);

            this.niederschlag_max = getValuesFromLine(14,weatherText.get(13),3);
            this.niederschlag     = getValuesFromLine(14,weatherText.get(14),3);
            this.niederschlag_min = getValuesFromLine(14,weatherText.get(15),3);

            this.lufttemperatur_max = getValuesFromLine(14,weatherText.get(18),3);
            this.lufttemperatur     = getValuesFromLine(14,weatherText.get(19),3);

            Log.v("WEATHERCARD","Temp min line:"+weatherText.get(20));
            this.lufttemperatur_min = getValuesFromLine(14,weatherText.get(20),3);
            for (int i=0; i<9; i++){
                Log.v("WEATHERCARD", "Value "+i+"=>"+this.lufttemperatur_min[i]);
            }
            this.wind  = getValuesFromLine(13,weatherText.get(22),4);
            this.boeen = getValuesFromLine(14,weatherText.get(23),3);
        }
    }

    public WeatherCard(Context context, String filename){
        File file = new File(context.getExternalFilesDir(null)+"/"+filename);
        FileInputStream fileInputStream;
        String line;
        ArrayList<String> file_content = new ArrayList<String>();
        try {
            fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            line=bufferedReader.readLine();
            while (line != null) {
                file_content.add(line);
                try {
                    line = bufferedReader.readLine();
                } catch (Exception IOException) {
                    line = null;
                }
            }
        } catch (Exception IOException){
            initEmptyClassValues();
        }
   }

    private boolean isValidDataChar(char c){
        String s = String.valueOf(c);
        if (s.equals("-") || s.equals("0") || s.equals("1") ||s.equals("2") ||s.equals("3") ||s.equals("4") ||s.equals("5") ||s.equals("6") ||s.equals("7") ||s.equals("8") ||s.equals("9") ||
                s.equals("S") ||s.equals("W") ||s.equals("O") ||s.equals("N") || s.equals("C")|| s.equals("R")) {
            return true;
        }
        return false;
    }

    private boolean isNumber(char c){
        String s = String.valueOf(c);
        if (s.equals("-") || s.equals("0") || s.equals("1") ||s.equals("2") ||s.equals("3") ||s.equals("4") ||s.equals("5") ||s.equals("6") ||s.equals("7") ||s.equals("8") ||s.equals("9")){
            return true;
        }
        return false;
    }

    private String truncateSpaces(String s){
        return s.trim();
    }

    private String[] getValuesFromLine(int start, String line, int value_width){
        String[] results = new String[9];
        for (int i=0; i<9; i++){
            results[i]=line.substring(start + i*7, start + i*7 + value_width).trim();
        }
        return results;
    }

    private void initEmptyClassValues(){
        fdat = "";
        ortscode = "";
        zeitstempel = "";
        klimagebiet = "";
        ausgegeben_am = "";
        ausgegeben_von = "";
        uhrzeit = new String[9];
        bewoelkung = new String[9];
        bewoelkung_max = new String[9];
        bewoelkung_min = new String[9];
        niederschlag = new String[9];
        niederschlag_max = new String[9];
        niederschlag_min = new String[9];
        lufttemperatur = new String[9];
        lufttemperatur_max = new String[9];
        lufttemperatur_min = new String[9];
        wind = new String[9];
        boeen = new String[9];
    }

    public String getName(){
        return this.klimagebiet;
    }

    /**
     * Returns current temperature.
     * @return
     */

    public double getCurrentTemp(){
        int i = Integer.parseInt(this.lufttemperatur[0]);
        double d = i;
        return d;
    }

    public String getNumbers(String s){
        // elimate non-number chars from the beginning of s
        while (!isNumber(s.charAt(0))){
            s = s.substring(1);
        }
        return s;
    }

    public int[] getIntArray(String[] s){
        // elimate non-number chars from the beginning of s
        for (int i=0; i<s.length; i++){
            while (!isNumber(s[i].charAt(0))){
                s[i] = s[i].substring(1);
            }
        }
        // convert to int
        int[] result = new int[9];
        for (int i=0; i<s.length; i++){
            result[i] = Integer.parseInt(s[i]);
        }
        return result;
    }

    public int[] getIntArray(String[] s, int start, int stop){
        // elimate non-number chars from the beginning of s
        for (int i=0; i<s.length; i++){
            while (!isNumber(s[i].charAt(0))){
                s[i] = s[i].substring(1);
            }
        }
        // convert to int
        int[] result = new int[9];
        for (int i=start; (i<s.length) && (i<=stop); i++){
            result[i] = Integer.parseInt(s[i]);
        }
        return result;
    }

    /**
     * Determines the position in the arrays which is the last forecast for the current day.
     * @return
     */

    public int getEndOfTodayPos(){
        int[] times = getIntArray(this.uhrzeit);
        int time = times[0];
        int i = 1;
        while ((i<9) && (times[i]>time)){
            i++;
        }
        // go one back so i will be the last record of the current day
        i--;
        return i;
    }

    /**
     * Returns highest temperture of the next 24h.
     * @return
     */

    public int todaysHigh(){
        int[] tempmax = getIntArray(this.lufttemperatur_max);
        int result = tempmax[0];
        for (int i=1; i<getEndOfTodayPos(); i++){
            if (tempmax[i]>result){
                result = tempmax[i];
            }
        }
        return result;
    }

    /**
     * Returns the lowest temperture of the next 24h.
     * @return
     */

    public int todaysLow(){
        int[] tempmin = getIntArray(this.lufttemperatur_min);
        int result = tempmin[0];
        for (int i=1; i<getEndOfTodayPos(); i++){
            if (tempmin[i]<result){
                result = tempmin[i];
            }
        }
        return result;
    }

    public int tomorrowLow(){
        int[] tempmin = getIntArray(this.lufttemperatur_min);
        int result = tempmin[getEndOfTodayPos()];
        for (int i=getEndOfTodayPos(); i<9; i++){
            if (tempmin[i]<result){
                result = tempmin[i];
            }
        }
        return result;
    }

    public double tomorrowHigh(){
        int[] tempmax = getIntArray(this.lufttemperatur_max);
        int result = tempmax[getEndOfTodayPos()];
        for (int i=getEndOfTodayPos(); i<9; i++){
            if (tempmax[i]>result){
                result = tempmax[i];
            }
        }
        double d = result;
        return d;
    }

    public String getWindDir(String s){
        Character c = s.charAt(1);
        String result = "";
        if (Character.isDigit(s.charAt(1))) {
            // one char wind code
            result = String.valueOf(s.charAt(0));
        } else {
            // two char wind code
            result = s.substring(0,1);
        }
        return result;
    }

    public double windDirection(int pos){
        String windstring = getWindDir(wind[pos]);
        Log.v("ID:","WIND IS: "+windstring);
        int i = 0;
        switch (windstring){
            case "N": i=0; break;
            case "NO": i=45; break;
            case "O": i=90; break;
            case "SO": i=135; break;
            case "S": i=180; break;
            case "SW": i=225; break;
            case "W": i=270; break;
            case "NW": i=315; break;
        }
        double d = i;
        return d;
    }

    public String getWindSpeed(String s){
        Character c = s.charAt(1);
        String result = "";
        if (Character.isDigit(s.charAt(1))) {
            // one char wind code
            result = s.substring(1);
        } else {
            // two char wind code
            result = s.substring(2);
        }
        return result;
    }

    public double windSpeed(int pos){
        String s = getWindSpeed(wind[pos]);
        int i = Integer.parseInt(s);
        double d = i;
        return d;
    }

    public String getCurrentFlurries(){
        return boeen[0];
    }

    public String getCurrentWind(){
        return wind[0];
    }

    public String getTodaysFlurries(){
        int[] flurries = getIntArray(this.boeen);
        int result = flurries[0];
        for (int i=1; i<getEndOfTodayPos(); i++){
            if (flurries[i]>result){
                result = flurries[i];
            }
        }
        return String.valueOf(result);
    }

    public String getTodaysWind(){
        int[] wind = getIntArray(this.wind);
        int result = 0;
        for (int i=1; i<getEndOfTodayPos(); i++){
            result = result + wind[i];
        }
        if (getEndOfTodayPos()<1){
            return String.valueOf(wind[0]);
        } else {
            result = result/(getEndOfTodayPos()-1);
            return String.valueOf(result);
        }
    }

    public Bitmap getArrowBitmap(Context context, float degrees){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.arrow);
        if (bitmap != null){
            Matrix m = new Matrix();
            m.postRotate(degrees);
            return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,false);
        } else {
            return null;
        }
    }

    public Bitmap getCurrentArrow(Context context){
        int degrees = 0;
        // no wind direction given, no wind.
        if (wind[0].length()<2) {
            return null;
        }
        String first_char = String.valueOf(wind[0].charAt(0));
        String first_two_chars = String.valueOf(wind[0].charAt(0)+wind[0].charAt(1));
        if (first_char.equals("N")){
            degrees = 0;
        }
        if (first_char.equals("O")){
            degrees = 90;
        }
        if (first_char.equals("S")){
            degrees = 180;
        }
        if (first_char.equals("W")){
            degrees = 270;
        }
        if (first_two_chars.equals("NO")){
            degrees = 45;
        }
        if (first_two_chars.equals("SO")){
            degrees = 135;
        }
        if (first_two_chars.equals("NO")){
            degrees = 225;
        }
        if (first_two_chars.equals("NO")){
            degrees = 315;
        }
        return getArrowBitmap(context,degrees);
    }

    private String arrayToString(String[] strarray, int start, int stop){
        String result = "";
        for (int i=start; i<=stop; i++){
            result = result + strarray[i];
            if (i<stop){
                result = result + "|";
            }
        }
        return result;
    }

    public String toString(int start, int stop){
        String seperator =",";
        String result = fdat + seperator +
                        ortscode + seperator +
                        zeitstempel + seperator +
                        klimagebiet + seperator +
                        ausgegeben_am + seperator +
                        ausgegeben_von + seperator +
                        uhrzeit;
        result = result + "|Bewölkung:"+arrayToString(bewoelkung,start,stop);
        result = result + "|Bewölkung_min:"+arrayToString(bewoelkung_min,start,stop);
        result = result + "|Bewölkung_max:"+arrayToString(bewoelkung_max,start,stop);
        result = result + "|Niederschlag:"+arrayToString(niederschlag,start,stop);
        result = result + "|Niederschlag_min:"+arrayToString(niederschlag_min,start,stop);
        result = result + "|Niederschlag_max:"+arrayToString(niederschlag_max,start,stop);
        result = result + "|Temperatur:"+arrayToString(lufttemperatur,start,stop);
        result = result + "|Temperatur_min:"+arrayToString(lufttemperatur_min,start,stop);
        result = result + "|Temperatur_max:"+arrayToString(lufttemperatur_max,start,stop);
        result = result + "|Wind:"+arrayToString(wind,start,stop);
        result = result + "|Boeen:"+arrayToString(boeen,start,stop);
        result = result + "|Zeit:"+String.valueOf(polling_time);
        return result;
    }

    public String toString(){
        return toString(0,11);
    }

}
