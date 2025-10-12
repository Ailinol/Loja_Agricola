/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author liliano
 */
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.AutenticacaoService;
import model.Pessoa;
import service.SessaoActual;

public class LoginController implements Initializable {

    @FXML private ComboBox<String> comboTipoUsuario;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private CheckBox checkLembrar;
    @FXML private Hyperlink linkEsqueciSenha;
    @FXML private Hyperlink linkCadastrarAgricultor;
    @FXML private Hyperlink linkCadastrarComprador;
    @FXML private Button btnLogin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> tiposUsuario = FXCollections.observableArrayList(
            "Agricultor",
            "Comprador"
        );
        comboTipoUsuario.setItems(tiposUsuario);
        
        // DEBUG: Verificar conexão e usuários
        try {
            service.UsuarioService usuarioService = new service.UsuarioService();
            usuarioService.verificarConexao();
            usuarioService.debugCompleto();
        } catch (Exception e) {
            System.err.println("Erro no debug: " + e.getMessage());
        }
        
        System.out.println("Tela de Login inicializada");
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String tipoUsuario = comboTipoUsuario.getValue();
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText();

        //Verificar o que está sendo enviado
        System.out.println("=== TENTATIVA DE LOGIN ===");
        System.out.println("Email: " + email);
        System.out.println("Tipo Usuario: " + tipoUsuario);
        
        // Validações básicas...
        if (tipoUsuario == null || tipoUsuario.isEmpty()) {
            mostrarAlerta("Erro", "Selecione o tipo de usuário");
            return;
        }
        
        if (email.isEmpty() || senha.isEmpty()) {
            mostrarAlerta("Erro", "Preencha email e senha");
            return;
        }

        // A senha é comparada via hash
        AutenticacaoService authService = new AutenticacaoService();
        AutenticacaoService.ResultadoLogin resultado = authService.autenticarUsuario(email, senha, tipoUsuario);

        if (resultado.sucesso) {
            System.out.println("Login bem-sucedido para: " + resultado.usuario.getNome());
            mostrarAlerta("Sucesso", "Login realizado com sucesso!");

            // Redirecionar para a tela principal
            SessaoActual.setUsuarioLogado(resultado.usuario, resultado.tokenSessao);
            redirecionarParaTelaPrincipal(resultado.usuario, tipoUsuario);

        } else {
            System.out.println("Falha no login: " + resultado.mensagem);
            mostrarAlerta("Erro", resultado.mensagem);
        }
    }
    
    private void redirecionarParaTelaPrincipal(Pessoa usuario, String tipoUsuario) {
    try {
        String fxmlFile = "";
        String titulo = "";

        switch (tipoUsuario.toUpperCase()) {
            case "AGRICULTOR":
                fxmlFile = "/view/Dashboard_Agricultor.fxml"; 
                titulo = "Dashboard do Agricultor - GreenMatch";
                break;
            case "COMPRADOR":
                fxmlFile = "/view/Dashboard_Cliente.fxml";
                titulo = "Dashboard do Comprador - GreenMatch";
                break;
            default:
                fxmlFile = "/view/Dashboard.fxml";
                titulo = "Dashboard - GreenMatch";
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        Stage stageAtual = (Stage) btnLogin.getScene().getWindow();
        Scene novaCena = new Scene(root);
        stageAtual.setScene(novaCena);
        stageAtual.setTitle(titulo);

        stageAtual.setFullScreen(true);

        stageAtual.show();

    } catch (Exception e) {
        e.printStackTrace();
        mostrarAlerta("Erro", "Não foi possível carregar a tela principal: " + e.getMessage());
    }
}

    
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    @FXML
    private void handleEsqueciSenha(ActionEvent event) {
        System.out.println("Link Esqueci Senha clicado");
        mostrarAlerta("Recuperação de Senha", "Funcionalidade em desenvolvimento");
    }
    
    @FXML
    private void handleCadastrarAgricultor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CadastroAgricultor.fxml"));
            Parent root = loader.load();
      
            Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
     
            Scene novaCena = new Scene(root);
            stageAtual.setScene(novaCena);
            
            stageAtual.sizeToScene();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível carregar a tela");
            alert.setContentText("Erro: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleCadastrarComprador(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CadastroCliente.fxml"));
            Parent root = loader.load();
      
            Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
     
            Scene novaCena = new Scene(root);
            stageAtual.setScene(novaCena);
            
            stageAtual.sizeToScene();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível carregar a tela");
            alert.setContentText("Erro: " + e.getMessage());
            alert.showAndWait();
        }
    }
}