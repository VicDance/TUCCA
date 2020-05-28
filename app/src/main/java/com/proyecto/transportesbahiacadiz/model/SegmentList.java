package com.proyecto.transportesbahiacadiz.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SegmentList {
    @SerializedName("bloques")
    private List<Segment> segmentList;

    public List<Segment> getSegmentList() {
        return segmentList;
    }
}
