package com.android.app.atfnews.repository;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.android.app.atfnews.model.AtfNewsItem;
import com.android.app.atfnews.model.FavAtfNewsItemFull;
import com.android.app.atfnews.model.FavoriteAtfNewsItem;
import com.android.app.atfnews.model.User;

import java.util.List;

@Dao
public interface FavoriteAtfNewsItemDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteAtfNewsItem favoriteatfnewsitem);

    @Query("SELECT * FROM atfnewsitem INNER JOIN favoriteatfnewsitem ON atfnewsitem.url=favoriteatfnewsitem.atfNewsItemId WHERE favoriteatfnewsitem.userId=:emailId")
    LiveData<List<AtfNewsItem>> getAllFavouriteNewsItemForUser(final String emailId);

    @Query("SELECT * FROM atfnewsitem INNER JOIN favoriteatfnewsitem ON atfnewsitem.url=favoriteatfnewsitem.atfNewsItemId WHERE favoriteatfnewsitem.userId=:emailId and atfnewsitem.url=:url")
    LiveData<AtfNewsItem> getLiveFavouriteNewsItemForUser(final String emailId, final String url);

    @Delete
    void deleteFavAtfNewsItem(FavoriteAtfNewsItem favoriteAtfNewsItem);
}