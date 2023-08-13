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

    import android.content.Context;
    import android.graphics.BlendMode;
    import android.graphics.BlendModeColorFilter;
    import android.graphics.Color;
    import android.graphics.PorterDuff;
    import android.graphics.drawable.Drawable;
    import android.os.Build;
    import android.os.Handler;
    import android.os.Looper;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseAdapter;
    import android.widget.LinearLayout;
    import android.widget.RelativeLayout;
    import android.widget.TextView;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.concurrent.Executor;

    public class WeatherWarningAdapter extends BaseAdapter {

        final static int AREAS_COLLAPSED=3;
        final static int AREAS_FULL=999;

        LayoutInflater layoutInflater;
        Context context;
        Executor executor;
        final Handler mainHandler;
        ArrayList<WeatherWarning> weatherWarnings;
        ArrayList<WeatherWarning> localWarnings;
        Weather.WeatherLocation stationLocation;

        public WeatherWarningAdapter(Context context, ArrayList<WeatherWarning> weatherWarnings, Executor executor){
            this.context = context;
            this.weatherWarnings = weatherWarnings;
            this.executor = executor;
            this.stationLocation = WeatherSettings.getSetStationLocation(context);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mainHandler = new Handler(Looper.getMainLooper());
        }

        public void setLocalWarnings(ArrayList<WeatherWarning> localWarnings){
            this.localWarnings = localWarnings;
        }

        private boolean isInLocalWarnings(WeatherWarning warning){
            if (localWarnings!=null){
                for (int i=0; i<localWarnings.size(); i++){
                    if (localWarnings.get(i).identifier.equals(warning.identifier)){
                        return true;
                    }
                }
            }
            return false;
        }

        public static class ViewHolder{
            RelativeLayout warning_item_supermaincontainer;
            LinearLayout warning_item_maincontainer;
            LinearLayout warning_item_line1container;
            TextView warning_item_effective;
            TextView warning_item_event;
            TextView warning_item_status;
            TextView warning_item_msgtype;
            LinearLayout warning_item_line2container;
            TextView warning_item_onset;
            TextView warning_item_untilTxt;
            TextView warning_item_expires;
            LinearLayout warning_item_line3container;
            TextView warning_item_urgency;
            TextView warning_item_severity;
            TextView warning_item_certainty;
            TextView warning_item_areas;
            TextView warning_item_headline;
            TextView warning_item_description;
            TextView warning_item_instruction;
            TextView warning_item_elements;

            public ViewHolder() {
            }
       }

       public static String formatTime(long time){
           SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm:ss");
           return simpleDateFormat.format(new Date(time));
       }

        @Override
        public int getCount() {
            if (weatherWarnings!=null){
                return weatherWarnings.size();
            }
            return 0;
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
            ViewHolder viewHolder = new ViewHolder();
            final WeatherWarning warning = weatherWarnings.get(i);
            view = setWarningViewElements(context,layoutInflater,view,viewGroup, warning,isInLocalWarnings(warning),mainHandler,executor);
            return view;
        }

        public static View setWarningViewElements(Context context, LayoutInflater layoutInflater, View view, ViewGroup viewGroup, final WeatherWarning warning, boolean highlight, final Handler mainHandler, Executor executor) {
            ViewHolder viewHolder = new ViewHolder();
            if (view != null) {
                viewHolder = (ViewHolder) view.getTag();
            } else {
                view = layoutInflater.inflate(R.layout.warning_item, viewGroup, false);
                viewHolder.warning_item_supermaincontainer = (RelativeLayout) view.findViewById(R.id.fcitem_supermaincontainer);
                viewHolder.warning_item_maincontainer = (LinearLayout) view.findViewById(R.id.warning_item_maincontainer);
                viewHolder.warning_item_line1container = (LinearLayout) view.findViewById(R.id.warning_item_line1container);
                viewHolder.warning_item_effective = (TextView) view.findViewById(R.id.warning_item_effective);
                viewHolder.warning_item_event = (TextView) view.findViewById(R.id.warning_item_event);
                viewHolder.warning_item_status = (TextView) view.findViewById(R.id.warning_item_status);
                viewHolder.warning_item_msgtype = (TextView) view.findViewById(R.id.warning_item_msgtype);
                viewHolder.warning_item_line2container = (LinearLayout) view.findViewById(R.id.warning_item_line2container);
                viewHolder.warning_item_onset = (TextView) view.findViewById(R.id.warning_item_onset);
                viewHolder.warning_item_untilTxt = (TextView) view.findViewById(R.id.warning_item_untilTxt);
                viewHolder.warning_item_expires = (TextView) view.findViewById(R.id.warning_item_expires);
                viewHolder.warning_item_line3container = (LinearLayout) view.findViewById(R.id.warning_item_line3container);
                viewHolder.warning_item_urgency = (TextView) view.findViewById(R.id.warning_item_urgency);
                viewHolder.warning_item_severity = (TextView) view.findViewById(R.id.warning_item_severity);
                viewHolder.warning_item_certainty = (TextView) view.findViewById(R.id.warning_item_certainty);
                viewHolder.warning_item_areas = (TextView) view.findViewById(R.id.warning_item_areas);
                viewHolder.warning_item_headline = (TextView) view.findViewById(R.id.warning_item_headline);
                viewHolder.warning_item_description = (TextView) view.findViewById(R.id.warning_item_description);
                viewHolder.warning_item_instruction = (TextView) view.findViewById(R.id.warning_item_instruction);
                viewHolder.warning_item_elements = (TextView) view.findViewById(R.id.warning_item_elements);
                view.setTag(viewHolder);
            }
            int color = warning.getWarningColor();
            float colorFactor = 3.5f;
            if (ThemePicker.GetTheme(context) == R.style.AppTheme_Solarized) {
                colorFactor = 1.2f;
            }
            color = Color.rgb(Math.round(Color.red(color) / colorFactor), Math.round(Color.green(color) / colorFactor), Math.round(Color.blue(color) / colorFactor));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Drawable drawable = context.getDrawable(ThemePicker.getWidgetBackgroundDrawableRessource(context));
                viewHolder.warning_item_maincontainer.setBackground(drawable);
                if (highlight) {
                    if (Build.VERSION.SDK_INT < 29) {
                        // drawable.setColorFilter(Color.argb(96, color, color, color), PorterDuff.Mode.SRC_IN);
                        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    } else {
                        drawable.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_IN));
                    }
                }
            } else {
                if (highlight) {
                    viewHolder.warning_item_maincontainer.setBackgroundColor(color);
                } else {
                    viewHolder.warning_item_maincontainer.setBackgroundColor(MainActivity.getColorFromResource(context, R.attr.colorPrimary));
                }
                viewHolder.warning_item_maincontainer.setBackgroundColor(ThemePicker.getColor(context, ThemePicker.ThemeColor.WIDGETBACKGROUND));
            }
            String line1 = new String();
            if (warning.effective != 0) {
                viewHolder.warning_item_effective.setText(formatTime(warning.effective));
            }
            if (warning.status != null) {
                viewHolder.warning_item_status.setText(warning.status);
            }
            if (warning.msgType != null) {
                viewHolder.warning_item_msgtype.setText(warning.msgType);
            }
            if (warning.event != null) {
                viewHolder.warning_item_event.setText(warning.event);
            }

            if (warning.onset != 0) {
                viewHolder.warning_item_onset.setText(formatTime(warning.onset));
            }
            if (warning.expires != 0) {
                viewHolder.warning_item_expires.setText(formatTime(warning.expires));
                viewHolder.warning_item_untilTxt.setVisibility(View.VISIBLE);
            } else {
                viewHolder.warning_item_untilTxt.setVisibility(View.GONE);
            }
            if (warning.urgency != null) {
                viewHolder.warning_item_urgency.setText(warning.urgency);
                viewHolder.warning_item_urgency.setTextColor(ThemePicker.adaptColorToTheme(context, warning.getWarningColor()));
            }
            if (warning.severity != null) {
                viewHolder.warning_item_severity.setText(warning.severity);
                viewHolder.warning_item_severity.setTextColor(ThemePicker.adaptColorToTheme(context, warning.getWarningColor()));
            }
            if (warning.certainty != null) {
                viewHolder.warning_item_certainty.setText(warning.certainty);
                viewHolder.warning_item_certainty.setTextColor(ThemePicker.adaptColorToTheme(context, warning.getWarningColor()));
            }
            final TextView multiLineTextview = viewHolder.warning_item_areas;
            Runnable longLineRunnable = new Runnable() {
                @Override
                public void run() {
                    String line3 = new String();
                    if (warning.area_names != null) {
                        for (int j = 0; j < warning.area_names.size(); j++) {
                            line3 = line3 + warning.area_names.get(j);
                            if (j < warning.area_names.size() - 1) {
                                line3 = line3 + ", ";
                            }
                        }
                    }
                    final String line3_s = line3;
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            multiLineTextview.setText(">" + line3_s);
                        }
                    });
                }
            };
            executor.execute(longLineRunnable);
            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.warning_item_areas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int max_lines = finalViewHolder.warning_item_areas.getMaxLines();
                    if (max_lines == AREAS_COLLAPSED) {
                        finalViewHolder.warning_item_areas.setMaxLines(999);
                    } else {
                        finalViewHolder.warning_item_areas.setMaxLines(AREAS_COLLAPSED);
                    }
                }
            });
            if (warning.headline != null) {
                viewHolder.warning_item_headline.setVisibility(View.VISIBLE);
                viewHolder.warning_item_headline.setText(warning.headline);
            } else {
                viewHolder.warning_item_headline.setVisibility(View.GONE);
            }
            if (warning.description != null) {
                viewHolder.warning_item_description.setVisibility(View.VISIBLE);
                viewHolder.warning_item_description.setText(warning.description);
            } else {
                viewHolder.warning_item_description.setVisibility(View.GONE);
            }
            if (warning.instruction != null) {
                viewHolder.warning_item_instruction.setVisibility(View.VISIBLE);
                viewHolder.warning_item_instruction.setText(warning.instruction);
            } else {
                viewHolder.warning_item_instruction.setVisibility(View.GONE);
            }
            String parameters = "";
            if ((warning.parameter_names != null) && (warning.parameter_values != null)) {
                for (int j = 0; (j < warning.parameter_names.size()) && (j < warning.parameter_values.size()); j++) {
                    parameters = parameters + warning.parameter_names.get(j) + " " + warning.parameter_values.get(j);
                    if (j < warning.parameter_names.size() - 1) {
                        parameters = parameters + ", ";
                    }
                }
                viewHolder.warning_item_elements.setText(parameters);
            }
            if (parameters.equals("")) {
                viewHolder.warning_item_elements.setVisibility(View.GONE);
            } else {
                viewHolder.warning_item_elements.setVisibility(View.VISIBLE);
            }
            return view;
        }
    }
