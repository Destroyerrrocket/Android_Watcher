package com.example.pol.androwatch.android_watcher;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import android.view.View;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link MainWidgetConfigureActivity MainWidgetConfigureActivity}
 */
public class MainWidget extends AppWidgetProvider {
    public static String RefreshButtonActionCaller = "com.example.pol.androwatch.WIDGET_BUTTON";

    //this code is blessed by god.
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        //determine OPTIONAL parameters.

        boolean noButton = MainWidgetConfigureActivity.loadDataPref(context, appWidgetId, 1) == "FALSE";;

        //declare data

        String widgetText = MainWidgetConfigureActivity.loadDataPref(context, appWidgetId, 0);
        String currentIP = Utils.getIPAddress(true);
        if (currentIP.equals("")) {
            currentIP = "IP: Unknown";
        } else {
            currentIP = "IP: " + currentIP;
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setTextViewText(R.id.Local_IP, currentIP);

        //first, let's decide if we let the button appear
        if(noButton) {
            //noButton you say? here you have!
            views.setViewVisibility(R.id.Refresh_Button, View.GONE);
        } else {
            //button stuff. making it ready to be touched!
            Intent intent = new Intent(RefreshButtonActionCaller);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.Refresh_Button, pendingIntent);
        }
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        int i = 0;
        for (int appWidgetId : appWidgetIds) {
            String[] args = new String[1];
            if (i > 0) {
                args[0] = "noButton";
            }
            updateAppWidget(context, appWidgetManager, appWidgetId);
            i++;
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            MainWidgetConfigureActivity.deleteDataPref(context, appWidgetId, 0);
            MainWidgetConfigureActivity.deleteDataPref(context, appWidgetId, 1);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Call the refresh!
        super.onReceive(context, intent);
        if (RefreshButtonActionCaller.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            ComponentName watchWidget = new ComponentName(context, MainWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(watchWidget);

            int i = 0;
            for (int appWidgetId : appWidgetIds) {
                String[] args = new String[1];
                if (i > 0) {
                    args[0] = "noButton";
                }
                updateAppWidget(context, appWidgetManager, appWidgetId);
                i++;
            }
        }
    }
}

