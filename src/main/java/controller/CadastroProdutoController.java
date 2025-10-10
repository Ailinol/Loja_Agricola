package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import model.Produto;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class CadastroProdutoController implements Initializable {

    // Aba 1: Informações Básicas
    @FXML private TextField txtNome;
    @FXML private TextField txtCodigo;
    @FXML private ComboBox<String> comboCategoria;
    @FXML private ComboBox<String> comboSubcategoria;
    @FXML private ComboBox<String> comboUnidadeMedida;
    @FXML private ComboBox<String> comboQualidade;
    @FXML private CheckBox checkOrganico;
    @FXML private CheckBox checkSustentavel;
    @FXML private CheckBox checkPerecivel;
    @FXML private CheckBox checkRequerRefrigeracao;

    // Aba 2: Preço e Estoque
    @FXML private TextField txtPreco;
    @FXML private TextField txtQuantidadeDisponivel;
    @FXML private TextField txtQuantidadeMinima;
    @FXML private TextField txtPesoUnitario;
    @FXML private ComboBox<String> comboDisponivel;
    @FXML private TextField txtPrazoValidade;
    @FXML private DatePicker dateDataColheita;
    @FXML private DatePicker dateDataValidade;

    // Aba 3: Descrição & Certificações
    @FXML private TextArea txtDescricao;
    @FXML private TextField txtCertificacoes;
    @FXML private TextField txtImagemPrincipal;
    @FXML private TextArea txtImagensAdicionais;
    @FXML private ComboBox<String> comboAgricultor;

    // Componentes gerais
    @FXML private TabPane tabPane;
    @FXML private Button btnCadastrar;
    @FXML private Button btnLimpar;

    private Map<String, List<String>> categoriasMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarComboboxes();
        configurarValidacoes();
        configurarDependencias();
    }

    private void inicializarComboboxes() {
        categoriasMap = new HashMap<>();
        categoriasMap.put("Hortaliças", Arrays.asList("Folhas", "Raízes", "Bulbos", "Flores"));
        categoriasMap.put("Frutas", Arrays.asList("Cítricas", "Tropicais", "Vermelhas", "Secas"));
        categoriasMap.put("Grãos", Arrays.asList("Cereais", "Leguminosas", "Oleaginosas"));
        categoriasMap.put("Leguminosas", Arrays.asList("Feijão", "Lentilha", "Grão-de-bico", "Ervilha"));
        categoriasMap.put("Tubérculos", Arrays.asList("Batata", "Mandioca", "Inhame", "Cará"));
        
        comboCategoria.getItems().addAll(categoriasMap.keySet());
        
        // Unidades de medida
        comboUnidadeMedida.getItems().addAll(
            "kg", "g", "unidade", "dúzia", "pacote", "caixa", "litro", "ml"
        );
        
        // Qualidades
        comboQualidade.getItems().addAll(
            "Premium", "Extra", "Primeira", "Segunda", "Comercial"
        );
        
        // Disponibilidade
        comboDisponivel.getItems().addAll("Sim", "Não");
        
        // Agricultores (em um sistema real, viria do banco de dados)
        comboAgricultor.getItems().addAll(
            "João Silva - Nampula",
            "Maria Santos - Maputo", 
            "Pedro Mondlane - Sofala",
            "Ana Muchanga - Zambézia"
        );
        
        // Configurar data de hoje como padrão para data de colheita
        dateDataColheita.setValue(LocalDate.now());
    }

    private void configurarValidacoes() {
        configurarMascaraNumerica(txtPreco);
        configurarMascaraNumerica(txtQuantidadeDisponivel);
        configurarMascaraNumerica(txtQuantidadeMinima);
        configurarMascaraDecimal(txtPesoUnitario);
        configurarMascaraNumerica(txtPrazoValidade);
    }

    private void configurarDependencias() {
        comboCategoria.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                comboSubcategoria.getItems().setAll(categoriasMap.get(newVal));
                comboSubcategoria.setValue(null);
            }
        });
        
        checkPerecivel.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                txtPrazoValidade.setDisable(false);
                dateDataValidade.setDisable(false);
            } else {
                txtPrazoValidade.setDisable(true);
                dateDataValidade.setDisable(true);
            }
        });
    }

    // Validacoes
    
    @FXML
    private void validarNome(ActionEvent event) {
        validarCampoObrigatorio(txtNome, "Nome do produto");
    }

    @FXML
    private void validarNomeOnEnter(javafx.scene.input.KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            validarCampoObrigatorio(txtNome, "Nome do produto");
        }
    }

    @FXML
    private void validarCodigo(ActionEvent event) {
        if (txtCodigo.getText().trim().isEmpty()) {
            String codigoAuto = "PROD" + System.currentTimeMillis();
            txtCodigo.setText(codigoAuto);
        }
    }

    @FXML
    private void validarCodigoOnEnter(javafx.scene.input.KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            validarCodigo(new ActionEvent());
        }
    }

    @FXML
    private void validarCategoria(ActionEvent event) {
        validarComboObrigatorio(comboCategoria, "Categoria");
    }

    @FXML
    private void validarSubcategoria(ActionEvent event) {
        // Subcategoria é opcional
    }

    @FXML
    private void validarUnidadeMedida(ActionEvent event) {
        validarComboObrigatorio(comboUnidadeMedida, "Unidade de medida");
    }

    @FXML
    private void validarQualidade(ActionEvent event) {
        // Qualidade é opcional
    }

    
    @FXML
    private void validarPreco(ActionEvent event) {
        if (!validarNumeroDecimal(txtPreco, "Preço")) {
            return;
        }
        
        double preco = Double.parseDouble(txtPreco.getText().replace(",", "."));
        if (preco <= 0) {
            mostrarErro(txtPreco, "O preço deve ser maior que zero");
        }
    }

    @FXML
    private void validarPrecoOnEnter(javafx.scene.input.KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            validarPreco(new ActionEvent());
        }
    }

    @FXML
    private void validarQuantidadeDisponivel(ActionEvent event) {
        if (!validarNumeroInteiro(txtQuantidadeDisponivel, "Quantidade disponível")) {
            return;
        }
        
        int quantidade = Integer.parseInt(txtQuantidadeDisponivel.getText());
        if (quantidade < 0) {
            mostrarErro(txtQuantidadeDisponivel, "A quantidade não pode ser negativa");
        }
    }

    @FXML
    private void validarQuantidadeDisponivelOnEnter(javafx.scene.input.KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            validarQuantidadeDisponivel(new ActionEvent());
        }
    }

    @FXML
    private void validarQuantidadeMinima(ActionEvent event) {
        if (!txtQuantidadeMinima.getText().trim().isEmpty()) {
            if (!validarNumeroInteiro(txtQuantidadeMinima, "Quantidade mínima")) {
                return;
            }
            
            int quantidadeMin = Integer.parseInt(txtQuantidadeMinima.getText());
            if (quantidadeMin < 0) {
                mostrarErro(txtQuantidadeMinima, "A quantidade mínima não pode ser negativa");
            }
        }
    }

    @FXML
    private void validarQuantidadeMinimaOnEnter(javafx.scene.input.KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            validarQuantidadeMinima(new ActionEvent());
        }
    }

    @FXML
    private void validarPesoUnitario(ActionEvent event) {
        if (!txtPesoUnitario.getText().trim().isEmpty()) {
            if (!validarNumeroDecimal(txtPesoUnitario, "Peso unitário")) {
                return;
            }
            
            double peso = Double.parseDouble(txtPesoUnitario.getText().replace(",", "."));
            if (peso <= 0) {
                mostrarErro(txtPesoUnitario, "O peso deve ser maior que zero");
            }
        }
    }

    @FXML
    private void validarPesoUnitarioOnEnter(javafx.scene.input.KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            validarPesoUnitario(new ActionEvent());
        }
    }

    @FXML
    private void validarDisponivel(ActionEvent event) {
        // Campo opcional
    }

    @FXML
    private void validarPrazoValidade(ActionEvent event) {
        if (!txtPrazoValidade.getText().trim().isEmpty()) {
            if (!validarNumeroInteiro(txtPrazoValidade, "Prazo de validade")) {
                return;
            }
            
            int prazo = Integer.parseInt(txtPrazoValidade.getText());
            if (prazo <= 0) {
                mostrarErro(txtPrazoValidade, "O prazo de validade deve ser maior que zero");
            }
        }
    }

    @FXML
    private void validarPrazoValidadeOnEnter(javafx.scene.input.KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            validarPrazoValidade(new ActionEvent());
        }
    }
    
    @FXML
    private void validarDescricao(javafx.scene.input.KeyEvent event) {
    }

    @FXML
    private void validarCertificacoes(ActionEvent event) {
        // Campo opcional
    }

    @FXML
    private void validarCertificacoesOnEnter(javafx.scene.input.KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            validarCertificacoes(new ActionEvent());
        }
    }

    @FXML
    private void validarImagemPrincipal(ActionEvent event) {
        String url = txtImagemPrincipal.getText().trim();
        if (!url.isEmpty() && !url.startsWith("http")) {
            mostrarAlerta("URL da imagem", "A URL da imagem principal pode não ser válida.");
        }
    }

    @FXML
    private void validarImagemPrincipalOnEnter(javafx.scene.input.KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            validarImagemPrincipal(new ActionEvent());
        }
    }

    @FXML
    private void validarImagensAdicionais(javafx.scene.input.KeyEvent event) {
        // Validação em tempo real opcional
    }

    @FXML
    private void validarAgricultor(ActionEvent event) {
        validarComboObrigatorio(comboAgricultor, "Agricultor");
    }

    // Metodos principais
    
    @FXML
    private void handleCadastrarProduto(ActionEvent event) {
        if (validarFormularioCompleto()) {
            try {
                Produto produto = criarProdutoFromFormulario();
                
                // AUsar banco de dados
                salvarProduto(produto);
                
                mostrarSucesso("Produto cadastrado com sucesso!");
                handleLimparCampos(new ActionEvent());
                
            } catch (Exception e) {
                mostrarErro("Erro ao cadastrar produto: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLimparCampos(ActionEvent event) {
        // Limpar Aba 1
        txtNome.clear();
        txtCodigo.clear();
        comboCategoria.setValue(null);
        comboSubcategoria.setValue(null);
        comboUnidadeMedida.setValue(null);
        comboQualidade.setValue(null);
        checkOrganico.setSelected(false);
        checkSustentavel.setSelected(false);
        checkPerecivel.setSelected(false);
        checkRequerRefrigeracao.setSelected(false);
        
        // Limpar Aba 2
        txtPreco.clear();
        txtQuantidadeDisponivel.clear();
        txtQuantidadeMinima.clear();
        txtPesoUnitario.clear();
        comboDisponivel.setValue("Sim");
        txtPrazoValidade.clear();
        dateDataColheita.setValue(LocalDate.now());
        dateDataValidade.setValue(null);
        
        // Limpar Aba 3
        txtDescricao.clear();
        txtCertificacoes.clear();
        txtImagemPrincipal.clear();
        txtImagensAdicionais.clear();
        comboAgricultor.setValue(null);
        
        tabPane.getSelectionModel().select(0);
        removerEstilosErro();
    }

    // Validacoes auxiliares
    
    private boolean validarFormularioCompleto() {
        List<String> erros = new ArrayList<>();
        
        if (txtNome.getText().trim().isEmpty()) {
            erros.add("Nome do produto é obrigatório");
            mostrarErro(txtNome, "Campo obrigatório");
        }
        
        if (comboCategoria.getValue() == null) {
            erros.add("Categoria é obrigatória");
            mostrarErro(comboCategoria, "Campo obrigatório");
        }
        
        if (comboUnidadeMedida.getValue() == null) {
            erros.add("Unidade de medida é obrigatória");
            mostrarErro(comboUnidadeMedida, "Campo obrigatório");
        }
        
        if (txtPreco.getText().trim().isEmpty()) {
            erros.add("Preço é obrigatório");
            mostrarErro(txtPreco, "Campo obrigatório");
        } else if (!validarNumeroDecimal(txtPreco, "Preço")) {
            erros.add("Preço deve ser um número válido");
        }
        
        if (txtQuantidadeDisponivel.getText().trim().isEmpty()) {
            erros.add("Quantidade disponível é obrigatória");
            mostrarErro(txtQuantidadeDisponivel, "Campo obrigatório");
        } else if (!validarNumeroInteiro(txtQuantidadeDisponivel, "Quantidade disponível")) {
            erros.add("Quantidade disponível deve ser um número inteiro válido");
        }
        
        if (comboAgricultor.getValue() == null) {
            erros.add("Agricultor é obrigatório");
            mostrarErro(comboAgricultor, "Campo obrigatório");
        }
        
        if (erros.isEmpty()) {
            return true;
        } else {
            mostrarErroValidacao(erros);
            return false;
        }
    }
    
    private Produto criarProdutoFromFormulario() {
        Produto produto = new Produto();
        
        produto.setNome(txtNome.getText().trim());
        produto.setCodigo(txtCodigo.getText().trim());
        produto.setCategoria(comboCategoria.getValue());
        produto.setSubcategoria(comboSubcategoria.getValue());
        produto.setUnidadeMedida(comboUnidadeMedida.getValue());
        produto.setQualidade(comboQualidade.getValue());
        produto.setOrganico(checkOrganico.isSelected());
        produto.setSustentavel(checkSustentavel.isSelected());
        
        produto.setPreco(Double.parseDouble(txtPreco.getText().replace(",", ".")));
        produto.setQuantidadeDisponivel(Integer.parseInt(txtQuantidadeDisponivel.getText()));
        
        if (!txtQuantidadeMinima.getText().trim().isEmpty()) {
            produto.setQuantidadeMinima(Integer.parseInt(txtQuantidadeMinima.getText()));
        }
        
        produto.setDisponivel(comboDisponivel.getValue() != null && 
                            comboDisponivel.getValue().equals("Sim"));
        
        produto.setPerecivel(checkPerecivel.isSelected());
        produto.setRequerRefrigeracao(checkRequerRefrigeracao.isSelected());
        
        if (!txtPrazoValidade.getText().trim().isEmpty()) {
            produto.setPrazoValidadeDias(Integer.parseInt(txtPrazoValidade.getText()));
        }
        
        if (!txtPesoUnitario.getText().trim().isEmpty()) {
            produto.setPesoUnitario(Double.parseDouble(txtPesoUnitario.getText().replace(",", ".")));
        }
        
        produto.setDataColheita(dateDataColheita.getValue());
        produto.setDataValidade(dateDataValidade.getValue());
        
        produto.setDescricao(txtDescricao.getText().trim());
        
        if (!txtCertificacoes.getText().trim().isEmpty()) {
            List<String> certificacoes = Arrays.stream(txtCertificacoes.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            produto.setCertificacoes(certificacoes);
        }
        
        // Processar imagens
        if (!txtImagemPrincipal.getText().trim().isEmpty()) {
            produto.setImagemPrincipal(txtImagemPrincipal.getText().trim());
            produto.adicionarImagem(txtImagemPrincipal.getText().trim());
        }
        
        if (!txtImagensAdicionais.getText().trim().isEmpty()) {
            Arrays.stream(txtImagensAdicionais.getText().split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(produto::adicionarImagem);
        }
        
        String agricultorSelecionado = comboAgricultor.getValue();
        if (agricultorSelecionado != null) {
            produto.setAgricultorNome(agricultorSelecionado);
            // produto.setAgricultorId(agricultor.getId());
        }
        
        return produto;
    }
    
    // Metodos auxiliares
    
    private void configurarMascaraNumerica(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    private void configurarMascaraDecimal(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*(\\.[0-9]*)?")) {
                field.setText(newValue.replaceAll("[^0-9.]", ""));
            }
        });
    }
    
    private boolean validarCampoObrigatorio(TextField field, String nomeCampo) {
        if (field.getText().trim().isEmpty()) {
            mostrarErro(field, nomeCampo + " é obrigatório");
            return false;
        }
        removerErro(field);
        return true;
    }
    
    private boolean validarComboObrigatorio(ComboBox<?> combo, String nomeCampo) {
        if (combo.getValue() == null) {
            mostrarErro(combo, nomeCampo + " é obrigatório");
            return false;
        }
        removerErro(combo);
        return true;
    }
    
    private boolean validarNumeroInteiro(TextField field, String nomeCampo) {
        try {
            Integer.parseInt(field.getText());
            removerErro(field);
            return true;
        } catch (NumberFormatException e) {
            mostrarErro(field, nomeCampo + " deve ser um número inteiro válido");
            return false;
        }
    }
    
    private boolean validarNumeroDecimal(TextField field, String nomeCampo) {
        try {
            Double.parseDouble(field.getText().replace(",", "."));
            removerErro(field);
            return true;
        } catch (NumberFormatException e) {
            mostrarErro(field, nomeCampo + " deve ser um número válido");
            return false;
        }
    }
    
    private void mostrarErro(Control control, String mensagem) {
        control.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
        control.setTooltip(new Tooltip(mensagem));
    }
    
    private void removerErro(Control control) {
        control.setStyle("");
        control.setTooltip(null);
    }
    
    private void removerEstilosErro() {
        Control[] controls = {
            txtNome, txtCodigo, comboCategoria, comboSubcategoria, comboUnidadeMedida,
            comboQualidade, txtPreco, txtQuantidadeDisponivel, txtQuantidadeMinima,
            txtPesoUnitario, comboDisponivel, txtPrazoValidade, comboAgricultor
        };
        
        for (Control control : controls) {
            removerErro(control);
        }
    }
    
    private void mostrarErroValidacao(List<String> erros) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro de Validação");
        alert.setHeaderText("Por favor, corrija os seguintes erros:");
        alert.setContentText(String.join("\n", erros));
        alert.showAndWait();
    }
    
    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void salvarProduto(Produto produto) {
        
    }
}