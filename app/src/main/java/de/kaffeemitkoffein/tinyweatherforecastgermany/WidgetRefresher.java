/**
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

import android.content.Context;
import android.content.Intent;

public class WidgetRefresher {

    public static void refresh(Context context){
        // update classic widget
        Intent intent1 = new Intent(context,ClassicWidget.class);
        intent1.setAction(ClassicWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        context.sendBroadcast(intent1);
        // update large widget
        Intent intent2 = new Intent(context,LargeWidget.class);
        intent2.setAction(LargeWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        context.sendBroadcast(intent2);
        // update bold widget
        Intent intent3 = new Intent(context,BoldWidget.class);
        intent3.setAction(BoldWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        context.sendBroadcast(intent3);
        // update clock widget
        refreshClockWidget(context);
        // update chart widget
        refreshChartWidget(context);
    }

    public static void refreshClockWidget(Context context){
        // update clock widget
        Intent intent4 = new Intent(context,ClockWidget.class);
        intent4.setAction(ClockWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        context.sendBroadcast(intent4);
    }

    public static void refreshChartWidget(Context context){
        // update clock widget
        Intent intent5 = new Intent(context,ChartWidget.class);
        intent5.setAction(ClockWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        context.sendBroadcast(intent5);
    }

}
