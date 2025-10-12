package service;

import model.Pessoa;
import model.Administrador;
import model.Agricultor;
import model.Comprador;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import model.Usuario;

/**
 *
 * @author liliano
 */
public class AutenticacaoService {
    private UsuarioService usuarioService;
    private Map<String, SessaoUsuario> sessoesAtivas;
    private Map<String, TokenRecuperacao> tokensRecuperacao;

    public AutenticacaoService() {
        this.usuarioService = new UsuarioService();
        this.sessoesAtivas = new HashMap<>();
        this.tokensRecuperacao = new HashMap<>();
    }
    
    // Método Para autenticar Login
    public ResultadoLogin autenticarUsuario(String email, String senha, String tipoUsuario) {
    ResultadoLogin resultado = new ResultadoLogin();
    
    if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
        resultado.sucesso = false;
        resultado.mensagem = "O E-mail e a senha são obrigatórios";
        return resultado;
    }
    
    // Buscar usuário pelo email
    Pessoa usuario = usuarioService.autenticar(email, senha);
    
    if (usuario == null) {
        resultado.sucesso = false;
        resultado.mensagem = "Email não encontrado";
        return resultado;
    }
    
    // Verificar se o tipo de usuário corresponde
    if (!verificarTipoUsuario(usuario, tipoUsuario)) {
        resultado.sucesso = false;
        resultado.mensagem = "Tipo de usuário incorreto";
        return resultado;
    }
    
    // Verificação de senha com hash
    if (!((Usuario) usuario).verificarSenha(senha)) {
        resultado.sucesso = false;
        resultado.mensagem = "Senha incorreta";
        return resultado;
    }
    
    // Verificar se usuário esta ativo
    if (!usuario.isAtivo()) {
        resultado.sucesso = false;
        resultado.mensagem = "Usuário inativo";
        return resultado;
    }
    
    // Login bem-sucedido
    resultado.sucesso = true;
    resultado.mensagem = "Login realizado com sucesso";
    resultado.usuario = usuario;
    resultado.tokenSessao = gerarTokenSessao(usuario.getId());
    
    return resultado;
}
    
    // Metodo auxiliar para verificar tipo de usuário
    private boolean verificarTipoUsuario(Pessoa usuario, String tipoUsuarioSelecionado) {
        if (tipoUsuarioSelecionado == null) return false;
        
        switch (tipoUsuarioSelecionado.toUpperCase()) {
            case "AGRICULTOR":
                return usuario instanceof Agricultor;
            case "COMPRADOR":
                return usuario instanceof Comprador;
            case "ADMINISTRADOR":
                return usuario instanceof Administrador;
            default:
                return false;
        }
    }
    
    // Gerar token de sessão
    private String gerarTokenSessao(int usuarioId) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiracao = LocalDateTime.now().plusHours(24); // 24 horas
        
        SessaoUsuario sessao = new SessaoUsuario(usuarioId, token, expiracao);
        sessoesAtivas.put(token, sessao);
        
        return token;
    }
    
    // Verificar se token é válido
    public boolean verificarSessao(String token) {
        SessaoUsuario sessao = sessoesAtivas.get(token);
        if (sessao == null) return false;
        
        if (LocalDateTime.now().isAfter(sessao.dataExpiracao)) {
            sessoesAtivas.remove(token);
            return false;
        }
        
        return true;
    }
    
    // Logout
    public void logout(String token) {
        sessoesAtivas.remove(token);
    }
    
    // Buscar usuário por token
    public Pessoa getUsuarioPorToken(String token) {
        SessaoUsuario sessao = sessoesAtivas.get(token);
        if (sessao == null) return null;
        
        return usuarioService.buscarUsuarioPorId(sessao.usuarioId);
    }
    
    // Buscar comprador logado
    public Comprador getCompradorLogado(String token) {
        SessaoUsuario sessao = sessoesAtivas.get(token);
        if (sessao == null) return null;

        Pessoa usuario = usuarioService.buscarUsuarioPorId(sessao.usuarioId);

        if (usuario instanceof Comprador) {
            return (Comprador) usuario;
        }

        return null;
    }
    
    // Classes internas
    public class ResultadoLogin {
        public boolean sucesso;
        public String mensagem;
        public String tokenSessao;
        public Pessoa usuario; // MUDANÇA: de Usuario para Pessoa
    }
    
    public class SessaoUsuario {
        public int usuarioId;
        public String token;
        public LocalDateTime dataExpiracao;
        
        public SessaoUsuario(int usuarioId, String token, LocalDateTime dataExpiracao) {
            this.usuarioId = usuarioId;
            this.token = token;
            this.dataExpiracao = dataExpiracao;
        }
    }
    
    public class TokenRecuperacao {
        public int usuarioId;
        public String token;
        public LocalDateTime expiracao;
        
        public TokenRecuperacao(int usuarioId, String token, LocalDateTime expiracao) {
            this.usuarioId = usuarioId;
            this.token = token;
            this.expiracao = expiracao;
        }
    }
}