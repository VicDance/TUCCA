package com.proyecto.transportesbahiacadiz.model;

public class Zone {
    private String idZona;
    private String nombreZona;
    private String color;

    public Zone(String idZona, String nombreZona){
        this.idZona = idZona;
        this.nombreZona = nombreZona;
    }

    public Zone(String idZona, String nombreZona, String color){
        this.idZona = idZona;
        this.nombreZona = nombreZona;
        this.color = color;
    }

    public String getIdZona() {
        return idZona;
    }

    public void setIdZona(String idZona) {
        this.idZona = idZona;
    }

    public String getNombreZona() {
        return nombreZona;
    }

    public void setNombreZona(String nombreZona) {
        this.nombreZona = nombreZona;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "idZona='" + idZona + '\'' +
                ", nombreZona='" + nombreZona + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
