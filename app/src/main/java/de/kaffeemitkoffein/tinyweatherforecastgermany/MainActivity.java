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

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.app.*;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private final static String SIS_ABOUT_DIALOG_STATE="ABOUT_DIALOG_VISIBLE";
    private final static String SIS_WHATSNEW_DIALOG_STATE="WHATSNEW_DIALOG_VISIBLE";

    public final static String MAINAPP_CUSTOM_REFRESH_ACTION = "MAINAPP_CUSTOM_ACTION_REFRESH";
    public final static String MAINAPP_SSL_ERROR = "MAINAPP_SSL_ERROR";
    public final static String MAINAPP_SHOW_PROGRESS = "SHOW_PROGRESS";
    public final static String MAINAPP_HIDE_PROGRESS = "HIDE_PROGRESS";

    public final static boolean API_TESTING_ENABLED = false;
    private int test_position = 0;

    private ForecastAdapter forecastAdapter;

    Context context;
    StationsManager stationsManager;
    ArrayList<String> spinnerItems;
    Spinner spinner;
    AutoCompleteTextView autoCompleteTextView;
    StationSearchEngine stationSearchEngine;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    CurrentWeatherInfo weatherCard;

    long last_updateweathercall = Calendar.getInstance().getTimeInMillis();

    private Dialog aboutDialog;
    private boolean aboutDiaglogVisible=false;

    private Dialog whatsNewDialog;
    private boolean whatsNewDialogVisible=false;

    Executor executor;

    ArrayList<WeatherWarning> localWarnings;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MAINAPP_CUSTOM_REFRESH_ACTION)){
                displayWeatherForecast();
                if (API_TESTING_ENABLED){
                    test_position ++;
                    testAPI_Call();
                }
            }
            if (intent.getAction().equals(MAINAPP_SSL_ERROR)){
                PrivateLog.log(getApplicationContext(),Tag.MAIN,"ssl error intent received by main app.");
                if ((!WeatherSettings.isTLSdisabled(context)) && (Build.VERSION.SDK_INT < 28)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getResources().getString(R.string.connerror_title));
                    builder.setIcon(R.mipmap.ic_announcement_white_24dp);
                    builder.setMessage(context.getResources().getString(R.string.connerror_message));
                    builder.setNegativeButton(R.string.geoinput_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // do nothing
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setPositiveButton(R.string.alertdialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            WeatherSettings.setDisableTLS(getApplicationContext(),true);
                            // re-launch the weather update via http
                            UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(),UpdateAlarmManager.FORCE_UPDATE);
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
            if (intent.getAction().equals(DataUpdateService.SHOW_PROGRESS)){
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.main_progressbar);
                if (progressBar!=null){
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
            if (intent.getAction().equals(DataUpdateService.HIDE_PROGRESS)){
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.main_progressbar);
                if (progressBar!=null){
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            if (intent.getAction().equals(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE)){
                checkIfWarningsApply();
            }
        }
    };

    final AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            // weather settings must be read at the time of selection!
            WeatherSettings weatherSettings = new WeatherSettings(context);
            /*
             * We found a bug; compare to https://developer.android.com/reference/android/widget/AdapterView.OnItemClickListener.
             * pos is the same as id, returning the position of the clicked item from top like shown on the screen, but
             * NOT the position in the adapter. We therefore have to get it manually from our own StationsManager class.
             */
            TextView tv = (TextView) view.findViewById(R.id.dropdown_textitem);
            String station_description = tv.getText().toString();
            Integer station_pos = stationsManager.getPositionFromDescription(station_description,true);
            if (station_pos != null) {
                if (!weatherSettings.station_name.equals(stationsManager.getName(station_pos)) && (last_updateweathercall + 3000 < Calendar.getInstance().getTimeInMillis())) {
                    newWeatherRegionSelected(weatherSettings,station_description);
                    if (autoCompleteTextView != null) {
                        autoCompleteTextView.setText("");
                        autoCompleteTextView.clearListSelection();
                    }
                    try{
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(),0);
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    } catch (Exception e){
                        PrivateLog.log(context,Tag.MAIN,"Warning: hiding soft keyboard failed.");
                    }
                }
            } else {
                final ArrayList<Weather.WeatherLocation> stations = stationsManager.getStations();
                Location startLocation = stationSearchEngine.getCentroidLocationFromArea(station_description);
                if (startLocation!=null){
                    calcualateClosestStations(stations,startLocation);
                } else {
                    try {
                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getText(R.string.station_does_not_exist),Toast.LENGTH_LONG).show();
                    } catch (Exception e){
                        PrivateLog.log(context,Tag.MAIN,"Error: station does not exist.");
                    }
                }
            }
        }
    };

    private void performTextSearch(){
    // weather settings must be read at the time of selection!
            WeatherSettings weatherSettings = new WeatherSettings(context);
            String station_description = autoCompleteTextView.getText().toString();
            Integer station_pos = stationsManager.getPositionFromDescription(station_description,true);
            if (station_pos!=null){
                if (!weatherSettings.station_name.equals(stationsManager.getName(station_pos))){
                    newWeatherRegionSelected(weatherSettings,station_description);
                }
            } else {
                final ArrayList<Weather.WeatherLocation> stations = stationsManager.getStations();
                Location startLocation = stationSearchEngine.getCentroidLocationFromArea(station_description);
                if (startLocation!=null){
                    calcualateClosestStations(stations,startLocation);
                } else {
                    try {
                      Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getText(R.string.station_does_not_exist),Toast.LENGTH_LONG).show();
                    } catch (Exception e){
                      PrivateLog.log(context,Tag.MAIN,"Error: station does not exist.");
                    }
                }
            }
        }


    final View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            performTextSearch();
        }
    };

    final View.OnKeyListener searchOnEnterListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            if ((keyEvent.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                performTextSearch();
                return true;
            } else {
                return false;
            }
        }
    };

    @Override
    protected void onPause(){
        if (aboutDialog != null){
            if (aboutDialog.isShowing()){
                aboutDialog.dismiss();
            }
        }
        if (whatsNewDialog != null){
            if (whatsNewDialog.isShowing()){
                whatsNewDialog.dismiss();
            }
        }
        unregisterReceiver(receiver);
        stopGPSLocationSearch();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putBoolean(SIS_ABOUT_DIALOG_STATE,aboutDiaglogVisible);
        savedInstanceState.putBoolean(SIS_WHATSNEW_DIALOG_STATE,whatsNewDialogVisible);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle restoreInstanceState){
        aboutDiaglogVisible   = restoreInstanceState.getBoolean(SIS_ABOUT_DIALOG_STATE);
        whatsNewDialogVisible = restoreInstanceState.getBoolean(SIS_WHATSNEW_DIALOG_STATE);
        if (aboutDiaglogVisible){
            showAboutDialog();
        }
        if (whatsNewDialogVisible){
            showWhatsNewDialog();
        }
    }

    @Override
    protected void onResume(){
        registerForBroadcast();
        // this is necessary if the update of weather data occurs while the app is in the background
        try {
            if (weatherCard==null){
                weatherCard = new Weather().getCurrentWeatherInfo(this);
            }
        } catch (Exception e){
            PrivateLog.log(getApplicationContext(),Tag.MAIN,"Error in onResume when getting weather: "+e.getMessage());
        }
        try {
            checkIfWarningsAreOutdated();
        } catch (Exception e){
            PrivateLog.log(getApplicationContext(),Tag.MAIN,"Error in onResume when checking for warnings: "+e.getMessage());
        }
        if (weatherCard!=null){
            try {
                displayWeatherForecast(weatherCard);
            } catch (Exception e){
                PrivateLog.log(getApplicationContext(),Tag.MAIN,"Error in onResume when displaying weather: "+e.getMessage());
            }
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        setTheme(R.style.AppTheme);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.actionbar_textview);
        // disable log to logcat if release is not a userdebug
        disableLogToLogcatIfNotUserDebug();
        // force a database access at the beginning to check for a needed database upgrade
        try {
            WeatherForecastContentProvider.checkForDatabaseUpgrade(getApplicationContext());
            AreaContentProvider.checkForDatabaseUpgrade(getApplicationContext());
        } catch (Exception e){
            PrivateLog.log(context,Tag.MAIN,"Error checking/upgrading database!");
        }
        // action bar layout
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_HOME);
        executor = Executors.newSingleThreadExecutor();
        prepareAraDatabase();
        final WeatherSettings weatherSettings = new WeatherSettings(this);
        if (weatherSettings.last_version_code != BuildConfig.VERSION_CODE){
            // remove shared preferences on app update if installed app is lower than build 20
            if ((weatherSettings.last_version_code>WeatherSettings.PREF_LAST_VERSION_CODE_DEFAULT) && (weatherSettings.last_version_code<20)){
                // PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
                WeatherSettings.resetStationToDefault(getApplicationContext());
                WeatherSettings.setCurrentAppVersionFlag(getApplicationContext());
                showWarning(R.mipmap.ic_warning_white_24dp,getResources().getString(R.string.warning_stationreset_title),getResources().getString(R.string.warning_stationreset_text));
            } else {
                showWhatsNewDialog();
            }
        }
        final Context context = getApplicationContext();
        stationsManager = new StationsManager(context);
        loadStationsSpinner();
        loadStationsData();
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(WeatherSettings.PREF_WIDGET_SHOWDWDNOTE) || (key.equals(WeatherSettings.PREF_WIDGET_OPACITY))){
                    WidgetRefresher.refresh(context.getApplicationContext());
                }
                // reload weather data if necessary
                if (key.equals(WeatherSettings.PREF_STATION_NAME)){
                    UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(),UpdateAlarmManager.CHECK_FOR_UPDATE);
                    getWeatherForecast();
                    // update warnings async
                    checkIfWarningsApply();
                    // notify GadgetBridge
                    GadgetbridgeAPI gadgetbridgeAPI = new GadgetbridgeAPI(context);
                    gadgetbridgeAPI.sendWeatherBroadcastIfEnabled();
                    // update widgets unconditonally
                    WidgetRefresher.refresh(getApplicationContext());
                }
                // show geo
                if (key.equals(WeatherSettings.PREF_DISPLAY_STATION_GEO)){
                    if (weatherCard != null){
                        displayUpdateTime(weatherCard);
                    }
                }
                // invalidate menu if warnings visibility has changed
                if (key.equals(WeatherSettings.PREF_WARNINGS_DISABLE)){
                    invalidateOptionsMenu();
                }
                // invalidate weather display beacuse the display options have changed
                if (key.equals(WeatherSettings.PREF_DISPLAY_TYPE) || (key.equals(WeatherSettings.PREF_DISPLAY_BAR)) || (key.equals(WeatherSettings.PREF_DISPLAY_PRESSURE)) ||
                        (key.equals(WeatherSettings.PREF_DISPLAY_VISIBILITY)) || (key.equals(WeatherSettings.PREF_DISPLAY_SUNRISE)) || (key.equals(WeatherSettings.PREF_DISPLAY_DISTANCE_UNIT))){
                    // on 1st app call, weatherCard can be still null
                    if (weatherCard!=null){
                        displayWeatherForecast(weatherCard);
                    }
                }
                // invalidate weather display and widgets
                if ((key.equals(WeatherSettings.PREF_DISPLAY_WIND_TYPE)) || (key.equals(WeatherSettings.PREF_DISPLAY_WIND_UNIT))){
                    // on 1st app call, weatherCard can be still null
                    if (weatherCard!=null){
                        displayWeatherForecast(weatherCard);
                        // refreshing widgets only makes sense when there is weather data
                        WidgetRefresher.refresh(getApplicationContext());
                    }
                }
            }
        };
        if (!API_TESTING_ENABLED){
            weatherSettings.sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
        try {
            weatherCard = new Weather().getCurrentWeatherInfo(getApplicationContext());
        } catch (Exception e){
            PrivateLog.log(context,Tag.MAIN,"Error loading present weather data: "+e.getMessage());
        }
        // get new data from api or display present data.
        if (!API_TESTING_ENABLED){
            if (weatherCard!=null){
                displayWeatherForecast(weatherCard);
            } else {
                UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(),UpdateAlarmManager.FORCE_UPDATE);
            }
        }

        // test API
        if (API_TESTING_ENABLED){
            testAPI_Init();
        }
        // register view to clear favorites
        ImageView reset_favorites_imageview = (ImageView) findViewById(R.id.main_reset_favorites);
        if (reset_favorites_imageview!=null){
            reset_favorites_imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearFavorites();
                    Toast.makeText(context,getApplicationContext().getResources().getString(R.string.favorites_cleared),Toast.LENGTH_LONG).show();
                }
            });
        }
        Button cancel_gps = (Button) findViewById(R.id.main_cancel_gps);
        cancel_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopGPSLocationSearch();
            }
        });
        // check if a geo intent was sent
        Intent geo_intent = getIntent();
        if (geo_intent!=null){
            String intent_action = geo_intent.getAction();
            if (intent_action!=null){
                if (intent_action.equals(Intent.ACTION_VIEW)){
                    String intent_scheme = geo_intent.getScheme();
                    if (intent_scheme!=null){
                        if (intent_scheme.equals("geo")){
                            String received_geolocation = geo_intent.getData().toString();
                            if (received_geolocation!=null){
                                try {
                                    String received_latitude = received_geolocation.substring(received_geolocation.indexOf(":")+1,received_geolocation.indexOf(","));
                                    String received_longitude = received_geolocation.substring(received_geolocation.indexOf(",")+1,received_geolocation.indexOf("?"));
                                    String received_zoom = received_geolocation.substring(received_geolocation.indexOf("z=")+2);
                                    Location own_location = new Location("manual");
                                    own_location.setTime(Calendar.getInstance().getTimeInMillis());
                                    try {
                                        double latitude = Location.convert(standardizeGeo(received_latitude));
                                        double longitude = Location.convert(standardizeGeo(received_longitude));
                                        own_location.setLatitude(latitude);
                                        own_location.setLongitude(longitude);
                                        launchStationSearchByLocation(own_location);
                                    } catch (Exception e){
                                        // invalid geo coordinates
                                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.georeceive_error),Toast.LENGTH_LONG).show();
                                    }
                                } catch (IndexOutOfBoundsException e){
                                    // invalid geo-string (uri)
                                    Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.georeceive_error),Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void newWeatherRegionSelected(WeatherSettings weatherSettings, String station_description){
        station_description= station_description.toUpperCase();
        final String station_description2 = station_description;
        final Context context = this.getApplicationContext();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getText(R.string.new_station)+" "+station_description2,Toast.LENGTH_LONG).show();
                } catch (Exception e){
                    PrivateLog.log(context,Tag.MAIN,"Warning: new station message failed.");
                }
            }
        });
        try {
            int station_pos = stationsManager.getPositionFromDescription(station_description);
            stationsManager.setStation(station_pos);
            PrivateLog.log(context,Tag.MAIN,"-----------------------------------");
            PrivateLog.log(context,Tag.MAIN,"New sensor: "+stationsManager.getDescription(station_pos)+ " ("+stationsManager.getName(station_pos)+")");
            PrivateLog.log(context,Tag.MAIN,"-----------------------------------");
            last_updateweathercall = Calendar.getInstance().getTimeInMillis();
            addToSpinner(station_description);
        } catch (Exception e){
            PrivateLog.log(context,Tag.MAIN,"Error: Setting new station failed: "+e.getMessage());
        }
        // we do not get the forecast data here since this triggers the preference-changed-listener. This
        // listener takes care of the weather data update and updates widgets and gadgetbridge.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (autoCompleteTextView!=null){
                    autoCompleteTextView.setText("");
                    autoCompleteTextView.clearListSelection();
                }
            }
        });
    }

    private class SpinnerListener implements View.OnTouchListener, AdapterView.OnItemSelectedListener{
        private boolean user_touched_spinner = false;
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            user_touched_spinner = true;
            return false;
        }
        public void handleItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            // to the stuff
            user_touched_spinner = false;
        }
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (user_touched_spinner){
                handleItemSelected(adapterView, view,  i, l);
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private void loadStationsSpinner() {
        WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
        // spinner code
        spinner = (Spinner) findViewById(R.id.stations_spinner);
        spinnerItems = weatherSettings.getFavorites();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner_item, spinnerItems);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        final Context context = this;
        // for the spinner
        final SpinnerListener spinnerListener = new SpinnerListener() {
            @Override
            public void handleItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                // weather settings must be read at the time of selection!
                WeatherSettings weatherSettings = new WeatherSettings(context);
                TextView tv = (TextView) view.findViewById(R.id.spinner_textitem);
                String station_description = tv.getText().toString();
                Integer station_pos = stationsManager.getPositionFromDescription(station_description);
                if (station_pos != null) {
                    if (!weatherSettings.station_name.equals(stationsManager.getName(station_pos))) {
                        newWeatherRegionSelected(weatherSettings, station_description);
                    }
                } else {
                    PrivateLog.log(context, Tag.MAIN, "Station from favorites not found!");
                    loadStationsSpinner();
                }
                super.handleItemSelected(adapterView, view, pos, l);
            }
        };
        spinner.setOnItemSelectedListener(spinnerListener);
        spinner.setOnTouchListener(spinnerListener);
    }

    private void addToSpinner(String s){
        WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
        spinnerItems = weatherSettings.getFavorites();
        ArrayList<String> new_spinner_items = new ArrayList<String>();
        new_spinner_items.add(s);
        for (int i=0; i<spinnerItems.size() && i<10; i++){
            // prevent double entries
            if (!spinnerItems.get(i).equals(s)){
                new_spinner_items.add(spinnerItems.get(i));
            }
        }
        spinnerItems = new_spinner_items;
        weatherSettings.updateFavorites(spinnerItems);
        loadStationsSpinner();
    }

    private void clearFavorites(){
        WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
        ArrayList<String> new_spinner_items = new ArrayList<String>();
        new_spinner_items.add(weatherSettings.station_description);
        spinnerItems = new_spinner_items;
        weatherSettings.updateFavorites(spinnerItems);
        loadStationsSpinner();
    }

    public void loadStationsData(){
        StationsManager.StationsReader stationsReader = new StationsManager.StationsReader(context) {
            @Override
            public void onLoadingListFinished(ArrayList<Weather.WeatherLocation> stations) {
                super.onLoadingListFinished(stations);
                stationsManager.stations = stations;
                //station_descriptions_onlytext = new ArrayList<String>();
                for (int j = 0; j < stations.size(); j++) {
                    String stat_description = stations.get(j).description;
                    //station_descriptions_onlytext.add(stat_description);
                }
                stationSearchEngine = new StationSearchEngine(context,executor,null,stationsManager){
                    @Override
                    public void newEntries(ArrayList<String> newEntries){
                        super.newEntries(newEntries);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context, R.layout.custom_dropdown_item, stationSearchEngine.entries);
                                if (autoCompleteTextView==null){
                                    autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.actionbar_textview);
                                }
                                autoCompleteTextView.setAdapter(stringArrayAdapter);
                                autoCompleteTextView.setCompletionHint(context.getResources().getString(R.string.actionbar_textinput_hint));
                                autoCompleteTextView.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                                autoCompleteTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                                autoCompleteTextView.setOnItemClickListener(clickListener);
                                // anchor search icon to search
                                ImageView search_icon = (ImageView) findViewById(R.id.actionbar_search_icon);
                                search_icon.setOnClickListener(searchListener);
                                // set listener to search field
                                autoCompleteTextView.setOnKeyListener(searchOnEnterListener);
                            }
                        });
                    }
                };
            }
        };
        executor.execute(stationsReader);
    }

    private void testAPI_Worker(){
        if (test_position<stationsManager.getStationCount()){
            final WeatherSettings weatherSettings = new WeatherSettings(this);
            weatherSettings.station_name = stationsManager.getName(test_position);
            weatherSettings.applyPreference(WeatherSettings.PREF_STATION_NAME,weatherSettings.station_name);
            final String name = stationsManager.getName(test_position);
            final String description = stationsManager.getDescription(test_position);
            Handler handler = new Handler();
            //Log.v(Tag.MAIN,"Waiting.");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   //Log.v(Tag.MAIN,"------------------------------------------");
                   //Log.v(Tag.MAIN,"Testing station # "+test_position+" named "+name+ " described as "+description);
                   //Log.v(Tag.MAIN,"-------------------------------------------");
                    getWeatherForecast();
                }
            },4000);
        } else {
             //Log.v(Tag.MAIN,"Testing finished.");
        }
    }



    private void testAPI_Call(){
        final Context context = this;
        registerForBroadcast();
        if (stationsManager.getStationCount()==0){
            StationsManager.StationsReader stationsReader = new StationsManager.StationsReader(context) {
                @Override
                public void onLoadingListFinished(ArrayList<Weather.WeatherLocation> stations) {
                    super.onLoadingListFinished(stations);
                    testAPI_Worker();
                }
            };
            executor.execute(stationsReader);
        } else {
            testAPI_Worker();
        }
    }

    private void testAPI_Init(){
        // reset preferences
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
        // set start position
        final WeatherSettings weatherSettings = new WeatherSettings(this);
        // disable gadgetbridge support for testing und set start position in settings
        weatherSettings.serve_gadgetbridge = false;
        weatherSettings.station_name = stationsManager.getName(test_position);
        weatherSettings.applyPreference(WeatherSettings.PREF_SERVE_GADGETBRIDGE,false);
        weatherSettings.applyPreference(WeatherSettings.PREF_STATION_NAME,weatherSettings.station_name);
        testAPI_Call();
    }

    public void displayUpdateTime(CurrentWeatherInfo currentWeatherInfo){
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, dd.MM.yyyy, HH:mm:ss");
        String updatetime = simpleDateFormat.format(new Date(currentWeatherInfo.polling_time));
        TextView textView_update_time = (TextView) findViewById(R.id.main_update_time);
        textView_update_time.setText(getApplicationContext().getResources().getString(R.string.main_updatetime)+" "+updatetime);
        TextView textView_station_geo = (TextView) findViewById(R.id.main_station_geo);
        WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
        if (weatherSettings.display_station_geo){
            String s = "Lat.: "+weatherSettings.station_latitude+" Long.: "+weatherSettings.station_longitude+" Alt.: "+weatherSettings.station_altitude;
            try {
                textView_station_geo.setText(getApplicationContext().getResources().getString(R.string.station)+" Lat.: "+new DecimalFormat("0.00").format(weatherSettings.station_latitude)+
                        " Long.: "+new DecimalFormat("0.00").format(weatherSettings.station_longitude)+
                        " Alt.: "+new DecimalFormat("0.00").format(weatherSettings.station_altitude));
            } catch (Exception e){
                textView_station_geo.setText("-");
            }
        } else {
            textView_station_geo.setVisibility(View.INVISIBLE);
            textView_station_geo.invalidate();
        }
    }

    private int get24passedPosition(CurrentWeatherInfo currentWeatherInfo,int lasthourlypostion){
        if ((currentWeatherInfo.forecast1hourly.size()>lasthourlypostion) && (lasthourlypostion>0)){
            long last1hourtime = currentWeatherInfo.forecast1hourly.get(lasthourlypostion).getTimestamp();
            if (currentWeatherInfo.forecast6hourly.size()>0){
                int position = 0;
                long time = currentWeatherInfo.forecast6hourly.get(position).getTimestamp();
                while (position<currentWeatherInfo.forecast6hourly.size() && time<last1hourtime){
                    position++;
                    time = currentWeatherInfo.forecast6hourly.get(position).getTimestamp();
                }
                return position;
            }
        }
        return 0;
    }

    private ArrayList<Weather.WeatherInfo> getCustomForecastWeatherInfoArray(CurrentWeatherInfo weatherCard){
        WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
        if (weatherSettings.getDisplayType() == WeatherSettings.DISPLAYTYPE_6HOURS){
            return weatherCard.forecast6hourly;
        }
        if (weatherSettings.getDisplayType() == WeatherSettings.DISPLAYTYPE_1HOUR){
            return weatherCard.forecast1hourly;
        }
        ArrayList<Weather.WeatherInfo> weatherInfos = new ArrayList<Weather.WeatherInfo>();
        for (int i=0; i<24 && i<weatherCard.forecast1hourly.size(); i++){
            weatherInfos.add(weatherCard.forecast1hourly.get(i));
        }
        for (int i=get24passedPosition(weatherCard,weatherInfos.size()-1); i<weatherCard.forecast6hourly.size(); i++){
            weatherInfos.add(weatherCard.forecast6hourly.get(i));
        }
        return weatherInfos;
    }

    public void displayWeatherForecast(CurrentWeatherInfo weatherCard){
        displayUpdateTime(weatherCard);
        //PrivateLog.log(getApplicationContext(),Tag.MAIN,"displaying: "+weatherCard.getCity()+" sensor: "+weatherCard.weatherLocation.name);
        ListView weatherList = (ListView) findViewById(R.id.main_listview);
        forecastAdapter = new ForecastAdapter(getApplicationContext(),getCustomForecastWeatherInfoArray(weatherCard),weatherCard.forecast1hourly,weatherCard.weatherLocation);
        if (localWarnings!=null){
            forecastAdapter.setWarnings(localWarnings);
        }
        weatherList.setAdapter(forecastAdapter);
        UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(),UpdateAlarmManager.CHECK_FOR_UPDATE);
   }

    public void displayWeatherForecast(){
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(this);
        if (weatherCard==null){
            weatherCard = new CurrentWeatherInfo();
        }
        displayWeatherForecast(weatherCard);
    }

    public void getWeatherForecast(){
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(this);
        if ((weatherCard == null) || (API_TESTING_ENABLED)){
            UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(), UpdateAlarmManager.FORCE_UPDATE);
        } else {
            displayWeatherForecast(weatherCard);
        }
    }

    public void forcedWeatherUpdate(){
        UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(), UpdateAlarmManager.FORCE_UPDATE);
    }

    private void checkIfWarningsApply(){
        // invalidate current warnings
        localWarnings = null;
        if (forecastAdapter!=null){
            forecastAdapter.setWarnings(null);
        }
        // determine new warnings async
        WeatherWarnings.getWarningsForLocationRunnable getWarningsForLocationRunnable = new WeatherWarnings.getWarningsForLocationRunnable(context,null,null){
            @Override
            public void onResult(ArrayList<WeatherWarning> result){
                localWarnings = result;
                if (forecastAdapter!=null){
                    forecastAdapter.setWarnings(localWarnings);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayWeatherForecast();
                    }
                });
            }
        };
        executor.execute(getWarningsForLocationRunnable);
    }

    private void checkIfWarningsAreOutdated(){
        if (WeatherSettings.areWarningsOutdated(context) && WeatherSettings.updateWarnings(context)){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    UpdateAlarmManager.updateWarnings(context,false);
                }
            });
        } else {
            checkIfWarningsApply();
        }
    }

    public static int getColorFromResource(Context context, int id){
        TypedValue typedValue = new TypedValue();
        context.getApplicationContext().getTheme().resolveAttribute(id,typedValue,true);
        return typedValue.data;
    }

    @SuppressWarnings("deprecation")
    private void setOverflowMenuItemColor(Menu menu, int id, int string_id,int color_id){
        String s = getApplicationContext().getResources().getString(string_id);
        MenuItem menuItem = menu.findItem(id);
        if (!menuItem.isVisible()){
            SpannableString spannableString = new SpannableString(s);
            int color = getColorFromResource(getApplicationContext(),color_id);
            spannableString.setSpan(color,0,s.length(),0);
            menuItem.setTitle(spannableString);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.main_activity,menu);
        if (!WeatherSettings.forceNoMenuIcons(getApplicationContext())){
            // try to show incons in drop-down menu
            if (menu.getClass().getSimpleName().equals("MenuBuilder")){
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu,true);
                } catch (Exception e){
                    // this is a hack to disable the icon-in-menu-feature permanently if it fails on some devices.
                    // A flag is set in the settings and the app is force-restarted with an intent.
                    // this should only happen once at the first app launch, if ever.
                    WeatherSettings.setForceNoMenuIconsFlag(getApplicationContext(),true);
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
            setOverflowMenuItemColor(menu,R.id.menu_refresh,R.string.warnings_update, R.attr.colorText);
            setOverflowMenuItemColor(menu,R.id.menu_warnings,R.string.warnings_button, R.attr.colorText);
            setOverflowMenuItemColor(menu,R.id.menu_texts,R.string.texts_button, R.attr.colorText);
            setOverflowMenuItemColor(menu,R.id.menu_settings,R.string.settings_button, R.attr.colorText);
            setOverflowMenuItemColor(menu,R.id.menu_geoinput,R.string.geoinput_button, R.attr.colorText);
            setOverflowMenuItemColor(menu,R.id.menu_about,R.string.about_button, R.attr.colorText);
            setOverflowMenuItemColor(menu,R.id.menu_license,R.string.license_button, R.attr.colorText);
            setOverflowMenuItemColor(menu,R.id.menu_whatsnew,R.string.whatsnew_button, R.attr.colorText);
        }
        // disable weather warnings if desired by user
        WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
        if (weatherSettings.warnings_disabled){
            for (int i=0; i<menu.size(); i++){
                if (menu.getItem(i).getItemId()==R.id.menu_warnings){
                    menu.getItem(i).setVisible(false);
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem mi){
        int item_id = mi.getItemId();
        if (item_id == R.id.menu_refresh){
            PrivateLog.log(this,Tag.MAIN,"user requests update => force update");
            forcedWeatherUpdate();
            return true;
        }
        if (item_id == R.id.menu_warnings) {
            Intent i = new Intent(this, WeatherWarningActivity.class);
            startActivity(i);
            return true;
        }
        if (item_id == R.id.menu_settings) {
            Intent i = new Intent(this, Settings.class);
            startActivity(i);
            return true;
        }
        if (item_id == R.id.menu_geoinput) {
            startGeoinput();
            return true;
        }
        if (item_id==R.id.menu_license) {
            Intent i = new Intent(this, LicenseInfo.class);
            i.putExtra(LicenseInfo.DATA_TITLE, getResources().getString(R.string.license_title));
            i.putExtra(LicenseInfo.DATA_TEXTRESOURCE, "license");
            i.putExtra(LicenseInfo.DATA_BUTTONTEXT,getResources().getString(R.string.button_continue));
            startActivity(i);
            return true;
        }
        if (item_id == R.id.menu_about) {
            showAboutDialog();
            return true;
        }
        if (item_id == R.id.menu_whatsnew) {
            showWhatsNewDialog();
            return true;
        }
        if (item_id == R.id.menu_texts) {
            Intent i = new Intent(this, TextForecastListActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(mi);
    }

    public void showAboutDialog(){
        aboutDialog = new Dialog(this);
        aboutDialog.setContentView(R.layout.aboutdialog);
        aboutDialog.setTitle(getResources().getString(R.string.app_name));
        aboutDialog.setCancelable(true);
        String versioning = BuildConfig.VERSION_NAME + " (build "+BuildConfig.VERSION_CODE+")";
        Button contbutton = (Button) aboutDialog.findViewById(R.id.about_button);
        contbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                aboutDiaglogVisible=false;
                aboutDialog.dismiss();
            }
        });
        aboutDialog.show();
        aboutDiaglogVisible=true;
        TextView textView = (TextView) aboutDialog.findViewById(R.id.about_textview);
        String textfile = "about";
        InputStream inputStream = getResources().openRawResource(getResources().getIdentifier(textfile,"raw",getApplicationContext().getPackageName()));
        try {
            int size = inputStream.available();
            byte[] textdata = new byte[size];
            inputStream.read(textdata);
            inputStream.close();
            String text = new String(textdata);
            text = text.replace("[VERSION]",versioning);
            if (WeatherSettings.appReleaseIsUserdebug()){
                text = text + PrivateLog.getDebugInfoString(getApplicationContext());
                text = text + PrivateLog.getDisplayInfoString(getApplicationContext());
                text = text + PrivateLog.getCurrentStationInfoString(getApplicationContext());
            }
            textView.setText(text);
        } catch (IOException e) {
            textView.setText("Error.");
        }
    }

    public void showWhatsNewDialog(){
        whatsNewDialog = new Dialog(this);
        whatsNewDialog.setContentView(R.layout.whatsnewdialog);
        whatsNewDialog.setTitle(getResources().getString(R.string.app_name));
        whatsNewDialog.setCancelable(true);
        Button contbutton = (Button) whatsNewDialog.findViewById(R.id.whatsnew_button);
        contbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                whatsNewDialogVisible=false;
                whatsNewDialog.dismiss();
                // update version code in preferences so that this dialog is not shown anymore in this version
                final WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
                weatherSettings.applyPreference(WeatherSettings.PREF_LAST_VERSION_CODE,BuildConfig.VERSION_CODE);
            }
        });
        whatsNewDialog.show();
        whatsNewDialogVisible=true;
        TextView textView = (TextView) whatsNewDialog.findViewById(R.id.whatsnew_textview);
        String textfile = "whatsnew";
        InputStream inputStream = getResources().openRawResource(getResources().getIdentifier(textfile,"raw",getApplicationContext().getPackageName()));
        try {
            int size = inputStream.available();
            byte[] textdata = new byte[size];
            inputStream.read(textdata);
            inputStream.close();
            String text = new String(textdata);
            text = text.replace("[VERSION]",BuildConfig.VERSION_NAME);
            textView.setText(text);
        } catch (IOException e) {
            textView.setText("Error.");
        }
    }

    public static boolean deleteAreaDatabase(Context context){
        boolean result = context.deleteDatabase(AreaContentProvider.AreaDatabaseHelper.DATABASE_NAME);
        if (result){
            PrivateLog.log(context,Tag.DATABASE,"Areas database deleted.");
        }
        return result;
    }


    private void prepareAraDatabase(){
        if (WeatherSettings.areWarningsDisabled(this)){
            deleteAreaDatabase(this);
            return;
        }
        // update area database if:
        // a) database does not exist
        // b) if sql database is outdated
        if ((!Areas.doesAreaDatabaseExist(this)) || (!Areas.AreaDatabaseCreator.areAreasUpToDate(this))){
            deleteAreaDatabase(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(getApplicationContext().getResources().getString(R.string.areadatabasecreator_title));
            LayoutInflater layoutInflater = this.getLayoutInflater();
            final View view = layoutInflater.inflate(R.layout.areadatabasecreator_message,null,false);
            builder.setView(view);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.areadatabasecreator_progressbar);
            final TextView progressText = (TextView) view.findViewById(R.id.areadatabasecreator_textinfo);
            Areas.AreaDatabaseCreator areasDataBaseCreator = new Areas.AreaDatabaseCreator(context,executor){
                @Override
                public void showProgress(final int progress, final String text){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progress);
                            progressText.setText(text);
                        }
                    });
                }

                @Override
                public void onFinished(){
                    super.onFinished();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                            // after new database is available, warnings need to be rebuilt and checked again
                            checkIfWarningsApply();
                        }
                    });
                }
            };
            areasDataBaseCreator.create();
        }
    }

    private void showWarning(int icon, String title, String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(icon);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setNeutralButton(getApplicationContext().getResources().getString(R.string.alertdialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void registerForBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(MAINAPP_CUSTOM_REFRESH_ACTION);
        filter.addAction(MAINAPP_SSL_ERROR);
        filter.addAction(MAINAPP_SHOW_PROGRESS);
        filter.addAction(MAINAPP_HIDE_PROGRESS);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE);
        registerReceiver(receiver,filter);
    }

    private void disableLogToLogcatIfNotUserDebug(){
        WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
        if (!WeatherSettings.appReleaseIsUserdebug()){
            if (weatherSettings.log_to_logcat){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_warning_white_24dp);
                builder.setTitle(getApplicationContext().getResources().getString(R.string.alertdialog_2_title));
                builder.setMessage(getApplicationContext().getResources().getString(R.string.alertdialog_2_text));
                final Context context = getApplicationContext();
                builder.setNeutralButton(getApplicationContext().getResources().getString(R.string.alertdialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PrivateLog.log(context,Tag.MAIN,"Logging to logcat is being disabled...");
                        WeatherSettings weatherSettings = new WeatherSettings(context);
                        weatherSettings.applyPreference(WeatherSettings.PREF_LOG_TO_LOGCAT,WeatherSettings.PREF_LOG_TO_LOGCAT_DEFAULT);
                        Toast.makeText(context,context.getResources().getString(R.string.alertdialog_2_toast),Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    private void startGeoinput(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getApplicationContext().getResources().getString(R.string.geoinput_title));
        LayoutInflater layoutInflater = this.getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.geoinput,null,false);
        final CheckBox useGPS = view.findViewById(R.id.geoinput_check_gps);
        final EditText text_latitude = view.findViewById(R.id.geoinput_edit_latitude);
        final EditText text_longitude = view.findViewById(R.id.geoinput_edit_longitude);
        useGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    text_latitude.setEnabled(false);
                    text_longitude.setEnabled(false);
                    WeatherSettings.setUSEGPSFlag(getApplicationContext(),true);
                } else {
                    text_latitude.setEnabled(true);
                    text_longitude.setEnabled(true);
                    WeatherSettings.setUSEGPSFlag(getApplicationContext(),false);
                }
            }
        });
        builder.setIcon(R.mipmap.ic_gps_fixed_white_24dp);
        builder.setView(view);
        builder.setPositiveButton(R.string.geoinput_search, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (useGPS.isChecked()){
                    if (!hasLocationPermission()){
                        requestLocationPermission();
                    } else {
                        startGPSLocationSearch();
                    }
                } else {
                    Location own_location = new Location("manual");
                    own_location.setTime(Calendar.getInstance().getTimeInMillis());
                    try {
                        double latitude = Location.convert(standardizeGeo(text_latitude.getText().toString()));
                        double longitude = Location.convert(standardizeGeo(text_longitude.getText().toString()));
                        own_location.setLatitude(latitude);
                        own_location.setLongitude(longitude);
                        if ((latitude>=-90) && (latitude<=90) && (longitude>=-180) && (longitude<=180)) {
                            launchStationSearchByLocation(own_location);
                        } else {
                            showSimpleLocationAlert(getApplicationContext().getResources().getString(R.string.geoinput_wrongvalue));
                        }
                    } catch (Exception e) {
                        showSimpleLocationAlert(getApplicationContext().getResources().getString(R.string.geoinput_wrongformat));
                    }
                }
            }
        });
        builder.setNegativeButton(R.string.geoinput_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        useGPS.setChecked(WeatherSettings.getUseGPSFlag(getApplicationContext()));
        Location location = getLastKnownLocation();
        if (location!=null){
            text_longitude.setText(new DecimalFormat("000.00000").format(location.getLongitude()));
            text_latitude.setText(new DecimalFormat("00.00000").format(location.getLatitude()));
            TextView gps_known_knote = view.findViewById(R.id.geoinput_known_note);
            gps_known_knote.setVisibility(View.VISIBLE);
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
            gps_known_knote.setText(getApplicationContext().getResources().getString(R.string.geoinput_known_note)+" "+simpleDateFormat.format(location.getTime()));
        }
    }

    private void launchStationSearchByLocation(final Location own_location){
        // load stations
        final ArrayList<Weather.WeatherLocation> stations = stationsManager.getStations();
        if (stations.size()>0){
            calcualateClosestStations(stations,own_location);
        } else {
            StationsManager.StationsReader stationsReader = new StationsManager.StationsReader(context) {
                @Override
                public void onLoadingListFinished(ArrayList<Weather.WeatherLocation> new_stations) {
                    super.onLoadingListFinished(new_stations);
                    calcualateClosestStations(stations, own_location);
                }
            };
            executor.execute(stationsReader);
        }
    }

    private void calcualateClosestStations(ArrayList<Weather.WeatherLocation> stations, final Location own_location){
        stopGPSLocationSearch();
        for (int i=0; i<stations.size(); i++){
            Location location_station = new Location("weather");
            location_station.setLatitude(stations.get(i).latitude);
            location_station.setLongitude(stations.get(i).longitude);
            stations.get(i).distance = location_station.distanceTo(own_location);
        }
        Collections.sort(stations, new Comparator<Weather.WeatherLocation>() {
            @Override
            public int compare(Weather.WeatherLocation t0, Weather.WeatherLocation t1) {
                if (t0.distance<t1.distance){
                    return -1;
                }
                if (t0.distance==t1.distance){
                    return 0;
                }
                return 1;
            }
        });
        int items_count = own_location.getExtras().getInt(Weather.WeatherLocation.EXTRAS_ITEMS_TO_SHOW,20);
        ArrayList<String> stationDistanceList = new ArrayList<String>();
        for (int i=0; (i<stations.size()) && (i<items_count); i++) {
            stationDistanceList.add(stations.get(i).description+" ["+new DecimalFormat("0.0").format(stations.get(i).distance/1000) + " km]");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getApplicationContext().getResources().getString(R.string.geoinput_title));
        builder.setIcon(R.mipmap.ic_gps_fixed_white_24dp);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.geochoice,null,false);
        TextView textView_long = (TextView) view.findViewById(R.id.geochoice_reference_longitude);
        TextView textView_lat  = (TextView) view.findViewById(R.id.geochoice_reference_latitude);
        TextView textView_time = (TextView) view.findViewById(R.id.geochoice_reference_time);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, dd.MM.yyyy, HH:mm:ss");
        textView_long.setText(getApplicationContext().getResources().getString(R.string.geoinput_reflocation_longitude)+" "+new DecimalFormat("000.00000").format(own_location.getLongitude()));
        textView_lat.setText(getApplicationContext().getResources().getString(R.string.geoinput_reflocation_latitude)+" "+new DecimalFormat("00.00000").format(own_location.getLatitude()));
        long time = own_location.getTime();
        if (time!=0){
        textView_time.setText(getApplicationContext().getResources().getString(R.string.geoinput_reflocation_time)+" "+simpleDateFormat.format(new Date(time)));
        } else {
            // do not display time if time is unknown in Location data
            String s = own_location.getExtras().getString(Weather.WeatherLocation.EXTRAS_NAME,"");
            textView_time.setText(s);
        }
        builder.setView(view);
        builder.setNegativeButton(R.string.geoinput_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.geochoice_item,R.id.geochoiceitem_text,stationDistanceList);
        ListView listView = view.findViewById(R.id.geochoice_listview);
        listView.setAdapter(arrayAdapter);
        final AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
                TextView textView = view.findViewById(R.id.geochoiceitem_text);
                String station_description = textView.getText().toString();
                station_description = station_description.substring(0,station_description.indexOf(" ["));
                Integer station_pos = stationsManager.getPositionFromDescription(station_description);
                if (station_pos!=null){
                    if (!weatherSettings.station_name.equals(stationsManager.getName(station_pos))){
                        alertDialog.dismiss();
                        newWeatherRegionSelected(weatherSettings,station_description);
                    } else {
                        alertDialog.dismiss();
                    }
                } else {
                    alertDialog.dismiss();
                    Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getText(R.string.station_does_not_exist),Toast.LENGTH_LONG).show();
                }
            }
        };
        listView.setOnItemClickListener(clickListener);
    }

    private static final int PERMISSION_CALLBACK_LOCATION = 121;

    private boolean hasLocationPermission(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
               // permission not granted
                return false;
            }
            else
            {
                // permission is granted, ok
                return true;
            }
        }
        else
        {
            // before api 23, permissions are always granted, so everything is ok
            return true;
        }
    }

    @SuppressLint("NewApi")
    private void requestLocationPermission(){
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_CALLBACK_LOCATION);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int permRequestCode, String perms[], int[] grantRes){
        Boolean hasLocationPermission = false;
        for (int i=0; i<grantRes.length; i++){
            if ((perms[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) && (grantRes[i]==PackageManager.PERMISSION_GRANTED)){
                hasLocationPermission = true;
            }
        }
        if (permRequestCode == PERMISSION_CALLBACK_LOCATION){
            if (hasLocationPermission){
                startGPSLocationSearch();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    showLocationPermissionsRationale();
                }
            }
        }
    }

    @Override
    public void onTrimMemory(final int level) {

        switch (level) {
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                if (forecastAdapter != null) {
                    forecastAdapter.clearBitmapCache();
                }
                break;
        }
    }

    private void showSimpleLocationAlert(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getApplicationContext().getResources().getString(R.string.geoinput_title));
        builder.setIcon(R.mipmap.ic_gps_fixed_white_24dp);
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

    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            launchStationSearchByLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            // nothing to do
        }

        @Override
        public void onProviderEnabled(String s) {
            startGPSLocationSearch();
        }

        @Override
        public void onProviderDisabled(String s) {
            startGPSLocationSearch();
        }
    };

    private void stopGPSLocationSearch(){
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager!=null){
            if (locationListener != null) {
                locationManager.removeUpdates(locationListener);
            }
        }
        RelativeLayout gps_spinner_layout = (RelativeLayout) findViewById(R.id.main_gps_progress_holder);
        gps_spinner_layout.setVisibility(View.GONE);
    }

    @SuppressLint("MissingPermission")
    private void startGPSLocationSearch(){
        RelativeLayout gps_spinner_layout = (RelativeLayout) findViewById(R.id.main_gps_progress_holder);
        gps_spinner_layout.setVisibility(View.VISIBLE);
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager!=null){
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,locationListener,null);
            } else
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,locationListener,null);
                }  else {
                    showSimpleLocationAlert(getApplicationContext().getResources().getString(R.string.geoinput_noprovider));
                    gps_spinner_layout.setVisibility(View.GONE);
                }
            }
    }

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation(){
        Location location = null;
        if (hasLocationPermission()) {
            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                    if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
                        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    }
                    if (location == null) {
                        if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
                            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            }
                        }
                    }
                    if (location == null) {
                        if (locationManager.getProvider(LocationManager.PASSIVE_PROVIDER) != null) {
                            if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                            }
                        }
                    }
            }
        }
        return location;
    }

    public String standardizeGeo(final String s){
        return s.replace(",",".");
    }
}



