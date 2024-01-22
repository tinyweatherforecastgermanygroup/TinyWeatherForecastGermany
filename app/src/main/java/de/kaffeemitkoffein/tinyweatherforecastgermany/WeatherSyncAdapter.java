package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class WeatherSyncAdapter extends AbstractThreadedSyncAdapter {

    ContentResolver contentResolver;
    private final Context context;
    public final static String EXTRAS_UPDATE_FLAG = "update_favorites";

    public static class UpdateFlags {
        public static final int FLAG_UPDATE_DEFAULT = 0;
        public static final int FLAG_UPDATE_WEATHER = 1;
        public static final int FLAG_UPDATE_WARNINGS = 2;
        public static final int FLAG_UPDATE_TEXTS = 4;
        public static final int FLAG_UPDATE_POLLEN = 8;
        public static final int FLAG_UPDATE_LAYERS = 16;
        public static final int FLAG_UPDATE_FAVORITES = 32;
        public static final int FLAG_UPDATE_FORCE = 1024;
    }


    public WeatherSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
        this.context = context.getApplicationContext();
    }

    public WeatherSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        contentResolver = context.getContentResolver();
        this.context = context.getApplicationContext();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"SyncAdapter called. Performing sync operations.");
        Update(context,bundle);
    }

    public static String IC_ID = "WEATHER_NOTIFICATION";
    public static String WARNING_NC_ID_SKELETON = "WEATHER_WARNING";
    public static String WARNING_NC_GROUP = "de.kaffeemitkoffein.tinyweatherforecastgermany.WARNINGS";

    public static Notification getNotification(Context context, NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final int IC_IMPORTANCE = NotificationManager.IMPORTANCE_LOW;
            final int WARNING_NC_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel nc = new NotificationChannel(IC_ID, context.getResources().getString(R.string.service_notification_categoryname), IC_IMPORTANCE);
            nc.setDescription(context.getResources().getString(R.string.service_notification_categoryname));
            nc.setShowBadge(true);
            notificationManager.createNotificationChannel(nc);
        }
        // Generate a unique ID for the notification, derived from the current time. The tag ist static.
        Notification.Builder notificationBuilder;
        Notification n;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(context.getApplicationContext(), IC_ID);
        } else {
            notificationBuilder = new Notification.Builder(context.getApplicationContext());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            n = notificationBuilder
                    .setContentTitle(context.getResources().getString(R.string.service_notification_title))
                    .setStyle(new Notification.BigTextStyle().bigText(context.getResources().getString(R.string.service_notification_text0)))
                    .setSmallIcon(R.mipmap.ic_launcher_bw)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setProgress(4, 0, false)
                    .setChannelId(IC_ID)
                    .build();
        } else {
            n = notificationBuilder
                    .setContentTitle(context.getResources().getString(R.string.service_notification_title))
                    .setStyle(new Notification.BigTextStyle().bigText(context.getResources().getString(R.string.service_notification_text0)))
                    // .setContentText(getResources().getString(R.string.service_notification_text0))
                    .setSmallIcon(R.mipmap.ic_launcher_bw)
                    .setProgress(6, 0, true)
                    .setAutoCancel(true)
                    .build();
        }
        return n;
    }

    public static Intent getWarningIntent(Context context, WeatherWarning weatherWarning) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, weatherWarning.getPlainTextWarning(context, true));
        intent.putExtra(Intent.EXTRA_SUBJECT, weatherWarning.event);
        return intent;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public static PendingIntent getWarningPendingIntent(Context context, WeatherWarning weatherWarning, int uniqueNotificationID) {
        Intent intent = getWarningIntent(context, weatherWarning);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= 23) {
            pendingIntent = PendingIntent.getActivity(context, uniqueNotificationID, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            pendingIntent = PendingIntent.getActivity(context, uniqueNotificationID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        return pendingIntent;
    }

    public static Notification getWarningNotification(Context context, NotificationManager notificationManager, WeatherWarning weatherWarning, String sortKey, int uniqueNotificationID) {
        final String notificationChannelID = WeatherSettings.getNotificationChannelID(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final int WARNING_NC_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel nc = new NotificationChannel(notificationChannelID, context.getResources().getString(R.string.preference_category_warnings), WARNING_NC_IMPORTANCE);
            nc.setDescription(context.getResources().getString(R.string.preference_category_warnings));
            if (WeatherSettings.LEDEnabled(context)) {
                nc.enableLights(true);
                nc.setLightColor(WeatherSettings.getLEDColor(context));
            }
            nc.setShowBadge(true);
            notificationManager.createNotificationChannel(nc);
        }
        Intent intent = new Intent(context, WeatherWarningActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= 23) {
            pendingIntent = PendingIntent.getActivity(context, uniqueNotificationID, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(context, uniqueNotificationID, intent, 0);
        }
        Bitmap warningIconBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.warning_icon);
        Bitmap iconMutable = warningIconBitmap.copy(Bitmap.Config.ARGB_8888, true);
        ThemePicker.applyColor(iconMutable, weatherWarning.getWarningColor());
        String notificationBody = weatherWarning.description;
        String expires = WeatherWarnings.getExpiresMiniString(context, weatherWarning);
        expires = expires.replaceFirst(String.valueOf(expires.charAt(0)), String.valueOf(expires.charAt(0)).toUpperCase());
        notificationBody = WeatherSettings.getSetStationLocation(context).getDescription(context).toUpperCase(Locale.getDefault()) + ": " + notificationBody + " (" + expires + ".)";
        // construct pending intent for sharing
        PendingIntent shareWarningPendingIntent = getWarningPendingIntent(context, weatherWarning, uniqueNotificationID);
        Notification n;
        Notification.Builder notificationBuilder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(context.getApplicationContext(), notificationChannelID);
        } else {
            notificationBuilder = new Notification.Builder(context.getApplicationContext());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder
                    .setContentTitle(weatherWarning.headline)
                    .setSmallIcon(WeatherIcons.getIconResource(context, WeatherIcons.WARNING_ICON))
                    .setStyle(new Notification.BigTextStyle().bigText(notificationBody))
                    .setLargeIcon(iconMutable)
                    .setOngoing(false)
                    .setChannelId(notificationChannelID)
                    .setContentIntent(pendingIntent)
                    .setShowWhen(true)
                    .setWhen(weatherWarning.onset)
                    .setGroup(WARNING_NC_GROUP)
                    .setSortKey(sortKey);
            // for api level 23 and above
            Notification.Action.Builder actionBuilder = new Notification.Action.Builder(WeatherIcons.getIconResource(context, WeatherIcons.IC_SHARE), context.getResources().getString(R.string.logging_button_share), shareWarningPendingIntent);
            Notification.Action notificationAction = actionBuilder.build();
            notificationBuilder.addAction(notificationAction);
        } else {
            notificationBuilder
                    .setContentTitle(weatherWarning.headline)
                    .setSmallIcon(WeatherIcons.getIconResource(context, WeatherIcons.WARNING_ICON))
                    .setStyle(new Notification.BigTextStyle().bigText(notificationBody))
                    .setContentIntent(pendingIntent)
                    .setShowWhen(true)
                    .setWhen(weatherWarning.onset);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // for api level 23 and above
                Notification.Action.Builder actionBuilder = new Notification.Action.Builder(WeatherIcons.getIconResource(context, WeatherIcons.IC_SHARE), context.getResources().getString(R.string.logging_button_share), shareWarningPendingIntent);
                Notification.Action notificationAction = actionBuilder.build();
                notificationBuilder.addAction(notificationAction);
            } else {
                notificationBuilder.addAction(WeatherIcons.getIconResource(context, WeatherIcons.IC_SHARE), context.getResources().getString(R.string.logging_button_share), shareWarningPendingIntent);
            }
            if (WeatherSettings.LEDEnabled(context)) {
                notificationBuilder.setLights(WeatherSettings.getLEDColor(context), 200, 1000);
            }
        }
        n = notificationBuilder.build();
        return n;
    }


    public static boolean launchWeatherWarningNotifications(Context context, ArrayList<WeatherWarning> warnings, boolean discardAlreadyNotified) {
        WeatherWarnings.clearNotified(context);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        PrivateLog.log(context, PrivateLog.ALERTS, PrivateLog.INFO, "Checking warnings..." + warnings.size());
        Weather.WeatherLocation weatherLocation = WeatherSettings.getSetStationLocation(context);
        ArrayList<WeatherWarning> locationWarnings = WeatherWarnings.getWarningsForLocation(context, warnings, weatherLocation);
        PrivateLog.log(context, PrivateLog.ALERTS, PrivateLog.INFO, "Checking warnings, found " + locationWarnings.size());
        boolean notified = false;
        for (int i = 0; i < locationWarnings.size(); i++) {
            WeatherWarning warning = locationWarnings.get(i);
            if (warning.getSeverity() >= WeatherSettings.getWarningsNotifySeverity(context)) {
                if (discardAlreadyNotified || !WeatherWarnings.alreadyNotified(context, warning)) {
                    int id = WeatherSettings.getUniqueNotificationIdentifier(context);
                    Notification notification = getWarningNotification(context, notificationManager, warning, Integer.toString(i), id);
                    notificationManager.notify(id, notification);
                    notified = true;
                    WeatherWarnings.addToNotified(context, warning, id);
                    PrivateLog.log(context, PrivateLog.ALERTS, PrivateLog.INFO, "Notifying " + warning.identifier + " " + warning.headline);
                } else {
                    PrivateLog.log(context, PrivateLog.ALERTS, PrivateLog.INFO, "already notified " + locationWarnings.get(i).headline);
                }
            } else {
                PrivateLog.log(context, PrivateLog.ALERTS, PrivateLog.INFO, "Severity too low to notify " + locationWarnings.get(i).headline);
            }
        }
        if (notified) {
            CancelNotificationBroadcastReceiver.setCancelNotificationsAlarm(context);
        }
        return notified;
    }


    public static void cancelDeprecatedWarningNotifications(Context context, NotificationManager notificationManager) {
        ArrayList<Integer> expiredNotificationIDs = WeatherWarnings.getExpiredWarningIds(context);
        if (notificationManager==null){
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
        if (notificationManager != null) {
            for (int i = 0; i < expiredNotificationIDs.size(); i++) {
                int id = expiredNotificationIDs.get(i);
                notificationManager.cancel(id);
                PrivateLog.log(context, PrivateLog.SYNC, PrivateLog.INFO, "Cancelled expired notification #" + id);
            }
        } else {
            PrivateLog.log(context, PrivateLog.SYNC, PrivateLog.ERR, "NotificationManager is null, cannot cancel expired notifications.");
        }
    }

    public static void resetNotifications(Context context, NotificationManager notificationManager){
        if (notificationManager==null){
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
        cancelDeprecatedWarningNotifications(context,notificationManager);
        ArrayList<WeatherWarning> weatherWarnings = WeatherWarnings.getWarningsForLocation(context,null,null);
        launchWeatherWarningNotifications(context,weatherWarnings,false);
    }

    public static void Update(final Context context, Bundle bundle) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // cancel deprecated warnings
        if (WeatherSettings.notifyWarnings(context)) {
            cancelDeprecatedWarningNotifications(context, notificationManager);
        }
        int updateFlags = UpdateFlags.FLAG_UPDATE_DEFAULT;
        if (bundle!=null){
            updateFlags = bundle.getInt(EXTRAS_UPDATE_FLAG);
        }
        boolean update_weather = ((updateFlags&UpdateFlags.FLAG_UPDATE_WEATHER)==UpdateFlags.FLAG_UPDATE_WEATHER);
        boolean update_warnings = ((updateFlags&UpdateFlags.FLAG_UPDATE_WARNINGS)==UpdateFlags.FLAG_UPDATE_WARNINGS);
        boolean update_texts = ((updateFlags&UpdateFlags.FLAG_UPDATE_TEXTS)==UpdateFlags.FLAG_UPDATE_TEXTS);
        boolean update_pollen = ((updateFlags&UpdateFlags.FLAG_UPDATE_POLLEN)==UpdateFlags.FLAG_UPDATE_POLLEN);
        boolean update_layers = ((updateFlags&UpdateFlags.FLAG_UPDATE_LAYERS)==UpdateFlags.FLAG_UPDATE_LAYERS);
        boolean update_favorites = ((updateFlags&UpdateFlags.FLAG_UPDATE_FAVORITES)==UpdateFlags.FLAG_UPDATE_FAVORITES);
        boolean update_forced = ((updateFlags&UpdateFlags.FLAG_UPDATE_FORCE)==UpdateFlags.FLAG_UPDATE_FORCE);
        ArrayList<Weather.WeatherLocation> weatherLocations = new ArrayList<Weather.WeatherLocation>();
        if (update_favorites){
            weatherLocations = StationFavorites.getFavorites(context);
        }
        // check if an arraylist of stations was provided, if not get the one from settings
        boolean getLocationFromSettings = false;
        if (weatherLocations.size() == 0) {
            getLocationFromSettings = true;
        }
        // when arrayList of stations is null or empty, get the station from settings
        if (getLocationFromSettings) {
            weatherLocations = new ArrayList<Weather.WeatherLocation>();
            weatherLocations.add(WeatherSettings.getSetStationLocation(context));
        }
        if (WeatherSettings.loggingEnabled(context)){
            if (update_forced){
                PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"This is a user-triggered, forced sync.");
                update_weather = true; update_texts = true; update_pollen = true; update_layers = true;
                if (!WeatherSettings.areWarningsDisabled(context)){
                    update_warnings = true;
                }
            }
            PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"Syncing stations:");
            for (int i=0; i<weatherLocations.size(); i++){
                PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"-> "+weatherLocations.get(i).getOriginalDescription()+" ["+weatherLocations.get(i).getName()+"]");
            }
            PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"Last syncs: ");
            PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"Weather : "+Weather.getSimpleDateFormat(Weather.SimpleDateFormats.DETAILED).format(WeatherSettings.Updates.getLastUpdate(context,WeatherSettings.Updates.Category.WEATHER))+", enabled: "+WeatherSettings.Updates.isSyncEnabled(context, WeatherSettings.Updates.Category.WEATHER)+" , due: "+WeatherSettings.Updates.isSyncDue(context, WeatherSettings.Updates.Category.WEATHER)+" , forced: "+update_weather);
            PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"Warnings: "+Weather.getSimpleDateFormat(Weather.SimpleDateFormats.DETAILED).format(WeatherSettings.Updates.getLastUpdate(context,WeatherSettings.Updates.Category.WARNINGS))+", enabled: "+WeatherSettings.Updates.isSyncEnabled(context, WeatherSettings.Updates.Category.WARNINGS)+", due: "+WeatherSettings.Updates.isSyncDue(context, WeatherSettings.Updates.Category.WARNINGS)+" , forced: "+update_warnings);
            PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"Texts   : "+Weather.getSimpleDateFormat(Weather.SimpleDateFormats.DETAILED).format(WeatherSettings.Updates.getLastUpdate(context,WeatherSettings.Updates.Category.TEXTS))+", enabled: "+WeatherSettings.Updates.isSyncEnabled(context, WeatherSettings.Updates.Category.TEXTS)+", due: "+WeatherSettings.Updates.isSyncDue(context, WeatherSettings.Updates.Category.TEXTS)+" , forced: "+update_texts);
            PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"Maps    : "+Weather.getSimpleDateFormat(Weather.SimpleDateFormats.DETAILED).format(WeatherSettings.Updates.getLastUpdate(context,WeatherSettings.Updates.Category.LAYERS))+", enabled: "+WeatherSettings.Updates.isSyncEnabled(context, WeatherSettings.Updates.Category.LAYERS)+", due: "+WeatherSettings.Updates.isSyncDue(context, WeatherSettings.Updates.Category.LAYERS)+" , forced: "+update_layers);
            PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"Pollen  : "+Weather.getSimpleDateFormat(Weather.SimpleDateFormats.DETAILED).format(WeatherSettings.Updates.getLastUpdate(context,WeatherSettings.Updates.Category.POLLEN))+", enabled: "+WeatherSettings.Updates.isSyncEnabled(context, WeatherSettings.Updates.Category.POLLEN)+", due: "+WeatherSettings.Updates.isSyncDue(context, WeatherSettings.Updates.Category.POLLEN)+" , forced: "+update_pollen);
        }
        // update Weather, also do the update if database has no data
        if ((WeatherSettings.Updates.isSyncDue(context, WeatherSettings.Updates.Category.WEATHER) && WeatherSettings.Updates.isSyncEnabled(context,WeatherSettings.Updates.Category.WEATHER))
                || (!Weather.hasCurrentWeatherInfo(context))
                || (update_weather)) {
            PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"-> syncing weather.");
            APIReaders.WeatherForecastRunnable weatherForecastRunnable = new APIReaders.WeatherForecastRunnable(context, weatherLocations) {
                @Override
                public void onPositiveResult() {
                    // update GadgetBridge and widgets
                    // MainActivity.updateAppViews(context, null);
                    // notify main class
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.MAINAPP_CUSTOM_REFRESH_ACTION);
                    context.sendBroadcast(intent);
                    PrivateLog.log(context, PrivateLog.SYNC, PrivateLog.INFO, "Weather update: success");
                    super.onPositiveResult();
                    WeatherSettings.Updates.setLastUpdate(context,WeatherSettings.Updates.Category.WEATHER,Calendar.getInstance().getTimeInMillis());
                }
                @Override
                public void onNegativeResult() {
                    PrivateLog.log(context, PrivateLog.SYNC, PrivateLog.ERR, "Weather update: failed, error.");
                    if (ssl_exception) {
                        PrivateLog.log(context, PrivateLog.SYNC, PrivateLog.ERR, "SSL exception detected by service.");
                        Intent ssl_intent = new Intent();
                        ssl_intent.setAction(MainActivity.MAINAPP_SSL_ERROR);
                        context.sendBroadcast(ssl_intent);
                    }
                    // need to update main app with old data
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.MAINAPP_CUSTOM_REFRESH_ACTION);
                    context.sendBroadcast(intent);
                    // need to update views with old data: GadgetBridge and widgets
                    // MainActivity.updateAppViews(context, null);
                }
            };
            weatherForecastRunnable.run();
        }
        // update warnings
        if ((WeatherSettings.Updates.isSyncDue(context, WeatherSettings.Updates.Category.WARNINGS) && WeatherSettings.Updates.isSyncEnabled(context,WeatherSettings.Updates.Category.WARNINGS))
                || (update_warnings)) {
            PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"-> syncing warnings.");
            APIReaders.WeatherWarningsRunnable weatherWarningsRunnable = new APIReaders.WeatherWarningsRunnable(context) {
                @Override
                public void onPositiveResult(ArrayList<WeatherWarning> warnings) {
                    super.onPositiveResult(warnings);
                    WeatherSettings.Updates.setLastUpdate(context,WeatherSettings.Updates.Category.WARNINGS,Calendar.getInstance().getTimeInMillis());
                    PrivateLog.log(context, PrivateLog.SYNC, PrivateLog.INFO, "Warnings updated successfully.");
                    // trigger update of views in activity
                    Intent intent = new Intent();
                    intent.setAction(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE);
                    intent.putExtra(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE_RESULT, true);
                    context.sendBroadcast(intent);
                    if (WeatherSettings.notifyWarnings(context)) {
                        // MainActivity.updateAppViews(context, null);
                        launchWeatherWarningNotifications(context, warnings, false);
                    }
                }
                public void onNegativeResult() {
                    // trigger update of views in activity
                    PrivateLog.log(context, PrivateLog.SYNC, PrivateLog.ERR, "Getting warnings failed.");
                    Intent intent = new Intent();
                    intent.setAction(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE);
                    intent.putExtra(WeatherWarningActivity.WEATHER_WARNINGS_UPDATE_RESULT, false);
                    context.sendBroadcast(intent);
                }
            };
            weatherWarningsRunnable.run();
        }
        if ((WeatherSettings.Updates.isSyncDue(context, WeatherSettings.Updates.Category.TEXTS)) || (update_texts)) {
            PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"-> syncing texts.");
            APIReaders.TextForecastRunnable textForecastRunnable = new APIReaders.TextForecastRunnable(context) {
                @Override
                public void onStart() {
                    //updateNotification(2);
                }

                @Override
                public void onPositiveResult() {
                    Intent intent = new Intent();
                    intent.setAction(TextForecastListActivity.ACTION_UPDATE_TEXTS);
                    intent.putExtra(TextForecastListActivity.UPDATE_TEXTS_RESULT, true);
                    PrivateLog.log(context, PrivateLog.SYNC, PrivateLog.INFO, "Weather texts updated successfully.");
                    context.sendBroadcast(intent);
                    WeatherSettings.Updates.setLastUpdate(context,WeatherSettings.Updates.Category.TEXTS,Calendar.getInstance().getTimeInMillis());
                }
            };
            textForecastRunnable.run();
        }
        if ((WeatherSettings.Updates.isSyncDue(context, WeatherSettings.Updates.Category.LAYERS) && WeatherSettings.Updates.isSyncEnabled(context,WeatherSettings.Updates.Category.LAYERS))
                || (update_layers)) {
            PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"-> syncing maps.");
            APIReaders.getLayerImages getLayerImages = new APIReaders.getLayerImages(context, WeatherLayer.getLayers(context)) {
                @Override
                public void onFinished(boolean success) {
                    Intent intent = new Intent();
                    intent.setAction(WeatherLayersActivity.ACTION_UPDATE_LAYERS);
                    intent.putExtra(WeatherLayersActivity.UPDATE_LAYERS_RESULT, success);
                    PrivateLog.log(context, PrivateLog.SYNC, PrivateLog.INFO, "Layer images updated successfully.");
                    context.sendBroadcast(intent);
                    WeatherSettings.Updates.setLastUpdate(context,WeatherSettings.Updates.Category.LAYERS,Calendar.getInstance().getTimeInMillis());
                }
            };
            getLayerImages.setForceUpdate(true);
            getLayerImages.run();
        }
        if ((WeatherSettings.Updates.isSyncDue(context, WeatherSettings.Updates.Category.POLLEN) && WeatherSettings.Updates.isSyncEnabled(context,WeatherSettings.Updates.Category.POLLEN))
                || (update_pollen)) {
            PrivateLog.log(context,PrivateLog.SYNC,PrivateLog.INFO,"-> syncing pollen.");
            APIReaders.PollenReader pollenReader = new APIReaders.PollenReader(context) {
                @Override
                public void onStart() {
                    // updateNotification(4);
                }
                @Override
                public void onFinished(boolean success) {
                    Intent intent = new Intent();
                    intent.setAction(Pollen.ACTION_UPDATE_POLLEN);
                    intent.putExtra(Pollen.UPDATE_POLLEN_RESULT, success);
                    PrivateLog.log(context, PrivateLog.SYNC, PrivateLog.INFO, "Pollen data updated successfully.");
                    context.sendBroadcast(intent);
                    WeatherSettings.Updates.setLastUpdate(context,WeatherSettings.Updates.Category.POLLEN,Calendar.getInstance().getTimeInMillis());

                }
            };
            pollenReader.run();
        }
        if (WeatherSettings.useBackgroundLocation(context)){
            WeatherLocationManager.checkForBackgroundLocation(context);
        }
        MainActivity.updateAppViews(context,null);
        Runnable cleanUpRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Weather.sanitizeDatabase(context);
                    TextForecasts.cleanTextForecastDatabase(context);
                } catch (Exception e) {
                    // nothing to do, this is lenient, because some clean-up may fail upon 1st app launch
                }
            }
        };
        cleanUpRunnable.run();
    }
}


