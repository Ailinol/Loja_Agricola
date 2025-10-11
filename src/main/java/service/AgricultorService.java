package service;

import dao.AgricultorDAO;
import model.Agricultor;
import model.Produto;
import model.Avaliacao;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AgricultorService {
    
    private AgricultorDAO agricultorRepository;
    private EntityManager entityManager;
    private UsuarioService usuarioService;
    
    public AgricultorService(EntityManager entityManager, UsuarioService usuarioService) {
        this.entityManager = entityManager;
        this.usuarioService = usuarioService;
        this.agricultorRepository = new AgricultorDAO(entityManager);
    }
    
    // REMOVER: Método de cadastro (já existe no UsuarioService)
    // MANTER: Métodos específicos de operações do agricultor
    
    // Buscar agricultor por ID
    public Optional<Agricultor> buscarPorId(Long id) {
        try {
            return agricultorRepository.buscarPorId(id);
        } catch (Exception e) {
            System.err.println("Erro ao buscar agricultor por ID: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    // Buscar agricultor por email
    public Optional<Agricultor> buscarPorEmail(String email) {
        try {
            return agricultorRepository.buscarPorEmail(email);
        } catch (Exception e) {
            System.err.println("Erro ao buscar agricultor por email: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    // Buscar agricultor por nome (para integração com ProdutoService)
    public Agricultor buscarAgricultorPorNome(String nomeCompleto) {
        try {
            String[] partes = nomeCompleto.split(" - ");
            if (partes.length >= 1) {
                String nome = partes[0].trim();
                return agricultorRepository.buscarPorNome(nome).orElse(null);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao buscar agricultor por nome: " + e.getMessage());
            return null;
        }
    }
    
    // Listar todos os agricultores
    public List<Agricultor> listarTodosAgricultores() {
        try {
            return agricultorRepository.listarTodos();
        } catch (Exception e) {
            System.err.println("Erro ao listar agricultores: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    //Listar nomes para combo box (formato: "Nome - Localização")
    /**public List<String> getNomesAgricultores() {
        try {
            return agricultorRepository.listarTodos().stream()
                    .map(a -> a.getNome() + " - " + a.getLocalizacao())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Erro ao obter nomes de agricultores: " + e.getMessage());
            return new ArrayList<>();
        }
    } **/
    
    // ADICIONAR: Método para atualizar informações específicas do agricultor
    public ResultadoValidacao atualizarInformacoesAgricultura(Long agricultorId, 
            String tipoAgricultura, double tamanhoPropriedade, int anosExperiencia,
            String biografia, boolean certificadoOrganico, boolean certificadoSustentavel,
            List<String> outrasCertificacoes) {
        
        ResultadoValidacao resultado = new ResultadoValidacao();
        EntityTransaction transaction = entityManager.getTransaction();
        
        try {
            transaction.begin();
            
            Optional<Agricultor> agricultorOpt = agricultorRepository.buscarPorId(agricultorId);
            if (!agricultorOpt.isPresent()) {
                transaction.rollback();
                resultado.valido = false;
                resultado.mensagem = "Agricultor não encontrado";
                return resultado;
            }
            
            Agricultor agricultor = agricultorOpt.get();
            
            // Atualizar apenas campos específicos do agricultor
            agricultor.setTipoAgricultura(tipoAgricultura);
            agricultor.setTamanhoPropriedade(tamanhoPropriedade);
            agricultor.setAnosExperiencia(anosExperiencia);
            agricultor.setBiografia(biografia);
            agricultor.setCertificadoOrganico(certificadoOrganico);
            agricultor.setCertificadoSustentavel(certificadoSustentavel);
            agricultor.setOutrasCertificacoes(outrasCertificacoes);
            
            agricultorRepository.atualizar(agricultor);
            transaction.commit();
            
            resultado.valido = true;
            resultado.mensagem = "Informações de agricultura atualizadas com sucesso";
            //resultado.dados = agricultor;
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            resultado.valido = false;
            resultado.mensagem = "Erro ao atualizar informações: " + e.getMessage();
        }
        
        return resultado;
    }
    
    // ADICIONAR: Método para configurar entrega
    public ResultadoValidacao configurarEntrega(Long agricultorId, boolean ofereceEntrega, 
            double raioEntrega, double custoEntrega) {
        
        ResultadoValidacao resultado = new ResultadoValidacao();
        EntityTransaction transaction = entityManager.getTransaction();
        
        try {
            transaction.begin();
            
            Optional<Agricultor> agricultorOpt = agricultorRepository.buscarPorId(agricultorId);
            if (!agricultorOpt.isPresent()) {
                transaction.rollback();
                resultado.valido = false;
                resultado.mensagem = "Agricultor não encontrado";
                return resultado;
            }
            
            Agricultor agricultor = agricultorOpt.get();
            agricultor.setOfereceEntrega(ofereceEntrega);
            agricultor.setRaioEntrega(raioEntrega);
            agricultor.setCustoEntrega(custoEntrega);
            
            agricultorRepository.atualizar(agricultor);
            transaction.commit();
            
            resultado.valido = true;
            resultado.mensagem = "Configurações de entrega atualizadas";
            //resultado.dados = agricultor;
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            resultado.valido = false;
            resultado.mensagem = "Erro ao configurar entrega: " + e.getMessage();
        }
        
        return resultado;
    }
    
    // ADICIONAR: Método para configurar contato e encomendas
    public ResultadoValidacao configurarContatoEncomendas(Long agricultorId, String whatsapp,
            boolean aceitaVisitas, boolean aceitaEncomendas, int prazoMinimoEncomenda,
            List<String> metodosContato, boolean disponivelParaContato) {
        
        ResultadoValidacao resultado = new ResultadoValidacao();
        EntityTransaction transaction = entityManager.getTransaction();
        
        try {
            transaction.begin();
            
            Optional<Agricultor> agricultorOpt = agricultorRepository.buscarPorId(agricultorId);
            if (!agricultorOpt.isPresent()) {
                transaction.rollback();
                resultado.valido = false;
                resultado.mensagem = "Agricultor não encontrado";
                return resultado;
            }
            
            Agricultor agricultor = agricultorOpt.get();
            agricultor.setWhatsapp(whatsapp);
            agricultor.setAceitaVisitas(aceitaVisitas);
            agricultor.setAceitaEncomendas(aceitaEncomendas);
            agricultor.setPrazoMinimoEncomenda(prazoMinimoEncomenda);
            agricultor.setMetodosContato(metodosContato);
            agricultor.setDisponivelParaContato(disponivelParaContato);
            
            agricultorRepository.atualizar(agricultor);
            transaction.commit();
            
            resultado.valido = true;
            resultado.mensagem = "Configurações de contato atualizadas";
            //resultado.dados = agricultor;
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            resultado.valido = false;
            resultado.mensagem = "Erro ao configurar contato: " + e.getMessage();
        }
        
        return resultado;
    }
    
    // MANTER: Métodos de busca por localização
    public List<Agricultor> buscarPorLocalizacao(String provincia, String distrito) {
        try {
            return agricultorRepository.buscarPorLocalizacao(provincia, distrito);
        } catch (Exception e) {
            System.err.println("Erro ao buscar agricultores por localização: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // MANTER: Métodos de busca por tipo de agricultura
    public List<Agricultor> buscarPorTipoAgricultura(String tipoAgricultura) {
        try {
            return agricultorRepository.buscarPorTipoAgricultura(tipoAgricultura);
        } catch (Exception e) {
            System.err.println("Erro ao buscar agricultores por tipo: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // MANTER: Listar agricultores orgânicos
    public List<Agricultor> listarOrganicos() {
        try {
            return agricultorRepository.listarOrganicos();
        } catch (Exception e) {
            System.err.println("Erro ao listar agricultores orgânicos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // MANTER: Listar agricultores que oferecem entrega
    public List<Agricultor> listarComEntrega() {
        try {
            return agricultorRepository.listarComEntrega();
        } catch (Exception e) {
            System.err.println("Erro ao listar agricultores com entrega: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // MANTER: Buscar por classificação
    public List<Agricultor> buscarPorClassificacaoMinima(double classificacaoMinima) {
        try {
            return agricultorRepository.buscarPorClassificacaoMinima(classificacaoMinima);
        } catch (Exception e) {
            System.err.println("Erro ao buscar agricultores por classificação: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // MANTER: Adicionar avaliação
    public ResultadoValidacao adicionarAvaliacao(Long agricultorId, Avaliacao avaliacao) {
        ResultadoValidacao resultado = new ResultadoValidacao();
        EntityTransaction transaction = entityManager.getTransaction();
        
        try {
            transaction.begin();
            
            Optional<Agricultor> agricultorOpt = agricultorRepository.buscarPorId(agricultorId);
            if (!agricultorOpt.isPresent()) {
                transaction.rollback();
                resultado.valido = false;
                resultado.mensagem = "Agricultor não encontrado";
                return resultado;
            }
            
            Agricultor agricultor = agricultorOpt.get();
            agricultor.receberAvaliacao(avaliacao);
            
            agricultorRepository.atualizar(agricultor);
            transaction.commit();
            
            resultado.valido = true;
            resultado.mensagem = "Avaliação adicionada com sucesso";
            //resultado.dados = agricultor;
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            resultado.valido = false;
            resultado.mensagem = "Erro ao adicionar avaliação: " + e.getMessage();
        }
        
        return resultado;
    }
    
    // MANTER: Incrementar clientes atendidos
    public void incrementarClientesAtendidos(Long agricultorId) {
        EntityTransaction transaction = entityManager.getTransaction();
        
        try {
            transaction.begin();
            
            Optional<Agricultor> agricultorOpt = agricultorRepository.buscarPorId(agricultorId);
            if (agricultorOpt.isPresent()) {
                Agricultor agricultor = agricultorOpt.get();
                agricultor.incrementarClientesAtendidos();
                agricultorRepository.atualizar(agricultor);
            }
            
            transaction.commit();
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Erro ao incrementar clientes atendidos: " + e.getMessage());
        }
    }
    
    // MANTER: Estatísticas
    public int getTotalAgricultores() {
        try {
            return agricultorRepository.getTotalAgricultores();
        } catch (Exception e) {
            System.err.println("Erro ao obter total de agricultores: " + e.getMessage());
            return 0;
        }
    }
    
    public int getTotalAgricultoresAtivos() {
        try {
            return agricultorRepository.getTotalAgricultoresAtivos();
        } catch (Exception e) {
            System.err.println("Erro ao obter total de agricultores ativos: " + e.getMessage());
            return 0;
        }
    }
    
    // ADICIONAR: Método para obter agricultor por ID do usuário
   /** public Optional<Agricultor> buscarPorUsuarioId(Long usuarioId) {
        try {
              
           return agricultorRepository.buscarPorUsuarioId(usuarioId);
        } catch (Exception e) {
            System.err.println("Erro ao buscar agricultor por ID de usuário: " + e.getMessage());
            return Optional.empty();
        }
    }**/
    
    // Fechar conexão
    public void fechar() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}