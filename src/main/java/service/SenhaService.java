/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author liliano
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SenhaService {
    
    public static String gerarHash(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(senha.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString(); // retorna o hash em formato hexadecimal
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }

    // Verifica se a senha digitada bate com a salva
    public static boolean verificarSenha(String senhaDigitada, String senhaSalva) {
        String hashDigitada = gerarHash(senhaDigitada);
        return hashDigitada.equals(senhaSalva);
    }
}
