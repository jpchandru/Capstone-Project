package com.android.app.atfnews.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AtfNewsCountries implements Parcelable {

    private static final String TAG = "AtfNewsCountries";

    private String countryName;
    @NonNull
    private String countryCode;

    public AtfNewsCountries(String countryName, @NonNull String countryCode) {
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @NonNull
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(@NonNull String countryCode) {
        this.countryCode = countryCode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.countryName);
        dest.writeString(this.countryCode);
    }

    protected AtfNewsCountries(Parcel in) {
        this.countryName = in.readString();
        this.countryCode = in.readString();
    }

    public static final Creator<AtfNewsCountries> CREATOR = new Creator<AtfNewsCountries>() {
        @Override
        public AtfNewsCountries createFromParcel(Parcel source) {
            return new AtfNewsCountries(source);
        }

        @Override
        public AtfNewsCountries[] newArray(int size) {
            return new AtfNewsCountries[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AtfNewsCountries that = (AtfNewsCountries) o;

        if (countryName != null ? !countryName.equals(that.countryName) : that.countryName != null)
            return false;
        return countryCode.equals(that.countryCode);
    }

    @Override
    public int hashCode() {
        int result = countryName != null ? countryName.hashCode() : 0;
        result = 31 * result + countryCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AtfNewsCountries{" +
                "countryName='" + countryName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}
