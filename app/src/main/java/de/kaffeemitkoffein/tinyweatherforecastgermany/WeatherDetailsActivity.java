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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WeatherDetailsActivity extends Activity {

    public static String INTENT_EXTRA_POSITION = "POSITION";
    public static String SIS_POSITION = "POSITION";

    public static class ListItemType{
        public static final int Label = 0;
        public static final int Chart = 1;
        public static final int MoonRise = 2;
    }

    Weather.WeatherInfo weatherInfo;
    ArrayList<WeatherWarning> weatherWarnings = null;
    Context context;
    Handler mainHandler;
    ScheduledExecutorService scheduledExecutorService;
    LayoutInflater layoutInflater;
    ActionBar actionBar;
    Executor executor;
    ForecastIcons forecastIcons;

    CurrentWeatherInfo currentWeatherInfo;
    Pollen pollen;
    PollenArea pollenArea;
    ScrollView scrollView;
    RelativeLayout currentWeatherElements;
    LinearLayout valuesListWarnings;
    LinearLayout valuesListClouds;
    LinearLayout valuesListWind;
    LinearLayout valuesListElements;
    LinearLayout valuesListVisibility;
    LinearLayout valuesListIncidents;
    LinearLayout valuesListPrecipitation;
    FrameLayout precipitationChartFrame;
    LinearLayout moonAndSun;
    LinearLayout valuesListPollen;
    ImageView weatherConditionIcon;
    TextView weatherConditionText;
    TextView stationDescription;
    TextView temperature;
    TextView temperatureHighLow;
    TextView pressure;
    ImageView precipitationChart;
    ProgressBar progressBar;

    int weatherPosition;
    PhaseImages phaseImages;

    // flag to prevent too fast view changes
    boolean viewIsBeingCreated = false;

    private SwipeGestureDetector swipeGestureDetector;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            PrivateLog.log(c,PrivateLog.MAIN,PrivateLog.INFO,"WeatherDetailsActivity: received broadcast.");
            progressBar.setVisibility(View.INVISIBLE);
            if (intent != null) {
               if (((intent.getAction().equals(Pollen.ACTION_UPDATE_POLLEN)) && (intent.getBooleanExtra(Pollen.ACTION_UPDATE_POLLEN,true)))
                       || (intent.getAction().equals(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE))
                       || (intent.getAction().equals(MainActivity.MAINAPP_CUSTOM_REFRESH_ACTION)))
                {
                   PrivateLog.log(c,PrivateLog.MAIN,PrivateLog.INFO,"WeatherDetailsActivity: received broadcast -> updating views.");
                   displayValues();
               }
            }
        }
    };

    public static class SwipeGestureDetector implements View.OnTouchListener{

        private float downX;
        private float downY;
        private float upX;
        private float upY;
        private int threshold = 80;

        public void setThreshold(int i){
            this.threshold = i;
        }

        public boolean onLeftSwipe(View view, MotionEvent motionEvent){
            return false;
        }

        public boolean onRightSwipe(View view, MotionEvent motionEvent){
            return false;
        }

        public boolean onUpSwipe(View view, MotionEvent motionEvent){
            return false;
        }

        public boolean onDownSwipe(View view, MotionEvent motionEvent){
            return false;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                downX = motionEvent.getX();
                downY = motionEvent.getY();
            }
            if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                upX = motionEvent.getX();
                upY = motionEvent.getY();
                float deltaX = downX-upX;
                float deltaY = downY-upY;
                if (Math.abs(deltaX)>Math.abs(deltaY)){
                    // this is a horizontal move
                    if (Math.abs(deltaX)>threshold){
                        if (deltaX>0){
                            // right-to-left swipe
                            return onLeftSwipe(view,motionEvent);
                        } else {
                            // left-to-right swipe
                            return onRightSwipe(view,motionEvent);
                        }
                    }
                } else {
                    // this is a vertical swipe
                    if (Math.abs(deltaY)>threshold){
                        if (deltaY>0){
                            // this is a bottom-to-top swipe
                            return onUpSwipe(view,motionEvent);
                        } else {
                            // this is a top-to-bottom swipe
                            return onDownSwipe(view,motionEvent);
                        }
                    }
                }
            }
            return false;
        }
    }

    public class DetailsElement{
        String heading;
        Integer icon;
        String value;
        String label;
        Bitmap bitmap;
        boolean applyFilter;
        boolean smallHeading = false;
        View.OnLongClickListener onLongClickListener = null;
        // for the sunrise/sunset moonrise/moonset element
        String twilightMorning; String sunrise; String sunset; String twilightEvening; String moonrise; String moonset;

        public DetailsElement(String heading, String twilightMorning, String sunrise, String sunset, String twilightEvening, String moonrise, String moonset, Bitmap bitmap, boolean applyFilter){
            this.heading = heading;
            this.twilightMorning = twilightMorning; this.sunrise = sunrise; this.sunset = sunset; this.twilightEvening=twilightEvening; this.moonrise = moonrise; this.moonset=moonset; this.bitmap = bitmap;
            this.applyFilter = applyFilter;
        }

        public DetailsElement(String heading, int icon, String value, String label, boolean applyFilter){
            this.heading = heading;
            this.icon=icon;
            this.value=value;
            this.label=label;
            this.applyFilter = applyFilter;
        }

        public DetailsElement(String heading, int icon, String value, String label){
            this.heading = heading;
            this.icon=icon;
            this.value=value;
            this.label=label;
            this.applyFilter = true;
        }

        public DetailsElement(String heading, Bitmap bitmap, String value, String label){
            this.heading = heading;
            this.bitmap=bitmap;
            this.value=value;
            this.label=label;
            this.applyFilter = true;
        }

        public DetailsElement(String heading, Bitmap bitmap, String value, String label, View.OnLongClickListener onLongClickListener){
            this.heading = heading;
            this.bitmap=bitmap;
            this.value=value;
            this.label=label;
            this.applyFilter = true;
            this.onLongClickListener = onLongClickListener;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt(SIS_POSITION,weatherPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle restoreInstanceState){
        super.onRestoreInstanceState(restoreInstanceState);
        weatherPosition = restoreInstanceState.getInt(SIS_POSITION,0);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePicker.SetTheme(this);
        WeatherSettings.setRotationMode(this);
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weatherdetails);
        actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar_textforecastview);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        executor = Executors.newSingleThreadExecutor();
        forecastIcons  = new ForecastIcons(context,weatherConditionIcon);
        phaseImages = new PhaseImages(context);
        if (savedInstanceState!=null){
            weatherPosition = savedInstanceState.getInt(SIS_POSITION);
        } else {
            Intent intent = getIntent();
            if (intent!=null){
                if (intent.hasExtra(INTENT_EXTRA_POSITION)){
                    weatherPosition = intent.getExtras().getInt(INTENT_EXTRA_POSITION);
                }
            }
        }
        mainHandler = new Handler(Looper.getMainLooper());
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        currentWeatherElements = (RelativeLayout) findViewById(R.id.weatherdetails_forecastcontainer);
        scrollView = (ScrollView) findViewById(R.id.weatherdetails_scrollview);
        valuesListWarnings = (LinearLayout) findViewById(R.id.weatherdetails_warnings);
        valuesListClouds = (LinearLayout) findViewById(R.id.weatherdetails_clouds);
        valuesListWind = (LinearLayout) findViewById(R.id.weatherdetails_wind);
        valuesListElements = (LinearLayout) findViewById(R.id.weatherdetails_valuelist);
        valuesListVisibility = (LinearLayout) findViewById(R.id.weatherdetails_visibility);
        valuesListIncidents = (LinearLayout) findViewById(R.id.weatherdetails_incidents);
        weatherConditionIcon = (ImageView) findViewById(R.id.weatherdetails_weatherconditionicon);
        weatherConditionText = (TextView) findViewById(R.id.weatherdetails_weatherconditiontext);
        stationDescription = (TextView) findViewById(R.id.weatherdetails_locationtext);
        temperature = (TextView) findViewById(R.id.weatherdetails_temperature);
        temperatureHighLow = (TextView) findViewById(R.id.weatherdetails_temperature_highlow);
        pressure = (TextView) findViewById(R.id.weatherdetails_pressure);
        valuesListPrecipitation = (LinearLayout) findViewById(R.id.weatherdetails_precipitation);
        precipitationChartFrame = (FrameLayout) findViewById(R.id.weatherdetails_precipitationchartframe);
        precipitationChart = (ImageView) findViewById(R.id.weatherdetails_precipitationchart);
        valuesListPollen = (LinearLayout) findViewById(R.id.weatherdetails_pollenvalues);
        moonAndSun = (LinearLayout) findViewById(R.id.weatherdetails_moonandsun);
        progressBar = (ProgressBar) findViewById(R.id.weatherdetails_progressbar);
        // make a list what do update
        // Load weather data, fetch from DWD if necessary, establish alarm cycles
        try {
            currentWeatherInfo = Weather.getCurrentWeatherInfo(getApplicationContext());
        } catch (Exception e){
            PrivateLog.log(context,PrivateLog.MAIN,PrivateLog.ERR,"Error loading present weather data: "+e.getMessage());
        }
        // Check if weather data is available before proceeding
        if (currentWeatherInfo == null) {
            PrivateLog.log(context,PrivateLog.MAIN,PrivateLog.ERR,"No weather data available - database may be empty");
            Toast.makeText(context, R.string.error_no_data, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        TextView textViewNotice = (TextView) findViewById(R.id.weatherdetails_reference_text);
        if ((textViewNotice!=null) && (ForecastBitmap.getDisplayOrientation(context)== Configuration.ORIENTATION_LANDSCAPE)){
            float textSize = context.getResources().getDimension(R.dimen.fcmain_textsize_smaller);
            textViewNotice.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        }
        swipeGestureDetector = new SwipeGestureDetector(){
            @Override
            public boolean onLeftSwipe(View view, MotionEvent motionEvent) {
                moveOneItemForward();
                return super.onLeftSwipe(view, motionEvent);
            }

            @Override
            public boolean onRightSwipe(View view, MotionEvent motionEvent) {
                moveOneItemBack();
                return super.onRightSwipe(view, motionEvent);
            }
        };
        scrollView.setOnTouchListener(swipeGestureDetector);
        // determine pollen area.
        pollenArea = WeatherSettings.getPollenRegion(context);
        // if location eligible for pollen data, read the pollen data.
        // The pollen data might be outdated; in this case, this will be checked in onResume and an update will be triggered
        if (pollenArea!=null){
            pollen = Pollen.GetPollenData(context,pollenArea);
        }
        displayValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.weatherdetails_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void moveOneItemBack(){
        if (weatherPosition>0){
            if (!viewIsBeingCreated){
                weatherPosition--;
                displayValues();
            }
        }
    }

    private void moveOneItemForward(){
        if (weatherPosition<currentWeatherInfo.forecast1hourly.size()-1){
            if (!viewIsBeingCreated){
                weatherPosition++;
                displayValues();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int item_id = mi.getItemId();
        if (item_id == R.id.weatherdetails_back) {
            moveOneItemBack();
            return true;
        }
        if (item_id == R.id.weatherdetails_next) {
            moveOneItemForward();
            return true;
        }
        return super.onOptionsItemSelected(mi);
    }

    public void displayValues(){
        viewIsBeingCreated = true;
        weatherInfo = currentWeatherInfo.forecast1hourly.get(weatherPosition);
        actionBar.setTitle(currentWeatherInfo.weatherLocation.getDescription(context));
        actionBar.setSubtitle(Weather.getSimpleDateFormat(Weather.SimpleDateFormats.DETAILED_NO_SECONDS).format(new Date(weatherInfo.getTimestamp())));
        valuesListWarnings.removeAllViews();
        valuesListClouds.removeAllViews();
        valuesListWind.removeAllViews();
        valuesListElements.removeAllViews();
        valuesListVisibility.removeAllViews();
        valuesListIncidents.removeAllViews();
        moonAndSun.removeAllViews();
        valuesListPollen.removeAllViews();
        if (valuesListPrecipitation!=null){
            valuesListPrecipitation.removeAllViews();
        }
        setWarnings(weatherInfo,currentWeatherInfo.weatherLocation);
        setDetails(weatherInfo);
        setValues(weatherInfo,currentWeatherInfo.weatherLocation);
        setPrecipitationChart(weatherInfo);
        viewIsBeingCreated = false;
    }


    public void setWarnings(final Weather.WeatherInfo weatherInfo, final Weather.WeatherLocation weatherLocation) {
        if (weatherWarnings==null){
            weatherWarnings = WeatherWarnings.getCurrentWarnings(context,true);
        }
        ArrayList<WeatherWarning> warningsForLocation = WeatherWarnings.getWarningsForLocation(context, weatherWarnings, weatherLocation);
        long itemStopTime = weatherInfo.getTimestamp();
        long itemStartTime = itemStopTime - 1000 * 60 * 60;
        ArrayList<WeatherWarning> applicableWarnings = new ArrayList<WeatherWarning>();
        for (int i = 0; i < warningsForLocation.size(); i++) {
            WeatherWarning warning = warningsForLocation.get(i);
            if ((warning.onset <= itemStopTime) && (warning.getApplicableExpires() >= itemStartTime)) {
                applicableWarnings.add(warning);
            }
        }
        if (applicableWarnings.size() > 0) {
            setDetail(valuesListWarnings,newDetail(getResources().getString(R.string.preference_category_warnings),null,null,null),ListItemType.Label);
            for (int i=0; i<applicableWarnings.size(); i++){
                final RelativeLayout relativeLayout = (RelativeLayout) WeatherWarningAdapter.setWarningViewElements(context,layoutInflater,null,null,applicableWarnings.get(i),true,mainHandler,scheduledExecutorService);
                valuesListWarnings.addView(relativeLayout);
            }
        }
    }

    public void setPrecipitationChart(Weather.WeatherInfo weatherInfo){
        if (weatherInfo.hasPrecipitationDetails()){
            if (valuesListPrecipitation!=null){
                valuesListPrecipitation.setVisibility(View.VISIBLE);
                valuesListPrecipitation.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
                DetailsElement precipitationChartHeading = newDetail(getResources().getString(R.string.wd_probablity_precipitation_amounts),null,null,null);
                if (ForecastBitmap.getDisplayOrientation(context)== Configuration.ORIENTATION_LANDSCAPE){
                    precipitationChartHeading.smallHeading = true;
                }
                setDetail(valuesListPrecipitation,precipitationChartHeading,ListItemType.Label);
            }
            Bitmap bitmap = ForecastBitmap.getPrecipitationChart2(context,weatherInfo);
            precipitationChart.setImageBitmap(bitmap);
            precipitationChartFrame.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
        } else {
            if (valuesListPrecipitation!=null){
                valuesListPrecipitation.setVisibility(View.GONE);
            }
        }
    }

    public void setValues(Weather.WeatherInfo weatherInfo, Weather.WeatherLocation weatherLocation){
        stationDescription.setText(weatherLocation.getDescription(context));
        if (weatherInfo.hasCondition()){
            weatherConditionIcon.setImageBitmap(forecastIcons.getIconBitmap(weatherInfo,weatherLocation));
            weatherConditionText.setText(WeatherCodeContract.getWeatherConditionText(context,weatherInfo.getCondition()));
        }
        if (weatherInfo.hasTemperature()){
            temperature.setText(Integer.toString(weatherInfo.getTemperatureInCelsiusInt())+"°");
            ThemePicker.applyTemperatureAccentColor(context,weatherInfo,temperature);
        }
        // WeatherInfo takes care if values are not present, never returns null
        temperatureHighLow.setText(weatherInfo.getMinMaxTemperatureInCelsiusIntString(true));
        if (weatherInfo.hasPressure()){
            pressure.setText(Integer.toString(weatherInfo.getPressure()/100)+" hPa");
        }
    }

    private void setDetail(LinearLayout targetView, DetailsElement detailsElement, int listItemType){
        int resourceID = R.layout.detailslistitem;
        if (listItemType == ListItemType.Chart){
            resourceID = R.layout.detailslistitem2;
        }
        if (listItemType == ListItemType.MoonRise){
            resourceID = R.layout.detailslistitem3;
        }
        View view = layoutInflater.inflate(resourceID,null);
        TextView heading = (TextView) view.findViewById(R.id.dl_heading);
        TextView label = (TextView) view.findViewById(R.id.dl_label);
        ImageView icon = (ImageView) view.findViewById(R.id.dl_icon);
        LinearLayout lineContainer = (LinearLayout) view.findViewById(R.id.dl_linearlayout);
        TextView value = null;
        ImageView chart = null;
        boolean hideLineContainer = false;
        if (listItemType==ListItemType.Label){
            /* Label is detailsitem.xml, and has the following views:
               dl_heading
               dl_icon, dl_value, dl_label
             */
            value = (TextView) view.findViewById(R.id.dl_value);
            if ((detailsElement.icon==null) && (detailsElement.value==null) && (detailsElement.label==null)) {
                hideLineContainer = true; // means line with data is empty
            }
        }
        if (listItemType == ListItemType.Chart){
            /* Chart is detailsitem2.xml, and has the following views:
               dl_heading
               dl_icon, dl_label, d_chart
             */
            chart = (ImageView) view.findViewById(R.id.dl_chart);
            if ((detailsElement.icon==null) && (detailsElement.label==null) && (detailsElement.bitmap==null)){
                hideLineContainer = true;
            }
        }
        if (listItemType == ListItemType.MoonRise){
            chart = (ImageView) view.findViewById(R.id.dl_chart);
            TextView twilightMorning = (TextView) view.findViewById(R.id.dl_sun_twilight_morning_value);
            twilightMorning.setText(detailsElement.twilightMorning);
            TextView sunrise = (TextView) view.findViewById(R.id.dl_sun_up_value);
            sunrise.setText(detailsElement.sunrise);
            TextView sunset  = (TextView) view.findViewById(R.id.dl_sun_down_value);
            sunset.setText(detailsElement.sunset);
            TextView twilightEvening = (TextView) view.findViewById(R.id.dl_sun_twilight_evening_value);
            twilightEvening.setText(detailsElement.twilightEvening);
            TextView moonrise = (TextView) view.findViewById(R.id.dl_moon_up_value);
            moonrise.setText(detailsElement.moonrise);
            TextView moonset  = (TextView) view.findViewById(R.id.dl_moon_down_value);
            moonset.setText(detailsElement.moonset);
            chart.setImageBitmap(detailsElement.bitmap);
        }
        if (heading!=null) {
            if (detailsElement.smallHeading){
                heading.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.fcmain_textsize_smaller));
            }
            if (detailsElement.heading==null){
                heading.setVisibility(View.GONE);
            } else {
                heading.setVisibility(View.VISIBLE);
                heading.setText(detailsElement.heading);
            }
        }
        if (value!=null){
            if (detailsElement.value==null){
                value.setVisibility(View.GONE);
            } else {
                value.setVisibility(View.VISIBLE);
                value.setText(detailsElement.value);
            }
        }
        if (label!=null){
            if (detailsElement.label==null){
                label.setVisibility(View.GONE);
            } else {
                label.setVisibility(View.VISIBLE);
                label.setText(detailsElement.label);
            }
        }
        if (chart!=null){
            if (detailsElement.bitmap!=null){
                chart.setVisibility(View.VISIBLE);
                chart.setImageBitmap(detailsElement.bitmap);
            } else {
                chart.setVisibility(View.GONE);
            }
        }
        if (icon!=null){
            if ((detailsElement.icon==null) && (detailsElement.bitmap==null)){
                icon.setVisibility(View.INVISIBLE);
            } else {
                if ((detailsElement.bitmap!=null) && (listItemType==ListItemType.Label)){
                    icon.setVisibility(View.VISIBLE);
                    icon.setImageBitmap(detailsElement.bitmap);
                }
                else {
                    // check if an icon is present
                    if (detailsElement.icon==null){
                        icon.setVisibility(View.GONE);
                    } else {
                        icon.setVisibility(View.VISIBLE);
                        icon.setImageBitmap(WeatherIcons.getIconBitmap(context,detailsElement.icon,false,detailsElement.applyFilter));
                    }
                }
            }
        }
        // lineContainer is the horizontal (value) line below the heading. This needs to be hidden when no items
        // present but the heading itself.
        if (lineContainer!=null){
            if (hideLineContainer){
                lineContainer.setVisibility(View.GONE);
            } else {
                lineContainer.setVisibility(View.VISIBLE);
            }
        }
        if (chart!=null){
            chart.setOnLongClickListener(detailsElement.onLongClickListener);
        }
        if (label!=null){
            label.setOnLongClickListener(detailsElement.onLongClickListener);
        }
        targetView.addView(view);
    }

    private DetailsElement newDetail(String title, int icon, String value, String label){
        DetailsElement detailsElement = new DetailsElement(title, icon, value, label);
        return detailsElement;
    }

    private DetailsElement newDetail(String title, Bitmap icon, String value, String label){
        DetailsElement detailsElement = new DetailsElement(title, icon, value, label);
        return detailsElement;
    }

    private DetailsElement newDetail(String title, Bitmap icon, String value, String label, View.OnLongClickListener onLongClickListener){
        DetailsElement detailsElement = new DetailsElement(title, icon, value, label, onLongClickListener);
        return detailsElement;
    }

    private DetailsElement newDetail(String title, int icon, String value, String label, boolean applyFilter){
        DetailsElement detailsElement = new DetailsElement(title, icon, value, label);
        detailsElement.applyFilter = applyFilter;
        return detailsElement;
    }

    public static void setPollenLegendColorBoxes(Context context, View view){
        ImageView box0 = (ImageView) view.findViewById(R.id.pl_box0);
        if (box0!=null){
            box0.setImageBitmap(ForecastBitmap.getPollenLegendBox(context,64,0));
        }
        ImageView box1 = (ImageView) view.findViewById(R.id.pl_box1);
        if (box1!=null){
            box1.setImageBitmap(ForecastBitmap.getPollenLegendBox(context,64,1));
        }
        ImageView box2 = (ImageView) view.findViewById(R.id.pl_box2);
        if (box2!=null){
            box2.setImageBitmap(ForecastBitmap.getPollenLegendBox(context,64,2));
        }
        ImageView box3 = (ImageView) view.findViewById(R.id.pl_box3);
        if (box3!=null){
            box3.setImageBitmap(ForecastBitmap.getPollenLegendBox(context,64,3));
        }
        ImageView box4 = (ImageView) view.findViewById(R.id.pl_box4);
        if (box4!=null){
            box4.setImageBitmap(ForecastBitmap.getPollenLegendBox(context,64,4));
        }
        ImageView box5 = (ImageView) view.findViewById(R.id.pl_box5);
        if (box5!=null){
            box5.setImageBitmap(ForecastBitmap.getPollenLegendBox(context,64,5));
        }
        ImageView box6 = (ImageView) view.findViewById(R.id.pl_box6);
        if (box6!=null){
            box6.setImageBitmap(ForecastBitmap.getPollenLegendBox(context,64,6));
        }
    }

    private void setPollenLegend(LinearLayout targetView){
        View view = layoutInflater.inflate(R.layout.pollenlegend_horizontal,null);
        setPollenLegendColorBoxes(context,view);
        targetView.addView(view);
    }

    private void setDetails(final Weather.WeatherInfo weatherInfo){
        ArrayList<DetailsElement> list = new ArrayList<DetailsElement>();
        if (weatherInfo.hasClouds()){
            valuesListClouds.setVisibility(View.VISIBLE);
            valuesListClouds.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
            list.add(newDetail(getResources().getString(R.string.wd_cc),null,null,null));
            list.add(newDetail(null,WeatherIcons.SYMBOL_CLOUD,weatherInfo.getClouds()+"%",getResources().getString(R.string.wd_cc)));
            if (weatherInfo.hasClouds_Nh()){
                list.add(newDetail(null,null,weatherInfo.getClouds_Nh()+"%",getResources().getString(R.string.wd_hcc)));
            }
            if (weatherInfo.hasClouds_Nm()){
                list.add(newDetail(null,null,weatherInfo.getClouds_Nm()+"%",getResources().getString(R.string.wd_mcc)));
            }
            if (weatherInfo.hasClouds_Nl()){
                list.add(newDetail(null,null,weatherInfo.getClouds_Nl()+"%",getResources().getString(R.string.wd_lcc)));
            }
            if (weatherInfo.hasClouds_N05()){
                list.add(newDetail(null,null,weatherInfo.getClouds_N05()+"%",getResources().getString(R.string.wd_ccb500)));
            }
            if (weatherInfo.hasClouds_H_BsC()){
                list.add(newDetail(null,null,weatherInfo.getClouds_H_BsC()+"%",getResources().getString(R.string.wd_ccb500)));
            }
            for (int i=0; i<list.size(); i++){
                setDetail(valuesListClouds,list.get(i),ListItemType.Label);
            }
        } else {
            valuesListClouds.setVisibility(View.GONE);
        }
        list = new ArrayList<DetailsElement>();
        if ((weatherInfo.hasWindSpeed()) || (weatherInfo.hasWindDirection())){
            valuesListWind.setVisibility(View.VISIBLE);
            valuesListWind.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
            list.add(newDetail(getResources().getString(R.string.wd_wind),null,null,null));
            if (weatherInfo.hasWindDirection()){
                list.add(newDetail(null,weatherInfo.getWindSymbol(context,WeatherSettings.getWindDisplayType(context),true),weatherInfo.getWindDirectionString(context),"Windrichtung"));
            }
            if (weatherInfo.hasWindSpeed()){
                list.add(newDetail(null,null,weatherInfo.getWindSpeedString(context,true),getResources().getString(R.string.preference_display_wind_unit_title)));
            }
            if (weatherInfo.hasFlurries()){
                list.add(newDetail(null,null,weatherInfo.getFlurriesString(context,true),getResources().getString(R.string.wd_gusts)));
            }
            for (int i=0; i<list.size(); i++){
                setDetail(valuesListWind,list.get(i),ListItemType.Label);
            }
        } else {
            valuesListWind.setVisibility(View.GONE);
        }
        list = new ArrayList<DetailsElement>();
        if ((weatherInfo.hasSunDuration()) || (weatherInfo.hasUvHazardIndex()) || (weatherInfo.hasRH()) || (weatherInfo.hasTemperature5cm()) || (weatherInfo.hasProbPrecipitation()) || (weatherInfo.hasPrecipitation())){
            valuesListElements.setVisibility(View.VISIBLE);
            valuesListElements.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
            list.add(newDetail(getResources().getString(R.string.wd_general),null,null,null));
            if (weatherInfo.hasSunDuration()){
                list.add(newDetail(null,WeatherIcons.SYMBOL_SUN,weatherInfo.getSunDurationInMinutes()+" min",getResources().getString(R.string.wd_sun)));
            }
            if (weatherInfo.hasUvHazardIndex()){
                list.add(newDetail(null,null,String.valueOf(weatherInfo.getUvHazardIndex()),getResources().getString(R.string.preference_screen_uvhi_title)));
            }
            if (weatherInfo.hasRH()){
                list.add(newDetail(null,WeatherIcons.SYMBOL_RH,weatherInfo.getRHInt()+"%",getResources().getString(R.string.welcome_s2_text11)));
            }
            if (weatherInfo.hasTd()){
                list.add(newDetail(null,null,weatherInfo.getDewPointInCelsiusRoundedString(),getResources().getString(R.string.dewpoint)));
            }
            if (weatherInfo.hasTemperature5cm()){
                list.add(newDetail(null,WeatherIcons.SYMBOL_TEMPERATURE5CM,weatherInfo.getTemperature5cmInCelsiusInt()+"°",getResources().getString(R.string.wd_t5)));
            }
            if (weatherInfo.hasProbPrecipitation()){
                list.add(newDetail(null,WeatherIcons.SYMBOL_PRECIPITATION,weatherInfo.getProbPrecipitation()+"%",getResources().getString(R.string.wd_pp)));
            }
            if (weatherInfo.hasPrecipitation()){
                list.add(newDetail(null,WeatherIcons.SYMBOL_PRECIPITATION,weatherInfo.getPrecipitationString(),getResources().getString(R.string.wd_pa),false));
            }
            for (int i=0; i<list.size(); i++){
                setDetail(valuesListElements,list.get(i),ListItemType.Label);
            }
        } else {
            valuesListElements.setVisibility(View.GONE);
        }
        list = new ArrayList<DetailsElement>();
        if ((weatherInfo.hasVisibility()) || (weatherInfo.hasProbVisibilityBelow1km())){
            valuesListVisibility.setVisibility(View.VISIBLE);
            valuesListVisibility.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
            list.add(newDetail(context.getResources().getString(R.string.preference_displayvisibility_title),null,null,null));
            if (weatherInfo.hasVisibility()){
                list.add(newDetail(null,WeatherIcons.BIOCULAR,(ForecastAdapter.getVisibilityCharSequence(weatherInfo,WeatherSettings.getDistanceDisplayUnit(context))).toString(),getResources().getString(R.string.preference_displayvisibility_title)));
            }
            if (weatherInfo.hasProbVisibilityBelow1km()){
                list.add(newDetail(null,null,(ForecastAdapter.getVisibilityBelow1kmCharSequence(weatherInfo)).toString(),getResources().getString(R.string.wd_pv1)));
            }
            for (int i=0; i<list.size(); i++){
                setDetail(valuesListVisibility,list.get(i),ListItemType.Label);
            }
        } else {
            valuesListVisibility.setVisibility(View.GONE);
        }
        list = new ArrayList<DetailsElement>();
        if ((weatherInfo.hasProbThunderstorms()) || (weatherInfo.hasProbSolidPrecipitation()) || (weatherInfo.hasProbFreezingRain()) || (weatherInfo.hasProbFog()) || (weatherInfo.hasProbDrizzle())){
            valuesListIncidents.setVisibility(View.VISIBLE);
            valuesListIncidents.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
            list.add(newDetail(getResources().getString(R.string.wd_wevents),null,null,null));
            if (weatherInfo.hasProbThunderstorms()){
                list.add(newDetail(null,WeatherIcons.SYMBOL_LIGHTNING,weatherInfo.getProbThunderStorms()+"%",getResources().getString(R.string.wd_pt),false));
            }
            if (weatherInfo.hasProbSolidPrecipitation()){
                list.add(newDetail(null,WeatherIcons.SYMBOL_HAIL,weatherInfo.getProbSolidPrecipitation()+"%",getResources().getString(R.string.wd_ph),false));
            }
            if (weatherInfo.hasProbFreezingRain()){
                list.add(newDetail(null,WeatherIcons.SYMBOL_FREEZING_RAIN,weatherInfo.getProbFreezingRain()+"%",getResources().getString(R.string.wd_pfr),false));
            }
            if (weatherInfo.hasProbFog()){
                list.add(newDetail(null,WeatherIcons.SYMBOL_FOG,weatherInfo.getProbFog()+"%",getResources().getString(R.string.wd_pf),false));
            }
            if (weatherInfo.hasProbDrizzle()){
                list.add(newDetail(null,WeatherIcons.SYMBOL_DRIZZLE,weatherInfo.getProbDrizzle()+"%",getResources().getString(R.string.wd_pd),false));
            }
            for (int i=0; i<list.size(); i++){
                setDetail(valuesListIncidents,list.get(i),ListItemType.Label);
            }
        } else {
            valuesListIncidents.setVisibility(View.GONE);
        }
        if (true){
            moonAndSun.setVisibility(View.VISIBLE);
            moonAndSun.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
            Weather.RiseSetTimes riseSetTimes = new Weather.RiseSetTimes(currentWeatherInfo.weatherLocation,weatherInfo.getTimestamp());
            final Bitmap bitmap = phaseImages.getMoonPhaseImage(currentWeatherInfo.weatherLocation,weatherInfo.getTimestamp());
            DetailsElement detailsElement = new DetailsElement(context.getResources().getString(R.string.preference_displaysunrise_title),
                    Weather.getSimpleDateFormat(Weather.SimpleDateFormats.TIME).format(riseSetTimes.sun[Weather.RiseSetTimes.TWILIGHT_MORNING]),
                    Weather.getSimpleDateFormat(Weather.SimpleDateFormats.TIME).format(riseSetTimes.sun[Weather.RiseSetTimes.RISE]),
                    Weather.getSimpleDateFormat(Weather.SimpleDateFormats.TIME).format(riseSetTimes.sun[Weather.RiseSetTimes.SET]),
                    Weather.getSimpleDateFormat(Weather.SimpleDateFormats.TIME).format(riseSetTimes.sun[Weather.RiseSetTimes.TWILIGHT_EVENING]),
                    Weather.getSimpleDateFormat(Weather.SimpleDateFormats.TIME).format(riseSetTimes.moon[Weather.RiseSetTimes.RISE]),
                    Weather.getSimpleDateFormat(Weather.SimpleDateFormats.TIME).format(riseSetTimes.moon[Weather.RiseSetTimes.SET])
                    ,bitmap,true);
            setDetail(moonAndSun,detailsElement,ListItemType.MoonRise);
        }
        if (pollenArea!=null){
            final int relativeDay = weatherInfo.getRelativeDay();
            pollen = Pollen.GetPollenData(context,pollenArea);
            if ((pollen!=null) && (relativeDay>=Pollen.Today) && (relativeDay<=Pollen.DayAfterTomorrow) && (WeatherSettings.anyPollenActive(context))){
                final int BAR_WIDTH = 1024; final int BAR_HEIGHT = 256;
                list = new ArrayList<DetailsElement>();
                list.add(newDetail(context.getResources().getString(R.string.pollen_title),null,null,null));
                int loadAmbrosia = pollen.getPollenLoad(context,Pollen.Ambrosia,relativeDay);
                int loadBeifuss = pollen.getPollenLoad(context,Pollen.Beifuss,relativeDay);
                int loadRoggen = pollen.getPollenLoad(context,Pollen.Roggen,relativeDay);
                int loadEsche = pollen.getPollenLoad(context,Pollen.Esche,relativeDay);
                int loadBirke = pollen.getPollenLoad(context,Pollen.Birke,relativeDay);
                int loadHasel = pollen.getPollenLoad(context,Pollen.Hasel,relativeDay);
                int loadErle = pollen.getPollenLoad(context,Pollen.Erle,relativeDay);
                int loadGraeser = pollen.getPollenLoad(context,Pollen.Graeser,relativeDay);
                if ((loadAmbrosia>=0) && (WeatherSettings.getPollenActiveAmbrosia(context))){
                    list.add(newDetail(null,ForecastBitmap.getHorizontalBar(context,BAR_WIDTH,BAR_HEIGHT,loadAmbrosia,6,Pollen.PollenLoadColors[loadAmbrosia],ThemePicker.getWidgetTextColor(context)),null,context.getResources().getString(R.string.pollen_ambrosia), new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (relativeDay==Pollen.Today){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_0);
                            }
                            if (relativeDay==Pollen.Tomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_1);
                            }
                            if (relativeDay==Pollen.DayAfterTomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_AMBROSIA_2);
                            }
                            return true;
                        }
                    }));
                }
                if ((loadBeifuss>=0) && (WeatherSettings.getPollenActiveBeifuss(context))){
                    list.add(newDetail(null,ForecastBitmap.getHorizontalBar(context,BAR_WIDTH,BAR_HEIGHT,loadBeifuss,6,Pollen.PollenLoadColors[loadBeifuss],ThemePicker.getWidgetTextColor(context)),null,context.getResources().getString(R.string.pollen_mugwort), new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (relativeDay==Pollen.Today){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_BEIFUSS_0);
                            }
                            if (relativeDay==Pollen.Tomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_BEIFUSS_1);
                            }
                            if (relativeDay==Pollen.DayAfterTomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_BEIFUSS_2);
                            }
                            return true;
                        }
                    }));
                }
                if ((loadRoggen>=0) && (WeatherSettings.getPollenActiveRoggen(context))){
                    list.add(newDetail(null,ForecastBitmap.getHorizontalBar(context,BAR_WIDTH,BAR_HEIGHT,loadRoggen,6,Pollen.PollenLoadColors[loadRoggen],ThemePicker.getWidgetTextColor(context)),null,context.getResources().getString(R.string.pollen_rye), new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (relativeDay==Pollen.Today){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_ROGGEN_0);
                            }
                            if (relativeDay==Pollen.Tomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_ROGGEN_1);
                            }
                            if (relativeDay==Pollen.DayAfterTomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_ROGGEN_2);
                            }
                            return true;
                        }
                    }));
                }
                if ((loadEsche>=0) && (WeatherSettings.getPollenActiveEsche(context))){
                    list.add(newDetail(null,ForecastBitmap.getHorizontalBar(context,BAR_WIDTH,BAR_HEIGHT,loadEsche,6,Pollen.PollenLoadColors[loadEsche],ThemePicker.getWidgetTextColor(context)),null,context.getResources().getString(R.string.pollen_ash), new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (relativeDay==Pollen.Today){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_ESCHE_0);
                            }
                            if (relativeDay==Pollen.Tomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_ESCHE_1);
                            }
                            if (relativeDay==Pollen.DayAfterTomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_ESCHE_2);
                            }
                            return true;
                        }
                    }));
                }
                if ((loadBirke>=0) && (WeatherSettings.getPollenActiveBirke(context))){
                    list.add(newDetail(null,ForecastBitmap.getHorizontalBar(context,BAR_WIDTH,BAR_HEIGHT,loadBirke,6,Pollen.PollenLoadColors[loadBirke],ThemePicker.getWidgetTextColor(context)),null,context.getResources().getString(R.string.pollen_birch), new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (relativeDay==Pollen.Today){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_BIRKE_0);
                            }
                            if (relativeDay==Pollen.Tomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_BIRKE_1);
                            }
                            if (relativeDay==Pollen.DayAfterTomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_BIRKE_2);
                            }
                            return true;
                        }
                    }));
                }
                if ((loadHasel>=0) && (WeatherSettings.getPollenActiveHasel(context))){
                    list.add(newDetail(null,ForecastBitmap.getHorizontalBar(context,BAR_WIDTH,BAR_HEIGHT,loadHasel,6,Pollen.PollenLoadColors[loadHasel],ThemePicker.getWidgetTextColor(context)),null,context.getResources().getString(R.string.pollen_hazel), new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (relativeDay==Pollen.Today){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_HASEL_0);
                            }
                            if (relativeDay==Pollen.Tomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_HASEL_1);
                            }
                            if (relativeDay==Pollen.DayAfterTomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_HASEL_2);
                            }
                            return true;
                        }
                    }));
                }
                if ((loadErle>=0) && (WeatherSettings.getPollenActiveErle(context))){
                    list.add(newDetail(null,ForecastBitmap.getHorizontalBar(context,BAR_WIDTH,BAR_HEIGHT,loadErle,6,Pollen.PollenLoadColors[loadErle],ThemePicker.getWidgetTextColor(context)),null,context.getResources().getString(R.string.pollen_alder),new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (relativeDay==Pollen.Today){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_ERLE_0);
                            }
                            if (relativeDay==Pollen.Tomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_ERLE_1);
                            }
                            if (relativeDay==Pollen.DayAfterTomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_ERLE_2);
                            }
                            return true;
                        }
                    }));
                }
                if ((loadGraeser>=0) && (WeatherSettings.getPollenActiveGraeser(context))){
                    list.add(newDetail(null, ForecastBitmap.getHorizontalBar(context, BAR_WIDTH, BAR_HEIGHT, loadGraeser, 6, Pollen.PollenLoadColors[loadGraeser], ThemePicker.getWidgetTextColor(context)), null, context.getResources().getString(R.string.pollen_grasses), new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (relativeDay==Pollen.Today){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_0);
                            }
                            if (relativeDay==Pollen.Tomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_1);
                            }
                            if (relativeDay==Pollen.DayAfterTomorrow){
                                openLayerMap(WeatherLayer.Layers.POLLEN_FORECAST_GRAESER_2);
                            }
                            return true;
                        }
                    }));
                }
                valuesListPollen.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
                for (int i=0; i<list.size(); i++){
                    setDetail(valuesListPollen,list.get(i),ListItemType.Chart);
                }
                setPollenLegend(valuesListPollen);
            } else {
                valuesListPollen.setVisibility(View.GONE);
            }
        } else {
            valuesListPollen.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerForBroadcast();
        // update pollen data if necessary. Callback is via broadcast-receiver.
        if (pollenArea!=null){
            if ((pollen==null) || DataStorage.Updates.isSyncDue(context, WeatherSettings.Updates.Category.POLLEN)){
                PrivateLog.log(context,PrivateLog.MAIN,PrivateLog.INFO,"WeatherDetailsActivity: requesting a pollen update.");
                SyncRequest syncRequest = MainActivity.getManualSyncRequest(context,WeatherSyncAdapter.UpdateFlags.FLAG_UPDATE_POLLEN);
                ContentResolver.requestSync(syncRequest);
            } else {
                PrivateLog.log(context,PrivateLog.MAIN,PrivateLog.INFO,"WeatherDetailsActivity: pollen data is in place, no update necessary.");
            }
        } else {
            PrivateLog.log(context,PrivateLog.MAIN,PrivateLog.INFO,"WeatherDetailsActivity: pollen data not available for this location.");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterForBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void openLayerMap(int layerMap){
        Intent intent = new Intent(this, WeatherLayerMapActivity.class);
        intent.putExtra(WeatherLayerMapActivity.LAYER,layerMap);
        startActivity(intent);
    }

    private void registerForBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Pollen.ACTION_UPDATE_POLLEN);
        filter.addAction(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE);
        filter.addAction(MainActivity.MAINAPP_CUSTOM_REFRESH_ACTION);
        registerReceiver(receiver,filter);
    }

    private void unRegisterForBroadcast(){
        unregisterReceiver(receiver);
    }


}
