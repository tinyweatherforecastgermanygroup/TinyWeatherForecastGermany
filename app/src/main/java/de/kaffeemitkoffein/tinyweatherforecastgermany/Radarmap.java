/**
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

import android.graphics.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Radarmap {

    long timestamp;
    String timestampStr0;
    String timestampStr1;
    int filesize;
    int formatVersion;
    String radolanVersion;
    double accuracy;
    int interval;
    int resolution;
    String stations;
    byte[][] map;
    int width;
    int height;

    public final static int RADAR_DATAINTERVAL = 1000*60*5; // 5 minutes

    public static class FormatVersion{
        final static int MIXED = 0; // 100 km + 128 km radius
        final static int KM100 = 1; // 100 km radius
        final static int KM128 = 2; // 128 km radius
        final static int KM150 = 3; // 150 km radius
    }

    public static class Resolution{
        final static int RES_900X900 = 0;
        final static int RES_1100X900 = 1;
        final static int RES_1500X1400 = 2;
    }

    public float XLeftGeo(){
        if (resolution==Resolution.RES_900X900){
            return 2.0715f;
        }
        if (resolution==Resolution.RES_1100X900){
            return 3.0889f;
        }
        return 0;
    }

    public float YTopGeo(){
        if (resolution==Resolution.RES_900X900){
            return 54.5877f;
        }
        if (resolution==Resolution.RES_1100X900){
            return 55.5482f;
        }
        return 0;
    }

    public float XRightGeo(){
        if (resolution==Resolution.RES_900X900){
            return 15.7208f;
        }
        if (resolution==Resolution.RES_1100X900){
            return 17.1128f;
        }
        return 0;
    }

    public float YBottomGeo(){
        if (resolution==Resolution.RES_900X900){
            return 47.0705f;
        }
        if (resolution==Resolution.RES_1100X900){
            return 46.1827f;
        }
        return 0;
    }

    public float radarmapWidthGeo(){
        return XRightGeo() - XLeftGeo();
    }

    public float radarmapHeightGeo(){
        return YTopGeo() - YBottomGeo();
    }

    public static int[]   RAINCOLORS={0x00000000,0xff99ffff,0xff33ffff,0xff00caca,0xff009934,0xff4dbf1a,
                                      0xff99cc00,0xffcce600,0xffffff00,0xffffc400,0xffffc400,0xffff0000,
                                      0xffb40000,0xff4848ff,0xff0000ca,0xff990099,0xffff33ff};
    public static float[] DBZVALUES ={1         ,5.5f      ,10f       ,14.5f     ,19f       ,23.5f,
                                      28f       ,32.5f     ,37f       ,41.5f     ,46f       ,50.5f,
                                      55f       ,60f       ,65f       ,75f       ,85f};

    public static byte CLUTTER = (byte) 249;
    public static byte ERROR = (byte) 250;

    public float highestDBZ=0;
    public float highestByte=0;

    public int getRadarMapColor(byte b){
        if ((b == CLUTTER) || (b == ERROR)) {
            return Color.TRANSPARENT;
        }
        // convert signed byte to unsigned int by simply getting the last 8 bytes of the int
        int ub = b & 0xff;
        float dbZ = (ub / 2f) - 32.5f;
        if (dbZ>highestDBZ){
            highestDBZ = dbZ;
        }
        if (b>highestByte){
            highestByte = b;
        }
        int color = RAINCOLORS[RAINCOLORS.length-1];
        for (int i=RAINCOLORS.length-2; i>=0; i--){
            if (dbZ < DBZVALUES[i]){
                color = RAINCOLORS[i];
            }
        }
        if (color==0){
            color = Color.TRANSPARENT;
        }
        return color;
    }

    public Radarmap(RawRadarmap rawRadarmap){
        this.timestampStr0 = new String(rawRadarmap.timestamp);
        this.timestampStr1 = new String(rawRadarmap.timestampStr);
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddHHmmMMyy");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String s = this.timestampStr0+this.timestampStr1;
            Date date = simpleDateFormat.parse(this.timestampStr0+this.timestampStr1);
            calendar.setTime(date);
            this.timestamp=calendar.getTimeInMillis();
        } catch (Exception e){
            this.timestamp=0;
        }

        try {
            this.filesize = Integer.parseInt(new String(rawRadarmap.filesize));
        } catch (NumberFormatException e){
            this.filesize = 0;
        }
        try {
            this.formatVersion = Integer.parseInt(new String(rawRadarmap.formatVersion));
        } catch (NumberFormatException e){
            this.formatVersion = 0;
        }
        this.radolanVersion = new String(rawRadarmap.radolanVersion);
        String sAcc = new String(rawRadarmap.accuracy);
        this.accuracy=1;
        if (sAcc.equals("E-01")){
            accuracy=0.1;
        }
        if (sAcc.equals("E-02")){
            accuracy=0.01;
        }
        try {
            this.interval = Integer.parseInt(new String(rawRadarmap.interval));
        } catch (NumberFormatException e){
            this.interval = 0;
        }
        // define default value
        this.resolution = Resolution.RES_1100X900;
        String s = new String(rawRadarmap.resolution);
        if (s.equals("900x 900")){
            this.resolution = Resolution.RES_900X900;
        }
        if (s.equals(" 1100x 900")){
            this.resolution = Resolution.RES_1100X900;
        }
        if (s.equals("1500x1400")){
            this.resolution = Resolution.RES_1500X1400;
        }
        if (rawRadarmap.stationString!=null){
            this.stations = new String(rawRadarmap.stationString);
        }
        if (resolution==Resolution.RES_900X900){
            map = new byte[900][900];
            width = 900;
            height = 900;
        } else
        if (resolution==Resolution.RES_1500X1400){
            map = new byte[1500][1400];
            width = 1500;
            height = 1400;
        } else {
            // default
            map = new byte[900][1100];
            width = 900;
            height = 1100;
        }
        int i = 0;
        for (int y=0; y<height; y++){
            for (int x=0; x<width; x++){
                map[x][y] = rawRadarmap.radarData[i];
                i++;
            }
        }

    }

}
