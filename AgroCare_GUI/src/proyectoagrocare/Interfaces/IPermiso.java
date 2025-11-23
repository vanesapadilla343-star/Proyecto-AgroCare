package proyectoagrocare.Interfaces;

public interface IPermiso {
    boolean tienePermiso(String accion);
    String[] obtenerPermisos();
}
