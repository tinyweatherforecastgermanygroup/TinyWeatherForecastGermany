/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021 Pawel Dube
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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        int[] idarray = new int[1];
        idarray[0]=appWidgetID;
        updateWidgetDisplay(c,awm,idarray);
    }

    @Override
    public void onUpdate(Context c, AppWidgetManager awm, int[] widget_instances){
        // checks for update & launches update if necessary;
        // refresh widgets, if no update was made.
        // in case of an update, the widgets are refreshed by a callback of WIDGET_CUSTOM_REFRESH_ACTION
        PrivateLog.log(c,PrivateLog.WIDGET,PrivateLog.INFO,"Updating widget (system): "+getClass().toString());
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
            if (action.equals(WIDGET_CUSTOM_REFRESH_ACTION) || action.equals(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE)){
                PrivateLog.log(c,PrivateLog.WIDGET,PrivateLog.INFO,"Updating widget (app, custom): "+getClass().toString());
                widgetRefreshAction(c,i);
            }
      }
    }

    /**
     * Updates the display of the wigdgets.
     *
     */

    public void setPressure(Context context, RemoteViews remoteViews, CurrentWeatherInfo weatherInfo){
        if (!weatherInfo.currentWeather.hasPressure()){
            remoteViews.setViewVisibility(R.id.widget_pressure,View.INVISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.widget_pressure,View.VISIBLE);
            remoteViews.setTextViewText(R.id.widget_pressure,weatherInfo.currentWeather.getPressure()/100+ " hPa");
            remoteViews.setTextColor(R.id.widget_pressure,ThemePicker.getWidgetTextColor(context));
        }
    }

    public void setPrecipitation(Context context, RemoteViews remoteViews, CurrentWeatherInfo weatherInfo){
        String preciptitation = "";
        if (weatherInfo.currentWeather.hasProbPrecipitation()){
            preciptitation = weatherInfo.currentWeather.getProbPrecipitation()+"%";
        }
        if (weatherInfo.currentWeather.hasProbPrecipitation() && weatherInfo.currentWeather.hasPrecipitation()){
            preciptitation = preciptitation +", ";
        }
        if (weatherInfo.currentWeather.hasPrecipitation()){
            preciptitation = preciptitation + weatherInfo.currentWeather.getPrecipitation();
        }
        if (preciptitation.equals("")){
            remoteViews.setViewVisibility(R.id.widget_precipitation_container, View.INVISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.widget_precipitation_container, View.VISIBLE);
            remoteViews.setTextViewText(R.id.widget_precipitation_text,preciptitation);
            remoteViews.setTextColor(R.id.widget_precipitation_text,ThemePicker.getWidgetTextColor(context));
        }
    }

    public void setVisibility(Context context, RemoteViews remoteViews, CurrentWeatherInfo weatherInfo, int display_distance_unit){
        if (weatherInfo.currentWeather.hasVisibility()){
            remoteViews.setViewVisibility(R.id.widget_visibility_icon,View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.widget_visibility_icon,View.GONE);
        }
        if (weatherInfo.currentWeather.hasVisibility()){
                CharSequence visibility = ForecastAdapter.getVisibilityCharSequence(weatherInfo.currentWeather,display_distance_unit);
                remoteViews.setViewVisibility(R.id.widget_visibility_text,View.VISIBLE);
                remoteViews.setTextViewText(R.id.widget_visibility_text,visibility);
                remoteViews.setTextColor(R.id.widget_visibility_text,ThemePicker.getWidgetTextColor(context));
            } else {
                remoteViews.setViewVisibility(R.id.widget_visibility_text,View.GONE);
            }
        if (weatherInfo.currentWeather.hasProbVisibilityBelow1km()){
            remoteViews.setViewVisibility(R.id.widget_visibility_probvalue,View.VISIBLE);
            remoteViews.setViewVisibility(R.id.widget_visibility_probunit1,View.VISIBLE);
            remoteViews.setTextColor(R.id.widget_visibility_probunit1,ThemePicker.getWidgetTextColor(context));
            remoteViews.setViewVisibility(R.id.widget_visibility_probunit2,View.VISIBLE);
            remoteViews.setTextColor(R.id.widget_visibility_probunit2,ThemePicker.getWidgetTextColor(context));
            remoteViews.setTextViewText(R.id.widget_visibility_probvalue,String.valueOf(weatherInfo.currentWeather.getProbVisibilityBelow1km())+"%");
            remoteViews.setTextColor(R.id.widget_visibility_probvalue,ThemePicker.getWidgetTextColor(context));
        } else {
            remoteViews.setViewVisibility(R.id.widget_visibility_probvalue,View.GONE);
            remoteViews.setViewVisibility(R.id.widget_visibility_probunit1,View.GONE);
            remoteViews.setViewVisibility(R.id.widget_visibility_probunit2,View.GONE);
        }
    }

    public void setClouds(Context context, RemoteViews remoteViews, CurrentWeatherInfo weatherInfo){
        if (weatherInfo.currentWeather.hasClouds()){
            remoteViews.setViewVisibility(R.id.widget_clouds_icon,View.VISIBLE);
            remoteViews.setViewVisibility(R.id.widget_clouds_value,View.VISIBLE);
            remoteViews.setTextViewText(R.id.widget_clouds_value,weatherInfo.currentWeather.getClouds()+"%");
            remoteViews.setTextColor(R.id.widget_clouds_value,ThemePicker.getWidgetTextColor(context));

        } else {
            remoteViews.setViewVisibility(R.id.widget_clouds_icon,View.GONE);
            remoteViews.setViewVisibility(R.id.widget_clouds_value,View.GONE);
        }
    }

    public void setTemperature5cm(Context context, RemoteViews remoteViews, CurrentWeatherInfo weatherInfo){
        if (weatherInfo.currentWeather.hasTemperature5cm()){
            remoteViews.setViewVisibility(R.id.widget_temperature5cm_icon,View.VISIBLE);
            remoteViews.setViewVisibility(R.id.widget_temperature5cm_value,View.VISIBLE);
            remoteViews.setTextViewText(R.id.widget_temperature5cm_value,weatherInfo.currentWeather.getTemperature5cmInCelsiusInt()+"°");
            remoteViews.setTextColor(R.id.widget_temperature5cm_value,ThemePicker.getWidgetTextColor(context));

        } else {
            remoteViews.setViewVisibility(R.id.widget_temperature5cm_icon,View.GONE);
            remoteViews.setViewVisibility(R.id.widget_temperature5cm_value,View.GONE);
        }
    }

    public void setHumidity(Context context, RemoteViews remoteViews, CurrentWeatherInfo weatherInfo){
        if (weatherInfo.currentWeather.hasRH()){
            remoteViews.setViewVisibility(R.id.widget_rh_icon,View.VISIBLE);
            remoteViews.setViewVisibility(R.id.widget_rh_value,View.VISIBLE);
            remoteViews.setTextViewText(R.id.widget_rh_value,Math.round(weatherInfo.currentWeather.getRH())+"%");
            remoteViews.setTextColor(R.id.widget_rh_value,ThemePicker.getWidgetTextColor(context));
        } else {
            remoteViews.setViewVisibility(R.id.widget_rh_icon,View.GONE);
            remoteViews.setViewVisibility(R.id.widget_rh_value,View.GONE);
        }
    }

    @SuppressWarnings("deprecation")
    private String getNextAlarm(Context context) {
        String alarm_string = null;
        if (Build.VERSION.SDK_INT >= 21) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                AlarmManager.AlarmClockInfo alarmClockInfo = alarmManager.getNextAlarmClock();
                if (alarmClockInfo != null) {
                    long l = alarmClockInfo.getTriggerTime();
                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, HH:mm");
                    alarm_string = simpleDateFormat.format(new Date(l));
                }
            }
        }
        if (alarm_string == null) {
            alarm_string = android.provider.Settings.System.getString(context.getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED);
        }
        if (alarm_string == null) {
            return "";
        } else {
            return alarm_string;
        }
    }

    public void setDateText(Context context, RemoteViews remoteViews) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E dd.MM.");
        String dateString = simpleDateFormat.format(new Date(Calendar.getInstance().getTimeInMillis()));
        remoteViews.setViewVisibility(R.id.widget_date, View.VISIBLE);
        remoteViews.setTextViewText(R.id.widget_date, dateString);
        remoteViews.setTextColor(R.id.widget_date,ThemePicker.getWidgetTextColor(context));

    }

    public void setAlarmText(Context context, RemoteViews remoteViews) {
        String alarmString = getNextAlarm(context);
        if (!alarmString.equals("")) {
            alarmString = "\u23F0 " + alarmString;
            remoteViews.setViewVisibility(R.id.widget_nextalarm,View.VISIBLE);
            remoteViews.setTextViewText(R.id.widget_nextalarm,alarmString);
            remoteViews.setTextColor(R.id.widget_nextalarm,ThemePicker.getWidgetTextColor(context));

        } else {
            remoteViews.setViewVisibility(R.id.widget_nextalarm, View.GONE);
        }
    }

    public void setConditionText(Context context, RemoteViews remoteViews, CurrentWeatherInfo weatherInfo) {
        if (weatherInfo.currentWeather.hasCondition()){
            remoteViews.setTextViewText(R.id.classicwidget_weatherconditiontext,WeatherCodeContract.getWeatherConditionText(context,weatherInfo.currentWeather.getCondition()));
            remoteViews.setTextColor(R.id.classicwidget_weatherconditiontext,ThemePicker.getWidgetTextColor(context));
        } else {
            remoteViews.setTextViewText(R.id.classicwidget_weatherconditiontext,context.getResources().getString(R.string.weathercode_UNKNOWN));
            remoteViews.setTextColor(R.id.classicwidget_weatherconditiontext,ThemePicker.getWidgetTextColor(context));
        }
    }

    public void setConditionIcon(Context context, RemoteViews remoteViews, CurrentWeatherInfo weatherInfo) {
        if (weatherInfo.currentWeather.hasCondition()){
            remoteViews.setImageViewResource(R.id.classicwidget_weatherconditionicon,WeatherCodeContract.getWeatherConditionDrawableResource(context,weatherInfo.currentWeather.getCondition(),weatherInfo.currentWeather.isDaytime(weatherInfo.weatherLocation)));
        } else {
            remoteViews.setImageViewBitmap(R.id.classicwidget_weatherconditionicon,WeatherIcons.getIconBitmap(context,WeatherIcons.NOT_AVAILABLE,true));
        }
    }

    public void setLocationText(Context context, RemoteViews remoteViews, CurrentWeatherInfo weatherInfo, boolean shorten_text){
        String location_text = weatherInfo.getCity();
        if ((location_text.length()>10) && (shorten_text)){
            location_text = location_text.substring(0,10)+".";
        }
        remoteViews.setTextViewText(R.id.classicwidget_locationtext,location_text);
        remoteViews.setTextColor(R.id.classicwidget_locationtext,ThemePicker.getWidgetTextColor(context));

    }

    public void setWarningTextAndIcon(Context context, RemoteViews remoteViews, int containerID, int imageViewId, int textViewId, int moreID){
        if (WeatherSettings.displayWarningsInWidget(context)){
            Weather.WeatherLocation weatherLocation = WeatherSettings.getSetStationLocation(context);
            ArrayList<WeatherWarning> warnings =  WeatherWarnings.getCurrentWarnings(context,true);
            ArrayList<WeatherWarning> locationWarnings = WeatherWarnings.getWarningsForLocation(context,warnings,weatherLocation);
            if (locationWarnings.size()>0){
                remoteViews.setViewVisibility(containerID,View.VISIBLE);
                long startTime = Calendar.getInstance().getTimeInMillis();
                long stopTime = startTime + 24*60*60*1000; // next 24h
                SpannableStringBuilder spannableStringBuilder = WeatherWarnings.getMiniWarningsString(context, locationWarnings, startTime, stopTime, false,WeatherWarnings.WarningStringType.EVENT);
                remoteViews.setTextViewText(textViewId,spannableStringBuilder);
                int color = ThemePicker.adaptColorToTheme(context,locationWarnings.get(0).getWarningColor());
                remoteViews.setInt(imageViewId,"setColorFilter",color);
                if (locationWarnings.size()>1){
                    remoteViews.setViewVisibility(moreID,View.VISIBLE);
                    remoteViews.setInt(moreID,"setColorFilter",color);
                } else {
                    remoteViews.setViewVisibility(moreID,View.GONE);
                }
            } else {
                remoteViews.setViewVisibility(containerID,View.GONE);
            }
        } else {
            remoteViews.setViewVisibility(containerID,View.GONE);
        }
    }

    public void setWarningIcon(Context context, RemoteViews remoteViews, int imageViewId){
        if (WeatherSettings.displayWarningsInWidget(context)){
            Weather.WeatherLocation weatherLocation = WeatherSettings.getSetStationLocation(context);
            ArrayList<WeatherWarning> warnings =  WeatherWarnings.getCurrentWarnings(context,true);
            ArrayList<WeatherWarning> locationWarnings = WeatherWarnings.getWarningsForLocation(context,warnings,weatherLocation);
            if (locationWarnings.size()>0){
                remoteViews.setViewVisibility(imageViewId,View.VISIBLE);
                int color = ThemePicker.adaptColorToTheme(context,locationWarnings.get(0).getWarningColor());
                remoteViews.setInt(imageViewId,"setColorFilter",color);
            } else {
                remoteViews.setViewVisibility(imageViewId,View.GONE);
            }
        } else {
            remoteViews.setViewVisibility(imageViewId,View.GONE);
        }
    }

    public void setClassicWidgetItems(RemoteViews remoteViews, WeatherSettings weatherSettings, CurrentWeatherInfo weatherCard, Context c, boolean shorten_text){
        if (weatherCard==null){
            weatherCard = new CurrentWeatherInfo();
            weatherCard.setToEmpty();
        }
        setWarningIcon(c,remoteViews,R.id.widget_warningsymbol);
        setLocationText(c,remoteViews,weatherCard,shorten_text);
        setConditionText(c,remoteViews,weatherCard);
        setConditionIcon(c,remoteViews,weatherCard);
        if (weatherCard.currentWeather.hasTemperature()){
            remoteViews.setTextViewText(R.id.classicwidget_temperature,String.valueOf(weatherCard.currentWeather.getTemperatureInCelsiusInt()+"°"));
        } else {
            remoteViews.setTextViewText(R.id.classicwidget_temperature,NOT_AVAILABLE);
        }
        remoteViews.setTextColor(R.id.classicwidget_temperature,ThemePicker.getWidgetTextColor(c));
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
        remoteViews.setTextColor(R.id.classicwidget_temperature_highlow,ThemePicker.getWidgetTextColor(c));
        remoteViews.setViewVisibility(R.id.classicwidget_temperature_highlow,View.VISIBLE);
        String windstring="";
        if (weatherCard.currentWeather.hasWindDirection()){
            if (weatherSettings.getWindDisplayType()==Weather.WindDisplayType.ARROW){
                remoteViews.setViewVisibility(R.id.classicwidget_windarrow,View.VISIBLE);
                if (weatherSettings.display_wind_arc) {
                    remoteViews.setImageViewBitmap(R.id.classicwidget_windarrow, Weather.WeatherInfo.getWindForecastTint(weatherCard.currentWeather.getArrowBitmap(c,true),weatherCard.getWindForecast(WeatherSettings.getWindArcPeriod(c))));
                } else {
                    remoteViews.setImageViewBitmap(R.id.classicwidget_windarrow,weatherCard.currentWeather.getArrowBitmap(c,true));
                }
            }
            if (weatherSettings.getWindDisplayType()==Weather.WindDisplayType.BEAUFORT){
                remoteViews.setViewVisibility(R.id.classicwidget_windarrow,View.VISIBLE);
                if (weatherSettings.display_wind_arc){
                    remoteViews.setImageViewBitmap(R.id.classicwidget_windarrow, Weather.WeatherInfo.getWindForecastTint(weatherCard.currentWeather.getBeaufortBitmap(c,true),weatherCard.getWindForecast(WeatherSettings.getWindArcPeriod(c))));
                } else {
                    remoteViews.setImageViewBitmap(R.id.classicwidget_windarrow,weatherCard.currentWeather.getBeaufortBitmap(c,true));
                }
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
            if (WeatherSettings.getWindDisplayUnit(c)==Weather.WindDisplayUnit.METERS_PER_SECOND){
                windspeed = String.valueOf(weatherCard.currentWeather.getWindSpeedInMsInt())+" ";
            }
            if (WeatherSettings.getWindDisplayUnit(c)==Weather.WindDisplayUnit.KILOMETERS_PER_HOUR){
                windspeed = String.valueOf(weatherCard.currentWeather.getWindSpeedInKmhInt())+" ";
            }
            if (WeatherSettings.getWindDisplayUnit(c)==Weather.WindDisplayUnit.BEAUFORT){
                windspeed = String.valueOf(weatherCard.currentWeather.getWindSpeedInBeaufortInt())+" ";
            }
            if (WeatherSettings.getWindDisplayUnit(c)==Weather.WindDisplayUnit.KNOTS){
                windspeed = String.valueOf(weatherCard.currentWeather.getWindSpeedInKnotsInt())+" ";
            }
            windstring = windstring + windspeed;
            if (weatherCard.currentWeather.hasFlurries()){
                String flurries = "";
                switch (WeatherSettings.getWindDisplayUnit(c)){
                    case Weather.WindDisplayUnit.METERS_PER_SECOND: flurries=String.valueOf(weatherCard.currentWeather.getFlurriesInMsInt()); break;
                    case Weather.WindDisplayUnit.BEAUFORT: flurries=String.valueOf(weatherCard.currentWeather.getFlurriesInBeaufortInt()); break;
                    case Weather.WindDisplayUnit.KILOMETERS_PER_HOUR: flurries=String.valueOf(weatherCard.currentWeather.getFlurriesInKmhInt()); break;
                    case Weather.WindDisplayUnit.KNOTS: flurries=String.valueOf(weatherCard.currentWeather.getFlurriesInKnotsInt());
                }
                windstring = windstring + " ("+flurries+") ";
            }
            remoteViews.setViewVisibility(R.id.classicwidget_wind,View.VISIBLE);
            remoteViews.setTextViewText(R.id.classicwidget_wind,windstring);
            remoteViews.setTextColor(R.id.classicwidget_wind,ThemePicker.getWidgetTextColor(c));
            remoteViews.setViewVisibility(R.id.classicwidget_wind_unit,View.VISIBLE);
            remoteViews.setTextViewText(R.id.classicwidget_wind_unit,Weather.getWindUnitString(weatherSettings.getWindDisplayUnit(c)));
            remoteViews.setTextColor(R.id.classicwidget_wind_unit,ThemePicker.getWidgetTextColor(c));
        } else {
            remoteViews.setViewVisibility(R.id.classicwidget_wind,View.INVISIBLE);
        }
        int opacity = 90;
        try {
            opacity = Integer.parseInt(weatherSettings.widget_opacity);
        } catch (Exception e){
            // do nothing
        }
        remoteViews.setImageViewResource(R.id.widget_backgroundimage,ThemePicker.getWidgetBackgroundDrawable(c));
        remoteViews.setInt(R.id.widget_backgroundimage,"setImageAlpha",Math.round(opacity*2.55f));
        if (weatherSettings.widget_showdwdnote) {
            remoteViews.setViewVisibility(R.id.widget_reference_text, View.VISIBLE);
            remoteViews.setTextColor(R.id.widget_reference_text,ThemePicker.getWidgetTextColor(c));
        } else {
            remoteViews.setViewVisibility(R.id.widget_reference_text, View.GONE);
        }
    }

    public void setClassicWidgetItems(RemoteViews remoteViews, WeatherSettings weatherSettings, CurrentWeatherInfo weatherCard, Context c){
        setClassicWidgetItems(remoteViews,weatherSettings,weatherCard,c,false);
    }

    public void updateWidgetDisplay(Context c, AppWidgetManager awm, int[] widget_instances){
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(c);
        if (weatherCard==null){
            //UpdateAlarmManager.startDataUpdateService(c,true,true,false,false);
            UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(c,UpdateAlarmManager.WIDGET_UPDATE);
        } else {
            WeatherSettings weatherSettings = new WeatherSettings(c);
            for (int i=0; i<widget_instances.length; i++){
                // sets up a pending intent to launch main activity when the widget is touched.
                Intent intent = new Intent(c,MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(c,0,intent,0);
                RemoteViews remoteViews = new RemoteViews(c.getPackageName(),R.layout.classicwidget_layout);
                remoteViews.setOnClickPendingIntent(R.id.widget_maincontainer,pendingIntent);
                setClassicWidgetItems(remoteViews,weatherSettings,weatherCard,c);
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
