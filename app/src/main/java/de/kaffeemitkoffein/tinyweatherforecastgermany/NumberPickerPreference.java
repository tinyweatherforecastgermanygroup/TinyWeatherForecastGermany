/*
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
    public final static String[] minValues = {"-35","-30","-25","-20","-15","-10","-5","0"};
    public final static String[] maxValues = {"5","10","15","20","25","30","35","40"};

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
        numberPicker.setMinValue(3);
        numberPicker.setMaxValue(10);
        if (getKey().equals(WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_MIN)){
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(7);
            numberPicker.setDisplayedValues(minValues);
        }
        if (getKey().equals(WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_MAX)){
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(7);
            numberPicker.setDisplayedValues(maxValues);
        }
        numberPicker.setValue(getPersistedInt(10));
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
