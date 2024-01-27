/**
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RawWeatherInfo{

    public final static class Source{
        public static final int UNKNOWN = -1;
        public static final int MOS = 0;
        public static final int DMO = 1;
        public static final int SWSMOS = 2;
        public static final int WARNMOS = 3;

        public String getSourceString(int i){
            switch (i){
                case MOS: return "MOS";
                case DMO: return "DMO";
                case SWSMOS: return "SWSMOS";
                case WARNMOS: return "WARNMOS";
                default: return "?";
            }
        }
    }

    long polling_time;       // polling time from API in millis UTC
    long timestamp;         // currently not used.
    int elements;            // # of elements read
    int source;             // API data source
    Weather.WeatherLocation weatherLocation;
    String timetext;    // text; original timestamp
    String[] timesteps; // millis; UTC stamp for forecast
    String[]  TTT;         // K; temp. 2 m above surface
    String[] E_TTT;       // K; absolute error of TTT
    String[] T5cm;        // K; temp. 5 cm above surface
    String[] Td;          // K; dew point 2m above surface (Taupunkt)
    String[] E_Td;        // K; absolute error Td;
    String[] Tx;          // K; max. temp. during last 12h
    String[] Tn;          // K; min. temp. during last 12h
    String[] TM;          // K; mean temp. during last 12h
    String[] TG;          // K; min. surface temp. at 5 cm within last 12h
    String[] DD;          // km/h; wind direction 0-360°
    String[] E_DD;        // 0-360°; absolute error DD
    String[] FF;          // m/s; wind speed
    String[] E_FF;        // m/s; absolute error wind speed 10m above surface
    String[] FX1;         // m/s; max. wind gust within last hour (Windböen)
    String[] FX3;         // m/s; max. wind gust within last 3h (Windböen)
    String[] FXh;         // m/s; max. wind gust within last 12h (Windböen)
    String[] FXh25;          // %, probability of wind gusts >= 25 km/h within last 12h
    String[] FXh40;          // %, probability of wind gusts >= 40 km/h within last 12h
    String[] FXh55;          // %, probability of wind gusts >= 55 km/h within last 12h
    String[] FX625;          // %, probability of wind gusts >= 25 km/h within last 6h
    String[] FX640;          // %, probability of wind gusts >= 40 km/h within last 6h
    String[] FX655;          // %, probability of wind gusts >= 55 km/h within last 6h
    String[] RR1;            // kg/m² total precipitation during the last hour
    String[] RR1c;           // kg/m², total precipitation during last hour consistent with significant weather
    String[] RRL1c;          // kg/m², total liquid during the last hour consistent with significant weather
    String[] RR3;            // kg/m², total precipitation during last 3h
    String[] RR6;            // kg/m², total precipitation during last 6h
    String[] RR3c;           // kg/m², total precipitation during last 3h consistent with significant weather
    String[] RR6c;           // kg/m², total precipitation during last 6h consistent with significant weather
    String[] RRhc;           // kg/m², total precipitation during last 12h consistent with significant weather
    String[] RRdc;           // kg/m², total precipitation during last 24h consistent with significant weather
    String[] RRS1c;          // kg/m², snow-rain equivalent during last hour
    String[] RRS3c;          // kg/m², snow-rain equivalent during last 3h
    String[] R101;           // %, probability of precipitation > 0.1 mm during the last hour
    String[] R102;           // %, probability of precipitation > 0.2 mm during the last hour
    String[] R103;           // %, probability of precipitation > 0.3 mm during the last hour
    String[] R105;           // %, probability of precipitation > 0.5 mm during the last hour
    String[] R107;           // %, probability of precipitation > 0.7 mm during the last hour
    String[] R110;           // %, probability of precipitation > 1.0 mm during the last hour
    String[] R120;           // %, probability of precipitation > 2.0 mm during the last hour
    String[] R130;           // %, probability of precipitation > 3.0 mm during the last hour
    String[] R150;           // %, probability of precipitation > 5.0 mm during the last hour
    String[] RR1o1;          // %, probability of precipitation > 10.0 mm during the last hour
    String[] RR1w1;          // %, probability of precipitation > 15.0 mm during the last hour
    String[] RR1u1;          // %, probability of precipitation > 25.0 mm during the last hour
    String[] R600;           // %, probability of precipitation > 0.0 mm during last 6h
    String[] Rh00;           // %, probability of precipitation > 0.0 mm during last 12h
    String[] R602;           // %, probability of precipitation > 0.2 mm during last 6h
    String[] Rh02;           // %, probability of precipitation > 0.2 mm during last 12h
    String[] Rd02;           // %, probability of precipitation > 0.2 mm during last 24h
    String[] R610;           // %, probability of precipitation > 1.0 mm during last gh
    String[] Rh10;           // %, probability of precipitation > 1.0 mm during last 12h
    String[] R650;           // %, probability of precipitation > 5.0 mm during last 6h
    String[] Rh50;           // %, probability of precipitation > 5.0 mm during last 12h
    String[] Rd00;           // %, probability of precipitation > 0.0 mm during last 24h
    String[] Rd10;           // %, probability of precipitation > 1.0 mm during last 24h
    String[] Rd50;           // %, probability of precipitation > 5.0 mm during last 24h
    String[] wwPd;           // %; occurrance of any precipitation within the last 24h
    String[] DRR1;           // s, duration of precipitation within the last hour
    String[] wwZ;            // %, occurrence of drizzle within the last hour
    String[] wwZ6;           // %, occurrence of drizzle within the last 6h
    String[] wwZh;           // %, occurrence of drizzle within the last 12h
    String[] wwD;            // %, occurrence of stratiform precipitation within the last hour
    String[] wwD6;           // %, occurrence of stratiform precipitation within the last 6h
    String[] wwDh;           // %, occurrence of stratiform precipitation within the last 12h
    String[] wwC;            // %, occurrence of convective precipitation within the last hour
    String[] wwC6;           // %, occurrence of convective precipitation within the last 6h
    String[] wwCh;           // %, occurrence of convective precipitation within the last 12h
    String[] wwT;            // %, occurrence of thunderstorms within the last hour
    String[] wwT6;           // %, occurrence of thunderstorms within the last 6h
    String[] wwTh;           // %, occurrence of thunderstorms within the last 12h
    String[] wwTd;           // %, occurrence of thunderstorms within the last 24h
    String[] wwL;            // %, occurrence of liquid precipitation within the last hour
    String[] wwL6;           // %, occurrence of liquid precipitation within the last 6h
    String[] wwLh;           // %, occurrence of liquid precipitation within the last 12h
    String[] wwS;            // %, occurrence of solid precipitation within the last hour
    String[] wwS6;           // %, occurrence of solid precipitation within the last 6h
    String[] wwSh;           // %, occurrence of solid precipitation within the last 12h
    String[] wwF;            // %, occurrence of freezing rain within the last hour
    String[] wwF6;           // %, occurrence of freezing rain within the last 6h
    String[] wwFh;           // %, occurrence of freezing rain within the last 12h
    String[] wwP;            // %, occurrence of precipitation within the last hour
    String[] wwP6;           // %, occurrence of precipitation within the last 6h
    String[] wwPh;           // %, occurrence of precipitation within the last 12h
    String[] VV10;           // %, probability visibility below 1 km
    String[] ww;             // significant weather
    String[] ww3;            // significant weather at last 3h
    String[] W1W2;           // weather during last 6h
    String[] WPc11;          // optional significant weather (highest priority) during last hour
    String[] WPc31;          // optional significant weather (highest priority) during last 3h
    String[] WPc61;          // optional significant weather (highest priority) during last 6h
    String[] WPch1;          // optional significant weather (highest priority) during last 12h
    String[] WPcd1;          // optional significant weather (highest priority) during last 24h (?)
    String[] N;              // 0-100% total cloud cover
    String[] Neff;           // %; Effective cloud cover
    String[] N05;            // % cloud cover below 500ft.
    String[] Nl;             // % low cloud cover (lower than 2 km)
    String[] Nm;             // % midlevel cloud cover (2-7 km)
    String[] Nh;             // % high cloud cover (>7 km)
    String[] Nlm;            // % cloud cover low and mid level clouds below  7 km
    String[] H_BsC;          // m; cloud base of convective clouds
    String[] PPPP;           // Pa, surface pressure reduced
    String[] E_PPP;          // Pa, absolute error of PPPP
    String[] RadS1;          // (?undocumented?) kJ/m² ? Short wave radiation balance during the last hour
    String[] RadS3;          // kJ/m²; short wave radiation balance during last 3h
    String[] RRad1;          // kJ/m²; global irradiance within the last hour
    String[] Rad1h;          // kJ/m²; global irradiance
    String[] RadL3;          // kJ/m²; long wave radiation balance during last 3h
    String[] VV;             // m; visibility
    String[] D1;             // s; sunshine duration during last hour
    String[] SunD;           // s; sunshine duration during last day
    String[] SunD3;          // s; sunshine duration during last 3h
    String[] RSunD;          // %; relative sunshine duration last 24h
    String[] PSd00;          // %; probability relative sunshine duration >0% within 24h
    String[] PSd30;          // %; probability relative sunshine duration >30% within 24h
    String[] PSd60;          // %; probability relative sunshine duration >60% within 24h
    String[] wwM;            // %; probability of fog within last hour
    String[] wwM6;           // %; probability of fog within last 6h
    String[] wwMh;           // %; probability of fog within last 12h
    String[] wwMd;           // %; occurrence of fog within the last 24h
    String[] PEvap;       // kg/m²; potential evapotranspiration within the last 24h
    String[] uvHazardIndex;
    // new xml mosmix elements 2023-01-20: Neff, RadS1, RR1

    public RawWeatherInfo(){
        initEmptyValues();
    }

    public RawWeatherInfo(long polling_time, int elements, Weather.WeatherLocation weatherLocation,
                          String timetext, String[] timesteps, String[] TTT, String[] E_TTT, String[] T5cm, String[] Td, String[] E_Td, String[] Tx, String[] Tn, String[] TM, String[] TG,
                          String[] DD, String[] E_DD, String[] FF, String[] E_FF, String[] FX1, String[] FX3, String[] FXh, String[] FXh25, String[] FXh40, String[] FXh55, String[] FX625, String[] FX640,
                          String[] FX655, String[] RR1, String[] RR1c, String[] RRL1c, String[] RR3, String[] RR6, String[] RR3c, String[] RR6c, String[] RRhc, String[] RRdc, String[] RRS1c, String[] RRS3c,
                          String[] R101, String[]  R102, String[]  R103, String[]  R105, String[] R107, String[] R110, String[] R120, String[] R130, String[] R150, String[] RR1o1, String[] RR1w1,
                          String[] RR1u1, String[] R600, String[] Rh00, String[] R602, String[] Rh02, String[] Rd02, String[] R610, String[] Rh10, String[] R650, String[] Rh50, String[] Rd00,
                          String[] Rd10, String[] Rd50, String[] wwPd, String[] DRR1, String[] wwZ, String[] wwZ6, String[] wwZh, String[] wwD, String[] wwD6, String[] wwDh, String[] wwC, String[] wwC6,
                          String[] wwCh, String[] wwT, String[] wwT6, String[] wwTh, String[] wwTd, String[] wwL, String[] wwL6, String[] wwLh, String[] wwS, String[] wwS6, String[] wwSh,
                          String[] wwF, String[] wwF6, String[] wwFh, String[] wwP, String[] wwP6, String[] wwPh, String[] VV10, String[] ww, String[] ww3, String[] W1W2, String[] WPc11, String[] WPc31,
                          String[] WPc61, String[] WPch1, String[] WPcd1, String[] N, String[] Neff, String[] N05, String[] Nl, String[] Nm, String[] Nh, String[] Nlm, String[] H_BsC, String[] PPPP, String[] E_PPP,
                          String[] RadS1, String[] RadS3, String[] RRad1, String[] Rad1h, String[] RadL3, String[] VV, String[] D1, String[] SunD, String[] SunD3, String[] RSunD, String[] PSd00, String[] PSd30, String[] PSd60,
                          String[] wwM, String[] wwM6, String[] wwMh, String[] wwMd, String[] PEvap, String[] uvHazardIndex){
        this.polling_time = polling_time;
        this.elements = elements;
        this.weatherLocation = weatherLocation;
        this.timetext=timetext; this.timesteps=timesteps; this.TTT=TTT; this.E_TTT=E_TTT; this.T5cm=T5cm;this.Td=Td; this.E_Td=E_Td; this.Tx=Tx; this.Tn=Tn; this.TM=TM; this.TG=TG;
        this.DD=DD; this.E_DD=E_DD; this.FF=FF; this.E_FF=E_FF; this.FX1=FX1; this.FX3=FX3; this.FXh=FXh; this.FXh25=FXh25; this.FXh40=FXh40; this.FXh55=FXh55; this.FX625=FX625; this.FX640=FX640;
        this.FX655=FX655; this.RR1=RR1; this.RR1c=RR1c; this.RRL1c=RRL1c; this.RR3=RR3; this.RR6=RR6; this.RR3c=RR3c; this.RR6c=RR6c; this.RRhc=RRhc; this.RRdc=RRdc; this.RRS1c=RRS1c; this.RRS3c=RRS3c;
        this.R101=R101; this.R102=R102; this.R103=R103; this.R105=R105; this.R107=R107; this.R110=R110; this.R120=R120; this.R130=R130; this.R150=R150; this.RR1o1=RR1o1; this.RR1w1=RR1w1;
        this.RR1u1=RR1u1; this.R600=R600;this.Rh00=Rh00;this.R602=R602;this.Rh02=Rh02;this.Rd02=Rd02;this.R610=R610;this.Rh10=Rh10;this.R650=R650;this.Rh50=Rh50;this.Rd00=Rd00;
        this.Rd10=Rd10;this.Rd50=Rd50;this.wwPd=wwPd;this.DRR1=DRR1;this.wwZ=wwZ;this.wwZ6=wwZ6;this.wwZh=wwZh;this.wwD=wwD;this.wwD6=wwD6;this.wwDh=wwDh;this.wwC=wwC;this.wwC6=wwC6;this.wwCh=wwCh;
        this.wwT=wwT;this.wwT6=wwT6;this.wwTh=wwTh;this.wwTd=wwTd;this.wwL=wwL;this.wwL6=wwL6;this.wwLh=wwLh;this.wwS=wwS;this.wwS6=wwS6;this.wwSh=wwSh;this.wwF=wwF;this.wwF6=wwF6;
        this.wwFh=wwFh;this.wwP=wwP;this.wwP6=wwP6;this.wwPh=wwPh;this.VV10=VV10;this.ww=ww;this.ww3=ww3;this.W1W2=W1W2; this.WPc11=WPc11; this.WPc31=WPc31;this.WPc61=WPc61;this.WPch1=WPch1;this.WPcd1=WPcd1;
        this.N=N; this.Neff=Neff; this.N05=N05;this.Nl=Nl;this.Nm=Nm;this.Nh=Nh;this.Nlm=Nlm;this.H_BsC=H_BsC;this.PPPP=PPPP;this.E_PPP=E_PPP; this.RadS1=RadS1; this.RadS3=RadS3;this.RRad1=RRad1;this.Rad1h=Rad1h;this.RadL3=RadL3;
        this.VV=VV;this.D1=D1;this.SunD=SunD;this.SunD3=SunD3;this.RSunD=RSunD;this.PSd00=PSd00;this.PSd30=PSd30;this.PSd60=PSd60;this.wwM=wwM;this.wwM6=wwM6;this.wwMh=wwMh;this.wwMd=wwMd;this.PEvap=PEvap;
        this.uvHazardIndex = uvHazardIndex;
    }

    public RawWeatherInfo copy(){
        return new RawWeatherInfo(polling_time,elements,weatherLocation,timetext, timesteps, TTT, E_TTT, T5cm, Td, E_Td, Tx, Tn, TM, TG, DD, E_DD, FF, E_FF, FX1, FX3, FXh, FXh25, FXh40, FXh55, FX625, FX640, FX655, RR1, RR1c,
                RRL1c, RR3, RR6, RR3c, RR6c, RRhc, RRdc, RRS1c, RRS3c, R101, R102, R103, R105, R107, R110, R120, R130, R150, RR1o1, RR1w1, RR1u1, R600, Rh00, R602, Rh02, Rd02, R610,
                Rh10, R650, Rh50, Rd00, Rd10, Rd50, wwPd, DRR1, wwZ, wwZ6, wwZh, wwD, wwD6, wwDh, wwC, wwC6, wwCh, wwT, wwT6, wwTh, wwTd, wwL, wwL6, wwLh, wwS, wwS6, wwSh, wwF, wwF6,
                wwFh, wwP, wwP6, wwPh, VV10, ww, ww3, W1W2, WPc11, WPc31, WPc61, WPch1, WPcd1, N, Neff, N05, Nl, Nm, Nh, Nlm, H_BsC, PPPP, E_PPP, RadS1, RadS3, RRad1, Rad1h, RadL3, VV, D1, SunD, SunD3,
                RSunD, PSd00, PSd30, PSd60, wwM, wwM6, wwMh, wwMd, PEvap, uvHazardIndex);
    }

    private void initEmptyValues(){
        int DATA_SIZE = Weather.DATA_SIZE;
        timetext = "";
        weatherLocation = new Weather.WeatherLocation();
        elements = 0;
        timesteps = new String[DATA_SIZE];TTT = new String[DATA_SIZE];E_TTT = new String[DATA_SIZE];T5cm = new String[DATA_SIZE];Td = new String[DATA_SIZE];E_Td = new String[DATA_SIZE];Tx = new String[DATA_SIZE];
        Tn = new String[DATA_SIZE];TM = new String[DATA_SIZE];TG = new String[DATA_SIZE];DD = new String[DATA_SIZE];E_DD = new String[DATA_SIZE];FF = new String[DATA_SIZE];E_FF = new String[DATA_SIZE];
        FX1 = new String[DATA_SIZE];FX3 = new String[DATA_SIZE];FXh = new String[DATA_SIZE];FXh25 = new String[DATA_SIZE];FXh40 = new String[DATA_SIZE];FXh55 = new String[DATA_SIZE];FX625 = new String[DATA_SIZE];
        FX640 = new String[DATA_SIZE];FX655 = new String[DATA_SIZE];  RR1 = new String[DATA_SIZE]; RR1c = new String[DATA_SIZE]; RRL1c = new String[DATA_SIZE];RR3 = new String[DATA_SIZE];RR6 = new String[DATA_SIZE];
        RR3c = new String[DATA_SIZE];RR6c = new String[DATA_SIZE];RRhc = new String[DATA_SIZE];RRdc = new String[DATA_SIZE];RRS1c = new String[DATA_SIZE];RRS3c = new String[DATA_SIZE];
        R101 = new String[DATA_SIZE];R102 = new String[DATA_SIZE];R103 = new String[DATA_SIZE];R105 = new String[DATA_SIZE];R107 = new String[DATA_SIZE];R110 = new String[DATA_SIZE];
        R120 = new String[DATA_SIZE];R130 = new String[DATA_SIZE];R150 = new String[DATA_SIZE];RR1o1 = new String[DATA_SIZE];RR1w1 = new String[DATA_SIZE];RR1u1 = new String[DATA_SIZE];R600 = new String[DATA_SIZE];
        Rh00 = new String[DATA_SIZE];R602 = new String[DATA_SIZE];Rh02 = new String[DATA_SIZE];Rd02 = new String[DATA_SIZE];R610 = new String[DATA_SIZE];Rh10 = new String[DATA_SIZE];
        R650 = new String[DATA_SIZE];Rh50 = new String[DATA_SIZE];Rd00 = new String[DATA_SIZE];Rd10 = new String[DATA_SIZE];Rd50 = new String[DATA_SIZE];wwPd = new String[DATA_SIZE];
        DRR1 = new String[DATA_SIZE];wwZ = new String[DATA_SIZE];wwZ6 = new String[DATA_SIZE];wwZh = new String[DATA_SIZE];wwD = new String[DATA_SIZE];wwD6 = new String[DATA_SIZE];
        wwDh = new String[DATA_SIZE];wwC = new String[DATA_SIZE];wwC6 = new String[DATA_SIZE];wwCh = new String[DATA_SIZE];wwT = new String[DATA_SIZE];wwT6 = new String[DATA_SIZE];wwTh = new String[DATA_SIZE];
        wwTd = new String[DATA_SIZE];wwL = new String[DATA_SIZE];wwL6 = new String[DATA_SIZE];wwLh = new String[DATA_SIZE];wwS = new String[DATA_SIZE];wwS6 = new String[DATA_SIZE];
        wwSh = new String[DATA_SIZE];wwF = new String[DATA_SIZE];wwF6 = new String[DATA_SIZE];wwFh = new String[DATA_SIZE];wwP = new String[DATA_SIZE];wwP6 = new String[DATA_SIZE];
        wwPh = new String[DATA_SIZE];VV10 = new String[DATA_SIZE];ww = new String[DATA_SIZE];ww3 = new String[DATA_SIZE];W1W2 = new String[DATA_SIZE];
        WPc11 = new String[DATA_SIZE]; WPc31 = new String[DATA_SIZE];
        WPc61 = new String[DATA_SIZE];WPch1 = new String[DATA_SIZE];WPcd1 = new String[DATA_SIZE]; Neff = new String[DATA_SIZE]; N = new String[DATA_SIZE]; N05 = new String[DATA_SIZE];Nl = new String[DATA_SIZE];
        Nm = new String[DATA_SIZE];Nh = new String[DATA_SIZE];Nlm = new String[DATA_SIZE];H_BsC = new String[DATA_SIZE];PPPP = new String[DATA_SIZE];E_PPP = new String[DATA_SIZE];
        RadS1 = new String[DATA_SIZE]; RadS3 = new String[DATA_SIZE]; RRad1 = new String[DATA_SIZE];Rad1h = new String[DATA_SIZE];RadL3 = new String[DATA_SIZE];VV = new String[DATA_SIZE];D1 = new String[DATA_SIZE];
        SunD = new String[DATA_SIZE];SunD3 = new String[DATA_SIZE];RSunD = new String[DATA_SIZE];PSd00 = new String[DATA_SIZE];PSd30 = new String[DATA_SIZE];PSd60 = new String[DATA_SIZE];
        wwM = new String[DATA_SIZE];wwM6 = new String[DATA_SIZE];wwMh = new String[DATA_SIZE];wwMd = new String[DATA_SIZE];PEvap = new String[DATA_SIZE];
        uvHazardIndex = new String[DATA_SIZE];
    }

    public long[] toLongArray(String[] valuearray){
        long[] result = new long[Weather.DATA_SIZE];
        for (int i=0; i<elements; i++){
            try {
                result[i] = Long.parseLong(valuearray[i]);
            } catch (NumberFormatException e){
                return null;
            }
        }
        return result;
    }

    public double[] toDoubleArray(String[] valuearray){
        double[] result = new double[Weather.DATA_SIZE];
        for (int i=0; i<elements; i++){
            try {
                result[i] = Double.parseDouble(valuearray[i]);
            } catch (NumberFormatException e){
                return null;
            }
        }
        return result;
    }

    public double[] toDoubleArray(String[] valuearray, int start, int end){
        double[] result = new double[Weather.DATA_SIZE];
         for (int i=start; i<=end; i++){
            try {
                result[i] = Double.parseDouble(valuearray[i]);
            } catch (NumberFormatException e){
                return null;
            }
        }
        return result;
    }

    public int[] toIntArray(String[] valuearray){
        int[] result = new int[Weather.DATA_SIZE];
        for (int i=0; i<elements; i++){
            try {
                result[i] = (int) Double.parseDouble(valuearray[i]);
            } catch (NumberFormatException e){
                result[i] = 0;
            }
        }
        return result;
    }

    public long[] getTimeSteps(){
        long[] result = new long[Weather.DATA_SIZE];
        SimpleDateFormat kml_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        kml_dateFormat.setLenient(true);
        for (int i=0; i<elements; i++){
            try {
                Date parse = kml_dateFormat.parse(timesteps[i]);
                result[i] = parse.getTime();
            } catch (Exception e){
                // do nothing
            }
        }
        return result;
    }

    public long[] getTimeSteps(int start, int stop){
        long[] result = new long[Weather.DATA_SIZE];
        SimpleDateFormat kml_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        kml_dateFormat.setLenient(true);
        for (int i=start; i<stop; i++){
            try {
                Date parse = kml_dateFormat.parse(timesteps[i]);
                result[i] = parse.getTime();
            } catch (Exception e){
                // nothing to dd
            }
        }
        return result;
    }


    public int getCurrentForecastPosition(){
        long current_time = Calendar.getInstance().getTimeInMillis();
        long[] timesteps = getTimeSteps();
        int i=0;
        while ((timesteps[i]<current_time) && (i<elements)){
            i++;
        }
        return i;
    }

    public long getCurrentForecastPositionTime(){
        int position = getCurrentForecastPosition();
        if (position<elements){
            long[] timeteps = getTimeSteps();
            return timeteps[position];
        } else {
            return 0;
        }
    }

    public long getTime(int index){
        long[] timesteps = getTimeSteps();
        return timesteps[index];
    }

    public int getNextMidnightAfterCurrentForecastPosition(){
        // construct calendar with next midnight from the current position.
        // Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getTime(getCurrentForecastPosition()));
        calendar.add(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        long next_midnight_utc = calendar.getTimeInMillis();
        int i = 0;
        while ((getTime(i)<next_midnight_utc) && (i<elements)){
            i++;
        }
        return i;
    }

    public int getNext1hPosition(){
        return getCurrentForecastPosition() + 1;
    }

    public int getNext6hPosition(){
        long[] timesteps = getTimeSteps();
        int i = getCurrentForecastPosition();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timesteps[i]);
        int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
        while ((hour_of_day !=0) && (hour_of_day!=6) && (hour_of_day!=12) && (hour_of_day!=18) && (i<elements)){
            calendar.add(Calendar.HOUR_OF_DAY,1);
            hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
            i++;
        }
        return i;
    }

    public int getNext24hPosition(){
        // get a calendar instance for the next midnight position
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        long next_midnight_in_millis = calendar.getTimeInMillis();
        long[] timesteps = getTimeSteps();
        int pos = elements-1;
        while ((pos>0) && (timesteps[pos]>next_midnight_in_millis)){
            pos--;
        }
        return pos;
    }


    public Double getAverageValueDouble(String[] item, int first, int last){
        if (first<0){
            first = 0;
        }
        if (last>elements){
            last = elements;
        }
        double[] itemlist = toDoubleArray(item, first, last);
        if (itemlist!=null){
            double d = 0;
            for (int i=first; i<=last; i++){
                d = d + itemlist[i];
            }
            if ((last-first+1) != 0){
                Double result = (d / (last-first+1));
                return result;
            }
        }
        return null;
    }

    public Integer getAverageValueInt(String[] item, int first, int last){
        if (first<0){
            first = 0;
        }
        if (last>elements){
            last = elements;
        }
        int[] itemlist = toIntArray(item);
        if (itemlist!=null){
            int v = 0;
            for (int i=first; i<=last; i++){
                v = v + itemlist[i];
            }
            if ((last-first+1) != 0){
                return v / (last-first+1);
            }
        }
        return null;
    }

    public Integer getSumInt(String[] item, int first, int last){
        if (first<0){
            first = 0;
        }
        if (last>elements){
            last = elements;
        }
        int[] itemlist = toIntArray(item);
        int result = 0;
        if (itemlist!=null){
            for (int i=first; i<=last; i++){
                result = result + itemlist[i];
            }
            return result;
        }
        return null;
    }

    public Integer getMaxIntValue(String[] item, int first, int last){
        if (first<0){
            first = 0;
        }
        if (last>elements){
            last = elements;
        }
        int[] itemlist = toIntArray(item);
        if (itemlist != null){
            int v = itemlist[0];
            for (int i=first; i<=last; i++){
                if (itemlist[i]>v){
                    v = itemlist[i];
                }
            }
            return v;
        }
        return null;
    }

    public Double getMaxDoubleValue(String[] item, int first, int last){
        if (first<0){
            first = 0;
        }
        if (last>elements){
            last = elements;
        }
        double[] itemlist = toDoubleArray(item,first,last);
        if (itemlist != null){
            double d = itemlist[first];
            for (int i=first; i<=last; i++){
                if (itemlist[i]>d){
                    d = itemlist[i];
                }
            }
            return d;
        }
        return null;
    }

    private Double getMinDoubleValue(String[] item, int first, int last){
        if (first<0){
            first = 0;
        }
        if (last>elements){
            last = elements;
        }
        double[] itemlist = toDoubleArray(item,first,last);
        if (itemlist != null){
            double d = itemlist[first];
            for (int i=first; i<=last; i++){
                if (itemlist[i]<d){
                    d = itemlist[i];
                }
            }
            return d;
        }
        return null;
    }

    public Integer getUVHIValue(boolean[] isDayTimeArray, String[] UVitems, int first, int last){
        int uvValue = -1;
        if (first<0){
            first = 0;
        }
        if (last>elements){
            last = elements;
        }
        int[] itemlist = toIntArray(UVitems);
        if (itemlist!=null){
            for (int i=first; i<=last; i++){
                if (isDayTimeArray[i]){
                    if (itemlist[i]>uvValue){
                        uvValue = itemlist[i];
                    }
                }
            }
        }
        if (uvValue==-1){
            return null;
        };
        return uvValue;
    }

    public Double getAverageTemperature(int first, int last){
        return getAverageValueDouble(TTT,first,last);
    }

    public Double getMinTemperature(int first, int last){
        return getMinDoubleValue(TTT,first,last);
    }

    public Double getMaxTemperature(int first, int last){
        return getMaxDoubleValue(TTT,first,last);
    }

    public Integer getAverageClouds(int first, int last){
        return getAverageValueInt(N,first,last);
    }

    public void addUVHazardIndexData(final long[] uvIndexTimes, final int[] uvIndexValues) {
        long[] timesteps = getTimeSteps();
        for (int i=0; i<timesteps.length; i++){
            this.uvHazardIndex[i]="-1";
            if ((uvIndexTimes!=null) && (uvIndexValues!=null)){
                for (int day=0; day<uvIndexTimes.length; day++){
                    if (WeatherLayer.getFullHourTime(timesteps[i],0,0,WeatherLayer.TZ.UTC)==uvIndexTimes[day]){
                        this.uvHazardIndex[i] = String.valueOf(uvIndexValues[day]);
                    }
                }
            }
        }
    }

    public boolean[] getIsDaytimeArray(final Weather.WeatherLocation weatherLocation){
        long[] timeSteps = getTimeSteps();
        boolean[] result = new boolean[timeSteps.length];
        for (int i=0; i<timeSteps.length; i++){
            result[i] = Weather.isDaytime(weatherLocation,timeSteps[i]);
        }
        return result;
    }



}

