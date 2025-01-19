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
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DeviceTweaks {
    public static boolean isDarkTextOnOverflowMenuDevice(){
        if ((Build.HARDWARE.equals("qcom")) && (Build.PRODUCT.equals("G8441"))){
            return true;
        }
        return false;
    }

    public static int[] confirmPlausibleWidgetSize(final Context context, final AppWidgetManager awm, final int widgetInstance, final int desiredWidgetWidth, final int desiredWidgetHeight){
        ClassicWidget.WidgetDimensionManager widgetDimensionManager = new ClassicWidget.WidgetDimensionManager(context,awm,widgetInstance);
        int width = widgetDimensionManager.getWidgetWidthInt();
        int height = widgetDimensionManager.getWidgetHeightInt();
        return confirmPlausibleWidgetSize(context,width,height,desiredWidgetWidth,desiredWidgetHeight);
    }

    public static int[] confirmPlausibleWidgetSize(final Context context, int width, int height, final int desiredWidgetWidth, final int desiredWidgetHeight){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        if ((width<=0) || (height<=0)){
            width = desiredWidgetWidth; height = desiredWidgetHeight;
            PrivateLog.log(context,PrivateLog.WIDGET,PrivateLog.INFO,"Widget size unknown, applying default dimensions: "+width+" x "+height);
        } else {
            PrivateLog.log(context,PrivateLog.WIDGET,PrivateLog.INFO,"Detected widget size is "+width+" x "+height);
            // check for implausible widget dimensions
            if ((width>displayMetrics.widthPixels) || (height>displayMetrics.heightPixels)){
                width = desiredWidgetWidth; height = desiredWidgetHeight;
                PrivateLog.log(context,PrivateLog.WIDGET,PrivateLog.ERR,"Detected widget size is bigger than the screen resolution. Falling back to widget default dimensions.");
            }
        }
        int[] resultArray = new int[2]; resultArray[0]=width; resultArray[1]=height;
        return resultArray;
    }


}
