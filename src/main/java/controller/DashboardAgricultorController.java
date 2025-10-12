/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author liliano
 */
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Usuario;
import model.Agricultor;
import service.AgricultorService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.input.KeyCombination;
import service.AutenticacaoService.SessaoUsuario;
import service.SessaoActual;
import service.UsuarioService;

public class DashboardAgricultorController implements Initializable {
    @FXML private Label lblSaudacao;
    @FXML
    private Label lblVendasHoje;
    
    @FXML
    private Label lblVendasMes;
    
    @FXML
    private Label lblProdutosAtivos;
    
    @FXML
    private Label lblAvaliacaoMedia;

    private Agricultor agricultor;
    private AgricultorService agricultorService;
    UsuarioService usuarioService = new UsuarioService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //agricultorService = new AgricultorService();
        carregarDadosAgricultor();
        atualizarMetricas();
    }

    public void setAgricultor(Agricultor agricultor) {
        this.agricultor = agricultor;
        atualizarSaudacao();
    }

    private void carregarDadosAgricultor() {
        Agricultor agricultorLogado = SessaoActual.getAgricultorLogado();

        if (agricultorLogado != null) {
            this.agricultor = agricultorLogado;
            lblSaudacao.setText("Bem-vindo, " + agricultor.getNome() + "!");

        } else {
            lblSaudacao.setText("Bem-vindo, Visitante!");
            // Opcional: redirecionar para login
            // redirecionarParaLogin();
        }
    }
    private void atualizarSaudacao() {
        if (agricultor != null && agricultor.getNome() != null) {
            lblSaudacao.setText("Bem-vindo, " + agricultor.getNome() + "!");
        }
    }

    private void atualizarMetricas() {
        lblVendasHoje.setText("12");
        lblVendasMes.setText("156");
        lblProdutosAtivos.setText("23");
        lblAvaliacaoMedia.setText("4.8");
    }

    @FXML
    private void abrirCadastroProdutos() {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GestaoProdutos.fxml"));
        Parent root = loader.load();
        
       Stage stage = (Stage) lblSaudacao.getScene().getWindow();
        
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        
    } catch (Exception e) {
        e.printStackTrace();
        mostrarErro( "Não foi possível abrir o mercado: " + e.getMessage());
    }
    }

    @FXML
    private void abrirRelatorios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RelatoriosAgricultor.fxml"));
            Parent root = loader.load();
            
            // Passar dados do agricultor para o controller de relatórios
          //  RelatoriosAgricultorController controller = loader.getController();
            //controller.setAgricultor(agricultor);
            
            Stage stage = new Stage();
            stage.setTitle("Relatórios - GreenMatch");
            stage.setScene(new Scene(root));
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.show();
            
        } catch (Exception e) {
            mostrarErro("Erro ao abrir relatórios: " + e.getMessage());
        }
    }

    @FXML
    private void abrirPerfil() {
          try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PerfilAgricultor.fxml"));
        Parent root = loader.load();
        
       Stage stage = (Stage) lblSaudacao.getScene().getWindow();
        
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        
    } catch (Exception e) {
        e.printStackTrace();
        mostrarErro( "Não foi possível abrir o mercado: " + e.getMessage());
    }
    }

    @FXML
    private void sairSistema() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Saída");
        alert.setHeaderText("Deseja realmente sair do sistema?");
        alert.setContentText("Você será redirecionado para a tela de login.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Fechar a tela atual
                    Stage stage = (Stage) lblSaudacao.getScene().getWindow();
                    stage.close();
                    
                    // Abrir tela de login
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
                    Parent root = loader.load();
                    
                    Stage loginStage = new Stage();
                    loginStage.setTitle("Login - GreenMatch");
                    loginStage.setScene(new Scene(root));
                    loginStage.setResizable(false);
                    loginStage.show();
                    
                } catch (Exception e) {
                    mostrarErro("Erro ao sair do sistema: " + e.getMessage());
                }
            }
        });
    }

    public void atualizarDadosAgricultor(Agricultor agricultorAtualizado) {
        this.agricultor = agricultorAtualizado;
        atualizarSaudacao();
        atualizarMetricas();
    }

    public void atualizarMetricasEmTempoReal() {
        // Método para atualizar as métricas quando necessário
        // Pode ser chamado por outros controllers ou serviços
        atualizarMetricas();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // Método para fechar a aplicação completamente
    @FXML
    private void fecharAplicacao() {
        System.exit(0);
    }

    // Método para minimizar a janela
    @FXML
    private void minimizarJanela() {
        Stage stage = (Stage) lblSaudacao.getScene().getWindow();
        stage.setIconified(true);
    }
}