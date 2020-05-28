package com.proyecto.transportesbahiacadiz.model;

import com.google.gson.annotations.SerializedName;

public class Segment {
    @SerializedName("nombre")
    private String nombre;

    public Segment(){}

    public Segment(String nombre){
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "nombre='" + nombre + '\'' +
                '}';
    }
}
