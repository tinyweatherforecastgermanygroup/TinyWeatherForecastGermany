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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class DeviceTweaks {
    public static boolean isDarkTextOnOverflowMenuDevice(){
        if ((Build.HARDWARE.equals("qcom")) && (Build.PRODUCT.equals("G8441"))){
            return true;
        }
        return false;
    }

    public static final class Widget{
        final static int CLASSIC = 0;
        final static int BOLD    = 1;
        final static int CHART   = 2;
        final static int TIME    = 3;

        // defines default size of widgets in cells, see xml-v31

        final static int[][] Cells          = new int[][] {{4,2},{4,1},{3,1},{4,2}};
        final static int[][] DefaultSizeDpi = new int[][] {{250,30},{285,110},{285,123},{285,120}};

        final static String getWidgetName(final int widget){
            switch (widget){
                case CLASSIC: return "ClassicWidget";
                case BOLD: return "BoldWidget";
                case CHART: return "ChartWidget";
                case TIME: return "ClockWidget";
                default: return "unknown widget";
            }
        }

    }

    public static int[] confirmPlausibleWidgetSize(final Context context, final int widget, final int width, final int height){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        // calculate default widget sizes from dpi to pixels
        int defaultWidth  = Math.round(Widget.DefaultSizeDpi[widget][0] * (displayMetrics.xdpi/160f));
        int defaultHeight = Math.round(Widget.DefaultSizeDpi[widget][0] * (displayMetrics.ydpi/160f));
        int resultWidth  = width;
        int resultHeight = height;
        if ((width<=0) || (height<=0)){
            resultWidth = defaultWidth; resultHeight = defaultHeight;
            PrivateLog.log(context,PrivateLog.WIDGET,PrivateLog.INFO,"Widget size unknown, applying default dimensions: "+resultWidth+" x "+resultHeight);
        } else {
            PrivateLog.log(context,PrivateLog.WIDGET,PrivateLog.INFO,Widget.getWidgetName(widget)+": detected widget size is "+width+" x "+height);
            // check for implausible widget dimensions
            if ((width>displayMetrics.widthPixels) || (height>displayMetrics.heightPixels)){
                // set default values
                resultWidth = defaultWidth; resultHeight = defaultHeight;
                PrivateLog.log(context,PrivateLog.WIDGET,PrivateLog.ERR,"Detected widget size is bigger than the screen resolution. Fixing dimensions: "+resultWidth+" x "+resultHeight);
            }
        }
        int[] resultArray = new int[2]; resultArray[0]=resultWidth; resultArray[1]=resultHeight;
        return resultArray;
    }

    public static int[] confirmPlausibleWidgetSize(final Context context, final int widget, final AppWidgetManager awm, final int widgetInstance){
        ClassicWidget.WidgetDimensionManager widgetDimensionManager = new ClassicWidget.WidgetDimensionManager(context,awm,widgetInstance);
        final int width = widgetDimensionManager.getWidgetWidthInt();
        final int height = widgetDimensionManager.getWidgetHeightInt();
        return confirmPlausibleWidgetSize(context,widget,width,height);
    }



}
