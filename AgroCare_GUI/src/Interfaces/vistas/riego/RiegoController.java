package Interfaces.vistas.riego;

import Interfaces.vistas.ControladorConContexto;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import proyectoagrocare.Clases.Cultivo;
import proyectoagrocare.Clases.Riego;
import proyectoagrocare.Clases.Usuario;
import proyectoagrocare.SistemaAgroCare;
import utils.Alerta;

public class RiegoController implements ControladorConContexto {

    private SistemaAgroCare sistema;
    private Riego riegoSeleccionado;
    private Usuario usuario;
    private ObservableList<Riego> listaRiegos;

    //campos
    @FXML
    private Label lblTotalRiegos;
    @FXML
    private ComboBox<Cultivo> cbCultivos;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField txtCantidad, txtBuscar;
    @FXML
    private ComboBox<String> cbProgramado;

    // tabla
    @FXML
    private TableView<Riego> tblRiegos;
    @FXML
    private TableColumn<Riego, String> colId, colCultivo, colProgramado, colEjecutado;
    @FXML
    private TableColumn<Riego, LocalDate> colFecha;
    @FXML
    private TableColumn<Riego, Double> colCantidad;

    //botones
    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar, btnLimpiar, btnMarcarEjecutado;

    @Override
    public void setSistema(SistemaAgroCare sistema) {
        this.sistema = sistema;

    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.configurarBotonesSegunUsuario();
        cargarCultivos();
        cargarTabla();
    }

    private void configurarBotonesSegunUsuario() {
        btnAgregar.setVisible(this.usuario.tienePermiso("crear_riego"));
        btnActualizar.setVisible(this.usuario.tienePermiso("editar_riego"));
        btnEliminar.setVisible(this.usuario.tienePermiso("eliminar_riego"));
        btnMarcarEjecutado.setVisible(this.usuario.tienePermiso("ejecutar_riego"));
    }

    @FXML
    public void initialize() {
        tblRiegos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadAgua"));
        colProgramado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().isEsProgramado() ? "Sí" : "No"));
        colEjecutado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().isEjecutado() ? "Sí" : "No"));
        colCultivo.setCellValueFactory(data -> {
            Cultivo c = sistema != null ? sistema.getGestorCultivo().buscarPorId(data.getValue().getCultivoId()) : null;
            return new javafx.beans.property.SimpleStringProperty(c != null ? c.getTipoPlanta() : "-");
        });

        cbCultivos.setConverter(new javafx.util.StringConverter<Cultivo>() {
            @Override
            public String toString(Cultivo cultivo) {
                return cultivo != null ? cultivo.getTipoPlanta() : "";
            }

            @Override
            public Cultivo fromString(String string) {
                for (Cultivo c : cbCultivos.getItems()) {
                    if (c.getTipoPlanta().equals(string)) {
                        return c;
                    }
                }
                return null;
            }
        });

        cbProgramado.setItems(FXCollections.observableArrayList("Sí", "No"));

        btnAgregar.setOnAction(e -> agregarRiego());
        btnActualizar.setOnAction(e -> actualizarRiego());
        btnEliminar.setOnAction(e -> eliminarRiego());
        btnLimpiar.setOnAction(e -> limpiarCampos());
        btnMarcarEjecutado.setOnAction(e -> marcarRiegoComoEjecutado());

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarRiegos(newVal));

        tblRiegos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                riegoSeleccionado = newSel;
                llenarCamposDesdeSeleccion(newSel);
            }
        });

        tblRiegos.setRowFactory(tv -> new TableRow<Riego>() {
            @Override
            protected void updateItem(Riego f, boolean empty) {
                super.updateItem(f, empty);

                if (f == null || empty) {
                    setStyle("");
                } else {
                    LocalDate hoy = LocalDate.now();

                    if (!f.isEjecutado()) {
                        if (f.getFecha().isBefore(hoy)) {
                            // Fecha pasada y pendiente → rojo
                            setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
                        } else if (f.getFecha().isEqual(hoy)) {
                            // Hoy pendiente → amarillo
                            setStyle("-fx-background-color: #ffeb99;");
                        } else {
                            // Fecha futura → sin estilo
                            setStyle("");
                        }
                    } else {
                        // Ejecutadas → sin estilo
                        setStyle("");
                    }
                }
            }
        });

    }

    private void cargarCultivos() {
        if (sistema != null) {
            cbCultivos.getItems().setAll(sistema.getGestorCultivo().obtenerTodos());
        }
    }

    private void cargarTabla() {
        if (sistema == null || usuario == null) {
            return;
        }

        if (listaRiegos == null) {
            listaRiegos = FXCollections.observableArrayList();
            tblRiegos.setItems(listaRiegos);
        }

        listaRiegos.clear();

        if (usuario.getTipo().equalsIgnoreCase("Administrador")) {
            listaRiegos.addAll(sistema.getGestorRiego().obtenerTodos());
        } else {
            listaRiegos.addAll(sistema.getGestorRiego().obtenerPorUsuario(usuario.getId()));
        }

        lblTotalRiegos.setText("Total: " + listaRiegos.size() + " riegos");
    }

    private boolean validarCamposUI(Riego riegoActual) {
        LocalDate hoy = LocalDate.now();
        Cultivo cultivoSeleccionado = cbCultivos.getValue();
        String cantidadTexto = txtCantidad.getText();
        LocalDate fecha = dpFecha.getValue();
        String programado = cbProgramado.getValue();

        // Validar cultivo
        if (cultivoSeleccionado == null) {
            Alerta.mostrarError("Debe seleccionar un cultivo.");
            return false;
        }

        // Validar cantidad
        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadTexto);
            if (cantidad <= 0) {
                Alerta.mostrarError("La cantidad de agua debe ser mayor a 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            Alerta.mostrarError("La cantidad de agua debe ser un número válido.");
            return false;
        }

        // Validar fecha
        if (fecha == null) {
            Alerta.mostrarError("Debe seleccionar una fecha.");
            return false;
        }

        if (fecha.isBefore(cultivoSeleccionado.getFechaSiembra())) {
            Alerta.mostrarError("La fecha no puede ser anterior a la siembra del cultivo.");
            return false;
        }

        // Validar programado
        if (programado == null || programado.trim().isEmpty()) {
            Alerta.mostrarError("Debe indicar si el riego es programado o no.");
            return false;
        }

        // Regls adicionales
        boolean nuevoEsProgramado = programado.equals("Sí");
        if (riegoActual != null) {
            if (riegoActual.isEjecutado()) {
                Alerta.mostrarAdvertencia("No se puede modificar un riego que ya fue ejecutado.");
                return false;
            }
            if (!riegoActual.isEsProgramado() && nuevoEsProgramado) {
                Alerta.mostrarError("No se puede convertir un riego ya registrado a programado.");
                return false;
            }
        }

        return true;
    }

    private void agregarRiego() {
        if (!validarCamposUI(null)) {
            return;
        }

        Riego r = new Riego(
                cbCultivos.getValue().getId(),
                dpFecha.getValue(),
                Double.parseDouble(txtCantidad.getText()),
                cbProgramado.getValue().equals("Sí"),
                this.usuario.getId()
        );

        if (sistema.getGestorRiego().registrar(r)) {
            listaRiegos.add(r);
            tblRiegos.refresh();
            lblTotalRiegos.setText("Total: " + listaRiegos.size() + " riegos");
            limpiarCampos();
            limpiarCampos();
        } else {
            Alerta.mostrarError("Error al registrar el riego");
        }
    }

    private void actualizarRiego() {
        if (riegoSeleccionado == null) {
            Alerta.mostrarAdvertencia("Seleccione un riego.");
            return;
        }

        if (!validarCamposUI(riegoSeleccionado)) {
            return;
        }

        riegoSeleccionado.setCultivoId(cbCultivos.getValue().getId());
        riegoSeleccionado.setFecha(dpFecha.getValue());
        riegoSeleccionado.setCantidadAgua(Double.parseDouble(txtCantidad.getText()));
        riegoSeleccionado.setEsProgramado(cbProgramado.getValue().equals("Sí"));

        if (sistema.getGestorRiego().actualizar(riegoSeleccionado)) {
            tblRiegos.refresh();
            limpiarCampos();
            Alerta.mostrarInformacion("Riego actualizado.");
        } else {
            Alerta.mostrarError("Error al actualizar.");
        }
    }

    private void eliminarRiego() {
        if (riegoSeleccionado == null) {
            Alerta.mostrarAdvertencia("Seleccione un riego.");
            return;
        }

        if (Alerta.mostrarConfirmacion("¿Eliminar este riego?")) {
            if (sistema.getGestorRiego().eliminar(riegoSeleccionado.getId())) {
                listaRiegos.remove(riegoSeleccionado);
                lblTotalRiegos.setText("Total: " + listaRiegos.size() + " riegos");
                limpiarCampos();
                Alerta.mostrarInformacion("Riego eliminado.");
            } else {
                Alerta.mostrarError("Error al eliminar.");
            }
        }
    }

    private void marcarRiegoComoEjecutado() {
        if (riegoSeleccionado == null) {
            Alerta.mostrarAdvertencia("Seleccione un riego.");
            return;
        }

        if (riegoSeleccionado.isEjecutado()) {
            Alerta.mostrarAdvertencia("El riego ya está marcado como ejecutado.");
            return;
        }

        if (riegoSeleccionado.getCantidadAgua() <= 0) {
            Alerta.mostrarError("No se puede marcar como ejecutado: la cantidad de agua es inválida.");
            return;
        }

        riegoSeleccionado.setEjecutado(true);
        riegoSeleccionado.setFecha(LocalDate.now());

        if (sistema.getGestorRiego().actualizar(riegoSeleccionado)) {
            tblRiegos.refresh();
            Alerta.mostrarInformacion("Riego marcado como ejecutado.");
        } else {
            Alerta.mostrarError("Error al actualizar el riego.");
        }
    }

    private void buscarRiegos(String filtro) {
        ObservableList<Riego> filtrados = FXCollections.observableArrayList();
        for (Riego r : sistema.getGestorRiego().obtenerTodos()) {
            Cultivo c = sistema.getGestorCultivo().buscarPorId(r.getCultivoId());
            if (c != null && c.getTipoPlanta().toLowerCase().contains(filtro.toLowerCase())) {
                filtrados.add(r);
            }
        }
        tblRiegos.setItems(filtrados);
        lblTotalRiegos.setText("Total: " + filtrados.size() + " riegos");
    }

    private void llenarCamposDesdeSeleccion(Riego r) {
        cbCultivos.getSelectionModel().select(sistema.getGestorCultivo().buscarPorId(r.getCultivoId()));
        dpFecha.setValue(r.getFecha());
        txtCantidad.setText(String.valueOf(r.getCantidadAgua()));
        cbProgramado.setValue(r.isEsProgramado() ? "Sí" : "No");
    }

    private void limpiarCampos() {
        cbCultivos.getSelectionModel().clearSelection();
        dpFecha.setValue(null);
        txtCantidad.clear();
        cbProgramado.getSelectionModel().clearSelection();
        tblRiegos.getSelectionModel().clearSelection();
        riegoSeleccionado = null;
    }
}
