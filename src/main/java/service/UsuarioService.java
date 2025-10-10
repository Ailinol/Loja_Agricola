package service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Agricultor;
import model.Comprador;
import model.Pessoa;
import static service.Validacoes.validarEmail;
import static service.Validacoes.validarRegiao;
import static service.Validacoes.validarSenha;
import static service.Validacoes.validarTelefone;

public class UsuarioService {
    private List<Pessoa> usuarios = new ArrayList<>();
    private int ultimoId;
    NotificacaoService comService = new NotificacaoService();
    
    // Metodo Responsavel por fazer o cadastro do Agricultor e do comprador
    
   public boolean cadastrarAgricultor(String nome, String email, String telefone, 
                                  String provincia, String distrito, String bairro, 
                                  String senha, String tipoAgricultura, int anosExperiencia,
                                  String biografia, double tamanhoPropriedade, 
                                  boolean certificadoOrganico, boolean ofereceEntrega,
                                  double raioEntrega, double custoEntrega,
                                  String whatsapp, boolean aceitaVisitas, 
                                  boolean aceitaEncomendas, int prazoMinimoEncomenda,
                                  String horarioAbertura, String horarioFechamento,
                                  boolean disponivelParaContato) {
    
    List<String> outrasCertificacoes = new ArrayList<>();
    double classificacaoMedia = 0.0;
    
    return cadastrarUsuario(
        nome, email, telefone, provincia, distrito, bairro,
        senha, "AGRICULTOR", tipoAgricultura, anosExperiencia, 
        biografia, tamanhoPropriedade, certificadoOrganico, 
        ofereceEntrega, outrasCertificacoes, classificacaoMedia, 
        raioEntrega, custoEntrega, whatsapp, aceitaVisitas,
        aceitaEncomendas, prazoMinimoEncomenda, horarioAbertura,
        horarioFechamento, disponivelParaContato
    );
}
     
    public boolean cadastrarComprador(String nome, String email, String telefone, 
                                 String provincia, String distrito, String bairro, 
                                 String senha, List<String> preferenciasCategorias,
                                 double raioBuscaPreferido, boolean recebeNewsletter) {
    
    return cadastrarUsuario(nome, email, telefone, provincia, distrito, bairro,
                                   senha, "COMPRADOR", null, 0, null, 0, false, false,
                                   preferenciasCategorias, raioBuscaPreferido, 0.0, 0.0,
                                   null, false, false, 1, null, null, true);
}
    
    
    private boolean cadastrarUsuario(String nome, String email, String telefone, 
                                       String provincia, String distrito, String bairro, 
                                       String senha, String tipoUsuario, String tipoAgricultura, 
                                       int anosExperiencia, String biografia, double tamanhoPropriedade,
                                       boolean certificadoOrganico, boolean ofereceEntrega,
                                       List<String> preferenciasCategorias, Double raioBuscaPreferido, 
                                       double raioEntrega, double custoEntrega, String whatsapp,
                                       boolean aceitaVisitas, boolean aceitaEncomendas, 
                                       int prazoMinimoEncomenda, String horarioAbertura,
                                       String horarioFechamento, boolean disponivelParaContato) {
    
    // Validações existentes...
    ResultadoValidacao resultadoEmail = validarEmail(email);
    if (!resultadoEmail.valido) {
        JOptionPane.showMessageDialog(null, resultadoEmail.mensagem, "Email inválido", JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    ResultadoValidacao resultadoNome = Validacoes.validarNome(nome);
    if(!resultadoNome.valido){
        JOptionPane.showMessageDialog(null, resultadoNome.mensagem, "Nome Invalido", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    ResultadoValidacao resultadoSenha = validarSenha(senha);
    if(!resultadoSenha.valido){
        JOptionPane.showMessageDialog(null, resultadoSenha.mensagem, "Senha invalido", JOptionPane.ERROR_MESSAGE );
        return false;
    }
    
    ResultadoValidacao resultadoTelefone = validarTelefone(telefone);
    if(!resultadoTelefone.valido){
        JOptionPane.showMessageDialog(null, resultadoTelefone.mensagem, "Telefone invalido", JOptionPane.ERROR_MESSAGE );
        return false;
    }
    
    ResultadoValidacao resultadoProvincia = validarRegiao(provincia);
    if(!resultadoProvincia.valido){
        JOptionPane.showMessageDialog(null, resultadoProvincia.mensagem, "Provincia invalido", JOptionPane.ERROR_MESSAGE );
        return false;
    }
    
    ResultadoValidacao resultadoDistrito = validarRegiao(distrito);
    if(!resultadoDistrito.valido){
        JOptionPane.showMessageDialog(null, resultadoDistrito.mensagem, "Distrito invalido", JOptionPane.ERROR_MESSAGE );
        return false;
    }
    
    ResultadoValidacao resultadoBairro= validarRegiao(bairro);
    if(!resultadoBairro.valido){
        JOptionPane.showMessageDialog(null, resultadoBairro.mensagem, "Bairro invalido", JOptionPane.ERROR_MESSAGE );
        return false;
    }
    
    // Validação do WhatsApp
    if (whatsapp != null && !whatsapp.isEmpty()) {
        ResultadoValidacao resultadoWhatsapp = Validacoes.validarWhatsapp(whatsapp);
        if(!resultadoWhatsapp.valido){
            JOptionPane.showMessageDialog(null, resultadoWhatsapp.mensagem, "WhatsApp inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Validação do horário
    if (horarioAbertura != null && !horarioAbertura.isEmpty()) {
        ResultadoValidacao resultadoHorarioAbertura = Validacoes.validarHorario(horarioAbertura);
        if(!resultadoHorarioAbertura.valido){
            JOptionPane.showMessageDialog(null, resultadoHorarioAbertura.mensagem, "Horário de abertura inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    if (horarioFechamento != null && !horarioFechamento.isEmpty()) {
        ResultadoValidacao resultadoHorarioFechamento = Validacoes.validarHorario(horarioFechamento);
        if(!resultadoHorarioFechamento.valido){
            JOptionPane.showMessageDialog(null, resultadoHorarioFechamento.mensagem, "Horário de fechamento inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    try {
        Pessoa novoUsuario;
        
        if ("AGRICULTOR".equals(tipoUsuario)) {
            Agricultor agricultor = new Agricultor(
                senha, nome, email, telefone, 
                provincia, distrito, bairro, 
                null, null,
                tipoAgricultura, tamanhoPropriedade, anosExperiencia,
                biografia, certificadoOrganico, ofereceEntrega,
                raioEntrega, custoEntrega
            );
            
            // Configuracao dos novos campos
            agricultor.setWhatsapp(whatsapp);
            agricultor.setAceitaVisitas(aceitaVisitas);
            agricultor.setAceitaEncomendas(aceitaEncomendas);
            agricultor.setPrazoMinimoEncomenda(prazoMinimoEncomenda);
            agricultor.setDisponivelParaContato(disponivelParaContato);
            
            // Configurar horário de funcionamento
            if (horarioAbertura != null && horarioFechamento != null) {
                agricultor.setHorarioFuncionamento(horarioAbertura + " - " + horarioFechamento);
            }
            
            novoUsuario = agricultor;
            
        } else {
            Comprador comprador = new Comprador(senha, nome, email, telefone, provincia, distrito, bairro);
            
            if (preferenciasCategorias != null) {
                comprador.setPreferenciasCategorias(preferenciasCategorias);
            }
            if (raioBuscaPreferido != null) {
                comprador.setRaioBuscaPreferido(raioBuscaPreferido);
            }
            
            novoUsuario = comprador;
        }
        
        novoUsuario.setId(gerarId());
        usuarios.add(novoUsuario);
        
        mostrarSucesso("Cadastro realizado", 
                      tipoUsuario + " '" + nome + "' cadastrado com sucesso!");
             
        comService.configurarEmail("smtp.gmail.com", "587", "lilianolicumba42@gmail.com", "jwqa yltv iqic iqpr");
        comService.enviarEmailBoasVindas(email, nome);
             
        return true;
        
    } catch (Exception e) {
        mostrarErro("Erro", "Erro ao cadastrar: " + e.getMessage());
        return false;
    }
}
    
    //Metodos de busca e consulta
    public Pessoa buscarUsuarioPorEmail(String email){
        if(email == null) return null;
        
        return usuarios.stream()
                .filter(p -> p.getEmail() != null && p.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
    
    
    public Pessoa buscarUsuarioPorId(int id){
        return usuarios.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public List<Pessoa> listarTodosUsuarios(){
        return new ArrayList<>(usuarios);
    }
    
    public List<Pessoa> listarPorTipo(String tipo){
        List<Pessoa> resultado = new ArrayList<>();
        
        for(Pessoa usuario : usuarios){
            if(tipo.equalsIgnoreCase("Agricultor") && usuario instanceof Agricultor){
                resultado.add(usuario);
            }else if(tipo.equalsIgnoreCase("Comprador") && usuario instanceof Comprador){
                resultado.add(usuario);
            }
        }
        
        return resultado;
        
    }
    
    public List<Pessoa> filtrarUsuariosPorStatus(boolean ativo){
        List<Pessoa> resultado = new ArrayList<>();
        
        for (Pessoa usuario : usuarios){
            if(usuario.isAtivo() == ativo){
                resultado.add(usuario);
            }
        }
        
        return resultado;
    }
    
    private void mostrarErro(String titulo, String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, titulo, JOptionPane.ERROR_MESSAGE);
    }
    
    private void mostrarSucesso(String titulo, String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public int gerarId(){
        return ++ultimoId;
    }
    
    
    
    public boolean removerUsuario(String email) {
        Pessoa usuario = buscarUsuarioPorEmail(email);
        if (usuario != null) {
            usuarios.remove(usuario);
            return true;
        }
        return false;
    }
}
