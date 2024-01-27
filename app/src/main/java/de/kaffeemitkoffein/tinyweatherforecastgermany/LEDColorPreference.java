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
import android.graphics.drawable.GradientDrawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LEDColorPreference extends Preference {

    private Context context;

    public LEDColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public LEDColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    private int LEDColor = WeatherSettings.getLEDColorItem(getContext());

    @Override
    protected View onCreateView(ViewGroup parent) {
        setLayoutResource(R.layout.ledcolorpreference);
        View view = super.onCreateView(parent);
        ImageView imageView1 = (ImageView) view.findViewById(R.id.preferenceLEDColor);
        imageView1.setImageDrawable(context.getResources().getDrawable(R.drawable.colorcircle));
        GradientDrawable gradientDrawable = (GradientDrawable) imageView1.getDrawable();
        gradientDrawable.setColor(WeatherSettings.getLEDColor(context));
        if (!isEnabled()){
            TextView textView1 = (TextView) view.findViewById(android.R.id.title);
            textView1.setTextColor(Color.GRAY);
            TextView textView2 = (TextView) view.findViewById(android.R.id.summary);
            textView2.setTextColor(Color.GRAY);
            TextView textView3 = (TextView) view.findViewById(R.id.currentLEDColorText);
            textView3.setTextColor(Color.GRAY);
            imageView1.setAlpha(0.5f);
        }
        return view;
    }

    public void setColorItem(int color){
        LEDColor = color;
        notifyChanged();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
    }

}

