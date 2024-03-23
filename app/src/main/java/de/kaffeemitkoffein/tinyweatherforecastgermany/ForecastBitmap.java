/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021, 2022, 2023, 2024 Pawel Dube
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

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ForecastBitmap{

    private Context context;
    private ArrayList<Weather.WeatherInfo> weatherInfos;
    private Weather.WeatherLocation weatherLocation;
    private ForecastIcons forecastIcons;
    private int bitmapWidth = 0;
    private int bitmapHeight = 0;
    private int anticipatedWidth = 0;
    private float itemWidth;
    private float iconRatio;
    private float iconHeight;
    private float fontSize_small;
    private float fontSize_medium;
    private boolean displayWind = false;
    private boolean displaySimpleBar = false;
    private int windDisplayType = Weather.WindDisplayType.ARROW;

    private static final Paint POLY_PAINT = new Paint();
    static {
        POLY_PAINT.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    private static final Paint TEXT_PAINT = new Paint();
    static {
        TEXT_PAINT.setAlpha(255);
    }

    static class Builder{
        private ArrayList<Weather.WeatherInfo> weatherInfos;
        private Weather.WeatherLocation weatherLocation;
        private int bitmapWidth=0;
        private int bitmapHeight=0;
        private int anticipatedWidth =0;
        private float iconRatio = (float) 0.5;
        private boolean displayWind = false;
        private boolean displaySimpleBar = false;
        private ForecastIcons forecastIcons;
        private int windDisplayType = Weather.WindDisplayType.ARROW;

        public Builder setWetherInfos(ArrayList<Weather.WeatherInfo> weatherInfos){
            this.weatherInfos = weatherInfos;
            return this;
        }

        public Builder setWeatherLocation(Weather.WeatherLocation weatherLocation){
            this.weatherLocation = weatherLocation;
            return this;
        }

        public Builder setAnticipatedWidth(int i){
            this.anticipatedWidth = i;
            return this;
        }

        public Builder setWidth(int width){
            this.bitmapWidth = width;
            return this;
        }

        public Builder setForcastIconsClass(ForecastIcons forecastIcons){
            this.forecastIcons = forecastIcons;
            return this;
        }

        public Builder setHeight(int height){
            this.bitmapHeight = height;
            return this;
        }

        public Builder setIconRatio(float f){
            this.iconRatio = f;
            return this;
        }

        public Builder displayWind(boolean b){
            this.displayWind = b;
            return this;
        }

        public Builder displaySimpleBar(boolean b){
            this.displaySimpleBar = b;
            return this;
        }

        public Builder setWindDisplayType(int i){
            this.windDisplayType = i;
            return this;
        }

        public ForecastBitmap create(Context context){
            return new ForecastBitmap(context,this);
        }
    }

    public class LayoutParams{
        public float width;
        public float height;
    }

    private ForecastBitmap(Context context, final Builder builder){
        this.context = context;
        this.weatherInfos =  builder.weatherInfos;
        this.weatherLocation = builder.weatherLocation;
        this.anticipatedWidth = builder.anticipatedWidth;
        this.bitmapWidth = builder.bitmapWidth;
        this.bitmapHeight = builder.bitmapHeight;
        this.iconRatio = builder.iconRatio;
        this.displayWind = builder.displayWind;
        this.displaySimpleBar = builder.displaySimpleBar;
        this.windDisplayType = builder.windDisplayType;
        this.forecastIcons = builder.forecastIcons;
        if ((bitmapHeight==0) || (bitmapWidth==0)){
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager!=null){
                DisplayMetrics displayMetrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                float screenAspectRatio = getPhysicalDisplayRatio(context);
                float screenWidth  = displayMetrics.widthPixels;
                float screenHeight = displayMetrics.heightPixels;
                if (screenWidth>screenHeight){
                    // landscape mode
                    bitmapWidth  = (int) screenWidth;
                    // catch a division by 0 possibility
                    if (displayMetrics.xdpi==0){
                        displayMetrics.xdpi = 60;
                    }
                    bitmapHeight = (int) (((bitmapWidth/displayMetrics.xdpi) * (displayMetrics.ydpi)) / 20);
                } else {
                    // portrait mode
                    bitmapWidth = displayMetrics.widthPixels;
                    bitmapHeight = Math.round(getForecastBarHeight(displayMetrics));
                }
            }
        }
        TEXT_PAINT.setColor(MainActivity.getColorFromResource(context,R.attr.colorText));
    }

    public float getForecastBarHeight(){
        return bitmapHeight;
    }

    public static float getForecastBarHeight(DisplayMetrics displayMetrics) {
        return (displayMetrics.heightPixels / 20f);
    }

    private Bitmap getIconBitmap(Context context, Weather.WeatherInfo weatherInfo, int bitmapWidth, int bitmapHeight){
        if (forecastIcons==null){
            forecastIcons = new ForecastIcons(context,bitmapWidth,bitmapHeight);
        }
        Bitmap bitmap = Bitmap.createScaledBitmap(forecastIcons.getIconBitmap(weatherInfo,weatherLocation),bitmapWidth,bitmapHeight,true);
        return bitmap;
    }

    private static void drawPolygon(Canvas canvas, float[] poly_x, float[] poly_y, int color, int alpha){
        POLY_PAINT.setColor(color);
        POLY_PAINT.setAlpha(alpha);
        Path path = new Path();
        path.moveTo(0,poly_y[0]);
        for (int i=0; i<poly_x.length; i++){
            path.lineTo(poly_x[i],poly_y[i]);
        }
        canvas.drawPath(path, POLY_PAINT);
    }

    public Bitmap getForecastBitmap(){
        if (weatherInfos==null){
            return null;
        }
        if (weatherInfos.size()==0){
            return null;
        }
        if ((bitmapWidth<=0)||(bitmapHeight<=0)){
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth,bitmapHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // set width to default if zero
        if (anticipatedWidth==0){
            anticipatedWidth = 6;
        }
        itemWidth = (float) (bitmapWidth / anticipatedWidth);
        fontSize_medium  = (float) (bitmapHeight/2.2);
        fontSize_small   = (float) (bitmapHeight/3.3);
        TEXT_PAINT.setTextSize(fontSize_medium);
        float x_offset = (bitmapWidth - itemWidth);
        // draw polygons for rain and clouds
        float[] x_polygon = new float[weatherInfos.size()+4];
        float[] y_polygon_rain = new float[weatherInfos.size()+4];
        float[] y_polygon_clouds = new float[weatherInfos.size()+4];
        // calculate offset of polygons when less than 6 items
        float polygon_x_offset = (6-weatherInfos.size())*itemWidth;
        // calculate values of polygons
        for (int j=0; j<weatherInfos.size(); j++){
            x_polygon[j] = polygon_x_offset + itemWidth*j + itemWidth/2;
            Weather.WeatherInfo wi = weatherInfos.get(j);
            if (wi.hasProbPrecipitation()){
                float pp = (float) wi.getProbPrecipitation()/100;
                y_polygon_rain[j] = (bitmapHeight - pp*bitmapHeight);
            } else {
                y_polygon_rain[j] = bitmapHeight;
            }
            if (wi.hasClouds()){
                float pc = (float) wi.getClouds()/100;
                y_polygon_clouds[j] = (bitmapHeight - pc*bitmapHeight);
            } else {
                y_polygon_clouds[j] = bitmapHeight;
            }
            x_polygon[weatherInfos.size()] = bitmapWidth;
            y_polygon_clouds[weatherInfos.size()] = y_polygon_clouds[weatherInfos.size()-1];
            x_polygon[weatherInfos.size()+1] = bitmapWidth;
            y_polygon_clouds[weatherInfos.size()+1] = bitmapHeight;
            x_polygon[weatherInfos.size()+2] = polygon_x_offset;
            y_polygon_clouds[weatherInfos.size()+2] = bitmapHeight;
            x_polygon[weatherInfos.size()+3] = polygon_x_offset;
            y_polygon_clouds[weatherInfos.size()+3] = y_polygon_clouds[0];

            y_polygon_rain[weatherInfos.size()] = y_polygon_rain[weatherInfos.size()-1];
            y_polygon_rain[weatherInfos.size()+1] = bitmapHeight;
            y_polygon_rain[weatherInfos.size()+2] = bitmapHeight;
            y_polygon_rain[weatherInfos.size()+3] = y_polygon_rain[0];

        }
        drawPolygon(canvas,x_polygon,y_polygon_clouds,0xaaaaaa,65);
        drawPolygon(canvas,x_polygon,y_polygon_rain,0x2222aa,85);
        int position = weatherInfos.size()-1;
        while (position>=0){
            // draw timestamp
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = new Date();
            date.setTime(weatherInfos.get(position).getTimestamp());
            String timetext = format.format(date);
            TEXT_PAINT.setTextSize(fontSize_small);
            canvas.drawText(timetext,x_offset,fontSize_small,TEXT_PAINT);
            // draw icon
            Weather.WeatherInfo wi = weatherInfos.get(position);
            float iconsize = itemWidth*iconRatio;
            if (iconsize>bitmapHeight-fontSize_small-1){
                iconsize = bitmapHeight-fontSize_small-1;
            }
            canvas.drawBitmap(getIconBitmap(context, wi,Math.round(iconsize),Math.round(iconsize)),x_offset, fontSize_small+1,TEXT_PAINT);
            // place temperature
            String temperature_text = "";
            if (weatherInfos.get(position).hasTemperature()){
                temperature_text = weatherInfos.get(position).getTemperatureInCelsiusInt()+"°";
                TEXT_PAINT.setTextSize(fontSize_medium);
                canvas.drawText(temperature_text,x_offset+iconsize+1,(float) (bitmapHeight/2)+fontSize_medium/2, TEXT_PAINT);
            }
            // place further temperature information if space is available
            if (!displaySimpleBar){
                float x_within_item_offset = TEXT_PAINT.measureText(temperature_text)+iconsize+2;
                if ((itemWidth-x_within_item_offset)>=fontSize_small*2){
                    if (weatherInfos.get(position).hasMaxTemperature() || weatherInfos.get(position).hasMinTemperature()){
                        Paint paint_minmax = new Paint();
                        paint_minmax.setColor(MainActivity.getColorFromResource(context,R.attr.colorText));
                        paint_minmax.setAlpha(255);
                        paint_minmax.setTextSize(fontSize_small);
                        String temperature_max ="";
                        String temperature_min ="";
                        float max_text_width = 0;
                        if (weatherInfos.get(position).hasMaxTemperature()){
                            temperature_max = weatherInfos.get(position).getMaxTemperatureInCelsiusInt()+"°";
                            max_text_width = paint_minmax.measureText(temperature_max);
                        }
                        if (weatherInfos.get(position).hasMinTemperature()){
                            temperature_min = weatherInfos.get(position).getMinTemperatureInCelsiusInt()+"°";
                            float width_mintemp = paint_minmax.measureText(temperature_min);
                            if (width_mintemp>max_text_width){
                                max_text_width = width_mintemp;
                            }
                        }
                        float y_max    = (bitmapHeight - (paint_minmax.getTextSize()*2)-2)/2 + paint_minmax.getTextSize();
                        float y_min    = (bitmapHeight - (paint_minmax.getTextSize()*2)-2)/2 + paint_minmax.getTextSize()*2+1;
                        if (weatherInfos.get(position).hasMaxTemperature()){
                            float x_max = x_offset + x_within_item_offset + (max_text_width-paint_minmax.measureText(temperature_max))/2;
                            canvas.drawText(temperature_max,x_max,y_max,paint_minmax);
                        }
                        if (weatherInfos.get(position).hasMinTemperature()){
                            float x_min = x_offset + x_within_item_offset + (max_text_width-paint_minmax.measureText(temperature_min))/2;
                            canvas.drawText(temperature_min,x_min,y_min,paint_minmax);
                        }
                        x_within_item_offset = x_within_item_offset + max_text_width + 1;
                    }
                }
                float winddirection_maxsize = iconsize*(float)0.8;
                if (windDisplayType==Weather.WindDisplayType.TEXT) {
                    winddirection_maxsize = fontSize_small*2;
                }
                if (itemWidth - x_within_item_offset>=winddirection_maxsize + 1){
                    if (weatherInfos.get(position).hasWindSpeed()||weatherInfos.get(position).hasWindDirection()){
                        if (windDisplayType!=Weather.WindDisplayType.TEXT){
                            Bitmap windsymbol = weatherInfos.get(position).getWindSymbol(context,windDisplayType,false);
                            if (windsymbol!=null){
                                windsymbol = Bitmap.createScaledBitmap(windsymbol,Math.round(winddirection_maxsize),Math.round(winddirection_maxsize),false);
                                float y_offset_wind = (bitmapHeight - windsymbol.getHeight())/2f;
                                canvas.drawBitmap(windsymbol,x_offset + x_within_item_offset,y_offset_wind,null);
                            }
                        } else {
                            TEXT_PAINT.setTextSize(fontSize_small);
                            TEXT_PAINT.setColor(MainActivity.getColorFromResource(context,R.attr.colorText));
                            TEXT_PAINT.setTextSize(fontSize_small);
                            String windtext=weatherInfos.get(position).getWindDirectionString(context);
                            if (windtext!=null){
                                float x_offset_wind = x_offset + x_within_item_offset;
                                float y_offset_wind = bitmapHeight-(bitmapHeight - TEXT_PAINT.getTextSize())/2;
                                winddirection_maxsize = TEXT_PAINT.measureText(windtext+" ");
                                canvas.drawText(windtext,x_offset_wind,y_offset_wind,TEXT_PAINT);
                            }
                        }
                        x_within_item_offset = x_within_item_offset + winddirection_maxsize + 1;
                    }
                }
                if (itemWidth - x_within_item_offset>=fontSize_small*3){
                    if (weatherInfos.get(position).hasWindSpeed()){
                        String windspeedstring = weatherInfos.get(position).getWindSpeedString(context,false);
                        Paint windspeed_paint = new Paint();
                        windspeed_paint.setColor(MainActivity.getColorFromResource(context,R.attr.colorText));
                        windspeed_paint.setTextSize(fontSize_small);
                        float y_offset = (bitmapHeight - fontSize_small)/2+fontSize_small;
                        canvas.drawText(windspeedstring,x_offset+x_within_item_offset,y_offset,windspeed_paint);
                        x_within_item_offset = x_within_item_offset + fontSize_small*3;
                    }
                }
            }
            x_offset = x_offset - itemWidth;
            position--;
        }
        return bitmap;
    }

    /**
     * Determines the display ratio of the screen based on the screen resolution in pixels. Pixels may
     * have different denities in the x- and y-axis. Therefore use getPhysicalDisplayRatio to determine
     * the real (physical) screen ratio based on the screen size in inches.
     *
     * @param context the current context
     * @return the screen ratio, or 0 if the display metrics are not available.
     */

    public static float getPixelDisplayRatio(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager!=null){
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            float widthPixels  = displayMetrics.widthPixels;
            float heightPixels = displayMetrics.heightPixels;
            if ((heightPixels!=0) && (widthPixels!=0)){
                if (heightPixels>widthPixels){
                    return heightPixels/widthPixels;
                } else {
                    return widthPixels/heightPixels;
                }
            }
        }
        return 0;
    }

    /**
     * Determines the display ratio based on the physical screen size in inches. This may give a different
     * result as calculating the display ratio from pixels, since pixel densities may be different for the
     * x- and the y- axis.
     *
     * @param context the current context
     * @return display ratio, or 0 if the display metrics are not available.
     */

    public static float getPhysicalDisplayRatio(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager!=null){
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            if ((displayMetrics.xdpi!=0) && (displayMetrics.ydpi!=0)){
                float widthInch  = displayMetrics.widthPixels / displayMetrics.xdpi;
                float heightInch = displayMetrics.heightPixels / displayMetrics.ydpi;
                if ((heightInch!=0) && (widthInch!=0)){
                    if (heightInch>widthInch){
                        return heightInch/widthInch;
                    } else {
                        return widthInch/heightInch;
                    }
                }
            }
        }
        return 0;
    }

    public static int getScreenWidthInPixels(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager!=null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
        return 0;
    }

    public static int getDisplayOrientation(Context context){
        return context.getResources().getConfiguration().orientation;
    }

    public static Bitmap getPrecipitationChartRaw(Context context, Weather.WeatherInfo weatherInfo, int width, int height, int maxValueX, int maxValueY){
        final int MAX_PRECIPITATION=25;
        final float[] PRECIPITATION_STEPS = {0.1f, 0.2f, 0.3f, 0.5f, 0.7f, 1.0f, 2.0f, 3.0f, 5.0f, 10.0f, 15.0f, 25.0f};
        final Integer[] precipitation_values = weatherInfo.getPrecipitationDetails();
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        float shiftX=(float) (width/maxValueX);
        float[] polygonX = new float[maxValueX+4];
        float[] polygonY = new float[maxValueX+4];
        for (int i=0; i<maxValueX; i++){
            polygonX[i] = shiftX + i*(width/maxValueX);
            polygonY[i] = height- ((height/maxValueY)*precipitation_values[i]);
        }
        polygonX[maxValueX] = width;
        polygonY[maxValueX] = polygonY[maxValueX-1];
        polygonX[maxValueX+1] = width;
        polygonY[maxValueX+1] = height;
        polygonX[maxValueX+2] = 0;
        polygonY[maxValueX+2] = height;
        polygonX[maxValueX+3] = polygonX[0];
        polygonY[maxValueX+3] = polygonY[0];
        drawPolygon(canvas,polygonX,polygonY, ThemePicker.getColor(context,ThemePicker.ThemeColor.SECONDARY),255);
        return bitmap;
    }

    public static Bitmap getPrecipitationChart(Context context, Weather.WeatherInfo weatherInfo, int width, int height, boolean isLandscape) {
        final String[] PRECIPITATION_STEP_LABELS = {"0.1", "0.2", "0.3", "0.5", "0.7", "1.0", "2.0", "3.0", "5.0", "10.0", "15.0", "25.0"};
        final Integer[] precipitation_values = weatherInfo.getPrecipitationDetails();
        int maxValueY=0;
        int maxScaleY=0;
        int maxValueX=weatherInfo.getPrecipitationDetails().length-1;
        while ((precipitation_values[maxValueX]==0) && (maxValueX>0)){
            maxValueX--;
        }
        for (int i=0;i<maxValueX;i++){
            if (precipitation_values[i]>maxValueY){
                maxValueY=precipitation_values[i];
                maxScaleY=maxValueY/10;
            }
        }
        // round y-scaling up to next 10
        maxScaleY++;
        if (maxScaleY>10){
            maxScaleY=10;
        }
        float chartWidth=Math.round(width*0.7f);
        float chartHeight=Math.round(height*0.7f);
        float shiftX=(float) ((chartWidth/maxValueX)/2);
        float stepX = chartWidth/maxValueX;
        int s = 100;
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTLIGHT));
        textPaint.setTextSize(s);
        while (textPaint.measureText("XX.0")>stepX*0.7f){
            s=s-1;
            textPaint.setTextSize(s);
        }
        float chartOffsetX = width * 0.1f;
        float chartOffsetY = s;
        Bitmap chart = getPrecipitationChartRaw(context, weatherInfo, Math.round(chartWidth), Math.round(chartHeight),maxValueX,maxValueY);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint linePaint = new Paint();
        linePaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTLIGHT));
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        Paint lineStrokePaint = new Paint();
        lineStrokePaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTLIGHT));
        lineStrokePaint.setStyle(Paint.Style.STROKE);
        lineStrokePaint.setStrokeWidth(1);
        lineStrokePaint.setPathEffect(new DashPathEffect(new float[] {2f,4f},0));
        canvas.drawLine(chartOffsetX-1, chartOffsetY, chartOffsetX-1, chartHeight+chartOffsetY, linePaint);
        canvas.drawLine(chartOffsetX-1, chartHeight+chartOffsetY, width, chartHeight+chartOffsetY, linePaint);
        Paint bitmapPaint = new Paint();
        bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(chart, chartOffsetX, chartOffsetY, bitmapPaint);
        for (int x=0; x<maxValueX;x++){
            canvas.drawLine(chartOffsetX+shiftX+x*stepX,chartHeight+chartOffsetY,chartOffsetX+shiftX+x*stepX,chartHeight+chartOffsetY+height*0.02f,linePaint);
            canvas.drawText(PRECIPITATION_STEP_LABELS[x],chartOffsetX+shiftX+x*stepX-textPaint.measureText(PRECIPITATION_STEP_LABELS[x])/2,chartHeight+chartOffsetY+height*0.02f+s,textPaint);
        }
        for (int y=0; y<=maxScaleY; y++){
            float ypos = chartHeight - (y*(chartHeight/maxScaleY)) +chartOffsetY;
            canvas.drawLine(chartOffsetX,ypos,chartOffsetX-width*0.02f,ypos,linePaint);
            canvas.drawLine(chartOffsetX,ypos,width,ypos,lineStrokePaint);
            if (((isLandscape) && (y%2==0)) | (!isLandscape)){
                canvas.drawText(y*10+"%",0,ypos+s/2f,textPaint);
            }
        }
        canvas.drawText("mm",width-textPaint.measureText("mm")*1.1f,chartOffsetY+chartHeight-textPaint.getTextSize()/10,textPaint);
        return bitmap;
    }

    final static Paint barFill = new Paint();
    final static Paint barStroke = new Paint();

    private static void drawBar(Canvas canvas, final int x, final int barWidth, final int y0, final int y1){
        canvas.drawRect(x-barWidth/2,y0,x+barWidth/2,y1,barFill);
        canvas.drawRect(x-barWidth/2,y0,x+barWidth/2,y1,barStroke);
    }

    public static Bitmap getPrecipitationChart2(Context context, Weather.WeatherInfo weatherInfo){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = 1038;
        options.outHeight = 792;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),ThemePicker.getRaingridDrawableRessource(context),options);
        if (!bitmap.isMutable()){
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        }
        final int baseY = 711;
        final int baseX = 191;
        final int stepY = 7;
        final int stepX = 70;
        final int stepsY = 10;
        final int stepsX = 12;
        final int barWidth = 45;
        barFill.setColor(ThemePicker.getPrecipitationAccentColor(context));
        barFill.setStyle(Paint.Style.FILL);
        barStroke.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTDARK));
        barStroke.setStyle(Paint.Style.STROKE);
        barStroke.setAntiAlias(true);
        if (weatherInfo.hasPrecipitationDetails()){
            Canvas canvas = new Canvas(bitmap);
            Integer[] rain = weatherInfo.getPrecipitationDetails();
            for (int x=0; x<stepsX; x++){
                if (rain[x]>0){
                    drawBar(canvas,baseX+x*stepX,barWidth,baseY,baseY-rain[x]*stepY);
                }
            }
            return bitmap;
        }
        return null;
    }

    public static Bitmap getCloudCoverChart(Context context, Weather.WeatherInfo weatherInfo, int width, int height){
        // height           label height       cloud display height
        //==================================================================
        // 500ft = 152.4 m  0.018 * height  => 76,2     0.006       * height
        // < 2km            0.286 * height  => 1152.4   0.16        * height
        // 2-7 km           1.0   * height  => 4500     0.64        * height
        // > 7km            1.0   * height  => 7000     1.0         * height
        // range is 7km
        // base convective cloud =          value/7000  * chartHeight
        final float[] RELATIVE_DISPLAY_CLOUD_HEIGHTS = new float[] {0.006f, 0.16f, 0.64f, 1.02f};
        final float[] RELATIVE_LABEL_CLOUD_HEIGHTS = new float[] {0.018f, 0.286f, 1.0f, 1.0f};
        final String[] LABEL_CLOUD_HEIGHTS         = new String[] {"152.4m","2 km", "7 km", "7 km"};
        final float strokeWidth = height*0.03f;
        final float chartHeight = height - strokeWidth*2;
        final float chartWidth  = width*0.7f;
        final float chartCenter = width*0.3f + (width*0.7f)/2f;
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint groundPaint = new Paint();
        groundPaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.ACCENT));
        groundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTLIGHT));
        Paint lineStrokePaint = new Paint();
        lineStrokePaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTLIGHT));
        lineStrokePaint.setStyle(Paint.Style.STROKE);
        lineStrokePaint.setStrokeWidth(1);
        lineStrokePaint.setPathEffect(new DashPathEffect(new float[] {2f,4f},0));
        Paint cloudPaint = new Paint();
        cloudPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        cloudPaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTLIGHT));
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTLIGHT));
        int s = 100;
        textPaint.setTextSize(s);
        while (textPaint.measureText("152.4m")>width*0.3f){
            s=s-1;
            textPaint.setTextSize(s);
        }
        canvas.drawRect(0,height-strokeWidth*0.6f,width,height,groundPaint);
        canvas.drawLine(width-chartWidth,0,width-chartWidth,height-strokeWidth,linePaint);
        Integer[] cloudData = weatherInfo.clouds.getIntArray();
        for (int y=1; y<5; y++ ){
            if (cloudData[y]!=null){
                float cloudWidth = cloudData[y]*(chartWidth/100);
                float cloudHeight = height - RELATIVE_DISPLAY_CLOUD_HEIGHTS[y-1]*chartHeight - strokeWidth;
                float labelHeight = height - RELATIVE_LABEL_CLOUD_HEIGHTS[y-1]*chartHeight - strokeWidth;
                canvas.drawRect(chartCenter-cloudWidth/2,cloudHeight-strokeWidth/2,chartCenter+cloudWidth/2,cloudHeight+strokeWidth/2,cloudPaint);
                canvas.drawLine(width-chartWidth,labelHeight,width,labelHeight,lineStrokePaint);
                canvas.drawText(LABEL_CLOUD_HEIGHTS[y-1],0,labelHeight+s/2,textPaint);
            }
        }
        return bitmap;
    }

    public static Paint GetDefaultLinePaint(int color, int lineWidth) {
        Paint rPaint = new Paint();
        rPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        rPaint.setColor(color);
        rPaint.setStrokeWidth(lineWidth);
        rPaint.setShadowLayer(5,5,5,Color.BLACK);
        rPaint.setAntiAlias(true);
        return rPaint;
    }

    public static Paint GetDefaultWindPaint(int color, int lineWidth) {
        Paint rPaint = new Paint();
        rPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        rPaint.setColor(color);
        rPaint.setStrokeWidth(lineWidth);
        rPaint.setPathEffect(new DashPathEffect(new float[]{1,2,1,2},0));
        rPaint.setAntiAlias(true);
        return rPaint;
    }

    public static Bitmap getOverviewChart(Context context, int width, int height, ArrayList<Weather.WeatherInfo> weatherInfos, ArrayList<WeatherWarning> warnings){
        // integrity checks of data
        if (weatherInfos==null){
            return null;
        }
        if (weatherInfos.size()<3){
            return null;
        }
        int startPosition = weatherInfos.size()-1;
        boolean hasTemperature = true;
        boolean hasProbPrecipitation = true;
        boolean hasAmountPrecipitation = true;
        boolean hasClouds = true;
        boolean hasWindSpeed = true;
        for (int i=startPosition; i<weatherInfos.size(); i++){
            Weather.WeatherInfo weatherInfo1 = weatherInfos.get(i);
            if (!weatherInfo1.hasTemperature()){
                hasTemperature = false;
            }
            if (!weatherInfo1.hasClouds()){
                hasClouds = false;
            }
            if (!weatherInfo1.hasPrecipitation()){
                hasAmountPrecipitation = false;
            }
            if (!weatherInfo1.hasProbPrecipitation()){
                hasProbPrecipitation = false;
            }
            if (!weatherInfo1.hasWindSpeed()){
                hasWindSpeed=false;
            }
        }
        if ((!hasTemperature)&&(!hasClouds)&&(!hasProbPrecipitation)&&(!hasAmountPrecipitation)){
            return null;
        }
        // default width is all available data
        int endPosition = weatherInfos.size();
        // draw the stuff
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int lineWidth = 4;
        Paint chartPaint = new Paint();
        chartPaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTLIGHT));
        chartPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        chartPaint.setAntiAlias(true);
        chartPaint.setStrokeWidth(1);
        // Paint borderPaint = new Paint();
        Paint temperaturePaint = GetDefaultLinePaint(ThemePicker.getColor(context,ThemePicker.ThemeColor.RED),lineWidth);
        Paint cloudsPaint = GetDefaultLinePaint(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTDARK),lineWidth);
        Paint precipitationPaint = GetDefaultLinePaint(ThemePicker.getColor(context,ThemePicker.ThemeColor.BLUE),lineWidth);
        Paint windPaint = GetDefaultWindPaint(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXT),lineWidth);
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1);
        linePaint.setPathEffect(new DashPathEffect(new float[]{2,4,2,4},0));
        linePaint.setAntiAlias(true);
        linePaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTLIGHT));
        Paint textPaint = new Paint();
        textPaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTLIGHT));
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setAntiAlias(true);
        final float yAxisFontSizeScaleFactor = 1.2f;
        final float yNoticeFontSizeScaleFactor = 1.0f;
        float labelTextSize = 100;
        int chartHeight = height;
        final int alphaClouds = 125;
        final int alphaRain = 125;
        final int alphaRainAmount = 256;
        final int alphaWarnings = 65;
        textPaint.setTextSize(labelTextSize);
        while (textPaint.measureText("-XX°")>(width/18f)){
            labelTextSize=labelTextSize-1;
            textPaint.setTextSize(labelTextSize);
        }
        textPaint.setTextSize(labelTextSize*yAxisFontSizeScaleFactor);
        float xChartOffset = textPaint.measureText("-XX°C");
        textPaint.setTextSize(labelTextSize);
        // determine current position in forecast data
        while ((weatherInfos.get(startPosition).getTimestamp()> Calendar.getInstance().getTimeInMillis()) && (startPosition>0)){
            startPosition--;
        }
        // calculate endPosition from the value in the settings
        endPosition=startPosition+24*WeatherSettings.getDisplayOverviewChartDays(context);
        if (endPosition>=weatherInfos.size()){
            endPosition=weatherInfos.size()-1;
        }
        int itemCount = Math.abs(endPosition-startPosition);
        int minTemp=weatherInfos.get(startPosition).getTemperatureInCelsiusInt();
        int maxTemp=weatherInfos.get(startPosition).getTemperatureInCelsiusInt();
        for (int i=startPosition; i<endPosition; i++){
            if (weatherInfos.get(i).getTemperatureInCelsiusInt()<minTemp){
                minTemp=weatherInfos.get(i).getTemperatureInCelsiusInt();
            }
            if (weatherInfos.get(i).getTemperatureInCelsiusInt()>maxTemp){
                maxTemp=weatherInfos.get(i).getTemperatureInCelsiusInt();
            }
        }
        if (WeatherSettings.useOverviewChartMinMax(context)){
            if (WeatherSettings.getOverviewChartMin(context)<minTemp){
                minTemp = WeatherSettings.getOverviewChartMin(context);
            }
            if (WeatherSettings.getOverviewChartMax(context)>maxTemp){
                maxTemp = WeatherSettings.getOverviewChartMax(context);
            }
        }
        //minTemp = -5; maxTemp = 25;
        int deltaTemp = maxTemp - minTemp;
        if (minTemp>=0){
            deltaTemp = maxTemp;
        }
        int temp_scale_step_value = 5;
        // if screen to narrow or deltaTemp too high, increase scale to 10°
        if ((chartHeight<100) || (deltaTemp>25)){
            temp_scale_step_value = 10;
        }
        int display_steps = deltaTemp / temp_scale_step_value;
        float temp_bottom_offset_value = 0;
        if (minTemp < 0){
            temp_bottom_offset_value = -(((float) (Math.abs(minTemp)/temp_scale_step_value))+1)*temp_scale_step_value;
        }
        while (maxTemp>temp_bottom_offset_value+display_steps*temp_scale_step_value){
            display_steps++;
        }
        float temp_graphscale = (float) temp_scale_step_value * display_steps / chartHeight;
        float zeroline_position = chartHeight + temp_bottom_offset_value / temp_graphscale;
        // paint chart outline
        canvas.drawLine(xChartOffset,0,xChartOffset,chartHeight,chartPaint);
        canvas.drawLine(xChartOffset,zeroline_position,width,zeroline_position,chartPaint);
        while ((labelTextSize+4> ((float) chartHeight)/display_steps) && (labelTextSize>6)){
            labelTextSize--;
        }
        for (int i=0; i<=display_steps; i++){
            String s2 = String.valueOf((int) (temp_bottom_offset_value+temp_scale_step_value*i));
            textPaint.setTextSize(labelTextSize*yAxisFontSizeScaleFactor);
            textPaint.setFakeBoldText(false);
            if ((i==0) || (i == display_steps)){
                s2 = s2 + "°C";
                textPaint.setFakeBoldText(true);
            }
            float x1 = 0;
            float x2 = width;
            float y1 = 100 / ((float) 100 / chartHeight) - (100f/display_steps*i/((float) 100 / chartHeight));
            canvas.drawLine(x1,y1,x2,y1,linePaint);
            // when there are no steps between the min/max scale, display the bottom value above the line
            // to make it visible
            if ((display_steps==1) && (i==0)){
                canvas.drawText(s2,x1+lineWidth+lineWidth/10f,y1,textPaint);
            } else {
                // default; display beneath the dotted, horizontal line
                canvas.drawText(s2,x1+lineWidth+lineWidth/10f,y1+textPaint.getTextSize(),textPaint);
            }
        }
        // paint warnings
        // the warnings arraylist may be empty at 1st call, but this will be called again once the main app knows
        // which warnings apply to the location. The warnings arraylist only includes warnings that apply to the
        // selected location. We need to only check here if time applies.
        long chartTimeStart = weatherInfos.get(0).getTimestamp();
        long chartTimeStop  = weatherInfos.get(endPosition-1).getTimestamp();
        float chartWidth = width-xChartOffset;
        if (warnings!=null){
             for (int i=0; i<warnings.size(); i++){
                 WeatherWarning warning = warnings.get(i);
                 // omit already expired warnings
                 if (warning.getApplicableExpires()>=chartTimeStart){
                     float x1 = xChartOffset + (warning.onset-chartTimeStart)*(((float)chartWidth)/((float)(chartTimeStop-chartTimeStart)));
                     if (x1<xChartOffset){
                         x1 = xChartOffset;
                     }
                     float x2 = xChartOffset + (warning.getApplicableExpires()-chartTimeStart)*(((float)chartWidth)/((float)(chartTimeStop-chartTimeStart)));
                     int color = warning.getWarningColor();
                     float[] warningPolygonX = new float[5];
                     float[] warningPolygonY = new float[5];
                     warningPolygonX[0] = x1; warningPolygonY[0] = 0;
                     warningPolygonX[1] = x2; warningPolygonY[1] = 0;
                     warningPolygonX[2] = x2; warningPolygonY[2] = chartHeight;
                     warningPolygonX[3] = x1; warningPolygonY[3] = chartTimeStart;
                     warningPolygonX[4] = x1; warningPolygonY[4] = 0;
                     drawPolygon(canvas,warningPolygonX,warningPolygonY,color,alphaWarnings);
                 }
             }
        }
        // paint clouds & rain
        textPaint.setTextSize(labelTextSize);
        textPaint.setFakeBoldText(false);
        float[] rainPolygonX = new float[itemCount+4];
        float[] rainPolygonY = new float[itemCount+4];
        float[] cloudPolygonX = new float[itemCount+4];
        float[] cloudPolygonY = new float[itemCount+4];
        for (int i=startPosition; i<endPosition; i++) {
            int pos = i - startPosition;
            Weather.WeatherInfo weatherInfo1 = weatherInfos.get(i);
            float x1 = xChartOffset+ ((float) chartWidth/(float) itemCount)*pos;
            if (hasProbPrecipitation){
                rainPolygonX[pos]=x1;
                rainPolygonY[pos]=chartHeight - (weatherInfo1.getProbPrecipitation()/100f) * chartHeight;
            }
            if (hasClouds){
                cloudPolygonX[pos]=x1;
                cloudPolygonY[pos]=chartHeight - (weatherInfo1.getClouds()/100f) * chartHeight;
            }
        }
        if (hasClouds){
            cloudPolygonX[itemCount]=width;
            cloudPolygonY[itemCount]=cloudPolygonY[itemCount-1];
            cloudPolygonX[itemCount+1]=width;
            cloudPolygonY[itemCount+1]=chartHeight;
            cloudPolygonX[itemCount+2]=xChartOffset;
            cloudPolygonY[itemCount+2]=chartHeight;
            cloudPolygonX[itemCount+3]=xChartOffset;
            cloudPolygonY[itemCount+3]=cloudPolygonY[0];
            drawPolygon(canvas,cloudPolygonX,cloudPolygonY,ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXTDARK),alphaClouds);
        }
        if (hasProbPrecipitation){
            rainPolygonX[itemCount]=width;
            rainPolygonY[itemCount]=rainPolygonY[itemCount-1];
            rainPolygonX[itemCount+1]=width;
            rainPolygonY[itemCount+1]=chartHeight;
            rainPolygonX[itemCount+2]=xChartOffset;
            rainPolygonY[itemCount+2]=chartHeight;
            rainPolygonX[itemCount+3]=xChartOffset;
            rainPolygonY[itemCount+3]=rainPolygonY[0];
            drawPolygon(canvas,rainPolygonX,rainPolygonY,ThemePicker.getColor(context,ThemePicker.ThemeColor.BLUE),alphaRain);
        }
        if (hasTemperature){
            for (int i=startPosition; i<endPosition-1; i++){
                int pos = i - startPosition;
                Weather.WeatherInfo weatherInfo1 = weatherInfos.get(i);
                Weather.WeatherInfo weatherInfo2 = weatherInfos.get(i+1);
                float y1_t = zeroline_position - weatherInfo1.getTemperatureInCelsiusInt() / temp_graphscale;
                float y2_t = zeroline_position - weatherInfo2.getTemperatureInCelsiusInt() / temp_graphscale;
                float x1 = xChartOffset+ ((float) chartWidth/(float) itemCount)*pos;
                float x2 = xChartOffset+ ((float) chartWidth/(float) itemCount)*(pos+1);
                if (weatherInfo2.getTemperatureInCelsiusInt()>0){
                    temperaturePaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.ORANGE));
                } else {
                    temperaturePaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.CYAN));
                }
                canvas.drawLine(x1,y1_t,x2,y2_t,temperaturePaint);
                //is midnight?
                if (weatherInfo1.getTimestamp()%86400000==0){
                    canvas.drawLine(x1,0,x1,chartHeight,linePaint);
                }
                // is noon?
                if ((weatherInfo1.getTimestamp()%43200000==0) && (weatherInfo1.getTimestamp()%86400000!=0)){
                    String dayOfWeek = Weather.GetDateString(Weather.getSimpleDateFormat(Weather.SimpleDateFormats.DAYOFWEEK),weatherInfo1.getTimestamp());
                    float DOWWidth = textPaint.measureText(dayOfWeek);
                    float startDOWX = x1 - DOWWidth/2;
                    // do not draw text if it starts left of the y-axis or if it is trimmed at the right bitmap border
                    if ((startDOWX>xChartOffset) && (startDOWX+DOWWidth<width)){
                        canvas.drawText(dayOfWeek,startDOWX,textPaint.getTextSize(),textPaint);
                    }
                }
            }
        }
        if ((hasWindSpeed) && (WeatherSettings.displayWindInCharts(context))){
            float maxScaleKmh = WeatherSettings.getWindInChartsMaxKmh(context);
            float windscale = maxScaleKmh;
            if (WeatherSettings.getWindDisplayUnit(context)==Weather.WindDisplayUnit.KNOTS){
                windscale = Weather.Units.KnotsFromKmh(maxScaleKmh);
            }
            if (WeatherSettings.getWindDisplayUnit(context)==Weather.WindDisplayUnit.BEAUFORT){
                windscale = Weather.Units.BeaufortFromKmh(maxScaleKmh);
            }
            if (WeatherSettings.getWindDisplayUnit(context)==Weather.WindDisplayUnit.METERS_PER_SECOND){
                windscale = Weather.Units.MSFromKmh(maxScaleKmh);
            }
            for (int i=startPosition; i<endPosition-1; i++) {
                int pos = i - startPosition;
                Weather.WeatherInfo weatherInfo1 = weatherInfos.get(i);
                Weather.WeatherInfo weatherInfo2 = weatherInfos.get(i + 1);
                float wind1 = weatherInfo1.getWindSpeedInKmhInt();
                float wind2 = weatherInfo2.getWindSpeedInKmhInt();
                if (WeatherSettings.getWindDisplayUnit(context)==Weather.WindDisplayUnit.METERS_PER_SECOND){
                    wind1 = weatherInfo1.getWindSpeedInMsInt();
                    wind2 = weatherInfo2.getWindSpeedInMsInt();
                }
                if (WeatherSettings.getWindDisplayUnit(context)==Weather.WindDisplayUnit.BEAUFORT){
                    wind1 = weatherInfo1.getWindSpeedInBeaufortInt();
                    wind2 = weatherInfo2.getWindSpeedInBeaufortInt();
                }
                if (WeatherSettings.getWindDisplayUnit(context)==Weather.WindDisplayUnit.KNOTS){
                    wind1 = weatherInfo1.getWindSpeedInKnotsInt();
                    wind2 = weatherInfo2.getWindSpeedInKnotsInt();
                }
                float y1_t = chartHeight - wind1 * (chartHeight/windscale);
                float y2_t = chartHeight - wind2 * (chartHeight/windscale);
                windPaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.TEXT));
                if (wind1>windscale){
                    y1_t = zeroline_position - chartHeight+lineWidth;
                    windPaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.YELLOW));
                }
                if (wind2>windscale){
                    y2_t = zeroline_position - chartHeight+lineWidth;
                    windPaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.YELLOW));
                }
                float x1 = xChartOffset + ((float) chartWidth / (float) itemCount) * pos;
                float x2 = xChartOffset + ((float) chartWidth / (float) itemCount) * (pos + 1);
                canvas.drawLine(x1, y1_t, x2, y2_t, windPaint);
            }
            String windLabel = getWindScaleString(context);
            Paint windTextPaint = new Paint(textPaint);
            windTextPaint.setTextSize(labelTextSize/1.5f);
            windTextPaint.setShadowLayer(2,2,2,Color.BLACK);
            windTextPaint.setAntiAlias(true);
            float windXOffset = bitmap.getWidth() - windTextPaint.measureText(windLabel)-3;
            canvas.drawText(windLabel,windXOffset,zeroline_position-windTextPaint.getTextSize()-2,windTextPaint);
        }
        if ((WeatherSettings.displayPrecipitationAmountInOverviewChart(context)) && (hasAmountPrecipitation)){
            Paint precipitationAmountPaint = new Paint();
            precipitationAmountPaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.CYAN));
            precipitationAmountPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            precipitationAmountPaint.setAlpha(alphaRainAmount);
            precipitationAmountPaint.setStrokeWidth(1);
            precipitationAmountPaint.setShadowLayer(1,1,1,Color.BLACK);
            for (int i=startPosition; i<endPosition-1; i++) {
                int pos = i - startPosition;
                Weather.WeatherInfo weatherInfo1 = weatherInfos.get(i);
                Weather.WeatherInfo weatherInfo2 = weatherInfos.get(i + 1);
                float maxPcScale = 5; // mm
                float pca1 = (float) weatherInfo1.getPrecipitation();
                float y1 = chartHeight - pca1 * (chartHeight / maxPcScale);
                float x1 = xChartOffset + ((float) chartWidth / (float) itemCount) * pos;
                float x2 = xChartOffset + ((float) chartWidth / (float) itemCount) * (pos + 1);
                float xCenter = (x1+x2)/2;
                float pcaWidth = (x2-x1)/1.0f;
                if (pcaWidth<0){
                    pcaWidth = 1;
                }
                precipitationAmountPaint.setStrokeWidth(pcaWidth);
                canvas.drawLine(xCenter,chartHeight,xCenter,y1,precipitationAmountPaint);
            }
            String pcaLabel = "25 mm";
            Paint pcaTextPaint = new Paint(textPaint);
            pcaTextPaint.setTextSize(labelTextSize/1.5f);
            pcaTextPaint.setShadowLayer(2,2,2,Color.BLACK);
            pcaTextPaint.setAntiAlias(true);
            float pcaXOffset = bitmap.getWidth() - pcaTextPaint.measureText(pcaLabel)-3;
            canvas.drawText(pcaLabel,pcaXOffset,zeroline_position-2,pcaTextPaint);
        }
        return bitmap;
    }

    public static String getWindScaleString(Context context){
        // scale is 0-100 km/h, 0-7 bf, 0-15.65 m/s, 30.41 kt
        int maxScaleKmh = WeatherSettings.getWindInChartsMaxKmh(context);
        final DecimalFormat df = new DecimalFormat("#.##");
        if (WeatherSettings.getWindDisplayUnit(context)==Weather.WindDisplayUnit.BEAUFORT){
            return "0 - " + df.format(Weather.Units.BeaufortFromKmh(WeatherSettings.getWindInChartsMaxKmh(context))) + " bf";
        }
        if (WeatherSettings.getWindDisplayUnit(context)==Weather.WindDisplayUnit.METERS_PER_SECOND){
            return "0 - " + df.format(Weather.Units.MSFromKmh(WeatherSettings.getWindInChartsMaxKmh(context))) + " m/s";
        }
        if (WeatherSettings.getWindDisplayUnit(context)==Weather.WindDisplayUnit.KILOMETERS_PER_HOUR){
            return "0 - " + df.format(Weather.Units.KnotsFromKmh(WeatherSettings.getWindInChartsMaxKmh(context))) + " kt";
        }
        return "0 - 100 km/h";
    }

    public static Bitmap getHorizontalBar(Context context, int width, int height, int value, int max, int color, int border){
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(border);
        borderPaint.setStrokeWidth(5.0f);
        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        fillPaint.setColor(color);
        float x = (float) ( (float) value/max*width);
        canvas.drawRect(0,0,x,height,fillPaint);
        canvas.drawRect(0,0,x,height,borderPaint);
        return bitmap;
    }

    public static Bitmap getPollenLegendBox(Context context, int size, int pollenType){
        Bitmap bitmap = Bitmap.createBitmap(size,size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int borderColor = ThemePicker.getWidgetTextColor(context);
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(5.0f);
        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        fillPaint.setColor(Pollen.PollenLoadColors[pollenType]);
        canvas.drawRect(0,0,bitmap.getWidth(),bitmap.getHeight(),fillPaint);
        canvas.drawRect(0,0,bitmap.getWidth(),bitmap.getHeight(),borderPaint);
        return bitmap;
    }

    public static class BBox{
        public float x0; public float y0; public float x1; public float y1;

        public BBox(float x0, float y0, float x1, float y1){
            this.x0 = x0; this.x1 = x1; this.y0 = y0; this.y1 = y1;
        }

        public BBox(float[] rect){
            this.x0 = rect[0]; this.y0 = rect[1]; this.x1 = rect[2];  this.y1 = rect[3];
        }

        public BBox(RectF rectF){
            this.x0 = rectF.left; this.x1 = rectF.right; this.y0 = rectF.top; this.y1 = rectF.bottom;
        }

        public RectF getRectF(){
            return new RectF(x0,y0,x1,x1);
        }

        public float getWidth(){
            return x1 - x0;
        }

        public float getHeight(){
            return y1 - y0;
        }

    }

    public static class BBoxBitmap{
        private BBox bbox;
        private Bitmap bitmap;
        private Canvas canvas;

        public BBoxBitmap(BBox bbox, Bitmap bitmap){
            this.bbox = bbox;
            this.bitmap = bitmap;
        }

        private void initCanvas(){
            if (canvas==null){
                canvas = new Canvas(bitmap);
            }
        }

        public int getBitmapHeight(){
            if (bitmap!=null){
                return bitmap.getHeight();
            }
            return -1;
        }

        public int getBitmapWidth(){
            if (bitmap!=null){
                return bitmap.getWidth();
            }
            return -1;
        }

        public float getXPixel(float xCoord){
            float x = (getBitmapWidth()/bbox.getWidth()) * (xCoord - bbox.x0);
            return x;
        }

        public float getYPixel(float yCoord){
            float y = getBitmapHeight()-(getBitmapHeight()/bbox.getHeight()) * (yCoord - bbox.y0);
            return y;
        }

        public void drawGeo(float longitude, float latitude, Paint paint){
            initCanvas();
            if (bitmap!=null){
                canvas.drawPoint(getXPixel(longitude),getYPixel(latitude),paint);
            }
        }

        public Path getPathFromPolygon(Polygon polygon){
            Path path = new Path();
            if (polygon.polygonX.length>0){
                path.moveTo(getXPixel(polygon.polygonX[0]),getYPixel(polygon.polygonY[0]));
                for (int i=1; i<polygon.polygonX.length; i++){
                    path.lineTo(getXPixel(polygon.polygonX[i]),getYPixel(polygon.polygonY[i]));
                }
                path.lineTo(getXPixel(polygon.polygonX[0]),getYPixel(polygon.polygonY[0]));
            }
            return path;
        }

        public void drawGeoPolygon(Polygon polygon, Paint paint,Paint borderPaint){
            initCanvas();
            Path path = getPathFromPolygon(polygon);
            canvas.drawPath(path,paint);
            if (borderPaint!=null){
                canvas.drawPath(path,borderPaint);
            }
        }

        public void drawPollenArea(PollenArea pollenArea, Paint paint, Paint borderPaint){
            if (pollenArea.geoPolygon==null){
                pollenArea.initPolygon();
            }
            drawGeoPolygon(pollenArea.geoPolygon,paint, borderPaint);
        }
    }

    public static Bitmap getPollenAreasBitmap(Context context, int pollenType, int timeParam){
        if ((timeParam>=Pollen.Today) && (timeParam<=Pollen.DayAfterTomorrow)){
            Paint pollenPaint = new Paint();
            pollenPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            Paint pollenBorderPaint = new Paint();
            pollenBorderPaint.setStyle(Paint.Style.STROKE);
            pollenBorderPaint.setColor(ThemePicker.getWidgetTextColor(context));
            BBox bBox = new BBox(WeatherLayer.WarnMapGeo);
            Bitmap bitmap = Bitmap.createBitmap(WeatherLayer.WarnMapSize[0],WeatherLayer.WarnMapSize[1], Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.TRANSPARENT);
            BBoxBitmap bboxBitmap = new BBoxBitmap(bBox,bitmap);
            ArrayList<PollenArea> pollenAreas = PollenArea.GetPollenAreas(context,null);
            ArrayList<Pollen> pollens = Pollen.GetPollenData(context);
            for (int i=0; i<pollenAreas.size(); i++){
                PollenArea pollenArea = pollenAreas.get(i);
                Pollen pollen = Pollen.GetPollenData(pollens,pollenArea);
                if (pollen!=null){
                    int pollenLoad = pollen.getPollenLoad(context,pollenType,timeParam);
                    if (pollenLoad>=0) {
                        pollenPaint.setColor(Pollen.PollenLoadColors[pollenLoad]);
                        bboxBitmap.drawPollenArea(pollenArea, pollenPaint, pollenBorderPaint);
                    }
                }
            }
            return bitmap;
        }
        return null;
    }

    public static Drawable getColoredBox(Context context, int color){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.roundedbox_small,context.getTheme());
            if (Build.VERSION.SDK_INT < 29) {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            } else {
                drawable.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_IN));
            }
            return drawable;
        }
        Bitmap bitmap = Bitmap.createBitmap(10,10, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(color);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(),bitmap);
        return bitmapDrawable;
    }

    public static void fadeOutAnimation(final ImageView imageView, final long duration){
        Animation fadeOutAnimation = new AlphaAnimation(1,0);
        fadeOutAnimation.setDuration(duration);
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // nothing to do
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // nothing to do
            }
        });
        imageView.startAnimation(fadeOutAnimation);

    }

    public static void fadeInAnimation(final ImageView imageView, final long duration){
        Animation fadeInAnimation = new AlphaAnimation(0,1);
        fadeInAnimation.setDuration(duration);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                imageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // nothing to do
                imageView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fadeOutAnimation(imageView,duration);
                    }
                },duration*2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // nothing to do
            }
        });
        imageView.startAnimation(fadeInAnimation);
    }

}
