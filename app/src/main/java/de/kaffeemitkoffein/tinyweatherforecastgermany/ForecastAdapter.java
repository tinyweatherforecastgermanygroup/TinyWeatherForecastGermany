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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ForecastAdapter extends BaseAdapter {

    private Weather.CurrentWeatherInfo weatherCard;
    private Context context;
    LayoutInflater layoutInflater;

    public ForecastAdapter(Context context, Weather.CurrentWeatherInfo weatherCard){
        this.context = context;
        this.weatherCard = weatherCard;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 9;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = this.layoutInflater.inflate(R.layout.forecastitem,viewGroup,false);
        // now fill the item with content
        TextView textView_weathercondition = (TextView) view.findViewById(R.id.fcitem_weatherconditiontext);
        Weather.WeatherInfo weatherInfo = weatherCard.forecast6hourly.get(i);
        int weathercondition = weatherInfo.getCondition();
        // heading
        TextView textView_heading = (TextView) view.findViewById(R.id.fcitem_heading);
        SimpleDateFormat format = new SimpleDateFormat("E, dd. MMM yyyy hh:mm:ss");
        Date date = new Date();
        date.setTime(weatherCard.forecast6hourly.get(i).timestamp);
        String timetext = format.format(date);
        textView_heading.setText(timetext);
        // left column
        textView_weathercondition.setText(new WeatherCodeContract().getWeatherConditionText(context,weathercondition));
        TextView textView_clouds = (TextView) view.findViewById(R.id.fcitem_clouds);
        textView_clouds.setText(context.getResources().getString(R.string.clouds)+" "+weatherCard.forecast6hourly.get(i).clouds+"%");
        TextView textView_rain = (TextView) view.findViewById(R.id.fcitem_rain);
        textView_rain.setText(context.getResources().getString(R.string.rain)+" "+weatherInfo.getProbPrecipitation()+" "+weatherInfo.getPrecipitation());
        // weather icon
        ImageView weather_icon = (ImageView) view.findViewById(R.id.fcitem_weatherconditionicon);
        weather_icon.setImageDrawable(new WeatherCodeContract().getWeatherConditionDrawable(context,weathercondition,weatherInfo.isDaytime()));
        // right column
        TextView textView_temp = (TextView) view.findViewById(R.id.fcitem_temperature);
        textView_temp.setText(weatherInfo.getTemperature());
        TextView textView_highlow = (TextView) view.findViewById(R.id.fcitem_temperature_highlow);
        textView_highlow.setText(weatherInfo.getMinTemperature()+" | "+weatherInfo.getMaxTemperature());
        TextView textView_wind = (TextView) view.findViewById(R.id.fcitem_wind);
        textView_wind.setText(weatherInfo.getWindSpeed()+" ("+weatherInfo.getFlurries()+")");
        ImageView imageView_windarrow = (ImageView) view.findViewById(R.id.fcitem_windarrow);
        imageView_windarrow.setImageBitmap(weatherInfo.getArrowBitmap(context,i));
        return view;
    }
}
