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
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardClienteController implements Initializable {

    @FXML private Label lblSaudacao;
    @FXML private Label lblPedidosAndamento;
    @FXML private Label lblPedidosPendentes;
    @FXML private Label lblPedidosConcluidos;
    @FXML private Label lblPedidosCancelados;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        carregarDadosUsuario();
        carregarEstatisticas();
    }

    private void carregarDadosUsuario() {
        // Aqui você carrega os dados do usuário logado
        String nomeUsuario = "João Cliente"; // Exemplo
        lblSaudacao.setText("Bem-vindo, " + nomeUsuario + "!");
    }

    private void carregarEstatisticas() {
        // Aqui você carrega as estatísticas do banco de dados
        lblPedidosAndamento.setText("3");
        lblPedidosPendentes.setText("2");
        lblPedidosConcluidos.setText("15");
        lblPedidosCancelados.setText("1");
    }

    @FXML
    private void abrirRelatorios() {
        System.out.println("Abrindo tela de Relatórios...");
        // Navegar para tela de relatórios
        // Main.trocarTela("RelatoriosCliente");
        
        mostrarMensagem("Relatórios", "Abrindo sistema de relatórios...");
    }

    @FXML
    private void abrirVendas() {
        System.out.println("Abrindo tela de Vendas/Mercado...");
        // Navegar para tela de vendas (aquela que criamos com mapa)
        // Main.trocarTela("Vendas");
        
        mostrarMensagem("Mercado", "Abrindo mercado agrícola...");
    }

    @FXML
    private void abrirPerfil() {
        System.out.println("Abrindo tela de Perfil...");
        // Navegar para tela de perfil
        // Main.trocarTela("PerfilCliente");
        
        mostrarMensagem("Perfil", "Abrindo configurações do perfil...");
    }

    @FXML
    private void sairSistema() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Saída");
        alert.setHeaderText("Deseja realmente sair do sistema?");
        alert.setContentText("Você será desconectado do GreenMatch.");
        
        // Estilizar o alerta
        alert.getDialogPane().setStyle("-fx-background-color: rgba(45,45,45,0.95); -fx-border-color: #4CAF50;");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            System.out.println("Saindo do sistema...");
            // Main.trocarTela("Login");
            javafx.application.Platform.exit();
        }
    }

    private void mostrarMensagem(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        
        // Estilizar o alerta
        alert.getDialogPane().setStyle("-fx-background-color: rgba(45,45,45,0.95); -fx-border-color: #4CAF50;");
        alert.showAndWait();
    }
}