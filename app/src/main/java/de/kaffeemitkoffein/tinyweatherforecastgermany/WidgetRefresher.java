/*
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
    }
}
