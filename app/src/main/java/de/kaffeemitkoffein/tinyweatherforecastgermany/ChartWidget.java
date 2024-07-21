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
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Queue;

public class ChartWidget extends ClassicWidget{

    @Override
    public void updateWidgetDisplay(Context c, AppWidgetManager awm, int[] widget_instances, int source) {
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(c);
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
                //Log.v("widget","Widget = "+widgetWidth+" / "+widgetHeight);
                // sets up a pending intent to launch main activity when the widget is touched.
                Intent intent = new Intent(c, MainActivity.class);
                PendingIntent pendingIntent;
                if (Build.VERSION.SDK_INT>=23){
                    pendingIntent = PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent = PendingIntent.getActivity(c, 0, intent, 0);
                }
                RemoteViews remoteViews = new RemoteViews(c.getPackageName(), R.layout.chartwidget_layout);
                fillChartWidgetItems(c, awm, widget_instances[i], remoteViews, weatherSettings, weatherCard);
                remoteViews.setOnClickPendingIntent(R.id.chartwidget_maincontainer, pendingIntent);
                try {
                    awm.updateAppWidget(widget_instances[i], remoteViews);
                } catch (IllegalArgumentException exception){
                    PrivateLog.log(c,PrivateLog.WIDGET,PrivateLog.ERR,"Chartwidget update failed: "+ exception.getMessage());
                }
            }
        } else
            // sync weather if no information is present, however do not loop syncs if widget update was already
            // triggered by the sync adapter.
            if (source!=WidgetRefresher.FROM_SYNCADAPTER){
                ContentResolver.requestSync(MainActivity.getManualSyncRequest(c,WeatherSyncAdapter.UpdateFlags.FLAG_UPDATE_WEATHER));
            }
    }

    private void fillChartWidgetItems(Context context, AppWidgetManager awm, int widgetInstance, RemoteViews remoteViews, WeatherSettings weatherSettings, CurrentWeatherInfo currentWeatherInfo) {
        if (currentWeatherInfo == null) {
            currentWeatherInfo = new CurrentWeatherInfo();
            currentWeatherInfo.setToEmpty();
        }
        if (weatherSettings.widget_showdwdnote) {
            remoteViews.setViewVisibility(R.id.widget_reference_text, View.VISIBLE);
            remoteViews.setTextColor(R.id.widget_reference_text,ThemePicker.getWidgetTextColor(context));
        } else {
            remoteViews.setViewVisibility(R.id.widget_reference_text, View.GONE);
        }
        ArrayList<WeatherWarning> locationWarnings = new ArrayList<WeatherWarning>();
        if (WeatherSettings.displayWarningsInWidget(context)) {
            Weather.WeatherLocation weatherLocation = WeatherSettings.getSetStationLocation(context);
            ArrayList<WeatherWarning> warnings = WeatherWarnings.getCurrentWarnings(context, true);
            locationWarnings = WeatherWarnings.getWarningsForLocation(context, warnings, weatherLocation);
        }
        WidgetDimensionManager widgetDimensionManager = new WidgetDimensionManager(context,awm,widgetInstance);
        int width = widgetDimensionManager.getWidgetWidthInt();
        int height = widgetDimensionManager.getWidgetHeightInt();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        if ((width<=0) || (height<=0)){
            width = 720; height = 160;
            PrivateLog.log(context,PrivateLog.WIDGET,PrivateLog.INFO,"Chartwidget size unknown, applying default dimensions: "+width+" x "+height);
        } else {
            PrivateLog.log(context,PrivateLog.WIDGET,PrivateLog.INFO,"Chartwidget size is "+width+" x "+height);
            // check for implausible widget dimensions
            if ((width>displayMetrics.widthPixels) || (height>displayMetrics.heightPixels)){
                width = 720; height = 160;
                PrivateLog.log(context,PrivateLog.WIDGET,PrivateLog.ERR,"Chartwidget size returned is bigger than the screen resolution. Falling back to widget default dimensions.");
            }
        }
        // measure DWD note and adapt the target bitmap size
        if (weatherSettings.widget_showdwdnote){
            float fontScale = context.getResources().getConfiguration().fontScale;
            float textSizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,7,context.getResources().getDisplayMetrics());
            Paint noteTextPaint = new Paint();
            noteTextPaint.setTextSize(textSizeInPixels);
            String noteString = context.getResources().getString(R.string.dwd_notice);
            float noteWidth = noteTextPaint.measureText(noteString);
            int lines = Math.round(noteWidth/width);
            if (noteWidth%width>0){
                lines++;
            }
            height = Math.round(height - lines*textSizeInPixels);
        }
        PrivateLog.log(context,PrivateLog.WIDGET,PrivateLog.INFO,"Chartwidget bitmap size is "+width+" x "+height);
        Bitmap bitmap = ForecastBitmap.getOverviewChart(context,width,height,currentWeatherInfo.forecast1hourly,locationWarnings);
        remoteViews.setImageViewBitmap(R.id.chartwidget_chart,bitmap);
        // set opacity
        int opacity = Integer.parseInt(weatherSettings.widget_opacity);
        remoteViews.setImageViewResource(android.R.id.background,ThemePicker.getWidgetBackgroundDrawableRessource(context));
        remoteViews.setInt(android.R.id.background,"setImageAlpha",Math.round(opacity*2.55f));
    }

}
