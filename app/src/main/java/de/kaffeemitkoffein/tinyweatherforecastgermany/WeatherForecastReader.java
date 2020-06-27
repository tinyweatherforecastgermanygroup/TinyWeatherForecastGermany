/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020 Pawel Dube
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
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class WeatherForecastReader extends AsyncTask<URL,Void, WeatherCard> {

    private Context context;

    public WeatherForecastReader(Context context){
        this.context = context;
    }

    @Override
    protected WeatherCard doInBackground(URL... urls) {
        for (int i = 0; i < urls.length; i++) {
            Boolean pageloaded = false;
            ArrayList<String> pageContent = new ArrayList<String>();
            if (urls[i] != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urls[i].openStream()));
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        pageContent.add(line);
                        try {
                            line = bufferedReader.readLine();
                        } catch (Exception IOException) {
                            line = null;
                        }
                    }
                    pageloaded = true;
                } catch (Exception e) {
                    pageloaded = false;
                }
                if (pageloaded){
                    try {
                        WeatherCard weatherCard = new WeatherCard(pageContent);
                        return weatherCard;
                    } catch (IllegalArgumentException e){
                        // show an error if the data returned from DWD has an unexpected format
                        Toast.makeText(context,e.getMessage(), Toast.LENGTH_LONG).show();
                        return null;
                    }
                }
       }
        }
        // when the loop finishes with no previous return, this means that we only got 404 page not found errors but
        // no weather forecast data at all.
        return null;
    }

    /**
     * Override this routine to define what to do if obtaining data failed.
     */

    public void onNegativeResult(){
        // do nothing at the moment.
    }

    /**
     * Override this routine to define what to do if obtaining data succeeded.
     *
     * Remember: at this point, the new data is already written to the database and can be
     * accessed via the CardHandler class.
     */

    public void onPositiveResult(){
        // do nothing at the moment.
    }

    public void onPositiveResult(WeatherCard weatherCard){
       onPositiveResult();
    }

    protected void onPostExecute(WeatherCard weatherCard) {
        if (weatherCard == null) {
            onNegativeResult();
        } else {
            // get timestamp
            Calendar calendar = Calendar.getInstance();
            weatherCard.polling_time = calendar.getTimeInMillis();
            // writes the weather data to the database
            WeatherForecastContentProvider weatherForecastContentProvider = new WeatherForecastContentProvider();
            weatherForecastContentProvider.writeWeatherForecast(context,weatherCard);
            onPositiveResult(weatherCard);
        }
    }
}
