package proyectoagrocare.Gestor;

import java.time.LocalDate;
import java.util.ArrayList;
import proyectoagrocare.Clases.Cosecha;
import proyectoagrocare.Clases.Cultivo;
import proyectoagrocare.Persistencia.PersistenciaJSON;

/**
 * Gestor de cosechas con persistencia.
 */
public class GestorCosecha {

    private ArrayList<Cosecha> cosechas;
    private GestorCultivo gestorCultivo;
    private static final String ARCHIVO_COSECHAS = "cosechas";

    public GestorCosecha() {
        this.cosechas = new ArrayList<>();
    }

    public void setGestorCultivo(GestorCultivo gestorCultivo) {
        this.gestorCultivo = gestorCultivo;
    }

    private String generarId() {
        return "COSECHA_" + System.currentTimeMillis();
    }

    public boolean registrar(Cosecha cosecha) {
        if (!validarDatos(cosecha)) {
            return false;
        }

        cosecha.setId(generarId());
        calcularRendimiento(cosecha);
        cosechas.add(cosecha);
        return true;
    }

    public Cosecha buscarPorId(String id) {
        for (Cosecha c : cosechas) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<Cosecha> obtenerTodas() {
        return new ArrayList<>(cosechas);
    }

    public ArrayList<Cosecha> obtenerPorUsuario(String idUsuario) {
        ArrayList<Cosecha> resultado = new ArrayList<>();
        for (Cosecha c : cosechas) {
            if (c.getIdUsuario() != null && c.getIdUsuario().equals(idUsuario)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public boolean eliminar(String id) {
        return cosechas.removeIf(cosecha -> cosecha.getId().equals(id));
    }

    public void eliminarPorCultivo(String cultivoId) {
        cosechas.removeIf(c -> c.getCultivoId().equals(cultivoId));
    }

    public boolean existenPorCultivo(String cultivoId) {
        for (Cosecha cosecha : cosechas) {
            if (cosecha.getCultivoId().equals(cultivoId)) {
                return true;
            }
        }
        return false;
    }

    public void calcularRendimiento(Cosecha cosecha) {
        if (gestorCultivo != null) {
            Cultivo cultivo = gestorCultivo.buscarPorId(cosecha.getCultivoId());
            if (cultivo != null && cultivo.getArea() > 0) {
                double rendimiento = cosecha.getCantidad() / cultivo.getArea();
                cosecha.setRendimiento(rendimiento);
            }
        }
    }

    public boolean validarDatos(Cosecha cosecha) {
        LocalDate hoy = LocalDate.now();

        if (cosecha.getCultivoId() == null || cosecha.getCultivoId().trim().isEmpty()) {
            System.out.println("ERROR: El ID del cultivo no puede estar vacío.");
            return false;
        }

        if (gestorCultivo != null && gestorCultivo.buscarPorId(cosecha.getCultivoId()) == null) {
            System.out.println("ERROR: El cultivo con ID '" + cosecha.getCultivoId() + "' no existe.");
            return false;
        }

        if (cosecha.getCantidad() <= 0) {
            System.out.println("ERROR: La cantidad debe ser mayor a 0.");
            return false;
        }

        if (cosecha.getFecha().isBefore(gestorCultivo.buscarPorId(cosecha.getCultivoId()).getFechaSiembra())) {
            System.out.println("ERROR: La fecha no puede ser anterior a la siembra del cultivo");
            return false;
        }

        if (cosecha.getFecha() == null || cosecha.getFecha().isAfter(hoy)) {
            System.out.println("ERROR: La fecha no puede ser futura.");
            return false;
        }

        return true;
    }

    public void cargarDesdeArchivo() {
        cosechas = PersistenciaJSON.cargar(ARCHIVO_COSECHAS, Cosecha.class);
        System.out.println("Cosechas cargadas: " + cosechas.size() + " registros.");
    }

    public void guardarEnArchivo() {
        if (PersistenciaJSON.guardar(ARCHIVO_COSECHAS, cosechas)) {
            System.out.println("Cosechas guardadas: " + cosechas.size() + " registros.");
        }
    }

    public boolean actualizar(Cosecha cosecha) {
        if (!validarDatos(cosecha)) {
            return false;
        }

        calcularRendimiento(cosecha);
        for (int i = 0; i < cosechas.size(); i++) {
            if (cosechas.get(i).getId().equals(cosecha.getId())) {
                cosechas.set(i, cosecha);
                return true;
            }
        }
        return false;
    }

}
