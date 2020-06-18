package serializable;

import java.io.Serializable;

public class Parada implements Serializable {
    private static final long serialVersionUID = 6529685098267757692L;

    private int idParada;
    private String idZona;
    private String nombreParada;
    private String latitud;
    private String longitud;

    public Parada(){

    }

    public Parada(String nombreParada){
        this.nombreParada = nombreParada;
    }

    public Parada(int idParada, String idZona, String nombreParada, String latitud, String longitud){
        this.idParada = idParada;
        this.idZona = idZona;
        this.nombreParada = nombreParada;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getIdParada() {
        return idParada;
    }

    public void setIdParada(int idParada){
        this.idParada = idParada;
    }

    public String getIdZona() {
        return idZona;
    }

    public void setIdZona(String idZona) {
        this.idZona = idZona;
    }

    public String getNombreParada() {
        return nombreParada;
    }

    public void setNombreParada(String nombreParada) {
        this.nombreParada = nombreParada;
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
        return "Parada{" + "idParada=" + idParada + ", idZona=" + idZona + ", nombreParada=" + nombreParada + ", latitud=" + latitud + ", longitud=" + longitud + '}';
    }
}
