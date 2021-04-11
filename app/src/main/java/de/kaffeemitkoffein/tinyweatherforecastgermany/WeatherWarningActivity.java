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

    ArrayList<Polygon> polygoncache;
    ArrayList<Polygon> excluded_polygoncache;
    ImageView germany;
    Bitmap mapBitmap;
    ImageView map_collapsed;
    boolean deviceIsLandscape;
    private GestureDetector gestureDetector;
    private View.OnTouchListener mapTouchListener;
    ListView weatherList;
    WeatherWarningAdapter weatherWarningAdapter;
    ActionBar actionBar;
    Executor executor;
    Boolean hide_rain = null;

    static float X_FACTOR;
    static float Y_FACTOR;
    final static float X_GEO_MAPOFFSET = 4.03f;
    final static float Y_GEO_MAPOFFSET = 46.98f;
    final static float GEO_MAPWIDTH = 12.130930434f;
    final static float GEO_MAPHEIGHT = 8.804865172f;

    static float MAP_WIDTH;
    static float MAP_HEIGHT;

    public final static String WEATHER_WARNINGS_UPDATE="WEATHER_WARNINGS_UPDATE";
    public final static String WEATHER_WARNINGS_UPDATE_RESULT="WEATHER_WARNINGS_UPDATE_RESULT";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                // update warning display if warnings have been updated
                if (intent.getAction().equals(WEATHER_WARNINGS_UPDATE)) {
                    weatherWarnings = WeatherWarnings.getCurrentWarnings(getApplicationContext());
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
    protected void onResume() {
        registerForBroadcast();
        updateWarningsIfNeeded();
        if (hide_rain==null){
            hide_rain = !WeatherSettings.showRadarByDefault(getApplicationContext());
        }
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
        executor = Executors.newSingleThreadExecutor();
        localStation = WeatherSettings.getSetStationLocation(getApplicationContext());
        // action bar layout
        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        weatherWarnings = WeatherWarnings.getCurrentWarnings(getApplicationContext());
        hide_rain = !WeatherSettings.showRadarByDefault(getApplicationContext());
        //updateWarningsIfNeeded();
        map_collapsed = (ImageView) findViewById(R.id.warningactivity_map_collapsed);
        // in layout w6600dp-land this element does not exist. This is the safest way to
        // limit collapse-function to portrait mode.
        if (map_collapsed!=null){
            deviceIsLandscape = false;
            map_collapsed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (germany!=null){
                        germany.setVisibility(View.VISIBLE);
                        germany.invalidate();
                        map_collapsed.setVisibility(View.GONE);
                        map_collapsed.invalidate();
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
            PrivateLog.log(this, Tag.MAIN, "starting update of weather warnings");
            if (UpdateAlarmManager.updateWarnings(getApplicationContext(),true)){
                // returns true if update service was launched sucessfully
               showProgressBar();
            }
            return true;
        }
        if (item_id==R.id.hide_rain) {
            hide_rain = !hide_rain;
            displayMap();
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
                        weatherWarningAdapter = new WeatherWarningAdapter(getApplicationContext(),weatherWarnings);
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
            displayWarnings();
            updateActionBarLabels();
        }
    }

    /*
    private float getX(float x_coordinate){
        return (x_coordinate-X_GEO_MAPOFFSET) * X_FACTOR;
    }

    private float getY(float y_coordinate){
        return MAP_HEIGHT - ((y_coordinate-Y_GEO_MAPOFFSET) * Y_FACTOR);
    }
     */

    public class PlotPoint{
        float x;
        float y;
    }

    private PlotPoint getPlotPoint(float x, float y){
        float xDeltaGeo = ((4.8f - 4f)/(52f - 46.8f) * (y - 46.8f));
        float xOffsetGeo = 4.8f - xDeltaGeo;
        float xWidthGeo = 2f*xDeltaGeo + 11f;
        float xFactor = xWidthGeo/11f;
        float xPixel = (x - xOffsetGeo) * (MAP_WIDTH/11.0f) * (1f/xFactor);
        float yPixel = (y - 46.8f) * (MAP_HEIGHT/(55f - 46.8f))*0.93f;
        float r = (float) (MAP_WIDTH / (2f * Math.sin((xWidthGeo*(Math.PI/180f))/2)));
        float l = (float) (2*r * Math.sin(((x-xOffsetGeo)*(Math.PI/180f))/2));
        float b = (x - xOffsetGeo) * (MAP_WIDTH/11.0f) * (1f/xFactor);
        float yCorrection = (float) Math.sqrt(l*l - b*b) ;
        PlotPoint plotPoint = new PlotPoint();
        plotPoint.x = xPixel;
        plotPoint.y = MAP_HEIGHT - yPixel + yCorrection;
        return plotPoint;
    }

    private void drawPin(Canvas canvas){
        Paint pinpaint = new Paint();
        pinpaint.setColor(Color.BLUE);
        pinpaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pinpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        PlotPoint pinPoint = getPlotPoint((float) localStation.longitude, (float) localStation.latitude);
        Bitmap pinBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.pin);
        canvas.drawBitmap(pinBitmap,pinPoint.x,pinPoint.y-pinBitmap.getHeight(),pinpaint);
    }

    @SuppressWarnings("unchecked")
    private void displayMap(){
        Bitmap resource_bitmap;
        if (deviceIsLandscape){
            resource_bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.germany_nc);
        } else {
            resource_bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.germany);
        }
        mapBitmap = Bitmap.createBitmap(resource_bitmap.getWidth(),resource_bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mapBitmap.eraseColor(Color.TRANSPARENT);
        MAP_HEIGHT = resource_bitmap.getHeight();
        MAP_WIDTH  = resource_bitmap.getWidth();
        X_FACTOR = (float) (MAP_WIDTH / GEO_MAPWIDTH);
        Y_FACTOR = (float) (MAP_HEIGHT / GEO_MAPHEIGHT);
        polygoncache = new ArrayList<Polygon>();
        excluded_polygoncache = new ArrayList<Polygon>();
        final Canvas canvas = new Canvas(mapBitmap);
        ArrayList<WeatherWarning> drawWarnings = (ArrayList<WeatherWarning>) weatherWarnings.clone();
        Collections.sort(drawWarnings);
        Collections.reverse(drawWarnings);
        for (int warning_counter=0; warning_counter<drawWarnings.size(); warning_counter++){
            WeatherWarning warning = drawWarnings.get(warning_counter);
            warning.initPolygons();
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
        canvas.drawBitmap(resource_bitmap, 0,0,cp);
        // rain radar
        APIReaders.RadarmapRunnable radarmapRunnable = new APIReaders.RadarmapRunnable(getApplicationContext()){
            @Override
            public void onFinished(Radarmap radarmap){
                if (radarmap!=null){
                    Paint paint = new Paint();
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    cp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
                    float yPS = (MAP_HEIGHT/radarmap.height)/2f;
                    float xPS = (MAP_WIDTH/radarmap.width)/2f;
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
                            int color = Radarmap.getRadarMapColor(radarmap.map[x][y]);
                            if (color!=0){
                                rpaint.setColor(color);
                                canvas.drawRect(plotPoint.x-xPS,plotPoint.y-yPS,plotPoint.x+xPS,plotPoint.y+yPS,rpaint);
                            }
                        }
                    }
                    Bitmap radarinfobarResourceBitmap;
                    radarinfobarResourceBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.radarinfobar);
                    canvas.drawBitmap(radarinfobarResourceBitmap,0,MAP_HEIGHT-radarinfobarResourceBitmap.getHeight(),rpaint);
                    Paint radarTextPaint = new Paint();
                    radarTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    radarTextPaint.setFakeBoldText(true);
                    radarTextPaint.setTextSize(radarinfobarResourceBitmap.getHeight());
                    radarTextPaint.setColor(Color.WHITE);
                    if (WeatherSettings.isRadarDataOutdated(getApplicationContext())){
                        radarTextPaint.setColor(Color.RED);
                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    String radartime = simpleDateFormat.format(new Date(radarmap.timestamp));
                    float ff=1.1f;
                    canvas.drawText(radartime,MAP_WIDTH/100,MAP_HEIGHT-radarinfobarResourceBitmap.getHeight()*ff*2,radarTextPaint);
                    radarTextPaint.setColor(Radarmap.RAINCOLORS[2]);
                    canvas.drawText(getResources().getString(R.string.radar_rain1),MAP_WIDTH*0.1f,MAP_HEIGHT-radarinfobarResourceBitmap.getHeight()*ff,radarTextPaint);
                    radarTextPaint.setColor(Radarmap.RAINCOLORS[7]);
                    canvas.drawText(getResources().getString(R.string.radar_rain2),MAP_WIDTH*0.3f,MAP_HEIGHT-radarinfobarResourceBitmap.getHeight(),radarTextPaint);
                    radarTextPaint.setColor(Radarmap.RAINCOLORS[11]);
                    canvas.drawText(getResources().getString(R.string.radar_rain3),MAP_WIDTH*0.6f,MAP_HEIGHT-radarinfobarResourceBitmap.getHeight(),radarTextPaint);
                    radarTextPaint.setColor(Radarmap.RAINCOLORS[16]);
                    canvas.drawText(getResources().getString(R.string.radar_rain4),MAP_WIDTH*0.8f,MAP_HEIGHT-radarinfobarResourceBitmap.getHeight(),radarTextPaint);
                    drawPin(canvas);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            germany.setImageBitmap(mapBitmap);
                        }
                    });
                }
            }
        };
        if (!hide_rain){
            executor.execute(radarmapRunnable);
        }
        drawPin(canvas);
        // set listener
        germany = (ImageView) findViewById(R.id.warningactivity_map);
        germany.setImageBitmap(mapBitmap);
        gestureDetector = new GestureDetector(this,new MapGestureListener());
        mapTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        };
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
            if ((!deviceIsLandscape) && (map_collapsed!=null) && (germany!=null) && (weatherList!=null)){
                float button_border_right  = (float) (germany.getMeasuredWidth() * 0.127427184);
                float button_border_bottom = (float) (germany.getMeasuredHeight() * 0.10041841);
                if ((e.getX()<button_border_right) && (e.getY()<button_border_bottom)){
                    germany.setVisibility(View.GONE);
                    germany.invalidate();
                    map_collapsed.setVisibility(View.VISIBLE);
                    map_collapsed.invalidate();
                    LinearLayout.LayoutParams lop = (LinearLayout.LayoutParams) weatherList.getLayoutParams();
                    lop.height=0;
                    lop.weight=19;
                    weatherList.setLayoutParams(lop);
                    weatherList.invalidate();
                    return true;
                }
            }
            return checkForTapInPolygonWarning(e);
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

    private boolean checkForTapInPolygonWarning(MotionEvent motionEvent){
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float view_width = germany.getMeasuredWidth();
        float view_height = germany.getMeasuredHeight();
        float x_laengengrade_pro_pixel = (float) (12.130930434 / view_width);
        float y_breitengrade_pro_pixel = (float) (8.804865172 / view_height);
        float x_geo = x_laengengrade_pro_pixel*x + X_GEO_MAPOFFSET;
        float y_geo = y_breitengrade_pro_pixel*(view_height-y) + Y_GEO_MAPOFFSET;
        if (polygoncache!=null){
            int position = 0;
            // first check if pointer is in excluded polygon; it is more efficient to do this first.
            if (excluded_polygoncache!=null){
                while (position<excluded_polygoncache.size()){
                    if (excluded_polygoncache.get(position).isInPolygon(x_geo,y_geo)){
                        // break (& do nothing) if pointer is in excluded polygon.
                        return true;
                    }
                    position++;
                }
            }
            position = 0;
            while (position<polygoncache.size()){
                if (polygoncache.get(position).isInPolygon(x_geo,y_geo)){
                    jumpListViewToSelection(polygoncache.get(position));
                    return true;
                }
                position++;
            }
        }
        return true;
    }

    private void jumpListViewToSelection(Polygon polygon){
        int position=0;
        while (position<weatherWarnings.size()){
            if (weatherWarnings.get(position).identifier.equals(polygon.identifier_link)){
                if (weatherList != null){
                    weatherList.setSelection(position);
                    break;
                }
            }
            position++;
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
