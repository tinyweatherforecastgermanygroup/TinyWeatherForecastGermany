package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClockWidget extends ClassicWidget {

    @Override
    public void updateWidgetDisplay(Context c, AppWidgetManager awm, int[] widget_instances) {
        CurrentWeatherInfo weatherCard = new Weather().getCurrentWeatherInfo(c);
        WeatherSettings weatherSettings = new WeatherSettings(c);
        for (int i = 0; i < widget_instances.length; i++) {
            RemoteViews remoteViews = new RemoteViews(c.getPackageName(), R.layout.clockwidget_layout);
            // sets up a pending intent to launch main activity when the widget is touched.
            Intent intent_weather = new Intent(c, MainActivity.class);
            PendingIntent pendingIntent_weather = PendingIntent.getActivity(c, 0, intent_weather, 0);
            remoteViews.setOnClickPendingIntent(R.id.weatherinclock_maincontainer, pendingIntent_weather);
            //sets 2nd pending intent to go to clock alarms when clock is touched.
            Intent intent_clock = new Intent(Intent.ACTION_MAIN);
            intent_clock.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName componentName = new ComponentName("com.android.deskclock", "com.android.deskclock.DeskClock");
            intent_clock.setComponent(componentName);
            PendingIntent pendingIntent_clock = PendingIntent.getActivity(c, 0, intent_clock, 0);
            remoteViews.setOnClickPendingIntent(R.id.clockwidget_clock, pendingIntent_clock);
            remoteViews.setOnClickPendingIntent(R.id.clockwidget_text, pendingIntent_clock);
            setClassicWidgetItems(remoteViews, weatherSettings, weatherCard, c);
            adjustClockFontSize(c, awm, i, remoteViews);
            updateClockText(c, awm, i, remoteViews);
            awm.updateAppWidget(widget_instances[i], remoteViews);
        }
    }

    private void adjustClockFontSize(Context context, final AppWidgetManager awm, final int widget_instance, RemoteViews remoteViews) {
        WidgetDimensionManager widgetDimensionManager = new WidgetDimensionManager(context, awm, widget_instance);
        float width_max = widgetDimensionManager.getWidgetWidth();
        // clock layout takes 50% of widget height
        float height_max = widgetDimensionManager.getWidgetHeight() / 2;
        if (height_max == 0) {
            height_max = (float) 90;
        }
        height_max = height_max * (float) 0.8;
        remoteViews.setTextViewTextSize(R.id.clockwidget_clock, TypedValue.COMPLEX_UNIT_SP, height_max);
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
                    Log.v("TWFG", "Alarm: " + alarm_string);
                }
            }
        }
        if (alarm_string == null) {
            alarm_string = Settings.System.getString(context.getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED);
        }
        if (alarm_string == null) {
            return "";
        } else {
            return alarm_string;
        }
    }

    private void updateClockText(Context context, final AppWidgetManager awm, final int widget_instance, RemoteViews remoteViews) {
        String alarmString = getNextAlarm(context);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd.MM.");
        String dateString = simpleDateFormat.format(new Date(Calendar.getInstance().getTimeInMillis()));
        if (!alarmString.equals("")) {
            dateString = dateString + ", \u23F0 " + alarmString;
        }
        remoteViews.setTextViewText(R.id.clockwidget_text, dateString);
    }

}








