package controller;

import java.io.IOException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Agricultor;
import model.Produto;
import service.RelatorioService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RelatorioSistemaController {

    private final RelatorioService relatorioService = new RelatorioService();

    @FXML
    private Label lblClientesAtivos;

    // Agricultores
    @FXML
    private TableView<AgricultorView> tabelaAgricultores;
    @FXML
    private TableColumn<AgricultorView, String> colAgricultorNome;
    @FXML
    private TableColumn<AgricultorView, Integer> colAgricultorProdutos;

    // Produtos
    @FXML
    private TableView<ProdutoView> tabelaProdutos;
    @FXML
    private TableColumn<ProdutoView, String> colProdutoNome;
    @FXML
    private TableColumn<ProdutoView, Long> colProdutoPedidos;

    // Áreas
    @FXML
    private TableView<AreaView> tabelaAreas;
    @FXML
    private TableColumn<AreaView, String> colAreaNome;
    @FXML
    private TableColumn<AreaView, Long> colAreaTransacoes;

    @FXML
    public void initialize() {
        carregarClientesAtivos();
        configurarTabelaAgricultores();
        carregarAgricultoresMaisProdutivos();
        configurarTabelaProdutos();
        carregarProdutosMaisProcurados();
        configurarTabelaAreas();
        carregarAreasMaisTransacoes();
    }

    /**
     * Clientes ativos
     */
    private void carregarClientesAtivos() {
        long total = relatorioService.totalClientesAtivos();
        lblClientesAtivos.setText(String.valueOf(total));
    }

    /**
     * Agricultores
     */
    private void configurarTabelaAgricultores() {
        colAgricultorNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colAgricultorProdutos.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantidadeProdutos()).asObject());
    }

    private void carregarAgricultoresMaisProdutivos() {
        List<Agricultor> topAgricultores = relatorioService.agricultoresMaisProdutivos(10);
        List<AgricultorView> viewList = topAgricultores.stream()
                .map(a -> new AgricultorView(a.getNome(), a.getProdutos().size()))
                .collect(Collectors.toList());
        tabelaAgricultores.setItems(FXCollections.observableArrayList(viewList));
    }

    /**
     * Produtos
     */
    private void configurarTabelaProdutos() {
        colProdutoNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colProdutoPedidos.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getQuantidade()).asObject());
    }

    private void carregarProdutosMaisProcurados() {
        Map<Produto, Long> topProdutos = relatorioService.produtosMaisProcurados(10);
        List<ProdutoView> viewList = topProdutos.entrySet().stream()
                .map(e -> new ProdutoView(e.getKey().getNome(), e.getValue()))
                .collect(Collectors.toList());
        tabelaProdutos.setItems(FXCollections.observableArrayList(viewList));
    }

    /**
     * Áreas
     */
    private void configurarTabelaAreas() {
        colAreaNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colAreaTransacoes.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getQuantidade()).asObject());
    }

    private void carregarAreasMaisTransacoes() {
        Map<String, Long> areas = relatorioService.areasMaisTransacoes();
        List<AreaView> viewList = areas.entrySet().stream()
                .map(e -> new AreaView(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        tabelaAreas.setItems(FXCollections.observableArrayList(viewList));
    }

    /**
     * Classes auxiliares para TableView
     */
    public static class AgricultorView {

        private final String nome;
        private final int quantidadeProdutos;

        public AgricultorView(String nome, int quantidadeProdutos) {
            this.nome = nome;
            this.quantidadeProdutos = quantidadeProdutos;
        }

        public String getNome() {
            return nome;
        }

        public int getQuantidadeProdutos() {
            return quantidadeProdutos;
        }
    }

    public static class ProdutoView {

        private final String nome;
        private final long quantidade;

        public ProdutoView(String nome, long quantidade) {
            this.nome = nome;
            this.quantidade = quantidade;
        }

        public String getNome() {
            return nome;
        }

        public long getQuantidade() {
            return quantidade;
        }
    }

    public static class AreaView {

        private final String nome;
        private final long quantidade;

        public AreaView(String nome, long quantidade) {
            this.nome = nome;
            this.quantidade = quantidade;
        }

        public String getNome() {
            return nome;
        }

        public long getQuantidade() {
            return quantidade;
        }
    }

    @FXML
    private void voltarDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DashboardAdministradorView.fxml"));
            AnchorPane dashboardRoot = loader.load();

            Stage stage = (Stage) lblClientesAtivos.getScene().getWindow();
            Scene scene = new Scene(dashboardRoot);
            scene.getStylesheets().add(getClass().getResource("/view/GreenMatch.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar o Dashboard do Administrador!");
        }
    }
}
