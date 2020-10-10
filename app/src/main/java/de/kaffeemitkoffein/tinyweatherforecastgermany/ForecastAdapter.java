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
    import android.graphics.Color;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.*;
    import org.astronomie.info.Astronomy;

    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.Date;

    public class ForecastAdapter extends BaseAdapter {

        private ArrayList<Weather.WeatherInfo> weatherForecasts;
        private ArrayList<Weather.WeatherInfo> weatherForecasts_hourly;
        private Weather.WeatherLocation weatherLocation;
        private Context context;
        private boolean display_bar;
        private boolean display_visibility;
        private boolean display_pressure;
        private boolean display_sunrise;
        private boolean display_endofday_bar;
        LayoutInflater layoutInflater;

        public ForecastAdapter(Context context, ArrayList<Weather.WeatherInfo> weatherForecasts, ArrayList<Weather.WeatherInfo> weatherForecasts_hourly, Weather.WeatherLocation weatherLocation) {
            this.context = context;
            this.weatherForecasts = weatherForecasts;
            this.weatherForecasts_hourly = weatherForecasts_hourly;
            this.weatherLocation = weatherLocation;
            WeatherSettings weatherSettings = new WeatherSettings(context);
            this.display_bar = weatherSettings.display_bar;
            this.display_pressure = weatherSettings.display_pressure;
            this.display_visibility = weatherSettings.display_visibility;
            this.display_sunrise = weatherSettings.display_sunrise;
            this.display_endofday_bar = weatherSettings.display_endofday_bar;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private final static int SCALE_CONDITION_ICON = 2;
        private final static int SCALE_MINI_ICON = 8;

        private Bitmap loadScaledIcon(int id, int scale){
            final Bitmap result = BitmapFactory.decodeResource(context.getResources(),id,null);
            return result;
        }

        static class ViewHolder {
            RelativeLayout main_container;
            TextView textView_heading;
            TextView condition_text;
            View iconbar1_view;
            TextView precipitation_textview;
            ImageView weather_icon;
            TextView textView_temp;
            TextView textView_highlow;
            TextView textView_wind;
            LinearLayout linearLayout_visibility;
            TextView textview_visibility;
            ImageView imageView_windarrow;
            ImageView[] symbols;
            TextView[] labels;
            ImageView imageView_forecastBar;
            TextView rise1;
            TextView rise2;
            ImageView sunset1;
            ImageView sunset2;
            View endofday_bar;

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
            RelativeLayout main_container = null;
            TextView textView_weathercondition = null;
            TextView textView_heading = null;
            View iconbar1_view = null;
            TextView precipitation_textview = null;
            ImageView weather_icon = null;
            TextView textView_temp = null;
            TextView textView_highlow = null;
            TextView textView_wind = null;
            LinearLayout linearLayout_visibility = null;
            TextView textview_visibility = null;
            ImageView imageView_windarrow = null;
            ImageView imageView_forecastBar = null;
            ImageView[] symbols = new ImageView[6];
            TextView[] labels = new TextView[6];
            TextView rise1 = null;
            TextView rise2 = null;
            ImageView sunset1 = null;
            ImageView sunset2 = null;
            View endofday_bar = null;
            if (view == null){
                // view is not available from cache
                newView = true;
                view = this.layoutInflater.inflate(R.layout.forecastitem,viewGroup,false);
            } else {
                // recycle view information
                viewHolder = (ViewHolder) view.getTag();
                main_container = viewHolder.main_container;
                textView_weathercondition = viewHolder.condition_text;
                textView_heading = viewHolder.textView_heading;
                iconbar1_view = viewHolder.iconbar1_view;
                precipitation_textview = viewHolder.precipitation_textview;
                weather_icon = viewHolder.weather_icon;
                textView_temp = viewHolder.textView_temp;
                textView_highlow = viewHolder.textView_highlow;
                textView_wind = viewHolder.textView_wind;
                linearLayout_visibility = viewHolder.linearLayout_visibility;
                textview_visibility = viewHolder.textview_visibility;
                imageView_windarrow = viewHolder.imageView_windarrow;
                symbols = viewHolder.symbols;
                labels = viewHolder.labels;
                imageView_forecastBar = viewHolder.imageView_forecastBar;
                rise1 = viewHolder.rise1;
                rise2 = viewHolder.rise2;
                sunset1 = viewHolder.sunset1;
                sunset2 = viewHolder.sunset2;
                endofday_bar = viewHolder.endofday_bar;
            }
            // now fill the item with content
            if (main_container==null) {
                main_container = (RelativeLayout) view.findViewById(R.id.fcitem_maincontainer);
                viewHolder.main_container = main_container;
            }
            int gradient = getColorGradient(i);
            main_container.setBackgroundColor(Color.argb(96,gradient,gradient,gradient));
            if (textView_weathercondition==null){
                textView_weathercondition = (TextView) view.findViewById(R.id.fcitem_weatherconditiontext);
                viewHolder.condition_text = textView_weathercondition;
            }
            Weather.WeatherInfo weatherInfo = weatherForecasts.get(i);
            // heading
            if (textView_heading==null){
                textView_heading = (TextView) view.findViewById(R.id.fcitem_heading);
            }
            SimpleDateFormat format = new SimpleDateFormat("EE, dd.MM.yyyy, HH:mm");
            Date date = new Date();
            date.setTime(weatherForecasts.get(i).getTimestamp());
            String timetext = format.format(date);
            textView_heading.setText(timetext);
            // left column
            if (weatherInfo.hasCondition()){
                Integer weathercondition = weatherInfo.getCondition();
                textView_weathercondition.setText(WeatherCodeContract.getWeatherConditionText(context,weathercondition));
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
                    iconbar1_view = view.findViewById(R.id.fcitem_iconbar1);
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
                clouds_view.setImageBitmap(loadScaledIcon(R.mipmap.symbol_cloud,SCALE_MINI_ICON));
                TextView clouds_text = getTextView(view,index,labels,viewHolder);
                clouds_text.setText(weatherInfo.getClouds()+"%");
                index ++;
            }
            if (weatherInfo.hasProbThunderstorms()){
                ImageView lightning_view = getSymbolView(view,index,symbols,viewHolder);
                lightning_view.setImageBitmap(loadScaledIcon(R.mipmap.symbol_lightning,SCALE_MINI_ICON));
                TextView lightning_text = getTextView(view,index,labels,viewHolder);
                lightning_text.setText(weatherInfo.getProbThunderStorms()+"%");
                index ++;
            }
            if (weatherInfo.hasProbSolidPrecipitation()){
                ImageView solid_view = getSymbolView(view,index,symbols,viewHolder);
                solid_view.setImageBitmap(loadScaledIcon(R.mipmap.symbol_hail,SCALE_MINI_ICON));
                TextView solid_text = getTextView(view,index,labels,viewHolder);
                solid_text.setText(weatherInfo.getProbSolidPrecipitation()+"%");
                index ++;
            }
            if (weatherInfo.hasProbFreezingRain()){
                ImageView freezingrain_view = getSymbolView(view,index,symbols,viewHolder);
                freezingrain_view.setImageBitmap(loadScaledIcon(R.mipmap.symbol_freezing_rain,SCALE_MINI_ICON));
                TextView freezingrain_text = getTextView(view,index,labels,viewHolder);
                freezingrain_text.setText(weatherInfo.getProbFreezingRain()+"%");
                index ++;
            }
            if (weatherInfo.hasProbFog()){
                ImageView fog_view = getSymbolView(view,index,symbols,viewHolder);
                fog_view.setImageBitmap(loadScaledIcon(R.mipmap.symbol_fog,SCALE_MINI_ICON));
                TextView fog_text = getTextView(view,index,labels,viewHolder);
                fog_text.setText(weatherInfo.getProbFog()+"%");
                index ++;
            }
            if (weatherInfo.hasProbDrizzle()){
                ImageView drizzle_view = getSymbolView(view,index,symbols,viewHolder);
                drizzle_view.setImageBitmap(loadScaledIcon(R.mipmap.symbol_drizzle,SCALE_MINI_ICON));
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
                int weathercondition = weatherInfo.getCondition();
                weather_icon.setImageBitmap(loadScaledIcon(WeatherCodeContract.getWeatherConditionDrawableResource(weathercondition,weatherInfo.isDaytime(weatherLocation)),SCALE_CONDITION_ICON));
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
            String temp_low_high_pressure = "";
            if (weatherInfo.hasMinTemperature() && weatherInfo.hasMaxTemperature()){
                temp_low_high_pressure = temp_low_high_pressure + weatherInfo.getMinTemperatureInCelsiusInt()+"° | "+weatherInfo.getMaxTemperatureInCelsiusInt()+"°";
            }
            if (weatherInfo.hasPressure() && display_pressure){
                temp_low_high_pressure = temp_low_high_pressure + ", "+weatherInfo.getPressure()/100+ " hPa";
            }
            textView_highlow.setText(temp_low_high_pressure);
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
            if (linearLayout_visibility == null){
                linearLayout_visibility = (LinearLayout) view.findViewById(R.id.fcitem_visibility_container);
                viewHolder.linearLayout_visibility = linearLayout_visibility;
            }
            if (textview_visibility == null){
                textview_visibility = (TextView) view.findViewById(R.id.fcitem_visibility);
                viewHolder.textview_visibility = textview_visibility;
            }
            if (display_visibility){
                String visibility = getVisibilityString(weatherInfo);
                if (visibility!=null){
                    linearLayout_visibility.setVisibility(View.VISIBLE);
                    textview_visibility.setVisibility(View.VISIBLE);
                    textview_visibility.setText(visibility);
                } else {
                    linearLayout_visibility.setVisibility(View.GONE);
                    textview_visibility.setVisibility(View.GONE);
                }
            } else {
                linearLayout_visibility.setVisibility(View.GONE);
                textview_visibility.setVisibility(View.GONE);
            }
            if (imageView_forecastBar == null){
                imageView_forecastBar = (ImageView) view.findViewById(R.id.fcitem_forecastbar);
                viewHolder.imageView_forecastBar = imageView_forecastBar;
            }
            // hourly forecast bar, display only if forecast is 6h
            if ((weatherInfo.getForecastType() == Weather.WeatherInfo.ForecastType.HOURS_6) && (display_bar)){
                imageView_forecastBar.setVisibility(View.VISIBLE);
                // calculate offset
                int start  = getLastHourlyForecast();
                int offset = getHourlyOffset(start,i);
                int count = 6;
                if (offset<start){
                    count = 6 - (start-offset);
                    offset = start;
                }
                // construct arraylist with 0-6 items
                ArrayList<Weather.WeatherInfo> baritems = new ArrayList<Weather.WeatherInfo>();
                for (int j=offset; j<=offset+count; j++){
                    baritems.add(weatherForecasts_hourly.get(j));
                }
                ForecastBitmap forecastBitmap = new ForecastBitmap.Builder()
                        .setWetherInfos(baritems)
                        .setAnticipatedWidth(6)
                        .setWeatherLocation(weatherLocation)
                        .create(context);
                imageView_forecastBar.setImageBitmap(forecastBitmap.getForecastBitmap());
            } else {
                // hide forecast bar when not needed
                imageView_forecastBar.setVisibility(View.GONE);
            }
            if (rise1 == null){
                rise1 = (TextView) view.findViewById(R.id.fcitem_rise1);
                viewHolder.rise1 = rise1;
            }
            if (rise2 == null){
                rise2 = (TextView) view.findViewById(R.id.fcitem_rise2);
                viewHolder.rise2 = rise2;
            }
            if (sunset1 == null){
                sunset1 = (ImageView) view.findViewById(R.id.fcitem_sunet1);
                viewHolder.sunset1 = sunset1;
            }
            if (sunset2 == null){
                sunset2 = (ImageView) view.findViewById(R.id.fcitem_sunet2);
                viewHolder.sunset2 = sunset2;
            }
            if (Weather.usePreciseIsDaytime(weatherLocation) && display_sunrise){
                Astronomy.Riseset riseset = Weather.getRiseset(weatherLocation,weatherInfo.getTimestamp());
                long time_interval_start = weatherInfo.getTimestamp()-Weather.MILLIS_IN_HOUR;
                if (weatherInfo.getForecastType() == Weather.WeatherInfo.ForecastType.HOURS_6){
                    time_interval_start = weatherInfo.getTimestamp()-Weather.MILLIS_IN_HOUR*6;
                }
                if (Weather.isSunriseInIntervalUTC(riseset,time_interval_start,weatherInfo.getTimestamp()) && (Weather.isSunsetInIntervalUTC(riseset,time_interval_start,weatherInfo.getTimestamp()))){
                    // handle rare case that sunrise & sunset are in the same interval, then display sundrise & sunset
                    rise1.setVisibility(View.VISIBLE);
                    rise2.setVisibility(View.VISIBLE);
                    sunset1.setVisibility(View.VISIBLE);
                    // in this case, display no arrow
                    sunset2.setVisibility(View.INVISIBLE);
                    String string_sunrise = context.getResources().getString(R.string.sunrise);
                    String string_sunset = context.getResources().getString(R.string.sunset);
                    string_sunrise = string_sunrise+": "+Weather.toHourMinuteString(Weather.getSunriseInUTC(riseset,weatherInfo.getTimestamp()));
                    string_sunset = string_sunset+": "+Weather.toHourMinuteString(Weather.getSunsetInUTC(riseset,weatherInfo.getTimestamp()));
                    rise1.setText(string_sunrise);
                    rise2.setText(string_sunset);
                } else
                if (Weather.isSunriseInIntervalUTC(riseset,time_interval_start,weatherInfo.getTimestamp())){
                    // when runrise is in the interval, display twilight & sunrise
                    rise1.setVisibility(View.VISIBLE);
                    rise2.setVisibility(View.VISIBLE);
                    sunset1.setVisibility(View.VISIBLE);
                    sunset2.setVisibility(View.VISIBLE);
                    sunset1.setImageBitmap(loadScaledIcon(R.mipmap.arrow_up,SCALE_MINI_ICON));
                    sunset2.setImageBitmap(loadScaledIcon(R.mipmap.sunset,SCALE_MINI_ICON));
                    String string_sunrise = context.getResources().getString(R.string.sunrise);
                    String string_twilight = context.getResources().getString(R.string.twilight);
                    string_sunrise = string_sunrise+": "+Weather.toHourMinuteString(Weather.getSunriseInUTC(riseset,weatherInfo.getTimestamp()));
                    string_twilight = string_twilight+": "+Weather.toHourMinuteString(Weather.getCivilTwilightMorning(riseset, weatherInfo.getTimestamp()));
                    rise1.setText(string_twilight);
                    rise2.setText(string_sunrise);
                } else
                if (Weather.isSunsetInIntervalUTC(riseset,time_interval_start,weatherInfo.getTimestamp())){
                    // when sunset is in the interval, display sunset and twilight
                    rise1.setVisibility(View.VISIBLE);
                    rise2.setVisibility(View.VISIBLE);
                    sunset1.setVisibility(View.VISIBLE);
                    sunset2.setVisibility(View.VISIBLE);
                    sunset1.setImageBitmap(loadScaledIcon(R.mipmap.sunset,SCALE_MINI_ICON));
                    sunset2.setImageBitmap(loadScaledIcon(R.mipmap.arrow_down,SCALE_MINI_ICON));
                    String string_sunset = context.getResources().getString(R.string.sunset);
                    string_sunset = string_sunset+": "+Weather.toHourMinuteString(Weather.getSunsetInUTC(riseset,weatherInfo.getTimestamp()));
                    String string_twilight = context.getResources().getString(R.string.twilight);
                    string_twilight = string_twilight+": "+Weather.toHourMinuteString(Weather.getCivilTwilightEvening(riseset, weatherInfo.getTimestamp()));
                    rise1.setText(string_sunset);
                    rise2.setText(string_twilight);
                } else {
                    rise1.setVisibility(View.INVISIBLE);
                    rise2.setVisibility(View.INVISIBLE);
                    sunset1.setVisibility(View.INVISIBLE);
                    sunset2.setVisibility(View.INVISIBLE);
                }
            } else {
                // hide if sunrise & sunset cannot be calculated (too far north or south)
                rise1.setVisibility(View.INVISIBLE);
                rise2.setVisibility(View.INVISIBLE);
                sunset1.setVisibility(View.INVISIBLE);
                sunset2.setVisibility(View.INVISIBLE);
            }
            if (endofday_bar == null){
                endofday_bar = (View) view.findViewById(R.id.fcitem_endofday_bar);
                viewHolder.endofday_bar = endofday_bar;
            }

            if (isEndOfDay(weatherInfo) && (display_endofday_bar)){
                endofday_bar.setVisibility(View.VISIBLE);
            } else {
                endofday_bar.setVisibility(View.GONE);
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

        private int getLastHourlyForecast(){
            int position = weatherForecasts.size()-1;
            while ((weatherForecasts.get(position).getForecastType()!=Weather.WeatherInfo.ForecastType.ONE_HOUR) && (position>0)){
                position--;
            }
            return position;
        }

        private int getHourlyOffset(int start, int position6h){
            int position = start;
            // calculates corresponding position in hourly forecasts that corresponds to 6-hourly forecasts.
            while (weatherForecasts_hourly.get(position).getTimestamp()<weatherForecasts.get(position6h).getTimestamp()){
                position++;
            }
            return position - 6; // display 6 hours before
        }

        public static String getVisibilityString(Weather.WeatherInfo weatherInfo) {
            if (!weatherInfo.hasVisibility() && !weatherInfo.hasProbVisibilityBelow1km()) {
                return null;
            } else {
                String s = "";
                if (weatherInfo.hasVisibility()) {
                    int v = weatherInfo.getVisibility();
                    if (v >= 10000) {
                        s = s + v / 1000 + " km";
                    } else {
                        s = s + v + " m";
                    }
                }
                if (weatherInfo.hasVisibility() && weatherInfo.hasProbVisibilityBelow1km()) {
                    s = s + ", ";
                }
                if (weatherInfo.hasProbVisibilityBelow1km()) {
                    s = s + "<1km: " + weatherInfo.getProbVisibilityBelow1km() + "%";
                }
                return s;
            }
        }

        private boolean isEndOfDay(Weather.WeatherInfo weatherInfo){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(weatherInfo.getTimestamp());
            if (c.get(Calendar.HOUR_OF_DAY)==0){
                return true;
            }
            return false;
        }

        private int getColorGradient(int position){
            float c_step = (float) (255/weatherForecasts.size());
            int pos = Math.round(c_step * position);
            return pos;
        }

    }
