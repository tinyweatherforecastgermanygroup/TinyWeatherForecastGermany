/**
 * This file is part of Tiny24hWeatherForecastGermany.
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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.net.URL;
import java.util.Calendar;

public class ClassicWidget extends AppWidgetProvider {

    public static final String WIDGET_CUSTOM_REFRESH_ACTION     = "de.kaffeemitkoffein.feinstaubwidget.WIDGET_CUSTOM_ACTION_REFRESH";

    /**
     * This is called when the widget gets enabled.
     */

    @Override
    public void onEnabled(Context c){
        super.onEnabled(c);
    }

    @Override
    public void onDisabled(Context c){
        super.onDisabled(c);
    }

    @Override
    public void onDeleted(Context c, int[] ints){
        super.onDisabled(c);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context c, AppWidgetManager awm, int appWidgetID, Bundle newOptions){
        Log.v("WIDGET","onAppWidgetOptionsChanged");
        int[] idarray = new int[appWidgetID];
        updateWidgetDisplay(c,awm,idarray);
    }

    @Override
    public void onUpdate(Context c, AppWidgetManager awm, int[] widget_instances){
        // get settings and time values
        WeatherSettings weatherSettings = new WeatherSettings(c);
        long update_hours = weatherSettings.getUpdateInterval();
        long time = Calendar.getInstance().getTimeInMillis();
        // 3 600 000 millisecs = 1 hour
        WeatherForecastContentProvider weatherForecastContentProvider = new WeatherForecastContentProvider();
        WeatherCard weatherCard = weatherForecastContentProvider.readWeatherForecast(c);
        if (weatherCard != null){
            if (time >= ((update_hours*3600000) + weatherCard.polling_time)) {
                // trigger a data update
                launchWeatherDataUpdate(c,awm,widget_instances);
            } else {
                // simply refresh display with present data
                updateWidgetDisplay(c,awm,widget_instances);
            }
        }
    }

    /**
     * This overrides the onReceive method to filter the call to simply update the display of the widgets. The
     * broadcast is sent by the DataFetcher after a successful data update from the api.
     *
     * @param c
     * @param i
     */

    @Override
    public void onReceive(Context c, Intent i){
        super.onReceive(c, i);
        if (i != null){
            String action = i.getAction();
            if (action.equals(WIDGET_CUSTOM_REFRESH_ACTION)){
                widgetRefreshAction(c,i);
            }
      }
    }

    /**
     * Updates the display of the wigdgets.
     * @param c
     * @param awm
     * @param widget_instances
     */

    public void updateWidgetDisplay(Context c, AppWidgetManager awm, int[] widget_instances){
        Log.v("WIDGET","UpdateWidgetDisplay");
        WeatherForecastContentProvider weatherForecastContentProvider = new WeatherForecastContentProvider();
        WeatherCard weatherCard = weatherForecastContentProvider.readWeatherForecast(c);
        // uncomment this to override with random weather data for test purposes
        // FakeWeatherData fakeWeatherData = new FakeWeatherData();
        // weatherCard = fakeWeatherData.getInstance();
        WeatherSettings weatherSettings = new WeatherSettings(c);
        if (weatherCard != null) {
            WeatherCodeContract weatherCodeContract = new WeatherCodeContract(weatherCard,WeatherCodeContract.WEATHER_TODAY);
            int weathercondition = weatherCodeContract.getWeatherCondition();
            for (int i=0; i<widget_instances.length; i++){
                // sets up a pending intent to launch main activity when the widget is touched.
                Intent intent = new Intent(c,MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(c,0,intent,0);
                RemoteViews remoteViews = new RemoteViews(c.getPackageName(),R.layout.classicwidget_layout);
                remoteViews.setOnClickPendingIntent(R.id.classicwidget_maincontainer,pendingIntent);
                remoteViews.setTextViewText(R.id.classicwidget_locationtext,weatherCard.klimagebiet);
                remoteViews.setTextViewText(R.id.classicwidget_weatherconditiontext,weatherCodeContract.getWeatherConditionText(c,weathercondition));
                remoteViews.setImageViewResource(R.id.classicwidget_weatherconditionicon,weatherCodeContract.getWeatherConditionDrawableResource(weathercondition));
                remoteViews.setTextViewText(R.id.classicwidget_temperature,String.valueOf(weatherCard.getCurrentTemp())+"°");
                remoteViews.setTextViewText(R.id.classicwidget_temperature_highlow,String.valueOf(weatherCard.todaysLow())+ "° | "+String.valueOf(weatherCard.todaysHigh())+"°");
                remoteViews.setTextViewText(R.id.classicwidget_wind,weatherCard.getCurrentWind()+" ("+ weatherCard.getCurrentFlurries()+") km/h");
                int opacity = Integer.parseInt(weatherSettings.widget_opacity);
                remoteViews.setInt(R.id.classicwidget_maincontainer,"setBackgroundColor",getBackgroundInt(opacity));
                if (weatherSettings.widget_showdwdnote) {
                    remoteViews.setViewVisibility(R.id.classicwidget_reference_text, View.VISIBLE);
                } else {
                    remoteViews.setViewVisibility(R.id.classicwidget_reference_text, View.GONE);
                }
                awm.updateAppWidget(widget_instances[i],remoteViews);
            }
        }
    }

    public void widgetRefreshAction(Context c, Intent i){
        AppWidgetManager awm = AppWidgetManager.getInstance(c);
        int[] wi = awm.getAppWidgetIds(new ComponentName(c,this.getClass().getName()));
        if (wi.length>0){
            updateWidgetDisplay(c,awm,wi);
        }
    }

    public void refreshWidgetDisplays(Context c){
        Intent widget_small = new Intent(c,ClassicWidget.class);
        widget_small.setAction(WIDGET_CUSTOM_REFRESH_ACTION);
        c.sendBroadcast(widget_small);
    }

    private void launchWeatherDataUpdate(Context c, AppWidgetManager awm, int[] widget_instances){
        // read stations list from resources
        StationsArrayList stationsArrayList = new StationsArrayList(c);
        // get position of station in arraylist; station is retrieved from settings.
        int position = stationsArrayList.getSetStationPositionByName(c);
        // gets station instance
        Station station = stationsArrayList.stations.get(position);
        // determines web urls of api
        URL stationURLs[] = station.getAbsoluteWebURLArray();
        // Launches an async task for update. The async task will call the widget update if everything
        // went well and new data is present.
        DataFetcher dataFetcher = new DataFetcher(c,awm,widget_instances);
        dataFetcher.execute(stationURLs);
    }

    public class DataFetcher extends WeatherForecastReader {
        AppWidgetManager awm_instance = null;
        int[] appwidget_ids;
        Context context;

        public DataFetcher(Context c, AppWidgetManager awm, int[] ids){
            super(c);
            this.context = c;
            this.awm_instance = awm;
            this.appwidget_ids = ids;
        }

        /**
         * The onPositiveResult routine is called when the API request of new data was
         * successful. It triggers a refresh of the display of all widgets.
         */

        @Override
        public void onPositiveResult(){
            refreshWidgetDisplays(context);
        }

        /**
         * The onNegativeResult routine generates a runnable that is delayed for WIDGET_CONN_RETRY_DELAY milliseconds.
         * Then, the widget retries to get data.
         *
         * Caution: due to typical garbage collection behaviour of the OS, this post-delayed task is at
         * risk being garbage-collected at any time and therefore may never be called at all.
         */

        @Override
        public void onNegativeResult(){
            // nothing to do here, as update simply failed. We can ignore this.
        }
    }

    private int getBackgroundInt(int alpha){
        String hex_string = Integer.toHexString(Math.round((float)alpha * (float)2.55));
        if (hex_string.length()<2)
        {
            hex_string = "0" + hex_string;
        }
        hex_string = hex_string+"101010";
        return Color.parseColor("#"+hex_string);
    }


}
