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
    import android.graphics.*;
    import android.graphics.drawable.Drawable;
    import android.text.SpannableStringBuilder;
    import android.text.Spanned;
    import android.text.style.ForegroundColorSpan;
    import android.util.Log;
    import android.util.SparseArray;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.*;
    import org.astronomie.info.Astronomy;
    import java.text.DecimalFormat;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.Date;
    import java.util.Objects;

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
        private boolean display_gradient;
        private int display_layout;
        private int display_wind_type;
        private int display_wind_unit;
        private int display_distance_unit;
        private boolean displaySimpleBar;
        private boolean display_wind_arc;
        private int display_wind_arc_perdiod;
        private boolean warnings_disabled;
        private LayoutInflater layoutInflater;

        private ArrayList<WeatherWarning> warnings;

        private final String labelSunrise;
        private final String labelSunset;
        private final String labelTwilight;

        private final SparseArray<Bitmap> bitmapCache = new SparseArray<>();

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
            this.display_gradient = weatherSettings.display_gradient;
            this.display_wind_type = weatherSettings.getWindDisplayType();
            this.display_wind_unit = weatherSettings.getWindDisplayUnit();
            this.display_distance_unit = weatherSettings.getDistanceDisplayUnit();
            this.display_layout = weatherSettings.getDisplayLayout();
            this.displaySimpleBar = weatherSettings.display_simple_bar;
            this.display_wind_arc = weatherSettings.display_wind_arc;
            this.display_wind_arc_perdiod = weatherSettings.getWindArcPeriod();
            this.warnings_disabled = weatherSettings.warnings_disabled;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.labelSunrise = context.getResources().getString(R.string.sunrise);
            this.labelSunset = context.getResources().getString(R.string.sunset);
            this.labelTwilight = context.getResources().getString(R.string.twilight);
        }

        public void setWarnings(ArrayList<WeatherWarning> warnings){
            this.warnings = warnings;
        }

        private final static int SCALE_CONDITION_ICON = 2;
        private final static int SCALE_MINI_ICON = 8;

        private void loadScaledIcon(final ImageView imageView, final int id, final int scale) {
            final Integer key = Objects.hash(id, scale);
            final boolean changed = !key.equals((Integer) imageView.getTag());

            // only change if new image does not match existing image
            if (changed) {
                imageView.setTag(key);
                imageView.post(new Runnable() {
                    @Override
                    public void run() {

                        // only set new image if image has not been changed until Runnable is executed
                        if (key.equals((Integer) imageView.getTag())) {
                            imageView.setTag(key);
                            Bitmap bitmap = bitmapCache.get(key);
                            if (bitmap == null) {
                                bitmap = BitmapFactory.decodeResource(context.getResources(), id, null);
                                bitmapCache.put(key, bitmap);
                            }
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        }

        public void clearBitmapCache() {
            bitmapCache.clear();
            ForecastBitmap.clearBitmapCache();
        }

        private static void setVisibility(final View view, final int visibility) {
            if (view.getVisibility() != visibility) {
                view.setVisibility(visibility);
            }
        }

        private static void switchVisibility(final View view) {
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }


        static class ViewHolder {
            RelativeLayout main_container;
            TextView textView_heading;
            TextView condition_text;
            View iconbar1_view;
            TextView precipitation_textview;
            TextView precipitation_unit_lower;
            ImageView weather_icon;
            TextView textView_temp;
            TextView textView_temphigh;
            TextView textView_templow;
            TextView textView_pressure;
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
            ImageView warningSymbol;
            TextView warningText;
            View endofday_bar;

            public ViewHolder() {
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
            TextView precipitation_unit_lower = null;
            ImageView weather_icon = null;
            TextView textView_temp = null;
            TextView textView_temphigh = null;
            TextView textView_templow = null;
            TextView textView_pressure = null;
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
            ImageView warningSymbol = null;
            TextView warningText = null;
            if (view == null) {
                // view is not available from cache
                newView = true;
                view = this.layoutInflater.inflate(R.layout.forecastitem, viewGroup, false);
                /*
                final View view1 = view;
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        int height = view1.getHeight();
                        ViewGroup.LayoutParams layoutParams = view1.getLayoutParams();
                        layoutParams.height = height;
                        Log.v("TWFG","Height=");
                        view1.setMinimumHeight(height);
                        view1.setLayoutParams(layoutParams);
                    }
                });

                 */
            } else {
                // recycle view information
                viewHolder = (ViewHolder) view.getTag();
                main_container = viewHolder.main_container;
                textView_weathercondition = viewHolder.condition_text;
                textView_heading = viewHolder.textView_heading;
                iconbar1_view = viewHolder.iconbar1_view;
                precipitation_textview = viewHolder.precipitation_textview;
                precipitation_unit_lower = viewHolder.precipitation_unit_lower;
                weather_icon = viewHolder.weather_icon;
                textView_temp = viewHolder.textView_temp;
                textView_temphigh = viewHolder.textView_temphigh;
                textView_templow = viewHolder.textView_templow;
                textView_pressure = viewHolder.textView_pressure;
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
                warningSymbol = viewHolder.warningSymbol;
                warningText = viewHolder.warningText;
            }
            // now fill the item with content
            if (main_container==null) {
                main_container = (RelativeLayout) view.findViewById(R.id.fcitem_maincontainer);
                viewHolder.main_container = main_container;
            }
            Weather.WeatherInfo weatherInfo = weatherForecasts.get(i);
            // optinal color gradient
            if (display_gradient){
                int gradient = getColorGradient(i);
                main_container.setBackgroundColor(Color.argb(96,gradient,gradient,gradient));
            }
            // heading with time of day
            if (textView_heading==null){
                textView_heading = (TextView) view.findViewById(R.id.fcitem_heading);
                viewHolder.textView_heading = textView_heading;
            }
            long six_hours_ago = neededHoursAgo(weatherInfo);
            SimpleDateFormat format1 = new SimpleDateFormat("EE, dd.MM., HH:mm");
            String timetext1 = format1.format(new Date(six_hours_ago));
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
            String timetext2 = format2.format(new Date(weatherInfo.getTimestamp()));
            textView_heading.setText(timetext1+" - "+timetext2);
            // handle warnings
            if (warningSymbol==null){
                warningSymbol = (ImageView) view.findViewById(R.id.fcitem_warningsymbol);
                viewHolder.warningSymbol = warningSymbol;
            }
            if (warningText==null){
                warningText = (TextView) view.findViewById(R.id.fcitem_warningtext);
                viewHolder.warningText = warningText;
                warningText.setVisibility(View.GONE);
            }
            if (!warnings_disabled){
                ArrayList<WeatherWarning> applicableWarnings = getApplicableWarnings(weatherInfo);
                if (applicableWarnings.size()>0){
                    Drawable drawable = warningSymbol.getDrawable();
                    drawable.mutate();
                    drawable.setColorFilter(applicableWarnings.get(0).getWarningColor(), PorterDuff.Mode.MULTIPLY);
                    warningSymbol.setVisibility(View.VISIBLE);
                    setMiniWarningsString(warningText,weatherInfo,applicableWarnings);
                    final View finalWarningText = warningText;
                    warningSymbol.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //switchVisibility(finalWarningText);
                            setVisibility(finalWarningText,View.VISIBLE);
                            finalWarningText.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finalWarningText.setVisibility(View.GONE);
                                };
                            },(long) 6000);
                        }
                    });
                } else {
                    warningSymbol.setVisibility(View.GONE);
                    warningText.setText("");
                }
            } else {
                warningSymbol.setVisibility(View.GONE);
                warningText.setText("");
            }
            // left column
            if (textView_weathercondition==null){
                textView_weathercondition = (TextView) view.findViewById(R.id.fcitem_weatherconditiontext);
                viewHolder.condition_text = textView_weathercondition;
            }
            if (weatherInfo.hasCondition()){
                int weathercondition = weatherInfo.getCondition();
                textView_weathercondition.setText(WeatherCodeContract.getWeatherConditionText(context,weathercondition));
            }
            String precipitation_string = "";
            if (weatherInfo.hasProbPrecipitation()){
                precipitation_string = weatherInfo.getProbPrecipitation()+"% ";
            }
            if (weatherInfo.hasPrecipitation()){
                precipitation_string = precipitation_string +weatherInfo.getPrecipitation();
            }
            if (precipitation_string.equals("")){
                if (iconbar1_view == null){
                    iconbar1_view = view.findViewById(R.id.fcitem_iconbar1);
                    viewHolder.iconbar1_view = iconbar1_view;
                }
                setVisibility(iconbar1_view, View.INVISIBLE);
            } else {
                if (precipitation_textview == null){
                    precipitation_textview = (TextView) view.findViewById(R.id.fcitem_precipitation_text);
                    viewHolder.precipitation_textview = precipitation_textview;
                }
                precipitation_textview.setText(precipitation_string);
            }
            if (precipitation_unit_lower==null){
                precipitation_unit_lower = (TextView) view.findViewById(R.id.fcitem_precipitation_unit_lower);
                viewHolder.precipitation_unit_lower = precipitation_unit_lower;
            }
            precipitation_unit_lower.setText(weatherInfo.getPrecipitationUnitLower());
            // weather probablities icons, sorted by priority
            // clouds
            int index = 0;
            if (weatherInfo.hasClouds()){
                ImageView clouds_view = getSymbolView(view,index,symbols,viewHolder);
                loadScaledIcon(clouds_view, R.mipmap.symbol_cloud, SCALE_MINI_ICON);
                TextView clouds_text = getTextView(view,index,labels,viewHolder);
                clouds_text.setText(weatherInfo.getClouds()+"%");
                index ++;
            }
            if (weatherInfo.hasProbThunderstorms()){
                ImageView lightning_view = getSymbolView(view,index,symbols,viewHolder);
                loadScaledIcon(lightning_view, R.mipmap.symbol_lightning, SCALE_MINI_ICON);
                TextView lightning_text = getTextView(view,index,labels,viewHolder);
                lightning_text.setText(weatherInfo.getProbThunderStorms()+"%");
                index ++;
            }
            if (weatherInfo.hasProbSolidPrecipitation()){
                ImageView solid_view = getSymbolView(view,index,symbols,viewHolder);
                loadScaledIcon(solid_view, R.mipmap.symbol_hail, SCALE_MINI_ICON);
                TextView solid_text = getTextView(view,index,labels,viewHolder);
                solid_text.setText(weatherInfo.getProbSolidPrecipitation()+"%");
                index ++;
            }
            if (weatherInfo.hasProbFreezingRain()){
                ImageView freezingrain_view = getSymbolView(view,index,symbols,viewHolder);
                loadScaledIcon(freezingrain_view, R.mipmap.symbol_freezing_rain, SCALE_MINI_ICON);
                TextView freezingrain_text = getTextView(view,index,labels,viewHolder);
                freezingrain_text.setText(weatherInfo.getProbFreezingRain()+"%");
                index ++;
            }
            if (weatherInfo.hasProbFog()){
                ImageView fog_view = getSymbolView(view,index,symbols,viewHolder);
                loadScaledIcon(fog_view, R.mipmap.symbol_fog, SCALE_MINI_ICON);
                TextView fog_text = getTextView(view,index,labels,viewHolder);
                fog_text.setText(weatherInfo.getProbFog()+"%");
                index ++;
            }
            if (weatherInfo.hasProbDrizzle()){
                ImageView drizzle_view = getSymbolView(view,index,symbols,viewHolder);
                loadScaledIcon(drizzle_view, R.mipmap.symbol_drizzle, SCALE_MINI_ICON);
                TextView drizzle_text = getTextView(view,index,labels,viewHolder);
                drizzle_text.setText(weatherInfo.getProbDrizzle()+"%");
                index ++;
            }
            // make remaining icons invisible
            while (index<6){
                ImageView iv = getSymbolView(view,index,symbols,viewHolder);
                setVisibility(iv, View.INVISIBLE);
                TextView tv = getTextView(view,index,labels,viewHolder);
                setVisibility(tv, View.INVISIBLE);
                index++;
            }
            // weather icon
            if (weather_icon == null){
                weather_icon = (ImageView) view.findViewById(R.id.fcitem_weatherconditionicon);
                viewHolder.weather_icon = weather_icon;
            }
            if (weatherInfo.hasCondition()){
                int weathercondition = weatherInfo.getCondition();
                loadScaledIcon(weather_icon, WeatherCodeContract.getWeatherConditionDrawableResource(weathercondition, weatherInfo.isDaytime(weatherLocation)), SCALE_CONDITION_ICON);
            }
            // right column
            if (textView_temp == null){
                textView_temp = (TextView) view.findViewById(R.id.fcitem_temperature);
                viewHolder.textView_temp = textView_temp;
            }
            if (weatherInfo.hasTemperature()){
                textView_temp.setText(weatherInfo.getTemperatureInCelsiusInt()+"°");
            }
            if (textView_temphigh == null){
                textView_temphigh = (TextView) view.findViewById(R.id.fcitem_temperature_high);
                viewHolder.textView_temphigh = textView_temphigh;
            }
            if (textView_templow == null){
                textView_templow = (TextView) view.findViewById(R.id.fcitem_temperature_low);
                viewHolder.textView_templow = textView_templow;
            }
            if (textView_pressure == null){
                textView_pressure = (TextView) view.findViewById(R.id.fcitem_pressure);
                viewHolder.textView_pressure = textView_pressure;
            }
            if (weatherInfo.hasMaxTemperature()){
                textView_temphigh.setText(weatherInfo.getMaxTemperatureInCelsiusInt()+"°");
            }
            if (weatherInfo.hasMinTemperature()){
                textView_templow.setText(weatherInfo.getMinTemperatureInCelsiusInt()+"°");
            }
            if (weatherInfo.hasPressure()){
                textView_pressure.setText(weatherInfo.getPressure()/100+ " hPa");
            }
            if (imageView_windarrow == null){
                imageView_windarrow = (ImageView) view.findViewById(R.id.fcitem_windarrow);
                viewHolder.imageView_windarrow = imageView_windarrow;
            }
            final StringBuilder windstring = new StringBuilder();
            if (weatherInfo.hasWindDirection()){
                switch (display_wind_type) {
                    case Weather.WindDisplayType.ARROW:
                        setVisibility(imageView_windarrow, View.VISIBLE);
                        if (display_wind_arc){
                            imageView_windarrow.setImageBitmap(Weather.WeatherInfo.getWindForecastTint(weatherInfo.getArrowBitmap(context),getWindForecast(weatherInfo)));
                        } else {
                            imageView_windarrow.setImageBitmap(weatherInfo.getArrowBitmap(context));
                        }
                        break;
                    case Weather.WindDisplayType.BEAUFORT:
                        setVisibility(imageView_windarrow, View.VISIBLE);
                        if (display_wind_arc){
                            imageView_windarrow.setImageBitmap(Weather.WeatherInfo.getWindForecastTint(weatherInfo.getBeaufortBitmap(context),getWindForecast(weatherInfo)));
                        } else {
                            imageView_windarrow.setImageBitmap(weatherInfo.getBeaufortBitmap(context));
                        }
                        break;
                    case Weather.WindDisplayType.TEXT:
                        setVisibility(imageView_windarrow, View.GONE);
                        windstring.append(weatherInfo.getWindDirectionString(context)).append(' ');
                        break;
                }
            }
            if (textView_wind == null){
                textView_wind = (TextView) view.findViewById(R.id.fcitem_wind);
                viewHolder.textView_wind = textView_wind;
            }
            if (weatherInfo.hasWindSpeed()){
                final String windspeed;
                switch (display_wind_unit) {
                    case Weather.WindDisplayUnit.METERS_PER_SECOND:
                        windspeed = String.valueOf(weatherInfo.getWindSpeedInMsInt()) + ' ';
                        break;
                    case Weather.WindDisplayUnit.BEAUFORT:
                        windspeed = String.valueOf(weatherInfo.getWindSpeedInBeaufortInt()) + ' ';
                        break;
                    case Weather.WindDisplayUnit.KILOMETERS_PER_HOUR:
                        windspeed = String.valueOf(weatherInfo.getWindSpeedInKmhInt()) + ' ';
                        break;
                    case Weather.WindDisplayUnit.KNOTS:
                        windspeed = String.valueOf(weatherInfo.getWindSpeedInKnotsInt()) + ' ';
                        break;
                    default:
                        windspeed = "";
                }
                windstring.append(windspeed).append(Weather.getWindUnitString(display_wind_unit));
            }
            if (weatherInfo.hasFlurries()){
                String flurries="";
                switch (display_wind_unit){
                    case Weather.WindDisplayUnit.METERS_PER_SECOND: flurries=String.valueOf(weatherInfo.getFlurriesInMsInt()); break;
                    case Weather.WindDisplayUnit.BEAUFORT: flurries=String.valueOf(weatherInfo.getFlurriesInBeaufortInt()); break;
                    case Weather.WindDisplayUnit.KILOMETERS_PER_HOUR: flurries=String.valueOf(weatherInfo.getFlurriesInKmhInt()); break;
                    case Weather.WindDisplayUnit.KNOTS: flurries=String.valueOf(weatherInfo.getFlurriesInKnotsInt());
                }
                windstring.append(" (").append(flurries).append(") ");
            }
            textView_wind.setText(windstring);
            if (linearLayout_visibility == null){
                linearLayout_visibility = (LinearLayout) view.findViewById(R.id.fcitem_visibility_container);
                viewHolder.linearLayout_visibility = linearLayout_visibility;
            }
            if (textview_visibility == null){
                textview_visibility = (TextView) view.findViewById(R.id.fcitem_visibility);
                viewHolder.textview_visibility = textview_visibility;
            }
            if (display_visibility){
                CharSequence visibility = getVisibilityString(weatherInfo, display_distance_unit);
                if (visibility!=null){
                    setVisibility(linearLayout_visibility, View.VISIBLE);
                    setVisibility(textview_visibility, View.VISIBLE);
                    textview_visibility.setText(visibility);
                } else {
                    setVisibility(linearLayout_visibility, View.GONE);
                    setVisibility(textview_visibility, View.GONE);
                }
            } else {
                setVisibility(linearLayout_visibility, View.GONE);
                setVisibility(textview_visibility, View.GONE);
            }
            if (imageView_forecastBar == null){
                imageView_forecastBar = (ImageView) view.findViewById(R.id.fcitem_forecastbar);
                viewHolder.imageView_forecastBar = imageView_forecastBar;
            }
            // hourly forecast bar, display only if forecast is 6h
            if ((weatherInfo.getForecastType() == Weather.WeatherInfo.ForecastType.HOURS_6) && (display_bar)){
                setVisibility(imageView_forecastBar, View.VISIBLE);
                // calculate offset
                int start  = getLastHourlyForecast();
                int offset = getHourlyOffset(start,i);
                int count = 6;
                if (offset<start){
                    count = 6 - (start-offset);
                    offset = start;
                }
                // construct arraylist with 0-6 items
                ArrayList<Weather.WeatherInfo> baritems = new ArrayList<>();
                for (int j=offset; j<=offset+count; j++){
                    baritems.add(weatherForecasts_hourly.get(j));
                }
                final ForecastBitmap forecastBitmap = new ForecastBitmap.Builder()
                        .setWetherInfos(baritems)
                        .setAnticipatedWidth(6)
                        .setWeatherLocation(weatherLocation)
                        .displaySimpleBar(displaySimpleBar)
                        .setWindDisplayType(display_wind_type)
                        .create(context);
                final ImageView v = imageView_forecastBar;
                final View view1 = view;
                final Long timestamp = System.currentTimeMillis();
                v.setTag(timestamp);
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        if (timestamp.equals((Long) v.getTag())) {
                            v.setImageBitmap(forecastBitmap.getForecastBitmap());
                            // when the forecastbar image was set, re-calculate real size of the listview item and set it
                            v.post(new Runnable() {
                                @Override
                                public void run() {
                                    int height = view1.getHeight();
                                    ViewGroup.LayoutParams layoutParams = view1.getLayoutParams();
                                    layoutParams.height = height;
                                    // view1.setMinimumHeight(height);
                                    view1.setLayoutParams(layoutParams);
                                }
                            });
                        }
                    }
                });
            } else {
                // hide forecast bar when not needed
                setVisibility(imageView_forecastBar, View.GONE);
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
                boolean sunriseInIntervalUTC = Weather.isSunriseInIntervalUTC(riseset, time_interval_start, weatherInfo.getTimestamp());
                boolean sunsetInIntervalUTC = Weather.isSunsetInIntervalUTC(riseset, time_interval_start, weatherInfo.getTimestamp());
                if (sunriseInIntervalUTC && sunsetInIntervalUTC) {
                    // handle rare case that sunrise & sunset are in the same interval, then display sundrise & sunset
                    setVisibility(rise1, View.VISIBLE);
                    setVisibility(rise2, View.VISIBLE);
                    setVisibility(sunset1, View.VISIBLE);
                    // in this case, display no arrow
                    setVisibility(sunset2, View.INVISIBLE);
                    String string_sunrise = labelSunrise + ": " + Weather.toHourMinuteString(Weather.getSunriseInUTC(riseset, weatherInfo.getTimestamp()));
                    String string_sunset = labelSunset + ": " + Weather.toHourMinuteString(Weather.getSunsetInUTC(riseset, weatherInfo.getTimestamp()));
                    rise1.setText(string_sunrise);
                    rise2.setText(string_sunset);
                } else if (sunriseInIntervalUTC) {
                    // when runrise is in the interval, display twilight & sunrise
                    setVisibility(rise1, View.VISIBLE);
                    setVisibility(rise2, View.VISIBLE);
                    setVisibility(sunset1, View.VISIBLE);
                    setVisibility(sunset2, View.VISIBLE);
                    loadScaledIcon(sunset1, R.mipmap.arrow_up, SCALE_MINI_ICON);
                    loadScaledIcon(sunset2, R.mipmap.sunset, SCALE_MINI_ICON);
                    String string_sunrise = labelSunrise + ": " + Weather.toHourMinuteString(Weather.getSunriseInUTC(riseset, weatherInfo.getTimestamp()));
                    String string_twilight = labelTwilight + ": " + Weather.toHourMinuteString(Weather.getCivilTwilightMorning(riseset, weatherInfo.getTimestamp()));
                    rise1.setText(string_twilight);
                    rise2.setText(string_sunrise);
                } else if (sunsetInIntervalUTC) {
                    // when sunset is in the interval, display sunset and twilight
                    setVisibility(rise1, View.VISIBLE);
                    setVisibility(rise2, View.VISIBLE);
                    setVisibility(sunset1, View.VISIBLE);
                    setVisibility(sunset2, View.VISIBLE);
                    loadScaledIcon(sunset1, R.mipmap.sunset, SCALE_MINI_ICON);
                    loadScaledIcon(sunset2, R.mipmap.arrow_down, SCALE_MINI_ICON);
                    String string_sunset = labelSunset + ": " + Weather.toHourMinuteString(Weather.getSunsetInUTC(riseset, weatherInfo.getTimestamp()));
                    String string_twilight = labelTwilight + ": " + Weather.toHourMinuteString(Weather.getCivilTwilightEvening(riseset, weatherInfo.getTimestamp()));
                    rise1.setText(string_sunset);
                    rise2.setText(string_twilight);
                } else {
                    setVisibility(rise1, View.INVISIBLE);
                    setVisibility(rise2, View.INVISIBLE);
                    setVisibility(sunset1, View.INVISIBLE);
                    setVisibility(sunset2, View.INVISIBLE);
                }
            } else {
                // hide if sunrise & sunset cannot be calculated (too far north or south)
                setVisibility(rise1, View.INVISIBLE);
                setVisibility(rise2, View.INVISIBLE);
                setVisibility(sunset1, View.INVISIBLE);
                setVisibility(sunset2, View.INVISIBLE);
            }
            if (endofday_bar == null){
                endofday_bar = view.findViewById(R.id.fcitem_endofday_bar);
                viewHolder.endofday_bar = endofday_bar;
            }

            if (isEndOfDay(weatherInfo) && (display_endofday_bar)){
                setVisibility(endofday_bar, View.VISIBLE);
            } else {
                setVisibility(endofday_bar, View.GONE);
            }
            if (newView){
                view.setTag(viewHolder);
            }
            return view;
        }

        private static ImageView getSymbolView(View view, int pos, ImageView[] symbols, ViewHolder viewHolder) {
            int result = 0;
            switch (pos){
                case 0:
                    result = R.id.fcitem_var1_symbol;
                    break;
                case 1:
                    result = R.id.fcitem_var2_symbol;
                    break;
                case 2:
                    result = R.id.fcitem_var3_symbol;
                    break;
                case 3:
                    result = R.id.fcitem_var4_symbol;
                    break;
                case 4:
                    result = R.id.fcitem_var5_symbol;
                    break;
                case 5:
                    result = R.id.fcitem_var6_symbol;
                    break;
            }
            if (symbols[pos] == null){
                symbols[pos] = (ImageView) view.findViewById(result);
                viewHolder.symbols[pos] = symbols[pos];
            }
            return symbols[pos];
        }

        private static TextView getTextView(View view, int pos, TextView[] labels, ViewHolder viewHolder) {
            int result = 0;
            switch (pos){
                case 0:
                    result = R.id.fcitem_var1_text;
                    break;
                case 1:
                    result = R.id.fcitem_var2_text;
                    break;
                case 2:
                    result = R.id.fcitem_var3_text;
                    break;
                case 3:
                    result = R.id.fcitem_var4_text;
                    break;
                case 4:
                    result = R.id.fcitem_var5_text;
                    break;
                case 5:
                    result = R.id.fcitem_var6_text;
                    break;
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

        public static String formatDistanceNumberToString(double d){
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setMinimumFractionDigits(2);
            decimalFormat.setMaximumFractionDigits(2);
            String r = decimalFormat.format(d);
            return r;
        }

        public static CharSequence getVisibilityCharSequence(Weather.WeatherInfo weatherInfo, int display_distance_unit) {
            if (!weatherInfo.hasVisibility() && !weatherInfo.hasProbVisibilityBelow1km()) {
                return null;
            } else {
                StringBuilder s = new StringBuilder();
                if (weatherInfo.hasVisibility()) {
                    if (display_distance_unit == Weather.DistanceDisplayUnit.METRIC) {
                        int v = weatherInfo.getVisibilityInMetres();
                        if (v >= 10000) {
                            s.append((v / 1000)).append(" km");
                        } else {
                            s.append(v).append(" m");
                        }
                    } else if (display_distance_unit == Weather.DistanceDisplayUnit.NAUTIC) {
                        Double v = weatherInfo.getVisibilityInNauticMiles();
                        final String result;
                        if (v < 1) {
                            result = formatDistanceNumberToString(v);
                        } else {
                            result = String.valueOf(v.intValue());
                        }
                        s.append(result).append(" nm");
                    } else if (display_distance_unit == Weather.DistanceDisplayUnit.IMPERIAL) {
                        Double v = weatherInfo.getVisibilityInMiles();
                        final String result;
                        if (v < 1) {
                            Double yd = weatherInfo.getVisibilityInYards();
                            result = String.valueOf(yd.intValue()) + " yd";
                        } else {
                            result = String.valueOf(v.intValue() + " mi");
                        }
                        s.append(result);
                    }
                }
                return s;
            }
        }



        public static CharSequence getVisibilityBelow1kmCharSequence(Weather.WeatherInfo weatherInfo){
            if (weatherInfo.hasProbVisibilityBelow1km()) {
                StringBuilder s = new StringBuilder();
                s.append("<1km: ");
                s.append(weatherInfo.getProbVisibilityBelow1km()).append("%");
                return s;
            } else {
                return null;
            }
        }

        public static CharSequence getVisibilityString(Weather.WeatherInfo weatherInfo, int display_distance_unit) {
            StringBuilder s = new StringBuilder();
            CharSequence visibility = getVisibilityCharSequence(weatherInfo,display_distance_unit);
            if (visibility!=null) {
                s.append(visibility);
            }
            if (weatherInfo.hasVisibility() && weatherInfo.hasProbVisibilityBelow1km()) {
                s.append(", ");
            }
            CharSequence visibilityProbBelow1km = getVisibilityBelow1kmCharSequence(weatherInfo);
            if (visibilityProbBelow1km!=null){
                s.append(visibilityProbBelow1km);
            }
            return s;
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

        private long neededHoursAgo(Weather.WeatherInfo weatherInfo){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(weatherInfo.getTimestamp());
            if (weatherInfo.getForecastType() == Weather.WeatherInfo.ForecastType.HOURS_6){
                calendar.add(Calendar.HOUR_OF_DAY,-6);
            } else {
                calendar.add(Calendar.HOUR_OF_DAY,-1);
            }
            if (calendar.getTimeInMillis()<Calendar.getInstance().getTimeInMillis()){
                Calendar result = Calendar.getInstance();
                result.set(Calendar.MINUTE,0);
                result.set(Calendar.SECOND,0);
                result.set(Calendar.MILLISECOND,0);
                return result.getTimeInMillis();
            }
            return calendar.getTimeInMillis();
        }

        private ArrayList<Weather.WindData> getWindForecast(Weather.WeatherInfo currentWeatherInfo){
            int number = display_wind_arc_perdiod;
            int currentpos = 0;
            while (currentpos<weatherForecasts_hourly.size() && weatherForecasts_hourly.get(currentpos).getTimestamp()<currentWeatherInfo.getTimestamp()){
                currentpos++;
            }
            ArrayList<Weather.WindData> windData = new ArrayList<Weather.WindData>();
            while (currentpos<weatherForecasts_hourly.size() && number>0){
                windData.add(new Weather.WindData(weatherForecasts_hourly.get(currentpos)));
                number--;
                currentpos++;
            }
            return windData;
        }

        private ArrayList<WeatherWarning> getApplicableWarnings(Weather.WeatherInfo weatherInfo){
            ArrayList<WeatherWarning> applicableWarnings = new ArrayList<WeatherWarning>();
            if (warnings!=null){
                long itemStartTime = neededHoursAgo(weatherInfo);
                long itemStopTime = weatherInfo.getTimestamp();
                for (int i=0; i<warnings.size(); i++){
                    if ((warnings.get(i).onset<=itemStopTime) && (warnings.get(i).expires>=itemStartTime)){
                        applicableWarnings.add(warnings.get(i));
                    }
                }
            }
            return applicableWarnings;
        }

        private void setMiniWarningsString(TextView textView, Weather.WeatherInfo weatherInfo, ArrayList<WeatherWarning> applicableWarnings){
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            long itemStartTime = neededHoursAgo(weatherInfo);
            long itemStopTime = weatherInfo.getTimestamp();
            int textPosition = 0;
            ArrayList<String> alreadyAddedWarnings = new ArrayList<String>();
            for (int i=0; i<applicableWarnings.size(); i++){
                String text = applicableWarnings.get(i).headline;
                if (applicableWarnings.get(i).onset>itemStartTime){
                    text = context.getResources().getString(R.string.from)+" "+simpleDateFormat.format(new Date(applicableWarnings.get(i).onset))+": " + text;
                }
                if (applicableWarnings.get(i).expires<itemStopTime){
                    text = text + " ("+context.getResources().getString(R.string.ends)+" "+simpleDateFormat.format(new Date(applicableWarnings.get(i).expires))+")";
                }
                if (!alreadyAddedWarnings.contains(text)){
                    alreadyAddedWarnings.add(text);
                    spannableStringBuilder.append(text);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(applicableWarnings.get(i).getWarningColor()),textPosition,textPosition+text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    textPosition = textPosition + text.length();
                    String s = System.getProperty("line.separator");
                    if ((s!=null) && (i<applicableWarnings.size()-1)){
                        if (s.length()>0){
                            spannableStringBuilder.append(s);
                            textPosition = textPosition + s.length();
                        }
                    }
                }
            }
            textView.setText(spannableStringBuilder);
        }
    }
