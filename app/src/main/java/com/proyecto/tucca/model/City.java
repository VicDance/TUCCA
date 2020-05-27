package com.proyecto.tucca.model;

import androidx.lifecycle.ViewModel;

public class City{
    private int idMunicipio;
    private String nombreMunicipio;

    public City(){

    }

    public City(int idMunicipio){
        this.idMunicipio = idMunicipio;
    }

    public City(int idMunicipio, String nombreMunicipio){
        this.idMunicipio = idMunicipio;
        this.nombreMunicipio = nombreMunicipio;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio){this.idMunicipio = idMunicipio;}

    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    public void setNombreMunicipio(String nombreMunicipio){this.nombreMunicipio = nombreMunicipio;}
}
