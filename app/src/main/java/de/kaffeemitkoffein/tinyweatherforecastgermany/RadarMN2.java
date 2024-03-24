package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.InputStream;

/*
 * This is the interface to get a rain radar image in mercator (EPSG 3857) projection that fits to the static
 * target map.
 *
 * The target map has the following dimensions:
 *
 * EPSG:4326 = WGS84 (gps coordinates):
 * ----------------------------------------
 * North-West corner: x=3.45 y=55.80
 * South-East corner: x=15.85 y=46.96
 *
 * EPSG:3857 = Mercator:
 * ---------------------
 * North-West corner: x=384052.24323679385 y=7518703.900398301
 * South-East corner: x=1764413.9290733861 y=5935547.496579564
 */

public class RadarMN2 {

    // dimensions of the 1100x1200 MN grid

    public static final int RADARMAP_PIXEL_FIXEDWIDTH  = 1100;
    public static final int RADARMAP_PIXEL_FIXEDHEIGHT = 1200;

    // this is the bbox corresponding to the specs above
    public static final String BBOX = "bbox=384052.24323679385%2C5935547.496579564%2C1764413.9290733861%2C7518703.900398301";

    public static int[] getPixels(){
        return new int[RADARMAP_PIXEL_FIXEDWIDTH*RADARMAP_PIXEL_FIXEDHEIGHT];
    }

    public static int[] getPixels(Bitmap bitmap){
        int[] color =  getPixels();
        bitmap.getPixels(color,0,RADARMAP_PIXEL_FIXEDWIDTH,0,0,RADARMAP_PIXEL_FIXEDWIDTH,RADARMAP_PIXEL_FIXEDHEIGHT);
        for (int i=0; i<RADARMAP_PIXEL_FIXEDWIDTH*RADARMAP_PIXEL_FIXEDHEIGHT; i++){
            if ((color[i]==-4342339)||(color[i]==-1)){
                color[i]= Color.TRANSPARENT;
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

    public static Bitmap getBitmap(Context context, int count){
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
            bitmap.setPixels(color,0,RADARMAP_PIXEL_FIXEDWIDTH,0,0,RADARMAP_PIXEL_FIXEDWIDTH,RADARMAP_PIXEL_FIXEDHEIGHT);
            return bitmap;
        }
        return null;
    }

    public static Bitmap getScaledBitmap(Context context, int count){
        Bitmap rawBitmap = getBitmap(context,count);
        if (rawBitmap!=null){
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(rawBitmap,WeatherWarningActivity.MAP_PIXEL_FIXEDWIDTH,WeatherWarningActivity.MAP_PIXEL_FIXEDHEIGHT,true);
            return scaledBitmap;
        }
        return null;
    }

}
