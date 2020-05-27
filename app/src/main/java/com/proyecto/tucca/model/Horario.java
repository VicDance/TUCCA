package com.proyecto.tucca.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Horario {
    @SerializedName("idlinea")
    private int idLinea;
    @SerializedName("codigo")
    private String nameLinea;
    @SerializedName("horas")
    private List<String> horas;
    @SerializedName("dias")
    private String dias;
    @SerializedName("observaciones")
    private String observaciones;

    public int getIdLinea() {
        return idLinea;
    }

    public String getNameLinea() {
        return nameLinea;
    }

    public List<String> getHoras() {
        return horas;
    }

    public String getDias() {
        return dias;
    }

    public String getObservaciones() {
        return observaciones;
    }

    @Override
    public String toString() {
        return "Horario{" +
                "idLinea=" + idLinea +
                ", nameLinea='" + nameLinea + '\'' +
                ", horas=" + horas +
                ", dias='" + dias + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
