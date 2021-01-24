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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TextForecastListActivity extends Activity {

    TextForecastAdapter textForecastAdapter;
    ListView textforecasts_listview;
    Context context;

    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView numberView = (TextView) view.findViewById(R.id.textforecast_item_number);
            int position = Integer.parseInt(numberView.getText().toString());
            Intent intent = new Intent(context,TextForecastViewActivity.class);
            intent.putExtra(TextForecastViewActivity.TEXTFORECAST_ITEM,position);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textforecastlist);
        context = getApplicationContext();
        // TESTING
        PrivateLog.log(this,"TWF","Testing");
        Executor executor = Executors.newSingleThreadExecutor();
        APIReaders.TextForecastRunnable textForecastRunnable = new APIReaders.TextForecastRunnable(this){
            @Override
            public void onPositiveResult(){
               showList();
            }
            @Override
            public void onNegativeResult(){

            }
        };
        executor.execute(textForecastRunnable);
    }

    private void showList(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<TextForecast> textForecasts = TextForecasts.getTextForecasts(context);
                textForecastAdapter = new TextForecastAdapter(context,textForecasts);
                textforecasts_listview = (ListView) findViewById(R.id.textforecasts_listview);
                textforecasts_listview.setAdapter(textForecastAdapter);
                textforecasts_listview.setOnItemClickListener(clickListener);
            }
        });
    }

}
