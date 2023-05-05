package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WeatherDetailsActivity extends Activity {

    public static String INTENT_EXTRA_POSITION = "POSITION";
    public static String SIS_POSITION = "POSITION";

    Weather.WeatherInfo weatherInfo;
    Context context;
    Handler mainHandler;
    ScheduledExecutorService scheduledExecutorService;
    LayoutInflater layoutInflater;
    ActionBar actionBar;

    CurrentWeatherInfo currentWeatherInfo;
    RelativeLayout currentWeatherElements;
    LinearLayout valuesListWarnings;
    LinearLayout valuesListClouds;
    LinearLayout valuesListWind;
    LinearLayout valuesListElements;
    LinearLayout valuesListVisibility;
    LinearLayout valuesListIncidents;
    LinearLayout valuesListPrecipitation;
    FrameLayout precipitationChartFrame;

    ImageView weatherConditionIcon;
    TextView weatherConditionText;
    TextView stationDescription;
    TextView temperature;
    TextView temperatureHighLow;
    TextView pressure;
    ImageView precipitationChart;

    int weatherPosition;

    public class DetailsElement{
        String heading;
        Integer icon;
        String value;
        String label;
        Bitmap bitmap;
        boolean applyFilter;
        boolean smallHeading = false;

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
        // Load weather data, fetch from DWD if necessary, establish alarm cycles
        try {
            currentWeatherInfo = new Weather().getCurrentWeatherInfo(getApplicationContext());
        } catch (Exception e){
            PrivateLog.log(context,PrivateLog.MAIN,PrivateLog.ERR,"Error loading present weather data: "+e.getMessage());
        }
        if (currentWeatherInfo!=null){
            UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(),UpdateAlarmManager.CHECK_FOR_UPDATE,currentWeatherInfo);
        } else {
            UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(),UpdateAlarmManager.FORCE_UPDATE,null);
        }
        currentWeatherElements = (RelativeLayout) findViewById(R.id.weatherdetails_forecastcontainer);
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
        displayValues();
        TextView textViewNotice = (TextView) findViewById(R.id.weatherdetails_reference_text);
        if ((textViewNotice!=null) && (ForecastBitmap.getDisplayOrientation(context)== Configuration.ORIENTATION_LANDSCAPE)){
            float textSize = context.getResources().getDimension(R.dimen.fcmain_textsize_smaller);
            textViewNotice.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.weatherdetails_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int item_id = mi.getItemId();
        if (item_id == R.id.weatherdetails_back) {
            if (weatherPosition>0){
                weatherPosition--;
                displayValues();
            }
            return true;
        }
        if (item_id == R.id.weatherdetails_next) {
            if (weatherPosition<currentWeatherInfo.forecast1hourly.size()-1){
                weatherPosition++;
                displayValues();
            }
            return true;
        }
        return super.onOptionsItemSelected(mi);
    }

    public void displayValues(){
        weatherInfo = currentWeatherInfo.forecast1hourly.get(weatherPosition);
        actionBar.setTitle(currentWeatherInfo.weatherLocation.description);
        actionBar.setSubtitle(Weather.SIMPLEDATEFORMATS.DETAILED_NO_SECONDS.format(new Date(weatherInfo.getTimestamp())));
        valuesListWarnings.removeAllViews();
        valuesListClouds.removeAllViews();
        valuesListWind.removeAllViews();
        valuesListElements.removeAllViews();
        valuesListVisibility.removeAllViews();
        valuesListIncidents.removeAllViews();
        if (valuesListPrecipitation!=null){
            valuesListPrecipitation.removeAllViews();
        }
        setWarnings(weatherInfo,currentWeatherInfo.weatherLocation);
        setDetails(weatherInfo);
        setValues(weatherInfo,currentWeatherInfo.weatherLocation);
        setPrecipitationChart(weatherInfo);
    }

    public void setWarnings(final Weather.WeatherInfo weatherInfo, final Weather.WeatherLocation weatherLocation){
        Runnable getWarningsRunnable = new WeatherWarnings.getWarningsForLocationRunnable(context,null,weatherLocation){
            @Override
            public void onResult(ArrayList<WeatherWarning> warnings) {
                super.onResult(warnings);
                long itemStopTime = weatherInfo.getTimestamp();
                long itemStartTime = itemStopTime - 1000*60*60;
                ArrayList<WeatherWarning> applicableWarnings = new ArrayList<WeatherWarning>();
                for (int i=0; i<warnings.size(); i++){
                    WeatherWarning warning = warnings.get(i);
                    if ((warnings.get(i).onset<=itemStopTime) && (warnings.get(i).expires>=itemStartTime)){
                        applicableWarnings.add(warnings.get(i));
                    }
                }
                if (applicableWarnings.size()>0){
                    setDetail(valuesListWarnings,newDetail("Wetterwarnungen",null,null,null));
                    for (int i=0; i<warnings.size(); i++){
                        final RelativeLayout relativeLayout = (RelativeLayout) WeatherWarningAdapter.setWarningViewElements(context,layoutInflater,null,null,warnings.get(i),true,mainHandler,scheduledExecutorService);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                valuesListWarnings.addView(relativeLayout);
                            }
                        });
                    }
                }
            }
        };
        scheduledExecutorService.execute(getWarningsRunnable);
    }

    public void setPrecipitationChart(Weather.WeatherInfo weatherInfo){
        if (weatherInfo.hasPrecipitationDetails()){
            if (valuesListPrecipitation!=null){
                valuesListPrecipitation.setVisibility(View.VISIBLE);
                valuesListPrecipitation.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
                DetailsElement precipitationChartHeading = newDetail("Wahrscheinlichkeit von Niederschlagsmengen",null,null,null);
                if (ForecastBitmap.getDisplayOrientation(context)== Configuration.ORIENTATION_LANDSCAPE){
                    precipitationChartHeading.smallHeading = true;
                }
                setDetail(valuesListPrecipitation,precipitationChartHeading);
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
        stationDescription.setText(weatherLocation.description);
        if (weatherInfo.hasCondition()){
            int resource = WeatherCodeContract.getWeatherConditionDrawableResource(context,weatherInfo.getCondition(),true);
            weatherConditionIcon.setImageResource(resource);
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

    private void setDetail(LinearLayout targetView, DetailsElement detailsElement){
        if (detailsElement!=null){
            View view = layoutInflater.inflate(R.layout.detailslistitem,null);
            TextView heading = (TextView) view.findViewById(R.id.dl_heading);
            if (detailsElement.smallHeading){
                heading.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.fcmain_textsize_smaller));
            }
            if (detailsElement.heading==null){
                heading.setVisibility(View.GONE);
            } else {
                heading.setVisibility(View.VISIBLE);
                heading.setText(detailsElement.heading);
            }
            TextView value = (TextView) view.findViewById(R.id.dl_value);
            if (detailsElement.value==null){
                value.setVisibility(View.GONE);
            } else {
                value.setVisibility(View.VISIBLE);
                value.setText(detailsElement.value);
            }
            TextView label = (TextView) view.findViewById(R.id.dl_label);
            if (detailsElement.label==null){
                label.setVisibility(View.GONE);
            } else {
                label.setVisibility(View.VISIBLE);
                label.setText(detailsElement.label);
            }
            ImageView icon = (ImageView) view.findViewById(R.id.dl_icon);
            if ((detailsElement.icon==null) && (detailsElement.bitmap==null)){
                icon.setVisibility(View.INVISIBLE);
            } else {
                if (detailsElement.bitmap!=null){
                    icon.setVisibility(View.VISIBLE);
                    icon.setImageBitmap(detailsElement.bitmap);
                }
                else {
                    icon.setVisibility(View.VISIBLE);
                    icon.setImageBitmap(WeatherIcons.getIconBitmap(context,detailsElement.icon,false,detailsElement.applyFilter));
                }
            }
            LinearLayout lineContainer = (LinearLayout) view.findViewById(R.id.dl_linearlayout);
            if ((value.getVisibility()==View.GONE) && (label.getVisibility()==View.GONE) && (icon.getVisibility()==View.INVISIBLE)){
                lineContainer.setVisibility(View.GONE);
            } else {
                lineContainer.setVisibility(View.VISIBLE);
            }
            if ((detailsElement.heading!=null) || (detailsElement.value!=null) || (detailsElement.icon!=null) || (detailsElement.label!=null)){
                targetView.addView(view);
            }
        }
    }

    private DetailsElement newDetail(String title, int icon, String value, String label){
        DetailsElement detailsElement = new DetailsElement(title, icon, value, label);
        return detailsElement;
    }

    private DetailsElement newDetail(String title, Bitmap icon, String value, String label){
        DetailsElement detailsElement = new DetailsElement(title, icon, value, label);
        return detailsElement;
    }

    private DetailsElement newDetail(String title, int icon, String value, String label, boolean applyFilter){
        DetailsElement detailsElement = new DetailsElement(title, icon, value, label);
        detailsElement.applyFilter = applyFilter;
        return detailsElement;
    }


    private void setDetails(Weather.WeatherInfo weatherInfo){
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
                setDetail(valuesListClouds,list.get(i));
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
                setDetail(valuesListWind,list.get(i));
            }
        } else {
            valuesListWind.setVisibility(View.GONE);
        }
        list = new ArrayList<DetailsElement>();
        if ((weatherInfo.hasSunDuration()) || (weatherInfo.hasRH()) || (weatherInfo.hasTemperature5cm()) || (weatherInfo.hasProbPrecipitation()) || (weatherInfo.hasPrecipitation())){
            valuesListElements.setVisibility(View.VISIBLE);
            valuesListElements.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
            list.add(newDetail(getResources().getString(R.string.wd_general),null,null,null));
            if (weatherInfo.hasSunDuration()){
                list.add(newDetail(null,WeatherIcons.SYMBOL_SUN,weatherInfo.getSunDurationInMinutes()+"min",getResources().getString(R.string.wd_sun)));
            }
            if (weatherInfo.hasRH()){
                list.add(newDetail(null,WeatherIcons.SYMBOL_RH,weatherInfo.getRHInt()+"%",getResources().getString(R.string.welcome_s2_text11)));
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
                setDetail(valuesListElements,list.get(i));
            }
        } else {
            valuesListElements.setVisibility(View.GONE);
        }
        list = new ArrayList<DetailsElement>();
        if ((weatherInfo.hasVisibility()) || (weatherInfo.hasProbVisibilityBelow1km())){
            valuesListVisibility.setVisibility(View.VISIBLE);
            valuesListVisibility.setBackground(ThemePicker.getWidgetBackgroundDrawable(context));
            list.add(newDetail("Sicht",null,null,null));
            if (weatherInfo.hasVisibility()){
                list.add(newDetail(null,WeatherIcons.BIOCULAR,(ForecastAdapter.getVisibilityCharSequence(weatherInfo,WeatherSettings.getDistanceDisplayUnit(context))).toString(),getResources().getString(R.string.preference_displayvisibility_title)));
            }
            if (weatherInfo.hasProbVisibilityBelow1km()){
                list.add(newDetail(null,null,(ForecastAdapter.getVisibilityBelow1kmCharSequence(weatherInfo)).toString(),getResources().getString(R.string.wd_pv1)));
            }
            double RadS3;       // kJ/m²; short wave radiation balance during last 3h
            double RRad1;       // % (0..80); global irradiance within the last hour
            double Rad1h;       // kJ/m²; global irradiance
            double RadL3;       // kJ/m²; long wave radiation balance during last 3h

            for (int i=0; i<list.size(); i++){
                setDetail(valuesListVisibility,list.get(i));
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
                setDetail(valuesListIncidents,list.get(i));
            }
        } else {
            valuesListIncidents.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
