package model;
import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.*;

@Entity
public class Agricultor extends Usuario {
    
    @OneToMany
    @JoinColumn(name = "agricultor_id")
    private List<Produto> produtos;
    
    @OneToMany
    @JoinColumn(name = "agricultor_id")
    private List<Avaliacao> avaliacoes; 
    
    private String tipoAgricultura;
    private double tamanhoPropriedade;
    private int anosExperiencia;
    private String biografia;
    
    @Transient
    private String fotoPerfil;
            
    private boolean certificadoOrganico;
    
    @Transient
    private boolean certificadoSustentavel;
    /*
    @ElementCollection
    @CollectionTable(
        name = "agricultor_certificacoes",
        joinColumns = @JoinColumn(name = "agricultor_id")
    )
    @Column(name = "certificacao")
    */
    @Transient
    private List<String> outrasCertificacoes;
    @Transient
    private String horarioFuncionamento;
    
    private boolean ofereceEntrega;
    private double raioEntrega;
    private double custoEntrega;
    
    @Transient
    private LocalDateTime dataCadastroComoAgricultor;
    @Transient
    private double classificacaoMedia; 
    @Transient
    private int totalAvaliacoes; 
    
    // Novos atributos
    @Transient
    private String whatsapp;
    @Transient
    private boolean aceitaVisitas;
    @Transient
    private boolean aceitaEncomendas;
    @Transient
    private int prazoMinimoEncomenda;
    
    /*
    @ElementCollection
    @CollectionTable(
        name = "agricultor_metodos_contato",
        joinColumns = @JoinColumn(name = "agricultor_id")
    )
    @Column(name = "metodo_contato")
    */
    @Transient
    private List<String> metodosContato;
    @Transient
    private boolean disponivelParaContato;
    @Transient
    private int totalClientesAtendidos;
    @Transient
    private boolean recomendado;

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
                     String biografia, boolean certificadoOrganico, boolean ofereceEntrega, 
                     double raioEntrega, double custoEntrega) {
        super(senha, nome, email, telefone, provincia, distrito, bairro, rua, numeroCasa);
        inicializarAtributos();
        this.tipoAgricultura = tipoAgricultura;
        this.tamanhoPropriedade = tamanhoPropriedade;
        this.anosExperiencia = anosExperiencia;
        this.biografia = biografia;
        this.certificadoOrganico = certificadoOrganico;
        this.ofereceEntrega = ofereceEntrega;
        this.raioEntrega = raioEntrega;
        this.custoEntrega = custoEntrega;
    }

    public Agricultor(List<Produto> produtos, List<Avaliacao> avaliacoes) {
        super();
        inicializarAtributos();
        this.produtos = produtos != null ? new ArrayList<>(produtos) : new ArrayList<>();
        this.avaliacoes = avaliacoes != null ? new ArrayList<>(avaliacoes) : new ArrayList<>();
    }
    
    // Método privado para inicializar atributos comuns
    private void inicializarAtributos() {
        this.produtos = new ArrayList<>();
        this.avaliacoes = new ArrayList<>();
        this.outrasCertificacoes = new ArrayList<>();
        this.metodosContato = new ArrayList<>();
        this.classificacaoMedia = 0.0;
        this.dataCadastroComoAgricultor = LocalDateTime.now();
        this.ofereceEntrega = false;
        this.raioEntrega = 0.0;
        this.custoEntrega = 0.0;
        this.tipoAgricultura = "FAMILIAR";
        this.totalAvaliacoes = 0;
        this.totalClientesAtendidos = 0;
        this.disponivelParaContato = true;
        this.aceitaVisitas = false;
        this.aceitaEncomendas = false;
        this.prazoMinimoEncomenda = 1;
    }
    
    // GETTERS E SETTERS
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
    
    // Novos getters e setters
    public String getWhatsapp() {
        return whatsapp;
    }
    
    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }
    
    public boolean isAceitaVisitas() {
        return aceitaVisitas;
    }
    
    public void setAceitaVisitas(boolean aceitaVisitas) {
        this.aceitaVisitas = aceitaVisitas;
    }
    
    public boolean isAceitaEncomendas() {
        return aceitaEncomendas;
    }
    
    public void setAceitaEncomendas(boolean aceitaEncomendas) {
        this.aceitaEncomendas = aceitaEncomendas;
    }
    
    public int getPrazoMinimoEncomenda() {
        return prazoMinimoEncomenda;
    }
    
    public void setPrazoMinimoEncomenda(int prazoMinimoEncomenda) {
        this.prazoMinimoEncomenda = prazoMinimoEncomenda;
    }
    
    public List<String> getMetodosContato() {
        return new ArrayList<>(metodosContato);
    }
    
    public void setMetodosContato(List<String> metodosContato) {
        this.metodosContato = metodosContato != null ? new ArrayList<>(metodosContato) : new ArrayList<>();
    }
    
    public boolean isDisponivelParaContato() {
        return disponivelParaContato;
    }
    
    public void setDisponivelParaContato(boolean disponivelParaContato) {
        this.disponivelParaContato = disponivelParaContato;
    }
    
    public int getTotalClientesAtendidos() {
        return totalClientesAtendidos;
    }
    
    public void setTotalClientesAtendidos(int totalClientesAtendidos) {
        this.totalClientesAtendidos = totalClientesAtendidos;
    }
    
    public boolean isRecomendado() {
        return recomendado;
    }
    
    public void setRecomendado(boolean recomendado) {
        this.recomendado = recomendado;
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
            this.atualizarClassificacaoMedia();
            this.totalAvaliacoes++;
        }
    }
    
    private void atualizarClassificacaoMedia() {
        if (avaliacoes.isEmpty()) {
            this.classificacaoMedia = 0.0;
            return;
        }
        
       // double soma = avaliacoes.stream()
         //   .mapToInt(Avaliacao::getClassificacao)
        //    .sum();
        //this.classificacaoMedia = soma / avaliacoes.size();
    }
    
    public void adicionarCertificacao(String certificacao) {
        if (!outrasCertificacoes.contains(certificacao)) {
            outrasCertificacoes.add(certificacao);
        }
    }
    
    public void adicionarMetodoContato(String metodo) {
        if (!metodosContato.contains(metodo)) {
            metodosContato.add(metodo);
        }
    }
    
    public boolean podeEntregar(String provinciaCliente, String distritoCliente) {
        if (!ofereceEntrega) return false;
        return this.getProvincia().equalsIgnoreCase(provinciaCliente) && 
               this.getDistrito().equalsIgnoreCase(distritoCliente);
    }
    
    public List<Produto> getProdutosDisponiveis() {
        List<Produto> resultado = new ArrayList<>();
        for (Produto produto : produtos) {
            if (produto.isDisponivel()) {
                resultado.add(produto);
            }
        }
        return resultado;
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
        return certificadoOrganico || "ORGANICO".equalsIgnoreCase(tipoAgricultura);
    }
    
    public int getTempoComoAgricultor() {
        return LocalDateTime.now().getYear() - dataCadastroComoAgricultor.getYear();
    }
    
    public String getReputacao() {
        if (totalAvaliacoes == 0) {
            return "Sem avaliações";
        }
        return String.format("⭐ %.1f/5.0 (%d avaliações)", classificacaoMedia, totalAvaliacoes);
    }
    
    public void incrementarClientesAtendidos() {
        this.totalClientesAtendidos++;
    }
    
    public boolean estaAtivo() {
        return !produtos.isEmpty() && disponivelParaContato;
    }
}