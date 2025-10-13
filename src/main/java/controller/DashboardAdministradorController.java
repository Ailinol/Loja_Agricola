package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import service.AdministradorService;

public class DashboardAdministradorController {

    @FXML
    private Label lblSaudacao;

    // Instância do service
    private AdministradorService adminService;

    public DashboardAdministradorController() {
        // Inicializa o service
        this.adminService = new AdministradorService();
    }

    // ===================== Abertura de telas =====================
    @FXML
    private void abrirGestaoUsuarios(MouseEvent event) {
        abrirTela("/view/GestaoUsuarios.fxml", "Gestão de Usuários");
    }

    @FXML
    private void abrirGestaoProdutos(MouseEvent event) {
        abrirTela("/view/GestaoProdutos.fxml", "Gestão de Produtos");
    }

    @FXML
    private void abrirRelatoriosGlobais(MouseEvent event) {
        abrirTela("/view/Relatorios.fxml", "Relatórios Globais");
    }

    @FXML
    private void abrirMonitorizacao(MouseEvent event) {
        abrirTela("/view/Monitorizacao.fxml", "Monitorização");
    }

    // ===================== Método auxiliar para abrir qualquer tela =====================
    private void abrirTela(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== Botão sair do sistema =====================
    @FXML
   
private void sair() {
    try {
        // Fecha a janela atual
        Stage stageAtual = (Stage) lblSaudacao.getScene().getWindow();
        stageAtual.close();

        // Abre a tela de login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
        Parent root = loader.load();
        Stage stageLogin = new Stage();
        stageLogin.setScene(new Scene(root));
        stageLogin.setTitle("Login");
        stageLogin.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    // ===================== Métodos auxiliares =====================
    // Atualizar saudação dinamicamente
    public void atualizarSaudacao(String nomeAdmin) {
        lblSaudacao.setText("Bem-vindo, " + nomeAdmin + "!");
    }

    // ===================== Exemplos de integração com AdministradorService =====================
    @FXML
    private void listarUsuarios() {
        // Lista todos os usuários cadastrados
        System.out.println(adminService.listarUsuarios());
    }

    @FXML
    private void listarProdutos() {
        // Lista todos os produtos cadastrados
        System.out.println(adminService.listarProdutos());
    }
/*
   @FXML
    //*private void gerarRelatorioClientesAtivos() {
        int total = adminService.totalClientesAtivos();
        System.out.println("Clientes ativos: " + total);
    }

    @FXML
    private void gerarRelatorioAgricultoresProdutivos() {
        System.out.println("Agricultores mais produtivos: " + adminService.agricultoresMaisProdutivos());
    }
*/
}
