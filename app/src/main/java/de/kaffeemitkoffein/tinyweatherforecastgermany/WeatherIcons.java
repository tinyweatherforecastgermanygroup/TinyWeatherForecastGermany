package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class WeatherIcons {
    final static int NOT_AVAILABLE = 0;
    final static int THUNDERSTORM=1;
    final static int FREEZING_DRIZZLE=2;
    final static int FREEZING_DRIZZLE_SLIGHT=3;
    final static int FREEZING_RAIN=4;
    final static int FREEZING_RAIN_SLIGHT=5;
    final static int SNOW_SHOWERS_PARTLY=6;
    final static int SNOW_SHOWERS_PARTLY_NIGHT=7;
    final static int LIGHT_SNOW_SHOWERS_PARTLY=8;
    final static int LIGHT_SNOW_SHOWERS_PARTLY_NIGHT=9;
    final static int MIXED_RAIN_AND_SNOW_PARTLY=10;
    final static int MIXED_RAIN_AND_SNOW_PARTLY_NIGHT=11;
    final static int LIGHT_MIXED_RAIN_AND_SNOW_PARTLY=12;
    final static int LIGHT_MIXED_RAIN_AND_SNOW_PARTLY_NIGHT=13;
    final static int EXTREMELY_HEAVY_SHOWERS_PARTLY=14;
    final static int EXTREMELY_HEAVY_SHOWERS_PARTLY_NIGHT=15;
    final static int SHOWERS_PARTLY=16;
    final static int SHOWERS_PARTLY_NIGHT=17;
    final static int LIGHT_SHOWERS_PARTLY=18;
    final static int LIGHT_SHOWERS_PARTLY_NIGHT=19;
    final static int HEAVY_SNOW_SHOWERS=20;
    final static int MODERATE_SNOW_SHOWERS=21;
    final static int LIGHT_SNOW_SHOWERS=22;
    final static int MIXED_RAIN_AND_SNOW=23;
    final static int LIGHT_MIXED_RAIN_AND_SNOW=24;
    final static int HEAVY_DRIZZLE=25;
    final static int MODERATE_DRIZZLE=26;
    final static int LIGHT_DRIZZLE=27;
    final static int HEAVY_SHOWERS=28;
    final static int MODERATE_SHOWERS=29;
    final static int LIGHT_SHOWERS=30;
    final static int ICE_FOG=31;
    final static int FOG=32;
    final static int CLOUDY=33;
    final static int MOSTLY_CLOUDY_DAY=34;
    final static int MOSTLY_CLOUDY_NIGHT=35;
    final static int PARTLY_CLOUDY_DAY=36;
    final static int PARTLY_CLOUDY_NIGHT=37;
    final static int SUNNY=38;
    final static int CLEAR_NIGHT=39;

    final static int SYMBOL_RH = 255;
    final static int SYMBOL_CLOUD = 256;
    final static int SYMBOL_DRIZZLE = 257;
    final static int SYMBOL_FOG = 258;
    final static int SYMBOL_FREEZING_RAIN = 259;
    final static int SYMBOL_HAIL = 260;
    final static int SYMBOL_LIGHTNING = 261;
    final static int SYMBOL_PRECIPITATION = 262;
    final static int SYMBOL_TEMPERATURE5CM = 263;
    final static int WHITE_BAR = 264;
    final static int ARROW = 265;
    final static int ARROW_UP = 266;
    final static int ARROW_DOWN = 267;
    final static int SUNSET = 268;
    final static int BIOCULAR = 269;
    final static int WIND_BEAUFORT_00 = 270;
    final static int WIND_BEAUFORT_01 = 271;
    final static int WIND_BEAUFORT_02 = 272;
    final static int WIND_BEAUFORT_03 = 273;
    final static int WIND_BEAUFORT_04 = 274;
    final static int WIND_BEAUFORT_05 = 275;
    final static int WIND_BEAUFORT_06 = 276;
    final static int WIND_BEAUFORT_07 = 277;
    final static int WIND_BEAUFORT_08 = 278;
    final static int WIND_BEAUFORT_09 = 279;
    final static int WIND_BEAUFORT_10 = 280;
    final static int WIND_BEAUFORT_11 = 281;
    final static int WIND_BEAUFORT_12 = 282;

    final static int GERMANY = 1024;
    final static int MAP_COLLAPSED = 1025;
    final static int RADARINFOBAR = 1026;
    final static int PIN = 1027;
    final static int IC_LAUNCHER_BW = 1028;
    final static int RADIO_BUTTON_UNCHECKED = 1100;
    final static int RADIO_BUTTON_CHECKED = 1101;
    final static int IC_INFO_OUTLINE = 1102;
    final static int IC_GPS_FIXED = 1103;
    final static int IC_ANNOUNCEMENT = 1104;

    public static int getIconResource(Context context, int icon) {
        int result = 0;
        switch (icon) {
            case NOT_AVAILABLE: result = R.mipmap.not_available; break;
            case THUNDERSTORM:
                result = R.mipmap.thunderstorm;
                break;
            case FREEZING_DRIZZLE:
                result = R.mipmap.freezing_drizzle;
                break;
            case FREEZING_DRIZZLE_SLIGHT:
                result = R.mipmap.freezing_drizzle_slight;
                break;
            case FREEZING_RAIN:
                result = R.mipmap.freezing_rain;
                break;
            case FREEZING_RAIN_SLIGHT:
                result = R.mipmap.freezing_rain_slight;
                break;
            case SNOW_SHOWERS_PARTLY:
                result = R.mipmap.snow_showers_partly;
                break;
            case SNOW_SHOWERS_PARTLY_NIGHT:
                result = R.mipmap.snow_showers_partly_night;
                break;
            case LIGHT_SNOW_SHOWERS_PARTLY:
                result = R.mipmap.light_snow_showers_partly;
                break;
            case LIGHT_SNOW_SHOWERS_PARTLY_NIGHT:
                result = R.mipmap.light_snow_showers_partly_night;
                break;
            case MIXED_RAIN_AND_SNOW_PARTLY:
                result = R.mipmap.mixed_rain_and_snow_partly;
                break;
            case MIXED_RAIN_AND_SNOW_PARTLY_NIGHT:
                result = R.mipmap.mixed_rain_and_snow_partly_night;
                break;
            case LIGHT_MIXED_RAIN_AND_SNOW_PARTLY:
                result = R.mipmap.light_mixed_rain_and_snow_partly;
                break;
            case LIGHT_MIXED_RAIN_AND_SNOW_PARTLY_NIGHT:
                result = R.mipmap.light_mixed_rain_and_snow_partly_night;
                break;
            case EXTREMELY_HEAVY_SHOWERS_PARTLY:
                result = R.mipmap.extremely_heavy_showers_partly;
                break;
            case EXTREMELY_HEAVY_SHOWERS_PARTLY_NIGHT:
                result = R.mipmap.extremely_heavy_showers_partly_night;
                break;
            case SHOWERS_PARTLY:
                result = R.mipmap.showers_partly;
                break;
            case SHOWERS_PARTLY_NIGHT:
                result = R.mipmap.showers_partly_night;
                break;
            case LIGHT_SHOWERS_PARTLY:
                result = R.mipmap.light_showers_partly;
                break;
            case LIGHT_SHOWERS_PARTLY_NIGHT:
                result = R.mipmap.light_showers_partly_night;
                break;
            case HEAVY_SNOW_SHOWERS:
                result = R.mipmap.heavy_snow_showers;
                break;
            case MODERATE_SNOW_SHOWERS:
                result = R.mipmap.moderate_snow_showers;
                break;
            case LIGHT_SNOW_SHOWERS:
                result = R.mipmap.light_snow_showers;
                break;
            case MIXED_RAIN_AND_SNOW:
                result = R.mipmap.mixed_rain_and_snow;
                break;
            case LIGHT_MIXED_RAIN_AND_SNOW:
                result = R.mipmap.light_mixed_rain_and_snow;
                break;
            case HEAVY_DRIZZLE:
                result = R.mipmap.heavy_drizzle;
                break;
            case MODERATE_DRIZZLE:
                result = R.mipmap.moderate_drizzle;
                break;
            case LIGHT_DRIZZLE:
                result = R.mipmap.light_drizzle;
                break;
            case HEAVY_SHOWERS:
                result = R.mipmap.heavy_showers;
                break;
            case MODERATE_SHOWERS:
                result = R.mipmap.moderate_showers;
                break;
            case LIGHT_SHOWERS:
                result = R.mipmap.light_showers;
                break;
            case ICE_FOG:
                result = R.mipmap.ice_fog;
                break;
            case FOG:
                result = R.mipmap.fog;
                break;
            case CLOUDY:
                result = R.mipmap.cloudy;
                break;
            case MOSTLY_CLOUDY_DAY:
                result = R.mipmap.mostly_cloudy_day;
                break;
            case MOSTLY_CLOUDY_NIGHT:
                result = R.mipmap.mostly_cloudy_night;
                break;
            case PARTLY_CLOUDY_DAY:
                result = R.mipmap.partly_cloudy_day;
                break;
            case PARTLY_CLOUDY_NIGHT:
                result = R.mipmap.partly_cloudy_night;
                break;
            case SUNNY:
                result = R.mipmap.sunny;
                break;
            case CLEAR_NIGHT: result = R.mipmap.clear_night; break;
            case SYMBOL_RH : result = R.mipmap.symbol_rh; break;
            case SYMBOL_CLOUD : result = R.mipmap.symbol_cloud; break;
            case SYMBOL_DRIZZLE : result = R.mipmap.symbol_drizzle; break;
            case SYMBOL_FOG : result = R.mipmap.symbol_fog; break;
            case SYMBOL_FREEZING_RAIN : result = R.mipmap.symbol_freezing_rain; break;
            case SYMBOL_HAIL : result = R.mipmap.symbol_hail; break;
            case SYMBOL_LIGHTNING : result = R.mipmap.symbol_lightning; break;
            case SYMBOL_PRECIPITATION : result = R.mipmap.symbol_precipitation; break;
            case SYMBOL_TEMPERATURE5CM : result = R.mipmap.symbol_temperature5cm; break;
            case WHITE_BAR : result = R.mipmap.white_bar; break;
            case ARROW : result = R.mipmap.arrow; break;
            case ARROW_UP : result = R.mipmap.arrow_up; break;
            case ARROW_DOWN : result = R.mipmap.arrow_down; break;
            case SUNSET : result = R.mipmap.sunset; break;
            case BIOCULAR : result = R.mipmap.biocular; break;
            case WIND_BEAUFORT_00 : result = R.mipmap.wind_beaufort_00; break;
            case WIND_BEAUFORT_01 : result = R.mipmap.wind_beaufort_01; break;
            case WIND_BEAUFORT_02 : result = R.mipmap.wind_beaufort_02; break;
            case WIND_BEAUFORT_03 : result = R.mipmap.wind_beaufort_03; break;
            case WIND_BEAUFORT_04 : result = R.mipmap.wind_beaufort_04; break;
            case WIND_BEAUFORT_05 : result = R.mipmap.wind_beaufort_05; break;
            case WIND_BEAUFORT_06 : result = R.mipmap.wind_beaufort_06; break;
            case WIND_BEAUFORT_07 : result = R.mipmap.wind_beaufort_07; break;
            case WIND_BEAUFORT_08 : result = R.mipmap.wind_beaufort_08; break;
            case WIND_BEAUFORT_09 : result = R.mipmap.wind_beaufort_09; break;
            case WIND_BEAUFORT_10 : result = R.mipmap.wind_beaufort_10; break;
            case WIND_BEAUFORT_11 : result = R.mipmap.wind_beaufort_11; break;
            case WIND_BEAUFORT_12 : result = R.mipmap.wind_beaufort_12; break;
            case PIN : result = R.mipmap.pin; break;
            case IC_LAUNCHER_BW : result = R.mipmap.ic_launcher_bw; break;
            case GERMANY: result = R.drawable.germany; break;
            case MAP_COLLAPSED: result = R.drawable.map_collapsed; break;
            case RADARINFOBAR: result = R.drawable.radarinfobar; break;
            case RADIO_BUTTON_UNCHECKED: result = R.mipmap.ic_radio_button_unchecked_white_24dp; break;
            case RADIO_BUTTON_CHECKED: result = R.mipmap.ic_radio_button_checked_white_24dp; break;
            case IC_INFO_OUTLINE: result = R.mipmap.ic_info_outline_white_24dp; break;
            case IC_GPS_FIXED: result = R.mipmap.ic_gps_fixed_white_24dp; break;
            case IC_ANNOUNCEMENT: result = R.mipmap.ic_announcement_white_24dp; break;
        }
        // override with dark variants if applicable
        if (!ThemePicker.isDarkTheme(context)) {
            switch (icon) {
                case NOT_AVAILABLE: result = R.mipmap.not_available_black; break;
                case CLEAR_NIGHT:
                    result = R.mipmap.clear_night_black; break;
                case MOSTLY_CLOUDY_NIGHT:
                    result = R.mipmap.mostly_cloudy_night_black;
                    break;
                case ARROW : result = R.mipmap.arrow_black; break;
                case SUNSET : result = R.mipmap.sunset_black; break;
                case ARROW_UP : result = R.mipmap.arrow_up_black; break;
                case ARROW_DOWN : result = R.mipmap.arrow_down_black; break;
                case WHITE_BAR : result = R.mipmap.white_bar_black; break;
                case BIOCULAR : result = R.mipmap.biocular_black; break;
                case SYMBOL_RH : result = R.mipmap.symbol_rh_black; break;
                case SYMBOL_CLOUD : result = R.mipmap.symbol_cloud_black; break;
                case SYMBOL_FOG : result = R.mipmap.symbol_fog_black; break;
                case SYMBOL_FREEZING_RAIN : result = R.mipmap.symbol_freezing_rain_black; break;
                case SYMBOL_HAIL : result = R.mipmap.symbol_hail_black; break;
                case SYMBOL_TEMPERATURE5CM : result = R.mipmap.symbol_temperature5cm_black; break;
                case WIND_BEAUFORT_00 : result = R.mipmap.wind_beaufort_00_black; break;
                case WIND_BEAUFORT_01 : result = R.mipmap.wind_beaufort_01_black; break;
                case WIND_BEAUFORT_02 : result = R.mipmap.wind_beaufort_02_black; break;
                case WIND_BEAUFORT_03 : result = R.mipmap.wind_beaufort_03_black; break;
                case WIND_BEAUFORT_04 : result = R.mipmap.wind_beaufort_04_black; break;
                case WIND_BEAUFORT_05 : result = R.mipmap.wind_beaufort_05_black; break;
                case WIND_BEAUFORT_06 : result = R.mipmap.wind_beaufort_06_black; break;
                case WIND_BEAUFORT_07 : result = R.mipmap.wind_beaufort_07_black; break;
                case WIND_BEAUFORT_08 : result = R.mipmap.wind_beaufort_08_black; break;
                case WIND_BEAUFORT_09 : result = R.mipmap.wind_beaufort_09_black; break;
                case WIND_BEAUFORT_10 : result = R.mipmap.wind_beaufort_10_black; break;
                case WIND_BEAUFORT_11 : result = R.mipmap.wind_beaufort_11_black; break;
                case WIND_BEAUFORT_12 : result = R.mipmap.wind_beaufort_12_black; break;
                case GERMANY: result = R.drawable.germany_black; break;
                case MAP_COLLAPSED: result = R.drawable.map_collapsed_black; break;
                case RADIO_BUTTON_UNCHECKED: result = R.mipmap.ic_radio_button_unchecked_black_24dp; break;
                case RADIO_BUTTON_CHECKED: result = R.mipmap.ic_radio_button_checked_black_24dp; break;
                case IC_INFO_OUTLINE: result = R.mipmap.ic_info_outline_black_24dp; break;
                case IC_GPS_FIXED: result = R.mipmap.ic_gps_fixed_black_24dp; break;
                case IC_ANNOUNCEMENT: result = R.mipmap.ic_announcement_black_24dp; break;
            }
        }
        return result;
    }

    public static Bitmap getIconBitmap(Context context, int icon, boolean fromWidget){
        int resource = getIconResource(context,icon);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resource,options);
        ThemePicker.applyColor(context,bitmap,fromWidget);
        return bitmap;
    }

}

