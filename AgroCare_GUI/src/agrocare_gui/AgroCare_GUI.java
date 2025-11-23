package agrocare_gui;

import Interfaces.vistas.login.LoginController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import proyectoagrocare.SistemaAgroCare;

public class AgroCare_GUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            SistemaAgroCare sistema = new SistemaAgroCare();
            sistema.inicializar();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Interfaces/vistas/login/login.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y pasar el sistema
            LoginController controller = loader.getController();
            controller.setSistema(sistema);

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("AgroCare");
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
