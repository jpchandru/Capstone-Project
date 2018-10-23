package com.android.app.atfnews.repository;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.android.app.atfnews.model.AtfNewsItem;

import java.util.List;

import kotlin.jvm.JvmSuppressWildcards;


/**
 * Created by cj on 10/5/18.
 */

@Dao
public interface AtfNewsItemDAO {

    @Query("SELECT * FROM atfnewsitem where country = :countryCode ORDER BY publishDate DESC")
    LiveData<List<AtfNewsItem>> loadAllAtfNewsItem(final String countryCode);

    /*@Query("SELECT * FROM atfnewsitem where id = :id")
    LiveData<AtfNewsItem> findAtfNewsItemById(int id);*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    void insertAtfNewsItem(List<AtfNewsItem> atfNewsItemList);

    @Delete
    void deleteAtfNewsItem(AtfNewsItem atfNewsItem);

}
