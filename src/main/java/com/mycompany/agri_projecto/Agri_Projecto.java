/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.agri_projecto;

import service.NotificacaoService;
import service.UsuarioService;
import javax.swing.JOptionPane;

public class Agri_Projecto {
    
    public static void main(String[] args) {
        UsuarioService usuarioService = new UsuarioService();
        
        // 4. 🔬 TESTAR CADASTRO DE AGRICULTOR
        System.out.println("\n=== TESTANDO CADASTRO DE AGRICULTOR ===");
        boolean agricultorCadastrado = usuarioService.cadastrarAgricultor(
            "João Agricultor", 
            "lilianolicumba@gmail.com", 
            "841234567", 
            "Maputo", 
            "KaMavota", 
            "Alto Maé",
            "SenhA123@"
        );
        
        System.out.println("Agricultor cadastrado: " + agricultorCadastrado);
        
        // 5. 🔬 TESTAR CADASTRO DE COMPRADOR
        System.out.println("\n=== TESTANDO CADASTRO DE COMPRADOR ===");
        boolean compradorCadastrado = usuarioService.cadastrarComprador(
            "Maria Compradora", 
            "lilianolicumba@gmail.com", 
            "821234567", 
            "Maputo", 
            "KaMpfumo", 
            "Central",
            "SenhA456@"
        );

        
        System.out.println("Comprador cadastrado: " + compradorCadastrado);
    }
}