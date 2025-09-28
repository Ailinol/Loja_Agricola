package model;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.*;

@Entity
public class Comprador extends Usuario implements Serializable {
    
   // @Id
    //@GeneratedValue (strategy=GenerationType.IDENTITY)
    
    @ElementCollection
    @CollectionTable(
        name = "Comprador_preferencias",
        joinColumns = @JoinColumn(name = "comprador_id")
    )
    @Column(name = "preferencias")
    private List<String> preferenciasCategorias;
    private double saldo;
    
    @OneToMany
    @JoinColumn(name = "comprador_id") //adiciona a FK na tabela Produto
    private List<Produto> carrinhoCompras;
   // private List<Compra> historicoCompras;
    private double valorTotalGasto;
    private int totalCompras;
    private int pontosFidelidade;
    
   /* @ManyToMany
    @JoinTable(
        name = "comprador_endereco_entrega",
        joinColumns = @JoinColumn(name = "comprador_id"),
        inverseJoinColumns = @JoinColumn(name = "endereco_id")
    )
    private List<Endereco> enderecosEntrega;*/
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco enderecoPadrao;
    
    @ManyToMany
    @JoinTable(
        name = "comprador_metodo_pagamento",
        joinColumns = @JoinColumn(name = "comprador_id"),
        inverseJoinColumns = @JoinColumn(name = "metodo_pagamento_id")
    )
    private List<MetodoPagamento> metodosPagamento;
    private double raioBuscaPreferido;
    private boolean recebeNewsletter;
    
    public Comprador() {
        super();
        inicializarAtributos();
    }
    
    public Comprador(String senha) {
        super(senha);
        inicializarAtributos();
    }
    
    public Comprador(String senha, String nome, String email, String telefone, 
                    String provincia, String distrito, String bairro) {
        super(senha, nome, email, telefone, provincia, distrito, bairro, null, null);
        inicializarAtributos();
    }
    
    public Comprador(String senha, String nome, String email, String telefone, 
                    String provincia, String distrito, String bairro, double saldo) {
        super(senha, nome, email, telefone, provincia, distrito, bairro, null, null);
        inicializarAtributos();
        this.saldo = saldo;
    }
    
    public Comprador(String senha, String nome, String email, String telefone, 
                    String provincia, String distrito, String bairro, 
                    List<String> preferenciasCategorias, double saldo) {
        super(senha, nome, email, telefone, provincia, distrito, bairro, null, null);
        inicializarAtributos();
        this.preferenciasCategorias = preferenciasCategorias != null ? new ArrayList<>(preferenciasCategorias) : new ArrayList<>();
        this.saldo = saldo;
    }
    
    // Método para inicializar atributos comuns
    private void inicializarAtributos() {
        this.preferenciasCategorias = new ArrayList<>();
        this.carrinhoCompras = new ArrayList<>();
       // this.historicoCompras = new ArrayList<>();
        //this.enderecosEntrega = new ArrayList<>();
        this.metodosPagamento = new ArrayList<>();
        this.saldo = 0.0;
        this.valorTotalGasto = 0.0;
        this.totalCompras = 0;
        this.pontosFidelidade = 0;
        this.raioBuscaPreferido = 10.0; // Raio padrão de 10km
        this.recebeNewsletter = true;
    }
    
    // Getters e Setters
    public List<String> getPreferenciasCategorias() { 
        return new ArrayList<>(preferenciasCategorias); 
    }
    
    public void setPreferenciasCategorias(List<String> preferenciasCategorias) { 
        this.preferenciasCategorias = preferenciasCategorias != null ? new ArrayList<>(preferenciasCategorias) : new ArrayList<>();
    }
    
    public double getSaldo() { 
        return saldo; 
    }
    
    public void setSaldo(double saldo) { 
        this.saldo = saldo; 
    }
    
    public List<Produto> getCarrinhoCompras() { 
        return new ArrayList<>(carrinhoCompras); 
    }
    
    public void setCarrinhoCompras(List<Produto> carrinhoCompras) { 
        this.carrinhoCompras = carrinhoCompras != null ? new ArrayList<>(carrinhoCompras) : new ArrayList<>();
    }
    
  /**  public List<Compra> getHistoricoCompras() {
        return new ArrayList<>(historicoCompras);
    }
    
    public void setHistoricoCompras(List<Compra> historicoCompras) {
        this.historicoCompras = historicoCompras != null ? new ArrayList<>(historicoCompras) : new ArrayList<>();
    }**/
    
    public double getValorTotalGasto() {
        return valorTotalGasto;
    }
    
    public void setValorTotalGasto(double valorTotalGasto) {
        this.valorTotalGasto = valorTotalGasto;
    }
    
    public int getTotalCompras() {
        return totalCompras;
    }
    
    public void setTotalCompras(int totalCompras) {
        this.totalCompras = totalCompras;
    }
    
    public int getPontosFidelidade() {
        return pontosFidelidade;
    }
    
    public void setPontosFidelidade(int pontosFidelidade) {
        this.pontosFidelidade = pontosFidelidade;
    }
    /*
    public List<Endereco> getEnderecosEntrega() {
        return new ArrayList<>(enderecosEntrega);
    }
    
    public void setEnderecosEntrega(List<Endereco> enderecosEntrega) {
        this.enderecosEntrega = enderecosEntrega != null ? new ArrayList<>(enderecosEntrega) : new ArrayList<>();
    }
    */
    public Endereco getEnderecoPadrao() {
        return enderecoPadrao;
    }
    
    public void setEnderecoPadrao(Endereco enderecoPadrao) {
        this.enderecoPadrao = enderecoPadrao;
    }
    
    public List<MetodoPagamento> getMetodosPagamento() {
        return new ArrayList<>(metodosPagamento);
    }
    
    public void setMetodosPagamento(List<MetodoPagamento> metodosPagamento) {
        this.metodosPagamento = metodosPagamento != null ? new ArrayList<>(metodosPagamento) : new ArrayList<>();
    }
    
    public double getRaioBuscaPreferido() {
        return raioBuscaPreferido;
    }
    
    public void setRaioBuscaPreferido(double raioBuscaPreferido) {
        this.raioBuscaPreferido = raioBuscaPreferido;
    }
    
    public boolean isRecebeNewsletter() {
        return recebeNewsletter;
    }
    
    public void setRecebeNewsletter(boolean recebeNewsletter) {
        this.recebeNewsletter = recebeNewsletter;
    }
    
    @Override
    public String getInfo() {
        return String.format("Comprador: %s - Email: %s - Localização: %s, %s - Compras: %d - Saldo: R$%.2f", 
                           getNome(), getEmail(), getBairro(), getDistrito(), 
                           totalCompras, saldo);
    }
    
    // Métodos específicos do Comprador
    public void adicionarPreferencia(String categoria) {
        if (categoria != null && !preferenciasCategorias.contains(categoria)) {
            preferenciasCategorias.add(categoria);
        }
    }
    
    public void removerPreferencia(String categoria) {
        preferenciasCategorias.remove(categoria);
    }
    
    public void adicionarProdutoCarrinho(Produto produto) {
        if (produto != null && produto.isDisponivel() && produto.getQuantidadeDisponivel() > 0) {
            carrinhoCompras.add(produto);
        }
    }
    
    public void removerProdutoCarrinho(Produto produto) {
        carrinhoCompras.remove(produto);
    }
    
    public void limparCarrinho() {
        carrinhoCompras.clear();
    }
    
    public double calcularTotalCarrinho() {
        return carrinhoCompras.stream()
                .mapToDouble(Produto::getPreco)
                .sum();
    }
    
    public boolean temSaldoSuficiente(double valor) {
        return saldo >= valor;
    }
    
    public boolean debitarSaldo(double valor) {
        if (temSaldoSuficiente(valor)) {
            saldo -= valor;
            return true;
        }
        return false;
    }
    
    public void creditarSaldo(double valor) {
        if (valor > 0) {
            saldo += valor;
        }
    }
    /*
    public void adicionarEndereco(Endereco endereco) {
        this.enderecosEntrega.add(endereco);
        if (this.enderecoPadrao == null) {
            this.enderecoPadrao = endereco;
        }
    }*/
    
    public void adicionarMetodoPagamento(MetodoPagamento metodo) {
        this.metodosPagamento.add(metodo);
    }
    
   /** public void adicionarCompra(Compra compra) {
        this.historicoCompras.add(compra);
        this.totalCompras++;
        this.valorTotalGasto += compra.getValorTotal();
        this.pontosFidelidade += (int) (compra.getValorTotal() / 10); // 1 ponto a cada 10 unidades monetárias
    }**/
    
    public List<Produto> verRecomendacoesBaseadasLocalizacao(List<Agricultor> agricultores) {
        List<Produto> recomendacoes = new ArrayList<>();
        
        for (Agricultor agricultor : agricultores) {
            double distancia = this.calcularDistancia(agricultor);
            
            // Se o agricultor está dentro do raio de busca preferido
            if (distancia <= this.raioBuscaPreferido && distancia >= 0) {
                for (Produto produto : agricultor.getProdutos()) {
                    if (produto.isDisponivel() && 
                        (preferenciasCategorias.isEmpty() || 
                         preferenciasCategorias.contains(produto.getCategoria()))) {
                        recomendacoes.add(produto);
                    }
                }
            }
        }
        
        // Ordenar por proximidade (implementação simplificada)
        recomendacoes.sort((p1, p2) -> {
            double dist1 = this.calcularDistancia(obterAgricultorDoProduto(p1, agricultores));
            double dist2 = this.calcularDistancia(obterAgricultorDoProduto(p2, agricultores));
            return Double.compare(dist1, dist2);
        });
        
        return recomendacoes;
    }
    
    private Agricultor obterAgricultorDoProduto(Produto produto, List<Agricultor> agricultores) {
        for (Agricultor agricultor : agricultores) {
            if (agricultor.getId() == produto.getAgricultorId()) {
                return agricultor;
            }
        }
        return null;
    }
    
    public List<Produto> buscarProdutosPorCategoria(String categoria, List<Agricultor> agricultores) {
        List<Produto> resultados = new ArrayList<>();
        
        for (Agricultor agricultor : agricultores) {
            double distancia = this.calcularDistancia(agricultor);
            
            // Considerar apenas agricultores dentro do raio de busca
            if (distancia <= this.raioBuscaPreferido && distancia >= 0) {
                for (Produto produto : agricultor.getProdutos()) {
                    if (produto.isDisponivel() && produto.getCategoria().equalsIgnoreCase(categoria)) {
                        resultados.add(produto);
                    }
                }
            }
        }
        
        return resultados;
    }
    
   /** public Avaliacao avaliarProduto(Produto produto, int nota, String comentario) {
        if (produto != null && nota >= 1 && nota <= 5) {
            // Verificar se o comprador já comprou este produto
            boolean comprouProduto = historicoCompras.stream()
                .anyMatch(compra -> compra.getItens().stream()
                    .anyMatch(item -> item.getProdutoId() == produto.getId()));
            
            if (comprouProduto) {
                Avaliacao avaliacao = new Avaliacao();
                avaliacao.setProdutoId(produto.getId());
                avaliacao.setCompradorId(this.getId());
                avaliacao.setClassificacao(nota);
                avaliacao.setComentario(comentario);
                avaliacao.setDataAvaliacao(LocalDateTime.now());
                avaliacao.setVerificada(true);
                
                return avaliacao;
            }
        }
        return null;
    }**/
    
    // Classes internas para representar estruturas de dados
    @Entity
    public class Endereco {
        @Id
        @GeneratedValue (strategy=GenerationType.IDENTITY)
        private int id;
        private String tipo; // CASA, TRABALHO, etc.
        private String rua;
        private String numero;
        private String complemento;
        private String bairro;
        private String distrito;
        private String provincia;
        private String codigoPostal;
        private boolean principal;
        
        // Construtor, getters e setters
        public Endereco(String tipo, String rua, String numero, String bairro, String distrito, String provincia) {
            this.tipo = tipo;
            this.rua = rua;
            this.numero = numero;
            this.bairro = bairro;
            this.distrito = distrito;
            this.provincia = provincia;
        }
        

        // Getters e Setters
        public int getId() { return id; }
        public void setId(int id) {this.id = id;}
        
        public String getTipo() {return tipo;}
        public void setTipo(String tipo) { this.tipo = tipo; }
        
        public String getRua() { return rua; }
        public void setRua(String rua) { this.rua = rua; }
        
        public String getNumero() { return numero; }
        public void setNumero(String numero) { this.numero = numero; }
        
        public String getComplemento() { return complemento; }
        public void setComplemento(String complemento) { this.complemento = complemento; }
        
        public String getBairro() { return bairro; }
        public void setBairro(String bairro) { this.bairro = bairro; }
        
        public String getDistrito() { return distrito; }
        public void setDistrito(String distrito) { this.distrito = distrito; }
        
        public String getProvincia() { return provincia; }
        public void setProvincia(String provincia) { this.provincia = provincia; }
        
        public String getCodigoPostal() { return codigoPostal; }
        public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
        
        public boolean isPrincipal() { return principal; }
        public void setPrincipal(boolean principal) { this.principal = principal; }
        
        public String getEnderecoCompleto() {
            return String.format("%s, %s, %s - %s, %s", rua, numero, bairro, distrito, provincia);
        }
    }
    
    @Entity
    public class MetodoPagamento {
        @Id
        @GeneratedValue (strategy=GenerationType.IDENTITY)
        private int id;
        private String tipo; // CARTAO, TRANSFERENCIA, etc.
        private String descricao;
        private String ultimosDigitos; // Para cartões
        private boolean preferido;
        
        // Construtor, getters e setters
        public MetodoPagamento(String tipo, String descricao) {
            this.tipo = tipo;
            this.descricao = descricao;
        }
             

        // Getters e Setters
        public int getId() { return id; }
        public void setId(int id) {this.id = id;}

        public String getTipo() {
            return tipo;
        }
        public void setTipo(String tipo) { this.tipo = tipo; }
        
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        
        public String getUltimosDigitos() { return ultimosDigitos; }
        public void setUltimosDigitos(String ultimosDigitos) { this.ultimosDigitos = ultimosDigitos; }
        
        public boolean isPreferido() { return preferido; }
        public void setPreferido(boolean preferido) { this.preferido = preferido; }
        
        
    }
}