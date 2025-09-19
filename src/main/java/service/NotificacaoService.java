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
            
            // Instrucao para usar formatacao em HTML
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
    
    
    // 3. Email de recuperação de senha
    public boolean enviarEmailRecuperacaoSenha(String destinatario, String token) {
        String assunto = "🔐 Recuperação de Senha";
        String mensagem = "<html>"
            + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
            + "<h2 style='color: #D32F2F;'>Recuperação de Senha</h2>"
            + "<p>Você solicitou a recuperação de senha.</p>"
            + "<div style='background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin: 20px 0;'>"
            + "<h3 style='text-align: center; color: #1976D2; font-size: 24px;'>" + token + "</h3>"
            + "</div>"
            + "<p><strong>Este código expira em 24 horas.</strong></p>"
            + "<p>Se não foi você, ignore este email.</p>"
            + "</body>"
            + "</html>";
        
        return enviarEmail(destinatario, assunto, mensagem);
    }
    
    //Notificacao de novo pedido
    public boolean enviarEmailNovoPedido(String destinatario, String nomeAgricultor, int numeroPedido) {
        String assunto = "🛒 Novo Pedido Recebido - #" + numeroPedido;
        String mensagem = "<html>"
            + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
            + "<h2 style='color: #FF9800;'>Olá " + nomeAgricultor + "!</h2>"
            + "<p>Você recebeu um novo pedido!</p>"
            + "<div style='background-color: #FFF3E0; padding: 15px; border-radius: 5px; margin: 20px 0;'>"
            + "<h3 style='color: #E65100;'>Número do Pedido: #" + numeroPedido + "</h3>"
            + "</div>"
            + "<p>Acesse sua conta para ver os detalhes completos do pedido.</p>"
            + "<br>"
            + "<p>Atenciosamente,<br>Equipe do Marketplace</p>"
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

