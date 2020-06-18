package serializable;

import java.io.Serializable;

public class Zona implements Serializable {
    private static final long serialVersionUID = 6529685098267757691L;

    private String idZona;
    private String nombreZona;
    private String color;

    public Zona(){

    }

    public Zona(String idZona, String nombreZona){
        this.idZona = idZona;
        this.nombreZona = nombreZona;
    }

    public Zona(String idZona, String nombreZona, String color){
        this.idZona = idZona;
        this.nombreZona = nombreZona;
        this.color = color;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Zona{" + "idZona=" + idZona + ", nombreZona=" + nombreZona + ", color=" + color + '}';
    }
}
