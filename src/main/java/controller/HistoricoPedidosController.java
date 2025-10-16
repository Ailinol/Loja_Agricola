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
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Pedido;
import model.ItemPedido;
import model.Produto;
import model.Avaliacao;
import service.SessaoActual;
import dao.PedidoDAO;
import dao.AvaliacaoDAO;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class HistoricoPedidosController implements Initializable {

    @FXML private TextField txtPesquisa;
    @FXML private ComboBox<String> comboStatus, comboOrdenacao, comboPeriodo;
    @FXML private VBox containerPedidos;
    @FXML private ScrollPane scrollPedidos;

    private PedidoDAO pedidoDAO;
    private AvaliacaoDAO avaliacaoDAO;
    private List<Pedido> pedidos = new ArrayList<>();
    private List<Pedido> pedidosFiltrados = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.pedidoDAO = new PedidoDAO();
        this.avaliacaoDAO = new AvaliacaoDAO();
        
        configurarComponentes();
        carregarPedidos();
        configurarEfeitosInterativos();
    }

    private void configurarComponentes() {
        comboStatus.getItems().addAll("📊 Todos", "✅ Concluídos", "🔄 Em Andamento", "❌ Cancelados", "⏳ Pendentes");
        comboStatus.setValue("📊 Todos");
        
        comboOrdenacao.getItems().addAll("🕒 Mais Recente", "📅 Mais Antigo", "💵 Maior Valor", "💵 Menor Valor");
        comboOrdenacao.setValue("🕒 Mais Recente");
        
        comboPeriodo.getItems().addAll("📅 Todos", "📅 Últimos 7 dias", "📅 Último mês", "📅 Últimos 3 meses");
        comboPeriodo.setValue("📅 Todos");
    }

    private void configurarEfeitosInterativos() {
        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> aplicarFiltros());
        comboStatus.setOnAction(e -> aplicarFiltros());
        comboOrdenacao.setOnAction(e -> aplicarFiltros());
        comboPeriodo.setOnAction(e -> aplicarFiltros());
    }

    private void carregarPedidos() {
        try {
            int compradorId = SessaoActual.getCompradorLogado().getId();
            pedidos = pedidoDAO.buscarPedidosPorComprador(compradorId);
            pedidosFiltrados = new ArrayList<>(pedidos);
            
            aplicarFiltros();
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar pedidos: " + e.getMessage());
            e.printStackTrace();
            
            containerPedidos.getChildren().clear();
            Label lblErro = new Label("Erro ao carregar pedidos: " + e.getMessage());
            lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 14; -fx-padding: 20;");
            containerPedidos.getChildren().add(lblErro);
        }
    }

    private void aplicarFiltros() {
        if (pedidos == null || pedidos.isEmpty()) {
            exibirPedidos(new ArrayList<>());
            return;
        }

        pedidosFiltrados = new ArrayList<>(pedidos);

        // Filtro de pesquisa
        String termoPesquisa = txtPesquisa.getText().toLowerCase().trim();
        if (!termoPesquisa.isEmpty()) {
            pedidosFiltrados = pedidosFiltrados.stream()
                .filter(p -> {
                    // Pesquisa por ID do pedido
                    if (String.valueOf(p.getId()).contains(termoPesquisa)) {
                        return true;
                    }
                    // Pesquisa por nome de produtos
                    for (ItemPedido item : p.getItensPedidos()) {
                        if (item.getProduto().getNome().toLowerCase().contains(termoPesquisa)) {
                            return true;
                        }
                    }
                    // Pesquisa por valor
                    if (String.format("%.2f", p.getValorTotal()).contains(termoPesquisa)) {
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        }

        // Filtro de status
        String statusSelecionado = comboStatus.getValue();
        if (!statusSelecionado.equals("📊 Todos")) {
            pedidosFiltrados = pedidosFiltrados.stream()
                .filter(p -> {
                    String status = p.getStatus().toString();
                    if (statusSelecionado.contains("Concluídos") && status.equals("CONCLUIDO")) return true;
                    if (statusSelecionado.contains("Andamento") && status.equals("EM_ANDAMENTO")) return true;
                    if (statusSelecionado.contains("Cancelados") && status.equals("CANCELADO")) return true;
                    if (statusSelecionado.contains("Pendentes") && status.equals("PENDENTE")) return true;
                    return false;
                })
                .collect(Collectors.toList());
        }

        // Filtro de período
        String periodoSelecionado = comboPeriodo.getValue();
        if (!periodoSelecionado.equals("📅 Todos")) {
            LocalDateTime agora = LocalDateTime.now();
            pedidosFiltrados = pedidosFiltrados.stream()
                .filter(p -> {
                    LocalDateTime dataPedido = p.getDataHora();
                    if (periodoSelecionado.contains("7 dias")) {
                        return dataPedido.isAfter(agora.minusDays(7));
                    } else if (periodoSelecionado.contains("mês")) {
                        return dataPedido.isAfter(agora.minusMonths(1));
                    } else if (periodoSelecionado.contains("3 meses")) {
                        return dataPedido.isAfter(agora.minusMonths(3));
                    }
                    return true;
                })
                .collect(Collectors.toList());
        }

        // Ordenação
        String ordenacaoSelecionada = comboOrdenacao.getValue();
        if (ordenacaoSelecionada.contains("Mais Recente")) {
            pedidosFiltrados.sort((p1, p2) -> p2.getDataHora().compareTo(p1.getDataHora()));
        } else if (ordenacaoSelecionada.contains("Mais Antigo")) {
            pedidosFiltrados.sort((p1, p2) -> p1.getDataHora().compareTo(p2.getDataHora()));
        } else if (ordenacaoSelecionada.contains("Maior Valor")) {
            pedidosFiltrados.sort((p1, p2) -> Double.compare(p2.getValorTotal(), p1.getValorTotal()));
        } else if (ordenacaoSelecionada.contains("Menor Valor")) {
            pedidosFiltrados.sort((p1, p2) -> Double.compare(p1.getValorTotal(), p2.getValorTotal()));
        }

        exibirPedidos(pedidosFiltrados);
    }

    private void exibirPedidos(List<Pedido> pedidosParaExibir) {
        containerPedidos.getChildren().clear();

        if (pedidosParaExibir.isEmpty()) {
            VBox emptyBox = new VBox(15);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(60));
            
            Label icone = new Label("📦");
            icone.setStyle("-fx-font-size: 72px;");
            
            Label lblVazio = new Label("Nenhum pedido encontrado");
            lblVazio.setStyle("-fx-text-fill: #999; -fx-font-size: 18px; -fx-font-weight: bold;");
            
            Label lblDica = new Label("Tente ajustar os filtros de pesquisa");
            lblDica.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
            
            emptyBox.getChildren().addAll(icone, lblVazio, lblDica);
            containerPedidos.getChildren().add(emptyBox);
            return;
        }

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(20);
        flowPane.setVgap(20);
        flowPane.setAlignment(Pos.TOP_CENTER);
        flowPane.setPrefWrapLength(1000);
        flowPane.setPadding(new Insets(20));

        for (Pedido pedido : pedidosParaExibir) {
            VBox card = criarCardPedido(pedido);
            flowPane.getChildren().add(card); 
        }
        
        containerPedidos.getChildren().add(flowPane);
    }

    private VBox criarCardPedido(Pedido pedido) {
        VBox card = new VBox();
        card.setPrefWidth(450);
        card.setMaxWidth(450);
        card.setAlignment(Pos.TOP_CENTER);
        card.setSpacing(0);
        
        // Sombra inicial suave
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(15);
        shadow.setSpread(0.2);
        card.setEffect(shadow);

        StackPane cardContainer = new StackPane();
        cardContainer.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #2a2a2a, #1e1e1e);" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #4CAF50;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 15;"
        );
        cardContainer.setPrefSize(450, 400);

        VBox content = new VBox();
        content.setPrefSize(450, 400);
        content.setAlignment(Pos.TOP_LEFT);
        content.setSpacing(0);

        // Header
        HBox headerBox = new HBox(20);
        headerBox.setStyle(
            "-fx-background-color: linear-gradient(to right, #4CAF50, #45a049);" +
            "-fx-background-radius: 15 15 0 0;" +
            "-fx-padding: 20;"
        );
        headerBox.setPrefHeight(80);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox infoHeader = new VBox(5);
        Label lblNumero = new Label("Pedido #" + pedido.getId());
        lblNumero.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        Label lblData = new Label("📅 " + pedido.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblData.setStyle(
            "-fx-text-fill: rgba(255, 255, 255, 0.95);" +
            "-fx-font-size: 13px;"
        );

        infoHeader.getChildren().addAll(lblNumero, lblData);

        Label lblStatus = criarBadgeStatus(pedido.getStatus().toString());
        
        VBox valorBox = new VBox(3);
        valorBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblTotalLabel = new Label("TOTAL");
        lblTotalLabel.setStyle(
            "-fx-text-fill: rgba(255, 255, 255, 0.85);" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );
        
        Label lblTotal = new Label(String.format("%.2f MT", pedido.getValorTotal()));
        lblTotal.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;"
        );
        
        valorBox.getChildren().addAll(lblTotalLabel, lblTotal);

        headerBox.getChildren().addAll(infoHeader, lblStatus, valorBox);
        HBox.setHgrow(infoHeader, Priority.ALWAYS);

        // ScrollPane de itens
        ScrollPane scrollItens = new ScrollPane();
        scrollItens.setStyle(
            "-fx-background: #1e1e1e;" +
            "-fx-background-color: #1e1e1e;" +
            "-fx-border-color: transparent;"
        );
        scrollItens.setPrefHeight(200);
        scrollItens.setFitToWidth(true);

        VBox itensContainer = new VBox(10);
        itensContainer.setStyle("-fx-padding: 20; -fx-background-color: #1e1e1e;");

        for (ItemPedido item : pedido.getItensPedidos()) {
            HBox itemBox = criarItemPedidoBox(item);
            itensContainer.getChildren().add(itemBox);
        }

        scrollItens.setContent(itensContainer);

        // Botões de ação
        HBox buttonBox = new HBox(15);
        buttonBox.setStyle(
            "-fx-background-color: #252525;" +
            "-fx-background-radius: 0 0 15 15;" +
            "-fx-padding: 20;" +
            "-fx-border-color: #333;" +
            "-fx-border-width: 1 0 0 0;"
        );
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        if (pedido.getStatus().toString().equals("PENDENTE")) {
            Button btnCancelar = criarBotao("❌ Cancelar Pedido", 
                "-fx-background-color: #ff4444; -fx-text-fill: white;");
            btnCancelar.setOnAction(e -> cancelarPedido(pedido));
            buttonBox.getChildren().add(btnCancelar);
        } 
        else if (pedido.getStatus().toString().equals("CONCLUIDO")) {
            boolean jaAvaliado = avaliacaoDAO.buscarAvaliacaoPorPedido(pedido.getId()) != null;
            
            if (!jaAvaliado) {
                Button btnAvaliar = criarBotao("⭐ Avaliar", 
                    "-fx-background-color: #FF9800; -fx-text-fill: white;");
                btnAvaliar.setOnAction(e -> avaliarPedido(pedido));
                buttonBox.getChildren().add(btnAvaliar);
            } else {
                Label lblAvaliado = new Label("✅ Já avaliado");
                lblAvaliado.setStyle(
                    "-fx-text-fill: #4CAF50;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;"
                );
                buttonBox.getChildren().add(lblAvaliado);
            }
        }

        Button btnDetalhes = criarBotao("📋 Detalhes", 
            "-fx-background-color: transparent; -fx-text-fill: #4CAF50; -fx-border-color: #4CAF50; -fx-border-width: 2;");
        btnDetalhes.setOnAction(e -> verDetalhesPedido(pedido));
        buttonBox.getChildren().add(btnDetalhes);

        content.getChildren().addAll(headerBox, scrollItens, buttonBox);
        cardContainer.getChildren().add(content);
        card.getChildren().add(cardContainer);

        configurarEfeitosHover(card, cardContainer);

        return card;
    }

    private void configurarEfeitosHover(VBox card, StackPane cardContainer) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), card);
        scaleUp.setToX(1.03);
        scaleUp.setToY(1.03);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), card);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        DropShadow shadowNormal = new DropShadow();
        shadowNormal.setColor(Color.rgb(0, 0, 0, 0.3));
        shadowNormal.setRadius(15);
        shadowNormal.setSpread(0.2);
        DropShadow shadowHover = new DropShadow();
        shadowHover.setColor(Color.rgb(76, 175, 80, 0.6));
        shadowHover.setRadius(25);
        shadowHover.setSpread(0.3);

        card.setOnMouseEntered(e -> {
            scaleUp.playFromStart();
            card.setEffect(shadowHover);
            cardContainer.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #2f2f2f, #232323);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #4CAF50;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 15;"
            );
        });

        card.setOnMouseExited(e -> {
            scaleDown.playFromStart();
            card.setEffect(shadowNormal);
            cardContainer.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #2a2a2a, #1e1e1e);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #4CAF50;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 15;"
            );
        });
    }

    private HBox criarItemPedidoBox(ItemPedido item) {
        HBox box = new HBox(15);
        box.setStyle(
            "-fx-background-color: linear-gradient(to right, #2d2d2d, #252525);" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 15;" +
            "-fx-border-color: #3a3a3a;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );
        box.setAlignment(Pos.CENTER_LEFT);

        StackPane imageContainer = new StackPane();
        imageContainer.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #4CAF50, #45a049);" +
            "-fx-background-radius: 8;"
        );
        imageContainer.setPrefSize(60, 60);

        ImageView img = new ImageView();
        img.setFitHeight(50);
        img.setFitWidth(50);
        img.setPreserveRatio(true);

        Produto produto = item.getProduto();
        if (produto.getImagemPrincipal() != null) {
            try {
                Image image = new Image(new java.io.File(produto.getImagemPrincipal()).toURI().toString());
                img.setImage(image);
                imageContainer.getChildren().add(img);
            } catch (Exception e) {
                Label icone = new Label("🌿");
                icone.setStyle("-fx-font-size: 30px;");
                imageContainer.getChildren().add(icone);
            }
        } else {
            Label icone = new Label("🌿");
            icone.setStyle("-fx-font-size: 30px;");
            imageContainer.getChildren().add(icone);
        }

        VBox detalhes = new VBox(5);
        HBox.setHgrow(detalhes, Priority.ALWAYS);
        
        Label nome = new Label(produto.getNome());
        nome.setStyle(
            "-fx-text-fill: #ffffff;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;"
        );

        Label quantidade = new Label(
            String.format("Qtd: %d × %.2f MT", item.getQuantidade(), produto.getPreco())
        );
        quantidade.setStyle(
            "-fx-text-fill: #b0b0b0;" +
            "-fx-font-size: 12px;"
        );

        detalhes.getChildren().addAll(nome, quantidade);

        Label subtotal = new Label(String.format("%.2f MT", item.getSubtotal()));
        subtotal.setStyle(
            "-fx-text-fill: #4CAF50;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;"
        );

        box.getChildren().addAll(imageContainer, detalhes, subtotal);
        return box;
    }

    private Label criarBadgeStatus(String status) {
        String texto = "";
        String cor = "";
        
        switch (status.toUpperCase()) {
            case "CONCLUIDO":
                texto = "✅ CONCLUÍDO";
                cor = "#4CAF50";
                break;
            case "PENDENTE":
                texto = "⏳ PENDENTE";
                cor = "#FF9800";
                break;
            case "CANCELADO":
                texto = "❌ CANCELADO";
                cor = "#ff4444";
                break;
            case "EM_ANDAMENTO":
                texto = "🔄 EM ANDAMENTO";
                cor = "#2196F3";
                break;
            default:
                texto = status;
                cor = "#9C27B0";
        }
        
        Label badge = new Label(texto);
        badge.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; -fx-font-size: 12px; " +
                      "-fx-font-weight: bold; -fx-padding: 8 15 8 15; -fx-background-radius: 15;");
        return badge;
    }

    private Button criarBotao(String texto, String estilo) {
        Button btn = new Button(texto);
        btn.setStyle(estilo + " -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 20; " +
                   "-fx-border-radius: 20; -fx-padding: 10 20 10 20; -fx-font-size: 12px;");
        return btn;
    }

    // Métodos auxiliares
    
    private void cancelarPedido(Pedido pedido) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancelar Pedido");
        alert.setHeaderText("Deseja cancelar este pedido?");
        alert.setContentText("Pedido #" + pedido.getId() + " - " + String.format("%.2f MT", pedido.getValorTotal()));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: rgba(45,45,45,0.95); -fx-border-color: #ff4444;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                pedido.setStatus(model.StatusPedido.CANCELADO);
                pedidoDAO.atualizarPedido(pedido);
                
                mostrarAlerta("Sucesso", "Pedido cancelado com sucesso!");
                carregarPedidos();
            } catch (Exception e) {
                mostrarAlerta("Erro", "Não foi possível cancelar o pedido: " + e.getMessage());
            }
        }
    }

    private void avaliarPedido(Pedido pedido) {
        mostrarAlerta("Avaliação", "Funcionalidade de avaliação em desenvolvimento");
    }

    private void verDetalhesPedido(Pedido pedido) {
        StringBuilder detalhes = new StringBuilder();
        detalhes.append("📋 Detalhes do Pedido #").append(pedido.getId()).append("\n\n");
        detalhes.append("📅 Data: ").append(pedido.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        detalhes.append("📊 Status: ").append(pedido.getStatus()).append("\n");
        detalhes.append("💵 Valor Total: ").append(String.format("%.2f MT", pedido.getValorTotal())).append("\n\n");
        
        detalhes.append("🛒 Itens do Pedido:\n");
        for (ItemPedido item : pedido.getItensPedidos()) {
            detalhes.append("• ").append(item.getProduto().getNome())
                   .append(" - ").append(item.getQuantidade()).append(" × ")
                   .append(String.format("%.2f MT", item.getProduto().getPreco()))
                   .append(" = ").append(String.format("%.2f MT", item.getSubtotal())).append("\n");
        }

        mostrarAlerta("Detalhes do Pedido", detalhes.toString());
    }

    @FXML
    private void onVoltarClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard_Cliente.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) txtPesquisa.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            
        } catch (Exception e) {
            mostrarAlerta("Erro", "Não foi possível voltar: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
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