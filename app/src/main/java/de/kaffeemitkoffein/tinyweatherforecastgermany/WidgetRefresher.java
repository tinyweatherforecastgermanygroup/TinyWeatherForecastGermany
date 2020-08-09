package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.content.Intent;

public class WidgetRefresher {

    public static void refresh(Context context){
        // update classic widget
        Intent intent1 = new Intent(context,ClassicWidget.class);
        intent1.setAction(ClassicWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        context.sendBroadcast(intent1);
        // update large widget
        Intent intent2 = new Intent(context,LargeWidget.class);
        intent2.setAction(LargeWidget.WIDGET_CUSTOM_REFRESH_ACTION);
        context.sendBroadcast(intent2);
    }
}
