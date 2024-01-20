/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021, 2022, 2023 Pawel Dube
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

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class OnBootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PrivateLog.log(context,PrivateLog.ONBOOT,PrivateLog.INFO,"received on boot completed broadcast.");
        if (intent!=null){
            PrivateLog.log(context,PrivateLog.ONBOOT,PrivateLog.INFO,"+-> intent passed");
            if (intent.getAction()!=null){
                PrivateLog.log(context,PrivateLog.ONBOOT,PrivateLog.INFO,"+-> intent has action");
                if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                    PrivateLog.log(context,PrivateLog.ONBOOT,PrivateLog.INFO,"+-> action is ACTION_BOOT_COMPLETED, triggering alarm update.");
                    MainActivity.registerSyncAdapter(context);
                    int i = WeatherWarnings.clearAllNotified(context);
                    PrivateLog.log(context,PrivateLog.ONBOOT,PrivateLog.INFO,"Cleared list of notified warnings: "+i+" warnings removed from list.");
                    if (WeatherSettings.notifyWarnings(context)){
                        PrivateLog.log(context,PrivateLog.ONBOOT,PrivateLog.INFO,"Triggering notification(s) for applicable warnings.");
                    }
                }
            }
        }
    }
}
