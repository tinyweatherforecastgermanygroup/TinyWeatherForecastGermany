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
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BoldWidget extends ClassicWidget {

    @Override
    public void onAppWidgetOptionsChanged(Context c, AppWidgetManager awm, int appWidgetID, Bundle appWidgetOptions){
        // diameters in portrait mode
        int widthPortrait = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int heightPortrait = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        // diameters in landscape mode
        int widthLandscape = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int heightLandscape = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int orientation = c.getResources().getConfiguration().orientation;
        int widgetWidth = widthPortrait; int widgetHeight = heightPortrait;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            widgetWidth = widthLandscape;
            widgetHeight = heightLandscape;
        }
        updateWidgetDisplay(c,awm,new int[] {appWidgetID});
        super.onAppWidgetOptionsChanged(c,awm,appWidgetID,appWidgetOptions);
    }


    @Override
    public void updateWidgetDisplay(Context c, AppWidgetManager awm, int[] widget_instances) {
        CurrentWeatherInfo weatherCard = Weather.getCurrentWeatherInfo(c);
        if (weatherCard!=null){
            WeatherSettings weatherSettings = new WeatherSettings(c);
            for (int i = 0; i < widget_instances.length; i++) {
                // determine widget diameters in pixels
                Bundle appWidgetOptions = awm.getAppWidgetOptions(widget_instances[i]);
                // diameters in portrait mode
                int widthPortrait = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
                int heightPortrait = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
                // diameters in landscape mode
                int widthLandscape = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
                int heightLandscape = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
                int orientation = c.getResources().getConfiguration().orientation;
                int widgetWidth = widthPortrait; int widgetHeight = heightPortrait;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE){
                    widgetWidth = widthLandscape;
                    widgetHeight = heightLandscape;
                }
                // sets up a pending intent to launch main activity when the widget is touched.
                Intent intent = new Intent(c, MainActivity.class);
                PendingIntent pendingIntent;
                // mutable/immutable flags are available since sdk 23
                if (Build.VERSION.SDK_INT>=23){
                    pendingIntent = PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent = PendingIntent.getActivity(c, 0, intent, 0);
                }
                int forecastDays = 2; // default
                int widgetResource = R.layout.boldwidget_layout;
                if (widgetWidth>=280){
                    forecastDays = 3;
                    widgetResource = R.layout.boldwidget_layout3;
                }
                if (widgetWidth>=335){
                    forecastDays = 4;
                    widgetResource = R.layout.boldwidget_layout4;
                }
                if (widgetWidth>=430){
                    forecastDays = 5;
                    widgetResource = R.layout.boldwidget_layout5;
                }
                // Log.v("widget","Widget = "+widgetWidth+" / "+widgetHeight);
                PrivateLog.log(c,PrivateLog.WIDGET, PrivateLog.INFO," Bold widget id "+widget_instances[i]+" size: "+widgetWidth+"/"+widgetHeight+" dp, showing "+forecastDays+" forecast days.");
                RemoteViews remoteViews = new RemoteViews(c.getPackageName(), widgetResource);
                fillBoldWidgetItems(c, remoteViews, weatherSettings, weatherCard,forecastDays);
                setClassicWidgetItems(remoteViews,weatherSettings,weatherCard,c);
                remoteViews.setOnClickPendingIntent(R.id.boldwidget_maincontainer, pendingIntent);
                awm.updateAppWidget(widget_instances[i], remoteViews);
            }
        }
    }

    private void fillBoldWidgetItems(Context c, RemoteViews remoteViews, WeatherSettings weatherSettings, CurrentWeatherInfo currentWeatherInfo, int forecastDays) {
        ForecastIcons forecastIcons = new ForecastIcons(c,null);
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
        // handle optional vertical line
        if (WeatherSettings.displayBoldwidgetVerticalBar(c)){
            remoteViews.setViewVisibility(R.id.boldwidget_fc1_verticalline,View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.boldwidget_fc1_verticalline,View.INVISIBLE);
        }
        if (currentWeatherInfo.currentWeather.hasTemperature()) {
            remoteViews.setTextViewText(R.id.boldwidget_current_temperature, currentWeatherInfo.currentWeather.getTemperatureInCelsiusInt() + "°");
            remoteViews.setTextColor(R.id.boldwidget_current_temperature,ThemePicker.getTemperatureAccentColor(c,currentWeatherInfo.currentWeather));
        } else {
            remoteViews.setTextViewText(R.id.boldwidget_current_temperature, NOT_AVAILABLE);
            remoteViews.setTextColor(R.id.boldwidget_current_temperature,ThemePicker.getWidgetTextColor(c));

        }
        if (currentWeatherInfo.currentWeather.hasCondition()) {
            remoteViews.setImageViewBitmap(R.id.boldwidget_today_condition,forecastIcons.getIconBitmap(currentWeatherInfo.currentWeather,currentWeatherInfo.weatherLocation));
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
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE", Locale.getDefault());
            // the timestamp will always be midnight. When we derive the day of week from it, it will be misleading, since
            // we want to show the day *before* this midnight position.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentWeatherInfo.forecast24hourly.get(1).getTimestamp());
            calendar.add(Calendar.DAY_OF_WEEK,-1);
            String weekday = simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
            remoteViews.setTextViewText(R.id.boldwidget_dayofweek_fc1, weekday);
            remoteViews.setTextColor(R.id.boldwidget_dayofweek_fc1,ThemePicker.getWidgetTextColor(c));
            if (currentWeatherInfo.forecast24hourly.get(1).hasCondition()) {
                remoteViews.setImageViewBitmap(R.id.boldwidget_fc1_weatherconditionicon,forecastIcons.getIconBitmap(currentWeatherInfo.forecast24hourly.get(1),currentWeatherInfo.weatherLocation));
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
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE",Locale.getDefault());
            // the timestamp will always be midnight. When we derive the day of week from it, it will be misleading, since
            // we want to show the day *before* this midnight position.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentWeatherInfo.forecast24hourly.get(2).getTimestamp());
            calendar.add(Calendar.DAY_OF_WEEK,-1);
            String weekday = simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
            remoteViews.setTextViewText(R.id.boldwidget_dayofweek_fc2, weekday);
            remoteViews.setTextColor(R.id.boldwidget_dayofweek_fc2,ThemePicker.getWidgetTextColor(c));
            if (currentWeatherInfo.forecast24hourly.get(2).hasCondition()) {
                remoteViews.setImageViewBitmap(R.id.boldwidget_fc2_weatherconditionicon,forecastIcons.getIconBitmap(currentWeatherInfo.forecast24hourly.get(2),currentWeatherInfo.weatherLocation));
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
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE",Locale.getDefault());
        for (int forecastDay=3; (forecastDay<=forecastDays); forecastDay++){
            // the timestamp will always be midnight. When we derive the day of week from it, it will be misleading, since
            // we want to show the day *before* this midnight position.
            int dayOfWeekID     = R.id.boldwidget_dayofweek_fc3;
            int conditionIconID = R.id.boldwidget_fc3_weatherconditionicon;
            int maxTempID       = R.id.boldwidget_fc3_max;
            int minTempID       = R.id.boldwidget_fc3_min;
            if (forecastDay==4){
                dayOfWeekID     = R.id.boldwidget_dayofweek_fc4;
                conditionIconID = R.id.boldwidget_fc4_weatherconditionicon;
                maxTempID       = R.id.boldwidget_fc4_max;
                minTempID       = R.id.boldwidget_fc4_min;
            }
            if (forecastDay==5){
                dayOfWeekID     = R.id.boldwidget_dayofweek_fc5;
                conditionIconID = R.id.boldwidget_fc5_weatherconditionicon;
                maxTempID       = R.id.boldwidget_fc5_max;
                minTempID       = R.id.boldwidget_fc5_min;
            }
            if (currentWeatherInfo.forecast24hourly.size() >= forecastDay+1){
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(currentWeatherInfo.forecast24hourly.get(forecastDay).getTimestamp());
                calendar.add(Calendar.DAY_OF_WEEK,-1);
                String weekday = simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
                remoteViews.setTextViewText(dayOfWeekID, weekday);
                remoteViews.setTextColor(dayOfWeekID,ThemePicker.getWidgetTextColor(c));
                remoteViews.setTextColor(maxTempID,ThemePicker.getWidgetTextColor(c));
                remoteViews.setTextColor(minTempID,ThemePicker.getWidgetTextColor(c));
                if (currentWeatherInfo.forecast24hourly.get(forecastDay).hasCondition()) {
                    remoteViews.setImageViewBitmap(conditionIconID,forecastIcons.getIconBitmap(currentWeatherInfo.forecast24hourly.get(forecastDay),currentWeatherInfo.weatherLocation));
                } else {
                    remoteViews.setImageViewBitmap(conditionIconID,WeatherIcons.getIconBitmap(c,WeatherIcons.NOT_AVAILABLE,true));
                }
                if (currentWeatherInfo.forecast24hourly.get(forecastDay).hasMaxTemperature()) {
                    remoteViews.setTextViewText(maxTempID, currentWeatherInfo.forecast24hourly.get(forecastDay).getMaxTemperatureInCelsiusInt() + "°");
                } else {
                    remoteViews.setTextViewText(maxTempID, NOT_AVAILABLE);
                }
                if (currentWeatherInfo.forecast24hourly.get(forecastDay).hasMinTemperature()) {
                    remoteViews.setTextViewText(minTempID, currentWeatherInfo.forecast24hourly.get(forecastDay).getMinTemperatureInCelsiusInt() + "°");
                } else {
                    remoteViews.setTextViewText(minTempID, NOT_AVAILABLE);
                }
            } else {
                remoteViews.setTextViewText(dayOfWeekID, NOT_AVAILABLE);
                remoteViews.setImageViewResource(conditionIconID, R.mipmap.not_available);
                remoteViews.setTextViewText(maxTempID, NOT_AVAILABLE);
                remoteViews.setTextViewText(minTempID, NOT_AVAILABLE);
            }
        }
        // set opacity
        int opacity = Integer.parseInt(weatherSettings.widget_opacity);
        remoteViews.setImageViewResource(android.R.id.background,ThemePicker.getWidgetBackgroundDrawableRessource(c));
        remoteViews.setInt(android.R.id.background,"setImageAlpha",Math.round(opacity*2.55f));
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
