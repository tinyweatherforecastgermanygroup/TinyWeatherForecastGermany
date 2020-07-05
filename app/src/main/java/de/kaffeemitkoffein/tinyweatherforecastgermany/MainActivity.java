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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private final static String SIS_ABOUT_DIALOG_STATE="ABOUT_DIALOG_VISIBLE";
    private final static String SIS_WHATSNEW_DIALOG_STATE="WHATSNEW_DIALOG_VISIBLE";

    public final static String MAINAPP_CUSTOM_REFRESH_ACTION     = "MAINAPP_CUSTOM_ACTION_REFRESH";

    private ArrayList<String> stationNames = new ArrayList<String>();
    private StationsArrayList stationsArrayList;
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
        final WeatherSettings weatherSettings = new WeatherSettings(this);
        if (weatherSettings.is_weatherprovider) {
            TextView infotext = (TextView) findViewById(R.id.main_selectstation_text);
            infotext.setText(R.string.main_isprovider);
        }
        final Context context = getApplicationContext();
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                // update widgets
                Intent intent = new Intent(context,ClassicWidget.class);
                intent.setAction(ClassicWidget.WIDGET_CUSTOM_REFRESH_ACTION);
                sendBroadcast(intent);
                // check for alarm sets
                UpdateAlarmManager.setUpdateAlarmsIfAppropriate(getApplicationContext());
            }
        };
        weatherSettings.sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        stationsArrayList = new StationsArrayList(this);
        stationNames = stationsArrayList.getStringArrayListOfNames();
        Spinner stationsSpinner = (Spinner) findViewById(R.id.stations_spinner);
        stationsSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> stationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,stationNames);
        stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stationsSpinner.setAdapter(stationAdapter);
        spinner_initial_position = getPositionInStationNames(weatherSettings.station);
        if (spinner_initial_position != -1){
            stationsSpinner.setSelection(spinner_initial_position);
        }
        WeatherCard weatherCard = new WeatherForecastContentProvider().readWeatherForecast(getApplicationContext());
        // get new data from api or display present data.
        if (weatherCard != null){
            if (UpdateChecker.eligibleForForecastUpdate(getApplicationContext())){
                getWeatherForecast();
            } else {
                displayWeatherForecast(weatherCard);
            }
        } else {
            getWeatherForecast();
        }
        // show whats new dialog if necessary
        if (weatherSettings.last_version_code != BuildConfig.VERSION_CODE){
            showWhatsNewDialog();
        }
        registerForBroadcast();
        UpdateAlarmManager.setUpdateAlarmsIfAppropriate(getApplicationContext());
    }

    private int getPositionInStationNames(String s){
        for (int i = 0; i<stationNames.size();i++){
            if (s.equals(stationNames.get(i))){
                return i;
            }
        }
        return -1;
    }

    private void Log(String s){
        // Log.v("ID",s);
    }

    public void displayWeatherForecast(WeatherCard weatherCard){
        // listview
        ListView weatherList = (ListView) findViewById(R.id.main_listview);
        ForecastAdapter forecastAdapter = new ForecastAdapter(getApplicationContext(),weatherCard);
        weatherList.setAdapter(forecastAdapter);
        // Upate the widgets, so that everything displays the same
        Intent intent = new Intent(this,ClassicWidget.class);
        intent.setAction(ClassicWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        sendBroadcast(intent);
        // Logs
        Log("***************************");
        Log("Ausgegeben am "+weatherCard.ausgegeben_am);
        Log("Ausgegeben von "+weatherCard.ausgegeben_von);
        Log("Code1: "+weatherCard.fdat);
        Log("Code2: "+weatherCard.ortscode);
        Log("Klimagebiet: "+weatherCard.klimagebiet);
        Log("Zeitstempel: "+weatherCard.zeitstempel);
        String s="";
        for (int i=0; i<9; i++){
            s = s + weatherCard.uhrzeit[i]+" ";
        }
        Log("Uhrzeit             : "+s);
        s="";
        for (int i=0; i<9; i++){
            s = s + weatherCard.bewoelkung_max[i]+" ";
        }
        Log("Bewoelkung (max)    : "+s);
        s="";
        for (int i=0; i<9; i++){
            s = s + weatherCard.bewoelkung[i]+" ";
        }
        Log("Bewoelkung          : "+s);
        s="";
        for (int i=0; i<9; i++){
            s = s + weatherCard.bewoelkung_min[i]+" ";
        }
        Log("Bewoelkung (min)    : "+s);
        s="";
        for (int i=0; i<9; i++){
            s = s + weatherCard.niederschlag_max[i]+" ";
        }
        Log("Niederschlag (max)  : "+s);
        s="";
        for (int i=0; i<9; i++){
            s = s + weatherCard.niederschlag[i]+" ";
        }
        Log("Niederschlag        : "+s);
        s="";
        for (int i=0; i<9; i++){
            s = s + weatherCard.niederschlag_min[i]+" ";
        }
        Log("Niederschlag (min)  : "+s);
        s="";
        for (int i=0; i<9; i++){
            s = s + weatherCard.lufttemperatur_max[i]+" ";
        }
        Log("Temperatur (max)    : "+s);
        s="";
        for (int i=0; i<9; i++){
            s = s + weatherCard.lufttemperatur[i]+" ";
        }
        Log("Temperatur          : "+s);
        s="";
        for (int i=0; i<9; i++){
            s = s + weatherCard.lufttemperatur_min[i]+" ";
        }
        Log("Temperatur (min)    : "+s);
        s="";
        for (int i=0; i<9; i++){
            s = s + weatherCard.wind[i]+" ";
        }
        Log("Wind                : "+s);
        s="";
        for (int i=0; i<9; i++){
            s = s + weatherCard.boeen[i]+" ";
        }
        Log("Boeen               : "+s);
        Log("***************************");
    }

    public void displayWeatherForecast(){
        WeatherForecastContentProvider weatherForecastContentProvider = new WeatherForecastContentProvider();
        WeatherCard weatherCard = weatherForecastContentProvider.readWeatherForecast(this);
        displayWeatherForecast(weatherCard);
    }

    public void getWeatherForecast(){
        Intent intent = new Intent(getApplicationContext(),WeatherUpdateService.class);
        startService(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        WeatherSettings weatherSettings = new WeatherSettings(getApplicationContext());
        if (!weatherSettings.is_weatherprovider){
            // save options & get data if new item is different from previous station.
            if (!weatherSettings.station.equals(stationNames.get(pos))){
                weatherSettings.station = stationNames.get(pos);
                weatherSettings.savePreferences();
                Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getText(R.string.new_station)+" "+stationNames.get(pos),Toast.LENGTH_LONG).show();
                getWeatherForecast();
            } else {
                // do nothing, as new station is old station.
            }
        } else {
            // if this app is the registered lineageos weather provider service, set spinner back to the weather
            // location set by the system.
            Spinner stationsSpinner = (Spinner) findViewById(R.id.stations_spinner);
            stationsSpinner.setSelection(spinner_initial_position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // nothing to do here.
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



