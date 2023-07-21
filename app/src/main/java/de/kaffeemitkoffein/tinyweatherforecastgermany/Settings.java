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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.*;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Context context;

    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if ((sharedPreferences != null) && (s != null)) {
                updateValuesDisplay();
                if (s.equals(WeatherSettings.PREF_LOG_TO_LOGCAT)) {
                    WeatherSettings ws = new WeatherSettings(context);
                    if (ws.log_to_logcat) {
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
                                weatherSettings.applyPreference(WeatherSettings.PREF_LOG_TO_LOGCAT, false);
                                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_LOG_TO_LOGCAT);
                                if (checkBoxPreference != null) {
                                    checkBoxPreference.setChecked(false);
                                    dialogInterface.dismiss();
                                }
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
                if ((s.equals(WeatherSettings.PREF_USE_METERED_NETWORKS) && (!WeatherSettings.useMeteredNetworks(context))) ||
                        ((s.equals(WeatherSettings.PREF_USE_WIFI_ONLY)) && (WeatherSettings.useWifiOnly(context)))
                                && ((WeatherSettings.notifyWarnings(context) || WeatherSettings.displayWarningsInWidget(context)))) {
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
                    alertDialog.show();
                }
                if (s.equals(WeatherSettings.PREF_SERVE_GADGETBRIDGE)) {
                    setAlarmSettingAllowed();
                }
                if (s.equals(WeatherSettings.PREF_WARNINGS_DISABLE)) {
                    setShowWarningsInWidgetAllowed();
                    setNotifyWarnings();
                    setNotifySeverity();
                }
                if (s.equals(WeatherSettings.PREF_WIDGET_DISPLAYWARNINGS)) {
                    setShowWarningsInWidgetAllowed();
                    WidgetRefresher.refresh(context);
                }
                if (s.equals(WeatherSettings.PREF_NOTIFY_WARNINGS)) {
                    setNotifyWarnings();
                    setNotifySeverity();
                }
                if (s.equals(WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_MINMAXUSE) ||
                        (s.equals(WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_MIN)) ||
                        (s.equals(WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_MAX))) {
                    setUseMinMax();
                    WidgetRefresher.refreshChartWidget(context);
                }
                if (s.equals(WeatherSettings.PREF_THEME)) {
                    recreate();
                }
                if (s.equals(WeatherSettings.PREF_ROTATIONMODE)) {
                    recreate();
                }
                if (s.equals(WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_DAYS)) {
                    WidgetRefresher.refreshChartWidget(context);
                }
            }
        }
    };

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle bundle) {
        setTheme(ThemePicker.GetTheme(this));
        super.onCreate(bundle);
        context = this;
        WeatherSettings.setRotationMode(this);
        addPreferencesFromResource(R.xml.preferences);
        updateValuesDisplay();
        // reset notifications option
        Preference resetNotifications = (Preference) findPreference(WeatherSettings.PREF_CLEARNOTIFICATIONS);
        if (resetNotifications != null) {
            resetNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(context, CancelNotificationBroadcastReceiver.class);
                    intent.setAction(CancelNotificationBroadcastReceiver.CLEAR_NOTIFICATIONS_ACTION);
                    sendBroadcast(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, context.getResources().getString(R.string.preference_clearnotifications_message), Toast.LENGTH_LONG).show();
                        }
                    });
                    return true;
                }
            });
        }
        // action bar layout
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
    }

    @SuppressWarnings("deprecation")
    public void disableLogCatLogging() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_LOG_TO_LOGCAT);
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(false);
            checkBoxPreference.setEnabled(false);
            checkBoxPreference.setShouldDisableView(true);
            PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("PREF_screen_logging");
            if (preferenceScreen != null) {
                preferenceScreen.removePreference(checkBoxPreference);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void disableClearNotifications() {
        Preference preference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_CLEARNOTIFICATIONS);
        if (preference != null) {
            preference.setEnabled(false);
            preference.setShouldDisableView(true);
            PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("PREF_screen_logging");
            if (preferenceScreen != null) {
                preferenceScreen.removePreference(preference);
            }
        }
    }


    @SuppressWarnings("deprecation")
    public void disableTLSOption() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_DISABLE_TLS);
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(false);
            checkBoxPreference.setEnabled(false);
            checkBoxPreference.setShouldDisableView(true);
            PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("PREF_screen_logging");
            if (preferenceScreen != null) {
                preferenceScreen.removePreference(checkBoxPreference);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void setAlarmSettingAllowed() {
        WeatherSettings weatherSettings = new WeatherSettings(context);
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_SETALARM);
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(!weatherSettings.serve_gadgetbridge);
            checkBoxPreference.setShouldDisableView(true);
            if (weatherSettings.serve_gadgetbridge) {
                checkBoxPreference.setSummary(context.getResources().getString(R.string.preference_setalarm_summary) + System.getProperty("line.separator") + context.getResources().getString(R.string.preference_setalarm_notice));
            } else {
                checkBoxPreference.setSummary(context.getResources().getString(R.string.preference_setalarm_summary));
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void setShowWarningsInWidgetAllowed() {
        WeatherSettings weatherSettings = new WeatherSettings(context);
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_WIDGET_DISPLAYWARNINGS);
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(!weatherSettings.warnings_disabled);
            checkBoxPreference.setShouldDisableView(true);
        }
    }

    public void setNotifyWarnings() {
        WeatherSettings weatherSettings = new WeatherSettings(context);
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_NOTIFY_WARNINGS);
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(!weatherSettings.warnings_disabled);
            checkBoxPreference.setShouldDisableView(true);
        }
    }

    public void setNotifySeverity() {
        WeatherSettings weatherSettings = new WeatherSettings(context);
        ListPreference listPreference = (ListPreference) findPreference(WeatherSettings.PREF_WARNINGS_NOTIFY_SEVERITY);
        if (listPreference != null) {
            if ((weatherSettings.warnings_disabled) || (!weatherSettings.notify_warnings)) {
                listPreference.setEnabled(false);
                listPreference.setShouldDisableView(true);
            } else {
                listPreference.setEnabled(true);
                listPreference.setShouldDisableView(false);
            }
        }
    }

    public void setNotifyLED() {
        CheckBoxPreference ledNotifications = (CheckBoxPreference) findPreference(WeatherSettings.PREF_WARNINGS_NOTIFY_LED);
        LEDColorPreference ledColorPreference = (LEDColorPreference) findPreference(WeatherSettings.PREF_LED_COLOR);
        if ((ledNotifications != null) && (ledColorPreference != null)) {
            if (!WeatherSettings.notifyWarnings(context) || (WeatherSettings.areWarningsDisabled(context))) {
                ledNotifications.setEnabled(false);
                ledNotifications.setShouldDisableView(true);
            } else {
                ledNotifications.setEnabled(true);
                ledNotifications.setShouldDisableView(false);
            }
            if ((!WeatherSettings.LEDEnabled(context)) || (WeatherSettings.areWarningsDisabled(context)) || (!WeatherSettings.notifyWarnings(context))) {
                ledColorPreference.setEnabled(false);
                ledColorPreference.setShouldDisableView(true);
            } else {
                ledColorPreference.setEnabled(true);
                ledColorPreference.setShouldDisableView(false);
            }
        }
    }

    public void setUseMinMax() {
        CheckBoxPreference checkBoxPreferenceChartUseMinMax = (CheckBoxPreference) findPreference(WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_MINMAXUSE);
        NumberPickerPreference numberPickerPreferenceChartRangeMin = (NumberPickerPreference) findPreference(WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_MIN);
        NumberPickerPreference numberPickerPreferenceChartRangeMax = (NumberPickerPreference) findPreference(WeatherSettings.PREF_DISPLAY_OVERVIEWCHART_MAX);
        if ((checkBoxPreferenceChartUseMinMax != null) && (numberPickerPreferenceChartRangeMin != null) && (numberPickerPreferenceChartRangeMax != null)) {
            if (!checkBoxPreferenceChartUseMinMax.isChecked()) {
                numberPickerPreferenceChartRangeMin.setEnabled(false);
                numberPickerPreferenceChartRangeMin.setShouldDisableView(true);
                numberPickerPreferenceChartRangeMax.setEnabled(false);
                numberPickerPreferenceChartRangeMax.setShouldDisableView(true);
                numberPickerPreferenceChartRangeMin.setSummary(context.getResources().getString(R.string.preference_screen_overviewchart_min_summary) + " -");
                numberPickerPreferenceChartRangeMax.setSummary(context.getResources().getString(R.string.preference_screen_overviewchart_max_summary) + " -");
            } else {
                numberPickerPreferenceChartRangeMin.setEnabled(true);
                numberPickerPreferenceChartRangeMin.setShouldDisableView(false);
                numberPickerPreferenceChartRangeMax.setEnabled(true);
                numberPickerPreferenceChartRangeMax.setShouldDisableView(false);
                numberPickerPreferenceChartRangeMin.setSummary(context.getResources().getString(R.string.preference_screen_overviewchart_min_summary) + " " + WeatherSettings.getOverviewChartMin(context));
                numberPickerPreferenceChartRangeMax.setSummary(context.getResources().getString(R.string.preference_screen_overviewchart_max_summary) + " " + WeatherSettings.getOverviewChartMax(context));
            }
        }
    }

    public boolean disableIfUnchecked(Preference target, final CheckBoxPreference source) {
        if ((target != null) && (source != null)) {
            if (source.isChecked()) {
                target.setEnabled(true);
                target.setShouldDisableView(false);
            } else {
                target.setEnabled(false);
                target.setShouldDisableView(true);
            }
            return true;
        }
        return false;
    }

    public void setUVHIdisplayMain() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(WeatherSettings.PREF_UVHI_MAINDISPLAY);
        if (!WeatherSettings.UVHIfetchData(context)) {
            WeatherSettings.setUVHImainDisplay(context,false);
            checkBoxPreference.setChecked(false);
            checkBoxPreference.setEnabled(false);
            checkBoxPreference.setShouldDisableView(true);
        } else {
            checkBoxPreference.setEnabled(true);
            checkBoxPreference.setShouldDisableView(false);
        }
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
        setUseMinMax();
        setNotifyLED();
        //setUVHIdisplayMain();
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
        if (warningsInWidget!=null){
            warningsInWidget.setSummary(getResources().getString(R.string.preference_displaywarninginwidget_summary)+" "+getResources().getString(R.string.battery_and_data_hint));
        }
        CheckBoxPreference notifyWarnings = (CheckBoxPreference) findPreference(WeatherSettings.PREF_NOTIFY_WARNINGS);
        if (notifyWarnings!=null){
            notifyWarnings.setSummary(getResources().getString(R.string.preference_notify_warnings_summary)+" "+getResources().getString(R.string.battery_and_data_hint));
        }
        ListPreference displayRotation = (ListPreference) findPreference(WeatherSettings.PREF_ROTATIONMODE);
        if (displayRotation!=null){
            displayRotation.setSummary(WeatherSettings.getDeviceRotationString(this));
        }
        Preference resetPreferences = (Preference) findPreference("PREF_reset");
        if (resetPreferences!=null){
            resetPreferences.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    MainActivity.askDialog(context,null,getResources().getString(R.string.alertdialog_3_title),
                            new String[] {getResources().getString(R.string.alertdialog_3_text1),"",getResources().getString(R.string.alertdialog_3_text2)},
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    WeatherSettings.resetPreferencesToDefault(context);
                                    Intent intent = new Intent(context, WelcomeActivity.class);
                                    Toast.makeText(context,getResources().getString(R.string.alertdialog_3_toast),Toast.LENGTH_LONG).show();
                                    // need to set this, because otherwise the WelcomeActivity might get called also from
                                    // a re-launched MainActivity.
                                    WeatherSettings.setAppLaunchedFlag(context);
                                    // force a replay
                                    intent.putExtra("mode","replay");
                                    startActivity(intent);
                                }
                            });
                    return true;
                };
            });
        }
        LEDColorPreference ledColorPreference = (LEDColorPreference) findPreference(WeatherSettings.PREF_LED_COLOR);
        if (ledColorPreference!=null){
            ledColorPreference.setColorItem(WeatherSettings.getLEDColorItem(context));
            ledColorPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    LEDColorPicker ledColorPicker = new LEDColorPicker(context);
                    ledColorPicker.setOnColorPickedListener(new LEDColorPicker.OnColorPickedListener() {
                        @Override
                        public void onColorSelected(int colorItem) {
                            WeatherSettings.setLEDColorItem(context,colorItem);
                        }
                    });
                    ledColorPicker.show();
                    return true;
                }
            });
        }
        /*
         See DataUpdateService.java, suitableNetworkAvailable(Context context):
         for api below 23, we can offer a check to use Wi-Fi only,
         for api above 22 we check for metered/unmetered networks.
         One of the preferences always needs to be removed.
         */
        PreferenceCategory preferenceCategoryGeneral = (PreferenceCategory) findPreference(WeatherSettings.PREF_CATEGORY_GENERAL);
        CheckBoxPreference wifiOnly = (CheckBoxPreference) findPreference(WeatherSettings.PREF_USE_WIFI_ONLY);
        CheckBoxPreference useMeteredNetworks = (CheckBoxPreference) findPreference(WeatherSettings.PREF_USE_METERED_NETWORKS);
        if (Build.VERSION.SDK_INT < 23){
            if (useMeteredNetworks!=null){
                preferenceCategoryGeneral.removePreference(useMeteredNetworks);
            }
        } else {
            if (wifiOnly!=null){
                preferenceCategoryGeneral.removePreference(wifiOnly);
            }
        }

     }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        updateValuesDisplay();
    }
}
