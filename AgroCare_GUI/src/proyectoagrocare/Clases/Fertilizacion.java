package proyectoagrocare.Clases;

import java.time.LocalDate;
import proyectoagrocare.UnidadFertilizante;

public class Fertilizacion {

    private String id;
    private String cultivoId;
    private LocalDate fecha;
    private String tipoFertilizante;
    private double cantidad;
    private UnidadFertilizante unidad;
    private boolean esProgramado;
    private boolean ejecutado;
    private String idUsuario;

    public Fertilizacion(String cultivoId, LocalDate fecha, String tipoFertilizante,
            double cantidad, UnidadFertilizante unidad, boolean esProgramado, String idUsuario) {
        this.cultivoId = cultivoId;
        this.fecha = fecha;
        this.tipoFertilizante = tipoFertilizante;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.esProgramado = esProgramado;
        this.ejecutado = !esProgramado;
        this.idUsuario = idUsuario;
    }

    // Getters y setters
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

    public String getTipoFertilizante() {
        return tipoFertilizante;
    }

    public void setTipoFertilizante(String tipoFertilizante) {
        this.tipoFertilizante = tipoFertilizante;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public UnidadFertilizante getUnidad() {
        return unidad;
    }

    public void setUnidad(UnidadFertilizante unidad) {
        this.unidad = unidad;
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
