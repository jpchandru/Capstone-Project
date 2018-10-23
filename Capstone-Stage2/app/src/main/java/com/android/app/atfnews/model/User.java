package com.android.app.atfnews.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 22-04-2015.
 */

@Entity(tableName = "atfnewsuser", indices = {@Index(value = {"facebookID"},
        unique = true)})
public class User implements Parcelable {


    @NonNull
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @NonNull
    @PrimaryKey
    @SerializedName("email")
    public String email;
    @SerializedName("facebookID")
    public String facebookID;
    @SerializedName("googleId")
    public String googleId;
    @SerializedName("gender")
    public String gender;
    @SerializedName("photoUrl")
    public String photoUrl;

    public User() {
    }

    public User(@NonNull String id, String name, @NonNull String email, String facebookID, String googleId, String gender, String photoUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.facebookID = facebookID;
        this.googleId = googleId;
        this.gender = gender;
        this.photoUrl = photoUrl;
    }

    protected User(Parcel source) {
        this.id = source.readString();
        this.name = source.readString();
        this.email = source.readString();
        this.facebookID = source.readString();
        this.googleId = source.readString();
        this.gender = source.readString();
        this.photoUrl = source.readString();
    }

    // interface Parcelable callback to parcel
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.facebookID);
        dest.writeString(this.googleId);
        dest.writeString(this.gender);
        dest.writeString(this.photoUrl);
    }


    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public String getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!id.equals(user.id)) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (facebookID != null ? !facebookID.equals(user.facebookID) : user.facebookID != null)
            return false;
        if (googleId != null ? !googleId.equals(user.googleId) : user.googleId != null)
            return false;
        if (gender != null ? !gender.equals(user.gender) : user.gender != null) return false;
        return photoUrl != null ? photoUrl.equals(user.photoUrl) : user.photoUrl == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (facebookID != null ? facebookID.hashCode() : 0);
        result = 31 * result + (googleId != null ? googleId.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (photoUrl != null ? photoUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", facebookID='" + facebookID + '\'' +
                ", googleId='" + googleId + '\'' +
                ", gender='" + gender + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
