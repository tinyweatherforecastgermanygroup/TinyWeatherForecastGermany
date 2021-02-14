package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class TextForecastViewActivity extends Activity {

    public final static String TEXTFORECAST_ITEM = "DATA_TEXTFORECAST_ITEM";
    private final static String EMPTY_VALUE = "";

    ActionBar actionBar;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textforecastview);
        actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);

        title = (TextView) findViewById(R.id.textforecastview_title);
        date = (TextView) findViewById(R.id.textforecastview_date);
        subtitle = (TextView) findViewById(R.id.textforecastview_subtitle);
        body = (TextView) findViewById(R.id.textforecastview_body);
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

    private void displayTextForecast(int position){
        if (position<textForecasts.size()){
            TextForecast textForecast = textForecasts.get(position);
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
