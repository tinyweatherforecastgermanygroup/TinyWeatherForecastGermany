package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.util.Log;

import java.util.ArrayList;

public class WeatherWarning {
    // <alert>
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
    ArrayList<String> area_names;
    ArrayList<String> area_warncellIDs;

    float[] polygonX;
    float[] polygonY;
    int nvert;

    /**
     * determines if point is inside a polygon.
     *
     * Adapted to java from C, see https://wrf.ecse.rpi.edu/Research/Short_Notes/pnpoly.html,
     * original code Copyright (c) 1970-2003, Wm. Randolph Franklin, see COPYING.txt for
     * license.
     *
     * @param nvert
     * @param vertx
     * @param verty
     * @param testx
     * @param testy
     * @return
     */

    private boolean isInPolygon(int nvert, float[] vertx, float[] verty, float testx, float testy ){
        boolean c = false;
        int i, j;
        for (i = 0, j = nvert-1; i < nvert; j = i++) {
            if ( ((verty[i]>testy) != (verty[j]>testy)) &&
                    (testx < (vertx[j]-vertx[i]) * (testy-verty[i]) / (verty[j]-verty[i]) + vertx[i]) )
                c = !c;
        }
        return c;
    }

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
        Log.v(Tag.WARNINGS, "Groups    #: "+groups.size());
        Log.v(Tag.WARNINGS, "Paramters #: "+parameter_names.size());
        Log.v(Tag.WARNINGS, "Values    #: "+parameter_values.size());
        Log.v(Tag.WARNINGS, "Polygons  #: "+polygons.size());
        Log.v(Tag.WARNINGS, "Cities    #: "+area_names.size());
        Log.v(Tag.WARNINGS, "WarnCellID#: "+area_warncellIDs.size());
    }
}
