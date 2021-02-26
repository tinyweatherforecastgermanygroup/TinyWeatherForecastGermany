/*
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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PrivateLog {
    public final static String LOGFILENAME="logs.txt";
    public final static String CLIPBOARD_LOGLABEL="Logs for";
    public final static String[] CLIPBOARD_MIMETYPES={"text/plain"};

    private static boolean loggingEnabled(Context context){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        return weatherSettings.logging;
    }

    private static boolean logToLogcat(Context context){
        WeatherSettings weatherSettings = new WeatherSettings(context);
        return weatherSettings.log_to_logcat;
    }

    public static String getLogs(Context context){
        try {
            FileInputStream fileInputStream = new FileInputStream(context.getFileStreamPath(LOGFILENAME));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            StringBuilder stringBuilder = new StringBuilder();
            char[] buffer = new char[1024];
            String line;
            int length;
            while ((length = inputStreamReader.read(buffer)) != -1) {
                stringBuilder.append(buffer,0,length);
            }
            fileInputStream.close();
            return stringBuilder.toString();
        } catch (Exception e){
            return null;
        }
    }

    public static boolean log(Context context, String s){
        if (loggingEnabled(context)) {
            if (logToLogcat(context)){
                Log.v("TWFG",s);
            }
            File path = context.getFilesDir();
            File logfile = new File(path,LOGFILENAME);
            if (!logfile.exists()){
                try {
                    if (logfile.createNewFile()){
                        PrivateLog.log(context,"Logging started, new file created. Device info:");
                        PrivateLog.log(context,"------------------------------------------------------");
                        PrivateLog.log(context,"Android SDK version: "+android.os.Build.VERSION.SDK_INT);
                        PrivateLog.log(context,"Android SDK version: "+ Build.VERSION.CODENAME);
                        PrivateLog.log(context,"Android build: "+ Build.DISPLAY);
                        PrivateLog.log(context,"Hardware: "+ Build.HARDWARE);
                        PrivateLog.log(context,"Product: "+ Build.PRODUCT);
                        PrivateLog.log(context,"Model: "+ Build.MODEL);
                        PrivateLog.log(context,"Manufacturer: "+ Build.MANUFACTURER);
                        PrivateLog.log(context,"App build: "+ BuildConfig.VERSION_CODE);
                        PrivateLog.log(context,"App build name: "+ BuildConfig.VERSION_NAME);
                        PrivateLog.log(context,"------------------------------------------------------");
                    }
                } catch (Exception e) {
                    return false;
                }
            }
            try {
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS ");
                s = sdf.format(date) + s + System.lineSeparator();
                FileOutputStream fileOutputStream = context.openFileOutput(LOGFILENAME,Context.MODE_PRIVATE | Context.MODE_APPEND);
                byte[] content = s.getBytes();
                fileOutputStream.write(content);
                fileOutputStream.flush();
                fileOutputStream.close();
                return true;
            } catch (Exception e){
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean log(Context context,String tag, String s){
        return log(context,tag.toUpperCase()+": "+s);
    }

    public static boolean copyLogsToClipboard(Context context){
        String logs = getLogs(context);
        if (logs != null){
            String applicationName = String.valueOf(context.getApplicationContext().getApplicationInfo().loadDescription(context.getPackageManager()));
            ClipData clipData = new ClipData(CLIPBOARD_LOGLABEL+" "+ applicationName,CLIPBOARD_MIMETYPES,new ClipData.Item(logs));
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(clipData);
            return true;
        } else {
            return false;
        }
    }

    public static boolean clearLogs(Context context){
        File file = new File(context.getFilesDir().getAbsolutePath(),LOGFILENAME);
        return file.delete();
    }

    public static class AsyncGetLogs extends AsyncTask<Void, Void, String> {

        private Context context;

        public AsyncGetLogs(Context context){
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = getLogs(context);
            return result;
        }

        public void onNegativeResult(){
        }

        public void onPositiveResult(String result) {
        }

        protected void onPostExecute(String result) {
            if (result != null){
                onPositiveResult(result);
            } else {
                onNegativeResult();
            }
        }
    }

}
