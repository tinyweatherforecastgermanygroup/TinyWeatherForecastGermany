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
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RemoteViews;

public class ClassicWidget extends AppWidgetProvider {

    public static final String WIDGET_CUSTOM_REFRESH_ACTION     = "de.kaffeemitkoffein.feinstaubwidget.WIDGET_CUSTOM_ACTION_REFRESH";
    public static final String NOT_AVAILABLE = "-";

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
        PrivateLog.log(c,Tag.WIDGET,"Updating widget (system): "+getClass().toString());
        UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(c,UpdateAlarmManager.WIDGET_UPDATE);
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
                PrivateLog.log(c,Tag.WIDGET,"Updating widget (app, custom): "+getClass().toString());
                widgetRefreshAction(c,i);
            }
      }
    }

    /**
     * Updates the display of the wigdgets.
     *
     */

    public void setClassicWidgetItems(RemoteViews remoteViews, WeatherSettings weatherSettings, CurrentWeatherInfo weatherCard, Context c, boolean shorten_text){
        if (weatherCard==null){
            weatherCard = new CurrentWeatherInfo();
            weatherCard.setToEmpty();
        }
        String location_text = weatherCard.getCity();
        if ((location_text.length()>10) && (shorten_text)){
            location_text = location_text.substring(0,10)+".";
        }
        remoteViews.setTextViewText(R.id.classicwidget_locationtext,location_text);
        if (weatherCard.currentWeather.hasCondition()){
            int weathercondition = weatherCard.currentWeather.getCondition();
            remoteViews.setTextViewText(R.id.classicwidget_weatherconditiontext,WeatherCodeContract.getWeatherConditionText(c,weathercondition));
            remoteViews.setImageViewResource(R.id.classicwidget_weatherconditionicon,WeatherCodeContract.getWeatherConditionDrawableResource(weathercondition,weatherCard.currentWeather.isDaytime(weatherCard.weatherLocation)));
        } else {
            remoteViews.setTextViewText(R.id.classicwidget_weatherconditiontext,NOT_AVAILABLE);
            remoteViews.setImageViewResource(R.id.classicwidget_weatherconditionicon,R.mipmap.not_available);
        }
        if (weatherCard.currentWeather.hasTemperature()){
            remoteViews.setTextViewText(R.id.classicwidget_temperature,String.valueOf(weatherCard.currentWeather.getTemperatureInCelsiusInt()+"°"));
        } else {
            remoteViews.setTextViewText(R.id.classicwidget_temperature,NOT_AVAILABLE);
        }
        String lowhigh = NOT_AVAILABLE;
        if (weatherCard.currentWeather.hasMinTemperature()){
            lowhigh = String.valueOf(weatherCard.currentWeather.getMinTemperatureInCelsiusInt()+"°");
        }
        lowhigh = lowhigh + " | ";
        if (weatherCard.currentWeather.hasMaxTemperature()){
            lowhigh = lowhigh + String.valueOf(weatherCard.currentWeather.getMaxTemperatureInCelsiusInt()+"°");
        } else {
            lowhigh = lowhigh + NOT_AVAILABLE;
        }
        remoteViews.setTextViewText(R.id.classicwidget_temperature_highlow,lowhigh);
        String windstring="";
        if (weatherCard.currentWeather.hasWindDirection()){
            if (weatherSettings.getWindDisplayType()==Weather.WindDisplayType.ARROW){
                remoteViews.setViewVisibility(R.id.classicwidget_windarrow,View.VISIBLE);
                remoteViews.setImageViewBitmap(R.id.classicwidget_windarrow,weatherCard.currentWeather.getArrowBitmap(c));
            }
            if (weatherSettings.getWindDisplayType()==Weather.WindDisplayType.BEAUFORT){
                remoteViews.setViewVisibility(R.id.classicwidget_windarrow,View.VISIBLE);
                remoteViews.setImageViewBitmap(R.id.classicwidget_windarrow,weatherCard.currentWeather.getBeaufortBitmap(c));
            }
            if (weatherSettings.getWindDisplayType()==Weather.WindDisplayType.TEXT){
                remoteViews.setViewVisibility(R.id.classicwidget_windarrow,View.GONE);
                windstring=weatherCard.currentWeather.getWindDirectionString(c)+" ";
            }
        } else {
            remoteViews.setViewVisibility(R.id.classicwidget_windarrow,View.GONE);
        }
        if (weatherCard.currentWeather.hasWindSpeed()){
            String windspeed = "";
            if (weatherSettings.getWindDisplayUnit()==Weather.WindDisplayUnit.METERS_PER_SECOND){
                windspeed = String.valueOf(weatherCard.currentWeather.getWindSpeedInMsInt())+" ";
            }
            if (weatherSettings.getWindDisplayUnit()==Weather.WindDisplayUnit.KILOMETERS_PER_HOUR){
                windspeed = String.valueOf(weatherCard.currentWeather.getWindSpeedInKmhInt())+" ";
            }
            if (weatherSettings.getWindDisplayUnit()==Weather.WindDisplayUnit.BEAUFORT){
                windspeed = String.valueOf(weatherCard.currentWeather.getWindSpeedInBeaufortInt())+" ";
            }
            if (weatherSettings.getWindDisplayUnit()==Weather.WindDisplayUnit.KNOTS){
                windspeed = String.valueOf(weatherCard.currentWeather.getWindSpeedInKnotsInt())+" ";
            }
            windstring = windstring + windspeed;
            if (weatherCard.currentWeather.hasFlurries()){
                String flurries = "";
                switch (weatherSettings.getWindDisplayUnit()){
                    case Weather.WindDisplayUnit.METERS_PER_SECOND: flurries=String.valueOf(weatherCard.currentWeather.getFlurriesInMsInt()); break;
                    case Weather.WindDisplayUnit.BEAUFORT: flurries=String.valueOf(weatherCard.currentWeather.getFlurriesInBeaufortInt()); break;
                    case Weather.WindDisplayUnit.KILOMETERS_PER_HOUR: flurries=String.valueOf(weatherCard.currentWeather.getFlurriesInKmhInt()); break;
                    case Weather.WindDisplayUnit.KNOTS: flurries=String.valueOf(weatherCard.currentWeather.getFlurriesInKnotsInt());
                }
                windstring = windstring + " ("+flurries+") ";
            }
            remoteViews.setViewVisibility(R.id.classicwidget_wind,View.VISIBLE);
            remoteViews.setTextViewText(R.id.classicwidget_wind,windstring);
            remoteViews.setViewVisibility(R.id.classicwidget_wind_unit,View.VISIBLE);
            remoteViews.setTextViewText(R.id.classicwidget_wind_unit,Weather.getWindUnitString(weatherSettings.getWindDisplayUnit()));
        } else {
            remoteViews.setViewVisibility(R.id.classicwidget_wind,View.INVISIBLE);
        }
        int opacity = Integer.parseInt(weatherSettings.widget_opacity);
        remoteViews.setInt(R.id.classicwidget_maincontainer,"setBackgroundColor",getBackgroundInt(opacity));
        if (weatherSettings.widget_showdwdnote) {
            remoteViews.setViewVisibility(R.id.classicwidget_reference_text, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.classicwidget_reference_text, View.GONE);
        }
    }

    public void setClassicWidgetItems(RemoteViews remoteViews, WeatherSettings weatherSettings, CurrentWeatherInfo weatherCard, Context c){
        setClassicWidgetItems(remoteViews,weatherSettings,weatherCard,c,false);
    }

        public void updateWidgetDisplay(Context c, AppWidgetManager awm, int[] widget_instances){
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(c);
        WeatherSettings weatherSettings = new WeatherSettings(c);
        for (int i=0; i<widget_instances.length; i++){
            // sets up a pending intent to launch main activity when the widget is touched.
            Intent intent = new Intent(c,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(c,0,intent,0);
            RemoteViews remoteViews = new RemoteViews(c.getPackageName(),R.layout.classicwidget_layout);
            remoteViews.setOnClickPendingIntent(R.id.classicwidget_maincontainer,pendingIntent);
            setClassicWidgetItems(remoteViews,weatherSettings,weatherCard,c);
            awm.updateAppWidget(widget_instances[i],remoteViews);
        }
    }

    public void widgetRefreshAction(Context c, Intent i){
        AppWidgetManager awm = AppWidgetManager.getInstance(c);
        int[] wi = awm.getAppWidgetIds(new ComponentName(c,this.getClass().getName()));
        if (wi.length>0){
            updateWidgetDisplay(c,awm,wi);
        }
    }

    public int getBackgroundInt(int alpha){
        String hex_string = Integer.toHexString(Math.round((float)alpha * (float)2.55));
        if (hex_string.length()<2)
        {
            hex_string = "0" + hex_string;
        }
        hex_string = hex_string+"101010";
        return Color.parseColor("#"+hex_string);
    }

    /**
     * This class provides some methods to determine the approximate current size of a widget in pixels.
     */

    public class WidgetDimensionManager {

        /*
         * You can get a Bundle from the AppWidgetManager (awm) that holds some metrics, which are
         * poorly documented. Going by the docs, they provide a range of min-max in dp that the widget
         * can have.
         *
         * Bundle bundle = awm.getAppWidgetOptions(widget_instances[i]);
         *
         * When yor home screen is in portrait mode, which should be the most common case, the
         * following values apply quite accurate when compared to screenshots and measuring the widget
         * sizes:
         *
         * OPTION_APPWIDGET_MIN_WIDTH  seems to roughly be the real widget width in dp (portrait mode).
         * OPTION_APPWIDGET_MAX_HEIGHT seems to roughly be the real widget height in dp (portrait mode).
         *
         * However, should your device allow switching the home screen to landscape mode:
         *
         * OPTION_APPWIDGET_MIN_HEIGHT seems to roughly be the real widget height in dp (landscape mode).
         * OPTION_APPWIDGET_MAX_WIDTH  seems to roughly be the real widget width in dp (landscape mode), but
         * on my device I am getting a width that is a little bit too small.
         *
         * All values are not correct to the last pixel, as all the metrics seem to be further modified
         * by the launcher before they get displayed.
         *
         * Therefore, depending too munch on the metrics from here puts the widget at risk to not work well
         * with custom launchers that may come with some unpredictable behaviour.
         *
         * However, they can be a pretty nice estimate how large text & graphics should be generated before
         * getting displayed.
         */

        int widget_width_portrait_dp;
        int widget_height_portrait_dp;
        int widget_width_landscape_dp;
        int widget_height_landscape_dp;
        float xdpi;
        float ydpi;
        int orientation;
        int density;
        float scaledDensity;
        Context context;

        /**
         * Public constructor to be called from the widget.
         * It fills all the local variables with values.
         *
         * @param c
         * @param awm
         * @param widget_instance
         */

        public WidgetDimensionManager(Context c, AppWidgetManager awm, int widget_instance){
            this.context = c;
            Bundle bundle = awm.getAppWidgetOptions(widget_instance);
            widget_width_portrait_dp = bundle.getInt(awm.OPTION_APPWIDGET_MIN_WIDTH);
            widget_width_landscape_dp = bundle.getInt(awm.OPTION_APPWIDGET_MAX_WIDTH);
            widget_height_landscape_dp= bundle.getInt(awm.OPTION_APPWIDGET_MIN_HEIGHT);
            widget_height_portrait_dp = bundle.getInt(awm.OPTION_APPWIDGET_MAX_HEIGHT);
            DisplayMetrics metrics = c.getResources().getDisplayMetrics();
            this.xdpi = metrics.xdpi;
            this.ydpi = metrics.ydpi;
            this.scaledDensity = metrics.scaledDensity;
            this.orientation = c.getResources().getConfiguration().orientation;
            this.density = c.getResources().getConfiguration().densityDpi;
        }

        /**
         * Gets the approximate current widget width in pixels.
         * @return
         */

        public float getWidgetWidth(){
            if (orientation == Configuration.ORIENTATION_LANDSCAPE){
                return (float) widget_width_landscape_dp * (xdpi/160);
            } else {
                return (float) widget_width_portrait_dp * (xdpi/160);
            }
        }

        /**
         * Gets the approximate current widget height in pixels.
         * @return
         */

        public float getWidgetHeight(){
            if (orientation == Configuration.ORIENTATION_LANDSCAPE){
                return (float) widget_height_landscape_dp * (ydpi/160);
            } else {
                return (float) widget_height_portrait_dp * (ydpi/160);
            }
        }

        /**
         * Gets the approximate current widget width in pixels.
         * @return
         */

        public int getWidgetWidthInt(){
            return Math.round(getWidgetWidth());
        }

        /**
         * Gets the approximate current widget height in pixels.
         * @return
         */

        public int getWidgetHeightInt(){
            return Math.round(getWidgetHeight());
        }

        public float getScaledDensity(){
            return this.scaledDensity;
        }

        /**
         * Gets the font height in pixels.
         * @param fontsize is the fontsize in sp
         * @return
         */

        public float getFontHeightInPixels(float fontsize){
            return (float) fontsize * scaledDensity;
        }
    }



}
