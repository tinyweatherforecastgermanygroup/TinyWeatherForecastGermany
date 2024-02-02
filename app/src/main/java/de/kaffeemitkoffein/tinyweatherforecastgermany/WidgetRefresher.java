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

import android.content.Context;
import android.content.Intent;

public class WidgetRefresher {

    public final static String EXTRA_SOURCE = "extra_source";
    public final static int FROM_SYSTEM = 0;
    public final static int FROM_MAIN_APP = 1;
    public final static int FROM_SYNCADAPTER = 2;
    public final static int FROM_SETTINGS = 3;
    public final static int FROM_ALARMCLOCK = 4;

    public static void refresh(Context context, int source){
        refreshBoldWidget(context,source);
        refreshClassicWidget(context,source);
        refreshClockWidget(context,source);
        refreshChartWidget(context,source);
    }

    public static void refreshBoldWidget(Context context, int source){
        // update bold widget
        Intent intent = new Intent(context,BoldWidget.class);
        intent.setAction(BoldWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        intent.putExtra(EXTRA_SOURCE,source);
        context.sendBroadcast(intent);
    }

    public static void refreshClassicWidget(Context context, int source){
        // update classic widget
        Intent intent = new Intent(context,ClassicWidget.class);
        intent.setAction(ClassicWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        intent.putExtra(EXTRA_SOURCE,source);
        context.sendBroadcast(intent);
    }

    public static void refreshClockWidget(Context context, int source){
        // update clock widget
        Intent intent = new Intent(context,ClockWidget.class);
        intent.setAction(ClockWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        intent.putExtra(EXTRA_SOURCE,source);
        context.sendBroadcast(intent);
    }

    public static void refreshChartWidget(Context context, int source){
        // update clock widget
        Intent intent = new Intent(context,ChartWidget.class);
        intent.setAction(ClockWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        intent.putExtra(EXTRA_SOURCE,source);
        context.sendBroadcast(intent);
    }

}
