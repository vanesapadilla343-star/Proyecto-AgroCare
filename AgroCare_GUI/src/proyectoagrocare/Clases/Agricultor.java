package proyectoagrocare.Clases;

//Extends == HERENCIA
public class Agricultor extends Usuario {

    private String areaAsignada;
    private static final String[] PERMISOS = {
        "gestion_cultivo",
        "gestion_fertilizacion",
        "gestion_riego",
        "gestion_cosecha",
        "gestion_reporte",
        "crear_cultivo",
        "editar_cultivo",
        "eliminar_cultivo",
        "crear_riego",
        "editar_riego",
        "eliminar_riego",
        "ejecutar_riego",
        "crear_fertilizacion",
        "editar_fertilizacion",
        "eliminar_fertilizacion",
        "ejecutar_fertilizacion",
        "crear_cosecha",
        "editar_cosecha"
    };

    public Agricultor(String nombre, String apellido, String email, String contraseña, String areaAsignada) {
        super(nombre, apellido, email, contraseña);
        setTipo("Agricultor");
        this.areaAsignada = areaAsignada;
    }

    public String getAreaAsignada() {
        return areaAsignada;
    }

    public void setAreaAsignada(String areaAsignada) {
        this.areaAsignada = areaAsignada;
    }

    @Override
    public boolean tienePermiso(String accion) {
        for (String permiso : PERMISOS) {
            if (permiso.equals(accion)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String[] obtenerPermisos() {
        return PERMISOS.clone();
    }

    @Override
    public String getTipoUsuario() {
        return "Agricultor";
    }
}
