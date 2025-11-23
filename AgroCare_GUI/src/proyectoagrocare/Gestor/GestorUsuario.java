package proyectoagrocare.Gestor;

import java.util.ArrayList;
import proyectoagrocare.Clases.Usuario;
import proyectoagrocare.Clases.Administrador;
import proyectoagrocare.Clases.Agricultor;
import proyectoagrocare.Persistencia.PersistenciaJSON;

public class GestorUsuario {

    private ArrayList<Usuario> usuarios;
    private Usuario usuarioActual;
    private static final String ARCHIVO_USUARIOS = "usuarios";

    public GestorUsuario() {
        this.usuarios = new ArrayList<>();
    }

    public boolean registrarUsuario(Usuario usuario) {
        if (!validarDatos(usuario)) {
            return false;
        }

        if (buscarPorEmail(usuario.getEmail()) != null) {
            System.out.println("ERROR: El email ya está registrado.");
            return false;
        }

        usuario.setId(generarId());
        usuarios.add(usuario);
        System.out.println("Usuario '" + usuario.getNombreCompleto() + "' registrado como " + usuario.getTipoUsuario());
        return true;
    }

    public Usuario autenticar(String email, String contraseña) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario == null) {
            System.out.println("ERROR: Usuario no encontrado.");
            return null;
        }

        if (!usuario.getContraseña().equals(contraseña)) {
            System.out.println("ERROR: Contraseña incorrecta.");
            return null;
        }

        if (!usuario.isActivo()) {
            System.out.println("ERROR: Usuario inactivo.");
            return null;
        }

        this.usuarioActual = usuario;
        System.out.println("Bienvenido, " + usuario.getNombreCompleto() + " (" + usuario.getTipoUsuario() + ")");
        return usuario;
    }

    public void cerrarSesion() {
        if (usuarioActual != null) {
            System.out.println("Sesión cerrada: " + usuarioActual.getNombreCompleto());
            usuarioActual = null;
        }
    }

    public Usuario obtenerUsuarioActual() {
        return usuarioActual;
    }

    public boolean hayUsuarioActual() {
        return usuarioActual != null;
    }

    public Usuario buscarPorId(String id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }
        return null;
    }

    public Usuario buscarPorEmail(String email) {
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(email)) {
                return usuario;
            }
        }
        return null;
    }

    public ArrayList<Usuario> obtenerTodos() {
        return new ArrayList<>(usuarios);
    }

    public ArrayList<Usuario> obtenerAdministradores() {
        ArrayList<Usuario> admins = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Administrador) {
                admins.add(usuario);
            }
        }
        return admins;
    }

    public ArrayList<Usuario> obtenerEmpleados() {
        ArrayList<Usuario> empleados = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Agricultor) {
                empleados.add(usuario);
            }
        }
        return empleados;
    }

    public boolean reemplazarUsuario(Usuario viejo, Usuario nuevo) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(viejo.getId())) {
                usuarios.set(i, nuevo);
                return true;
            }
        }
        return false;
    }

    public boolean actualizar(Usuario usuario) {
        if (!validarDatos(usuario)) {
            return false;
        }

        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(usuario.getId())) {
                usuarios.set(i, usuario);
                return true;
            }
        }
        return false;
    }

    public boolean desactivarUsuario(String id) {
        Usuario usuario = buscarPorId(id);
        if (usuario != null) {
            usuario.setActivo(false);
            System.out.println("Usuario desactivado: " + usuario.getNombreCompleto());
            return true;
        }
        return false;
    }

    public boolean activarUsuario(String id) {
        Usuario usuario = buscarPorId(id);
        if (usuario != null) {
            usuario.setActivo(true);
            System.out.println("Usuario activado: " + usuario.getNombreCompleto());
            return true;
        }
        return false;
    }

    private boolean validarDatos(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            System.out.println("ERROR: El nombre no puede estar vacío.");
            return false;
        }

        if (usuario.getApellido() == null || usuario.getApellido().trim().isEmpty()) {
            System.out.println("ERROR: El apellido no puede estar vacío.");
            return false;
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            System.out.println("ERROR: El email no puede estar vacío.");
            return false;
        }

        if (!usuario.getEmail().contains("@")) {
            System.out.println("ERROR: Email inválido.");
            return false;
        }

        if (usuario.getContraseña() == null || usuario.getContraseña().length() < 4) {
            System.out.println("ERROR: La contraseña debe tener al menos 4 caracteres.");
            return false;
        }

        return true;
    }

    private String generarId() {
        return "USR_" + System.currentTimeMillis();
    }

    public void cargarDesdeArchivo() {
        usuarios = PersistenciaJSON.cargar(ARCHIVO_USUARIOS, Usuario.class);
        System.out.println("[GESTOR USUARIO] Usuarios cargados: " + usuarios.size() + " registros.");
    }

    public void guardarEnArchivo() {
        if (PersistenciaJSON.guardar(ARCHIVO_USUARIOS, usuarios)) {
            System.out.println("[GESTOR USUARIO] Usuarios guardados: " + usuarios.size() + " registros.");
        }
    }
}
