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
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WeatherLayersActivity extends Activity {

    public static final String ACTION_UPDATE_LAYERS = "ACTION_UPDATE_LAYERS";
    public static final String UPDATE_LAYERS_RESULT = "ACTION_UPDATE_LAYERS_RESULT";
    public static final String ACTION_UPDATE_FORBIDDEN = "ACTION_UPDATE_FORBIDDEN";

    private Context context;
    Executor executor;
    ActionBar actionBar;
    TableLayout tableLayout;
    TableRow tableRowAmbrosia; TableRow tableRowBeifuss; TableRow tableRowRoggen; TableRow tableRowEsche;
    TableRow tableRowBirke; TableRow tableRowHazel; TableRow tableRowErle; TableRow tableRowGraeser;
    boolean forceWeatherUpdateFlag = false;
    TextView wm_heading_4_1;
    TextView wm_heading_5_1;

    public class DisplayLayer{
        WeatherLayer weatherLayer;
        ImageView imageView;
        TextView textViewTitle;
        TextView textViewDate;
        RelativeLayout frame;
        int id;
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
            final String errorText = DataUpdateService.StopReason.getStopReasonErrorText(context,intent);
            if ((errorText!=null) && (forceWeatherUpdateFlag)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, errorText, Toast.LENGTH_LONG).show();
                    }
                });
            }
            if (intent!=null){
                if (intent.getAction().equals(ACTION_UPDATE_LAYERS) || (intent.getAction().equals(Pollen.ACTION_UPDATE_POLLEN))){
                    boolean result = false;
                    if (intent.hasExtra(UPDATE_LAYERS_RESULT)){
                        result = intent.getBooleanExtra(UPDATE_LAYERS_RESULT,false);
                    }
                    if (intent.hasExtra(Pollen.UPDATE_POLLEN_RESULT)){
                        result = intent.getBooleanExtra(Pollen.UPDATE_POLLEN_RESULT,false);
                    }
                    if (result){
                        updateDisplay();
                    }
                }
                if (intent.getAction().equals(MainActivity.MAINAPP_HIDE_PROGRESS)){
                    forceWeatherUpdateFlag = false;
                }
                if (intent.getAction().equals(ACTION_UPDATE_FORBIDDEN)){
                    long nextUpdateTime = WeatherSettings.getMapLastUpdateTime(context)+1000*60*5;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    String timeString = simpleDateFormat.format(new Date(nextUpdateTime));
                    String text = "\u29d6 "+String.format(context.getResources().getString(R.string.wm_update_not_allowed_yet),timeString);
                    Toast.makeText(context,text,Toast.LENGTH_LONG).show();
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
        // make a list what do update
        ArrayList<String> updateTasks = new ArrayList<String>();
        // check for pollen update
        PollenArea pollenArea = WeatherSettings.getPollenRegion(context);
        Pollen pollen = Pollen.GetPollenData(context,pollenArea);
        if ((pollen==null) || (Pollen.isUpdateDue(context))){
            updateTasks.add(DataUpdateService.SERVICEEXTRAS_UPDATE_POLLEN);
        }
        updateData(updateTasks);
        weatherLayers = WeatherLayer.getLayers(context);
        getViewIDs();
        APIReaders.getLayerImages getLayerImages = new APIReaders.getLayerImages(context,weatherLayers){
            @Override
            public void onProgress(final WeatherLayer weatherLayer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateLayer(weatherLayer);
                    }
                });
            }

            @Override
            public void onFinished(boolean result){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // nothing to do, update done in onProgress, view should be complete
                        //updateDisplay();
                    }
                });
            }
        };
        executor.execute(getLayerImages);
        TextView wm_heading_4_1 = findViewById(R.id.wm_heading_4_1);
        TextView wm_heading_5_1 = findViewById(R.id.wm_heading_5_1);
        if (wm_heading_4_1!=null){
            wm_heading_4_1.setText(String.valueOf(wm_heading_4_1.getText()).replace("6:00","12:00"));
        }
        if (wm_heading_5_1!=null){
            wm_heading_5_1.setText(String.valueOf(wm_heading_5_1.getText()).replace("6:00","18:00"));
        }
        setRowBackgrounds();
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
            if (!forceWeatherUpdateFlag){
                ArrayList<String> updateTasks = new ArrayList<String>();
                updateTasks.add(DataUpdateService.SERVICEEXTRAS_UPDATE_LAYERS);
                updateTasks.add(DataUpdateService.SERVICEEXTRAS_UPDATE_POLLEN);
                UpdateAlarmManager.startDataUpdateService(context,updateTasks);
                forceWeatherUpdateFlag = true;
            } else {
                PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.ERR,"Layer update already running. Ignoring new user request to do so.");
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
        tableLayout = (TableLayout) findViewById(R.id.wm_table);
        tableRowAmbrosia = (TableRow) findViewById(R.id.wm_row14);
        tableRowBeifuss = (TableRow) findViewById(R.id.wm_row15);
        tableRowRoggen = (TableRow) findViewById(R.id.wm_row16);
        tableRowEsche = (TableRow) findViewById(R.id.wm_row17);
        tableRowBirke = (TableRow) findViewById(R.id.wm_row18);
        tableRowHazel = (TableRow) findViewById(R.id.wm_row19);
        tableRowErle = (TableRow) findViewById(R.id.wm_row20);
        tableRowGraeser = (TableRow) findViewById(R.id.wm_row21);
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
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.SENSED_TEMPERATURE_MIN_0,
                getWeatherLayerByID(WeatherLayer.Layers.SENSED_TEMPERATURE_MIN_0),
                (RelativeLayout) findViewById(R.id.wm_element_8_1),
                (ImageView) findViewById(R.id.wm_image_8_1),
                (TextView) findViewById(R.id.wm_heading_8_1),
                (TextView) findViewById(R.id.wm_date_8_1)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.SENSED_TEMPERATURE_MIN_1,
                getWeatherLayerByID(WeatherLayer.Layers.SENSED_TEMPERATURE_MIN_1),
                (RelativeLayout) findViewById(R.id.wm_element_9_1),
                (ImageView) findViewById(R.id.wm_image_9_1),
                (TextView) findViewById(R.id.wm_heading_9_1),
                (TextView) findViewById(R.id.wm_date_9_1)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.SENSED_TEMPERATURE_MIN_2,
                getWeatherLayerByID(WeatherLayer.Layers.SENSED_TEMPERATURE_MIN_2),
                (RelativeLayout) findViewById(R.id.wm_element_10_1),
                (ImageView) findViewById(R.id.wm_image_10_1),
                (TextView) findViewById(R.id.wm_heading_10_1),
                (TextView) findViewById(R.id.wm_date_10_1)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_0,
                getWeatherLayerByID(WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_0),
                (RelativeLayout) findViewById(R.id.wm_element_11_1),
                (ImageView) findViewById(R.id.wm_image_11_1),
                (TextView) findViewById(R.id.wm_heading_11_1),
                (TextView) findViewById(R.id.wm_date_11_1)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_1,
                getWeatherLayerByID(WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_1),
                (RelativeLayout) findViewById(R.id.wm_element_12_1),
                (ImageView) findViewById(R.id.wm_image_12_1),
                (TextView) findViewById(R.id.wm_heading_12_1),
                (TextView) findViewById(R.id.wm_date_12_1)));
        displayLayers.add(new DisplayLayer(WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_2,
                getWeatherLayerByID(WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_2),
                (RelativeLayout) findViewById(R.id.wm_element_13_1),
                (ImageView) findViewById(R.id.wm_image_13_1),
                (TextView) findViewById(R.id.wm_heading_13_1),
                (TextView) findViewById(R.id.wm_date_13_1)));
        if (WeatherSettings.getPollenActiveAmbrosia(context)){
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_0,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_0),
                    (RelativeLayout) findViewById(R.id.wm_element_14_1),
                    (ImageView) findViewById(R.id.wm_image_14_1),
                    (TextView) findViewById(R.id.wm_heading_14_1),
                    (TextView) findViewById(R.id.wm_date_14_1)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_1,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_1),
                    (RelativeLayout) findViewById(R.id.wm_element_14_2),
                    (ImageView) findViewById(R.id.wm_image_14_2),
                    (TextView) findViewById(R.id.wm_heading_14_2),
                    (TextView) findViewById(R.id.wm_date_14_2)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_2,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_2),
                    (RelativeLayout) findViewById(R.id.wm_element_14_3),
                    (ImageView) findViewById(R.id.wm_image_14_3),
                    (TextView) findViewById(R.id.wm_heading_14_3),
                    (TextView) findViewById(R.id.wm_date_14_3)));
        } else {
            tableLayout.removeView(tableRowAmbrosia);
        }
        if (WeatherSettings.getPollenActiveBeifuss(context)){
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_BEIFUSS_0,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_BEIFUSS_0),
                    (RelativeLayout) findViewById(R.id.wm_element_15_1),
                    (ImageView) findViewById(R.id.wm_image_15_1),
                    (TextView) findViewById(R.id.wm_heading_15_1),
                    (TextView) findViewById(R.id.wm_date_15_1)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_BEIFUSS_1,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_BEIFUSS_1),
                    (RelativeLayout) findViewById(R.id.wm_element_15_2),
                    (ImageView) findViewById(R.id.wm_image_15_2),
                    (TextView) findViewById(R.id.wm_heading_15_2),
                    (TextView) findViewById(R.id.wm_date_15_2)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_BEIFUSS_2,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_BEIFUSS_2),
                    (RelativeLayout) findViewById(R.id.wm_element_15_3),
                    (ImageView) findViewById(R.id.wm_image_15_3),
                    (TextView) findViewById(R.id.wm_heading_15_3),
                    (TextView) findViewById(R.id.wm_date_15_3)));
        } else {
            tableLayout.removeView(tableRowBeifuss);
        }
        if (WeatherSettings.getPollenActiveRoggen(context)){
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_ROGGEN_0,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_ROGGEN_0),
                    (RelativeLayout) findViewById(R.id.wm_element_16_1),
                    (ImageView) findViewById(R.id.wm_image_16_1),
                    (TextView) findViewById(R.id.wm_heading_16_1),
                    (TextView) findViewById(R.id.wm_date_16_1)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_ROGGEN_1,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_ROGGEN_1),
                    (RelativeLayout) findViewById(R.id.wm_element_16_2),
                    (ImageView) findViewById(R.id.wm_image_16_2),
                    (TextView) findViewById(R.id.wm_heading_16_2),
                    (TextView) findViewById(R.id.wm_date_16_2)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_ROGGEN_2,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_ROGGEN_2),
                    (RelativeLayout) findViewById(R.id.wm_element_15_3),
                    (ImageView) findViewById(R.id.wm_image_16_3),
                    (TextView) findViewById(R.id.wm_heading_16_3),
                    (TextView) findViewById(R.id.wm_date_16_3)));
        } else {
            tableLayout.removeView(tableRowRoggen);
        }
        if (WeatherSettings.getPollenActiveEsche(context)){
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_ESCHE_0,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_ESCHE_0),
                    (RelativeLayout) findViewById(R.id.wm_element_17_1),
                    (ImageView) findViewById(R.id.wm_image_17_1),
                    (TextView) findViewById(R.id.wm_heading_17_1),
                    (TextView) findViewById(R.id.wm_date_17_1)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_ESCHE_1,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_ESCHE_1),
                    (RelativeLayout) findViewById(R.id.wm_element_17_2),
                    (ImageView) findViewById(R.id.wm_image_17_2),
                    (TextView) findViewById(R.id.wm_heading_17_2),
                    (TextView) findViewById(R.id.wm_date_17_2)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_ESCHE_2,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_ESCHE_2),
                    (RelativeLayout) findViewById(R.id.wm_element_17_3),
                    (ImageView) findViewById(R.id.wm_image_17_3),
                    (TextView) findViewById(R.id.wm_heading_17_3),
                    (TextView) findViewById(R.id.wm_date_17_3)));
        } else {
            tableLayout.removeView(tableRowEsche);
        }
        if (WeatherSettings.getPollenActiveBirke(context)){
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_BIRKE_0,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_BIRKE_0),
                    (RelativeLayout) findViewById(R.id.wm_element_18_1),
                    (ImageView) findViewById(R.id.wm_image_18_1),
                    (TextView) findViewById(R.id.wm_heading_18_1),
                    (TextView) findViewById(R.id.wm_date_18_1)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_BIRKE_1,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_BIRKE_1),
                    (RelativeLayout) findViewById(R.id.wm_element_18_2),
                    (ImageView) findViewById(R.id.wm_image_18_2),
                    (TextView) findViewById(R.id.wm_heading_18_2),
                    (TextView) findViewById(R.id.wm_date_18_2)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_BIRKE_2,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_BIRKE_2),
                    (RelativeLayout) findViewById(R.id.wm_element_18_3),
                    (ImageView) findViewById(R.id.wm_image_18_3),
                    (TextView) findViewById(R.id.wm_heading_18_3),
                    (TextView) findViewById(R.id.wm_date_18_3)));
        } else {
            tableLayout.removeView(tableRowBirke);
        }
        if (WeatherSettings.getPollenActiveHasel(context)){
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_HASEL_0,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_HASEL_0),
                    (RelativeLayout) findViewById(R.id.wm_element_19_1),
                    (ImageView) findViewById(R.id.wm_image_19_1),
                    (TextView) findViewById(R.id.wm_heading_19_1),
                    (TextView) findViewById(R.id.wm_date_19_1)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_HASEL_1,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_HASEL_1),
                    (RelativeLayout) findViewById(R.id.wm_element_19_2),
                    (ImageView) findViewById(R.id.wm_image_19_2),
                    (TextView) findViewById(R.id.wm_heading_19_2),
                    (TextView) findViewById(R.id.wm_date_19_2)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_HASEL_2,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_HASEL_2),
                    (RelativeLayout) findViewById(R.id.wm_element_19_3),
                    (ImageView) findViewById(R.id.wm_image_19_3),
                    (TextView) findViewById(R.id.wm_heading_19_3),
                    (TextView) findViewById(R.id.wm_date_19_3)));
        } else {
            tableLayout.removeView(tableRowHazel);
        }
        if (WeatherSettings.getPollenActiveErle(context)){
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_ERLE_0,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_ERLE_0),
                    (RelativeLayout) findViewById(R.id.wm_element_20_1),
                    (ImageView) findViewById(R.id.wm_image_20_1),
                    (TextView) findViewById(R.id.wm_heading_20_1),
                    (TextView) findViewById(R.id.wm_date_20_1)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_ERLE_1,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_ERLE_1),
                    (RelativeLayout) findViewById(R.id.wm_element_20_2),
                    (ImageView) findViewById(R.id.wm_image_20_2),
                    (TextView) findViewById(R.id.wm_heading_20_2),
                    (TextView) findViewById(R.id.wm_date_20_2)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_ERLE_2,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_ERLE_2),
                    (RelativeLayout) findViewById(R.id.wm_element_20_3),
                    (ImageView) findViewById(R.id.wm_image_20_3),
                    (TextView) findViewById(R.id.wm_heading_20_3),
                    (TextView) findViewById(R.id.wm_date_20_3)));
        } else {
            tableLayout.removeView(tableRowErle);
        }
        if (WeatherSettings.getPollenActiveGraeser(context)){
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_0,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_0),
                    (RelativeLayout) findViewById(R.id.wm_element_21_1),
                    (ImageView) findViewById(R.id.wm_image_21_1),
                    (TextView) findViewById(R.id.wm_heading_21_1),
                    (TextView) findViewById(R.id.wm_date_21_1)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_1,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_1),
                    (RelativeLayout) findViewById(R.id.wm_element_21_2),
                    (ImageView) findViewById(R.id.wm_image_21_2),
                    (TextView) findViewById(R.id.wm_heading_21_2),
                    (TextView) findViewById(R.id.wm_date_21_2)));
            displayLayers.add(new DisplayLayer(WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_2,
                    getWeatherLayerByID(WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_2),
                    (RelativeLayout) findViewById(R.id.wm_element_21_3),
                    (ImageView) findViewById(R.id.wm_image_21_3),
                    (TextView) findViewById(R.id.wm_heading_21_3),
                    (TextView) findViewById(R.id.wm_date_21_3)));
        } else {
            tableLayout.removeView(tableRowHazel);
        }
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

    public DisplayLayer getDisplayLayerByID(int id){
        if (displayLayers!=null){
            for (int i=0; i<displayLayers.size(); i++){
                DisplayLayer displayLayer = displayLayers.get(i);
                if (displayLayer.weatherLayer.layer == id){
                    return displayLayer;
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

    public void updateLayer(WeatherLayer updateLayer){
        if (updateLayer!=null){
            DisplayLayer displayLayer = getDisplayLayerByID(updateLayer.layer);
            if (displayLayer!=null){
                Bitmap bitmap = updateLayer.getLayerBitmap(context);
                if (bitmap!=null){
                    displayLayer.imageView.setImageBitmap(bitmap);
                    displayLayer.textViewDate.setText(displayLayer.weatherLayer.getTimestampString());
                }
            }
        }
    }

    private void setRowBackgrounds(){
        for (int i=1; i<=21; i++){
            int id = getResources().getIdentifier("wm_row"+i,"id",context.getPackageName());
            TableRow tableRow1 = (TableRow) findViewById(id);
            if (tableRow1!=null){
                tableRow1.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
            }
        }
    }

    private void updateData(ArrayList<String> updateTasks){
        if (updateTasks!=null){
            if (updateTasks.size()>0){
                UpdateAlarmManager.startDataUpdateService(context,updateTasks);
            }
        }
    }

    private void registerForBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_LAYERS);
        filter.addAction(Pollen.ACTION_UPDATE_POLLEN);
        filter.addAction(MainActivity.MAINAPP_HIDE_PROGRESS);
        filter.addAction(ACTION_UPDATE_FORBIDDEN);
        registerReceiver(receiver,filter);
    }



}
