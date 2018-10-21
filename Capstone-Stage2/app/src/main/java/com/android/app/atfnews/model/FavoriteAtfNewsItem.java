package com.android.app.atfnews.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by cj on 10/4/18.
 */

@Entity(tableName = "favoriteatfnewsitem", indices = {@Index(value = {"atfNewsItemId"},
        unique = true)},
        primaryKeys = {"atfNewsItemId"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "email",
                        childColumns = "userId"),
                @ForeignKey(entity = AtfNewsItem.class,
                        parentColumns = "url",
                        childColumns = "atfNewsItemId")
        })
public class FavoriteAtfNewsItem implements Parcelable {

    private static final String TAG = "FavoriteAtfNewsItem";

    @NonNull
    public String userId;
    @NonNull
    public String atfNewsItemId;

    public FavoriteAtfNewsItem(final String userId, final String atfNewsItemId) {
        this.userId = userId;
        this.atfNewsItemId = atfNewsItemId;
    }


    public FavoriteAtfNewsItem() {

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.atfNewsItemId);
    }

    protected FavoriteAtfNewsItem(Parcel in) {
        this.userId = in.readString();
        this.atfNewsItemId = in.readString();
    }

    public static final Creator<FavoriteAtfNewsItem> CREATOR = new Creator<FavoriteAtfNewsItem>() {
        @Override
        public FavoriteAtfNewsItem createFromParcel(Parcel source) {
            return new FavoriteAtfNewsItem(source);
        }

        @Override
        public FavoriteAtfNewsItem[] newArray(int size) {
            return new FavoriteAtfNewsItem[size];
        }
    };
}
