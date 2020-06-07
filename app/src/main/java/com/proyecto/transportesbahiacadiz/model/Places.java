package com.proyecto.transportesbahiacadiz.model;

import java.io.Serializable;

public class Places implements Serializable {
    private int idLugar;
    private int idMunicipio;
    private String latitud;
    private String longitud;
    private String nombre;
    //private String nombreMunicipio;

    public Places(){

    }

    public Places(String latitud, String longitud, String nombre){
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Places(int idMunicipio, String latitud, String longitud, String nombre) {
        this.idMunicipio = idMunicipio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombre = nombre;
    }

    public Places(int idLugar, int idMunicipio, String latitud, String longitud, String nombre){
        this.idLugar = idLugar;
        this.idMunicipio = idMunicipio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombre = nombre;
    }

    public int getIdLugar() {
        return idLugar;
    }

    public void setIdLugar(int idLugar) {
        this.idLugar = idLugar;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Places{" +
                "idLugar=" + idLugar +
                ", idMunicipio=" + idMunicipio +
                ", latitud='" + latitud + '\'' +
                ", longitud='" + longitud + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
