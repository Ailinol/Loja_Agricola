/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.regex.Pattern;
import model.Produto;

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
    
    
    // O presente Metodo e responsavel por fazer a validacao de um produto novo
    public static ResultadoValidacao validarProduto(Produto produto) {
        ResultadoValidacao resultado = new ResultadoValidacao();
        
        if(produto.getNome() == null || produto.getNome().trim().isEmpty()){
            resultado.valido = false;
            resultado.mensagem = "O nome tem que ser valido";
            return resultado;
        }
        if(produto.getPreco() <= 0){
            resultado.valido = false;
            resultado.mensagem = "O preco tem que ser maior que 0";
            return resultado;
        }
        if(produto.getQuantidadeDisponivel() < 0) {
            resultado.valido = false;
            resultado.mensagem = "A quantidade tem que ser maior que 0";
            return resultado;
        }
        if(produto.getAgricultorId() <= 0){
            resultado.valido = false;
            resultado.mensagem = "O agricultor nao existe";
        }
        
        resultado.valido = true;
        return resultado;
    }
    
    
    public static ResultadoValidacao validarBiografia(String biografia){
        ResultadoValidacao resultado = new ResultadoValidacao();
        
        if(biografia == null || biografia.trim().isEmpty()){
            resultado.valido = false;
            resultado.mensagem = "O campo nao pode estar vazio";
            return resultado;
        }
        
        if(biografia.length() < 50){
            resultado.valido = false;
            resultado.mensagem = "A biografia deve conter um minimo de 50 caracteres";
            return resultado;
        }
        
        resultado.valido = true;
        return resultado;
    }
    
    
    //Metodo Responssavel por validar os anos de esperiencia como agricultor
    public static ResultadoValidacao validarAnosExperiencia(String anos) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if(anos == null || anos.isEmpty()){
            resultado.valido = false;
            resultado.mensagem = "Os anos de experiência são um campo obrigatório";
            return resultado;
        }

        try {
            int valor = Integer.parseInt(anos.trim());
            if(valor < 0) {
                resultado.valido = false;
                resultado.mensagem = "Os anos de experiência não podem ser negativos";
                return resultado;
            }

            if(valor > 100) {
                resultado.valido = false;
                resultado.mensagem = "Os anos de experiência não podem ser maiores que 100";
                return resultado;
            }

            resultado.valido = true;
            resultado.mensagem = "Os anos de experiência são válidos";
            return resultado;
        } catch (NumberFormatException e) {
            resultado.valido = false;
            resultado.mensagem = "Os anos de experiência devem conter apenas números inteiros";
            return resultado;
        }
    }
    
    // Metodo responsavel por validar o tamanho dapropiedade
    public static ResultadoValidacao validarTamanhoPropriedade(String tamanho) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if(tamanho == null || tamanho.isEmpty()){
            resultado.valido = false;
            resultado.mensagem = "O tamanho da propriedade é um campo obrigatório";
            return resultado;
        }

        try {
            double valor = Double.parseDouble(tamanho.trim());
            if(valor <= 0) {
                resultado.valido = false;
                resultado.mensagem = "O tamanho da propriedade deve ser maior que zero";
                return resultado;
            }

            if(valor > 10000) {
                resultado.valido = false;
                resultado.mensagem = "O tamanho da propriedade não pode ser maior que 10.000 hectares";
                return resultado;
            }

            resultado.valido = true;
            resultado.mensagem = "O tamanho da propriedade é válido";
            return resultado;
        } catch (NumberFormatException e) {
            resultado.valido = false;
            resultado.mensagem = "O tamanho da propriedade deve conter apenas números decimais";
            return resultado;
        }
    }
    
    
    //Metodo responsavel por validar o raio de entrega
    public static ResultadoValidacao validarRaioEntrega(String raio) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if(raio == null || raio.isEmpty()){
            resultado.valido = false;
            resultado.mensagem = "O raio de entrega é um campo obrigatório";
            return resultado;
        }

        try {
            double valor = Double.parseDouble(raio.trim());
            if(valor < 0) {
                resultado.valido = false;
                resultado.mensagem = "O raio de entrega não pode ser negativo";
                return resultado;
            }

            if(valor > 500) {
                resultado.valido = false;
                resultado.mensagem = "O raio de entrega não pode ser maior que 500 km";
                return resultado;
            }

            resultado.valido = true;
            resultado.mensagem = "O raio de entrega é válido";
            return resultado;
        } catch (NumberFormatException e) {
            resultado.valido = false;
            resultado.mensagem = "O raio de entrega deve conter apenas números decimais";
            return resultado;
        }
    }
    
    
    //Metodo Responsavel por validar o Custo de entrega
    public static ResultadoValidacao validarCustoEntrega(String custo) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if(custo == null || custo.isEmpty()){
            resultado.valido = false;
            resultado.mensagem = "O custo de entrega é um campo obrigatório";
            return resultado;
        }

        try {
            double valor = Double.parseDouble(custo.trim());
            if(valor < 0) {
                resultado.valido = false;
                resultado.mensagem = "O custo de entrega não pode ser negativo";
                return resultado;
            }

            if(valor > 10000) {
                resultado.valido = false;
                resultado.mensagem = "O custo de entrega não pode ser maior que 10.000 MT";
                return resultado;
            }

            resultado.valido = true;
            resultado.mensagem = "O custo de entrega é válido";
            return resultado;
        } catch (NumberFormatException e) {
            resultado.valido = false;
            resultado.mensagem = "O custo de entrega deve conter apenas números decimais";
                return resultado;
        }
    }
    
    // Método para validar prazo de encomenda
    public static ResultadoValidacao validarPrazoEncomenda(String prazo) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if(prazo == null || prazo.isEmpty()){
            resultado.valido = false;
            resultado.mensagem = "O prazo de encomenda é um campo obrigatório";
            return resultado;
        }

        try {
            int valor = Integer.parseInt(prazo.trim());
            if(valor < 1) {
                resultado.valido = false;
                resultado.mensagem = "O prazo de encomenda deve ser pelo menos 1 dia";
                return resultado;
            }

            if(valor > 365) {
                resultado.valido = false;
                resultado.mensagem = "O prazo de encomenda não pode ser maior que 365 dias";
                return resultado;
            }

            resultado.valido = true;
            resultado.mensagem = "O prazo de encomenda é válido";
            return resultado;
        } catch (NumberFormatException e) {
            resultado.valido = false;
            resultado.mensagem = "O prazo de encomenda deve conter apenas números inteiros";
            return resultado;
        }
    }

    // Método para validar horário (formato HH:MM)
    public static ResultadoValidacao validarHorario(String horario) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if(horario == null || horario.isEmpty()){
            resultado.valido = false;
            resultado.mensagem = "O horário é um campo obrigatório";
            return resultado;
        }

        // Regex para validar formato HH:MM (24 horas)
        String regexHorario = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";
        if(!Pattern.matches(regexHorario, horario.trim())) {
            resultado.valido = false;
            resultado.mensagem = "Formato de horário inválido. Use HH:MM (ex: 08:00, 14:30)";
            return resultado;
        }

        resultado.valido = true;
        resultado.mensagem = "Horário válido";
        return resultado;
    }

    // Método para validar confirmação de senha
    public static ResultadoValidacao validarConfirmacaoSenha(String senha, String confirmacao) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if(confirmacao == null || confirmacao.isEmpty()){
            resultado.valido = false;
            resultado.mensagem = "A confirmação de senha é obrigatória";
            return resultado;
        }

        if(!confirmacao.equals(senha)) {
            resultado.valido = false;
            resultado.mensagem = "As senhas não coincidem";
            return resultado;
        }

        resultado.valido = true;
        resultado.mensagem = "Confirmação de senha válida";
        return resultado;
    }

    // Método para validar WhatsApp (pode usar a mesma validação do telefone)
    public static ResultadoValidacao validarWhatsapp(String whatsapp) {
        return validarTelefone(whatsapp);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    // Método para validar nome do produto
    public static ResultadoValidacao validarNomeProduto(String nome) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (nome == null || nome.trim().isEmpty()) {
            resultado.valido = false;
            resultado.mensagem = "O nome do produto é obrigatório";
            return resultado;
        }

        nome = nome.trim();

        if (nome.length() < 2) {
            resultado.valido = false;
            resultado.mensagem = "O nome do produto deve ter pelo menos 2 caracteres";
            return resultado;
        }

        if (nome.length() > 100) {
            resultado.valido = false;
            resultado.mensagem = "O nome do produto deve ter no máximo 100 caracteres";
            return resultado;
        }

        resultado.valido = true;
        resultado.mensagem = "Nome do produto válido";
        return resultado;
    }

    // Método para validar descrição do produto
    public static ResultadoValidacao validarDescricaoProduto(String descricao) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (descricao == null || descricao.trim().isEmpty()) {
            resultado.valido = false;
            resultado.mensagem = "A descrição do produto é obrigatória";
            return resultado;
        }

        descricao = descricao.trim();

        if (descricao.length() < 10) {
            resultado.valido = false;
            resultado.mensagem = "A descrição deve ter pelo menos 10 caracteres";
            return resultado;
        }

        if (descricao.length() > 500) {
            resultado.valido = false;
            resultado.mensagem = "A descrição deve ter no máximo 500 caracteres";
            return resultado;
        }

        resultado.valido = true;
        resultado.mensagem = "Descrição válida";
        return resultado;
    }

    // Método para validar preço do produto
    public static ResultadoValidacao validarPreco(String preco) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (preco == null || preco.trim().isEmpty()) {
            resultado.valido = false;
            resultado.mensagem = "O preço é obrigatório";
            return resultado;
        }

        try {
            double valor = Double.parseDouble(preco.trim().replace(",", "."));
            if (valor <= 0) {
                resultado.valido = false;
                resultado.mensagem = "O preço deve ser maior que zero";
                return resultado;
            }

            if (valor > 1000000) {
                resultado.valido = false;
                resultado.mensagem = "O preço não pode ser maior que 1.000.000 MT";
                return resultado;
            }

            resultado.valido = true;
            resultado.mensagem = "Preço válido";
            return resultado;

        } catch (NumberFormatException e) {
            resultado.valido = false;
            resultado.mensagem = "O preço deve conter apenas números decimais";
            return resultado;
        }
    }

    // Método para validar quantidade disponível
    public static ResultadoValidacao validarQuantidade(String quantidade) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (quantidade == null || quantidade.trim().isEmpty()) {
            resultado.valido = false;
            resultado.mensagem = "A quantidade é obrigatória";
            return resultado;
        }

        try {
            int valor = Integer.parseInt(quantidade.trim());
            if (valor < 0) {
                resultado.valido = false;
                resultado.mensagem = "A quantidade não pode ser negativa";
                return resultado;
            }

            if (valor > 100000) {
                resultado.valido = false;
                resultado.mensagem = "A quantidade não pode ser maior que 100.000";
                return resultado;
            }

            resultado.valido = true;
            resultado.mensagem = "Quantidade válida";
            return resultado;

        } catch (NumberFormatException e) {
            resultado.valido = false;
            resultado.mensagem = "A quantidade deve conter apenas números inteiros";
            return resultado;
        }
    }

    // Método para validar quantidade mínima
    public static ResultadoValidacao validarQuantidadeMinima(String quantidadeMinima) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (quantidadeMinima == null || quantidadeMinima.trim().isEmpty()) {
            resultado.valido = false;
            resultado.mensagem = "A quantidade mínima é obrigatória";
            return resultado;
        }

        try {
            int valor = Integer.parseInt(quantidadeMinima.trim());
            if (valor < 0) {
                resultado.valido = false;
                resultado.mensagem = "A quantidade mínima não pode ser negativa";
                return resultado;
            }

            if (valor > 1000) {
                resultado.valido = false;
                resultado.mensagem = "A quantidade mínima não pode ser maior que 1.000";
                return resultado;
            }

            resultado.valido = true;
            resultado.mensagem = "Quantidade mínima válida";
            return resultado;

        } catch (NumberFormatException e) {
            resultado.valido = false;
            resultado.mensagem = "A quantidade mínima deve conter apenas números inteiros";
            return resultado;
        }
    }

    // Método para validar peso unitário
    public static ResultadoValidacao validarPeso(String peso) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (peso == null || peso.trim().isEmpty()) {
            resultado.valido = false;
            resultado.mensagem = "O peso unitário é obrigatório";
            return resultado;
        }

        try {
            double valor = Double.parseDouble(peso.trim().replace(",", "."));
            if (valor <= 0) {
                resultado.valido = false;
                resultado.mensagem = "O peso deve ser maior que zero";
                return resultado;
            }

            if (valor > 1000) {
                resultado.valido = false;
                resultado.mensagem = "O peso não pode ser maior que 1.000 kg";
                return resultado;
            }

            resultado.valido = true;
            resultado.mensagem = "Peso válido";
            return resultado;

        } catch (NumberFormatException e) {
            resultado.valido = false;
            resultado.mensagem = "O peso deve conter apenas números decimais";
            return resultado;
        }
    }

    // Método para validar prazo de validade
    public static ResultadoValidacao validarPrazoValidade(String prazoValidade) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (prazoValidade == null || prazoValidade.trim().isEmpty()) {
            resultado.valido = false;
            resultado.mensagem = "O prazo de validade é obrigatório para produtos perecíveis";
            return resultado;
        }

        try {
            int valor = Integer.parseInt(prazoValidade.trim());
            if (valor < 1) {
                resultado.valido = false;
                resultado.mensagem = "O prazo de validade deve ser pelo menos 1 dia";
                return resultado;
            }

            if (valor > 3650) {
                resultado.valido = false;
                resultado.mensagem = "O prazo de validade não pode ser maior que 10 anos (3650 dias)";
                return resultado;
            }

            resultado.valido = true;
            resultado.mensagem = "Prazo de validade válido";
            return resultado;

        } catch (NumberFormatException e) {
            resultado.valido = false;
            resultado.mensagem = "O prazo de validade deve conter apenas números inteiros";
            return resultado;
        }
    }

    // Método para validar categoria
    public static ResultadoValidacao validarCategoria(String categoria) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (categoria == null || categoria.trim().isEmpty()) {
            resultado.valido = false;
            resultado.mensagem = "A categoria é obrigatória";
            return resultado;
        }

        resultado.valido = true;
        resultado.mensagem = "Categoria válida";
        return resultado;
    }

    // Método para validar unidade de medida
    public static ResultadoValidacao validarUnidadeMedida(String unidade) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (unidade == null || unidade.trim().isEmpty()) {
            resultado.valido = false;
            resultado.mensagem = "A unidade de medida é obrigatória";
            return resultado;
        }

        resultado.valido = true;
        resultado.mensagem = "Unidade de medida válida";
        return resultado;
    }

    // Método para validar qualidade
    public static ResultadoValidacao validarQualidade(String qualidade) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (qualidade == null || qualidade.trim().isEmpty()) {
            resultado.valido = false;
            resultado.mensagem = "A qualidade é obrigatória";
            return resultado;
        }

        resultado.valido = true;
        resultado.mensagem = "Qualidade válida";
        return resultado;
    }

    // Método para validar certificação
    public static ResultadoValidacao validarCertificacao(String certificacao) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (certificacao == null || certificacao.trim().isEmpty()) {
            resultado.valido = false;
            resultado.mensagem = "A certificação não pode estar vazia";
            return resultado;
        }

        if (certificacao.length() > 50) {
            resultado.valido = false;
            resultado.mensagem = "A certificação deve ter no máximo 50 caracteres";
            return resultado;
        }

        resultado.valido = true;
        resultado.mensagem = "Certificação válida";
        return resultado;
    }

    // Método para validar data de colheita (não pode ser no futuro)
    public static ResultadoValidacao validarDataColheita(java.time.LocalDate data) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (data == null) {
            resultado.valido = false;
            resultado.mensagem = "A data de colheita é obrigatória";
            return resultado;
        }

        if (data.isAfter(java.time.LocalDate.now())) {
            resultado.valido = false;
            resultado.mensagem = "A data de colheita não pode ser no futuro";
            return resultado;
        }

        resultado.valido = true;
        resultado.mensagem = "Data de colheita válida";
        return resultado;
    }

    // Método para validar data de validade (deve ser no futuro)
    public static ResultadoValidacao validarDataValidade(java.time.LocalDate data) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (data == null) {
            resultado.valido = false;
            resultado.mensagem = "A data de validade é obrigatória para produtos perecíveis";
            return resultado;
        }

        if (data.isBefore(java.time.LocalDate.now())) {
            resultado.valido = false;
            resultado.mensagem = "A data de validade não pode ser no passado";
            return resultado;
        }

        resultado.valido = true;
        resultado.mensagem = "Data de validade válida";
        return resultado;
    }

    // Método para validar combobox obrigatória (Sim/Não)
    public static ResultadoValidacao validarComboObrigatorio(String valor, String nomeCampo) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (valor == null || valor.trim().isEmpty()) {
            resultado.valido = false;
            resultado.mensagem = nomeCampo + " é obrigatório";
            return resultado;
        }

        resultado.valido = true;
        resultado.mensagem = nomeCampo + " válido";
        return resultado;
    }
}
