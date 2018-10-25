package com.android.app.atfnews.controller;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.AtfNewsUrlType;
import com.android.app.atfnews.utils.PrefUtils;
import com.android.app.atfnews.view.LoginActivity;

/**
 * Implementation of App Widget functionality.
 */
public class WorldNewsWidgetProvider extends AppWidgetProvider {

    public static final String TAG = WorldNewsWidgetProvider.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        PrefUtils.setUrlNewsType(AtfNewsUrlType.us.name(), context);
        Intent countriesIntent = new Intent(context, AtfNewsWidgetService.class);
        countriesIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // Create an intent to launch TopNewsActivity when clicked
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("country_code", PrefUtils.getUrlNewsType(context));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.atfnews_widget_provider);
        // Click handler will allow launching the PendingIntent
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);
        Log.d(TAG, "Country name header has been successfully inserted: " + "Country Name");
        views.setRemoteAdapter(R.id.widget_listview, countriesIntent);

        Intent clickIntentTemplate = new Intent(context, LoginActivity.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_listview, clickPendingIntentTemplate);
        // Instruct the widget manager to update the widget
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_listview);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.d(TAG, "onUpdate is called");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Log.d(TAG, "Notifying...");
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

