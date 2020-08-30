/*
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020 Pawel Dube
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
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeatherWarningActivity extends Activity {

    ArrayList<WeatherWarning> weatherWarnings;
    ArrayList<Polygon> polygoncache;
    ImageView germany;
    ImageView map_collapsed;
    private GestureDetector gestureDetector;
    private View.OnTouchListener mapTouchListener;
    ListView weatherList;

    ActionBar actionBar;

    static float X_FACTOR;
    static float Y_FACTOR;
    final static float X_GEO_MAPOFFSET=(float) 4.03;
    final static float Y_GEO_MAPOFFSET=(float) 46.98;

    static float MAP_WIDTH;
    static float MAP_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weatherwarning);
        // action bar layout
        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);

        weatherWarnings = WeatherWarnings.getCurrentWarnings(getApplicationContext());
        // Log.v("POLLVALUE","Warnings size: "+weatherWarnings.size());
        if (areWarningsOutdated()){
            PrivateLog.log(getApplicationContext(),Tag.WARNINGS,"Warnings outdated, getting new ones.");
            // read warnings
            updateWarnings();
        } else {
            PrivateLog.log(getApplicationContext(),Tag.WARNINGS,"Warnings not outdated, recycling.");
            displayWarnings();
            updateActionBarLabels();
        }
        map_collapsed = (ImageView) findViewById(R.id.warningactivity_map_collapsed);
        // in layout w6600dp-land this element does not exist. This is the safest way to
        // limit collapse-function to portrait mode.
        if (map_collapsed!=null){
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
            updateWarnings();
            return true;
        }
        return super.onOptionsItemSelected(mi);
    }

    public void updateActionBarLabels(){
        long updatetime = getOldestPollValue();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        String update = simpleDateFormat.format(updatetime);
        actionBar.setSubtitle(getApplicationContext().getResources().getString(R.string.warnings)+": "+weatherWarnings.size()+" "+update);

    }

    public void updateWarnings(){
        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.warnings_update),Toast.LENGTH_LONG).show();
        WeatherWarnings.cleanWeatherWarningsDatabase(getApplicationContext());
        final WeatherWarningReader weatherWarningReader = new WeatherWarningReader(getApplicationContext()){
            @Override
            public void onPositiveResult(ArrayList<WeatherWarning> warnings){
                super.onPositiveResult(warnings);
                weatherWarnings = warnings;
                PrivateLog.log(getApplicationContext(),Tag.WARNINGS,"Warnings updated successfully.");
                displayWarnings();
                if (warnings!=null){
                    updateActionBarLabels();
                }
            }
            public void onNegativeResult(){
                PrivateLog.log(getApplicationContext(),Tag.WARNINGS,"Getting warnings failed.");
                // to do !
            }
        };
        weatherWarningReader.execute();
    }

    private void displayWarnings(){
        weatherList = (ListView) findViewById(R.id.warningactivity_listview);
        WeatherWarningAdapter weatherWarningAdapter = new WeatherWarningAdapter(getApplicationContext(),weatherWarnings);
        weatherList.setAdapter(weatherWarningAdapter);
        displayMap();
    }

    private long getOldestPollValue(){
        long oldest_poll = 0;
        if (weatherWarnings!=null){
            if (weatherWarnings.size()>0){
                oldest_poll = weatherWarnings.get(0).polling_time;
                for (int i=1; i<weatherWarnings.size(); i++){
                    Log.v("POLLVALUE","time: "+weatherWarnings.get(i).polling_time);
                    if (weatherWarnings.get(i).polling_time<oldest_poll){
                        oldest_poll = weatherWarnings.get(i).polling_time;
                    }
                }
            }
        }
        return oldest_poll;
    }

    private boolean areWarningsOutdated(){
        WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
        return getOldestPollValue() + weatherSettings.getWarningsCacheTimeInMillis() <= Calendar.getInstance().getTimeInMillis();
    }

    private float getX(float x_coordinate){
        return (x_coordinate-X_GEO_MAPOFFSET) * X_FACTOR;
    }

    private float getY(float y_coordinate){
        return MAP_HEIGHT - ((y_coordinate-Y_GEO_MAPOFFSET) * Y_FACTOR);
    }

    private void displayMap(){
        Bitmap resource_bitmap;
        if (isDeviceLandscape()){
            resource_bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.germany_nc);
        } else {
            resource_bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.germany);
        }
        Bitmap bitmap = Bitmap.createBitmap(resource_bitmap.getWidth(),resource_bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        MAP_HEIGHT = resource_bitmap.getHeight();
        MAP_WIDTH  = resource_bitmap.getWidth();

        X_FACTOR = (float) (MAP_WIDTH / 12.130930434);
        Y_FACTOR = (float) (MAP_HEIGHT / 8.804865172);

        polygoncache = new ArrayList<Polygon>();

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(resource_bitmap, 0,0,null);
        // Längengrad = x, Breitengrad = y
        for (int warning_counter=0; warning_counter<weatherWarnings.size(); warning_counter++){
            WeatherWarning warning = weatherWarnings.get(warning_counter);
            warning.initPolygons();
            for (int polygon_counter=0; polygon_counter<warning.polygonlist.size(); polygon_counter++){
                float[] polygonX = warning.polygonlist.get(polygon_counter).polygonX;
                float[] polygonY = warning.polygonlist.get(polygon_counter).polygonY;
                // add polygon to cache
                polygoncache.add(new Polygon(polygonX,polygonY,warning.identifier));
                if (polygonX.length>0){
                    Path path = new Path();
                    path.moveTo(getX(polygonX[0]),getY(polygonY[0]));
                    for (int vertex_count=1; vertex_count<polygonX.length; vertex_count++){
                        path.lineTo(getX(polygonX[vertex_count]),getY(polygonY[vertex_count]));
                    }
                    Paint polypaint = new Paint();
                    Color color = new Color();
                    color.blue(125);
                    color.red(polygon_counter*2);
                    color.green(polygon_counter*3);
                    polypaint.setColor(warning.getWarningColor());
                    polypaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    canvas.drawPath(path,polypaint);
                }
            }
        }
        germany = (ImageView) findViewById(R.id.warningactivity_map);
        germany.setImageBitmap(bitmap);
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
        //Log.v("MOTIONEVENT ONTOUCH","IMAGE  "+view_width+"/"+view_height);
        float x_geo = x_laengengrade_pro_pixel*x + X_GEO_MAPOFFSET;
        float y_geo = y_breitengrade_pro_pixel*(view_height-y) + Y_GEO_MAPOFFSET;
        //Log.v("MOTIONEVENT ONTOUCH",x+"/"+y+" => "+x_geo+"/"+y_geo);
        if (polygoncache!=null){
            int position = 0;
            while (position<polygoncache.size()){
                if (polygoncache.get(position).isInPolygon(x_geo,y_geo)){
                    jumpListViewToSelection(polygoncache.get(position));
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
}
