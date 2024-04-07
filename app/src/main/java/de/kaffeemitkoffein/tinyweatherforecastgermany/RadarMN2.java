package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/*
 * This is the interface to get a rain radar image in Mercator (EPSG 3857) projection that fits to the static
 * target osm map, also in EPSG 3857 aka Mercator.
 *
 * The map y (latitude) is cropped at 85.1° and -85.1° like in osm.
 *
 * DIMENSIONS OF TARGET MAP
 * ===============================
 * => is the rain radar composite borders (determined by try-out)
 *
 * EPSG:3857 = Mercator (in brackets corresponding EPSG:4326 = EGS84 gps coordinates)
 * -----------------------------------------------------------
 * South-West corner:                      x=493000.00  (4.4286943°)  longitude   y=5861000.00 (46.5009905°) latitude
 * North-East corner:                      x=1791000.00 (16.0888267°) longitude   y=7470000.00 (55.5533029°) latitude
 *
 */

public class RadarMN2 {

    public static final int WIDTH_SCALE2 = 1079;

    public static final int getScaleFactor(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int scaleFactor=1;
        if (displayMetrics.widthPixels>WIDTH_SCALE2){
            scaleFactor=2;
        }
        if (WeatherSettings.forceMapHighResolution(context)){
            scaleFactor=2;
        }
        return scaleFactor;
    }

    public static final int getTrueScaleFactor(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int scaleFactor=1;
        if (displayMetrics.widthPixels>WIDTH_SCALE2){
            scaleFactor=2;
        }
        return scaleFactor;
    }

    public static int getMapResource(Context context){
        int scaleFactor = getScaleFactor(context);
        if (scaleFactor==2){
            return R.drawable.germany2_scale2;
        }
        return R.drawable.germany2_scale1;
    }

    public static int getFixedRadarMapWidth(Context context){
        return getScaleFactor(context)*1108;
    }

    public static int getFixedRadarMapHeight(Context context){
        return getScaleFactor(context)*1360;
    }

    public static class MercatorProjection{

        /*
         * This class defines the mercator projection of coordinates to pixels and vice versa.
         * The only custom parameter is the map width.
         * The y-axis is converted to the display with (0,0) in the upper left corner.
         */

        // custom map width in pixels.
        double mapWidth;
        double heightOsm; // is calculated from the mapWidth.

        private MercatorProjection(){
        }

        public MercatorProjection(double width){
            this.mapWidth = width;
            this.heightOsm = getYraw(85.1)*2;
        }

        public double getX(double xcoord){
            return ( mapWidth / (2*Math.PI) ) * ( Math.toRadians(xcoord) - (Math.toRadians(-180)) );
        }

        public double getYraw(double ycoord){
            return ( mapWidth/ (2*Math.PI)) * Math.log( Math.tan( (Math.PI/4) + Math.toRadians(ycoord)/2) );
        }

        public double getY(double ycoord){
            return (heightOsm/2) - ( mapWidth/ (2*Math.PI)) * Math.log( Math.tan( (Math.PI/4) + Math.toRadians(ycoord)/2) );
        }

        public double getXCoord(double x){
            return (-180) + Math.toDegrees( x / (mapWidth/(2*Math.PI)) );
        }

        public double getYCoord(double y){
            return Math.toDegrees( 2 * Math.atan( Math.exp( ((heightOsm/2)-y)/(mapWidth/(2*Math.PI)) ) ) - (Math.PI/2) );
        }
    }

    public static class MercatorProjectionTile extends MercatorProjection {

        /*
         * This class extends the MercatorProjection to display a frame inside the whole map.
         */

        double x0coord; double y0coord; double x1coord; double y1coord;
        private final double xOffsetPixel;
        private final double yOffsetPixel;

        public MercatorProjectionTile(double widthPixels, double x0coord, double y0coord, double x1coord, double y1coord) {
            mapWidth = ( 360/Math.abs((x1coord - x0coord)) ) * widthPixels;
            heightOsm = getYraw(85.1)*2;
            this.x0coord = x0coord; this.y0coord = y0coord; this.x1coord=x1coord; this.y1coord = y1coord;
            this.xOffsetPixel = getX(x0coord);
            this.yOffsetPixel = getY(y1coord);
        }

        public double getXPixel(double xcoord){
            return getX(xcoord) -xOffsetPixel;
        }

        public double getYPixel(double ycoord){
            return getY(ycoord) - yOffsetPixel;
        }

        @Override
        public double getXCoord(double x){
            return super.getXCoord(x + xOffsetPixel);
        }

        @Override
        public double getYCoord(double y){
            return super.getYCoord(y+yOffsetPixel);
        }

    }

    public static MercatorProjectionTile getRadarMapMercatorProjectionTile(Context context){
        MercatorProjectionTile mercatorProjectionTile = new MercatorProjectionTile(getFixedRadarMapWidth(context),
                4.4286943,46.5009905,16.0888267,55.5533029);
        return mercatorProjectionTile;
    }

    // this is the bbox corresponding to the specs above
    public static final String BBOX = "bbox=493000.00%2C5861000.00%2C1791000.00%2C7470000.00";

    public static int[] getPixels(Context context){
        return new int[getFixedRadarMapWidth(context)*getFixedRadarMapHeight(context)];
    }

    public static Bitmap getBitmap(Context context, int count){
        if (APIReaders.RadarMNSetGeoserverRunnable.radarCacheFileExists(context,count)){
            int[] color = getPixels(context);
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inMutable = true;
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeFile(APIReaders.RadarMNSetGeoserverRunnable.getRadarMNFile(context,count).getAbsolutePath().toString(),bitmapOptions);
            bitmap.getPixels(color,0,getFixedRadarMapWidth(context),0,0,getFixedRadarMapWidth(context),getFixedRadarMapHeight(context));
            for (int i=0; i<getFixedRadarMapWidth(context)*getFixedRadarMapHeight(context); i++){
                if ((color[i]==-4342339)){
                    color[i]=Color.TRANSPARENT;
                }
                if ((color[i]==-1)){
                    color[i]=Color.TRANSPARENT;
                }

            }
            bitmap.setPixels(color,0,getFixedRadarMapWidth(context),0,0,getFixedRadarMapWidth(context),getFixedRadarMapHeight(context));
            return bitmap;
        }
        return null;
    }

    public static Bitmap getScaledBitmap(Context context, int count){
        return getBitmap(context,count);
    }
}
