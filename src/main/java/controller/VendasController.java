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
import service.MapaService;
import service.ProdutoService;
import service.SessaoActual;
import service.UsuarioService;

public class VendasController implements Initializable {

    @FXML private TextField txtPesquisa;
    @FXML private ComboBox<String> comboCategoria, comboOrdenacao, comboDistancia;
    @FXML private FlowPane containerProdutos;
    @FXML private Label lblDistancia, lblTempoViagem, lblEndereco;
    @FXML private Button btnRotas;
    @FXML private SplitPane splitPane;
    @FXML private WebView webViewMapa;

    private String produtoSelecionadoId;
    private MapaService webMapService;
    private ProdutoService produtoService;
    private UsuarioService usuarioService;

    private double userLatitude = -25.9692; // Maputo padrão
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
        // Inicializar serviços
        this.produtoService = new ProdutoService();
        this.usuarioService = new UsuarioService();
        
        carregarLocalizacaoComprador();
        
        configurarComponentesPremium();
        inicializarMapaReal();
        carregarProdutosPremium();
        configurarEfeitosInterativos();
    }

    private void inicializarMapaReal() {
        try {
            System.out.println("Inicializando mapa real com OpenStreetMap...");
            
            // Configuracao WebView
            webViewMapa.setContextMenuEnabled(false);
            webViewMapa.setFocusTraversable(true);
            
            webMapService = new MapaService(webViewMapa.getEngine());
            
            // Configuracao callback quando mapa estiver pronto
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
            // 🎯 BUSCAR DIRETO DA CLASSE ESTÁTICA
            Comprador comprador = SessaoActual.getCompradorLogado();
            
            if (comprador != null) {
                userLatitude = comprador.getLatitude();
                userLongitude = comprador.getLongitude();
                
                System.out.println("📍 Localização carregada: " + comprador.getNome() + 
                                 " (" + userLatitude + ", " + userLongitude + ")");
            } else {
                // Fallback para Maputo
                userLatitude = -25.9692;
                userLongitude = 32.5732;
                System.out.println("⚠️ Usando coordenadas padrão");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro: " + e.getMessage());
            userLatitude = -25.9692;
            userLongitude = 32.5732;
        }
    }

    private void adicionarAgricultoresReaisNoMapa() {
        try {
            // Buscar do banco de dados
            List<Agricultor> agricultores = usuarioService.listarAgricultores();
            
            if (agricultores.isEmpty()) {
                System.out.println("Nenhum agricultor encontrado no banco de dados");
                return;
            }
            
            // 🎯 DEFINIR LOCALIZACAO DO USUÁRIO
            webMapService.definirLocalizacaoUsuario(userLatitude, userLongitude);
            
            // 🎯 ADICIONAR  AGRICULTOR  
            for (Agricultor agricultor : agricultores) {
                double lat = agricultor.getLatitude();
                double lng = agricultor.getLongitude();
                
                // Verificar se tem coordenadas válidas
                if (lat != 0 && lng != 0) {
                    // Buscar produtos deste agricultor
                    List<Produto> produtosAgricultor = produtoService.buscarProdutosPorAgricultor(agricultor.getId());
                    String produtoPrincipal = produtosAgricultor.isEmpty() ? 
                        "Produtos Agrícolas" : produtosAgricultor.get(0).getNome();
                    
                    // 🎯 ADICIONAR NO MAPA COM COORDENADAS 
                    adicionarAgricultorNoMapa(agricultor, produtoPrincipal);
                    
                    System.out.println("✅ Agricultor adicionado: " + agricultor.getNome() + 
                                     " (" + lat + ", " + lng + ")");
                } else {
                    System.out.println("⚠️ Agricultor sem coordenadas: " + agricultor.getNome());
                }
            }
            
            System.out.println("🎉 " + agricultores.size() + " agricultores reais processados no mapa");
            
        } catch (Exception e) {
            System.err.println("Erro ao adicionar agricultores reais no mapa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🎯 NOVO MÉTODO: Adicionar agricultor no mapa com coordenadas reais
    private void adicionarAgricultorNoMapa(Agricultor agricultor, String produtoPrincipal) {
        try {
            // Usar o método existente do MapaService
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

    // 🎯 NOVO MÉTODO: Calcular distância entre duas coordenadas (em km)
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raio da Terra em km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    // 🎯 NOVO MÉTODO: Calcular distância para um produto específico
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

    // 🎯 NOVO MÉTODO: Buscar agricultor por ID (alternativo sem modificar service)
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

    // 🎯 NOVO MÉTODO: Calcular tempo de viagem aproximado
    private String calcularTempoViagem(double distancia) {
        // Fórmula simples: 2 minutos por km em cidade + 10 minutos base
        int tempo = (int) (distancia * 2 + 10);
        return String.valueOf(tempo);
    }

    private void configurarComponentesPremium() {
        // Buscar categorias reais dos produtos
        List<Produto> produtos = produtoService.listarTodosProdutos();
        comboCategoria.getItems().add("🌿 Todos");
        
        // Adicionar categorias únicas dos produtos reais
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

    // 🎯 SEÇÃO DA IMAGEM - FULL SIZE (180px de altura)
    StackPane imageSection = new StackPane();
    imageSection.setPrefHeight(180);
    imageSection.setAlignment(Pos.CENTER);
    imageSection.setStyle("-fx-background-radius: 20 20 0 0;"); // Cantos arredondados apenas no topo

    // 🎯 VERIFICAR SE TEM IMAGEM
    if (produto.getImagemPrincipal() != null && !produto.getImagemPrincipal().isEmpty()) {
        try {
            File imageFile = new File(produto.getImagemPrincipal());
            
            if (imageFile.exists() && imageFile.isFile()) {
                // 🎯 IMAGEM FULL SIZE - preenche toda a seção
                ImageView productImage = new ImageView();
                
                // Configurar para preencher o espaço todo
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
                System.out.println("✅ Imagem FULL SIZE carregada: " + produto.getNome());
                
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
            System.out.println("⚠️ Fallback para gradiente: " + e.getMessage());
        }
    } else {
        // 🎯 SEM IMAGEM: Gradiente sutil
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

    // 🎯 BADGES 
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

    // 🎯 CONTAINER COM IMAGEM E BADGES
    StackPane imageWithBadges = new StackPane();
    imageWithBadges.getChildren().addAll(imageSection, badgesContainer);

    // Seção de informações (parte inferior do card)
    VBox infoSection = new VBox(12);
    infoSection.setStyle("-fx-padding: 20; -fx-background-color: rgba(255,255,255,0.02);");
    infoSection.setPrefSize(300, 240); // 420 total - 180 imagem = 240 para info
    infoSection.setAlignment(Pos.TOP_CENTER);

    Label nomeLabel = new Label(produto.getNome());
    nomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
    nomeLabel.setWrapText(true);
    nomeLabel.setMaxWidth(260);
    
    // Agricultor
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
    
    VBox stockBox = criarMetricaBox("📦", 
        produto.getQuantidadeDisponivel() + " " + produto.getUnidadeMedida(), 
        "Stock", "#4CAF50");

    metricsBox.getChildren().addAll(ratingBox, distanceBox, stockBox);

    // Preço real
    Label priceLabel = new Label(String.format("%.2f MT", produto.getPreco()));
    priceLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 22px; -fx-font-weight: bold;");

    // Botões de ação
    HBox buttonBox = new HBox(15);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.setStyle("-fx-padding: 15 0 0 0;");

    Button btnEncomendar = criarBotao("🛒 ENCOMENDAR", 
        "-fx-background-color: linear-gradient(to right, #4CAF50, #45a049); -fx-text-fill: white;");
    btnEncomendar.setOnAction(e -> onEncomendarClick(produto, index));

    Button btnContactar = criarBotao("💬 DETALHES", 
        "-fx-background-color: transparent; -fx-text-fill: #b3e5d1; -fx-border-color: #b3e5d1;");
    btnContactar.setOnAction(e -> onContactarClick(produto, index));

    buttonBox.getChildren().addAll(btnEncomendar, btnContactar);

    infoSection.getChildren().addAll(nomeLabel, farmerBox, metricsBox, priceLabel, buttonBox);
    
    // 🎯 CONTEÚDO FINAL: Imagem + Informações
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
            card.setEffect(new DropShadow(30, Color.rgb(76, 175, 80, 0.4)));
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
        
        // 🎯 ATUALIZADO: Calcular distância real
        calcularEAtualizarDistancia(produto);
        
        btnRotas.setDisable(false);
    }

    // 🎯 NOVO MÉTODO: Calcular e atualizar distância na interface
    private void calcularEAtualizarDistancia(Produto produto) {
        try {
            Agricultor agricultor = buscarAgricultorPorId(produto.getAgricultorId());
            if (agricultor != null && agricultor.getLatitude() != 0 && agricultor.getLongitude() != 0) {
                
                double distancia = calcularDistancia(
                    userLatitude, userLongitude,
                    agricultor.getLatitude(), agricultor.getLongitude()
                );
                
                // Atualizar labels com informações reais
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
            mostrarAlertaPremium("🗺️ Rota Calculada", 
                "Rota calculada com sucesso no mapa real!\n\n" +
                "📍 Distância: " + lblDistancia.getText() + "\n" +
                "Tempo estimado: " + lblTempoViagem.getText());
        } else {
            mostrarAlertaPremium("⚠️ Atenção", "Selecione um produto primeiro!");
        }
    }

    @FXML
    private void onEncomendarClick(Produto produto, int index) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/carrinho.fxml"));
            Parent root = loader.load();

            // ✅ Agora usando o controller correto para a tela de Vendas
            CarrinhoController controller = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("GreenMatch - Mercado"); // ✅ Título correto
            stage.setScene(scene);

            // Configurações adicionais da janela
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setMaximized(true); // Abrir maximizado

            stage.show();

            // Fechar o dashboard atual se desejar
            // ((Stage) lblSaudacao.getScene().getWindow()).close();

        } catch (Exception e) {
            System.err.println("❌ Erro ao abrir mercado: " + e.getMessage());
            mostrarAlertaPremium("Erro", "Não foi possível abrir o mercado: " + e.getMessage());
        }
    }

    @FXML
    private void onContactarClick(Produto produto, int index) {
        String tipoAgricultura = produto.isOrganico() ? "🌿 Produto Orgânico Certificado" : "🏭 Produção Convencional";
        String sustentabilidade = produto.isSustentavel() ? "✅ Práticas Sustentáveis" : "🔍 Produção Regular";
        
        mostrarAlertaPremium("💬 Detalhes do Produto", 
            "**" + produto.getNome() + "**\n\n" +
            "👨‍🌾 **Agricultor:** " + produto.getAgricultorNome() + "\n" +
            "⭐ **Avaliação:** " + String.format("%.1f/5.0", produto.getClassificacaoMedia()) + "\n" +
            "📊 **Total de Avaliações:** " + produto.getTotalAvaliacoes() + "\n" +
            "📦 **Disponível:** " + produto.getQuantidadeDisponivel() + " " + produto.getUnidadeMedida() + "\n" +
            "🏷️ **Categoria:** " + produto.getCategoria() + "\n" +
            "📝 **Descrição:** " + (produto.getDescricao() != null ? produto.getDescricao() : "Sem descrição disponível") + "\n" +
            tipoAgricultura + "\n" +
            sustentabilidade);
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
            
            // Atualizar a exibição
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
        
        // 🎯 ATUALIZADO: Ordenação por distância real
        if ("📍 Mais Próximo".equals(ordenacao)) {
            try {
                List<Produto> produtos = produtoService.listarProdutosDisponiveis();
                
                // Ordenar por distância calculada
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
                
                // Aplicar filtro baseado na distância selecionada
                if (distancia.equals("📍 Todos") || 
                    (distancia.equals("📏 5 km") && distanciaReal <= 5) ||
                    (distancia.equals("📏 10 km") && distanciaReal <= 10) ||
                    (distancia.equals("📏 15 km") && distanciaReal <= 15) ||
                    (distancia.equals("📏 20 km") && distanciaReal <= 20)) {
                    
                    produtosFiltrados.add(produto);
                }
            }
            
            // Atualizar exibição
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