package proyectoagrocare.Gestor;

import java.time.LocalDate;
import java.util.ArrayList;
import proyectoagrocare.Clases.Cultivo;
import proyectoagrocare.Clases.Fertilizacion;
import proyectoagrocare.Clases.Riego;
import proyectoagrocare.Persistencia.PersistenciaJSON;
import proyectoagrocare.UnidadFertilizante;

public class GestorCultivo {

    private ArrayList<Cultivo> cultivos;
    private GestorCosecha gestorCosecha;
    private GestorFertilizacion gestorFertilizacion;
    private GestorRiego gestorRiego;
    private static final String ARCHIVO_CULTIVOS = "cultivos";

    public GestorCultivo() {
        this.cultivos = new ArrayList<>();
    }

    public void setGestores(GestorCosecha gestorCosecha, GestorFertilizacion gestorFertilizacion,
            GestorRiego gestorRiego) {
        this.gestorCosecha = gestorCosecha;
        this.gestorFertilizacion = gestorFertilizacion;
        this.gestorRiego = gestorRiego;
    }

    private String generarId() {
        return "CULT_" + System.currentTimeMillis();
    }

    public boolean registrar(Cultivo cultivo) {
        if (!validarDatos(cultivo)) {
            return false;
        }

        cultivo.setId(generarId());
        cultivos.add(cultivo);

        // Crear automáticamente un riego programado inicial
        try {

            LocalDate fechaRiego = cultivo.getFechaSiembra().plusDays(2);
            Riego riegoProgramado = new Riego(
                    cultivo.getId(),
                    fechaRiego,
                    0,
                    true,
                    cultivo.getIdUsuario()
            );

            LocalDate fechaFertilizacion = cultivo.getFechaSiembra().plusDays(2);
            Fertilizacion Nitrogeno = new Fertilizacion(
                    cultivo.getId(),
                    fechaFertilizacion,
                    "Nitrogeno",
                    0,
                    UnidadFertilizante.KG,
                    true,
                    cultivo.getIdUsuario()
            );

            Fertilizacion fosforo = new Fertilizacion(
                    cultivo.getId(),
                    fechaFertilizacion,
                    "Fosforo",
                    0,
                    UnidadFertilizante.KG,
                    true,
                    cultivo.getIdUsuario()
            );

            gestorRiego.registrar(riegoProgramado);
            gestorFertilizacion.registrar(Nitrogeno);
            gestorFertilizacion.registrar(fosforo);
            System.out.println("Riego programado automáticamente para el cultivo " + cultivo.getId());

        } catch (Exception e) {
            System.out.println(" Error al crear riego programado automático: " + e.getMessage());
        }

        return true;
    }

    public boolean actualizar(Cultivo cultivo) {
        if (!validarDatos(cultivo)) {
            return false;
        }

        for (int i = 0; i < cultivos.size(); i++) {
            if (cultivos.get(i).getId().equals(cultivo.getId())) {
                cultivos.set(i, cultivo);
                return true;
            }
        }
        return false;
    }

    public boolean eliminar(String id) {

        if (gestorFertilizacion != null) {
            gestorFertilizacion.eliminarPorCultivo(id);
        }
        if (gestorRiego != null) {
            gestorRiego.eliminarPorCultivo(id);
        }
        if (gestorCosecha != null) {
            gestorCosecha.eliminarPorCultivo(id);
        }

        return cultivos.removeIf(cultivo -> cultivo.getId().equals(id));
    }

    public Cultivo buscarPorId(String id) {
        for (Cultivo cultivo : cultivos) {
            if (cultivo.getId().equals(id)) {
                return cultivo;
            }
        }
        return null;
    }

    public ArrayList<Cultivo> obtenerTodos() {
        return new ArrayList<>(cultivos);
    }

    public ArrayList<Cultivo> obtenerPorUsuario(String idUsuario) {
        ArrayList<Cultivo> resultado = new ArrayList<>();
        for (Cultivo c : cultivos) {
            if (c.getIdUsuario() != null && c.getIdUsuario().equals(idUsuario)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public boolean tieneCosechas(String cultivoId) {
        if (gestorCosecha == null) {
            return false;
        }
        return gestorCosecha.existenPorCultivo(cultivoId);
    }

    public boolean validarDatos(Cultivo cultivo) {
        LocalDate hoy = LocalDate.now();

        if (cultivo.getTipoPlanta() == null || cultivo.getTipoPlanta().trim().isEmpty()) {
            System.out.println("ERROR: El tipo de planta no puede estar vacío.");
            return false;
        }

        if (cultivo.getFechaSiembra() == null || cultivo.getFechaSiembra().isAfter(hoy)) {
            System.out.println("ERROR: La fecha de siembra no puede ser futura.");
            return false;
        }

        if (cultivo.getArea() <= 0) {
            System.out.println("ERROR: El área debe ser mayor a 0.");
            return false;
        }

        return true;
    }

    public boolean validarEdicionFechaOArea(String cultivoId) {
        if (tieneCosechas(cultivoId)) {
            System.out.println("ERROR: No se puede editar fecha ni área porque este cultivo ya tiene cosechas registradas.");
            return false;
        }
        return true;
    }

    public void cargarDesdeArchivo() {
        cultivos = PersistenciaJSON.cargar(ARCHIVO_CULTIVOS, Cultivo.class);
        System.out.println("Cultivos cargados: " + cultivos.size() + " registros.");
    }

    public void guardarEnArchivo() {
        if (PersistenciaJSON.guardar(ARCHIVO_CULTIVOS, cultivos)) {
            System.out.println("Cultivos guardados: " + cultivos.size() + " registros.");
        }
    }

}
