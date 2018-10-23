package com.android.app.atfnews.utils;

import android.content.Context;

import com.android.app.atfnews.model.AtfNewsUrlType;
import com.android.app.atfnews.model.ComplexPreferences;
import com.android.app.atfnews.model.FirebaseAtfNewsUser;
import com.android.app.atfnews.model.User;

public class PrefUtils {

    public static void setCurrentUser(User currentUser, Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        complexPreferences.putObject("current_user_value", currentUser);
        complexPreferences.commit();
    }

    public static void setCurrentUser(FirebaseAtfNewsUser currentUser, Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs_firebase", 0);
        complexPreferences.putObject("current_user_value_firebase", currentUser);
        complexPreferences.commit();
    }

    public static void setUrlNewsType(String countryCode, Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs_country_code", 0);
        complexPreferences.putObject("current_country_code", countryCode);
        complexPreferences.commit();
    }

    public static User getCurrentUser(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        User currentUser = complexPreferences.getObject("current_user_value", User.class);
        return currentUser;
    }

    public static FirebaseAtfNewsUser getCurrentUserFromFirebase(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs_firebase", 0);
        FirebaseAtfNewsUser currentUser = complexPreferences.getObject("current_user_value_firebase", FirebaseAtfNewsUser.class);
        return currentUser;
    }

    public static String getUrlNewsType(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs_country_code", 0);
        String atfNewsUrlType = complexPreferences.getObject("current_country_code", String.class);
        return atfNewsUrlType;
    }

    public static void clearCurrentUser(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }

    public static void clearCurrentUserFromFirebase(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs_firebase", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }

    public static void setFavoriteAtfNews(int userId, int favNewsId, Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "fav_news", 0);
        complexPreferences.putObject("current_fav_news", favNewsId);
        complexPreferences.commit();
    }

    public static String getFavoriteAtfNews(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "fav_news", 0);
        String favNewsId = complexPreferences.getObject("current_fav_news", String.class);
        return favNewsId;
    }

    public static void clearFavoriteAtfNews(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "fav_news", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }

}
