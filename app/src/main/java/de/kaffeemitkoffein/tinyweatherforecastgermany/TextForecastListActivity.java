/**
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
import android.content.*;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TextForecastListActivity extends Activity {

    final static public String ACTION_UPDATE_TEXTS = "ACTION_UPDATE_TEXTS";
    final static public String UPDATE_TEXTS_RESULT = "UPDATE_TEXTS_RESULT";

    ArrayList<TextForecast> textForecasts;
    TextForecastAdapter textForecastAdapter;
    ListView textforecasts_listview;
    ImageView floatButton;
    Context context;
    ActionBar actionBar;
    boolean forceWeatherUpdateFlag = false;
    Executor executor;
    APIReaders.TextForecastRunnable textForecastRunnable;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(ACTION_UPDATE_TEXTS)){
                hideProgressBar();
                showList();
            }
            if (intent.getAction().equals(MainActivity.MAINAPP_HIDE_PROGRESS)){
                hideProgressBar();
                forceWeatherUpdateFlag = false;
            }
        }
    };

    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView numberView = (TextView) view.findViewById(R.id.textforecast_item_number);
            int position = Integer.parseInt(numberView.getText().toString());
            Intent intent = new Intent(context,TextForecastViewActivity.class);
            intent.putExtra(TextForecastViewActivity.TEXTFORECAST_ITEM,position);
            startActivity(intent);
        }
    };

    View.OnClickListener floatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WeatherSettings.setTextForecastFilterEnabled(context,!WeatherSettings.isTextForecastFilterEnabled(context));
            displayFloatButton();
            showList();
        }
    };

    @Override
    protected void onResume() {
        registerForBroadcast();
        if (DataStorage.Updates.isSyncDue(context,WeatherSettings.Updates.Category.TEXTS)){
            PrivateLog.log(context,PrivateLog.TEXTS,PrivateLog.INFO,"Weather texts are outdated, updating data.");
            executor.execute(textForecastRunnable);
        } else {
            PrivateLog.log(context,PrivateLog.TEXTS,PrivateLog.INFO,"Weather texts are oup to date, showing available data.");
            showList();
        }
        super.onResume();
    }

    @Override
    protected void onPause(){
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        ThemePicker.SetTheme(this);
        super.onCreate(savedInstanceState);
        WeatherSettings.setRotationMode(this);
        setContentView(R.layout.activity_textforecastlist);
        registerForBroadcast();
        executor = Executors.newSingleThreadExecutor();
        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        displayFloatButton();
        floatButton.setOnClickListener(floatClickListener);
        textForecastRunnable = new APIReaders.TextForecastRunnable(this){
            @Override
            public void onPositiveResult(){
               DataStorage.Updates.setLastUpdate(context,WeatherSettings.Updates.Category.TEXTS,Calendar.getInstance().getTimeInMillis());
               showList();
               // trigger a sync request to update other items if necessary.
               // Texts will not be updated again because setLastUpdate was called. When no syncs are due, nothing
               // will happen at all.
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        ContentResolver.requestSync(MainActivity.getManualSyncRequest(context,WeatherSyncAdapter.UpdateFlags.FLAG_UPDATE_DEFAULT));
                    }
                });
            }
            @Override
            public void onNegativeResult(){
                // do nothing
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.textforecastlist,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int item_id = mi.getItemId();
        if (item_id == R.id.menu_refresh) {
            APIReaders.TextForecastRunnable textForecastRunnable = new APIReaders.TextForecastRunnable(context) {
                @Override
                public void onPositiveResult() {
                    Intent intent = new Intent();
                    intent.setAction(TextForecastListActivity.ACTION_UPDATE_TEXTS);
                    intent.putExtra(TextForecastListActivity.UPDATE_TEXTS_RESULT, true);
                    PrivateLog.log(context, PrivateLog.TEXTS, PrivateLog.INFO, "Weather texts updated successfully.");
                    context.sendBroadcast(intent);
                    DataStorage.Updates.setLastUpdate(context,WeatherSettings.Updates.Category.TEXTS, Calendar.getInstance().getTimeInMillis());
                }

                @Override
                public void onNegativeResult() {
                    super.onNegativeResult();
                    hideProgressBar();
                }
            };
            showProgressBar();
            executor.execute(textForecastRunnable);
            return true;
        }
        return super.onOptionsItemSelected(mi);
    }

    private void showList(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textForecasts = new ArrayList<TextForecast>();
                if (WeatherSettings.isTextForecastFilterEnabled(context)) {
                    textForecasts = TextForecasts.getLatestTextForecastsOnly(context);
                } else {
                    textForecasts = TextForecasts.getTextForecasts(context);
                }
                // update action bar
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, dd.MM.yyyy, HH:mm:ss");
                actionBar.setSubtitle(simpleDateFormat.format(new Date(DataStorage.Updates.getLastUpdate(context,WeatherSettings.Updates.Category.TEXTS)))+" ("+textForecasts.size()+")");
                // set adapter
                textForecastAdapter = new TextForecastAdapter(getBaseContext(),textForecasts);
                textforecasts_listview = (ListView) findViewById(R.id.textforecasts_listview);
                textforecasts_listview.setAdapter(textForecastAdapter);
                textforecasts_listview.setOnItemClickListener(clickListener);
            }
        });
    }

    private void displayFloatButton(){
        if (floatButton==null){
            floatButton = (ImageView) findViewById(R.id.textforecasts_circlefloat);
        }
        GradientDrawable gradientDrawable = (GradientDrawable) floatButton.getDrawable();
        if (WeatherSettings.isTextForecastFilterEnabled(context)){
            gradientDrawable.setColorFilter(MainActivity.getColorFromResource(context,R.attr.colorSecondary), PorterDuff.Mode.SRC_ATOP);
        } else {
            gradientDrawable.setColorFilter(MainActivity.getColorFromResource(context,R.attr.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void showProgressBar(){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.textforecasts_progressbar);
        if (progressBar!=null){
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar(){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.textforecasts_progressbar);
        if (progressBar!=null){
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void registerForBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_TEXTS);
        filter.addAction(MainActivity.MAINAPP_HIDE_PROGRESS);
        registerReceiver(broadcastReceiver,filter);
    }

}
