package controller;

import java.io.File;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Produto;
import model.Agricultor;
import model.Comprador;
import model.ItemCarrinho;
import model.Pedido;
import model.ItemPedido;
import service.MapaService;
import service.ProdutoService;
import service.SessaoActual;
import service.UsuarioService;
import dao.PedidoDAO;
import dao.ItemPedidoDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyCombination;

public class VendasController implements Initializable {

    @FXML private TextField txtPesquisa;
    @FXML private ComboBox<String> comboCategoria, comboOrdenacao, comboDistancia;
    @FXML private FlowPane containerProdutos;
    @FXML private Label lblDistancia, lblTempoViagem, lblEndereco;
    @FXML private Button btnRotas;
    @FXML private Button voltarButton;
    @FXML private Button btnCarrinho;
    @FXML private SplitPane splitPane;
    @FXML private WebView webViewMapa;

    private String produtoSelecionadoId;
    private MapaService webMapService;
    private ProdutoService produtoService;
    private UsuarioService usuarioService;
    private List<ItemCarrinho> carrinho = new ArrayList<>();
    private Label cartBadge;

    private double userLatitude = -25.9692;
    private double userLongitude = 32.5732;

    private final LinearGradient[] cardGradients = {
        new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#4CAF50")), new Stop(1, Color.web("#45a049"))),
        new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#FF9800")), new Stop(1, Color.web("#F57C00"))),
        new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#2196F3")), new Stop(1, Color.web("#1976D2"))),
        new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#9C27B0")), new Stop(1, Color.web("#7B1FA2")))
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.produtoService = new ProdutoService();
        this.usuarioService = new UsuarioService();
        
        carregarLocalizacaoComprador();
        
        configurarComponentesPremium();
        inicializarMapaReal();
        carregarProdutosPremium();
        configurarEfeitosInterativos();
        
        inicializarCarrinho();
    }

    private void inicializarCarrinho() {
        cartBadge = new Label("0");
        cartBadge.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-size: 10px; " +
                          "-fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 2 5 2 5; " +
                          "-fx-min-width: 18px; -fx-min-height: 18px;");
        cartBadge.setVisible(false);
        
        StackPane cartStack = new StackPane();
        cartStack.getChildren().addAll(btnCarrinho.getGraphic(), cartBadge);
        btnCarrinho.setGraphic(cartStack);
        
        btnCarrinho.setOnMouseEntered(e -> {
            btnCarrinho.setEffect(new Glow(0.3));
        });
        btnCarrinho.setOnMouseExited(e -> {
            btnCarrinho.setEffect(null);
        });
    }

    private void atualizarBadgeCarrinho() {
        int totalItens = carrinho.stream()
            .mapToInt(ItemCarrinho::getQuantidade)
            .sum();
        
        if (totalItens > 0) {
            cartBadge.setText(String.valueOf(totalItens));
            cartBadge.setVisible(true);
            cartBadge.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-size: 10px; " +
                             "-fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 2 5 2 5; " +
                             "-fx-min-width: 18px; -fx-min-height: 18px; " +
                             "-fx-effect: dropshadow(gaussian, #ff4444, 10, 0.5, 0, 0);");
        } else {
            cartBadge.setVisible(false);
        }
    }

    @FXML
    private void onCarrinhoClick() {
        if (carrinho.isEmpty()) {
            mostrarAlertaPremium("🛒 Carrinho Vazio", 
                "Seu carrinho está vazio. Adicione produtos para continuar.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("🛒 Meu Carrinho");
        dialog.setHeaderText("Revise seus itens antes de finalizar");

        ButtonType btnFinalizar = new ButtonType("Finalizar Compra", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnContinuar = new ButtonType("Continuar Comprando", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btnLimpar = new ButtonType("Limpar Carrinho", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(btnFinalizar, btnLimpar, btnContinuar);

        VBox content = new VBox(15);
        content.setStyle("-fx-background-color: rgba(45,45,45,0.95); -fx-padding: 20;");

        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-background: transparent; -fx-border-color: #4CAF50; -fx-border-radius: 10;");
        scroll.setPrefHeight(350);

        VBox itensContainer = new VBox(10);
        itensContainer.setStyle("-fx-padding: 10;");

        double total = 0.0;
        int totalItens = 0;

        for (ItemCarrinho item : carrinho) {
            HBox itemBox = criarItemCarrinhoBox(item);
            itensContainer.getChildren().add(itemBox);
            total += item.getTotal();
            totalItens += item.getQuantidade();
        }

        scroll.setContent(itensContainer);

        VBox resumoBox = new VBox(10);
        resumoBox.setStyle("-fx-padding: 15 0 0 0; -fx-border-color: #4CAF50; -fx-border-width: 1 0 0 0;");

        Label lblResumo = new Label("RESUMO DO PEDIDO");
        lblResumo.setStyle("-fx-text-fill: #b3e5d1; -fx-font-size: 16px; -fx-font-weight: bold;");

        HBox totalItensBox = new HBox();
        totalItensBox.setAlignment(Pos.CENTER_LEFT);
        Label lblTotalItens = new Label("Total de itens: " + totalItens);
        lblTotalItens.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 14px;");
        totalItensBox.getChildren().add(lblTotalItens);

        HBox totalBox = new HBox();
        totalBox.setAlignment(Pos.CENTER_LEFT);
        Label lblTotal = new Label("TOTAL: " + String.format("%.2f MT", total));
        lblTotal.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 20px; -fx-font-weight: bold;");
        totalBox.getChildren().add(lblTotal);

        resumoBox.getChildren().addAll(lblResumo, totalItensBox, totalBox);
        content.getChildren().addAll(scroll, resumoBox);
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().setStyle("-fx-background-color: linear-gradient(to bottom, #1a3a1a, #0d1f0d); -fx-border-color: #2d5a2d; -fx-border-width: 2;");
      
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            ButtonType buttonType = resultado.get();
            if (buttonType == btnFinalizar) {
                finalizarCompra();
            } else if (buttonType == btnLimpar) {
                limparCarrinho();
            }
        }
    }

    private HBox criarItemCarrinhoBox(ItemCarrinho item) {
        HBox box = new HBox(15);
        box.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 15; -fx-padding: 15;");
        box.setAlignment(Pos.CENTER_LEFT);

        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 10;");
        imageContainer.setPrefSize(70, 70);

        ImageView img = new ImageView();
        img.setFitHeight(60);
        img.setFitWidth(60);
        img.setStyle("-fx-background-radius: 8;");

        if (item.getProduto().getImagemPrincipal() != null) {
            try {
                File imageFile = new File(item.getProduto().getImagemPrincipal());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    img.setImage(image);
                } else {
                    Rectangle fallback = new Rectangle(60, 60);
                    fallback.setFill(cardGradients[0]);
                    fallback.setArcWidth(10);
                    fallback.setArcHeight(10);
                    imageContainer.getChildren().add(fallback);
                }
            } catch (Exception e) {
                Rectangle fallback = new Rectangle(60, 60);
                fallback.setFill(cardGradients[0]);
                fallback.setArcWidth(10);
                fallback.setArcHeight(10);
                imageContainer.getChildren().add(fallback);
            }
        }

        if (imageContainer.getChildren().isEmpty()) {
            imageContainer.getChildren().add(img);
        }

        VBox detalhes = new VBox(8);
        detalhes.setMaxWidth(200);

        Label nome = new Label(item.getProduto().getNome());
        nome.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        nome.setWrapText(true);

        Label precoUnitario = new Label(String.format("Preço: %.2f MT/unidade", item.getProduto().getPreco()));
        precoUnitario.setStyle("-fx-text-fill: #b3e5d1; -fx-font-size: 12px;");

        HBox quantidadeBox = new HBox(10);
        quantidadeBox.setAlignment(Pos.CENTER_LEFT);

        Button btnMenos = new Button("➖");
        btnMenos.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-size: 12px; " +
                         "-fx-background-radius: 15; -fx-min-width: 25px; -fx-min-height: 25px;");
        btnMenos.setOnAction(e -> alterarQuantidadeItem(item, -1));

        Label lblQuantidade = new Label(String.valueOf(item.getQuantidade()));
        lblQuantidade.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Button btnMais = new Button("➕");
        btnMais.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px; " +
                        "-fx-background-radius: 15; -fx-min-width: 25px; -fx-min-height: 25px;");
        btnMais.setOnAction(e -> alterarQuantidadeItem(item, 1));

        quantidadeBox.getChildren().addAll(btnMenos, lblQuantidade, btnMais);

        Label precoTotal = new Label(String.format("Subtotal: %.2f MT", item.getTotal()));
        precoTotal.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 14px; -fx-font-weight: bold;");

        detalhes.getChildren().addAll(nome, precoUnitario, quantidadeBox, precoTotal);

        VBox controles = new VBox(10);
        controles.setAlignment(Pos.CENTER_RIGHT);

        Button btnRemover = new Button("Remover");
        btnRemover.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff4444; -fx-border-color: #ff4444; " +
                          "-fx-border-radius: 15; -fx-background-radius: 15; -fx-padding: 5 10 5 10;");
        btnRemover.setOnAction(e -> removerItemCarrinho(item));

        controles.getChildren().add(btnRemover);

        box.getChildren().addAll(imageContainer, detalhes, controles);
        return box;
    }

    private void alterarQuantidadeItem(ItemCarrinho item, int mudanca) {
        int novaQuantidade = item.getQuantidade() + mudanca;
        
        if (novaQuantidade <= 0) {
            removerItemCarrinho(item);
        } else if (novaQuantidade <= item.getProduto().getQuantidadeDisponivel()) {
            item.setQuantidade(novaQuantidade);
            onCarrinhoClick(); 
        } else {
            mostrarAlertaPremium("⚠️ Limite", "Quantidade máxima disponível: " + item.getProduto().getQuantidadeDisponivel());
        }
    }

    private void removerItemCarrinho(ItemCarrinho item) {
        carrinho.remove(item);
        atualizarBadgeCarrinho();
        onCarrinhoClick(); 
    }

    private void finalizarCompra() {
        try {
            Pedido pedido = new Pedido();
            pedido.setComprador(SessaoActual.getCompradorLogado());
           
            List<ItemPedido> itensPedido = new ArrayList<>();
            for (ItemCarrinho itemCarrinho : carrinho) {
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setProduto(itemCarrinho.getProduto());
                itemPedido.setQuantidade(itemCarrinho.getQuantidade());
                itemPedido.setPedido(pedido);
                itensPedido.add(itemPedido);
            }
            
            pedido.setItensPedidos(itensPedido);
            
            PedidoDAO pedidoDAO = new PedidoDAO();
            pedidoDAO.salvarPedido(pedido);
            
            ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO();
            for (ItemPedido item : itensPedido) {
                itemPedidoDAO.adicionarItem(item);
            }
            
            mostrarAlertaPremium("Pedido Realizado", 
                "Seu pedido foi realizado com sucesso!\n\n" +
                "Total: " + String.format("%.2f MT", pedido.getValorTotal()) + "\n" +
                "Itens: " + carrinho.size() + "\n\n" +
                "O agricultor entrará em contato em breve.");
            
            limparCarrinho();
            
        } catch (Exception e) {
            mostrarAlertaPremium("❌ Erro", 
                "Não foi possível finalizar o pedido: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limparCarrinho() {
        carrinho.clear();
        atualizarBadgeCarrinho();
        mostrarAlertaPremium("🛒 Carrinho", "Carrinho limpo com sucesso!");
    }

    private void mostrarDialogoQuantidade(Produto produto) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("🛒 Adicionar ao Carrinho");
        dialog.setHeaderText("Quantidade de " + produto.getNome());

        ButtonType btnConfirmar = new ButtonType("✅ Confirmar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("❌ Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirmar, btnCancelar);

        VBox content = new VBox(20);
        content.setStyle("-fx-background-color: rgba(45,45,45,0.95); -fx-padding: 25;");
        content.setAlignment(Pos.CENTER);

        HBox infoBox = new HBox(15);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15;");
        imageContainer.setPrefSize(80, 80);

        ImageView imgProduto = new ImageView();
        imgProduto.setFitHeight(70);
        imgProduto.setFitWidth(70);
        imgProduto.setStyle("-fx-background-radius: 10;");

        if (produto.getImagemPrincipal() != null && !produto.getImagemPrincipal().isEmpty()) {
            try {
                File imageFile = new File(produto.getImagemPrincipal());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imgProduto.setImage(image);
                    imageContainer.getChildren().add(imgProduto);
                } else {
                    throw new Exception("Arquivo não encontrado");
                }
            } catch (Exception e) {
                Rectangle fallback = new Rectangle(70, 70);
                fallback.setFill(cardGradients[0]);
                fallback.setArcWidth(15);
                fallback.setArcHeight(15);
                imageContainer.getChildren().add(fallback);
                
                Label emoji = new Label(obterEmojiPorCategoria(produto.getCategoria()));
                emoji.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
                imageContainer.getChildren().add(emoji);
            }
        } else {
            Rectangle fallback = new Rectangle(70, 70);
            fallback.setFill(cardGradients[0]);
            fallback.setArcWidth(15);
            fallback.setArcHeight(15);
            imageContainer.getChildren().add(fallback);
            
            Label emoji = new Label(obterEmojiPorCategoria(produto.getCategoria()));
            emoji.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
            imageContainer.getChildren().add(emoji);
        }

        VBox detalhes = new VBox(8);
        Label nome = new Label(produto.getNome());
        nome.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label preco = new Label(String.format("Preço: %.2f MT/unidade", produto.getPreco()));
        preco.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 14px;");

        Label stock = new Label("Disponível: " + produto.getQuantidadeDisponivel() + " " + produto.getUnidadeMedida());
        stock.setStyle("-fx-text-fill: #b3e5d1; -fx-font-size: 12px;");

        detalhes.getChildren().addAll(nome, preco, stock);
        infoBox.getChildren().addAll(imageContainer, detalhes);

        VBox quantidadeContainer = new VBox(15);
        quantidadeContainer.setAlignment(Pos.CENTER);

        Label lblSelecionar = new Label("Selecione a quantidade:");
        lblSelecionar.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        HBox quantidadeBox = new HBox(20);
        quantidadeBox.setAlignment(Pos.CENTER);

        Button btnMenos = new Button("➖");
        btnMenos.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-size: 18px; " +
                         "-fx-background-radius: 20; -fx-min-width: 45px; -fx-min-height: 45px;");

        Label lblQuantidade = new Label("1");
        lblQuantidade.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; " +
                              "-fx-padding: 0 25 0 25;");

        Button btnMais = new Button("➕");
        btnMais.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; " +
                        "-fx-background-radius: 20; -fx-min-width: 45px; -fx-min-height: 45px;");

        quantidadeBox.getChildren().addAll(btnMenos, lblQuantidade, btnMais);

        Label lblTotal = new Label("Total: " + String.format("%.2f MT", produto.getPreco()));
        lblTotal.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 20px; -fx-font-weight: bold;");

        quantidadeContainer.getChildren().addAll(lblSelecionar, quantidadeBox, lblTotal);

        content.getChildren().addAll(infoBox, quantidadeContainer);
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().setStyle("-fx-background-color: rgba(45,45,45,0.95); -fx-border-color: #4CAF50;");

        final int[] quantidade = {1};

        btnMenos.setOnAction(e -> {
            if (quantidade[0] > 1) {
                quantidade[0]--;
                lblQuantidade.setText(String.valueOf(quantidade[0]));
                lblTotal.setText("Total: " + String.format("%.2f MT", produto.getPreco() * quantidade[0]));
            }
        });

        btnMais.setOnAction(e -> {
            if (quantidade[0] < produto.getQuantidadeDisponivel()) {
                quantidade[0]++;
                lblQuantidade.setText(String.valueOf(quantidade[0]));
                lblTotal.setText("Total: " + String.format("%.2f MT", produto.getPreco() * quantidade[0]));
            } else {
                mostrarAlertaPremium("Limite", "Quantidade máxima disponível: " + produto.getQuantidadeDisponivel());
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnConfirmar) {
                return quantidade[0];
            }
            return null;
        });

        Optional<Integer> resultado = dialog.showAndWait();
        resultado.ifPresent(qtd -> {
            adicionarAoCarrinhoComQuantidade(produto, qtd);
        });
    }

    private void adicionarAoCarrinhoComQuantidade(Produto produto, int quantidade) {
        Optional<ItemCarrinho> itemExistente = carrinho.stream()
            .filter(item -> item.getProduto().getId() == produto.getId())
            .findFirst();

        if (itemExistente.isPresent()) {
            ItemCarrinho item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + quantidade);

            mostrarAlertaPremium("🛒 Carrinho Atualizado", 
                String.format("'%s' - Quantidade atualizada para %d (Total: %.2f MT)", 
                    produto.getNome(), item.getQuantidade(), item.getTotal()));
        } else {
            ItemCarrinho novoItem = new ItemCarrinho(produto, quantidade);
            carrinho.add(novoItem);

            mostrarAlertaPremium("Adicionado ao Carrinho", 
                String.format("'%s' - %d unidade(s) adicionada(s) (Total: %.2f MT)", 
                    produto.getNome(), quantidade, novoItem.getTotal()));
        }

        atualizarBadgeCarrinho();

        System.out.println("🛒 Carrinho atualizado: " + carrinho.size() + " itens");
    }

    private void inicializarMapaReal() {
        try {
            System.out.println("Inicializando mapa real com OpenStreetMap...");
            
            webViewMapa.setContextMenuEnabled(false);
            webViewMapa.setFocusTraversable(true);
            
            webMapService = new MapaService(webViewMapa.getEngine());
            
            webMapService.quandoMapaPronto(status -> {
                System.out.println("" + status);
                adicionarAgricultoresReaisNoMapa();
            });
            
            System.out.println("Serviço de mapa real inicializado!");
            
        } catch (Exception e) {
            System.err.println("Erro crítico no mapa real: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void carregarLocalizacaoComprador() {
        try {
            Comprador comprador = SessaoActual.getCompradorLogado();
            
            if (comprador != null) {
                userLatitude = comprador.getLatitude();
                userLongitude = comprador.getLongitude();
                
                System.out.println("📍 Localização carregada: " + comprador.getNome() + 
                                 " (" + userLatitude + ", " + userLongitude + ")");
            } else {
                userLatitude = -25.9692;
                userLongitude = 32.5732;
                System.out.println("Usando coordenadas padrão");
            }
            
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            userLatitude = -25.9692;
            userLongitude = 32.5732;
        }
    }

    private void adicionarAgricultoresReaisNoMapa() {
        try {
            List<Agricultor> agricultores = usuarioService.listarAgricultores();
            
            if (agricultores.isEmpty()) {
                System.out.println("Nenhum agricultor encontrado no banco de dados");
                return;
            }
            
            webMapService.definirLocalizacaoUsuario(userLatitude, userLongitude);
             
            for (Agricultor agricultor : agricultores) {
                double lat = agricultor.getLatitude();
                double lng = agricultor.getLongitude();
                
                if (lat != 0 && lng != 0) {
                    List<Produto> produtosAgricultor = produtoService.buscarProdutosPorAgricultor(agricultor.getId());
                    String produtoPrincipal = produtosAgricultor.isEmpty() ? 
                        "Produtos Agrícolas" : produtosAgricultor.get(0).getNome();
                     
                    adicionarAgricultorNoMapa(agricultor, produtoPrincipal);
                    
                    System.out.println("Agricultor adicionado: " + agricultor.getNome() + 
                                     " (" + lat + ", " + lng + ")");
                } else {
                    System.out.println("Agricultor sem coordenadas: " + agricultor.getNome());
                }
            }
            
            System.out.println("🎉 " + agricultores.size() + " agricultores reais processados no mapa");
            
        } catch (Exception e) {
            System.err.println("Erro ao adicionar agricultores reais no mapa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void adicionarAgricultorNoMapa(Agricultor agricultor, String produtoPrincipal) {
        try {
            webMapService.adicionarMarcadorAgricultor(
                "agricultor_" + agricultor.getId(),
                new model.Localizacao(agricultor.getLatitude(), agricultor.getLongitude()),
                agricultor.getNome(),
                produtoPrincipal
            );
        } catch (Exception e) {
            System.err.println("Erro ao adicionar agricultor no mapa: " + e.getMessage());
        }
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    private double calcularDistanciaParaProduto(Produto produto) {
        try {
            Agricultor agricultor = buscarAgricultorPorId(produto.getAgricultorId());
            if (agricultor != null && agricultor.getLatitude() != 0 && agricultor.getLongitude() != 0) {
                return calcularDistancia(
                    userLatitude, userLongitude,
                    agricultor.getLatitude(), agricultor.getLongitude()
                );
            }
        } catch (Exception e) {
            System.err.println("Erro ao calcular distância: " + e.getMessage());
        }
        return 0;
    }

    private Agricultor buscarAgricultorPorId(int agricultorId) {
        try {
            List<Agricultor> agricultores = usuarioService.listarAgricultores();
            for (Agricultor agricultor : agricultores) {
                if (agricultor.getId() == agricultorId) {
                    return agricultor;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar agricultor: " + e.getMessage());
        }
        return null;
    }

    private String calcularTempoViagem(double distancia) {
        int tempo = (int) (distancia * 2 + 10);
        return String.valueOf(tempo);
    }

    private void configurarComponentesPremium() {
        List<Produto> produtos = produtoService.listarTodosProdutos();
        comboCategoria.getItems().add("🌿 Todos");
        
        produtos.stream()
            .map(Produto::getCategoria)
            .distinct()
            .forEach(categoria -> comboCategoria.getItems().add(categoria));
        
        comboCategoria.setValue("🌿 Todos");
        
        comboOrdenacao.getItems().addAll("📍 Mais Próximo", "Mais Barato", "Melhor Avaliado", "Mais Recente");
        comboOrdenacao.setValue("Mais Recente");
        
        comboDistancia.getItems().addAll("📍 Todos", "📏 5 km", "📏 10 km", "📏 15 km", "📏 20 km");
        comboDistancia.setValue("📍 Todos");

        splitPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnRotas.setDisable(true);
    }

    private void configurarEfeitosInterativos() {
        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> filtrarProdutos());
        comboCategoria.setOnAction(e -> filtrarProdutos());
        comboOrdenacao.setOnAction(e -> ordenarProdutos());
        comboDistancia.setOnAction(e -> filtrarPorDistancia());
        
        btnRotas.setOnMouseEntered(e -> btnRotas.setEffect(new Glow(0.3)));
        btnRotas.setOnMouseExited(e -> btnRotas.setEffect(null));
    }

    private void carregarProdutosPremium() {
        try {
            containerProdutos.getChildren().clear();

            List<Produto> produtos = produtoService.listarProdutosDisponiveis();
            
            if (produtos.isEmpty()) {
                Label lblVazio = new Label("Nenhum produto disponível no momento");
                lblVazio.setStyle("-fx-text-fill: #666; -fx-font-size: 16; -fx-padding: 40;");
                containerProdutos.getChildren().add(lblVazio);
                System.out.println("Nenhum produto disponível encontrado");
                return;
            }

            System.out.println("Carregando " + produtos.size() + " produtos reais do banco de dados");
            
            int index = 0;
            for (Produto produto : produtos) {
                VBox card = criarCardProduto(produto, index++);
                containerProdutos.getChildren().add(card);
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar produtos: " + e.getMessage());
            e.printStackTrace();
            
            Label lblErro = new Label("Erro ao carregar produtos");
            lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 14; -fx-padding: 20;");
            containerProdutos.getChildren().add(lblErro);
        }
    }

    private VBox criarCardProduto(Produto produto, int index) {
        VBox card = new VBox();
        card.getStyleClass().add("product-card-premium");
        card.setPrefWidth(300);
        card.setMaxWidth(300);
        card.setAlignment(Pos.TOP_CENTER);
        
        DropShadow shadow = new DropShadow(20, Color.rgb(0, 0, 0, 0.3));
        card.setEffect(shadow);

        StackPane cardContainer = new StackPane();
        cardContainer.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 20; " +
                             "-fx-border-color: rgba(255,255,255,0.15); -fx-border-radius: 20;");
        cardContainer.setPrefSize(300, 420);

        VBox content = new VBox();
        content.setPrefSize(300, 420);
        content.setAlignment(Pos.TOP_CENTER);
        content.setSpacing(0);

        StackPane imageSection = new StackPane();
        imageSection.setPrefHeight(180);
        imageSection.setAlignment(Pos.CENTER);
        imageSection.setStyle("-fx-background-radius: 20 20 0 0;"); 

        if (produto.getImagemPrincipal() != null && !produto.getImagemPrincipal().isEmpty()) {
            try {
                File imageFile = new File(produto.getImagemPrincipal());
                
                if (imageFile.exists() && imageFile.isFile()) {
                    ImageView productImage = new ImageView();
                    
                    productImage.setFitHeight(180);  
                    productImage.setFitWidth(300);  
                    productImage.setPreserveRatio(false);
                    
                    Image image = new Image(imageFile.toURI().toString());
                    productImage.setImage(image);
                    
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), productImage);
                    productImage.setOnMouseEntered(e -> {
                        scaleTransition.setToX(1.05);
                        scaleTransition.setToY(1.05);
                        scaleTransition.play();
                    });
                    productImage.setOnMouseExited(e -> {
                        scaleTransition.setToX(1.0);
                        scaleTransition.setToY(1.0);
                        scaleTransition.play();
                    });
                    
                    imageSection.getChildren().add(productImage);
                    System.out.println("Imagem FULL SIZE carregada: " + produto.getNome());
                    
                } else {
                    throw new Exception("Arquivo de imagem não encontrado");
                }
            } catch (Exception e) {
                Rectangle fallbackBackground = new Rectangle(300, 180);
                fallbackBackground.setFill(cardGradients[index % cardGradients.length]);
                fallbackBackground.setArcWidth(20);
                fallbackBackground.setArcHeight(20);
                
                String emoji = obterEmojiPorCategoria(produto.getCategoria());
                Label fallbackIcon = new Label(emoji);
                fallbackIcon.setStyle("-fx-font-size: 40px; -fx-text-fill: white;");
                
                imageSection.getChildren().addAll(fallbackBackground, fallbackIcon);
                System.out.println("Fallback para gradiente: " + e.getMessage());
            }
        } else {
            Rectangle fallbackBackground = new Rectangle(300, 180);
            fallbackBackground.setFill(cardGradients[index % cardGradients.length]);
            fallbackBackground.setArcWidth(20);
            fallbackBackground.setArcHeight(20);
            
            String emoji = obterEmojiPorCategoria(produto.getCategoria());
            Label fallbackIcon = new Label(emoji);
            fallbackIcon.setStyle("-fx-font-size: 40px; -fx-text-fill: white;");
            
            imageSection.getChildren().addAll(fallbackBackground, fallbackIcon);
            System.out.println("ℹ️  Sem imagem definida para: " + produto.getNome());
        }

        HBox badgesContainer = new HBox(10);
        badgesContainer.setAlignment(Pos.TOP_CENTER);
        badgesContainer.setStyle("-fx-padding: 10;");
        badgesContainer.setMouseTransparent(true); 

        if (produto.isOrganico()) {
            Label organicBadge = criarBadge("ORGÂNICO", "#4CAF50");
            badgesContainer.getChildren().add(organicBadge);
        }

        if (produto.isSustentavel()) {
            Label sustainableBadge = criarBadge("SUSTENTÁVEL", "#2196F3");
            badgesContainer.getChildren().add(sustainableBadge);
        }

        Label freshBadge = criarBadge("FRESCO", "#FF9800");
        badgesContainer.getChildren().add(freshBadge);

        StackPane imageWithBadges = new StackPane();
        imageWithBadges.getChildren().addAll(imageSection, badgesContainer);

        VBox infoSection = new VBox(12);
        infoSection.setStyle("-fx-padding: 20; -fx-background-color: rgba(255,255,255,0.02);");
        infoSection.setPrefSize(300, 240);
        infoSection.setAlignment(Pos.TOP_CENTER);

        Label nomeLabel = new Label(produto.getNome());
        nomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        nomeLabel.setWrapText(true);
        nomeLabel.setMaxWidth(260);
        
        HBox farmerBox = new HBox(8);
        farmerBox.setAlignment(Pos.CENTER);
        Label farmerIcon = new Label("👨‍🌾");
        Label farmerLabel = new Label(produto.getAgricultorNome());
        farmerLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 14px;");
        farmerBox.getChildren().addAll(farmerIcon, farmerLabel);

        HBox metricsBox = new HBox(20);
        metricsBox.setAlignment(Pos.CENTER);
        metricsBox.setStyle("-fx-padding: 10 0 0 0;");

        VBox ratingBox = criarMetricaBox("⭐", 
            String.format("%.1f", produto.getClassificacaoMedia()), 
            "Avaliação", "#FFD700");
        
        double distanciaProduto = calcularDistanciaParaProduto(produto);
        String distanciaTexto = distanciaProduto > 0 ? 
            String.format("%.1f km", distanciaProduto) : "--- km";
        
        VBox distanceBox = criarMetricaBox("📍", 
            distanciaTexto,
            "Distância", "#b3e5d1");
        
        VBox stockBox = criarMetricaBox("", 
            produto.getQuantidadeDisponivel() + " " + produto.getUnidadeMedida(), 
            "Stock", "#4CAF50");

        metricsBox.getChildren().addAll(ratingBox, distanceBox, stockBox);

        Label priceLabel = new Label(String.format("%.2f MT", produto.getPreco()));
        priceLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 22px; -fx-font-weight: bold;");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-padding: 15 0 0 0;");

        Button btnAdicionarCarrinho = criarBotao("🛒 ADICIONAR", 
            "-fx-background-color: linear-gradient(to right, #4CAF50, #45a049); -fx-text-fill: white;");
        btnAdicionarCarrinho.setOnAction(e -> mostrarDialogoQuantidade(produto));

        Button btnContactar = criarBotao("💬 DETALHES", 
            "-fx-background-color: transparent; -fx-text-fill: #b3e5d1; -fx-border-color: #b3e5d1;");
        btnContactar.setOnAction(e -> mostrarDetalhesModerno(produto, index));

        buttonBox.getChildren().addAll(btnAdicionarCarrinho, btnContactar);

        infoSection.getChildren().addAll(nomeLabel, farmerBox, metricsBox, priceLabel, buttonBox);
        
        content.getChildren().addAll(imageWithBadges, infoSection);
        cardContainer.getChildren().add(content);
        card.getChildren().add(cardContainer);

        configurarEfeitosHover(card);
        card.setOnMouseClicked(e -> onCardSelecionado(produto, index));

        return card;
    }

    private String obterEmojiPorCategoria(String categoria) {
        if (categoria == null) return "🌱";
        
        switch(categoria.toLowerCase()) {
            case "frutas": return "🍎";
            case "legumes": return "🥦";
            case "verduras": return "🥬";
            case "grãos": return "🌾";
            case "tubérculos": return "🥔";
            case "leguminosas": return "🫘";
            case "hortaliças": return "🥕";
            default: return "🌱";
        }
    }

    private Label criarBadge(String texto, String cor) {
        Label badge = new Label(texto);
        badge.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; -fx-font-size: 10px; " +
                      "-fx-font-weight: bold; -fx-padding: 4 8 4 8; -fx-background-radius: 12;");
        return badge;
    }

    private VBox criarMetricaBox(String icone, String valor, String label, String cor) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label(icone);
        Label valueLabel = new Label(valor);
        valueLabel.setStyle("-fx-text-fill: " + cor + "; -fx-font-size: 13px;");
        
        Label descLabel = new Label(label);
        descLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 10px;");
        
        box.getChildren().addAll(iconLabel, valueLabel, descLabel);
        return box;
    }

    private Button criarBotao(String texto, String estilo) {
        Button btn = new Button(texto);
        btn.setStyle(estilo + " -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 20; " +
                   "-fx-border-radius: 20; -fx-padding: 10 20 10 20; -fx-font-size: 12px;");
        return btn;
    }

    private void configurarEfeitosHover(VBox card) {
        Scale scale = new Scale(1, 1);
        card.setScaleX(1);
        card.setScaleY(1);

        card.setOnMouseEntered(e -> {
            card.setEffect(new DropShadow(30, Color.rgb(76, 175, 80, 0.6)));
            scale.setX(1.02);
            scale.setY(1.02);
            card.getTransforms().setAll(scale);
        });

        card.setOnMouseExited(e -> {
            card.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.3)));
            scale.setX(1.0);
            scale.setY(1.0);
            card.getTransforms().setAll(scale);
        });
    }

    @FXML
    private void onCardSelecionado(Produto produto, int index) {
        produtoSelecionadoId = "agricultor_" + produto.getAgricultorId();
        
        if (webMapService != null) {
            webMapService.focarNoAgricultor(produtoSelecionadoId);
        }
        
        calcularEAtualizarDistancia(produto);
        
        btnRotas.setDisable(false);
    }

    private void calcularEAtualizarDistancia(Produto produto) {
        try {
            Agricultor agricultor = buscarAgricultorPorId(produto.getAgricultorId());
            if (agricultor != null && agricultor.getLatitude() != 0 && agricultor.getLongitude() != 0) {
                
                double distancia = calcularDistancia(
                    userLatitude, userLongitude,
                    agricultor.getLatitude(), agricultor.getLongitude()
                );
                
                lblDistancia.setText(String.format("%.1f km", distancia));
                lblTempoViagem.setText(calcularTempoViagem(distancia) + " min");
                lblEndereco.setText(agricultor.getNome() + " • " + 
                                  agricultor.getBairro() + ", " + agricultor.getDistrito());
                
                System.out.println("📏 Distância calculada: " + distancia + " km");
            } else {
                lblDistancia.setText("--- km");
                lblTempoViagem.setText("--- min");
                lblEndereco.setText(produto.getAgricultorNome() + " • Produtor Agrícola");
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao calcular distância: " + e.getMessage());
        }
    }

    @FXML
    private void onCalcularRotas() {
        if (produtoSelecionadoId != null && webMapService != null) {
            webMapService.calcularRotaParaAgricultor(produtoSelecionadoId);
            mostrarAlertaPremium("Rota Calculada", 
                "Rota calculada com sucesso no mapa real!\n\n" +
                "📍 Distância: " + lblDistancia.getText() + "\n" +
                "Tempo estimado: " + lblTempoViagem.getText());
        } else {
            mostrarAlertaPremium("Atenção", "Selecione um produto primeiro!");
        }
    }
    
    @FXML
    private void onVoltarClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard_Cliente.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaPremium("Erro", "Não foi possível voltar: " + e.getMessage());
        }
    }

    private void mostrarDetalhesModerno(Produto produto, int index) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalhes do Produto");
        
        ButtonType btnFechar = new ButtonType("Fechar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(btnFechar);

        VBox mainContainer = new VBox(0);
        mainContainer.setStyle("-fx-background-color: #1a1a1a;");
        mainContainer.setPrefWidth(600);

        HBox headerBox = new HBox();
        headerBox.setStyle("-fx-background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); -fx-padding: 30 40;");
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(20);

        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 15;");
        imageContainer.setPrefSize(120, 120);

        if (produto.getImagemPrincipal() != null && !produto.getImagemPrincipal().isEmpty()) {
            try {
                File imageFile = new File(produto.getImagemPrincipal());
                if (imageFile.exists()) {
                    ImageView img = new ImageView(new Image(imageFile.toURI().toString()));
                    img.setFitWidth(110);
                    img.setFitHeight(110);
                    img.setPreserveRatio(true);
                    Circle clip = new Circle(55, 55, 55);
                    img.setClip(clip);
                    imageContainer.getChildren().add(img);
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                Label emoji = new Label(obterEmojiPorCategoria(produto.getCategoria()));
                emoji.setStyle("-fx-font-size: 48px;");
                imageContainer.getChildren().add(emoji);
            }
        } else {
            Label emoji = new Label(obterEmojiPorCategoria(produto.getCategoria()));
            emoji.setStyle("-fx-font-size: 48px;");
            imageContainer.getChildren().add(emoji);
        }

        VBox headerInfo = new VBox(8);
        Label nomeProduto = new Label(produto.getNome());
        nomeProduto.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label categoria = new Label(produto.getCategoria());
        categoria.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 14px;");
        
        HBox precoBox = new HBox(10);
        precoBox.setAlignment(Pos.CENTER_LEFT);
        Label preco = new Label(String.format("%.2f MT", produto.getPreco()));
        preco.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 28px; -fx-font-weight: bold;");
        Label unidade = new Label("/ " + produto.getUnidadeMedida());
        unidade.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 14px;");
        precoBox.getChildren().addAll(preco, unidade);

        headerInfo.getChildren().addAll(nomeProduto, categoria, precoBox);
        headerBox.getChildren().addAll(imageContainer, headerInfo);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #1a1a1a; -fx-background-color: #1a1a1a;");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        VBox contentBox = new VBox(20);
        contentBox.setStyle("-fx-padding: 30 40; -fx-background-color: #1a1a1a;");

        VBox agricultorSection = criarSecaoDetalhe("👨‍🌾 Informações do Produtor", 
            "Agricultor: " + produto.getAgricultorNome(),
            "Localização: " + (buscarAgricultorPorId(produto.getAgricultorId()) != null ? 
                buscarAgricultorPorId(produto.getAgricultorId()).getBairro() + ", " + 
                buscarAgricultorPorId(produto.getAgricultorId()).getDistrito() : "Não disponível")
        );

        VBox estoqueSec = criarSecaoDetalhe("Disponibilidade", 
            "Estoque: " + produto.getQuantidadeDisponivel() + " " + produto.getUnidadeMedida(),
            "Status: " + (produto.getQuantidadeDisponivel() > 0 ? "Disponível" : "Esgotado")
        );

        VBox qualidadeSection = criarSecaoDetalhe("🌟 Qualidade e Certificações", 
            (produto.isOrganico() ? " Produto Orgânico Certificado" : "Produção Convencional"),
            (produto.isSustentavel() ? "Práticas Sustentáveis" : "Produção Regular"),
            "⭐ Avaliação: " + String.format("%.1f/5.0", produto.getClassificacaoMedia()) + 
                " (" + produto.getTotalAvaliacoes() + " avaliações)"
        );

        VBox descricaoSection = new VBox(10);
        descricaoSection.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 15; -fx-padding: 20;");
        
        Label descTitulo = new Label("📝 Descrição");
        descTitulo.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label descTexto = new Label(produto.getDescricao() != null && !produto.getDescricao().isEmpty() ? 
            produto.getDescricao() : "Sem descrição disponível");
        descTexto.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 14px; -fx-wrap-text: true;");
        descTexto.setWrapText(true);
        descTexto.setMaxWidth(500);
        
        descricaoSection.getChildren().addAll(descTitulo, descTexto);

        double distancia = calcularDistanciaParaProduto(produto);
        VBox distanciaSection = criarSecaoDetalhe("📍 Informações de Entrega", 
            "Distância: " + (distancia > 0 ? String.format("%.1f km", distancia) : "Calcular"),
            "Tempo estimado: " + (distancia > 0 ? calcularTempoViagem(distancia) + " min" : "---")
        );

        contentBox.getChildren().addAll(
            agricultorSection,
            estoqueSec,
            qualidadeSection,
            descricaoSection,
            distanciaSection
        );

        scrollPane.setContent(contentBox);
        mainContainer.getChildren().addAll(headerBox, scrollPane);

        dialog.getDialogPane().setContent(mainContainer);
        dialog.getDialogPane().setStyle("-fx-background-color: #1a1a1a;");

        dialog.showAndWait();
    }

    private VBox criarSecaoDetalhe(String titulo, String... itens) {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 15; -fx-padding: 20;");
        
        Label tituloLabel = new Label(titulo);
        tituloLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 16px; -fx-font-weight: bold;");
        section.getChildren().add(tituloLabel);
        
        for (String item : itens) {
            Label itemLabel = new Label(item);
            itemLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 14px;");
            itemLabel.setWrapText(true);
            section.getChildren().add(itemLabel);
        }
        
        return section;
    }

    private void filtrarProdutos() {
        String pesquisa = txtPesquisa.getText().toLowerCase();
        String categoria = comboCategoria.getValue();
        
        try {
            List<Produto> produtosFiltrados;
            
            if (categoria.equals("🌿 Todos") && pesquisa.isEmpty()) {
                produtosFiltrados = produtoService.listarProdutosDisponiveis();
            } else if (!pesquisa.isEmpty()) {
                produtosFiltrados = produtoService.buscarProdutosPorNome(pesquisa);
            } else if (!categoria.equals("🌿 Todos")) {
                produtosFiltrados = produtoService.buscarProdutosPorCategoria(categoria);
            } else {
                produtosFiltrados = produtoService.listarProdutosDisponiveis();
            }
            
            containerProdutos.getChildren().clear();
            for (int i = 0; i < produtosFiltrados.size(); i++) {
                VBox card = criarCardProduto(produtosFiltrados.get(i), i);
                containerProdutos.getChildren().add(card);
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao filtrar produtos: " + e.getMessage());
        }
    }

    private void ordenarProdutos() {
        String ordenacao = comboOrdenacao.getValue();
        
        if ("📍 Mais Próximo".equals(ordenacao)) {
            try {
                List<Produto> produtos = produtoService.listarProdutosDisponiveis();
                
                produtos.sort((p1, p2) -> {
                    double dist1 = calcularDistanciaParaProduto(p1);
                    double dist2 = calcularDistanciaParaProduto(p2);
                    return Double.compare(dist1, dist2);
                });
                
                containerProdutos.getChildren().clear();
                for (int i = 0; i < produtos.size(); i++) {
                    VBox card = criarCardProduto(produtos.get(i), i);
                    containerProdutos.getChildren().add(card);
                }
                
                System.out.println("📏 Produtos ordenados por distância");
            } catch (Exception e) {
                System.err.println("Erro ao ordenar por distância: " + e.getMessage());
            }
        } else {
            System.out.println("Ordenando por: " + ordenacao);
        }
    }

    private void filtrarPorDistancia() {
        String distancia = comboDistancia.getValue();
        System.out.println("Filtrando por distância: " + distancia);
        
        try {
            List<Produto> produtos = produtoService.listarProdutosDisponiveis();
            List<Produto> produtosFiltrados = new java.util.ArrayList<>();
            
            for (Produto produto : produtos) {
                double distanciaReal = calcularDistanciaParaProduto(produto);
                
                if (distancia.equals("📍 Todos") || 
                    (distancia.equals("📏 5 km") && distanciaReal <= 5) ||
                    (distancia.equals("📏 10 km") && distanciaReal <= 10) ||
                    (distancia.equals("📏 15 km") && distanciaReal <= 15) ||
                    (distancia.equals("📏 20 km") && distanciaReal <= 20)) {
                    
                    produtosFiltrados.add(produto);
                }
            }
            
            containerProdutos.getChildren().clear();
            for (int i = 0; i < produtosFiltrados.size(); i++) {
                VBox card = criarCardProduto(produtosFiltrados.get(i), i);
                containerProdutos.getChildren().add(card);
            }
            
            System.out.println("📏 Filtro aplicado: " + produtosFiltrados.size() + " produtos");
            
        } catch (Exception e) {
            System.err.println("Erro ao filtrar por distância: " + e.getMessage());
        }
    }

    private void mostrarAlertaPremium(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: rgba(45,45,45,0.95); -fx-border-color: #4CAF50;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        
        alert.showAndWait();
    }
}
        