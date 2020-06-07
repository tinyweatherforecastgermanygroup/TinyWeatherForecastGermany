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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.net.URL;
import java.util.Comparator;

public class Station implements Comparator<Station> {
    private String name;
    private String code1;
    private String code2;

    public final static String BASICWEBURL="https://opendata.dwd.de/weather/text_forecasts/tables/";

    public Station() {
        this.name = "";
        this.code1 = "";
        this.code2 = "";
    }

    public Station(String code1, String code2, String name) {
        this.name = name;
        this.code1 = code1;
        this.code2 = code2;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCode1(String code1){
        this.code1 = code1;
    }

    public void setCode2(String code1){
        this.code2 = code2;
    }

    public String getName(){
        return this.name;
    }

    public String getCode1(){
        return this.code1;
    }

    public String getCode2(){
        return this.code2;
    }

    /**
     * Returns an arraylist of strings that hold all the possible forecast web urls, starting with the most recent possibility.
     * Does not roll back to the previous day, assuming that there will be a forecast made at 00:00 at least.
     * @return
     */

    public ArrayList<String> getAbsoluteWebURLStringArrayList(){
        Calendar calendar = Calendar.getInstance();
        String day_of_month = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        if (day_of_month.length()<2) {
            day_of_month = "0" + day_of_month;
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        ArrayList<String> hourlist = new ArrayList<String>();
        while (hour>=0){
            String hour_string = String.valueOf(hour);
            if (hour_string.length()<2){
                hour_string = "0" + hour_string;
            }
            hourlist.add(this.BASICWEBURL+this.code1+"_"+this.code2+"_"+day_of_month+hour_string+"00");
            hour--;
        }
        return hourlist;
    }

    /**
     * Returns an arraylist of Urls that hold all the possible forecast web urls, starting with the most recent possibility.
     * Does not roll back to the previous day, assuming that there will be a forecast made at 00:00 at least.
     * @return
     */

    public ArrayList<URL> getAbsoluteWebURLArrayList(){
        ArrayList<String> url_strings = new ArrayList<String>();
        ArrayList<URL> urls = new ArrayList<URL>();
        url_strings = getAbsoluteWebURLStringArrayList();
        for (int i=0; i<url_strings.size(); i++){
            try {
                URL u = new URL(url_strings.get(i));
                urls.add(u);
            } catch (MalformedURLException e){
                // do nothing
            }
        }
        return urls;
    }

    public URL[] getAbsoluteWebURLArray(){
        ArrayList<URL> url_list = getAbsoluteWebURLArrayList();
        URL url_array[] = new URL[24];
        for (int i=0; i<url_list.size(); i++){
            url_array[i]=url_list.get(i);
        }
       return url_array;
    }


    @Override
    public int compare(Station s1, Station s2) {
        return s1.getName().compareTo(s2.getName());
    }
}
