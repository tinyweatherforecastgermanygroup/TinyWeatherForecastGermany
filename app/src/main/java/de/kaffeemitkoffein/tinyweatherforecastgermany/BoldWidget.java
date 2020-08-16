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
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BoldWidget extends ClassicWidget{

    @Override
    public void updateWidgetDisplay(Context c, AppWidgetManager awm, int[] widget_instances) {
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(c);
        WeatherSettings weatherSettings = new WeatherSettings(c);
        if (weatherCard != null) {
            for (int i=0; i<widget_instances.length; i++){
                // sets up a pending intent to launch main activity when the widget is touched.
                Intent intent = new Intent(c,MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(c,0,intent,0);
                RemoteViews remoteViews = new RemoteViews(c.getPackageName(),R.layout.boldwidget_layout);
                fillBoldWidgetItems(c,remoteViews,weatherSettings,weatherCard);
                remoteViews.setOnClickPendingIntent(R.id.boldwidget_maincontainer,pendingIntent);
                awm.updateAppWidget(widget_instances[i],remoteViews);
            }
        }
    }

    private void fillBoldWidgetItems(Context c, RemoteViews remoteViews, WeatherSettings weatherSettings,CurrentWeatherInfo currentWeatherInfo){
        if (weatherSettings.widget_showdwdnote) {
            remoteViews.setViewVisibility(R.id.classicwidget_reference_text, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.classicwidget_reference_text, View.GONE);
        }
        if (currentWeatherInfo.currentWeather.hasTemperature()){
            remoteViews.setTextViewText(R.id.boldwidget_current_temperature, currentWeatherInfo.currentWeather.getTemperatureInCelsiusInt()+"°");
        } else {
            remoteViews.setTextViewText(R.id.boldwidget_current_temperature,NOT_AVAILABLE);
        }
        if (currentWeatherInfo.currentWeather.hasCondition()){
            remoteViews.setImageViewResource(R.id.boldwidget_today_condition,WeatherCodeContract.getWeatherConditionDrawableResource(currentWeatherInfo.currentWeather.getCondition(),true));
        } else {
            remoteViews.setTextViewText(R.id.boldwidget_today_condition,NOT_AVAILABLE);
        }
        if (currentWeatherInfo.currentWeather.hasMaxTemperature()){
            remoteViews.setTextViewText(R.id.boldwidget_today_max,currentWeatherInfo.currentWeather.getMaxTemperatureInCelsiusInt()+"°");
        } else {
            remoteViews.setTextViewText(R.id.boldwidget_today_max,NOT_AVAILABLE);
        }
        if (currentWeatherInfo.currentWeather.hasMinTemperature()){
            remoteViews.setTextViewText(R.id.boldwidget_today_min,currentWeatherInfo.currentWeather.getMinTemperatureInCelsiusInt()+"°");
        } else {
            remoteViews.setTextViewText(R.id.boldwidget_today_min,NOT_AVAILABLE);
        }
        // FORECAST 1st DAY
        if (currentWeatherInfo.forecast24hourly.size()>=1){
            Long l = currentWeatherInfo.forecast24hourly.get(0).getTimestamp();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
            Date date = new Date();
            date.setTime(l);
            String weekday = simpleDateFormat.format(date);
            remoteViews.setTextViewText(R.id.boldwidget_dayofweek_fc1,weekday);
            if (currentWeatherInfo.forecast24hourly.get(0).hasCondition()){
                remoteViews.setImageViewResource(R.id.boldwidget_fc1_weatherconditionicon,WeatherCodeContract.getWeatherConditionDrawableResource(currentWeatherInfo.forecast24hourly.get(0).getCondition(),true));
            } else {
                remoteViews.setTextViewText(R.id.boldwidget_fc1_weatherconditionicon,NOT_AVAILABLE);
            }
            if (currentWeatherInfo.forecast24hourly.get(0).hasMaxTemperature()){
                remoteViews.setTextViewText(R.id.boldwidget_fc1_max,currentWeatherInfo.forecast24hourly.get(0).getMaxTemperatureInCelsiusInt()+"°");
            } else {
                remoteViews.setTextViewText(R.id.boldwidget_fc1_max,NOT_AVAILABLE);
            }
            if (currentWeatherInfo.forecast24hourly.get(0).hasMinTemperature()){
                remoteViews.setTextViewText(R.id.boldwidget_fc1_min,currentWeatherInfo.forecast24hourly.get(0).getMinTemperatureInCelsiusInt()+"°");
            } else {
                remoteViews.setTextViewText(R.id.boldwidget_fc1_min,NOT_AVAILABLE);
            }
        }
        // FORECAST 2nd DAY
        if (currentWeatherInfo.forecast24hourly.size()>=2){
            Long l = currentWeatherInfo.forecast24hourly.get(1).getTimestamp();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
            Date date = new Date();
            date.setTime(l);
            String weekday = simpleDateFormat.format(date);
            remoteViews.setTextViewText(R.id.boldwidget_dayofweek_fc2,weekday);
            if (currentWeatherInfo.forecast24hourly.get(1).hasCondition()){
                remoteViews.setImageViewResource(R.id.boldwidget_fc2_weatherconditionicon,WeatherCodeContract.getWeatherConditionDrawableResource(currentWeatherInfo.forecast24hourly.get(1).getCondition(),true));
            } else {
                remoteViews.setTextViewText(R.id.boldwidget_fc2_weatherconditionicon,NOT_AVAILABLE);
            }
            if (currentWeatherInfo.forecast24hourly.get(1).hasMaxTemperature()){
                remoteViews.setTextViewText(R.id.boldwidget_fc2_max,currentWeatherInfo.forecast24hourly.get(1).getMaxTemperatureInCelsiusInt()+"°");
            } else {
                remoteViews.setTextViewText(R.id.boldwidget_fc2_max,NOT_AVAILABLE);
            }
            if (currentWeatherInfo.forecast24hourly.get(1).hasMinTemperature()){
                remoteViews.setTextViewText(R.id.boldwidget_fc2_min,currentWeatherInfo.forecast24hourly.get(1).getMinTemperatureInCelsiusInt()+"°");
            } else {
                remoteViews.setTextViewText(R.id.boldwidget_fc2_min,NOT_AVAILABLE);
            }
        }
        // set opacity
        int opacity = Integer.parseInt(weatherSettings.widget_opacity);
        remoteViews.setInt(R.id.boldwidget_relative_container,"setBackgroundColor",getBackgroundInt(opacity));
    }

}
