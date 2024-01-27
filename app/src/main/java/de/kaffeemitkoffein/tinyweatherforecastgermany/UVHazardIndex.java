/*
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
