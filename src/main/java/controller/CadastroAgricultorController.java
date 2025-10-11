package controller;

import dao.ProdutoDAO;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import service.UsuarioService;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import service.Validacoes;
import service.ResultadoValidacao;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import model.Produto;


public class CadastroAgricultorController implements Initializable {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private PasswordField txtSenha;
    @FXML private PasswordField txtConfirmarSenha;
    @FXML private TextField txtBairro;
    @FXML private ComboBox<String> txtProvincia;
    @FXML private ComboBox<String> txtDistrito;
    @FXML private ComboBox<String> txtTipoAgricultura;
    @FXML private TextField txtNumeroCasa;
    @FXML private TextArea txtBiografia;
    @FXML private TextField txtAnosExperiencia;
    @FXML private TextField txtTamanhoPropiedade;
    @FXML private TextField txtRaioEntrega;
    @FXML private TextField txtCustoEntrega;
    @FXML private ComboBox<String> txtOfereceEntrega;
    @FXML private TextField txtWhatsapp;
    @FXML private ComboBox<String> comboCertificadoOrganico;
    @FXML private ComboBox<String> comboAceitaVisitas;
    @FXML private ComboBox<String> comboAceitaEncomendas;
    @FXML private ComboBox<String> comboDisponivelContato;
    @FXML private TextField txtPrazoEncomenda;
    @FXML private TextField txtHorarioAbertura;
    @FXML private TextField txtHorarioFechamento;
    @FXML private VBox containerPrazoEncomenda;
    @FXML private Button btnCadastrar;
    @FXML private Button btnLimpar;
    @FXML private StackPane rootPane; 
    @FXML private VBox containerEntrega;


    private UsuarioService usuarioService;

    public CadastroAgricultorController() {      
        this.usuarioService = new UsuarioService();
        System.out.println("🔄 Controller instanciado!");
        /*
        Produto prod = new Produto(
           "Tomate", "AAA", "BBB", 400, 100, 10, true, LocalDate.now(), LocalDate.now(), LocalDate.now(),1, "ccc"
        );
        
        ProdutoDAO dao = new ProdutoDAO();
        dao.salvarProduto(prod);
        */
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        txtProvincia.getItems().addAll("Maputo", "Maputo Cidade", "Gaza", "Inhambane", "Sofala", "Manica",
                                        "Tete", "Zambézia", "Nampula", "Cabo Delgado", "Niassa");
        
        txtTipoAgricultura.getItems().addAll("Agricultura Familiar",
        "Agricultura Comercial",
        "Agricultura Orgânica",
        "Agricultura Mista",
        "Horticultura",
        "Fruticultura");
        
        txtOfereceEntrega.getItems().addAll("Sim", "Nao");
        comboCertificadoOrganico.getItems().addAll("Sim", "Não");
        comboAceitaVisitas.getItems().addAll("Sim", "Não");
        comboAceitaEncomendas.getItems().addAll("Sim", "Não");
        comboDisponivelContato.getItems().addAll("Sim", "Não");
        
        txtProvincia.setOnAction(e -> carregarDistritos());
        adicionarListeners();
    }
    
    private void carregarDistritos(){
        String provincia = txtProvincia.getValue();
        txtDistrito.getItems().clear();
        
        if("Maputo".equals(provincia)){
            txtDistrito.getItems().addAll("Boane", "Magude", "Manhica", "Marracuene", "Matola"
                                          , "Matutuine", "Moamba", "Namaacha");
        }
        if("Gaza".equals(provincia)){
            txtDistrito.getItems().addAll("Bilene", "Chibuto", "Chicualacuala", "Chigubo", "Chókwè", "Chongoene", "Guijá",
                                            "Limpopo", "Mabalane", "Manjacaze", "Mapai", "Massangena", "Massingir", "Xai-Xai");
        }
        if("Maputo Cidade".equals(provincia)){
            txtDistrito.getItems().addAll("KaMpfumo", "Nlhamankulu", "KaMaxaquene", "KaMavota", "KaMubukwana", "KaTembe", "KaNyaka"
                                            );
        }
        if("Inhambane".equals(provincia)){
            txtDistrito.getItems().addAll("Funhalouro", "Govuro", "Homoíne", "Inhambane", "Inharrime", "Inhassoro", "Jangamo",
                                           "Mabote", "Massinga", "Maxixe", "Morrumbene", "Panda", "Vilanculos", "Zavala");
        }
        if("Sofala".equals(provincia)){
            txtDistrito.getItems().addAll("Beira", "Búzi", "Caia", "Chemba", "Cheringoma", "Chibabava", "Dondo", "Gorongosa",
                                            "Machanga", "Maringué", "Marromeu", "Muanza", "Nhamatanda");
        }
        if("Manica".equals(provincia)){
            txtDistrito.getItems().addAll("Bárue", "Chimoio", "Gondola", "Guro", "Macate", "Machaze", "Macossa", "Manica",
                                            "Mossurize", "Sussundenga", "Tambara", "Vanduzi");
        }
        if("Tete".equals(provincia)){
            txtDistrito.getItems().addAll("Angónia", "Cahora-Bassa", "Changara", "Chifunde", "Chiuta", "Dôa", "Macanga", "Magoé",
                                            "Marara", "Marávia", "Moatize", "Mutarara", "Tete", "Tsangano", "Zumbo");
        }
        if("Zambézia".equals(provincia)){
            txtDistrito.getItems().addAll("Alto Molócue", "Chinde", "Derre", "Gilé", "Gurué", "Ile", "Inhassunge", "Luabo",
                                            "Lugela", "Maganja da Costa", "Milange", "Mocuba", "Mocubela", "Molumbo", "Mopeia",
                                            "Morrumbala", "Mulevala", "Namacurra", "Namarroi", "Nicoadala", "Pebane", "Quelimane");
        }
        if("Nampula".equals(provincia)){
            txtDistrito.getItems().addAll("Angoche", "Eráti", "Ilha de Moçambique", "Lalaua", "Larde", "Liúpo", "Malema",
                                            "Meconta", "Mecubúri", "Memba", "Mogincual", "Mogovolas", "Moma", "Monapo", "Mossuril",
                                            "Muecate", "Murrupula", "Nacala-a-Velha", "Nacala Porto", "Nacarôa", "Nampula",
                                            "Rapale", "Ribaué");
        }
        if("Cabo Delgado".equals(provincia)){
            txtDistrito.getItems().addAll("Ancuabe", "Balama", "Chiúre", "Ibo", "Macomia", "Mecúfi", "Meluco", "Metuge",
                                            "Mocímboa da Praia", "Montepuez", "Mueda", "Muidumbe", "Namuno", "Nangade", "Palma",
                                            "Pemba", "Quissanga");
        }
        if("Niassa".equals(provincia)){
            txtDistrito.getItems().addAll("Beira", "Búzi", "Caia", "Chemba", "Cheringoma", "Chibabava", "Dondo", "Gorongosa",
                                            "Machanga", "Maringué", "Marromeu", "Muanza", "Nhamatanda");
        }
    }
    
    
    @FXML
    private void onOfereceEntregaChanged() {
        String selecao = txtOfereceEntrega.getValue();
        boolean fazEntrega = "Sim".equals(selecao);

        containerEntrega.setVisible(fazEntrega);
        containerEntrega.setManaged(fazEntrega);

        if (!fazEntrega) {
            txtRaioEntrega.clear();
            txtCustoEntrega.clear();
            limparEstilosValidacao();
        }
    }
    
    
    //Métodos Responsáveis por validação em tempo real
private void adicionarListeners() {
    
    txtNome.textProperty().addListener((obs, oldVal, newVal) ->{
        if(!newVal.isEmpty() && !newVal.equals(oldVal)) {
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


    txtAnosExperiencia.textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.isEmpty() && !newVal.equals(oldVal)) {
            boolean valido = Validacoes.validarAnosExperiencia(newVal).valido;
            aplicarEstiloValidacao(txtAnosExperiencia, valido);
        }
    });

    txtTamanhoPropiedade.textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.isEmpty() && !newVal.equals(oldVal)) {
            boolean valido = Validacoes.validarTamanhoPropriedade(newVal).valido;
            aplicarEstiloValidacao(txtTamanhoPropiedade, valido);
        }
    });

    txtBiografia.textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.isEmpty() && !newVal.equals(oldVal)) {
            boolean valido = Validacoes.validarBiografia(newVal).valido;
            aplicarEstiloValidacao(txtBiografia, valido);
        }
    });

    txtRaioEntrega.textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.isEmpty() && !newVal.equals(oldVal)) {
            boolean valido = Validacoes.validarRaioEntrega(newVal).valido;
            aplicarEstiloValidacao(txtRaioEntrega, valido);
        }
    });

    txtCustoEntrega.textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.isEmpty() && !newVal.equals(oldVal)) {
            boolean valido = Validacoes.validarCustoEntrega(newVal).valido;
            aplicarEstiloValidacao(txtCustoEntrega, valido);
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
        String senha = txtSenha.getText();
        String confirmacao = txtConfirmarSenha.getText();
        
        if (confirmacao.equals(senha)) {
            aplicarEstiloValidacao(txtConfirmarSenha, true);
        } else {
            aplicarEstiloValidacao(txtConfirmarSenha, false);
        }
    } else {
        limparEstilosValidacao();
    }
});

}
     
    private void aplicarEstiloValidacao(javafx.scene.Node node, boolean valido){
        String estiloValido = "-fx-border-color: #4CAF50; -fx-border-width: 2;";
        String estiloInvalido = "-fx-border-color: #F44336; -fx-border-width: 2;";
        
        if(node instanceof TextField) {
            TextField campo = (TextField) node;
            if(valido){
                campo.setStyle(campo.getStyle() + estiloValido);
            } else {
                campo.setStyle(campo.getStyle() + estiloInvalido);
            }
        } else if (node instanceof ComboBox) {
            ComboBox<?> combo = (ComboBox<?>) node;
            
            if(valido) {
                combo.setStyle(combo.getStyle() + estiloValido);
            }else{
                combo.setStyle(combo.getStyle() + estiloInvalido);
            }
        }
    }
    
    public void mostrarErroValidacao(Node node, String mensagem) {
        // Aplicar estilo de erro
        aplicarEstiloValidacao(node, false);
        
        VBox popup = new VBox(5);
        popup.setStyle(
            "-fx-background-color: #f44336;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 3;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);"
        );
        popup.setMaxWidth(250);
        
        // Triângulo apontando para cima (seta do popup)
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
            
            // Remover após 3 segundos
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
    javafx.scene.Node[] campos = {txtNome, txtEmail, txtTelefone, txtWhatsapp, txtSenha, txtConfirmarSenha,
                                 txtProvincia, txtDistrito, txtBairro, txtNumeroCasa, txtTipoAgricultura,
                                 txtHorarioAbertura, txtHorarioFechamento, txtPrazoEncomenda,
                                 comboCertificadoOrganico, comboAceitaVisitas, comboAceitaEncomendas, comboDisponivelContato};
    
    for (javafx.scene.Node campo : campos) {
        if (campo instanceof TextField) {
            ((TextField) campo).setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-prompt-text-fill: rgba(255,255,255,0.5); -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 5; -fx-background-radius: 5;");
        } else if (campo instanceof ComboBox) {
            ((ComboBox<?>) campo).setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 5; -fx-background-radius: 5;");
        }
    }
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
    
    private void aplicarValidacao(TextField campo, ResultadoValidacao resultado) {
        if (!resultado.valido) {
            mostrarErroValidacao(campo, resultado.mensagem);
        } else {
            aplicarEstiloValidacao(campo, true);
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
            ResultadoValidacao resultado = Validacoes.validarNome(txtNome.getText());
            if(!resultado.valido) {
                mostrarErroValidacao(txtNome, resultado.mensagem);
            } else {
                aplicarEstiloValidacao(txtNome, true);
            }
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
            ResultadoValidacao resultado = Validacoes.validarSenha(txtSenha.getText());
            if(!resultado.valido) {
                mostrarErroValidacao(txtSenha, resultado.mensagem);
            } else {
                aplicarEstiloValidacao(txtSenha, true);
            }
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
    private void validarEmail() {
        ResultadoValidacao resultado = Validacoes.validarEmail(txtEmail.getText());
        aplicarEstiloValidacao(txtEmail, resultado.valido);
    }

    @FXML
    private void validarEmailOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            ResultadoValidacao resultado = Validacoes.validarEmail(txtEmail.getText());
            if (!resultado.valido) {
                mostrarErroValidacao(txtEmail, resultado.mensagem);
            } else {
                aplicarEstiloValidacao(txtEmail, true);
            }
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
            ResultadoValidacao resultado = Validacoes.validarTelefone(txtTelefone.getText());
            if (!resultado.valido) {
                mostrarErroValidacao(txtTelefone, resultado.mensagem);
            } else {
                aplicarEstiloValidacao(txtTelefone, true);
            }
        }
    }

    @FXML
    private void validarProvincia() {
        validarComboBoxObrigatorio(txtProvincia, "Província");
    }
    
    @FXML
    private void validarProvinciaOnEnter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER) {
            validarProvincia();
        }
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
    private void validarNumeroCasa() {
        validarCampoObrigatorio(txtNumeroCasa, "Número da Casa");
    }

    @FXML
    private void validarNumeroCasaOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validarNumeroCasa();
        }
    }

    @FXML
    private void validarTipoAgricultura() {
        validarComboBoxObrigatorio(txtTipoAgricultura, "Tipo de Agricultura");
    }
    
    @FXML
    private void validarBiografia(KeyEvent event){
        ResultadoValidacao resultado = Validacoes.validarBiografia(txtBiografia.getText());
        aplicarEstiloValidacao(txtBiografia, resultado.valido);
    }
    
    @FXML
    private void validarAnosExperiencia(){
        ResultadoValidacao resultado = Validacoes.validarAnosExperiencia(txtAnosExperiencia.getText());
        aplicarEstiloValidacao(txtAnosExperiencia, resultado.valido);
    }
    
    @FXML
    private void validarAnosExperienciaOnEnter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            ResultadoValidacao resultado = Validacoes.validarAnosExperiencia(txtAnosExperiencia.getText());
            if(!resultado.valido){
                mostrarErroValidacao(txtAnosExperiencia, resultado.mensagem);
            }else{
                aplicarEstiloValidacao(txtAnosExperiencia, true);
            }
        }
    }
    
    @FXML
    private void validarTamanhoPropriedade(){
        ResultadoValidacao resultado = Validacoes.validarTamanhoPropriedade(txtTamanhoPropiedade.getText());
        aplicarEstiloValidacao(txtTamanhoPropiedade, resultado.valido);
    }
    
    @FXML
    private void validarTamanhoPropriedadeOnEnter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            ResultadoValidacao resultado = Validacoes.validarTamanhoPropriedade(txtTamanhoPropiedade.getText());
            if(!resultado.valido){
                mostrarErroValidacao(txtTamanhoPropiedade, resultado.mensagem);
            }else{
                aplicarEstiloValidacao(txtTamanhoPropiedade, true);
            }
        }
    }
    
    @FXML
    private void validarRaioEntrega(){
        ResultadoValidacao resultado = Validacoes.validarRaioEntrega(txtRaioEntrega.getText());
        aplicarEstiloValidacao(txtRaioEntrega, resultado.valido);
    }

    @FXML
    private void validarRaioEntregaOnEnter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            ResultadoValidacao resultado = Validacoes.validarRaioEntrega(txtRaioEntrega.getText());
            if(!resultado.valido){
                mostrarErroValidacao(txtRaioEntrega, resultado.mensagem);
            }else{
                aplicarEstiloValidacao(txtRaioEntrega, true);
            }
        }
    }

    @FXML
    private void validarCustoEntrega(){
        ResultadoValidacao resultado = Validacoes.validarCustoEntrega(txtCustoEntrega.getText());
        aplicarEstiloValidacao(txtCustoEntrega, resultado.valido);
    }

    @FXML
    private void validarCustoEntregaOnEnter(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            ResultadoValidacao resultado = Validacoes.validarCustoEntrega(txtCustoEntrega.getText());
            if(!resultado.valido){
                mostrarErroValidacao(txtCustoEntrega, resultado.mensagem);
            }else{
                aplicarEstiloValidacao(txtCustoEntrega, true);
            }
        }
    }
    
    @FXML
    private void validarOfereceEntrega() {
        validarComboBoxObrigatorio(txtOfereceEntrega, "Oferece Entrega");
    }
    
    // Adicione estes métodos na classe CadastroAgricultorController

    @FXML
    private void validarConfirmarSenhaOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validarConfirmarSenha();
        }
    }

    @FXML
    private void validarWhatsapp() {
        ResultadoValidacao resultado = Validacoes.validarTelefone(txtWhatsapp.getText());
        aplicarEstiloValidacao(txtWhatsapp, resultado.valido);
    }

    @FXML
    private void validarWhatsappOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            ResultadoValidacao resultado = Validacoes.validarTelefone(txtWhatsapp.getText());
            if (!resultado.valido) {
                mostrarErroValidacao(txtWhatsapp, resultado.mensagem);
            } else {
                aplicarEstiloValidacao(txtWhatsapp, true);
            }
        }
    }

    @FXML
    private void validarCertificadoOrganico() {
        validarComboBoxObrigatorio(comboCertificadoOrganico, "Certificado Orgânico");
    }

    @FXML
    private void validarAceitaVisitas() {
        validarComboBoxObrigatorio(comboAceitaVisitas, "Aceita Visitas");
    }

    @FXML
    private void onAceitaEncomendasChanged() {
        String selecao = comboAceitaEncomendas.getValue();
        boolean aceitaEncomendas = "Sim".equals(selecao);

        containerPrazoEncomenda.setVisible(aceitaEncomendas);
        containerPrazoEncomenda.setManaged(aceitaEncomendas);

        if (!aceitaEncomendas) {
            txtPrazoEncomenda.clear();
            limparEstilosValidacao();
        }
    }

    @FXML
    private void validarPrazoEncomenda() {
        ResultadoValidacao resultado = Validacoes.validarPrazoEncomenda(txtPrazoEncomenda.getText());
        aplicarEstiloValidacao(txtPrazoEncomenda, resultado.valido);
    }

    @FXML
    private void validarPrazoEncomendaOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            ResultadoValidacao resultado = Validacoes.validarPrazoEncomenda(txtPrazoEncomenda.getText());
            if (!resultado.valido) {
                mostrarErroValidacao(txtPrazoEncomenda, resultado.mensagem);
            } else {
                aplicarEstiloValidacao(txtPrazoEncomenda, true);
            }
        }
    }

    @FXML
    private void validarHorarioAbertura() {
        ResultadoValidacao resultado = Validacoes.validarHorario(txtHorarioAbertura.getText());
        aplicarEstiloValidacao(txtHorarioAbertura, resultado.valido);
    }

    @FXML
    private void validarHorarioAberturaOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            ResultadoValidacao resultado = Validacoes.validarHorario(txtHorarioAbertura.getText());
            if (!resultado.valido) {
                mostrarErroValidacao(txtHorarioAbertura, resultado.mensagem);
            } else {
                aplicarEstiloValidacao(txtHorarioAbertura, true);
            }
        }
    }

    @FXML
    private void validarHorarioFechamento() {
        ResultadoValidacao resultado = Validacoes.validarHorario(txtHorarioFechamento.getText());
        aplicarEstiloValidacao(txtHorarioFechamento, resultado.valido);
    }

    @FXML
    private void validarHorarioFechamentoOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            ResultadoValidacao resultado = Validacoes.validarHorario(txtHorarioFechamento.getText());
            if (!resultado.valido) {
                mostrarErroValidacao(txtHorarioFechamento, resultado.mensagem);
            } else {
                aplicarEstiloValidacao(txtHorarioFechamento, true);
            }
        }
    }

    @FXML
    private void validarDisponivelContato() {
        validarComboBoxObrigatorio(comboDisponivelContato, "Disponível para Contato");
    }
    
    
    

    @FXML
    public void handleCadastrarAgricultor() {
        System.out.println("✅ FXML inicializado! Configurando botão...");
        
        btnCadastrar.setOnAction(event -> {
            cadastrarAgricultor();
        });
        
        System.out.println("🎉 Controller totalmente configurado!");
    }

    private void cadastrarAgricultor() {
    try {
        String nome = txtNome.getText();
        String email = txtEmail.getText();
        String telefone = txtTelefone.getText();
        String whatsapp = txtWhatsapp.getText();
        String bairro = txtBairro.getText();
        String provincia = txtProvincia.getValue();
        String distrito = txtDistrito.getValue();
        String tipoAgricultura = txtTipoAgricultura.getValue();
        String biografia = txtBiografia.getText();
        String senha = txtSenha.getText();
        int anosExperiencia = Integer.parseInt(txtAnosExperiencia.getText());
        double tamanhoPropriedade = Double.parseDouble(txtTamanhoPropiedade.getText());
        boolean ofereceEntrega = "Sim".equals(txtOfereceEntrega.getValue());
        
        boolean certificadoOrganico = "Sim".equals(comboCertificadoOrganico.getValue());
        boolean aceitaVisitas = "Sim".equals(comboAceitaVisitas.getValue());
        boolean aceitaEncomendas = "Sim".equals(comboAceitaEncomendas.getValue());
        boolean disponivelParaContato = "Sim".equals(comboDisponivelContato.getValue());
        
        String horarioAbertura = txtHorarioAbertura.getText();
        String horarioFechamento = txtHorarioFechamento.getText();
        
        // Só obter raio, custo e prazo se oferecer entrega/encomendas
        double raioEntrega = ofereceEntrega ? Double.parseDouble(txtRaioEntrega.getText()) : 0.0;
        double custoEntrega = ofereceEntrega ? Double.parseDouble(txtCustoEntrega.getText()) : 0.0;
        int prazoMinimoEncomenda = aceitaEncomendas ? Integer.parseInt(txtPrazoEncomenda.getText()) : 1;
        
        boolean sucesso = usuarioService.cadastrarAgricultor(
            nome, 
            email, 
            telefone, 
            provincia, 
            distrito, 
            bairro,
            senha, 
            tipoAgricultura, 
            anosExperiencia, 
            biografia, 
            tamanhoPropriedade, 
            certificadoOrganico,
            ofereceEntrega,
            raioEntrega,   
            custoEntrega,
            whatsapp,
            aceitaVisitas,
            aceitaEncomendas,
            prazoMinimoEncomenda,
            horarioAbertura,
            horarioFechamento,
            disponivelParaContato
        );
           
        if (sucesso) {
            mostrarAlerta("Sucesso", "Agricultor cadastrado: " + nome);
            limparCampos();
        } else {
            mostrarAlerta("Erro", "Falha no cadastro");
        }

    } catch (Exception e) {
        System.err.println("💥 ERRO: " + e.getMessage());
        mostrarAlerta("Erro", "Erro: " + e.getMessage());
    }
}
    private void limparCampos() {
        txtNome.clear();
        txtEmail.clear();
        txtTelefone.clear();
        txtWhatsapp.clear();
        txtSenha.clear();
        txtConfirmarSenha.clear();
        comboCertificadoOrganico.getSelectionModel().clearSelection();
        comboAceitaVisitas.getSelectionModel().clearSelection();
        comboAceitaEncomendas.getSelectionModel().clearSelection();
        comboDisponivelContato.getSelectionModel().clearSelection();
        txtHorarioAbertura.clear();
        txtHorarioFechamento.clear();
        txtPrazoEncomenda.clear();
}

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    @FXML
    private void handleLimparCampos() {
        // Limpar todos os campos
        txtNome.clear();
        txtEmail.clear();
        txtTelefone.clear();
        txtProvincia.getSelectionModel().clearSelection();
        txtDistrito.getSelectionModel().clearSelection();
        txtBairro.clear();
        txtNumeroCasa.clear();
        txtTipoAgricultura.getSelectionModel().clearSelection();
        txtBiografia.clear();
        txtAnosExperiencia.clear();
        txtTamanhoPropiedade.clear();
        txtRaioEntrega.clear();
        txtCustoEntrega.clear();
        
        limparEstilosValidacao();
    }
}