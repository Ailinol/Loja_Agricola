/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author liliano
 */

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import service.NotificacaoService;
import service.ResultadoValidacao;
import service.UsuarioService;
import service.Validacoes;

public class CadastroCompradorController implements Initializable {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private PasswordField txtSenha;
    @FXML private PasswordField txtConfirmarSenha;
    @FXML private TextField txtBairro;
    @FXML private ComboBox<String> txtProvincia;
    @FXML private ComboBox<String> txtDistrito;
    @FXML private TextField txtRaioBusca;
    @FXML private TextArea txtPreferencias;
    @FXML private ComboBox<String> comboRecebeNewsletter;
    @FXML private Button btnCadastrar;
    @FXML private Button btnLimpar;
    @FXML private StackPane rootPane;
    @FXML private TabPane tabPane;

    private UsuarioService usuarioService;
    private NotificacaoService notificacaoService;

    public CadastroCompradorController() {
        this.usuarioService = new UsuarioService();
        this.notificacaoService = new NotificacaoService();
        System.out.println("🔄 Controller Comprador instanciado!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtProvincia.getItems().addAll("Maputo", "Maputo Cidade", "Gaza", "Inhambane", "Sofala", "Manica",
                                      "Tete", "Zambézia", "Nampula", "Cabo Delgado", "Niassa");
        
        comboRecebeNewsletter.getItems().addAll("Sim", "Não");
        
        txtProvincia.setOnAction(e -> carregarDistritos());
        
        adicionarListeners();
        
        btnCadastrar.setOnAction(event -> cadastrarComprador());
        
        System.out.println("✅ Controller Comprador inicializado!");
    }

    private void carregarDistritos() {
        String provincia = txtProvincia.getValue();
        txtDistrito.getItems().clear();
        
        if ("Maputo".equals(provincia)) {
            txtDistrito.getItems().addAll("Boane", "Magude", "Manhica", "Marracuene", "Matola",
                                          "Matutuine", "Moamba", "Namaacha");
        } else if ("Gaza".equals(provincia)) {
            txtDistrito.getItems().addAll("Bilene", "Chibuto", "Chicualacuala", "Chigubo", "Chókwè", "Chongoene", "Guijá",
                                        "Limpopo", "Mabalane", "Manjacaze", "Mapai", "Massangena", "Massingir", "Xai-Xai");
        } else if ("Maputo Cidade".equals(provincia)) {
            txtDistrito.getItems().addAll("KaMpfumo", "Nlhamankulu", "KaMaxaquene", "KaMavota", "KaMubukwana", "KaTembe", "KaNyaka");
        } else if ("Inhambane".equals(provincia)) {
            txtDistrito.getItems().addAll("Funhalouro", "Govuro", "Homoíne", "Inhambane", "Inharrime", "Inhassoro", "Jangamo",
                                       "Mabote", "Massinga", "Maxixe", "Morrumbene", "Panda", "Vilanculos", "Zavala");
        } else if ("Sofala".equals(provincia)) {
            txtDistrito.getItems().addAll("Beira", "Búzi", "Caia", "Chemba", "Cheringoma", "Chibabava", "Dondo", "Gorongosa",
                                        "Machanga", "Maringué", "Marromeu", "Muanza", "Nhamatanda");
        } else if ("Manica".equals(provincia)) {
            txtDistrito.getItems().addAll("Bárue", "Chimoio", "Gondola", "Guro", "Macate", "Machaze", "Macossa", "Manica",
                                        "Mossurize", "Sussundenga", "Tambara", "Vanduzi");
        } else if ("Tete".equals(provincia)) {
            txtDistrito.getItems().addAll("Angónia", "Cahora-Bassa", "Changara", "Chifunde", "Chiuta", "Dôa", "Macanga", "Magoé",
                                        "Marara", "Marávia", "Moatize", "Mutarara", "Tete", "Tsangano", "Zumbo");
        } else if ("Zambézia".equals(provincia)) {
            txtDistrito.getItems().addAll("Alto Molócue", "Chinde", "Derre", "Gilé", "Gurué", "Ile", "Inhassunge", "Luabo",
                                        "Lugela", "Maganja da Costa", "Milange", "Mocuba", "Mocubela", "Molumbo", "Mopeia",
                                        "Morrumbala", "Mulevala", "Namacurra", "Namarroi", "Nicoadala", "Pebane", "Quelimane");
        } else if ("Nampula".equals(provincia)) {
            txtDistrito.getItems().addAll("Angoche", "Eráti", "Ilha de Moçambique", "Lalaua", "Larde", "Liúpo", "Malema",
                                        "Meconta", "Mecubúri", "Memba", "Mogincual", "Mogovolas", "Moma", "Monapo", "Mossuril",
                                        "Muecate", "Murrupula", "Nacala-a-Velha", "Nacala Porto", "Nacarôa", "Nampula",
                                        "Rapale", "Ribaué");
        } else if ("Cabo Delgado".equals(provincia)) {
            txtDistrito.getItems().addAll("Ancuabe", "Balama", "Chiúre", "Ibo", "Macomia", "Mecúfi", "Meluco", "Metuge",
                                        "Mocímboa da Praia", "Montepuez", "Mueda", "Muidumbe", "Namuno", "Nangade", "Palma",
                                        "Pemba", "Quissanga");
        } else if ("Niassa".equals(provincia)) {
            txtDistrito.getItems().addAll("Cuamba", "Lago", "Lichinga", "Majune", "Mandimba", "Marrupa", "Maúa", "Mavago",
                                        "Mecanhelas", "Mecula", "Metarica", "Muembe", "N'gauma", "Nipepe", "Sanga");
        }
    }

    private void adicionarListeners() {
        // Listeners para validação em tempo real
        txtNome.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !newVal.equals(oldVal)) {
                boolean valido = Validacoes.validarNome(newVal).valido;
                aplicarEstiloValidacao(txtNome, valido);
            }
        });

        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !newVal.equals(oldVal)) {
                boolean valido = Validacoes.validarEmail(newVal).valido;
                aplicarEstiloValidacao(txtEmail, valido);
            }
        });

        txtTelefone.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !newVal.equals(oldVal)) {
                boolean valido = Validacoes.validarTelefone(newVal).valido;
                aplicarEstiloValidacao(txtTelefone, valido);
            }
        });


        txtBairro.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !newVal.equals(oldVal)) {
                boolean valido = Validacoes.validarRegiao(newVal).valido;
                aplicarEstiloValidacao(txtBairro, valido);
            }
        });

        

        txtRaioBusca.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !newVal.equals(oldVal)) {
                boolean valido = validarRaioBusca(newVal);
                aplicarEstiloValidacao(txtRaioBusca, valido);
            }
        });

        txtSenha.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                limparEstilosValidacao();
                return;
            }

            boolean valido = Validacoes.validarSenha(newVal).valido;
            aplicarEstiloValidacao(txtSenha, valido);

            if (txtConfirmarSenha != null && !txtConfirmarSenha.getText().isEmpty()) {
                validarConfirmarSenha();
            }
        });

        txtConfirmarSenha.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                validarConfirmarSenha();
            } else {
                limparEstilosValidacao();
            }
        });
    }

    private boolean validarRaioBusca(String raio) {
        try {
            double valor = Double.parseDouble(raio.trim());
            return valor > 0 && valor <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validarSaldo(String saldo) {
        try {
            double valor = Double.parseDouble(saldo.trim());
            return valor >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void aplicarEstiloValidacao(Node node, boolean valido) {
        String estiloValido = "-fx-border-color: #4CAF50; -fx-border-width: 2;";
        String estiloInvalido = "-fx-border-color: #F44336; -fx-border-width: 2;";
        
        if (node instanceof TextField) {
            TextField campo = (TextField) node;
            if (valido) {
                campo.setStyle(campo.getStyle() + estiloValido);
            } else {
                campo.setStyle(campo.getStyle() + estiloInvalido);
            }
        } else if (node instanceof ComboBox) {
            ComboBox<?> combo = (ComboBox<?>) node;
            if (valido) {
                combo.setStyle(combo.getStyle() + estiloValido);
            } else {
                combo.setStyle(combo.getStyle() + estiloInvalido);
            }
        } else if (node instanceof TextArea) {
            TextArea area = (TextArea) node;
            if (valido) {
                area.setStyle(area.getStyle() + estiloValido);
            } else {
                area.setStyle(area.getStyle() + estiloInvalido);
            }
        }
    }

    public void mostrarErroValidacao(Node node, String mensagem) {
        aplicarEstiloValidacao(node, false);
        
        VBox popup = new VBox(5);
        popup.setStyle(
            "-fx-background-color: #f44336;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 3;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);"
        );
        popup.setMaxWidth(250);
        
        Region arrow = new Region();
        arrow.setStyle(
            "-fx-shape: 'M 0 10 L 10 0 L 20 10 Z';" +
            "-fx-background-color: #f44336;" +
            "-fx-min-width: 20;" +
            "-fx-min-height: 10;" +
            "-fx-max-width: 20;" +
            "-fx-max-height: 10;"
        );
        
        Label errorLabel = new Label(mensagem);
        errorLabel.setTextFill(Color.WHITE);
        errorLabel.setFont(Font.font("Segoe UI", 12));
        errorLabel.setWrapText(true);
        
        popup.getChildren().addAll(arrow, errorLabel);
        
        if (node.getParent() instanceof Pane) {
            Pane parent = (Pane) node.getParent();
            popup.setLayoutX(node.getLayoutX());
            popup.setLayoutY(node.getLayoutY() + node.getBoundsInLocal().getHeight() + 5);
            
            parent.getChildren().add(popup);
            
            FadeTransition fade = new FadeTransition(Duration.millis(200), popup);
            fade.setFromValue(0);
            fade.setToValue(1);
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), popup);
            scale.setFromX(0.8);
            scale.setFromY(0.8);
            scale.setToX(1);
            scale.setToY(1);
            
            ParallelTransition show = new ParallelTransition(fade, scale);
            show.play();
            
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), popup);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(ev -> parent.getChildren().remove(popup));
                fadeOut.play();
            });
            pause.play();
        }
    }

    private void limparEstilosValidacao() {
        Node[] campos = {txtNome, txtEmail, txtTelefone, txtSenha, txtConfirmarSenha,
                        txtProvincia, txtDistrito, txtBairro, 
                        txtRaioBusca, txtPreferencias, comboRecebeNewsletter};
        
        for (Node campo : campos) {
            if (campo instanceof TextField) {
                ((TextField) campo).setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-prompt-text-fill: rgba(255,255,255,0.4); -fx-border-color: rgba(255,255,255,0.15); -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 0 12 0 12;");
            } else if (campo instanceof ComboBox) {
                ((ComboBox<?>) campo).setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-border-color: rgba(255,255,255,0.15); -fx-border-radius: 8; -fx-background-radius: 8; -fx-prompt-text-fill: rgba(255,255,255,0.4);");
            } else if (campo instanceof TextArea) {
                ((TextArea) campo).setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-prompt-text-fill: rgba(255,255,255,0.4); -fx-border-color: rgba(255,255,255,0.15); -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12; -fx-control-inner-background: rgba(255,255,255,0.08);");
            }
        }
    }

    // Métodos de validação para Enter/Action
    @FXML
    private void validarNome() {
        ResultadoValidacao resultado = Validacoes.validarNome(txtNome.getText());
        aplicarEstiloValidacao(txtNome, resultado.valido);
    }

    @FXML
    private void validarNomeOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validarNome();
        }
    }

    @FXML
    private void validarEmail() {
        ResultadoValidacao resultado = Validacoes.validarEmail(txtEmail.getText());
        aplicarEstiloValidacao(txtEmail, resultado.valido);
    }

    @FXML
    private void validarEmailOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validarEmail();
        }
    }

    @FXML
    private void validarTelefone() {
        ResultadoValidacao resultado = Validacoes.validarTelefone(txtTelefone.getText());
        aplicarEstiloValidacao(txtTelefone, resultado.valido);
    }

    @FXML
    private void validarTelefoneOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validarTelefone();
        }
    }

    

    @FXML
    private void validarSenha() {
        ResultadoValidacao resultado = Validacoes.validarSenha(txtSenha.getText());
        aplicarEstiloValidacao(txtSenha, resultado.valido);
    }

    @FXML
    private void validarSenhaOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validarSenha();
        }
    }

    @FXML
    private void validarConfirmarSenha() {
        String senha = txtSenha.getText();
        String confirmacao = txtConfirmarSenha.getText();

        if (confirmacao.isEmpty()) {
            limparEstilosValidacao();
        } else if (confirmacao.equals(senha)) {
            aplicarEstiloValidacao(txtConfirmarSenha, true);
        } else {
            aplicarEstiloValidacao(txtConfirmarSenha, false);
        }
    }

    @FXML
    private void validarConfirmarSenhaOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validarConfirmarSenha();
        }
    }

    @FXML
    private void validarProvincia() {
        validarComboBoxObrigatorio(txtProvincia, "Província");
    }

    @FXML
    private void validarDistrito() {
        validarComboBoxObrigatorio(txtDistrito, "Distrito");
    }

    @FXML
    private void validarBairro() {
        validarCampoObrigatorio(txtBairro, "Bairro");
    }

    @FXML
    private void validarBairroOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validarBairro();
        }
    }

    @FXML
    private void validarRaioBusca() {
        boolean valido = validarRaioBusca(txtRaioBusca.getText());
        aplicarEstiloValidacao(txtRaioBusca, valido);
    }

    @FXML
    private void validarRaioBuscaOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validarRaioBusca();
        }
    }

    @FXML
    private void validarRecebeNewsletter() {
        validarComboBoxObrigatorio(comboRecebeNewsletter, "Receber Newsletter");
    }

    private void validarCampoObrigatorio(TextField campo, String nomeCampo) {
        if (campo.getText() == null || campo.getText().trim().isEmpty()) {
            mostrarErroValidacao(campo, nomeCampo + " é obrigatório!");
        } else {
            aplicarEstiloValidacao(campo, true);
        }
    }

    private void validarComboBoxObrigatorio(ComboBox<String> comboBox, String nomeCampo) {
        if (comboBox.getValue() == null || comboBox.getValue().trim().isEmpty()) {
            mostrarErroValidacao(comboBox, nomeCampo + " é obrigatório!");
        } else {
            aplicarEstiloValidacao(comboBox, true);
        }
    }

    @FXML
    private void handleCadastrarComprador() {
        cadastrarComprador();
    }

    private void cadastrarComprador() {
        try {
            // Dados pessoais
            String nome = txtNome.getText();
            String email = txtEmail.getText();
            String telefone = txtTelefone.getText();
            String provincia = txtProvincia.getValue();
            String distrito = txtDistrito.getValue();
            String bairro = txtBairro.getText();
            String senha = txtSenha.getText();

            double raioBuscaPreferido = Double.parseDouble(txtRaioBusca.getText());
            boolean recebeNewsletter = "Sim".equals(comboRecebeNewsletter.getValue());
            
            List<String> preferenciasCategorias = new ArrayList<>();
            String preferenciasText = txtPreferencias.getText();
            if (preferenciasText != null && !preferenciasText.trim().isEmpty()) {
                String[] categorias = preferenciasText.split(",");
                for (String categoria : categorias) {
                    String categoriaLimpa = categoria.trim();
                    if (!categoriaLimpa.isEmpty()) {
                        preferenciasCategorias.add(categoriaLimpa);
                    }
                }
            }

            boolean sucesso = usuarioService.cadastrarComprador(
                nome, email, telefone, provincia, distrito, bairro,
                senha, preferenciasCategorias, raioBuscaPreferido, recebeNewsletter
            );

            if (sucesso) {
                mostrarAlerta("Sucesso", "Comprador cadastrado: " + nome);
                limparCampos();
            } else {
                mostrarAlerta("Erro", "Falha no cadastro do comprador");
            }

        } catch (Exception e) {
            System.err.println("ERRO: " + e.getMessage());
            mostrarAlerta("Erro", "Erro: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimparCampos() {
        limparCampos();
    }

    private void limparCampos() {
        txtNome.clear();
        txtEmail.clear();
        txtTelefone.clear();
        txtSenha.clear();
        txtConfirmarSenha.clear();
        txtBairro.clear();
        txtRaioBusca.clear();
        txtPreferencias.clear();

        txtProvincia.getSelectionModel().clearSelection();
        txtDistrito.getSelectionModel().clearSelection();
        comboRecebeNewsletter.getSelectionModel().clearSelection();

        limparEstilosValidacao();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}