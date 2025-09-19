/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.ArrayList;
import java.util.List;
import model.Pessoa;

/**
 *
 * @author liliano
 */
public class BloqueioService {
    
    /**
     A seguinte a classe e responsavel pelo bloqueio e desbloqueio de contas de usuario
     * Ela e constituida inicialmente por tres Metodos
     * 1.Bloqueio do usuario: QUe bloqueia os usuarios colocando-os ativos ou nao activos
     * 2.Desbloqueio  do ususario: QUe desbloqueia os usuarios que estao inativos
     * 3.Eliminar conta: Esse metodo e responsavel por remover permanetemente uma conta
     * 
     * O bloqueio de usuarios pode ser usado em casos de fraudes e o usuario precise se justificar 
     * Ou exclarecer-se para poder voltar a usar a conta
     * 
     * A eliminacao permanente da conta e nos casos de ser necessario remover permanentemente a conta de um usuario
     **/
    List<Pessoa>  usuarios = new ArrayList<>();
    
    public boolean bloquearUsuario(int usuarioId){
        UsuarioService service = new UsuarioService();
        Pessoa usuario = service.buscarUsuarioPorId(usuarioId);
        
        if(usuario == null){
            return false;
        }
        
        usuario.setAtivo(false);
        return true;
    }
    
    public boolean desbloquearUsuario(int usuarioId){
        UsuarioService service = new UsuarioService();
        Pessoa usuario = service.buscarUsuarioPorId(usuarioId);
        
        if(usuario == null){
            return false;
        }
        
        usuario.setAtivo(true);
        return true;
    }
    
    public boolean eliminarConta(int usuarioId){
        UsuarioService service = new UsuarioService();
        Pessoa usuario = service.buscarUsuarioPorId(usuarioId);
      
        if(usuario != null){
            usuarios.remove(usuario);
            return true;
        }
        
        return false;

    }
    
}
