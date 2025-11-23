package proyectoagrocare.Clases;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Representa un reporte de producción de un cultivo en una temporada. Incluye
 * estadísticas agregadas y detalles de riegos, fertilizaciones y cosechas.
 */
public class Reporte {

    private String nombreTemporada;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String cultivoId;
    private String tipoPlanta;
    private LocalDate fechaSiembra;
    private double area;
    private ArrayList<String> detallesRiegos = new ArrayList<>();
    private ArrayList<String> detallesFertilizaciones = new ArrayList<>();
    private ArrayList<String> detallesCosechas = new ArrayList<>();
    private int numeroRiegos;
    private double litrosUsados;
    private int numeroFertilizaciones;
    private int numeroCosechas;
    private double kgCosechados;
    private double rendimientoPromedio;
    private String observaciones;
    private String idUsuario;

    public Reporte(String nombreTemporada, LocalDate fechaInicio, LocalDate fechaFin,
            String cultivoId, String tipoPlanta, LocalDate fechaSiembra, double area,
            int numeroRiegos, double litrosUsados,
            int numeroFertilizaciones, int numeroCosechas,
            double kgCosechados, double rendimientoPromedio,
            String observaciones, String idUsuario) {
        this.nombreTemporada = nombreTemporada;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cultivoId = cultivoId;
        this.tipoPlanta = tipoPlanta;
        this.fechaSiembra = fechaSiembra;
        this.area = area;
        this.numeroRiegos = numeroRiegos;
        this.litrosUsados = litrosUsados;
        this.numeroFertilizaciones = numeroFertilizaciones;
        this.numeroCosechas = numeroCosechas;
        this.kgCosechados = kgCosechados;
        this.rendimientoPromedio = rendimientoPromedio;
        this.observaciones = observaciones;
        this.idUsuario = idUsuario;
    }

    // ---------- GETTERS ----------
    public String getIdUsuario() {
        return idUsuario;
    }

    public String getNombreTemporada() {
        return nombreTemporada;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public String getCultivoId() {
        return cultivoId;
    }

    public String getTipoPlanta() {
        return tipoPlanta;
    }

    public LocalDate getFechaSiembra() {
        return fechaSiembra;
    }

    public double getArea() {
        return area;
    }

    public int getNumeroRiegos() {
        return numeroRiegos;
    }

    public double getLitrosUsados() {
        return litrosUsados;
    }

    public int getNumeroFertilizaciones() {
        return numeroFertilizaciones;
    }

    public int getNumeroCosechas() {
        return numeroCosechas;
    }

    public double getKgCosechados() {
        return kgCosechados;
    }

    public double getRendimientoPromedio() {
        return rendimientoPromedio;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public ArrayList<String> getDetallesRiegos() {
        return new ArrayList<>(detallesRiegos);
    }

    public ArrayList<String> getDetallesFertilizaciones() {
        return new ArrayList<>(detallesFertilizaciones);
    }

    public ArrayList<String> getDetallesCosechas() {
        return new ArrayList<>(detallesCosechas);
    }

    // ---------- MÉTODOS PARA AGREGAR DETALLES ----------
    public void agregarDetalleRiego(String detalle) {
        detallesRiegos.add(detalle);
    }

    public void agregarDetalleFertilizacion(String detalle) {
        detallesFertilizaciones.add(detalle);
    }

    public void agregarDetalleCosecha(String detalle) {
        detallesCosechas.add(detalle);
    }
}
