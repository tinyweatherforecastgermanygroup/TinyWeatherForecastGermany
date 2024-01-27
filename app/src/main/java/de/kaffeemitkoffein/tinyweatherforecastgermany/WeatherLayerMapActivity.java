/*
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021, 2022, 2023, 2024 Pawel Dube
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
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class WeatherLayerMapActivity extends Activity {

    public final static String LAYER = "layer";

    Context context;
    ZoomableImageView zoomableImageView;
    int layer;
    WeatherLayer weatherLayer;
    TextView titleTextView;
    ImageView legendImageView;
    ImageView mapImageView;
    RelativeLayout mainContainer;
    RelativeLayout legendHookView;
    RelativeLayout legendHolder;
    boolean pollenLegendInitated=false;
    ActionBar actionBar;
    Executor executor;
    ArrayAdapter<String> adapter1;
    ArrayAdapter<String> adapter2;
    LinearLayout spinnerHolder;
    Spinner spinner1;
    Spinner spinner2;
    int[] browseItemsOrder;

    ArrayList<String> baseSpinnerItems;
    ArrayList<Integer> baseJumpTarget;
    ArrayList<String> additonalSpinnerItems;
    ArrayList<Integer> additionalJumpTarget;

    WeatherDetailsActivity.SwipeGestureDetector swipeGestureDetector = new WeatherDetailsActivity.SwipeGestureDetector(){
        @Override
        public boolean onLeftSwipe(View view, MotionEvent motionEvent) {
            changeMap(1);
            return super.onLeftSwipe(view, motionEvent);
        }

        @Override
        public boolean onRightSwipe(View view, MotionEvent motionEvent) {
            changeMap(-1);
            return super.onRightSwipe(view, motionEvent);
        }

        @Override
        public boolean onUpSwipe(View view, MotionEvent motionEvent) {
            return super.onUpSwipe(view, motionEvent);
        }

        @Override
        public boolean onDownSwipe(View view, MotionEvent motionEvent) {
            return super.onDownSwipe(view, motionEvent);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePicker.SetTheme(this);
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_weatherlayermap);
        executor = Executors.newSingleThreadExecutor();
        // get browse items order
        browseItemsOrder = WeatherLayer.getFilteredBrowseItemsOrder(context);
        // get last displayed layer if no intent or extra
        layer = WeatherSettings.getLastDisplayedLayer(context);
        // get layer from bundle if possible
        layer = -1;
        if (savedInstanceState!=null){
            layer = savedInstanceState.getInt(LAYER,-1);
        }
        if (layer==-1){
            // get layer from intent if possible
            Intent intent = getIntent();
            if (intent!=null){
                if (intent.hasExtra(LAYER)){
                    layer = intent.getIntExtra(LAYER, WeatherLayer.Layers.UVI_CLOUDS_0);
                }
            }
        }
        if (layer==-1){
            layer = WeatherLayer.Layers.UVI_CLOUDS_0;
        }
        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        // titleTextView will be null in landscape mode
        titleTextView = (TextView) findViewById(R.id.wlm_title);
        spinnerHolder = (LinearLayout) findViewById(R.id.wlm_spinnerholder);
        spinner1 = (Spinner) findViewById(R.id.wlm_spinner1);
        spinner2 = (Spinner) findViewById(R.id.wlm_spinner2);
        weatherLayer = new WeatherLayer(layer);
        mainContainer = (RelativeLayout) findViewById(R.id.wlm_maincontainer);
        mapImageView = (ImageView) findViewById(R.id.wlm_map);
        attachSpinner();
        displayMap();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt(LAYER,layer);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.weatherlayermap_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public int findLayerPosition(int layer){
        for (int i=0; i<browseItemsOrder.length; i++){
            if (browseItemsOrder[i]==layer){
                return i;
            }
        }
        return -1;
    }

    public void changeMap(int direction){
        int currentPosition = findLayerPosition(layer);
        int newPosition = currentPosition + direction;
        if (newPosition<0){
            newPosition = browseItemsOrder.length-1;
        }
        if (newPosition>=browseItemsOrder.length){
            newPosition=0;
        }
        layer = browseItemsOrder[newPosition];
        attachSpinner();
        displayMap();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int item_id = mi.getItemId();
        if (item_id == R.id.wlm_menu_back) {
            changeMap(-1);
        }
        if (item_id == R.id.wlm_menu_next) {
            changeMap(1);
        }
        return super.onOptionsItemSelected(mi);
    }

    public final String removeSpecialChars(String s){
        String result = s.replace(":","");
        return result;
    }

    public void setTimeSpinnerList(){
        additonalSpinnerItems = new ArrayList<String>();
        additionalJumpTarget =  new ArrayList<Integer>();
        additonalSpinnerItems.add(removeSpecialChars(getResources().getString(R.string.today))); additionalJumpTarget.add(Pollen.Today);
        additonalSpinnerItems.add(removeSpecialChars(getResources().getString(R.string.tomorrow))); additionalJumpTarget.add(Pollen.Tomorrow);
        additonalSpinnerItems.add(removeSpecialChars(getResources().getString(R.string.dayaftertomorrow))); additionalJumpTarget.add(Pollen.DayAfterTomorrow);
    }

    public void setPollenSpinnerList(){
        baseSpinnerItems = new ArrayList<String>();
        baseJumpTarget =  new ArrayList<Integer>();
        if (WeatherSettings.getPollenActiveAmbrosia(context)){
            baseSpinnerItems.add(getResources().getString(R.string.pollen_ambrosia));
            baseJumpTarget.add(WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_0);
        }
        if (WeatherSettings.getPollenActiveBeifuss(context)){
            baseSpinnerItems.add(getResources().getString(R.string.pollen_mugwort));
            baseJumpTarget.add(WeatherLayer.Layers.POLLEN_FORECAST_BEIFUSS_0);
        }
        if (WeatherSettings.getPollenActiveRoggen(context)){
            baseSpinnerItems.add(getResources().getString(R.string.pollen_rye));
            baseJumpTarget.add(WeatherLayer.Layers.POLLEN_FORECAST_ROGGEN_0);
        }
        if (WeatherSettings.getPollenActiveEsche(context)){
            baseSpinnerItems.add(getResources().getString(R.string.pollen_ash));
            baseJumpTarget.add(WeatherLayer.Layers.POLLEN_FORECAST_ESCHE_0);
        }
        if (WeatherSettings.getPollenActiveBirke(context)){
            baseSpinnerItems.add(getResources().getString(R.string.pollen_birch));
            baseJumpTarget.add(WeatherLayer.Layers.POLLEN_FORECAST_BIRKE_0);
        }
        if (WeatherSettings.getPollenActiveHasel(context)){
            baseSpinnerItems.add(getResources().getString(R.string.pollen_hazel));
            baseJumpTarget.add(WeatherLayer.Layers.POLLEN_FORECAST_HASEL_0);
        }
        if (WeatherSettings.getPollenActiveErle(context)){
            baseSpinnerItems.add(getResources().getString(R.string.pollen_alder));
            baseJumpTarget.add(WeatherLayer.Layers.POLLEN_FORECAST_ERLE_0);
        }
        if (WeatherSettings.getPollenActiveGraeser(context)){
            baseSpinnerItems.add(getResources().getString(R.string.pollen_grasses));
            baseJumpTarget.add(WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_0);
        }
    }

    public class SpinnerPositionPair{
        int base; int additional;
    }

    public SpinnerPositionPair getBaseSpinnerPosition(){
      SpinnerPositionPair result = new SpinnerPositionPair();
      for (int i=0; i<baseJumpTarget.size(); i++){
          if ((layer==baseJumpTarget.get(i)) || (layer-1==baseJumpTarget.get(i)) || (layer-2==baseJumpTarget.get(i))){
              result.base = i;
              result.additional = 0;
              if (layer-1==baseJumpTarget.get(i)){
                  result.additional = 1;
              }
              if (layer-2==baseJumpTarget.get(i)){
                  result.additional = 2;
              }
              return result;
          }
      }
      return null;
    };

    public void setCloudSpinnerList(){
        baseSpinnerItems = new ArrayList<String>();
        baseJumpTarget =  new ArrayList<Integer>();
        baseSpinnerItems.add(removeSpecialChars(getResources().getString(R.string.clouds))); baseJumpTarget.add(WeatherLayer.Layers.UVI_CLOUDS_0);
        baseSpinnerItems.add(removeSpecialChars(getResources().getString(R.string.clear_sky))); baseJumpTarget.add(WeatherLayer.Layers.UVI_CLOUDLESS_0);
    }

    public void setTodaySensedTemeraturesSpinnerList(){
        baseSpinnerItems = new ArrayList<String>();
        baseJumpTarget =  new ArrayList<Integer>();
        baseSpinnerItems.add(getResources().getString(R.string.layerlabel_short_ts)); baseJumpTarget.add(WeatherLayer.Layers.SENSED_TEMPERATURE_1M_0);
    }

    public void setTodaySensedTemperatureTimesSpinnerList(){
        additonalSpinnerItems = new ArrayList<String>();
        additionalJumpTarget =  new ArrayList<Integer>();
        additonalSpinnerItems.add(getResources().getString(R.string.local_time_6)); additionalJumpTarget.add(0);
        additonalSpinnerItems.add(getResources().getString(R.string.local_time_6).replace("6:00","12:00")); additionalJumpTarget.add(1);
        additonalSpinnerItems.add(getResources().getString(R.string.local_time_6).replace("6:00","18:00")); additionalJumpTarget.add(2);
    }

    public void setMinMaxTemperatureSpinnerList(){
        baseSpinnerItems = new ArrayList<String>();
        baseJumpTarget =  new ArrayList<Integer>();
        baseSpinnerItems.add(getResources().getString(R.string.temp_min)); baseJumpTarget.add(WeatherLayer.Layers.SENSED_TEMPERATURE_MIN_0);
        baseSpinnerItems.add(getResources().getString(R.string.temp_max)); baseJumpTarget.add(WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_0);
    }

    final MainActivity.SpinnerListener spinnerClickListener = new MainActivity.SpinnerListener() {
        @Override
        public void handleItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            int baseLayer = baseJumpTarget.get(spinner1.getSelectedItemPosition());
            int addPosition = additionalJumpTarget.get(spinner2.getSelectedItemPosition());
            int newPosition = baseLayer + addPosition;
            layer = newPosition;
            displayMap();
        }
    };

    public void attachSpinner(){
        int pos1=-1; int pos2=-1;
        if ((layer>= WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_0) &&
                (layer<=WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_2)){
            setPollenSpinnerList();
            adapter1 = new ArrayAdapter<String>(context, R.layout.custom_spinner_item_small,baseSpinnerItems);
            setTimeSpinnerList();
            adapter2 = new ArrayAdapter<String>(context, R.layout.custom_spinner_item_small,additonalSpinnerItems);
            SpinnerPositionPair spinnerPositionPair = getBaseSpinnerPosition();
            if (spinnerPositionPair!=null){
                pos1 = spinnerPositionPair.base; pos2=spinnerPositionPair.additional;
            }
        }
        if ((layer>= WeatherLayer.Layers.UVI_CLOUDS_0) &&
                (layer<=WeatherLayer.Layers.UVI_CLOUDLESS_2)){
            setCloudSpinnerList();
            adapter1 = new ArrayAdapter<String>(context, R.layout.custom_spinner_item_small,baseSpinnerItems);
            setTimeSpinnerList();
            adapter2 = new ArrayAdapter<String>(context, R.layout.custom_spinner_item_small,additonalSpinnerItems);
            SpinnerPositionPair spinnerPositionPair = getBaseSpinnerPosition();
            if (spinnerPositionPair!=null){
                pos1 = spinnerPositionPair.base; pos2=spinnerPositionPair.additional;
            }
        }
        if ((layer>=WeatherLayer.Layers.SENSED_TEMPERATURE_1M_0) && (layer<=WeatherLayer.Layers.SENSED_TEMPERATURE_1M_2)){
            setTodaySensedTemeraturesSpinnerList();
            adapter1 = new ArrayAdapter<String>(context, R.layout.custom_spinner_item_small,baseSpinnerItems);
            setTodaySensedTemperatureTimesSpinnerList();
            adapter2 = new ArrayAdapter<String>(context, R.layout.custom_spinner_item_small,additonalSpinnerItems);
            SpinnerPositionPair spinnerPositionPair = getBaseSpinnerPosition();
            if (spinnerPositionPair!=null){
                pos1 = spinnerPositionPair.base; pos2=spinnerPositionPair.additional;
            }
        }
        if ((layer>=WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_0) && (layer<=WeatherLayer.Layers.SENSED_TEMPERATURE_MIN_2)){
            setMinMaxTemperatureSpinnerList();
            adapter1 = new ArrayAdapter<String>(context, R.layout.custom_spinner_item_small,baseSpinnerItems);
            setTimeSpinnerList();
            adapter2 = new ArrayAdapter<String>(context, R.layout.custom_spinner_item_small,additonalSpinnerItems);
            SpinnerPositionPair spinnerPositionPair = getBaseSpinnerPosition();
            if (spinnerPositionPair!=null){
                pos1 = spinnerPositionPair.base; pos2=spinnerPositionPair.additional;
            }
        }
        if (pos1!=-1) {
            spinner1.setVisibility(View.VISIBLE);
            spinner1.setAdapter(adapter1);
            spinner1.setSelection(pos1);
            spinner1.setOnItemSelectedListener(spinnerClickListener);
            spinner1.setOnTouchListener(spinnerClickListener);
        } else {
            spinner1.setVisibility(View.GONE);
        }
        if (pos2!=-1){
            spinner2.setVisibility(View.VISIBLE);
            spinner2.setAdapter(adapter2);
            spinner2.setSelection(pos2);
            spinner2.setOnItemSelectedListener(spinnerClickListener);
            spinner2.setOnTouchListener(spinnerClickListener);
        } else {
            spinner2.setVisibility(View.GONE);
        }
        if (((pos1!=-1) || (pos2!=-1)) && (spinnerHolder!=null)){
            spinnerHolder.setVisibility(View.VISIBLE);
            spinnerHolder.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
        }
        if ((pos1==-1) && (pos2==-1) && (spinnerHolder!=null)){
            spinnerHolder.setVisibility(View.GONE);
            // adapt dwd notice to use complete width
            TextView notice = (TextView) findViewById(R.id.wlm_reference_text);
            if (notice!=null){
                ViewGroup.LayoutParams layoutParams = notice.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                notice.setLayoutParams(layoutParams);
            }
        }

    }

    public void displayMap(){
        if (titleTextView!=null){
            // in portrait mode, display title in main view
            titleTextView.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
            titleTextView.setText(WeatherLayer.getLabel(context,layer));
            // display only short label in actionbar
            actionBar.setTitle(WeatherLayer.getShortLabel(context,layer));
        } else {
            // in landscape mode, display title in actionbar to save space
          actionBar.setTitle(WeatherLayer.getLabel(context,layer));
        }
        weatherLayer = new WeatherLayer(layer);
        // files in cache might be missing, so we must check if the layer is "outdated". This check will
        // return also true when the underlying file is simply missing.
        if (weatherLayer.isOutdated(context)){
            ArrayList<WeatherLayer> layersToFetch = new ArrayList<WeatherLayer>();
            layersToFetch.add(weatherLayer);
            APIReaders.getLayerImages getLayerImages = new APIReaders.getLayerImages(context,layersToFetch){
                @Override
                public void onFinished(boolean success) {
                    super.onFinished(success);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initZoomableImageMap(weatherLayer);
                        }
                    });
                }
            };
            executor.execute(getLayerImages);
        } else {
            initZoomableImageMap(weatherLayer);
        }
    }

    private void initZoomableImageMap(WeatherLayer weatherLayer){
        if (mapImageView==null){
            mapImageView = (ImageView) findViewById(R.id.wlm_map);
            mapImageView.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
        }
        drawLegend(weatherLayer);
        Bitmap visibleBitmap = weatherLayer.getLayerBitmap(context);
        // layer might be null due to day-update shifts
        if (visibleBitmap!=null){
            zoomableImageView = new ZoomableImageView(context,mapImageView,visibleBitmap,true){
                @Override
                public void onGestureFinished(float scaleFactor, float lastPressX, float lastPressY, float xFocus, float yFocus, float xFocusRelative, float yFocusRelative, RectF currentlyVisibleArea){
                    // things to do after gesture finished.
                }
            };
            mapImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    zoomableImageView.onTouchEvent(motionEvent);
                    return true;
                }
            });
        } else {
            mapImageView.setImageBitmap(WeatherIcons.getIconBitmap(context,WeatherIcons.IC_IMAGE_NOT_SUPPORTED,false));
        }
    }

    private void initPollenLegend(){
        int resourceID = R.layout.pollenlegend_horizontal;
        // change if landscape mode
        if (legendHolder!=null){
            resourceID = R.layout.pollenlegend_vertical;
        }
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(resourceID,null);
        WeatherDetailsActivity.setPollenLegendColorBoxes(context,view);
        legendHookView.addView(view);
        pollenLegendInitated=true;
    }

    public void drawLegend(WeatherLayer weatherLayer){
        // always init to check if device is landscape or portrait mode
        legendHolder = (RelativeLayout) findViewById(R.id.wlm_legendholder);
        if (legendImageView==null) {
            legendImageView = (ImageView) findViewById(R.id.wlm_legend);
        }
        if (legendHookView==null){
            legendHookView = (RelativeLayout) findViewById(R.id.wlm_legend_hook);
        }
        if (legendHolder==null){
            // legendHolder not present in portrait layout
        }
        if (!weatherLayer.isPollen()){
            legendImageView.setVisibility(View.VISIBLE);
            legendImageView.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
            legendHookView.setVisibility(View.INVISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable=true;
            Bitmap legendBitmap = null;
            int legendResource = R.drawable.uvi_legend_horizontal;
            if (legendHolder==null){
                // is portrait mode
                if (weatherLayer.legendType==WeatherLayer.Legend.TS){
                    legendResource = R.drawable.legend_ts_horizontal;
                } else {
                    legendResource = R.drawable.uvi_legend_horizontal;
                }
            } else {
                if (weatherLayer.legendType==WeatherLayer.Legend.TS) {
                    legendResource = R.drawable.legend_ts_vertical;
                } else {
                    legendResource = R.drawable.uvi_legend_vertical;
                }
            }
            legendBitmap = BitmapFactory.decodeResource(getResources(),legendResource,options);
            legendBitmap = WeatherLayer.replaceBitmapColor(legendBitmap,0xff3d3d3d,ThemePicker.getWidgetTextColor(context));
            legendImageView.setImageBitmap(legendBitmap);
        } else {
            legendImageView.setVisibility(View.INVISIBLE);
            legendHookView.setVisibility(View.VISIBLE);
            legendHookView.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
            if (!pollenLegendInitated){
                initPollenLegend();
            }
        }
    }
}
