/**
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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WelcomeActivity extends Activity {

    RelativeLayout pager;
    LayoutInflater layoutInflater;
    Executor executor;

    int page = 1;
    ImageView dot1;
    ImageView dot2;
    ImageView dot3;
    ImageView dot4;
    ImageView arrow_right;
    ImageView arrow_left;
    TextView skip;

    ArrayAdapter arrayAdapter;
    Spinner spinner;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;

    View result_view;

    private final static String SIS_PAGENUMBER = "PAGENUMBER";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt(SIS_PAGENUMBER,page);
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemePicker.GetTheme(this));
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null){
            Integer i = savedInstanceState.getInt(SIS_PAGENUMBER);
            if (i!=null){
                page = i;
            }
        }
        executor = Executors.newSingleThreadExecutor();
        boolean force_replay = false;
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String action = bundle.getString("mode");
                if (action != null) {
                    if (action.equals("replay")) {
                        force_replay = true;
                    }
                }
            }
        }
        if ((WeatherSettings.isFirstAppLaunch(getApplicationContext())) || (force_replay)) {
            setContentView(R.layout.activity_welcome);
            // action bar layout
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
            pager = (RelativeLayout) findViewById(R.id.welcome_pager);
            skip = (TextView) findViewById(R.id.welcome_skip);
            arrow_left = (ImageView) findViewById(R.id.welcome_arrow_left);
            arrow_left.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            arrow_right = (ImageView) findViewById(R.id.welcome_arrow_right);
            arrow_right.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dot1 = (ImageView) findViewById(R.id.welcome_dot1);
            dot1.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            dot1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 1;
                    setPage(page);
                }
            });
            dot2 = (ImageView) findViewById(R.id.welcome_dot2);
            dot2.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            dot2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 2;
                    setPage(page);
                }
            });
            dot3 = (ImageView) findViewById(R.id.welcome_dot3);
            dot3.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            dot3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 3;
                    setPage(page);
                }
            });
            dot4 = (ImageView) findViewById(R.id.welcome_dot4);
            dot4.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            dot4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 4;
                    setPage(page);
                }
            });
            pager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (page < 4) {
                        page++;
                        setPage(page);
                    } else {
                        //startMainActivityAndShowCircle();
                    }
                }
            });
            setPage(page);
            arrow_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (page < 4) {
                        page++;
                        setPage(page);
                    } else {
                        startMainActivityAndShowCircle();
                    }
                }
            });
            arrow_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (page > 1) {
                        page--;
                        setPage(page);
                    }
                }
            });
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startMainActivityAndShowCircle();
                }
            });
        } else {
            startMainActivity();
        }
    }

    private View setPage(int page) {
        if (result_view!=null){
            pager.removeView(result_view);
            result_view = null;
        }
        if (page == 1) {
            result_view = layoutInflater.inflate(R.layout.welcome_screen1, pager, false);
            pager.addView(result_view);
            dot1.setImageResource(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADIO_BUTTON_CHECKED));
            dot2.setImageResource(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADIO_BUTTON_UNCHECKED));
            dot3.setImageResource(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADIO_BUTTON_UNCHECKED));
            dot4.setImageResource(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADIO_BUTTON_UNCHECKED));
            skip.setTextColor(ThemePicker.getWidgetTextColor(this));
            skip.setTypeface(null,Typeface.NORMAL);
        }
        if (page == 2) {
            result_view = layoutInflater.inflate(R.layout.welcome_screen2, pager, false);
            pager.addView(result_view);
            dot1.setImageResource(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADIO_BUTTON_UNCHECKED));
            dot2.setImageResource(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADIO_BUTTON_CHECKED));
            dot3.setImageResource(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADIO_BUTTON_UNCHECKED));
            dot4.setImageResource(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADIO_BUTTON_UNCHECKED));
            skip.setTextColor(ThemePicker.getWidgetTextColor(this));
            skip.setTypeface(null,Typeface.NORMAL);
            ImageView icon2 = (ImageView) result_view.findViewById(R.id.welcome_screen2_icon2);
            icon2.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            ImageView icon4 = (ImageView) result_view.findViewById(R.id.welcome_screen2_icon4);
            icon4.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            ImageView icon5 = (ImageView) result_view.findViewById(R.id.welcome_screen2_icon5);
            icon5.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            ImageView icon6 = (ImageView) result_view.findViewById(R.id.welcome_screen2_icon6);
            icon6.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            ImageView icon8 = (ImageView) result_view.findViewById(R.id.welcome_screen2_icon8);
            icon8.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            ImageView icon9 = (ImageView) result_view.findViewById(R.id.welcome_screen2_icon9);
            icon9.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            ImageView icon10 = (ImageView) result_view.findViewById(R.id.welcome_screen2_icon10);
            icon10.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
            ImageView icon11 = (ImageView) result_view.findViewById(R.id.welcome_screen2_icon11);
            icon11.setColorFilter(ThemePicker.getColorTextLight(getApplicationContext()), PorterDuff.Mode.SRC_IN);
       }
        if (page == 3) {
            result_view = layoutInflater.inflate(R.layout.welcome_screen3, pager, false);
            pager.addView(result_view);
            dot1.setImageResource(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADIO_BUTTON_UNCHECKED));
            dot2.setImageResource(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADIO_BUTTON_UNCHECKED));
            dot3.setImageResource(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADIO_BUTTON_CHECKED));
            dot4.setImageResource(WeatherIcons.getIconResource(getApplicationContext(),WeatherIcons.RADIO_BUTTON_UNCHECKED));
            skip.setTextColor(ThemePicker.getWidgetTextColor(this));
            skip.setTypeface(null,Typeface.NORMAL);
        }
        if (page == 4) {
            result_view = layoutInflater.inflate(R.layout.welcome_screen4, pager, false);
            pager.addView(result_view);
            dot1.setImageResource(WeatherIcons.getIconResource(getApplicationContext(), WeatherIcons.RADIO_BUTTON_UNCHECKED));
            dot2.setImageResource(WeatherIcons.getIconResource(getApplicationContext(), WeatherIcons.RADIO_BUTTON_UNCHECKED));
            dot3.setImageResource(WeatherIcons.getIconResource(getApplicationContext(), WeatherIcons.RADIO_BUTTON_UNCHECKED));
            dot4.setImageResource(WeatherIcons.getIconResource(getApplicationContext(), WeatherIcons.RADIO_BUTTON_CHECKED));
            skip.setTextColor(Color.GREEN);
            skip.setText(getApplicationContext().getResources().getString(R.string.welcome_ready));
            skip.setTypeface(null, Typeface.BOLD);
            arrayAdapter = new ArrayAdapter(getBaseContext(), R.layout.welcome_dropdownitem, getResources().getTextArray(R.array.display_type_text));
            arrayAdapter.setDropDownViewResource(R.layout.welcome_dropdownitem);
            spinner = (Spinner) result_view.findViewById(R.id.welcome_screen4_spinner);
            spinner.setAdapter(arrayAdapter);
            switch (WeatherSettings.getDisplayType(this)){
                case WeatherSettings.DISPLAYTYPE_1HOUR: spinner.setSelection(0); break;
                case WeatherSettings.DISPLAYTYPE_6HOURS: spinner.setSelection(1); break;
                case WeatherSettings.DISPLAYTYPE_24HOURS: spinner.setSelection(2); break;
                case WeatherSettings.DISPLAYTYPE_MIXED: spinner.setSelection(3);
            }
            MainActivity.SpinnerListener spinnerListener = new MainActivity.SpinnerListener(){
                @Override
                public void handleItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (i){
                        case 0: WeatherSettings.setDisplayType(getApplicationContext(),String.valueOf(WeatherSettings.DISPLAYTYPE_1HOUR)); break;
                        case 1: WeatherSettings.setDisplayType(getApplicationContext(),String.valueOf(WeatherSettings.DISPLAYTYPE_6HOURS)); break;
                        case 2: WeatherSettings.setDisplayType(getApplicationContext(),String.valueOf(WeatherSettings.DISPLAYTYPE_24HOURS)); break;
                        case 3: WeatherSettings.setDisplayType(getApplicationContext(),String.valueOf(WeatherSettings.DISPLAYTYPE_MIXED));
                    }
                    super.handleItemSelected(adapterView, view, i, l);
                }
            };
            spinner.setOnItemSelectedListener(spinnerListener);
            spinner.setOnTouchListener(spinnerListener);
            checkBox1 = result_view.findViewById(R.id.welcome_screen4_check1);
            if (WeatherSettings.getViewModel(getApplicationContext()).equals(WeatherSettings.ViewModel.SIMPLE)){
                checkBox1.setChecked(false);
            } else {
                checkBox1.setChecked(true);
            }
            checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!b){
                        WeatherSettings.setViewModel(getApplicationContext(), WeatherSettings.ViewModel.SIMPLE);
                    } else {
                        WeatherSettings.setViewModel(getApplicationContext(),WeatherSettings.ViewModel.EXTENDED);
                    }
                }
            });
            checkBox2 = result_view.findViewById(R.id.welcome_screen4_check2);
            checkBox2.setChecked(WeatherSettings.getUpdateForecastRegularly(getApplicationContext()));
            checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    WeatherSettings.setUpdateForecastRegularly(getApplicationContext(),b);
                }
            });
            checkBox3 = result_view.findViewById(R.id.welcome_screen4_check3);
            checkBox3.setChecked(WeatherSettings.getDisplaySunrise(getApplicationContext()));
            checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    WeatherSettings.setDisplaySunrise(getApplicationContext(),b);
                }
            });
        }
        if (page == 5) {
            result_view = layoutInflater.inflate(R.layout.welcome_spinner, pager, true);
            dot1.setVisibility(View.GONE);
            dot2.setVisibility(View.GONE);
            dot3.setVisibility(View.GONE);
            dot4.setVisibility(View.GONE);
            arrow_left.setVisibility(View.GONE);
            arrow_right.setVisibility(View.GONE);
            skip.setVisibility(View.GONE);
        }
        return result_view;
    }

    private void startMainActivity() {
        // Intent i = new Intent(this, MainActivity.class);
        Intent i = new Intent(this, LoggingActivity.class);
        WeatherSettings.setAppLaunchedFlag(getApplicationContext());
        startActivity(i);
        finish();
    }

    private void startMainActivityAndShowCircle() {
        setPage(5);
        startMainActivity();
        finish();
    }
}
