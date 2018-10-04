package com.udacity.gradle.atfnews.backend;

import com.android.atfnewsjavalibrary.ATFNewsJavaLibraryActivity;

public class MyBean {

    private String myData;

    public String getData() {

        myData = new ATFNewsJavaLibraryActivity().getRandomJokes();
        return myData;
    }

    public void setData(String data) {
        myData = data;
    }
}