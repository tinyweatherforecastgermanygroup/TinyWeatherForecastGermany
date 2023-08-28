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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class WeatherLocationManager implements Application.ActivityLifecycleCallbacks {

    public static final int GPSFIXINTERVAL = 1000*60*60*24; // once per day
    public static final int PERMISSION_CALLBACK_LOCATION = 121;
    private Activity activity;
    private int activityHash;
    private LocationManager locationManager;
    private RelativeLayout gps_progress_holder;
    private Button gpsCancelButton;
    Context context;

    View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            removeCallback();
        }
    };

    public WeatherLocationManager(Context context){
        this.context = context;
    }

    public void setView(RelativeLayout relativeLayout){
        this.gps_progress_holder = relativeLayout;
    }

    public void registerCancelButton(Button cancelButton){
        this.gpsCancelButton = cancelButton;
        this.gpsCancelButton.setOnClickListener(cancelButtonListener);
    }

    private boolean isLegitActivity(Activity activity){
        if ((activity.getLocalClassName().equals("MainActivity")) || (activity.getLocalClassName().equals("WeatherWarningActivity"))){
            return true;
        }
        return false;
    }

    private void displaySpinner(){
        if (gps_progress_holder != null) {
            try {
                gps_progress_holder.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                //
            }
        }
    }

    private void removeSpinner() {
        if (gps_progress_holder != null) {
            try {
                gps_progress_holder.setVisibility(View.GONE);
            } catch (Exception e) {
                //
            }
        }
    }

    private void removeCallback(){
        if (locationManager!=null){
            locationManager.removeUpdates(locationListener);
            locationManager = null;
            removeSpinner();
        }
    }

    @SuppressLint("MissingPermission")
    private void updateActivity(Activity activity){
        if (this.activity==null){
            this.activity = activity;
            activityHash  = activity.hashCode();
        }
        if (activityHash!=activity.hashCode()){
            if (locationManager!=null) {
                removeCallback();
                removeSpinner();
            }
            this.activity = activity;
            this.activityHash = activity.hashCode();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        updateActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        updateActivity(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        updateActivity(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //removeCallback();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //removeCallback();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        //removeCallback();
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (isLegitActivity(activity)){
            removeCallback();
        }
    }

    public static boolean hasLocationPermission(Context context){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                // permission not granted
                PrivateLog.log(context.getApplicationContext(),PrivateLog.MAIN, PrivateLog.WARN,"No location permission granted by user.");
                return false;
            }
            else
            {
                // permission is granted, ok
                return true;
            }
        }
        else
        {
            // before api 23, permissions are always granted, so everything is ok
            return true;
        }
    }

    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            newLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            // nothing to do
        }

        @Override
        public void onProviderEnabled(String s) {
            // start
        }

        @Override
        public void onProviderDisabled(String s) {
            // start
        }
    };

    @SuppressLint("MissingPermission")
    public static Location getLastKnownLocation(Context context){
        Location location = null;
        if (hasLocationPermission(context)) {
            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                if (location == null) {
                    if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
                if (location == null) {
                    if (locationManager.getProvider(LocationManager.PASSIVE_PROVIDER) != null) {
                        if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        }
                    }
                }
            }
        }
        return location;
    }

    @SuppressLint("MissingPermission")
    public void startGPSLocationSearch(){
        if (activity!=null){
            if (!hasLocationPermission(activity.getApplicationContext())){
                // fail silently
            } else {
                displaySpinner();
                if (locationManager==null){
                    locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                    // register callback only in MainActivity & WeatherWarningActivity
                    if ((locationManager!=null) && (isLegitActivity(activity))){
                        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,locationListener,null);
                            displaySpinner();
                        } else
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,locationListener,null);
                            displaySpinner();
                        }  else {
                            // fail silently
                            removeSpinner();
                        }
                    }
                } else {
                    // fail silently
                }
            }
        }
    }

    public void stopGPSLocationSearch(){
        removeCallback();
    }

    public void checkLocation(){
        if (WeatherSettings.GPSAuto(context)){
            Location lastKnownLocation = getLastKnownLocation(context);
            if (lastKnownLocation!=null){
                if ((!WeatherSettings.isGPSFixOutdated(context,lastKnownLocation.getTime())) && (lastKnownLocation.getTime()>WeatherSettings.getlastGPSfixtime(context))){
                    newLocation(lastKnownLocation);
                    return;
                } else {
                    // nothing to do
                }
            }
            if (WeatherSettings.isLastGPSFixOutdated(context)){
                startGPSLocationSearch();
            }
        }
    }

    /*
     * Overrride this to get the location in the activity, call super to remove callback & hide spinner
     */

    public void newLocation(Location location){
        WeatherSettings.saveGPSfixtime(context,location.getTime());
        removeCallback();
    }

}
