package Interfaces.vistas.reporte;

import Interfaces.vistas.ControladorConContexto;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import proyectoagrocare.Clases.Reporte;
import proyectoagrocare.Clases.Usuario;
import proyectoagrocare.SistemaAgroCare;
import proyectoagrocare.Gestor.GestorReporte;
import proyectoagrocare.FormateadorDatos;
import utils.Alerta;

public class ReporteController implements ControladorConContexto {

    private SistemaAgroCare sistema;
    private Usuario usuario;

    @FXML
    private DatePicker dpFechaInicio, dpFechaFin;
    @FXML
    private Button btnGenerar, btnGuardarReporte;
    @FXML
    private TextArea taReporteGenerado;
    @FXML
    private TextField txtBuscarReporte;
    @FXML
    private TextField txtTemporada;
    @FXML
    private TableView<Reporte> tblReportes;
    @FXML
    private TableColumn<Reporte, String> colId, colTemporada, colFechaInicio, colFechaFin, colAcciones;

    private ObservableList<Reporte> listaReportes;
    private Reporte reporteGenerado; // Reporte que se genera pero aún no se guarda
    private GestorReporte gestorReporte;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void setSistema(SistemaAgroCare sistema) {
        this.sistema = sistema;
        this.gestorReporte = sistema.getGestorReporte();
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarTablaReportes();
    }

    @FXML
    public void initialize() {
        // Configuración tabla
        listaReportes = FXCollections.observableArrayList();
        tblReportes.setItems(listaReportes);
        tblReportes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setCellValueFactory(new PropertyValueFactory<>("cultivoId"));
        colTemporada.setCellValueFactory(new PropertyValueFactory<>("nombreTemporada"));
        colFechaInicio.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                cellData.getValue().getFechaInicio().format(formatter)
        ));
        colFechaFin.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                cellData.getValue().getFechaFin().format(formatter)
        ));

        // Columna de acciones
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnVer = new Button("Ver");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(5, btnVer, btnEliminar);

            {
                btnVer.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                btnEliminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

                btnVer.setOnAction(e -> {
                    Reporte rep = getTableView().getItems().get(getIndex());
                    taReporteGenerado.setText(FormateadorDatos.formatearReporte(rep));
                    btnGuardarReporte.setDisable(true); // ya está guardado
                });

                btnEliminar.setOnAction(e -> {
                    Reporte rep = getTableView().getItems().get(getIndex());
                    if (Alerta.mostrarConfirmacion("¿Eliminar este reporte?")) {
                        listaReportes.remove(rep);
                        gestorReporte.obtenerTodos().remove(rep);
                        Alerta.mostrarInformacion("Reporte eliminado.");
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });

        // Configuración botones
        btnGenerar.setOnAction(e -> generarReporte());
        btnGuardarReporte.setOnAction(e -> guardarReporte());

        // Búsqueda
        txtBuscarReporte.textProperty().addListener((obs, oldVal, newVal) -> filtrarTabla(newVal));
    }

    private void generarReporte() {
        LocalDate inicio = dpFechaInicio.getValue();
        LocalDate fin = dpFechaFin.getValue();
        String nombreTemporada = txtTemporada.getText().trim();
        if (nombreTemporada.isEmpty()) {
            Alerta.mostrarError("Debes ingresar un nombre para la temporada.");
            return;
        }
        if (inicio == null || fin == null) {
            Alerta.mostrarError("Selecciona ambas fechas.");
            return;
        }
        if (inicio.isAfter(fin)) {
            Alerta.mostrarError("La fecha de inicio no puede ser mayor a la fecha fin.");
            return;
        }

        var reportesGenerados = gestorReporte.generarReportePorTemporada(usuario, nombreTemporada, inicio, fin);

        if (reportesGenerados.isEmpty()) {
            Alerta.mostrarInformacion("No hay datos para generar el reporte en este rango.");
            taReporteGenerado.clear();
            btnGuardarReporte.setDisable(true);
            return;
        }

        // Para simplificar, tomamos el primer reporte generado (podría adaptarse si hay varios)
        reporteGenerado = reportesGenerados.get(0);
        taReporteGenerado.setText(FormateadorDatos.formatearReporte(reporteGenerado));
        btnGuardarReporte.setDisable(false);
    }

    private void guardarReporte() {
        if (reporteGenerado == null) {
            return;
        }

        gestorReporte.guardarReporte(reporteGenerado); // Guarda correctamente y persiste
        listaReportes.add(reporteGenerado); // Actualiza la tabla
        Alerta.mostrarInformacion("Reporte guardado exitosamente.");

        btnGuardarReporte.setDisable(true);
        taReporteGenerado.clear();
        reporteGenerado = null;
    }

    private void cargarTablaReportes() {
        listaReportes.clear();
        if (usuario.getTipo().equalsIgnoreCase("Administrador")) {
            listaReportes.addAll(gestorReporte.obtenerTodos());
        } else {
            listaReportes.addAll(gestorReporte.obtenerReportesPorUsuario(usuario.getId()));
        }
    }

    private void filtrarTabla(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            tblReportes.setItems(listaReportes);
            return;
        }

        ObservableList<Reporte> listaFiltrada = FXCollections.observableArrayList();
        for (Reporte r : listaReportes) {
            if (r.getNombreTemporada().toLowerCase().contains(filtro.toLowerCase())) {
                listaFiltrada.add(r);
            }
        }
        tblReportes.setItems(listaFiltrada);
    }
}
