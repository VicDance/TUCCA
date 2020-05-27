package com.proyecto.tucca.model;

public class TimeStopsItem {
    int imageResource;
    String tiempoLLegada, destino;

    public TimeStopsItem(int imageResource, String tiempoLLegada, String destino){
        this.imageResource = imageResource;
        this.tiempoLLegada = tiempoLLegada;
        this.destino = destino;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTiempoLLegada() {
        return tiempoLLegada;
    }

    public String getDestino() {
        return destino;
    }
}
