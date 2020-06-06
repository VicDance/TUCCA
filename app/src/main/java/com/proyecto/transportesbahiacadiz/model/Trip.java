package com.proyecto.transportesbahiacadiz.model;

import java.sql.Date;

public class Trip {
    private int idUsuario;
    private int idLinea;
    private int idMunicipio;
    private double tarifa;
    private String horaSalida;
    private String horaLlegada;
    private Date fechaViaje;
    private String linea;
    private String municipio;

    public Trip(){}

    public Trip(int idUsuario, int idLinea, int idMunicipio, double tarifa, String horaSalida, String horaLlegada, Date fechaViaje) {
        this.idUsuario = idUsuario;
        this.idLinea = idLinea;
        this.idMunicipio = idMunicipio;
        this.tarifa = tarifa;
        this.horaSalida = horaSalida;
        this.horaLlegada = horaLlegada;
        this.fechaViaje = fechaViaje;
    }

    public Trip(int idUsuario, int idLinea, int idMunicipio, String horaSalida){
        this.idUsuario = idUsuario;
        this.idLinea = idLinea;
        this.idMunicipio = idMunicipio;
        this.horaSalida = horaSalida;
    }

    public Trip(String linea, String municipio, String horaSalida, Date fechaViaje){
        this.linea = linea;
        this.municipio = municipio;
        this.horaSalida = horaSalida;
        this.fechaViaje = fechaViaje;
    }

    public Trip(int idMunicipio, String horaSalida){
        this.idMunicipio = idMunicipio;
        this.horaSalida = horaSalida;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdLinea() {
        return idLinea;
    }

    public void setIdLinea(int idLinea) {
        this.idLinea = idLinea;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public double getTarifa() {
        return tarifa;
    }

    public void setTarifa(double tarifa) {
        this.tarifa = tarifa;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getHoraLlegada() {
        return horaLlegada;
    }

    public void setHoraLlegada(String horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    public Date getFechaViaje() {
        return fechaViaje;
    }

    public void setFechaViaje(Date fechaViaje) {
        this.fechaViaje = fechaViaje;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "idUsuario=" + idUsuario +
                ", idLinea=" + idLinea +
                ", idMunicipio=" + idMunicipio +
                ", tarifa=" + tarifa +
                ", horaSalida='" + horaSalida + '\'' +
                ", horaLlegada='" + horaLlegada + '\'' +
                ", fechaViaje=" + fechaViaje +
                '}';
    }
}
