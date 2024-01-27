/*
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
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class PhaseImages {

    private Context context;
    private int bitmapWidth = 0;
    private int bitmapHeight = 0;
    private int shiftPixels = 0;
    private ForecastIcons forecastIcons;
    private int iconSize = 0;
    private boolean drawCircle = false;
    Bitmap sunBitmap;

    private static final Paint paint = new Paint();

    static {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
    }

    private static final Paint paint2 = new Paint();
    static {
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        paint2.setColor(Color.YELLOW);
    }

    public PhaseImages(Context context){
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
                bitmapWidth = displayMetrics.widthPixels / 5;
                bitmapHeight = bitmapWidth / 2;
            } else {
                bitmapHeight = displayMetrics.heightPixels / 10;
                bitmapWidth = bitmapHeight * 2;
            }
        }
        if ((bitmapWidth <=0) || (bitmapHeight <= 0)) {
            bitmapWidth = 400;
            bitmapHeight = 200;
        }
        iconSize = bitmapWidth / 5;
        forecastIcons = new ForecastIcons(context,iconSize,iconSize);
    }

    public void setShiftPixels(int pixels){
        this.shiftPixels = pixels;
    }

    public void setDrawCircle(boolean drawCircle){
        this.drawCircle = drawCircle;
    }

    public Bitmap getMoonPhaseImage(Weather.WeatherLocation weatherLocation, long time) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);
        //bitmap.eraseColor(Color.GREEN);
        Canvas canvas = new Canvas(bitmap);
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(ThemePicker.getWidgetTextColor(context));
        linePaint.setPathEffect(new DashPathEffect(new float[] {2,2},0));
        linePaint.setStrokeWidth(2);
        float iconPadding = iconSize*0.24f; // sun: 122 space + 268 sun + 122 space (factor 0,24)
        float iconRadius = (iconSize-iconPadding*2)/2f;
        float radius = bitmapWidth/2f-iconRadius;
        float cx = bitmapWidth / 2f;
        float cy = bitmapHeight;
        if (drawCircle){
            canvas.drawCircle(cx,cy,radius,linePaint);
        }
        float moonPositionOnSky = Weather.getApproxMoonPositionOnSky(weatherLocation, time);
        float sunPositionOnSky = Weather.getApproxSunPositionOnSky(weatherLocation, time);
        // equation for x,y in circle is:
        // x = cx + r * cos (degree)
        // y = cy + r * sin (degree)
        if (moonPositionOnSky!=-1){
            float xMoon = iconRadius + (bitmapWidth- iconRadius * 2 - shiftPixels) * moonPositionOnSky;
            float yMoon = (float) (cy - radius * Math.sin(moonPositionOnSky * Math.PI)) + shiftPixels;
            Bitmap moonBitmap = forecastIcons.getDisposableMoonLayer(time, weatherLocation);
            canvas.drawBitmap(moonBitmap,xMoon-moonBitmap.getWidth()/2f,yMoon-moonBitmap.getHeight()/2f,paint);
            //canvas.drawCircle(xMoon, yMoon, iconRadius, paint);
        }
        if (sunPositionOnSky!=-1){
            float xSun = iconRadius + (bitmapWidth - iconRadius * 2) * sunPositionOnSky;
            float ySun = (float) (cy - radius * Math.sin(sunPositionOnSky * Math.PI));
            sunBitmap = forecastIcons.getLayer(ForecastIcons.Layer.SUN);
            canvas.drawBitmap(sunBitmap,xSun-sunBitmap.getWidth()/2f,ySun-sunBitmap.getHeight()/2f,paint);
            //canvas.drawCircle(xSun, ySun, iconRadius, paint2);
        }
        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        fillPaint.setColor(ThemePicker.getWidgetTextColor(context));
        canvas.drawRect(0,bitmapHeight-(bitmapHeight/100f)*1.5f,bitmapWidth,bitmapHeight,fillPaint);
        return bitmap;
    }
}

