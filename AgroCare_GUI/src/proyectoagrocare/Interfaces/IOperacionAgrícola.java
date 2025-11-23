package proyectoagrocare.Interfaces;

public interface IOperacionAgrícola {
    boolean registrar(Object operacion);
    boolean actualizar(Object operacion);
    boolean eliminar(String id);
    Object buscarPorId(String id);
}
