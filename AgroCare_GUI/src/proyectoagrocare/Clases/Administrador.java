package proyectoagrocare.Clases;

/**
 * Clase para administrador con todos los permisos. Extiende Usuario e
 * implementa todos los permisos disponibles.
 */
public class Administrador extends Usuario {

    private static final String[] PERMISOS = {
        "gestion_cultivo",
        "gestion_fertilizacion",
        "gestion_riego",
        "gestion_cosecha",
        "gestion_usuario",
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
        "editar_cosecha",
        "eliminar_cosecha",
        "generar_reportes",
        "gestionar_usuarios",
        "ver_auditoria",
        "configurar_sistema"
    };

    public Administrador() {
        super();
        this.tipo = "Administrador";
    }

    public Administrador(String nombre, String apellido, String email, String contraseña) {
        super(nombre, apellido, email, contraseña);
        setTipo("Administrador");
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
        return "Administrador";
    }
}
