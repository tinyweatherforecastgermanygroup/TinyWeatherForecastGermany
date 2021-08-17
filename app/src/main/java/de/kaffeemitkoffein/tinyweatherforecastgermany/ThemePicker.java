package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.Log;

public final class ThemePicker {



    private ThemePicker(){

    }

    public static boolean isDarkTheme(Context context){
        String themePreference = WeatherSettings.getThemePreference(context);
        if ((themePreference.equals(WeatherSettings.Theme.DARK)) || (themePreference.equals(WeatherSettings.Theme.SOLARIZED_DARK))){
            return true;
        }
        if ((themePreference.equals(WeatherSettings.Theme.LIGHT)) || (themePreference.equals(WeatherSettings.Theme.SOLARIZED))){
            return false;
        }
        // check device theme
        int uiMode =  context.getResources().getConfiguration().uiMode;
        int nightMode = uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_NO){
            return false;
        }
        else if (nightMode == Configuration.UI_MODE_NIGHT_YES){
            return true;
        }
        else {
            // no specific theme set, dark is default
            return true;
        }
    }

    public static int GetTheme(Context context){
        String themePreference = WeatherSettings.getThemePreference(context);
        if (themePreference.equals(WeatherSettings.Theme.DARK)){
            return R.style.AppTheme_Dark;
        }
        if (themePreference.equals(WeatherSettings.Theme.LIGHT)){
            return R.style.AppTheme_Light;
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED)){
            return R.style.AppTheme_Solarized;
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED_DARK)){
            return R.style.AppTheme_SolarizedDark;
        }
        if (!isDarkTheme(context)){
            return R.style.AppTheme_Light;
        } else {
            return R.style.AppTheme_Dark;
        }
    }

    public static void SetTheme(Context context){
        context.setTheme(GetTheme(context));
    }

    public static int getWidgetTextColor(Context context){
        if (isDarkTheme(context)){
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    public static int getWidgetBackgroundColor(Context context){
        if (isDarkTheme(context)){
            return MainActivity.getColorFromResource(context,R.color.colorPrimaryDark_DarkTheme);
        } else {
            return MainActivity.getColorFromResource(context,R.color.colorPrimaryDark_LightTheme);
        }
    }

    public static String getWidgetBackgroundColorString(Context context){
        if (isDarkTheme(context)){
            return "000000";
        } else {
            return "eeeeee";
        }
    }

    public static int adaptColorToTheme(Context context, int color){
        if (!isDarkTheme(context)){
            final float DFACTOR = 0.6f;
            float hsv[] = new float[3];
            Color.colorToHSV(color,hsv);
            hsv[2] = hsv[2] * DFACTOR;
            return Color.HSVToColor(hsv);
        }
        return color;
    }

}
