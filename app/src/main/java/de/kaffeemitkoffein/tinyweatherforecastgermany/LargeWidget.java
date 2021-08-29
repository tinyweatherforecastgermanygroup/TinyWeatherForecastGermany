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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
            remoteViews.setOnClickPendingIntent(R.id.widget_maincontainer, pendingIntent);
            setClassicWidgetItems(remoteViews, weatherSettings, weatherCard, c);
            remoteViews.setImageViewBitmap(R.id.largewidget_10daysbitmap, get10DaysForecastBar(c,awm,widget_instances[i],weatherCard));
            awm.updateAppWidget(widget_instances[i], remoteViews);
        }
    }

    private int getDailyItemCount(Weather.WeatherInfo weatherInfo){
        if (weatherInfo == null) {
            return 0;
        }
        int item_count = 0;
        if (weatherInfo.hasCondition()){
            item_count ++;
        }
        if (weatherInfo.hasMinTemperature()){
            item_count ++;
        }
        if (weatherInfo.hasMaxTemperature()){
            item_count ++;
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

    private void determineMaxFontSizes(CurrentWeatherInfo currentWeatherInfo, float max_width, float max_height){
        int item_count=0;
        for (int i=0; i<currentWeatherInfo.forecast24hourly.size(); i++){
            if (getDailyItemCount(currentWeatherInfo.forecast24hourly.get(i))>item_count){
                item_count = getDailyItemCount(currentWeatherInfo.forecast24hourly.get(i));
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

    private Bitmap getDailyBar(Context context, float width_bar, float height_bar, Weather.WeatherInfo weatherInfo){
        // create an empty bitmap with black being the transparent color
        Bitmap bitmap = Bitmap.createBitmap(Math.round(width_bar),Math.round(height_bar),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.TRANSPARENT);
        // return empty, transparent bitmap if no weather data present
        if (weatherInfo == null) {
            return bitmap;
        }
        int item_count = getDailyItemCount(weatherInfo);
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
            Bitmap condition_icon = BitmapFactory.decodeResource(context.getResources(),WeatherCodeContract.getWeatherConditionDrawableResource(context,weatherInfo.getCondition(),true));
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

    private Bitmap get10DaysForecastBar(Context context, AppWidgetManager awm, int widget_instance, CurrentWeatherInfo currentWeatherInfo){
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
        determineMaxFontSizes(currentWeatherInfo,width_oneday,height_oneday);
        for (int i=1; i<number_of_forecast_days; i++){
            Bitmap item = getDailyBar(context,width_oneday,height_oneday,currentWeatherInfo.forecast24hourly.get(i));
            canvas.drawBitmap(item,(i-1)*width_oneday,0,null);
        }
        return bitmap;
    }

}
