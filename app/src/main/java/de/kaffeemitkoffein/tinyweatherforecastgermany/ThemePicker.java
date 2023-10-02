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

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.TextView;

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
        return getColor(context,ThemeColor.TEXT);
    }

    public static int getColorTextLight(Context context){
         return getColor(context,ThemeColor.TEXTLIGHT);
    }

    public static int getColorPrimary(Context context){
        return getColor(context,ThemeColor.PRIMARY);
    }

    public static class ThemeColor {
        public static final int PRIMARY = 0;
        public static final int PRIMARYDARK = 1;
        public static final int PRIMARYLIGHT = 2;
        public static final int ACCENT = 3;
        public static final int TEXT = 4;
        public static final int TEXTDARK = 5;
        public static final int TEXTLIGHT = 6;
        public static final int WELCOMEBACKGROUND = 7;
        public static final int WIDGETBACKGROUND = 8;
        public static final int SECONDARY = 9;
        public static final int YELLOW = 10;
        public static final int ORANGE = 11;
        public static final int RED = 12;
        public static final int MAGENTA = 13;
        public static final int VIOLET = 14;
        public static final int BLUE = 15;
        public static final int CYAN = 16;
        public static final int GREEN = 17;
        public static final int[] VALUES_SOLARIZED = new int[]
                {R.color.colorPrimary_Solarized, R.color.colorPrimaryDark_Solarized, R.color.colorPrimaryLight_Solarized,
                 R.color.colorAccent_Solarized, R.color.colorText_Solarized, R.color.colorTextDark_Solarized,
                 R.color.colorTextLight_Solarized,R.color.colorWelcomeBackground_Solarized, R.color.colorWidgetBackground_Solarized,
                 R.color.colorSecondary_Solarized, R.color.colorAccentYellow_Solarized, R.color.colorAccentOrange_Solarized,
                 R.color.colorAccentRed_Solarized, R.color.colorAccentMagenta_Solarized, R.color.colorAccentViolet_Solarized,
                 R.color.colorAccentBlue_Solarized, R.color.colorAccentCyan_Solarized, R.color.colorAccentGreen_Solarized};
        public static final int[] VALUES_SOLARIZED_DARK = new int[]
                {R.color.colorPrimary_SolarizedDark, R.color.colorPrimaryDark_SolarizedDark, R.color.colorPrimaryLight_SolarizedDark,
                        R.color.colorAccent_SolarizedDark, R.color.colorText_SolarizedDark, R.color.colorTextDark_SolarizedDark,
                        R.color.colorTextLight_SolarizedDark,R.color.colorWelcomeBackground_SolarizedDark, R.color.colorWidgetBackground_SolarizedDark,
                        R.color.colorSecondary_SolarizedDark, R.color.colorAccentYellow_Solarized, R.color.colorAccentOrange_Solarized,
                        R.color.colorAccentRed_Solarized, R.color.colorAccentMagenta_Solarized, R.color.colorAccentViolet_Solarized,
                        R.color.colorAccentBlue_Solarized, R.color.colorAccentCyan_Solarized, R.color.colorAccentGreen_Solarized};
        public static final int[] VALUES_LIGHT = new int[]
                {R.color.colorPrimary_LightTheme, R.color.colorPrimaryDark_LightTheme, R.color.colorPrimaryLight_LightTheme,
                        R.color.colorAccent_LightTheme, R.color.colorText_LightTheme, R.color.colorTextDark_LightTheme,
                        R.color.colorTextLight_LightTheme,R.color.colorWelcomeBackground_LightTheme, R.color.colorWidgetBackground_LightTheme,
                        R.color.colorSecondary_LightTheme, R.color.colorAccentYellow_Solarized, R.color.colorAccentOrange_Solarized,
                        R.color.colorAccentRed_Solarized, R.color.colorAccentMagenta_Solarized, R.color.colorAccentViolet_Solarized,
                        R.color.colorAccentBlue_Solarized, R.color.colorAccentCyan_Solarized, R.color.colorAccentGreen_Solarized};
        public static final int[] VALUES_DARK= new int[]
                {R.color.colorPrimary_DarkTheme, R.color.colorPrimaryDark_DarkTheme, R.color.colorPrimaryLight_DarkTheme,
                        R.color.colorAccent_DarkTheme, R.color.colorText_DarkTheme, R.color.colorTextDark_DarkTheme,
                        R.color.colorTextLight_DarkTheme,R.color.colorWelcomeBackground_DarkTheme, R.color.colorWidgetBackground_DarkTheme,
                        R.color.colorSecondary_DarkTheme, R.color.colorAccentYellow_Solarized, R.color.colorAccentOrange_Solarized,
                        R.color.colorAccentRed_Solarized, R.color.colorAccentMagenta_Solarized, R.color.colorAccentViolet_Solarized,
                        R.color.colorAccentBlue_Solarized, R.color.colorAccentCyan_Solarized, R.color.colorAccentGreen_Solarized};
    }

    public static int getColor(Context context, int color){
        int c;
        String themePreference = WeatherSettings.getThemePreference(context);
        if (!isDarkTheme(context)){
            c = ThemeColor.VALUES_SOLARIZED[color];
        } else {
            c = ThemeColor.VALUES_SOLARIZED_DARK[color];
        }
        if (themePreference.equals(WeatherSettings.Theme.DARK)){
            c = ThemeColor.VALUES_DARK[color];
        }
        if (themePreference.equals(WeatherSettings.Theme.LIGHT)){
            c = ThemeColor.VALUES_LIGHT[color];
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED)){
            c = ThemeColor.VALUES_SOLARIZED[color];
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED_DARK)){
            c = ThemeColor.VALUES_SOLARIZED_DARK[color];
        }
        int result;
        if (android.os.Build.VERSION.SDK_INT>22){
            result = context.getResources().getColor(c,context.getTheme());
        } else {
            result = context.getResources().getColor(c);
        }
        float[] hsv = new float[3];
        Color.colorToHSV(result,hsv);
        int c2 = Color.HSVToColor(255,hsv);
        return  c2;
    }

    public static int getWidgetBackgroundDrawableRessource(Context context){
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

    public static int getRaingridDrawableRessource(Context context){
        String themePreference = WeatherSettings.getThemePreference(context);
        if (themePreference.equals(WeatherSettings.Theme.DARK)){
            return R.drawable.raingrid_dark;
        }
        if (themePreference.equals(WeatherSettings.Theme.LIGHT)){
            return R.drawable.raingrid_light;
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED)){
            return R.drawable.raingrid_solarized;
        }
        if (themePreference.equals(WeatherSettings.Theme.SOLARIZED_DARK)){
            return R.drawable.raingrid_solarizeddark;
        }
        if (!isDarkTheme(context)){
            return R.drawable.raingrid_solarized;
        } else {
            return R.drawable.raingrid_solarizeddark;
        }
    }

    public static Drawable getWidgetBackgroundDrawable(Context context){
        int resource = getWidgetBackgroundDrawableRessource(context);
        Drawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
             drawable = context.getResources().getDrawable(resource, context.getTheme());
        } else {
            drawable = context.getResources().getDrawable(resource);
        }
        return drawable;
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

    public static int getTemperatureAccentColor(Context context, Weather.WeatherInfo weatherInfo){
        if (weatherInfo.getTemperatureInCelsius()>0){
            return ThemePicker.getColor(context,ThemePicker.ThemeColor.RED);
        } else {
            return ThemePicker.getColor(context,ThemePicker.ThemeColor.CYAN);
        }
    }

    public static void applyTemperatureAccentColor(Context context, Weather.WeatherInfo weatherInfo, TextView textView){
        textView.setTextColor(getTemperatureAccentColor(context,weatherInfo));
    }

    public static int getPrecipitationAccentColor(Context context){
        return ThemePicker.getColor(context, ThemeColor.BLUE);
    }

    public static void applyPrecipitationAccentColor(Context context, TextView textView){
        textView.setTextColor(getPrecipitationAccentColor(context));
    }

    public static int getGermanyResource(Context context){
        if (isDarkTheme(context)){
            return R.drawable.germany;
        }
        return R.drawable.germany_black;
    }

    private static void tintButton(Context context, Button button){
        if (button!=null){
            //button.setBackgroundColor(getColor(context,ThemeColor.PRIMARYLIGHT));
            //button.setTextColor(getColor(context,ThemeColor.TEXTLIGHT));
            button.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    public static void tintAlertDialogButtons(Context context, AlertDialog alertDialog){
        Button button1 = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        tintButton(context,button1);
        Button button2 = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        tintButton(context,button2);
        Button button3 = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        tintButton(context,button3);
    }

}
