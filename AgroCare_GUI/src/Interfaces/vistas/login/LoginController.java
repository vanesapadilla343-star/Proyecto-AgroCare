package Interfaces.vistas.login;

import Interfaces.vistas.home.HomeController;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import proyectoagrocare.Clases.Usuario;
import proyectoagrocare.SistemaAgroCare;

public class LoginController {

    private SistemaAgroCare sistema;

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private Button btnIngresar;
    @FXML
    private Label lblError;

    public void setSistema(SistemaAgroCare sistema) {
        this.sistema = sistema;
    }

    @FXML
    private void ingresar() {
        String email = txtUsuario.getText().trim();
        String password = txtContrasena.getText();

        Usuario usuario = sistema.getGestorUsuario().autenticar(email, password);

        if (usuario != null) {
            try {
                // Cargar el Home.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Interfaces/vistas/home/home.fxml"));
                Parent homeRoot = loader.load();

                // Obtener el controlador de Home y pasar el sistema y usuario si es necesario
                HomeController homeController = loader.getController();
                homeController.setSistema(sistema);
                homeController.setUsuario(usuario);

                Stage stage = (Stage) btnIngresar.getScene().getWindow();
                Scene homeScene = new Scene(homeRoot);
                stage.setScene(homeScene);
                stage.setTitle("AgroCare - Home");
                stage.show();

                javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                stage.setX(screenBounds.getMinX());
                stage.setY(screenBounds.getMinY());
                stage.setWidth(screenBounds.getWidth());
                stage.setHeight(screenBounds.getHeight());
                stage.toFront();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lblError.setText("Usuario o contraseña incorrectos");
            lblError.setVisible(true);
        }
    }

}
