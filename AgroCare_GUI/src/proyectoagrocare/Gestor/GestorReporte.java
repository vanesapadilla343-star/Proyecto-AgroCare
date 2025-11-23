package proyectoagrocare.Gestor;

import java.time.LocalDate;
import java.util.ArrayList;
import proyectoagrocare.Clases.Cosecha;
import proyectoagrocare.Clases.Cultivo;
import proyectoagrocare.Clases.Fertilizacion;
import proyectoagrocare.Clases.Reporte;
import proyectoagrocare.Clases.Riego;
import proyectoagrocare.Clases.Usuario;
import proyectoagrocare.Persistencia.PersistenciaJSON;

/**
 * Gestor de reportes mejorado con detalles de especificaciones de cultivos.
 * Permite generar, consultar, eliminar y decidir guardar reportes.
 */
public class GestorReporte {

    private ArrayList<Reporte> reportes;
    private GestorCultivo gestorCultivo;
    private GestorRiego gestorRiego;
    private GestorFertilizacion gestorFertilizacion;
    private GestorCosecha gestorCosecha;
    private static final String ARCHIVO_REPORTES = "reportes";

    public GestorReporte(GestorCultivo gestorCultivo, GestorRiego gestorRiego,
            GestorFertilizacion gestorFertilizacion, GestorCosecha gestorCosecha) {
        this.reportes = new ArrayList<>();
        this.gestorCultivo = gestorCultivo;
        this.gestorRiego = gestorRiego;
        this.gestorFertilizacion = gestorFertilizacion;
        this.gestorCosecha = gestorCosecha;
    }

    /**
     * Genera un reporte por temporada para un usuario.
     */
    public ArrayList<Reporte> generarReportePorTemporada(Usuario usuario, String nombreTemporada,
            LocalDate fechaInicio, LocalDate fechaFin) {
        ArrayList<Reporte> reportesTemporada = new ArrayList<>();

        if (fechaInicio == null || fechaFin == null || fechaInicio.isAfter(fechaFin)) {
            System.out.println("Error: fechas inválidas.");
            return reportesTemporada;
        }

        ArrayList<Cultivo> cultivos = usuario.getTipo().equalsIgnoreCase("Administrador")
                ? gestorCultivo.obtenerTodos()
                : gestorCultivo.obtenerPorUsuario(usuario.getId());

        for (Cultivo cultivo : cultivos) {
            if (!cultivo.getFechaSiembra().isAfter(fechaFin)) {

                ArrayList<Riego> riegos = filtrarRiegosPorFecha(cultivo.getId(), fechaInicio, fechaFin);
                ArrayList<Fertilizacion> fertilizaciones = filtrarFertilizacionesPorFecha(cultivo.getId(), fechaInicio, fechaFin);
                ArrayList<Cosecha> cosechas = filtrarCosechasPorFecha(cultivo.getId(), fechaInicio, fechaFin);

                if (!riegos.isEmpty() || !fertilizaciones.isEmpty() || !cosechas.isEmpty()) {
                    Reporte reporte = new Reporte(
                            nombreTemporada,
                            fechaInicio,
                            fechaFin,
                            cultivo.getId(),
                            cultivo.getTipoPlanta(),
                            cultivo.getFechaSiembra(),
                            cultivo.getArea(),
                            riegos.size(),
                            calcularTotalLitros(riegos),
                            fertilizaciones.size(),
                            cosechas.size(),
                            calcularTotalKg(cosechas),
                            calcularRendimientoPromedio(cosechas),
                            cultivo.getObservaciones(),
                            usuario.getId()
                    );

                    riegos.forEach(r -> reporte.agregarDetalleRiego(
                            String.format("Fecha: %s | Cantidad: %.2f L | Estado: %s",
                                    r.getFecha(), r.getCantidadAgua(), r.isEsProgramado() ? "Programado" : "Ejecutado")
                    ));

                    fertilizaciones.forEach(f -> reporte.agregarDetalleFertilizacion(
                            String.format("Fecha: %s | Tipo: %s | Cantidad: %.2f %s",
                                    f.getFecha(), f.getTipoFertilizante(), f.getCantidad(), f.getUnidad().getEtiqueta())
                    ));

                    cosechas.forEach(c -> reporte.agregarDetalleCosecha(
                            String.format("Fecha: %s | Cantidad: %.2f kg | Rendimiento: %.2f kg/m²",
                                    c.getFecha(), c.getCantidad(), c.getRendimiento())
                    ));

                    reportesTemporada.add(reporte);
                }
            }
        }

        return reportesTemporada;
    }

    /**
     * Guarda un reporte generado.
     */
    public void guardarReporte(Reporte reporte) {
        reportes.add(reporte);
        guardarEnArchivo();
    }

    /**
     * Elimina un reporte por índice o referencia.
     */
    public boolean eliminarReporte(Reporte reporte) {
        boolean eliminado = reportes.remove(reporte);
        if (eliminado) {
            guardarEnArchivo();
        }
        return eliminado;
    }

    /**
     * Consulta reportes guardados por usuario.
     */
    public ArrayList<Reporte> obtenerReportesPorUsuario(String idUsuario) {
        ArrayList<Reporte> encontrados = new ArrayList<>();
        for (Reporte r : reportes) {
            if (r.getIdUsuario().equals(idUsuario)) {
                encontrados.add(r);
            }
        }
        return encontrados;
    }

    /**
     * Consulta reportes por temporada.
     */
    public ArrayList<Reporte> buscarPorTemporada(String nombreTemporada) {
        ArrayList<Reporte> encontrados = new ArrayList<>();
        for (Reporte r : reportes) {
            if (r.getNombreTemporada().equalsIgnoreCase(nombreTemporada)) {
                encontrados.add(r);
            }
        }
        return encontrados;
    }

    /**
     * Obtiene todos los reportes generados.
     */
    public ArrayList<Reporte> obtenerTodos() {
        return new ArrayList<>(reportes);
    }

    /**
     * Carga desde archivo
     */
    public void cargarDesdeArchivo() {
        reportes = PersistenciaJSON.cargar(ARCHIVO_REPORTES, Reporte.class);
    }

    /**
     * Guarda en archivo
     */
    public void guardarEnArchivo() {
        PersistenciaJSON.guardar(ARCHIVO_REPORTES, reportes);
    }

    // --------- MÉTODOS AUXILIARES PRIVADOS ---------
    private ArrayList<Riego> filtrarRiegosPorFecha(String cultivoId, LocalDate inicio, LocalDate fin) {
        ArrayList<Riego> filtrados = new ArrayList<>();
        for (Riego r : gestorRiego.obtenerTodos()) {
            if (r.getCultivoId().equals(cultivoId) && !r.getFecha().isBefore(inicio) && !r.getFecha().isAfter(fin)) {
                filtrados.add(r);
            }
        }
        return filtrados;
    }

    private ArrayList<Fertilizacion> filtrarFertilizacionesPorFecha(String cultivoId, LocalDate inicio, LocalDate fin) {
        ArrayList<Fertilizacion> filtrados = new ArrayList<>();
        for (Fertilizacion f : gestorFertilizacion.obtenerTodas()) {
            if (f.getCultivoId().equals(cultivoId) && !f.getFecha().isBefore(inicio) && !f.getFecha().isAfter(fin)) {
                filtrados.add(f);
            }
        }
        return filtrados;
    }

    private ArrayList<Cosecha> filtrarCosechasPorFecha(String cultivoId, LocalDate inicio, LocalDate fin) {
        ArrayList<Cosecha> filtrados = new ArrayList<>();
        for (Cosecha c : gestorCosecha.obtenerTodas()) {
            if (c.getCultivoId().equals(cultivoId) && !c.getFecha().isBefore(inicio) && !c.getFecha().isAfter(fin)) {
                filtrados.add(c);
            }
        }
        return filtrados;
    }

    private double calcularTotalLitros(ArrayList<Riego> riegos) {
        return riegos.stream().mapToDouble(Riego::getCantidadAgua).sum();
    }

    private double calcularTotalKg(ArrayList<Cosecha> cosechas) {
        return cosechas.stream().mapToDouble(Cosecha::getCantidad).sum();
    }

    private double calcularRendimientoPromedio(ArrayList<Cosecha> cosechas) {
        if (cosechas.isEmpty()) {
            return 0.0;
        }
        return cosechas.stream().mapToDouble(Cosecha::getRendimiento).average().orElse(0.0);
    }
}
