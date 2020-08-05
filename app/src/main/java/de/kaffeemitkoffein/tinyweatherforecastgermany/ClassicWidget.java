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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

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
        int[] idarray = new int[appWidgetID];
        updateWidgetDisplay(c,awm,idarray);
    }

    @Override
    public void onUpdate(Context c, AppWidgetManager awm, int[] widget_instances){
        // checks for update & launches update if necessary;
        // refresh widgets, if no update was made.
        // in case of an update, the widgets are refreshed by a callback of WIDGET_CUSTOM_REFRESH_ACTION
        UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(c);
        updateWidgetDisplay(c,awm,widget_instances);
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
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(c);
        WeatherSettings weatherSettings = new WeatherSettings(c);
        if (weatherCard != null) {
            for (int i=0; i<widget_instances.length; i++){
                // sets up a pending intent to launch main activity when the widget is touched.
                Intent intent = new Intent(c,MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(c,0,intent,0);
                RemoteViews remoteViews = new RemoteViews(c.getPackageName(),R.layout.classicwidget_layout);
                remoteViews.setOnClickPendingIntent(R.id.classicwidget_maincontainer,pendingIntent);
                remoteViews.setTextViewText(R.id.classicwidget_locationtext,weatherCard.getCity());
                if (weatherCard.currentWeather.hasCondition()){
                    int weathercondition = weatherCard.currentWeather.getCondition();
                    remoteViews.setTextViewText(R.id.classicwidget_weatherconditiontext,new WeatherCodeContract().getWeatherConditionText(c,weathercondition));
                    remoteViews.setImageViewResource(R.id.classicwidget_weatherconditionicon,new WeatherCodeContract().getWeatherConditionDrawableResource(weathercondition,weatherCard.currentWeather.isDaytime()));
                }
                if (weatherCard.currentWeather.hasTemperature()){
                    remoteViews.setTextViewText(R.id.classicwidget_temperature,String.valueOf(weatherCard.currentWeather.getTemperatureInCelsius()+"°"));
                }
                if ((weatherCard.currentWeather.hasMinTemperature())&&(weatherCard.currentWeather.hasMaxTemperature())){
                    remoteViews.setTextViewText(R.id.classicwidget_temperature_highlow,weatherCard.currentWeather.getMinTemperatureInCelsiusInt()+"° | "+weatherCard.currentWeather.getMaxTemperatureInCelsius()+"°");
                }
                if (weatherCard.currentWeather.hasWindSpeed()){
                    String s = String.valueOf(weatherCard.currentWeather.getWindSpeedInKmhInt());
                    if (weatherCard.currentWeather.hasFlurries()){
                        s = s + " ("+ weatherCard.currentWeather.getFlurriesInKmhInt()+")";
                    }
                    remoteViews.setTextViewText(R.id.classicwidget_wind,s);
                    remoteViews.setTextViewText(R.id.classicwidget_wind_unit,"km/h");
                }
                if (weatherCard.currentWeather.hasWindDirection()){
                    remoteViews.setImageViewBitmap(R.id.classicwidget_windarrow,weatherCard.currentWeather.getArrowBitmap(c));
                }
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
