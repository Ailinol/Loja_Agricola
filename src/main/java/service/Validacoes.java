/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.regex.Pattern;

/**
 *
 * @author liliano
 */
public class Validacoes {
    
    // O seguinte Metodo e Responsavel pela Validacao do e-mail
    public static ResultadoValidacao validarEmail(String email){
        ResultadoValidacao resultado = new ResultadoValidacao();
        
        //Verifica se e nulo ou nao
        if(email == null){
            resultado.valido = false;
            resultado.mensagem = "Email Nao pode ser Nulo";
            return resultado;
        }
        
        //Verifica se esta vazio
        if(email.trim().isEmpty()){
            resultado.valido = false;
            resultado.mensagem = "Email nao Pode estar Vazio";
            return resultado;
        }
        
        email = email.trim();
        
        if(email.length() > 254){
            resultado.valido = false;
            resultado.mensagem = "Email muito longo, Maximo de 254 caracteres";
            return resultado;
        }
        
        if(email.startsWith(".") || email.endsWith(".")){
            resultado.valido = false;
            resultado.mensagem = "Email nao Pode Comecar ou terminar com ponto";
            return resultado;
        }
        
        // Verificador de @
        int contadorArroba = 0;
        int posicaoArroba = -1;
        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                contadorArroba++;
                posicaoArroba = i;
            }
        }
        
        if (contadorArroba != 1){
            resultado.valido = false;
            resultado.mensagem = "O email deve conter um @";
            return resultado;
        }
        
        String usuario = email.substring(0, posicaoArroba);
        String dominio = email.substring(posicaoArroba + 1);
        
        //Validacao do Nome
        if(usuario.isBlank()){
            resultado.valido = false;
            resultado.mensagem = "Deve haver um texto antes do @ no email";
            return resultado;
        }
        
        if(usuario.length() > 64){
            resultado.valido = false;
            resultado.mensagem = "A parte inicial do email deve ter no maximo 64 caracteres";
            return resultado;
        }
        
        // Validacao do dominio
        if(dominio.isEmpty()){
            resultado.valido = false;
            resultado.mensagem = "O dominio do email nao pode estar vazio";
            return resultado;
        }
        
        if(!dominio.contains(".")){
            resultado.valido = false;
            resultado.mensagem = "O dominio do email deve Conter pelo menos 1 ponto";
        }
        
        
        String[] partesDominio = dominio.split("\\.");
        String extensao = partesDominio[partesDominio.length - 1];
        
        if (extensao.length() < 2) {
            resultado.valido = false;
            resultado.mensagem = "Extensão do domínio do email deve ter pelo menos 2 caracteres";
            return resultado;
        }
        
        // Validacao Final
        String regexCompleta = "^[a-zA-Z0-9][a-zA-Z0-9._-]*@[a-zA-Z0-9][a-zA-Z0-9.-]*\\.[a-zA-Z]{2,}$";
        if (!Pattern.matches(regexCompleta, email)) {
            resultado.valido = false;
            resultado.mensagem = "Formato de email inválido";
            return resultado;
        }
        
        //Se chegou até aqui, é válido
        resultado.valido = true;
        resultado.mensagem = "Email válido";
        resultado.emailFormatado = email.toLowerCase();
        
        return resultado;
    }
    
    
    // O seguinte Metodo e responsavel por validar o nome Dos usuarios
    public static ResultadoValidacao validarNome(String nome) {
    ResultadoValidacao resultado = new ResultadoValidacao();
    
    if (nome == null || nome.trim().isEmpty()) {
        resultado.valido = false;
        resultado.mensagem = "O campo nome não pode estar vazio";
        return resultado;
    }
    
    nome = nome.trim();
    
    // Verificar se tem pelo menos 2 caracteres
    if (nome.length() < 2) {
        resultado.valido = false;
        resultado.mensagem = "Nome deve ter pelo menos 2 caracteres";
        return resultado;
    }
    
    // Verificar se começa com letra maiúscula
    if (!Character.isUpperCase(nome.charAt(0))) {
        resultado.valido = false;
        resultado.mensagem = "Nome deve começar com letra maiúscula";
        return resultado;
    }
    
    // Verificar caracteres válidos
    for (int i = 0; i < nome.length(); i++) {
        char c = nome.charAt(i);
        
        // Permitir: letras, espaços, hífens, apóstrofes
        if (!Character.isLetter(c) && c != ' ' && c != '-' && c != '\'') {
            resultado.valido = false;
            resultado.mensagem = "Nome contém caracteres inválidos: '" + c + "'";
            return resultado;
        }
        
        // Após espaço ou hífen, deve ter letra maiúscula
        if ((c == ' ' || c == '-') && i < nome.length() - 1) {
            char nextChar = nome.charAt(i + 1);
            if (!Character.isUpperCase(nextChar) && !Character.isLetter(nextChar)) {
                resultado.valido = false;
                resultado.mensagem = "Após espaço ou hífen deve vir uma letra";
                return resultado;
            }
        }
    }
    
    resultado.valido = true;
    resultado.mensagem = "Nome válido";
    return resultado;
}
    
    
    // O seguinte Metodo e responsavel por validar O email dos usuarios
    /**
     1.A senha deve conter no minimo 8 caracteres
     2.A senha deve conter Pelo menos uma letra maiuscula
     3.A senha deve conter pelo menos uma letra minuscula
     4.A senha deve conter pelo menos um numero
     5.A senha deve conter pelo menos um caractere especial
     6.Nao deve conter espacos
     **/
    public static ResultadoValidacao validarSenha(String senha) {
    ResultadoValidacao resultado = new ResultadoValidacao();
    
    if (senha == null || senha.length() < 8) {
        resultado.valido = false;
        resultado.mensagem = "Senha deve ter no mínimo 8 caracteres";
        return resultado;
    }
    
    // Verifica cada requisito separadamente (mais legível)
    boolean temMaiuscula = false;
    boolean temMinuscula = false;
    boolean temNumero = false;
    boolean temEspecial = false;
    
    for (char c : senha.toCharArray()) {
        if (Character.isUpperCase(c)) temMaiuscula = true;
        if (Character.isLowerCase(c)) temMinuscula = true;
        if (Character.isDigit(c)) temNumero = true;
        if (!Character.isLetterOrDigit(c)) temEspecial = true;
    }
    
    if (!temMaiuscula) {
        resultado.valido = false;
        resultado.mensagem = "Senha precisa de pelo menos 1 letra maiúscula";
        return resultado;
    }
    
    if (!temMinuscula) {
        resultado.valido = false;
        resultado.mensagem = "Senha precisa de pelo menos 1 letra minúscula";
        return resultado;
    }
    
    if (!temNumero) {
        resultado.valido = false;
        resultado.mensagem = "Senha precisa de pelo menos 1 número";
        return resultado;
    }
    
    if (!temEspecial) {
        resultado.valido = false;
        resultado.mensagem = "Senha precisa de pelo menos 1 caractere especial";
        return resultado;
    }
    
    resultado.valido = true;
    resultado.mensagem = "Senha válida";
    return resultado;
}
    
    
    //O seguinte Metodo e responsavel por vazer a validcao do numero de telefone
    
    public static ResultadoValidacao validarTelefone(String telefone){
        ResultadoValidacao resultado = new ResultadoValidacao();
        
        if(telefone == null || telefone.trim().isEmpty()){
            resultado.valido = false;
            resultado.mensagem = "O campo telefone nao pode estar vazio";
            return resultado;
        }
        
        String regexTelefone = "^(8[2-9]\\d{7}|2\\d{6,7})$";
        if(!Pattern.matches(regexTelefone, telefone)){
            resultado.valido = false;
            resultado.mensagem = "O numero de telefone nao e valido";
            return resultado;
        }
        
        
        resultado.valido = true;
        resultado.mensagem = "Numero de telefone Valido";
        return resultado;
    }
    
    //O presente Metodo e responsavel por fazer a validacao da regiao
    public static ResultadoValidacao validarRegiao(String regiao){
        ResultadoValidacao resultado = new ResultadoValidacao();
        
        if(regiao == null || regiao.isEmpty()){
            resultado.valido =  false;
            resultado.mensagem = "O bairro e um campo Obrigatorio";
            return resultado;
        }
        
        resultado.valido = true;
        resultado.mensagem = "O bairro e valido";
        return resultado;
    }
    
    
}
