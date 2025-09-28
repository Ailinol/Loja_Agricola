/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.agri_projecto;

import service.NotificacaoService;
import service.UsuarioService;
import javax.swing.JOptionPane;

import model.*;
import dao.AgricultorDAO;

public class Agri_Projecto {
    
    public static void main(String[] args) {
        UsuarioService usuarioService = new UsuarioService();
        /*
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

        
        System.out.println("Comprador cadastrado: " + compradorCadastrado);*/
        
        Agricultor agr1 = new Agricultor(
            "João Agricultor", 
            "lilianolicumba@gmail.com", 
            "841234567", 
            "Maputo", 
            "KaMavota", 
            "Alto Maé",
            "SenhA123@"
        );
        AgricultorDAO dao = new AgricultorDAO();
        
        //dao.salvarAgricultor(agr1);
        Agricultor agr2 = dao.buscarPorId(1);
        System.out.println("HELLO "+agr2);
    }
}