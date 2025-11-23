package Interfaces.vistas.fertilizacion;

import Interfaces.vistas.ControladorConContexto;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import proyectoagrocare.Clases.Cultivo;
import proyectoagrocare.Clases.Fertilizacion;
import proyectoagrocare.Clases.Usuario;
import proyectoagrocare.SistemaAgroCare;
import proyectoagrocare.UnidadFertilizante;
import utils.Alerta;

public class FertilizacionController implements ControladorConContexto {

    private SistemaAgroCare sistema;
    private Fertilizacion fertilizacionSeleccionada;
    private Usuario usuario;
    private ObservableList<Fertilizacion> listaFertilizaciones;
    // SE llama observable por "Observa" los cambios que suceden en los items de la lista y los actualiza en "vista" 

    @FXML
    private Label lblTotalFertilizaciones;
    @FXML
    private ComboBox<Cultivo> cbCultivos;
    @FXML
    private TextField txtCantidad, txtTipoFertilizante, txtBuscar;
    @FXML
    private ComboBox<UnidadFertilizante> cbUnidad;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private ComboBox<String> cbProgramado;
    @FXML
    private TableView<Fertilizacion> tblFertilizaciones;
    @FXML
    private TableColumn<Fertilizacion, String> colId, colCultivo, colTipo, colUnidad, colProgramado, colEjecutado;
 
    //COL == COLUMNA 
    @FXML
    private TableColumn<Fertilizacion, LocalDate> colFecha;
    @FXML
    private TableColumn<Fertilizacion, Double> colCantidad;
    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar, btnLimpiar, btnMarcarEjecutado;

    @Override
    public void setSistema(SistemaAgroCare sistema) {
        this.sistema = sistema;
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        configurarBotonesSegunUsuario();
        cargarCultivos();
        cargarTabla();
        cbUnidad.setItems(FXCollections.observableArrayList(UnidadFertilizante.values()));
        cbProgramado.setItems(FXCollections.observableArrayList("Sí", "No"));
    }

    private void configurarBotonesSegunUsuario() {
        btnAgregar.setVisible(usuario.tienePermiso("crear_fertilizacion"));
        btnActualizar.setVisible(usuario.tienePermiso("editar_fertilizacion"));
        btnEliminar.setVisible(usuario.tienePermiso("eliminar_fertilizacion"));
        btnMarcarEjecutado.setVisible(usuario.tienePermiso("ejecutar_fertilizacion"));
    }

    @FXML
    public void initialize() {
        tblFertilizaciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoFertilizante"));
        colUnidad.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUnidad().toString()));
        colProgramado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().isEsProgramado() ? "Sí" : "No"));
        colEjecutado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().isEjecutado() ? "Sí" : "No"));
        colCultivo.setCellValueFactory(data -> {
            Cultivo c = sistema.getGestorCultivo().buscarPorId(data.getValue().getCultivoId());
            return new javafx.beans.property.SimpleStringProperty(c != null ? c.getTipoPlanta() : "-");
        });

        cbCultivos.setConverter(new javafx.util.StringConverter<Cultivo>() {
            @Override
            public String toString(Cultivo c) {
                return c != null ? c.getTipoPlanta() : "";
            }

            @Override
            public Cultivo fromString(String s) {
                for (Cultivo c : cbCultivos.getItems()) {
                    if (c.getTipoPlanta().equals(s)) {
                        return c;
                    }
                }
                return null;
            }
        });

        btnAgregar.setOnAction(e -> agregarFertilizacion());
        btnActualizar.setOnAction(e -> actualizarFertilizacion());
        btnEliminar.setOnAction(e -> eliminarFertilizacion());
        btnLimpiar.setOnAction(e -> limpiarCampos());
        btnMarcarEjecutado.setOnAction(e -> marcarFertilizacionComoEjecutada());

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarFertilizaciones(newVal));

        tblFertilizaciones.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                fertilizacionSeleccionada = newSel;
                llenarCamposDesdeSeleccion(newSel);
            }
        });

        tblFertilizaciones.setRowFactory(tv -> new TableRow<Fertilizacion>() {
            @Override
            protected void updateItem(Fertilizacion f, boolean empty) {
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
        if (listaFertilizaciones == null) {
            listaFertilizaciones = FXCollections.observableArrayList();
            tblFertilizaciones.setItems(listaFertilizaciones);
        }
        listaFertilizaciones.clear();
        listaFertilizaciones.addAll(usuario.getTipo().equalsIgnoreCase("Administrador") ? sistema.getGestorFertilizacion().obtenerTodas() : sistema.getGestorFertilizacion().obtenerPorUsuario(usuario.getId()));
        lblTotalFertilizaciones.setText("Total: " + listaFertilizaciones.size() + " fertilizaciones");
    }

    private boolean validarCamposUI(Fertilizacion fActual) {
        Cultivo cultivo = cbCultivos.getValue();
        String tipo = txtTipoFertilizante.getText();
        String cantidadText = txtCantidad.getText();
        UnidadFertilizante unidad = cbUnidad.getValue();
        LocalDate fecha = dpFecha.getValue();
        String programado = cbProgramado.getValue();

        if (cultivo == null) {
            Alerta.mostrarError("Seleccione un cultivo.");
            return false;
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            Alerta.mostrarError("Ingrese el tipo de fertilizante.");
            return false;
        }
        if (unidad == null) {
            Alerta.mostrarError("Seleccione la unidad.");
            return false;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadText);
            if (cantidad <= 0) {
                Alerta.mostrarError("Cantidad debe ser mayor a 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            Alerta.mostrarError("Cantidad inválida.");
            return false;
        }

        if (fecha == null) {
            Alerta.mostrarError("Seleccione una fecha.");
            return false;
        }
        if (fecha.isBefore(cultivo.getFechaSiembra())) {
            Alerta.mostrarError("Fecha no puede ser anterior a la siembra.");
            return false;
        }

        if (programado == null || programado.trim().isEmpty()) {
            Alerta.mostrarError("Indique si es programado.");
            return false;
        }

        if (fActual != null && fActual.isEjecutado()) {
            Alerta.mostrarAdvertencia("No se puede modificar una fertilización ejecutada.");
            return false;
        }

        return true;
    }

    private void agregarFertilizacion() {
        if (!validarCamposUI(null)) {
            return;
        }
        Fertilizacion f = new Fertilizacion(cbCultivos.getValue().getId(), dpFecha.getValue(), txtTipoFertilizante.getText(), Double.parseDouble(txtCantidad.getText()), cbUnidad.getValue(), cbProgramado.getValue().equals("Sí"), usuario.getId());
        if (sistema.getGestorFertilizacion().registrar(f)) {
            listaFertilizaciones.add(f);
            tblFertilizaciones.refresh();
            lblTotalFertilizaciones.setText("Total: " + listaFertilizaciones.size() + " fertilizaciones");
            limpiarCampos();
        } else {
            Alerta.mostrarError("Error al registrar fertilización");
        }
    }

    private void actualizarFertilizacion() {
        if (fertilizacionSeleccionada == null) {
            Alerta.mostrarAdvertencia("Seleccione una fertilización.");
            return;
        }
        if (!validarCamposUI(fertilizacionSeleccionada)) {
            return;
        }

        fertilizacionSeleccionada.setCultivoId(cbCultivos.getValue().getId());
        fertilizacionSeleccionada.setTipoFertilizante(txtTipoFertilizante.getText());
        fertilizacionSeleccionada.setCantidad(Double.parseDouble(txtCantidad.getText()));
        fertilizacionSeleccionada.setUnidad(cbUnidad.getValue());
        fertilizacionSeleccionada.setFecha(dpFecha.getValue());
        fertilizacionSeleccionada.setEsProgramado(cbProgramado.getValue().equals("Sí"));

        if (sistema.getGestorFertilizacion().actualizar(fertilizacionSeleccionada)) {
            tblFertilizaciones.refresh();
            limpiarCampos();
            Alerta.mostrarInformacion("Fertilización actualizada.");
        } else {
            Alerta.mostrarError("Error al actualizar.");
        }
    }

    private void eliminarFertilizacion() {
        if (fertilizacionSeleccionada == null) {
            Alerta.mostrarAdvertencia("Seleccione una fertilización.");
            return;
        }
        if (Alerta.mostrarConfirmacion("¿Eliminar esta fertilización?")) {
            if (sistema.getGestorFertilizacion().eliminar(fertilizacionSeleccionada.getId())) {
                listaFertilizaciones.remove(fertilizacionSeleccionada);
                lblTotalFertilizaciones.setText("Total: " + listaFertilizaciones.size() + " fertilizaciones");
                limpiarCampos();
                Alerta.mostrarInformacion("Fertilización eliminada.");
            } else {
                Alerta.mostrarError("Error al eliminar.");
            }
        }
    }

    private void marcarFertilizacionComoEjecutada() {
        if (fertilizacionSeleccionada == null) {
            Alerta.mostrarAdvertencia("Seleccione una fertilización.");
            return;
        }

        if (fertilizacionSeleccionada.isEjecutado()) {
            Alerta.mostrarAdvertencia("Ya está ejecutada.");
            return;
        }

        // Validar que la cantidad sea mayor a 0
        if (fertilizacionSeleccionada.getCantidad() <= 0) {
            Alerta.mostrarError("No se puede marcar como ejecutada: la cantidad debe ser mayor a 0.");
            return;
        }

        fertilizacionSeleccionada.setEjecutado(true);
        fertilizacionSeleccionada.setFecha(LocalDate.now());

        if (sistema.getGestorFertilizacion().actualizar(fertilizacionSeleccionada)) {
            tblFertilizaciones.refresh();
            Alerta.mostrarInformacion("Fertilización marcada como ejecutada.");
        } else {
            Alerta.mostrarError("Error al actualizar.");
        }
    }

    private void buscarFertilizaciones(String filtro) {
        ObservableList<Fertilizacion> filtrados = FXCollections.observableArrayList();
        for (Fertilizacion f : sistema.getGestorFertilizacion().obtenerTodas()) {
            Cultivo c = sistema.getGestorCultivo().buscarPorId(f.getCultivoId());
            if (c != null && c.getTipoPlanta().toLowerCase().contains(filtro.toLowerCase())) {
                filtrados.add(f);
            }
        }
        tblFertilizaciones.setItems(filtrados);
        lblTotalFertilizaciones.setText("Total: " + filtrados.size() + " fertilizaciones");
    }

    private void llenarCamposDesdeSeleccion(Fertilizacion f) {
        cbCultivos.getSelectionModel().select(sistema.getGestorCultivo().buscarPorId(f.getCultivoId()));
        txtTipoFertilizante.setText(f.getTipoFertilizante());
        txtCantidad.setText(String.valueOf(f.getCantidad()));
        cbUnidad.setValue(f.getUnidad());
        dpFecha.setValue(f.getFecha());
        cbProgramado.setValue(f.isEsProgramado() ? "Sí" : "No");
    }

    private void limpiarCampos() {
        cbCultivos.getSelectionModel().clearSelection();
        txtTipoFertilizante.clear();
        txtCantidad.clear();
        cbUnidad.getSelectionModel().clearSelection();
        dpFecha.setValue(null);
        cbProgramado.getSelectionModel().clearSelection();
        tblFertilizaciones.getSelectionModel().clearSelection();
        fertilizacionSeleccionada = null;
    }
}
