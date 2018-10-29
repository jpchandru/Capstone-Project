package com.android.app.atfnews.repository;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.android.app.atfnews.model.User;

/**
 * Created by cj on 10/5/18.
 */

@Dao
public interface UserDAO {

    @Query("SELECT * FROM atfnewsuser where id = :id")
    User findUserWithId(String id);

    @Query("SELECT * FROM atfnewsuser where email = :email")
    User findUserWithEmail(String email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("UPDATE atfnewsuser SET id=:id, name=:name, email=:email, facebookID=:faceBookID, googleId=:googleId, photoUrl=:photoUrl  WHERE email = :email")
    void updateUser(String id, String name, String email, String faceBookID, String googleId, String photoUrl);

}
