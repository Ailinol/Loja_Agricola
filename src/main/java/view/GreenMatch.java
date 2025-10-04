package view;

import controller.CadastroAgricultorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class GreenMatch extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        // 🔥 CARREGAR O FXML E SEU CONTROLLER
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CadastroAgricultor.fxml"));
        Parent root = loader.load();
        
        // 🔥 OPcional: Acessar o controller se precisar
        CadastroAgricultorController controller = loader.getController();
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("GreenMatch - Cadastro Agricultor");
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}