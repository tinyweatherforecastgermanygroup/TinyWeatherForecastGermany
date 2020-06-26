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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class StationListReader {

    private final String STATIONSRESOURCE="stations.txt";
    private Context context;

    public StationListReader(Context context){
        this.context = context;
    }

    private String getStationsStringFromResource(){
        // InputStream inputStream = context.getResources().openRawResource(context.getResources().getIdentifier(STATIONSRESOURCE,"raw",context.getApplicationContext().getPackageName()));
        InputStream inputStream = context.getResources().openRawResource(R.raw.stations);
        try {
            int size = inputStream.available();
            byte[] textdata = new byte[size];
            inputStream.read(textdata);
            inputStream.close();
            String text = new String(textdata);
            return text;
        } catch (IOException e) {
            return null;
        }
    }

    public ArrayList<Station> getStations(){
        ArrayList<Station> stations = new ArrayList<Station>();
        String s = getStationsStringFromResource();
        while (s.length()>0){
            // cut out the station separated by ";"
            int pos = s.indexOf(";");
            if (pos != -1){
                // Log.v("ID:","Trying to string: "+pos);
                String station_string = s.substring(0,pos);
                // cut away this station from the main string
                s = s.substring(pos+1);
                // cut out code1 that ends with the 1st ":"
                int station_pos1 = station_string.indexOf(":");
                String code1 = station_string.substring(0,station_pos1);
                station_string = station_string.substring(station_pos1+1);
                // cut out code2 that ends with the 2nd ":"
                int station_pos2 = station_string.indexOf(":");
                String code2 = station_string.substring(0,station_pos2);
                // cut out the station name. This is what remains after the 2nd ":".
                String name = station_string.substring(station_pos2+1);
                Station station = new Station(code1,code2,name);
                stations.add(station);
                // Log.v("ID:",station.getCode1()+"|"+station.getCode2()+"|"+station.getName());
            } else {
                // failsafe if source file is broken or chars occur after last item;
                break;
            }
        }
        Collections.sort(stations, new Station());
        return stations;
    }

    public ArrayList<String> getStationNames(){
        ArrayList<Station> stations = getStations();
        ArrayList<String> names = new ArrayList<String>();
        for (int i=0; i<stations.size(); i++){
            Station station = stations.get(i);
            names.add(station.getName());
        }
        return names;
    }
}
