package com.proyecto.tucca.model;

public class Centre {
    private int idNucleo;
    private int idMunicipio;
    private String idZona;
    private String nombreNucleo;

    public Centre(){

    }

    public Centre(int idNucleo){
        this.idNucleo = idNucleo;
    }

    public Centre(String nombreNucleo){
        this.nombreNucleo = nombreNucleo;
    }

    public Centre(int idNucleo, String nombreNucleo){
        this.idNucleo = idNucleo;
        this.nombreNucleo = nombreNucleo;
    }

    public Centre(int idNucleo, int idMunicipio, String idZona, String nombreNucleo){
        this.idNucleo = idNucleo;
        this.idMunicipio = idMunicipio;
        this.idZona = idZona;
        this.nombreNucleo = nombreNucleo;
    }

    public int getIdNucleo() {
        return idNucleo;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public String getIdZona(){ return  idZona; }

    public String getNombreNucleo() {
        return nombreNucleo;
    }
}
