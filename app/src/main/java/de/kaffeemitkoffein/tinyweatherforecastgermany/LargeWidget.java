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

public class LargeWidget extends ClassicWidget{

    @Override
    public void updateWidgetDisplay(Context c, AppWidgetManager awm, int[] widget_instances) {
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(c);
        WeatherSettings weatherSettings = new WeatherSettings(c);
        for (int i=0; i<widget_instances.length; i++) {
            // sets up a pending intent to launch main activity when the widget is touched.
            Intent intent = new Intent(c, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent, 0);
            RemoteViews remoteViews = new RemoteViews(c.getPackageName(), R.layout.largewidget_layout);
            remoteViews.setOnClickPendingIntent(R.id.classicwidget_maincontainer, pendingIntent);
            setClassicWidgetItems(remoteViews, weatherSettings, weatherCard, c);
            fillForecastBar(c, remoteViews, weatherCard);
            awm.updateAppWidget(widget_instances[i], remoteViews);
        }
    }

    private void fillForecastItem(Context c, int pos, RemoteViews remoteViews, Weather.WeatherInfo weatherInfo){
        int id_day = getWeekDayResource(pos);
        int id_condition = getConditionResource(pos);
        int id_max = getMaxResource(pos);
        int id_min = getMinResource(pos);
        if (weatherInfo==null){
            remoteViews.setViewVisibility(id_day, View.INVISIBLE);
            remoteViews.setViewVisibility(id_condition, View.INVISIBLE);
            remoteViews.setViewVisibility(id_max, View.INVISIBLE);
            remoteViews.setViewVisibility(id_min, View.INVISIBLE);
        } else {
            if (id_day != 0){
                if ((weatherInfo.hasMaxTemperature())||(weatherInfo.hasMinTemperature())||(weatherInfo.hasCondition())){
                    long l = weatherInfo.getTimestamp();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE");
                    Date date = new Date();
                    date.setTime(l);
                    String weekday = simpleDateFormat.format(date);
                    remoteViews.setViewVisibility(id_day, View.VISIBLE);
                    remoteViews.setTextViewText(id_day,weekday);
                } else {
                    remoteViews.setViewVisibility(id_day, View.INVISIBLE);
                }
            }
            if (id_condition != 0){
                if (weatherInfo.hasCondition()){
                    remoteViews.setViewVisibility(id_condition, View.VISIBLE);
                    remoteViews.setImageViewResource(id_condition,WeatherCodeContract.getWeatherConditionDrawableResource(weatherInfo.getCondition(),true));
                } else {
                    remoteViews.setViewVisibility(id_condition, View.INVISIBLE);
                }
            }
            if (id_max != 0){
                if (weatherInfo.hasMaxTemperature()){
                    remoteViews.setViewVisibility(id_max, View.VISIBLE);
                    remoteViews.setTextViewText(id_max,weatherInfo.getMaxTemperatureInCelsiusInt()+"°");
                } else {
                    remoteViews.setViewVisibility(id_max, View.INVISIBLE);
                }
            }
            if (id_min != 0){
                if (weatherInfo.hasMinTemperature()){
                    remoteViews.setViewVisibility(id_min, View.VISIBLE);
                    remoteViews.setTextViewText(id_min,weatherInfo.getMinTemperatureInCelsiusInt()+"°");
                } else {
                    remoteViews.setViewVisibility(id_min, View.INVISIBLE);
                }
            }
        }
    }

    private void fillForecastBar(Context c, RemoteViews remoteViews, CurrentWeatherInfo currentWeatherInfo){
        if (currentWeatherInfo==null){
            currentWeatherInfo = new CurrentWeatherInfo();
            currentWeatherInfo.setToEmpty();
        }
        int index = 0;
        while ((index < currentWeatherInfo.forecast24hourly.size() && (index<=10))) {
            fillForecastItem(c,index + 1, remoteViews, currentWeatherInfo.forecast24hourly.get(index));
            index ++;
        }
        while ((index<=10)){
            fillForecastItem(c,index + 1, remoteViews, null);
            index++;
        }
    }

    private int getWeekDayResource(int pos){
        int result = 0;
        switch (pos){
            case 1: result = R.id.largewidget_forecast_day1; break;
            case 2: result = R.id.largewidget_forecast_day2; break;
            case 3: result = R.id.largewidget_forecast_day3; break;
            case 4: result = R.id.largewidget_forecast_day4; break;
            case 5: result = R.id.largewidget_forecast_day5; break;
            case 6: result = R.id.largewidget_forecast_day6; break;
            case 7: result = R.id.largewidget_forecast_day7; break;
            case 8: result = R.id.largewidget_forecast_day8; break;
            case 9: result = R.id.largewidget_forecast_day9; break;
            case 10: result = R.id.largewidget_forecast_day10; break;
        }
        return result;
    }

    private int getConditionResource(int pos){
        int result = 0;
        switch (pos){
            case 1: result = R.id.largewidget_forecast_icon1; break;
            case 2: result = R.id.largewidget_forecast_icon2; break;
            case 3: result = R.id.largewidget_forecast_icon3; break;
            case 4: result = R.id.largewidget_forecast_icon4; break;
            case 5: result = R.id.largewidget_forecast_icon5; break;
            case 6: result = R.id.largewidget_forecast_icon6; break;
            case 7: result = R.id.largewidget_forecast_icon7; break;
            case 8: result = R.id.largewidget_forecast_icon8; break;
            case 9: result = R.id.largewidget_forecast_icon9; break;
            case 10: result = R.id.largewidget_forecast_icon10; break;
        }
        return result;
    }

    private int getMaxResource(int pos){
        int result = 0;
        switch (pos){
            case 1: result = R.id.largewidget_forecast_high1; break;
            case 2: result = R.id.largewidget_forecast_high2; break;
            case 3: result = R.id.largewidget_forecast_high3; break;
            case 4: result = R.id.largewidget_forecast_high4; break;
            case 5: result = R.id.largewidget_forecast_high5; break;
            case 6: result = R.id.largewidget_forecast_high6; break;
            case 7: result = R.id.largewidget_forecast_high7; break;
            case 8: result = R.id.largewidget_forecast_high8; break;
            case 9: result = R.id.largewidget_forecast_high9; break;
            case 10: result = R.id.largewidget_forecast_high10; break;
        }
        return result;
    }

    private int getMinResource(int pos){
        int result = 0;
        switch (pos){
            case 1: result = R.id.largewidget_forecast_low1; break;
            case 2: result = R.id.largewidget_forecast_low2; break;
            case 3: result = R.id.largewidget_forecast_low3; break;
            case 4: result = R.id.largewidget_forecast_low4; break;
            case 5: result = R.id.largewidget_forecast_low5; break;
            case 6: result = R.id.largewidget_forecast_low6; break;
            case 7: result = R.id.largewidget_forecast_low7; break;
            case 8: result = R.id.largewidget_forecast_low8; break;
            case 9: result = R.id.largewidget_forecast_low9; break;
            case 10: result = R.id.largewidget_forecast_low10; break;
        }
        return result;
    }

}
