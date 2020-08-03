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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {

    private final static String SIS_ABOUT_DIALOG_STATE="ABOUT_DIALOG_VISIBLE";
    private final static String SIS_WHATSNEW_DIALOG_STATE="WHATSNEW_DIALOG_VISIBLE";

    public final static String MAINAPP_CUSTOM_REFRESH_ACTION     = "MAINAPP_CUSTOM_ACTION_REFRESH";

    StationsManager stationsManager;
    ArrayList<String> station_descriptions;
    int spinner_initial_position;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private Dialog aboutDialog;
    private boolean aboutDiaglogVisible=false;

    private Dialog whatsNewDialog;
    private boolean whatsNewDialogVisible=false;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MAINAPP_CUSTOM_REFRESH_ACTION)){
                displayWeatherForecast();
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
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PrivateLog.log(this,Tag.MAIN,"App started.");
        final WeatherSettings weatherSettings = new WeatherSettings(this);
        PrivateLog.log(this,Tag.MAIN,"Settings loaded.");
        final Context context = getApplicationContext();
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                // update widgets
                Intent intent = new Intent(context,ClassicWidget.class);
                intent.setAction(ClassicWidget.WIDGET_CUSTOM_REFRESH_ACTION);
                sendBroadcast(intent);
                // check for alarm sets
                WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
                // only react if regular updates are set
                if (weatherSettings.setalarm){
                    UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext());
                }
            }
        };
        weatherSettings.sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        stationsManager = new StationsManager(context);
        final Spinner stationsSpinner = (Spinner) findViewById(R.id.stations_spinner);
        final AdapterView.OnItemSelectedListener changeListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
                // save options & get data if new item is different from previous station.
                if (!weatherSettings.station_name.equals(stationsManager.getName(pos))){
                    if (stationsManager.setStation(pos)) {
                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getText(R.string.new_station)+" "+stationsManager.getDescription(pos),Toast.LENGTH_LONG).show();
                        PrivateLog.log(context,Tag.MAIN,"-----------------------------------");
                        PrivateLog.log(context,Tag.MAIN,"New sensor: "+stationsManager.getDescription(pos)+ "("+stationsManager.getName(pos)+")");
                        PrivateLog.log(context,Tag.MAIN,"-----------------------------------");
                        getWeatherForecast();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        stationsManager.new AsyncStationsReader(){
            @Override
            public void onLoadingListFinished(ArrayList<Weather.WeatherLocation> stations) {
                super.onLoadingListFinished(stations);
                ArrayList<String> stationnames = new ArrayList<String>();
                for (int j=0;j<stations.size(); j++){
                    stationnames.add(stations.get(j).description);
                }

                ArrayAdapter<String> stationAdapter = new ArrayAdapter<String>(context,R.layout.custom_spinner_item,stationnames);
                station_descriptions = stationnames;
                stationAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
                stationsSpinner.setAdapter(stationAdapter);
                stationsSpinner.setOnItemSelectedListener(changeListener);
                spinner_initial_position = stationsManager.getSetPosition();
                if (spinner_initial_position != -1){
                    stationsSpinner.setSelection(spinner_initial_position);
                }
            }
        }.execute();

        // station_descriptions = stationsManager.getStationNames();

        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(getApplicationContext());
        if (weatherCard==null){
            Log.v(Tag.MAIN,"weatherCard ist null");
        } else {
            Log.v(Tag.MAIN,"weatherCard 6h forecasts:"+weatherCard.forecast6hourly.size());
        }
        // get new data from api or display present data.
        if (weatherCard!=null){
            Log.v(Tag.MAIN,"weather info is present in local database.");
            displayWeatherForecast(weatherCard);
            UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext());
        } else {
            Log.v(Tag.MAIN,"no weather info present in local database => forcing update.");
            UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(),UpdateAlarmManager.FORCE_UPDATE);
        }
        // show whats new dialog if necessary
        if (weatherSettings.last_version_code != BuildConfig.VERSION_CODE){
            Log.v(Tag.MAIN,"Showing wat's new dialog.");
            showWhatsNewDialog();
        }
        registerForBroadcast();
        // TEST OF NEW API
        /*
        Weather.WeatherLocation wl = new Weather().new WeatherLocation();
        wl.name="01194";
        Weather.WeatherForecastReader forecastReader = new Weather().new WeatherForecastReader(this,wl);
        forecastReader.doInBackground();
        forecastReader.execute();
        */
    }

    public void displayWeatherForecast(CurrentWeatherInfo weatherCard){
        // date
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, dd.MM.yyyy, HH:mm:ss");
        String updatetime = simpleDateFormat.format(new Date(weatherCard.polling_time));
        TextView textView_update_time = (TextView) findViewById(R.id.main_update_time);
        textView_update_time.setText(getApplicationContext().getResources().getString(R.string.main_updatetime)+" "+updatetime);
        // listview
        ListView weatherList = (ListView) findViewById(R.id.main_listview);
        Log.v("MAIN","6h forecast items availabe: "+weatherCard.forecast6hourly.size());
        ForecastAdapter forecastAdapter = new ForecastAdapter(getApplicationContext(),weatherCard.forecast6hourly);
        weatherList.setAdapter(forecastAdapter);
        // Upate the widgets, so that everything displays the same
        Intent intent = new Intent(this,ClassicWidget.class);
        intent.setAction(ClassicWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        sendBroadcast(intent);
   }

    public void displayWeatherForecast(){
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(this);
        displayWeatherForecast(weatherCard);
    }

    public void getWeatherForecast(){
        UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(getApplicationContext(),UpdateAlarmManager.FORCE_UPDATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.main_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi){
        int item_id = mi.getItemId();
        if (item_id == R.id.menu_refresh){
            PrivateLog.log(this,Tag.MAIN,"user requests update => force update");
            getWeatherForecast();
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



