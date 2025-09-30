/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.Usuario;
import model.Administrador;
import model.Agricultor;
import model.Comprador;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author liliano
 */
public class AutenticacaoService {
    private UsuarioService usuarioService;
    private Map<String, SessaoUsuario> sessoesAtivas;
    private Map<String,  TokenRecuperacao> tokensRecuperacao;

    public AutenticacaoService(UsuarioService usuarioService, Map<String, SessaoUsuario> sessoesAtivas, Map<String, TokenRecuperacao> tokensRecuperacao) {
        this.usuarioService = usuarioService;
        this.sessoesAtivas = sessoesAtivas;
        this.tokensRecuperacao = tokensRecuperacao;
    }
    
    
    
    
    //Metodo para autenticar usuario
    
    //Metodo Para autenticar Login
    public ResultadoLogin autenticarUsuario(String email, String senha){
        ResultadoLogin resultado = new ResultadoLogin();
        UsuarioService usuario = new UsuarioService();
        
        if(email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            resultado.sucesso = false;
            resultado.mensagem = "O E-mail e a senha sao obrigatorios";
            return resultado;
        }
        
        if(usuario.buscarUsuarioPorEmail(email) == null){
            resultado.sucesso = false;
            resultado.mensagem = "O Email nao existe";
            return resultado;
        }
        
        return resultado;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
     public class ResultadoLogin {
        public boolean sucesso;
        public String mensagem;
        public String tokenSessao;
        public Usuario usuario;
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
