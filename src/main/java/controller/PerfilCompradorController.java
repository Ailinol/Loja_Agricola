package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import model.Comprador;
import service.SessaoActual;
import service.UsuarioService;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PerfilCompradorController implements Initializable {

    @FXML private Label lblNomeUsuario;
    @FXML private Label lblEmail;
    @FXML private Label lblNome;
    @FXML private Label lblEmailInfo;
    @FXML private Label lblTelefone;
    @FXML private Label lblLocalizacao;
    @FXML private Label lblDataCadastro;
    @FXML private Label lblStatus;
    @FXML private Label lblRaioBusca;
    @FXML private Label lblNewsletter;
    @FXML private Label lblPreferencias;
    
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVoltar;
    @FXML private Button btnSair;

    private UsuarioService usuarioService;
    private Comprador compradorLogado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioService = new UsuarioService();
        carregarDadosUsuario();
    }

    private void carregarDadosUsuario() {
        compradorLogado = SessaoActual.getCompradorLogado();
        
        if (compradorLogado != null) {
            lblNomeUsuario.setText(compradorLogado.getNome());
            lblEmail.setText(compradorLogado.getEmail());
            lblNome.setText(compradorLogado.getNome());
            lblEmailInfo.setText(compradorLogado.getEmail());
            lblTelefone.setText(compradorLogado.getTelefone() != null ? compradorLogado.getTelefone() : "Não informado");
            
            String localizacao = compradorLogado.getProvincia() + ", " + 
                               compradorLogado.getDistrito() + ", " + 
                               compradorLogado.getBairro();
            lblLocalizacao.setText(localizacao);
            
            if (compradorLogado.getDataCadastro() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                lblDataCadastro.setText(compradorLogado.getDataCadastro().format(formatter));
            } else {
                lblDataCadastro.setText("Não disponível");
            }
            
            lblStatus.setText(compradorLogado.isAtivo() ? "Ativo" : "Inativo");
            lblStatus.setStyle(compradorLogado.isAtivo() ? 
                "-fx-text-fill: #4CAF50; -fx-font-weight: bold;" : 
                "-fx-text-fill: #F44336; -fx-font-weight: bold;");
            
            lblRaioBusca.setText(compradorLogado.getRaioBuscaPreferido() + " km");
            lblNewsletter.setText(compradorLogado.isRecebeNewsletter() ? "Sim" : "Não");
            
            List<String> preferencias = compradorLogado.getPreferenciasCategorias();
            if (preferencias != null && !preferencias.isEmpty()) {
                lblPreferencias.setText(String.join(", ", preferencias));
            } else {
                lblPreferencias.setText("Nenhuma preferência definida");
            }
        } else {
            mostrarAlerta("Erro", "Nenhum comprador logado encontrado!");
        }
    }

    @FXML
    private void handleEditarDados() {
        try {
            mostrarAlerta("Funcionalidade", "Abrindo tela de edição com dados preenchidos...");
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao abrir edição: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminarConta() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmação de Eliminação");
        confirmacao.setHeaderText("Eliminar Conta");
        confirmacao.setContentText("Tem certeza que deseja eliminar sua conta? Esta ação não pode ser desfeita!");
        
        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean sucesso = usuarioService.removerUsuario(compradorLogado.getEmail());
                if (sucesso) {
                    mostrarAlerta("Sucesso", "Conta eliminada com sucesso!");
                    SessaoActual.limparSessao();
                    handleSair();
                } else {
                    mostrarAlerta("Erro", "Erro ao eliminar conta!");
                }
            } catch (Exception e) {
                mostrarAlerta("Erro", "Erro ao eliminar conta: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        try {
            if (SessaoActual.getCompradorLogado() == null) {
                mostrarAlerta("Erro", "Sessão perdida. Faça login novamente.");
                voltarParaLogin(event);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GestaoProdutos.fxml"));
            Parent root = loader.load();
      
            Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
     
            Scene novaCena = new Scene(root);
            stageAtual.setScene(novaCena);
            stageAtual.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível carregar a tela");
            alert.setContentText("Erro: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void voltarParaLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSair() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmação de Saída");
        confirmacao.setHeaderText("Sair do Sistema");
        confirmacao.setContentText("Tem certeza que deseja sair?");
        
        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            SessaoActual.limparSessao();
            javafx.application.Platform.exit();
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}