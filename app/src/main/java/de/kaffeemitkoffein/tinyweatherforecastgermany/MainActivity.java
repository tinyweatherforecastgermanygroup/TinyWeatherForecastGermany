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

import android.content.*;
import android.os.Bundle;
import android.app.*;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {

    private final static String SIS_ABOUT_DIALOG_STATE="ABOUT_DIALOG_VISIBLE";
    private final static String SIS_WHATSNEW_DIALOG_STATE="WHATSNEW_DIALOG_VISIBLE";

    public final static String MAINAPP_CUSTOM_REFRESH_ACTION = "MAINAPP_CUSTOM_ACTION_REFRESH";

    public final static boolean API_TESTING_ENABLED = false;
    private int test_position = 0;

    StationsManager stationsManager;

    ArrayList<String> spinnerItems;
    Spinner spinner;

    ArrayList<String> station_descriptions_onlytext;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    CurrentWeatherInfo weatherCard;

    long last_updateweathercall = Calendar.getInstance().getTimeInMillis();

    private Dialog aboutDialog;
    private boolean aboutDiaglogVisible=false;

    private Dialog whatsNewDialog;
    private boolean whatsNewDialogVisible=false;

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
        weatherCard = new Weather().getCurrentWeatherInfo(this);
        if (weatherCard!=null){
            displayWeatherForecast(weatherCard);
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // action bar layout
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_HOME);

        final WeatherSettings weatherSettings = new WeatherSettings(this);
        if (weatherSettings.last_version_code != BuildConfig.VERSION_CODE){
            // remove shared preferences on app update if installed app is lower than build 6
            if (weatherSettings.last_version_code<6){
                PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
            }
            showWhatsNewDialog();
        }
        final Context context = getApplicationContext();

        final AdapterView.OnItemSelectedListener changeListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };

        stationsManager = new StationsManager(context);
        loadStationsSpinner(weatherSettings);
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
                    AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.actionbar_textview);
                    // notify GadgetBridge
                    GadgetbridgeAPI gadgetbridgeAPI = new GadgetbridgeAPI(context);
                    gadgetbridgeAPI.sendWeatherBroadcastIfEnabled();
                    // update widgets unconditonally
                    WidgetRefresher.refresh(getApplicationContext());
                }
                // reload spinner, but only if geo coordinates setting was changed
                if (key.equals(WeatherSettings.PREF_DISPLAY_STATION_GEO)){
                    stationsManager = new StationsManager(context);
                    loadStationsData();
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
                // invalidate weather display beacuse the display type or bar visibility have changed
                if (key.equals(WeatherSettings.PREF_DISPLAY_TYPE) || (key.equals(WeatherSettings.PREF_DISPLAY_BAR))){
                    displayWeatherForecast(weatherCard);
                }
            }
        };
        if (!API_TESTING_ENABLED){
            weatherSettings.sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
        weatherCard = new Weather().getCurrentWeatherInfo(getApplicationContext());
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
                    clearFavorites(weatherSettings);
                    Toast.makeText(context,getApplicationContext().getResources().getString(R.string.favorites_cleared),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void newWeatherRegionSelected(WeatherSettings weatherSettings, String station_description){
        station_description = station_description.toUpperCase();
        Context context = this.getApplicationContext();
        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getText(R.string.new_station)+" "+station_description,Toast.LENGTH_LONG).show();
        int station_pos = stationsManager.getPositionFromDescription(station_description);
        String name = stationsManager.getName(station_pos);
        stationsManager.setStation(station_pos);
        PrivateLog.log(context,Tag.MAIN,"-----------------------------------");
        PrivateLog.log(context,Tag.MAIN,"New sensor: "+stationsManager.getDescription(station_pos)+ " ("+stationsManager.getName(station_pos)+")");
        PrivateLog.log(context,Tag.MAIN,"-----------------------------------");
        last_updateweathercall = Calendar.getInstance().getTimeInMillis();
        addToSpinner(weatherSettings,station_description);
        // we do not get the forecast data here since this triggers the preference-changed-listener. This
        // listener takes care of the weather data update and updates widgets and gadgetbridge.
        // getWeatherForecast();
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.actionbar_textview);
        if (autoCompleteTextView!=null){
            autoCompleteTextView.setText("");
            autoCompleteTextView.clearListSelection();
        }
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

    private void loadStationsSpinner(final WeatherSettings weatherSettings) {
        // spinner code
        spinner = (Spinner) findViewById(R.id.stations_spinner);
        if (spinnerItems == null) {
            spinnerItems = weatherSettings.getFavorites();
        }
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
                    loadStationsSpinner(weatherSettings);
                }
                super.handleItemSelected(adapterView, view, pos, l);
            }
        };
        spinner.setOnItemSelectedListener(spinnerListener);
        spinner.setOnTouchListener(spinnerListener);
    }

    private void addToSpinner(WeatherSettings weatherSettings, String s){
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
        loadStationsSpinner(weatherSettings);
    }

    private void clearFavorites(WeatherSettings weatherSettings){
        ArrayList<String> new_spinner_items = new ArrayList<String>();
        new_spinner_items.add(weatherSettings.station_description);
        spinnerItems = new_spinner_items;
        weatherSettings.updateFavorites(spinnerItems);
        loadStationsSpinner(weatherSettings);
    }

    public void loadStationsData(){
        final Context context = this.getApplicationContext();
        // for the textview
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
                Integer station_pos = stationsManager.getPositionFromDescription(station_description);
                if (station_pos != null) {
                    if (!weatherSettings.station_name.equals(stationsManager.getName(station_pos)) && (last_updateweathercall + 3000 < Calendar.getInstance().getTimeInMillis())) {
                            newWeatherRegionSelected(weatherSettings,station_description);
                            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.actionbar_textview);
                            if (autoCompleteTextView != null) {
                                autoCompleteTextView.setText("");
                                autoCompleteTextView.clearListSelection();
                            }
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(),0);
                            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getText(R.string.station_does_not_exist), Toast.LENGTH_LONG).show();
                }
            }
        };
        // for the search icon
        final View.OnClickListener searchListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // weather settings must be read at the time of selection!
                WeatherSettings weatherSettings = new WeatherSettings(context);
                AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.actionbar_textview);
                String station_description = autoCompleteTextView.getText().toString();
                Integer station_pos = stationsManager.getPositionFromDescription(station_description);
                if (station_pos!=null){
                    if (!weatherSettings.station_name.equals(stationsManager.getName(station_pos)) && (last_updateweathercall+3000<Calendar.getInstance().getTimeInMillis())){
                        newWeatherRegionSelected(weatherSettings,station_description);
                        }
                } else {
                    Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getText(R.string.station_does_not_exist),Toast.LENGTH_LONG).show();
                }
            }
        };
        stationsManager.new AsyncStationsReader(){
            @Override
            public void onLoadingListFinished(ArrayList<Weather.WeatherLocation> stations) {
                super.onLoadingListFinished(stations);
                station_descriptions_onlytext = new ArrayList<String>();
                for (int j=0;j<stations.size(); j++){
                    String stat_description = stations.get(j).description;
                    station_descriptions_onlytext.add(stat_description);
                }
                // text searcher
                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context,R.layout.custom_dropdown_item,station_descriptions_onlytext);
                AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.actionbar_textview);
                autoCompleteTextView.setAdapter(stringArrayAdapter);
                autoCompleteTextView.setCompletionHint(context.getResources().getString(R.string.actionbar_textinput_hint));
                autoCompleteTextView.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                autoCompleteTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                autoCompleteTextView.setOnItemClickListener(clickListener);
                // anchor search icon to search
                ImageView search_icon = (ImageView) findViewById(R.id.actionbar_search_icon);
                search_icon.setOnClickListener(searchListener);
            }
        }.execute();
    }

    private void testAPI_Worker(){
        if (test_position<stationsManager.getStationCount()){
            final WeatherSettings weatherSettings = new WeatherSettings(this);
            weatherSettings.station_name = stationsManager.getName(test_position);
            weatherSettings.applyPreference(WeatherSettings.PREF_STATION_NAME,weatherSettings.station_name);
            final String name = stationsManager.getName(test_position);
            final String description = stationsManager.getDescription(test_position);
            Handler handler = new Handler();
            // Log.v(Tag.MAIN,"Waiting.");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   //  Log.v(Tag.MAIN,"------------------------------------------");
                   // Log.v(Tag.MAIN,"Testing station # "+test_position+" named "+name+ " described as "+description);
                   // Log.v(Tag.MAIN,"-------------------------------------------");
                    getWeatherForecast();
                }
            },2000);
        } else {
            // Log.v(Tag.MAIN,"Testing finished.");
        }
    }



    private void testAPI_Call(){
        final Context context = this;
        registerForBroadcast();
        if (stationsManager.getStationCount()==0){
            stationsManager.new AsyncStationsReader(){
                @Override
                public void onLoadingListFinished(ArrayList<Weather.WeatherLocation> stations) {
                    super.onLoadingListFinished(stations);
                    testAPI_Worker();
                }
            }.execute();
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
        PrivateLog.log(getApplicationContext(),Tag.MAIN,"displaying: "+weatherCard.getCity()+" sensor: "+weatherCard.name);
        ListView weatherList = (ListView) findViewById(R.id.main_listview);
        // ForecastAdapter forecastAdapter = new ForecastAdapter(getApplicationContext(),weatherCard.forecast6hourly,weatherCard.forecast1hourly);
        ForecastAdapter forecastAdapter = new ForecastAdapter(getApplicationContext(),getCustomForecastWeatherInfoArray(weatherCard),weatherCard.forecast1hourly);
        weatherList.setAdapter(forecastAdapter);
        UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(),UpdateAlarmManager.CHECK_FOR_UPDATE);
   }

    public void displayWeatherForecast(){
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(this);
        if (weatherCard != null){
            displayWeatherForecast(weatherCard);
        }
    }

    public void getWeatherForecast(){
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(this);
        if (weatherCard == null){
            UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(), UpdateAlarmManager.FORCE_UPDATE);
        } else {
            displayWeatherForecast(weatherCard);
        }
    }

    public void forcedWeatherUpdate(){
        UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(), UpdateAlarmManager.FORCE_UPDATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.main_activity,menu);
        // try to show incons in drop-down menu
        if (menu.getClass().getSimpleName().equals("MenuBuilder")){
            try {
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(menu,true);
            } catch (Exception e){
                // todo
            }
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

    private void registerForBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(MAINAPP_CUSTOM_REFRESH_ACTION);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(receiver,filter);
    }

}



