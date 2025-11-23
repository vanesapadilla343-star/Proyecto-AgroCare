package Interfaces.vistas.usuario;

import Interfaces.vistas.ControladorConContexto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import proyectoagrocare.Clases.Administrador;
import proyectoagrocare.Clases.Agricultor;
import proyectoagrocare.Clases.Usuario;
import proyectoagrocare.SistemaAgroCare;
import utils.Alerta;

public class UsuarioController implements ControladorConContexto {

    private SistemaAgroCare sistema;
    private Usuario usuario;

    @FXML
    private Label lblTotalUsuarios;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private ComboBox<String> cbTipoUsuario;
    @FXML
    private TextField txtAreaAsignada;

    @FXML
    private TableView<Usuario> tblUsuarios;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colApellido;
    @FXML
    private TableColumn<Usuario, String> colEmail;
    @FXML
    private TableColumn<Usuario, String> colTipo;
    @FXML
    private TableColumn<Usuario, String> colArea;
    @FXML
    private TableColumn<Usuario, String> colEstado;

    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnHabilitar;
    @FXML
    private Button btnDeshabilitar;

    @FXML
    private Button btnLimpiar;

    private ObservableList<Usuario> listaUsuarios;
    private Usuario usuarioSeleccionado;

    @FXML
    public void initialize() {
        // ComboBox de tipo de usuario
        cbTipoUsuario.getItems().addAll("Administrador", "Empleado");
        cbTipoUsuario.valueProperty().addListener((obs, oldVal, newVal) -> {
            txtAreaAsignada.setDisable(!"Empleado".equals(newVal));
            if (!"Empleado".equals(newVal)) {
                txtAreaAsignada.clear();
            }
        });

        // Configurar tabla
        tblUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colApellido.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getApellido()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colTipo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTipoUsuario()));
        colArea.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue() instanceof Agricultor ? ((Agricultor) data.getValue()).getAreaAsignada() : ""
        ));
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().isActivo() ? "Activo" : "Inactivo"
        ));

        // Lista observable
        listaUsuarios = FXCollections.observableArrayList();
        tblUsuarios.setItems(listaUsuarios);

        // Escuchar selección de tabla
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                usuarioSeleccionado = newSel;
                llenarCamposDesdeSeleccion(newSel);
            }
        });

        // Buscar usuarios
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarUsuarios(newVal));

        // Botones
        btnAgregar.setOnAction(e -> agregarUsuario());
        btnActualizar.setOnAction(e -> actualizarUsuario());
        btnHabilitar.setOnAction(e -> habilitarUsuario());
        btnDeshabilitar.setOnAction(e -> deshabilitarUsuario());
        btnLimpiar.setOnAction(e -> limpiarCampos());

    }

    @Override
    public void setSistema(SistemaAgroCare sistema) {
        this.sistema = sistema;
        cargarUsuarios();
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    private void cargarUsuarios() {
        listaUsuarios.setAll(sistema.getGestorUsuario().obtenerTodos());
        lblTotalUsuarios.setText("Total: " + listaUsuarios.size() + " Usuarios");
    }

    private void llenarCamposDesdeSeleccion(Usuario u) {
        txtNombre.setText(u.getNombre());
        txtApellido.setText(u.getApellido());
        txtEmail.setText(u.getEmail());
        txtContrasena.setText(u.getContraseña());
        cbTipoUsuario.setValue(u.getTipoUsuario());
        txtAreaAsignada.setText(u instanceof Agricultor ? ((Agricultor) u).getAreaAsignada() : "");
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();
        txtContrasena.clear();
        txtAreaAsignada.clear();
        cbTipoUsuario.getSelectionModel().clearSelection();
        usuarioSeleccionado = null;
        txtAreaAsignada.setDisable(true);
        tblUsuarios.getSelectionModel().clearSelection();
    }

    private void buscarUsuarios(String filtro) {
        ObservableList<Usuario> listaFiltrada = FXCollections.observableArrayList();
        for (Usuario u : sistema.getGestorUsuario().obtenerTodos()) {
            if (u.getNombre().toLowerCase().contains(filtro.toLowerCase())
                    || u.getApellido().toLowerCase().contains(filtro.toLowerCase())
                    || u.getEmail().toLowerCase().contains(filtro.toLowerCase())) {
                listaFiltrada.add(u);
            }
        }
        listaUsuarios.setAll(listaFiltrada);
    }

    private void agregarUsuario() {
        Usuario nuevo;
        if ("Empleado".equals(cbTipoUsuario.getValue())) {
            nuevo = new Agricultor(txtNombre.getText(), txtApellido.getText(), txtEmail.getText(),
                    txtContrasena.getText(), txtAreaAsignada.getText());
        } else {
            nuevo = new Administrador(txtNombre.getText(), txtApellido.getText(), txtEmail.getText(),
                    txtContrasena.getText());
        }

        if (sistema.getGestorUsuario().registrarUsuario(nuevo)) {
            listaUsuarios.add(nuevo);
            cargarUsuarios();
            limpiarCampos();

        } else {
            Alerta.mostrarError("Error al registrar el usuario");
        }
    }

    private void actualizarUsuario() {
        if (usuarioSeleccionado == null) {
            return;
        }

        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String email = txtEmail.getText();
        String contrasena = txtContrasena.getText();
        String tipoSeleccionado = cbTipoUsuario.getValue();
        String area = txtAreaAsignada.getText();

        Usuario actualizado;

        // Crear nuevo objeto si el tipo cambió
        if (!usuarioSeleccionado.getTipoUsuario().equals(tipoSeleccionado)) {
            if ("Empleado".equals(tipoSeleccionado)) {
                actualizado = new Agricultor(nombre, apellido, email, contrasena, area);
            } else {
                actualizado = new Administrador(nombre, apellido, email, contrasena);
            }
            actualizado.setId(usuarioSeleccionado.getId());
            actualizado.setActivo(usuarioSeleccionado.isActivo());

            // Reemplazar en el gestor
            sistema.getGestorUsuario().reemplazarUsuario(usuarioSeleccionado, actualizado);
        } else {
            // Mismo tipo, solo actualizar campos
            usuarioSeleccionado.setNombre(nombre);
            usuarioSeleccionado.setApellido(apellido);
            usuarioSeleccionado.setEmail(email);
            usuarioSeleccionado.setContraseña(contrasena);
            if (usuarioSeleccionado instanceof Agricultor) {
                ((Agricultor) usuarioSeleccionado).setAreaAsignada(area);
            }
            sistema.getGestorUsuario().actualizar(usuarioSeleccionado);
        }

        cargarUsuarios();
        tblUsuarios.refresh();
        limpiarCampos();
    }

    private void habilitarUsuario() {
        if (usuarioSeleccionado != null && sistema.getGestorUsuario().activarUsuario(usuarioSeleccionado.getId())) {
            tblUsuarios.refresh();
        }
    }

    private void deshabilitarUsuario() {
        if (usuarioSeleccionado != null && sistema.getGestorUsuario().desactivarUsuario(usuarioSeleccionado.getId())) {
            tblUsuarios.refresh();
        }
    }
}
