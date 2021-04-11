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
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RawRadarmap {

    public byte[] productID;             // 2   00-01 byte, e.g. WX
    public byte[] timestamp;             // 6   02-07 byte, time in UTC
    public byte[] id10000;               // 5   08-12 radarID, composite is always 10000
    public byte[] timestampStr;          // 4   13-16 byte, timestamp in String Month-Year, e.g. 0421 for April 2021
    public byte[] idBY;                  // 2   17-18 byte, String, always "BY"
    public byte[] filesize;              // 7   19-25 file size as String ("1620142")
    public byte[] idVS;                  // 2   26-27 always "VS", if missing composite has been generated from local 100 km
                                         //           radar data
    public byte[] formatVersion;         // 2   28-29 byte, 0=mixed (100 km + 128 km radius), 1=100km radius, 2=128km radius, 3=150km
    public byte[] idSW;                  // 2   30-31 byte, String, always "SW"
    public byte[] radolanVersion;        // 9   32-40 byte, radolan version, String
    public byte[] idPR;                  // 2   41-42 byte, always "PR"

    public byte[] accuracy;              // 5   43-47 byte, String, "E-00" = whole numbers,
                                         //                     "E-01" = 1/10
                                         //                     "E-02" = 1/100
    public byte[] idINT;                 // 3   48-50 byte, always "INT"
    public byte[] interval;              // 4   51-54 byte, String, interval in minutes
    public byte[] idGP;                  // 2   55-56 byte, always "GP"
    public byte[] resolution;            // 9   57-65 byte, String, resolution, e.g. " 900x 900" = national composite
                                         //                                      "1100x 900" = extended national composite
                                         //                                      "1500x1400" = central europe composite
    public byte[] idMS;                  // 2   66-67 byte, always "VV"
    public byte[] stationStringLength;   // 68-70 byte, String, e.g. " 70", length of following string 0-999
    public byte[] stationString;         // 69-x  byte, String of radar stations, format is "<...>"
    public byte[] etxMark;               // 1 byte, 0x03 end of Text (=^C)
    public byte[] radarData;             // binary radar data, each int consists of 2 bytes, little endian

    public RawRadarmap(InputStream ip){
        try {
            productID           = new byte[2];
            int i = ip.read(productID);
            timestamp           = new byte[6];
            i = i + ip.read(timestamp);
            id10000             = new byte[5];
            i = i + ip.read(id10000);
            timestampStr        = new byte[4];
            i = i + ip.read(timestampStr);
            idBY                = new byte[2];
            i = i + ip.read(idBY);
            filesize            = new byte[7];
            i = i + ip.read(filesize);
            idVS                = new byte[2];
            i = i + ip.read(idVS);
            formatVersion       = new byte[2];
            i = i + ip.read(formatVersion);
            idSW                = new byte[2];
            i = i + ip.read(idSW);
            radolanVersion      = new byte[9];
            i = i + ip.read(radolanVersion);
            idPR                = new byte[2];
            i = i + ip.read(idPR);
            accuracy            = new byte[5];
            i = i + ip.read(accuracy);
            idINT               = new byte[3];
            i = i + ip.read(idINT);
            interval            = new byte[4];
            i = i + ip.read(interval);
            idGP                = new byte[2];
            i = i + ip.read(idGP);
            resolution          = new byte[9];
            i = i + ip.read(resolution);
            idMS                = new byte[2];
            i = i + ip.read(idMS);
            stationStringLength = new byte[3];
            i = i + ip.read(stationStringLength);
            int length = 0;
            try {
                stationString = new byte[Integer.getInteger(new String(stationStringLength))];
            } catch (Exception e){
                // this is catched up below with seeking for extMark
            }
            if (stationString!=null){
                i = i + ip.read(stationString);
            }
            // catch up to extMark
            etxMark = new byte[1];
            etxMark[0] = 0x0;
            while ((etxMark[0]!=0x03) && (i>0)){
                i = i + ip.read(etxMark);
            }
            radarData = new byte[1100*900];
            DataInputStream dataInputStream = new DataInputStream(ip);
            dataInputStream.readFully(radarData);
        } catch (IOException e){
            // nothing to do
        }
    }


}
