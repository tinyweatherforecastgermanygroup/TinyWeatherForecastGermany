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
import android.graphics.*;
import java.io.*;

// This class is deprecated and not used anymore. See RadarMN2 class.

public class RadarMN {

    final static int RADAR_DATAINTERVAL = 1000*60*5;

    // represents the 1100x1200 grid in colors already populated correctly by the geoserver
    int[] color = null;
    long timestamp;

    // some data constants
    public static final int RADARMAP_PIXEL_FIXEDWIDTH  = 1100;
    public static final int RADARMAP_PIXEL_FIXEDHEIGHT = 1200;
    public static final long RADARMAP_BYTESIZE = RADARMAP_PIXEL_FIXEDWIDTH*RADARMAP_PIXEL_FIXEDHEIGHT*4L;

    private static final float GEO_X_NORTH_WEST = 1.435612143f;
    private static final float GEO_Y_NORTH_WEST =  55.865842289f;

    private static final float GEO_X_NORTH_EAST = 18.76728172f;
    private static final float GEO_Y_NORTH_EAST = 55.84848692f;

    private static final float GEO_X_SOUTH_WEST = 3.551921296f;
    private static final float GEO_Y_SOUTH_WEST = 45.69587068f;

    private static final float GEO_X_SOUTH_EAST = 16.60186543f;
    private static final float GEO_Y_SOUTH_EAST = 45.68358331f;

    public static float getGeoWidth(int y){
        float left = ((GEO_X_SOUTH_WEST - GEO_X_NORTH_WEST)/RADARMAP_PIXEL_FIXEDHEIGHT)*y;
        float right = ((GEO_X_SOUTH_EAST - GEO_X_NORTH_EAST)/RADARMAP_PIXEL_FIXEDHEIGHT)*y;
        float baseline = GEO_X_SOUTH_EAST - GEO_X_SOUTH_WEST;
        float geoWidth = baseline + left - right;
        return geoWidth;
    }

    public static float getGeoHeight(int x){
        float top =    ((GEO_Y_NORTH_WEST - GEO_Y_NORTH_EAST)/RADARMAP_PIXEL_FIXEDWIDTH)*x;
        float bottom = ((GEO_Y_SOUTH_EAST - GEO_Y_SOUTH_WEST)/RADARMAP_PIXEL_FIXEDWIDTH)*x;
        float baseline = GEO_Y_NORTH_WEST - GEO_Y_SOUTH_WEST;
        float geoHeight = baseline + top + bottom;
        return geoHeight;
    }

    public static float getGeoXRowStart(int y){
        float left = ((GEO_X_SOUTH_WEST - GEO_X_NORTH_WEST)/RADARMAP_PIXEL_FIXEDHEIGHT)*y;
        float baseline = GEO_X_SOUTH_EAST - GEO_X_SOUTH_WEST;
        float geoXRowStart = GEO_X_SOUTH_WEST - left;
        return geoXRowStart;
    }

    public static float getGeoX(int x, int y){
        float a = getGeoXRowStart(y);
        float b = (getGeoWidth(y)/RADARMAP_PIXEL_FIXEDWIDTH)*x;
        float geoX = a+b;
        return  geoX;
    }

    public static float getGeoYStartTop(int x){
        final float GEO_Y_TOP_DIFF = GEO_Y_NORTH_EAST - GEO_Y_NORTH_WEST;
        float difference = (GEO_Y_TOP_DIFF/RADARMAP_PIXEL_FIXEDWIDTH) * x;
        float offset = GEO_Y_NORTH_WEST + difference;
        return offset;
    }

    public static float getGeoY(int x, int y){
        float a = getGeoYStartTop(x);
        float height = getGeoHeight(x);
        float geoY = a - (height/RADARMAP_PIXEL_FIXEDHEIGHT)*y;
        return geoY;
    }

    public static int[] getPixels(){
        return new int[RADARMAP_PIXEL_FIXEDWIDTH*RADARMAP_PIXEL_FIXEDHEIGHT];
    }

    public static int[] getPixels(Bitmap bitmap){
        int[] color =  getPixels();
        bitmap.getPixels(color,0,RADARMAP_PIXEL_FIXEDWIDTH,0,0,RADARMAP_PIXEL_FIXEDWIDTH,RADARMAP_PIXEL_FIXEDHEIGHT);
        for (int i=0; i<RADARMAP_PIXEL_FIXEDWIDTH*RADARMAP_PIXEL_FIXEDHEIGHT; i++){
            if ((color[i]==-4342339)||(color[i]==-1)){
                color[i]=Color.TRANSPARENT;
            }
        }
        return color;
    }

    public static int[] getPixels(InputStream inputStream){
        try {
            int[] color = getPixels(BitmapFactory.decodeStream(inputStream));
            return color;
        } catch (Exception e){
            // nothing to do
        }
        return null;
    }

    public static int[] getData(Context context, int count){
        if (APIReaders.RadarMNSetGeoserverRunnable.radarCacheFileExists(context,count)){
            int[] color = getPixels();
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inMutable = true;
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeFile(APIReaders.RadarMNSetGeoserverRunnable.getRadarMNFile(context,count).getAbsolutePath().toString(),bitmapOptions);
            bitmap.getPixels(color,0,RADARMAP_PIXEL_FIXEDWIDTH,0,0,RADARMAP_PIXEL_FIXEDWIDTH,RADARMAP_PIXEL_FIXEDHEIGHT);
            for (int i=0; i<RADARMAP_PIXEL_FIXEDWIDTH*RADARMAP_PIXEL_FIXEDHEIGHT; i++){
                if ((color[i]==-4342339)||(color[i]==-1)){
                    color[i]=Color.TRANSPARENT;
                }
            }
            return color;
        }
        return null;
    }

    /*
    public RadarMN(Context context){
        if (APIReaders.RadarMNGeoserverRunnable.radarCacheFileExists(context)){
            color = new int[RADARMAP_PIXEL_FIXEDWIDTH*RADARMAP_PIXEL_FIXEDHEIGHT];
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inMutable = true;
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeFile(APIReaders.RadarMNGeoserverRunnable.getRadarMNFile(context).getAbsolutePath().toString(),bitmapOptions);
            Canvas canvas = new Canvas(bitmap);
            final Paint transparentPaint = new Paint();
            transparentPaint.setColor(Color.TRANSPARENT);
            transparentPaint.setAntiAlias(true);
            for (int x=0; x<RADARMAP_PIXEL_FIXEDWIDTH; x++){
                for (int y=0; y<RADARMAP_PIXEL_FIXEDHEIGHT; y++){
                    int i = bitmap.getPixel(x,y);
                    color[x + y*RADARMAP_PIXEL_FIXEDWIDTH] = i;
                    if ((i==-4342339)||(i==-1)){
                        color[x + y*RADARMAP_PIXEL_FIXEDWIDTH] = Color.TRANSPARENT;
                        canvas.drawPoint(x,y,transparentPaint);
                    }
                }
            }
        }
    }

    public RadarMN(Context context, int count){
        if (APIReaders.RadarMNSetGeoserverRunnable.radarCacheFileExists(context,count)){
            color = new int[RADARMAP_PIXEL_FIXEDWIDTH*RADARMAP_PIXEL_FIXEDHEIGHT];
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inMutable = true;
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeFile(APIReaders.RadarMNSetGeoserverRunnable.getRadarMNFile(context,count).getAbsolutePath().toString(),bitmapOptions);
            bitmap.getPixels(color,0,RADARMAP_PIXEL_FIXEDWIDTH,0,0,RADARMAP_PIXEL_FIXEDWIDTH,RADARMAP_PIXEL_FIXEDHEIGHT);
            for (int i=0; i<RADARMAP_PIXEL_FIXEDWIDTH*RADARMAP_PIXEL_FIXEDHEIGHT; i++){
                if ((color[i]==-4342339)||(color[i]==-1)){
                    color[i]=Color.TRANSPARENT;
                }
            }

        }
    }

 */


    /*
    public void drawDebugBorders(){
        Canvas canvas = new Canvas(bitmap);
        final Paint borderPaint = new Paint();
        borderPaint.setColor(Color.RED);
        for (int x=0; x<RADARMAP_PIXEL_FIXEDWIDTH; x++){
            for (int y=0; y<RADARMAP_PIXEL_FIXEDHEIGHT; y++){
                canvas.drawPoint(x,0,borderPaint);
                canvas.drawPoint(0,y,borderPaint);
                canvas.drawPoint(RADARMAP_PIXEL_FIXEDWIDTH-1,y,borderPaint);
                canvas.drawPoint(x,RADARMAP_PIXEL_FIXEDHEIGHT-1,borderPaint);
                color[x][0] = Color.RED;
                color[0][y] = Color.RED;
                color[RADARMAP_PIXEL_FIXEDWIDTH-1][y] = Color.RED;
                color[x][RADARMAP_PIXEL_FIXEDHEIGHT-1] = Color.RED;
            }
        }
    }

     */

}
