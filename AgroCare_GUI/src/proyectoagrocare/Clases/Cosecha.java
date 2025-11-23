package proyectoagrocare.Clases;

import java.time.LocalDate;

public class Cosecha {

    private String id;
    private String cultivoId;
    private LocalDate fecha;
    private double cantidad;
    private double rendimiento;
    private String idUsuario;

    public Cosecha(String cultivoId, LocalDate fecha, double cantidad, String idUsuario) {
        this.cultivoId = cultivoId;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.idUsuario = idUsuario;
        this.rendimiento = 0.0;
    }

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

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getRendimiento() {
        return rendimiento;
    }

    public void setRendimiento(double rendimiento) {
        this.rendimiento = rendimiento;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
