package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.*;

public final class Weather {

    public class WeatherLocation implements Comparator<WeatherLocation> {
        public String description;
        public String name;
        double latitude;
        double longitude;
        double altitude;

        public WeatherLocation(){
        }

        public WeatherLocation(String description, String name, long latitude, long longitude, long altitude){
            this.description = description;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
        }

        @Override
        public int compare(WeatherLocation s1, WeatherLocation s2) {
            return s1.description.compareTo(s2.description);
        }

    }

    public class WeatherItem{
        long polling_time;
        WeatherLocation location;
        String timetext;    // text; original timestamp
        long timestamp;     // millis; UTC stamp for forecast
        double TTT;         // K; temp. 2 m above surface
        double E_TTT;       // K; absolute error of TTT
        double T5cm;        // K; temp. 5 cm above surface
        double Td;          // K; dew point 2m above surface (Taupunkt)
        double E_Td;        // K; absolute error Td;
        double Tx;          // K; max. temp. during last 12h
        double Tn;          // K; min. temp. during last 12h
        double TM;          // K; mean temp. during last 12h
        double TG;          // K; min. surface temp. at 5 cm within last 12h
        double DD;          // km/h; wind direction 0-360°
        double E_DD;        // 0-360°; absolute error DD
        double FF;          // m/s; wind speed in km/h
        double E_FF;        // m/s; absolute error wind speed 10m above surface
        double FX1;         // m/s; max. wind gust within last hour (Windböen)
        double FX3;         // m/s; max. wind gust within last 3h (Windböen)
        double FXh;         // m/s; max. wind gust within last 12h (Windböen)
        int FXh25;          // %, probability of wind gusts >= 25 km/h within last 12h
        int FXh40;          // %, probability of wind gusts >= 40 km/h within last 12h
        int FXh55;          // %, probability of wind gusts >= 55 km/h within last 12h
        int FX625;          // %, probability of wind gusts >= 25 km/h within last 6h
        int FX640;          // %, probability of wind gusts >= 40 km/h within last 6h
        int FX655;          // %, probability of wind gusts >= 55 km/h within last 6h
        double RR1c;        // kg/m³, total precipitation during last hour consistent with significant weather
        double RRL1c;       // kg/m³, total liquid during the last hour consistent with significant weather
        double RR3;         // kg/m³, total precipitation during last 3h
        double RR6;         // kg/m³, total precipitation during last 6h
        double RR3c;        // kg/m³, total precipitation during last 3h consistent with significant weather
        double RR6c;        // kg/m³, total precipitation during last 6h consistent with significant weather
        double RRhc;        // kg/m³, total precipitation during last 12h consistent with significant weather
        double RRdc;        // kg/m², total precipitation during last 24h consistent with significant weather
        double RRS1c;       // kg/m², snow-rain equivalent during last hour
        double RRS3c;       // kg/m², snow-rain equivalent during last 3h
        int R101;           // %, probability of precipitation > 0.1 mm during the last hour
        int R102;           // %, probability of precipitation > 0.2 mm during the last hour
        int R103;           // %, probability of precipitation > 0.3 mm during the last hour
        int R105;           // %, probability of precipitation > 0.5 mm during the last hour
        int R107;           // %, probability of precipitation > 0.7 mm during the last hour
        int R110;           // %, probability of precipitation > 1.0 mm during the last hour
        int R120;           // %, probability of precipitation > 2.0 mm during the last hour
        int R130;           // %, probability of precipitation > 3.0 mm during the last hour
        int R150;           // %, probability of precipitation > 5.0 mm during the last hour
        int RR1o1;          // %, probability of precipitation > 10.0 mm during the last hour
        int RR1w1;          // %, probability of precipitation > 15.0 mm during the last hour
        int RR1u1;          // %, probability of precipitation > 25.0 mm during the last hour
        int R600;           // %, probability of precipitation > 0.0 mm during last 6h
        int Rh00;           // %, probability of precipitation > 0.0 mm during last 12h
        int R602;           // %, probability of precipitation > 0.2 mm during last 6h
        int Rh02;           // %, probability of precipitation > 0.2 mm during last 12h
        int Rd02;           // %, probability of precipitation > 0.2 mm during last 24h
        int R610;           // %, probability of precipitation > 1.0 mm during last gh
        int Rh10;           // %, probability of precipitation > 1.0 mm during last 12h
        int R650;           // %, probability of precipitation > 5.0 mm during last 6h
        int Rh50;           // %, probability of precipitation > 5.0 mm during last 12h
        int Rd00;           // %, probability of precipitation > 0.0 mm during last 24h
        int Rd10;           // %, probability of precipitation > 1.0 mm during last 24h
        int Rd50;           // %, probability of precipitation > 5.0 mm during last 24h
        int wwPd;           // %; occurrance of any precipitation within the last 24h
        double DRR1;        // s, duration of precipitation within the last hour
        int wwZ;            // %, occurrence of drizzle within the last hour
        int wwZ6;           // %, occurrence of drizzle within the last 6h
        int wwZh;           // %, occurrence of drizzle within the last 12h
        int wwD;            // %, occurrence of stratiform precipitation within the last hour
        int wwD6;           // %, occurrence of stratiform precipitation within the last 6h
        int wwDh;           // %, occurrence of stratiform precipitation within the last 12h
        int wwC;            // %, occurrence of convective precipitation within the last hour
        int wwC6;           // %, occurrence of convective precipitation within the last 6h
        int wwCh;           // %, occurrence of convective precipitation within the last 12h
        int wwT;            // %, occurrence of thunderstorms within the last hour
        int wwT6;           // %, occurrence of thunderstorms within the last 6h
        int wwTh;           // %, occurrence of thunderstorms within the last 12h
        int wwTd;           // %, occurrence of thunderstorms within the last 24h
        int wwL;            // %, occurrence of liquid precipitation within the last hour
        int wwL6;           // %, occurrence of liquid precipitation within the last 6h
        int wwLh;           // %, occurrence of liquid precipitation within the last 12h
        int wwS;            // %, occurrence of solid precipitation within the last hour
        int wwS6;           // %, occurrence of solid precipitation within the last 6h
        int wwSh;           // %, occurrence of solid precipitation within the last 12h
        int wwF;            // %, occurrence of freezing rain within the last hour
        int wwF6;           // %, occurrence of freezing rain within the last 6h
        int wwFh;           // %, occurrence of freezing rain within the last 12h
        int wwP;            // %, occurrence of precipitation within the last hour
        int wwP6;           // %, occurrence of precipitation within the last 6h
        int wwPh;           // %, occurrence of precipitation within the last 12h
        int VV10;           // %, probability visibility below 1 km
        int ww;             // significant weather
        int ww3;            // significant weather at last 3h
        int W1W2;           // weather during last 6h
        int WPc31;          // optional significant weather (highest priority) during last 3h
        int WPc61;          // optional significant weather (highest priority) during last 6h
        int WPch1;          // optional significant weather (highest priority) during last 12h
        int WPcd1;          // optional significant weather (highest priority) during last 24h (?)
        int N;              // 0-100% total cloud cover
        int N05;            // % cloud cover below 500ft.
        int Nl;             // % low cloud cover (lower than 2 km)
        int Nm;             // % midlevel cloud cover (2-7 km)
        int Nh;             // % high cloud cover (>7 km)
        int Nlm;            // % cloud cover low and mid level clouds below  7 km
        double H_BsC;       // m; cloud base of convective clouds
        double PPPP;        // Pa, surface pressure reduced
        double E_PPP;       // Pa, absolute error of PPPP
        double RadS3;       // kJ/m²; short wave radiation balance during last 3h
        double RRad1;       // kJ/m²; global irradiance within the last hour
        double Rad1h;       // kJ/m²; global irradiance
        double RadL3;       // kJ/m²; long wave radiation balance during last 3h (UVA)
        double VV;          // m; visibility
        double D1;          // s; sunshine duration during last hour
        double SunD;        // s; sunshine duration during last day
        double SunD3;       // s; sunshine duration during last 3h
        int RSunD;          // %; relative sunshine duration last 24h
        int PSd00;          // %; probability relative sunshine duration >0% within 24h
        int PSd30;          // %; probability relative sunshine duration >30% within 24h
        int PSd60;          // %; probability relative sunshine duration >60% within 24h
        int wwM;            // %; probability of fog within last hour
        int wwM6;           // %; probability of fog within last 6h
        int wwMh;           // %; probability of fog within last 12h
        int wwMd;           // %; occurrence of fog within the last 24h
        double PEvap;       // kg/m²; potential evapotranspiration within the last 24h
    }

    public final static int DATA_SIZE = 250;

    public class RawWeatherInfo{
        long polling_time;       // polling time from API in millis UTC
        int elements;            // # of elements read
        String description;     // sensor description, usually city name
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
        String[] RR1c;        // kg/m³, total precipitation during last hour consistent with significant weather
        String[] RRL1c;       // kg/m³, total liquid during the last hour consistent with significant weather
        String[] RR3;         // kg/m³, total precipitation during last 3h
        String[] RR6;         // kg/m³, total precipitation during last 6h
        String[] RR3c;        // kg/m³, total precipitation during last 3h consistent with significant weather
        String[] RR6c;        // kg/m³, total precipitation during last 6h consistent with significant weather
        String[] RRhc;        // kg/m³, total precipitation during last 12h consistent with significant weather
        String[] RRdc;        // kg/m², total precipitation during last 24h consistent with significant weather
        String[] RRS1c;       // kg/m², snow-rain equivalent during last hour
        String[] RRS3c;       // kg/m², snow-rain equivalent during last 3h
        String[]  R101;           // %, probability of precipitation > 0.1 mm during the last hour
        String[]  R102;           // %, probability of precipitation > 0.2 mm during the last hour
        String[]  R103;           // %, probability of precipitation > 0.3 mm during the last hour
        String[]  R105;           // %, probability of precipitation > 0.5 mm during the last hour
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
        String[] WPc31;          // optional significant weather (highest priority) during last 3h
        String[] WPc61;          // optional significant weather (highest priority) during last 6h
        String[] WPch1;          // optional significant weather (highest priority) during last 12h
        String[] WPcd1;          // optional significant weather (highest priority) during last 24h (?)
        String[] N;              // 0-100% total cloud cover
        String[] N05;            // % cloud cover below 500ft.
        String[] Nl;             // % low cloud cover (lower than 2 km)
        String[] Nm;             // % midlevel cloud cover (2-7 km)
        String[] Nh;             // % high cloud cover (>7 km)
        String[] Nlm;            // % cloud cover low and mid level clouds below  7 km
        String[] H_BsC;       // m; cloud base of convective clouds
        String[] PPPP;        // Pa, surface pressure reduced
        String[] E_PPP;       // Pa, absolute error of PPPP
        String[] RadS3;       // kJ/m²; short wave radiation balance during last 3h
        String[] RRad1;       // kJ/m²; global irradiance within the last hour
        String[] Rad1h;       // kJ/m²; global irradiance
        String[] RadL3;       // kJ/m²; long wave radiation balance during last 3h (UVA)
        String[] VV;          // m; visibility
        String[] D1;          // s; sunshine duration during last hour
        String[] SunD;        // s; sunshine duration during last day
        String[] SunD3;       // s; sunshine duration during last 3h
        String[] RSunD;          // %; relative sunshine duration last 24h
        String[] PSd00;          // %; probability relative sunshine duration >0% within 24h
        String[] PSd30;          // %; probability relative sunshine duration >30% within 24h
        String[] PSd60;          // %; probability relative sunshine duration >60% within 24h
        String[] wwM;            // %; probability of fog within last hour
        String[] wwM6;           // %; probability of fog within last 6h
        String[] wwMh;           // %; probability of fog within last 12h
        String[] wwMd;           // %; occurrence of fog within the last 24h
        String[] PEvap;       // kg/m²; potential evapotranspiration within the last 24h

        public RawWeatherInfo(){
            initEmptyValues();
        }

        public RawWeatherInfo(long polling_time, int elements, String description,
                String timetext, String[] timesteps, String[]  TTT, String[] E_TTT, String[] T5cm, String[] Td, String[] E_Td, String[] Tx, String[] Tn, String[] TM, String[] TG,
                String[] DD, String[] E_DD, String[] FF, String[] E_FF, String[] FX1, String[] FX3, String[] FXh, String[] FXh25, String[] FXh40, String[] FXh55, String[] FX625, String[] FX640,
                String[] FX655, String[] RR1c, String[] RRL1c, String[] RR3, String[] RR6, String[] RR3c, String[] RR6c, String[] RRhc, String[] RRdc, String[] RRS1c, String[] RRS3c,
                String[] R101, String[]  R102, String[]  R103, String[]  R105, String[] R107, String[] R110, String[] R120, String[] R130, String[] R150, String[] RR1o1, String[] RR1w1,
                String[] RR1u1, String[] R600, String[] Rh00, String[] R602, String[] Rh02, String[] Rd02, String[] R610, String[] Rh10, String[] R650, String[] Rh50, String[] Rd00,
                String[] Rd10, String[] Rd50, String[] wwPd, String[] DRR1, String[] wwZ, String[] wwZ6, String[] wwZh, String[] wwD, String[] wwD6, String[] wwDh, String[] wwC, String[] wwC6,
                String[] wwCh, String[] wwT, String[] wwT6, String[] wwTh, String[] wwTd, String[] wwL, String[] wwL6, String[] wwLh, String[] wwS, String[] wwS6, String[] wwSh,
                String[] wwF, String[] wwF6, String[] wwFh, String[] wwP, String[] wwP6, String[] wwPh, String[] VV10, String[] ww, String[] ww3, String[] W1W2, String[] WPc31,
                String[] WPc61, String[] WPch1, String[] WPcd1, String[] N, String[] N05, String[] Nl, String[] Nm, String[] Nh, String[] Nlm, String[] H_BsC, String[] PPPP, String[] E_PPP,
                String[] RadS3, String[] RRad1, String[] Rad1h, String[] RadL3, String[] VV, String[] D1, String[] SunD, String[] SunD3, String[] RSunD, String[] PSd00, String[] PSd30, String[] PSd60,
                String[] wwM, String[] wwM6, String[] wwMh, String[] wwMd, String[] PEvap){
            this.polling_time = polling_time;
            this.elements = elements;
            this.description = description;
            this.timetext=timetext; this.timesteps=timesteps; this.TTT=TTT; this.E_TTT=E_TTT; this.T5cm=T5cm;this.Td=Td; this.E_Td=E_Td; this.Tx=Tx; this.Tn=Tn; this.TM=TM; this.TG=TG;
            this.DD=DD; this.E_DD=E_DD; this.FF=FF; this.E_FF=E_FF; this.FX1=FX1; this.FX3=FX3; this.FXh=FXh; this.FXh25=FXh25; this.FXh40=FXh40; this.FXh55=FXh55; this.FX625=FX625; this.FX640=FX640;
            this.FX655=FX655; this.RR1c=RR1c; this.RRL1c=RRL1c; this.RR3=RR3; this.RR6=RR6; this.RR3c=RR3c; this.RR6c=RR6c; this.RRhc=RRhc; this.RRdc=RRdc; this.RRS1c=RRS1c; this.RRS3c=RRS3c;
            this.R101=R101; this.R102=R102; this.R103=R103; this.R105=R105; this.R107=R107; this.R110=R110; this.R120=R120; this.R130=R130; this.R150=R150; this.RR1o1=RR1o1; this.RR1w1=RR1w1;
            this.RR1u1=RR1u1; this.R600=R600;this.Rh00=Rh00;this.R602=R602;this.Rh02=Rh02;this.Rd02=Rd02;this.R610=R610;this.Rh10=Rh10;this.R650=R650;this.Rh50=Rh50;this.Rd00=Rd00;
            this.Rd10=Rd10;this.Rd50=Rd50;this.wwPd=wwPd;this.DRR1=DRR1;this.wwZ=wwZ;this.wwZ6=wwZ6;this.wwZh=wwZh;this.wwD=wwD;this.wwD6=wwD6;this.wwDh=wwDh;this.wwC=wwC;this.wwC6=wwC6;this.wwCh=wwCh;
            this.wwT=wwT;this.wwT6=wwT6;this.wwTh=wwTh;this.wwTd=wwTd;this.wwL=wwL;this.wwL6=wwL6;this.wwLh=wwLh;this.wwS=wwS;this.wwS6=wwS6;this.wwSh=wwSh;this.wwF=wwF;this.wwF6=wwF6;
            this.wwFh=wwFh;this.wwP=wwP;this.wwP6=wwP6;this.wwPh=wwPh;this.VV10=VV10;this.ww=ww;this.ww3=ww3;this.W1W2=W1W2;this.WPc31=WPc31;this.WPc61=WPc61;this.WPch1=WPch1;this.WPcd1=WPcd1;
            this.N=N;this.N05=N05;this.Nl=Nl;this.Nm=Nm;this.Nh=Nh;this.Nlm=Nlm;this.H_BsC=H_BsC;this.PPPP=PPPP;this.E_PPP=E_PPP;this.RadS3=RadS3;this.RRad1=RRad1;this.Rad1h=Rad1h;this.RadL3=RadL3;
            this.VV=VV;this.D1=D1;this.SunD=SunD;this.SunD3=SunD3;this.RSunD=RSunD;this.PSd00=PSd00;this.PSd30=PSd30;this.PSd60=PSd60;this.wwM=wwM;this.wwM6=wwM6;this.wwMh=wwMh;this.wwMd=wwMd;this.PEvap=PEvap;
        }

        public RawWeatherInfo copy(){
            return new RawWeatherInfo(polling_time,elements,description,timetext, timesteps, TTT, E_TTT, T5cm, Td, E_Td, Tx, Tn, TM, TG, DD, E_DD, FF, E_FF, FX1, FX3, FXh, FXh25, FXh40, FXh55, FX625, FX640, FX655, RR1c,
                    RRL1c, RR3, RR6, RR3c, RR6c, RRhc, RRdc, RRS1c, RRS3c, R101, R102, R103, R105, R107, R110, R120, R130, R150, RR1o1, RR1w1, RR1u1, R600, Rh00, R602, Rh02, Rd02, R610,
                    Rh10, R650, Rh50, Rd00, Rd10, Rd50, wwPd, DRR1, wwZ, wwZ6, wwZh, wwD, wwD6, wwDh, wwC, wwC6, wwCh, wwT, wwT6, wwTh, wwTd, wwL, wwL6, wwLh, wwS, wwS6, wwSh, wwF, wwF6,
                    wwFh, wwP, wwP6, wwPh, VV10, ww, ww3, W1W2, WPc31, WPc61, WPch1, WPcd1, N, N05, Nl, Nm, Nh, Nlm, H_BsC, PPPP, E_PPP, RadS3, RRad1, Rad1h, RadL3, VV, D1, SunD, SunD3,
                    RSunD, PSd00, PSd30, PSd60, wwM, wwM6, wwMh, wwMd, PEvap);
        }

        private void initEmptyValues(){
            timetext = "";
            description = "";
            elements = 0;
            timesteps = new String[DATA_SIZE];TTT = new String[DATA_SIZE];E_TTT = new String[DATA_SIZE];T5cm = new String[DATA_SIZE];Td = new String[DATA_SIZE];E_Td = new String[DATA_SIZE];Tx = new String[DATA_SIZE];
            Tn = new String[DATA_SIZE];TM = new String[DATA_SIZE];TG = new String[DATA_SIZE];DD = new String[DATA_SIZE];E_DD = new String[DATA_SIZE];FF = new String[DATA_SIZE];E_FF = new String[DATA_SIZE];
            FX1 = new String[DATA_SIZE];FX3 = new String[DATA_SIZE];FXh = new String[DATA_SIZE];FXh25 = new String[DATA_SIZE];FXh40 = new String[DATA_SIZE];FXh55 = new String[DATA_SIZE];FX625 = new String[DATA_SIZE];
            FX640 = new String[DATA_SIZE];FX655 = new String[DATA_SIZE];RR1c = new String[DATA_SIZE];RRL1c = new String[DATA_SIZE];RR3 = new String[DATA_SIZE];RR6 = new String[DATA_SIZE];
            RR3c = new String[DATA_SIZE];RR6c = new String[DATA_SIZE];RRhc = new String[DATA_SIZE];RRdc = new String[DATA_SIZE];RRS1c = new String[DATA_SIZE];RRS3c = new String[DATA_SIZE];
            R101 = new String[DATA_SIZE];R102 = new String[DATA_SIZE];R103 = new String[DATA_SIZE];R105 = new String[DATA_SIZE];R107 = new String[DATA_SIZE];R110 = new String[DATA_SIZE];
            R120 = new String[DATA_SIZE];R130 = new String[DATA_SIZE];R150 = new String[DATA_SIZE];RR1o1 = new String[DATA_SIZE];RR1w1 = new String[DATA_SIZE];RR1u1 = new String[DATA_SIZE];R600 = new String[DATA_SIZE];
            Rh00 = new String[DATA_SIZE];R602 = new String[DATA_SIZE];Rh02 = new String[DATA_SIZE];Rd02 = new String[DATA_SIZE];R610 = new String[DATA_SIZE];Rh10 = new String[DATA_SIZE];
            R650 = new String[DATA_SIZE];Rh50 = new String[DATA_SIZE];Rd00 = new String[DATA_SIZE];Rd10 = new String[DATA_SIZE];Rd50 = new String[DATA_SIZE];wwPd = new String[DATA_SIZE];
            DRR1 = new String[DATA_SIZE];wwZ = new String[DATA_SIZE];wwZ6 = new String[DATA_SIZE];wwZh = new String[DATA_SIZE];wwD = new String[DATA_SIZE];wwD6 = new String[DATA_SIZE];
            wwDh = new String[DATA_SIZE];wwC = new String[DATA_SIZE];wwC6 = new String[DATA_SIZE];wwCh = new String[DATA_SIZE];wwT = new String[DATA_SIZE];wwT6 = new String[DATA_SIZE];wwTh = new String[DATA_SIZE];
            wwTd = new String[DATA_SIZE];wwL = new String[DATA_SIZE];wwL6 = new String[DATA_SIZE];wwLh = new String[DATA_SIZE];wwS = new String[DATA_SIZE];wwS6 = new String[DATA_SIZE];
            wwSh = new String[DATA_SIZE];wwF = new String[DATA_SIZE];wwF6 = new String[DATA_SIZE];wwFh = new String[DATA_SIZE];wwP = new String[DATA_SIZE];wwP6 = new String[DATA_SIZE];
            wwPh = new String[DATA_SIZE];VV10 = new String[DATA_SIZE];ww = new String[DATA_SIZE];ww3 = new String[DATA_SIZE];W1W2 = new String[DATA_SIZE];WPc31 = new String[DATA_SIZE];
            WPc61 = new String[DATA_SIZE];WPch1 = new String[DATA_SIZE];WPcd1 = new String[DATA_SIZE];N = new String[DATA_SIZE];N05 = new String[DATA_SIZE];Nl = new String[DATA_SIZE];
            Nm = new String[DATA_SIZE];Nh = new String[DATA_SIZE];Nlm = new String[DATA_SIZE];H_BsC = new String[DATA_SIZE];PPPP = new String[DATA_SIZE];E_PPP = new String[DATA_SIZE];
            RadS3 = new String[DATA_SIZE];RRad1 = new String[DATA_SIZE];Rad1h = new String[DATA_SIZE];RadL3 = new String[DATA_SIZE];VV = new String[DATA_SIZE];D1 = new String[DATA_SIZE];
            SunD = new String[DATA_SIZE];SunD3 = new String[DATA_SIZE];RSunD = new String[DATA_SIZE];PSd00 = new String[DATA_SIZE];PSd30 = new String[DATA_SIZE];PSd60 = new String[DATA_SIZE];
            wwM = new String[DATA_SIZE];wwM6 = new String[DATA_SIZE];wwMh = new String[DATA_SIZE];wwMd = new String[DATA_SIZE];PEvap = new String[DATA_SIZE];
        }

        public long[] toLongArray(String[] valuearray){
            long[] result = new long[DATA_SIZE];
            for (int i=0; i<elements; i++){
                result[i] = Long.parseLong(valuearray[i]);
            }
            return result;
        }

        public double[] toDoubleArray(String[] valuearray){
            double[] result = new double[DATA_SIZE];
            for (int i=0; i<elements; i++){
                result[i] = Double.parseDouble(valuearray[i]);
            }
            return result;
        }

        public int[] toIntArray(String[] valuearray){
            int[] result = new int[DATA_SIZE];
            for (int i=0; i<elements; i++){
                result[i] = Integer.parseInt(valuearray[i]);
            }
            return result;
        }

        public long[] getTimeSteps(){
            long[] result = new long[DATA_SIZE];
            SimpleDateFormat kml_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'");
            for (int i=0; i<elements; i++){
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
            long[] timeteps = getTimeSteps();
            int i=0;
            while (timeteps[i]<current_time){
                i++;
            }
            return i;
        }

        public long getTime(int index){
            long[] timeteps = getTimeSteps();
            return timeteps[index];
        }

        public int getNextMidnightAfterCurrentForecastPosition(){
            // construct calendar with next midnight from the current position.
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(getTime(getCurrentForecastPosition()));
            calendar.add(Calendar.DAY_OF_MONTH,1);
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            long next_midnight_utc = calendar.getTimeInMillis();
            int i = 0;
            while (getTime(i)<next_midnight_utc){
                i++;
            }
            return i;
        }

        public int getAverageTemperature(int first, int last){
            int[] temperature = toIntArray(TTT);
            int v = 0;
            for (int i=first; i<=last; i++){
                v = v + temperature[i];
            }
            return v / (last+first+1);
        }

        public int getMinTemperature(int first, int last){
            int[] temperature = toIntArray(TTT);
            int v = temperature[0];
            for (int i=first; i<=last; i++){
                if (temperature[i]<v){
                    v = temperature[i];
                }
            }
            return v;
        }

        public int getMaxTemperature(int first, int last){
            int[] temperature = toIntArray(TTT);
            int v = temperature[0];
            for (int i=first; i<=last; i++){
                if (temperature[i]>v){
                    v = temperature[i];
                }
            }
            return v;
        }

    }

    public static class WeatherInfo{
        long timestamp;
        int condition_code;
        double temperature;
        double temperature_high;
        double temperature_low;
        double wind_speed;
        double wind_direction;
        double flurries;
        double precipitation;
        int clouds;
        int prob_thunderstorms;
        int prob_precipitation;
        int prob_solid_precipitation;
        int prob_freezing_rain;
        int prob_flurries_above55;
        int prob_fog;
        int visibility;
        double uva;
    }

    public class currentWeatherInfo{
        String city;
        long issue_timestamp;
        WeatherInfo currentWeather;
        ArrayList<WeatherInfo> forecastDaily;
        ArrayList<WeatherInfo> forecastHourly;
        ArrayList<WeatherInfo> forecast3hourly;
        ArrayList<WeatherInfo> forecast6hourly;
        ArrayList<WeatherInfo> forecast12hourly;

        public currentWeatherInfo(RawWeatherInfo rawWeatherInfo){
            city = rawWeatherInfo.description;
            issue_timestamp = rawWeatherInfo.polling_time;
            currentWeather = new WeatherInfo();
            long[] timesteps = rawWeatherInfo.getTimeSteps();
            int current_weather_position = rawWeatherInfo.getCurrentForecastPosition();
            int next_midnight_position   = rawWeatherInfo.getNextMidnightAfterCurrentForecastPosition();
            currentWeather.timestamp          = Integer.parseInt(rawWeatherInfo.timesteps[current_weather_position]);
            currentWeather.condition_code     = Integer.parseInt(rawWeatherInfo.ww[current_weather_position]);
            currentWeather.clouds             = Integer.parseInt(rawWeatherInfo.N[current_weather_position]);
            currentWeather.temperature        = Double.parseDouble(rawWeatherInfo.TTT[current_weather_position]);
            currentWeather.temperature_low    = rawWeatherInfo.getMinTemperature(current_weather_position,next_midnight_position);
            currentWeather.temperature_high   = rawWeatherInfo.getMaxTemperature(current_weather_position,next_midnight_position);
            currentWeather.wind_speed         = Double.parseDouble(rawWeatherInfo.FF[current_weather_position]);
            currentWeather.wind_direction     = Double.parseDouble(rawWeatherInfo.DD[current_weather_position]);
            currentWeather.flurries           = Double.parseDouble(rawWeatherInfo.FX1[current_weather_position]);
            currentWeather.precipitation      = Double.parseDouble(rawWeatherInfo.RR1c[current_weather_position]);
            currentWeather.prob_precipitation = Integer.parseInt(rawWeatherInfo.wwP[current_weather_position]);

        }
    }

    public class WeatherForecastReader extends AsyncTask<Void,Void, RawWeatherInfo> {

        private Context context;
        private WeatherLocation weatherLocation;

        private String[] seperateValues(String s){
            String[] resultarray = new String[DATA_SIZE];
            resultarray = s.split(" {1,}");
            return resultarray;
        }

        private String[] assigntoRaw(final Element element){
            String[] result = new String[DATA_SIZE];
            NodeList values = element.getElementsByTagName("dwd:value");
            for (int j=0; j<values.getLength(); j++){
                Element value_element = (Element) values.item(j);
                String value_string = value_element.getFirstChild().getNodeValue();
                result = seperateValues(value_string);
            }
            return result;
        }


    public WeatherForecastReader(Context context, WeatherLocation weatherLocation){
            this.context = context;
            this.weatherLocation = weatherLocation;
        }

        @Override
        protected RawWeatherInfo doInBackground(Void... voids) {
            String weather_url = "https://opendata.dwd.de/weather/local_forecasts/mos/MOSMIX_L/single_stations/"+weatherLocation.name+"/kml/MOSMIX_L_LATEST_"+weatherLocation.name+".kmz";
            try{
                URL url = new URL(weather_url);
                ZipInputStream zipInputStream = new ZipInputStream(new URL(weather_url).openStream());
                zipInputStream.getNextEntry();
                // init new RawWeatherInfo instance to fill with data
                RawWeatherInfo rawWeatherInfo = new RawWeatherInfo();
                // rawWeatherInfo.description = weatherLocation.description;
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document document = documentBuilder.parse(zipInputStream);
                // get sensor description, usually city name. This should be equal to weatherLocation.description,
                // but we take it from the api to be sure nothing changed and the right city gets displayed!
                NodeList placemark_nodes = document.getElementsByTagName("kml:description");
                for (int i=0;i<placemark_nodes.getLength(); i++){
                    // should be only one, but we take the latest
                    Element placemark_element = (Element) placemark_nodes.item(i);
                    String description = placemark_element.getFirstChild().getNodeValue();
                    Log.v("PPPP",description);
                    rawWeatherInfo.description = description;
                }

                NodeList timesteps = document.getElementsByTagName("dwd:TimeStep");
                for (int i=0; i<timesteps.getLength(); i++){
                    Element element = (Element) timesteps.item(i);
                    rawWeatherInfo.timesteps[i] = element.getFirstChild().getNodeValue();
                    rawWeatherInfo.elements = i;
                }
                NodeList forecast = document.getElementsByTagName("dwd:Forecast");
                for (int i=0; i<forecast.getLength(); i++){
                    Element element = (Element) forecast.item(i);
                    String type     = element.getAttribute("dwd:elementName");
                    // PrivateLog.log(context,"Type/Element Name: "+type);
                    switch (type){
                        case "TTT": rawWeatherInfo.TTT = assigntoRaw(element); break;
                        case "E_TTT": rawWeatherInfo.E_TTT = assigntoRaw(element); break;
                        case "T5cm": rawWeatherInfo.T5cm = assigntoRaw(element); break;
                        case "Td": rawWeatherInfo.Td = assigntoRaw(element); break;
                        case "E_Td": rawWeatherInfo.E_Td = assigntoRaw(element); break;
                        case "Tx": rawWeatherInfo.Tx = assigntoRaw(element); break;
                        case "Tn": rawWeatherInfo.Tn = assigntoRaw(element); break;
                        case "TM": rawWeatherInfo.TM = assigntoRaw(element); break;
                        case "TG": rawWeatherInfo.TG = assigntoRaw(element); break;
                        case "DD": rawWeatherInfo.DD = assigntoRaw(element); break;
                        case "E_DD": rawWeatherInfo.E_DD = assigntoRaw(element); break;
                        case "FF": rawWeatherInfo.FF = assigntoRaw(element); break;
                        case "E_FF": rawWeatherInfo.E_FF = assigntoRaw(element); break;
                        case "FX1": rawWeatherInfo.FX1 = assigntoRaw(element); break;
                        case "FX3": rawWeatherInfo.FX3 = assigntoRaw(element); break;
                        case "FXh": rawWeatherInfo.FXh = assigntoRaw(element); break;
                        case "FXh25": rawWeatherInfo.FXh25 = assigntoRaw(element); break;
                        case "FXh40": rawWeatherInfo.FXh40 = assigntoRaw(element); break;
                        case "FXh55": rawWeatherInfo.FXh55 = assigntoRaw(element); break;
                        case "FX625": rawWeatherInfo.FX625 = assigntoRaw(element); break;
                        case "FX640": rawWeatherInfo.FX640 = assigntoRaw(element); break;
                        case "FX655": rawWeatherInfo.FX655 = assigntoRaw(element); break;
                        case "RR1c": rawWeatherInfo.RR1c = assigntoRaw(element); break;
                        case "RRL1c": rawWeatherInfo.RRL1c = assigntoRaw(element); break;
                        case "RR3": rawWeatherInfo.RR3 = assigntoRaw(element); break;
                        case "RR6": rawWeatherInfo.RR6 = assigntoRaw(element); break;
                        case "RR3c": rawWeatherInfo.RR3c = assigntoRaw(element); break;
                        case "RR6c": rawWeatherInfo.RR6c = assigntoRaw(element); break;
                        case "RRhc": rawWeatherInfo.RRhc = assigntoRaw(element); break;
                        case "RRdc": rawWeatherInfo.RRdc = assigntoRaw(element); break;
                        case "RRS1c": rawWeatherInfo.RRS1c = assigntoRaw(element); break;
                        case "RRS3c": rawWeatherInfo.RRS3c = assigntoRaw(element); break;
                        case "R101": rawWeatherInfo.R101 = assigntoRaw(element); break;
                        case "R102": rawWeatherInfo.R102 = assigntoRaw(element); break;
                        case "R103": rawWeatherInfo.R103 = assigntoRaw(element); break;
                        case "R105": rawWeatherInfo.R105 = assigntoRaw(element); break;
                        case "R107": rawWeatherInfo.R107 = assigntoRaw(element); break;
                        case "R110": rawWeatherInfo.R110 = assigntoRaw(element); break;
                        case "R120": rawWeatherInfo.R120 = assigntoRaw(element); break;
                        case "R130": rawWeatherInfo.R130 = assigntoRaw(element); break;
                        case "R150": rawWeatherInfo.R150 = assigntoRaw(element); break;
                        case "RR1o1": rawWeatherInfo.RR1o1 = assigntoRaw(element); break;
                        case "RR1w1": rawWeatherInfo.RR1w1 = assigntoRaw(element); break;
                        case "RR1u1": rawWeatherInfo.RR1u1 = assigntoRaw(element); break;
                        case "R600": rawWeatherInfo.R600 = assigntoRaw(element); break;
                        case "Rh00": rawWeatherInfo.Rh00 = assigntoRaw(element); break;
                        case "R602": rawWeatherInfo.R602 = assigntoRaw(element); break;
                        case "Rh02": rawWeatherInfo.Rh02 = assigntoRaw(element); break;
                        case "Rd02": rawWeatherInfo.Rd02 = assigntoRaw(element); break;
                        case "R610": rawWeatherInfo.R610 = assigntoRaw(element); break;
                        case "Rh10": rawWeatherInfo.Rh10 = assigntoRaw(element); break;
                        case "R650": rawWeatherInfo.R650 = assigntoRaw(element); break;
                        case "Rh50": rawWeatherInfo.Rh50 = assigntoRaw(element); break;
                        case "Rd00": rawWeatherInfo.Rd00 = assigntoRaw(element); break;
                        case "Rd10": rawWeatherInfo.Rd10 = assigntoRaw(element); break;
                        case "Rd50": rawWeatherInfo.Rd50 = assigntoRaw(element); break;
                        case "wwPd": rawWeatherInfo.wwPd = assigntoRaw(element); break;
                        case "DRR1": rawWeatherInfo.DRR1 = assigntoRaw(element); break;
                        case "wwZ": rawWeatherInfo.wwZ = assigntoRaw(element); break;
                        case "wwZ6": rawWeatherInfo.wwZ6 = assigntoRaw(element); break;
                        case "wwZh": rawWeatherInfo.wwZh = assigntoRaw(element); break;
                        case "wwD": rawWeatherInfo.wwD = assigntoRaw(element); break;
                        case "wwD6": rawWeatherInfo.wwD6 = assigntoRaw(element); break;
                        case "wwDh": rawWeatherInfo.wwDh = assigntoRaw(element); break;
                        case "wwC": rawWeatherInfo.wwC = assigntoRaw(element); break;
                        case "wwC6": rawWeatherInfo.wwC6 = assigntoRaw(element); break;
                        case "wwCh": rawWeatherInfo.wwCh = assigntoRaw(element); break;
                        case "wwT": rawWeatherInfo.wwT = assigntoRaw(element); break;
                        case "wwT6": rawWeatherInfo.wwT6 = assigntoRaw(element); break;
                        case "wwTh": rawWeatherInfo.wwTh = assigntoRaw(element); break;
                        case "wwTd": rawWeatherInfo.wwTd = assigntoRaw(element); break;
                        case "wwL": rawWeatherInfo.wwL = assigntoRaw(element); break;
                        case "wwL6": rawWeatherInfo.wwL6 = assigntoRaw(element); break;
                        case "wwLh": rawWeatherInfo.wwLh = assigntoRaw(element); break;
                        case "wwS": rawWeatherInfo.wwS = assigntoRaw(element); break;
                        case "wwS6": rawWeatherInfo.wwS6 = assigntoRaw(element); break;
                        case "wwSh": rawWeatherInfo.wwSh = assigntoRaw(element); break;
                        case "wwF": rawWeatherInfo.wwF = assigntoRaw(element); break;
                        case "wwF6": rawWeatherInfo.wwF6 = assigntoRaw(element); break;
                        case "wwFh": rawWeatherInfo.wwFh = assigntoRaw(element); break;
                        case "wwP": rawWeatherInfo.wwP = assigntoRaw(element); break;
                        case "wwP6": rawWeatherInfo.wwP6 = assigntoRaw(element); break;
                        case "wwPh": rawWeatherInfo.wwPh = assigntoRaw(element); break;
                        case "VV10": rawWeatherInfo.VV10 = assigntoRaw(element); break;
                        case "ww": rawWeatherInfo.ww = assigntoRaw(element); break;
                        case "ww3": rawWeatherInfo.ww3 = assigntoRaw(element); break;
                        case "W1W2": rawWeatherInfo.W1W2 = assigntoRaw(element); break;
                        case "WPc31": rawWeatherInfo.WPc31 = assigntoRaw(element); break;
                        case "WPc61": rawWeatherInfo.WPc61 = assigntoRaw(element); break;
                        case "WPch1": rawWeatherInfo.WPch1 = assigntoRaw(element); break;
                        case "WPcd1": rawWeatherInfo.WPcd1 = assigntoRaw(element); break;
                        case "N": rawWeatherInfo.N = assigntoRaw(element); break;
                        case "N05": rawWeatherInfo.N05 = assigntoRaw(element); break;
                        case "Nl": rawWeatherInfo.Nl = assigntoRaw(element); break;
                        case "Nm": rawWeatherInfo.Nm = assigntoRaw(element); break;
                        case "Nh": rawWeatherInfo.Nh = assigntoRaw(element); break;
                        case "Nlm": rawWeatherInfo.Nlm = assigntoRaw(element); break;
                        case "H_BsC": rawWeatherInfo.H_BsC = assigntoRaw(element); break;
                        case "PPPP": rawWeatherInfo.PPPP = assigntoRaw(element); break;
                        case "E_PPP": rawWeatherInfo.E_PPP = assigntoRaw(element); break;
                        case "RadS3": rawWeatherInfo.RadS3 = assigntoRaw(element); break;
                        case "RRad1": rawWeatherInfo.RRad1 = assigntoRaw(element); break;
                        case "Rad1h": rawWeatherInfo.Rad1h = assigntoRaw(element); break;
                        case "RadL3": rawWeatherInfo.RadL3 = assigntoRaw(element); break;
                        case "VV": rawWeatherInfo.VV = assigntoRaw(element); break;
                        case "D1": rawWeatherInfo.D1 = assigntoRaw(element); break;
                        case "SunD": rawWeatherInfo.SunD = assigntoRaw(element); break;
                        case "SunD3": rawWeatherInfo.SunD3 = assigntoRaw(element); break;
                        case "RSunD": rawWeatherInfo.RSunD = assigntoRaw(element); break;
                        case "PSd00": rawWeatherInfo.PSd00 = assigntoRaw(element); break;
                        case "PSd30": rawWeatherInfo.PSd30 = assigntoRaw(element); break;
                        case "PSd60": rawWeatherInfo.PSd60 = assigntoRaw(element); break;
                        case "wwM": rawWeatherInfo.wwM = assigntoRaw(element); break;
                        case "wwM6": rawWeatherInfo.wwM6 = assigntoRaw(element); break;
                        case "wwMh": rawWeatherInfo.wwMh = assigntoRaw(element); break;
                        case "wwMd": rawWeatherInfo.wwMd = assigntoRaw(element); break;
                        case "PEvap": rawWeatherInfo.PEvap = assigntoRaw(element); break;
                    }
                }
                PrivateLog.log(context,"Elements read: "+rawWeatherInfo.elements);
                return rawWeatherInfo;
            } catch (Exception e){

            }
            return null;
        }

        public void onNegativeResult(){
            // do nothing at the moment.
            PrivateLog.log(context,"","FAILED GETTING DATA!!!");
        }

        /*
         * Override this routine to define what to do if obtaining data succeeded.
         * Remember: at this point, the new data is already written to the database and can be
         * accessed via the CardHandler class.
         */

        public void onPositiveResult(){
            // do nothing at the moment.
        }

        public void onPositiveResult(RawWeatherInfo rawWeatherInfo){
            onPositiveResult();

            for (int i=0;i< rawWeatherInfo.elements;i++){
                PrivateLog.log(context,rawWeatherInfo.timesteps[i]+" PPPP:"+rawWeatherInfo.PPPP[i]+" TTT:"+rawWeatherInfo.TTT[i]);
            }

        }

        protected void onPostExecute(RawWeatherInfo rawWeatherInfo) {
            if (rawWeatherInfo == null) {
                onNegativeResult();
            } else {
                // get timestamp
                Calendar calendar = Calendar.getInstance();
                rawWeatherInfo.polling_time = calendar.getTimeInMillis();
                // writes the weather data to the database
                WeatherForecastContentProvider weatherForecastContentProvider = new WeatherForecastContentProvider();
                weatherForecastContentProvider.writeWeatherForecast(context,rawWeatherInfo);
                onPositiveResult(rawWeatherInfo);
            }
        }

    }

    public class StationsManager {

        private Context context;

        public StationsManager(Context context){
            this.context = context;
        }

        private String getStationsStringFromResource(){
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

        private String getStringValue(String source, String seperator){
            int pos = source.indexOf(seperator);
            // return whole string when no seperator present and delete source
            String result;
            if (pos == -1){
                result = source;
            } else {
                // create result-string (=value)
                result = source.substring(0,pos);
            }
            return result;
        }

        private String cutSourceString(String source, String seperator){
            int pos = source.indexOf(seperator);
            // return whole string when no seperator present and delete source
            String result;
            if (pos == -1){
                result = "";
            } else {
                result = source.substring(pos+1);
            }
            return result;
        }

        public ArrayList<WeatherLocation> getStations(){
            ArrayList<WeatherLocation> stations = new ArrayList<WeatherLocation>();
            String stationString = getStationsStringFromResource();
            while (stationString.length()>0){
                // example: NEUHERBERG;G262;10.28,49.52,380.0|
                WeatherLocation weatherLocation = new WeatherLocation();
                weatherLocation.description = getStringValue(stationString,";");
                stationString               = cutSourceString(stationString,";");
                weatherLocation.name        = getStringValue(stationString,";");
                stationString               = cutSourceString(stationString,";");
                weatherLocation.latitude    = Double.parseDouble(getStringValue(stationString,";"));
                stationString               = cutSourceString(stationString,";");
                weatherLocation.longitude   = Double.parseDouble(getStringValue(stationString,";"));
                stationString               = cutSourceString(stationString,";");
                weatherLocation.altitude    = Double.parseDouble(getStringValue(stationString,"|"));
                stationString               = cutSourceString(stationString,"|");
            }
            Collections.sort(stations, new WeatherLocation());
            return stations;
        }

        public ArrayList<String> getStationNames(Context context){
            ArrayList<WeatherLocation> weatherLocations = getStations();
            ArrayList<String> names = new ArrayList<String>();
            for (int i=0; i<weatherLocations.size(); i++){
                WeatherLocation weatherLocation = weatherLocations.get(i);
                names.add(weatherLocation.description);
            }
            return names;
        }
    }

    public class StationsArrayList {

        public ArrayList<WeatherLocation> stations = new ArrayList<WeatherLocation>();
        private Context context;

        /**
         * Public constructor. Needs the context.
         * @param context
         */

        public StationsArrayList(Context context){
            this.context = context;
            StationsManager stationsManager = new StationsManager(context);
            stations = stationsManager.getStations();
        }

        /**
         * Gets the position of a specific station by name. Returns position or -1 if the station name does
         * not exist in the stations list.
         * @param name
         * @return
         */

        public int getStationPositionByName(String name){
            for (int i=0; i<stations.size(); i++){
                if (stations.get(i).description.equals(name)){
                    return i;
                }
            }
            return -1;
        }

        /**
         * Gets the position of the set up station.
         * @param context
         * @return
         */

        public int getSetStationPositionByName(Context context){
            WeatherSettings weatherSettings = new WeatherSettings(context);
            return getStationPositionByName(weatherSettings.station);
        }

        /**
         * Returns an arraylist of the station names in the same order like in this class.
         * @return
         */

        public ArrayList<String> getStringArrayListOfNames(){
            ArrayList<String> result = new ArrayList<String>();
            for (int i=0; i<stations.size(); i++){
                result.add(stations.get(i).description);
            }
            return result;
        }
    }

}
