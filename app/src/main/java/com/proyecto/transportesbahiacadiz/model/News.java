package com.proyecto.transportesbahiacadiz.model;

import com.google.gson.annotations.SerializedName;

public class News {
    @SerializedName("idNoticia")
    private int idNew;
    @SerializedName("titulo")
    private String title;
    @SerializedName("subTitulo")
    private String subtitle;
    @SerializedName("resumen")
    private String overview;
    @SerializedName("fechaInicio")
    private String startDate;
    @SerializedName("fechafinFija")
    private String finishDate;
    @SerializedName("categoria")
    private String category;

    public News(){}

    public int getIdNew() {
        return idNew;
    }

    public void setIdNew(int idNew) {
        this.idNew = idNew;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "News{" +
                "idNew=" + idNew +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", overview='" + overview + '\'' +
                ", startDate='" + startDate + '\'' +
                ", finishDate='" + finishDate + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
