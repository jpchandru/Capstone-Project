package com.android.app.atfnews.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.AtfNewsCountries;
import com.android.app.atfnews.utils.PrefUtils;

import java.util.ArrayList;

public class AtfNewsCountriesListFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String TAG = AtfNewsCountriesListFactory.class.getSimpleName();
    private Context mContext;
    private ArrayList<AtfNewsCountries> atfNewsCountriesArrayList;
    private static final String CLICKEDCOUNTRYCODE = "clicked_country_code";

    public AtfNewsCountriesListFactory(Context context) {
        this.mContext = context;
        atfNewsCountriesArrayList = new ArrayList<>();

    }

    @Override
    public void onCreate() {
        //Below hardcoded countrylist will be replaced by a REST API call to retrieve all countries
        atfNewsCountriesArrayList.add(new AtfNewsCountries("USA", "us"));
        atfNewsCountriesArrayList.add(new AtfNewsCountries("United Kingdom", "gb"));
        atfNewsCountriesArrayList.add(new AtfNewsCountries("Australia", "au"));
        atfNewsCountriesArrayList.add(new AtfNewsCountries("India", "in"));
    }

    /**
     * onDataSetChanged is called whenever the widget is updated
     */
    @Override
    public void onDataSetChanged() {
        //Below hardcoded countrylist will be replaced by a REST API call to retrieve all countries
        if (atfNewsCountriesArrayList == null && atfNewsCountriesArrayList.size() == 0) {
            atfNewsCountriesArrayList.add(new AtfNewsCountries("USA", "us"));
            atfNewsCountriesArrayList.add(new AtfNewsCountries("United Kingdom", "gb"));
            atfNewsCountriesArrayList.add(new AtfNewsCountries("Australia", "au"));
            atfNewsCountriesArrayList.add(new AtfNewsCountries("India", "in"));
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        Log.d(TAG, "Number of Countries " + String.valueOf(atfNewsCountriesArrayList.size()));
        return atfNewsCountriesArrayList.size();

    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        String countryCode = atfNewsCountriesArrayList.get(position).getCountryCode();
        String countryName = atfNewsCountriesArrayList.get(position).getCountryName();
        rv.setTextViewText(R.id.widget_atfnews, countryName);
        Log.d(TAG, "position: " + position
                + ", countryCode: " + countryCode);
        Intent fillInIntent = new Intent();
        //PrefUtils.setUrlNewsType(countryCode,mContext);
        fillInIntent.putExtra(CLICKEDCOUNTRYCODE, countryCode);
        rv.setOnClickFillInIntent(R.id.widget_atfnews, fillInIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
