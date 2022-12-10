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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class Settings extends PreferenceActivity{

    private Context context;

    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            updateValuesDisplay();
            if (s.equals(WeatherSettings.PREF_LOG_TO_LOGCAT)){
                WeatherSettings ws = new WeatherSettings(context);
                if (ws.log_to_logcat){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getResources().getString(R.string.alertdialog_1_title));
                    builder.setMessage(context.getResources().getString(R.string.alertdialog_1_text));
                    builder.setIcon(R.mipmap.ic_warning_white_24dp);
                    builder.setPositiveButton(context.getResources().getString(R.string.alertdialog_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton(context.getResources().getString(R.string.alertdialog_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            WeatherSettings weatherSettings = new WeatherSettings(context);
                            weatherSettings.applyPreference(WeatherSettings.PREF_LOG_TO_LOGCAT,false);
                            SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
                            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_LOG_TO_LOGCAT);
                            checkBoxPreference.setChecked(false);
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
            if (s.equals(WeatherSettings.PREF_USE_METERED_NETWORKS) && (!WeatherSettings.useMeteredNetworks(context)) && (WeatherSettings.notifyWarnings(context) || WeatherSettings.displayWarningsInWidget(context))){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getResources().getString(R.string.alertdialog_1_title));
                builder.setMessage(context.getResources().getString(R.string.preference_meterednetwork_notice));
                builder.setIcon(R.mipmap.warning_icon);
                builder.setPositiveButton(context.getResources().getString(R.string.alertdialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();           }
            if (s.equals(WeatherSettings.PREF_SERVE_GADGETBRIDGE)){
                setAlarmSettingAllowed();
            }
            if (s.equals(WeatherSettings.PREF_WARNINGS_DISABLE)){
                setShowWarningsInWidgetAllowed();
                setNotifyWarnings();
                setNotifySeverity();
            }
            if (s.equals(WeatherSettings.PREF_WIDGET_DISPLAYWARNINGS)){
                setShowWarningsInWidgetAllowed();
                WidgetRefresher.refresh(context);
            }
            if (s.equals(WeatherSettings.PREF_NOTIFY_WARNINGS)){
                setNotifyWarnings();
                setNotifySeverity();
            }
            if (s.equals(WeatherSettings.PREF_THEME)){
                recreate();
            }
            if (s.equals(WeatherSettings.PREF_ROTATIONMODE)){
                recreate();
            }
            if (s.equals(WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_DAYS)){
                WidgetRefresher.refreshChartWidget(context);
            }
        }
    };

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle bundle){
        setTheme(ThemePicker.GetTheme(this));
        super.onCreate(bundle);
        context = this;
        WeatherSettings.setRotationMode(this);
        addPreferencesFromResource(R.xml.preferences);
        if (!WeatherSettings.appReleaseIsUserdebug()){
            disableLogCatLogging();
            //disableClearNotifications();
        }
        if (!WeatherSettings.isTLSdisabled(context)){
            disableTLSOption();
        }
        if (WeatherSettings.getViewModel(context).equals(WeatherSettings.ViewModel.EXTENDED)){
            // do something
        }
        // allow changing alarm state?
        setAlarmSettingAllowed();
        // allow warinings in widget setting?
        setShowWarningsInWidgetAllowed();
        setNotifyWarnings();
        setNotifySeverity();
        updateValuesDisplay();
        // reset notifications option
        Preference resetNotifications = (Preference) findPreference(WeatherSettings.PREF_CLEARNOTIFICATIONS);
        if (resetNotifications!=null){
            resetNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(context,CancelNotificationBroadcastReceiver.class);
                    intent.setAction(CancelNotificationBroadcastReceiver.CLEAR_NOTIFICATIONS_ACTION);
                    sendBroadcast(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,context.getResources().getString(R.string.preference_clearnotifications_message),Toast.LENGTH_LONG).show();
                        }
                    });
                    return true;
                }
            });
        }
        // action bar layout
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
    }

    @SuppressWarnings("deprecation")
    public void disableLogCatLogging(){
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_LOG_TO_LOGCAT);
        checkBoxPreference.setChecked(false);
        checkBoxPreference.setEnabled(false);
        checkBoxPreference.setShouldDisableView(true);
        PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("PREF_screen_logging");
        preferenceScreen.removePreference(checkBoxPreference);
    }

    @SuppressWarnings("deprecation")
    public void disableClearNotifications(){
        Preference preference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_CLEARNOTIFICATIONS);
        preference.setEnabled(false);
        preference.setShouldDisableView(true);
        PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("PREF_screen_logging");
        preferenceScreen.removePreference(preference);
    }


    @SuppressWarnings("deprecation")
    public void disableTLSOption(){
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_DISABLE_TLS);
        checkBoxPreference.setChecked(false);
        checkBoxPreference.setEnabled(false);
        checkBoxPreference.setShouldDisableView(true);
        PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("PREF_screen_logging");
        preferenceScreen.removePreference(checkBoxPreference);
    }

    @SuppressWarnings("deprecation")
    public void setAlarmSettingAllowed(){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_SETALARM);
        checkBoxPreference.setEnabled(!weatherSettings.serve_gadgetbridge);
        checkBoxPreference.setShouldDisableView(true);
        if (weatherSettings.serve_gadgetbridge){
            checkBoxPreference.setSummary(context.getResources().getString(R.string.preference_setalarm_summary)+System.getProperty("line.separator")+context.getResources().getString(R.string.preference_setalarm_notice));
        } else {
            checkBoxPreference.setSummary(context.getResources().getString(R.string.preference_setalarm_summary));
        }
    }

    @SuppressWarnings("deprecation")
    public void setShowWarningsInWidgetAllowed(){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_WIDGET_DISPLAYWARNINGS);
        checkBoxPreference.setEnabled(!weatherSettings.warnings_disabled);
        checkBoxPreference.setShouldDisableView(true);
    }

    public void setNotifyWarnings(){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_NOTIFY_WARNINGS);
        checkBoxPreference.setEnabled(!weatherSettings.warnings_disabled);
        checkBoxPreference.setShouldDisableView(true);
    }

    public void setNotifySeverity(){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        ListPreference listPreference = (ListPreference) findPreference(WeatherSettings.PREF_WARNINGS_NOTIFY_SEVERITY);
        if ((weatherSettings.warnings_disabled) || (!weatherSettings.notify_warnings)){
            listPreference.setEnabled(false);
            listPreference.setShouldDisableView(true);
        } else {
            listPreference.setEnabled(true);
            listPreference.setShouldDisableView(false);
        }
        /*
        // String[] severities = getResources().getStringArray(R.array.display_notifySeverity_text);
        int minSeverity = WeatherWarning.Severity.toInt(weatherSettings.notifySeverity);
        int severityColor = WeatherWarning.Severity.getColor(this,minSeverity);
        String text = getResources().getString(R.string.preference_notifySeverity_summary);
        int position1 = text.indexOf("\"");
        int position2 = text.lastIndexOf("\"");
        Log.v("twfg","POS1 "+position1);
        Log.v("twfg","POS2 "+position2);
        if ((position1>0) && (position2>0)){
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new ForegroundColorSpan(severityColor),position1,position2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            listPreference.setSummary(spannableString);
        } else {
            listPreference.setSummary(text);
        }
        */
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onResume(){
        super.onResume();
        context = this;
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onPause(){
        super.onPause();
        if (listener!=null){
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    @SuppressWarnings("deprecation")
    private void updateValuesDisplay(){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        String gadgetbridge_packagename = sp.getString(WeatherSettings.PREF_GADGETBRIDGE_PACKAGENAME,WeatherSettings.PREF_GADGETBRIDGE_PACKAGENAME_DEFAULT);
        if (gadgetbridge_packagename.equals("")){
            gadgetbridge_packagename = WeatherSettings.PREF_GADGETBRIDGE_PACKAGENAME_DEFAULT;
            SharedPreferences.Editor preferences_editor = sp.edit();
            preferences_editor.putString(WeatherSettings.PREF_GADGETBRIDGE_PACKAGENAME, gadgetbridge_packagename);
            preferences_editor.commit();
            Toast.makeText(this,getResources().getString(R.string.preference_gadgetbridge_package_reset_toast),Toast.LENGTH_LONG).show();
            finish();
        }
        CheckBoxPreference warningsInWidget = (CheckBoxPreference) findPreference(WeatherSettings.PREF_WIDGET_DISPLAYWARNINGS);
        warningsInWidget.setSummary(getResources().getString(R.string.preference_displaywarninginwidget_summary)+" "+getResources().getString(R.string.battery_and_data_hint));
        CheckBoxPreference notifyWarnings = (CheckBoxPreference) findPreference(WeatherSettings.PREF_NOTIFY_WARNINGS);
        notifyWarnings.setSummary(getResources().getString(R.string.preference_notify_warnings_summary)+" "+getResources().getString(R.string.battery_and_data_hint));
        ListPreference displayRotation = (ListPreference) findPreference(WeatherSettings.PREF_ROTATIONMODE);
        displayRotation.setSummary(WeatherSettings.getDeviceRotationString(this));
     }

}
