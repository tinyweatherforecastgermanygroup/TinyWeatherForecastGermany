package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.os.PersistableBundle;
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
    TextView titleTextView;
    ImageView legendImageView;
    ImageView mapImageView;
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
                    layer = intent.getIntExtra(LAYER, WeatherLayer.Layers.WARNING_AREAS_GERMANY);
                }
            }
        }
        if (layer==-1){
            layer = 0;
        }
        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        // titleTextView will be null in landscape mode
        titleTextView = (TextView) findViewById(R.id.wlm_title);
        spinnerHolder = (LinearLayout) findViewById(R.id.wlm_spinnerholder);
        spinner1 = (Spinner) findViewById(R.id.wlm_spinner1);
        spinner2 = (Spinner) findViewById(R.id.wlm_spinner2);
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
        int i=0;
        while ((browseItemsOrder[i]!=layer) && (i<browseItemsOrder.length)){
            i++;
        }
        return i;
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

    public void jumpToLayer(int newPosition){
        if (newPosition<0){
            newPosition = browseItemsOrder.length-1;
        }
        if (newPosition>=browseItemsOrder.length){
            newPosition=0;
        }
        layer = newPosition;
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

    public ArrayList<String> getTimeList(){
        ArrayList<String> result = new ArrayList<String>();
        result.add(removeSpecialChars(getResources().getString(R.string.today)));
        result.add(removeSpecialChars(getResources().getString(R.string.tomorrow)));
        result.add(removeSpecialChars(getResources().getString(R.string.dayaftertomorrow)));
        return result;
    }

    public ArrayList<String> getPollenList(){
        ArrayList<String> result = new ArrayList<String>();
        result.add(getResources().getString(R.string.pollen_ambrosia));
        result.add(getResources().getString(R.string.pollen_mugwort));
        result.add(getResources().getString(R.string.pollen_rye));
        result.add(getResources().getString(R.string.pollen_ash));
        result.add(getResources().getString(R.string.pollen_birch));
        result.add(getResources().getString(R.string.pollen_hazel));
        result.add(getResources().getString(R.string.pollen_alder));
        result.add(getResources().getString(R.string.pollen_grasses));
        return result;
    }

    public ArrayList<String> getCloudList(){
        ArrayList<String> result = new ArrayList<String>();
        result.add(removeSpecialChars(getResources().getString(R.string.clouds)));
        result.add(removeSpecialChars(getResources().getString(R.string.clear_sky)));
        return result;
    }

    public ArrayList<String> getTodaySensedTemeraturesList(){
        ArrayList<String> result = new ArrayList<String>();
        result.add(getResources().getString(R.string.layerlabel_short_ts));
        return result;
    }

    public ArrayList<String> getTodaySensedTemeratureTimesList(){
        ArrayList<String> result = new ArrayList<String>();
        result.add(getResources().getString(R.string.local_time_6));
        result.add(getResources().getString(R.string.local_time_12));
        result.add(getResources().getString(R.string.local_time_18));
        return result;
    }

    public ArrayList<String> getMinMaxTemperatureList(){
        ArrayList<String> result = new ArrayList<String>();
        result.add(getResources().getString(R.string.temp_min));
        result.add(getResources().getString(R.string.temp_max));
        return result;
    }

    final MainActivity.SpinnerListener spinnerClickListener = new MainActivity.SpinnerListener() {
        @Override
        public void handleItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            int newPosition = -1;
            if ((layer>= WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_0) &&
                    (layer<= WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_2)){
                newPosition = WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_0+spinner1.getSelectedItemPosition()*3+spinner2.getSelectedItemPosition();
            }
            if ((layer>= WeatherLayer.Layers.UVI_CLOUDS_0) &&
                    (layer<=WeatherLayer.Layers.UVI_CLOUDLESS_2)){
                newPosition = spinner1.getSelectedItemPosition()*3+1+spinner2.getSelectedItemPosition();
            }
            if ((layer>=WeatherLayer.Layers.SENSED_TEMPERATURE_1M_0) && (layer<=WeatherLayer.Layers.SENSED_TEMPERATURE_1M_2)){
                newPosition = spinner2.getSelectedItemPosition()+9;
            }
            if ((layer>=WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_0) && (layer<=WeatherLayer.Layers.SENSED_TEMPERATURE_MIN_2)){
                if (spinner1.getSelectedItemPosition()==0){
                    newPosition = WeatherLayer.Layers.SENSED_TEMPERATURE_MIN_0 + spinner2.getSelectedItemPosition();
                }
                if (spinner1.getSelectedItemPosition()==1){
                    newPosition = WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_0 + spinner2.getSelectedItemPosition();
                }
            }
            if (newPosition>=0){
                //Log.v("twfg","New layer is => "+newPosition);
                jumpToLayer(newPosition);
            }
        }
    };

    public void attachSpinner(){
        int pos1=-1; int pos2=-1;
        if ((layer>= WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_0) &&
                (layer<=WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_2)){
            ArrayList<String> spinnerItems1 = getPollenList();
            adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,spinnerItems1);
            ArrayList<String> spinnerItems2 = getTimeList();
            adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,spinnerItems2);
            pos1 = (layer - WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_0)/3;
            pos2 = (layer - WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_0)%3;
        }
        if ((layer>= WeatherLayer.Layers.UVI_CLOUDS_0) &&
                (layer<=WeatherLayer.Layers.UVI_CLOUDLESS_2)){
            ArrayList<String> spinnerItems1 = getCloudList();
            adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,spinnerItems1);
            ArrayList<String> spinnerItems2 = getTimeList();
            adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,spinnerItems2);
            pos1 = 0;
            pos2 = layer-1;
            if ((layer>=WeatherLayer.Layers.UVI_CLOUDLESS_0) && (layer<=WeatherLayer.Layers.UVI_CLOUDLESS_2)){
                pos1=1; // clouds
                pos2=layer-4;
            }
        }
        if ((layer>=WeatherLayer.Layers.SENSED_TEMPERATURE_1M_0) && (layer<=WeatherLayer.Layers.SENSED_TEMPERATURE_1M_2)){
            ArrayList<String> spinnerItems1 = getTodaySensedTemeraturesList();
            adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,spinnerItems1);
            ArrayList<String> spinnerItems2 = getTodaySensedTemeratureTimesList();
            adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,spinnerItems2);
            pos1 = 0;
            pos2 = layer-9;
        }
        if ((layer>=WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_0) && (layer<=WeatherLayer.Layers.SENSED_TEMPERATURE_MIN_2)){
            ArrayList<String> spinnerItems1 = getMinMaxTemperatureList();
            adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,spinnerItems1);
            ArrayList<String> spinnerItems2 = getTimeList();
            adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,spinnerItems2);
            if ((layer>=WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_0) && (layer<=WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_2)){
                pos1 = 1;
                pos2 = layer - WeatherLayer.Layers.SENSED_TEMPERATURE_MAX_0;
            } else {
                pos1 = 0;
                pos2 = layer - WeatherLayer.Layers.SENSED_TEMPERATURE_MIN_0;
            }
        }
        //Log.v("twfg","SPINNER "+pos1+"/"+pos2);
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
            spinnerHolder.setVisibility(View.INVISIBLE);
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
        final WeatherLayer weatherLayer = new WeatherLayer(context,layer);
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

        Bitmap visibleBitmap = weatherLayer.getLayerBitmap(context);
        // layer might be null due to day-update shifts
        if (visibleBitmap!=null){
            zoomableImageView = new ZoomableImageView(context,mapImageView,visibleBitmap,false){
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
        drawLegend(weatherLayer);
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
