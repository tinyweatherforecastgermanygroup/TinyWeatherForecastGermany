package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DataUpdateService extends Service {

    public final static String SHOW_PROGRESS = "SHOW_PROGRESS";
    public final static String HIDE_PROGRESS = "HIDE_PROGRESS";

    private NotificationManager notificationManager;
    int notification_id;
    Notification notification;
    Notification.Builder notificationBuilder;

    private static boolean serviceStarted = false;

    public static String IC_ID = "WEATHER_NOTIFICATION";
    public static String IC_NAME = "Updating weather data";
    public static int    IC_IMPORTANCE = NotificationManager.IMPORTANCE_LOW;
    public static String SERVICEEXTRAS_UPDATE_WEATHER="SERVICEEXTRAS_UPDATE_WEATHER";
    public static String SERVICEEXTRAS_UPDATE_WARNINGS="SERVICEEXTRAS_UPDATE_WARNINGS";
    public static String SERVICEEXTRAS_UPDATE_TEXTFORECASTS="SERVICEEXTRAS_UPDATE_TEXTFORECASTS";

    private ConnectivityManager connectivityManager;

    private Runnable serviceTerminationRunnable = new Runnable() {
        @Override
        public void run() {
            stopThisService();
        }
    };

    private Runnable cleanUpRunnable = new Runnable() {
        @Override
        public void run() {
            updateNotification(3);
            // remove old weather entries from the data base
            // Weather.cleanDataBase(getApplicationContext());
            Weather.sanitizeDatabase(getApplicationContext());
            // remove old text forecasts
            TextForecasts.cleanTextForecastDatabase(getApplicationContext());
        }

    };

    private Object networkCallback;

    private static final long TIMEOUTTASK_DELAY = 1000*60*60*3; // 3 minutes

    private static Timer timer = new Timer();

    TimerTask timeOutTask = new TimerTask() {
        @Override
        public void run() {
            stopThisService();
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void stopThisService(){
        PrivateLog.log(this,PrivateLog.SERVICE,PrivateLog.INFO,"Shutting down service...");
        Intent intent = new Intent();
        intent.setAction(HIDE_PROGRESS);
        sendBroadcast(intent);
        notificationManager.cancel(notification_id);
        if ((connectivityManager!=null) && (networkCallback!=null) && (Build.VERSION.SDK_INT > 23)){
            connectivityManager.unregisterNetworkCallback((ConnectivityManager.NetworkCallback) networkCallback);
        }
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        PrivateLog.log(this,PrivateLog.SERVICE,PrivateLog.INFO,"Service started.");
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notification_id = (int) Calendar.getInstance().getTimeInMillis();
        notification = getNotification();
        startForeground(notification_id,notification);
        PrivateLog.log(this,PrivateLog.SERVICE,PrivateLog.INFO,"Service is foreground now.");
        serviceStarted = false;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        if (!serviceStarted){
            serviceStarted = true;
            connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            // perform service task only if:
            // 1) intent supplied telling what do do, AND
            // 2) internet connection is present
            if ((intent!=null) && (isConnectedToInternet())){
                boolean updateWeather = intent.getBooleanExtra(SERVICEEXTRAS_UPDATE_WEATHER,false);
                boolean updateWarnings = intent.getBooleanExtra(SERVICEEXTRAS_UPDATE_WARNINGS,false);
                boolean updateTextForecasts = intent.getBooleanExtra(SERVICEEXTRAS_UPDATE_TEXTFORECASTS,false);
                // create single thread
                final Executor executor = Executors.newSingleThreadExecutor();
                // put
                if ((Build.VERSION.SDK_INT > 23) && (connectivityManager!=null)){
                    Network network = connectivityManager.getActiveNetwork();
                    networkCallback = new ConnectivityManager.NetworkCallback(){
                        @Override
                        public void onLost(Network network) {
                            stopThisService();
                        }
                    };
                    connectivityManager.registerDefaultNetworkCallback((ConnectivityManager.NetworkCallback) networkCallback);
                } else {
                    timer.schedule(timeOutTask,TIMEOUTTASK_DELAY);
                }
                if (updateWeather) {
                    APIReaders.WeatherForecastRunnable weatherForecastRunnable = new APIReaders.WeatherForecastRunnable(this){
                        @Override
                        public void onStart(){
                            updateNotification(0);
                        }
                        @Override
                        public void onPositiveResult(){
                            // update GadgetBridge and widgets
                            UpdateAlarmManager.updateAppViews(context);
                            // notify main class
                            Intent intent = new Intent();
                            intent.setAction(MainActivity.MAINAPP_CUSTOM_REFRESH_ACTION);
                            sendBroadcast(intent);
                            PrivateLog.log(context,PrivateLog.SERVICE,PrivateLog.INFO,"Weather update: success");
                        }
                        @Override
                        public void onNegativeResult(){
                            PrivateLog.log(context,PrivateLog.SERVICE,PrivateLog.ERR,"Weather update: failed, error.");
                            if (ssl_exception){
                                PrivateLog.log(context,PrivateLog.SERVICE,PrivateLog.ERR,"SSL exception detected by service.");
                                Intent ssl_intent = new Intent();
                                ssl_intent.setAction(MainActivity.MAINAPP_SSL_ERROR);
                                sendBroadcast(ssl_intent);
                            }
                            // need to update main app with old data
                            Intent intent = new Intent();
                            intent.setAction(MainActivity.MAINAPP_CUSTOM_REFRESH_ACTION);
                            sendBroadcast(intent);
                            // need to update views with old data: GadgetBridge and widgets
                            UpdateAlarmManager.updateAppViews(context);
                        }
                    };
                    executor.execute(weatherForecastRunnable);
                }
                if (updateWarnings) {
                    APIReaders.WeatherWarningsRunnable weatherWarningsRunnable = new APIReaders.WeatherWarningsRunnable(this){
                        @Override
                        public void onStart(){
                            updateNotification(1);
                        }
                        @Override
                        public void onPositiveResult(ArrayList<WeatherWarning> warnings){
                            super.onPositiveResult(warnings);
                            PrivateLog.log(getApplicationContext(),PrivateLog.SERVICE,PrivateLog.INFO,"Warnings updated successfully.");
                            // trigger update of views in activity
                            Intent intent = new Intent();
                            intent.setAction(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE);
                            intent.putExtra(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE_RESULT,true);
                            sendBroadcast(intent);
                        }
                        public void onNegativeResult(){
                            // trigger update of views in activity
                            PrivateLog.log(getApplicationContext(),PrivateLog.SERVICE,PrivateLog.ERR,"Getting warnings failed.");
                            Intent intent = new Intent();
                            intent.setAction(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE);
                            intent.putExtra(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE_RESULT,false);
                            sendBroadcast(intent);
                        }
                    };
                    executor.execute(weatherWarningsRunnable);
                }
                if (updateTextForecasts){
                    APIReaders.TextForecastRunnable textForecastRunnable = new APIReaders.TextForecastRunnable(this){
                        @Override
                        public void onStart(){
                            updateNotification(2);
                        }
                        @Override
                        public void onPositiveResult(){
                            Intent intent = new Intent();
                            intent.setAction(TextForecastListActivity.ACTION_UPDATE_TEXTS);
                            intent.putExtra(TextForecastListActivity.UPDATE_TEXTS_RESULT,true);
                            sendBroadcast(intent);
                            WeatherSettings.setLastTextForecastsUpdateTime(getApplicationContext(),Calendar.getInstance().getTimeInMillis());
                        }
                    };
                    executor.execute(textForecastRunnable);
                }
                executor.execute(cleanUpRunnable);
                executor.execute(serviceTerminationRunnable);
            } else {
                // terminate immediately, because no intent with tasks delivered and/or no internet connection.
                PrivateLog.log(getApplicationContext(),PrivateLog.SERVICE,PrivateLog.WARN,"Nothing to do, service received no tasks and/or no internet connection available.");
                Intent i = new Intent();
                i.setAction(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE);
                i.putExtra(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE_RESULT,false);
                sendBroadcast(i);
                stopThisService();
            }
        } else {
            PrivateLog.log(getApplicationContext(),PrivateLog.SERVICE,PrivateLog.INFO,"Service already running.");
            // terminate, because service is already running
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        notificationManager.cancel(notification_id);
        // hide progressbar in main app
        Intent progressbar_intent = new Intent();
        progressbar_intent.setAction(MainActivity.MAINAPP_HIDE_PROGRESS);
        sendBroadcast(progressbar_intent);
        PrivateLog.log(this,PrivateLog.SERVICE,PrivateLog.INFO,"Service destroyed.");
    }

    @Deprecated
    private Notification getNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel nc = new NotificationChannel(IC_ID,IC_NAME,IC_IMPORTANCE);
            nc.setDescription(getResources().getString(R.string.service_notification_channelname));
            nc.setShowBadge(true);
            notificationManager.createNotificationChannel(nc);
        }
        // Generate a unique ID for the notification, derived from the current time. The tag ist static.
        Notification n;
        notificationBuilder = new Notification.Builder(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            n = notificationBuilder
                    .setContentTitle(getResources().getString(R.string.service_notification_title))
                    .setStyle(new Notification.BigTextStyle().bigText(getResources().getString(R.string.service_notification_text0)))
                    //.setContentText(getResources().getString(R.string.service_notification_text0))
                    .setSmallIcon(R.mipmap.ic_launcher_bw)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setProgress(4,0,true)
                    .setChannelId(IC_ID)
                    .build();
        } else {
            n = notificationBuilder
                    .setContentTitle(getResources().getString(R.string.service_notification_title))
                    .setStyle(new Notification.BigTextStyle().bigText(getResources().getString(R.string.service_notification_text0)))
                    // .setContentText(getResources().getString(R.string.service_notification_text0))
                    .setSmallIcon(R.mipmap.ic_launcher_bw)
                    .setProgress(4,0,true)
                    .setAutoCancel(true)
                    .build();
        }
        return n;
    }

    private void updateNotification(int state){
        notificationBuilder.setProgress(4,state,false);
        switch (state){
            case 0: notificationBuilder.setStyle(new Notification.BigTextStyle().bigText(getResources().getString(R.string.service_notification_text0))); break;
            case 1: notificationBuilder.setStyle(new Notification.BigTextStyle().bigText(getResources().getString(R.string.service_notification_text1))); break;
            case 2: notificationBuilder.setStyle(new Notification.BigTextStyle().bigText(getResources().getString(R.string.service_notification_text2))); break;
            case 3: notificationBuilder.setStyle(new Notification.BigTextStyle().bigText(getResources().getString(R.string.service_notification_text3)));
        }
        notificationManager.notify(notification_id,notificationBuilder.build());
    }

    public static boolean isConnectedToInternet(Context context){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager!=null){
                // use networkInfo for api below 23
                if (Build.VERSION.SDK_INT < 23){
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null) {
                        // returns if the network can establish connections and pass data.
                        return networkInfo.isConnected();
                    }
                    return false;
                // use connectivityManager on api 23 and higher
                } else {
                    Network network = connectivityManager.getActiveNetwork();
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                    if (networkCapabilities==null) {
                        // no network
                        return false;
                    } else {
                        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))){
                            // valid internet conn
                            return true;
                        } else {
                            // conn is not proven
                            return false;
                        }
                    }
                }
            }
        } catch (Exception e){
            // connectivityManager not found, return true to be safe
        }
        return true;
    }

    private boolean isConnectedToInternet(){
        return isConnectedToInternet(this);
    }

}
