package com.android.app.atfnews.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.AtfNewsCountries;

import java.util.ArrayList;

public class AtfNewsCountriesListFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String TAG = AtfNewsCountriesListFactory.class.getSimpleName();
    /**
     * The RemoteViewsFactory acts as an adapter providing the data to the widget
     * Explanation for most of the methods in:
     * https://www.sitepoint.com/killer-way-to-show-a-list-of-items-in-android-collection-widget/
     */
    private Context mContext;
    private AtfNewsCountries atfNewsCountries;
    private ArrayList<AtfNewsCountries> atfNewsCountriesArrayList;

    public AtfNewsCountriesListFactory(Context context) {
        this.mContext = context;
        atfNewsCountriesArrayList = new ArrayList<>();

    }

    @Override
    public void onCreate() {
        //recipe = Preferences.loadRecipe(mContext);
        //atfNewsCountriesArrayList = recipe.getIngredientList();
        //ArrayList<AtfNewsCountries> atfNewsCountriesList = new ArrayList<AtfNewsCountries>();

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
        if(atfNewsCountriesArrayList == null && atfNewsCountriesArrayList.size() == 0){
            atfNewsCountriesArrayList.add(new AtfNewsCountries("USA", "us"));
            atfNewsCountriesArrayList.add(new AtfNewsCountries("United Kingdom", "gb"));
            atfNewsCountriesArrayList.add(new AtfNewsCountries("Australia", "au"));
            atfNewsCountriesArrayList.add(new AtfNewsCountries("India", "in"));
        }
        /*atfNewsCountriesArrayList.add(new AtfNewsCountries("USA", "us"));
        atfNewsCountriesArrayList.add(new AtfNewsCountries("United Kingdom", "gb"));
        atfNewsCountriesArrayList.add(new AtfNewsCountries("Australia", "au"));
        atfNewsCountriesArrayList.add(new AtfNewsCountries("India", "in"));*/
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
        rv.setTextViewText(R.id.widget_atfnews, countryCode);
        Log.d(TAG, "position: " + position
                + ", countryCode: " + countryCode);
       // Toast.makeText(mContext, "Clicked on country code: "+countryCode, Toast.LENGTH_SHORT);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("clicked_country_code", countryCode);
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
