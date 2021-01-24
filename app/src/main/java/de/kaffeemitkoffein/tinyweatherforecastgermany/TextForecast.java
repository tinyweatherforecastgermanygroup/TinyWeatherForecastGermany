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

import android.util.Log;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TextForecast implements Comparable<TextForecast>{

    public static final String TEXT_WEBPATH = "https://opendata.dwd.de/weather/text_forecasts/txt/";
    public static final String TEXT_WEBPATH_LEGACY = "http://opendata.dwd.de/weather/text_forecasts/txt/";

    public static final String TIMESTAMP_PATTERN="dd-MMM-yyyy HH:mm";

    /*
     identifier         is the file name @ the dwd open data without any path(s)
     title              parsed headline
     subtitle           parsed subtitle
     issued_text        description about issuing the text in plain text, parsed
     issued             UTC when item was issued; this is taken from the file date, not the text. The issued_text
                        usually indicates a slightly earlier date.
     polled             UTC when item was polled
     outdated           true if item is not available any more @ dwd open data. Currently not used and not set properly.

    */

    public String identifier;
    public String content;
    public String title;
    public String subtitle;
    public String issued_text;
    public int type;
    public long issued;
    public long polled;
    public boolean outdated;

    public static class Type{
        public final static int FEATURE = 0;
        public final static int KURZFRIST = 1;
        public final static int MITTELFRIST = 2;
    }

    public TextForecast(){
    }

    private int findFirstSeparator(ArrayList<String> source, String separator){
        if (source!=null){
            for (int i=0; i<source.size(); i++){
                if (source.get(i).contains(separator)) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * Helper to remove multiple, adjacent spaces from a String, eg. "foo    bar" results in "foo bar".
     *
     * @param source
     * @return
     */

    private String removeDoubleSpaces(String source){
        while (source.contains("  ")){
            source = source.replace("  "," ");
        }
        return source;
    }

    /**
     * Changes S P A C E   F O N T  into an usable, simple String removing any unnecessary spaces. The result remains
     * upper case. E.g., "F O O   B A R" results in "FOO BAR".
     *
     * @param source
     * @return
     */

    private String fromSpaceFont(String source){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<source.length(); i++){
            if (source.charAt(i) == ' '){
                boolean isRealSpace = false;
                if (i>0){
                    if (source.charAt(i-1) == ' '){
                        isRealSpace = true;
                    }
                }
                if (i<source.length()-1){
                    if (source.charAt(i+1) == ' '){
                        isRealSpace = true;
                    }
                }
                if (isRealSpace) {
                    stringBuilder.append(source.charAt(i));
                }
            } else {
                stringBuilder.append(source.charAt(i));
            }
        }
        return removeDoubleSpaces(stringBuilder.toString());
    }

    public void parse(ArrayList<String> source){
        Log.v("TEXTF","type "+this.type);
        if (type==Type.FEATURE){
            this.title = source.get(1);
            int textStartPosition=2;
            while (source.get(textStartPosition).equals("") && textStartPosition<source.size()){
                textStartPosition++;
            }
            this.subtitle = source.get(textStartPosition)+"…";
            getBody(source,textStartPosition);
        } else if (type==Type.MITTELFRIST){
            // parse title
            this.title = fromSpaceFont(source.get(1));
            // pare issued text
            this.issued_text=source.get(3);
            // find first separator with "_"
            int subtitleEndPos=findFirstSeparator(source,"___");
            Log.v("TEXTF","titleEndPos "+subtitleEndPos);
            StringBuilder stringBuilder = new StringBuilder();
            // hardcoded subtitle starts at line 6
            for (int i=6; i<subtitleEndPos; i++){
                 stringBuilder.append(source.get(i));
            }
            this.subtitle = stringBuilder.toString();
            getBody(source,subtitleEndPos+1);

        } else if (type==Type.KURZFRIST){
            // parse title
            this.title = fromSpaceFont(source.get(2));
            // pare issued text
            this.issued_text=source.get(3);
            // find first separator with "---"
            int subtitleEndPos=findFirstSeparator(source,"---");
            int subtitleStartPos = subtitleEndPos;
            // find first empty line above the seperator
            while (!source.get(subtitleStartPos).equals("") && subtitleStartPos>0){
                subtitleStartPos--;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=subtitleStartPos+1; i<subtitleEndPos; i++){
                stringBuilder.append(source.get(i));
            }
            this.subtitle = stringBuilder.toString();
            getBody(source,6);
        }
        // todo: other formats + legacy fallback
    }

    private void getBody(ArrayList<String> source, int startPos){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=startPos; i<source.size(); i++){
            String s = source.get(i);
            String target = s;
            // change empty lines to space
            if (target.length()<1){
                target = target + " ";
            } else
            // sometimes lines end with a space and sometimes not
            if (!target.substring(target.length()-1).equals(" ")){
                target = target + " ";
            }
            // detect new paragraphs
            if (target.length()<42){
                target = target + System.getProperty("line.separator");
            }
            // ignore (inconsistent) md-like underlines
            if (!target.contains("--") && !target.contains("__")){
                stringBuilder.append(target);
            }
        }
        this.content = stringBuilder.toString();
    }

    public boolean setIssued(String timestring, int parsePosition){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIMESTAMP_PATTERN);
        Date date = simpleDateFormat.parse(timestring,new ParsePosition(parsePosition));
        if (date==null){
            this.issued = 0;
            return false;
        }
        this.issued = date.getTime();
        return true;
    }

    public String getIssued(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIMESTAMP_PATTERN);
        return simpleDateFormat.format(new Date(this.issued));
    }

    public String getUrlString(){
        if (this.identifier != null){
            return TEXT_WEBPATH + this.identifier;
        }
        return null;
    }

    public String getLegacyUrlString(){
        if (this.identifier != null){
            return TEXT_WEBPATH_LEGACY + this.identifier;
        }
        return null;
    }

    // two textForecasts are equal if id & issued are equal

    public boolean equals(TextForecast textForecast){
        return ((textForecast.identifier.equals(this.identifier)) && (textForecast.issued == this.issued));
    }

    @Override
    public int compareTo(TextForecast t1) {
        if (this.issued < t1.issued){
            return -1;
        }
        if (this.issued > t1.issued){
            return 1;
        }
        return 0;
    }

}
