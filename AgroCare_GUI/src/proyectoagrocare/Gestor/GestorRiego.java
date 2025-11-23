package proyectoagrocare.Gestor;

import java.time.LocalDate;
import java.util.ArrayList;
import proyectoagrocare.Clases.Riego;
import proyectoagrocare.Persistencia.PersistenciaJSON;

public class GestorRiego {

    private ArrayList<Riego> riegos;
    private GestorCultivo gestorCultivo;
    private static final String ARCHIVO_RIEGOS = "riegos";

    public GestorRiego() {
        this.riegos = new ArrayList<>();
    }

    public void setGestorCultivo(GestorCultivo gestorCultivo) {
        this.gestorCultivo = gestorCultivo;
    }

    public boolean registrar(Riego riego) {
        if (!validarDatos(riego)) {
            return false;
        }
        riego.setId("RIEGO_" + System.currentTimeMillis());
        riegos.add(riego);
        return true;
    }

    public boolean actualizar(Riego riego) {
        if (!validarDatos(riego)) {
            return false;
        }
        for (int i = 0; i < riegos.size(); i++) {
            if (riegos.get(i).getId().equals(riego.getId())) {
                riegos.set(i, riego);
                return true;
            }
        }
        return false;
    }

    public boolean eliminar(String id) {
        return riegos.removeIf(r -> r.getId().equals(id));
    }

    public void eliminarPorCultivo(String cultivoId) {
        riegos.removeIf(r -> r.getCultivoId().equals(cultivoId));
    }

    public Riego buscarPorId(String id) {
        for (Riego r : riegos) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public ArrayList<Riego> obtenerTodos() {
        return new ArrayList<>(riegos);
    }
    
    public ArrayList<Riego> obtenerPorUsuario(String idUsuario) {
        ArrayList<Riego> resultado = new ArrayList<>();
        for (Riego c : riegos) {
            if (c.getIdUsuario() != null && c.getIdUsuario().equals(idUsuario)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public boolean validarDatos(Riego riego) {
        if (riego.getCultivoId() == null || riego.getCultivoId().trim().isEmpty()) {
            return false;
        }
        if (gestorCultivo != null && gestorCultivo.buscarPorId(riego.getCultivoId()) == null) {
            return false;
        }
        if (riego.getFecha() == null) {
            return false;
        }

        LocalDate hoy = LocalDate.now();

        if (riego.isEsProgramado()) {
            // Fecha futura permitida
            return true;
        } else {
            // Registro ejecutado: no puede ser antes de siembra ni futura
            if (riego.getFecha().isBefore(gestorCultivo.buscarPorId(riego.getCultivoId()).getFechaSiembra())) {
                return false;
            }
            if (riego.getFecha().isAfter(hoy)) {
                return false;
            }
        }

        return true;
    }

    public void cargarDesdeArchivo() {
        riegos = PersistenciaJSON.cargar(ARCHIVO_RIEGOS, Riego.class);
    }

    public void guardarEnArchivo() {
        PersistenciaJSON.guardar(ARCHIVO_RIEGOS, riegos);
    }
}
