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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.*;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.*;

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
        super.onDeleted(c,ints);
    }

    @Override
    public void onRestored(Context c, int[] ints, int[] ints2){
        super.onRestored(c,ints,ints2);
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
        updateWidgetDisplay(c,awm,widget_instances);
        // serve GadgetBridge if necessary
        if (WeatherSettings.serveGadgetBridge(c)){
            GadgetbridgeAPI.sendWeatherBroadcastIfEnabled(c,null);
            GadgetbridgeBroadcastReceiver.setNextGadgetbridgeUpdateAction(c);
        }
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
        if (weatherInfo.currentWeather.hasPrecipitation()){
            double precipitationAmount = weatherInfo.currentWeather.getPrecipitation();
            if (precipitationAmount>0){
                remoteViews.setViewVisibility(R.id.clockwidget_precipitation_unit1,View.VISIBLE);
                remoteViews.setViewVisibility(R.id.clockwidget_precipitation_unit2,View.VISIBLE);
                if (weatherInfo.currentWeather.hasProbPrecipitation() && weatherInfo.currentWeather.hasPrecipitation()){
                    preciptitation = preciptitation +", ";
                }
                preciptitation = preciptitation + weatherInfo.currentWeather.getPrecipitation();
            } else {
                remoteViews.setViewVisibility(R.id.clockwidget_precipitation_unit1,View.GONE);
                remoteViews.setViewVisibility(R.id.clockwidget_precipitation_unit2,View.GONE);
            }
        } else {
            remoteViews.setViewVisibility(R.id.clockwidget_precipitation_unit1,View.GONE);
            remoteViews.setViewVisibility(R.id.clockwidget_precipitation_unit2,View.GONE);
        }
        if (preciptitation.equals("")){
            remoteViews.setViewVisibility(R.id.widget_precipitation_container, View.INVISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.widget_precipitation_container, View.VISIBLE);
            remoteViews.setTextViewText(R.id.widget_precipitation_text,preciptitation);
            remoteViews.setTextColor(R.id.widget_precipitation_text,ThemePicker.getPrecipitationAccentColor(context));
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
                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, HH:mm",Locale.getDefault());
                    simpleDateFormat.setTimeZone(TimeZone.getDefault());
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
            //alarmString = "\u23F0 " + alarmString;
            remoteViews.setViewVisibility(R.id.widget_nextalarm,View.VISIBLE);
            remoteViews.setViewVisibility(R.id.widget_alarmicon,View.VISIBLE);
            remoteViews.setTextViewText(R.id.widget_nextalarm,alarmString);
            remoteViews.setTextColor(R.id.widget_nextalarm,ThemePicker.getWidgetTextColor(context));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_access_alarm_black_24dp,options);
            ThemePicker.applyColor(bitmap,ThemePicker.getWidgetTextColor(context));
            remoteViews.setImageViewBitmap(R.id.widget_alarmicon,bitmap);
        } else {
            remoteViews.setViewVisibility(R.id.widget_nextalarm, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_alarmicon, View.GONE);
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
            ForecastIcons forecastIcons = new ForecastIcons(context,null);
            remoteViews.setImageViewBitmap(R.id.classicwidget_weatherconditionicon,forecastIcons.getIconBitmap(weatherInfo.currentWeather,weatherInfo.weatherLocation));
            //remoteViews.setImageViewResource(R.id.classicwidget_weatherconditionicon,WeatherCodeContract.getWeatherConditionDrawableResource(context,weatherInfo.currentWeather.getCondition(),weatherInfo.currentWeather.isDaytime(weatherInfo.weatherLocation)));
        } else {
            remoteViews.setImageViewBitmap(R.id.classicwidget_weatherconditionicon,WeatherIcons.getIconBitmap(context,WeatherIcons.NOT_AVAILABLE,true));
        }
    }

    public void setLocationText(Context context, RemoteViews remoteViews, CurrentWeatherInfo weatherInfo, boolean shorten_text){
        String location_text = weatherInfo.getCity(context);
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
            remoteViews.setTextColor(R.id.classicwidget_temperature,ThemePicker.getTemperatureAccentColor(c,weatherCard.currentWeather));
        } else {
            remoteViews.setTextViewText(R.id.classicwidget_temperature,NOT_AVAILABLE);
        }
        //remoteViews.setTextColor(R.id.classicwidget_temperature,ThemePicker.getWidgetTextColor(c));
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
        remoteViews.setImageViewResource(android.R.id.background,ThemePicker.getWidgetBackgroundDrawableRessource(c));
        remoteViews.setInt(android.R.id.background,"setImageAlpha",Math.round(opacity*2.55f));
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
        CurrentWeatherInfo weatherCard = Weather.getCurrentWeatherInfo(c);
        if (weatherCard!=null){
            WeatherSettings weatherSettings = new WeatherSettings(c);
            for (int i=0; i<widget_instances.length; i++){
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
                Intent intent = new Intent(c,MainActivity.class);
                PendingIntent pendingIntent;
                if (Build.VERSION.SDK_INT>=23){
                    pendingIntent = PendingIntent.getActivity(c,0,intent,PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent = PendingIntent.getActivity(c,0,intent,0);
                }
                int widgetResource = R.layout.classicwidget_layout;
                int lines = 1;
                if (widgetHeight>120){
                    lines = 2;
                    widgetResource = R.layout.largewidget_layout;
                }
                if (widgetHeight>240){
                    lines = 3;
                }
                RemoteViews remoteViews = new RemoteViews(c.getPackageName(),widgetResource);
                if (lines==2){
                    remoteViews.setImageViewBitmap(R.id.largewidget_10daysbitmap, get10DaysForecastBar(c,awm,widget_instances[i],weatherCard,false));
                }
                if (lines==3){
                    remoteViews.setImageViewBitmap(R.id.largewidget_10daysbitmap, get10DaysForecastBar(c,awm,widget_instances[i],weatherCard,true));
                }
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

    // bitmap functions for the lower part of the widget

    private int getDailyItemCount(Weather.WeatherInfo weatherInfo, boolean showTemperatures){
        if (weatherInfo == null) {
            return 0;
        }
        int item_count = 0;
        if (weatherInfo.hasCondition()){
            item_count ++;
        }
        if (showTemperatures){
            if (weatherInfo.hasMinTemperature()){
                item_count ++;
            }
            if (weatherInfo.hasMaxTemperature()){
                item_count ++;
            }
        }
        return item_count;
    }

    private final static float OFFSET_FONTSIZE = 60;
    private final static float FONTSIZESTEP = 1;

    public static float getMaxPossibleFontsize(String string, float max_width, float max_height, Float offset){
        if (offset==null){
            if (max_width>max_height){
                offset = max_width;
            } else {
                offset = max_height;
            }
        }
        float textsize = offset;
        Paint paint = new Paint();
        paint.setTextSize(textsize);
        while ((textsize>0) && (paint.measureText(string)>max_width)){
            textsize = textsize - FONTSIZESTEP;
            paint.setTextSize(textsize);
        }
        while ((textsize>0) && (paint.getTextSize()>max_height)){
            textsize = textsize - FONTSIZESTEP;
            paint.setTextSize(textsize);
        }
        return textsize;
    }

    private float fontsize_temperature = OFFSET_FONTSIZE;
    private float fontsize_dayofweek = OFFSET_FONTSIZE;

    private void determineMaxFontSizes(CurrentWeatherInfo currentWeatherInfo, float max_width, float max_height, boolean showTemperatures){
        int item_count=0;
        for (int i=0; i<currentWeatherInfo.forecast24hourly.size(); i++){
            if (getDailyItemCount(currentWeatherInfo.forecast24hourly.get(i),showTemperatures)>item_count){
                item_count = getDailyItemCount(currentWeatherInfo.forecast24hourly.get(i),showTemperatures);
            }
            String min_temp = "-°";
            if (currentWeatherInfo.forecast24hourly.get(i).hasMinTemperature()){
                min_temp = currentWeatherInfo.forecast24hourly.get(i).getMinTemperatureInCelsiusInt()+"°";
            }
            String max_temp = "-°";
            if (currentWeatherInfo.forecast24hourly.get(i).hasMaxTemperature()){
                max_temp = currentWeatherInfo.forecast24hourly.get(i).getMaxTemperatureInCelsiusInt()+"°";
            }
            Paint p_temp = new Paint();
            p_temp.setTextSize(fontsize_temperature);
            float mf1 = getMaxPossibleFontsize(min_temp,max_width,(max_height/(item_count+1))*0.95f,OFFSET_FONTSIZE);
            if (mf1<fontsize_temperature){
                fontsize_temperature = mf1;
            }
            float mf2 = getMaxPossibleFontsize(max_temp,max_width,(max_height/(item_count+1))*0.95f,OFFSET_FONTSIZE);
            if (mf2<fontsize_temperature){
                fontsize_temperature = mf2;
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE");
        for (int i=0; i<currentWeatherInfo.forecast24hourly.size(); i++){
            String day      = simpleDateFormat.format(new Date(currentWeatherInfo.forecast24hourly.get(i).getTimestamp()));
            Paint p_day = new Paint();
            p_day.setTextSize(fontsize_dayofweek);
            float mf3 = getMaxPossibleFontsize(day,max_width,(max_height/(item_count+1))*0.95f,OFFSET_FONTSIZE);
            if (mf3<fontsize_dayofweek){
                fontsize_dayofweek = mf3;
            }
        }
        fontsize_temperature = (float) (fontsize_temperature * 0.85);
        fontsize_dayofweek = (float) (fontsize_dayofweek * 0.85);
    }

    private Bitmap getDailyBar(Context context, float width_bar, float height_bar, Weather.WeatherInfo weatherInfo, boolean showTemperatures){
        ForecastIcons forecastIcons = new ForecastIcons(context,null);
        // create an empty bitmap with black being the transparent color
        Bitmap bitmap = Bitmap.createBitmap(Math.round(width_bar),Math.round(height_bar),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.TRANSPARENT);
        // return empty, transparent bitmap if no weather data present
        if (weatherInfo == null) {
            return bitmap;
        }
        int item_count = getDailyItemCount(weatherInfo,showTemperatures);
        // return empty, transparent bitmap if no suitable weather data present
        if (item_count==0){
            return bitmap;
        }
        // weekday also is an item
        float height_item = (height_bar / (item_count+1));
        // *** draw the weekday ***
        // get the day of week string
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE");
        // the timestamp will always be midnight. When we derive the day of week from it, it will be misleading, since
        // we want to show the day *before* this midnight position.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(weatherInfo.getTimestamp());
        calendar.add(Calendar.DAY_OF_WEEK,-1);
        //String weekday = simpleDateFormat.format(new Date(weatherInfo.getTimestamp()));
        String weekday = simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
        // determine max. possible fontsize
        Paint paint_weekday = new Paint();
        paint_weekday.setColor(ThemePicker.getWidgetTextColor(context));
        paint_weekday.setAntiAlias(true);
        paint_weekday.setTextSize(fontsize_dayofweek);
        float x_offset_day = (width_bar - paint_weekday.measureText(weekday))/2;
        float y_offset_day = height_item - paint_weekday.getTextSize()/2;
        canvas.drawText(weekday,x_offset_day,y_offset_day,paint_weekday);
        // number of items may vary, so we need to iterate the y offset
        float y_offset_counter = height_item;
        // *** draw the weather icon ***
        if (weatherInfo.hasCondition()){
            //Bitmap condition_icon = BitmapFactory.decodeResource(context.getResources(),WeatherCodeContract.getWeatherConditionDrawableResource(context,weatherInfo.getCondition(),true));
            Bitmap condition_icon = forecastIcons.getIconBitmap(weatherInfo,null);
            // determine the necessary icon size, the icon ratio is always 1:1
            float max_icon_diameter = width_bar;
            if (height_item<width_bar){
                max_icon_diameter = height_item;
            }
            // scale the bitmap
            condition_icon = Bitmap.createScaledBitmap(condition_icon,(int) max_icon_diameter,(int) max_icon_diameter,false);
            float x_offset_condition = (width_bar - condition_icon.getWidth())/2;
            float y_offset_condition = y_offset_counter;
            canvas.drawBitmap(condition_icon,x_offset_condition,y_offset_condition,null);
            // iterate offset
            y_offset_counter = y_offset_counter + height_item;
        }
        // *** draw max. temperature ***
        if (weatherInfo.hasMaxTemperature()){
            String max_temperature_string = String.valueOf(weatherInfo.getMaxTemperatureInCelsiusInt())+"°";
            Paint paint_maxtemp = new Paint();
            paint_maxtemp.setColor(ThemePicker.getWidgetTextColor(context));
            paint_maxtemp.setAntiAlias(true);
            paint_maxtemp.setTextSize(fontsize_temperature);
            float x_offset_maxtemp = (width_bar - paint_weekday.measureText(max_temperature_string))/2;
            float y_offset_maxtemp = y_offset_counter - paint_maxtemp.getTextSize()/2;
            canvas.drawText(max_temperature_string,x_offset_maxtemp,y_offset_maxtemp+height_item,paint_maxtemp);
            // iterate offset
            y_offset_counter = y_offset_counter + height_item;
        }
        // *** draw min. temperature ***
        if (weatherInfo.hasMinTemperature()){
            String min_temperature_string = String.valueOf(weatherInfo.getMinTemperatureInCelsiusInt())+"°";
            Paint paint_mintemp = new Paint();
            paint_mintemp.setColor(ThemePicker.getWidgetTextColor(context));
            paint_mintemp.setAntiAlias(true);
            paint_mintemp.setTextSize(fontsize_temperature);
            float x_offset_mintemp = (width_bar - paint_weekday.measureText(min_temperature_string))/2;
            float y_offset_mintemp = y_offset_counter - paint_mintemp.getTextSize()/2;
            canvas.drawText(min_temperature_string,x_offset_mintemp,y_offset_mintemp+height_item,paint_mintemp);
            // iterate offset
            y_offset_counter = y_offset_counter + height_item;
        }
        return bitmap;
    }

    private Bitmap get10DaysForecastBar(Context context, AppWidgetManager awm, int widget_instance, CurrentWeatherInfo currentWeatherInfo, boolean showTemperatures){
        /*
         * Determine the approximate diameters of the bitmap.
         *
         * The /2 is hardcoded from the largewidget_layout.xml: the forecast bitmap holding the 10 days
         * forecast takes the lower half of the forecast bar.
         *
         * It may be a little bit smaller in fact if the reference text is displayed. However, this will be
         * adapted by the system and/or launcher when the widget view gets inflated. It is the better choice to
         * assume the larger size (image gets downscaled) than a too small size (image gets upscaled and may look
         * awful).
         */
        WidgetDimensionManager widgetDimensionManager = new WidgetDimensionManager(context, awm,widget_instance);
        float width_bitmap = widgetDimensionManager.getWidgetWidth();
        float height_bitmap = widgetDimensionManager.getWidgetHeight()/2;
        if ((width_bitmap<=0) || (height_bitmap<=0)){
            // make some fallback values if the widget dimensions remain unknown
            width_bitmap = 500;
            height_bitmap = 250;
        }
        // create an empty, transparent bitmap
        Bitmap bitmap = Bitmap.createBitmap(Math.round(width_bitmap),Math.round(height_bitmap),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.TRANSPARENT);
        // return empty, transparent bitmap if no weather data present
        if (currentWeatherInfo==null){
            return bitmap;
        }
        int number_of_forecast_days = currentWeatherInfo.forecast24hourly.size();
        if (number_of_forecast_days==0){
            return bitmap;
        }
        float width_oneday = width_bitmap / (number_of_forecast_days-1);
        float height_oneday = height_bitmap;
        determineMaxFontSizes(currentWeatherInfo,width_oneday,height_oneday,showTemperatures);
        for (int i=1; i<number_of_forecast_days; i++){
            Bitmap item = getDailyBar(context,width_oneday,height_oneday,currentWeatherInfo.forecast24hourly.get(i),showTemperatures);
            canvas.drawBitmap(item,(i-1)*width_oneday,0,null);
        }
        return bitmap;
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
            widget_width_portrait_dp = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            widget_width_landscape_dp = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
            widget_height_landscape_dp= bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            widget_height_portrait_dp = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
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
