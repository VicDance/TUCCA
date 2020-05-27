package com.proyecto.tucca.model;

public class Stop {
    private int idParada;
    private String idZona;
    private String nombre;
    private String latitud;
    private String longitud;

    public Stop(){}

    public Stop(String nombre){
        this.nombre = nombre;
    }

    public Stop(int idParada, String idZona, String nombre, String latitud, String longitud) {
        this.idParada = idParada;
        this.idZona = idZona;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getIdParada() {
        return idParada;
    }

    public void setIdParada(int idParada) {
        this.idParada = idParada;
    }

    public String getIdZona() {
        return idZona;
    }

    public void setIdZona(String idZona) {
        this.idZona = idZona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    @Override
    public String toString() {
        return "Stop{" +
                "idParada=" + idParada +
                ", idZona='" + idZona + '\'' +
                ", nombre='" + nombre + '\'' +
                ", latitud='" + latitud + '\'' +
                ", longitud='" + longitud + '\'' +
                '}';
    }
}
