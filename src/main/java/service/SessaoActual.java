/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author liliano
 */
import model.Comprador;
import model.Agricultor;
import model.Pessoa;

public class SessaoActual {
    private static Pessoa usuarioLogado;
    private static String tokenSessao;
    
    // Salvar usuário logado
    public static void setUsuarioLogado(Pessoa usuario, String token) {
        usuarioLogado = usuario;
        tokenSessao = token;
    }
    
    // Pegar usuário logado
    public static Pessoa getUsuarioLogado() {
        return usuarioLogado;
    }
    
    // Pegar comprador logado
    public static Comprador getCompradorLogado() {
        if (usuarioLogado instanceof Comprador) {
            return (Comprador) usuarioLogado;
        }
        return null;
    }
    
    // Pegar agricultor logado
    public static Agricultor getAgricultorLogado() {
        if (usuarioLogado instanceof Agricultor) {
            return (Agricultor) usuarioLogado;
        }
        return null;
    }
    
    // Pegar token
    public static String getToken() {
        return tokenSessao;
    }
    
    // Limpar sessão (logout)
    public static void limparSessao() {
        usuarioLogado = null;
        tokenSessao = null;
    }
}