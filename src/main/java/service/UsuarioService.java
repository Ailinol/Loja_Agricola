package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.swing.JOptionPane;
import model.Agricultor;
import model.Comprador;
import model.Pessoa;
import model.Usuario;
import static service.Validacoes.validarEmail;
import static service.Validacoes.validarRegiao;
import static service.Validacoes.validarSenha;
import static service.Validacoes.validarTelefone;

public class UsuarioService {
    private EntityManagerFactory emf;
    private EntityManager em;
    private int ultimoId;
    NotificacaoService comService = new NotificacaoService();
    
    public UsuarioService() {
        this.emf = Persistence.createEntityManagerFactory("greenMatch-jpa");
        this.em = emf.createEntityManager();
    }
    
    // Método Responsavel por fazer o cadastro do Agricultor e do comprador
    
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

        // Verificar se email já existe no banco
        if (buscarUsuarioPorEmail(email) != null) {
            JOptionPane.showMessageDialog(null, "Email já cadastrado no sistema", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            em.getTransaction().begin();
            
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
                
                agricultor.setWhatsapp(whatsapp);
                agricultor.setAceitaVisitas(aceitaVisitas);
                agricultor.setAceitaEncomendas(aceitaEncomendas);
                agricultor.setPrazoMinimoEncomenda(prazoMinimoEncomenda);
                agricultor.setDisponivelParaContato(disponivelParaContato);
                
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
            
            // Salvar no banco de dados
            em.persist(novoUsuario);
            em.getTransaction().commit();
            
            mostrarSucesso("Cadastro realizado", 
                          tipoUsuario + " '" + nome + "' cadastrado com sucesso!");
                 
            comService.configurarEmail("smtp.gmail.com", "587", "lilianolicumba42@gmail.com", "jwqa yltv iqic iqpr");
            comService.enviarEmailBoasVindas(email, nome);
                 
            return true;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            mostrarErro("Erro", "Erro ao cadastrar: " + e.getMessage());
            return false;
        }
    }
    
    //Metodos de busca e consulta
    public Pessoa buscarUsuarioPorEmail(String email){
        if(email == null) return null;
        
        try {
            TypedQuery<Pessoa> query = em.createQuery(
                "SELECT p FROM Pessoa p WHERE p.email = :email", Pessoa.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    
    public Pessoa buscarUsuarioPorId(int id){
        return em.find(Pessoa.class, id);
    }
    
    public List<Pessoa> listarTodosUsuarios(){
        try {
            return em.createQuery("SELECT p FROM Pessoa p", Pessoa.class).getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<Pessoa> listarPorTipo(String tipo){
        try {
            if("Agricultor".equalsIgnoreCase(tipo)) {
                TypedQuery<Agricultor> query = em.createQuery("SELECT a FROM Agricultor a", Agricultor.class);
                return new ArrayList<Pessoa>(query.getResultList());
            } else if("Comprador".equalsIgnoreCase(tipo)) {
                TypedQuery<Comprador> query = em.createQuery("SELECT c FROM Comprador c", Comprador.class);
                return new ArrayList<Pessoa>(query.getResultList());
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar por tipo: " + e.getMessage());
        }
        return new ArrayList<>();
    }
    
    public List<Agricultor> listarAgricultores() {
        try {
            return em.createQuery("SELECT a FROM Agricultor a", Agricultor.class).getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<Comprador> listarCompradores() {
        try {
            return em.createQuery("SELECT c FROM Comprador c", Comprador.class).getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<Pessoa> filtrarUsuariosPorStatus(boolean ativo){
        try {
            TypedQuery<Pessoa> query = em.createQuery(
                "SELECT p FROM Pessoa p WHERE p.ativo = :ativo", Pessoa.class);
            query.setParameter("ativo", ativo);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
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
        try {
            em.getTransaction().begin();
            
            Pessoa usuario = buscarUsuarioPorEmail(email);
            if (usuario != null) {
                em.remove(usuario);
                em.getTransaction().commit();
                return true;
            }
            
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            mostrarErro("Erro", "Erro ao remover usuário: " + e.getMessage());
            return false;
        }
    }
    
    public boolean atualizarUsuario(Pessoa usuario) {
        try {
            em.getTransaction().begin();
            em.merge(usuario);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            mostrarErro("Erro", "Erro ao atualizar usuário: " + e.getMessage());
            return false;
        }
    }
    
    public void fecharConexao() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    
    // Método para verificar se as tabelas foram criadas
    public void verificarConexao() {
        try {
            long count = em.createQuery("SELECT COUNT(a) FROM Agricultor a", Long.class).getSingleResult();
            System.out.println("✅ Conexão com banco de dados estabelecida com sucesso!");
            System.out.println("✅ Tabelas criadas automaticamente pelo JPA");
        } catch (Exception e) {
            System.err.println("❌ Erro na conexão com banco de dados: " + e.getMessage());
        }
    }
}