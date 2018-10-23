package com.android.app.atfnews.utils;

import android.net.Uri;

import com.android.app.atfnews.model.AtfNewsUrlType;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by cj on 10/7/18.
 */

public class NetworkUtils {

    public static final String BASE_URL = "https://newsapi.org/v2";
    public static final String TRENDING_NEWS_URL_HEAD= "/top-headlines?country=";
    public static final String TRENDING_NEWS_URL_TAIL = "&category=business&apiKey=43d16424dfb047119d540c2daf1bbdde";
    public static final String TRENDING_NEWS_URL= "/top-headlines?country=us&category=business&apiKey=43d16424dfb047119d540c2daf1bbdde";


    public static URL buildUrl(String atfNewsUrlType){
        URL url = null;
        if(atfNewsUrlType !=null){
            Uri builtUri = Uri.parse(BASE_URL+TRENDING_NEWS_URL_HEAD+atfNewsUrlType+TRENDING_NEWS_URL_TAIL);
            url = null;
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e){
                e.printStackTrace();
            }
        }else{
            Uri builtUri = Uri.parse(BASE_URL+TRENDING_NEWS_URL);
            url = null;
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e){
                e.printStackTrace();
            }
        }
        return url;
    }


    public static String getResponseFromHTTPUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
