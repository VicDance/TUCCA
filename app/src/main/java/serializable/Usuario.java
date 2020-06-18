package serializable;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Date;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;

    private int id;
    private String nombre;
    private String correo;
    private int tfno;
    private Date fecha_nac;
    private byte[] imagen;
    private String contraseña;

    public Usuario(){}

    public Usuario(int id, String nombre, String contraseña, String correo, byte[] imagen){
        this.id = id;
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.correo = correo;
        this.imagen = imagen;
    }

    public Usuario(String nombre, String contraseña, String correo, Date fecha_nac, int tfno){
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.correo = correo;
        this.fecha_nac = fecha_nac;
        this.tfno = tfno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getTfno() {
        return tfno;
    }

    public void setTfno(int tfno) {
        this.tfno = tfno;
    }

    public Date getFecha_nac() {
        return fecha_nac;
    }

    public void setFecha_nac(Date fecha_nac) {
        this.fecha_nac = fecha_nac;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", tfno=" + tfno +
                ", fecha_nac='" + fecha_nac + '\'' +
                ", imagen='" + imagen + '\'' +
                ", contraseña='" + contraseña + '\'' +
                '}';
    }
}
