/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author liliano
 */
public class ResultadoValidacao {
        public boolean valido;
        public String mensagem;
        public String emailFormatado;
        
        public ResultadoValidacao() {
            this.valido = false;
            this.mensagem = "";
            this.emailFormatado = "";
        }
    }
