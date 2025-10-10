package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebView;

import model.Localizacao;
import service.MapaService;

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
        configurarComponentesPremium();
        inicializarMapaReal();
        carregarProdutosPremium();
        configurarEfeitosInterativos();
    }

    private void inicializarMapaReal() {
        try {
            System.out.println("Inicializando mapa real com OpenStreetMap...");
            
            // Configurar WebView
            webViewMapa.setContextMenuEnabled(false);
            webViewMapa.setFocusTraversable(true);
            
            webMapService = new MapaService(webViewMapa.getEngine());
            
            // Configurar callback quando mapa estiver pronto
            webMapService.quandoMapaPronto(status -> {
                System.out.println("" + status);
                adicionarDadosExemploMapaReal();
            });
            
            System.out.println("Serviço de mapa real inicializado!");
            
        } catch (Exception e) {
            System.err.println("Erro crítico no mapa real: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void adicionarDadosExemploMapaReal() {
        webMapService.definirLocalizacaoUsuario(-25.9692, 32.5732);
        
        Localizacao[] localizacoes = {
            new Localizacao(-25.9680, 32.5800, "João Silva - Matola"),
            new Localizacao(-25.9600, 32.5900, "Maria Santos - Maputo Centro"),
            new Localizacao(-25.9550, 32.5750, "Carlos Oliveira - Costa do Sol"),
            new Localizacao(-25.9750, 32.5650, "Ana Pereira - Machava"),
            new Localizacao(-25.9500, 32.6000, "Pedro Costa - Zimpeto"),
            new Localizacao(-25.9650, 32.5550, "Luisa Fernandes - Boane"),
            new Localizacao(-25.9400, 32.5700, "Miguel Souza - Marracuene"),
            new Localizacao(-25.9850, 32.5850, "Elena Gomes - Matola Rio")
        };
        
        String[] produtos = {
            "Tomate Orgânico", "Alface Crespa", "Cenoura Fresca", "Batata Doce",
            "Maçã Verde", "Banana Prata", "Abacate Maduro", "Repolho Roxo"
        };
        
        for (int i = 0; i < localizacoes.length; i++) {
            String nome = localizacoes[i].getEndereco().split(" - ")[0];
            webMapService.adicionarMarcadorAgricultor(
                "agricultor_" + i,
                localizacoes[i],
                nome,
                produtos[i]
            );
        }
        
        System.out.println("" + localizacoes.length + " agricultores adicionados ao mapa real");
    }

    private void configurarComponentesPremium() {
        comboCategoria.getItems().addAll("🌿 Todos", "🍎 Frutas", "🥦 Legumes", "🥬 Verduras", "🌾 Grãos", "✅ Orgânicos");
        comboCategoria.setValue("🌿 Todos");
        
        comboOrdenacao.getItems().addAll("📍 Mais Próximo", "Mais Barato", "Melhor Avaliado", "Mais Recente");
        comboOrdenacao.setValue("📍 Mais Próximo");
        
        comboDistancia.getItems().addAll("Todos", "📏 5 km", "📏 10 km", "📏 15 km", "📏 20 km");
        comboDistancia.setValue("📏 10 km");

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
        containerProdutos.getChildren().clear();

        Produto[] produtos = {
            new Produto("Tomate Orgânico", "95 MT/kg", "João Silva", 2.5, 4.8, 25, "🍅", true, "Colhido hoje"),
            new Produto("Alface Crespa", "55 MT/un", "Maria Santos", 1.8, 4.5, 18, "🥬", true, "Fresca da horta"),
            new Produto("Cenoura Fresca", "75 MT/kg", "Carlos Oliveira", 3.2, 4.9, 32, "🥕", true, "Colhida pela manhã"),
            new Produto("Batata Doce", "65 MT/kg", "Ana Pereira", 4.1, 4.2, 15, "🍠", false, "Produção familiar"),
            new Produto("Maçã Verde", "125 MT/kg", "Pedro Costa", 5.5, 4.7, 22, "🍏", true, "Importada selecionada"),
            new Produto("Banana Prata", "60 MT/kg", "Luisa Fernandes", 2.1, 4.6, 28, "🍌", true, "Cacho maduro"),
            new Produto("Abacate Maduro", "110 MT/un", "Miguel Souza", 6.3, 4.3, 12, "🥑", false, "Pronto para consumo"),
            new Produto("Repolho Roxo", "70 MT/un", "Elena Gomes", 3.8, 4.8, 20, "🥬", true, "Colhido sob pedido")
        };

        for (int i = 0; i < produtos.length; i++) {
            VBox card = criarCardProduto(produtos[i], i);
            containerProdutos.getChildren().add(card);
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

        // Seção da imagem
        StackPane imageSection = new StackPane();
        imageSection.setPrefHeight(180);
        imageSection.setAlignment(Pos.CENTER);

        Rectangle gradientBackground = new Rectangle(300, 180);
        gradientBackground.setFill(cardGradients[index % cardGradients.length]);
        gradientBackground.setArcWidth(20);
        gradientBackground.setArcHeight(20);

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(140, 140);
        imageContainer.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 70;");

        Label productIcon = new Label(produto.getEmoji());
        productIcon.setStyle("-fx-font-size: 50px; -fx-text-fill: #333;");

        imageContainer.getChildren().add(productIcon);
        imageSection.getChildren().addAll(gradientBackground, imageContainer);

        // Badges
        HBox badgesContainer = new HBox(10);
        badgesContainer.setAlignment(Pos.TOP_CENTER);
        badgesContainer.setStyle("-fx-padding: 10 0 0 0;");

        if (produto.isOrganico()) {
            Label organicBadge = criarBadge("ORGÂNICO", "#4CAF50");
            badgesContainer.getChildren().add(organicBadge);
        }

        Label freshBadge = criarBadge("FRESCO", "#2196F3");
        badgesContainer.getChildren().add(freshBadge);

        // Seção de informações
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
        Label farmerLabel = new Label(produto.getAgricultor());
        farmerLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 14px;");
        farmerBox.getChildren().addAll(farmerIcon, farmerLabel);

        HBox metricsBox = new HBox(20);
        metricsBox.setAlignment(Pos.CENTER);
        metricsBox.setStyle("-fx-padding: 10 0 0 0;");

        VBox ratingBox = criarMetricaBox("⭐", String.format("%.1f", produto.getAvaliacao()), "Avaliação", "#FFD700");
        VBox distanceBox = criarMetricaBox("📍", String.format("%.1fkm", produto.getDistancia()), "Distância", "#b3e5d1");
        VBox stockBox = criarMetricaBox("📦", produto.getDisponibilidade() + " un", "Stock", "#4CAF50");

        metricsBox.getChildren().addAll(ratingBox, distanceBox, stockBox);

        Label priceLabel = new Label(produto.getPreco());
        priceLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 22px; -fx-font-weight: bold;");

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
        content.getChildren().addAll(imageSection, badgesContainer, infoSection);
        cardContainer.getChildren().add(content);
        card.getChildren().add(cardContainer);

        configurarEfeitosHover(card);
        card.setOnMouseClicked(e -> onCardSelecionado(produto, index));

        return card;
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
        produtoSelecionadoId = "agricultor_" + index;
        
        if (webMapService != null) {
            webMapService.focarNoAgricultor(produtoSelecionadoId);
        }
        
        Localizacao localizacao = obterLocalizacaoPorIndex(index);
        if (localizacao != null) {
            double distancia = calcularDistanciaUsuario(localizacao);
            lblDistancia.setText(String.format("%.1f km", distancia));
            lblTempoViagem.setText(String.format("%.0f min", distancia * 2.5));
            lblEndereco.setText(localizacao.getEndereco());
        } else {
            lblDistancia.setText(String.format("%.1f km", produto.getDistancia()));
            lblTempoViagem.setText(String.format("%.0f min", produto.getDistancia() * 2.5));
            lblEndereco.setText(produto.getAgricultor() + " • Propriedade Agrícola");
        }
        
        btnRotas.setDisable(false);
    }

    private Localizacao obterLocalizacaoPorIndex(int index) {
        Localizacao[] localizacoes = {
            new Localizacao(-25.9680, 32.5800, "João Silva - Matola"),
            new Localizacao(-25.9600, 32.5900, "Maria Santos - Maputo Centro"),
            new Localizacao(-25.9550, 32.5750, "Carlos Oliveira - Costa do Sol"),
            new Localizacao(-25.9750, 32.5650, "Ana Pereira - Machava"),
            new Localizacao(-25.9500, 32.6000, "Pedro Costa - Zimpeto"),
            new Localizacao(-25.9650, 32.5550, "Luisa Fernandes - Boane"),
            new Localizacao(-25.9400, 32.5700, "Miguel Souza - Marracuene"),
            new Localizacao(-25.9850, 32.5850, "Elena Gomes - Matola Rio")
        };
        return index < localizacoes.length ? localizacoes[index] : null;
    }

    private double calcularDistanciaUsuario(Localizacao destino) {
        Localizacao usuario = new Localizacao(-25.9692, 32.5732, "Usuário");
        return calcularDistancia(usuario, destino);
    }

    private double calcularDistancia(Localizacao origem, Localizacao destino) {
        double lat1 = Math.toRadians(origem.getLatitude());
        double lon1 = Math.toRadians(origem.getLongitude());
        double lat2 = Math.toRadians(destino.getLatitude());
        double lon2 = Math.toRadians(destino.getLongitude());
        
        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;
        
        double a = Math.sin(dlat/2) * Math.sin(dlat/2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dlon/2) * Math.sin(dlon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        return 6371 * c;
    }

    @FXML
    private void onCalcularRotas() {
        if (produtoSelecionadoId != null && webMapService != null) {
            webMapService.calcularRotaParaAgricultor(produtoSelecionadoId);
            mostrarAlertaPremium("🗺️ Rota Calculada", 
                "Rota calculada com sucesso no mapa real!\n\n" +
                "📍 Distância: " + lblDistancia.getText() + "\n" +
                "⏱️ Tempo estimado: " + lblTempoViagem.getText());
        } else {
            mostrarAlertaPremium("⚠️ Atenção", "Selecione um produto primeiro!");
        }
    }

    @FXML
    private void onEncomendarClick(Produto produto, int index) {
        mostrarAlertaPremium("🛒 Encomenda Realizada", 
            "Sua encomenda foi realizada com sucesso!\n\n" +
            "**Produto:** " + produto.getNome() + "\n" +
            "**Agricultor:** " + produto.getAgricultor() + "\n" +
            "**Preço:** " + produto.getPreco() + "\n\n" +
            "📞 O agricultor entrará em contacto em breve\n" +
            "🚚 Entrega estimada: 2-3 dias úteis");
    }

    @FXML
    private void onContactarClick(Produto produto, int index) {
        mostrarAlertaPremium("💬 Detalhes do Produto", 
            "**" + produto.getNome() + "**\n\n" +
            "👨‍🌾 **Agricultor:** " + produto.getAgricultor() + "\n" +
            "⭐ **Avaliação:** " + produto.getAvaliacao() + "/5.0\n" +
            "📍 **Distância:** " + produto.getDistancia() + " km\n" +
            "**Disponível:** " + produto.getDisponibilidade() + " unidades\n" +
            "🌿 **Tipo:** " + (produto.isOrganico() ? "Produto Orgânico Certificado" : "Produção Convencional"));
    }

    private void filtrarProdutos() {
        String pesquisa = txtPesquisa.getText().toLowerCase();
        String categoria = comboCategoria.getValue();
        String distancia = comboDistancia.getValue();
        
        System.out.println("🔍 Filtrando produtos...");
        System.out.println("   Pesquisa: " + pesquisa);
        System.out.println("   Categoria: " + categoria);
        System.out.println("   Distância: " + distancia);
    }

    private void ordenarProdutos() {
        String ordenacao = comboOrdenacao.getValue();
        System.out.println("📊 Ordenando por: " + ordenacao);
    }

    private void filtrarPorDistancia() {
        String distancia = comboDistancia.getValue();
        System.out.println("📍 Filtrando por distância: " + distancia);
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

    private static class Produto {
        private final String nome;
        private final String preco;
        private final String agricultor;
        private final double distancia;
        private final double avaliacao;
        private final int disponibilidade;
        private final String emoji;
        private final boolean organico;
        private final String estado;

        public Produto(String nome, String preco, String agricultor, double distancia, 
                             double avaliacao, int disponibilidade, String emoji, boolean organico, String estado) {
            this.nome = nome;
            this.preco = preco;
            this.agricultor = agricultor;
            this.distancia = distancia;
            this.avaliacao = avaliacao;
            this.disponibilidade = disponibilidade;
            this.emoji = emoji;
            this.organico = organico;
            this.estado = estado;
        }

        public String getNome() { return nome; }
        public String getPreco() { return preco; }
        public String getAgricultor() { return agricultor; }
        public double getDistancia() { return distancia; }
        public double getAvaliacao() { return avaliacao; }
        public int getDisponibilidade() { return disponibilidade; }
        public String getEmoji() { return emoji; }
        public boolean isOrganico() { return organico; }
        public String getEstado() { return estado; }
    }
}