/*
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021, 2022, 2023, 2024 Pawel Dube
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
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {

    private NumberPicker numberPicker;
    private int resultNumber;
    private Context context;
    private int minValue = 7;
    private int maxValue = 4;
    private int arcValue = 2;
    public final static String[] minValues  = {"-35","-30","-25","-20","-15","-10","-5","0"};
    public final static String[] maxValues  = {"5","10","15","20","25","30","35","40"};
    public final static String[] arcValues  = {"0","30","60","90","120","150","180","210","240","270","300","330","360"};
    public final static String[] hourValues = {"1","2","3","4","5","6","7","8","9","10","11","12"};

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected View onCreateDialogView() {
        if (context==null){
            context = getContext();
        }
        numberPicker = new NumberPicker(context);
        int defaultPosition = 5;
        numberPicker.setMinValue(3);
        numberPicker.setMaxValue(10);
        if (getKey().equals(WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_MIN)){
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(7);
            numberPicker.setDisplayedValues(minValues);
            defaultPosition = WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_MIN_DEFAULT;
        }
        if (getKey().equals(WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_MAX)){
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(7);
            numberPicker.setDisplayedValues(maxValues);
            defaultPosition = WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_MAX_DEFAULT;
        }
        if (getKey().equals(WeatherSettings.PREF_WIND_DISTANCE_ARC)){
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(12);
            numberPicker.setDisplayedValues(arcValues);
            defaultPosition = WeatherSettings.PREF_WIND_DISTANCE_ARC_DEFAULT;
        }

        if (getKey().equals(WeatherSettings.PREF_WIND_DISTANCE_HOURS)){
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(12);
            //numberPicker.setDisplayedValues(maxValues);
            defaultPosition = WeatherSettings.PREF_WIND_DISTANCE_HOURS_DEFAULT;
        }
        if (getKey().equals(WeatherSettings.PREF_MAX_LOCATIONS_IN_SHARED_WARNINGS)){
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(255);
            //numberPicker.setDisplayedValues(maxValues);
        }
        numberPicker.setValue(getPersistedInt(defaultPosition));
        return numberPicker;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult){
            resultNumber = numberPicker.getValue();
            notifyChanged();
            persistInt(resultNumber);
        }
        super.onDialogClosed(positiveResult);
    }

}
