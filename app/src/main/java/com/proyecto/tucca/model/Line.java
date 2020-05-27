package com.proyecto.tucca.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Line {
    @SerializedName("nucleosIda")
    private List<Centre> centres;
    @SerializedName("codigo")
    private String codigo;
    @SerializedName("nombre")
    private String nombre;



    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }
}
