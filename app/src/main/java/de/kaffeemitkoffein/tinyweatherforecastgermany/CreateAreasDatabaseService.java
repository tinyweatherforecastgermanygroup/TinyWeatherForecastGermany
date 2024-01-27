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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CreateAreasDatabaseService extends Service {

    private Context context;
    private Executor executor;

    @Override
    public void onCreate(){
        PrivateLog.log(this,PrivateLog.SERVICE,PrivateLog.INFO,"Area database ervice started.");
        executor = Executors.newSingleThreadExecutor();
        context = this;
    }

    Runnable createAreaDatabaseRunnable = new Runnable() {
        @Override
        public void run() {
            // update area database if:
            // a) database does not exist
            // b) if sql database is outdated
            // c) does not already run
            if ((!Areas.doesAreaDatabaseExist(getApplicationContext())) || (!Areas.AreaDatabaseCreator.areAreasUpToDate(getApplicationContext()))){
                if (!WeatherSettings.isAreaDatabaseLocked(context)){
                    PrivateLog.log(getApplicationContext(),PrivateLog.MAIN, PrivateLog.INFO,"Start building area database...");
                    Areas.AreaDatabaseCreator areasDataBaseCreator = new Areas.AreaDatabaseCreator(getApplicationContext(),executor){
                        @Override
                        public void showProgress(final int progress, final String text){
                            Intent i = new Intent();
                            i.setAction(MainActivity.MAINAPP_AREADB_PROGRESS);
                            i.putExtra(MainActivity.EXTRA_AREADB_PROGRESS_VALUE,progress);
                            i.putExtra(MainActivity.EXTRA_AREADB_PROGRESS_TEXT,text);
                            sendBroadcast(i);
                        }

                        @Override
                        public void onFinished(){
                            super.onFinished();
                            // notify warnings activity that update was not successful.
                            Intent i = new Intent();
                            i.setAction(MainActivity.MAINAPP_AREADB_READY);
                            PrivateLog.log(getApplicationContext(), PrivateLog.SERVICE, PrivateLog.WARN, "Area database created successfully.");
                            sendBroadcast(i);
                            WeatherSettings.unlockAreaDatabase(context);
                        }
                    };
                    areasDataBaseCreator.setProgressSteps(100);
                    areasDataBaseCreator.create();
                    WeatherSettings.lockAreaDatabase(context);
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        executor = Executors.newSingleThreadExecutor();
        executor.execute(createAreaDatabaseRunnable);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
