/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021, 2022, 2023 Pawel Dube
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
import java.util.Calendar;
import java.util.Date;

public class BoldWidget extends ClassicWidget {

    @Override
    public void updateWidgetDisplay(Context c, AppWidgetManager awm, int[] widget_instances) {
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(c);
        if (weatherCard!=null){
            WeatherSettings weatherSettings = new WeatherSettings(c);
            for (int i = 0; i < widget_instances.length; i++) {
                // sets up a pending intent to launch main activity when the widget is touched.
                Intent intent = new Intent(c, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent, 0);
                RemoteViews remoteViews = new RemoteViews(c.getPackageName(), R.layout.boldwidget_layout);
                fillBoldWidgetItems(c, remoteViews, weatherSettings, weatherCard);
                setClassicWidgetItems(remoteViews,weatherSettings,weatherCard,c);
                remoteViews.setOnClickPendingIntent(R.id.boldwidget_maincontainer, pendingIntent);
                awm.updateAppWidget(widget_instances[i], remoteViews);
            }
        }
    }

    private void fillBoldWidgetItems(Context c, RemoteViews remoteViews, WeatherSettings weatherSettings, CurrentWeatherInfo currentWeatherInfo) {
        if (currentWeatherInfo == null) {
            currentWeatherInfo = new CurrentWeatherInfo();
            currentWeatherInfo.setToEmpty();
        }
        setWarningTextAndIcon(c,remoteViews,R.id.widget_warningcontainer,R.id.widget_warningsymbol,R.id.widget_warningtext,R.id.widget_warning_more);
        remoteViews.setTextColor(R.id.boldwidget_dayofweek_today,ThemePicker.getWidgetTextColor(c));
        if (weatherSettings.widget_showdwdnote) {
            remoteViews.setViewVisibility(R.id.widget_reference_text, View.VISIBLE);
            remoteViews.setTextColor(R.id.widget_reference_text,ThemePicker.getWidgetTextColor(c));
        } else {
            remoteViews.setViewVisibility(R.id.widget_reference_text, View.GONE);
        }
        if (currentWeatherInfo.currentWeather.hasTemperature()) {
            remoteViews.setTextViewText(R.id.boldwidget_current_temperature, currentWeatherInfo.currentWeather.getTemperatureInCelsiusInt() + "°");
            remoteViews.setTextColor(R.id.boldwidget_current_temperature,ThemePicker.getTemperatureAccentColor(c,currentWeatherInfo.currentWeather));
        } else {
            remoteViews.setTextViewText(R.id.boldwidget_current_temperature, NOT_AVAILABLE);
            remoteViews.setTextColor(R.id.boldwidget_current_temperature,ThemePicker.getWidgetTextColor(c));

        }
        if (currentWeatherInfo.currentWeather.hasCondition()) {
            remoteViews.setImageViewResource(R.id.boldwidget_today_condition, WeatherCodeContract.getWeatherConditionDrawableResource(c,currentWeatherInfo.currentWeather.getCondition(), true));
        } else {
            remoteViews.setImageViewResource(R.id.boldwidget_today_condition, R.mipmap.not_available);
            remoteViews.setImageViewBitmap(R.id.boldwidget_today_condition,WeatherIcons.getIconBitmap(c,WeatherIcons.NOT_AVAILABLE,true));
        }
        if (currentWeatherInfo.currentWeather.hasMaxTemperature()) {
            remoteViews.setTextViewText(R.id.boldwidget_today_max, currentWeatherInfo.currentWeather.getMaxTemperatureInCelsiusInt() + "°");
            remoteViews.setTextColor(R.id.boldwidget_today_max,ThemePicker.getWidgetTextColor(c));

        } else {
            remoteViews.setTextViewText(R.id.boldwidget_today_max, NOT_AVAILABLE);
            remoteViews.setTextColor(R.id.boldwidget_today_max,ThemePicker.getWidgetTextColor(c));

        }
        if (currentWeatherInfo.currentWeather.hasMinTemperature()) {
            remoteViews.setTextViewText(R.id.boldwidget_today_min, currentWeatherInfo.currentWeather.getMinTemperatureInCelsiusInt() + "°");
            remoteViews.setTextColor(R.id.boldwidget_today_min,ThemePicker.getWidgetTextColor(c));
        } else {
            remoteViews.setTextViewText(R.id.boldwidget_today_min, NOT_AVAILABLE);
            remoteViews.setTextColor(R.id.boldwidget_today_min,ThemePicker.getWidgetTextColor(c));

        }
        // FORECAST 1st DAY
        if (currentWeatherInfo.forecast24hourly.size() >= 2) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
            // the timestamp will always be midnight. When we derive the day of week from it, it will be misleading, since
            // we want to show the day *before* this midnight position.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentWeatherInfo.forecast24hourly.get(1).getTimestamp());
            calendar.add(Calendar.DAY_OF_WEEK,-1);
            String weekday = simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
            remoteViews.setTextViewText(R.id.boldwidget_dayofweek_fc1, weekday);
            remoteViews.setTextColor(R.id.boldwidget_dayofweek_fc1,ThemePicker.getWidgetTextColor(c));
            if (currentWeatherInfo.forecast24hourly.get(1).hasCondition()) {
                remoteViews.setImageViewResource(R.id.boldwidget_fc1_weatherconditionicon, WeatherCodeContract.getWeatherConditionDrawableResource(c,currentWeatherInfo.forecast24hourly.get(1).getCondition(), true));
            } else {
                remoteViews.setImageViewBitmap(R.id.boldwidget_fc1_weatherconditionicon,WeatherIcons.getIconBitmap(c,WeatherIcons.NOT_AVAILABLE,true));

            }
            if (currentWeatherInfo.forecast24hourly.get(1).hasMaxTemperature()) {
                remoteViews.setTextViewText(R.id.boldwidget_fc1_max, currentWeatherInfo.forecast24hourly.get(1).getMaxTemperatureInCelsiusInt() + "°");
                remoteViews.setTextColor(R.id.boldwidget_fc1_max,ThemePicker.getWidgetTextColor(c));
            } else {
                remoteViews.setTextViewText(R.id.boldwidget_fc1_max, NOT_AVAILABLE);
                remoteViews.setTextColor(R.id.boldwidget_fc1_max,ThemePicker.getWidgetTextColor(c));
            }
            if (currentWeatherInfo.forecast24hourly.get(1).hasMinTemperature()) {
                remoteViews.setTextViewText(R.id.boldwidget_fc1_min, currentWeatherInfo.forecast24hourly.get(1).getMinTemperatureInCelsiusInt() + "°");
                remoteViews.setTextColor(R.id.boldwidget_fc1_min, ThemePicker.getWidgetTextColor(c));
            } else {
                remoteViews.setTextViewText(R.id.boldwidget_fc1_min, NOT_AVAILABLE);
                remoteViews.setTextColor(R.id.boldwidget_fc1_min,ThemePicker.getWidgetTextColor(c));
            }
        } else {
            remoteViews.setTextViewText(R.id.boldwidget_dayofweek_fc1, NOT_AVAILABLE);
            remoteViews.setImageViewResource(R.id.boldwidget_fc1_weatherconditionicon, R.mipmap.not_available);
            remoteViews.setTextViewText(R.id.boldwidget_fc1_max, NOT_AVAILABLE);
            remoteViews.setTextViewText(R.id.boldwidget_fc1_min, NOT_AVAILABLE);
        }
        // FORECAST 2nd DAY
        if (currentWeatherInfo.forecast24hourly.size() >= 3) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
            // the timestamp will always be midnight. When we derive the day of week from it, it will be misleading, since
            // we want to show the day *before* this midnight position.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentWeatherInfo.forecast24hourly.get(2).getTimestamp());
            calendar.add(Calendar.DAY_OF_WEEK,-1);
            String weekday = simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
            remoteViews.setTextViewText(R.id.boldwidget_dayofweek_fc2, weekday);
            remoteViews.setTextColor(R.id.boldwidget_dayofweek_fc2,ThemePicker.getWidgetTextColor(c));
            if (currentWeatherInfo.forecast24hourly.get(2).hasCondition()) {
                remoteViews.setImageViewResource(R.id.boldwidget_fc2_weatherconditionicon, WeatherCodeContract.getWeatherConditionDrawableResource(c,currentWeatherInfo.forecast24hourly.get(2).getCondition(), true));
            } else {
                remoteViews.setImageViewBitmap(R.id.boldwidget_fc2_weatherconditionicon,WeatherIcons.getIconBitmap(c,WeatherIcons.NOT_AVAILABLE,true));
            }
            if (currentWeatherInfo.forecast24hourly.get(2).hasMaxTemperature()) {
                remoteViews.setTextViewText(R.id.boldwidget_fc2_max, currentWeatherInfo.forecast24hourly.get(2).getMaxTemperatureInCelsiusInt() + "°");
                remoteViews.setTextColor(R.id.boldwidget_fc2_max,ThemePicker.getWidgetTextColor(c));
            } else {
                remoteViews.setTextViewText(R.id.boldwidget_fc2_max, NOT_AVAILABLE);
                remoteViews.setTextColor(R.id.boldwidget_fc2_max,ThemePicker.getWidgetTextColor(c));
            }
            if (currentWeatherInfo.forecast24hourly.get(2).hasMinTemperature()) {
                remoteViews.setTextViewText(R.id.boldwidget_fc2_min, currentWeatherInfo.forecast24hourly.get(2).getMinTemperatureInCelsiusInt() + "°");
                remoteViews.setTextColor(R.id.boldwidget_fc2_min,ThemePicker.getWidgetTextColor(c));
            } else {
                remoteViews.setTextViewText(R.id.boldwidget_fc2_min, NOT_AVAILABLE);
                remoteViews.setTextColor(R.id.boldwidget_fc2_min,ThemePicker.getWidgetTextColor(c));
            }
        } else {
            remoteViews.setTextViewText(R.id.boldwidget_dayofweek_fc2, NOT_AVAILABLE);
            remoteViews.setTextColor(R.id.boldwidget_dayofweek_fc2,ThemePicker.getWidgetTextColor(c));
            remoteViews.setImageViewResource(R.id.boldwidget_fc2_weatherconditionicon, R.mipmap.not_available);
            remoteViews.setTextViewText(R.id.boldwidget_fc2_max, NOT_AVAILABLE);
            remoteViews.setTextColor(R.id.boldwidget_fc2_max,ThemePicker.getWidgetTextColor(c));
            remoteViews.setTextViewText(R.id.boldwidget_fc2_min, NOT_AVAILABLE);
            remoteViews.setTextColor(R.id.boldwidget_fc2_min,ThemePicker.getWidgetTextColor(c));
        }
        // set opacity
        int opacity = Integer.parseInt(weatherSettings.widget_opacity);
        remoteViews.setImageViewResource(R.id.widget_backgroundimage,ThemePicker.getWidgetBackgroundDrawable(c));
        remoteViews.setInt(R.id.widget_backgroundimage,"setImageAlpha",Math.round(opacity*2.55f));
    }

    @Override
    public void setClassicWidgetItems(RemoteViews remoteViews, WeatherSettings weatherSettings, CurrentWeatherInfo weatherCard, Context c, boolean shorten_text) {
        if (weatherCard==null){
            weatherCard = new CurrentWeatherInfo();
            weatherCard.setToEmpty();
        }
        setLocationText(c,remoteViews,weatherCard,shorten_text);
        setConditionText(c,remoteViews,weatherCard);
    }

}
