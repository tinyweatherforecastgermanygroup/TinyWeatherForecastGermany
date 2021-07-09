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

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WeatherWarningActivity extends Activity {

    ArrayList<WeatherWarning> weatherWarnings;
    ArrayList<WeatherWarning> localWarnings;
    Weather.WeatherLocation localStation;
    View.OnTouchListener mapTouchListener;
    ArrayList<Polygon> polygoncache;
    ArrayList<Polygon> excluded_polygoncache;
    ImageView germany;
    RelativeLayout mapcontainer;
    Bitmap germanyBitmap;
    Bitmap warningsBitmap;
    Bitmap radarBitmap;
    Bitmap visibleBitmap;
    ZoomableImageView mapZoomable;
    RelativeLayout map_collapsed_container;
    boolean deviceIsLandscape;
    private GestureDetector gestureDetector;
    ListView weatherList;
    WeatherWarningAdapter weatherWarningAdapter;
    ActionBar actionBar;
    Executor executor;
    Boolean hide_rain = null;
    Radarmap radarmap;

    Bundle zoomMapState = null;

    static float MAP_PIXEL_WIDTH;
    static float MAP_PIXEL_HEIGHT;

    public final static String WEATHER_WARNINGS_UPDATE="WEATHER_WARNINGS_UPDATE";
    public final static String WEATHER_WARNINGS_UPDATE_RESULT="WEATHER_WARNINGS_UPDATE_RESULT";

    public final static String SIS_ZOOMMAPSTATEBUNDLE="ZOOMMAPSTATEBUNDLE";
    public final static String SIS_HIDERAIN="HIDERAIN";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                // update warning display if warnings have been updated
                if (intent.getAction().equals(WEATHER_WARNINGS_UPDATE)) {
                    weatherWarnings = WeatherWarnings.getCurrentWarnings(getApplicationContext(),true);
                    displayWarnings();
                    updateActionBarLabels();
                    hideProgressBar();
                }
                if (intent.hasExtra(WEATHER_WARNINGS_UPDATE_RESULT)){
                    // gets result if update was successful, currently not used
                    boolean updateResult = intent.getBooleanExtra(WEATHER_WARNINGS_UPDATE_RESULT,false);
                }
                if (intent.getAction().equals(DataUpdateService.HIDE_PROGRESS)){
                    hideProgressBar();
                }
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle state){
        if (mapZoomable != null){
            zoomMapState = mapZoomable.saveZoomViewState();
        }
        if (zoomMapState!=null){
            state.putBundle(SIS_ZOOMMAPSTATEBUNDLE,zoomMapState);
        }
        state.putBoolean(SIS_HIDERAIN,hide_rain);
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state){
        super.onRestoreInstanceState(state);
        // do nothing here for the moment
    }

    @Override
    protected void onResume() {
        registerForBroadcast();
        if (germany==null){
            germany = (ImageView) findViewById(R.id.warningactivity_map);
        }
        updateWarningsIfNeeded();
        super.onResume();
    }

    @Override
    protected void onPause(){
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weatherwarning);
        registerForBroadcast();
        // try to restore zoom factor if it is available from the savedInstanceState
        if (savedInstanceState!=null){
            Bundle bundle = savedInstanceState.getBundle(SIS_ZOOMMAPSTATEBUNDLE);
            if (bundle!=null){
                zoomMapState = bundle;
            }
            hide_rain = savedInstanceState.getBoolean(SIS_HIDERAIN,!WeatherSettings.showRadarByDefault(getApplicationContext()));
        } else {
            hide_rain = !WeatherSettings.showRadarByDefault(getApplicationContext());
        }
        executor = Executors.newSingleThreadExecutor();
        localStation = WeatherSettings.getSetStationLocation(getApplicationContext());
        // action bar layout
        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        weatherWarnings = WeatherWarnings.getCurrentWarnings(getApplicationContext(),true);
        mapcontainer = (RelativeLayout) findViewById(R.id.warningactivity_mapcontainer);
        map_collapsed_container = (RelativeLayout) findViewById(R.id.warningactivity_map_collapsed_container);
        // in layout w6600dp-land this element does not exist. This is the safest way to
        // limit collapse-function to portrait mode.
        if (map_collapsed_container!=null){
            deviceIsLandscape = false;
            map_collapsed_container.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (germany!=null){
                        mapcontainer.setVisibility(View.VISIBLE);
                        mapcontainer.invalidate();
                        map_collapsed_container.setVisibility(View.GONE);
                        map_collapsed_container.invalidate();
                        LinearLayout.LayoutParams lop = (LinearLayout.LayoutParams) weatherList.getLayoutParams();
                        lop.height=0;
                        lop.weight=6;
                        weatherList.setLayoutParams(lop);
                        weatherList.invalidate();
                    }
                    return true;
                }
            });
        } else {
            deviceIsLandscape = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.weatherwarning_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int item_id = mi.getItemId();
        if (item_id == R.id.menu_refresh) {
            if (mapZoomable!=null){
                zoomMapState = mapZoomable.saveZoomViewState();
            }
            PrivateLog.log(this, Tag.MAIN, "starting update of weather warnings");
            if (UpdateAlarmManager.updateWarnings(getApplicationContext(),true)){
                // returns true if update service was launched sucessfully
               showProgressBar();
            }
            return true;
        }
        if (item_id==R.id.hide_rain) {
            hide_rain = !hide_rain;
            drawMapBitmap(radarmap);
            return true;
        }
        return super.onOptionsItemSelected(mi);
    }

    public void updateActionBarLabels(){
        WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        String update = simpleDateFormat.format(weatherSettings.getWarningsLastUpdateTime());
        if (weatherWarnings!=null){
            //actionBar.setSubtitle(getApplicationContext().getResources().getString(R.string.warnings)+": "+update+" ("+weatherWarnings.size()+")");
            actionBar.setSubtitle(update+" ("+weatherWarnings.size()+")");
        } else {
            actionBar.setSubtitle(getApplicationContext().getResources().getString(R.string.warnings_update_fail));
        }
    }

    private void displayWarnings(){
        if (weatherWarnings!=null){
            updateActionBarLabels();
            TextView noWarnings = (TextView) findViewById(R.id.warningactivity_no_warnings);
            if (weatherWarnings.size()==0){
                noWarnings.setVisibility(View.VISIBLE);
            } else {
                noWarnings.setVisibility(View.GONE);
            }
            TextView warningsDeprecated = (TextView) findViewById(R.id.warningactivity_warnings_deprecated);
            if (WeatherSettings.areWarningsOutdated(getApplicationContext())){
                warningsDeprecated.setVisibility(View.VISIBLE);
            } else {
                warningsDeprecated.setVisibility(View.GONE);
            }
        }
        WeatherWarnings.getWarningsForLocationRunnable getWarningsForLocationRunnable = new WeatherWarnings.getWarningsForLocationRunnable(getApplicationContext(),null,null) {
            @Override
            public void onResult(ArrayList<WeatherWarning> result) {
                localWarnings = result;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weatherList = (ListView) findViewById(R.id.warningactivity_listview);
                        weatherWarningAdapter = new WeatherWarningAdapter(getApplicationContext(),weatherWarnings,executor);
                        weatherWarningAdapter.setLocalWarnings(localWarnings);
                        weatherList.setAdapter(weatherWarningAdapter);
                    }
                });
            }
        };
        executor.execute(getWarningsForLocationRunnable);
        if (weatherWarnings!=null){
            displayMap();
        }
    }

    private void updateWarningsIfNeeded(){
        if (WeatherSettings.areWarningsOutdated(getApplicationContext())){
            PrivateLog.log(getApplicationContext(),Tag.WARNINGS,"Warnings outdated, getting new ones.");
            UpdateAlarmManager.updateWarnings(getApplicationContext(),false);
        } else {
            PrivateLog.log(getApplicationContext(),Tag.WARNINGS,"Warnings not outdated, recycling.");
            germany.post(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayWarnings();
                        }
                    });
                }
            });
            updateActionBarLabels();
        }
    }

    public class PlotPoint{
        float x;
        float y;
    }

    private static final int MAP_PIXEL_FIXEDWIDTH  = 824;
    private static final int MAP_PIXEL_FIXEDHEIGHT = 956;

    private static final float MAP_GEO_TOP = 55.80f;
    private static final float MAP_GEO_BOTTOM = 46.96f;
    private static final float MAP_GEO_HEIGHT= MAP_GEO_TOP - MAP_GEO_BOTTOM;
    private static final float MAP_GEO_OFFSETX_TOP = 3.45f;
    private static final float MAP_GEO_ENDX_TOP = 16.9f;
    private static final float MAP_GEO_OFFSETX_BOTTOM = 4.65f;
    private static final float MAP_GEO_ENDX_BOTTOM = 15.85f;

    private static final float MAP_GEO_WIDTH_TOP=MAP_GEO_ENDX_TOP - MAP_GEO_OFFSETX_TOP;
    private static final float MAP_GEO_WIDTH_BOTTOM= MAP_GEO_ENDX_BOTTOM - MAP_GEO_OFFSETX_BOTTOM;
    private static final float MAP_GEO_WIDTH_DELTA = MAP_GEO_WIDTH_TOP - MAP_GEO_WIDTH_BOTTOM;

    private static final float[] MAP_THRESHOLD ={0,0.16f,0.20f,0.23f,0.30f,0.38f,0.45f,0.62f,0.70f,0.77f,0.80f,0.84f};
    private static final float[] MAP_CORRECTION={7,6    ,5    ,4    ,3    ,1    ,0    ,1    ,2    ,3    ,6    ,7    };

    private static float yCorrectionPixels(float lon, float lat){
        float p = (lon - getXOffsetGeo(lat))/getGeoWidth(lat);
        float c=MAP_CORRECTION[0];
        for (int i=1; i< MAP_THRESHOLD.length;i++){
            if (p>MAP_THRESHOLD[i]){
                c=MAP_CORRECTION[i];
            }
        }
        return (c/956)*MAP_PIXEL_HEIGHT;
    }

    private static float getGeoWidth(float geo_height){
        float geowidth = (MAP_GEO_WIDTH_DELTA/MAP_GEO_HEIGHT) * (geo_height-MAP_GEO_BOTTOM) + MAP_GEO_WIDTH_BOTTOM;
        return geowidth;
    }

    private static float getXOffsetGeo(float geo_height){
        float xDeltaGeo = ((MAP_GEO_OFFSETX_BOTTOM - MAP_GEO_OFFSETX_TOP)/(MAP_GEO_TOP - MAP_GEO_BOTTOM) * (geo_height - MAP_GEO_BOTTOM));
        float xOffsetGeo = MAP_GEO_OFFSETX_BOTTOM - xDeltaGeo;
        return xOffsetGeo;
    }

    private PlotPoint getPlotPoint(float lon, float lat){
        float x = (lon - getXOffsetGeo(lat)) * (MAP_PIXEL_WIDTH/getGeoWidth(lat));
        float y = (lat - MAP_GEO_BOTTOM)*(MAP_PIXEL_HEIGHT/MAP_GEO_HEIGHT) + yCorrectionPixels(lon,lat);
        PlotPoint plotPoint = new PlotPoint();
        plotPoint.y = MAP_PIXEL_HEIGHT - y;
        plotPoint.x = x;
        return plotPoint;
    }

    private float getXGeo(PlotPoint plotPoint){
        float p = (plotPoint.x/MAP_PIXEL_WIDTH);
        float c = MAP_CORRECTION[0];
        for (int i=1; i<MAP_THRESHOLD.length; i++ ){
            if (p>MAP_THRESHOLD[i]){
                c = MAP_CORRECTION[i];
            }
        }
        float yPixCorr = (c/956)*(MAP_GEO_TOP-MAP_GEO_BOTTOM);
        float geoy = ((MAP_GEO_TOP-MAP_GEO_BOTTOM)/(MAP_PIXEL_HEIGHT)) * (MAP_PIXEL_HEIGHT-plotPoint.y) + MAP_GEO_BOTTOM - yPixCorr;
        float geox = getXOffsetGeo(geoy) + (getGeoWidth(geoy)/MAP_PIXEL_WIDTH) * plotPoint.x;
        return geox;
    }

    private float getYGeo(PlotPoint plotPoint){
        float p = (plotPoint.x/MAP_PIXEL_WIDTH);
        float c = MAP_CORRECTION[0];
        for (int i=1; i<MAP_THRESHOLD.length; i++ ){
            if (p>MAP_THRESHOLD[i]){
                c = MAP_CORRECTION[i];
            }
        }
        float yPixCorr = (c/956)*(MAP_GEO_TOP-MAP_GEO_BOTTOM);
        float geoy = ((MAP_GEO_TOP-MAP_GEO_BOTTOM)/(MAP_PIXEL_HEIGHT)) * (MAP_PIXEL_HEIGHT-plotPoint.y) + MAP_GEO_BOTTOM - yPixCorr;
        float geox = getXOffsetGeo(geoy) + (getGeoWidth(geoy)/MAP_PIXEL_WIDTH) * plotPoint.x;
        return geoy;
    }

    private void drawPin(Canvas canvas){
        int pinsize = 42;
        Paint pinpaint = new Paint();
        pinpaint.setColor(Color.BLUE);
        pinpaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pinpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        PlotPoint pinPoint = getPlotPoint((float) localStation.longitude, (float) localStation.latitude);
        Bitmap pinBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.pin),pinsize,pinsize,false);
        canvas.drawBitmap(pinBitmap,pinPoint.x,pinPoint.y-pinBitmap.getHeight(),pinpaint);
    }

    private void drawStrokedText(Canvas canvas, String text, float x, float y, Paint paint){
        Paint strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        strokePaint.setTypeface(Typeface.DEFAULT);
        strokePaint.setTextSize(paint.getTextSize());
        strokePaint.setAntiAlias(true);
        int shiftX = Math.max(2,germany.getWidth()/MAP_PIXEL_FIXEDWIDTH);
        int shiftY = Math.max(2,germany.getHeight()/MAP_PIXEL_FIXEDHEIGHT);
        canvas.drawText(text,x-shiftX,y,strokePaint);
        canvas.drawText(text,x+shiftX,y,strokePaint);
        canvas.drawText(text,x,y-shiftY,strokePaint);
        canvas.drawText(text,x,y+shiftY,strokePaint);
        canvas.drawText(text,x,y,paint);
    }

    private void showRainDescription(Radarmap radarmap){
        Bitmap infoBitmap=Bitmap.createBitmap(Math.round(MAP_PIXEL_WIDTH),Math.round(MAP_PIXEL_HEIGHT*0.12f), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(infoBitmap);
        Bitmap radarinfobarResourceBitmap;
        radarinfobarResourceBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.radarinfobar),Math.round(MAP_PIXEL_WIDTH),34,false);
        Paint rpaint = new Paint();
        rpaint.setStyle(Paint.Style.FILL);
        canvas.drawBitmap(radarinfobarResourceBitmap,0,infoBitmap.getHeight()-radarinfobarResourceBitmap.getHeight(),rpaint);
        Paint radarTextPaint = new Paint();
        radarTextPaint.setTypeface(Typeface.DEFAULT);
        radarTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        radarTextPaint.setAntiAlias(true);
        radarTextPaint.setFakeBoldText(true);
        int textsize = radarinfobarResourceBitmap.getHeight();
        radarTextPaint.setTextSize(textsize);
        radarTextPaint.setColor(Color.WHITE);
        if (WeatherSettings.isRadarDataOutdated(getApplicationContext())){
            radarTextPaint.setColor(Color.RED);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String radartime = simpleDateFormat.format(new Date(radarmap.timestamp));
        float ff=1.1f;
        drawStrokedText(canvas,radartime,MAP_PIXEL_WIDTH/100,infoBitmap.getHeight()-radarinfobarResourceBitmap.getHeight()*ff*2,radarTextPaint);
        radarTextPaint.setColor(Radarmap.RAINCOLORS[2]);
        drawStrokedText(canvas,getResources().getString(R.string.radar_rain1),MAP_PIXEL_WIDTH*0.1f,infoBitmap.getHeight()-radarinfobarResourceBitmap.getHeight()*ff,radarTextPaint);
        radarTextPaint.setColor(Radarmap.RAINCOLORS[7]);
        drawStrokedText(canvas,getResources().getString(R.string.radar_rain2),MAP_PIXEL_WIDTH*0.3f,infoBitmap.getHeight()-radarinfobarResourceBitmap.getHeight(),radarTextPaint);
        radarTextPaint.setColor(Radarmap.RAINCOLORS[11]);
        drawStrokedText(canvas,getResources().getString(R.string.radar_rain3),MAP_PIXEL_WIDTH*0.6f,infoBitmap.getHeight()-radarinfobarResourceBitmap.getHeight(),radarTextPaint);
        radarTextPaint.setColor(Radarmap.RAINCOLORS[16]);
        drawStrokedText(canvas,getResources().getString(R.string.radar_rain4),MAP_PIXEL_WIDTH*0.8f,infoBitmap.getHeight()-radarinfobarResourceBitmap.getHeight(),radarTextPaint);
        ImageView rainDescription = (ImageView) findViewById(R.id.warningactivity_mapinfo);
        if (rainDescription!=null){
            rainDescription.setImageBitmap(infoBitmap);
        }
    }

    private void clearRainDescription(){
        ImageView rainDescription = (ImageView) findViewById(R.id.warningactivity_mapinfo);
        if (rainDescription!=null){
            rainDescription.setImageDrawable(null);
        }
    }

    private Bitmap loadBitmapMap(int res_id){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),res_id),MAP_PIXEL_FIXEDWIDTH,MAP_PIXEL_FIXEDHEIGHT,false);
        return bitmap;
    }

    private void drawMapBitmap(Radarmap radarmap){
        //visibleBitmap = Bitmap.createBitmap(mapBitmap.getHeight(),mapBitmap.getWidth(), Bitmap.Config.ARGB_8888);
        visibleBitmap = warningsBitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(visibleBitmap);
        if ((!hide_rain) && (radarBitmap!=null)){
            final Paint cp = new Paint();
            cp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            canvas.drawBitmap(radarBitmap, 0,0,cp);
            showRainDescription(radarmap);
        } else {
            clearRainDescription();
        }
        //drawPin(canvas);
        mapZoomable.updateBitmap(visibleBitmap);
        visibleBitmap.recycle();
    }

    private void drawWindIcon(){
        final Context context = getApplicationContext();
        final ImageView windicon = (ImageView) findViewById(R.id.warningactivity_windicon);
        Runnable windRunnable = new Runnable() {
            @Override
            public void run() {
                if (windicon!=null){
                    if (WeatherSettings.displayWindInRadar(getApplicationContext())){
                        windicon.setVisibility(View.VISIBLE);
                        CurrentWeatherInfo currentWeatherInfo = new Weather().getCurrentWeatherInfo(context);
                        if (currentWeatherInfo!=null){
                            final Bitmap windiconBitmap = currentWeatherInfo.currentWeather.getWindSymbol(getApplicationContext(),WeatherSettings.getWindDisplayType(getApplicationContext()));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    windicon.setImageBitmap(windiconBitmap);
                                }
                            });

                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                windicon.setVisibility(View.INVISIBLE);
                            }
                        });

                    }
                }
            }
        };
        executor.execute(windRunnable);
    }

    @SuppressWarnings("unchecked")
    private void displayMap(){
        if (deviceIsLandscape){
            germanyBitmap = loadBitmapMap(R.drawable.germany_nc);
        } else {
            germanyBitmap = loadBitmapMap(R.drawable.germany);
        }
        warningsBitmap = Bitmap.createBitmap(germanyBitmap.getWidth(),germanyBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        warningsBitmap.eraseColor(Color.TRANSPARENT);
        radarBitmap = Bitmap.createBitmap(germanyBitmap.getWidth(),germanyBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        radarBitmap.eraseColor(Color.TRANSPARENT);
        MAP_PIXEL_HEIGHT = warningsBitmap.getHeight();
        MAP_PIXEL_WIDTH  = warningsBitmap.getWidth();
        polygoncache = new ArrayList<Polygon>();
        excluded_polygoncache = new ArrayList<Polygon>();
        final Canvas canvas = new Canvas(warningsBitmap);
        ArrayList<WeatherWarning> drawWarnings = (ArrayList<WeatherWarning>) weatherWarnings.clone();
        Collections.sort(drawWarnings);
        Collections.reverse(drawWarnings);
        for (int warning_counter=0; warning_counter<drawWarnings.size(); warning_counter++){
            WeatherWarning warning = drawWarnings.get(warning_counter);
            for (int polygon_counter=0; polygon_counter<warning.polygonlist.size(); polygon_counter++){
                float[] polygonX = warning.polygonlist.get(polygon_counter).polygonX;
                float[] polygonY = warning.polygonlist.get(polygon_counter).polygonY;
                // add polygon to cache
                polygoncache.add(new Polygon(polygonX,polygonY,warning.identifier));
                if (polygonX.length>0){
                    Path path = new Path();
                    PlotPoint plotPoint = getPlotPoint(polygonX[0],polygonY[0]);
                    path.moveTo(plotPoint.x, plotPoint.y);
                    for (int vertex_count=1; vertex_count<polygonX.length; vertex_count++){
                        plotPoint = getPlotPoint(polygonX[vertex_count],polygonY[vertex_count]);
                        path.lineTo(plotPoint.x, plotPoint.y);
                    }
                    Paint polypaint = new Paint();
                    polypaint.setColor(warning.getWarningColor());
                    polypaint.setAlpha(128);
                    polypaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    canvas.drawPath(path,polypaint);
                }
            }
            // draw black excluded polygons after other polygons were drawn
            for (int polygon_counter=0; polygon_counter<warning.excluded_polygonlist.size(); polygon_counter++){
                float[] polygonX = warning.excluded_polygonlist.get(polygon_counter).polygonX;
                float[] polygonY = warning.excluded_polygonlist.get(polygon_counter).polygonY;
                // add excluded-polygon to cache
                excluded_polygoncache.add(new Polygon(polygonX,polygonY,warning.identifier));
                if (polygonX.length>0){
                    Path path = new Path();
                    PlotPoint plotPoint = getPlotPoint(polygonX[0],polygonY[0]);
                    path.moveTo(plotPoint.x,plotPoint.y);
                    for (int vertex_count=1; vertex_count<polygonX.length; vertex_count++){
                        plotPoint = getPlotPoint(polygonX[vertex_count],polygonY[vertex_count]);
                        path.lineTo(plotPoint.x,plotPoint.y);
                    }
                    Paint polypaint = new Paint();
                    Color color = new Color();
                    polypaint.setColor(Color.TRANSPARENT);
                    polypaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    canvas.drawPath(path,polypaint);
                }
            }
        }
        final Paint cp = new Paint();
        cp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        canvas.drawBitmap(germanyBitmap, 0,0,cp);
        // rain radar
        APIReaders.RadarmapRunnable radarmapRunnable = new APIReaders.RadarmapRunnable(getApplicationContext()){
            @Override
            public void onFinished(final Radarmap rm){
                if (rm!=null){
                    radarmap = rm;
                    Canvas radarCanvas = new Canvas(radarBitmap);
                    float yPS = (MAP_PIXEL_HEIGHT/radarmap.height)/2f+1;
                    float xPS = (MAP_PIXEL_WIDTH/radarmap.width)/2f+1;
                    Paint rpaint = new Paint();
                    rpaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    for (int y=0; y<radarmap.height; y++){
                        float delta = (1.60985f/radarmap.height)*y;
                        float xoffset = 4.6759f - delta;
                        float widthLine = 2*delta + (15.4801f-4.6759f);
                        float yGeo = 46.1878f + y * (9.3534f/radarmap.height);
                        for (int x=0; x<radarmap.width; x++){
                            float xGeo = xoffset + x * widthLine/radarmap.width;
                            PlotPoint plotPoint = getPlotPoint(xGeo,yGeo);
                            int color = radarmap.getRadarMapColor(radarmap.map[x][y]);
                            if (color!=0){
                                rpaint.setColor(color);
                                radarCanvas.drawRect(plotPoint.x-xPS,plotPoint.y-yPS,plotPoint.x+xPS,plotPoint.y+yPS,rpaint);
                            }
                        }
                    }
                    // Log.v(Tag.RADAR,"Max dbZ: "+radarmap.highestDBZ+" Max byte: "+radarmap.highestByte);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!hide_rain){
                                drawMapBitmap(radarmap);
                            }
                        }
                    });
                }
            }
        };
        executor.execute(radarmapRunnable);
        // set close listener
        ImageView closeImageview = (ImageView) findViewById(R.id.closeicon_map);
        if (closeImageview != null){
            closeImageview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    mapcontainer.setVisibility(View.GONE);
                    mapcontainer.invalidate();
                    map_collapsed_container.setVisibility(View.VISIBLE);
                    map_collapsed_container.invalidate();
                    LinearLayout.LayoutParams lop = (LinearLayout.LayoutParams) weatherList.getLayoutParams();
                    lop.height=0;
                    lop.weight=19;
                    weatherList.setLayoutParams(lop);
                    weatherList.invalidate();
                    return true;
                }
            });
        }
        // set listener
        germany = (ImageView) findViewById(R.id.warningactivity_map);
        gestureDetector = new GestureDetector(this,new MapGestureListener());
        //mapZoomable = new ZoomableImageView(getApplicationContext(),germany,germanyBitmap) {

        mapZoomable = new ZoomableImageView(getApplicationContext(),germany,warningsBitmap){
            @Override
            public void onGestureFinished(float scaleFactor, float lastXtouch, float lastYtouch, float xFocus, float yFocus, float xFocusRelative, float yFocusRelative, RectF currentlyVisibleArea){
                //Log.v("ZT","-------------------------------------");
                //Log.v("ZT","The scale factor is "+scaleFactor);
                //Log.v("ZT","Last pointer/touch at: "+lastXtouch+"/"+lastYtouch);
                //Log.v("ZT","Focus: abs: "+yFocus+"/"+xFocus+"  rel: "+xFocusRelative+"/"+yFocusRelative);
                //Log.v("ZT","Visible rectangle: "+Math.round(currentlyVisibleArea.left)+"/"+Math.round(currentlyVisibleArea.top)+" "+Math.round(currentlyVisibleArea.right)+"/"+Math.round(currentlyVisibleArea.bottom));
                final PlotPoint plotPoint = new PlotPoint();
                plotPoint.x = lastXtouch;
                plotPoint.y = lastYtouch;
                Runnable tapRunnable = new Runnable() {
                    @Override
                    public void run() {
                        checkForTapInPolygonWarning(getXGeo(plotPoint),getYGeo(plotPoint));
                    }
                };
                executor.execute(tapRunnable);
            }
        };
        if (zoomMapState!=null){
            mapZoomable.restoreZoomViewState(zoomMapState);
        }
        //Paint pdijon = new Paint();
        //pdijon.setStyle(Paint.Style.FILL_AND_STROKE);
        //pdijon.setColor(Color.BLUE);
        // add the pin sprite
        int pinsize = Math.round(0.05f*germanyBitmap.getHeight());
        PlotPoint pinPoint = getPlotPoint((float) localStation.longitude, (float) localStation.latitude);
        Bitmap pinBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.pin),pinsize,pinsize,false);
        mapZoomable.addSpite(pinBitmap,pinPoint.x,pinPoint.y-pinBitmap.getHeight(),ZoomableImageView.SPRITEFIXPOINT.BOTTOM_LEFT);
        mapZoomable.redrawBitmap();
        mapTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mapZoomable.onTouchEvent(motionEvent);
                return true;
            };
        };
        drawWindIcon();
        germany.setOnTouchListener(mapTouchListener);

    }

    class MapGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //Log.v("MOTIONEVENT","singletapup");
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            //Log.v("MOTIONEVENT","longpress");
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //Log.v("MOTIONEVENT","scroll");
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //Log.v("MOTIONEVENT","fling");
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            //Log.v("MOTIONEVENT","showpress");
            super.onShowPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            //Log.v("MOTIONEVENT","down");
            // catch collapse-button-press prior to map-selection
            // perform this only if NOT in landscape layout
            if ((!deviceIsLandscape) && (map_collapsed_container!=null) && (germany!=null) && (weatherList!=null)){
                float button_border_right  = (float) (germany.getMeasuredWidth() * 0.127427184);
                float button_border_bottom = (float) (germany.getMeasuredHeight() * 0.10041841);
                if ((e.getX()<button_border_right) && (e.getY()<button_border_bottom)){
                    germany.setVisibility(View.GONE);
                    germany.invalidate();
                    map_collapsed_container.setVisibility(View.VISIBLE);
                    map_collapsed_container.invalidate();
                    LinearLayout.LayoutParams lop = (LinearLayout.LayoutParams) weatherList.getLayoutParams();
                    lop.height=0;
                    lop.weight=19;
                    weatherList.setLayoutParams(lop);
                    weatherList.invalidate();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //Log.v("MOTIONEVENT","doubletap");
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            //Log.v("MOTIONEVENT","doubletap event");
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //Log.v("MOTIONEVENT","single tap confirmed");
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onContextClick(MotionEvent e) {
            // Log.v("MOTIONEVENT","context click");
            return super.onContextClick(e);
        }
    }

    private void checkForTapInPolygonWarning(float x_geo, float y_geo){

        if (polygoncache!=null){
            int position = 0;
            // first check if pointer is in excluded polygon; it is more efficient to do this first.
            if (excluded_polygoncache!=null){
                while (position<excluded_polygoncache.size()){
                    if (excluded_polygoncache.get(position).isInPolygon(x_geo,y_geo)){
                        // break (& do nothing) if pointer is in excluded polygon.
                        return;
                    }
                    position++;
                }
            }
            position = 0;
            while (position<polygoncache.size()){
                if (polygoncache.get(position).isInPolygon(x_geo,y_geo)){
                    jumpListViewToSelection(polygoncache.get(position));
                    return;
                }
                position++;
            }
        }
    }

    private void jumpListViewToSelection(Polygon polygon){
        int position=0;
        while (position<weatherWarnings.size()){
            if (weatherWarnings.get(position).identifier.equals(polygon.identifier_link)){
                break;
            }
            position++;
        }
        if (weatherList != null){
            final int jumpPosition = position;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weatherList.setSelection(jumpPosition);
                }
            });
        }
    }

    private boolean isDeviceLandscape(){
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();
        if ((orientation==Surface.ROTATION_0)|| (orientation==Surface.ROTATION_180)){
            return false;
        }
        return true;
    }

    private void showProgressBar(){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.warningactivity_progressbar);
        if (progressBar!=null){
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar(){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.warningactivity_progressbar);
        if (progressBar!=null){
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void registerForBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WEATHER_WARNINGS_UPDATE);
        filter.addAction(DataUpdateService.HIDE_PROGRESS);
        registerReceiver(receiver,filter);
    }



}
