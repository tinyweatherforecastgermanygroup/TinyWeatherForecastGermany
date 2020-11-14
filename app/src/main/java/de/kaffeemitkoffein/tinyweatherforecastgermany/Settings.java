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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class Settings extends PreferenceActivity{

    private Context context;

    @SuppressWarnings("deprecation")
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
            if (s.equals(WeatherSettings.PREF_SERVE_GADGETBRIDGE)){
                setAlarmSettingAllowed();
            }

        }
    };

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        context = this;
        addPreferencesFromResource(R.xml.preferences);
        if (!WeatherSettings.appReleaseIsUserdebug()){
            disableLogCatLogging();
        }
        if (!WeatherSettings.isTLSdisabled(context)){
            disableTLSOption();
        }
        // allow changing alarm state?
        setAlarmSettingAllowed();
        updateValuesDisplay();
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
     }

}
