package Interfaces.vistas.cosecha;

import Interfaces.vistas.ControladorConContexto;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import proyectoagrocare.Clases.Cosecha;
import proyectoagrocare.Clases.Cultivo;
import proyectoagrocare.Clases.Usuario;
import proyectoagrocare.SistemaAgroCare;
import utils.Alerta;

public class CosechaController implements ControladorConContexto {

    private SistemaAgroCare sistema;
    private Usuario usuario;

    //campos
    @FXML
    private VBox tblContainer;
    @FXML
    private Label lblTotalCosechas;
    @FXML
    private TextField txtBuscar, txtCantidad;
    @FXML
    private ComboBox<Cultivo> cbCultivos;
    @FXML
    private DatePicker dpFecha;

    //tabla
    @FXML
    private TableView<Cosecha> tblCosechas;
    @FXML
    private TableColumn<Cosecha, String> colId, colCultivo;
    @FXML
    private TableColumn<Cosecha, LocalDate> colFecha;
    @FXML
    private TableColumn<Cosecha, Double> colCantidad, colRendimiento;

    //botones
    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar, btnLimpiar;

    //lista observable y cosecha seleccionada
    private ObservableList<Cosecha> listaCosechas;
    private Cosecha cosechaSeleccionada;

    @Override
    public void setSistema(SistemaAgroCare sistema) {
        this.sistema = sistema;
        cargarCultivos();
        cargarTabla();
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        configurarBotonesSegunUsuario();
        cargarTabla();
    }

    @FXML
    public void initialize() {
        tblCosechas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCultivo.setCellValueFactory(data -> {
            Cultivo c = sistema.getGestorCultivo().buscarPorId(data.getValue().getCultivoId());
            return new javafx.beans.property.SimpleStringProperty(c != null ? c.getTipoPlanta() : "-");
        });
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colRendimiento.setCellValueFactory(new PropertyValueFactory<>("rendimiento"));

        btnAgregar.setOnAction(e -> agregarCosecha());
        btnActualizar.setOnAction(e -> actualizarCosecha());
        btnEliminar.setOnAction(e -> eliminarCosecha());
        btnLimpiar.setOnAction(e -> limpiarCampos());

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarCosecha(newVal));

        tblCosechas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cosechaSeleccionada = newSel;
                llenarCamposDesdeSeleccion(newSel);
            }
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

    }

    private void configurarBotonesSegunUsuario() {
        btnAgregar.setVisible(usuario.tienePermiso("crear_cosecha"));
        btnActualizar.setVisible(usuario.tienePermiso("editar_cosecha"));
        btnEliminar.setVisible(usuario.tienePermiso("eliminar_cosecha"));
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
        if (listaCosechas == null) {
            listaCosechas = FXCollections.observableArrayList();
            tblCosechas.setItems(listaCosechas);
        }

        listaCosechas.clear();

        if (usuario.getTipo().equalsIgnoreCase("Administrador")) {
            listaCosechas.addAll(sistema.getGestorCosecha().obtenerTodas());
        } else {
            listaCosechas.addAll(sistema.getGestorCosecha().obtenerPorUsuario(usuario.getId()));
        }

        lblTotalCosechas.setText("Total: " + listaCosechas.size() + " Cosechas");
    }

    private boolean validarCamposUI() {
        LocalDate hoy = LocalDate.now();
        Cultivo cultivoSeleccionado = cbCultivos.getValue();
        String cantidadTexto = txtCantidad.getText();
        LocalDate fecha = dpFecha.getValue();

        if (cultivoSeleccionado == null) {
            Alerta.mostrarError("Debe seleccionar un cultivo.");
            return false;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadTexto);
            if (cantidad <= 0) {
                Alerta.mostrarError("La cantidad debe ser mayor a 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            Alerta.mostrarError("La cantidad debe ser un número válido.");
            return false;
        }

        if (fecha == null) {
            Alerta.mostrarError("Debe seleccionar una fecha.");
            return false;
        }
        if (fecha.isAfter(hoy)) {
            Alerta.mostrarError("La fecha no puede ser futura.");
            return false;
        }
        if (fecha.isBefore(cultivoSeleccionado.getFechaSiembra())) {
            Alerta.mostrarError("La fecha no puede ser anterior a la siembra del cultivo.");
            return false;
        }

        return true;
    }

    private void agregarCosecha() {
        if (!validarCamposUI()) {
            return;
        }
        Cosecha c = new Cosecha(cbCultivos.getValue().getId(), dpFecha.getValue(), Double.parseDouble(txtCantidad.getText()), usuario.getId());

        if (sistema.getGestorCosecha().registrar(c)) {
            listaCosechas.add(c);
            tblCosechas.refresh();
            lblTotalCosechas.setText("Total: " + listaCosechas.size() + " Cosechas");
            limpiarCampos();
        } else {
            Alerta.mostrarError("Error al registrar la cosecha");
        }
    }

    private void actualizarCosecha() {
        if (cosechaSeleccionada == null) {
            Alerta.mostrarError("Debe seleccionar una cosecha para actualizar.");
            return;
        }

        if (!validarCamposUI()) {
            return;
        }

        cosechaSeleccionada.setCultivoId(cbCultivos.getValue().getId());
        cosechaSeleccionada.setCantidad(Double.parseDouble(txtCantidad.getText()));
        cosechaSeleccionada.setFecha(dpFecha.getValue());

        if (sistema.getGestorCosecha().actualizar(cosechaSeleccionada)) {
            tblCosechas.refresh();
            limpiarCampos();
        } else {
            Alerta.mostrarError("Error al actualizar la cosecha");
        }
    }

    private void eliminarCosecha() {
        if (cosechaSeleccionada == null) {
            Alerta.mostrarAdvertencia("Seleccione una cosecha.");
            return;
        }

        if (Alerta.mostrarConfirmacion("¿Eliminar esta cosecha?")) {
            if (sistema.getGestorCosecha().eliminar(cosechaSeleccionada.getId())) {
                listaCosechas.remove(cosechaSeleccionada);
                lblTotalCosechas.setText("Total: " + listaCosechas.size() + " Cosechas");
                limpiarCampos();
                Alerta.mostrarInformacion("Cosecha eliminada.");
            } else {
                Alerta.mostrarError("Error al eliminar.");
            }
        }
    }

    private void llenarCamposDesdeSeleccion(Cosecha c) {
        cbCultivos.getSelectionModel().select(sistema.getGestorCultivo().buscarPorId(c.getCultivoId()));
        dpFecha.setValue(c.getFecha());
        txtCantidad.setText(String.valueOf(c.getCantidad()));
    }

    private void limpiarCampos() {
        cbCultivos.getSelectionModel().clearSelection();
        dpFecha.setValue(null);
        txtCantidad.clear();
        tblCosechas.getSelectionModel().clearSelection();
        cosechaSeleccionada = null;
    }

    private void buscarCosecha(String filtro) {
        ObservableList<Cosecha> listaFiltrada = FXCollections.observableArrayList();
        var todas = usuario.getTipo().equalsIgnoreCase("Administrador")
                ? sistema.getGestorCosecha().obtenerTodas()
                : sistema.getGestorCosecha().obtenerPorUsuario(usuario.getId());

        for (Cosecha c : todas) {
            Cultivo cultivo = sistema.getGestorCultivo().buscarPorId(c.getCultivoId());
            if ((cultivo != null && cultivo.getTipoPlanta().toLowerCase().contains(filtro.toLowerCase()))
                    || c.getId().contains(filtro)) {
                listaFiltrada.add(c);
            }
        }

        tblCosechas.setItems(listaFiltrada);
        lblTotalCosechas.setText("Total: " + listaFiltrada.size() + " Cosechas");
    }
}
