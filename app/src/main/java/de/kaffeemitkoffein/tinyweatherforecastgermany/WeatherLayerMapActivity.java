package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
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
    ActionBar actionBar;
    Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePicker.SetTheme(this);
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_weatherlayermap);
        executor = Executors.newSingleThreadExecutor();
        // get last displayed layer if no intent or extra
        layer = WeatherSettings.getLastDisplayedLayer(context);
        // get layer from intent if possible
        Intent intent = getIntent();
        if (intent!=null){
            if (intent.hasExtra(LAYER)){
                layer = intent.getIntExtra(LAYER, WeatherLayer.Layers.WARNING_AREAS_GERMANY);
            }
        }
        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        // titleTextView will be null in landscape mode
        titleTextView = (TextView) findViewById(R.id.wlm_title);
        displayMap();
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
        while ((WeatherLayer.browseItemsOrder[i]!=layer) && (i<WeatherLayer.browseItemsOrder.length)){
            i++;
        }
        return i;
    }

    public void changeMap(int direction){
        int currentPosition = findLayerPosition(layer);
        int newPosition = currentPosition + direction;
        if (newPosition<0){
            newPosition = WeatherLayer.browseItemsOrder.length-1;
        }
        if (newPosition>=WeatherLayer.browseItemsOrder.length){
            newPosition=0;
        }
        layer = WeatherLayer.browseItemsOrder[newPosition];
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

    public void displayMap(){
        if (titleTextView!=null){
            // in portrait mode, display title in main view
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
        ImageView mapImageView = (ImageView) findViewById(R.id.wlm_map);
        Bitmap visibleBitmap = weatherLayer.getLayerBitmap(context);
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
        drawLegend(weatherLayer);
    }

    public void drawLegend(WeatherLayer weatherLayer){
        if (legendImageView==null) {
            legendImageView = (ImageView) findViewById(R.id.wlm_legend);
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable=true;
        Bitmap legendBitmap = null;
        int legendResource = R.drawable.uvi_legend_horizontal;
        if (titleTextView!=null){
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
    }
}
