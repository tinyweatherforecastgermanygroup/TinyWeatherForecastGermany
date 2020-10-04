
/**
 *
 * Copyright (C) 2010-2012, Helmut Lehmeyer <helmut.lehmeyer@gmail.com>
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 */

/**
 *
 * Astronomical Calculations and and helper Classes
 *
 * java Source code based on the javascript by Arnold Barmettler, www.astronomie.info / www.CalSky.com
 * based on algorithms by Peter Duffett-Smith's great and easy book 'Practical Astronomy with your Calculator'.
 *
 * @author Helmut Lehmeyer
 * @date 15.05.2012
 * @version 0.1
 */

/**
 * Modifiea as follows:
 * javadoc corrected, references shortened by removing package name.
 */

package org.astronomie.info;

/**
 *
 * Astronomical Calculations
 *
 */
public class Astronomy
{


    final static double pi = Math.PI;
    final static double DEG = pi/180.0;
    final static double RAD = 180./pi;


    // return integer value, closer to 0
    public static int Int(double x) { if (x<0) { return (int) (Math.ceil(x)); } else return (int) (Math.floor(x)); }

    public static double sqr( double x) { return x*x; }

    public static double frac( double x) { return(x-Math.floor(x)); }

    public static double mod( double a, double b) { return(a-Math.floor(a/b)*b); }

    public static double mod2Pi( double x) { return( mod(x, 2.*pi)); } 				// Modulo PI

    public static double round1000000( double x) { return(Math.round(1000000.*x)/1000000.); }
    public static double round100000( double x) { return(Math.round(100000.*x)/100000.); }
    public static double round10000( double x) { return(Math.round(10000.*x)/10000.); }
    public static double round1000( double x) { return(Math.round(1000.*x)/1000.); }
    public static double round100( double x) { return(Math.round(100.*x)/100.); }
    public static double round10( double x) { return(Math.round(10.*x)/10.); }

    /**
     * @param hh Decimal Time
     * @return   a <code>String</code> of decimal Time <code>HH:MM:SS</code>
     */
    public static String timeHHMMSS(double hh)
    {
        Time reTime = new Time(hh);

        return reTime.hhmmssString;
    }

    /**
     * @param hh Decimal Time
     * @return   a <code>String</code> of decimal Time <code>HH:MM:SS = Decimal Time</code>
     */
    public static String timeHHMMSSdec(double hh)
    {
        Time reTime = new Time(hh);

        return reTime.hhmmssStringdec;
    }

    /**
     * @param hh Decimal Time
     * @return   a <code>String</code> of decimal Time <code>HH:MM = Decimal Time</code>
     */
    public static String timeHHMMdec(double hh)
    {
        Time reTime = new Time(hh);

        return reTime.hhmmStringdec;
    }

    /**
     * @param hh Decimal Time
     * @return   a <code>String</code> of decimal Time <code>HH:MM</code>
     */
    public static String timeHHMM(double hh)
    {
        Time reTime = new Time(hh);

        return reTime.hhmmString;
    }

    /**
     * @param lon geographical longitude in degree
     * @return    the name <code>String</code> of a sign
     */
    public static String sign(double lon) {
        String[] signs = {"Widder", "Stier", "Zwillinge", "Krebs", "LÃ¶we", "Jungfrau",
                "Waage", "Skorpion", "SchÃ¼tze", "Steinbock", "Wassermann", "Fische"};
        return( signs[Int(Math.floor(lon*RAD/30))] );
    }

    /**
     * @param day
     * @param month
     * @param year
     * @return Julian date: valid only from 1.3.1901 to 28.2.2100
     */
    public static double calcJD(int day, int month, int year) {
        double jd = 2415020.5-64; // 1.1.1900 - correction of algorithm
        if (month<=2) { year--; month += 12; }
        jd += Int( (year-1900)*365.25 );
        jd += Int( 30.6001*(1+month) );
        return(jd + day);
    }

    /**
     * @param JD Julian Date
     * @return Julian Date converted to Greenwich Mean Sidereal Time
     */
    public static double gMST(double JD) {
        double UT = frac(JD-0.5)*24.; // UT in hours
        JD = Math.floor(JD-0.5)+0.5;   // JD at 0 hours UT
        double T = (JD-2451545.0)/36525.0;
        double T0 = 6.697374558 + T*(2400.051336 + T*0.000025862);
        return(mod(T0+UT*1.002737909, 24.));
    }

    /**
     * @param JD  Julian Date
     * @param gmst Greenwich mean sidereal
     * @return convert Greenweek mean sidereal time to UT
     */
    public static double gMST2UT(double JD, double gmst) {
        JD = Math.floor(JD-0.5)+0.5;   // JD at 0 hours UT
        double T = (JD-2451545.0)/36525.0;
        double T0 = mod(6.697374558 + T*(2400.051336 + T*0.000025862), 24.);
        double UT = 0.9972695663*((gmst-T0));
        return(UT);
    }

    /**
     * @param gmst Greenwich mean sidereal
     * @param lon geographical longitude in radians, East is positive
     * @return Local Mean Sidereal Time
     */
    public static double gMST2LMST(double gmst, double lon) {
        return(mod(gmst+RAD*lon/15, 24.));
    }

    /**
     * Transform ecliptical coordinates (lon/lat) to equatorial coordinates (RA/dec)
     *
     * @param coor
     * @param TDT
     * @return equatorial coordinate (RA/dec)
     */
    public static Coor ecl2Equ(Coor coor, double TDT) {
        double T = (TDT-2451545.0)/36525.; // Epoch 2000 January 1.5
        double eps = (23.+(26+21.45/60.)/60. + T*(-46.815 +T*(-0.0006 + T*0.00181) )/3600. )*DEG;
        double coseps = Math.cos(eps);
        double sineps = Math.sin(eps);

        double sinlon = Math.sin(coor.lon);
        coor.ra  = mod2Pi( Math.atan2( (sinlon*coseps-Math.tan(coor.lat)*sineps), Math.cos(coor.lon) ) );
        coor.dec = Math.asin( Math.sin(coor.lat)*coseps + Math.cos(coor.lat)*sineps*sinlon );

        return(coor);
    }

    /**
     * Transform equatorial coordinates (RA/Dec) to horizonal coordinates (azimuth/altitude)
     * Refraction is ignored
     *
     * @param coor
     * @param TDT
     * @param geolat
     * @param lmst
     * @return horizonal coordinates (azimuth/altitude)
     */
    public static Coor equ2Altaz(Coor coor, double TDT, double geolat, double lmst) {
        double cosdec = Math.cos(coor.dec);
        double sindec = Math.sin(coor.dec);
        double lha = lmst - coor.ra;
        double coslha = Math.cos(lha);
        double sinlha = Math.sin(lha);
        double coslat = Math.cos(geolat);
        double sinlat = Math.sin(geolat);

        double N = -cosdec * sinlha;
        double D = sindec * coslat - cosdec * coslha * sinlat;
        coor.az = mod2Pi( Math.atan2(N, D) );
        coor.alt = Math.asin( sindec * sinlat + cosdec * coslha * coslat );

        return(coor);
    }

    /**
     * Transform geocentric equatorial coordinates (RA/Dec) to topocentric equatorial coordinates
     *
     * @param coor
     * @param observer
     * @param lmst
     * @return topocentric equatorial coordinates
     */
    public static Coor geoEqu2TopoEqu(Coor coor, Coor observer, double lmst) {
        double cosdec = Math.cos(coor.dec);
        double sindec = Math.sin(coor.dec);
        double coslst = Math.cos(lmst);
        double sinlst = Math.sin(lmst);
        double coslat = Math.cos(observer.lat); // we should use geocentric latitude, not geodetic latitude
        double sinlat = Math.sin(observer.lat);
        double rho    = observer.radius; // observer-geocenter in Kilometer

        double x = coor.distance*cosdec*Math.cos(coor.ra) - rho*coslat*coslst;
        double y = coor.distance*cosdec*Math.sin(coor.ra) - rho*coslat*sinlst;
        double z = coor.distance*sindec - rho*sinlat;

        coor.distanceTopocentric = Math.sqrt(x*x + y*y + z*z);
        coor.decTopocentric = Math.asin(z/coor.distanceTopocentric);
        coor.raTopocentric = mod2Pi( Math.atan2(y, x) );

        return(coor);
    }

    /**
     *  Calculate cartesian from polar coordinates
     *
     * @param lon
     * @param lat
     * @param distance
     * @return cartesian
     */
    public static Coor equPolar2Cart(double lon, double lat, double distance)
    {
        Coor c1 = new Coor();
        double rcd = Math.cos(lat) * distance;
        c1.x = rcd * Math.cos(lon);
        c1.y = rcd * Math.sin(lon);
        c1.z = distance * Math.sin(lat);
        return c1;
    }

    /**
     * Calculate observers cartesian equatorial coordinates (x,y,z in celestial frame)
     * from geodetic coordinates (longitude, latitude, height above WGS84 ellipsoid)
     * Currently only used to calculate distance of a body from the observer
     *
     * @param lon
     * @param lat
     * @param height
     * @param gmst
     * @return observers cartesian equatorial coordinates
     */
    public static Coor observer2EquCart(double lon, double lat, double height, double gmst)
    {
        double flat = 298.257223563;        // WGS84 flatening of earth
        double aearth = 6378.137;           // GRS80/WGS84 semi major axis of earth ellipsoid
        Coor cart = new Coor();
        // Calculate geocentric latitude from geodetic latitude
        double co = Math.cos (lat);
        double si = Math.sin (lat);
        double fl = 1.0 - 1.0 / flat;
        fl = fl * fl;
        si = si * si;
        double u = 1.0 / Math.sqrt (co * co + fl * si);
        double a = aearth * u + height;
        double b = aearth * fl * u + height;
        double radius = Math.sqrt (a * a * co * co + b * b * si); // geocentric distance from earth center
        cart.y = Math.acos (a * co / radius); // geocentric latitude, rad
        cart.x = lon; // longitude stays the same
        if (lat < 0.0) { cart.y = -cart.y; } // adjust sign
        cart = equPolar2Cart( cart.x, cart.y, radius ); // convert from geocentric polar to geocentric cartesian, with regard to Greenwich
        // rotate around earth's polar axis to align coordinate system from Greenwich to vernal equinox
        double x=cart.x;
        double y=cart.y;
        double rotangle = gmst/24*2*pi; // sideral time gmst given in hours. Convert to radians
        cart.x = x*Math.cos(rotangle)-y*Math.sin(rotangle);
        cart.y = x*Math.sin(rotangle)+y*Math.cos(rotangle);
        cart.radius = radius;
        cart.lon = lon;
        cart.lat = lat;
        return(cart);
    }

    /**
     * Calculate coordinates for Sun
     * Coordinates are accurate to about 10s (right ascension)
     * and a few minutes of arc (declination)
     *
     * @param TDT
     * @return coordinates for Sun
     */
    public static Coor sunPosition(double TDT)
    {
        return sunPosition(TDT, 0, 0, false );
    }
    /**
     * @param TDT
     * @param geolat
     * @param lmst
     * @return coordinates for Sun
     */
    public static Coor sunPosition(double TDT, double geolat, double lmst)
    {
        return sunPosition(TDT, geolat, lmst, true );
    }
    /**
     * @param TDT
     * @param geolat
     * @param lmst
     * @param useGeo
     * @return coordinates for Sun
     */
    private static Coor sunPosition(double TDT, double geolat, double lmst, boolean useGeo )
    {

        double D = TDT - 2447891.5;

        double eg = 279.403303 * DEG;
        double wg = 282.768422 * DEG;
        double e = 0.016713;
        int a = 149598500; // km
        double diameter0 = 0.533128 * DEG; // angular diameter of Moon at a distance

        double MSun = 360 * DEG / 365.242191 * D + eg - wg;
        double nu = MSun + 360.0 * DEG / pi * e * Math.sin(MSun);

        Coor sunCoor = new Coor();
        sunCoor.lon = mod2Pi(nu + wg);
        sunCoor.lat = 0;
        sunCoor.anomalyMean = MSun;

        sunCoor.distance = (1 - sqr(e)) / (1 + e * Math.cos(nu)); // distance in astronomical units
        sunCoor.diameter = diameter0 / sunCoor.distance; // angular diameter in radians
        sunCoor.distance *= a;                         // distance in km
        sunCoor.parallax = 6378.137 / sunCoor.distance;  // horizonal parallax

        sunCoor = ecl2Equ(sunCoor, TDT);

        // Calculate horizonal coordinates of sun, if geographic positions is given
        if (useGeo) {
            sunCoor = equ2Altaz(sunCoor, TDT, geolat, lmst);
        }
        // changed this to a local reference, removing org.openhab.util.Astronomy.
        sunCoor.sign = sign(sunCoor.lon);
        return sunCoor;
    }

    /**
     * Calculate data and coordinates for the Moon
     * Coordinates are accurate to about 1/5 degree (in ecliptic coordinates)
     *
     * @param sunCoor
     * @param TDT
     * @return data and coordinates for the Moon
     */
    public static Coor moonPosition(Coor sunCoor, double TDT)
    {
        return moonPosition(sunCoor, TDT, new Coor(), 0, false);
    }
    /**
     * @param sunCoor
     * @param TDT
     * @param observer
     * @param lmst
     * @return data and coordinates for the Moon
     */
    public static Coor moonPosition(Coor sunCoor, double TDT, Coor observer, double lmst)
    {
        return moonPosition(sunCoor, TDT, observer, lmst, true);
    }
    /**
     * @param sunCoor
     * @param TDT
     * @param observer
     * @param lmst
     * @param useObs
     * @return data and coordinates for the Moon
     */
    private static Coor moonPosition(Coor sunCoor, double TDT, Coor observer, double lmst, boolean useObs)
    {
        double D = TDT - 2447891.5;

        // Mean Moon orbit elements as of 1990.0
        double l0 = 318.351648 * DEG;
        double P0 = 36.340410 * DEG;
        double N0 = 318.510107 * DEG;
        double i = 5.145396 * DEG;
        double e = 0.054900;
        int a = 384401;                // km
        double diameter0 = 0.5181 * DEG;     // angular diameter of Moon at a distance
        double parallax0 = 0.9507 * DEG;     // parallax at distance a

        double l = 13.1763966 * DEG * D + l0;
        double MMoon = l - 0.1114041 * DEG * D - P0; // Moon's mean anomaly M
        double N = N0 - 0.0529539 * DEG * D;       // Moon's mean ascending node longitude
        double C = l - sunCoor.lon;
        double Ev = 1.2739 * DEG * Math.sin(2 * C - MMoon);
        double Ae = 0.1858 * DEG * Math.sin(sunCoor.anomalyMean);
        double A3 = 0.37 * DEG * Math.sin(sunCoor.anomalyMean);
        double MMoon2 = MMoon + Ev - Ae - A3;            // corrected Moon anomaly
        double Ec = 6.2886 * DEG * Math.sin(MMoon2);   // equation of centre
        double A4 = 0.214 * DEG * Math.sin(2 * MMoon2);
        double l2 = l + Ev + Ec - Ae + A4;                 // corrected Moon's longitude
        double V = 0.6583 * DEG * Math.sin(2 * (l2 - sunCoor.lon));
        double l3 = l2 + V;                          // true orbital longitude;

        double N2 = N - 0.16 * DEG * Math.sin(sunCoor.anomalyMean);

        Coor moonCoor = new Coor();
        moonCoor.lon = mod2Pi( N2 + Math.atan2( Math.sin(l3-N2)*Math.cos(i), Math.cos(l3-N2) ) );
        moonCoor.lat = Math.asin( Math.sin(l3-N2)*Math.sin(i) );
        moonCoor.orbitLon = l3;

        moonCoor = ecl2Equ(moonCoor, TDT);
        // relative distance to semi mayor axis of lunar oribt
        moonCoor.distance = (1-sqr(e)) / (1+e*Math.cos(MMoon2+Ec) );
        moonCoor.diameter = diameter0/moonCoor.distance; // angular diameter in radians
        moonCoor.parallax = parallax0/moonCoor.distance; // horizontal parallax in radians
        moonCoor.distance *= a; // distance in km

        // Calculate horizonal coordinates of sun, if geographic positions is given
        if (useObs) {
            // transform geocentric coordinates into topocentric (==observer based) coordinates
            moonCoor = geoEqu2TopoEqu(moonCoor, observer, lmst);
            moonCoor.raGeocentric = moonCoor.ra; // backup geocentric coordinates
            moonCoor.decGeocentric = moonCoor.dec;
            moonCoor.ra=moonCoor.raTopocentric;
            moonCoor.dec=moonCoor.decTopocentric;
            moonCoor = equ2Altaz(moonCoor, TDT, observer.lat, lmst); // now ra and dec are topocentric
        }

        // Age of Moon in radians since New Moon (0) - Full Moon (pi)
        moonCoor.moonAge = mod2Pi(l3-sunCoor.lon);
        moonCoor.phase   = 0.5*(1-Math.cos(moonCoor.moonAge)); // Moon phase, 0-1

        //String[] phases_EN = { "New moon", "Increasing crescent", "1st quarter", "Increasing Moon", "Full moon", "Decreasing Moon", "Last quarter", "decreasing crescent", "New moon" };
        String[] phases_DE = {"Neumond", "Zunehmende Sichel", "Erstes Viertel", "Zunehmender Mond", "Vollmond", "Abnehmender Mond", "Letztes Viertel", "Abnehmende Sichel", "Neumond"};
        double mainPhase = 1.0 / 29.53 * 360 * DEG; // show 'Newmoon, 'Quarter' for +/-1 day arond the actual event
        double p = mod(moonCoor.moonAge, 90.0 * DEG);


        if (p < mainPhase || p > 90*DEG-mainPhase) p = 2*Math.round(moonCoor.moonAge / (90.*DEG));
        else p = 2*Math.floor(moonCoor.moonAge / (90.*DEG))+1;
        moonCoor.moonPhase = phases_DE[(int) p];
        moonCoor.sign = sign(moonCoor.lon);

        return moonCoor;
    }

    /**
     * Rough refraction formula using standard atmosphere: 1015 mbar and 10Â°C
     * Input true altitude in radians, Output: increase in altitude in degrees
     *
     * @param alt
     * @return increase in altitude in degrees
     */
    public static double refraction(double alt)
    {
        int pressure = 1015;
        int temperature = 10;
        double altdeg = alt * RAD;

        if (altdeg < -2 || altdeg >= 90)
            return 0;

        if (altdeg > 15)
            return 0.00452 * pressure / ((273 + temperature) * Math.tan(alt));

        double y = alt;
        double D = 0.0;
        double P = (pressure - 80.0) / 930.0;
        double Q = 0.0048 * (temperature - 10.0);
        double y0 = y;
        double D0 = D;
        double N = 0.0;

        for (int i = 0; i < 3; i++)
        {
            N = y + (7.31 / (y + 4.4));
            N = 1.0 / Math.tan(N * DEG);
            D = N * P / (60.0 + Q * (N + 39.0));
            N = y - y0;
            y0 = D - D0 - N;
            N = ((N != 0.0) && (y0 != 0.0)) ? y - N * (alt + D - y) / y0 : alt + D;
            y0 = y;
            D0 = D;
            y = N;
        }
        return D; // Hebung durch Refraktion in radians
    }

    /**
     * Correction for refraction and semi-diameter/parallax of body is taken care of in function RiseSet
     *
     * @param corr
     * @param lon
     * @param lat
     * @param h is used to calculate the twilights. It gives the required elevation of the disk center of the sun
     * @return Greenwich sidereal time (hours) of time of rise and set of object with coordinates Coor.ra/Coor.dec at geographic position lon/lat (all values in radians)
     */
    private static Riseset gMSTRiseSet(Coor corr, double lon, double lat, double h)
    {

        double tagbogen = Math.acos((Math.sin(h) - Math.sin(lat) * Math.sin(corr.dec)) / (Math.cos(lat) * Math.cos(corr.dec)));

        Riseset r1 = new Riseset();
        r1.transit = RAD / 15 * (+corr.ra - lon);
        r1.rise = 24.0 + RAD / 15 * (-tagbogen + corr.ra - lon); // calculate GMST of rise of object
        r1.set = RAD / 15 * (+tagbogen + corr.ra - lon); // calculate GMST of set of object

        // using the modulo function Mod, the day number goes missing. This may get a problem for the moon
        r1.transit = mod(r1.transit, 24);
        r1.rise = mod(r1.rise, 24);
        r1.set = mod(r1.set, 24);

        return r1;
    }

    /**
     * Find GMST of rise/set of object from the two calculates
     * (start)points (day 1 and 2) and at midnight UT(0)
     *
     * @param gmst0
     * @param gmst1
     * @param gmst2
     * @param timefactor
     * @return GMST of rise/set of object
     */
    private static double interpolateGMST(double gmst0, double gmst1, double gmst2, double timefactor)
    {
        return (timefactor * 24.07 * gmst1 - gmst0 * (gmst2 - gmst1)) / (timefactor * 24.07 + gmst1 - gmst2);
    }

    /**
     * JD is the Julian Date of 0h UTC time (midnight)
     *
     * @param jd0UT
     * @param coor1
     * @param coor2
     * @param lon
     * @param lat
     * @param timeinterval
     * @return rise/set object
     */
    private static Riseset riseSet(double jd0UT, Coor coor1, Coor coor2, double lon, double lat, double timeinterval)
    {
        return riseSet(jd0UT, coor1, coor2, lon, lat, timeinterval, 0, false);
    }
    private static Riseset riseSet(double jd0UT, Coor coor1, Coor coor2, double lon, double lat, double timeinterval, double altitude)
    {
        return riseSet(jd0UT, coor1, coor2, lon, lat, timeinterval, altitude, true);
    }
    private static Riseset riseSet(double jd0UT, Coor coor1, Coor coor2, double lon, double lat, double timeinterval, double altitude, boolean useAlt)
    {
        // altitude of sun center: semi-diameter, horizontal parallax and (standard) refraction of 34'
        double alt = 0.0; // calculate

        // true height of sun center for sunrise and set calculation. Is kept 0 for twilight (ie. altitude given):
        if (!useAlt && altitude == 0.0) alt = 0.5 * coor1.diameter - coor1.parallax + 34.0 / 60 * DEG;

        Riseset rise1 = gMSTRiseSet(coor1, lon, lat, altitude);
        Riseset rise2 = gMSTRiseSet(coor2, lon, lat, altitude);

        Riseset rise = new Riseset();

        // unwrap GMST in case we move across 24h -> 0h
        if (rise1.transit > rise2.transit && Math.abs(rise1.transit - rise2.transit) > 18) rise2.transit += 24;
        if (rise1.rise > rise2.rise && Math.abs(rise1.rise - rise2.rise) > 18) rise2.rise += 24;
        if (rise1.set > rise2.set && Math.abs(rise1.set - rise2.set) > 18) rise2.set += 24;
        double T0 = gMST(jd0UT);
        //  var T02 = T0-zone*1.002738; // Greenwich sidereal time at 0h time zone (zone: hours)

        // Greenwich sidereal time for 0h at selected longitude
        double T02 = T0 - lon * RAD / 15 * 1.002738; if (T02 < 0) T02 += 24;

        if (rise1.transit < T02) { rise1.transit += 24; rise2.transit += 24; }
        if (rise1.rise < T02) { rise1.rise += 24; rise2.rise += 24; }
        if (rise1.set < T02) { rise1.set += 24; rise2.set += 24; }

        // Refraction and Parallax correction
        double decMean = 0.5 * (coor2.dec + coor2.dec);
        double psi = Math.acos(Math.sin(lat) / Math.cos(decMean));
        double y = Math.asin(Math.sin(alt) / Math.sin(psi));
        double dt = 240 * RAD * y / Math.cos(decMean) / 3600; // time correction due to refraction, parallax

        rise.transit = gMST2UT(jd0UT, interpolateGMST(T0, rise1.transit, rise2.transit, timeinterval));
        rise.rise = gMST2UT(jd0UT, interpolateGMST(T0, rise1.rise, rise2.rise, timeinterval) - dt);
        rise.set = gMST2UT(jd0UT, interpolateGMST(T0, rise1.set, rise2.set, timeinterval) + dt);

        return rise;
    }

    /**
     * Find (local) time of sunrise and sunset, and twilights
     * JD is the Julian Date of 0h local time (midnight)
     * Accurate to about 1-2 minutes
     * recursive: 1 - calculate rise/set in UTC in a second run
     * recursive: 0 - find rise/set on the current local day. This is set when doing the first call to this function
     *
     * @param JD
     * @param deltaT
     * @param lon
     * @param lat
     * @param zone
     * @param recursive
     * @return (local) time of sunrise and sunset, and twilights
     */
    public static Riseset sunRise(double JD, double deltaT, double lon, double lat, double zone, boolean recursive)
    {
        double jd0UT = Math.floor(JD - 0.5) + 0.5;   // JD at 0 hours UT
        Coor coor1 = sunPosition(jd0UT+  deltaT/24./3600.);
        Coor coor2 = sunPosition(jd0UT+1.+deltaT/24./3600.); // calculations for next day's UTC midnight

        Riseset risetemp = new Riseset();
        Riseset rise = new Riseset();
        // rise/set time in UTC.
        rise = riseSet(jd0UT, coor1, coor2, lon, lat, 1);
        if (!recursive)
        { // check and adjust to have rise/set time on local calendar day
            if (zone>0) {
                // rise time was yesterday local time -> calculate rise time for next UTC day
                if (rise.rise>=24-zone || rise.transit>=24-zone || rise.set>=24-zone) {
                    risetemp = sunRise(JD+1, deltaT, lon, lat, zone, true);
                    if (rise.rise>=24-zone) rise.rise = risetemp.rise;
                    if (rise.transit >=24-zone) rise.transit = risetemp.transit;
                    if (rise.set >=24-zone) rise.set  = risetemp.set;
                }
            }
            else if (zone<0) {
                // rise time was yesterday local time -> calculate rise time for next UTC day
                if (rise.rise<-zone || rise.transit<-zone || rise.set<-zone) {
                    risetemp = sunRise(JD-1, deltaT, lon, lat, zone, true);
                    if (rise.rise<-zone) rise.rise = risetemp.rise;
                    if (rise.transit<-zone) rise.transit = risetemp.transit;
                    if (rise.set <-zone) rise.set  = risetemp.set;
                }
            }

            rise.transit = mod(rise.transit + zone, 24.0);
            rise.rise = mod(rise.rise + zone, 24.0);
            rise.set = mod(rise.set + zone, 24.0);

            // Twilight calculation
            // civil twilight time in UTC.
            risetemp = riseSet(jd0UT, coor1, coor2, lon, lat, 1, -6.*DEG);
            rise.cicilTwilightMorning = mod(risetemp.rise +zone, 24.);
            rise.cicilTwilightEvening = mod(risetemp.set  +zone, 24.);

            // nautical twilight time in UTC.
            risetemp = riseSet(jd0UT, coor1, coor2, lon, lat, 1, -12.*DEG);
            rise.nauticalTwilightMorning = mod(risetemp.rise +zone, 24.);
            rise.nauticalTwilightEvening = mod(risetemp.set  +zone, 24.);

            // astronomical twilight time in UTC.
            risetemp = riseSet(jd0UT, coor1, coor2, lon, lat, 1, -18.*DEG);
            rise.astronomicalTwilightMorning = mod(risetemp.rise +zone, 24.);
            rise.astronomicalTwilightEvening = mod(risetemp.set  +zone, 24.);
        }
        return rise;
    }

    /**
     * Find local time of moonrise and moonset
     * JD is the Julian Date of 0h local time (midnight)
     * Accurate to about 5 minutes or better
     * recursive: 1 - calculate rise/set in UTC
     * recursive: 0 - find rise/set on the current local day (set could also be first)
     * returns 0.000000000 for moonrise/set does not occur on selected day
     *
     * @param JD
     * @param deltaT
     * @param lon
     * @param lat
     * @param zone
     * @param recursive
     * @return local time of moonrise and moonset
     */
    public static Riseset moonRise(double JD, double deltaT, double lon, double lat, double zone, boolean recursive)
    {
        double timeinterval = 0.5;

        double jd0UT = Math.floor(JD - 0.5) + 0.5;   // JD at 0 hours UT
        Coor suncoor1 = sunPosition(jd0UT+ deltaT/24./3600.);
        Coor coor1 = moonPosition(suncoor1, jd0UT+ deltaT/24./3600.);

        Coor suncoor2 = sunPosition(jd0UT +timeinterval + deltaT/24./3600.); // calculations for noon
        // calculations for next day's midnight
        Coor coor2 = moonPosition(suncoor2, jd0UT +timeinterval + deltaT/24./3600.);

        Riseset risetemp = new Riseset();
        Riseset rise = new Riseset();

        // rise/set time in UTC, time zone corrected later.
        // Taking into account refraction, semi-diameter and parallax
        rise = riseSet(jd0UT, coor1, coor2, lon, lat, timeinterval);

        if (!recursive)
        { // check and adjust to have rise/set time on local calendar day
            if (zone > 0)
            {
                // recursive call to MoonRise returns events in UTC
                Riseset riseprev = moonRise(JD-1., deltaT, lon, lat, zone, true);

                if (rise.transit >= 24.0 - zone || rise.transit < -zone)
                { // transit time is tomorrow local time
                    if (riseprev.transit < 24.0 - zone) rise.transit = 0.000000000; // there is no moontransit today
                    else rise.transit = riseprev.transit;
                }

                if (rise.rise >= 24.0 - zone || rise.rise < -zone)
                { // transit time is tomorrow local time
                    if (riseprev.rise < 24.0 - zone) rise.rise = 0.000000000; // there is no moontransit today
                    else rise.rise = riseprev.rise;
                }

                if (rise.set >= 24.0 - zone || rise.set < -zone)
                { // transit time is tomorrow local time
                    if (riseprev.set < 24.0 - zone) rise.set = 0.000000000; // there is no moontransit today
                    else rise.set = riseprev.set;
                }

            }
            else if (zone < 0)
            {
                // rise/set time was tomorrow local time -> calculate rise time for former UTC day
                if (rise.rise < -zone || rise.set < -zone || rise.transit < -zone)
                {
                    risetemp = moonRise(JD+1., deltaT, lon, lat, zone, true);

                    if (rise.rise < -zone)
                    {
                        if (risetemp.rise > -zone) rise.rise = 0.000000000; // there is no moonrise today
                        else rise.rise = risetemp.rise;
                    }

                    if (rise.transit < -zone)
                    {
                        if (risetemp.transit > -zone) rise.transit = 0.000000000; // there is no moonset today
                        else rise.transit = risetemp.transit;
                    }

                    if (rise.set < -zone)
                    {
                        if (risetemp.set > -zone) rise.set = 0.000000000; // there is no moonset today
                        else rise.set = risetemp.set;
                    }

                }
            }

            //stimmt das?
            if (rise.rise != 0.000000000) rise.rise = mod(rise.rise + zone, 24.0);          // correct for time zone, if time is valid
            if (rise.transit != 0.000000000) rise.transit = mod(rise.transit + zone, 24.0); // correct for time zone, if time is valid
            if (rise.set != 0.000000000) rise.set = mod(rise.set + zone, 24.0);             // correct for time zone, if time is valid
        }
        return rise;
    }

    /**
     * set of variables for sun calculations
     */
    public static class Coor
    {
        public Coor(){}

        public double lon;
        public double lat;

        public double ra;
        public double dec;
        public double raGeocentric;
        public double decGeocentric;

        public double az;
        public double alt;

        public double x;
        public double y;
        public double z;

        public double radius;
        public double diameter;
        public double distance;
        public double distanceTopocentric;
        public double decTopocentric;
        public double raTopocentric;

        public double anomalyMean;
        public double parallax;
        public double orbitLon;

        public double moonAge;
        public double phase;

        public String moonPhase;
        public String sign;
    }

    /**
     * set of variables for sunrise calculations
     */
    public static class Riseset
    {
        public Riseset(){}

        public double transit;
        public double rise;
        public double set;

        public double cicilTwilightMorning;
        public double cicilTwilightEvening;

        public double nauticalTwilightMorning;
        public double nauticalTwilightEvening;

        public double astronomicalTwilightMorning;
        public double astronomicalTwilightEvening;
    }

    /**
     * time calculations
     */
    public static class Time
    {
        public Time(){}

        public Time(double hhi)
        {

            double m 				= frac(hhi)*60;
            int h 				= Int(hhi);
            double s 				= frac(m)*60.;
            m 					= Int(m);
            if (s>=59.5) { m++; s -= 60.; }
            if (m>=60)   { h++; m -= 60; }
            s 					= Math.round(s);

            //create String HH:MM and HH:MM:SS
            hhmmssString 			  = ""+h;
            if (h<10) hhmmssString  = "0"+h;
            hhmmssString 			 += ":";
            if (m<10) hhmmssString += "0";
            hhmmssString 			 += Int(m);
            hhmmString 			  = hhmmssString;
            hhmmssString 			 += ":";
            if (s<10) hhmmssString += "0";
            hhmmssString 			 += Int(s);

            //create String HH:MM = dec and HH:MM:SS = dec
            hhmmssStringdec = hhmmssString+" = "+hhi;
            hhmmStringdec = hhmmString+" = "+hhi;
        }

        public int hh;
        public int mm;
        public int ss;

        public String hhmmString;
        public String hhmmStringdec;
        public String hhmmssString;
        public String hhmmssStringdec;

    }
}

