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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RemoteViews;


public class ClockWidget extends ClassicWidget {

    @Override
    public void updateWidgetDisplay(Context c, AppWidgetManager awm, int[] widget_instances, int source) {
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(c);
        if (weatherCard!=null){
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
                RemoteViews remoteViews = new RemoteViews(c.getPackageName(), R.layout.clockwidget_layout);
                // sets up a pending intent to launch main activity when the widget is touched.
                Intent intent_weather = new Intent(c, MainActivity.class);
                // mutable/immutable flags are available since sdk 23
                PendingIntent pendingIntent_weather;
                if (Build.VERSION.SDK_INT>=23){
                    pendingIntent_weather = PendingIntent.getActivity(c, 0, intent_weather, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent_weather = PendingIntent.getActivity(c, 0, intent_weather, 0);
                }
                remoteViews.setOnClickPendingIntent(R.id.clockwidget_weather_container, pendingIntent_weather);
                //sets 2nd pending intent to go to clock alarms when clock is touched.
                Intent intent_clock = new Intent(Intent.ACTION_MAIN);
                intent_clock.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName componentName = new ComponentName("com.android.deskclock", "com.android.deskclock.DeskClock");
                intent_clock.setComponent(componentName);
                PendingIntent pendingIntent_clock;
                if (Build.VERSION.SDK_INT>=23){
                    pendingIntent_clock = PendingIntent.getActivity(c, 0, intent_clock, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent_clock = PendingIntent.getActivity(c, 0, intent_clock, 0);
                }
                remoteViews.setOnClickPendingIntent(R.id.clockwidget_clock, pendingIntent_clock);
                remoteViews.setOnClickPendingIntent(R.id.widget_date, pendingIntent_clock);
                remoteViews.setOnClickPendingIntent(R.id.widget_nextalarm, pendingIntent_clock);
                setClassicWidgetItems(remoteViews, weatherCard, c,false);
                adjustClockFontSize(c, awm, i, remoteViews);
                if (weatherCard!=null){
                    fillClockWeatherItems(c,remoteViews, weatherCard);
                }
                awm.updateAppWidget(widget_instances[i], remoteViews);
            }
        } else
            // sync weather if no information is present, however do not loop syncs if widget update was already
            // triggered by the sync adapter.
            if (source!=WidgetRefresher.FROM_SYNCADAPTER){
                ContentResolver.requestSync(MainActivity.getManualSyncRequest(c,WeatherSyncAdapter.UpdateFlags.FLAG_UPDATE_WEATHER));
            }
    }

    private void adjustClockFontSize(Context context, final AppWidgetManager awm, final int widget_instance, RemoteViews remoteViews) {
        WidgetDimensionManager widgetDimensionManager = new WidgetDimensionManager(context, awm, widget_instance);
        float width_max = widgetDimensionManager.getWidgetWidth();
        // clock layout takes 50% of widget height
        float height_max = widgetDimensionManager.getWidgetHeight() / (float) 0.36;
        if (height_max == 0) {
            height_max = (float) 64;
        }
        remoteViews.setTextViewTextSize(R.id.clockwidget_clock, TypedValue.COMPLEX_UNIT_SP, height_max);
    }

    private void adaptIconColorsToTheme(Context context, final RemoteViews remoteViews){
        remoteViews.setImageViewBitmap(R.id.widget_visibility_icon,WeatherIcons.getIconBitmap(context,WeatherIcons.BIOCULAR,true));
        remoteViews.setImageViewBitmap(R.id.widget_temperature5cm_icon,WeatherIcons.getIconBitmap(context,WeatherIcons.SYMBOL_TEMPERATURE5CM,true));
        remoteViews.setImageViewBitmap(R.id.widget_clouds_icon,WeatherIcons.getIconBitmap(context,WeatherIcons.SYMBOL_CLOUD,true));
        remoteViews.setImageViewBitmap(R.id.widget_rh_icon,WeatherIcons.getIconBitmap(context,WeatherIcons.SYMBOL_RH,true));
    }

    private void fillClockWeatherItems(Context context, final RemoteViews remoteViews, CurrentWeatherInfo weatherInfo) {
        setPrecipitation(context, remoteViews, weatherInfo);
        setPressure(context, remoteViews, weatherInfo);
        setVisibility(context, remoteViews,weatherInfo,WeatherSettings.getDistanceDisplayUnit(context));
        setClouds(context, remoteViews,weatherInfo);
        setTemperature5cm(context, remoteViews,weatherInfo);
        setHumidity(context,remoteViews,weatherInfo);
        setDateText(context, remoteViews);
        setAlarmText(context,remoteViews);
        setWarningTextAndIcon(context,remoteViews,R.id.widget_warningcontainer,R.id.widget_warningsymbol,R.id.widget_warningtext,R.id.widget_warning_more);
        remoteViews.setTextColor(R.id.clockwidget_clock,ThemePicker.getWidgetTextColor(context));
        remoteViews.setTextColor(R.id.clockwidget_precipitation_unit1,ThemePicker.getPrecipitationAccentColor(context));
        remoteViews.setTextColor(R.id.clockwidget_precipitation_unit2,ThemePicker.getPrecipitationAccentColor(context));
        remoteViews.setImageViewResource(R.id.widget_visibility_icon,WeatherIcons.getIconResource(context,WeatherIcons.BIOCULAR));
        remoteViews.setImageViewResource(R.id.widget_temperature5cm_icon,WeatherIcons.getIconResource(context,WeatherIcons.SYMBOL_TEMPERATURE5CM));
        remoteViews.setImageViewResource(R.id.widget_clouds_icon,WeatherIcons.getIconResource(context,WeatherIcons.SYMBOL_CLOUD));
        adaptIconColorsToTheme(context,remoteViews);
    }

}








