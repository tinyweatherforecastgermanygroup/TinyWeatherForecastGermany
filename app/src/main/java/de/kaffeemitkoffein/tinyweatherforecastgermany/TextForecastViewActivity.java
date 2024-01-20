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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TextForecastViewActivity extends Activity {

    public final static String TEXTFORECAST_ITEM = "DATA_TEXTFORECAST_ITEM";
    private final static String EMPTY_VALUE = "";

    Context context;
    ActionBar actionBar;
    RelativeLayout mainContainer;
    ScrollView mainScrollView;
    TextView title;
    TextView date;
    TextView subtitle;
    TextView body;
    ArrayList<TextForecast> textForecasts;
    int itemIndex;

    private final static String SIS_ITEMINDEX = "SIS_ITEMINDEX";

    public class ForecastVisibility{
        public boolean titleVisible;
        public boolean dateVisible;
        public boolean subtitleVisible;
        public boolean bodyVisible;

        public ForecastVisibility(){
        }

        public ForecastVisibility(boolean titleVisible, boolean dateVisible, boolean subtitleVisible, boolean bodyVisible){
            this.titleVisible = titleVisible; this.dateVisible = dateVisible; this.subtitleVisible=subtitleVisible; this.bodyVisible=bodyVisible;
        }

        public ForecastVisibility(TextForecast textForecast){
            this.titleVisible = true; this.dateVisible = true; this.subtitleVisible=true; this.bodyVisible=true;
            if (textForecast.type == TextForecasts.Type.FEATURE){
                this.subtitleVisible = false;
                this.dateVisible = false;
            }
            if (textForecast.title==null){
                this.titleVisible = false;
            } else {
                if (textForecast.title.equals("")){
                    this.titleVisible = false;
                }
            }
            if (textForecast.issued_text==null){
                this.dateVisible = false;
            } else {
                if (textForecast.issued_text.equals("")){
                    this.dateVisible = false;
                }
            }
            if (textForecast.subtitle==null){
                this.subtitleVisible = false;
            } else {
                if (textForecast.subtitle.equals("")){
                    this.subtitleVisible = false;
                }
            }
            if (textForecast.issued_text==null){
                this.dateVisible = false;
            } else {
                if (textForecast.issued_text.equals("")){
                    this.dateVisible = false;
                }
            }
        }

        public boolean isTitleVisible(){
            return this.titleVisible;
        }

        public boolean isDateVisible(){
            return this.dateVisible;
        }

        public boolean isSubtitleVisible(){
            return this.subtitleVisible;
        }

        public boolean isBodyVisible(){
            return this.bodyVisible;
        }
    }

    WeatherDetailsActivity.SwipeGestureDetector swipeGestureDetector = new WeatherDetailsActivity.SwipeGestureDetector() {
        @Override
        public boolean onLeftSwipe(View view, MotionEvent motionEvent) {
            if (itemIndex<textForecasts.size()-1){
                itemIndex++;
                displayTextForecast(itemIndex);
            }
            return super.onLeftSwipe(view, motionEvent);
        }

        @Override
        public boolean onRightSwipe(View view, MotionEvent motionEvent) {
            if (itemIndex>0){
                itemIndex--;
                displayTextForecast(itemIndex);
            }
            return super.onRightSwipe(view, motionEvent);
        }

        @Override
        public boolean onUpSwipe(View view, MotionEvent motionEvent) {
            return super.onUpSwipe(view, motionEvent);
        }

        @Override
        public boolean onDownSwipe(View view, MotionEvent motionEvent) {
            return super.onDownSwipe(view, motionEvent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt(SIS_ITEMINDEX,itemIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle restoreInstanceState){
        super.onRestoreInstanceState(restoreInstanceState);
        itemIndex = restoreInstanceState.getInt(SIS_ITEMINDEX,0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        ThemePicker.SetTheme(this);
        super.onCreate(savedInstanceState);
        WeatherSettings.setRotationMode(this);
        setContentView(R.layout.activity_textforecastview);
        actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar_textforecastview);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_HOME_AS_UP);
        title = (TextView) findViewById(R.id.textforecastview_title);
        date = (TextView) findViewById(R.id.textforecastview_date);
        subtitle = (TextView) findViewById(R.id.textforecastview_subtitle);
        body = (TextView) findViewById(R.id.textforecastview_body);
        mainContainer = (RelativeLayout) findViewById(R.id.actionbar_textforecastview_maincontainer);
        mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mainScrollView = (ScrollView) findViewById(R.id.textforecastview_scrollview);
        mainScrollView.setOnTouchListener(swipeGestureDetector);
        Intent intent = getIntent();
        if (WeatherSettings.isTextForecastFilterEnabled(this)){
            textForecasts = TextForecasts.getLatestTextForecastsOnly(this);
        } else {
            textForecasts = TextForecasts.getTextForecasts(this);
        }
        itemIndex = 0;
        if (savedInstanceState!=null){
            itemIndex = savedInstanceState.getInt(SIS_ITEMINDEX,itemIndex);
        } else
        if (intent.hasExtra(TEXTFORECAST_ITEM)){
            itemIndex = intent.getExtras().getInt(TEXTFORECAST_ITEM,0);
        }
        displayTextForecast(itemIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.testforecastview_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int item_id = mi.getItemId();
        if (item_id == R.id.textforecastview_back) {
            if (itemIndex>0){
                itemIndex--;
                displayTextForecast(itemIndex);
            }
            return true;
        }
        if (item_id == R.id.textforecastview_next) {
            if (itemIndex<textForecasts.size()-1){
                itemIndex++;
                displayTextForecast(itemIndex);
            }
            return true;
        }
        return super.onOptionsItemSelected(mi);
    }

    public void updateActionBarLabels(TextForecast textForecast){
        ImageView iconView = (ImageView) findViewById(R.id.actionbar_textforecastview_icon);
        TextView typeView = (TextView) findViewById(R.id.actionbar_textforecastview_type);
        TextView issuedView = (TextView) findViewById(R.id.actionbar_textforecastview_issued);
        if (iconView!=null){
            iconView.setImageDrawable(TextForecasts.getTextForecastDrawable(context,textForecast.type));
        }
        if (typeView!=null){
            typeView.setText(TextForecasts.getTypeString(context,textForecast));
        }
        if (issuedView!=null){
            final SimpleDateFormat simpleDateFormat = Weather.getSimpleDateFormat(Weather.SimpleDateFormats.DETAILED_NO_SECONDS);
            issuedView.setText(simpleDateFormat.format(new Date(textForecast.issued)));
        }
    }

    private void displayTextForecast(int position){
        if (position<textForecasts.size()){
            TextForecast textForecast = textForecasts.get(position);
            updateActionBarLabels(textForecast);
            ForecastVisibility forecastVisibility = new ForecastVisibility(textForecast);
            if (forecastVisibility.isTitleVisible()){
                title.setVisibility(View.VISIBLE);
                title.setText(textForecast.title);
            } else {
                title.setVisibility(View.GONE);
            }
            if (forecastVisibility.isSubtitleVisible()){
                subtitle.setVisibility(View.VISIBLE);
                subtitle.setText(textForecast.subtitle);
            } else {
                subtitle.setVisibility(View.GONE);
            }
            if (forecastVisibility.isDateVisible()){
                date.setVisibility(View.VISIBLE);
                date.setText(textForecast.issued_text);
            } else {
                date.setVisibility(View.GONE);
            }
            if (forecastVisibility.isBodyVisible()){
                body.setVisibility(View.VISIBLE);
                body.setText(textForecast.content);
                if (Build.VERSION.SDK_INT>=26){
                    body.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                }
            } else {
                body.setVisibility(View.GONE);
            }
        }
    }

}
