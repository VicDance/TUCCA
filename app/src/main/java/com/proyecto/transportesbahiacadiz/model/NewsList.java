package com.proyecto.transportesbahiacadiz.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsList {
    @SerializedName("noticias")
    private List<News> newsList;

    public List<News> getNewsList() {
        return newsList;
    }
}
