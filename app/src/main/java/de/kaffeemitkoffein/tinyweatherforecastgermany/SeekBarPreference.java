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
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends Preference {

    private Context context;
    private SeekBar seekBar;
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener;

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    private int progress = 1;
    private int max = 5;

    @Override
    protected View onCreateView(ViewGroup parent) {
        setLayoutResource(R.layout.seekbarpreference);
        View view = super.onCreateView(parent);
        seekBar = (SeekBar) view.findViewById(R.id.seekBarPreference_progress);
        seekBar.setMax(max);
        seekBar.setProgress(progress);
        if (!isEnabled()){
            TextView textView1 = (TextView) view.findViewById(android.R.id.title);
            textView1.setTextColor(Color.GRAY);
            TextView textView2 = (TextView) view.findViewById(android.R.id.summary);
            textView2.setTextColor(Color.GRAY);
            TextView textView3 = (TextView) view.findViewById(R.id.currentLEDColorText);
            textView3.setTextColor(Color.GRAY);
            seekBar.setAlpha(0.5f);
        }
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        return view;
    }

    public void setProgress(int progress){
        this.progress = progress;
        if (seekBar!=null){
            seekBar.setProgress(this.progress);
        }
        notifyChanged();
    }

    public void setMax(int max){
        this.max = max;
        if (seekBar!=null){
            seekBar.setMax(this.max);
        }
        notifyChanged();
    }

    public void setSeekBarChangeListener(SeekBar.OnSeekBarChangeListener seekBarChangeListener){
        this.seekBarChangeListener = seekBarChangeListener;
        if (seekBar!=null){
            seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        }
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
    }

}


