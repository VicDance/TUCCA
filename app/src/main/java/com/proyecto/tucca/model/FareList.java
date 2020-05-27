package com.proyecto.tucca.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FareList {
    @SerializedName("tarifasInterurbanas")
    private List<Fare> fareList;

    public List<Fare> getFareList() {
        return fareList;
    }
}
