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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

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

    /**
     * Constructor for an empty class.
     */

    public WeatherCard(){
        initEmptyClassValues();
    }

    public WeatherCard(String fdat,
            String ortscode,
            String zeitstempel,
             String klimagebiet,
             String ausgegeben_am,
            String ausgegeben_von,
            String[] uhrzeit,
             String[] bewoelkung,
            String[] bewoelkung_max,
            String[] bewoelkung_min,
            String[] niederschlag,
            String[] niederschlag_max,
            String[] niederschlag_min,
            String[] lufttemperatur,
             String[] lufttemperatur_max,
            String[] lufttemperatur_min,
            String[] wind,
            String[] boeen,
            long polling_time){
        this.fdat = fdat;
        this.ortscode = ortscode;
        this.zeitstempel = zeitstempel;
        this.klimagebiet = klimagebiet;
        this.ausgegeben_am = ausgegeben_am;
        this.ausgegeben_von = ausgegeben_von;
        this.uhrzeit = uhrzeit;
        this.bewoelkung=bewoelkung;
        this.bewoelkung_max=bewoelkung_max;
        this.bewoelkung_min=bewoelkung_min;
        this.niederschlag=niederschlag;
        this.niederschlag_max=niederschlag_max;
        this.niederschlag_min=niederschlag_min;
        this.lufttemperatur=lufttemperatur;
        this.lufttemperatur_max=lufttemperatur_max;
        this.lufttemperatur_min=lufttemperatur_min;
        this.wind=wind;
        this.boeen=boeen;
        this.polling_time=polling_time;
    }

    public WeatherCard copy(){
        return new WeatherCard(
        fdat, ortscode, zeitstempel, klimagebiet, ausgegeben_am, ausgegeben_von, uhrzeit, bewoelkung, bewoelkung_max, bewoelkung_min, niederschlag, niederschlag_max, niederschlag_min, lufttemperatur, lufttemperatur_max, lufttemperatur_min, wind, boeen, polling_time);
    }

    /**
     * Constructor for a WeatherCard from a arraylist of strings that represents the weather data file that
     * has been received from the DWD open data source.
     *
     * Errors will likely occur if the file format ever changes. In this case, an IlleagalArgumentException is
     * thrown that has details about what went wrong.
     *
     * @param weatherText
     * @throws IllegalArgumentException
     *
     */

    public WeatherCard(ArrayList<String> weatherText) throws IllegalArgumentException{
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
            throw new IllegalArgumentException("wrong number of lines in API file");
        } else {
            String s;
            // **********************************************************
            // line #0: get headers: fdat, ortscode, zeitstempel
            // **********************************************************
            s = weatherText.get(0);
            try{
                this.fdat = s.substring(0,6);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException ("fdat has wrong format:"+s);
            }
            try {
                this.ortscode = s.substring(7,11);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("ortscode has wrong fromat:"+s);
            }
            try {
                this.zeitstempel = s.substring(12,18);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("zeitstempel has wrong format:"+s);
            }
            // **********************************************************
            // line #3: get weather area in readable text, truncate all
            //          unnecessary data.
            // **********************************************************
            s = weatherText.get(3);
            try {
                this.klimagebiet = truncateSpaces( s.substring(15) );
                if (this.klimagebiet.contains("Hoehen")){
                    int hp = this.klimagebiet.indexOf("Hoehen");
                    this.klimagebiet = this.klimagebiet.substring(0,hp-1);
                    this.klimagebiet = this.klimagebiet.trim();
                }
             } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("klimagebiet has wrong format:"+s);
            }
            // **********************************************************
            // line #4: get time of the weather forecast
            // **********************************************************
            s = weatherText.get(4);
            try {
                this.ausgegeben_am = truncateSpaces( s.substring(15) );
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("ausgegeben_am has wrong format:"+s);
            }
            // **********************************************************
            // line #5: get source of the weather forecast (usually DWD)
            // **********************************************************
            s = weatherText.get(5);
            try {
                this.ausgegeben_von = truncateSpaces( s.substring(15) );
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("ausgegeben_von has wrong format:"+s);
            }
            // **********************************************************
            // line #7: get the timestamps array of weather data
            // **********************************************************
            s = weatherText.get(7);
            try {
                this.uhrzeit = getValuesFromLine(15,s,2);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("uhrzeit array has wrong format:"+s);
            }
            // **********************************************************
            // line #9,10,11: get the clouds data
            // **********************************************************
            try {
                this.bewoelkung_max = getValuesFromLine(16,weatherText.get(9),1);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("bewolekung_max has wrong format:"+weatherText.get(9));
            }
            try {
                this.bewoelkung     = getValuesFromLine(16,weatherText.get(10),1);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("bewolekung has wrong format:"+weatherText.get(10));
            }
            try {
                this.bewoelkung_min = getValuesFromLine(16,weatherText.get(11),1);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("bewolekung_min has wrong format:"+weatherText.get(11));
            }
            // **********************************************************
            // line #13,14,15: get the precipitation data
            // **********************************************************
            try {
                this.niederschlag_max = getValuesFromLine(14,weatherText.get(13),3);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("niederschlag_max has wrong format:"+weatherText.get(13));
            }
            try {
                this.niederschlag     = getValuesFromLine(14,weatherText.get(14),3);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("niederschlag has wrong format:"+weatherText.get(14));
            }
            try {
                this.niederschlag_min = getValuesFromLine(14,weatherText.get(15),3);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("niederschlag_min has wrong format:"+weatherText.get(15));
            }
            // **********************************************************
            // line #18,19,20: get the temperature data
            // **********************************************************
            try {
                this.lufttemperatur_max = getValuesFromLine(14,weatherText.get(18),3);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("lufttemperatur_max has wrong format:"+weatherText.get(18));
            }
            try {
                this.lufttemperatur     = getValuesFromLine(14,weatherText.get(19),3);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("lufttemperatur has wrong format:"+weatherText.get(19));
            }
            try {
                this.lufttemperatur_min = getValuesFromLine(14,weatherText.get(20),3);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("lufttemperatur_min has wrong format:"+weatherText.get(20));
            }
            // **********************************************************
            // line #22: get wind data
            // **********************************************************
            try {
                this.wind  = getValuesFromLine(12,weatherText.get(22),5);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("wind has wrong format:"+weatherText.get(22));
            }
            try {
                this.boeen = getValuesFromLine(14,weatherText.get(23),3);
            } catch (IndexOutOfBoundsException e){
                throw new IllegalArgumentException("boeen has wrong format:"+weatherText.get(23));
            }
        }
    }

    /**
     * Reads a WeatherCard from a local file. Currently not used.
     *
     * @param context
     * @param filename
     */

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
     *
     * @return
     */

    public double getCurrentTemp(){
        int i = Integer.parseInt(this.lufttemperatur[0]);
        double d = i;
        return d;
    }

    public String getNumbers(String parameter){
        String s = new String(parameter);
        // elimate non-number chars from the beginning of s
        while (!isNumber(s.charAt(0))){
            s = s.substring(1);
        }
        return s;
    }

    public int[] getIntArray(String[] parameter){
        String[] s = parameter.clone();
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

    public int[] getIntArray(String[] parameter, int start, int stop){
        // elimate non-number chars from the beginning of s
        String[] s = parameter.clone();
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
        // go one back so it will be the last record of the current day
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

    /**
     * Returns lowest temperature of tomorrow. Currently not used.
     * CAUTION: keep in mind that due to a 24h forecast this result is of limited value,
     *          as in most cases no complete data is available for the next day.
     *
     * @return
     */

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

    /**
     * Returns highest temperature of tomorrow. Currently not used.
     * CAUTION: keep in mind that due to a 24h forecast this result is of limited value,
     *          as in most cases no complete data is available for the next day.
     *
     * @return
     * */

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

    public double get24hHigh(){
        int[] tempmax = getIntArray(this.lufttemperatur_max);
        int result = tempmax[0];
        for (int i=0; i<9; i++){
            if (tempmax[i]>result){
                result = tempmax[i];
            }
        }
        double d = result;
        return d;
    }

    public double get24hLow(){
        int[] tempmin = getIntArray(this.lufttemperatur_min);
        int result = tempmin[0];
        for (int i=0; i<9; i++){
            if (tempmin[i]<result){
                result = tempmin[i];
            }
        }
        double d = result;
        return d;
    }

    public boolean isValidWindDirChar(String s){
        if ((s.equals("N"))||(s.equals("S"))||(s.equals("W"))||(s.equals("O"))||(s.equals("C"))){
            return true;
        }
        return false;
    }

    public String getWindDir(String parameter){
        String s = new String(parameter);
        Character c = s.charAt(0);
        int winddir_end = 0;
        while (isValidWindDirChar(s.substring(winddir_end,winddir_end+1))){
            winddir_end = winddir_end + 1;
        }
        String result = s.substring(0,winddir_end);
        result = result.trim();
        return result;
        /*
        if (Character.isDigit(s.charAt()))
        if (Character.isDigit(s.charAt(0))) {
            // one char wind code
            result = String.valueOf(s.charAt(0));
        } else {
            // two char wind code
            result = s.substring(0,1);
        }
        return result;
         */
    }

    public double windDirection(int pos){
        String windstring = getWindDir(wind[pos]);
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
            case "C": i=0; break;
        }
        double d = i;
        return d;
    }

    public String getWindDirString(Context c, int pos){
        String windstring = getWindDir(wind[pos]);
        String result = "";
        switch (windstring){
            case "N":  result = c.getApplicationContext().getResources().getString(R.string.direction_north); break;
            case "NO": result = c.getApplicationContext().getResources().getString(R.string.direction_northeast); break;
            case "O":  result = c.getApplicationContext().getResources().getString(R.string.direction_east); break;
            case "SO": result = c.getApplicationContext().getResources().getString(R.string.direction_southeast); break;
            case "S":  result = c.getApplicationContext().getResources().getString(R.string.direction_south); break;
            case "SW": result = c.getApplicationContext().getResources().getString(R.string.direction_southwest); break;
            case "W":  result = c.getApplicationContext().getResources().getString(R.string.direction_west); break;
            case "NW": result = c.getApplicationContext().getResources().getString(R.string.direction_northwest); break;
            case "C":  result = c.getApplicationContext().getResources().getString(R.string.direction_calm); break;
        }
        return result;

    }

    public String getWindSpeed(String parameter){
        String s = new String(parameter);
        String result = "";
        if (Character.isDigit(s.charAt(0))) {
            // no wind direction given, 1st char is number, e.g. "10"
            result = s.substring(0);
        } else {
            if (Character.isDigit(s.charAt(1))) {
                // one char wind direction, e.g. "N10"
                result = s.substring(1);
            } else {
                // two char wind direction, e.g. "NW10"
                result = s.substring(2);
            }
        }
        return result;
    }

    public double windSpeed(int pos){
        String s = getWindSpeed(wind[pos]);
        int i = Integer.parseInt(s);
        double d = i;
        return d;
    }

    public double getCurrentWindSpeed(){
     return windSpeed(0);
    }

    public double getCurrentWindDirection(){
     return windDirection(0);
    }

    public String getCurrentFlurries(){
        return boeen[0];
    }

    public String getCurrentWind(){
        return String.valueOf((int) (windSpeed(0)));
    }

    public String getWindSpeed(int i){
        return String.valueOf((int) (windSpeed(i)));
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

    public Bitmap getArrow(Context context, int pos){
        int degrees = 0;
        // no wind direction given, no wind.
        // String first_char = String.valueOf(wind[pos].charAt(0));
        // String first_two_chars = String.valueOf(wind[pos].charAt(0))+String.valueOf(wind[pos].charAt(1));
        // Log.v("WEATHERWIDGET","FIRST:"+first_char);
        // Log.v("WEATHERWIDGET","TWO  :"+first_two_chars);
        String winddir = getWindDir(wind[pos]);
        if (winddir.equals("C")){
            return null;
        }
        if (winddir.equals("N")){
            degrees = 0;
        }
        if (winddir.equals("O")){
            degrees = 270;
        }
        if (winddir.equals("S")){
            degrees = 180;
        }
        if (winddir.equals("W")){
            degrees = 90;
        }
        if (winddir.equals("NO")){
            degrees = 315;
        }
        if (winddir.equals("SO")){
            degrees = 225;
        }
        if (winddir.equals("SW")){
            degrees = 135;
        }
        if (winddir.equals("NW")){
            degrees = 45;
        }
        return getArrowBitmap(context,degrees);
    }

    public Bitmap getCurrentArrow(Context c){
        return getArrow(c,0);
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
        return toString(0,8);
    }

}
