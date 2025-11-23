package proyectoagrocare;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import proyectoagrocare.Clases.Cosecha;
import proyectoagrocare.Clases.Cultivo;
import proyectoagrocare.Clases.Fertilizacion;
import proyectoagrocare.Clases.Reporte;

public class FormateadorDatos {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Formatea la información de un cultivo para mostrar.
     */
    public static String formatearCultivo(Cultivo cultivo) {
        return String.format("ID: %s | Planta: %s | Fecha: %s | Area: %.2f (m2) | Obs: %s",
                cultivo.getId(),
                cultivo.getTipoPlanta(),
                cultivo.getFechaSiembra().format(formatter),
                cultivo.getArea(),
                cultivo.getObservaciones());
    }

    /**
     * Formatea la información de una fertilización para mostrar.
     */
    public static String formatearFertilizacion(Fertilizacion fertilizacion) {
        return String.format("ID: %s | Cultivo: %s | Fecha: %s | Tipo: %s | Cantidad: %.2f %s",
                fertilizacion.getId(),
                fertilizacion.getCultivoId(),
                fertilizacion.getFecha().format(formatter),
                fertilizacion.getTipoFertilizante(),
                fertilizacion.getCantidad(),
                fertilizacion.getUnidad().getEtiqueta());
    }

    public static String formatearCosecha(Cosecha cosecha) {
        return String.format("ID: %s | Cultivo: %s | Fecha: %s | Cantidad: %.2f kg | Rendimiento: %.2f kg/m²",
                cosecha.getId(),
                cosecha.getCultivoId(),
                cosecha.getFecha().format(formatter),
                cosecha.getCantidad(),
                cosecha.getRendimiento());
    }

    /**
     * Formatea reporte detallado con especificaciones completas del cultivo.
     */
    public static String formatearReporte(Reporte r) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n┌──────── REPORTE TEMPORADA ").append(r.getNombreTemporada()).append(" ────────┐\n");
        sb.append("Rango Fechas: ").append(r.getFechaInicio().format(formatter))
                .append(" → ").append(r.getFechaFin().format(formatter)).append("\n");
        sb.append("Cultivo ID: ").append(r.getCultivoId()).append(" | Planta: ").append(r.getTipoPlanta()).append("\n");
        sb.append("Fecha Siembra: ").append(r.getFechaSiembra().format(formatter))
                .append(" | Área: ").append(r.getArea()).append(" m²\n");
        sb.append("Riegos: ").append(r.getNumeroRiegos()).append(" | Litros: ").append(r.getLitrosUsados()).append("\n");
        for (String det : r.getDetallesRiegos()) {
            sb.append("  - ").append(det).append("\n");
        }
        sb.append("Fertilizaciones: ").append(r.getNumeroFertilizaciones()).append("\n");
        for (String det : r.getDetallesFertilizaciones()) {
            sb.append("  - ").append(det).append("\n");
        }
        sb.append("Cosechas: ").append(r.getNumeroCosechas()).append(" | Total Kg: ").append(r.getKgCosechados()).append("\n");
        for (String det : r.getDetallesCosechas()) {
            sb.append("  - ").append(det).append("\n");
        }
        sb.append("Rendimiento promedio: ").append(r.getRendimientoPromedio()).append(" kg/m²\n");
        sb.append("Observaciones: ").append(r.getObservaciones()).append("\n");
        sb.append("└───────────────────────────────────────────────┘\n");
        return sb.toString();
    }
}
