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

import android.content.Context;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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

        public static int getColor(Context context, int severity){
            switch (severity){
                case MINOR_INT: return Color.YELLOW;
                case MODERATE_INT: return 0xe6700b;
                case SEVERE_INT: return Color.RED;
                case EXTREME_INT: return 0xb629cd;
                default: return ThemePicker.getColorTextLight(context);
            }
        }

    }

    public final static String ID_UPDATE="Update";
    public final static String ID_SILENT_UPDATE="SILENT_UPDATE";

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
    long effective = 0;
    long onset = 0;
    long expires = 0;
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

    public void initPolygons(Context context){
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
        if (polygonlist.size()==0){
            ArrayList<Areas.Area> areas = Areas.getAreas(context,area_warncellIDs);
            for (int i=0; i<areas.size(); i++){
                polygonlist.addAll(areas.get(i).polygons);
            }
        }
    }

    public boolean isInPolygonGeoOld(float testy, float testx){
        if (polygons==null){
            return false;
        }
        if (polygons.size()==0){
            return false;
        }
        // return false if point is in excluded polygon; it is efficient to check this first.
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

    public boolean isInPolygonGeo(float testy, float testx){
        if (polygonlist==null){
            return false;
        }
        if (polygonlist.size()==0){
            return false;
        }
        // return false if point is in excluded polygon; it is efficient to check this first.
        for (int j=0; j<excluded_polygonlist.size(); j++){
            Polygon polygon = excluded_polygonlist.get(j);
            if (polygon.isInPolygon(testx,testy)){
                return false;
            }
        }
        // return true if point is in polygon
        for (int j=0; j<polygonlist.size(); j++){
            Polygon polygon = polygonlist.get(j);
            if (polygon.isInPolygon(testx,testy)){
                return true;
            }
        }
        // otherwise false
        return false;
    }

    public boolean isInPolygonGeo(Weather.WeatherLocation weatherLocation){
        return isInPolygonGeo((float) weatherLocation.latitude,(float) weatherLocation.longitude);
    }

    public int getWarningColor(){
        String[] colors = area_color.trim().split("\\s+");
        int result = Color.rgb(Integer.parseInt(colors[0]),Integer.parseInt(colors[1]),Integer.parseInt(colors[2]));
        return result;
    }

    /**
     * Checks if this warning is an update of a previously issued warning.
     *
     * @return true if the warning is an update
     */

    public boolean isUpdate(){
        return msgType.equalsIgnoreCase(ID_UPDATE);
    }

    public boolean isSilentUpdate(){
        for (int i=0; i<codes.size(); i++){
            if (codes.get(i).equalsIgnoreCase(ID_SILENT_UPDATE)){
                return true;
            }
        }
        return false;
    }

    public boolean hasReferenceID(String referenceID){
        for (int i=0; i<references.size(); i++){
            if (references.get(i).equalsIgnoreCase(referenceID)){
                return true;
            }
        }
        return false;
    }

    public int getSeverity(){
        if (severity!=null){
            return Severity.toInt(severity);
        }
        return 0;
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

    public static String getUnderlineString(String source, String underlineChar){
        if (source==null){
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0; i<source.length(); i++){
                stringBuilder.append(underlineChar);
            }
            return stringBuilder.toString();
        }
    }

    public String getPlainTextWarning(Context context, boolean includeCredentials){
        String newLine = System.getProperty("line.separator");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(WeatherSettings.getSetStationLocation(context).getDescription(context).toUpperCase(Locale.getDefault()));
        stringBuilder.append(": ");
        stringBuilder.append(this.description);
        stringBuilder.append(newLine);
        stringBuilder.append(newLine);
        stringBuilder.append(WeatherWarningAdapter.formatTime(this.effective)); stringBuilder.append(" ");
        stringBuilder.append(this.status);
        stringBuilder.append(" ");
        stringBuilder.append(this.msgType);
        stringBuilder.append(newLine);
        stringBuilder.append(this.event.toUpperCase(Locale.getDefault()));
        stringBuilder.append(newLine);
        stringBuilder.append(getUnderlineString(this.event,"="));
        stringBuilder.append(newLine);
        stringBuilder.append(WeatherWarningAdapter.formatTime(this.onset)); stringBuilder.append(" ");
        stringBuilder.append(context.getResources().getString(R.string.warnings_until)); stringBuilder.append(" ");
        stringBuilder.append(WeatherWarningAdapter.formatTime(this.expires));
        stringBuilder.append(newLine);
        stringBuilder.append(this.urgency);  stringBuilder.append(" ");
        stringBuilder.append(this.severity); stringBuilder.append(" ");
        stringBuilder.append(this.certainty);
        stringBuilder.append(newLine);
        stringBuilder.append("> ");
        boolean limitLocationsInWarnings = true;
        int max_locations = WeatherSettings.getMaxLocationsInSharedWarnings(context);
        int locationCount = 0;
        // if zero, do not limit location count but set to arraylist length
        if (max_locations == 0){
            limitLocationsInWarnings = false;
            max_locations = area_names.size();
        }
        for (int i=0; ((i<this.area_names.size()) && (i<max_locations)); i++){
            stringBuilder.append(this.area_names.get(i));
            if (i<this.area_names.size()-1){
                stringBuilder.append(", ");
            }
            locationCount++;
        }
        if ((limitLocationsInWarnings) && (locationCount<this.area_names.size())){
            stringBuilder.append("…");
        }
        stringBuilder.append(newLine); stringBuilder.append(newLine);
        stringBuilder.append(this.headline);
        stringBuilder.append(newLine);
        stringBuilder.append(getUnderlineString(this.headline,"="));
        stringBuilder.append(newLine);
        stringBuilder.append(newLine);
        if (this.instruction.trim().length()>0){
            stringBuilder.append(this.instruction);
            stringBuilder.append(newLine); stringBuilder.append(newLine);
        }
        if ((this.parameter_names!=null) && (this.parameter_values!=null)){
            for (int i=0; i<this.parameter_names.size() && i<this.parameter_values.size(); i++){
                stringBuilder.append(this.parameter_names.get(i)); stringBuilder.append(": ");
                stringBuilder.append(this.parameter_values.get(i)); stringBuilder.append(newLine);
            }
            if (this.parameter_names.size()>0){
                stringBuilder.append(newLine);
            }
        }
        if (includeCredentials){
            stringBuilder.append(context.getResources().getString(R.string.dwd_warnings_notice));
        }
        return stringBuilder.toString();
    }

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

    // some warnings may have no expiry date, we need to put fictional ones in for graphs & calculations if this
    // warning applies

    public long getApplicableExpires(){
        if (expires==0){
            // return 32503680000L; // 01.01.3000
            if (onset!=0){
                return onset + 1036800000L;  // plus 12 days in millis
            } else {
                return Calendar.getInstance().getTimeInMillis() + 1036800000L;
            }
        } else {
            return expires;
        }
    }

}
