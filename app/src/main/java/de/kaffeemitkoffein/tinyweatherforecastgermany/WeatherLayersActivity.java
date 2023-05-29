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

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WeatherLayersActivity extends Activity {

    public static final String ACTION_UPDATE_LAYERS = "ACTION_UPDATE_LAYERS";
    public static final String UPDATE_LAYERS_RESULT = "ACTION_UPDATE_LAYERS_RESULT";

    private Context context;
    Executor executor;
    ActionBar actionBar;

    public class DisplayLayer{
        WeatherLayer weatherLayer;
        ImageView imageView;
        TextView textViewTitle;
        TextView textViewDate;
        RelativeLayout frame;
        int id;

        public final class BlendMode{
            public final static int NONE = 0;
            public final static int GERMANY = 1;
            public final static int EUROPE = 2;
            public final static int WORLD = 3;
        }

        public DisplayLayer(int id, WeatherLayer weatherLayer, RelativeLayout frame, ImageView imageView, TextView textViewTitle, TextView textViewDate){
            this.id=id;
            this.frame = frame;
            this.weatherLayer = weatherLayer;
            this.imageView=imageView;
            this.textViewTitle=textViewTitle;
            this.textViewDate=textViewDate;
            this.frame.setOnClickListener(onClickListener_Map);
            this.frame.setTag(weatherLayer.layer);
        }
    }

    ArrayList<DisplayLayer> displayLayers;
    ArrayList<WeatherLayer> weatherLayers;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent!=null){
                if (intent.getAction().equals(ACTION_UPDATE_LAYERS)){
                    boolean result = false;
                    if (intent.hasExtra(UPDATE_LAYERS_RESULT)){
                        result = intent.getBooleanExtra(UPDATE_LAYERS_RESULT,false);
                    }
                    if (result){
                        updateDisplay();
                    }
                }
            }
        }
    };

    final View.OnClickListener onClickListener_Map = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int layer = (int) view.getTag();
            openDetailedMap(layer);
        }
    };

    @Override
    protected void onResume() {
        registerForBroadcast();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePicker.SetTheme(this);
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_weatherlayers);
        // action bar layout
        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        executor = Executors.newSingleThreadExecutor();
        weatherLayers = WeatherLayer.getLayers();
        getViewIDs();
        APIReaders.getLayerImages getLayerImages = new APIReaders.getLayerImages(context,weatherLayers){
            @Override
            public void onFinished(boolean result){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDisplay();
                    }
                });
            }
        };
        executor.execute(getLayerImages);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.weatherlayers_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int item_id = mi.getItemId();
        if (item_id == R.id.wl_refresh) {
            if (WeatherSettings.isLayerUpdateAllowed(context)){
                ArrayList<String> updateTasks = new ArrayList<String>();
                updateTasks.add(DataUpdateService.SERVICEEXTRAS_UPDATE_LAYERS);
                UpdateAlarmManager.startDataUpdateService(context,updateTasks);
            } else {
                long lastUpdateTime = WeatherSettings.getMapLastUpdateTime(context);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                String timeString = simpleDateFormat.format(new Date(lastUpdateTime));
                String text = String.format(context.getResources().getString(R.string.wm_update_not_allowed_yet),timeString);
                Toast.makeText(context,text,Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(mi);
    }

    public void openDetailedMap(int layer){
        Intent i = new Intent(this, WeatherLayerMapActivity.class);
        i.putExtra(WeatherLayerMapActivity.LAYER,layer);
        startActivity(i);
    }

    public void getViewIDs(){
        /*
        imageView_1_1 = (ImageView) findViewById(R.id.wm_image_1_1);
        textViewTitle_1_1 = (TextView) findViewById(R.id.wm_heading_1_1);
        textViewDate_1_1 = (TextView) findViewById(R.id.wm_date_1_1);
        imageView_1_2 = (ImageView) findViewById(R.id.wm_image_1_2);
        textViewTitle_1_2 = (TextView) findViewById(R.id.wm_heading_1_2);
        textViewDate_1_2 = (TextView) findViewById(R.id.wm_date_1_2);
        imageView_1_3 = (ImageView) findViewById(R.id.wm_image_1_3);
        textViewTitle_1_3 = (TextView) findViewById(R.id.wm_heading_1_3);
        textViewDate_1_3 = (TextView) findViewById(R.id.wm_date_1_3);
         */
        displayLayers = new ArrayList<DisplayLayer>();
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.UVI_CLOUDS_0,
                getWeatherLayerByID(WeatherLayer.Layers.UVI_CLOUDS_0),
                (RelativeLayout) findViewById(R.id.wm_element_1_1),
                (ImageView) findViewById(R.id.wm_image_1_1),
                (TextView) findViewById(R.id.wm_heading_1_1),
                (TextView) findViewById(R.id.wm_date_1_1)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.UVI_CLOUDS_1,
                getWeatherLayerByID(WeatherLayer.Layers.UVI_CLOUDS_1),
                (RelativeLayout) findViewById(R.id.wm_element_1_2),
                (ImageView) findViewById(R.id.wm_image_1_2),
                (TextView) findViewById(R.id.wm_heading_1_2),
                (TextView) findViewById(R.id.wm_date_1_2)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.UVI_CLOUDS_2,
                getWeatherLayerByID(WeatherLayer.Layers.UVI_CLOUDS_2),
                (RelativeLayout) findViewById(R.id.wm_element_1_3),
                (ImageView) findViewById(R.id.wm_image_1_3),
                (TextView) findViewById(R.id.wm_heading_1_3),
                (TextView) findViewById(R.id.wm_date_1_3)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.UVI_CLOUDLESS_0,
                getWeatherLayerByID(WeatherLayer.Layers.UVI_CLOUDLESS_0),
                (RelativeLayout) findViewById(R.id.wm_element_2_1),
                (ImageView) findViewById(R.id.wm_image_2_1),
                (TextView) findViewById(R.id.wm_heading_2_1),
                (TextView) findViewById(R.id.wm_date_2_1)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.UVI_CLOUDLESS_1,
                getWeatherLayerByID(WeatherLayer.Layers.UVI_CLOUDLESS_1),
                (RelativeLayout) findViewById(R.id.wm_element_2_2),
                (ImageView) findViewById(R.id.wm_image_2_2),
                (TextView) findViewById(R.id.wm_heading_2_2),
                (TextView) findViewById(R.id.wm_date_2_2)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.UVI_CLOUDLESS_2,
                getWeatherLayerByID(WeatherLayer.Layers.UVI_CLOUDLESS_2),
                (RelativeLayout) findViewById(R.id.wm_element_2_3),
                (ImageView) findViewById(R.id.wm_image_2_3),
                (TextView) findViewById(R.id.wm_heading_2_3),
                (TextView) findViewById(R.id.wm_date_2_3)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.UVI_CLOUDS_EUROPE_0,
                getWeatherLayerByID(WeatherLayer.Layers.UVI_CLOUDS_EUROPE_0),
                (RelativeLayout) findViewById(R.id.wm_element_6_1),
                (ImageView) findViewById(R.id.wm_image_6_1),
                (TextView) findViewById(R.id.wm_heading_6_1),
                (TextView) findViewById(R.id.wm_date_6_1)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.UVI_CLOUDLESS_EUROPE_0,
                getWeatherLayerByID(WeatherLayer.Layers.UVI_CLOUDLESS_EUROPE_0),
                (RelativeLayout) findViewById(R.id.wm_element_7_1),
                (ImageView) findViewById(R.id.wm_image_7_1),
                (TextView) findViewById(R.id.wm_heading_7_1),
                (TextView) findViewById(R.id.wm_date_7_1)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.SENSED_TEMPERATURE_1M_0,
                getWeatherLayerByID(WeatherLayer.Layers.SENSED_TEMPERATURE_1M_0),
                (RelativeLayout) findViewById(R.id.wm_element_3_1),
                (ImageView) findViewById(R.id.wm_image_3_1),
                (TextView) findViewById(R.id.wm_heading_3_1),
                (TextView) findViewById(R.id.wm_date_3_1)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.SENSED_TEMPERATURE_1M_1,
                getWeatherLayerByID(WeatherLayer.Layers.SENSED_TEMPERATURE_1M_1),
                (RelativeLayout) findViewById(R.id.wm_element_4_1),
                (ImageView) findViewById(R.id.wm_image_4_1),
                (TextView) findViewById(R.id.wm_heading_4_1),
                (TextView) findViewById(R.id.wm_date_4_1)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.SENSED_TEMPERATURE_1M_2,
                getWeatherLayerByID(WeatherLayer.Layers.SENSED_TEMPERATURE_1M_2),
                (RelativeLayout) findViewById(R.id.wm_element_5_1),
                (ImageView) findViewById(R.id.wm_image_5_1),
                (TextView) findViewById(R.id.wm_heading_5_1),
                (TextView) findViewById(R.id.wm_date_5_1)));
    }

    public WeatherLayer getWeatherLayerByID(int id){
        if (weatherLayers!=null){
            for (int i=0; i<weatherLayers.size(); i++){
                WeatherLayer weatherLayer = weatherLayers.get(i);
                if (weatherLayer.layer==id){
                    return weatherLayer;
                }
            }
        }
        return null;
    }

    public void updateDisplay(){
        for (int i=0; i<displayLayers.size(); i++){
            DisplayLayer displayLayer = displayLayers.get(i);
            Bitmap map = displayLayer.weatherLayer.getLayerBitmap(context);
            displayLayer.imageView.setImageBitmap(map);
            displayLayer.textViewDate.setText(displayLayer.weatherLayer.getTimestampString());
        }
    }

    private void registerForBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_LAYERS);
        registerReceiver(receiver,filter);
    }



}
