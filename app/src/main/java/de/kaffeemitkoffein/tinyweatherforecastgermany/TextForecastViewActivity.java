package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class TextForecastViewActivity extends Activity {

    public final static String TEXTFORECAST_ITEM = "DATA_TEXTFORECAST_ITEM";
    private final static String EMPTY_VALUE = "";

    TextView title;
    TextView date;
    TextView subtitle;
    TextView body;
    ArrayList<TextForecast> textForecasts;
    int itemIndex;

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
            if (textForecast.type == TextForecast.Type.FEATURE){
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textforecastview);
        title = (TextView) findViewById(R.id.textforecastview_title);
        date = (TextView) findViewById(R.id.textforecastview_date);
        subtitle = (TextView) findViewById(R.id.textforecastview_subtitle);
        body = (TextView) findViewById(R.id.textforecastview_body);
        Intent intent = getIntent();
        textForecasts = TextForecasts.getTextForecasts(this);
        itemIndex = 0;
        if (intent.hasExtra(TEXTFORECAST_ITEM)){
            itemIndex = intent.getExtras().getInt(TEXTFORECAST_ITEM,0);
        }
        if (savedInstanceState!=null){
            itemIndex = savedInstanceState.getInt(TEXTFORECAST_ITEM,0);
        }
        displayTextForecast(itemIndex);
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
