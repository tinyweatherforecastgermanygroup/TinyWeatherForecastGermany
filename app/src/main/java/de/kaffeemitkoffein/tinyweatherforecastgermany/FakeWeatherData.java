package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.util.Log;

import java.util.Calendar;
import java.util.Random;

public class FakeWeatherData {

    private String getRandomMinAboveZero(int value, int bound){
        Random random = new Random();
        int i = value - random.nextInt(bound);
        if  (i < 0) {
            i=0;
        }
        return String.valueOf(i);
    }

    private String getRandomMin(int value, int bound){
        Random random = new Random();
        return String.valueOf(value - random.nextInt(bound));
    }


    private String getRandomMax(int value, int bound){
        Random random = new Random();
        return String.valueOf(value + random.nextInt(bound));
    }

    private String getRandomPrecipitationType(){
        Random random = new Random();
        int i = random.nextInt(2);
        if (i==0) return "R";
        if (i==1) return "S";
        return "SR";
    }

    public WeatherCard getInstance(){
        Random random = new Random();
        int value;
        int min;
        int max;
        String s;
        String seperator;
        WeatherCard weatherCard = new WeatherCard();
        weatherCard.fdat = "FDAT00";
        weatherCard.ortscode = "XXXX";
        weatherCard.zeitstempel = "060812";
        weatherCard.klimagebiet = "Hamburg";
        weatherCard.ausgegeben_am = "Montag, den 08.06.2020 um 12:00 Uhr";
        weatherCard.ausgegeben_von = "dummy weather data";
        for (int i=0; i<9; i++){
            int uhrzeit = i*3 + 12;
            if (uhrzeit>23){
                uhrzeit = uhrzeit - 12;
            }
            weatherCard.uhrzeit[i]   =String.valueOf(uhrzeit);
            value = random.nextInt(10);
            weatherCard.bewoelkung[i]=String.valueOf(value);
            weatherCard.bewoelkung_min[i] = getRandomMinAboveZero(value,3);
            weatherCard.bewoelkung_max[i] = getRandomMax(value,3);
            value = random.nextInt(10);
            s     = getRandomPrecipitationType();
            if (s.length()==1){
                seperator = " ";
            } else {
                seperator = "";
            }
            weatherCard.niederschlag[i]     = s+seperator+String.valueOf(value);
            weatherCard.niederschlag_min[i] = s+seperator+getRandomMinAboveZero(value,3);
            weatherCard.niederschlag_max[i] = s+seperator+getRandomMax(value,3);
            value = random.nextInt(40) - 15;
            weatherCard.lufttemperatur[i]     = String.valueOf(value);
            Log.v("RANDOM","Luft:"+value);
            weatherCard.lufttemperatur_min[i] = getRandomMin(value,3);
            weatherCard.lufttemperatur_max[i] = getRandomMax(value,3);
            value = random.nextInt(2) * 10;
            max   = value + random.nextInt(3) * 10;
            weatherCard.wind[i]    = String.valueOf(value);
            weatherCard.boeen[i]   = String.valueOf(max);
        }
        weatherCard.polling_time = Calendar.getInstance().getTimeInMillis();
        return weatherCard;
    }

}
