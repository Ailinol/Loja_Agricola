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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import model.Agricultor;
import service.SessaoActual;
import service.UsuarioService;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PerfilAgricultorController implements Initializable {

    @FXML private Label lblNomeUsuario;
    @FXML private Label lblEmail;
    @FXML private Label lblNome;
    @FXML private Label lblEmailInfo;
    @FXML private Label lblTelefone;
    @FXML private Label lblWhatsapp;
    @FXML private Label lblLocalizacao;
    @FXML private Label lblDataCadastro;
    @FXML private Label lblStatus;
    @FXML private Label lblDisponivelContato;
    
    @FXML private Label lblTipoAgricultura;
    @FXML private Label lblAnosExperiencia;
    @FXML private Label lblTamanhoPropriedade;
    @FXML private Label lblCertificadoOrganico;
    @FXML private Label lblAceitaVisitas;
    @FXML private Label lblHorarioFuncionamento;
    
    @FXML private Label lblOfereceEntrega;
    @FXML private Label lblRaioEntrega;
    @FXML private Label lblCustoEntrega;
    @FXML private Label lblAceitaEncomendas;
    @FXML private Label lblPrazoEncomenda;
    @FXML private Label lblBiografia;
    
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVoltar;
    @FXML private Button btnSair;

    private UsuarioService usuarioService;
    private Agricultor agricultorLogado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioService = new UsuarioService();
        carregarDadosUsuario();
    }

    private void carregarDadosUsuario() {
        agricultorLogado = SessaoActual.getAgricultorLogado();
        
        if (agricultorLogado != null) {
            lblNomeUsuario.setText(agricultorLogado.getNome());
            lblEmail.setText(agricultorLogado.getEmail());
            lblNome.setText(agricultorLogado.getNome());
            lblEmailInfo.setText(agricultorLogado.getEmail());
            lblTelefone.setText(agricultorLogado.getTelefone() != null ? agricultorLogado.getTelefone() : "Não informado");
            lblWhatsapp.setText(agricultorLogado.getWhatsapp() != null ? agricultorLogado.getWhatsapp() : "Não informado");
            
            String localizacao = agricultorLogado.getProvincia() + ", " + 
                               agricultorLogado.getDistrito() + ", " + 
                               agricultorLogado.getBairro();
            lblLocalizacao.setText(localizacao);
            
            if (agricultorLogado.getDataCadastro() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                lblDataCadastro.setText(agricultorLogado.getDataCadastro().format(formatter));
            } else {
                lblDataCadastro.setText("Não disponível");
            }
            
            // Status
            lblStatus.setText(agricultorLogado.isAtivo() ? "Ativo" : "Inativo");
            lblStatus.setStyle(agricultorLogado.isAtivo() ? 
                "-fx-text-fill: #4CAF50; -fx-font-weight: bold;" : 
                "-fx-text-fill: #F44336; -fx-font-weight: bold;");
            
            
            lblDisponivelContato.setText(agricultorLogado.isDisponivelParaContato() ? "Sim" : "Não");
            
            lblTipoAgricultura.setText(agricultorLogado.getTipoAgricultura() != null ? 
                agricultorLogado.getTipoAgricultura() : "Não informado");
            
            lblAnosExperiencia.setText(agricultorLogado.getAnosExperiencia() + " anos");
            lblTamanhoPropriedade.setText(String.format("%.2f hectares", agricultorLogado.getTamanhoPropriedade()));
            
            lblCertificadoOrganico.setText(agricultorLogado.isCertificadoOrganico() ? "Sim" : "Não");
            lblAceitaVisitas.setText(agricultorLogado.isAceitaVisitas() ? "Sim" : "Não");
            
            // Horário de funcionamento
            if (agricultorLogado.getHorarioFuncionamento() != null && 
                !agricultorLogado.getHorarioFuncionamento().isEmpty()) {
                lblHorarioFuncionamento.setText(agricultorLogado.getHorarioFuncionamento());
            } else {
                lblHorarioFuncionamento.setText("Não definido");
            }
            
            lblOfereceEntrega.setText(agricultorLogado.isOfereceEntrega() ? "Sim" : "Não");
            
            if (agricultorLogado.isOfereceEntrega()) {
                lblRaioEntrega.setText(String.format("%.2f km", agricultorLogado.getRaioEntrega()));
                lblCustoEntrega.setText(String.format("%.2f MZN", agricultorLogado.getCustoEntrega()));
            } else {
                lblRaioEntrega.setText("Não oferece");
                lblCustoEntrega.setText("Não oferece");
            }
            
            lblAceitaEncomendas.setText(agricultorLogado.isAceitaEncomendas() ? "Sim" : "Não");
            
            if (agricultorLogado.isAceitaEncomendas()) {
                lblPrazoEncomenda.setText(agricultorLogado.getPrazoMinimoEncomenda() + " dias");
            } else {
                lblPrazoEncomenda.setText("Não aceita");
            }
            
            // Biografia
            if (agricultorLogado.getBiografia() != null && !agricultorLogado.getBiografia().isEmpty()) {
                lblBiografia.setText(agricultorLogado.getBiografia());
            } else {
                lblBiografia.setText("Nenhuma biografia fornecida");
            }
            
        } else {
            mostrarAlerta("Erro", "Nenhum agricultor logado encontrado!");
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
                boolean sucesso = usuarioService.removerUsuario(agricultorLogado.getEmail());
                if (sucesso) {
                    mostrarAlerta("Sucesso", "Conta eliminada com sucesso!");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard_Agricultor.fxml"));
        Parent root = loader.load();
  
        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
 
        Scene novaCena = new Scene(root);
        stageAtual.setScene(novaCena);
        
        stageAtual.sizeToScene();

        } catch (IOException e) {
            e.printStackTrace();
            // Mostra mensagem de erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível carregar a tela");
            alert.setContentText("Erro: " + e.getMessage());
            alert.showAndWait();
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
            mostrarAlerta("Saída", "Saindo do sistema...");
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
