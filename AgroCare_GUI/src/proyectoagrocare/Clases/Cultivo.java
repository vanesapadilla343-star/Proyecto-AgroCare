package proyectoagrocare.Clases;

import java.time.LocalDate;

public class Cultivo {

    private String id;
    private String tipoPlanta;
    private LocalDate fechaSiembra;
    private double area;
    private String observaciones;
    private String idUsuario;

    public Cultivo(String tipoPlanta, LocalDate fechaSiembra, double area, String idUsuario) {
        this.tipoPlanta = tipoPlanta;
        this.fechaSiembra = fechaSiembra;
        this.area = area;
        this.idUsuario = idUsuario;
        this.observaciones = "";
    }

    public Cultivo(String tipoPlanta, LocalDate fechaSiembra, double area, String observaciones, String idUsuario) {
        this(tipoPlanta, fechaSiembra, area, idUsuario);
        this.observaciones = observaciones;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipoPlanta() {
        return tipoPlanta;
    }

    public void setTipoPlanta(String tipoPlanta) {
        this.tipoPlanta = tipoPlanta;
    }

    public LocalDate getFechaSiembra() {
        return fechaSiembra;
    }

    public void setFechaSiembra(LocalDate fechaSiembra) {
        this.fechaSiembra = fechaSiembra;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

}
