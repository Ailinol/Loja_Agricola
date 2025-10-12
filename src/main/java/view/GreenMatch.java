package view;

import controller.CadastroAgricultorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Random;
import service.UsuarioService;

public class GreenMatch extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
        Parent root = loader.load();
        
        //CadastroAgricultorController controller = loader.getController();
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("GreenMatch - Cadastro Agricultor");
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
