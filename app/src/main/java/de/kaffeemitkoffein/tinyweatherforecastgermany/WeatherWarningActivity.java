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

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class WeatherWarningActivity extends Activity {

    ArrayList<WeatherWarning> weatherWarnings;
    ArrayList<WeatherWarning> localWarnings;
    Weather.WeatherLocation ownLocation;
    View.OnTouchListener mapTouchListener;
    ArrayList<Polygon> polygoncache;
    ArrayList<Polygon> excluded_polygoncache;
    ImageView germany;
    ImageView warningactivity_map_collapsed;
    RelativeLayout mapcontainer;
    ProgressBar rainSlideProgressBar;
    TextView rainSlideProgressBarText;
    TextView rainSlideTime;
    TextView mapAttributionText;
    ImageView rainDescription;
    Bitmap germanyBitmap;
    Bitmap warningsBitmap;
    Bitmap radarBitmap;
    Bitmap administrativeBitmap;
    Bitmap visibleBitmap;
    ZoomableImageView mapZoomable;
    RelativeLayout map_collapsed_container;
    boolean deviceIsLandscape;
    private GestureDetector gestureDetector;
    ListView weatherList;
    WeatherWarningAdapter weatherWarningAdapter;
    Context context;
    ActionBar actionBar;
    ScheduledExecutorService scheduledExecutorService;
    boolean hide_rain = false;
    boolean hide_admin = true;
    WeatherLocationManager weatherLocationManager;
    RelativeLayout gpsProgressHolder;

    Bundle zoomMapState = null;

    final static RadarMN2.MercatorProjectionTile mercatorProjectionTile = RadarMN2.getRadarMapMercatorProjectionTile();

    boolean forceWeatherUpdateFlag = false;

    static float MAP_PIXEL_WIDTH;
    static float MAP_PIXEL_HEIGHT;

    public final static String WEATHER_WARNINGS_UPDATE="WEATHER_WARNINGS_UPDATE";
    public final static String WEATHER_WARNINGS_UPDATE_RESULT="WEATHER_WARNINGS_UPDATE_RESULT";
    public final static String ACTION_RAINRADAR_UPDATE="ACTION_RAINRADAR_UPDATE";

    public final static String SIS_ZOOMMAPSTATEBUNDLE="ZOOMMAPSTATEBUNDLE";
    public final static String SIS_HIDERAIN="HIDERAIN";
    public final static String SIS_HIDEADMIN="HIDEADMIN";

    PopupWindow hintPopupWindow = null;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
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
                if (intent.getAction().equals(MainActivity.MAINAPP_HIDE_PROGRESS)){
                    forceWeatherUpdateFlag = false;
                    hideProgressBar();
                }
            }
        }
    };

    private int nextRainSlide = 0;
    private long rainSlidesStartTime = 0;
    private boolean cancelRainSlides = false;
    private boolean validSlideSetObtained = false;
    public final static int RAINSLIDEDELAY=750;
    private int[] rainRadarData;

    private final Runnable showNextRainSlide = new Runnable() {
        @Override
        public void run() {
            long startTime = Calendar.getInstance().getTimeInMillis();
            drawRadarSlide(nextRainSlide);
            long finishedTime = Calendar.getInstance().getTimeInMillis();
            long duration = finishedTime - startTime;
            nextRainSlide++;
            if (nextRainSlide>APIReaders.RadarMNSetGeoserverRunnable.DATASET_SIZE){
                nextRainSlide=0;
            }
            if ((!cancelRainSlides) && (validSlideSetObtained)){
                if (RAINSLIDEDELAY-duration>0){
                    scheduledExecutorService.schedule(showNextRainSlide,RAINSLIDEDELAY-duration,TimeUnit.MILLISECONDS);
                } else {
                    scheduledExecutorService.execute(showNextRainSlide);
                }
            }
            cancelRainSlides = false;
        }
    };

    APIReaders.RadarMNSetGeoserverRunnable radarMNSetGeoserverRunnable;

    View.OnTouchListener forwardRainSlidesOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            float position = motionEvent.getX()/ view.getWidth();
            nextRainSlide = Math.round(position * 24f);
            return true;
        }
    };

    APIReaders.WeatherWarningsRunnable weatherWarningsUpdateRunnable;

    @Override
    public void onSaveInstanceState(Bundle state){
        if (mapZoomable != null){
            zoomMapState = mapZoomable.saveZoomViewState();
        }
        if (zoomMapState!=null){
            state.putBundle(SIS_ZOOMMAPSTATEBUNDLE,zoomMapState);
        }
        state.putBoolean(SIS_HIDERAIN,hide_rain);
        state.putBoolean(SIS_HIDEADMIN,hide_admin);
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state){
        super.onRestoreInstanceState(state);
        // do nothing here for the moment
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerForBroadcast();
        if (germany==null){
            germany = (ImageView) findViewById(R.id.warningactivity_map);
        }
        PrivateLog.log(getApplicationContext(),PrivateLog.WARNINGS,PrivateLog.INFO,"app resumed.");
        if (WeatherSettings.GPSAuto(context)){
            weatherLocationManager.checkLocation();
        }
        if ((!hide_rain) && (!cancelRainSlides) && (validSlideSetObtained)){
            scheduledExecutorService.execute(showNextRainSlide);
        }
        if (WeatherSettings.Updates.isSyncDue(context,WeatherSettings.Updates.Category.WARNINGS)){
            PrivateLog.log(context,PrivateLog.WARNINGS,PrivateLog.INFO,"Weather warnings are outdated, updating data.");
            scheduledExecutorService.execute(weatherWarningsUpdateRunnable);
        } else {
            weatherWarnings = WeatherWarnings.getCurrentWarnings(getApplicationContext(),true);
            PrivateLog.log(context,PrivateLog.WARNINGS,PrivateLog.INFO,"Weather warnings are up to date, showing the data available.");
            displayWarnings();
        }
    }

    @Override
    protected void onPause(){
        cancelRainSlides = true;
        unregisterReceiver(receiver);
        super.onPause();
        PrivateLog.log(getApplicationContext(),PrivateLog.WARNINGS,PrivateLog.INFO,"app paused.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hintPopupWindow!=null){
            if (hintPopupWindow.isShowing()){
                hintPopupWindow.dismiss();
                WeatherSettings.setHintCounter2(context,WeatherSettings.getHintCounter2(context)-1);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PrivateLog.log(getApplicationContext(),PrivateLog.WARNINGS,PrivateLog.INFO,"WeatherWarningActivity started.");
        try {
            ThemePicker.SetTheme(this);
        } catch (Exception e){
            PrivateLog.log(context,PrivateLog.WARNINGS,PrivateLog.INFO,"Error setting theme in WeatherWarnings activity.");
        }
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        rainSlidesStartTime = WeatherSettings.getPrefRadarLastdatapoll(context);
        WeatherSettings.setRotationMode(this);
        setContentView(R.layout.activity_weatherwarning);
        registerForBroadcast();
        // try to restore zoom factor if it is available from the savedInstanceState
        if (savedInstanceState!=null){
            Bundle bundle = savedInstanceState.getBundle(SIS_ZOOMMAPSTATEBUNDLE);
            if (bundle!=null){
                zoomMapState = bundle;
            }
            hide_rain = savedInstanceState.getBoolean(SIS_HIDERAIN,!WeatherSettings.showRadarByDefault(getApplicationContext()));
            hide_admin = savedInstanceState.getBoolean(SIS_HIDEADMIN,!WeatherSettings.showAdminMapByDefault(getApplicationContext()));
        } else {
            hide_rain = !WeatherSettings.showRadarByDefault(getApplicationContext());
            hide_admin = !WeatherSettings.showAdminMapByDefault(getApplicationContext());
        }
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // action bar layout
        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        mapcontainer = (RelativeLayout) findViewById(R.id.warningactivity_mapcontainer);
        map_collapsed_container = (RelativeLayout) findViewById(R.id.warningactivity_map_collapsed_container);
        warningactivity_map_collapsed = (ImageView) findViewById(R.id.warningactivity_map_collapsed);
        if (warningactivity_map_collapsed!=null){
            warningactivity_map_collapsed.setImageResource(WeatherIcons.getIconResource(context,WeatherIcons.MAP_COLLAPSED));
        }
        // in layout w6600dp-land this element does not exist. This is the safest way to
        // limit collapse-function to portrait mode.
        if (map_collapsed_container!=null){
            deviceIsLandscape = false;
            map_collapsed_container.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (germany!=null){
                        showMap();
                    }
                    return true;
                }
            });
        } else {
            deviceIsLandscape = true;
        }
        rainSlideProgressBar = (ProgressBar) findViewById(R.id.warningactivity_rainslideprogressbar);
        rainSlideProgressBarText = (TextView) findViewById(R.id.warningactivity_rainslideprogressbartext);
        rainSlideTime = (TextView) findViewById(R.id.warningactivity_rainslidetime);
        rainSlideTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextRainSlide=0;
            }
        });
        rainDescription = (ImageView) findViewById(R.id.warningactivity_mapinfo);
        rainDescription.setOnTouchListener(forwardRainSlidesOnTouchListener);
        gpsProgressHolder = (RelativeLayout) findViewById(R.id.gps_progress_holder);
        mapAttributionText = (TextView) findViewById(R.id.warningactivity_mapattribution);
        //mapAttributionText.setText(Html.fromHtml(context.getResources().getString(R.string.map_attribution)));
        mapAttributionText.setVisibility(View.VISIBLE);
        mapAttributionText.setMovementMethod(LinkMovementMethod.getInstance());
        if (!WeatherSettings.appReleaseIsUserdebug()){
            mapAttributionText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mapAttributionText.setVisibility(View.GONE);
                        }
                    });
                }
            },7000);
        }
        radarMNSetGeoserverRunnable = new APIReaders.RadarMNSetGeoserverRunnable(getApplicationContext()){
            @Override
            public void onProgress(long startTime, final int progress) {
                if (progress==0){
                    rainSlidesStartTime = startTime;
                    drawRadarSlide(0);
                }
                updateRainSlideProgressBar(progress);
            }
            @Override
            public void onFinished(long startTime, boolean success){
                super.onFinished(startTime,success);
                nextRainSlide = 0;
                rainSlidesStartTime = startTime;
                if (success){
                    validSlideSetObtained = true;
                    scheduledExecutorService.execute(showNextRainSlide);
                } else {
                    validSlideSetObtained = false;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rainSlideProgressBar.setVisibility(View.INVISIBLE);
                        rainSlideProgressBarText.setVisibility(View.INVISIBLE);
                    }
                });
            }
        };
        weatherLocationManager = new WeatherLocationManager(context){
          @Override
          public void newLocation(Location location){
              super.newLocation(location);
              Weather.WeatherLocationFinder weatherLocationFinder = new Weather.WeatherLocationFinder(context,location){
                @Override
                public void newWeatherLocation(Weather.WeatherLocation weatherLocation){
                    ownLocation = weatherLocation;
                    displayWarnings();
                }
              };
              weatherLocationFinder.run();
          }
        };
        weatherWarningsUpdateRunnable = new APIReaders.WeatherWarningsRunnable(getApplicationContext()) {
            @Override
            public void onStart() {
                showProgressBar();
                super.onStart();
            }
            @Override
            public void onNegativeResult() {
                hideProgressBar();
                displayWarnings();
                PrivateLog.log(context,PrivateLog.WARNINGS,PrivateLog.ERR,"Getting warnings failed.");
                super.onNegativeResult();
            }
            @Override
            public void onPositiveResult(ArrayList<WeatherWarning> warnings) {
                hideProgressBar();
                WeatherSettings.Updates.setLastUpdate(context,WeatherSettings.Updates.Category.WARNINGS,Calendar.getInstance().getTimeInMillis());
                PrivateLog.log(context,PrivateLog.WARNINGS,PrivateLog.INFO,"Updated warnings: "+warnings.size()+" records.");
                weatherWarnings = warnings;
                for (int i=0; i<weatherWarnings.size(); i++){
                    weatherWarnings.get(i).initPolygons(context);
                }
                super.onPositiveResult(warnings);
                // finally do a sync of other parameters; if nothing is due, nothing will happen
                // warnings will be also ignored, because setLastUpdate was called.
                scheduledExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        ContentResolver.requestSync(MainActivity.getManualSyncRequest(context,WeatherSyncAdapter.UpdateFlags.FLAG_UPDATE_DEFAULT));
                    }
                });
                displayWarnings();
            }
        };
        WeatherSettings.saveGPSfixtime(context,0);
        // set to station, perhaps override with current location later
        ownLocation = WeatherSettings.getSetStationLocation(getApplicationContext());
        getApplication().registerActivityLifecycleCallbacks(weatherLocationManager);
        weatherLocationManager.setView(gpsProgressHolder);
        weatherLocationManager.registerCancelButton((Button) findViewById(R.id.cancel_gps));
        popupHint();
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
            PrivateLog.log(this, PrivateLog.WARNINGS,PrivateLog.INFO, "starting update of weather warnings");
            SyncRequest syncRequest = MainActivity.getManualSyncRequest(context,WeatherSyncAdapter.UpdateFlags.FLAG_UPDATE_WARNINGS);
            ContentResolver.requestSync(syncRequest);
            forceWeatherUpdateFlag = true;
            showProgressBar();
            // force update or rain radar if shown
            if (!hide_rain){
                    cancelRainSlides=true;
            }
            return true;
        }
        if (item_id==R.id.hide_rain) {
            if ((hide_rain) && (hide_admin)){
                hide_rain = false;
            } else
            if ((!hide_rain) && (hide_admin)){
                hide_admin = false;
            } else
            if (!hide_rain) {
                hide_rain = true;
            }
            else {
                hide_rain = true; hide_admin = true;
            }
            // hide_rain = !hide_rain;
            drawMapBitmap();
            return true;
        }
        return super.onOptionsItemSelected(mi);
    }

    public void updateActionBarLabels(){
        final SimpleDateFormat simpleDateFormat = Weather.getSimpleDateFormat(Weather.SimpleDateFormats.DATEYEARTIME);
        String update = simpleDateFormat.format(WeatherSettings.Updates.getLastUpdate(context,WeatherSettings.Updates.Category.WARNINGS));
        if (weatherWarnings!=null){
            actionBar.setSubtitle(update+" ("+weatherWarnings.size()+")");
        } else {
            actionBar.setSubtitle(getApplicationContext().getResources().getString(R.string.warnings_update_fail));
        }
    }

    private void displayWarnings(){
        if (weatherWarnings!=null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateActionBarLabels();
                }
            });
            TextView noWarnings = (TextView) findViewById(R.id.warningactivity_no_warnings);
            if (weatherWarnings.size()==0){
                noWarnings.setVisibility(View.VISIBLE);
            } else {
                noWarnings.setVisibility(View.GONE);
            }
            TextView warningsDeprecated = (TextView) findViewById(R.id.warningactivity_warnings_deprecated);
            if (WeatherSettings.Updates.isSyncDue(context,WeatherSettings.Updates.Category.WARNINGS)){
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
                        weatherWarningAdapter = new WeatherWarningAdapter(getBaseContext(),weatherWarnings,scheduledExecutorService);
                        weatherWarningAdapter.setLocalWarnings(localWarnings);
                        weatherList.setAdapter(weatherWarningAdapter);
                        weatherList.setSelection(WeatherWarnings.getFirstWarningPosition(weatherWarnings,localWarnings));
                        weatherList.invalidate();
                    }
                });
            }
        };
        scheduledExecutorService.execute(getWarningsForLocationRunnable);
        if (weatherWarnings!=null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayMap();
                }
            });
        }
    }

    public static class PlotPoint{
        float x;
        float y;
    }

    public static PlotPoint getPlotPoint(float lon, float lat){
        PlotPoint plotPoint = new PlotPoint();
        plotPoint.x = (float) mercatorProjectionTile.getXPixel(lon);
        plotPoint.y = (float) mercatorProjectionTile.getYPixel(lat);
        return plotPoint;
    }

    private float getXGeo(PlotPoint plotPoint){
        float xCoord = (float) mercatorProjectionTile.getXCoord(plotPoint.x);
        return xCoord;
    }

    private float getYGeo(PlotPoint plotPoint){
        float yCoord = (float) mercatorProjectionTile.getYCoord(plotPoint.y);
        return yCoord;
    }

    private void drawStrokedText(Canvas canvas, String text, float x, float y, Paint paint){
        Paint strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        strokePaint.setTypeface(Typeface.DEFAULT);
        strokePaint.setTextSize(paint.getTextSize());
        strokePaint.setAntiAlias(true);
        int shiftX = Math.max(2,germany.getWidth()/RadarMN2.getFixedRadarMapWidth());
        int shiftY = Math.max(2,germany.getHeight()/RadarMN2.getFixedRadarMapHeight());
        canvas.drawText(text,x-shiftX,y,strokePaint);
        canvas.drawText(text,x+shiftX,y,strokePaint);
        canvas.drawText(text,x,y-shiftY,strokePaint);
        canvas.drawText(text,x,y+shiftY,strokePaint);
        canvas.drawText(text,x,y,paint);
    }

    private void showRainDescription(){
        Bitmap infoBitmap=Bitmap.createBitmap(Math.round(MAP_PIXEL_WIDTH),Math.round(MAP_PIXEL_HEIGHT*0.12f), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(infoBitmap);
        Bitmap radarinfobarResourceBitmap;
        radarinfobarResourceBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADARINFOBAR)),Math.round(MAP_PIXEL_WIDTH),34,false);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date rainSlideDate = new Date(rainSlidesStartTime+(nextRainSlide)*APIReaders.RadarMNSetGeoserverRunnable.TIMESTEP_5MINUTES);
        String radartime = simpleDateFormat.format(rainSlideDate);
        float ff=1.1f;
        //drawStrokedText(canvas,radartime,MAP_PIXEL_WIDTH/100,infoBitmap.getHeight()-radarinfobarResourceBitmap.getHeight()*ff*2,radarTextPaint);
        if (validSlideSetObtained) {
            rainSlideTime.setTextColor(Color.WHITE);
            if (Calendar.getInstance().getTimeInMillis() > rainSlidesStartTime + +1000*60*60*1.5f){
                rainSlideTime.setTextColor(0xfffa7712);
            }
        } else {
            rainSlideTime.setTextColor(Color.YELLOW);
        }
        rainSlideTime.setText(radartime);
        radarTextPaint.setColor(Radarmap.RAINCOLORS[2]);
        drawStrokedText(canvas,getResources().getString(R.string.radar_rain1),MAP_PIXEL_WIDTH*0.1f,infoBitmap.getHeight()-radarinfobarResourceBitmap.getHeight()*ff,radarTextPaint);
        radarTextPaint.setColor(Radarmap.RAINCOLORS[7]);
        drawStrokedText(canvas,getResources().getString(R.string.radar_rain2),MAP_PIXEL_WIDTH*0.3f,infoBitmap.getHeight()-radarinfobarResourceBitmap.getHeight(),radarTextPaint);
        radarTextPaint.setColor(Radarmap.RAINCOLORS[11]);
        drawStrokedText(canvas,getResources().getString(R.string.radar_rain3),MAP_PIXEL_WIDTH*0.6f,infoBitmap.getHeight()-radarinfobarResourceBitmap.getHeight(),radarTextPaint);
        radarTextPaint.setColor(Radarmap.RAINCOLORS[16]);
        drawStrokedText(canvas,getResources().getString(R.string.radar_rain4),MAP_PIXEL_WIDTH*0.8f,infoBitmap.getHeight()-radarinfobarResourceBitmap.getHeight(),radarTextPaint);
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
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),res_id),RadarMN2.getFixedRadarMapWidth(),RadarMN2.getFixedRadarMapHeight(),false);
        return bitmap;
    }

    private void updateRainSlideProgressBar(final int progress){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rainSlideProgressBar.setVisibility(View.VISIBLE);
                rainSlideProgressBar.setProgress(progress);
                rainSlideProgressBar.invalidate();
                rainSlideProgressBarText.setVisibility(View.VISIBLE);
                rainSlideProgressBarText.setText(Math.round((float) progress/(float) (APIReaders.RadarMNSetGeoserverRunnable.DATASET_SIZE-1)*100f)+"%");
                rainSlideProgressBarText.invalidate();
            }
        });
    }

    /*
    private void drawRadarSlide(final int count){
        if (APIReaders.RadarMNSetGeoserverRunnable.radarCacheFileValid(context,count)) {
            rainRadarData = RadarMN.getData(context, count);
            int[] target = new int[radarBitmap.getHeight()*radarBitmap.getWidth()];
            float yPS = (MAP_PIXEL_HEIGHT / RadarMN.RADARMAP_PIXEL_FIXEDHEIGHT) / 2f + 1;
            float xPS = (MAP_PIXEL_WIDTH / RadarMN.RADARMAP_PIXEL_FIXEDWIDTH) / 2f + 1;
            radarBitmap.eraseColor(Color.TRANSPARENT);
            Canvas radarCanvas = new Canvas(radarBitmap);
            Paint rpaint = new Paint();
            rpaint.setStyle(Paint.Style.FILL_AND_STROKE);
            for (int y = 0; y < RadarMN.RADARMAP_PIXEL_FIXEDHEIGHT; y++) {
                for (int x = 0; x < RadarMN.RADARMAP_PIXEL_FIXEDWIDTH; x++) {
                    if (rainRadarData[x + y * RadarMN.RADARMAP_PIXEL_FIXEDWIDTH]!=Color.TRANSPARENT){
                        rpaint.setColor(rainRadarData[x + y * RadarMN.RADARMAP_PIXEL_FIXEDWIDTH]);
                  Log.v("twfg","Slide cache file NOT valid: "+count);                  PlotPoint plotPoint = getPlotPoint(RadarMN.getGeoX(x,y),RadarMN.getGeoY(x,y));
                        radarCanvas.drawRect(plotPoint.x-xPS,plotPoint.y-yPS,plotPoint.x+xPS,plotPoint.y+yPS,rpaint);
                    }
                }
            }
            if (!hide_rain) {
                drawMapBitmap();
            }
        } else {
            // nothing to do
        }
    }

     */

    private void drawRadarSlide(final int count){
        if (APIReaders.RadarMNSetGeoserverRunnable.radarCacheFileValid(context,count)) {
            radarBitmap.eraseColor(Color.TRANSPARENT);
            Canvas radarCanvas = new Canvas(radarBitmap);
            Bitmap slideBitmap = RadarMN2.getScaledBitmap(context,count);
            final Paint radarPaint = new Paint();
            radarPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            if (slideBitmap!=null){
                radarCanvas.drawBitmap(slideBitmap,0,0,radarPaint);
            }
            if (!hide_rain) {
                drawMapBitmap();
            }
        } else {
            // nothing to do
        }
    }

    private void drawMapBitmap(){
        visibleBitmap = germanyBitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(visibleBitmap);
        final Paint cp = new Paint();
        cp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(warningsBitmap,0,0,cp);
        if ((!hide_admin)){
            if (administrativeBitmap==null){
                administrativeBitmap = getAdministrativeBitmap(context,germanyBitmap.getWidth(),germanyBitmap.getHeight(),WeatherSettings.getAreaTypeArray(context));
            }
            cp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            canvas.drawBitmap(administrativeBitmap,0,0,cp);
        }
        if ((!hide_rain) && (radarBitmap!=null)){
            cp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            canvas.drawBitmap(radarBitmap, 0,0,cp);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showRainDescription();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    clearRainDescription();
                }
            });
        }
        mapZoomable.updateBitmap(visibleBitmap);
        visibleBitmap.recycle();
    }

    private void drawWindIcon(){
        final Context context = getApplicationContext();
        final ImageView windicon = (ImageView) findViewById(R.id.warningactivity_windicon);
        Runnable windRunnable = new Runnable() {
            @Override
            public void run() {
                final RelativeLayout windiconContainer = (RelativeLayout) findViewById(R.id.warningactivity_windicon_container);
                if (windiconContainer!=null){
                    if (WeatherSettings.displayWindInRadar(getApplicationContext())){
                        windiconContainer.setVisibility(View.VISIBLE);
                        final ImageView windiconBackground = (ImageView) findViewById(R.id.warningactivity_windicon_background);
                        CurrentWeatherInfo currentWeatherInfo = Weather.getCurrentWeatherInfo(context);
                        if (currentWeatherInfo!=null){
                            final Bitmap windiconBitmap = currentWeatherInfo.currentWeather.getWindSymbol(getApplicationContext(),WeatherSettings.getWindDisplayType(getApplicationContext()),false);
                            ThemePicker.applyColor(context,windiconBitmap,false);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    windiconBackground.setImageResource(R.drawable.blue);
                                    windiconBackground.setColorFilter(ThemePicker.getColorPrimary(context),PorterDuff.Mode.SRC_IN);
                                    windicon.setImageBitmap(windiconBitmap);
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                windiconContainer.setVisibility(View.INVISIBLE);
                            }
                        });

                    }
                }
            }
        };
        scheduledExecutorService.execute(windRunnable);
    }

    private boolean hideMap(){
        germany.setVisibility(View.GONE);
        germany.invalidate();
        LinearLayout.LayoutParams rllp = (LinearLayout.LayoutParams) mapcontainer.getLayoutParams();
        rllp.height=0;
        rllp.weight=0;
        mapcontainer.setLayoutParams(rllp);
        mapcontainer.invalidate();
        mapcontainer.setVisibility(View.GONE);
        LinearLayout.LayoutParams mclp = (LinearLayout.LayoutParams) map_collapsed_container.getLayoutParams();
        map_collapsed_container.setVisibility(View.VISIBLE);
        mclp.weight=1;
        map_collapsed_container.setLayoutParams(mclp);
        map_collapsed_container.invalidate();
        LinearLayout.LayoutParams lop = (LinearLayout.LayoutParams) weatherList.getLayoutParams();
        lop.weight=19;
        weatherList.setLayoutParams(lop);
        weatherList.invalidate();
        return true;
    }

    private boolean showMap(){
        germany.setVisibility(View.VISIBLE);
        mapcontainer.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams rllp = (LinearLayout.LayoutParams) mapcontainer.getLayoutParams();
        rllp.height=0;
        rllp.weight=14;
        mapcontainer.setLayoutParams(rllp);
        mapcontainer.invalidate();
        LinearLayout.LayoutParams mclp = (LinearLayout.LayoutParams) map_collapsed_container.getLayoutParams();
        mclp.height=0;
        mclp.weight=0;
        map_collapsed_container.setLayoutParams(mclp);
        map_collapsed_container.invalidate();
        map_collapsed_container.setVisibility(View.GONE);
        LinearLayout.LayoutParams lop = (LinearLayout.LayoutParams) weatherList.getLayoutParams();
        lop.height=0;
        lop.weight=6;
        weatherList.setLayoutParams(lop);
        weatherList.invalidate();
        return true;
    }
    
    private void displayMap(){
        germanyBitmap = loadBitmapMap(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.GERMANY));
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

        /* old rain radar
        APIReaders.RadarMNGeoserverRunnable radarMNGeoserverRunnable = new APIReaders.RadarMNGeoserverRunnable(getApplicationContext()){
            @Override
            public void onFinished(final RadarMN radarMN){
                // override to do something with the map
                if (radarMN!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            float yPS = (MAP_PIXEL_HEIGHT/RadarMN.RADARMAP_PIXEL_FIXEDHEIGHT)/2f+1;
                            float xPS = (MAP_PIXEL_WIDTH/RadarMN.RADARMAP_PIXEL_FIXEDWIDTH)/2f+1;
                            Canvas radarCanvas = new Canvas(radarBitmap);
                            Paint rpaint = new Paint();
                            rpaint.setStyle(Paint.Style.FILL_AND_STROKE);
                            for (int y=0; y<RadarMN.RADARMAP_PIXEL_FIXEDHEIGHT; y++){
                                for (int x=0; x<RadarMN.RADARMAP_PIXEL_FIXEDWIDTH; x++){
                                    rpaint.setColor(radarMN.color[x][y]);
                                    PlotPoint plotPoint = getPlotPoint(radarMN.getGeoX(x,y),radarMN.getGeoY(x,y));
                                    radarCanvas.drawRect(plotPoint.x-xPS,plotPoint.y-yPS,plotPoint.x+xPS,plotPoint.y+yPS,rpaint);
                                    //radarCanvas.drawRect(x,y,x+xPS,y+yPS,rpaint);
                                }
                            }
                            if (!hide_rain){
                                drawMapBitmap();
                                //mapZoomable.updateBitmap(radarMN.getBitmap());
                            }
                        }
                    });

                }
            }
        };
        executor.execute(radarMNGeoserverRunnable);
        */
        drawWindIcon();
        if (Weather.suitableNetworkAvailable(context)){
            scheduledExecutorService.execute(radarMNSetGeoserverRunnable);
        }
        // set close listener
        ImageView closeImageview = (ImageView) findViewById(R.id.closeicon_map);
        if (closeImageview != null){
            closeImageview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return hideMap();
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
                final PlotPoint plotPoint = new PlotPoint();
                plotPoint.x = lastXtouch;
                plotPoint.y = lastYtouch;
                Runnable tapRunnable = new Runnable() {
                    @Override
                    public void run() {
                        checkForTapInPolygonWarning(getXGeo(plotPoint),getYGeo(plotPoint));
                    }
                };
                scheduledExecutorService.execute(tapRunnable);
            }
        };
        if (zoomMapState!=null){
            mapZoomable.restoreZoomViewState(zoomMapState);
        }
        drawMapBitmap();
        // add the pin sprite
        int pinsize = Math.round(18*this.getApplicationContext().getResources().getDisplayMetrics().density);
        PlotPoint pinPoint = getPlotPoint((float) ownLocation.longitude, (float) ownLocation.latitude);
        Bitmap pinBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.pin),pinsize,pinsize,false);
        mapZoomable.addSpite(pinBitmap,pinPoint.x,pinPoint.y-pinBitmap.getHeight(),ZoomableImageView.SPRITEFIXPOINT.BOTTOM_LEFT,null);
        mapZoomable.redrawBitmap();
        mapTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mapZoomable.onTouchEvent(motionEvent);
                return true;
            };
        };
        germany.setOnTouchListener(mapTouchListener);

    }

    class MapGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            // catch collapse-button-press prior to map-selection
            // perform this only if NOT in landscape layout
            if ((!deviceIsLandscape) && (map_collapsed_container!=null) && (germany!=null) && (weatherList!=null)){
                float button_border_right  = (float) (germany.getMeasuredWidth() * 0.127427184);
                float button_border_bottom = (float) (germany.getMeasuredHeight() * 0.10041841);
                if ((e.getX()<button_border_right) && (e.getY()<button_border_bottom)){
                    return hideMap();
                }
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onContextClick(MotionEvent e) {
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
        filter.addAction(MainActivity.MAINAPP_HIDE_PROGRESS);
        filter.addAction(ACTION_RAINRADAR_UPDATE);
        registerReceiver(receiver,filter);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int permRequestCode, String perms[], int[] grantRes){
        Boolean hasLocationPermission = false;
        for (int i=0; i<grantRes.length; i++){
            if ((perms[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) && (grantRes[i]== PackageManager.PERMISSION_GRANTED)){
                hasLocationPermission = true;
            }
        }
        if (permRequestCode == WeatherLocationManager.PERMISSION_CALLBACK_LOCATION){
            if (hasLocationPermission){
                if (weatherLocationManager!=null){
                    weatherLocationManager.checkLocation();
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    showLocationPermissionsRationale();
                }
            }
        }
    }

    private void showSimpleLocationAlert(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,0);
        builder.setTitle(getApplicationContext().getResources().getString(R.string.geoinput_title));
        Drawable drawable = new BitmapDrawable(getResources(),WeatherIcons.getIconBitmap(context,WeatherIcons.IC_GPS_FIXED,false));
        builder.setIcon(drawable);
        builder.setMessage(text);
        builder.setPositiveButton(R.string.alertdialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showLocationPermissionsRationale(){
        showSimpleLocationAlert(getApplicationContext().getResources().getString(R.string.geoinput_rationale));
    }

    public static Bitmap getAdministrativeBitmap(Context context, int targetWidth, int targetHeight, int[] types){
        Bitmap resultBitmap = Bitmap.createBitmap(targetWidth,targetHeight, Bitmap.Config.ARGB_8888);
        resultBitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(resultBitmap);
        for (int type=0; type<types.length; type++){
            ArrayList<Areas.Area> allAreas = Areas.getAreas(context, types[type]);
            Paint areaPaint = new Paint();
            areaPaint.setColor(Color.BLACK);
            areaPaint.setAlpha(96);
            areaPaint.setStyle(Paint.Style.STROKE);
            if (types[type]==Areas.Area.Type.SEE){
                areaPaint.setColor(Color.CYAN);
            }
            if (types[type]==Areas.Area.Type.KUESTE){
                areaPaint.setColor(Color.YELLOW);
            }
            if (types[type]==Areas.Area.Type.GEMEINDE){
                areaPaint.setColor(Color.GRAY);
            }
            if (types[type]==Areas.Area.Type.BUNDESLAND){
                areaPaint.setColor(Color.BLUE);
                areaPaint.setStrokeWidth(2);
            }
            for (int i=0; i<allAreas.size(); i++){
                Areas.Area cellArea = allAreas.get(i);
                ArrayList<Polygon> areaPolygons = cellArea.polygons;
                for (int p=0; p<areaPolygons.size(); p++){
                    Polygon areaPolygon = areaPolygons.get(p);
                    Path path = new Path();
                     PlotPoint plotPoint = getPlotPoint(areaPolygon.polygonX[0],areaPolygon.polygonY[0]);
                    path.moveTo(plotPoint.x, plotPoint.y);
                    for (int v=0; v<areaPolygon.polygonX.length; v++){
                        plotPoint = getPlotPoint(areaPolygon.polygonX[v],areaPolygon.polygonY[v]);
                        path.lineTo(plotPoint.x, plotPoint.y);
                    }
                    canvas.drawPath(path,areaPaint);
                }
            }
        }
        return resultBitmap;
    }

    public void popupHint(){
        final int[] hintTimes = {20,3,6,9};
        final int count = WeatherSettings.getHintCounter2(context);
        if ((count==hintTimes[1]) || (count==hintTimes[2]) || (count==hintTimes[3])){
            final RelativeLayout anchorView = (RelativeLayout) findViewById(R.id.warningactivity_main_relative_container);
            if (anchorView!=null){
                anchorView.post(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                int displayWidth  = Math.round(displayMetrics.widthPixels);
                                int displayHeight = Math.round(displayMetrics.heightPixels);
                                final boolean isLandscape = displayWidth>displayHeight;
                                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View popupView = layoutInflater.inflate(R.layout.popup_hint1,null);
                                // set correct theme textcolors
                                TextView textView1 = (TextView) popupView.findViewById(R.id.hint1_text);
                                textView1.setTextColor(Color.WHITE);
                                // register click callbacks
                                Button bottonOk = (Button) popupView.findViewById(R.id.hint1_button);
                                bottonOk.setTextColor(Color.WHITE);
                                bottonOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (hintPopupWindow!=null){
                                            hintPopupWindow.dismiss();
                                        }
                                    }
                                });
                                CheckBox checkNo = (CheckBox) popupView.findViewById(R.id.hint1_checkbox);
                                checkNo.setTextColor(Color.WHITE);
                                checkNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                                        if (checked){
                                            WeatherSettings.setHintCounter1(context,hintTimes[0]);
                                            WeatherSettings.setHintCounter2(context,hintTimes[0]);
                                        } else {
                                            WeatherSettings.setHintCounter1(context,0);
                                            WeatherSettings.setHintCounter2(context,0);
                                        }
                                    }
                                });
                                int width  = Math.round(displayWidth * 0.8f);
                                int height = Math.round(displayHeight * 0.26f);
                                if (isLandscape){
                                    height = Math.round(displayHeight * 0.4f);
                                }
                                ImageView imageView = (ImageView) popupView.findViewById(R.id.hint1_image);
                                if (count==hintTimes[1]){
                                    textView1.setText(context.getResources().getString(R.string.hint_1));
                                    imageView.setImageResource(R.drawable.radar_hint);
                                    height = Math.round(displayHeight * 0.47f);
                                    if (isLandscape){
                                        height = Math.round(displayHeight * 0.6f);
                                    }
                                }
                                if (count==hintTimes[2]){
                                    textView1.setText(context.getResources().getString(R.string.welcome_s3_text1));
                                    imageView.setImageResource(R.drawable.collapse_hint);
                                }
                                if (count==hintTimes[3]){
                                    textView1.setText(context.getResources().getString(R.string.welcome_s3_text2));
                                    imageView.setImageResource(R.drawable.expand_hint);
                                }
                                hintPopupWindow = new PopupWindow(popupView,width,height,true);
                                hintPopupWindow.showAtLocation(anchorView,Gravity.CENTER,0,0);
                            }
                        });
                    }
                });
            }
        }
        if (count<hintTimes[0]){
            int newCount = count + 1;
            WeatherSettings.setHintCounter2(context,newCount);
        }
    }

}
