package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Node;
import model.Produto;
import model.Agricultor;
import service.ProdutoService;
import service.UsuarioService;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import service.ResultadoValidacao;

public class CadastroProdutoController implements Initializable {

    // Serviços
    private ProdutoService produtoService;
    private UsuarioService usuarioService;
    private Agricultor agricultorLogado;

    // Variáveis para modo edição (NOVAS)
    private Produto produtoEditando;
    private boolean modoEdicao = false;

    // Aba 1: Informações Básicas
    @FXML private TextField txtNome;
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
    @FXML private DatePicker dateDataColheita;
    @FXML private DatePicker dateDataValidade;

    // Aba 3: Descrição & Certificações
    @FXML private TextArea txtDescricao;
    @FXML private TextField txtImagemPrincipal;
    @FXML private TextArea txtImagensAdicionais;

    // Componentes gerais
    @FXML private TabPane tabPane;
    @FXML private Button btnCadastrar;
    @FXML private Button btnLimpar;
    @FXML private Label lblTitulo; // Adicione este Label no FXML

    private Map<String, List<String>> categoriasMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar serviços
        this.produtoService = new ProdutoService();
        this.usuarioService = new UsuarioService();
        
        // Inicializar agricultor (por enquanto busca o primeiro disponível)
        inicializarAgricultor();
        
        inicializarComboboxes();
        configurarValidacoes();
        configurarDependencias();
        
        // Atualizar interface baseada no modo
        atualizarInterfaceParaModo();
    }

    /**
     * Configura o controller para modo de edição
     */
    public void setProdutoParaEdicao(Produto produto) {
        this.produtoEditando = produto;
        this.modoEdicao = true;
        preencherCamposComDadosProduto();
        atualizarInterfaceParaModo();
    }

    /**
     * Preenche todos os campos com os dados do produto em edição
     */
    private void preencherCamposComDadosProduto() {
        if (produtoEditando != null) {
            System.out.println("📝 Preenchendo campos para edição: " + produtoEditando.getNome());
            
            // Aba 1: Informações Básicas
            txtNome.setText(produtoEditando.getNome());
            
            if (produtoEditando.getCategoria() != null) {
                comboCategoria.setValue(produtoEditando.getCategoria());
            }
            
            if (produtoEditando.getSubcategoria() != null) {
                comboSubcategoria.setValue(produtoEditando.getSubcategoria());
            }
            
            if (produtoEditando.getUnidadeMedida() != null) {
                comboUnidadeMedida.setValue(produtoEditando.getUnidadeMedida());
            }
            
            if (produtoEditando.getQualidade() != null) {
                comboQualidade.setValue(produtoEditando.getQualidade());
            }
            
            checkOrganico.setSelected(produtoEditando.isOrganico());
            checkSustentavel.setSelected(produtoEditando.isSustentavel());
            checkPerecivel.setSelected(produtoEditando.isPerecivel());
            checkRequerRefrigeracao.setSelected(produtoEditando.isRequerRefrigeracao());
            
            // Aba 2: Preço e Estoque
            txtPreco.setText(String.format("%.2f", produtoEditando.getPreco()).replace(".", ","));
            txtQuantidadeDisponivel.setText(String.valueOf(produtoEditando.getQuantidadeDisponivel()));
            txtQuantidadeMinima.setText(String.valueOf(produtoEditando.getQuantidadeMinima()));
            txtPesoUnitario.setText(String.valueOf(produtoEditando.getPesoUnitario()));
            
            comboDisponivel.setValue(produtoEditando.isDisponivel() ? "Sim" : "Não");
            
            if (produtoEditando.getDataColheita() != null) {
                dateDataColheita.setValue(produtoEditando.getDataColheita());
            }
            
            if (produtoEditando.getDataValidade() != null) {
                dateDataValidade.setValue(produtoEditando.getDataValidade());
            }
            
            // Aba 3: Descrição & Certificações
            txtDescricao.setText(produtoEditando.getDescricao());
            
            if (produtoEditando.getImagemPrincipal() != null) {
                txtImagemPrincipal.setText(produtoEditando.getImagemPrincipal());
            }
            
            // Atualizar título da janela
            atualizarTituloJanela();
        }
    }

    /**
     * Atualiza a interface baseada no modo (cadastro/edição)
     */
    private void atualizarInterfaceParaModo() {
        if (modoEdicao) {
            if (lblTitulo != null) {
                lblTitulo.setText("Editar Produto");
            }
            if (btnCadastrar != null) {
                btnCadastrar.setText("Atualizar");
                btnCadastrar.setStyle("-fx-background-color: #ffa500; -fx-text-fill: white;");
            }
        } else {
            if (lblTitulo != null) {
                lblTitulo.setText("Cadastro de Novo Produto");
            }
            if (btnCadastrar != null) {
                btnCadastrar.setText("Cadastrar");
                btnCadastrar.setStyle("");
            }
        }
    }

    /**
     * Atualiza o título da janela para modo edição
     */
    private void atualizarTituloJanela() {
        try {
            Stage stage = (Stage) txtNome.getScene().getWindow();
            if (modoEdicao && produtoEditando != null) {
                stage.setTitle("Editar Produto - " + produtoEditando.getNome());
            } else {
                stage.setTitle("Cadastro de Novo Produto");
            }
        } catch (Exception e) {
            System.out.println("Não foi possível atualizar o título da janela: " + e.getMessage());
        }
    }

    private void inicializarAgricultor() {
        try {
            // Buscar algum agricultor no banco para teste
            List<Agricultor> agricultores = usuarioService.listarAgricultores();
            if (!agricultores.isEmpty()) {
                this.agricultorLogado = agricultores.get(0);
                System.out.println("Agricultor para cadastro: " + agricultorLogado.getNome());
            } else {
                System.err.println("Nenhum agricultor cadastrado no sistema");
                mostrarAlerta("Configuração Necessária", 
                    "Nenhum agricultor encontrado. Cadastre um agricultor primeiro.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar agricultor: " + e.getMessage());
            mostrarErro("Erro de conexão", "Não foi possível conectar ao banco de dados.");
        }
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
            "Fresco","Colhido a dias", "Colhido a semanas"
        );
        
        // Disponibilidade
        comboDisponivel.getItems().addAll("Sim", "Não");
        comboDisponivel.setValue("Sim");
        
        // Configurar data de hoje como padrão para data de colheita
        dateDataColheita.setValue(LocalDate.now());
    }

    private void configurarValidacoes() {
        configurarMascaraNumerica(txtPreco);
        configurarMascaraNumerica(txtQuantidadeDisponivel);
        configurarMascaraNumerica(txtQuantidadeMinima);
        configurarMascaraDecimal(txtPesoUnitario);
    }

    private void configurarDependencias() {
        comboCategoria.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                comboSubcategoria.getItems().setAll(categoriasMap.get(newVal));
                comboSubcategoria.setValue(null);
            }
        });
    }

    // Métodos principais - ATUALIZADOS
    @FXML
    private void handleCadastrarProduto(ActionEvent event) {
        if (validarFormularioCompleto()) {
            try {
                if (agricultorLogado == null) {
                    mostrarErro("Erro de Configuração", "Nenhum agricultor disponível para cadastrar o produto.");
                    return;
                }

                if (modoEdicao) {
                    // MODO EDIÇÃO - Atualizar produto existente
                    atualizarProdutoExistente();
                } else {
                    // MODO CADASTRO - Criar novo produto
                    criarNovoProduto();
                }
                
            } catch (Exception e) {
                mostrarErro("Erro no Processamento", "Erro ao processar produto: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Atualiza um produto existente com os dados do formulário
     */
    private void atualizarProdutoExistente() {
        try {
            // Atualiza os dados do produto existente
            produtoEditando.setNome(txtNome.getText().trim());
            produtoEditando.setCategoria(comboCategoria.getValue());
            produtoEditando.setSubcategoria(comboSubcategoria.getValue());
            produtoEditando.setUnidadeMedida(comboUnidadeMedida.getValue());
            produtoEditando.setQualidade(comboQualidade.getValue());
            produtoEditando.setOrganico(checkOrganico.isSelected());
            produtoEditando.setSustentavel(checkSustentavel.isSelected());
            produtoEditando.setPerecivel(checkPerecivel.isSelected());
            produtoEditando.setRequerRefrigeracao(checkRequerRefrigeracao.isSelected());
            
            // Preço e estoque
            produtoEditando.setPreco(Double.parseDouble(txtPreco.getText().replace(",", ".")));
            produtoEditando.setQuantidadeDisponivel(Integer.parseInt(txtQuantidadeDisponivel.getText()));
            
            if (!txtQuantidadeMinima.getText().trim().isEmpty()) {
                produtoEditando.setQuantidadeMinima(Integer.parseInt(txtQuantidadeMinima.getText()));
            }
            
            if (!txtPesoUnitario.getText().trim().isEmpty()) {
                produtoEditando.setPesoUnitario(Double.parseDouble(txtPesoUnitario.getText().replace(",", ".")));
            }
            
            produtoEditando.setDisponivel(comboDisponivel.getValue() != null && 
                                        comboDisponivel.getValue().equals("Sim"));
            
            produtoEditando.setDataColheita(dateDataColheita.getValue());
            produtoEditando.setDataValidade(dateDataValidade.getValue());
            
            // Descrição
            produtoEditando.setDescricao(txtDescricao.getText().trim());
            
            // Imagens
            if (!txtImagemPrincipal.getText().trim().isEmpty()) {
                produtoEditando.setImagemPrincipal(txtImagemPrincipal.getText().trim());
            }
            
            // Chama o service para atualizar
            ResultadoValidacao sucesso = produtoService.actualizarProduto(produtoEditando);
            
            if (sucesso.valido == true) {
                mostrarSucesso("Produto atualizado com sucesso!\n" +
                              "Nome: " + produtoEditando.getNome() + "\n" +
                              "Preço: " + produtoEditando.getPreco() + " MT");
                fecharTela();
            } else {
                mostrarErro("Falha na Atualização", "Não foi possível atualizar o produto no banco de dados.");
            }
            
        } catch (Exception e) {
            mostrarErro("Erro na Atualização", "Erro ao atualizar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cria um novo produto
     */
    private void criarNovoProduto() {
        Produto produto = criarProdutoFromFormulario();
        
        System.out.println("Tentando cadastrar produto: " + produto.getNome());
        System.out.println("👨‍🌾 Agricultor: " + agricultorLogado.getNome());
        
        boolean sucesso = produtoService.cadastrarProduto(produto, agricultorLogado);
        
        if (sucesso) {
            mostrarSucesso("Produto cadastrado com sucesso!\n" +
                          "Nome: " + produto.getNome() + "\n" +
                          "Preço: " + produto.getPreco() + " MT\n" +
                          "Quantidade: " + produto.getQuantidadeDisponivel() + " " + produto.getUnidadeMedida());
            handleLimparCampos(new ActionEvent());
        } else {
            mostrarErro("Falha no Cadastro", "Não foi possível cadastrar o produto no banco de dados.");
        }
    }

    @FXML
    private void handleLimparCampos(ActionEvent event) {
        // Se estiver em modo edição, não limpa completamente - apenas reseta para valores originais
        if (modoEdicao && produtoEditando != null) {
            preencherCamposComDadosProduto(); // Recarrega os dados originais
            removerEstilosErro();
            return;
        }
        
        // Limpar completa (modo cadastro)
        // Aba 1
        txtNome.clear();
        comboCategoria.setValue(null);
        comboSubcategoria.setValue(null);
        comboUnidadeMedida.setValue(null);
        comboQualidade.setValue(null);
        checkOrganico.setSelected(false);
        checkSustentavel.setSelected(false);
        checkPerecivel.setSelected(false);
        checkRequerRefrigeracao.setSelected(false);
        
        // Aba 2
        txtPreco.clear();
        txtQuantidadeDisponivel.clear();
        txtQuantidadeMinima.clear();
        txtPesoUnitario.clear();
        comboDisponivel.setValue("Sim");
        dateDataColheita.setValue(LocalDate.now());
        dateDataValidade.setValue(null);
        
        // Aba 3
        txtDescricao.clear();
        txtImagemPrincipal.clear();
        txtImagensAdicionais.clear();
        
        tabPane.getSelectionModel().select(0);
        removerEstilosErro();
        
        // Resetar modo edição
        this.modoEdicao = false;
        this.produtoEditando = null;
        atualizarInterfaceParaModo();
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        fecharTela();
    }

    /**
     * Fecha a tela atual
     */
    private void fecharTela() {
        try {
            Stage stage = (Stage) txtNome.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.out.println("Erro ao fechar tela: " + e.getMessage());
        }
    }

    // VALIDAÇÕES
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

    // MÉTODOS AUXILIARES
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
        
        if (agricultorLogado == null) {
            erros.add("Nenhum agricultor disponível para associar o produto");
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
        
        // Informações básicas
        produto.setNome(txtNome.getText().trim());
        produto.setCategoria(comboCategoria.getValue());
        produto.setSubcategoria(comboSubcategoria.getValue());
        produto.setUnidadeMedida(comboUnidadeMedida.getValue());
        produto.setQualidade(comboQualidade.getValue());
        produto.setOrganico(checkOrganico.isSelected());
        produto.setSustentavel(checkSustentavel.isSelected());
        produto.setPerecivel(checkPerecivel.isSelected());
        produto.setRequerRefrigeracao(checkRequerRefrigeracao.isSelected());
        
        produto.setPreco(Double.parseDouble(txtPreco.getText().replace(",", ".")));
        produto.setQuantidadeDisponivel(Integer.parseInt(txtQuantidadeDisponivel.getText()));
        
        if (!txtQuantidadeMinima.getText().trim().isEmpty()) {
            produto.setQuantidadeMinima(Integer.parseInt(txtQuantidadeMinima.getText()));
        } else {
            produto.setQuantidadeMinima(5); // Valor padrão
        }
        
        produto.setDisponivel(comboDisponivel.getValue() != null && 
                            comboDisponivel.getValue().equals("Sim"));
        
        if (!txtPesoUnitario.getText().trim().isEmpty()) {
            produto.setPesoUnitario(Double.parseDouble(txtPesoUnitario.getText().replace(",", ".")));
        } else {
            produto.setPesoUnitario(1.0); // Valor padrão
        }
        
        produto.setDataColheita(dateDataColheita.getValue());
        produto.setDataValidade(dateDataValidade.getValue());
        
        // Descrição
        produto.setDescricao(txtDescricao.getText().trim());
        
        // Imagens
        if (!txtImagemPrincipal.getText().trim().isEmpty()) {
            produto.setImagemPrincipal(txtImagemPrincipal.getText().trim());
        }
        
        if (!txtImagensAdicionais.getText().trim().isEmpty()) {
            List<String> imagensAdicionais = Arrays.stream(txtImagensAdicionais.getText().split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            produto.setImagens(imagensAdicionais);
        }
        
        // Valores padrão
        produto.setClassificacaoMedia(0.0);
        produto.setTotalAvaliacoes(0);
        produto.setTotalVendidos(0);
        
        return produto;
    }
    
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
            txtNome, comboCategoria, comboSubcategoria, comboUnidadeMedida,
            comboQualidade, txtPreco, txtQuantidadeDisponivel, txtQuantidadeMinima,
            txtPesoUnitario, comboDisponivel
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
    
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
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

    // Método para definir o agricultor logado
    public void setAgricultorLogado(Agricultor agricultor) {
        this.agricultorLogado = agricultor;
        if (agricultor != null) {
            System.out.println("✅ Agricultor definido no controller: " + agricultor.getNome());
        }
    }

    // Método para limpar recursos
    public void fecharServicos() {
        try {
            if (produtoService != null) {
                produtoService.fecharConexao();
            }
            if (usuarioService != null) {
                usuarioService.fecharConexao();
            }
            System.out.println("🔒 Serviços fechados com sucesso");
        } catch (Exception e) {
            System.err.println("Erro ao fechar serviços: " + e.getMessage());
        }
    }

    // Getters para modo de operação (úteis para testes)
    public boolean isModoEdicao() {
        return modoEdicao;
    }

    public Produto getProdutoEditando() {
        return produtoEditando;
    }
}