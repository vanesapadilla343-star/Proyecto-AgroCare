package Interfaces.vistas.home;

import Interfaces.vistas.ControladorConContexto;
import Interfaces.vistas.login.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import proyectoagrocare.SistemaAgroCare;
import proyectoagrocare.Clases.Usuario;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HomeController implements Initializable {

    private SistemaAgroCare sistema;
    private Usuario usuario;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Label lblUsuario, lblRol;

    @FXML
    private StackPane contenidoPrincipal;

    @FXML
    private Button btnReporte;
    @FXML
    private Button btnCultivo;
    @FXML
    private Button btnRiego;
    @FXML
    private Button btnFertilizacion;
    @FXML
    private Button btnCosecha;
    @FXML
    private Button btnUsuario;

    public void setSistema(SistemaAgroCare sistema) {
        this.sistema = sistema;
    }

    // Setter para recibir el usuario
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (lblUsuario != null) {
            lblUsuario.setText(usuario.getNombre());
            lblRol.setText("(" + usuario.getTipo() + ")");
            this.configurarBotonesSegunUsuario();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Al iniciar, podría mostrar un panel de bienvenida
    }

    // Método genérico para cargar vistas en el panel central
    private void cargarVista(String rutaFXML) {
        try {
            // Cargar la vista como Node genérico
            Node vista = FXMLLoader.load(getClass().getResource(rutaFXML));
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarSesion() {
        try {
            this.sistema.finalizar();
            // Cargar el FXML del login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Interfaces/vistas/login/login.fxml"));
            Parent root = loader.load();

            // Crear un nuevo sistema limpio
            SistemaAgroCare nuevoSistema = new SistemaAgroCare();
            nuevoSistema.inicializar();

            // Pasar el sistema al login
            LoginController loginController = loader.getController();
            loginController.setSistema(nuevoSistema);

            // Cambiar la escena al login
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Login - AgroCare");

            // Limpiar referencias actuales
            this.usuario = null;
            this.sistema = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configurarBotonesSegunUsuario() {
        btnReporte.setVisible(this.usuario.tienePermiso("gestion_reporte"));
        btnCultivo.setVisible(this.usuario.tienePermiso("gestion_cultivo"));
        btnRiego.setVisible(this.usuario.tienePermiso("gestion_riego"));
        btnFertilizacion.setVisible(this.usuario.tienePermiso("gestion_fertilizacion"));
        btnCosecha.setVisible(this.usuario.tienePermiso("gestion_cosecha"));
        btnUsuario.setVisible(this.usuario.tienePermiso("gestion_usuario"));
    }

    private void cargarVistaConContexto(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Node vista = loader.load();

            // Obtener controlador y pasar contexto
            Object controlador = loader.getController();
            if (controlador instanceof ControladorConContexto c) {
                c.setSistema(this.sistema);
                c.setUsuario(this.usuario);
            }

            // Cargar la vista en el StackPane
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Métodos de los botones del sidebar
    @FXML
    private void abrirCultivo() {
        cargarVistaConContexto("/Interfaces/vistas/cultivo/Cultivo.fxml");
    }

    @FXML
    private void abrirRiego() {
        cargarVistaConContexto("/Interfaces/vistas/riego/riego.fxml");
    }

    @FXML
    private void abrirCosecha() {
        cargarVistaConContexto("/Interfaces/vistas/cosecha/cosecha.fxml");
    }

    @FXML
    private void abrirFertilizacion() {
        cargarVistaConContexto("/Interfaces/vistas/fertilizacion/fertilizacion.fxml");
    }

    @FXML
    private void abrirUsuarios() {
        cargarVistaConContexto("/Interfaces/vistas/usuario/usuario.fxml");
    }

    @FXML
    private void abrirReporte() {
        cargarVistaConContexto("/Interfaces/vistas/reporte/reporte.fxml");
    }
}
