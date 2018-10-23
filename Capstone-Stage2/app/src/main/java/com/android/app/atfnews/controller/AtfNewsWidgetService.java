package com.android.app.atfnews.controller;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class AtfNewsWidgetService extends RemoteViewsService {

    public final static String TAG = AtfNewsWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        Log.d(TAG, "onGetViewFactory is called");
        return new AtfNewsCountriesListFactory(getApplicationContext());
    }
}
