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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WeatherUpdateBroadcastReceiver extends BroadcastReceiver {

    public final static String UPDATE_ACTION = "de.kaffeemitkoffein.broadcast.REQUEST_UPDATE";

    @Override
    public void onReceive(final Context context, Intent intent) {
        PrivateLog.log(context,Tag.WUBR,"received a broadcast.");
        if (intent != null) {
            PrivateLog.log(context,Tag.WUBR,"+-> intent not null.");
            String action = intent.getAction();
            if (action != null){
                PrivateLog.log(context,Tag.WUBR,"+-> intent has action.");
            }
            if (intent.getAction().equals(UPDATE_ACTION)) {
                PrivateLog.log(context,Tag.WUBR,"+-> action is a custom request update, handling by the UpdateAlarmManager.");
                UpdateAlarmManager.updateAndSetAlarmsIfAppropriate(context);
            }
        }
    }
}