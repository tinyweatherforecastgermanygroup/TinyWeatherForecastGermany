package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.Log;

public final class ThemePicker {



    private ThemePicker(){

    }

    public static boolean isDarkTheme(Context context){
        int uiMode =  context.getResources().getConfiguration().uiMode;
        int nightMode = uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_NO){
            return false;
        }
        else if (nightMode == Configuration.UI_MODE_NIGHT_YES){
            return true;
        }
        else {
            // no specific theme set
            return true;
        }
    }

    public static int GetTheme(Context context){
        // use dark theme as default
        int themeId = R.style.AppTheme_Dark;
        if (!isDarkTheme(context)){
            themeId = R.style.AppTheme_Light;
            PrivateLog.log(context,Tag.MAIN,"Notice: device theme is not a night (dark) theme.");
        } else {
            PrivateLog.log(context,Tag.MAIN,"Notice: device theme is a night (dark) theme.");
        }
        return themeId;
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
