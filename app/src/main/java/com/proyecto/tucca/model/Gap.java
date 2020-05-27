package com.proyecto.tucca.model;

import com.google.gson.annotations.SerializedName;

public class Gap {
    @SerializedName("origen")
    private String zonaOrigen;
    @SerializedName("destino")
    private String zonaDestino;
    @SerializedName("saltos")
    private int saltos;

    public Gap() {
    }

    public Gap(int saltos) {
        this.saltos = saltos;
    }

    public Gap(String zonaOrigen, String zonaDestino, int saltos) {
        this.zonaOrigen = zonaOrigen;
        this.zonaDestino = zonaDestino;
        this.saltos = saltos;
    }

    public String getZonaOrigen() {
        return zonaOrigen;
    }

    public void setZonaOrigen(String zonaOrigen) {
        this.zonaOrigen = zonaOrigen;
    }

    public String getZonaDestino() {
        return zonaDestino;
    }

    public void setZonaDestino(String zonaDestino) {
        this.zonaDestino = zonaDestino;
    }

    public int getSaltos() {
        return saltos;
    }

    public void setSaltos(int saltos) {
        this.saltos = saltos;
    }

    @Override
    public String toString() {
        return "Gap{" +
                "zonaOrigen='" + zonaOrigen + '\'' +
                ", zonaDestino='" + zonaDestino + '\'' +
                ", saltos=" + saltos +
                '}';
    }
}
