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

import android.graphics.Color;
import java.util.ArrayList;

public class WeatherWarning implements Comparable<WeatherWarning> {

    public static class Severity{
        final static String MINOR = "Minor";
        final static String MODERATE = "Moderate";
        final static String SEVERE = "Severe";
        final static String EXTREME = "Extreme";

        final static int MINOR_INT = 1;
        final static int MODERATE_INT = 2;
        final static int SEVERE_INT = 3;
        final static int EXTREME_INT = 4;

        public static int toInt(String value){
            if (value.equals(MINOR)){
                return MINOR_INT;
            }
            if (value.equals(MODERATE)){
                return MODERATE_INT;
            }
            if (value.equals(SEVERE)){
                return SEVERE_INT;
            }
            if (value.equals(EXTREME)){
                return EXTREME_INT;
            }
            return 0;
        }
    }

    long polling_time;
    String identifier;      // id of the warning
    String sender;          // sender, usually "opendata@dwd.de"
    long sent;              // time of issuing in UTC
    String status;          // status, e.g. "Actual"
    String msgType;         // e.g. "Update"
    String source;          // local source of warning
    String scope;           // only known value at opendata is "Public", but indicates there may be others
    ArrayList<String> codes;
    ArrayList<String> references; // id of reference
    String language;
    String category;
    String event;
    String responseType;
    String urgency;
    String severity;
    String certainty;
    long effective;
    long onset;
    long expires;
    String senderName;
    String headline;
    String description;
    String instruction;
    String web;
    String contact;
    String profile_version;
    String license;
    String ii;
    ArrayList<String> groups;
    String area_color;
    ArrayList<String> parameter_names;
    ArrayList<String> parameter_values;
    ArrayList<String> polygons;
    ArrayList<String> excluded_polygons;
    ArrayList<String> area_names;
    ArrayList<String> area_warncellIDs;

    ArrayList<Polygon> polygonlist;
    ArrayList<Polygon> excluded_polygonlist;

    public void initPolygons(){
        polygonlist = new ArrayList<Polygon>();
        excluded_polygonlist = new ArrayList<Polygon>();
        if (polygons!=null){
            for (int j=0; j<polygons.size(); j++){
                Polygon polygon = new Polygon(polygons.get(j));
                polygonlist.add(polygon);
            }
            if (excluded_polygons!=null){
                for (int j=0; j<excluded_polygons.size(); j++){
                    Polygon polygon = new Polygon(excluded_polygons.get(j));
                    excluded_polygonlist.add(polygon);
                }
            }
        }
    }

    public boolean isInPolygonGeo(float testx, float testy){
        if (polygonlist==null){
            initPolygons();
        }
        if (polygons==null){
            return false;
        }
        if (polygons.size()==0){
            return false;
        }
        // return false if point is in excluded poygon; it is efficient to check this first.
        for (int j=0; j<excluded_polygons.size(); j++){
            Polygon polygon = new Polygon(excluded_polygons.get(j));
            if (polygon.isInPolygon(testx,testy)){
                return false;
            }
        }
        // return true if point is in polygon
        for (int j=0; j<polygons.size(); j++){
            Polygon polygon = new Polygon(polygons.get(j));
            if (polygon.isInPolygon(testx,testy)){
                return true;
            }
        }
        // otherwise false
        return false;
    }

    public int getWarningColor(){
        String[] colors = area_color.trim().split("\\s+");
        int result = Color.rgb(Integer.parseInt(colors[0]),Integer.parseInt(colors[1]),Integer.parseInt(colors[2]));
        return result;
    }

    /*
    public void outputToLog(){
        Log.v(Tag.WARNINGS,"====================================================");
        Log.v(Tag.WARNINGS,"Identifier: "+identifier);
        Log.v(Tag.WARNINGS, "Sender: "+sender);
        Log.v(Tag.WARNINGS,"Sent: "+sent);
        Log.v(Tag.WARNINGS, "Status: "+status);
        Log.v(Tag.WARNINGS, "MsgType: "+msgType);
        Log.v(Tag.WARNINGS, "Source: "+source);
        Log.v(Tag.WARNINGS, "Scopa: "+scope);
        Log.v(Tag.WARNINGS, "Codes: "+codes.size());
        for (int i=0; i<references.size(); i++){
            Log.v(Tag.WARNINGS,"Ref #"+i+": "+references.get(i));
        }
        Log.v(Tag.WARNINGS, "Language: "+language);
        Log.v(Tag.WARNINGS, "Category: "+category);
        Log.v(Tag.WARNINGS, "Event: "+event);
        Log.v(Tag.WARNINGS, "ResponseType: "+responseType);
        Log.v(Tag.WARNINGS, "Urgency: "+urgency);
        Log.v(Tag.WARNINGS, "Severity: "+severity);
        Log.v(Tag.WARNINGS, "Certainty: "+certainty);
        Log.v(Tag.WARNINGS, "Effective: "+effective);
        Log.v(Tag.WARNINGS, "Onset    : "+onset);
        Log.v(Tag.WARNINGS, "Expires  : "+expires);
        Log.v(Tag.WARNINGS, "SenderName: "+senderName);
        Log.v(Tag.WARNINGS, "Headline: "+headline);
        Log.v(Tag.WARNINGS, "Description: "+description);
        Log.v(Tag.WARNINGS, "Instruction: "+instruction);
        Log.v(Tag.WARNINGS, "Web: "+web);
        Log.v(Tag.WARNINGS, "Contact: "+contact);
        Log.v(Tag.WARNINGS, "Groups     #: "+groups.size());
        Log.v(Tag.WARNINGS, "Paramters  #: "+parameter_names.size());
        Log.v(Tag.WARNINGS, "Values     #: "+parameter_values.size());
        Log.v(Tag.WARNINGS, "Polygons   #: "+polygons.size());
        Log.v(Tag.WARNINGS, "(-)Polygons#: "+excluded_polygons.size());
        Log.v(Tag.WARNINGS, "Cities     #: "+area_names.size());
        Log.v(Tag.WARNINGS, "WarnCellID #: "+area_warncellIDs.size());
    }

     */

    /*
     * Warnings are sorted by severity and timestamp.
     */

    @Override
    public int compareTo(WeatherWarning w) {
        if ((Severity.toInt(this.severity) == Severity.toInt(w.severity))){
            if ((this.effective==0)||(w.effective==0)){
                // when one of the objects has no effective time stamp, they are regarded "equal"
                return 0;
            }
            if (this.effective<w.effective){
                return -1;
            }
            if (this.effective> w.effective){
                return 1;
            }
        } else if (Severity.toInt(this.severity) > Severity.toInt(w.severity)){
            return -1;
        } else if (Severity.toInt(this.severity) < Severity.toInt(w.severity)){
            return 1;
        }
        return 0;
    }

}
