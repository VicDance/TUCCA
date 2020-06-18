package serializable;

import java.io.Serializable;

public class Municipio implements Serializable {
    private static final long serialVersionUID = 6529685098267757693L;

    private int idMunicipio;
    private String nombreMunicipio;

    public Municipio(){

    }

    public Municipio(int idMunicipio, String nombreMunicipio) {
        this.idMunicipio = idMunicipio;
        this.nombreMunicipio = nombreMunicipio;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }

    @Override
    public String toString() {
        return "Municipio{" + "idMunicipio=" + idMunicipio + ", nombreMunicipio=" + nombreMunicipio + '}';
    }
}
