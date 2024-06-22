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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
            if ((context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
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

    public static boolean hasBackgroundLocationPermission(Context context){
        // always present below 23
        if (android.os.Build.VERSION.SDK_INT < 23){
            return true;
        } else {
            if (android.os.Build.VERSION.SDK_INT < 29) {
                // below 29, background permission is always granted with normal "foreground" permission
                return WeatherLocationManager.hasLocationPermission(context);
            } else {
                if (context.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
                return true;
            }
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
        if (hasBackgroundLocationPermission(context)){
            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager!=null){
                ArrayList<Location> locationCandidates = new ArrayList<Location>();
                List<String> enabledProviders = locationManager.getProviders(true);
                for (int i=0; i<enabledProviders.size(); i++){
                    Location location = locationManager.getLastKnownLocation(enabledProviders.get(i));
                    if (location!=null){
                        locationCandidates.add(location);
                    }
                }
                // if known last locations were found, check which one is the most recent
                if (locationCandidates.size()>0){
                    Location newLocationCandidate=locationCandidates.get(0);
                    if (locationCandidates.size()>1){
                        for (int i=0; i<locationCandidates.size(); i++){
                            Location location = locationCandidates.get(i);
                            // always take the newer one
                            if (location.getTime()>newLocationCandidate.getTime()){
                                newLocationCandidate = location;
                            }
                            // if both are of the same time, take the more precise one
                            if (location.hasAccuracy()){
                                if (!newLocationCandidate.hasAccuracy()){
                                    newLocationCandidate = location;
                                } else {
                                    // means both locations have an accuracy; take the better one
                                    if (location.getAccuracy()<newLocationCandidate.getAccuracy()){
                                        newLocationCandidate = location;
                                    }
                                }
                            }
                        }
                    }
                    return newLocationCandidate;
                }
            }
        }
        return null;
    }

    /**
     * checks for a new location using known locations, meaning that no active location search is triggered.
     * Requires the location background permission.
     *
     * @param context
     * @return true if a new station was set, otherwise false (including not granted permission)
     */

    public static boolean checkForBackgroundLocation(Context context) {
        String message = "* Background location check: " + Weather.getSimpleDateFormat(Weather.SimpleDateFormats.TIME).format(new Date(Calendar.getInstance().getTimeInMillis()));
        PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,message);
        if (WeatherLocationManager.hasBackgroundLocationPermission(context)){
            Location location = WeatherLocationManager.getLastKnownLocation(context);
            if (location!=null) {
                Weather.WeatherLocation oldWeatherLocation = WeatherSettings.getSetStationLocation(context);
                Weather.WeatherLocation newWeatherLocation = new Weather.WeatherLocation(location);

                if (newWeatherLocation.time > oldWeatherLocation.time) {
                    // check for distance significance
                    float distance = oldWeatherLocation.toLocation().distanceTo(newWeatherLocation.toLocation());
                    if (distance > newWeatherLocation.accuracy){
                        // is significant geo
                        // find the closest station
                        Weather.WeatherLocation closestStation = findClosestStation(context,newWeatherLocation.toLocation());
                        if (closestStation!=null){
                            Weather.WeatherLocation currentStation = WeatherSettings.getSetStationLocation(context);
                            if (!closestStation.getName().equals(currentStation.getName())){
                                PrivateLog.log(context,PrivateLog.GEO,PrivateLog.INFO,"* New station from a background location: "+closestStation.getName()+" ["+closestStation.getOriginalDescription()+"]"+" (from "+currentStation.getName()+")");
                                // new station is different to old one
                                WeatherSettings.setStation(context,closestStation);
                                // set this flag so that the main activity can change the station from onResume
                                WeatherSettings.setWeatherUpdatedFlag(context,WeatherSettings.UpdateType.STATION);
                                // set flag so that at next app start the new location will be added to the spinner
                                WeatherSettings.setWeatherUpdatedFlag(context,WeatherSettings.UpdateType.STATION);
                                ContentResolver.requestSync(MainActivity.getManualSyncRequest(context,WeatherSyncAdapter.UpdateFlags.FLAG_UPDATE_DEFAULT|WeatherSyncAdapter.UpdateFlags.FLAG_NO_LOCATION_CHECK));
                                return true;
                            } else {
                                PrivateLog.log(context,PrivateLog.GEO,PrivateLog.INFO,"* closest station did not change (current: "+currentStation.getName()+" ["+currentStation.getOriginalDescription()+"]"+", location: "+closestStation.getName()+").");
                            }
                        } else {
                            PrivateLog.log(context,PrivateLog.GEO,PrivateLog.INFO,"* not able to find closest station.");
                        }
                    } else {
                        PrivateLog.log(context,PrivateLog.GEO,PrivateLog.INFO,"* no need to change station.");
                    }
                } else {
                    PrivateLog.log(context,PrivateLog.GEO,PrivateLog.INFO,"* no new location data.");
                }
            } else {
                PrivateLog.log(context,PrivateLog.GEO,PrivateLog.INFO,"* no last known location present.");
            }
        } else {
            PrivateLog.log(context,PrivateLog.GEO,PrivateLog.ERR,"* background location check failed due to missing permissions.");
        }
        return false;
    }

    public static Weather.WeatherLocation findClosestStation(Context context, Location location) {
        StationsManager stationsManager = new StationsManager(context);
        stationsManager.readStations();
        ArrayList<Weather.WeatherLocation> stations = stationsManager.getStations();
        if (stations.size()>0) {
            stations = StationsManager.sortStationsByDistance(stations,location);
            Weather.WeatherLocation closestStation = stations.get(0);
            // copy location data into stations data so that we have the real, more exact coordinates for future use
            // like e.g. warnings
            Weather.WeatherLocation resultStation = new Weather.WeatherLocation(location);
            resultStation.setName(closestStation.getName());
            resultStation.setDescription(closestStation.getOriginalDescription());
            return resultStation;
        }
        return null;
    }

    public static String getDescriptionGeo(Weather.WeatherLocation weatherLocation){
        if (weatherLocation!=null){
            if (weatherLocation.getOriginalDescription().contains("SWIS-PUNKT")){
                DecimalFormat decimalFormat = new DecimalFormat("##0.000");
                String latString = decimalFormat.format(weatherLocation.latitude);
                String lonString = decimalFormat.format(weatherLocation.longitude);
                return latString+" / "+lonString;
            }
        }
        return null;
    }

    public static String getDescriptionAlternate(Context context, Weather.WeatherLocation weatherLocation) {
        String newDescriptionAlternate = null;
        if (weatherLocation != null) {
            int[] types = {Areas.Area.Type.GEMEINDE, Areas.Area.Type.SEE, Areas.Area.Type.KUESTE, Areas.Area.Type.BINNENSEE};
            ArrayList<Areas.Area> areas = Areas.getAreas(context,types);
            int i = 0;
            while ((i<areas.size()) && (!areas.get(i).isInArea(weatherLocation))){
                i++;
            }
            if (i<areas.size()){
                Areas.Area nameArea = areas.get(i);
                newDescriptionAlternate = nameArea.name;
            } else {
                newDescriptionAlternate = getDescriptionGeo(weatherLocation);
            }
            WeatherSettings.setDescriptionAlternate(context,newDescriptionAlternate);
        }
        return newDescriptionAlternate;
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

    /*
     * Overrride this to get the location in the activity, call super to remove callback & hide spinner
     */

    public void newLocation(Location location){
        WeatherSettings.saveGPSfixtime(context,location.getTime());
        removeCallback();
    }

}
