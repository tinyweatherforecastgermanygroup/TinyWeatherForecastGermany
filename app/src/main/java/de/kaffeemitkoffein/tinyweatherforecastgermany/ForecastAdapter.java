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
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
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

        private final static int SCALE_CONDITION_ICON = 2;
        private final static int SCALE_MINI_ICON = 8;

        private Bitmap loadScaledIcon(int id, int scale){
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = scale;
            final Bitmap result = BitmapFactory.decodeResource(context.getResources(),id,options);
            return result;
        }

        static class ViewHolder {
            TextView textView_heading;
            TextView condition_text;
            View iconbar1_view;
            TextView precipitation_textview;
            ImageView weather_icon;
            TextView textView_temp;
            TextView textView_highlow;
            TextView textView_wind;
            ImageView imageView_windarrow;
            ImageView[] symbols;
            TextView[] labels;

            public ViewHolder(){
                symbols = new ImageView[6];
                labels = new TextView[6];
            }
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
            boolean newView = false;
            ViewHolder viewHolder = new ViewHolder();
            TextView textView_weathercondition = null;
            TextView textView_heading = null;
            View iconbar1_view = null;
            TextView precipitation_textview = null;
            ImageView weather_icon = null;
            TextView textView_temp = null;
            TextView textView_highlow = null;
            TextView textView_wind = null;
            ImageView imageView_windarrow = null;
            ImageView[] symbols = new ImageView[6];
            TextView[] labels = new TextView[6];
            if (view == null){
                // view is not available from cache
                newView = true;
                view = this.layoutInflater.inflate(R.layout.forecastitem,viewGroup,false);
            } else {
                // recycle view information
                viewHolder = (ViewHolder) view.getTag();
                textView_weathercondition = viewHolder.condition_text;
                textView_heading = viewHolder.textView_heading;
                iconbar1_view = viewHolder.iconbar1_view;
                precipitation_textview = viewHolder.precipitation_textview;
                weather_icon = viewHolder.weather_icon;
                textView_temp = viewHolder.textView_temp;
                textView_highlow = viewHolder.textView_highlow;
                textView_wind = viewHolder.textView_wind;
                imageView_windarrow = viewHolder.imageView_windarrow;
                symbols = viewHolder.symbols;
                labels = viewHolder.labels;
            }
            // now fill the item with content
            if (textView_weathercondition==null){
                textView_weathercondition = (TextView) view.findViewById(R.id.fcitem_weatherconditiontext);
                viewHolder.condition_text = textView_weathercondition;
            }
            Weather.WeatherInfo weatherInfo = weatherForecasts.get(i);
            // heading
            if (textView_heading==null){
                textView_heading = (TextView) view.findViewById(R.id.fcitem_heading);
            }
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
            String precipitation_string = "";
            if (weatherInfo.hasProbPrecipitation()){
                precipitation_string = weatherInfo.getProbPrecipitation()+"% ";
            }
            if (weatherInfo.hasPrecipitation()){
                precipitation_string = precipitation_string + weatherInfo.getPrecipitation()+" kg/m²";
            }
            if (precipitation_string.equals("")){
                if (iconbar1_view == null){
                    iconbar1_view = (View) view.findViewById(R.id.fcitem_iconbar1);
                    viewHolder.iconbar1_view = iconbar1_view;
                }
                iconbar1_view.setVisibility(View.INVISIBLE);
            } else {
                if (precipitation_textview == null){
                    precipitation_textview = (TextView) view.findViewById(R.id.fcitem_precipitation_text);
                    viewHolder.precipitation_textview = precipitation_textview;
                }
                precipitation_textview.setText(precipitation_string);
            }
            // weather probablities icons, sorted by priority
            // clouds
            int index = 0;
            if (weatherInfo.hasClouds()){
                ImageView clouds_view = getSymbolView(view,index,symbols,viewHolder);
                clouds_view.setImageBitmap(loadScaledIcon(R.drawable.symbol_cloud,SCALE_MINI_ICON));
                TextView clouds_text = getTextView(view,index,labels,viewHolder);
                clouds_text.setText(weatherInfo.getClouds()+"%");
                index ++;
            }
            if (weatherInfo.hasProbThunderstorms()){
                ImageView lightning_view = getSymbolView(view,index,symbols,viewHolder);
                lightning_view.setImageBitmap(loadScaledIcon(R.drawable.symbol_lightning,SCALE_MINI_ICON));
                TextView lightning_text = getTextView(view,index,labels,viewHolder);
                lightning_text.setText(weatherInfo.getProbThunderStorms()+"%");
                index ++;
            }
            if (weatherInfo.hasProbSolidPrecipitation()){
                ImageView solid_view = getSymbolView(view,index,symbols,viewHolder);
                solid_view.setImageBitmap(loadScaledIcon(R.drawable.symbol_hail,SCALE_MINI_ICON));
                TextView solid_text = getTextView(view,index,labels,viewHolder);
                solid_text.setText(weatherInfo.getProbSolidPrecipitation()+"%");
                index ++;
            }
            if (weatherInfo.hasProbFreezingRain()){
                ImageView freezingrain_view = getSymbolView(view,index,symbols,viewHolder);
                freezingrain_view.setImageBitmap(loadScaledIcon(R.drawable.symbol_freezing_rain,SCALE_MINI_ICON));
                TextView freezingrain_text = getTextView(view,index,labels,viewHolder);
                freezingrain_text.setText(weatherInfo.getProbFreezingRain()+"%");
                index ++;
            }
            if (weatherInfo.hasProbFog()){
                ImageView fog_view = getSymbolView(view,index,symbols,viewHolder);
                fog_view.setImageBitmap(loadScaledIcon(R.drawable.symbol_fog,SCALE_MINI_ICON));
                TextView fog_text = getTextView(view,index,labels,viewHolder);
                fog_text.setText(weatherInfo.getProbFog()+"%");
                index ++;
            }
            if (weatherInfo.hasProbDrizzle()){
                ImageView drizzle_view = getSymbolView(view,index,symbols,viewHolder);
                drizzle_view.setImageBitmap(loadScaledIcon(R.drawable.symbol_drizzle,SCALE_MINI_ICON));
                TextView drizzle_text = getTextView(view,index,labels,viewHolder);
                drizzle_text.setText(weatherInfo.getProbDrizzle()+"%");
                index ++;
            }
            // make remaining icons invisible
            while (index<6){
                ImageView iv = getSymbolView(view,index,symbols,viewHolder);
                iv.setVisibility(View.INVISIBLE);
                TextView tv = getTextView(view,index,labels,viewHolder);
                tv.setVisibility(View.INVISIBLE);
                index++;
            }
            // weather icon
            if (weather_icon == null){
                weather_icon = (ImageView) view.findViewById(R.id.fcitem_weatherconditionicon);
                viewHolder.weather_icon = weather_icon;
            }
            if (weatherInfo.hasCondition()){
                Integer weathercondition = weatherInfo.getCondition();
                weather_icon.setImageBitmap(loadScaledIcon(new WeatherCodeContract().getWeatherConditionDrawableResource(weathercondition,weatherInfo.isDaytime()),SCALE_CONDITION_ICON));
            }
            // right column
            if (textView_temp == null){
                textView_temp = (TextView) view.findViewById(R.id.fcitem_temperature);
                viewHolder.textView_temp = textView_temp;
            }
            if (weatherInfo.hasTemperature()){
                textView_temp.setText(String.valueOf(weatherInfo.getTemperatureInCelsiusInt()+"°"));
            }
            if (textView_highlow == null){
                textView_highlow = (TextView) view.findViewById(R.id.fcitem_temperature_highlow);
                viewHolder.textView_highlow = textView_highlow;
            }
            if (weatherInfo.hasMinTemperature() && weatherInfo.hasMaxTemperature()){
                textView_highlow.setText(weatherInfo.getMinTemperatureInCelsiusInt()+"° | "+weatherInfo.getMaxTemperatureInCelsiusInt()+"°");
            }
            if (textView_wind == null){
                textView_wind = (TextView) view.findViewById(R.id.fcitem_wind);
                viewHolder.textView_wind = textView_wind;
            }
            if (weatherInfo.hasWindSpeed()){
                String s = String.valueOf(weatherInfo.getWindSpeedInKmhInt()+" ");
                if (weatherInfo.hasFlurries()){
                    s = s + "("+weatherInfo.getFlurriesInKmhInt()+") ";
                }
                s = s +"km/h";
                textView_wind.setText(s);
            }
            if (imageView_windarrow == null){
                imageView_windarrow = (ImageView) view.findViewById(R.id.fcitem_windarrow);
                viewHolder.imageView_windarrow = imageView_windarrow;
            }
            if (weatherInfo.hasWindDirection()){
                imageView_windarrow.setImageBitmap(weatherInfo.getArrowBitmap(context));
            }
            if (newView){
                view.setTag(viewHolder);
            }
            return view;
        }

        private ImageView getSymbolView(View view, int pos, ImageView[] symbols, ViewHolder viewHolder){
            int result = 0;
            switch (pos){
                case 0: result = R.id.fcitem_var1_symbol; break;
                case 1: result = R.id.fcitem_var2_symbol; break;
                case 2: result = R.id.fcitem_var3_symbol; break;
                case 3: result = R.id.fcitem_var4_symbol; break;
                case 4: result = R.id.fcitem_var5_symbol; break;
                case 5: result = R.id.fcitem_var6_symbol; break;
            }
            if (symbols[pos]==null){
                symbols[pos] = (ImageView) view.findViewById(result);
                viewHolder.symbols[pos] = symbols[pos];
            }
            return symbols[pos];
        }

        private TextView getTextView(View view, int pos, TextView[] labels, ViewHolder viewHolder){
            int result = 0;
            switch (pos){
                case 0: result = R.id.fcitem_var1_text; break;
                case 1: result = R.id.fcitem_var2_text; break;
                case 2: result = R.id.fcitem_var3_text; break;
                case 3: result = R.id.fcitem_var4_text; break;
                case 4: result = R.id.fcitem_var5_text; break;
                case 5: result = R.id.fcitem_var6_text; break;
            }
            if (labels[pos] == null){
                labels[pos] = (TextView) view.findViewById(result);
                viewHolder.labels[pos] = labels[pos];
            }
            return labels[pos];
        }



    }
