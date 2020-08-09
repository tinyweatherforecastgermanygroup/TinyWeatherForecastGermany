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
import java.util.ArrayList;
import java.util.Date;

public class ForecastAdapter extends BaseAdapter {

    private ArrayList<Weather.WeatherInfo> weatherForecasts;
    private Context context;
    LayoutInflater layoutInflater;

    public ForecastAdapter(Context context, ArrayList<Weather.WeatherInfo> weatherForecasts){
        this.context = context;
        this.weatherForecasts = weatherForecasts;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return weatherForecasts.size();
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
        Weather.WeatherInfo weatherInfo = weatherForecasts.get(i);
        // heading
        TextView textView_heading = (TextView) view.findViewById(R.id.fcitem_heading);
        SimpleDateFormat format = new SimpleDateFormat("EE, dd.MM.yyyy, HH:mm:ss");
        Date date = new Date();
        date.setTime(weatherForecasts.get(i).getTimestamp());
        String timetext = format.format(date);
        textView_heading.setText(timetext);
        // left column
        if (weatherInfo.hasCondition()){
            Integer weathercondition = weatherInfo.getCondition();
            textView_weathercondition.setText(new WeatherCodeContract().getWeatherConditionText(context,weathercondition));
        }
        /*
        TextView textView_clouds = (TextView) view.findViewById(R.id.fcitem_clouds);
        if (weatherInfo.hasClouds()){
            textView_clouds.setText(context.getResources().getString(R.string.clouds)+" "+weatherInfo.getClouds()+"%");
        }
        TextView textView_rain = (TextView) view.findViewById(R.id.fcitem_rain);
        if ((weatherInfo.hasPrecipitation()) && (weatherInfo.hasProbPrecipitation())){
            textView_rain.setText(context.getResources().getString(R.string.rain)+" "+weatherInfo.getProbPrecipitation()+"% "+weatherInfo.getPrecipitation()+ " kg/m²");
        }
        */
        // precipitation information
        String precipitation_string = "";
        if (weatherInfo.hasProbPrecipitation()){
            precipitation_string = weatherInfo.getProbPrecipitation()+"% ";
        }
        if (weatherInfo.hasPrecipitation()){
            precipitation_string = precipitation_string + weatherInfo.getPrecipitation()+" kg/m²";
        }
        // hide bar if no precipitation information available
        if (precipitation_string.equals("")){
            View iconbar1_view = (View) view.findViewById(R.id.fcitem_iconbar1);
            iconbar1_view.setVisibility(View.INVISIBLE);
        } else {
            TextView precipitation_textview = (TextView) view.findViewById(R.id.fcitem_precipitation_text);
            precipitation_textview.setText(precipitation_string);
        }
        // weather probablities icons, sorted by priority
        // clouds
        int index = 1;
        if (weatherInfo.hasClouds()){
            ImageView clouds_view = getSymbolView(view,index);
            clouds_view.setImageResource(R.drawable.symbol_cloud);
            TextView clouds_text = getTextView(view,index);
            clouds_text.setText(weatherInfo.getClouds()+"%");
            index ++;
        }
        if (weatherInfo.hasProbThunderstorms()){
            ImageView lightning_view = getSymbolView(view,index);
            lightning_view.setImageResource(R.drawable.symbol_lightning);
            TextView lightning_text = getTextView(view,index);
            lightning_text.setText(weatherInfo.getProbThunderStorms()+"%");
            index ++;
        }
        if (weatherInfo.hasProbSolidPrecipitation()){
            ImageView solid_view = getSymbolView(view,index);
            solid_view.setImageResource(R.drawable.symbol_hail);
            TextView solid_text = getTextView(view,index);
            solid_text.setText(weatherInfo.getProbSolidPrecipitation()+"%");
            index ++;
        }
        if (weatherInfo.hasProbFreezingRain()){
            ImageView freezingrain_view = getSymbolView(view,index);
            freezingrain_view.setImageResource(R.drawable.symbol_freezing_rain);
            TextView freezingrain_text = getTextView(view,index);
            freezingrain_text.setText(weatherInfo.getProbFreezingRain()+"%");
            index ++;
        }
        if (weatherInfo.hasProbFog()){
            ImageView fog_view = getSymbolView(view,index);
            fog_view.setImageResource(R.drawable.symbol_fog);
            TextView fog_text = getTextView(view,index);
            fog_text.setText(weatherInfo.getProbFog()+"%");
            index ++;
        }
        if (weatherInfo.hasProbDrizzle()){
            ImageView drizzle_view = getSymbolView(view,index);
            drizzle_view.setImageResource(R.drawable.symbol_drizzle);
            TextView drizzle_text = getTextView(view,index);
            drizzle_text.setText(weatherInfo.getProbDrizzle()+"%");
            index ++;
        }
        // make remaining icons invisible
        while (index<6){
            ImageView iv = getSymbolView(view,index);
            iv.setVisibility(View.INVISIBLE);
            TextView tv = getTextView(view,index);
            tv.setVisibility(View.INVISIBLE);
            index++;
        }
        // weather icon
        ImageView weather_icon = (ImageView) view.findViewById(R.id.fcitem_weatherconditionicon);
        if (weatherInfo.hasCondition()){
            Integer weathercondition = weatherInfo.getCondition();
            weather_icon.setImageDrawable(new WeatherCodeContract().getWeatherConditionDrawable(context,weathercondition,weatherInfo.isDaytime()));
        }
        // right column
        TextView textView_temp = (TextView) view.findViewById(R.id.fcitem_temperature);
        if (weatherInfo.hasTemperature()){
            textView_temp.setText(String.valueOf(weatherInfo.getTemperatureInCelsiusInt()+"°"));
        }
        TextView textView_highlow = (TextView) view.findViewById(R.id.fcitem_temperature_highlow);
        if (weatherInfo.hasMinTemperature() && weatherInfo.hasMaxTemperature()){
            textView_highlow.setText(weatherInfo.getMinTemperatureInCelsiusInt()+"° | "+weatherInfo.getMaxTemperatureInCelsiusInt()+"°");
        }
        TextView textView_wind = (TextView) view.findViewById(R.id.fcitem_wind);
        if (weatherInfo.hasWindSpeed()){
            String s = weatherInfo.getWindSpeedInKmhInt()+" km/h";
            if (weatherInfo.hasFlurries()){
                s = s + " ("+weatherInfo.getFlurriesInKmhInt()+")";
            }
            textView_wind.setText(s);
        }
        ImageView imageView_windarrow = (ImageView) view.findViewById(R.id.fcitem_windarrow);
        if (weatherInfo.hasWindDirection()){
            imageView_windarrow.setImageBitmap(weatherInfo.getArrowBitmap(context));
        }
        return view;
    }

    private ImageView getSymbolView(View view, int pos){
        ImageView result = null;
        switch (pos){
            case 1: result = (ImageView) view.findViewById(R.id.fcitem_var1_symbol); break;
            case 2: result = (ImageView) view.findViewById(R.id.fcitem_var2_symbol); break;
            case 3: result = (ImageView) view.findViewById(R.id.fcitem_var3_symbol); break;
            case 4: result = (ImageView) view.findViewById(R.id.fcitem_var4_symbol); break;
            case 5: result = (ImageView) view.findViewById(R.id.fcitem_var5_symbol); break;
            case 6: result = (ImageView) view.findViewById(R.id.fcitem_var6_symbol); break;
        }
        return result;
    }

    private TextView getTextView(View view, int pos){
        TextView result = null;
        switch (pos){
            case 1: result = (TextView) view.findViewById(R.id.fcitem_var1_text); break;
            case 2: result = (TextView) view.findViewById(R.id.fcitem_var2_text); break;
            case 3: result = (TextView) view.findViewById(R.id.fcitem_var3_text); break;
            case 4: result = (TextView) view.findViewById(R.id.fcitem_var4_text); break;
            case 5: result = (TextView) view.findViewById(R.id.fcitem_var5_text); break;
            case 6: result = (TextView) view.findViewById(R.id.fcitem_var6_text); break;
        }
        return result;
    }

}
