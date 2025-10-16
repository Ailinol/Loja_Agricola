/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import javax.swing.JOptionPane;

/**
 *
 * @author liliano
 */
public class NotificacaoService {
    private boolean emailHabilitado = false;
    private Properties emailProperties;
    private String emailRemetente;
    private String senhaRemetente;
    
    public NotificacaoService(){
        emailProperties = new Properties();
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");
        emailProperties.put("mail.smtp.host", "smtp.gmail.com");
        emailProperties.put("mail.smtp.port", "587");
        emailProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
    }
    
    public boolean enviarEmail(String destinatario, String assunto, String mensagem){
        try{
            Session session = Session.getInstance(emailProperties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailRemetente, senhaRemetente);
                }
            });
            
            session.setDebug(true);
            
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(emailRemetente));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            email.setSubject(assunto);
            
            email.setContent(mensagem, "text/html; charset=utf-8");
            
            Transport.send(email);
            System.out.println("Email enviado com sucesso para: " + destinatario);
            return true;
        
        } catch(Exception e) {
             System.err.println("Erro ao enviar email: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Erro ao enviar email: " + e.getMessage(),
                "Erro de Envio",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    
    
    // Email de boas-vindas 
    public boolean enviarEmailBoasVindas(String destinatario, String nomeUsuario) {
        String assunto = "🌱 Bem-vindo ao nosso Marketplace Agrícola!";
        String mensagem = "<html>"
            + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
            + "<h2 style='color: #2E7D32;'>Olá " + nomeUsuario + "!</h2>"
            + "<p>Obrigado por se cadastrar em nossa plataforma.</p>"
            + "<p>Agora você pode:</p>"
            + "<ul>"
            + "<li>Comprar produtos frescos diretamente dos agricultores locais</li>"
            + "<li>Explorar ofertas baseadas na sua localização</li>"
            + "<li>Avaliar produtos e agricultores</li>"
            + "</ul>"
            + "<br>"
            + "<p>Atenciosamente,<br><strong>Equipe do Marketplace Agrícola</strong></p>"
            + "</body>"
            + "</html>";
        
        return enviarEmail(destinatario, assunto, mensagem);
    }
    
    
     public void configurarEmail(String host, String porta, String email, String senha) {
        emailProperties.put("mail.smtp.host", host);
        emailProperties.put("mail.smtp.port", porta);
        this.emailRemetente = email;
        this.senhaRemetente = senha;
        
        System.out.println("Serviço de email configurado com: " + email);
    }
     
    // Adicione este método no seu ComunicacaoService para configurar Gmail
    public void configurarGmail(String email, String senhaApp) {
        configurarEmail("smtp.gmail.com", "587", email, senhaApp);
    }
  }

