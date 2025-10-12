package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import service.SenhaService;

@MappedSuperclass
public abstract class Usuario extends Pessoa {
    private String senha;
    @Transient
    private String salt; 
    @Transient
    private LocalDateTime ultimoLogin;
    @Transient
    private String status;
    @Transient
    private int tentativasLogin;
    @Transient
    private boolean contaBloqueada;
    @Transient
    private LocalDateTime dataExpiracaoSenha;
    @Transient
    private List<String> historicoSenhas;
    @Transient
    private List<String> permissoes;
    @Transient
    private String tokenRecuperacao;
    @Transient
    private LocalDateTime expiracaoTokenRecuperacao;
    @Transient
    private boolean receberNotificacoes;
    @Transient
    private String preferenciaComunicacao;
    
    public Usuario() {
        super();
        this.status = "ATIVO";
        this.ultimoLogin = null;
        this.tentativasLogin = 0;
        this.contaBloqueada = false;
        this.dataExpiracaoSenha = LocalDateTime.now().plusMonths(3); // Expira em 3 meses
        this.historicoSenhas = new ArrayList<>();
        this.permissoes = new ArrayList<>();
        this.receberNotificacoes = true;
        this.preferenciaComunicacao = "EMAIL";
        this.latitude = 0.0;
        this.longitude = 0.0;
    }
    
    public Usuario(String senha) {
        this();
        this.senha = senha;
    }
    
    public Usuario(String senha, String nome, String email, String telefone, 
                  String provincia, String distrito, String bairro,double latitude, double longitude) {
        super(nome, email, telefone, provincia, distrito, bairro, null, null, latitude,longitude);
        this.senha = senha;
        this.status = "ATIVO";
        this.ultimoLogin = null;
        this.tentativasLogin = 0;
        this.contaBloqueada = false;
        this.dataExpiracaoSenha = LocalDateTime.now().plusMonths(3);
        this.historicoSenhas = new ArrayList<>();
        this.permissoes = new ArrayList<>();
        this.receberNotificacoes = true;
        this.preferenciaComunicacao = "EMAIL";
    }
    
    
     public Usuario(String senha, String nome, String email, String telefone, 
                  String provincia, String distrito, String bairro, String rua, String numeroCasa, double latitude, double longitude) {
        super(nome, email, telefone, provincia, distrito, bairro, rua, numeroCasa, latitude, longitude);
        this.senha = senha;
        this.status = "ATIVO";
        this.ultimoLogin = null;
        this.tentativasLogin = 0;
        this.contaBloqueada = false;
        this.dataExpiracaoSenha = LocalDateTime.now().plusMonths(3);
        this.historicoSenhas = new ArrayList<>();
        this.permissoes = new ArrayList<>();
        this.receberNotificacoes = true;
        this.preferenciaComunicacao = "EMAIL";
    }
    
    public Usuario(String senha, LocalDateTime ultimoLogin, String status) {
        this();
        this.senha = senha;
        this.ultimoLogin = ultimoLogin;
        this.status = status;
    }
    
    
    
    public void setSenha(String senha) {
        this.senha = SenhaService.gerarHash(senha); // Armazena o HASH, não a senha em texto
    }
    
    public boolean verificarSenha(String senha) {
        return SenhaService.verificarSenha(senha, this.senha);
    }
    
    public String getSenha() {
        return this.senha;
    }
    
    
    
    public String getSalt() {
        return salt;
    }
    
    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    public LocalDateTime getUltimoLogin() { 
        return ultimoLogin; 
    }
    
    public void setUltimoLogin(LocalDateTime ultimoLogin) { 
        this.ultimoLogin = ultimoLogin; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public int getTentativasLogin() {
        return tentativasLogin;
    }
    
    public void setTentativasLogin(int tentativasLogin) {
        this.tentativasLogin = tentativasLogin;
    }
    
    public boolean isContaBloqueada() {
        return contaBloqueada;
    }
    
    public void setContaBloqueada(boolean contaBloqueada) {
        this.contaBloqueada = contaBloqueada;
    }
    
    public LocalDateTime getDataExpiracaoSenha() {
        return dataExpiracaoSenha;
    }
    
    public void setDataExpiracaoSenha(LocalDateTime dataExpiracaoSenha) {
        this.dataExpiracaoSenha = dataExpiracaoSenha;
    }
    
    public List<String> getHistoricoSenhas() {
        return new ArrayList<>(historicoSenhas);
    }
    
    public List<String> getPermissoes() {
        return new ArrayList<>(permissoes);
    }
    
    public void adicionarPermissao(String permissao) {
        if (!permissoes.contains(permissao)) {
            permissoes.add(permissao);
        }
    }
    
    public void removerPermissao(String permissao) {
        permissoes.remove(permissao);
    }
    
    public boolean temPermissao(String permissao) {
        return permissoes.contains(permissao);
    }
    
    public String getTokenRecuperacao() {
        return tokenRecuperacao;
    }
    
    public void setTokenRecuperacao(String tokenRecuperacao) {
        this.tokenRecuperacao = tokenRecuperacao;
    }
    
    public LocalDateTime getExpiracaoTokenRecuperacao() {
        return expiracaoTokenRecuperacao;
    }
    
    public void setExpiracaoTokenRecuperacao(LocalDateTime expiracaoTokenRecuperacao) {
        this.expiracaoTokenRecuperacao = expiracaoTokenRecuperacao;
    }
    
    public boolean isReceberNotificacoes() {
        return receberNotificacoes;
    }
    
    public void setReceberNotificacoes(boolean receberNotificacoes) {
        this.receberNotificacoes = receberNotificacoes;
    }
    
    public String getPreferenciaComunicacao() {
        return preferenciaComunicacao;
    }
    
    public void setPreferenciaComunicacao(String preferenciaComunicacao) {
        this.preferenciaComunicacao = preferenciaComunicacao;
    }
    
    // Métodos de negócio
    public boolean login(String senha) {
        if (contaBloqueada) {
            return false;
        }
        
        if (this.senha != null && this.senha.equals(senha)) {
            this.ultimoLogin = LocalDateTime.now();
            this.tentativasLogin = 0;
            return true;
        } else {
            this.tentativasLogin++;
            if (this.tentativasLogin >= 5) {
                this.contaBloqueada = true;
                this.status = "BLOQUEADA";
            }
            return false;
        }
    }
    
    public void logout() {
    }
    
    public void atualizarSenha(String novaSenha) {
        // Verificar se a nova senha não está no histórico
        if (historicoSenhas.contains(novaSenha)) {
            throw new IllegalArgumentException("Não é possível reutilizar uma senha anterior");
        }
        
        setSenha(novaSenha);
    }
    
    public boolean isSenhaExpirada() {
        return LocalDateTime.now().isAfter(dataExpiracaoSenha);
    }
    
    public void gerarTokenRecuperacao() {
        this.tokenRecuperacao = java.util.UUID.randomUUID().toString();
        this.expiracaoTokenRecuperacao = LocalDateTime.now().plusHours(24); // Válido por 24 horas
    }
    
    public boolean validarTokenRecuperacao(String token) {
        return this.tokenRecuperacao != null && 
               this.tokenRecuperacao.equals(token) && 
               LocalDateTime.now().isBefore(expiracaoTokenRecuperacao);
    }
    
    public void desbloquearConta() {
        this.contaBloqueada = false;
        this.tentativasLogin = 0;
        this.status = "ATIVO";
    }
    
    public boolean isAtivo() {
        return "ATIVO".equals(status) && !contaBloqueada;
    }
    
    public String getNivelAcesso() {
        if (this instanceof Agricultor) {
            return "AGRICULTOR";
        } else if (this instanceof Comprador) {
            return "CLIENTE";
        } else if (this instanceof Administrador) {
            return "ADMINISTRADOR";
        }
        return "USUARIO";
    }
    
    @Override
    public String getInfo() {
        return String.format("Usuário: %s (%s) - Status: %s - Último login: %s", 
                           getNome(), getEmail(), status, 
                           ultimoLogin != null ? ultimoLogin.toString() : "Nunca logou");
    }
}