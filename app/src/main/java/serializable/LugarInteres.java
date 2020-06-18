package serializable;

import java.io.Serializable;

public class LugarInteres implements Serializable {
    private static final long serialVersionUID = 6529685098267757697L;

    private int idLugarInteres;
    private int idMunicipio;
    private String latitud;
    private String longitud;
    private String nombreLugar;

    public LugarInteres(){

    }

    public LugarInteres(int idLugarInteres, int idMunicipio, String latitud, String longitud, String nombreLugar) {
        this.idLugarInteres = idLugarInteres;
        this.idMunicipio = idMunicipio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreLugar = nombreLugar;
    }

    public int getLugarInteres() {
        return idLugarInteres;
    }

    public void setIdLugarInteres(int lugarInteres) {
        this.idLugarInteres = lugarInteres;
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

    public String getNombreLugar() {
        return nombreLugar;
    }

    public void setNombreLugar(String nombreLugar) {
        this.nombreLugar = nombreLugar;
    }

    @Override
    public String toString() {
        return "LugarInteres{" + "lugarInteres=" + idLugarInteres + ", idMunicipio=" + idMunicipio + ", latitud=" + latitud + ", longitud=" + longitud + ", nombreLugar=" + nombreLugar + '}';
    }
}
