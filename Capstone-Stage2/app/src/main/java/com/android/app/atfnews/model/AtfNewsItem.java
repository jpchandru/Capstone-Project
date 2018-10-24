package com.android.app.atfnews.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.android.app.atfnews.utils.DateConverter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by cj on 10/4/18.
 */

@Entity(tableName = "atfnewsitem", indices = {@Index(value = {"url"},
        unique = true)})
public class AtfNewsItem implements Parcelable {

    private static final String TAG = "AtfNewsItem";

    /*@NonNull
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;*/
    @NonNull
    @SerializedName("emailId")
    private String emailId;
    @SerializedName("source")
    private String sourceName;
    @SerializedName("author")
    private String author;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @NonNull
    @PrimaryKey
    @SerializedName("url")
    private String url;
    @SerializedName("urlToImage")
    private String imgUrl;
    @SerializedName("publishedAt")
    @TypeConverters(DateConverter.class)
    private String publishDate;
    @SerializedName("content")
    private String content;
    @SerializedName("newsType")
    private String newsType;
    @SerializedName("country")
    private String country;
    @SerializedName("category")
    private String category;

    @Ignore
    public AtfNewsItem() {

    }

    public AtfNewsItem(@NonNull String emailId, String sourceName, String author, String title, String description, @NonNull String url, String imgUrl, String publishDate, String content, String newsType, String country, String category) {
        this.emailId = emailId;
        this.sourceName = sourceName;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imgUrl = imgUrl;
        this.publishDate = publishDate;
        this.content = content;
        this.newsType = newsType;
        this.country = country;
        this.category = category;
    }

    /*public AtfNewsItem(JSONObject jsonObject) {
        try {
            this.sourceName = getSourceName(jsonObject.getJSONObject("source"));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        this.author = jsonObject.optString("author");
        this.title = jsonObject.optString("title");
        this.description = jsonObject.optString("description");
        this.url = jsonObject.optString("url");
        this.imgUrl = jsonObject.optString("urlToImage");
        this.publishDate = jsonObject.optString("publishedAt");
        this.content = jsonObject.optString("content");
    }*/

    /*private String getSourceName(JSONObject jsonObject) {
        this.sourceName = jsonObject.optString("name");
        return this.sourceName;
    }*/

    @NonNull
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(@NonNull String emailId) {
        this.emailId = emailId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AtfNewsItem that = (AtfNewsItem) o;

        if (!emailId.equals(that.emailId)) return false;
        if (sourceName != null ? !sourceName.equals(that.sourceName) : that.sourceName != null)
            return false;
        if (author != null ? !author.equals(that.author) : that.author != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (!url.equals(that.url)) return false;
        if (imgUrl != null ? !imgUrl.equals(that.imgUrl) : that.imgUrl != null) return false;
        if (publishDate != null ? !publishDate.equals(that.publishDate) : that.publishDate != null)
            return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (newsType != null ? !newsType.equals(that.newsType) : that.newsType != null)
            return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        return category != null ? category.equals(that.category) : that.category == null;
    }

    @Override
    public int hashCode() {
        int result = emailId.hashCode();
        result = 31 * result + (sourceName != null ? sourceName.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + url.hashCode();
        result = 31 * result + (imgUrl != null ? imgUrl.hashCode() : 0);
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (newsType != null ? newsType.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AtfNewsItem{" +
                "emailId='" + emailId + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", content='" + content + '\'' +
                ", newsType='" + newsType + '\'' +
                ", country='" + country + '\'' +
                ", category='" + category + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.emailId);
        dest.writeString(this.sourceName);
        dest.writeString(this.author);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.imgUrl);
        dest.writeString(this.publishDate);
        dest.writeString(this.content);
        dest.writeString(this.newsType);
        dest.writeString(this.country);
        dest.writeString(this.category);
    }

    protected AtfNewsItem(Parcel in) {
        this.emailId = in.readString();
        this.sourceName = in.readString();
        this.author = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.url = in.readString();
        this.imgUrl = in.readString();
        this.publishDate = in.readString();
        this.content = in.readString();
        this.newsType = in.readString();
        this.country = in.readString();
        this.category = in.readString();
    }

    public static final Creator<AtfNewsItem> CREATOR = new Creator<AtfNewsItem>() {
        @Override
        public AtfNewsItem createFromParcel(Parcel source) {
            return new AtfNewsItem(source);
        }

        @Override
        public AtfNewsItem[] newArray(int size) {
            return new AtfNewsItem[size];
        }
    };
}
