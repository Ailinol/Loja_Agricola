package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class Produto {
    // Identificação
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String codigo;
    
    // Informações básicas
    private String nome;
    private String descricao;
    private String categoria;
    private String subcategoria;
    private String unidadeMedida;
    
    // Preço e quantidade
    private double preco;
    private int quantidadeDisponivel;
    private int quantidadeMinima;
    private boolean disponivel;
    
    // Informações do produtor
    private int agricultorId;
    private String agricultorNome;
    
    // Classificações e características
    private String qualidade;
    private boolean organico;
    private boolean sustentavel;
    
    @ElementCollection
    @CollectionTable(
        name = "produto_certificacoes",
        joinColumns = @JoinColumn(name = "produto_id")
    )
    @Column(name = "certificacao")
    private List<String> certificacoes;
    
    //private List<String> tags;
    
    // Datas importantes
    private LocalDate dataColheita;
    private LocalDate dataValidade;
    private LocalDate dataCadastro;
    
    // Imagens
    @ElementCollection
    @CollectionTable(
        name = "produto_imagem",
        joinColumns = @JoinColumn(name = "produto_id")
    )
    @Column(name = "imagem_URL")
    private List<String> imagens;
    private String imagemPrincipal;
    
    // Estatísticas
    private double classificacaoMedia;
    private int totalAvaliacoes;
    private int totalVendidos;
    
    // Informações para entrega
    private boolean perecivel;
    private int prazoValidadeDias;
    private boolean requerRefrigeracao;
    private double pesoUnitario;
    
    public Produto() {
        this.certificacoes = new ArrayList<>();
        //this.tags = new ArrayList<>();
        this.imagens = new ArrayList<>();
        this.dataCadastro = LocalDate.now();
        this.disponivel = true;
        this.classificacaoMedia = 0.0;
    }
    
    public Produto(String nome, String descricao, String categoria, double preco, int quantidadeDisponivel) {
        this();
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        this.quantidadeDisponivel = quantidadeDisponivel;
    }
    
    
    public int getAgricultorId() {
        return agricultorId;
    }
    
    public void setAgricultorId(int agricultorId) {
        this.agricultorId = agricultorId;
    }
    
    public String getAgricultorNome() {
        return agricultorNome;
    }
    
    public void setAgricultorNome(String agricultorNome) {
        this.agricultorNome = agricultorNome;
    }
    
    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }
    
    public String getCodigo() { 
        return codigo; 
    }
    public void setCodigo(String codigo) { 
        this.codigo = codigo;
    }
    
    public String getNome() { 
        return nome; 
    }
    public void setNome(String nome) { 
        this.nome = nome; 
    }
    
    public String getDescricao() { 
        return descricao; 
    }
    public void setDescricao(String descricao) { 
        this.descricao = descricao; 
    }
    
    public String getCategoria() { 
        return categoria; 
    }
    public void setCategoria(String categoria) { 
        this.categoria = categoria; 
    }
    
    public String getSubcategoria() { 
        return subcategoria; 
    }
    public void setSubcategoria(String subcategoria) { 
        this.subcategoria = subcategoria; 
    }
    
    public String getUnidadeMedida() { 
        return unidadeMedida; 
    }
    public void setUnidadeMedida(String unidadeMedida) { 
        this.unidadeMedida = unidadeMedida; 
    }
    
    public double getPreco() { 
        return preco; 
    }
    public void setPreco(double preco) { 
        this.preco = preco;
    }
    
    public int getQuantidadeDisponivel() { 
        return quantidadeDisponivel; 
    }
    public void setQuantidadeDisponivel(int quantidadeDisponivel) { 
        this.quantidadeDisponivel = quantidadeDisponivel; 
    }
    
    public int getQuantidadeMinima() { 
        return quantidadeMinima; 
    }
    public void setQuantidadeMinima(int quantidadeMinima) { 
        this.quantidadeMinima = quantidadeMinima;
    }
    
    public boolean isDisponivel() { 
        return disponivel; 
    }
    public void setDisponivel(boolean disponivel) { 
        this.disponivel = disponivel;
    }
    
    public String getQualidade() { 
        return qualidade; 
    }
    public void setQualidade(String qualidade) { 
        this.qualidade = qualidade; 
    }
    
    public boolean isOrganico() { 
        return organico; 
    }
    public void setOrganico(boolean organico) {
        this.organico = organico; 
    }
    
    public boolean isSustentavel() { 
        return sustentavel; 
    }
    public void setSustentavel(boolean sustentavel) { 
        this.sustentavel = sustentavel; 
    }
    
    public List<String> getCertificacoes() { 
        return new ArrayList<>(certificacoes);
    }
    public void setCertificacoes(List<String> certificacoes) { 
        this.certificacoes = certificacoes != null ? new ArrayList<>(certificacoes) : new ArrayList<>(); 
    }
   /* 
    public List<String> getTags() { 
        return new ArrayList<>(tags); 
    }
    public void setTags(List<String> tags) { 
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>(); 
    }
    */
    public LocalDate getDataColheita() { 
        return dataColheita; 
    }
    public void setDataColheita(LocalDate dataColheita) { 
        this.dataColheita = dataColheita; 
    }
    
    public LocalDate getDataValidade() { 
        return dataValidade; 
    }
    public void setDataValidade(LocalDate dataValidade) { 
        this.dataValidade = dataValidade; 
    }
    
    public LocalDate getDataCadastro() { 
        return dataCadastro; 
    }
    public void setDataCadastro(LocalDate dataCadastro) { 
        this.dataCadastro = dataCadastro; 
    }
    
    public List<String> getImagens() { 
        return new ArrayList<>(imagens); 
    }
    public void setImagens(List<String> imagens) { 
        this.imagens = imagens != null ? new ArrayList<>(imagens) : new ArrayList<>(); 
    }
    
    public String getImagemPrincipal() { 
        return imagemPrincipal; 
    }
    public void setImagemPrincipal(String imagemPrincipal) { 
        this.imagemPrincipal = imagemPrincipal;
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
    
    public int getTotalVendidos() { 
        return totalVendidos; 
    }
    public void setTotalVendidos(int totalVendidos) { 
        this.totalVendidos = totalVendidos; 
    }
    
    public boolean isPerecivel() { 
        return perecivel;
    }
    public void setPerecivel(boolean perecivel) { 
        this.perecivel = perecivel;
    }
    
    public int getPrazoValidadeDias() { 
        return prazoValidadeDias; 
    }
    public void setPrazoValidadeDias(int prazoValidadeDias) {
        this.prazoValidadeDias = prazoValidadeDias;
    }
    
    public boolean isRequerRefrigeracao() { 
        return requerRefrigeracao;
    }
    public void setRequerRefrigeracao(boolean requerRefrigeracao) { 
        this.requerRefrigeracao = requerRefrigeracao;
    }
    
    public double getPesoUnitario() { 
        return pesoUnitario; 
    }
    public void setPesoUnitario(double pesoUnitario) {
        this.pesoUnitario = pesoUnitario;
    }
    
    // Métodos de negócio
    public void adicionarImagem(String urlImagem) {
        this.imagens.add(urlImagem);
        if (this.imagemPrincipal == null) {
            this.imagemPrincipal = urlImagem;
        }
    }
    
    public boolean reduzirEstoque(int quantidade) {
        if (this.quantidadeDisponivel >= quantidade) {
            this.quantidadeDisponivel -= quantidade;
            this.totalVendidos += quantidade;
            
            if (this.quantidadeDisponivel < this.quantidadeMinima) {
                // Disparar alerta de estoque baixo
            }
            
            return true;
        }
        return false;
    }
    
    public void reporEstoque(int quantidade) {
        this.quantidadeDisponivel += quantidade;
    }
    
    public void atualizarClassificacaoMedia(int novaAvaliacao) {
        this.classificacaoMedia = (this.classificacaoMedia * this.totalAvaliacoes + novaAvaliacao) / (this.totalAvaliacoes + 1);
        this.totalAvaliacoes++;
    }
}