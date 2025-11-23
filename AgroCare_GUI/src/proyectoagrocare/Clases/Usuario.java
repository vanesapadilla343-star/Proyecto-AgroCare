package proyectoagrocare.Clases;

import java.time.LocalDate;
import proyectoagrocare.Interfaces.IPermiso;

/**
 * Clase abstracta base para usuarios. Implementa herencia y polimorfismo para
 * diferentes tipos de usuarios.
 */

//IMPLEMENTS == INTERFACE ES DECIR MEDIANTE LO CUAL IMPLEMENTAS UNA INTERFAZ EN UNA CLASE ESPECIFICA
public abstract class Usuario implements IPermiso {

    protected String id;
    protected String nombre;
    protected String apellido;
    protected String email;
    protected String contraseña;
    protected LocalDate fechaRegistro;
    protected boolean activo;
    protected String tipo;

    public Usuario() {
        // Constructor sin argumentos para Gson
        this.fechaRegistro = LocalDate.now();
        this.activo = true;
    }

    public Usuario(String nombre, String apellido, String email, String contraseña) {
        this();
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contraseña = contraseña;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public abstract String getTipoUsuario();
}
