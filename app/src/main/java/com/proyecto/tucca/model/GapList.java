package com.proyecto.tucca.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GapList {
    @SerializedName("saltos")
    private List<Gap> gapList;

    public List<Gap> getGapList() {
        return gapList;
    }
}
