package model;
import java.time.LocalDateTime;
import java.util.*;

public class Agricultor extends Usuario {
    private List<Produto> produtos;
    private List<Avaliacao> avaliacoes;
    private double saldo;
    private double saldoBloqueado;
    private String tipoAgricultura;
    private double tamanhoPropriedade;
    private int anosExperiencia;
    private String biografia;
    private String fotoPerfil;
    private String metodoPagamentoPreferido;
    private String contaBancaria;
    private String nib;
    private boolean certificadoOrganico;
    private boolean certificadoSustentavel;
    private List<String> outrasCertificacoes;
    private String horarioFuncionamento;
    private boolean ofereceEntrega;
    private double raioEntrega;
    private double custoEntrega;
    private double classificacaoMedia;
    private int totalVendas;
    private int totalAvaliacoes;
    private LocalDateTime dataCadastroComoAgricultor;
    private List<Transacao> historicoTransacoes;
    
    // Construtor padrão
    public Agricultor() {
        super();
        inicializarAtributos();
    }
    
    // Construtor com senha apenas
    public Agricultor(String senha) {
        super(senha);
        inicializarAtributos();
    }
    
    // Construtor básico com informações essenciais
    public Agricultor(String senha, String nome, String email, String telefone, 
                     String provincia, String distrito, String bairro) {
        super(senha, nome, email, telefone, provincia, distrito, bairro, null, null);
        inicializarAtributos();
    }
    
    // Construtor completo para cadastro
    public Agricultor(String senha, String nome, String email, String telefone, 
                     String provincia, String distrito, String bairro, String rua, String numeroCasa,
                     String tipoAgricultura, double tamanhoPropriedade, int anosExperiencia,
                     String biografia, boolean certificadoOrganico) {
        super(senha, nome, email, telefone, provincia, distrito, bairro, rua, numeroCasa);
        inicializarAtributos();
        this.tipoAgricultura = tipoAgricultura;
        this.tamanhoPropriedade = tamanhoPropriedade;
        this.anosExperiencia = anosExperiencia;
        this.biografia = biografia;
        this.certificadoOrganico = certificadoOrganico;
    }
    
    // Construtor para testes ou carga de dados
    public Agricultor(List<Produto> produtos, List<Avaliacao> avaliacoes, double saldo) {
        super();
        inicializarAtributos();
        this.produtos = produtos != null ? new ArrayList<>(produtos) : new ArrayList<>();
        this.avaliacoes = avaliacoes != null ? new ArrayList<>(avaliacoes) : new ArrayList<>();
        this.saldo = saldo;
    }
    
    // Método privado para inicializar atributos comuns
    private void inicializarAtributos() {
        this.produtos = new ArrayList<>();
        this.avaliacoes = new ArrayList<>();
        this.outrasCertificacoes = new ArrayList<>();
        this.historicoTransacoes = new ArrayList<>();
        this.saldo = 0.0;
        this.saldoBloqueado = 0.0;
        this.classificacaoMedia = 0.0;
        this.dataCadastroComoAgricultor = LocalDateTime.now();
        this.ofereceEntrega = false;
        this.raioEntrega = 0.0;
        this.tipoAgricultura = "FAMILIAR";
        this.totalVendas = 0;
        this.totalAvaliacoes = 0;
    }
    
    // RESTANTE DOS GETTERS, SETTERS E MÉTODOS DE NEGÓCIO 
    // (mantidos iguais da versão anterior)
    
    public List<Produto> getProdutos() { 
        return new ArrayList<>(produtos); 
    }
    
    public void setProdutos(List<Produto> produtos) { 
        this.produtos = produtos != null ? new ArrayList<>(produtos) : new ArrayList<>();
    }
    
    public List<Avaliacao> getAvaliacoes() { 
        return new ArrayList<>(avaliacoes); 
    }
    
    public void setAvaliacoes(List<Avaliacao> avaliacoes) { 
        this.avaliacoes = avaliacoes != null ? new ArrayList<>(avaliacoes) : new ArrayList<>();
    }
    
    public double getSaldo() { 
        return saldo; 
    }
    
    public void setSaldo(double saldo) { 
        this.saldo = saldo; 
    }
    
    public double getSaldoBloqueado() {
        return saldoBloqueado;
    }
    
    public void setSaldoBloqueado(double saldoBloqueado) {
        this.saldoBloqueado = saldoBloqueado;
    }
    
    public double getSaldoDisponivel() {
        return saldo - saldoBloqueado;
    }
    
    public String getTipoAgricultura() {
        return tipoAgricultura;
    }
    
    public void setTipoAgricultura(String tipoAgricultura) {
        this.tipoAgricultura = tipoAgricultura;
    }
    
    public double getTamanhoPropriedade() {
        return tamanhoPropriedade;
    }
    
    public void setTamanhoPropriedade(double tamanhoPropriedade) {
        this.tamanhoPropriedade = tamanhoPropriedade;
    }
    
    public int getAnosExperiencia() {
        return anosExperiencia;
    }
    
    public void setAnosExperiencia(int anosExperiencia) {
        this.anosExperiencia = anosExperiencia;
    }
    
    public String getBiografia() {
        return biografia;
    }
    
    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }
    
    public String getFotoPerfil() {
        return fotoPerfil;
    }
    
    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
    
    public String getMetodoPagamentoPreferido() {
        return metodoPagamentoPreferido;
    }
    
    public void setMetodoPagamentoPreferido(String metodoPagamentoPreferido) {
        this.metodoPagamentoPreferido = metodoPagamentoPreferido;
    }
    
    public String getContaBancaria() {
        return contaBancaria;
    }
    
    public void setContaBancaria(String contaBancaria) {
        this.contaBancaria = contaBancaria;
    }
    
    public String getNib() {
        return nib;
    }
    
    public void setNib(String nib) {
        this.nib = nib;
    }
    
    public boolean isCertificadoOrganico() {
        return certificadoOrganico;
    }
    
    public void setCertificadoOrganico(boolean certificadoOrganico) {
        this.certificadoOrganico = certificadoOrganico;
    }
    
    public boolean isCertificadoSustentavel() {
        return certificadoSustentavel;
    }
    
    public void setCertificadoSustentavel(boolean certificadoSustentavel) {
        this.certificadoSustentavel = certificadoSustentavel;
    }
    
    public List<String> getOutrasCertificacoes() {
        return new ArrayList<>(outrasCertificacoes);
    }
    
    public void setOutrasCertificacoes(List<String> outrasCertificacoes) {
        this.outrasCertificacoes = outrasCertificacoes != null ? new ArrayList<>(outrasCertificacoes) : new ArrayList<>();
    }
    
    public void adicionarCertificacao(String certificacao) {
        if (!outrasCertificacoes.contains(certificacao)) {
            outrasCertificacoes.add(certificacao);
        }
    }
    
    public String getHorarioFuncionamento() {
        return horarioFuncionamento;
    }
    
    public void setHorarioFuncionamento(String horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }
    
    public boolean isOfereceEntrega() {
        return ofereceEntrega;
    }
    
    public void setOfereceEntrega(boolean ofereceEntrega) {
        this.ofereceEntrega = ofereceEntrega;
    }
    
    public double getRaioEntrega() {
        return raioEntrega;
    }
    
    public void setRaioEntrega(double raioEntrega) {
        this.raioEntrega = raioEntrega;
    }
    
    public double getCustoEntrega() {
        return custoEntrega;
    }
    
    public void setCustoEntrega(double custoEntrega) {
        this.custoEntrega = custoEntrega;
    }
    
    public double getClassificacaoMedia() {
        return classificacaoMedia;
    }
    
    public void setClassificacaoMedia(double classificacaoMedia) {
        this.classificacaoMedia = classificacaoMedia;
    }
    
    public int getTotalVendas() {
        return totalVendas;
    }
    
    public void setTotalVendas(int totalVendas) {
        this.totalVendas = totalVendas;
    }
    
    public int getTotalAvaliacoes() {
        return totalAvaliacoes;
    }
    
    public void setTotalAvaliacoes(int totalAvaliacoes) {
        this.totalAvaliacoes = totalAvaliacoes;
    }
    
    public LocalDateTime getDataCadastroComoAgricultor() {
        return dataCadastroComoAgricultor;
    }
    
    public void setDataCadastroComoAgricultor(LocalDateTime dataCadastroComoAgricultor) {
        this.dataCadastroComoAgricultor = dataCadastroComoAgricultor;
    }
    
    public List<Transacao> getHistoricoTransacoes() {
        return new ArrayList<>(historicoTransacoes);
    }
    
    public void setHistoricoTransacoes(List<Transacao> historicoTransacoes) {
        this.historicoTransacoes = historicoTransacoes != null ? new ArrayList<>(historicoTransacoes) : new ArrayList<>();
    }
    
    // Métodos de negócio
    @Override
    public String getInfo() {
        return String.format("Agricultor: %s - Email: %s - Localização: %s, %s - Produtos: %d - Avaliações: %d - Classificação: %.1f/5.0", 
                           getNome(), getEmail(), getBairro(), getDistrito(), 
                           produtos.size(), avaliacoes.size(), classificacaoMedia);
    }
    
    public void cadastrarProduto(Produto p) {
        if (p != null) {
            p.setAgricultorId(this.getId());
            p.setAgricultorNome(this.getNome());
            this.produtos.add(p);
        }
    }
    
    public void removerProduto(int prodId) {
        produtos.removeIf(p -> p.getId() == prodId);
    }
    
    public void atualizarProduto(Produto p) {
        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getId() == p.getId()) {
                produtos.set(i, p);
                break;
            }
        }
    }
    
    public void receberAvaliacao(Avaliacao a) {
        if (a != null) {
            this.avaliacoes.add(a);
            //this.atualizarClassificacaoMedia(a.getClassificacao());
            this.totalAvaliacoes++;
        }
    }
    
    private void atualizarClassificacaoMedia(int novaClassificacao) {
        if (totalAvaliacoes == 0) {
            this.classificacaoMedia = novaClassificacao;
        } else {
            this.classificacaoMedia = (classificacaoMedia * totalAvaliacoes + novaClassificacao) / (totalAvaliacoes + 1);
        }
    }
    
    public void creditarSaldo(double valor) {
        this.saldo += valor;
        this.registrarTransacao("CREDITO", valor, "Crédito no saldo");
    }
    
    public boolean debitarSaldo(double valor) {
        if (this.saldo >= valor) {
            this.saldo -= valor;
            this.registrarTransacao("DEBITO", valor, "Débito no saldo");
            return true;
        }
        return false;
    }
    
    public void bloquearSaldo(double valor) {
        if (this.saldo >= valor) {
            this.saldoBloqueado += valor;
            this.saldo -= valor;
        }
    }
    
    public void liberarSaldoBloqueado(double valor) {
        if (this.saldoBloqueado >= valor) {
            this.saldoBloqueado -= valor;
            this.saldo += valor;
        }
    }
    
    private void registrarTransacao(String tipo, double valor, String descricao) {
        Transacao transacao = new Transacao(tipo, valor, descricao, LocalDateTime.now());
        this.historicoTransacoes.add(transacao);
    }
    
    public boolean podeEntregar(String provinciaCliente, String distritoCliente) {
        if (!ofereceEntrega) return false;
        
        return this.getProvincia().equalsIgnoreCase(provinciaCliente) && 
               this.getDistrito().equalsIgnoreCase(distritoCliente);
    }
    
    public double calcularDistanciaParaEntrega(Comprador cliente) {
        if (!ofereceEntrega || raioEntrega <= 0) return -1;
        
        double distancia = this.calcularDistancia(cliente);
        return distancia <= raioEntrega ? distancia : -1;
    }
    
    public int calcularTempoEntrega(Comprador cliente) {
        double distancia = calcularDistanciaParaEntrega(cliente);
        if (distancia == -1) return -1;
        
        return (int) (distancia * 10 + 30);
    }
    
    public List<Produto> getProdutosPorCategoria(String categoria) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto produto : produtos) {
            if (produto.getCategoria().equalsIgnoreCase(categoria) && produto.isDisponivel()) {
                resultado.add(produto);
            }
        }
        return resultado;
    }
    
    public boolean isProdutorOrganico() {
        return certificadoOrganico || tipoAgricultura.equalsIgnoreCase("ORGANICO");
    }
    
    public int getTempoComoAgricultor() {
        return LocalDateTime.now().getYear() - dataCadastroComoAgricultor.getYear();
    }
    
    // Classe interna para transações
    public class Transacao {
        private String tipo;
        private double valor;
        private String descricao;
        private LocalDateTime data;
        
        public Transacao(String tipo, double valor, String descricao, LocalDateTime data) {
            this.tipo = tipo;
            this.valor = valor;
            this.descricao = descricao;
            this.data = data;
        }
        
        public String getTipo() { return tipo; }
        public double getValor() { return valor; }
        public String getDescricao() { return descricao; }
        public LocalDateTime getData() { return data; }
    }
}