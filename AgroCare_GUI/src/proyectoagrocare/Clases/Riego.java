package proyectoagrocare.Clases;

import java.time.LocalDate;

public class Riego {

    private String id;
    private String cultivoId;
    private LocalDate fecha;
    private double cantidadAgua; // Cantidad solo para riego ejecutado
    private boolean esProgramado;
    private boolean ejecutado;
    private String idUsuario;

    public Riego(String cultivoId, LocalDate fecha, double cantidadAgua, boolean esProgramado, String idUsuario) {
        this.cultivoId = cultivoId;
        this.fecha = fecha;
        this.cantidadAgua = cantidadAgua;
        this.esProgramado = esProgramado;
        this.ejecutado = !esProgramado;
        this.idUsuario = idUsuario;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCultivoId() {
        return cultivoId;
    }

    public void setCultivoId(String cultivoId) {
        this.cultivoId = cultivoId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getCantidadAgua() {
        return cantidadAgua;
    }

    public void setCantidadAgua(double cantidadAgua) {
        this.cantidadAgua = cantidadAgua;
    }

    public boolean isEsProgramado() {
        return esProgramado;
    }

    public void setEsProgramado(boolean esProgramado) {
        this.esProgramado = esProgramado;
    }

    public boolean isEjecutado() {
        return ejecutado;
    }

    public void setEjecutado(boolean ejecutado) {
        this.ejecutado = ejecutado;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

}
