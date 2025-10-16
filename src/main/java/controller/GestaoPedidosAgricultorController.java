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
import model.Comprador;
import service.SessaoActual;
import dao.PedidoDAO;
import dao.ProdutoDAO;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class GestaoPedidosAgricultorController implements Initializable {

    @FXML private TextField txtPesquisa;
    @FXML private ComboBox<String> comboStatus, comboOrdenacao, comboPeriodo;
    @FXML private FlowPane containerPedidos; 
    @FXML private ScrollPane scrollPedidos;

    private PedidoDAO pedidoDAO;
    private ProdutoDAO produtoDAO;
    private List<Pedido> pedidos = new ArrayList<>();

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
        this.pedidoDAO = new PedidoDAO();
        this.produtoDAO = new ProdutoDAO();
        
        configurarComponentes();
        carregarPedidos();
        configurarEfeitosInterativos();
    }

    private void configurarComponentes() {
        // Configurar comboboxes
        comboStatus.getItems().addAll("📊 Todos", "⏳ Pendentes", "✅ Confirmados", "🔄 Em Preparação", "🚚 Em Entrega", "✅ Concluídos", "❌ Cancelados", "❌ Recusados");
        comboStatus.setValue("📊 Todos");
        
        comboOrdenacao.getItems().addAll("🕒 Mais Recente", "📅 Mais Antigo", "💵 Maior Valor", "💵 Menor Valor");
        comboOrdenacao.setValue("🕒 Mais Recente");
        
        comboPeriodo.getItems().addAll("📅 Todos", "📅 Hoje", "📅 Últimos 7 dias", "📅 Último mês");
        comboPeriodo.setValue("📅 Todos");
    }

    private void configurarEfeitosInterativos() {
        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> filtrarPedidos());
        comboStatus.setOnAction(e -> filtrarPedidos());
        comboOrdenacao.setOnAction(e -> ordenarPedidos());
        comboPeriodo.setOnAction(e -> filtrarPedidos());
    }

    private void carregarPedidos() {
        try {
            containerPedidos.getChildren().clear();

            // Buscar pedidos do agricultor logado
            int agricultorId = SessaoActual.getAgricultorLogado().getId();
            pedidos = pedidoDAO.buscarPedidosPorAgricultor(agricultorId);
            
            if (pedidos.isEmpty()) {
                mostrarMensagemSemPedidos();
                return;
            }

            for (Pedido pedido : pedidos) {
                VBox card = criarCardPedido(pedido);
                containerPedidos.getChildren().add(card);
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar pedidos: " + e.getMessage());
            e.printStackTrace();
            
            Label lblErro = new Label("Erro ao carregar pedidos: " + e.getMessage());
            lblErro.setStyle("-fx-text-fill: red; -fx-font-size: 14; -fx-padding: 20;");
            containerPedidos.getChildren().add(lblErro);
        }
    }

    private VBox criarCardPedido(Pedido pedido) {
        VBox card = new VBox();
        card.getStyleClass().add("product-card-premium");
        card.setPrefWidth(800);
        card.setMaxWidth(800);
        card.setAlignment(Pos.TOP_CENTER);
        
        DropShadow shadow = new DropShadow(20, Color.rgb(0, 0, 0, 0.3));
        card.setEffect(shadow);

        StackPane cardContainer = new StackPane();
        cardContainer.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 20; " +
                             "-fx-border-color: rgba(255,255,255,0.15); -fx-border-radius: 20;");
        cardContainer.setPrefSize(800, 450);

        VBox content = new VBox();
        content.setPrefSize(800, 450);
        content.setAlignment(Pos.TOP_LEFT);
        content.setSpacing(0);

        HBox headerBox = new HBox(20);
        headerBox.setStyle("-fx-background-color: rgba(76,175,80,0.2); -fx-background-radius: 20 20 0 0; -fx-padding: 20;");
        headerBox.setPrefHeight(80);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox infoHeader = new VBox(5);
        Label lblNumero = new Label("Pedido #" + pedido.getId());
        lblNumero.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblData = new Label("Data: " + pedido.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblData.setStyle("-fx-text-fill: #b3e5d1; -fx-font-size: 14px;");

        Comprador cliente = pedido.getComprador();
        Label lblCliente = new Label("Cliente: " + cliente.getNome());
        lblCliente.setStyle("-fx-text-fill: #b3e5d1; -fx-font-size: 14px;");

        infoHeader.getChildren().addAll(lblNumero, lblData, lblCliente);

        Label lblStatus = criarBadgeStatus(pedido.getStatus().toString());
        
        Label lblTotal = new Label(String.format("Total: %.2f MT", pedido.getValorTotal()));
        lblTotal.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 18px; -fx-font-weight: bold;");

        headerBox.getChildren().addAll(infoHeader, lblStatus, lblTotal);
        HBox.setHgrow(infoHeader, Priority.ALWAYS);

        ScrollPane scrollItens = new ScrollPane();
        scrollItens.setStyle("-fx-background: transparent; -fx-border-color: transparent;");
        scrollItens.setPrefHeight(220);
        scrollItens.setFitToWidth(true);

        VBox itensContainer = new VBox(10);
        itensContainer.setStyle("-fx-padding: 20;");

        for (ItemPedido item : pedido.getItensPedidos()) {
            HBox itemBox = criarItemPedidoBox(item);
            itensContainer.getChildren().add(itemBox);
        }

        scrollItens.setContent(itensContainer);

        HBox buttonBox = new HBox(15);
        buttonBox.setStyle("-fx-background-color: rgba(255,255,255,0.02); -fx-background-radius: 0 0 20 20; -fx-padding: 20;");
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        String status = pedido.getStatus().toString();
        
        if (status.equals("PENDENTE")) {
            Button btnConfirmar = criarBotao("✅ Confirmar Pedido", 
                "-fx-background-color: linear-gradient(to right, #4CAF50, #45a049); -fx-text-fill: white;");
            btnConfirmar.setOnAction(e -> confirmarPedido(pedido));

            Button btnRecusar = criarBotao("❌ Recusar Pedido", 
                "-fx-background-color: linear-gradient(to right, #ff4444, #cc0000); -fx-text-fill: white;");
            btnRecusar.setOnAction(e -> recusarPedido(pedido));

            buttonBox.getChildren().addAll(btnRecusar, btnConfirmar);
        } 
        else if (status.equals("CONFIRMADO")) {
            Button btnPreparar = criarBotao("👨‍🍳 Iniciar Preparação", 
                "-fx-background-color: linear-gradient(to right, #FF9800, #F57C00); -fx-text-fill: white;");
            btnPreparar.setOnAction(e -> iniciarPreparacao(pedido));

            buttonBox.getChildren().add(btnPreparar);
        }
        else if (status.equals("EM_PREPARACAO")) {
            Button btnPronto = criarBotao("🚚 Pronto para Entrega", 
                "-fx-background-color: linear-gradient(to right, #2196F3, #1976D2); -fx-text-fill: white;");
            btnPronto.setOnAction(e -> prontoParaEntrega(pedido));

            buttonBox.getChildren().add(btnPronto);
        }
        else if (status.equals("PRONTO_ENTREGA")) {
            Button btnEntregar = criarBotao("Marcar como Entregue", 
                "-fx-background-color: linear-gradient(to right, #4CAF50, #45a049); -fx-text-fill: white;");
            btnEntregar.setOnAction(e -> marcarComoEntregue(pedido));

            buttonBox.getChildren().add(btnEntregar);
        }

        Button btnDetalhes = criarBotao("Ver Detalhes", 
            "-fx-background-color: transparent; -fx-text-fill: #b3e5d1; -fx-border-color: #b3e5d1;");
        btnDetalhes.setOnAction(e -> verDetalhesPedido(pedido));

        buttonBox.getChildren().add(btnDetalhes);

        content.getChildren().addAll(headerBox, scrollItens, buttonBox);
        cardContainer.getChildren().add(content);
        card.getChildren().add(cardContainer);

        configurarEfeitosHover(card);

        return card;
    }

    private HBox criarItemPedidoBox(ItemPedido item) {
        HBox box = new HBox(15);
        box.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10; -fx-padding: 15;");
        box.setAlignment(Pos.CENTER_LEFT);

        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8;");
        imageContainer.setPrefSize(50, 50);

        ImageView img = new ImageView();
        img.setFitHeight(40);
        img.setFitWidth(40);
        img.setStyle("-fx-background-radius: 6;");

        Produto produto = item.getProduto();
        if (produto.getImagemPrincipal() != null) {
            try {
                Image image = new Image(new java.io.File(produto.getImagemPrincipal()).toURI().toString());
                img.setImage(image);
            } catch (Exception e) {
                // Fallback para gradiente
                Rectangle fallback = new Rectangle(40, 40);
                fallback.setFill(cardGradients[0]);
                fallback.setArcWidth(8);
                fallback.setArcHeight(8);
                imageContainer.getChildren().add(fallback);
            }
        }

        if (imageContainer.getChildren().isEmpty()) {
            imageContainer.getChildren().add(img);
        }

        VBox detalhes = new VBox(5);
        Label nome = new Label(produto.getNome());
        nome.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label quantidade = new Label("Quantidade: " + item.getQuantidade() + " × " + String.format("%.2f MT", produto.getPreco()));
        quantidade.setStyle("-fx-text-fill: #b3e5d1; -fx-font-size: 12px;");

        Label subtotal = new Label("Subtotal: " + String.format("%.2f MT", item.getSubtotal()));
        subtotal.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 13px; -fx-font-weight: bold;");

        String statusEstoque = produto.getQuantidadeDisponivel() >= item.getQuantidade() ? 
            "Estoque suficiente" : " Estoque insuficiente";
        Label lblEstoque = new Label(statusEstoque);
        lblEstoque.setStyle("-fx-text-fill: " + (produto.getQuantidadeDisponivel() >= item.getQuantidade() ? "#4CAF50" : "#FF9800") + 
                           "; -fx-font-size: 11px;");

        detalhes.getChildren().addAll(nome, quantidade, subtotal, lblEstoque);

        box.getChildren().addAll(imageContainer, detalhes);
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
            case "CONFIRMADO":
                texto = "✅ CONFIRMADO";
                cor = "#2196F3";
                break;
            case "EM_PREPARACAO":
                texto = "👨‍🍳 EM PREPARAÇÃO";
                cor = "#FF9800";
                break;
            case "PRONTO_ENTREGA":
                texto = "🚚 PRONTO ENTREGA";
                cor = "#2196F3";
                break;
            case "CANCELADO":
                texto = "❌ CANCELADO";
                cor = "#ff4444";
                break;
            case "RECUSADO":
                texto = "❌ RECUSADO";
                cor = "#ff4444";
                break;
            default:
                texto = "📊 " + status;
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

    // MÉTODOS DE AÇÃO PARA O AGRICULTOR

    private void confirmarPedido(Pedido pedido) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("✅ Confirmar Pedido");
        alert.setHeaderText("Deseja confirmar este pedido?");
        alert.setContentText("Pedido #" + pedido.getId() + " - Cliente: " + pedido.getComprador().getNome());

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: rgba(45,45,45,0.95); -fx-border-color: #4CAF50;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Verificar estoque primeiro
                if (!verificarEstoque(pedido)) {
                    mostrarAlerta(" Estoque Insuficiente", 
                        "Não há estoque suficiente para atender este pedido.");
                    return;
                }

                Pedido pedidoCompleto = pedidoDAO.buscarPedidoCompletoPorId(pedido.getId());
                pedidoCompleto.setStatus(model.StatusPedido.CONFIRMADO);
                pedidoDAO.atualizarPedido(pedidoCompleto);
                
                // Atualizar estoque
                atualizarEstoque(pedidoCompleto);
                
                mostrarAlerta("Sucesso", "Pedido confirmado com sucesso!");
                carregarPedidos();
            } catch (Exception e) {
                mostrarAlerta("Erro", "Não foi possível confirmar o pedido: " + e.getMessage());
            }
        }
    }

    private void recusarPedido(Pedido pedido) {
        // Diálogo para motivo da recusa
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Recusar Pedido");
        dialog.setHeaderText("Informe o motivo da recusa do pedido #" + pedido.getId());

        ButtonType btnConfirmar = new ButtonType("Confirmar Recusa", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirmar, btnCancelar);

        TextArea txtMotivo = new TextArea();
        txtMotivo.setPromptText("Motivo da recusa...");
        txtMotivo.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-border-color: #ff4444;");
        txtMotivo.setPrefHeight(100);
        txtMotivo.setPrefWidth(400);

        dialog.getDialogPane().setContent(txtMotivo);
        dialog.getDialogPane().setStyle("-fx-background-color: rgba(45,45,45,0.95); -fx-border-color: #ff4444;");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnConfirmar) {
                return txtMotivo.getText();
            }
            return null;
        });

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(motivo -> {
            try {
                Pedido pedidoCompleto = pedidoDAO.buscarPedidoCompletoPorId(pedido.getId());
               // pedidoCompleto.setStatus(model.StatusPedido.RECUSADO);
                pedidoDAO.atualizarPedido(pedidoCompleto);
                
                mostrarAlerta("✅ Pedido Recusado", "Pedido recusado com sucesso.\nMotivo: " + 
                    (motivo.isEmpty() ? "Não informado" : motivo));
                carregarPedidos();
            } catch (Exception e) {
                mostrarAlerta("❌ Erro", "Não foi possível recusar o pedido: " + e.getMessage());
            }
        });
    }

    private void iniciarPreparacao(Pedido pedido) {
        try {
            Pedido pedidoCompleto = pedidoDAO.buscarPedidoCompletoPorId(pedido.getId());
           // pedidoCompleto.setStatus(model.StatusPedido.EM_PREPARACAO);
            pedidoDAO.atualizarPedido(pedidoCompleto);
            
            mostrarAlerta("👨‍🍳 Preparação Iniciada", "Preparação do pedido iniciada com sucesso!");
            carregarPedidos();
        } catch (Exception e) {
            mostrarAlerta("Erro", "Não foi possível iniciar a preparação: " + e.getMessage());
        }
    }

    private void prontoParaEntrega(Pedido pedido) {
        try {
            Pedido pedidoCompleto = pedidoDAO.buscarPedidoCompletoPorId(pedido.getId());
           // pedidoCompleto.setStatus(model.StatusPedido.PRONTO_ENTREGA);
            pedidoDAO.atualizarPedido(pedidoCompleto);
            
            mostrarAlerta("🚚 Pronto para Entrega", "Pedido marcado como pronto para entrega!");
            carregarPedidos();
        } catch (Exception e) {
            mostrarAlerta("❌ Erro", "Não foi possível marcar como pronto: " + e.getMessage());
        }
    }

    private void marcarComoEntregue(Pedido pedido) {
        try {
            Pedido pedidoCompleto = pedidoDAO.buscarPedidoCompletoPorId(pedido.getId());
            //pedidoCompleto.setStatus(model.StatusPedido.CONCLUIDO);
            pedidoDAO.atualizarPedido(pedidoCompleto);
            
            mostrarAlerta("Entregue", "Pedido marcado como entregue com sucesso!");
            carregarPedidos();
        } catch (Exception e) {
            mostrarAlerta("Erro", "Não foi possível marcar como entregue: " + e.getMessage());
        }
    }

    private boolean verificarEstoque(Pedido pedido) {
        for (ItemPedido item : pedido.getItensPedidos()) {
            Produto produto = item.getProduto();
            if (produto.getQuantidadeDisponivel() < item.getQuantidade()) {
                return false;
            }
        }
        return true;
    }

    private void atualizarEstoque(Pedido pedido) {
        for (ItemPedido item : pedido.getItensPedidos()) {
            Produto produto = item.getProduto();
            int novaQuantidade = produto.getQuantidadeDisponivel() - item.getQuantidade();
            produto.setQuantidadeDisponivel(novaQuantidade);
            produtoDAO.atualizarProduto(produto);
        }
    }

    private void verDetalhesPedido(Pedido pedido) {
        StringBuilder detalhes = new StringBuilder();
        detalhes.append(" **Detalhes do Pedido #").append(pedido.getId()).append("**\n\n");
        detalhes.append("👤 **Cliente:** ").append(pedido.getComprador().getNome()).append("\n");
        detalhes.append(" **Telefone:** ").append(pedido.getComprador().getTelefone()).append("\n");
        detalhes.append("📅 **Data:** ").append(pedido.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        detalhes.append(" **Status:** ").append(pedido.getStatus()).append("\n");
        detalhes.append(" **Valor Total:** ").append(String.format("%.2f MT", pedido.getValorTotal())).append("\n\n");
        
        detalhes.append("🛒 **Itens do Pedido:**\n");
        for (ItemPedido item : pedido.getItensPedidos()) {
            String statusEstoque = item.getProduto().getQuantidadeDisponivel() >= item.getQuantidade() ? 
                "" : "️";
            detalhes.append(statusEstoque).append(" ")
                   .append(item.getProduto().getNome())
                   .append(" - ").append(item.getQuantidade()).append(" × ")
                   .append(String.format("%.2f MT", item.getProduto().getPreco()))
                   .append(" = ").append(String.format("%.2f MT", item.getSubtotal())).append("\n");
        }

        mostrarAlerta("Detalhes do Pedido", detalhes.toString());
    }

    private void mostrarMensagemSemPedidos() {
        VBox mensagemContainer = new VBox(20);
        mensagemContainer.setAlignment(Pos.CENTER);
        mensagemContainer.setStyle("-fx-padding: 50;");
        
        Label icone = new Label("");
        icone.setStyle("-fx-font-size: 60px;");
        
        Label titulo = new Label("Nenhum Pedido Encontrado");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label subtitulo = new Label("Os pedidos dos clientes aparecerão aqui");
        subtitulo.setStyle("-fx-text-fill: #b3e5d1; -fx-font-size: 14px;");
        
        mensagemContainer.getChildren().addAll(icone, titulo, subtitulo);
        containerPedidos.getChildren().add(mensagemContainer);
    }

    private void filtrarPedidos() {
        carregarPedidos();
    }

    private void ordenarPedidos() {
        carregarPedidos();
    }

    @FXML
    private void onVoltarClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard_Agricultor.fxml"));
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