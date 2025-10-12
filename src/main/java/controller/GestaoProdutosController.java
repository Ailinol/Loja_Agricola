/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author liliano
 */
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import model.Produto;
import model.Agricultor;
import service.ProdutoService;
import service.UsuarioService;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.jboss.jandex.Main;
import service.ResultadoValidacao;

public class GestaoProdutosController implements Initializable {

    @FXML private TextField txtPesquisa;
    @FXML private ComboBox<String> comboFiltrarCategoria;
    @FXML private ComboBox<String> comboFiltrarStatus;
    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, String> colCategoria;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, Integer> colQuantidade;
    @FXML private TableColumn<Produto, Boolean> colStatus;
    @FXML private TableColumn<Produto, String> colDataCadastro;
    @FXML private TableColumn<Produto, Integer> colAcoes;
    @FXML private Label lblTotalProdutos;

    private ProdutoService produtoService;
    private UsuarioService usuarioService;
    private Agricultor agricultorLogado;
    private ObservableList<Produto> produtosList;
    private ObservableList<Produto> produtosFiltrados;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.produtoService = new ProdutoService();
        this.usuarioService = new UsuarioService();
        
        // Inicializar agricultor
        inicializarAgricultor();
        
        configurarTabela();
        configurarFiltros();
        carregarProdutos();
    }

    private void inicializarAgricultor() {
        try {
            List<Agricultor> agricultores = usuarioService.listarAgricultores();
            if (!agricultores.isEmpty()) {
                this.agricultorLogado = agricultores.get(0);
                System.out.println("Agricultor para gestão: " + agricultorLogado.getNome());
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar agricultor: " + e.getMessage());
        }
    }

    private void configurarTabela() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidadeDisponivel"));
        colDataCadastro.setCellValueFactory(new PropertyValueFactory<>("dataCadastro"));
        
        colStatus.setCellValueFactory(new PropertyValueFactory<>("disponivel"));
        colStatus.setCellFactory(column -> new TableCell<Produto, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "DISPONÍVEL" : "INDISPONÍVEL");
                    setStyle(item ? 
                        "-fx-text-fill: #4CAF50; -fx-font-weight: bold;" : 
                        "-fx-text-fill: #f44336; -fx-font-weight: bold;");
                }
            }
        });
        
        // Configurar coluna de ações
        colAcoes.setCellFactory(column -> new TableCell<Produto, Integer>() {
            private final HBox botoes = new HBox(5);
            private final Button btnEditar = new Button();
            private final Button btnEliminar = new Button();
            
            {
                // Botão Editar
                btnEditar.setStyle("-fx-background-color: rgba(33,150,243,0.3); -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4;");
                //btnEditar.setGraphic(new FontAwesomeIconView("EDIT"));
                btnEditar.setOnAction(event -> {
                    Produto produto = getTableView().getItems().get(getIndex());
                    editarProduto(produto);
                });
                
                // Botão Eliminar
                btnEliminar.setStyle("-fx-background-color: rgba(244,67,54,0.3); -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4;");
                //btnEliminar.setGraphic(new FontAwesomeIconView("TRASH"));
                btnEliminar.setOnAction(event -> {
                    Produto produto = getTableView().getItems().get(getIndex());
                    eliminarProduto(produto);
                });
                
                botoes.getChildren().addAll(btnEditar, btnEliminar);
            }
            
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(botoes);
                }
            }
        });
    }

    private void configurarFiltros() {
        // Configurar categorias
        comboFiltrarCategoria.getItems().addAll("Todas", "Hortaliças", "Frutas", "Grãos", "Leguminosas", "Tubérculos");
        comboFiltrarCategoria.setValue("Todas");
        
        // Configurar status
        comboFiltrarStatus.getItems().addAll("Todos", "Disponíveis", "Indisponíveis");
        comboFiltrarStatus.setValue("Todos");
    }

    private void carregarProdutos() {
        if (agricultorLogado == null) return;
        
        try {
            List<Produto> produtos = produtoService.buscarProdutosPorAgricultor(agricultorLogado.getId());
            produtosList = FXCollections.observableArrayList(produtos);
            produtosFiltrados = FXCollections.observableArrayList(produtos);
            
            tabelaProdutos.setItems(produtosFiltrados);
            atualizarContador();
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar produtos: " + e.getMessage());
            mostrarErro("Erro", "Não foi possível carregar os produtos.");
        }
    }

    @FXML
    private void handlePesquisar(ActionEvent event) {
        filtrarProdutos();
    }

    @FXML
    private void handleFiltrarCategoria(ActionEvent event) {
        filtrarProdutos();
    }

    @FXML
    private void handleFiltrarStatus(ActionEvent event) {
        filtrarProdutos();
    }

    private void filtrarProdutos() {
        if (produtosList == null) return;
        
        String termoPesquisa = txtPesquisa.getText().toLowerCase();
        String categoriaFiltro = comboFiltrarCategoria.getValue();
        String statusFiltro = comboFiltrarStatus.getValue();
        
        List<Produto> filtrados = produtosList.stream()
            .filter(produto -> 
                produto.getNome().toLowerCase().contains(termoPesquisa) ||
                produto.getDescricao().toLowerCase().contains(termoPesquisa))
            .filter(produto -> 
                categoriaFiltro.equals("Todas") || 
                produto.getCategoria().equals(categoriaFiltro))
            .filter(produto -> {
                if (statusFiltro.equals("Todos")) return true;
                if (statusFiltro.equals("Disponíveis")) return produto.isDisponivel();
                if (statusFiltro.equals("Indisponíveis")) return !produto.isDisponivel();
                return true;
            })
            .collect(Collectors.toList());
        
        produtosFiltrados.setAll(filtrados);
        atualizarContador();
    }

    @FXML
    private void handleNovoProduto(ActionEvent event) throws IOException {
       try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/cadastro_produto.fxml"));
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
    private void handleEditarProduto(ActionEvent event) {
        Produto produtoSelecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (produtoSelecionado != null) {
            editarProduto(produtoSelecionado);
        } else {
            mostrarAlerta("Seleção Necessária", "Por favor, selecione um produto para editar.");
        }
    }

    @FXML
    private void handleEliminarProduto(ActionEvent event) {
        Produto produtoSelecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (produtoSelecionado != null) {
            eliminarProduto(produtoSelecionado);
        } else {
            mostrarAlerta("Seleção Necessária", "Por favor, selecione um produto para eliminar.");
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        // Voltar para tela anterior (dashboard do agricultor)
        System.out.println("Voltando para tela anterior...");
        //Main.trocarCena("dashboard_agricultor.fxml");
    }

    private void editarProduto(Produto produto) {
         try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/cadastro_produto.fxml"));
        Parent root = loader.load();
        
        CadastroProdutoController controller = loader.getController();
        controller.setProdutoParaEdicao(produto);
        
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Editar Produto");
        stage.setScene(scene);
        stage.show();
        
    } catch (Exception e) {
        mostrarErro("Erro", "Não foi possível abrir a edição: " + e.getMessage());
    }
    }

    private void eliminarProduto(Produto produto) {
        try {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Eliminação");
            confirmacao.setHeaderText("Eliminar Produto");
            confirmacao.setContentText("Tem certeza que deseja eliminar o produto:\n" + 
                                     produto.getNome() + "?\n\nEsta ação não pode ser desfeita.");
            
            ButtonType resultado = confirmacao.showAndWait().orElse(ButtonType.CANCEL);
            
            if (resultado == ButtonType.OK) {
                ResultadoValidacao sucesso = produtoService.removerProduto(produto.getId(), agricultorLogado.getId());
                
                if (sucesso.valido == true) {
                    mostrarSucesso("Produto eliminado com sucesso!");
                    carregarProdutos(); // Recarregar lista
                } else {
                    mostrarErro("Erro", "Não foi possível eliminar o produto.");
                }
            }
            
        } catch (Exception e) {
            mostrarErro("Erro", "Erro ao eliminar produto: " + e.getMessage());
        }
    }

    private void atualizarContador() {
        int total = produtosFiltrados.size();
        lblTotalProdutos.setText("(" + total + " produto" + (total != 1 ? "s" : "") + ")");
    }

    // Métodos auxiliares para mensagens
    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // Método para definir o agricultor logado
    public void setAgricultorLogado(Agricultor agricultor) {
        this.agricultorLogado = agricultor;
        if (agricultor != null) {
            carregarProdutos();
        }
    }

    // Método para limpar recursos
    public void fecharServicos() {
        if (produtoService != null) {
            produtoService.fecharConexao();
        }
        if (usuarioService != null) {
            usuarioService.fecharConexao();
        }
    }
}