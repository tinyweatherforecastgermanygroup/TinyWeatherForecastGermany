package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;

import java.util.Calendar;

public class UVHazardIndex {

    public final static int[] UVIndexColors={0xff000000, 0xff4eb400, 0xffa0ce00, 0xfff7e400, 0xfff8b600, 0xfff88700, 0xfff85900, 0xffe82c0e, 0xffd8001d, 0xffff0099, 0xffb54cff, 0xff998cff, 0xffd58cbc, 0xffeaa8d3, 0xfff4c8e5};

    public class Values{
        public long[] timeArray;
        public int[] UVHazardIndexArray;
    }

    public static int getUvIndexFromColor(int color){
        int result = -1;
        for (int i=0; i<UVIndexColors.length; i++){
            if (UVIndexColors[i]==color){
                return i;
            }
        }
        return -1;
    }

}
