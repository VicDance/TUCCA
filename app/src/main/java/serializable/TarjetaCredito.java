package serializable;

import java.io.Serializable;

public class TarjetaCredito implements Serializable {
    private static final long serialVersionUID = 6529685098267757699L;

    private int idUser;
    private String titular;
    private String numTarjeta;
    private String caducidad;

    public TarjetaCredito(){

    }

    public TarjetaCredito(String numTarjeta, int idUser, String caducidad, String titular){
        this.numTarjeta = numTarjeta;
        this.idUser = idUser;
        this.caducidad = caducidad;
        this.titular = titular;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getNumTarjeta() {
        return numTarjeta;
    }

    public void setNumTarjeta(String numTarjeta) {
        this.numTarjeta = numTarjeta;
    }

    public String getCaducidad() {
        return caducidad;
    }

    public void setCaducidad(String caducidad) {
        this.caducidad = caducidad;
    }

    @Override
    public String toString() {
        return "TarjetaCredito{" + "idUser=" + idUser + ", titular=" + titular + ", numTarjeta=" + numTarjeta + ", caducidad=" + caducidad + '}';
    }
}
