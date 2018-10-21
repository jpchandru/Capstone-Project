package com.android.app.atfnews.utils;

import android.text.format.DateUtils;

import com.android.app.atfnews.model.AtfNewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * Created by cj on 10/7/18.
 */

public class JsonUtils {

    private static final String ARTICLES = "articles";
    public static JSONObject newsSourceResult; //TODO sourceNAME

    public static List<AtfNewsItem> parseNewsApiJson(String json, String newsType, String country, String category, String emailId) throws JSONException {
        JSONObject newsApiDetails = new JSONObject(json);
        //newsSourceResult = newsApiDetails.getJSONObject("source");
        JSONArray newsApiResults = newsApiDetails.getJSONArray(ARTICLES);
        AtfNewsItem[] atfNewsItemsArray = new AtfNewsItem[newsApiResults.length()];
        for (int i = 0; i <newsApiResults.length(); i++){
            JSONObject newsData = newsApiResults.getJSONObject(i);
            atfNewsItemsArray[i] = createApiNewsItemObject(newsData, newsType, country, category, emailId);
        }
        return Arrays.asList(atfNewsItemsArray);
    }

    private static AtfNewsItem createApiNewsItemObject(JSONObject jsonObject, String newsType, String country, String category, String emailId){
        AtfNewsItem atfNewsItem = new AtfNewsItem(emailId,
                null,
                jsonObject.optString("author"),
                jsonObject.optString("title"),
                jsonObject.optString("description"),
                jsonObject.optString("url"),
                jsonObject.optString("urlToImage"),
                jsonObject.optString("publishedAt"),
        jsonObject.optString("content"),
        newsType, country, category);
        return atfNewsItem;
    }

    private static String getSourceName(JSONObject jsonObject) {
        return jsonObject.optString("name");
        //return this.sourceName;
    }
}
