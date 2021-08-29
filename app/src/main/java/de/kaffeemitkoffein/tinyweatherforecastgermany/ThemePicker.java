package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.*;

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
            return R.style.AppTheme_Solarized;
        } else {
            return R.style.AppTheme_SolarizedDark;
        }
    }

    public static void SetTheme(Context context){
        context.setTheme(GetTheme(context));
    }

    public static int getWidgetTextColor(Context context){
        /*
        if (isDarkTheme(context)){
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
         */
        int c;
        String themePreference = WeatherSettings.getThemePreference(context);
        if (!isDarkTheme(context)){
            c = R.color.colorText_Solarized;
        } else {
            c = R.color.colorText_SolarizedDark;
        }
        if (themePreference.equals(WeatherSettings.Theme.DARK)){
            c = R.color.colorText_DarkTheme;
        }
        if (themePreference.equals(WeatherSettings.Theme.LIGHT)){
            c = R.color.colorText_LightTheme;
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED)){
            c = R.color.colorText_Solarized;
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED_DARK)){
            c = R.color.colorText_SolarizedDark;
        }
        int color;
        if (android.os.Build.VERSION.SDK_INT>22){
            color = context.getResources().getColor(c,context.getTheme());
        } else {
            color= context.getResources().getColor(c);
        }
        float[] hsv = new float[3];
        Color.colorToHSV(color,hsv);
        int c2 = Color.HSVToColor(255,hsv);
        return  c2;
    }

    public static int getColorTextLight(Context context){
        int c;
        String themePreference = WeatherSettings.getThemePreference(context);
        if (!isDarkTheme(context)){
            c = R.color.colorTextLight_Solarized;
        } else {
            c = R.color.colorTextLight_SolarizedDark;
        }
        if (themePreference.equals(WeatherSettings.Theme.DARK)){
            c = R.color.colorTextLight_DarkTheme;
        }
        if (themePreference.equals(WeatherSettings.Theme.LIGHT)){
            c = R.color.colorTextLight_LightTheme;
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED)){
            c = R.color.colorTextLight_Solarized;
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED_DARK)){
            c = R.color.colorTextLight_SolarizedDark;
        }
        int color;
        if (android.os.Build.VERSION.SDK_INT>22){
            color = context.getResources().getColor(c,context.getTheme());
        } else {
            color= context.getResources().getColor(c);
        }
        float[] hsv = new float[3];
        Color.colorToHSV(color,hsv);
        int c2 = Color.HSVToColor(255,hsv);
        return  c2;
    }

    public static int getColorPrimary(Context context){
        int c;
        String themePreference = WeatherSettings.getThemePreference(context);
        if (!isDarkTheme(context)){
            c = R.color.colorPrimary_Solarized;
        } else {
            c = R.color.colorPrimary_SolarizedDark;
        }
        if (themePreference.equals(WeatherSettings.Theme.DARK)){
            c = R.color.colorPrimary_DarkTheme;
        }
        if (themePreference.equals(WeatherSettings.Theme.LIGHT)){
            c = R.color.colorPrimary_LightTheme;
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED)){
            c = R.color.colorPrimary_Solarized;
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED_DARK)){
            c = R.color.colorPrimary_SolarizedDark;
        }
        int color;
        if (android.os.Build.VERSION.SDK_INT>22){
            color = context.getResources().getColor(c,context.getTheme());
        } else {
            color= context.getResources().getColor(c);
        }
        float[] hsv = new float[3];
        Color.colorToHSV(color,hsv);
        int c2 = Color.HSVToColor(255,hsv);
        return  c2;
    }

    public static int getWidgetBackgroundDrawable(Context context){
        String themePreference = WeatherSettings.getThemePreference(context);
        if (themePreference.equals(WeatherSettings.Theme.DARK)){
            return R.drawable.roundedbox_dark;
        }
        if (themePreference.equals(WeatherSettings.Theme.LIGHT)){
            return R.drawable.roundedbox_light;
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED)){
            return R.drawable.roundedbox_solarized;
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED_DARK)){
            return R.drawable.roundedbox_solarizeddark;
        }
        if (!isDarkTheme(context)){
            return R.drawable.roundedbox_solarized;
        } else {
            return R.drawable.roundedbox_solarizeddark;
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

    public static void applyColor(final Bitmap bitmap, int color){
        Canvas canvas = new Canvas(bitmap);
        /*
            final Paint pTR = new Paint();
            pTR.setColor(color);
            pTR.setStyle(Paint.Style.FILL_AND_STROKE);
            for (int x=0; x<bitmap.getWidth();x++){
                for (int y=0; y<bitmap.getHeight(); y++){
                    if (bitmap.getPixel(x,y)==Color.WHITE){
                        canvas.drawPoint(x,y,pTR);
                    }
                }
            }

         */
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        paint.setColor(color);
        canvas.drawRect(0,0,bitmap.getWidth(),bitmap.getHeight(),paint);

    }

    public static void applyColor(Context context, final Bitmap bitmap, boolean fromWidget){
        if (fromWidget) {
            ThemePicker.applyColor(bitmap,ThemePicker.getWidgetTextColor(context));
        } else {
            ThemePicker.applyColor(bitmap,ThemePicker.getColorTextLight(context));
        }
    }

}
