package com.proyecto.transportesbahiacadiz.model;

public class CodigoQR {
    private String hora_utilizacion;
    private String hora_salida;
    private String mensaje;
    /*private String nombreMunicipio;
    private String tipoTarjeta;*/

    public CodigoQR(){}

    public CodigoQR(String hora_utilizacion, String hora_salida, String mensaje/*String nombreMunicipio, String tipoTarjeta*/) {
        this.hora_utilizacion = hora_utilizacion;
        this.hora_salida = hora_salida;
        this.mensaje = mensaje;
        /*this.nombreMunicipio = nombreMunicipio;
        this.tipoTarjeta = tipoTarjeta;*/
    }

    public String getHora_utilizacion() {
        return hora_utilizacion;
    }

    public void setHora_utilizacion(String hora_utilizacion) {
        this.hora_utilizacion = hora_utilizacion;
    }

    public String getHora_salida() {
        return hora_salida;
    }

    public void setHora_salida(String hora_salida) {
        this.hora_salida = hora_salida;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "CodigoQR{" +
                "hora_utilizacion='" + hora_utilizacion + '\'' +
                ", hora_salida='" + hora_salida + '\'' +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }
/*public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    @Override
    public String toString() {
        return "CodigoQR{" +
                "hora_utilizacion='" + hora_utilizacion + '\'' +
                ", hora_salida='" + hora_salida + '\'' +
                ", nombreMunicipio='" + nombreMunicipio + '\'' +
                ", tipoTarjeta='" + tipoTarjeta + '\'' +
                '}';
    }*/
}
