package Interfaces.vistas.cultivo;

import Interfaces.vistas.ControladorConContexto;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import proyectoagrocare.Clases.Cultivo;
import proyectoagrocare.Clases.Usuario;
import proyectoagrocare.SistemaAgroCare;
import utils.Alerta;

public class CultivoController implements ControladorConContexto {

    private SistemaAgroCare sistema;
    private Usuario usuario;

    @FXML
    private VBox tblContainer;
    @FXML
    private Label lblTotalCultivos;
    @FXML
    private TextField txtBuscar, txtPlanta, txtArea;
    @FXML
    private TextArea txtObservaciones;
    @FXML
    private DatePicker dpFecha;

    // tabla
    @FXML
    private TableView<Cultivo> tblCultivos;
    @FXML
    private TableColumn<Cultivo, String> colId, colPlanta, colObservacion;
    @FXML
    private TableColumn<Cultivo, LocalDate> colFecha;
    @FXML
    private TableColumn<Cultivo, Double> colArea;

    //botones
    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar, btnLimpiar;

    private ObservableList<Cultivo> listaCultivos;// Lista observable para la tabla
    private Cultivo cultivoSeleccionado;   // Cultivo seleccionado actualmente

    @Override
    public void setSistema(SistemaAgroCare sistema) {
        this.sistema = sistema;
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.configurarBotonesSegunUsuario();
        cargarTabla();
    }

    @FXML
    public void initialize() {
        tblCultivos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPlanta.setCellValueFactory(new PropertyValueFactory<>("tipoPlanta"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaSiembra"));
        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        colObservacion.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        // Configurar botones
        btnAgregar.setOnAction(e -> agregarCultivo());
        btnActualizar.setOnAction(e -> actualizarCultivo());
        btnEliminar.setOnAction(e -> eliminarCultivo());
        btnLimpiar.setOnAction(e -> limpiarCampos());

        // Configurar búsqueda
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarCultivo(newVal));

        // Detectar selección en la tabla
        tblCultivos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cultivoSeleccionado = newSel;
                llenarCamposDesdeSeleccion(newSel);
            }
        });
    }

    private void cargarTabla() {
        if (sistema == null || usuario == null) {
            return;
        }

        if (listaCultivos == null) {
            listaCultivos = FXCollections.observableArrayList();
            tblCultivos.setItems(listaCultivos);
        }

        listaCultivos.clear();

        if (usuario.getTipo().equalsIgnoreCase("Administrador")) {
            listaCultivos.addAll(sistema.getGestorCultivo().obtenerTodos());
        } else {
            listaCultivos.addAll(sistema.getGestorCultivo().obtenerPorUsuario(usuario.getId()));
        }

        lblTotalCultivos.setText("Total: " + listaCultivos.size() + " Cultivos");
    }

    private void configurarBotonesSegunUsuario() {
        btnAgregar.setVisible(this.usuario.tienePermiso("crear_cultivo"));
        btnActualizar.setVisible(this.usuario.tienePermiso("editar_cultivo"));
        btnEliminar.setVisible(this.usuario.tienePermiso("eliminar_cultivo"));
    }

    private boolean validarCamposUI() {
        LocalDate fecha = dpFecha.getValue();
        String planta = txtPlanta.getText();
        String areaTexto = txtArea.getText();

        if (planta == null || planta.trim().isEmpty()) {
            Alerta.mostrarError("El tipo de planta no puede estar vacío.");
            return false;
        }

        if (fecha == null || fecha.isAfter(LocalDate.now())) {
            Alerta.mostrarError("La fecha de siembra no puede ser futura ni vacía.");
            return false;
        }

        double area;
        try {
            area = Double.parseDouble(areaTexto);
            if (area <= 0) {
                Alerta.mostrarError("El área debe ser mayor a 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            Alerta.mostrarError("El área debe ser un número válido.");
            return false;
        }

        return true;
    }

    private void agregarCultivo() {
        if (!validarCamposUI()) {
            return;
        }

        Cultivo c = new Cultivo(
                txtPlanta.getText(),
                dpFecha.getValue(),
                Double.parseDouble(txtArea.getText()),
                txtObservaciones.getText(),
                this.usuario.getId()
        );

        if (sistema.getGestorCultivo().registrar(c)) {

            listaCultivos.add(c);
            tblCultivos.refresh();
            lblTotalCultivos.setText("Total: " + listaCultivos.size() + " Cultivos");
            limpiarCampos();

        } else {
            Alerta.mostrarError("Error al registrar el cultivo");
        }
    }

    private void buscarCultivo(String filtro) {
        if (sistema == null || usuario == null) {
            return;
        }

        ObservableList<Cultivo> listaFiltrada = FXCollections.observableArrayList();
        var cultivos = usuario.getTipo().equalsIgnoreCase("Administrador")
                ? sistema.getGestorCultivo().obtenerTodos()
                : sistema.getGestorCultivo().obtenerPorUsuario(usuario.getId());

        for (Cultivo c : cultivos) {
            if (c.getTipoPlanta().toLowerCase().contains(filtro.toLowerCase())
                    || (c.getId() != null && c.getId().contains(filtro))) {
                listaFiltrada.add(c);
            }
        }

        tblCultivos.setItems(listaFiltrada);
        lblTotalCultivos.setText("Total: " + listaFiltrada.size() + " Cultivos");
    }

    private void actualizarCultivo() {
        if (cultivoSeleccionado == null) {
            return;
        }

        boolean tieneCosechas = sistema.getGestorCultivo().tieneCosechas(cultivoSeleccionado.getId());

        cultivoSeleccionado.setTipoPlanta(txtPlanta.getText());
        cultivoSeleccionado.setArea(Double.parseDouble(txtArea.getText()));
        cultivoSeleccionado.setFechaSiembra(dpFecha.getValue());
        cultivoSeleccionado.setObservaciones(txtObservaciones.getText());

        if (!validarCamposUI()) {
            return;
        }

        int idx = listaCultivos.indexOf(cultivoSeleccionado);
        if (idx >= 0) {
            listaCultivos.set(idx, cultivoSeleccionado);
        }

        if (tieneCosechas) {
            Alerta.mostrarAdvertencia("Este cultivo tiene cosechas registradas. NO puede editar la fecha de siembra ni el área");
        } else {
            sistema.getGestorCultivo().actualizar(cultivoSeleccionado);
            limpiarCampos();
            tblCultivos.refresh();
        }

    }

    private void eliminarCultivo() {
        if (cultivoSeleccionado == null) {
            return;
        }

        if (Alerta.mostrarConfirmacion("Al elimianr el cultivo, elimina toda su informción relacionada (riegos, fertilizaciones, cosecha). ¿Desea Eliminarlo?")) {
            if (sistema.getGestorCultivo().eliminar(cultivoSeleccionado.getId())) {
                listaCultivos.remove(cultivoSeleccionado);
                lblTotalCultivos.setText("Total: " + listaCultivos.size() + " Cultivos");
                limpiarCampos();
                Alerta.mostrarInformacion("Cultivo eliminado.");
            } else {
                Alerta.mostrarError("Error al eliminar.");
            }

            listaCultivos.remove(cultivoSeleccionado);
            limpiarCampos();
        }

    }

    private void llenarCamposDesdeSeleccion(Cultivo cultivo) {
        txtPlanta.setText(cultivo.getTipoPlanta());
        txtArea.setText(String.valueOf(cultivo.getArea()));
        dpFecha.setValue(cultivo.getFechaSiembra());
        txtObservaciones.setText(cultivo.getObservaciones());
    }

    private void limpiarCampos() {
        txtPlanta.clear();
        txtArea.clear();
        dpFecha.setValue(null);
        txtObservaciones.clear();
        tblCultivos.getSelectionModel().clearSelection();
        cultivoSeleccionado = null;
    }
}
