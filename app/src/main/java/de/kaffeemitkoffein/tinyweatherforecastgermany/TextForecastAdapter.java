/*
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

public class TextForecastAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<TextForecast> textForecasts;

    public TextForecastAdapter(Context context, ArrayList<TextForecast> textForecasts){
        this.context = context;
        this.textForecasts = textForecasts;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder{
        ImageView image;
        TextView date;
        TextView title;
        TextView subtitle;
        TextView number;
    }

    private String formatTime(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        return simpleDateFormat.format(new Date(time));
    }

    @Override
    public int getCount() {
        return textForecasts.size();
    }

    @Override
    public Object getItem(int i) {
        return textForecasts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        if (view!=null){
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = this.layoutInflater.inflate(R.layout.textforecast_item,viewGroup,false);
            viewHolder.number = (TextView) view.findViewById(R.id.textforecast_item_number);
            viewHolder.image = (ImageView) view.findViewById(R.id.textforecast_item_image);
            viewHolder.date = (TextView) view.findViewById(R.id.textforecast_item_date);
            viewHolder.title = (TextView) view.findViewById(R.id.textforecast_item_title);
            viewHolder.subtitle = (TextView) view.findViewById(R.id.textforecast_item_subtitle);
            view.setTag(viewHolder);
        }
        TextForecast textForecast = textForecasts.get(i);
        viewHolder.date.setText(textForecast.getIssued());
        viewHolder.number.setText(String.valueOf(i));
        viewHolder.image.setImageDrawable(TextForecasts.getTextForecastDrawable(context,textForecast.type));
        if (textForecast.title!=null){
            viewHolder.title.setVisibility(View.VISIBLE);
            viewHolder.title.setText(textForecast.title);
        } else {
            viewHolder.title.setVisibility(View.INVISIBLE);
        }
        if (textForecast.subtitle!=null){
            viewHolder.subtitle.setVisibility(View.VISIBLE);
            viewHolder.subtitle.setText(textForecast.subtitle);
        } else {
            viewHolder.subtitle.setVisibility(View.INVISIBLE);
        }
        return view;
    }
}
