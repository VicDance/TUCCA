package com.proyecto.tucca.model;

public class Zone {
    private String idZona;
    private String nombreZona;

    public Zone(String idZona, String nombreZona){
        this.idZona = idZona;
        this.nombreZona = nombreZona;
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
}
