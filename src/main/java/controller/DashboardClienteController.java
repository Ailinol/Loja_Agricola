/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author liliano
 */
import java.lang.ModuleLayer.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javax.persistence.TypedQuery;
import model.Comprador;
import model.Produto;
import service.UsuarioService;

public class DashboardClienteController implements Initializable {

    @FXML private Label lblSaudacao;
    @FXML private Label lblPedidosAndamento;
    @FXML private Label lblPedidosPendentes;
    @FXML private Label lblPedidosConcluidos;
    @FXML private Label lblPedidosCancelados;
    
    Produto produto = new Produto();
    UsuarioService usuarioService = new UsuarioService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        carregarDadosUsuario();
        carregarEstatisticas();
    }

    private void carregarDadosUsuario() {
    String nomeUsuario = "Visitante";
    
    try {
        // Tentativa 1: Buscar por ID específico
        Object resultado = usuarioService.buscarUsuarioPorId(5);
        
        if (resultado instanceof Comprador) {
            // Se retorna Comprador direto
            Comprador comprador = (Comprador) resultado;
            nomeUsuario = comprador.getNome();
            System.out.println("✅ Comprador direto: " + nomeUsuario);
            
        } else if (resultado instanceof List) {
            // Se retorna List<Comprador>
            List<?> lista = (List<?>) resultado;
            if (!lista.isEmpty() && lista.get(0) instanceof Comprador) {
                Comprador comprador = (Comprador) lista.get(0);
                nomeUsuario = comprador.getNome();
                System.out.println("✅ Comprador da lista: " + nomeUsuario);
            }
        } else {
            System.out.println("⚠️ Tipo de retorno não reconhecido: " + 
                (resultado != null ? resultado.getClass().getSimpleName() : "null"));
        }
        
    } catch (Exception e) {
        System.err.println("❌ Erro ao carregar usuário: " + e.getMessage());
        
        // Fallback: buscar qualquer comprador
        try {
            List<Comprador> compradores = usuarioService.listarCompradores();
            if (compradores != null && !compradores.isEmpty()) {
                Comprador comprador = compradores.get(0);
                nomeUsuario = comprador.getNome();
                System.out.println("✅ Primeiro comprador do banco: " + nomeUsuario);
            }
        } catch (Exception ex) {
            System.err.println("❌ Erro no fallback: " + ex.getMessage());
        }
    }
    
    lblSaudacao.setText("Bem-vindo, " + nomeUsuario + "!");
}

    private void carregarEstatisticas() {
        lblPedidosAndamento.setText("3");
        lblPedidosPendentes.setText("2");
        lblPedidosConcluidos.setText("15");
        lblPedidosCancelados.setText("1");
    }

    @FXML
    private void abrirRelatorios() {
         try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/carrinho.fxml"));
        Parent root = loader.load();
        
        CadastroProdutoController controller = loader.getController();
        controller.setProdutoParaEdicao(produto);
        
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Editar Produto");
        stage.setScene(scene);
        stage.show();
        
    } catch (Exception e) {
        mostrarMensagem("Erro", "Não foi possível abrir a edição: " + e.getMessage());
    }
    }

   @FXML
private void abrirVendas() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Vendas.fxml"));
        Parent root = loader.load();
        
        Stage stage = (Stage) lblSaudacao.getScene().getWindow();
        
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        
    } catch (Exception e) {
        e.printStackTrace();
        mostrarMensagem("Erro", "Não foi possível abrir o mercado: " + e.getMessage());
    }



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