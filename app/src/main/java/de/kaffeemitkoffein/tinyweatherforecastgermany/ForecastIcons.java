package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.graphics.*;
import android.widget.ImageView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ForecastIcons {

    public static class Holidays{

        public static boolean isChristmas(){
            Calendar calendar = Calendar.getInstance();
            if ((calendar.get(Calendar.MONTH)==Calendar.DECEMBER) && (calendar.get(Calendar.DAY_OF_MONTH)==24)){
                return true;
            }
            if ((calendar.get(Calendar.MONTH)==Calendar.DECEMBER) && (calendar.get(Calendar.DAY_OF_MONTH)==25)){
                return true;
            }
            if ((calendar.get(Calendar.MONTH)==Calendar.DECEMBER) && (calendar.get(Calendar.DAY_OF_MONTH)==26)){
                return true;
            }
            return false;
        }

        public static boolean isHalloween(){
            Calendar calendar = Calendar.getInstance();
            if ((calendar.get(Calendar.MONTH)==Calendar.OCTOBER) && (calendar.get(Calendar.DAY_OF_MONTH)==31)){
                return true;
            }
            return false;
        }

        public static int getEasterInMarch(int year){
            float a = year%4;
            float b = year%7;
            float c = year%19;
            float d = (19*c + 24)%30;
            float e = (2*a + 4*b + 6*d +5)%7;
            float f = (c + 11*d + 22*e)/451;
            float dayInMarch = 22 + d + e - 7*f;
            return Math.round(dayInMarch);
        }

        public static boolean isEaster(){
            Calendar calendar = Calendar.getInstance();
            return false;
        }

    }

    private final static int DEFAULTICONWIDTH = 256;
    private final static int DEFAULTICONHEIGHT = 256;
    private final ConcurrentHashMap<Integer,Bitmap> bitmapCache = new ConcurrentHashMap<>();
    private final Context context;
    private ImageView imageView;
    private int iconWidth=DEFAULTICONWIDTH;
    private int iconHeight=DEFAULTICONHEIGHT;
    private static final Paint paint = new Paint();
    static {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }
    private static final Paint MoonFill = new Paint();

    private static class Layer{
        private final static int NOT_AVAILABLE = 0;
        private final static int DRIZZLE_1 = 1;
        private final static int DRIZZLE_2 = 2;
        private final static int DRIZZLE_3 = 3;
        private final static int RAIN_1 = 4;
        private final static int RAIN_2 = 5;
        private final static int RAIN_3 = 6;
        private final static int CLOUDS = 7;
        private final static int CLOUD_1 = 8;
        private final static int CLOUD_2 = 9;
        private final static int CLOUD_3 = 10;
        private final static int SNOW_1 = 11;
        private final static int SNOW_2 = 12;
        private final static int SNOW_3 = 13;
        private final static int CLOUDY = 14;
        private final static int FREEZING_1 = 15;
        private final static int FREEZING_2 = 16;
        private final static int FOG = 17;
        private final static int SUN = 18;
        private final static int MOON = 19;
        private final static int LIGHTNING = 20;
    }

    private int getLayerResourceID(int layer){
        switch (layer){
            case Layer.DRIZZLE_1 : return R.mipmap.mod_drizzle_1;
            case Layer.DRIZZLE_2 : return R.mipmap.mod_drizzle_2;
            case Layer.DRIZZLE_3 : return R.mipmap.mod_drizzle_3;
            case Layer.RAIN_1    : return R.mipmap.mod_rain_1;
            case Layer.RAIN_2    : return R.mipmap.mod_rain_2;
            case Layer.RAIN_3    : return R.mipmap.mod_rain_3;
            case Layer.CLOUDS    : return R.mipmap.mod_clouds;
            case Layer.CLOUD_1   : return R.mipmap.mod_cloud_1;
            case Layer.CLOUD_2   : return R.mipmap.mod_cloud_2;
            case Layer.CLOUD_3   : return R.mipmap.mod_cloud_3;
            case Layer.SNOW_1    : return R.mipmap.mod_snow_1;
            case Layer.SNOW_2    : return R.mipmap.mod_snow_2;
            case Layer.SNOW_3    : return R.mipmap.mod_snow_3;
            case Layer.CLOUDY    : return R.mipmap.mod_cloudy;
            case Layer.FREEZING_1: return R.mipmap.mod_freezing_1;
            case Layer.FREEZING_2: return R.mipmap.mod_freezing_2;
            case Layer.FOG       : return R.mipmap.mod_fog;
            case Layer.SUN       : return R.mipmap.mod_sun;
            case Layer.MOON      : return R.mipmap.mod_moon;
            case Layer.LIGHTNING : return R.mipmap.mod_lightning;
            default: return R.mipmap.not_available;
        }
    }

    private Map<Integer, int[]> ConditionLayers;

    private void initLayers(){
        ConditionLayers = new HashMap<>();
        ConditionLayers.put(WeatherCodeContract.NOT_AVAILABLE,new int[] {Layer.NOT_AVAILABLE});
        ConditionLayers.put(WeatherCodeContract.SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW,new int[] {Layer.CLOUDS,Layer.LIGHTNING});
        ConditionLayers.put(WeatherCodeContract.DRIZZLE_FREEZING_MODERATE_OR_HEAVY,new int[] {Layer.CLOUDS,Layer.DRIZZLE_3,Layer.FREEZING_2});
        ConditionLayers.put(WeatherCodeContract.DRIZZLE_FREEZING_SLIGHT,new int[] {Layer.CLOUDS,Layer.DRIZZLE_1,Layer.FREEZING_1});
        ConditionLayers.put(WeatherCodeContract.RAIN_FREEZING_MODERATE_OR_HEAVY,new int[] {Layer.CLOUDS,Layer.FREEZING_2,Layer.RAIN_3});
        ConditionLayers.put(WeatherCodeContract.RAIN_FREEZING_SLIGHT,new int[] {Layer.CLOUDS,Layer.FREEZING_1,Layer.RAIN_1});
        ConditionLayers.put(WeatherCodeContract.SNOW_SHOWERS_MODERATE_OR_HEAVY,new int[] {Layer.CLOUDS,Layer.SNOW_3});
        ConditionLayers.put(WeatherCodeContract.SNOW_SHOWERS_SLIGHT,new int[] {Layer.SNOW_1});
        ConditionLayers.put(WeatherCodeContract.SHOWERS_OF_RAIN_AND_SNOW_MIXED_MODERATE_OR_HEAVY,new int[] {Layer.CLOUDS,Layer.SNOW_2,Layer.RAIN_2});
        ConditionLayers.put(WeatherCodeContract.SHOWERS_OF_RAIN_AND_SNOW_MIXED_SLIGHT,new int[] {Layer.CLOUDS,Layer.SNOW_1,Layer.RAIN_1});
        ConditionLayers.put(WeatherCodeContract.EXTREMELY_HEAVY_RAIN_SHOWER,new int[] {Layer.CLOUDS,Layer.RAIN_3});
        ConditionLayers.put(WeatherCodeContract.MODERATE_OR_HEAVY_RAIN_SHOWERS,new int[] {Layer.CLOUDS,Layer.RAIN_2});
        ConditionLayers.put(WeatherCodeContract.SLIGHT_RAIN_SHOWER,new int[] {Layer.CLOUDS,Layer.RAIN_1});
        ConditionLayers.put(WeatherCodeContract.HEAVY_SNOWFALL_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.SNOW_3});
        ConditionLayers.put(WeatherCodeContract.MODERATE_SNOWFALL_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.SNOW_2});
        ConditionLayers.put(WeatherCodeContract.SLIGHT_SNOWFALL_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.SNOW_1});
        ConditionLayers.put(WeatherCodeContract.MODERATE_OR_HEAVY_RAIN_AND_SNOW,new int[] {Layer.CLOUDS,Layer.RAIN_2,Layer.SNOW_2});
        ConditionLayers.put(WeatherCodeContract.SLIGHT_RAIN_AND_SNOW,new int[] {Layer.CLOUDS,Layer.RAIN_1,Layer.SNOW_1});
        ConditionLayers.put(WeatherCodeContract.HEAVY_DRIZZLE_NOT_FREEZING_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.DRIZZLE_3});
        ConditionLayers.put(WeatherCodeContract.MODERATE_DRIZZLE_NOT_FREEZING_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.DRIZZLE_2});
        ConditionLayers.put(WeatherCodeContract.SLIGHT_DRIZZLE_NOT_FREEZING_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.DRIZZLE_1});
        ConditionLayers.put(WeatherCodeContract.HEAVY_RAIN_NOT_FREEZING_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.RAIN_3});
        ConditionLayers.put(WeatherCodeContract.MODERATE_RAIN_NOT_FREEZING_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.RAIN_2});
        ConditionLayers.put(WeatherCodeContract.SLIGHT_RAIN_NOT_FREEZING_CONTINUOUS,new int[] {Layer.CLOUDS,Layer.RAIN_1});
        ConditionLayers.put(WeatherCodeContract.ICE_FOG_SKY_NOT_RECOGNIZABLE,new int[] {Layer.FOG,R.mipmap.mod_freezing_1});
        ConditionLayers.put(WeatherCodeContract.FOG_SKY_NOT_RECOGNIZABLE,new int[] {Layer.FOG});
        ConditionLayers.put(WeatherCodeContract.EFFECTIVE_CLOUD_COVER_AT_LEAST_7_8,new int[] {Layer.CLOUDY});
        ConditionLayers.put(WeatherCodeContract.EFFECTIVE_CLOUD_COVER_BETWEEN_46_8_AND_6_8,new int[] {Layer.SUN,Layer.CLOUD_2});
        ConditionLayers.put(WeatherCodeContract.EFFECTIVE_CLOUD_COVER_BETWEEN_1_8_AND_45_8,new int[] {Layer.SUN,Layer.CLOUD_1});
        ConditionLayers.put(WeatherCodeContract.EFFECTIVE_CLOUD_COVER_LESS_THAN_1_8,new int[] {Layer.SUN});
        MoonFill.setStyle(Paint.Style.FILL_AND_STROKE);
        MoonFill.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.PRIMARYLIGHT));
        MoonFill.setAlpha(230);
        MoonFill.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
    }

    public ForecastIcons(Context context, ImageView imageView){
        this.context = context;
        this.imageView = imageView;
        if (imageView!=null){
            this.iconWidth = imageView.getWidth();
            this.iconHeight = imageView.getHeight();
        }
        if ((this.iconHeight==0) || (this.iconWidth==0)){
            this.iconWidth = DEFAULTICONWIDTH;
            this.iconHeight = DEFAULTICONHEIGHT;
        }
        initLayers();
    }

    public ForecastIcons(Context context, int iconWidth, int iconHeight){
        this.context = context;
        this.iconHeight = iconHeight;
        this.iconWidth = iconWidth;
        initLayers();
    }

    private Bitmap getLayer(int layer){
        Bitmap bitmap = bitmapCache.get(layer);
        if (bitmap==null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inMutable = true;
            BitmapFactory.decodeResource(context.getResources(), getLayerResourceID(layer), options);
            if (imageView!=null){
                options.inSampleSize = ForecastAdapter.calculateInSampleSize(options,imageView);
            } else {
                options.inSampleSize = ForecastAdapter.calculateInSampleSize(options,iconWidth,iconHeight);
            }
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeResource(context.getResources(), getLayerResourceID(layer), options);
            bitmap = Bitmap.createScaledBitmap(bitmap,iconWidth,iconHeight,true);
            bitmapCache.put(layer, bitmap);
        }
        return bitmap;
    }

    private Bitmap getDisposableMoonLayer(Weather.WeatherInfo weatherInfo, Weather.WeatherLocation weatherLocation){
        float moonPhase = (float) Weather.getMoonPhase(weatherInfo.getTimestamp());
        float moonDiameter = iconWidth * 204f/512f;
        float moonPositionX = iconWidth/2f - (moonPhase*moonDiameter*2);
        if (moonPhase>=0.5){
            moonPositionX = iconWidth/2f + moonDiameter*2 -  (moonPhase*moonDiameter*2);
        }
        Bitmap targetBitmap = Bitmap.createBitmap(iconWidth,iconHeight, Bitmap.Config.ARGB_8888);
        Bitmap moonLayer = getLayer(Layer.MOON);
        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawBitmap(moonLayer,0,0,paint);
        canvas.drawCircle(moonPositionX,iconHeight/2f,moonDiameter/2f,MoonFill);
        Bitmap solidBitmap = Bitmap.createBitmap(iconWidth,iconHeight, Bitmap.Config.ARGB_8888);
        canvas.drawBitmap(solidBitmap,0,0,MoonFill);
        // make the eath transition also look right on the southern hemisphere
        if (weatherLocation!=null){
            if (weatherLocation.latitude<0){
                Matrix matrix = new Matrix();
                matrix.preScale(-1,1);
                targetBitmap = Bitmap.createBitmap(targetBitmap,0,0,targetBitmap.getWidth(),targetBitmap.getHeight(),matrix,true);
            }
        }
        return targetBitmap;
    }

    public Bitmap getIconBitmap(Weather.WeatherInfo weatherInfo, Weather.WeatherLocation weatherLocation){
        int condition = WeatherCodeContract.NOT_AVAILABLE;
        if (weatherInfo.hasCondition()) {
            condition = weatherInfo.getCondition();
        }
        int[] layers = ConditionLayers.get(condition);
        if (layers != null) {
            Bitmap targetBitmap = Bitmap.createBitmap(iconWidth,iconHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(targetBitmap);
            // modify for day/night
            if (layers[0] == Layer.SUN) {
                if (weatherLocation != null) {
                    if (!weatherInfo.isDaytime(weatherLocation)) {
                        //canvas.drawBitmap(getLayer(Layer.MOON),0,0,paint);
                        canvas.drawBitmap(getDisposableMoonLayer(weatherInfo,weatherLocation),0,0,paint);
                    } else {
                        canvas.drawBitmap(getLayer(Layer.SUN),0,0,paint);
                    }
                } else {
                    canvas.drawBitmap(getLayer(Layer.SUN),0,0,paint);
                }
            } else {
                canvas.drawBitmap(getLayer(layers[0]),0,0,paint);
            }
            for (int i = 1; i < layers.length; i++) {
                Bitmap layerBitmap = getLayer(layers[i]);
                canvas.drawBitmap(layerBitmap, 0, 0, paint);
            }
            // modify thunderstorms for rain or snow
            if (condition == WeatherCodeContract.SLIGHT_OR_MODERATE_THUNDERSTORM_WITH_RAIN_OR_SNOW) {
                if (weatherInfo.hasTemperature()) {
                    Bitmap precipitationBitmap;
                    if (weatherInfo.getTemperatureInCelsiusInt() > 0) {
                        precipitationBitmap = getLayer(Layer.RAIN_2);
                    } else {
                        precipitationBitmap = getLayer(Layer.SNOW_2);
                    }
                    Canvas pcanvas = new Canvas(precipitationBitmap);
                    pcanvas.drawBitmap(targetBitmap,0,0,paint);
                    targetBitmap = precipitationBitmap;
                }
            }
            return targetBitmap;
        }
        return null;
    }

    public void clearCache() {
        bitmapCache.clear();
    }
}
