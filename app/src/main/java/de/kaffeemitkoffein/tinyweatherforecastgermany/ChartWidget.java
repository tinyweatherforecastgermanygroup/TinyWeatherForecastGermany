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
            for (int i = 0; i < widget_instances.length; i++) {
                // sets up a pending intent to launch main activity when the widget is touched.
                Intent intent = new Intent(c, MainActivity.class);
                PendingIntent pendingIntent;
                if (Build.VERSION.SDK_INT>=23){
                    pendingIntent = PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent = PendingIntent.getActivity(c, 0, intent, 0);
                }
                RemoteViews remoteViews = new RemoteViews(c.getPackageName(), R.layout.chartwidget_layout);
                fillChartWidgetItems(c, awm, widget_instances[i], remoteViews, weatherCard);
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

    private void fillChartWidgetItems(Context context, AppWidgetManager awm, int widgetInstance, RemoteViews remoteViews, CurrentWeatherInfo currentWeatherInfo) {
        if (currentWeatherInfo == null) {
            currentWeatherInfo = new CurrentWeatherInfo();
            currentWeatherInfo.setToEmpty();
        }
        if (WeatherSettings.showDWDNote(context)) {
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
        WidgetDimensionManager widgetDimensionManager = new WidgetDimensionManager(context,awm,widgetInstance,"Chart Widget #"+widgetInstance);
        int width = widgetDimensionManager.getWidgetWidthInt();
        int height = widgetDimensionManager.getWidgetHeightInt();
        // confirm if determined widget size is plausible, fall back to defaults if necessary
        int[] widgetSize = DeviceTweaks.confirmPlausibleWidgetSize(context,DeviceTweaks.Widget.CLASSIC,width,height);
        width = widgetSize[0]; height = widgetSize[1];
        // measure DWD note and adapt the target bitmap size
        if (WeatherSettings.showDWDNote(context)){
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
        int opacity = WeatherSettings.getWidgetOpacity(context);
        remoteViews.setImageViewResource(android.R.id.background,ThemePicker.getWidgetBackgroundDrawableRessource(context));
        remoteViews.setInt(android.R.id.background,"setImageAlpha",Math.round(opacity*2.55f));
    }

}
