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

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PrivateLog {
    public final static String LOGHANDLE="TIWF";
    public final static String LOGFILENAME="logs.txt";
    public final static String CLIPBOARD_LOGLABEL="Logs for";
    public final static String[] CLIPBOARD_MIMETYPES={"text/plain"};
    public final static long LOG_MAX_FILESIZE = 1024 * 512;

    public static final String MAIN = "main";
    public static final String SERVICE = "service";
    public static final String ONBOOT = "onboot";
    public static final String GB = "wearable";
    public static final String UPDATER = "updater";
    public static final String DATA = "data";
    public static final String WARNINGS = "warnings";
    public static final String WIDGET = "widget";
    public static final String TEXTS = "texts";
    public static final String STATIONS = "stations";
    public static final String RADAR  = "radar";
    public static final String ALERTS  = "alerts";

    public static final int INFO = 0;
    public static final int WARN = 1;
    public static final int ERR  = 2;
    public static final int FATAL = 3;

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

    public static ArrayList<String> getLogsList(Context context){
        try {
            FileInputStream fileInputStream = new FileInputStream(context.getFileStreamPath(LOGFILENAME));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<String> logList = new ArrayList<String>();
            String line;
            int length;
            while ((line = bufferedReader.readLine()) != null) {
                logList.add(line);
            }
            fileInputStream.close();
            return logList;
        } catch (Exception e){
            return null;
        }
    }

    public static long getLogFilesize(Context context){
        File path = context.getFilesDir();
        File logfile = new File(path,LOGFILENAME);
        long logsize = logfile.length();
        return logsize;
    }

    private static boolean log(Context context, String s){
        if (loggingEnabled(context)) {
            if (logToLogcat(context)){
                Log.v(LOGHANDLE,s);
            }
            File path = context.getFilesDir();
            File logfile = new File(path,LOGFILENAME);
            long logsize = logfile.length();
            if (logsize > LOG_MAX_FILESIZE){
                logfile.delete();
            }
            if (!logfile.exists()){
                try {
                    if (logfile.createNewFile()){
                        PrivateLog.log(context,PrivateLog.MAIN,PrivateLog.INFO,"Logging started, new file created.");
                        PrivateLog.log(context,getInfoString(context));
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

    public static boolean log(Context context,String tag, int severity, String s){
        return log(context,tag.toUpperCase()+" ["+severity+"] "+s);
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

    public static Uri getLogUri(Context context){
        File file = new File(context.getFilesDir().getAbsolutePath(),LOGFILENAME);
        return Uri.parse(file.toString());
    }

    public static class AsyncGetLogs implements Runnable {

        private Context context;

        public AsyncGetLogs(Context context){
            this.context = context;
        }

        public void onNegativeResult(){
        }

        public void onPositiveResult(ArrayList<String> result) {
        }

        @Override
        public void run() {
            ArrayList<String> logs = getLogsList(context);
            if (logs==null){
                onNegativeResult();
            } else {
                onPositiveResult(logs);
            }
        }
    }

    public static String getDebugInfoString(Context context) {
        final String lineBreak = System.getProperty("line.separator");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(lineBreak);
        stringBuilder.append(lineBreak);
        stringBuilder.append("Debug information");
        stringBuilder.append(lineBreak);
        stringBuilder.append("=================");
        stringBuilder.append(lineBreak);
        return stringBuilder.toString();
    }


    public static String getInfoString(Context context) {
        final String lineBreak = System.getProperty("line.separator");
        DecimalFormat df = new DecimalFormat("#.##");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(lineBreak);
        stringBuilder.append("Device info:");
        stringBuilder.append(lineBreak);
        stringBuilder.append("------------------------------------------------------"); stringBuilder.append(lineBreak);
        stringBuilder.append("Android SDK version: "+android.os.Build.VERSION.SDK_INT); stringBuilder.append(lineBreak);
        stringBuilder.append("Android SDK version: "+ Build.VERSION.CODENAME); stringBuilder.append(lineBreak);
        stringBuilder.append("Android build: "+ Build.DISPLAY); stringBuilder.append(lineBreak);
        stringBuilder.append("Hardware: "+ Build.HARDWARE); stringBuilder.append(lineBreak);
        stringBuilder.append("Product: "+ Build.PRODUCT); stringBuilder.append(lineBreak);
        stringBuilder.append("Model: "+ Build.MODEL); stringBuilder.append(lineBreak);
        stringBuilder.append("Manufacturer: "+ Build.MANUFACTURER); stringBuilder.append(lineBreak);
        stringBuilder.append("App build: "+ BuildConfig.VERSION_CODE); stringBuilder.append(lineBreak);
        stringBuilder.append("App build name: "+ BuildConfig.VERSION_NAME); stringBuilder.append(lineBreak);
        stringBuilder.append("------------------------------------------------------"); stringBuilder.append(lineBreak);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); stringBuilder.append(lineBreak);
        stringBuilder.append(lineBreak);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        stringBuilder.append("Available display:");
        stringBuilder.append(lineBreak);
        stringBuilder.append("Logical density: "+displayMetrics.density);
        stringBuilder.append(lineBreak);
        stringBuilder.append("Scaled density: "+displayMetrics.scaledDensity);
        stringBuilder.append(lineBreak);
        stringBuilder.append("Width  (pixels): "+displayMetrics.widthPixels);
        stringBuilder.append(lineBreak);
        stringBuilder.append("Height (pixels): "+displayMetrics.heightPixels);
        stringBuilder.append(lineBreak);
        stringBuilder.append("x-dpi: "+displayMetrics.xdpi);
        stringBuilder.append(lineBreak);
        stringBuilder.append("y-dpi: "+displayMetrics.ydpi);
        stringBuilder.append(lineBreak);
        stringBuilder.append("calculated physical metrics:");
        stringBuilder.append(lineBreak);
        stringBuilder.append("Width  (x): "+df.format(displayMetrics.widthPixels/displayMetrics.xdpi) +" inch");
        stringBuilder.append(lineBreak);
        stringBuilder.append("Height (y): "+df.format(displayMetrics.heightPixels/displayMetrics.ydpi) +" inch");
        stringBuilder.append(lineBreak);
        stringBuilder.append("Pixel-independent metrics:");
        stringBuilder.append(lineBreak);
        // px = dp * (dpi/160)
        // px/(dpi/160) = dp
        stringBuilder.append("Width (x) in dp: "+Math.round(displayMetrics.widthPixels/(displayMetrics.xdpi/160)));
        stringBuilder.append(lineBreak);
        stringBuilder.append("Height (y) in dp: "+Math.round(displayMetrics.heightPixels/(displayMetrics.ydpi/160)));
        stringBuilder.append(lineBreak);
        // Mem Info
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        stringBuilder.append(lineBreak);
        stringBuilder.append("RAM available: "+memoryInfo.availMem/(1024*1024)+ "Mb RAM total: "+memoryInfo.totalMem/(1024*1024)+" Mb");
        stringBuilder.append(lineBreak);
        stringBuilder.append("Threshold for low memory state: "+memoryInfo.threshold/(1024*1024)+ " Mb");
        stringBuilder.append(lineBreak);
        if (memoryInfo.lowMemory){
            stringBuilder.append("The device is in a low memory state, potentially killing services and other processes.");
        } else {
            stringBuilder.append("The device is not in a low memory state.");
        }
        stringBuilder.append(lineBreak);
        return stringBuilder.toString();
    }

    public static String getCurrentStationInfoString(Context context){
        Weather.WeatherLocation weatherLocation = WeatherSettings.getSetStationLocation(context);
        final String lineBreak = System.getProperty("line.separator");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(lineBreak);
        stringBuilder.append("Currently set station in settings:");
        stringBuilder.append(lineBreak);
        stringBuilder.append("Station name: "+weatherLocation.name);
        stringBuilder.append(lineBreak);
        stringBuilder.append("Station description: "+weatherLocation.description);
        stringBuilder.append(lineBreak);
        return stringBuilder.toString();
    }


}
