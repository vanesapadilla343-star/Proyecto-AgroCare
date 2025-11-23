package proyectoagrocare.Gestor;

import java.time.LocalDate;
import java.util.ArrayList;
import proyectoagrocare.Clases.Fertilizacion;
import proyectoagrocare.Persistencia.PersistenciaJSON;

public class GestorFertilizacion {

    private ArrayList<Fertilizacion> fertilizaciones;
    private GestorCultivo gestorCultivo;
    private static final String ARCHIVO_FERTILIZACIONES = "fertilizaciones";

    public GestorFertilizacion() {
        this.fertilizaciones = new ArrayList<>();
    }

    public void setGestorCultivo(GestorCultivo gestorCultivo) {
        this.gestorCultivo = gestorCultivo;
    }

    public boolean registrar(Fertilizacion fertilizacion) {
        if (!validarDatos(fertilizacion)) {
            return false;
        }

        fertilizacion.setId("FERT_" + System.currentTimeMillis());
        fertilizaciones.add(fertilizacion);
        return true;
    }

    public boolean actualizar(Fertilizacion fertilizacion) {
        if (!validarDatos(fertilizacion)) {
            return false;
        }

        for (int i = 0; i < fertilizaciones.size(); i++) {
            if (fertilizaciones.get(i).getId().equals(fertilizacion.getId())) {
                fertilizaciones.set(i, fertilizacion);
                return true;
            }
        }
        return false;
    }

    public boolean eliminar(String id) {
        return fertilizaciones.removeIf(fert -> fert.getId().equals(id));
    }

    public void eliminarPorCultivo(String cultivoId) {
        fertilizaciones.removeIf(f -> f.getCultivoId().equals(cultivoId));
    }

    public Fertilizacion buscarPorId(String id) {
        for (Fertilizacion f : fertilizaciones) {
            if (f.getId().equals(id)) {
                return f;
            }
        }
        return null;
    }

    public ArrayList<Fertilizacion> obtenerTodas() {
        return new ArrayList<>(fertilizaciones);
    }

    public ArrayList<Fertilizacion> obtenerPorUsuario(String idUsuario) {
        ArrayList<Fertilizacion> resultado = new ArrayList<>();
        for (Fertilizacion c : fertilizaciones) {
            if (c.getIdUsuario() != null && c.getIdUsuario().equals(idUsuario)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public boolean validarDatos(Fertilizacion fertilizacion) {
        if (fertilizacion.getCultivoId() == null || fertilizacion.getCultivoId().trim().isEmpty()) {
            return false;
        }
        if (gestorCultivo != null && gestorCultivo.buscarPorId(fertilizacion.getCultivoId()) == null) {
            return false;
        }
        if (fertilizacion.getFecha() == null) {
            return false;
        }

        if (fertilizacion.getTipoFertilizante() == null || fertilizacion.getTipoFertilizante().trim().isEmpty()) {
            return false;
        }

        LocalDate hoy = LocalDate.now();

        if (fertilizacion.isEsProgramado()) {
            // Fecha futura permitida
            return true;
        } else {
            // Registro ejecutado: no puede ser antes de siembra ni futura
            if (fertilizacion.getFecha().isBefore(gestorCultivo.buscarPorId(fertilizacion.getCultivoId()).getFechaSiembra())) {
                return false;
            }
            if (fertilizacion.getFecha().isAfter(hoy)) {
                return false;
            }
        }

        return true;
    }

    public void cargarDesdeArchivo() {
        fertilizaciones = PersistenciaJSON.cargar(ARCHIVO_FERTILIZACIONES, Fertilizacion.class);
        System.out.println("Fertilizaciones cargadas: " + fertilizaciones.size() + " registros.");
    }

    public void guardarEnArchivo() {
        if (PersistenciaJSON.guardar(ARCHIVO_FERTILIZACIONES, fertilizaciones)) {
            System.out.println("Fertilizaciones guardadas: " + fertilizaciones.size() + " registros.");
        }
    }
}
