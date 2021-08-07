package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.graphics.*;

public class RadarMN {

    final static int RADAR_DATAINTERVAL = 1000*60*10;

    // represents the 1100x1200 grid in colors already populated correctly by the geoserver
    int[][] color = null;
    Bitmap bitmap;

    // some data constants
    public static final int RADARMAP_PIXEL_FIXEDWIDTH  = 1100;
    public static final int RADARMAP_PIXEL_FIXEDHEIGHT = 1200;

    private static final float GEO_X_NORTH_WEST = 1.435612143f;
    private static final float GEO_Y_NORTH_WEST =  55.865842289f;

    private static final float GEO_X_NORTH_EAST = 18.76728172f;
    private static final float GEO_Y_NORTH_EAST = 55.84848692f;

    private static final float GEO_X_SOUTH_WEST = 3.551921296f;
    private static final float GEO_Y_SOUTH_WEST = 45.69587068f;

    private static final float GEO_X_SOUTH_EAST = 16.60186543f;
    private static final float GEO_Y_SOUTH_EAST = 45.68358331f;

    public float getGeoWidth(int y){
        float left = ((GEO_X_SOUTH_WEST - GEO_X_NORTH_WEST)/RADARMAP_PIXEL_FIXEDHEIGHT)*y;
        float right = ((GEO_X_SOUTH_EAST - GEO_X_NORTH_EAST)/RADARMAP_PIXEL_FIXEDHEIGHT)*y;
        float baseline = GEO_X_SOUTH_EAST - GEO_X_SOUTH_WEST;
        float geoWidth = baseline + left - right;
        return geoWidth;
    }

    public float getGeoHeight(int x){
        float top =    ((GEO_Y_NORTH_WEST - GEO_Y_NORTH_EAST)/RADARMAP_PIXEL_FIXEDWIDTH)*x;
        float bottom = ((GEO_Y_SOUTH_EAST - GEO_Y_SOUTH_WEST)/RADARMAP_PIXEL_FIXEDWIDTH)*x;
        float baseline = GEO_Y_NORTH_WEST - GEO_Y_SOUTH_WEST;
        float geoHeight = baseline + top + bottom;
        return geoHeight;
    }

    public float getGeoXRowStart(int y){
        float left = ((GEO_X_SOUTH_WEST - GEO_X_NORTH_WEST)/RADARMAP_PIXEL_FIXEDHEIGHT)*y;
        float baseline = GEO_X_SOUTH_EAST - GEO_X_SOUTH_WEST;
        float geoXRowStart = GEO_X_SOUTH_WEST - left;
        return geoXRowStart;
    }

    public float getGeoX(int x, int y){
        float a = getGeoXRowStart(y);
        float b = (getGeoWidth(y)/RADARMAP_PIXEL_FIXEDWIDTH)*x;
        float geoX = a+b;
        return  geoX;
    }

    public float getGeoYStartTop(int x){
        final float GEO_Y_TOP_DIFF = GEO_Y_NORTH_EAST - GEO_Y_NORTH_WEST;
        float difference = (GEO_Y_TOP_DIFF/RADARMAP_PIXEL_FIXEDWIDTH) * x;
        float offset = GEO_Y_NORTH_WEST + difference;
        return offset;
    }

    public float getGeoY(int x, int y){
        float a = getGeoYStartTop(x);
        float height = getGeoHeight(x);
        float geoY = a - (height/RADARMAP_PIXEL_FIXEDHEIGHT)*y;
        return geoY;
    }

    public RadarMN(Context context){
        if (APIReaders.RadarMNGeoserverRunnable.radarCacheFileExists(context)){
            color = new int[RADARMAP_PIXEL_FIXEDWIDTH][RADARMAP_PIXEL_FIXEDHEIGHT];
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inMutable = true;
            bitmap = BitmapFactory.decodeFile(APIReaders.RadarMNGeoserverRunnable.getRadarMNFile(context).getAbsolutePath().toString(),bitmapOptions);
            Canvas canvas = new Canvas(bitmap);
            final Paint transparentPaint = new Paint();
            transparentPaint.setColor(Color.TRANSPARENT);
            transparentPaint.setAntiAlias(true);
            for (int x=0; x<RADARMAP_PIXEL_FIXEDWIDTH; x++){
                for (int y=0; y<RADARMAP_PIXEL_FIXEDHEIGHT; y++){
                    int i = bitmap.getPixel(x,y);
                    color[x][y] = i;
                    if ((i==-4342339)||(i==-1)){
                        color[x][y] = Color.TRANSPARENT;
                        canvas.drawPoint(x,y,transparentPaint);
                    }
                }
            }
        }
    }

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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean hasData(){
        return color != null;
    }
}
